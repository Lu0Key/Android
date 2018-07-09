package cn.edu.sdu.online.isdu.ui.fragments

import android.animation.*
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.bean.Post
import cn.edu.sdu.online.isdu.ui.design.recyclerviewpack.PullRefreshLayout
import cn.edu.sdu.online.isdu.interfaces.OnRefreshListener
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView
import java.util.*
import kotlin.collections.ArrayList

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/5/25
 *
 * 主页论坛推荐碎片
 ****************************************************
 */

class FragmentHomeRecommend : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var adapter: MyAdapter? = null
    private var updateBar: TextView? = null
    private var pullRefreshLayout: PullRefreshLayout? = null
    private var dataList: MutableList<Post> = ArrayList()
    private var blankView: TextView? = null

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
        updateBar = view.findViewById(R.id.update_bar)
        pullRefreshLayout = view.findViewById(R.id.pull_refresh_layout)
        blankView = view.findViewById(R.id.blank_view)

        updateBar!!.translationY = -100f
        blankView!!.visibility = View.GONE
    }

    /**
     * 初始化RecyclerView
     */
    private fun initRecyclerView() {
        adapter = MyAdapter(dataList, context!!)

        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = adapter
    }

    /**
     * 初始化下拉刷新
     */
    private fun initPullRefreshLayout() {
        // 下拉刷新监听器
        pullRefreshLayout!!.setOnRefreshListener {

            val onRefreshListener = OnRefreshListener { result, data ->
                showUpdateBar(Random().nextInt(20))
                pullRefreshLayout!!.setRefreshing(false)

                blankView!!.visibility = if (dataList.isEmpty()) View.VISIBLE else View.GONE
            }

            Handler().postDelayed({
                onRefreshListener.onRefresh(0, 0)
            }, 2000)
        }

        // 上拉加载监听器
        pullRefreshLayout!!.setOnLoadListener {

            val onRefreshListener = OnRefreshListener { result, data ->
                adapter!!.notifyDataSetChanged()
                pullRefreshLayout!!.setLoading(false)
                recyclerView!!.smoothScrollBy(0, 250)
            }

            Handler().postDelayed({
                onRefreshListener.onRefresh(0, 0)
            }, 2000)

        }
    }

    /**
     * 显示更新条
     *
     * @param updateNumber 更新内容的数量，大于等于0
     */
    private fun showUpdateBar(updateNumber: Int) {
        if (updateNumber < 0) return

        if (updateNumber == 0) {
            updateBar!!.text = "没有更新内容"
        } else updateBar!!.text = "推荐引擎有 $updateNumber 条更新"

        showUpdateBar()

        Handler().postDelayed({hideUpdateBar()}, 3000)
    }

    /**
     * 隐藏更新条
     */
    private fun hideUpdateBar() {
        val animator = ObjectAnimator.ofFloat(updateBar!!, "translationY", 0f, -100f)

        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    /**
     * 显示更新条
     */
    private fun showUpdateBar() {
        val animator = ObjectAnimator.ofFloat(updateBar!!, "translationY", -100f, 0f)

        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()
        animator.start()
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