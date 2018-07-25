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
 * 主页校内相关碎片
 * 一般为###管理员###发布的与学校相关的内容
 ****************************************************
 */

class HomeSchoolFragment : Fragment() {


    private var springView: SpringView? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home_school, container, false)

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