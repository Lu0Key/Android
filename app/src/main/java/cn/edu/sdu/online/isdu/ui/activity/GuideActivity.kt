package cn.edu.sdu.online.isdu.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.BaseActivity
import cn.edu.sdu.online.isdu.ui.fragments.GuideFinishFragment
import cn.edu.sdu.online.isdu.ui.fragments.ImageFragment
import kotlinx.android.synthetic.main.activity_guide.*
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator

class GuideActivity : BaseActivity() {

    private var magicIndicator: MagicIndicator? = null
    private var mViewPager: ViewPager? = null
    private val mImageResources = listOf(R.drawable.img_guide_0, R.drawable.img_guide_1,
            R.drawable.img_guide_2, R.drawable.img_guide_3)
    private val mFragments: MutableList<Fragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        decorateWindow()

        initView()
        initIndicator()
    }

    private fun initView() {
        magicIndicator = magic_indicator
        mViewPager = view_pager

        // Initialize Fragments
        for (i in 0 until mImageResources.size) {
            mFragments.add(ImageFragment.newInstance(mImageResources[i]))
        }
        mFragments.add(GuideFinishFragment())

        mViewPager!!.adapter = FragAdapter(supportFragmentManager)

    }

    private fun initIndicator() {
        val navigator = CircleNavigator(this)
        navigator.circleCount = mImageResources.size
        navigator.circleColor = (0xFF717EDB).toInt()
        navigator.setCircleClickListener { index ->
            mViewPager!!.currentItem = index
        }

        magicIndicator!!.navigator = navigator
        ViewPagerHelper.bind(magicIndicator, mViewPager)
    }

    inner class FragAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = mFragments[position]

        override fun getCount(): Int = mFragments.size

        override fun getPageTitle(position: Int): CharSequence? {
            return ""
        }
    }

    override fun prepareBroadcastReceiver() {

    }

    override fun unRegBroadcastReceiver() {
    }
}
