package cn.edu.sdu.online.isdu.ui.activity

import android.content.ComponentCallbacks2
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.view.menu.MenuView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.History
import cn.edu.sdu.online.isdu.util.DataBase.DAO_history


import kotlinx.android.synthetic.main.activity_history.*
import java.text.SimpleDateFormat


class HistoryActivity : SlideActivity(), View.OnClickListener {

    private var dataList = arrayListOf<History>()
    private var mAdapter: MyAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var btnBack: ImageView? = null
    private var btnClear:TextView? = null
    private var blankView: TextView? = null
    private var dao_history:DAO_history? = null
    public var historyActivity:HistoryActivity? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyActivity=this;
        setContentView(R.layout.activity_history)
        initView()
        dao_history= DAO_history(this)
        dao_history!!.newHistory(History("学工部、武装部召开会议部署暑假工作","山大实训",12345L,"http://"))
        dao_history!!.newHistory(History("学工部、武装部召开会议部署暑假工作2","山大实训2",123456L,"http://2"))
        dao_history!!.newHistory(History("学工部、武装部召开会议部署暑假工作3","山大实训3",123456L,"http://3"))
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
        //dataList.add(History("学工部、武装部召开会议部署暑假工作","山大实训",12345L,"http://"))
    }
    private fun initRecyclerView() {
        mAdapter = MyAdapter(dataList)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = mAdapter
    }

    class MyAdapter(private val dataList: List<History>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = dataList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val history = dataList[position]
            holder.historyTitle.text = history.title
            holder.historySubject.text = history.subject
            holder.historyTime.text =SimpleDateFormat("yyyy-MM-dd HH:mm").format(history.time)
            holder.itemLayout.setOnClickListener(){
                Log.w("click",dataList[position].url)
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
