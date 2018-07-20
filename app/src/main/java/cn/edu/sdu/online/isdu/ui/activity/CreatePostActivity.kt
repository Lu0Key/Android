package cn.edu.sdu.online.isdu.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.BaseActivity
import cn.edu.sdu.online.isdu.app.NormActivity
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.ui.design.dialog.AlertDialog
import cn.edu.sdu.online.isdu.ui.design.xrichtext.RichTextEditor
import cn.edu.sdu.online.isdu.util.ImageManager
import cn.edu.sdu.online.isdu.util.Logger
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.edit_operation_bar.*
import okhttp3.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/20
 *
 * 新建帖子页面
 ****************************************************
 */

class CreatePostActivity : NormActivity(), View.OnClickListener {

    private var btnAlbum: View? = null
    private var btnCamera: View? = null
    private var editTitle: EditText? = null
    private var richEditText: RichTextEditor? = null
    private var btnDone: TextView? = null
    private var btnBack: View? = null

    private val imageManager = ImageManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        initView()


        if (User.staticUser == null) User.staticUser = User.load()
        if (User.staticUser.studentNumber == null) {
            val dialog = AlertDialog(this)
            dialog.setTitle("未登录")
            dialog.setMessage("请登录后重试")
            dialog.setCancelOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.setPositiveButton("登录") {
                dialog.dismiss()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            dialog.setNegativeButton("返回") {
                finish()
                dialog.dismiss()
            }
            dialog.show()
        } else {

        }

    }

    private fun initView() {
        btnAlbum = operate_album
        btnCamera = operate_camera
        editTitle = edit_title
        richEditText = rich_edit_content
        btnDone = btn_done
        btnBack = btn_back

        btnAlbum!!.setOnClickListener(this)
        btnCamera!!.setOnClickListener(this)
        btnDone!!.setOnClickListener(this)
        btnBack!!.setOnClickListener(this)

        richEditText!!.setOnRtImageClickListener { path: String ->
            startActivity(Intent(this, ViewImageActivity::class.java)
                    .putExtra("file_path", path))
        }
        loadDraft()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            operate_album.id -> {
                imageManager.selectFromGallery(this)
            }
            operate_camera.id -> {
                imageManager.captureByCamera(this)
            }
            btn_done.id -> {
                val list = richEditText!!.buildEditData()
                performUpload(list)
            }
            btn_back.id -> {
                if (editTitle!!.text.toString() == "" &&
                        richEditText!!.buildEditData().size == 1 &&
                        richEditText!!.buildEditData()[0].imagePath == null &&
                        richEditText!!.buildEditData()[0].inputStr == "") {
                    finish()
                } else {
                    val dialog = AlertDialog(this)
                    dialog.setTitle("退出")
                    dialog.setMessage("保存草稿？")
                    dialog.setPositiveButton("保存") {
                        saveDraft()
                        dialog.dismiss()
                        finish()
                    }
                    dialog.setNegativeButton("放弃") {
                        clearDraft()
                        dialog.dismiss()
                        finish()
                    }
                    dialog.show()
                }
            }
        }
    }

    override fun onBackPressed() {
        btnBack!!.callOnClick()
//        super.onBackPressed()
    }

    /**
     * 上传帖子策略：
     * 向服务器申请上传，获得返回的帖子ID值
     * 遍历列表，依次上传图片，并保存服务端返回的图片URL
     * 再构造Post的对象列表，转为JSONArray后再上传服务器
     *
     * @param list 富文本编辑器生成的数据列表
     */
    private fun performUpload(list: List<RichTextEditor.EditData>) {
        // 预处理图片列表
        val hashMap = handleImages(list) // 获取优化后的图片散列表

        val hashMapList = ArrayList<String>()
        for (entry in hashMap.entries) {
            hashMapList.add(entry.value)
        }


        val dataObj = org.json.JSONObject()
        dataObj.put("uid", User.staticUser.uid)

        Log.d("AAA", JSON.toJSONString(hashMapList))


        val params = HashMap<String, String>()

        val jsonArray = org.json.JSONArray()

        for (i in 0 until list.size) {
            val data = list[i] // Get each data
            val obj = org.json.JSONObject()
            if (data.imagePath != null && data.imagePath != "") {
                // Image
                obj.put("type", 0)
                obj.put("content", "http://202.194.15.133:8380/isdu/forum/img/" + data.imageName)
            } else {
                // Text
                obj.put("type", 1)
                obj.put("content", data.inputStr)
            }

            jsonArray.put(obj)
        }
        // Create Complete!


        params.put("uid", User.staticUser.uid.toString())
        params.put("data", jsonArray.toString())
        post("http://211.87.226.186:8384/upload/img", "{\"uid\": \"${User.staticUser.uid}\", \"data\": \"${jsonArray}\"}", hashMap)
    }


    /**
     * 预处理图片
     * 给List中的图片编号
     * 存入Map中，可以优化上传
     */
    private fun handleImages(list: List<RichTextEditor.EditData>): HashMap<String, String> {
        val hashMap = HashMap<String, String>()

        for (data in list) {
            if (data.imagePath != null && data.imagePath != "") {
                // 是图片
                if (hashMap.containsKey(data.imagePath)) {
                    data.imageName = hashMap[data.imagePath]
                } else {
                    data.imageName = User.staticUser.uid.toString() + "_" + System.nanoTime().toString() + ".jpg"
                    hashMap[data.imagePath] = data.imageName
                }
            }
        }

        return hashMap
    }

    /**
     * 保存草稿
     */
    private fun saveDraft() {
        val string = JSON.toJSONString(richEditText!!.buildEditData())
        val editor = getSharedPreferences("post_draft", Context.MODE_PRIVATE).edit()
        editor.putString("content", string)
        editor.putString("title", editTitle!!.text.toString())
        editor.apply()
    }

    private fun clearDraft() {
        val editor = getSharedPreferences("post_draft", Context.MODE_PRIVATE).edit()
        editor.remove("content")
        editor.remove("title")
        editor.apply()
    }

    /**
     * 加载草稿
     */
    private fun loadDraft() {
        val sp = getSharedPreferences("post_draft", Context.MODE_PRIVATE)
        val string = sp.getString("content", "")
        val title = sp.getString("title", "")


        if (string != "") {
            richEditText!!.setEditData(JSON.parseArray(string, RichTextEditor.EditData::class.java))
        }

        editTitle!!.setText(title)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ImageManager.TAKE_PHOTO -> {
                if (resultCode == Activity.RESULT_OK) {
                    richEditText!!.insertImage(BitmapFactory.decodeFile(imageManager.imagePath),
                            imageManager.imagePath)
                }
            }
            ImageManager.OPEN_GALLERY -> {
                if (resultCode == Activity.RESULT_OK) {
                    imageManager.handleImage(data, this)
                    richEditText!!.insertImage(BitmapFactory.decodeFile(imageManager.imagePath),
                            imageManager.imagePath)
                }
            }
        }
    }

    /***********************************
     * 访问服务器
     * 上传帖子
     ***********************************/

    /**
     *
     */
    private fun post(actionUrl: String, params: String, hashMap: HashMap<String, String>) {
        Thread(Runnable {
            try {
                val BOUNDARY = "--------------et567z"
                //数据分隔线
                val MULTIPART_FORM_DATA = "Multipart/form-data"
                val url = URL(actionUrl)

                val conn = url.openConnection() as HttpURLConnection
                conn.doInput = true
                //允许输入
                conn.doOutput = true
                //允许输出
                conn.useCaches = false
                //不使用Cache
                conn.requestMethod = "POST"
                conn.setRequestProperty("Connection", "Keep-Alive")
                conn.setRequestProperty("Charset", "UTF-8")
                conn.setRequestProperty("Content-Type", "$MULTIPART_FORM_DATA;boundary=$BOUNDARY")

                //获取map对象里面的数据，并转化为string
                val sb = StringBuilder()
                //上传的表单参数部分，不需要更改
//            for (entry in params.entries) {
                //构建表单字段内容
                sb.append("--")
                sb.append(BOUNDARY)
                sb.append("\r\n")
                sb.append("Content-Disposition: form-data; name=\"" + "data" + "\"\r\n\r\n")
                sb.append(params)
                sb.append("\r\n")

                //上传图片部分
                val outStream = DataOutputStream(conn.outputStream)
                outStream.write(sb.toString().toByteArray())
                //发送表单字段数据

                for (entry in hashMap.entries) {
                    //调用自定义方法获取图片文件的byte数组
                    val content = readFileImage(entry.key)
                    //再次设置报头信息
                    val split = StringBuilder()
                    split.append("--")
                    split.append(BOUNDARY)
                    split.append("\r\n")

                    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!非常重要
                    //此处将图片的name设置为file ,filename不做限制，不需要管
                    split.append("Content-Disposition: form-data;name=\"file\";filename=\"${entry.value}\"\r\n")
                    //这里的Content-Type非常重要，一定要是图片的格式，例如image/jpeg或者image/jpg
                    //服务器端对根据图片结尾进行判断图片格式到底是什么,因此务必保证这里类型正确
                    split.append("Content-Type: image/jpg\r\n\r\n")
                    outStream.write(split.toString().toByteArray())
                    outStream.write(content, 0, content.size)
                    outStream.write("\r\n".toByteArray())
                    val endData = ("--$BOUNDARY--\r\n").toByteArray()
                    //数据结束标志
                    outStream.write(endData)
                    Log.d("AAA", "Image ${entry.key} Success")
                }



                //返回状态判断
                val cah = conn.responseCode

                runOnUiThread {
                    if (cah == 200) {
                        Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show()
                        clearDraft()
                        finish()
                    } else if (cah == 400) {
                        Toast.makeText(this, "发布失败(400)", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "发布失败($cah)", Toast.LENGTH_SHORT).show()
                    }
                }


                outStream.flush()
                outStream.close()
                conn.disconnect()

            } catch (e: Exception) {
                Logger.log(e)
                e.printStackTrace()
            }
        }).start()

    }


    @Throws(IOException::class)
    private fun readFileImage(filePath: String): ByteArray {
//        val bufferedInputStream = BufferedInputStream(
//                FileInputStream(filePath))
//        val len = bufferedInputStream.available()
//
//
//
//        var bytes: ByteArray? = ByteArray(len)
//        val r = bufferedInputStream.read(bytes)
//        if (len != r) {
//            bytes = null
//            throw IOException("读取文件不正确")
//        }
//        bufferedInputStream.close()
//        return bytes!!
        return ImageManager.convertBitmapToByteArray(BitmapFactory.decodeFile(filePath))
    }
}
