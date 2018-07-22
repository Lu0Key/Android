package cn.edu.sdu.online.isdu.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.ui.design.dialog.AlertDialog
import cn.edu.sdu.online.isdu.ui.design.dialog.ProgressDialog
import cn.edu.sdu.online.isdu.ui.design.popupwindow.BasePopupWindow
import cn.edu.sdu.online.isdu.ui.design.xrichtext.RichTextEditor
import cn.edu.sdu.online.isdu.ui.design.xrichtext.RichTextView
import cn.edu.sdu.online.isdu.util.FileUtil
import cn.edu.sdu.online.isdu.util.ImageManager
import cn.edu.sdu.online.isdu.util.Logger
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_history.view.*
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.edit_area.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat

class PostDetailActivity : SlideActivity(), View.OnClickListener {

    private var url: String = "" // URL

    private var txtTitle: TextView? = null
    private var txtContent: RichTextView? = null
    private var circleImageView: CircleImageView? = null // 发帖者头像
    private var txtNickname: TextView? = null
    private var txtDate: TextView? = null
    private var posterLayout: View? = null
    private var btnOptions: View? = null

    private var btnComment: View? = null
    private var btnLike: View? = null
    private var btnCollect: View? = null

    private var editArea: View? = null // 隐藏的编辑区域
    private var editText: EditText? = null // 编辑区域的文本框
    private var btnSend: View? = null

    private var uid = ""
    private var postId = 0
    private var title = ""
    private var time = 0L
    private var commentIds = ""

    private var window: BasePopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        postId = intent.getIntExtra("id", 0)
        url = ServerInfo.getPost(postId)
        uid = intent.getStringExtra("uid") ?: ""
        title = intent.getStringExtra("title") ?: ""
        time = intent.getLongExtra("time", 0L)

        if (User.staticUser == null) User.staticUser = User.load()

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
        btnOptions = btn_options

        btnComment!!.setOnClickListener(this)
        btnSend!!.setOnClickListener(this)
        posterLayout!!.setOnClickListener(this)

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

        if (User.staticUser.studentNumber == null) {
            // 未登录
            operate_bar!!.visibility = View.GONE
        } else if (User.staticUser.uid.toString() == uid) {
            // 本用户的帖子
            btnOptions!!.setOnClickListener(this)
        } else {
            btnOptions!!.visibility = View.INVISIBLE
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
                if (editText!!.text.toString() == "") {
                    // 防止空评论
                    Toast.makeText(this, "评论不能为空", Toast.LENGTH_SHORT).show()
                    return
                }

                val keys = arrayListOf("content", "userId", "postId", "fatherCommentId", "time")
                val values = arrayListOf(editText!!.text.toString(),
                        User.staticUser.uid.toString(), postId.toString(), "-1", System.currentTimeMillis().toString())
                NetworkAccess.buildRequest(ServerInfo.postCommentUrl, keys, values, object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        Logger.log(e)
                        runOnUiThread {
                            Toast.makeText(this@PostDetailActivity, "网络错误", Toast.LENGTH_SHORT).show()
                            editArea!!.clearFocus()
                        }
                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        runOnUiThread {
                            editArea!!.clearFocus()
                        }
                    }
                })

            }
            btn_options.id -> {
                editArea!!.clearFocus()
                // 弹出弹窗
                window = object : BasePopupWindow(this, R.layout.popup_post_detail,
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) {
                    override fun initView() {

                    }

                    override fun initEvent() {
                        getContentView().findViewById<View>(R.id.btn_delete).setOnClickListener {
                            popupWindow.dismiss()
                            editArea!!.clearFocus()
                            val dialog = AlertDialog(this@PostDetailActivity)
                            dialog.setTitle("删除帖子")
                            dialog.setMessage("确定要删除帖子吗？该操作不可逆")
                            dialog.setPositiveButton("删除") {
                                val dialog1 = ProgressDialog(this@PostDetailActivity, false)
                                dialog1.setMessage("正在删除")
                                dialog1.setButton(null, null)
                                dialog1.setCancelable(false)
                                dialog1.show()
                                dialog.dismiss()
                                NetworkAccess.buildRequest(ServerInfo.deletePost, "id", postId.toString(),
                                        object : Callback {
                                            override fun onFailure(call: Call?, e: IOException?) {
                                                Logger.log(e)
                                                runOnUiThread {
                                                    dialog1.dismiss()
                                                    Toast.makeText(this@PostDetailActivity,
                                                            "网络错误，删除失败", Toast.LENGTH_SHORT).show()
                                                }
                                            }

                                            override fun onResponse(call: Call?, response: Response?) {
                                                runOnUiThread {
                                                    dialog1.dismiss()
                                                }
                                                try {
                                                    val str = response?.body()?.string()
                                                    if (str != null && str.contains("success")) {
                                                        runOnUiThread {
                                                            Toast.makeText(this@PostDetailActivity,
                                                                    "删除成功", Toast.LENGTH_SHORT).show()
                                                            finish()
                                                        }
                                                    } else {
                                                        runOnUiThread {
                                                            Toast.makeText(this@PostDetailActivity,
                                                                    "删除失败", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    Logger.log(e)
                                                    runOnUiThread {
                                                        Toast.makeText(this@PostDetailActivity,
                                                                "网络错误，删除失败", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        })
                            }

                            dialog.setNegativeButton("取消") {
                                dialog.dismiss()
                            }

                            dialog.show()
                        }

                        getContentView().findViewById<View>(R.id.btn_cancel).setOnClickListener {
                            popupWindow.dismiss()
                        }
                    }

                    override fun initWindow() {
                        super.initWindow()
                        val instance = popupWindow
                        instance.setOnDismissListener {
                            val lp = getWindow().attributes
                            lp.alpha = 1f
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                            getWindow().attributes = lp
                        }
                    }
                }
                window!!.popupWindow.animationStyle = R.style.popupAnimTranslate
                window!!.showAtLocation(base_view, Gravity.BOTTOM, 0, 0)
                val lp = getWindow().attributes
                lp.alpha = 0.5f
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                getWindow().attributes = lp
            }
            poster_layout.id -> {
                if (uid != "") startActivity(Intent(this, MyHomePageActivity::class.java)
                        .putExtra("id", uid.toInt()))
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
        NetworkAccess
                .cache(url) { success, cachePath ->
            if (success) {
                if (FileUtil.getStringFromFile(cachePath) != "") {
                    val json = JSONObject(FileUtil.getStringFromFile(cachePath))

                    getUserData(uid)

                    val data = JSONObject(json.getString("obj"))
                    val editDataList = ArrayList<RichTextEditor.EditData>()
                    val content = JSONArray(data.getString("content"))
                    for (i in 0 until content.length()) {
                        val obj = content.getJSONObject(i)
                        val data = RichTextEditor.EditData()
                        if (obj.getInt("type") == 0) {
                            data.imageName = obj.getString("content")
                        } else {
                            data.inputStr = obj.getString("content")
                        }
                        editDataList.add(data)
                    }



                    runOnUiThread {
                        txtTitle!!.text = title
                        txtDate!!.text =
                                "发表于 ${SimpleDateFormat("yyyy-MM-dd HH:mm").format(time)}"

                        txtContent!!.setData(editDataList)
                        
                        txtContent!!.setOnRtImageClickListener {imagePath ->  
                            if (imagePath.startsWith("http")) {
                                // 网络图片
                                startActivity(Intent(this@PostDetailActivity, ViewImageActivity::class.java)
                                        .putExtra("url", imagePath))
                            } else {
                                // 本地图片
                            }
                        }
                    }
                }


            } else {
                runOnUiThread {
                    Toast.makeText(this, "获取内容出错", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * 获取发帖用户的信息
     */
    private fun getUserData(id: String) {
        NetworkAccess.cache(ServerInfo.getUserInfo(id, "nickname"), "nickname") {success, cachePath ->
            if (success) {
                val obj = FileUtil.getStringFromFile(cachePath)
                runOnUiThread { txtNickname!!.text = obj }
            } else {
                Toast.makeText(this, "获取用户信息失败", Toast.LENGTH_SHORT).show()
            }
        }

        NetworkAccess.cache(ServerInfo.getUserInfo(id, "avatar"), "avatar") {success, cachePath ->
            if (success) {
                val obj = FileUtil.getStringFromFile(cachePath)
                val bmp = ImageManager.convertStringToBitmap(obj)
                runOnUiThread {
                    circleImageView!!.setImageBitmap(bmp)
                }
            } else {
                Toast.makeText(this, "获取用户信息失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getComments() {

    }

}
