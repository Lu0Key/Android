package cn.edu.sdu.online.isdu.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.Collect
import java.text.SimpleDateFormat
import java.util.*

class CollectActivity : SlideActivity() {
    private var collectList: MutableList<Collect> = ArrayList()

    private var btnCollectBack: View? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: CollectAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_me_collect)

        initView()

        initRecyclerView()

    }

    private fun initView() {
        btnCollectBack = findViewById(R.id.btn_back)
        recyclerView = findViewById(R.id.recycler_view)

        btnCollectBack!!.setOnClickListener {
            finish()
        }
    }

    private fun initRecyclerView() {
        recyclerView!!.layoutManager = LinearLayoutManager(this)
//        collectList = fileRead()
        adapter = CollectAdapter()
        recyclerView!!.adapter = adapter
    }


//    private fun fileRead(): List<Collect> {
//        val fileReader = FileReader(Environment.getExternalStorageDirectory().toString() + "/iSDU/cache")
//        val bufferedReader = BufferedReader(fileReader)
//        val fileStringBuffer = StringBuffer()
//        while (bufferedReader.readLine() != null) {
//            val fileString = bufferedReader.readLine()
//            fileStringBuffer.append(fileString)
//        }
//        val fileReadList = JSON.parseArray(fileStringBuffer.toString())
//        bufferedReader.close()
//        return fileReadList
//    }

    inner class CollectAdapter :
            RecyclerView.Adapter<CollectAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var collectTitle: TextView = view.findViewById(R.id.textview_collect_title)
            var collectContent: TextView = view.findViewById(R.id.textview_collect_content)
            var collectTime: TextView = view.findViewById(R.id.collect_time)
            var itemLayout: View = view.findViewById(R.id.item_layout)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_collect, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val collect = collectList[position]
            holder.collectTitle.text = collect.collectTitle
            holder.collectContent.text = collect.collectContent
            holder.collectTime.text = SimpleDateFormat("yyyy-MM-dd").format(collect.collectTime)
            holder.itemLayout.setOnClickListener {
                when (collect.collectType) {
                    Collect.TYPE_NEWS -> {
                        startActivity(Intent(this@CollectActivity, NewsActivity::class.java)
                                .putExtra("url", collect.collectUrl))
                    }
                    Collect.TYPE_POST -> {
                        startActivity(Intent(this@CollectActivity, PostDetailActivity::class.java)
                                .putExtra("url", collect.collectUrl))
                    }
                }
            }
        }

        override fun getItemCount(): Int = collectList.size

    }


}

