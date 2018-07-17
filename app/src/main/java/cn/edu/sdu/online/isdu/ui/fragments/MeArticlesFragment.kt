package cn.edu.sdu.online.isdu.ui.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
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
import cn.edu.sdu.online.isdu.bean.Post
import cn.edu.sdu.online.isdu.interfaces.OnRefreshListener
import cn.edu.sdu.online.isdu.ui.activity.MyHomePageActivity
import com.liaoinstan.springview.widget.SpringView
import kotlinx.android.synthetic.main.activity_my_home_page.*
import kotlin.collections.ArrayList

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/5/26
 *
 * 个人主页我的文章碎片
 ****************************************************
 */

class MeArticlesFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var adapter: MyAdapter? = null
    private var dataList: MutableList<Post> = ArrayList()

    private var pullRefreshLayout: SpringView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_me_articles, container, false)

        initView(view)
        initRecyclerView()
        initPullRefreshLayout()

        return view
    }

    private fun initView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)
        pullRefreshLayout = view.findViewById(R.id.pull_refresh_layout)

//        pullRefreshLayout!!.setAppBarLayout((activity as MyHomePageActivity).getAppBar())
    }

    private fun initRecyclerView() {
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        adapter = MyAdapter(dataList, context!!)
        recyclerView!!.adapter = adapter
    }


    /**
     * 初始化下拉刷新
     */
    private fun initPullRefreshLayout() {
        // 下拉刷新监听器
        pullRefreshLayout!!.setListener(object : SpringView.OnFreshListener {
            override fun onLoadmore() {
                val onRefreshListener = OnRefreshListener { result, data ->
                    dataList.add(Post())

                    adapter!!.notifyDataSetChanged()
                    pullRefreshLayout!!.onFinishFreshAndLoad()
                    recyclerView!!.smoothScrollBy(0, 250)
                }

                Handler().postDelayed({
                    onRefreshListener.onRefresh(0, 0)
                }, 2000)
            }

            override fun onRefresh() {

                val onRefreshListener = OnRefreshListener { result, data ->
                    pullRefreshLayout!!.onFinishFreshAndLoad()
                }

                Handler().postDelayed({
                    onRefreshListener.onRefresh(0, 0)
                }, 2000)
            }
        })

    }


    class MyAdapter(dataList: List<Post>?, context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        private val context = context
        private val dataList = dataList

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.recommend_item, parent, false)
            return ViewHolder(v = view)
        }

        override fun getItemCount(): Int = dataList?.size ?: 0

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val cardView: FrameLayout = v.findViewById(R.id.card_view)
            val titleView: TextView = v.findViewById(R.id.title_view) // 标题
            val contentLayout: LinearLayout = v.findViewById(R.id.content_layout) // 内容Layout
            val userName: TextView = v.findViewById(R.id.user_name) // 用户名
            val commentNumber: TextView = v.findViewById(R.id.comments_number) // 评论数
            val releaseTime: TextView = v.findViewById(R.id.release_time) // 发布时间
        }
    }


}