package cn.edu.sdu.online.isdu.ui.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.bean.Grade

/**
 ****************************************************
 * @author Cola_Mentos
 * Last Modifier: Cola_Mentos
 * Last Modify Time: 2018/7/11
 *
 * 历年成绩碎片
 ****************************************************
 */
class FragmentPastGradeDetail : Fragment() {

    private var recyclerView : RecyclerView?= null
    private var textView : TextView?= null
    private var adapter: MyAdapter? = null
    private var dataList: MutableList<Grade> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_past_grade_detail, container, false)
        initView(view)
        initRecyclerView()

        return view
    }

    fun initView(v : View){

        recyclerView = v.findViewById(R.id.recycler_view)
        textView = v.findViewById(R.id.text_view)

    }

    /**
     * 初始化RecyclerView
     */
    private fun initRecyclerView() {
        adapter = MyAdapter(dataList, context!!)

        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = adapter
    }

    inner class MyAdapter(dataList: List<Grade>?, context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        private val context = context
        private val dataList = dataList

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (!dataList!!.isEmpty()){
                textView!!.text = ""
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.grade_item, parent, false)
            return ViewHolder(v = view)
        }

        override fun getItemCount(): Int = dataList?.size ?: 0

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val cjText : TextView = v.findViewById(R.id.cj) // 成绩
            val jdText : TextView = v.findViewById(R.id.jd) // 绩点
        }
    }

}