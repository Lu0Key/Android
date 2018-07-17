package cn.edu.sdu.online.isdu.ui.fragments.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.bean.News
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.ui.activity.NewsActivity
import cn.edu.sdu.online.isdu.util.FileUtil
import cn.edu.sdu.online.isdu.util.Logger
import org.json.JSONArray

class NewsContentFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private val sectionName = listOf("学生在线", "本科生院", "青春山大", "山大视点")
    private var index = 0
    private var dataList: MutableList<News> = ArrayList()
    private var adapter: MyAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_news_content, container, false)

        initView(view)

        getNewsList()
        return view
    }

    private fun initView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        adapter = MyAdapter(dataList)
    }

    private fun getNewsList() {
        NetworkAccess.cache(ServerInfo.getNewsUrl(index)) { success, cachePath ->
            if (success) {
                try {
                    val jsonArray = JSONArray(FileUtil.getStringFromFile(cachePath))

                    dataList.clear()

                    for (i in 0 until jsonArray.length()) {
                        val jsonObj = jsonArray.getJSONObject(i)
                        val news = News()
                        news.title = jsonObj.getString("title")
                        news.date = jsonObj.getString("date")
                        news.source = jsonObj.getString("source")
                        news.url = jsonObj.getString("url")
                        dataList.add(news)
                    }

                    adapter!!.notifyDataSetChanged()
                } catch (e: Exception) {
                    Logger.log(e)
                }
            } else {
                Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setArguments(index: Int) {
        this.index = index
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
                activity!!.startActivity(Intent(activity, NewsActivity::class.java)
                        .putExtra("section", sectionName[index])
                        .putExtra("url", news.url))
            }

            holder.newsDate.text = news.date
            holder.newsSource.text = news.source
            holder.newsTitle.text = news.title
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var itemLayout: View = view.findViewById(R.id.item_layout)
            var newsTitle: TextView = view.findViewById(R.id.news_item_title)
            var newsSource: TextView = view.findViewById(R.id.news_source)
            var newsDate: TextView = view.findViewById(R.id.news_date)
        }
    }

    companion object {
        fun newInstance(index: Int): NewsContentFragment {
            val fragment = NewsContentFragment()
            fragment.setArguments(index)
            return fragment
        }
    }
}