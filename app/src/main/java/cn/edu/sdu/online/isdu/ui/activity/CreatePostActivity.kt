package cn.edu.sdu.online.isdu.ui.activity

import android.app.Activity
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
import cn.edu.sdu.online.isdu.ui.design.dialog.AlertDialog
import cn.edu.sdu.online.isdu.ui.design.xrichtext.RichTextEditor
import cn.edu.sdu.online.isdu.util.ImageManager

import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.edit_operation_bar.*
import java.io.File

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/19
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
                val dialog = AlertDialog(this)
                dialog.setTitle("退出")
                dialog.setMessage("确定要退出吗？所做更改将不会保存")
                dialog.setPositiveButton("退出") {
                    dialog.dismiss()
                    finish()
                }
                dialog.setNegativeButton("取消") {
                    dialog.dismiss()
                }
                dialog.show()
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
        // 向服务器请求上传
        
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
}
