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
 * 主页关注碎片
 * 内容为关注的用户的帖子
 ****************************************************
 */

class HomeLikeFragment : Fragment() {


    private var springView: SpringView? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home_like, container, false)

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