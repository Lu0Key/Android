package cn.edu.sdu.online.isdu.ui.fragments.search

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.LazyLoadFragment
import cn.edu.sdu.online.isdu.bean.News
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.ui.activity.NewsActivity
import cn.edu.sdu.online.isdu.util.FileUtil
import cn.edu.sdu.online.isdu.util.Logger
import org.json.JSONArray
import java.util.regex.Pattern

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/17
 *
 * 搜索资讯的Fragment
 ****************************************************
 */

class SearchNewsFragment : LazyLoadFragment() {

    private var dataList: MutableList<News> = arrayListOf<News>()
    private var adapter: MyAdapter? = null
    private var loadingLayout: View? = null
    private var recyclerView: RecyclerView? = null
    private var blankView: TextView? = null
    private var search : String? = null
    private var isLoadComplete = false
    private val section = listOf("sduonline","undergraduate","sduyouth","sduview")
    private val sectionName = listOf("学生在线", "本科生院", "青春山大", "山大视点")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_news, container, false)
        initView(view)
        initRecyclerView()
        return view
    }

    fun setSearch(search: String?){
        this.search = search
        isLoadComplete = false
        if(this.isVisible){
            loadData()
        }
    }

    private fun initView(view: View) {
        loadingLayout = view.findViewById(R.id.loading_layout)
        recyclerView = view.findViewById(R.id.recycler_view)
        blankView = view.findViewById(R.id.blank_view)
    }

    override fun isLoadComplete(): Boolean {
        return isLoadComplete
    }

    override fun loadData() {
        super.loadData()
        if(search!=null){
            val pattern = Pattern.compile(search, Pattern.CASE_INSENSITIVE)
            onLoading()
            dataList.clear()
            try {
                for(j in 0 until section.size) {
                    val cachePath = "ews_api_index.php.site=" + section[j]
                    val jsonArray = JSONArray(FileUtil.getStringFromFile(Environment.getExternalStorageDirectory().toString() + "/iSDU/cache/" + cachePath))
                    for (i in 0 until jsonArray.length()) {
                        val jsonObj = jsonArray.getJSONObject(i)
                        val title = jsonObj.getString("title")
                        val matcher1 = pattern.matcher(title)
                        //val matcher2 = pattern.matcher(jsonObj.getString("block"))
                        //Log.d(section[j],title)
                        if(matcher1.find()){
                            val news = News()
                            news.title = title
                            news.date = jsonObj.getString("date")
                            news.source = jsonObj.getString("block")
                            news.section = sectionName[j]
                            news.url = ServerInfo.getNewsUrl(j, i)
                            dataList.add(news)
                        }
                    }
                }
                Log.d("news",dataList.size.toString())
            } catch (e: Exception) {
                Logger.log(e)
            }
            activity!!.runOnUiThread {
                isLoadComplete = true
                publishData()
            }
        }
    }

    override fun publishData() {
        super.publishData()
        super.publishData()
        if(dataList.size!= 0){
            recyclerView!!.visibility = View.VISIBLE
            loadingLayout!!.visibility = View.GONE
            blankView!!.visibility = View.GONE
        }else{
            recyclerView!!.visibility = View.GONE
            loadingLayout!!.visibility = View.GONE
            blankView!!.visibility = View.VISIBLE
        }
        if(adapter!=null){
            adapter!!.notifyDataSetChanged()
        }
    }

    fun onLoading(){
        recyclerView!!.visibility = View.GONE
        loadingLayout!!.visibility = View.VISIBLE
        blankView!!.visibility = View.GONE
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
                //clear()
                //(activity as SearchActivity).editSearch!!.setText("")
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