package cn.edu.sdu.online.isdu.ui.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.ui.design.button.RadioImageButton
import cn.edu.sdu.online.isdu.ui.design.dialog.WeekDialog
import cn.edu.sdu.online.isdu.util.EnvVariables

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/6/5
 *
 * 自习室活动
 ****************************************************
 */

class StudyRoomActivity : SlideActivity(), View.OnClickListener {

    private var dayNames = listOf("一", "二", "三", "四", "五", "六", "日")
    private var radioButtons = ArrayList<RadioImageButton>()
    private var selectLayout: LinearLayout? = null
    private var selectWeek: TextView? = null
    private var selectDay: TextView? = null
    private var weeks = ArrayList<Int>()
    private var days = ArrayList<Int>()
    private var btnBack: ImageView? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_room)

        initView()
        initSelection()
        updateData()

        updateRecyclerView()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_back -> {
                finish()
            }
        }
    }

    private fun initView() {
        radioButtons.add(findViewById(R.id.radio_zhongxin))
        radioButtons.add(findViewById(R.id.radio_honglou))
        radioButtons.add(findViewById(R.id.radio_baotuq))
        radioButtons.add(findViewById(R.id.radio_ruanjian))
        radioButtons.add(findViewById(R.id.radio_xinglong))
        radioButtons.add(findViewById(R.id.radio_qianfo))
        selectLayout = findViewById(R.id.select_layout)
        selectWeek = findViewById(R.id.select_week)
        selectDay = findViewById(R.id.select_day)
        recyclerView = findViewById(R.id.recycler_view)
        btnBack = findViewById(R.id.btn_back)

        btnBack!!.setOnClickListener(this)
        for (i in 0 until 6) {
            radioButtons[i].setOnClickListener {
                if (radioButtons[i].isSelected)
                    cancelAllClick()
                else {
                    cancelAllClick()
                    radioButtons[i].isSelected = true
                }
            }
        }

        selectLayout!!.setOnClickListener {
            val dialog = WeekDialog(this, 1, 20)
            dialog.setTitle("选择周/星期")
            val listWeek = ArrayList<WeekDialog.Week>()
            val listDay = ArrayList<WeekDialog.Day>()

            for (i in EnvVariables.startWeek until EnvVariables.endWeek + 1) {
                listWeek.add(WeekDialog.Week(i))
            }
            for (i in 0 until EnvVariables.endWeek - EnvVariables.startWeek + 1) {
                if (weeks.contains(i + EnvVariables.startWeek)) listWeek[i].chosen = true
            }

            for (i in 0 until 7) {
                listDay.add(WeekDialog.Day(dayNames[i]))
            }
            for (i in 0 until 7) {
                if (days.contains(i + 1)) listDay[i].chosen = true
            }

            dialog.setWeeks(listWeek)
            dialog.setDays(listDay)

            dialog.setSingleOption(true)
            dialog.setPositiveButton("确定") {
                weeks = dialog.weeks as ArrayList<Int>
                days = dialog.days as ArrayList<Int>
                updateData()
                dialog.dismiss()
            }
            dialog.setNegativeButton("取消") {dialog.dismiss()}
            dialog.show()
            dialog.show()
        }
    }

    private fun initSelection() {
        weeks.clear()
        if (EnvVariables.currentWeek == -1)
            EnvVariables.currentWeek = EnvVariables.calculateWeekIndex(System.currentTimeMillis())
        weeks.add(EnvVariables.currentWeek)

        days.clear()
        days.add(EnvVariables.getCurrentDay())
    }

    private fun cancelAllClick() {
        for (i in 0 until 6)
            radioButtons[i].isSelected = false
    }

    private fun updateData() {
        selectWeek!!.text = weeks[0].toString()
        selectDay!!.text = dayNames[days[0] - 1]
    }

    override fun onResume() {
        super.onResume()
        EnvVariables.init(this)
    }

    private fun getDataFromNet() {

    }

    private fun updateRecyclerView() {
        val adapter = MyAdapter(listOf("软件园1区", "软件园5区"))
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = adapter
    }

    class MyAdapter(list: List<String>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        private var list = list

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.study_room_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = list[position]
            holder.layout.setOnClickListener {

            }
        }

        override fun getItemCount(): Int = list.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textView: TextView = view.findViewById(R.id.study_room_name)
            var layout: View = view.findViewById(R.id.layout)
        }
    }
}
