package cn.edu.sdu.online.isdu.ui.fragments.search

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.bean.News
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.ui.activity.NewsActivity
import cn.edu.sdu.online.isdu.ui.activity.SearchActivity
import cn.edu.sdu.online.isdu.util.FileUtil
import cn.edu.sdu.online.isdu.util.Logger
import org.json.JSONArray

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/17
 *
 * 搜索资讯的Fragment
 ****************************************************
 */

class SearchNewsFragment : Fragment() {

    private var dataList: MutableList<News> = ArrayList()
    private var adapter: MyAdapter? = null
    private var loadingLayout: View? = null
    private var recyclerView: RecyclerView? = null
    private var blankView: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_news, container, false)
        initView(view)
        initRecyclerView()
        return view
    }

    private fun initView(view: View) {
        loadingLayout = view.findViewById(R.id.loading_layout)
        recyclerView = view.findViewById(R.id.recycler_view)
        blankView = view.findViewById(R.id.blank_view)
    }

    fun initData(list : List<News>){
        dataList.clear()
        dataList.addAll(list)
        recyclerView!!.visibility = View.VISIBLE
        loadingLayout!!.visibility = View.GONE
        blankView!!.visibility = View.GONE
    }

    fun refresh(){
        adapter!!.notifyDataSetChanged()
    }

    fun clear(){
        dataList.clear();
        if(adapter!=null){
            adapter!!.notifyDataSetChanged()
        }
    }

    fun noResult(){
        clear()
        recyclerView!!.visibility = View.GONE
        loadingLayout!!.visibility = View.GONE
        blankView!!.visibility = View.VISIBLE
    }
    fun onLoading(){
        recyclerView!!.visibility = View.GONE
        loadingLayout!!.visibility = View.VISIBLE
        blankView!!.visibility = View.GONE
    }
    override fun onResume() {
        super.onResume()
        dataList.clear()
    }

    private fun initRecyclerView() {
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        adapter = MyAdapter(dataList)
        recyclerView!!.adapter = adapter
    }

    inner class MyAdapter(mDataList: List<News>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        private var mDataList = mDataList

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.news_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = mDataList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val news = mDataList[position]
            holder.itemLayout.setOnClickListener {
                clear()
                (activity as SearchActivity).editSearch!!.setText("")
                activity!!.startActivity(Intent(activity, NewsActivity::class.java)
                        .putExtra("section", news.section)
                        .putExtra("url", news.url))
            }

            holder.newsDate.text = news.date
            holder.newsSource.text = news.source + "-" + news.section
            holder.newsTitle.text = news.title
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var itemLayout: View = view.findViewById(R.id.item_layout)
            var newsTitle: TextView = view.findViewById(R.id.news_item_title)
            var newsSource: TextView = view.findViewById(R.id.news_source)
            var newsDate: TextView = view.findViewById(R.id.news_date)
        }
    }

}