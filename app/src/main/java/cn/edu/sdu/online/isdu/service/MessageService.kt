package cn.edu.sdu.online.isdu.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import cn.edu.sdu.online.isdu.bean.Message
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.util.Logger
import com.alibaba.fastjson.JSON
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class MessageService : Service() {
    private var userId: String = ""

    private var noticeRunnable = Runnable {
        val client = OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build()
        val request = Request.Builder()
                .get()
                .url(ServerInfo.getNotice(userId))
                .build()
        while (true) {
            try {
                val response = client.newCall(request).execute()
                try {
                    val str = response?.body()?.string()
                    val jsonString = str!!.substring(str.indexOf('['), str.indexOf(']') + 1)

                    val msgList = JSON.parseArray(jsonString, Message::class.java)
                    Message.msgList.addAll(msgList)
                    Message.saveMsgList()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Thread.sleep(10000)
            } catch (e: Exception) {

            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        throw Exception("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        userId = intent?.getStringExtra("uid") ?: ""
        if (userId != "") {
            Thread(noticeRunnable).start()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
    }

}
