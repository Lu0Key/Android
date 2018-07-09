package cn.edu.sdu.online.isdu.ui.activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.Book
import cn.edu.sdu.online.isdu.bean.Bus
import kotlinx.android.synthetic.main.activity_school_bus_table.*
import java.util.ArrayList

/**
 ****************************************************
 * @author Cola_Mentos
 * Last Modifier: Cola_Mentos
 * Last Modify Time: 2018/7/9
 *
 * 校车时间表活动
 ****************************************************
 */

class SchoolBusTable : SlideActivity() ,View.OnClickListener{

    private var searchNum : Int ?= null
    private var fromP : Int ?= null
    private var toP : Int ?= null
    private var backBtn : ImageView ?= null
    private var adapter: SchoolBusTable.MyAdapter? = null
    private var recyclerView: RecyclerView ?= null
    private val dataList: MutableList<Bus> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_bus_table)
        initView()
        initRecyclerView()
    }

    /**
     * 初始化view
     */
    private fun initView(){
        val intent = intent
        searchNum=intent.getIntExtra("searchNum",0)
        fromP=intent.getIntExtra("fromP",0)
        toP=intent.getIntExtra("toP",0)
        backBtn = btn_back
        recyclerView = recycler_view
        backBtn!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            btn_back.id->{
                finish();
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
     * recyclerView容器类
     */
    internal inner class MyAdapter(dataList: List<Bus>?, context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        private val context = context
        private val dataList = dataList

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.bus_item, parent, false)
            return ViewHolder(v = view)
        }

        override fun getItemCount(): Int = 2//dataList?.size ?: 0

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val busTime : TextView = v.findViewById(R.id.bus_time) // 发车时间
            val busFrom : TextView = v.findViewById(R.id.bus_from) // 起点
            val busPass : TextView = v.findViewById(R.id.bus_pass) // 途径
            val busTo : TextView = v.findViewById(R.id.bus_to) // 终点
        }
    }
}
