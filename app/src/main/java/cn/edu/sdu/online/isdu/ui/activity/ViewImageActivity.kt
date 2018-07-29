package cn.edu.sdu.online.isdu.ui.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.*
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.BaseActivity
import cn.edu.sdu.online.isdu.app.MyApplication
import cn.edu.sdu.online.isdu.app.NormActivity
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.ui.design.DraggableImageView
import cn.edu.sdu.online.isdu.ui.design.dialog.OptionDialog
import cn.edu.sdu.online.isdu.util.FileUtil
import cn.edu.sdu.online.isdu.util.ImageManager
import cn.edu.sdu.online.isdu.util.Phone
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.activity_view_image.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/14
 *
 * 图片浏览活动
 ****************************************************
 */

class ViewImageActivity : NormActivity() {

    private var draggableImageView: DraggableImageView? = null
    private var progressBar: ProgressBar? = null
    private var textView: TextView? = null
    private var loadingLayout: LinearLayout? = null

    var resId: Int = 0
    var url: String = ""
    var bmpStr: String = ""
    var cacheKey: String = ""
    var isString = false
    var filePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        decorateWindow()

        draggableImageView = image_view
        progressBar = progress_bar
        textView = text
        loadingLayout = loading_layout

        loadingLayout!!.visibility = View.GONE

        draggableImageView!!.setOnClickListener(DraggableImageView.OnClickListener {
            finish()
        })

        draggableImageView!!.setOnLongClickListener(DraggableImageView.OnLongClickListener {
            Phone.vibrate(this, Phone.VibrateType.Once)
            val dialog = OptionDialog(this, listOf("保存图片", "取消"))
            dialog.setCancelOnTouchOutside(true)
            dialog.setMessage("图片")
            dialog.setOnItemSelectListener { itemName ->
                when (itemName) {
                    "保存图片" -> {
                        dialog.dismiss()
                        val file = File(Environment.getExternalStorageDirectory().toString() +
                                "/iSDU/Image/" + System.currentTimeMillis() + ".jpg")

                        if (!file.exists()) {
                            if (!file.parentFile.exists()) file.parentFile.mkdirs()
                            file.createNewFile()
                        }
                        val fos = FileOutputStream(file)

                        if (resId != 0) {
                            val bmp = BitmapFactory.decodeResource(resources, resId)
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                            fos.flush()
                            fos.close()
                            runOnUiThread {
                                Toast.makeText(this, "成功保存至 /sdcard/iSDU/Image/" + file.name, Toast.LENGTH_SHORT).show()
                            }
                        } else if (url != "") {
                            if (cacheKey == "") {
                                NetworkAccess.cache(url) { success, cachePath ->
                                    if (success) {
                                        /********************************************
                                         *
                                         ********************************************/
                                        val bmp = if (isString) ImageManager.convertStringToBitmap(FileUtil.getStringFromFile(cachePath)) else
                                            BitmapFactory.decodeFile(cachePath)
                                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                                        fos.flush()
                                        fos.close()
                                        runOnUiThread {
                                            Toast.makeText(this, "成功保存至 /sdcard/iSDU/Image/" + file.name, Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        runOnUiThread {
                                            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                NetworkAccess.cache(url, cacheKey) { success, cachePath ->
                                    if (success) {
                                        /********************************************
                         dd                *
                                         ********************************************/
                                        val bmp = if (isString) ImageManager.convertStringToBitmap(FileUtil.getStringFromFile(cachePath)) else
                                            BitmapFactory.decodeFile(cachePath)
                                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                                        fos.flush()
                                        fos.close()
                                        runOnUiThread {
                                            Toast.makeText(this, "成功保存至 /sdcard/iSDU/Image/" + file.name, Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        runOnUiThread {
                                            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }

                        } else if (bmpStr != "") {
                            val bmp = ImageManager.convertStringToBitmap(bmpStr)
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                            fos.flush()
                            fos.close()
                            runOnUiThread {
                                Toast.makeText(this, "成功保存至 /sdcard/iSDU/Image/" + file.name, Toast.LENGTH_SHORT).show()
                            }
                        }

                        runOnUiThread {
                            decorateWindow()
                        }

                    }
                    "取消" -> {
                        dialog.dismiss()
                        runOnUiThread {
                            decorateWindow()
                        }

                    }
                }
            }
            dialog.show()
        })

        resId = intent.getIntExtra("res_id", 0)
        url = if (intent.getStringExtra("url") == null) "" else intent.getStringExtra("url")
        bmpStr = if (intent.getStringExtra("bmp_str") == null) "" else intent.getStringExtra("bmp_str")
        cacheKey = if (intent.getStringExtra("key") == null) "" else intent.getStringExtra("key")
        isString = intent.getBooleanExtra("isString", false)
        filePath = if (intent.getStringExtra("file_path") == null) "" else intent.getStringExtra("file_path")

        if (resId != 0) {
            draggableImageView!!.setImageResource(resId)
        } else if (url != "") {
            loadingLayout!!.visibility = View.VISIBLE
            textView!!.text = "正在加载..."
            NetworkAccess.cache(url, cacheKey) { success, cachePath ->
                if (success) {
                    if (isString) {
                        val bmp = ImageManager.loadStringFromFile(cachePath)
                        runOnUiThread {
                            draggableImageView!!.setImageBitmap(bmp)
                            loadingLayout!!.visibility = View.GONE
                        }
                    } else {
                        if (cachePath.toLowerCase().endsWith(".gif#") ||
                                cachePath.toLowerCase().endsWith(".gif")) {
                            runOnUiThread {
                                Glide.with(MyApplication.getContext())
                                        .asGif()
                                        .load(cachePath)
                                        .into(draggableImageView!!)

                                loadingLayout!!.visibility = View.GONE
                            }
                        } else {
                            val target = object : ViewTarget<DraggableImageView, Drawable>(draggableImageView!!) {
                                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                    this.view.setImageBitmap((resource as BitmapDrawable).bitmap)
                                }
                            }
                            runOnUiThread {
                                Glide.with(MyApplication.getContext())
                                        .load(cachePath)
                                        .into(target)

                                loadingLayout!!.visibility = View.GONE
                            }
                        }

                    }

                } else {
                    runOnUiThread {
                        loadingLayout!!.visibility = View.VISIBLE
                        progressBar!!.visibility = View.GONE
                        textView!!.text = "加载失败"
                    }
                }
            }
        } else if (bmpStr != "") {
            val bmp = ImageManager.convertStringToBitmap(bmpStr)
            draggableImageView!!.setImageBitmap(bmp)
        } else if (filePath != "") {
            val bmp = BitmapFactory.decodeFile(filePath)
            draggableImageView!!.setImageBitmap(bmp)
        }
    }

    override fun onResume() {
        super.onResume()
        decorateWindow()
    }

//    private fun decorateWindow() {
//        val decorView = window.decorView
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//        } else {
//            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
//        }
//    }

}
