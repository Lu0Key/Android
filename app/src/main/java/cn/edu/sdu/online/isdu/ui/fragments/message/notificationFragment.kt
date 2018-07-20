package cn.edu.sdu.online.isdu.ui.fragments.message

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.bean.News
import cn.edu.sdu.online.isdu.ui.activity.NewsActivity


class notificationFragment : Fragment(){

    private var dataList: MutableList<News> = arrayListOf<News>()
    private var adapter: MyAdapter? = null
    private var loadingLayout: View? = null
    private var recyclerView: RecyclerView? = null
    private var blankView: TextView? = null
    private var isLoadComplete = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        initView(view)
        loadData()
        initRecyclerView()
        return view
    }

    private fun initRecyclerView() {
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        adapter = MyAdapter(dataList)
        recyclerView!!.adapter = adapter
    }

    private fun initView(view: View) {
        loadingLayout = view.findViewById(R.id.loading_layout)
        recyclerView = view.findViewById(R.id.recycler_view)
        blankView = view.findViewById(R.id.blank_view)
    }



     fun loadData() {
         onLoading()
         //TODO 消息碎片数据加载
         isLoadComplete = true
         publishData()
    }

     fun publishData() {
         Log.w("messageFragment","publishData")
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
                activity!!.startActivity(Intent(activity, NewsActivity::class.java))
                //TODO(启动通知详情的putExtra)
                Log.w("notificationItemClick",position.toString())
            }
            holder.title.text = news.title
            holder.news_source.text = news.source
            holder.news_date.text = news.date
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemLayout: FrameLayout = view.findViewById(R.id.item_layout)
            var title: TextView = view.findViewById(R.id.news_item_title)
            var news_source: TextView = view.findViewById(R.id.news_source)
            val news_date: TextView = view.findViewById(R.id.news_date)
        }
    }
}