package cn.edu.sdu.online.isdu.ui.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.*
import android.view.animation.OvershootInterpolator
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import kotlinx.android.synthetic.main.fragment_home.*
import net.lucode.hackware.magicindicator.FragmentContainerHelper
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView
import java.io.Serializable
import java.time.LocalDate

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/5/25
 *
 * 主页论坛碎片
 ****************************************************
 */

class FragmentHome : Fragment(), Serializable {

    private var searchBar: LinearLayout? = null // 搜索框
    private var askBar: LinearLayout? = null // 提问框

    private var magicIndicator: MagicIndicator? = null // Magic Indicator
    private var viewPager: ViewPager? = null // ViewPager

    private val mDataList = listOf("推荐", "关注", "热榜", "校内相关") // Indicator 数据
    private val mFragments = listOf(FragmentHomeRecommend(),
            FragmentHomeRecommend(), FragmentHomeRecommend(), FragmentHomeRecommend()) // Fragment 数组
    private var mViewPagerAdapter: FragAdapter? = null // ViewPager适配器

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        initView(view)
        initFragments()
        initIndicator()
        return view
    }

    /**
     * 初始化View
     */
    private fun initView(view: View) {
        /* 获取实例 */
        searchBar = view.findViewById(R.id.search_bar)
        askBar = view.findViewById(R.id.ask_bar)
        magicIndicator = view.findViewById(R.id.magic_indicator)
        viewPager = view.findViewById(R.id.view_pager)

//        setAskBadgeNumber(100)

        viewPager!!.offscreenPageLimit = mDataList.size // 设置ViewPager的预加载页面数量，防止销毁
    }

    /**
     * 设置提问按钮小红点数字
     *
     * @param number 提问框的小红点数量
     */
//    private fun setAskBadgeNumber(number: Int) {
//        QBadgeView(context).bindTarget(askBar!!)
//                .setBadgeNumber(number)
//                .setBadgeGravity(Gravity.START or Gravity.TOP)
//                .setGravityOffset(4f, 0f, true)
//    }

    /**
     * 初始化MagicIndicator导航栏
     */
    private fun initIndicator() {
        val commonNavigator = CommonNavigator(context)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getTitleView(p0: Context?, p1: Int): IPagerTitleView {
                val simplePagerTitleView = ColorTransitionPagerTitleView(p0!!)
                simplePagerTitleView.normalColor = 0xFF808080.toInt()
                simplePagerTitleView.selectedColor = 0xFF131313.toInt()
                simplePagerTitleView.text = mDataList[p1]
                simplePagerTitleView.textSize = 18f
                simplePagerTitleView.setOnClickListener { viewPager?.currentItem = p1 }
                return simplePagerTitleView
            }

            override fun getCount(): Int = mDataList.size

            override fun getIndicator(p0: Context?): IPagerIndicator {
                val linePagerIndicator = LinePagerIndicator(p0)
                linePagerIndicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                linePagerIndicator.lineWidth = UIUtil.dip2px(context, 10.0).toFloat()
                linePagerIndicator.setColors(0xFF717DEB.toInt())
                return linePagerIndicator
            }
        }

        commonNavigator.isAdjustMode = true
        magicIndicator!!.navigator = commonNavigator

        ViewPagerHelper.bind(magicIndicator, viewPager)
    }

    /**
     * 初始化推荐、关注、热榜和校内相关碎片
     */
    private fun initFragments() {
        mViewPagerAdapter = FragAdapter(childFragmentManager, mFragments)
        viewPager!!.adapter = mViewPagerAdapter
    }

    /**
     * 自定义ViewPager适配器类
     */
    class FragAdapter(fm: FragmentManager, fragments: List<Fragment>) : FragmentPagerAdapter(fm) {
        private val mFragments = fragments
        private val mDataList = listOf("推荐", "关注", "热榜", "校内相关") // Indicator 数据

        override fun getItem(position: Int): Fragment = mFragments[position]

        override fun getCount(): Int = mFragments.size

        override fun getPageTitle(position: Int): CharSequence? {
            return mDataList[position]
        }
    }

    companion object {
        val TAG = "FragmentHome"
    }
}