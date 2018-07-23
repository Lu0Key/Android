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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.History
import cn.edu.sdu.online.isdu.bean.Post
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.util.Logger
import cn.edu.sdu.online.isdu.util.database.DAOHistory
import com.alibaba.fastjson.JSONObject


import kotlinx.android.synthetic.main.activity_history.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.lang.ref.WeakReference


class HistoryActivity : SlideActivity(), View.OnClickListener{
    private var dataList = arrayListOf<History>()
    private var mAdapter: MyAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var btnBack: ImageView? = null
    private var btnClear:TextView? = null
    private var blankView: TextView? = null
    private var dao_history: DAOHistory? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        initView()
        dao_history= DAOHistory(this)
        initData()
        blankView!!.visibility = if (dataList.isEmpty()) View.VISIBLE else View.GONE
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

    override fun onDestroy() {
        super.onDestroy()
        dao_history!!.close();
    }

  //override fun onResume() {
  //    super.onResume()
  //    dataList.clear()
  //    initData()
  //    Log.w("ha",dataList.size.toString())
  //    blankView!!.visibility = if (dataList.isEmpty()) View.VISIBLE else View.GONE
  //    mAdapter!!.notifyDataSetChanged()
  //}
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_back -> {
                finish()
                dao_history!!.close();
            }
            R.id.clear -> {
                dataList.clear()
                dao_history!!.clear();
                mAdapter!!.notifyDataSetChanged()
                blankView!!.visibility = View.VISIBLE
            }
        }
    }
    private fun initData(){
        dataList=dao_history!!.history
    }
    private fun initRecyclerView() {
        mAdapter = MyAdapter(dataList,this@HistoryActivity)
        recyclerView!!.layoutManager = LinearLayoutManager(this@HistoryActivity)
        recyclerView!!.adapter = mAdapter
    }

    class MyAdapter(private val dataList: List<History>,context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        private val context =context
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = dataList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val history = dataList[position]
            holder.historyTitle.text = history.title
            holder.historySubject.text = history.subject
            val timeDelta:Long = System.currentTimeMillis()-history.time
            if(((timeDelta/1000/60).toInt())<1){
                holder.historyTime.text ="刚刚"
            }else if((timeDelta/1000/60)<60){
                holder.historyTime.text =(((timeDelta/1000/60).toInt()).toString()+"分钟前")
            }else if((timeDelta/1000/60/60)<24){
                holder.historyTime.text =(((timeDelta/1000/60/60).toInt()).toString() + "小时前")
            }else if((timeDelta/1000/60/60/24)<30){
                holder.historyTime.text =(((timeDelta/1000/60/60/24).toInt()).toString() + "天前")
            }else if((timeDelta/1000/60/60/24/30)<12){
                holder.historyTime.text =(((timeDelta/1000/60/60/24/30).toInt()).toString() + "月前")
            }else {
                holder.historyTime.text =(((timeDelta/1000/60/60/24/365).toInt()).toString() + "年前")
            }
            try{
                val json = JSONObject.parseObject(history.url)
                val post = json.toJavaObject(Post::class.java)
                holder.itemLayout.setOnClickListener(){
                    NetworkAccess.buildRequest(ServerInfo.getPost(post.postId),
                        object : Callback {
                            override fun onResponse(call: Call?, response: Response?) {
                                if(response?.body()?.string()?.get(2)=='c'){
                                    (context as Activity).runOnUiThread{
                                        context.startActivity(Intent(context, PostDetailActivity::class.java)
                                                .putExtra("id",post.postId)
                                                .putExtra("uid", post.uid)
                                                .putExtra("title", post.title)
                                                .putExtra("time", 0L)
                                                .putExtra("tag", "HistoryActivity"))
                                    }
                                }else{
                                    (context as Activity).runOnUiThread{
                                        context.startActivity(Intent(context, NotFindPostActivity::class.java))
                                    }
                                }
                            }

                            override fun onFailure(call: Call?, e: IOException?) {
                                (context as Activity).runOnUiThread {
                                    Toast.makeText(context,"网络错误",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }catch (e : Exception){
                Logger.log(e)
            }
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var historyTitle: TextView = v.findViewById(R.id.txt_item_name)
            var historySubject: TextView = v.findViewById(R.id.txt_comment)
            var historyTime: TextView = v.findViewById(R.id.txt_time)
            var itemLayout: View = v.findViewById(R.id.item_layout)
        }
    }
}
