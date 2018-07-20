package cn.edu.sdu.online.isdu.ui.activity

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.Schedule
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.util.*
import cn.edu.sdu.online.isdu.util.download.Download
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

    private var splashHandler: SplashHandler? = null
    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        decorateWindow()

        showSplash()

        img.visibility = View.INVISIBLE

    }

    /******************************************
     * 这是个彩蛋
     * 冬冬nb！
     * 点击Splash两次即可看到冬冬的照片
     ******************************************/
    var count = 0
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                count++
                if (count == 2) {
                    img.visibility = View.VISIBLE
                    if (handler != null) {
                        handler!!.removeCallbacks(splashHandler)
                        splashHandler = null
                        handler = null
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
                count = count % 2
            }

        }
        return super.onTouchEvent(event)
    }

    private fun showSplash() {
        handler = Handler()
        splashHandler = SplashHandler()
    }

    override fun onPause() {
        super.onPause()
        if (handler != null && splashHandler != null) {
            handler!!.removeCallbacks(splashHandler)
        }
    }

    override fun onResume() {
        super.onResume()
        if (handler != null && splashHandler != null) {
            handler!!.postDelayed(splashHandler, PAGE_SHOW_TIME_MILLIS)
        }
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

    inner class SplashHandler : Runnable {
        override fun run() {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }

    companion object {
        const val PAGE_SHOW_TIME_MILLIS = 1500L // 展示TimeOut
    }
}
