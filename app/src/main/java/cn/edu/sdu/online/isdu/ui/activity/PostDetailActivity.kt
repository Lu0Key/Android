package cn.edu.sdu.online.isdu.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.ui.design.xrichtext.RichTextView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_post_detail.*

class PostDetailActivity : SlideActivity() {

    private var postId: Int = 0 // 帖子ID
    private var url: String = "" // URL

    private var txtTitle: TextView? = null
    private var txtContent: RichTextView? = null
    private var circleImageView: CircleImageView? = null // 发帖者头像
    private var txtNickname: TextView? = null
    private var txtDate: TextView? = null
    private var posterLayout: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

//        postId = intent.getIntExtra("url", 0)
        url = intent.getStringExtra("url")

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

    }

    /**
     * 获取帖子内容
     */
    private fun getPostData() {

    }

    /**
     * 获取发帖用户的信息
     */
    private fun getUserData(id: Int) {

    }

}
