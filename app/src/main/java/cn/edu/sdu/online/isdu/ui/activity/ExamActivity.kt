package cn.edu.sdu.online.isdu.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.Exam
import kotlinx.android.synthetic.main.activity_exam.*

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/8
 *
 * 考试安排活动
 ****************************************************
 */

class ExamActivity : SlideActivity(), View.OnClickListener {

    private var dataList = arrayListOf<Exam>()
    private var mAdapter: MyAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var btnBack: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)

        initView()
        initRecyclerView()
    }

    private fun initView() {
        recyclerView = recycler_view
        btnBack = btn_back

        btnBack!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_back -> {
                finish()
            }
        }
    }

    private fun initRecyclerView() {
        dataList.add(Exam("2018年07月06日", "8:30-10:30", "软件园5区108d", "笔试：%", "闭卷", "大学物理（2）"))
        dataList.add(Exam("2018年07月04日", "8:30-10:30", "软件园1区108d", "笔试：%", "闭卷", "高等数学（2）"))
        dataList.add(Exam("2018年07月06日", "8:30-10:30", "软件园5区108d", "笔试：%", "闭卷", "大学物理（2）"))
        dataList.add(Exam("2018年07月04日", "8:30-10:30", "软件园1区108d", "笔试：%", "闭卷", "高等数学（2）"))
        dataList.add(Exam("2018年07月06日", "8:30-10:30", "软件园5区108d", "笔试：%", "闭卷", "大学物理（2）"))
        dataList.add(Exam("2018年07月04日", "8:30-10:30", "软件园1区108d", "笔试：%", "闭卷", "高等数学（2）"))

        mAdapter = MyAdapter(dataList)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = mAdapter
    }

    class MyAdapter(private val dataList: List<Exam>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.exam_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = dataList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val exam = dataList[position]
            holder.examName.text = exam.name
            holder.examType.text = exam.type
            holder.examDate.text = exam.date
            holder.examTime.text = exam.time
            holder.examLocation.text = exam.location
            holder.examRate.text = exam.gradeRate
            holder.blankView.visibility = if (position == 0) View.VISIBLE else View.GONE
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var examName: TextView = v.findViewById(R.id.exam_name)
            var examDate: TextView = v.findViewById(R.id.exam_date)
            var examTime: TextView = v.findViewById(R.id.exam_time)
            var examType: TextView = v.findViewById(R.id.exam_type)
            var examLocation: TextView = v.findViewById(R.id.exam_location)
            var examRate: TextView = v.findViewById(R.id.exam_rate)
            var blankView: View = v.findViewById(R.id.blank_view)
        }
    }
}
