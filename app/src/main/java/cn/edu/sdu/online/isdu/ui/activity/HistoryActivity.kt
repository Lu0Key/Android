package cn.edu.sdu.online.isdu.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.interfaces.PostViewable
import cn.edu.sdu.online.isdu.ui.adapter.PostItemAdapter
import cn.edu.sdu.online.isdu.util.WeakReferences
import cn.edu.sdu.online.isdu.util.history.History


import kotlinx.android.synthetic.main.activity_history.*
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat


class HistoryActivity : SlideActivity(), View.OnClickListener, PostViewable{

    private var mAdapter: PostItemAdapter? = null
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
        mAdapter = PostItemAdapter(this, History.historyList)
        recyclerView!!.adapter = mAdapter
    }

    companion object {
        val TAG = "HistoryActivity"
    }
}
