package cn.edu.sdu.online.isdu.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/6/5
 *
 * 校车活动
 ****************************************************
 */

class SchoolBusActivity : SlideActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_bus)
    }
}
