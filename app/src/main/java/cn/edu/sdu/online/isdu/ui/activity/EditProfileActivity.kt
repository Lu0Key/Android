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
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.ui.design.dialog.AlertDialog
import cn.edu.sdu.online.isdu.ui.design.dialog.OptionDialog
import cn.edu.sdu.online.isdu.util.ImageManager
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/6/13
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

    private var imageManager: ImageManager? = ImageManager()

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

//        editGender!!.setOnClickListener {
//            val list = listOf("男", "女", "保密")
//            val dialog = OptionDialog(this, list)
//            dialog.setMessage("选择性别")
//            dialog.setOnItemSelectListener {
//                itemName ->
//                editGender!!.text = itemName
//            }
//            dialog.show()
//        }

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

        }
    }

    private fun loadUserInformation() {
        val user = User.staticUser
        val bmp = ImageManager.convertStringToBitmap(user.avatarString)
        avatar!!.setImageBitmap(bmp)
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
                    avatar!!.setImageBitmap(BitmapFactory.decodeStream(
                            contentResolver.openInputStream(imageManager!!.destUri)))
                }
            }
            UCrop.RESULT_ERROR -> {
                Toast.makeText(this, "裁剪图片失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
