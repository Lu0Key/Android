package cn.edu.sdu.online.isdu.ui.fragments.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.LazyLoadFragment
import cn.edu.sdu.online.isdu.bean.Post
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.ui.activity.PostDetailActivity
import cn.edu.sdu.online.isdu.util.FileUtil
import cn.edu.sdu.online.isdu.util.Logger
import com.liaoinstan.springview.widget.SpringView
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat

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

class HomeSchoolFragment : LazyLoadFragment() {

    private var recyclerView: RecyclerView? = null
    private var adapter: MyAdapter? = null
    //    private var updateBar: TextView? = null
    private var pullRefreshLayout: SpringView? = null
    private var dataList: MutableList<Post> = ArrayList()
//    private var blankView: TextView? = null

    private var lastValue = 0
    private var needOffset = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home_recommend, container, false)
        initView(view)
        initRecyclerView()
        initPullRefreshLayout()

        return view
    }

    /**
     * 初始化View
     */
    private fun initView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)
//        updateBar = view.findViewById(R.id.update_bar)
        pullRefreshLayout = view.findViewById(R.id.pull_refresh_layout)
//        blankView = view.findViewById(R.id.blank_view)

//        updateBar!!.translationY = -100f
//        blankView!!.visibility = View.GONE
    }

    /**
     * 初始化RecyclerView
     */
    private fun initRecyclerView() {
        adapter = MyAdapter()

        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = adapter
    }

    /**
     * 初始化下拉刷新
     */
    private fun initPullRefreshLayout() {
        // 下拉刷新监听器
        pullRefreshLayout!!.setListener(object : SpringView.OnFreshListener {
            override fun onLoadmore() {
                // 上拉加载更多
                lastValue = if (dataList.isEmpty()) 0 else dataList[dataList.size - 1].postId
                needOffset = true
                loadData()
            }

            override fun onRefresh() {
                // 下拉刷新
                lastValue = 0
                dataList.clear()
                needOffset = false
                loadData()
            }
        })

    }

    override fun loadData() {
        NetworkAccess.cache(ServerInfo.getSchoolAboutList(lastValue)) { success, cachePath ->
            if (success) {
                try {
                    val arr = JSONArray(JSONObject(FileUtil.getStringFromFile(cachePath)).getString("obj"))
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        val post = Post()
                        post.uid = obj.getString("uid")
                        post.commentsNumbers = obj.getInt("commentNumber")
                        post.postId = obj.getInt("id")
                        post.time = obj.getString("time").toLong()
                        post.title = obj.getString("title")
                        post.likeNumber = obj.getInt("likeNumber")
                        post.content = obj.getString("info")

                        if (!dataList.contains(post))
                            dataList.add(post)

                        lastValue = post.postId
                    }
                } catch (e: Exception) {
                    Logger.log(e)
                }
            } else {

            }

            activity?.runOnUiThread {
                adapter?.notifyDataSetChanged()
                if (needOffset) recyclerView?.smoothScrollBy(0, 50)
                pullRefreshLayout!!.onFinishFreshAndLoad()
            }
        }
    }

    inner class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.recommend_item, parent, false)
            return ViewHolder(v = view)
        }

        override fun getItemCount(): Int = dataList?.size ?: 0

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