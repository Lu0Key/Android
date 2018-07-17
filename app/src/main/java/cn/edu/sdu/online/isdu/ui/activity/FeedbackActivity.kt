package cn.edu.sdu.online.isdu.ui.activity

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.view.View
import android.widget.*
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.ui.design.button.WideButton
import cn.edu.sdu.online.isdu.ui.design.dialog.OptionDialog
import cn.edu.sdu.online.isdu.util.Settings

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/8
 *
 * 设置Activity
 *
 * #添加云同步
 ****************************************************
 */

class FeedbackActivity : SlideActivity(), View.OnClickListener{
    private var btnFeedback: Button?=null;
    private var btnBack: ImageView? = null
    private var textQQ: TextInputEditText? = null
    private var textPhone:TextInputEditText? = null
    private var textFeedback:EditText?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        initView()
    }
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_back -> {
                finish()
            }
            R.id.btn_feedback -> {
                val feedback = textFeedback!!.text.toString()
                val qq = textQQ!!.text.toString()
                val phone = textPhone!!.text.toString()
                Toast.makeText(this, "反馈成功",Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    private fun initView() {
        btnBack = findViewById(R.id.btn_back)
        btnFeedback = findViewById(R.id.btn_feedback)
        textQQ = findViewById(R.id.feedback_qq)
        textPhone = findViewById(R.id.feedback_phone)
        textFeedback = findViewById(R.id.et_feedback)
        textFeedback!!.setSingleLine(false)
        btnBack!!.setOnClickListener(this)
        btnFeedback!!.setOnClickListener(this)
    }
}
