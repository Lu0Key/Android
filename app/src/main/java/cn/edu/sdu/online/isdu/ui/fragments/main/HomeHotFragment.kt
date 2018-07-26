package cn.edu.sdu.online.isdu.ui.fragments.main

import android.content.Context
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
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.LazyLoadFragment
import cn.edu.sdu.online.isdu.bean.Post
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.util.FileUtil
import com.liaoinstan.springview.widget.SpringView
import org.json.JSONArray
import org.json.JSONObject

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/25
 *
 * 主页热榜碎片
 * 热榜后台算法：
 * 采用加权求权值的方法，
 *  权值 = 点赞数 * 点赞权值 + 评论数 * 评论权值 + 发布时长权值 / 发布时长（天数 + 1）
 *  其中，点赞权值 =
 *       评论权值 =
 *       发布时长权值 =
 ****************************************************
 */


class HomeHotFragment : LazyLoadFragment() {

    private var springView: SpringView? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: MyAdapter? = null
    private var dataList: MutableList<Post> = ArrayList()

    private var lastHotValue = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home_hot, container, false)

        initView(view)
        initRecyclerView()
        initPullRefresh()

        return view
    }

    private fun initView(view: View) {
        springView = view.findViewById(R.id.pull_refresh_layout)
        recyclerView = view.findViewById(R.id.recycler_view)
    }

    private fun initRecyclerView() {
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        adapter = MyAdapter()
        recyclerView!!.adapter = adapter
    }

    private fun initPullRefresh() {
        springView!!.setListener(object : SpringView.OnFreshListener {
            override fun onLoadmore() {
                lastHotValue = if (dataList.isEmpty()) 0.0
                        else calculateValue(dataList[dataList.size - 1])
                loadData()
            }

            override fun onRefresh() {
                lastHotValue = 0.0
                dataList.clear()
                loadData()
            }
        })
    }

    private fun calculateValue(post: Post) : Double = 1000000.0 * (1 * post.likeNumber +
            2 * post.commentsNumbers) +
            (post.time % 100000000000 / 100000) + (post.time % 100000) / 100000

    override fun loadData() {
        super.loadData()
        NetworkAccess.cache(ServerInfo.getHotPostList(lastHotValue)) {success, cachePath ->
            if (success) {
                val str = FileUtil.getStringFromFile(cachePath)
                val arr = JSONArray(JSONObject(str).getJSONArray("obj"))
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val post = Post()
                    post.uid = obj.getString("uid")
                    post.commentsNumbers = obj.getInt("commentNumber")
                    post.postId = obj.getInt("id")
                    post.likeNumber = obj.getInt("likeNumber")
                    post.content = obj.getString("info")
                    post.title = obj.getString("title")
                    if (!dataList.contains(post)) dataList.add(post)
                }
                activity?.runOnUiThread {
                    adapter?.notifyDataSetChanged()
                }
            }
            activity?.runOnUiThread {
                springView!!.onFinishFreshAndLoad()
            }
        }
    }

    inner class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.recommend_item, parent, false)
            return ViewHolder(v = view)
        }

        override fun getItemCount(): Int = dataList.size

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val cardView: FrameLayout = v.findViewById(R.id.card_view)
            val titleView: TextView = v.findViewById(R.id.title_view) // 标题
            val contentLayout: LinearLayout = v.findViewById(R.id.content_layout) // 内容Layout
            val userName: TextView = v.findViewById(R.id.user_name) // 用户名
            val commentNumber: TextView = v.findViewById(R.id.comments_number) // 评论数
            val releaseTime: TextView = v.findViewById(R.id.release_time) // 发布时间
        }
    }
}