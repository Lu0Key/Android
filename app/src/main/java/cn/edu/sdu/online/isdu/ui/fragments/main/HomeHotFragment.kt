package cn.edu.sdu.online.isdu.ui.fragments.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.edu.sdu.online.isdu.R
import com.liaoinstan.springview.widget.SpringView

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/25
 *
 * 主页热榜碎片
 * 热榜后台算法：
 * 采用加权求权值的方法，
 *  权值 = 点赞数 * 点赞权值 + 评论数 * 评论权值 + 发布时长权值 / 发布时长（天数 + 1）
 *  其中，点赞权值 =
 *       评论权值 =
 *       发布时长权值 =
 ****************************************************
 */


class HomeHotFragment : Fragment() {

    private var springView: SpringView? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home_hot, container, false)

        initView(view)
        initRecyclerView()
        initPullRefresh()

        return view
    }

    private fun initView(view: View) {

    }

    private fun initRecyclerView() {

    }

    private fun initPullRefresh() {

    }
}