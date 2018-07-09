package cn.edu.sdu.online.isdu.ui.activity

import android.animation.ObjectAnimator
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.Book
import cn.edu.sdu.online.isdu.bean.Post
import cn.edu.sdu.online.isdu.interfaces.OnRefreshListener
import cn.edu.sdu.online.isdu.ui.design.recyclerviewpack.PullRefreshLayout
import cn.edu.sdu.online.isdu.ui.fragments.FragmentHomeRecommend
import kotlinx.android.synthetic.main.activity_library.*
import java.util.*

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/6/5
 *
 * 图书馆活动
 ****************************************************
 */

class LibraryActivity : SlideActivity(), View.OnClickListener{

    private var backBtn : ImageView ?= null
    private var recyclerView: RecyclerView ?= null
    private var adapter: LibraryActivity.MyAdapter? = null
//    private var updateBar: TextView ?= null
    private var pullRefreshLayout: PullRefreshLayout ?= null
    private val dataList: MutableList<Book> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        initView()
        initRecyclerView()
        initPullRefreshLayout()
//        hideUpdateBar()
    }

    /**
     * 初始化view
     */
    private fun initView(){
//        updateBar = update_bar
        recyclerView = recycler_view
        backBtn = btn_back
        pullRefreshLayout = pull_refresh_layout
        backBtn!!.setOnClickListener(this)

    }

    override fun onClick(v : View){
       when (v.id){
           btn_back.id -> {
               finish()
           }
       }
    }

    /**
     * 初始化RecyclerView
     */
    private fun initRecyclerView() {
        adapter = MyAdapter(dataList, this)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = adapter
    }

    /**
     * 初始化下拉刷新
     */
    private fun initPullRefreshLayout() {
        // 下拉刷新监听器
        pullRefreshLayout!!.setOnRefreshListener {

            val onRefreshListener = OnRefreshListener { result, data ->
//                showUpdateBar(Random().nextInt(20))
                pullRefreshLayout!!.setRefreshing(false)
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
/*
    /**
     * 显示更新条
     *
     * @param updateNumber 更新内容的数量，大于等于0
     */
    private fun showUpdateBar(updateNumber: Int) {
        if (updateNumber < 0) return

        if (updateNumber == 0) {
            updateBar!!.text = "没有新的借阅记录"
        } else {
            updateBar!!.text = "发现 $updateNumber 条新的借阅记录"
        }

        updateBar!!.visibility = View.VISIBLE

        Handler().postDelayed({hideUpdateBar()}, 3000)
    }

    /**
     * 隐藏更新条
     */
    private fun hideUpdateBar() {
        val animator = ObjectAnimator.ofFloat(updateBar!!, "translationY", 0f, -100f, 0f)

        animator.duration = 200
        animator.interpolator = DecelerateInterpolator()
        animator.start()

        Handler().postDelayed({
            this.runOnUiThread { updateBar!!.visibility = View.GONE }
        }, 100)
    }

*/
    /**
     * recyclerView容器类
     */
    internal inner class MyAdapter(dataList: List<Book>?, context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        private val context = context
        private val dataList = dataList

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (position == 0){
                holder.line.visibility = View.GONE
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.book_item, parent, false)
            return ViewHolder(v = view)
        }

        override fun getItemCount(): Int = 2 //dataList?.size ?: 0

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val bookName : TextView = v.findViewById(R.id.book_name) // 借阅书名
            val idNumber : TextView = v.findViewById(R.id.id_number) // 借阅号
            val bookPlace : TextView = v.findViewById(R.id.book_place) // 借阅地点
            val borrowDate : TextView = v.findViewById(R.id.borrow_date) //借阅日期
            val backDate : TextView = v.findViewById(R.id.back_date) // 应还日期
            val remainDays : TextView = v.findViewById(R.id.remain_days) // 剩余天数
            val borrowTimes : TextView = v.findViewById(R.id.borrow_times) // 续借次数
            val line : View = v.findViewById(R.id.separate_line) // 分割线
        }
    }

}
