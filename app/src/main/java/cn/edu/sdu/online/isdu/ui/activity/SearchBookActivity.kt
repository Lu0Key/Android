package cn.edu.sdu.online.isdu.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.AlphaActivity

class SearchBookActivity : AlphaActivity(), View.OnClickListener {

    private var btnBack: View? = null
    private var editSearch: EditText? = null
    private var btnSearch: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_book)

        initView()
    }

    private fun initView() {
        btnBack = findViewById(R.id.btn_back)
        editSearch = findViewById(R.id.edit_search)
        btnSearch = findViewById(R.id.btn_search)

        btnBack!!.setOnClickListener(this)
        btnSearch!!.setOnClickListener(this)

        editSearch!!.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                btnSearch!!.callOnClick()
                true
            }
            false
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_back -> {
                finish()
            }
            R.id.btn_search -> {

            }
        }
    }
}