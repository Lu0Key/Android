package cn.edu.sdu.online.isdu.ui.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.SlideActivity
import kotlinx.android.synthetic.main.activity_school_bus.*
import kotlinx.android.synthetic.main.design_image_button.view.*

/**
 ****************************************************
 * @author zsj
 * Last Modifier: Cola_Mentos
 * Last Modify Time: 2018/7/9
 *
 * 校车活动
 ****************************************************
 */

class SchoolBusActivity : SlideActivity() , View.OnClickListener{

    private var clearBtn : Button ?= null
    private var searchBtn : Button ?= null
    private var workdayBtn : Button ?= null
    private var non_workdayBtn : Button ?= null
    private var backBtn : ImageView ?= null
    private var searchNum : Int = 1
    private var fromP : Int = 0
    private var toP : Int = 0
    private val xqBtn : Array<cn.edu.sdu.online.isdu.ui.design.ImageButton?> = arrayOfNulls(10)
    private val xqName = arrayOf("","中心校区","洪家楼校区","趵突泉校区","软件园校区","兴隆山校区","千佛山校区")
    private var tipText : TextView ?= null
    /*
    private var zxBtn : cn.edu.sdu.online.isdu.ui.design.ImageButton ?= null
    private var hjlBtn : cn.edu.sdu.online.isdu.ui.design.ImageButton ?= null
    private var btqBtn : cn.edu.sdu.online.isdu.ui.design.ImageButton ?= null
    private var rjyBtn : cn.edu.sdu.online.isdu.ui.design.ImageButton ?= null
    private var xlsBtn : cn.edu.sdu.online.isdu.ui.design.ImageButton ?= null
    private var qfsBtn : cn.edu.sdu.online.isdu.ui.design.ImageButton ?= null*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_bus)
        initView()
    }

    /**
     * 初始化view
     */
    private fun initView(){
        clearBtn = btn_clear
        searchBtn = btn_search
        workdayBtn = workday
        non_workdayBtn = non_workday
        tipText = tips
        backBtn = btn_back
        xqBtn[1] = btn_zhongxin
        xqBtn[2] = btn_hongjialou
        xqBtn[3] = btn_baotuquan
        xqBtn[4] = btn_ruanjianyuan
        xqBtn[5] = btn_xinglongshan
        xqBtn[6] = btn_qianfoshan
        clearBtn!!.setOnClickListener(this)
        searchBtn!!.setOnClickListener(this)
        workdayBtn!!.setOnClickListener(this)
        non_workdayBtn!!.setOnClickListener(this)
        backBtn!!.setOnClickListener(this)
        for (i in 1..6){
            xqBtn[i]!!.setOnClickListener(this)
            xqBtn[i]!!.tag = i
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
        /**
         * 重置按钮
         */
            btn_clear.id->{
                fromP=0
                toP=0
                for (i in 1..6){
                    xqBtn[i]!!.linear_layout.setBackgroundColor(resources.getColor(R.color.colorWhite))
                    xqBtn[i]!!.setText(xqName[i])
                }
                tipText!!.text=""
            }
        /**
         * 查询按钮
         */
            btn_search.id->{
                if (fromP == 0 && toP == 0){
                    tipText!!.text = "请选择起点和终点"
                }
                else if (toP == 0){
                    tipText!!.text = "请选择终点"
                }
                else {
                    val intent : Intent = Intent(this,SchoolBusTable::class.java)
                    intent.putExtra("searchNum",searchNum)
                    intent.putExtra("fromP",fromP)
                    intent.putExtra("toP",toP)
                    startActivity(intent)
                }
            }
            workday.id->{
                searchNum = 1
            }
            non_workday.id->{
                searchNum = 2
            }
            btn_back.id->{
                finish();
            }
        /**
         * 校区选择按钮
         */
            else ->{
                for (i in 1..6){
                    if (v!!.id == xqBtn[i]!!.id){
                        xqBtn[i]!!.linear_layout.setBackgroundColor(resources.getColor(R.color.colorThemeGrey))
                        if (fromP == 0) {
                            fromP = i
                            xqBtn[i]!!.setText("从  "+xqName[i])
                        }
                        else if (i != fromP && i != toP){
                            if (toP != 0){
                                xqBtn[fromP]!!.linear_layout.setBackgroundColor(resources.getColor(R.color.colorWhite))
                                xqBtn[fromP]!!.setText(xqName[fromP])
                                fromP = toP
                                xqBtn[fromP]!!.setText("从  "+xqName[fromP])
                            }
                            toP = i
                            xqBtn[i]!!.setText("到  "+xqName[i])

                        }
                    }
                }

            }

        }
    }
}
