package cn.edu.sdu.online.isdu.ui.fragments.me

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import cn.edu.sdu.online.isdu.interfaces.PostViewable
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.ui.activity.PostDetailActivity
import cn.edu.sdu.online.isdu.util.Logger
import cn.edu.sdu.online.isdu.util.WeakReferences
import com.liaoinstan.springview.widget.SpringView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/25
 *
 * 个人主页我的帖子碎片
 ****************************************************
 */

class MePostsFragment : LazyLoadFragment(), PostViewable {

    private var recyclerView: RecyclerView? = null
    private var adapter: MyAdapter? = null
    private var dataList: MutableList<Post> = ArrayList()

    private var pullRefreshLayout: SpringView? = null

    private var uid = -1

    private var lastId = 0
    private var needOffset = false // 是否需要列表位移

    fun setUid(uid: Int) {
        this.uid = uid
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_me_articles, container, false)

        initView(view)
        initRecyclerView()
        initPullRefreshLayout()

        return view
    }

    override fun onResume() {
        super.onResume()
        adapter?.notifyDataSetChanged()
    }

    override fun removeItem(item: Any?) {
        var j = 0
        for (i in 0 until  dataList.size) {
            if (dataList[i].postId == item as Int) j = i
        }
        dataList.removeAt(j)
        adapter?.notifyDataSetChanged()
    }

    private fun initView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)
        pullRefreshLayout = view.findViewById(R.id.pull_refresh_layout)

    }

    private fun initRecyclerView() {
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        adapter = MyAdapter(dataList, context!!)
        recyclerView!!.adapter = adapter
    }

    override fun loadData() {
        NetworkAccess.buildRequest(ServerInfo.getPostList(uid,
                if (dataList.size > 0) dataList[dataList.size - 1].postId - 1 else 0),
                object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        Logger.log(e)
                        activity!!.runOnUiThread {
                            pullRefreshLayout!!.onFinishFreshAndLoad()
                        }
                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        activity!!.runOnUiThread {
                            pullRefreshLayout!!.onFinishFreshAndLoad()
                        }
                        try {
                            val list = ArrayList<Post>()
                            val str = response?.body()?.string()
                            val jsonObj = JSONObject(str)

                            val jsonArr = JSONArray(jsonObj.getString("obj"))

                            for (i in 0 until jsonArr.length()) {
                                val obj = jsonArr.getJSONObject(i)
                                val post = Post()

                                post.postId = obj.getInt("id")
                                post.commentsNumbers = obj.getInt("commentNumber")
                                post.collectNumber = obj.getInt("collectNumber")
                                post.likeNumber = obj.getInt("likeNumber")
                                post.uid = obj.getString("uid")
                                post.title = obj.getString("title")
                                post.time = obj.getString("time").toLong()
                                post.content = obj.getString("info")

                                if (!list.contains(post))
                                    list.add(post)
                            }

                            activity!!.runOnUiThread {
                                publishLoadData(list)
                            }
                        } catch (e: Exception) {
                            Logger.log(e)
                        }
                    }
                })
    }

    /**
     * 下拉刷新发布最新帖子信息
     */
    private fun publishNewData(list: List<Post>) {
        if (list.isEmpty()) {

        } else {
            dataList.clear()
            dataList.addAll(list)
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun publishLoadData(list: List<Post>) {
        if (list.isNotEmpty()) {
            dataList.addAll(list)
            adapter!!.notifyDataSetChanged()
            if (needOffset)
                recyclerView!!.smoothScrollBy(0, 100)
        }
    }

    override fun publishData() {
        super.publishData()
    }

    /**
     * 初始化下拉刷新
     */
    private fun initPullRefreshLayout() {
        // 下拉刷新监听器
        pullRefreshLayout!!.setListener(object : SpringView.OnFreshListener {
            override fun onLoadmore() {
                loadData()
                needOffset = true
            }

            override fun onRefresh() {
                dataList.clear()
                needOffset = false
                loadData()
            }
        })

    }


    inner class MyAdapter(dataList: List<Post>?, context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        private val dataList = dataList

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = dataList!![position]
            holder.cardView.setOnClickListener {
                WeakReferences.postViewableWeakReference = WeakReference(this@MePostsFragment)
                context!!.startActivity(Intent(context, PostDetailActivity::class.java)
                        .putExtra("id", item.postId)
                        .putExtra("uid", item.uid)
                        .putExtra("title", item.title)
                        .putExtra("time", item.time)
                        .putExtra("tag", TAG))
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("uid", uid)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState?.getInt("uid") != null)
            uid = savedInstanceState.getInt("uid")
    }

    companion object {
        const val TAG = "MeArticleFragment"
    }
}