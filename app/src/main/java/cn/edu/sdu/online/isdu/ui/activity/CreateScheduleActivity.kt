package cn.edu.sdu.online.isdu.ui.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import kotlinx.android.synthetic.main.activity_create_schedule.*
import kotlin.coroutines.experimental.coroutineContext
import android.widget.TextView
import android.widget.BaseAdapter
import cn.edu.sdu.online.isdu.ui.design.dialog.WeekDialog


/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/8
 *
 * 新建日程活动
 ****************************************************
 */

class CreateScheduleActivity : SlideActivity(), View.OnClickListener {

    private var btnBack: ImageView? = null
    private var scheduleName: EditText? = null
    private var scheduleLocation: EditText? = null
    private var scheduleWeeks: TextView? = null
    private var scheduleTimeStart: TextView? = null
    private var scheduleTimeEnd: TextView? = null
    private var repeatType: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_schedule)

        initView()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_back -> {
                finish()
            }
            R.id.schedule_weeks -> {
                val dialog = WeekDialog(this, 1, 20)
                dialog.show()
            }
            R.id.schedule_time_start -> {

            }
            R.id.schedule_time_end -> {

            }
        }
    }

    private fun initView() {
        btnBack = btn_back
        scheduleName = schedule_name
        scheduleLocation = schedule_location
        scheduleWeeks = schedule_weeks
        scheduleTimeStart = schedule_time_start
        scheduleTimeEnd = schedule_time_end
        repeatType = repeat_type

        btnBack!!.setOnClickListener(this)
        scheduleWeeks!!.setOnClickListener(this)
        scheduleTimeStart!!.setOnClickListener(this)
        scheduleTimeEnd!!.setOnClickListener(this)

        ///Spinner
        val adapter = MySpinnerAdapter(this)
        repeatType!!.adapter = adapter
        repeatType!!.setSelection(0)
    }


    class MySpinnerAdapter(context: Context) : BaseAdapter() {
        private val repeatTypes = listOf("仅一次", "每天", "每周")
        private val context = context

        override fun isEmpty(): Boolean = repeatTypes.isEmpty()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var convertView = convertView
            var holder: ViewHolder
            if (convertView == null) {
                holder = ViewHolder()
                convertView = LayoutInflater.from(context).inflate(R.layout.create_schedule_repeat_item, null)
                holder.text = convertView as TextView
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }

            holder.text!!.text = repeatTypes[position]

            return convertView
        }

        override fun getItem(position: Int): Any = repeatTypes[position]

        override fun getCount(): Int = repeatTypes.size

        override fun getItemId(position: Int): Long = position.toLong()

        class ViewHolder {
            var text: TextView? = null
        }
    }


}
