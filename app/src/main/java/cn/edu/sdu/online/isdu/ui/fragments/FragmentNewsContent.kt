package cn.edu.sdu.online.isdu.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.bean.Post

class FragmentNewsContent : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var index = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_news_content, container, false)

        initView(view)
        return view
    }

    private fun initView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)

    }

    private fun setArguments(index: Int) {
        this.index = index
    }

    class MyAdapter(mDataList: List<Post>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_news_content_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return 0
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        }
    }

    companion object {
        fun newInstance(index: Int): FragmentNewsContent {
            val fragment = FragmentNewsContent()
            fragment.setArguments(index)
            return fragment
        }
    }
}