package cn.edu.sdu.online.isdu.ui.activity

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.BaseActivity
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.net.AccountOp
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.ui.design.viewpager.NoScrollViewPager
import cn.edu.sdu.online.isdu.ui.fragments.FragmentMeArticles
import cn.edu.sdu.online.isdu.util.ImageManager
import com.bumptech.glide.Glide
import com.zhouwei.blurlibrary.EasyBlur
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_my_home_page.*
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/13
 *
 * 主页个人主页碎片
 ****************************************************
 */

class MyHomePageActivity : SlideActivity(), View.OnClickListener {

    private var magicIndicator: MagicIndicator? = null
    private var viewPager: NoScrollViewPager? = null
    private val mDataList = listOf("我的帖子", "我的评论", "我的关注") // Indicator 数据
    private val mFragments = listOf(FragmentMeArticles(),
            FragmentMeArticles(), FragmentMeArticles()) // Fragment 数组
    private var mViewPagerAdapter: FragAdapter? = null // ViewPager适配器

    private var collapsingToolbar: CollapsingToolbarLayout? = null
    private var toolBar: Toolbar? = null
    private var appBarLayout: AppBarLayout? = null // AppBarLayout实例

    private var btnEditProfile: ImageView? = null // 编辑个人资料
    private var txtMyFollower: TextView? = null // 我关注的人
    private var txtFollowMe: TextView? = null // 关注我的人
    private var userName: TextView? = null
    private var btnBack: ImageView? = null
    private var miniCircleImageView: CircleImageView? = null
    private var backgroundImage: ImageView? = null
    private var btnSettings: ImageView? = null
    private var circleImageView: CircleImageView? = null
    private var txtSign: TextView? = null // 个人签名

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_home_page)

        initView()
        initFragments()
        initIndicator()

        loadUserInfo()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            btn_edit_profile.id -> {
                startActivity(Intent(this, EditProfileActivity::class.java))
            }
            btn_back.id -> {
                finish()
            }
            btn_settings.id -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            background_image.id, circle_image_view.id -> {
                startActivity(Intent(this, ViewImageActivity::class.java)
                        .putExtra("url", ServerInfo.getUserInfo(User.staticUser.uid.toString(), "avatar")))
            }
        }
    }

    private fun initView() {
        magicIndicator = findViewById(R.id.magic_indicator)
        viewPager = findViewById(R.id.view_pager)
        appBarLayout = findViewById(R.id.appbar_layout)
        btnEditProfile = findViewById(R.id.btn_edit_profile)
        txtMyFollower = findViewById(R.id.my_follower_count)
        txtFollowMe = findViewById(R.id.following_me_count)
        userName = findViewById(R.id.user_name)
        collapsingToolbar = findViewById(R.id.collapsing_toolbar)
        btnBack = findViewById(R.id.btn_back)
        toolBar = findViewById(R.id.tool_bar)
        miniCircleImageView = findViewById(R.id.mini_circle_image_view)
        backgroundImage = findViewById(R.id.background_image)
        btnSettings = findViewById(R.id.btn_settings)
        circleImageView = findViewById(R.id.circle_image_view)
        txtSign = findViewById(R.id.txt_sign)

        viewPager!!.setAppBarLayout(appBarLayout)

        appBarLayout!!.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if ((verticalOffset == 0) or (Math.abs(verticalOffset) == Math.abs(appBarLayout.totalScrollRange))) {
                viewPager!!.setScroll(true)
            } else {
                viewPager!!.setScroll(false)
            }
            if (Math.abs(verticalOffset) == Math.abs(appBarLayout.totalScrollRange))
                miniCircleImageView!!.visibility = View.VISIBLE
            else
                miniCircleImageView!!.visibility = View.GONE
        }

        toolBar!!.setOnClickListener { appBarLayout!!.setExpanded(true, true) }
        toolBar!!.setOnLongClickListener {
            appBarLayout!!.setExpanded(true, true)
            true
        }


        btnBack!!.setOnClickListener(this)
        btnEditProfile!!.setOnClickListener(this)
        btnSettings!!.setOnClickListener(this)
        circleImageView!!.setOnClickListener(this)
        backgroundImage!!.setOnClickListener(this)

    }

    /**
     * 初始化MagicIndicator导航栏
     */
    private fun initIndicator() {
        val commonNavigator = CommonNavigator(this)
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
                linePagerIndicator.lineWidth = UIUtil.dip2px(this@MyHomePageActivity, 10.0).toFloat()
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
        mViewPagerAdapter = FragAdapter(supportFragmentManager, mFragments)
        viewPager!!.adapter = mViewPagerAdapter
    }

    private fun loadUserInfo() {
        if (User.staticUser == null)
            User.staticUser = User.load()
        val user = User.staticUser
        val bmp = ImageManager.convertStringToBitmap(user.avatarString)
        userName!!.text = user.nickName
        txtSign!!.text = "个人签名：${user.selfIntroduce}"
        fillBackgroundImage(bmp)
        fillAvatarImage(bmp)
    }

    override fun onResume() {
        super.onResume()
        loadUserInfo()
    }

    private fun fillBackgroundImage(bmp: Bitmap?) {
        backgroundImage!!.tag = null
        if (bmp != null) {
            val bitmap = EasyBlur.with(this)
                    .bitmap(bmp)
                    .policy(EasyBlur.BlurPolicy.RS_BLUR)
                    .radius(15)
                    .scale(2)
                    .blur()
            Glide.with(this)
                    .load(bitmap)
                    .into(backgroundImage!!)
        } else {
            backgroundImage!!.setImageBitmap(null)
        }

    }

    private fun fillAvatarImage(bmp: Bitmap?) {
        if (bmp == null) {
            circleImageView!!.setImageBitmap(null)
            miniCircleImageView!!.setImageBitmap(null)
        } else {
            Glide.with(this)
                    .load(bmp)
                    .into(circleImageView!!)
            Glide.with(this)
                    .load(bmp)
                    .into(miniCircleImageView!!)
        }
    }

    fun getAppBar(): AppBarLayout = appBarLayout!!

    /**
     * 自定义ViewPager适配器类
     */
    class FragAdapter(fm: FragmentManager, fragments: List<Fragment>) : FragmentPagerAdapter(fm) {
        private val mFragments = fragments
        private val mDataList = listOf("我的帖子", "我的评论", "我的关注") // Indicator 数据

        override fun getItem(position: Int): Fragment = mFragments[position]

        override fun getCount(): Int = mFragments.size

        override fun getPageTitle(position: Int): CharSequence? {
            return mDataList[position]
        }
    }

    private class UserSyncBroadcastReceiver(activity: MyHomePageActivity) :
            BaseActivity.MyBroadcastReceiver(activity) {
        private val activity = activity
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.action == AccountOp.ACTION_USER_LOG_OUT) {
                activity.finish()
            }
        }
    }

    override fun prepareBroadcastReceiver() {
        if (myBroadcastReceiver == null) {
            val intentFilter = IntentFilter(AccountOp.ACTION_USER_LOG_OUT)
            myBroadcastReceiver = UserSyncBroadcastReceiver(this)
            AccountOp.localBroadcastManager.registerReceiver(myBroadcastReceiver!!,
                    intentFilter)
        }
    }

    override fun unRegBroadcastReceiver() {
        if (myBroadcastReceiver != null)
            AccountOp.localBroadcastManager.unregisterReceiver(myBroadcastReceiver!!)
    }
}
