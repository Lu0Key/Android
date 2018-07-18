package cn.edu.sdu.online.isdu.ui.activity


import android.os.Bundle
import android.view.View
import android.widget.ImageView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/6/5
 *
 * 校历活动
 ****************************************************
 */

class CalendarActivity : SlideActivity(), View.OnClickListener {

    private var btnBack: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        initView()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_back -> {
                finish()
            }
        }
    }
    private fun initView() {
        btnBack=findViewById(R.id.btn_back)
        btnBack!!.setOnClickListener(this)
    }

}
