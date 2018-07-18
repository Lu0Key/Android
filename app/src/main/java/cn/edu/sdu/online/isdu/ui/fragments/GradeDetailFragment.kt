package cn.edu.sdu.online.isdu.ui.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.bean.Grade
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.ui.design.MyLinearLayoutManager
import cn.edu.sdu.online.isdu.ui.design.dialog.AlertDialog
import cn.edu.sdu.online.isdu.util.FileUtil
import cn.edu.sdu.online.isdu.util.Logger
import org.json.JSONArray
import org.json.JSONObject
import java.math.RoundingMode
import java.text.NumberFormat

/**
 ****************************************************
 * @author Cola_Mentos
 * Last Modifier: Cola_Mentos
 * Last Modify Time: 2018/7/11
 *
 * 本学期成绩碎片
 ****************************************************
 */
class GradeDetailFragment : Fragment() {

    private var recyclerView : RecyclerView ?= null
    private var textView : TextView ?= null
    private var adapter: MyAdapter? = null
    private var zjdText : TextView ?= null
    private var dataList: MutableList<Grade> = ArrayList()
    private var jdLayout : LinearLayout ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_grade_detail, container, false)
        initView(view)

        initRecyclerView()
        getInfo()
        return view
    }

    fun initView(v : View){

        recyclerView = v.findViewById(R.id.recycler_view)
        textView = v.findViewById(R.id.text_view)
        zjdText = v.findViewById(R.id.jd_text)
        jdLayout = v.findViewById(R.id.jd_layout)
    }

    private fun getInfo() {


        var url: String
        url = ServerInfo.getGradeUrl(User.staticUser.uid.toString())
        NetworkAccess.cache(url) { success, cachePath ->
            if (success) {
                try {
                    dataList.clear()
                    dataList.addAll(Grade.loadFromString(FileUtil.getStringFromFile(cachePath)))
                    activity!!.runOnUiThread {
                        val  nf : NumberFormat= NumberFormat.getNumberInstance();
                        //保留两位小数
                        nf.maximumFractionDigits = 2
                        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
                        nf.roundingMode=RoundingMode.UP
                        zjdText!!.text = nf.format(Grade.zjd)
                        textView!!.visibility = View.GONE
                        adapter!!.notifyDataSetChanged()
                    }

                } catch (e: Exception) {
                    Logger.log(e)
                }
            }else{

            }

        }
    }

    /**
     * 初始化RecyclerView
     */
    private fun initRecyclerView() {
        recyclerView!!.layoutManager = MyLinearLayoutManager(context,LinearLayoutManager.VERTICAL, false)
        adapter = MyAdapter(dataList)
        recyclerView!!.adapter = adapter
    }

    inner class MyAdapter(dataList : List<Grade>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        private val dataList = dataList

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val grade = dataList[position]

            holder.cjText.text = grade.cj
            holder.kcmText.text = grade.kcm
            holder.pscjText.text = grade.pscj
            holder.qmcjText.text = grade.qmcj
            holder.ddText.text = grade.dd
            holder.jdText.text = grade.jd.toString()
            holder.pmText.text = grade.pm.toString()
            holder.zrsText.text = grade.zrs.toString()
            holder.zgfText.text = grade.zgf
            holder.zdfext.text = grade.zdf

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.grade_item, parent, false)
            return ViewHolder(v = view)
        }

        override fun getItemCount(): Int = dataList.size

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val kcmText : TextView = v.findViewById(R.id.kcm) // 课程名
            val cjText : TextView = v.findViewById(R.id.cj) // 成绩
            val pscjText : TextView = v.findViewById(R.id.pscj) // 平时成绩
            val qmcjText : TextView = v.findViewById(R.id.qmcj) // 期末成绩
            val jdText : TextView = v.findViewById(R.id.jd) // 绩点
            val ddText : TextView = v.findViewById(R.id.dd) // 等第
            val zgfText : TextView = v.findViewById(R.id.zgf) // 等第
            val zdfext : TextView = v.findViewById(R.id.zdf) // 等第
            val pmText : TextView = v.findViewById(R.id.pm) // 排名
            val zrsText : TextView = v.findViewById(R.id.zrs) // 总人数
        }
    }

}
