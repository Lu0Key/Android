package cn.edu.sdu.online.isdu.ui.fragments.search

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.LazyLoadFragment
import cn.edu.sdu.online.isdu.bean.Post
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.ui.activity.PostDetailActivity
import cn.edu.sdu.online.isdu.ui.fragments.MePostsFragment
import cn.edu.sdu.online.isdu.util.Logger
import cn.edu.sdu.online.isdu.util.WeakReferences
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat

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
    private var isLoading = false
    private var isLoadComplete = false


    private var lastSearchString = ""
    private var searchCall: Call? = null
    //private var isLoadComplete = false

    fun setSearch(search: String?){
        this.search = search
        if(userVisibleHint && (isLoadComplete || search != lastSearchString)){
            if (searchCall != null && !searchCall!!.isCanceled) searchCall!!.cancel()
            isLoadComplete = false

            lastSearchString = search!!
            loadData()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_post, container, false)
        initView(view)
        initRecyclerView()
        return view
    }

    private fun initView(view: View) {
        loadingLayout = view.findViewById(R.id.loading_layout)
        recyclerView = view.findViewById(R.id.recycler_view)
    }

    override fun isLoadComplete(): Boolean = isLoadComplete && lastSearchString == search

    override fun loadData() {
        super.loadData()
        if(search != null){
            isLoading = true
//            onLoading()
            var url = ServerInfo.queryPost(search)
            searchCall = NetworkAccess.buildRequest(url, object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    activity!!.runOnUiThread {
                        Logger.log(e)
                        Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onResponse(call: Call?, response: Response?) {
                    val json = response?.body()?.string()
                    try {
                        Thread(Runnable {
                            synchronized(dataList) {
                                dataList.clear()

                                    val jsonArray = JSONArray(JSONObject(json).getString("obj"))
                                    for (k in 0 until jsonArray.length()) {
                                        val obj = jsonArray.getJSONObject(k)
                                        val item = Post()
                                        item.postId = obj.getInt("id")
                                        item.uid = obj.getString("uid")
                                        item.time = obj.getString("time").toLong()
                                        item.title = obj.getString("title")
                                        item.content = obj.getString("info")
                                        item.likeNumber = obj.getInt("likeNumber")
                                        item.commentsNumbers = obj.getInt("commentNumber")
//                                        item.isLiked = myLikeList.contains(item.uid.toString())
                                        dataList.add(item)
                                    }
                                    activity!!.runOnUiThread {
                                        isLoadComplete = true
                                        publishData()
                                    }

                            }
                        }).start()

                    } catch (e: Exception) {
                        Logger.log(e)
                        activity!!.runOnUiThread {
                            Toast.makeText(context, "网络错误\n服务器无响应", Toast.LENGTH_SHORT).show()
                        }
                    }
                    activity!!.runOnUiThread {
                        isLoadComplete = true
                        publishData()
                    }
                }
            })
        }
    }

    override fun publishData() {
        if(adapter != null){
            adapter!!.notifyDataSetChanged()
        }
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
            val item = dataList[position]
            holder.cardView.setOnClickListener {
                context!!.startActivity(Intent(context, PostDetailActivity::class.java)
                        .putExtra("id", item.postId)
                        .putExtra("uid", item.uid)
                        .putExtra("title", item.title)
                        .putExtra("time", item.time))
            }
            holder.titleView.text = item.title
            holder.commentNumber.text = item.commentsNumbers.toString()
            holder.content.text = item.content
            holder.txtLike.text = item.likeNumber.toString()
            holder.releaseTime.text = if (System.currentTimeMillis() - item.time < 60 * 1000)
                "刚刚" else (if (System.currentTimeMillis() - item.time < 24 * 60 * 60 * 1000)
                "${(System.currentTimeMillis() - item.time) / (60 * 60 * 1000)} 小时前" else (
                    if (System.currentTimeMillis() - item.time < 48 * 60 * 60 * 1000) "昨天 ${SimpleDateFormat("HH:mm").format(item.time)}"
                    else SimpleDateFormat("yyyy-MM-dd HH:mm").format(item.time)))
        }

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val cardView: FrameLayout = v.findViewById(R.id.card_view)
            val titleView: TextView = v.findViewById(R.id.title_view) // 标题
            //            val contentLayout: LinearLayout = v.findViewById(R.id.content_layout) // 内容Layout
//            val userName: TextView = v.findViewById(R.id.user_name) // 用户名
            val commentNumber: TextView = v.findViewById(R.id.comments_number) // 评论数
            val releaseTime: TextView = v.findViewById(R.id.release_time) // 发布时间
            val content: TextView = v.findViewById(R.id.content)
            val txtLike: TextView = v.findViewById(R.id.like_count)
        }

    }

}