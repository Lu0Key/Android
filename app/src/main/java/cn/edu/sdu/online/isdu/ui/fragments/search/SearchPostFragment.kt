package cn.edu.sdu.online.isdu.ui.fragments.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.LazyLoadFragment
import cn.edu.sdu.online.isdu.bean.Post
import cn.edu.sdu.online.isdu.bean.User

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/17
 *
 * 搜索帖子的Fragment
 ****************************************************
 */

class SearchPostFragment : LazyLoadFragment() {

    private var dataList: MutableList<Post> = ArrayList()
    private var adapter: MyAdapter? = null
    private var loadingLayout: View? = null
    private var recyclerView: RecyclerView? = null
    private var search : String? = null
    //private var isLoadComplete = false

    fun setSearch(search: String?){
        this.search = search
        //isLoadComplete = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_post, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        loadingLayout = view.findViewById(R.id.loading_layout)
        recyclerView = view.findViewById(R.id.recycler_view)
    }

    fun initData(){

    }
    fun clear(){

    }
    private fun initRecyclerView() {
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        adapter = MyAdapter(dataList)
        recyclerView!!.adapter = adapter
    }

    inner class MyAdapter(mDataList: List<Post>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        private var mDataList = mDataList

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recommend_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = mDataList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var itemLayout: View = view.findViewById(R.id.card_view)
            var postFlag:TextView = view.findViewById(R.id.title_flag)
            var postTitle: TextView = view.findViewById(R.id.title_view)
            var newsCommentNumber: TextView = view.findViewById(R.id.comments_number)
            var newsTime: TextView = view.findViewById(R.id.release_time)
        }

    }

}