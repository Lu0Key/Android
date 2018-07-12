package cn.edu.sdu.online.isdu.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.ui.design.dialog.AlertDialog
import cn.edu.sdu.online.isdu.ui.design.dialog.OptionDialog
import cn.edu.sdu.online.isdu.ui.design.dialog.ProgressDialog
import cn.edu.sdu.online.isdu.util.ImageManager
import cn.edu.sdu.online.isdu.util.Logger
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/12
 *
 * 编辑个人资料页面
 *
 * #增加返回键确认
 ****************************************************
 */

class EditProfileActivity : SlideActivity() {

    private var editUserName: EditText? = null
    private var editGender: TextView? = null
    private var editMajor: TextView? = null
    private var editDepart: TextView? = null
    private var editName: TextView? = null
    private var editIntroduction: EditText? = null
    private var avatar: CircleImageView? = null
    private var btnBack: ImageView? = null
    private var btnDone: ImageView? = null
    private var btnEditAvatar: TextView? = null

    private var finalBitmap: Bitmap? = null

    private var imageManager: ImageManager? = ImageManager()

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        initView()
        loadUserInformation()
    }

    private fun initView() {
        editUserName = findViewById(R.id.edit_user_name)
        editGender = findViewById(R.id.edit_gender)
        editMajor = findViewById(R.id.edit_profession)
        editDepart = findViewById(R.id.edit_campus)
        editName = findViewById(R.id.edit_name)
        editIntroduction = findViewById(R.id.edit_introduction)
        avatar = findViewById(R.id.circle_image_view)
        btnBack = findViewById(R.id.btn_back)
        btnDone = findViewById(R.id.btn_done)
        btnEditAvatar = findViewById(R.id.btn_edit_avatar)


        btnEditAvatar!!.setOnClickListener {
            val list = listOf("相机拍摄", "从相册选择")
            val dialog = OptionDialog(this, list)
            dialog.setMessage("设置头像")
            dialog.setOnItemSelectListener {
                itemName ->
                if (itemName == "相机拍摄") {
                    imageManager!!.captureByCamera(this)
                    dialog.dismiss()
                } else {
                    imageManager!!.selectFromGallery(this)
                    dialog.dismiss()
                }
            }
            dialog.show()
        }

        btnBack!!.setOnClickListener {
            val dialog = AlertDialog(this)
            dialog.setTitle("退出")
            dialog.setMessage("确定要退出吗？所做的更改将不会保存。")
            dialog.setPositiveButton("是") {
                dialog.dismiss()
                finish()
            }
            dialog.setNegativeButton("否") {
                dialog.dismiss()
            }
            dialog.show()
        }

        btnDone!!.setOnClickListener {
            submitChange()
        }
    }

    private fun loadUserInformation() {
        var user = User.staticUser
        if (user == null) {
            User.staticUser = User.load()
            user = User.staticUser
        }
        finalBitmap = ImageManager.convertStringToBitmap(user.avatarString)
        avatar!!.setImageBitmap(finalBitmap)
        editUserName!!.setText(user.nickName)

        when (user.gender) {
            User.GENDER_MALE -> editGender!!.text = "男"
            User.GENDER_FEMALE -> editGender!!.text = "女"
            User.GENDER_SECRET -> editGender!!.text = "保密"
        }

        editMajor!!.text = user.major
        editDepart!!.text = user.depart
        editName!!.text = user.name
        editIntroduction!!.setText(user.selfIntroduce)
    }

    /**
     * 提交更新
     */
    private fun submitChange() {
        if (editUserName!!.text.toString() == "") {
            val d = AlertDialog(this)
            d.setCancelOnTouchOutside(true)
            d.setTitle("缺少信息")
            d.setMessage("用户名不能为空")
            d.setPositiveButton("取消") {d.dismiss()}
            d.setNegativeButton(null, null)
            d.show()
        } else if (finalBitmap == null) {
            val d = AlertDialog(this)
            d.setCancelOnTouchOutside(true)
            d.setTitle("缺少信息")
            d.setMessage("头像不能为空")
            d.setPositiveButton("取消") {d.dismiss()}
            d.setNegativeButton(null, null)
            d.show()
        } else {
            progressDialog = ProgressDialog(this, false)
            progressDialog!!.setMessage("正在更新信息...")
            progressDialog!!.setButton(null, null)
            progressDialog!!.show()

            val keys = listOf("studentNumber", "j_password", "nickname", "avatar", "sign")
            val values = listOf(User.staticUser.studentNumber, User.staticUser.passwordMD5,
                    editUserName!!.text.toString(), ImageManager.convertBitmapToString(finalBitmap),
                    editIntroduction!!.text.toString())

            val callback = object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    runOnUiThread {
                        progressDialog!!.dismiss()
                        Toast.makeText(this@EditProfileActivity,
                                "网络出错", Toast.LENGTH_SHORT).show()
                        Logger.log(e)
                    }
                }

                override fun onResponse(call: Call?, response: Response?) {
                    runOnUiThread {
//                        Log.d("AAA", response?.body()?.string())
//                        Log.d("AAA", ImageManager.getBitmapSize(finalBitmap).toString())
                        progressDialog!!.dismiss()
                        User.staticUser.nickName = editUserName!!.text.toString()
                        User.staticUser.avatarString = ImageManager.convertBitmapToString(finalBitmap)
                        User.staticUser.selfIntroduce = editIntroduction!!.text.toString()
                        User.staticUser.save(this@EditProfileActivity)
                        Toast.makeText(this@EditProfileActivity,
                                "更新成功", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
            NetworkAccess.buildRequest(ServerInfo.urlUpdate, keys, values, callback)
        }
    }

    override fun onBackPressed() {
        btnBack!!.callOnClick()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imageManager?.selectFromGallery(this)
            } else {
                Toast.makeText(this, "权限拒绝，无法打开相册", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ImageManager.TAKE_PHOTO -> {
                if (resultCode == Activity.RESULT_OK) {
                    imageManager?.openCrop(this)
                }
            }
            ImageManager.OPEN_GALLERY -> {
                if (resultCode == Activity.RESULT_OK) {
                    imageManager?.handleImage(this, data)
                }
            }
            UCrop.REQUEST_CROP -> {
                if (data != null) {
                    finalBitmap = BitmapFactory.decodeStream(
                            contentResolver.openInputStream(imageManager!!.destUri))
                    avatar!!.setImageBitmap(finalBitmap)
                }
            }
            UCrop.RESULT_ERROR -> {
                Toast.makeText(this, "裁剪图片失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
