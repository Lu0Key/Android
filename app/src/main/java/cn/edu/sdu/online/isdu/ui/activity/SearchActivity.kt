package cn.edu.sdu.online.isdu.ui.activity

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.AlphaActivity
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.ui.fragments.search.SearchNewsFragment
import cn.edu.sdu.online.isdu.ui.fragments.search.SearchPostFragment
import cn.edu.sdu.online.isdu.ui.fragments.search.SearchUserFragment
import cn.edu.sdu.online.isdu.util.Logger
import kotlinx.android.synthetic.main.activity_download.*
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/17
 *
 * 搜索页面
 ****************************************************
 */

class SearchActivity : AlphaActivity(), View.OnClickListener {

    private var btnBack: View? = null
    private var editSearch: EditText? = null
    private var btnSearch: TextView? = null
    private var viewPager: ViewPager? = null
    private var magicIndicator: MagicIndicator? = null
    private var mViewPagerAdapter: FragAdapter? = null
    private val mDataList = listOf("帖子", "资讯", "用户")
    private val mFragments = listOf(SearchPostFragment(), SearchNewsFragment(), SearchUserFragment())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initView()
        initFragments()
        initIndicator()
    }

    private fun initView() {
        btnBack = findViewById(R.id.btn_back)
        editSearch = findViewById(R.id.edit_search)
        btnSearch = findViewById(R.id.btn_search)
        viewPager = view_pager
        magicIndicator = magic_indicator

        btnBack!!.setOnClickListener(this)
        btnSearch!!.setOnClickListener(this)

        editSearch!!.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                btnSearch!!.callOnClick()
                true
            }
            false
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_back -> {
                finish()
            }
            R.id.btn_search -> {

                var text:String=editSearch!!.text.toString()
                when(viewPager?.currentItem){
                    0 -> {
                        (mFragments[0]as SearchPostFragment ).initData()
                    }
                    1 -> {
                        (mFragments[1]as SearchNewsFragment ).initData()
                    }
                    2 -> {
                        if(editSearch!!.text!=null){
                            val url = ServerInfo.searchUser(editSearch!!.text.toString())
                            NetworkAccess.buildRequest(url, object : Callback {
                                override fun onFailure(call: Call?, e: IOException?) {
                                    runOnUiThread {
                                        Logger.log(e)
                                        Toast.makeText(this@SearchActivity, "网络错误", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                override fun onResponse(call: Call?, response: Response?) {
                                    val json = response?.body()?.string()
                                    try {
                                        val jsonObj = JSONObject(json)
                                        if (!jsonObj.isNull("status") && jsonObj.getString("status") == "failed") {
                                            runOnUiThread {
                                                Toast.makeText(this@SearchActivity, "用户不存在", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            val user = User()
                                            user!!.avatarString = jsonObj.getString("avatar")
                                            user!!.nickName = jsonObj.getString("nickname")
                                            user!!.selfIntroduce = jsonObj.getString("sign")
                                            user!!.studentNumber=jsonObj.getString("studentnumber")
                                            user!!.uid=jsonObj.getInt("id")
                                            runOnUiThread {
                                                (mFragments[2]as SearchUserFragment ).initData(user)
                                                viewPager!!.adapter!!.notifyDataSetChanged()
                                                (mFragments[2]as SearchUserFragment ).refresh()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Logger.log(e)
                                        runOnUiThread {
                                            Toast.makeText(this@SearchActivity, "网络错误\n服务器无响应", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            })
                        }
                    }
                }

            }
        }
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
                simplePagerTitleView.textSize = 16f
                simplePagerTitleView.setOnClickListener {
                    viewPager?.currentItem = p1
                    if(p1!=2){
                        (mFragments[2]as SearchUserFragment ).clear()
                    }
                }
                return simplePagerTitleView
            }

            override fun getCount(): Int = mDataList.size

            override fun getIndicator(p0: Context?): IPagerIndicator {
                val linePagerIndicator = LinePagerIndicator(p0)
                linePagerIndicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                linePagerIndicator.lineWidth = UIUtil.dip2px(this@SearchActivity,
                        10.0).toFloat()
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

    /**
     * 自定义ViewPager适配器类
     */
    inner class FragAdapter(fm: FragmentManager, fragments: List<Fragment>) : FragmentPagerAdapter(fm) {
        private val mFragments = fragments

        override fun getItem(position: Int): Fragment = mFragments[position]

        override fun getCount(): Int = mFragments.size

        override fun getPageTitle(position: Int): CharSequence? {
            return mDataList[position]
        }
    }
}
