package cn.edu.sdu.online.isdu.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.ui.design.WideButton
import cn.edu.sdu.online.isdu.ui.design.dialog.OptionDialog
import cn.edu.sdu.online.isdu.util.Settings

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/8
 *
 * 设置Activity
 *
 * #添加云同步
 ****************************************************
 */

class SettingsActivity : SlideActivity(), View.OnClickListener, WideButton.OnItemClickListener,
                    WideButton.OnItemSwitchListener {

    private var btnBack: ImageView? = null

    private var btnStartupPage: WideButton? = null
    private var btnAlarmMessage: WideButton? = null
    private var btnAlarmNews: WideButton? = null
    private var btnAlarmSchedule: WideButton? = null
    private var btnCloudSync: WideButton? = null

    private var btnLogout: TextView? = null

    private val startupPages = arrayListOf("论坛", "资讯", "个人中心")
    private val alarmScheduleList = listOf("不提醒", "震动", "铃声（需要开启媒体音量）")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        Settings.load(this)

        initView()

        initSettings()
    }

    /**
     * 加载设置项
     */
    private fun initSettings() {
        btnStartupPage!!.setTxtComment(startupPages[Settings.STARTUP_PAGE])

        btnAlarmMessage!!.setSwitch(Settings.ALARM_MESSAGE)
        btnAlarmNews!!.setSwitch(Settings.ALARM_NEWS)
        btnAlarmSchedule!!.setTxtComment(alarmScheduleList[Settings.ALARM_SCHEDULE])
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_back -> {
                finish()
            }
            R.id.btn_logout -> {
                User.logout(this)
                Toast.makeText(this, "注销成功", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onClick(itemId: String?) {
        when (itemId) {
            "startup_page" -> {
                val dialog = OptionDialog(this, startupPages)
                dialog.setMessage("选择启动页")
                dialog.setOnItemSelectListener { itemName ->
                    Settings.STARTUP_PAGE = startupPages.indexOf(itemName)
                    Settings.store(this)
                    btnStartupPage!!.setTxtComment(itemName)
                }
                dialog.show()
            }
            "alarm_schedule" -> {
                val dialog = OptionDialog(this, alarmScheduleList)
                dialog.setMessage("日程提醒")
                dialog.setOnItemSelectListener { itemName ->
                    Settings.ALARM_SCHEDULE = alarmScheduleList.indexOf(itemName)
                    Settings.store(this)
                    btnAlarmSchedule!!.setTxtComment(itemName)
                }
                dialog.show()
            }
        }
    }

    override fun onSwitch(itemId: String?, b: Boolean) {
        when (itemId) {
            "alarm_message" -> {
                Settings.ALARM_MESSAGE = b
                Settings.store(this)
            }
            "alarm_news" -> {
                Settings.ALARM_NEWS = b
                Settings.store(this)
            }
            "cloud_sync" -> {
                Settings.CLOUD_SYNC = b
                Settings.store(this)
            }
        }
    }

    private fun initView() {
        btnBack = findViewById(R.id.btn_back)
        btnStartupPage = findViewById(R.id.btn_startup_page)
        btnAlarmMessage = findViewById(R.id.btn_alarm_message)
        btnAlarmNews = findViewById(R.id.btn_alarm_news)
        btnAlarmSchedule = findViewById(R.id.btn_alarm_schedule)
        btnCloudSync = findViewById(R.id.btn_cloud_sync)

        btnLogout = findViewById(R.id.btn_logout)

        btnBack!!.setOnClickListener(this)
        btnLogout!!.setOnClickListener(this)

        btnStartupPage!!.setOnItemClickListener(this)
        btnAlarmSchedule!!.setOnItemClickListener(this)
        btnAlarmNews!!.setOnItemSwitchListener(this)
        btnAlarmMessage!!.setOnItemSwitchListener(this)
        btnCloudSync!!.setOnItemSwitchListener(this)
    }

    override fun prepareBroadcastReceiver() {

    }

    override fun unRegBroadcastReceiver() {

    }
}
