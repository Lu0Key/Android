package cn.edu.sdu.online.isdu.ui.activity

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.Schedule
import cn.edu.sdu.online.isdu.ui.design.ScheduleTable
import cn.edu.sdu.online.isdu.ui.design.dialog.AlertDialog
import cn.edu.sdu.online.isdu.util.EnvVariables
import cn.edu.sdu.online.isdu.util.PixelUtil
import cn.edu.sdu.online.isdu.util.ScheduleTime
import kotlinx.android.synthetic.main.activity_schedule.*

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/5/29
 *
 * 日程表活动
 ****************************************************
 */

class ScheduleActivity : SlideActivity(), View.OnClickListener {

    private var scheduleTable: ScheduleTable? = null
    private var totalWeeks = 20
    private var currentWeek = 1

    private var txtCurrentWeek: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var icIndicator: ImageView? = null
    private var btnBack: ImageView? = null
    private var btnAdd: ImageView? = null

    private var onWeekSelectListener: MyAdapter.OnWeekSelectListener? = null

    private var selectLayoutVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        if (EnvVariables.currentWeek == -1) {
            val dialog = AlertDialog(this)
            dialog.setTitle("无数据")
            dialog.setMessage("未获取到数据，请稍后重试")
            dialog.setCancelOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.setPositiveButton("返回") {
                finish()
                dialog.dismiss()
            }
            dialog.show()
        } else {
            initView()
            initSchedule()
        }
    }

    override fun onResume() {
        super.onResume()
        EnvVariables.init(this)
    }

    private fun initView() {
        scheduleTable = findViewById(R.id.schedule_table)
        recyclerView = findViewById(R.id.recycler_view)
        txtCurrentWeek = findViewById(R.id.txt_current_week)
        icIndicator = findViewById(R.id.ic_indicator)
        btnBack = findViewById(R.id.btn_back)
        btnAdd = findViewById(R.id.btn_add)

        txtCurrentWeek!!.setOnClickListener(this)
        btnBack!!.setOnClickListener(this)
        btnAdd!!.setOnClickListener(this)

        getTotalWeeks()
        getCurrentWeek()

        setCurrentWeek(currentWeek)

        initRecyclerView()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.txt_current_week -> {
                if (selectLayoutVisible)
                    hideWeekSelect()
                else
                    showWeekSelect()

                rotateIndicator(selectLayoutVisible)
                selectLayoutVisible = !selectLayoutVisible
            }
            R.id.btn_back -> {
                finish()
            }
            R.id.btn_add -> {
                startActivity(Intent(this, CreateScheduleActivity::class.java))
            }
        }
    }

    /**
     * 旋转上下标识
     *
     * @param orientation 和 selectLayoutVisible 同真值
     */
    private fun rotateIndicator(orientation: Boolean) {
        if (orientation) {
            val animator = ObjectAnimator.ofFloat(icIndicator!!, "rotation", -180f, 0f)
            animator.duration = 100
            animator.interpolator = DecelerateInterpolator()
            animator.start()
        } else {
            val animator = ObjectAnimator.ofFloat(icIndicator!!, "rotation", 0f, 180f)
            animator.duration = 100
            animator.interpolator = DecelerateInterpolator()
            animator.start()
        }
    }

    /**
     * 在初始化日程表时
     * 指定currentWeek
     * 从startWeek到endWeek中选取第currentWeek - 1个列表进行绘制
     */
    private fun initSchedule() {
        val list = ArrayList<ArrayList<Schedule>>()
        initScheduleData(list)

        scheduleTable!!.setOnItemClickListener {
            schedule ->
            val intent = Intent(this@ScheduleActivity, ScheduleDetailActivity::class.java)
            intent.putExtra("schedule", schedule)
            startActivity(intent)
        }
        scheduleTable!!.setScheduleList(list as List<MutableList<Schedule>>?)
    }

    private fun initRecyclerView() {
        val dataList = ArrayList<SelectableWeekIndex>()
        for (i in EnvVariables.startWeek until EnvVariables.endWeek + 1) {
            dataList.add(SelectableWeekIndex(i + 1))
        }
        dataList[currentWeek - 1].selected = true
        val adapter = MyAdapter(dataList)

        onWeekSelectListener = object : MyAdapter.OnWeekSelectListener {
            override fun onWeekSelect(index: Int) {
                setCurrentWeek(index)
            }
        }

        adapter.setOnWeekSelectListener(onWeekSelectListener!!)

        recyclerView!!.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView!!.adapter = adapter

        recyclerView!!.scrollToPosition(currentWeek - 1)
    }

    private fun initScheduleData(list: ArrayList<ArrayList<Schedule>>) {

    }

    private fun getTotalWeeks() {
        totalWeeks = EnvVariables.endWeek - EnvVariables.startWeek + 1
    }

    private fun getCurrentWeek() {
        currentWeek = EnvVariables.currentWeek
    }

    private fun setCurrentWeek(index: Int) {
        currentWeek = index
        scheduleTable!!.setCurrentWeekIndex(currentWeek)
        txtCurrentWeek!!.text = "第 $index 周"
    }

    private fun hideWeekSelect() {
        val px = PixelUtil.dp2px(this, 60)
        val animator = ObjectAnimator.ofFloat(scheduleTable!!, "translationY",
                scheduleTable!!.translationY, 0f)
        animator.duration = 100
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    private fun showWeekSelect() {
        val px = PixelUtil.dp2px(this, 60)
        val animator = ObjectAnimator.ofFloat(scheduleTable!!, "translationY",
                scheduleTable!!.translationY, px.toFloat())
        animator.duration = 100
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    class MyAdapter(dataList: List<SelectableWeekIndex>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        private val dataList = dataList
        private var onWeekSelectListener: OnWeekSelectListener? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.week_select_item, parent, false)

            return ViewHolder(view)
        }

        override fun getItemCount(): Int = dataList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemLayout.text = (position + 1).toString()
            holder.itemLayout.setBackgroundResource(
                    if (dataList[position].selected) R.drawable.purple_circle
                    else R.drawable.white_circle)
            holder.itemLayout.setOnClickListener {
                onWeekSelectListener?.onWeekSelect(position + 1)

                for (item in dataList) {
                    item.selected = false
                }
                dataList[position].selected = true

                notifyDataSetChanged()
            }
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var itemLayout: TextView = view.findViewById(R.id.item_layout)
        }

        fun setOnWeekSelectListener(onWeekSelectListener: OnWeekSelectListener) {
            this.onWeekSelectListener = onWeekSelectListener
        }

        interface OnWeekSelectListener {
            fun onWeekSelect(index: Int)
        }
    }

    class SelectableWeekIndex(index: Int) {
        var weekIndex: Int = 0
        var selected: Boolean = false

        init {
            weekIndex = index
        }
    }
}
