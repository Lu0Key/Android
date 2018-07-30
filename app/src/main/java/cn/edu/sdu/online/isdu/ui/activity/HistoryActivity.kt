package cn.edu.sdu.online.isdu.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.Post
import cn.edu.sdu.online.isdu.interfaces.PostViewable
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.ui.fragments.me.MePostsFragment
import cn.edu.sdu.online.isdu.util.Logger
import cn.edu.sdu.online.isdu.util.WeakReferences
import cn.edu.sdu.online.isdu.util.database.DAOHistory
import cn.edu.sdu.online.isdu.util.history.History
import com.alibaba.fastjson.JSONObject


import kotlinx.android.synthetic.main.activity_history.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat


class HistoryActivity : SlideActivity(), View.OnClickListener, PostViewable{

    private var mAdapter: MyAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var btnBack: ImageView? = null
    private var btnClear:TextView? = null
    private var blankView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        initView()
        initData()
        blankView!!.visibility = if (History.historyList.isEmpty()) View.VISIBLE else View.GONE
        initRecyclerView()
    }

    private fun initView() {
        recyclerView = recycler_view
        btnBack = btn_back
        btnClear = clear
        blankView = findViewById(R.id.blank_view)
        btnBack!!.setOnClickListener(this)
        btnClear!!.setOnClickListener(this)
    }

    override fun removeItem(item: Any?) {
        History.historyList.remove(item)
        mAdapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        mAdapter?.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_back -> {
                finish()
            }
            R.id.clear -> {
                History.removeAllHistory()
                mAdapter!!.notifyDataSetChanged()
                blankView!!.visibility = View.VISIBLE
            }
        }
    }
    private fun initData(){
//        dataList=dao_history!!.history
    }
    private fun initRecyclerView() {
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        mAdapter = MyAdapter()
        recyclerView!!.adapter = mAdapter
    }

    inner class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = History.historyList[position]
            holder.cardView.setOnClickListener {
                WeakReferences.postViewableWeakReference = WeakReference(this@HistoryActivity)
                startActivity(Intent(this@HistoryActivity, PostDetailActivity::class.java)
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

        override fun getItemCount(): Int = History.historyList.size

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

    companion object {
        val TAG = "HistoryActivity"
    }
}
