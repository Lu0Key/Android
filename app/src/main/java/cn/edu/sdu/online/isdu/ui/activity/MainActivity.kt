package cn.edu.sdu.online.isdu.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.KeyEvent
import android.view.View
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.net.AccountOp
import cn.edu.sdu.online.isdu.ui.fragments.HomeFragment
import cn.edu.sdu.online.isdu.ui.fragments.MeFragment
import cn.edu.sdu.online.isdu.ui.fragments.NewsFragment
import kotlinx.android.synthetic.main.activity_main.*
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView
import android.view.LayoutInflater
import android.widget.*
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.util.*
import cn.edu.sdu.online.isdu.util.download.DownloadItem
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator


/**
****************************************************
* @author zsj
* Last Modifier: ZSJ
* Last Modify Time: 2018/7/15
*
* 主活动页面
* 其下附属3个Fragment
* 分别是：主页面、个人中心、资讯页面
*
* #修复了常驻后台进程被杀重启时的崩溃bug
* #添加按下返回键回到桌面的功能
* #7/7重构主活动
* #下载服务
****************************************************
*/
class MainActivity : SlideActivity(), View.OnClickListener {

    private var fragments: MutableList<Fragment> = ArrayList() // Fragment列表
    private var fragmentTags = listOf("HomeFragment", "NewsFragment", "MeFragment") // Fragment Tag
    private val imgRes = listOf(R.drawable.home_selected, R.drawable.news_selected, R.drawable.me_selected)
    private val imgBackRes = listOf(R.drawable.home_back, R.drawable.news_back, R.drawable.me_back)

    private var mDataList = listOf("主页", "资讯", "个人中心")

    /////////////////////整体滑动布局/////////////////////////
    private var magicIndicator: MagicIndicator? = null // 指引器
    private var mViewPager: ViewPager? = null // ViewPager
    private var mPagerAdapter: FragAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 请求关键权限
        Permissions.requestPermission(this, Permissions.VIBRATE)
        Permissions.requestPermission(this, Permissions.INTERNET)
        /* 获取实例 */
        magicIndicator = magic_indicator
        mViewPager = view_pager

        initFragment()
        mPagerAdapter = FragAdapter(supportFragmentManager, fragments)
        mViewPager!!.adapter = mPagerAdapter

        initMagicIndicator()

        // 同步用户信息
        AccountOp.syncUserInformation()
    }

    override fun onResume() {
        super.onResume()
        EnvVariables.init(this)
    }

    private fun initFragment() {
        if (fragments.isEmpty()) {
            fragments.add(HomeFragment())
            fragments.add(NewsFragment())
            fragments.add(MeFragment())
        }
    }

    private fun initMagicIndicator() {
        magicIndicator!!.setBackgroundColor(Color.WHITE)
        val commonNavigator = CommonNavigator(this)
        commonNavigator.isAdjustMode = true
        commonNavigator.adapter = object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return mDataList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val commonPagerTitleView = CommonPagerTitleView(context)
                // load custom layout
                val customLayout = LayoutInflater.from(context).inflate(R.layout.simple_pager_title_layout, null)
                val titleImg = customLayout.findViewById(R.id.title_img) as ImageView
                val titleImgBack = customLayout.findViewById(R.id.title_img_back) as ImageView

                titleImgBack.setImageResource(imgBackRes[index])
                titleImg.setImageResource(imgRes[index])
                commonPagerTitleView.setContentView(customLayout)

                commonPagerTitleView.onPagerTitleChangeListener = object : CommonPagerTitleView.OnPagerTitleChangeListener {

                    override fun onSelected(index: Int, totalCount: Int) {
                    }

                    override fun onDeselected(index: Int, totalCount: Int) {
                    }

                    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
                        titleImg.alpha = 1.0f + (0.0f - 1.0f) * leavePercent
                    }

                    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
                        titleImg.alpha = 0.0f + (1.0f - 0.0f) * enterPercent
                    }
                }

                commonPagerTitleView.setOnClickListener { mViewPager!!.currentItem = index }

                return commonPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator? {
                return null
            }
        }
        magicIndicator!!.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, mViewPager)
    }

    /**
     * 重写点击事件
     */
    override fun onClick(v: View?) {
    }


    /**
     * 按下返回键不退出应用
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(Intent(Intent.ACTION_MAIN)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addCategory(Intent.CATEGORY_HOME))
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 自定义ViewPager适配器类
     */
    class FragAdapter(fm: FragmentManager, fragments: List<Fragment>) : FragmentPagerAdapter(fm) {
        private val mFragments = fragments
        private val mDataList = listOf("主页", "资讯", "个人中心")

        override fun getItem(position: Int): Fragment = mFragments[position]

        override fun getCount(): Int = mFragments.size

        override fun getPageTitle(position: Int): CharSequence? {
            return mDataList[position]
        }
    }

    override fun prepareBroadcastReceiver() {

    }

    override fun unRegBroadcastReceiver() {

    }

    companion object {
        val FRAGMENT_HOME = 0
        val FRAGMENT_NEWS = 1
        val FRAGMENT_ME = 2
    }
}
