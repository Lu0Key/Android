package cn.edu.sdu.online.isdu.util.history

import android.content.Context
import android.util.Log
import cn.edu.sdu.online.isdu.app.MyApplication
import cn.edu.sdu.online.isdu.bean.Post
import com.alibaba.fastjson.JSON
import java.util.*
import kotlin.collections.HashSet

object History {
    public var historyList: LinkedList<Post> = LinkedList() // 采用链表，方便进行插入移动操作
    private var historySet: MutableSet<Post> = HashSet()

    fun newHistory(post: Post) {
//        load()
        if (!historySet.contains(post)) {
            historyList.addFirst(post)
            historySet.add(post)
        } else {
            historyList.remove(post)
            historyList.addFirst(post)
        }
        save()
    }

    fun removeHistory(post: Post) {
        load()
        if (historySet.contains(post)) {
            historySet.remove(post)
            historyList.remove(post)
        }
        save()
    }

    fun removeAllHistory() {
        load()
        historySet.clear()
        historyList.clear()
        save()
    }

    private fun save() {
        val editor = MyApplication.getContext().getSharedPreferences("history", Context.MODE_PRIVATE).edit()
        editor.putString("json", JSON.toJSONString(historyList))
        editor.apply()
    }

    private fun load() {
        val sp = MyApplication.getContext().getSharedPreferences("history", Context.MODE_PRIVATE)
        val str = sp.getString("json", "")
        if (str != "") {
            for (item in JSON.parseArray(str, Post::class.java))
                newHistory(item)
//            historySet.addAll(historyList)
        }
    }

}