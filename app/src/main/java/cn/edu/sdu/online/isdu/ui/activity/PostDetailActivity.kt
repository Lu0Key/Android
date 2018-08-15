package cn.edu.sdu.online.isdu.ui.activity

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.MyApplication
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.app.ThreadPool
import cn.edu.sdu.online.isdu.bean.CommentUser
import cn.edu.sdu.online.isdu.bean.PostComment
import cn.edu.sdu.online.isdu.bean.Post
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
import cn.edu.sdu.online.isdu.util.database.DAOHistory
import cn.edu.sdu.online.isdu.util.history.History
import com.alibaba.fastjson.JSON
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.qmuiteam.qmui.span.QMUITouchableSpan
import com.qmuiteam.qmui.widget.textview.QMUISpanTouchFixTextView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.edit_area.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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
////    private var postCommentList = ArrayList<PostComment>()
//    private var userIdMap: HashMap<String, String> = HashMap()
//    private var userNicknameMap: HashMap<String, String> = HashMap()

    private var isLike = false // 是否点赞
    private var showCollectToast = false  // 是否显示已经收藏

    private var postId = 0
    private var fatherCommentId = -1 // 父评论ID
    private var tag = ""
    private var commentList = LinkedList<PostComment>()
    private var toUserId = "0"

    private var window: BasePopupWindow? = null // 右上角点击弹出窗口

    private val post = Post() // 该浏览页面的帖子实例

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        postId = intent.getIntExtra("id", 0)
        url = ServerInfo.getPost(postId)
//        uid = intent.getStringExtra("uid") ?: ""
//        title = intent.getStringExtra("title") ?: ""
//        time = intent.getLongExtra("time", 0L)
        tag = intent.getStringExtra("tag") ?: ""

        if (User.staticUser == null) User.staticUser = User.load()

        initView()

        //写数据库纪录浏览
        post.postId = postId
//        post.uid = uid
//        post.title = title
//        post.time = time
//        History.newHistory(post)


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
                fatherCommentId = -1 // 评论帖子，将父评论ID设为-1
                toUserId = "0"
                editText!!.requestFocus()
                editText!!.hint = "评论帖子"
                showSoftKeyboard()
            }
            btn_send.id -> {
                if (editText!!.text.toString() == "") {
                    // 防止空评论
                    Toast.makeText(this, "评论不能为空", Toast.LENGTH_SHORT).show()
                    return
                }
                if (User.staticUser == null) User.staticUser = User.load()
                val keys = arrayListOf("content", "userId", "postId", "fatherCommentId", "time", "toUserId")
                val values = arrayListOf(editText!!.text.toString(),
                        User.staticUser.uid.toString(), postId.toString(), fatherCommentId.toString(),
                        System.currentTimeMillis().toString(), toUserId)
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
                        getContentView().findViewById<View>(R.id.btn_delete).setOnClickListener { _ ->
                            popupWindow.dismiss()
                            editArea!!.clearFocus()
                            val dialog = AlertDialog(this@PostDetailActivity)
                            dialog.setTitle("删除帖子")
                            dialog.setMessage("确定要删除帖子吗？该操作不可逆")
                            dialog.setPositiveButton("删除") {_ ->
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
                if (post.uid != "") startActivity(Intent(this, MyHomePageActivity::class.java)
                        .putExtra("id", post.uid.toInt()))
            }
            btn_like.id -> {
                if (User.staticUser != null &&
                        User.staticUser.studentNumber != null)
                NetworkAccess.buildRequest(ServerInfo.likePost + "?postId=$postId&userId=${User.staticUser.uid}",
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
                NetworkAccess.buildRequest(ServerInfo.collectPost + "?postId=$postId&userId=${User.staticUser.uid}",
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
        NetworkAccess.buildRequest(ServerInfo.getIsLike(postId, User.staticUser.uid.toString()), object : Callback {
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
                    post.likeNumber = 0
                    History.newHistory(post)
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
                            des = "${(Math.floor((strInt / 1000).toDouble()))} 千"
                        } else {
                            des = "${(Math.floor((strInt / 10000).toDouble()))} 万"
                        }
                        txtLike!!.text = "点赞 $des 次"
//                        btnLike!!.setImageResource(if (isLike) R.drawable.ic_like_yes else R.drawable.ic_like_no)

                        post.likeNumber = strInt
                        History.newHistory(post)
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
                    try {
                        if (success) {
                            if (FileUtil.getStringFromFile(cachePath) != "") {
                                val json = JSONObject(FileUtil.getStringFromFile(cachePath))

                                val data = JSONObject(json.getString("obj"))
                                val editDataList = ArrayList<RichTextEditor.EditData>()
                                val content = JSONArray(data.getString("content"))


                                post.title = data.getString("title")
                                post.time = data.getString("time").toLong()
                                post.uid = data.getString("userId")

                                getUserData(post.uid)

                                getLikeNumber()

                                for (i in 0 until content.length()) {
                                    val obj = content.getJSONObject(i)
                                    val data = RichTextEditor.EditData()
                                    if (obj.getInt("type") == 0) {
                                        data.imageName = obj.getString("content")
                                        if (data.inputStr != "" && (post.content == null || post.content == ""))
                                            post.content = "[图片]"
                                    } else {
                                        data.inputStr = obj.getString("content")
                                        if (data.inputStr != "" && (post.content == null || post.content == ""))
                                            post.content = data.inputStr
                                    }
                                    editDataList.add(data)
                                }

                                // 写入浏览记录
                                History.newHistory(post)

                                runOnUiThread {

                                    if (User.staticUser.studentNumber == null) {
                                        // 未登录
                                        operate_bar!!.visibility = View.GONE
                                        btnOptions!!.visibility = View.INVISIBLE
                                    } else if (User.staticUser.uid.toString() == post.uid) {
                                        // 本用户的帖子
                                        operate_bar!!.visibility = View.VISIBLE
                                        btnOptions!!.setOnClickListener(this)
                                        btnOptions!!.visibility = View.VISIBLE
                                    } else {
                                        btnOptions!!.visibility = View.INVISIBLE
                                        operate_bar!!.visibility = View.VISIBLE
                                    }

                                    txtTitle!!.text = post.title
                                    txtDate!!.text =
                                            "发表于 ${SimpleDateFormat("yyyy-MM-dd HH:mm").format(post.time)}"

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

                                    val commentStr = data.getString("comment")
                                    // 获取评论ID串
                                    val commentList = commentStr.split("-").subList(0, commentStr.split("-").size - 1)
                                    this.commentList.clear()

                                    // 写入历史浏览
                                    post.commentsNumbers = commentList.size
                                    History.newHistory(post)

                                    getComments(commentList.size - 1, commentList) // 获取评论

                                    commentLine!!.text = "${commentList.size} 条评论"

                                    // 挪开空白View
                                    blank_view.visibility = View.GONE

                                }
                            }

                        } else {
                            runOnUiThread {
                                Toast.makeText(this, "获取内容出错", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
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
//                val bmp = ImageManager.convertStringToBitmap(obj)
                runOnUiThread {
                    Glide.with(MyApplication.getContext())
                            .load(obj)
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                            .into(circleImageView!!)
//                    circleImageView!!.setImageBitmap(bmp)
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
        NetworkAccess.buildRequest(ServerInfo.getIsCollect(postId, User.staticUser.uid.toString()), object : Callback {
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


    /**
     * 获取帖子内容
     */
    private fun getCommentContent(id: String): JSONObject {
        val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()
        val formBody = FormBody.Builder().add("id", id).build()
        val request = Request.Builder()
                .url(ServerInfo.getComments())
                .post(formBody)
                .build()
        val response = client.newCall(request).execute()

        return JSONObject(response?.body()?.string()).getJSONObject("obj")
    }

    /**
     * 获取评论
     * 一次性从服务器获取全部评论
     *
     * @param index 评论在list中的序号
     * @param list 评论ID列表
     */
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

                    val theUser = obj.getJSONObject("theUser")
                    comment.theUser = CommentUser(theUser.getInt("userId"),
                            theUser.getString("nickName"),
                            theUser.getString("avatar"))

                    if (comment.fatherId != -1) {
                        val fatherUser = obj.getJSONObject("fatherUser")
                        comment.fatheruser = CommentUser(fatherUser.getInt("userId"),
                                fatherUser.getString("nickName"),
                                fatherUser.getString("avatar"))
                    }

//                    if (!userIdMap.containsKey(comment.uid)) {
//                        userIdMap.put(comment.uid, "")
//                        userNicknameMap.put(comment.uid, "")
//                        requestUserInfoMap(comment.uid)
//                    }

                    if (!commentList.contains(comment)) commentList.add(comment) // 防止重复添加Comment
                    // 也可以使用线程锁（（那就别用OKHTTP了））

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
            view.findViewById<QMUISpanTouchFixTextView>(R.id.reply_comment)
                    .setMovementMethodDefault()
            view.findViewById<QMUISpanTouchFixTextView>(R.id.reply_comment)
                    .setNeedForceEventToParent(true)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = commentList.size

        override fun getItemId(position: Int): Long = position.toLong()

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val comment = commentList[position]

            ThreadPool.execute {
                if (comment.fatherId != -1) {
                    // 获取父评论的内容
                    try {
                        val commentJsonObj = getCommentContent(comment.fatherId.toString())
                        val fatherCommentContent = commentJsonObj.getString("content")
                        val userName = comment.fatheruser.nickName

                        val sp = SpannableString("$userName: $fatherCommentContent")
                        sp.setSpan(object : QMUITouchableSpan(
                                0xFF717EDB.toInt(), 0xFF717EDB.toInt(),
                                0x00000000, 0x11000000
                        ) {
                            override fun onSpanClick(widget: View?) {
                                startActivity(Intent(this@PostDetailActivity, MyHomePageActivity::class.java)
                                        .putExtra("id", comment.fatheruser.userId))
                            }
                        }, 0, userName.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

                        runOnUiThread {
                            holder.txtReply.setText(sp)
                        }
                    } catch (e: Exception) {
                    }
                }
                val bmp = comment.theUser.avatar
                runOnUiThread {
                    if (bmp != null && bmp != "")
                        Glide.with(MyApplication.getContext())
                                .load(bmp)
                                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                                .into(holder.circleImageView)
                }
            }

            holder.txtNickname.text = comment.theUser.nickName

            holder.txtTime.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(comment.time)
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
                btnComment!!.callOnClick()
                showSoftKeyboard()
                editText!!.hint = "回复：${comment.content}"
                fatherCommentId = comment.id
                toUserId = comment.uid
            }

            holder.itemLayout.setOnLongClickListener {

                if (User.staticUser == null) User.staticUser = User.load()
                if (User.staticUser.studentNumber == null || User.staticUser.studentNumber.equals("")) {
                    return@setOnLongClickListener false
                }

                if (comment.uid == User.staticUser.uid.toString()) {
                    val dialog = OptionDialog(this@PostDetailActivity, listOf("删除评论", "回复评论"))
                    dialog.setMessage("选择操作")
                    dialog.setCancelOnTouchOutside(true)
                    dialog.setOnItemSelectListener {itemName ->
                        when (itemName) {
                            "删除评论" -> {
                                runOnUiThread { deleteComment(comment) }
                                dialog.dismiss()
                            }
                            "回复评论" -> {
                                runOnUiThread {
                                    btnComment!!.callOnClick()
                                    showSoftKeyboard()
                                    editText!!.hint = "回复：${comment.content}"
                                    fatherCommentId = comment.id
                                    toUserId = comment.uid
                                }
                                dialog.dismiss()
                            }
                        }
                    }
                    dialog.show()
                } else {
                    val dialog = OptionDialog(this@PostDetailActivity, listOf("回复评论"))
                    dialog.setMessage("选择操作")
                    dialog.setCancelOnTouchOutside(true)
                    dialog.setOnItemSelectListener {itemName ->
                        when (itemName) {
                            "回复评论" -> {
                                runOnUiThread {
                                    btnComment!!.callOnClick()
                                    showSoftKeyboard()
                                    editText!!.hint = "回复：${comment.content}"
                                    fatherCommentId = comment.id
                                    toUserId = comment.uid
                                }
                                dialog.dismiss()
                            }
                        }
                    }
                    dialog.show()
                }
                true
            }

        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemLayout = view.findViewById<View>(R.id.item_layout)
            val circleImageView = view.findViewById<CircleImageView>(R.id.circle_image_view)
            val txtNickname = view.findViewById<TextView>(R.id.txt_nickname)
            val txtTime = view.findViewById<TextView>(R.id.txt_time)
            val txtContent = view.findViewById<TextView>(R.id.txt_content)
            val txtReply = view.findViewById<QMUISpanTouchFixTextView>(R.id.reply_comment)
        }
    }

}
