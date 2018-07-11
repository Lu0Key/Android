package cn.edu.sdu.online.isdu.ui.activity

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.Schedule
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.util.EnvVariables
import cn.edu.sdu.online.isdu.util.Security
import cn.edu.sdu.online.isdu.util.Settings
import kotlinx.android.synthetic.main.activity_splash.*

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/6/21
 *
 * 启动展示页面
 *
 * 在加载时读取本地设置和用户信息
 *
 * #修正展示时间
 * #隐藏系统状态栏
 ****************************************************
 */

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        EnvVariables.init(this)

        decorateWindow()

        loadLocalSettings()
        loadLocalUser()

        Schedule.localScheduleList = Schedule.load(this)

        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, PAGE_SHOW_TIME_MILLIS)

    }


    private fun decorateWindow() {
        val decorView = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        } else {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }


    /**
     * 加载本地设置
     *
     */
    private fun loadLocalSettings() {
        Settings.load(this)
    }

    /**
     * 加载本地用户缓存
     *
     */
    private fun loadLocalUser() {
        User.staticUser = User.load()
    }

    companion object {
        const val PAGE_SHOW_TIME_MILLIS = 1000L // 展示TimeOut
    }
}
