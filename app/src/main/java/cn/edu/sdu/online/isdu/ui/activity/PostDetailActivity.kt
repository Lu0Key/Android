package cn.edu.sdu.online.isdu.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.PostComment
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.ui.design.dialog.AlertDialog
import cn.edu.sdu.online.isdu.ui.design.dialog.OptionDialog
import cn.edu.sdu.online.isdu.ui.design.dialog.ProgressDialog
import cn.edu.sdu.online.isdu.ui.design.popupwindow.BasePopupWindow
import cn.edu.sdu.online.isdu.ui.design.xrichtext.RichTextEditor
import cn.edu.sdu.online.isdu.ui.design.xrichtext.RichTextView
import cn.edu.sdu.online.isdu.util.FileUtil
import cn.edu.sdu.online.isdu.util.ImageManager
import cn.edu.sdu.online.isdu.util.Logger
import cn.edu.sdu.online.isdu.util.WeakReferences
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.edit_area.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.ref.WeakReference
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
    private var txtLike: TextView? = null

    private var btnComment: View? = null
    private var btnLike: ImageView? = null
    private var btnCollect: ImageView? = null

    private var commentLine: TextView? = null

    private var editArea: View? = null // 隐藏的编辑区域
    private var editText: EditText? = null // 编辑区域的文本框
    private var btnSend: View? = null

    private var commentRecyclerView: RecyclerView? = null
    private var commentAdapter: MyAdapter? = null
    private var postCommentList = ArrayList<PostComment>()
    private var userIdMap: HashMap<String, String> = HashMap()
    private var userNicknameMap: HashMap<String, String> = HashMap()

    private var isLike = false // 是否点赞
    private var showCollectToast = false  // 是否显示已经收藏

    private var uid = ""
    private var postId = 0
    private var title = ""
    private var time = 0L
    private var fatherCommentId = -1
    private var tag = ""
    private var commentList = ArrayList<PostComment>()

    private var window: BasePopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        postId = intent.getIntExtra("id", 0)
        url = ServerInfo.getPost(postId)
        uid = intent.getStringExtra("uid") ?: ""
        title = intent.getStringExtra("title") ?: ""
        time = intent.getLongExtra("time", 0L)
        tag = intent.getStringExtra("tag") ?: ""

        if (User.staticUser == null) User.staticUser = User.load()

        initView()

//        btnLike!!.setBackgroundResource(R.drawable.ic_like_yes)

        getPostData()
        getIsCollect()

        commentRecyclerView!!.layoutManager = LinearLayoutManager(this)
        commentAdapter = MyAdapter()
        commentAdapter!!.setHasStableIds(true)
        commentRecyclerView!!.adapter = commentAdapter
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
        commentLine = comment_line
        commentRecyclerView = comment_recycler_view
        txtLike = like_count

        btn_back.setOnClickListener { finish() }

        title_text_view.setOnClickListener {
            scroll_view.scrollTo(0, 0)
        }

        btnComment!!.setOnClickListener(this)
        btnSend!!.setOnClickListener(this)
        posterLayout!!.setOnClickListener(this)
        btnLike!!.setOnClickListener(this)
        btnCollect!!.setOnClickListener(this)

        editText!!.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                editArea!!.visibility = View.GONE
                operate_bar.visibility = View.VISIBLE
                hideSoftKeyboard()
                fatherCommentId = -1
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
                        User.staticUser.uid.toString(), postId.toString(), fatherCommentId.toString(),
                        System.currentTimeMillis().toString())
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
                            editText!!.setText("")
                            editArea!!.clearFocus()
                            getPostData()
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

                                                            WeakReferences.postViewableWeakReference?.get()?.removeItem(postId)

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
            btn_like.id -> {
                NetworkAccess.buildRequest(ServerInfo.likePost + "?postId=$postId&userId=$uid",
                        object : Callback {
                            override fun onFailure(call: Call?, e: IOException?) {
                                Logger.log(e)
                                runOnUiThread {
                                    Toast.makeText(this@PostDetailActivity, "点赞失败", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onResponse(call: Call?, response: Response?) {
                                try {
                                    runOnUiThread {
                                        isLike = !isLike
                                        getLikeNumber()
                                    }
                                } catch (e: Exception) {
                                    Logger.log(e)
                                }
                            }
                        })
            }
            btn_collect.id -> {
                showCollectToast = true
                NetworkAccess.buildRequest(ServerInfo.collectPost + "?postId=$postId&userId=$uid",
                        object : Callback {
                            override fun onFailure(call: Call?, e: IOException?) {
                                Logger.log(e)
                                runOnUiThread {
                                    Toast.makeText(this@PostDetailActivity, "收藏失败", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onResponse(call: Call?, response: Response?) {
                                try {
                                    runOnUiThread {
                                        getIsCollect()
                                    }
                                } catch (e: Exception) {
                                    Logger.log(e)
                                }
                            }
                        })
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

    private fun getLikeNumber() {
        NetworkAccess.buildRequest(ServerInfo.getIsLike(postId, uid), object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Logger.log(e)
                isLike = false
            }

            override fun onResponse(call: Call?, response: Response?) {
                try {
                    var str = response?.body()?.string()
                    isLike = str == "true"
                    runOnUiThread {
                        btnLike!!.setImageResource(if (isLike) R.drawable.ic_like_yes else R.drawable.ic_like_no)
                    }
                } catch (e: Exception) {
                    Logger.log(e)
                }
            }
        })
        NetworkAccess.buildRequest(ServerInfo.getLikeNumber(postId), object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Logger.log(e)
                runOnUiThread {
                    txtLike!!.text = "点赞 0 次"

                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                try {
                    val str = response?.body()?.string()
                    runOnUiThread {
                        val strInt = str!!.toInt()
                        var des = ""
                        if (strInt < 1000)
                            des = strInt.toString()
                        else if (strInt < 10000) {
                            des = "${(Math.floor((strInt / 1000).toDouble())).toString()} 千"
                        } else {
                            des = "${(Math.floor((strInt / 10000).toDouble())).toString()} 万"
                        }
                        txtLike!!.text = "点赞 $des 次"
//                        btnLike!!.setImageResource(if (isLike) R.drawable.ic_like_yes else R.drawable.ic_like_no)
                    }
                } catch (e: Exception) {
                    Logger.log(e)
                }
            }
        })
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

                        getLikeNumber()

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

//                            txtLike!!.text = likeCount.toString()

                            txtContent!!.setData(editDataList)

                            val commentStr = data.getString("comment")
                            val commentList = commentStr.split("-").subList(0, commentStr.split("-").size - 1)
                            this.commentList.clear()
                            getComments(commentList.size - 1, commentList)

//                        var commentCounter = 0
//                        for (com in commentList)
//                            if (com != "") {
//                                commentCounter++
//                            }

                            commentLine!!.text = "${commentList.size} 条评论"

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
                runOnUiThread {
                    Toast.makeText(this, "获取用户信息失败", Toast.LENGTH_SHORT).show()
                }

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
                runOnUiThread {
                    Toast.makeText(this, "获取用户信息失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * 获取是否收藏
     */
    private fun getIsCollect() {
        NetworkAccess.buildRequest(ServerInfo.getIsCollect(postId, uid), object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Logger.log(e)
            }

            override fun onResponse(call: Call?, response: Response?) {
                try {
                    val str = response?.body()?.string()
                    runOnUiThread {
                        btnCollect!!.setImageResource(if (str == "true") R.drawable.ic_collect_yes else R.drawable.ic_collect_no)
                        if ((str == "true") && showCollectToast)
                            Toast.makeText(this@PostDetailActivity, "收藏成功", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Logger.log(e)
                }
            }
        })
    }

    private fun requestUserInfoMap(uid: String) {
        NetworkAccess.cache(ServerInfo.getUserInfo(uid, "nickname"), "nickname") {success, cachePath ->
            if (success) {
                userNicknameMap[uid] = cachePath
            }
            runOnUiThread {
                commentAdapter?.notifyDataSetChanged()
            }
        }
        NetworkAccess.cache(ServerInfo.getUserInfo(uid, "avatar"), "avatar") {success, cachePath ->
            if (success) {
                userIdMap[uid] = cachePath
            }
            runOnUiThread {
                commentAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun getComments(index: Int, list: List<String>) {
        if (index < 0) return
        NetworkAccess.buildRequest(ServerInfo.getComments(), "id", list[index], object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Logger.log(e)
            }

            override fun onResponse(call: Call?, response: Response?) {
                val str = response?.body()?.string()
                try {
                    val obj = JSONObject(str).getJSONObject("obj")

                    val comment = PostComment()
                    comment.postId = obj.getInt("postId")
                    comment.id = obj.getInt("id")
                    comment.fatherId = obj.getInt("fatherCommenrId")
                    comment.time = obj.getString("time").toLong()
                    comment.content = obj.getString("content")
                    comment.uid = obj.getString("userId")

                    if (!userIdMap.containsKey(comment.uid)) {
                        userIdMap.put(comment.uid, "")
                        userNicknameMap.put(comment.uid, "")
                        requestUserInfoMap(comment.uid)
                    }

                    if (!commentList.contains(comment)) commentList.add(comment)

                    runOnUiThread {
                        commentAdapter?.notifyDataSetChanged()
                        getComments(index - 1, list)
                    }

                } catch (e: Exception) {
                    Logger.log(e)
                }
            }
        })
    }

    private fun deleteComment(comment: PostComment) {
        NetworkAccess.buildRequest(ServerInfo.deleteComment, "id", comment.id.toString(), object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Logger.log(e)
                runOnUiThread {
                    Toast.makeText(this@PostDetailActivity, "删除失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                try {
                    val obj = JSONObject(response?.body()?.string())
                    if (obj.getString("status") == "success") {
                        runOnUiThread {
                            Toast.makeText(this@PostDetailActivity, "删除成功", Toast.LENGTH_SHORT).show()
                            getPostData()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@PostDetailActivity, "删除失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Logger.log(e)
                    runOnUiThread {
                        Toast.makeText(this@PostDetailActivity, "删除失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    inner class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.post_comment_item,
                    parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = commentList.size

        override fun getItemId(position: Int): Long = position.toLong()

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val comment = commentList[position]

            Thread(Runnable {
                val bmp = ImageManager.convertStringToBitmap(FileUtil.getStringFromFile(userIdMap[comment.uid]))
                runOnUiThread {
                    if (bmp != null)
                        holder.circleImageView.setImageBitmap(bmp)
                }
            }).start()

            holder.txtNickname.text = FileUtil.getStringFromFile(userNicknameMap[comment.uid])

            holder.txtTime.text = SimpleDateFormat("yyyy-MM-dd HH:mm").format(comment.time)
            holder.txtContent.text = comment.content
            if (comment.fatherId != -1) {
                holder.txtReply.visibility = View.VISIBLE
            } else {
                holder.txtReply.visibility = View.GONE
            }
            holder.circleImageView.setOnClickListener {
                startActivity(Intent(this@PostDetailActivity, MyHomePageActivity::class.java)
                        .putExtra("id", comment.uid.toInt()))
            }

            holder.itemLayout.setOnClickListener {
                if (comment.uid == User.staticUser.uid.toString()) {
                    val dialog = OptionDialog(this@PostDetailActivity, listOf("删除评论"))
                    dialog.setMessage("选择操作")
                    dialog.setCancelOnTouchOutside(true)
                    dialog.setOnItemSelectListener {itemName ->
                        when (itemName) {
                            "删除评论" -> {
                                runOnUiThread { deleteComment(comment) }
                                dialog.dismiss()
                            }
                        }
                    }
                    dialog.show()
                }

            }

            holder.itemLayout.setOnLongClickListener {
                holder.itemLayout.callOnClick()
                true
            }

        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemLayout = view.findViewById<View>(R.id.item_layout)
            val circleImageView = view.findViewById<CircleImageView>(R.id.circle_image_view)
            val txtNickname = view.findViewById<TextView>(R.id.txt_nickname)
            val txtTime = view.findViewById<TextView>(R.id.txt_time)
            val txtContent = view.findViewById<TextView>(R.id.txt_content)
            val txtReply = view.findViewById<TextView>(R.id.reply_comment)
        }
    }

}
