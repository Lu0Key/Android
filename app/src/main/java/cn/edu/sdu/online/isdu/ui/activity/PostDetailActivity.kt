package cn.edu.sdu.online.isdu.ui.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.FileUriExposedException
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.ui.design.xrichtext.RichTextEditor
import cn.edu.sdu.online.isdu.ui.design.xrichtext.RichTextView
import cn.edu.sdu.online.isdu.util.FileUtil
import cn.edu.sdu.online.isdu.util.ImageManager
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.edit_area.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat

class PostDetailActivity : SlideActivity(), View.OnClickListener {

    private var postId: Int = 0 // 帖子ID

    private var url: String = "" // URL

    private var txtTitle: TextView? = null
    private var txtContent: RichTextView? = null
    private var circleImageView: CircleImageView? = null // 发帖者头像
    private var txtNickname: TextView? = null
    private var txtDate: TextView? = null
    private var posterLayout: View? = null

    private var btnComment: View? = null
    private var btnLike: View? = null
    private var btnCollect: View? = null

    private var editArea: View? = null // 隐藏的编辑区域
    private var editText: EditText? = null // 编辑区域的文本框
    private var btnSend: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        url = ServerInfo.getPost(intent.getIntExtra("id", 0))

        initView()

        getPostData()
    }

    private fun initView() {
        txtTitle = txt_title
        txtContent = rich_text_content
        circleImageView = circle_image_view
        txtNickname = txt_nickname
        posterLayout = poster_layout
        txtDate = post_date
        editArea = comment_area
        editText = edit_text
        btnSend = btn_send
        btnComment = btn_comment
        btnLike = btn_like
        btnCollect = btn_collect

        btnComment!!.setOnClickListener(this)
        btnSend!!.setOnClickListener(this)

        editText!!.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                editArea!!.visibility = View.GONE
                operate_bar.visibility = View.VISIBLE
                hideSoftKeyboard()
            }
        }

        comment_blank_view.setOnClickListener {
            editText!!.clearFocus()

        }
    }

    override fun onBackPressed() {
        if (editArea!!.visibility == View.VISIBLE) {
            editArea!!.clearFocus()
        } else super.onBackPressed()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            btn_comment.id -> {
                editArea!!.visibility = View.VISIBLE
                operate_bar.visibility = View.GONE
                editText!!.requestFocus()
                showSoftKeyboard()
            }
            btn_send.id -> {
                editArea!!.clearFocus()
            }
        }
    }

    private fun showSoftKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, 0)
    }

    private fun hideSoftKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editText!!.windowToken, 0)
    }

    /**
     * 获取帖子内容
     */
    private fun getPostData() {
        NetworkAccess.cache(url) { success, cachePath ->
            if (success) {
                val json = JSONObject(FileUtil.getStringFromFile(cachePath))

                getUserData(json.getString("uid"))

                val data = JSONObject(json.getString("obj"))

                val editDataList = ArrayList<RichTextEditor.EditData>()

                runOnUiThread {
                    txtDate!!.text =
                            "发表于 ${SimpleDateFormat("yyyy-MM-dd HH:mm").format(json.getLong("time"))}"

                    txtContent!!.setData(editDataList)
                }



            } else {
                Toast.makeText(this, "获取内容出错", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 获取发帖用户的信息
     */
    private fun getUserData(id: String) {
        NetworkAccess.cache(ServerInfo.getUserInfo(id, "nickname"), "nickname") {success, cachePath ->
            if (success) {
                val obj = JSONObject(FileUtil.getStringFromFile(cachePath))
                runOnUiThread { txtNickname!!.text = obj.getString("nickname") }
            } else {
                Toast.makeText(this, "获取用户信息失败", Toast.LENGTH_SHORT).show()
            }
        }

        NetworkAccess.cache(ServerInfo.getUserInfo(id, "avatar"), "avatar") {success, cachePath ->
            if (success) {
                val obj = FileUtil.getStringFromFile(cachePath)
                val bmp = ImageManager.convertStringToBitmap(obj)
                runOnUiThread {
                    Glide.with(this)
                            .load(bmp)
                            .into(circleImageView!!)
                }
            } else {
                Toast.makeText(this, "获取用户信息失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
