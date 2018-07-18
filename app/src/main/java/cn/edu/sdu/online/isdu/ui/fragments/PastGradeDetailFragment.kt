package cn.edu.sdu.online.isdu.ui.fragments

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.NumberPicker
import android.widget.PopupWindow
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.bean.Grade
import kotlinx.android.synthetic.main.design_custom_number_picker.*
import kotlinx.android.synthetic.main.fragment_past_grade_detail.*
import android.view.KeyEvent.KEYCODE_BACK




/**
 ****************************************************
 * @author Cola_Mentos
 * Last Modifier: Cola_Mentos
 * Last Modify Time: 2018/7/11
 *
 * 历年成绩碎片
 ****************************************************
 */
class PastGradeDetailFragment : Fragment() , View.OnClickListener {

    private var recyclerView : RecyclerView?= null
    private var textView : TextView?= null
    private var adapter: MyAdapter? = null
    private var dataList: MutableList<Grade> = ArrayList()
    private var chooseTerm : TextView ?= null
    private var numberPicker: NumberPicker? = null
    private var btnConfirm : Button ?= null
    private var btnCancel : Button ?= null
    private var pickerView : View ?= null
    private var currentTerm : Int = 0
    private var popupWindow : PopupWindow ?= null
    private val term = arrayOf("一", "二", "三", "四", "五", "六","七","八")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_past_grade_detail, container, false)
        initView(view)
        initRecyclerView()
        initNumberPicker()
        return view
    }

    fun initView(v : View){


        chooseTerm = v.findViewById(R.id.choose_term)
        recyclerView = v.findViewById(R.id.recycler_view)
        textView = v.findViewById(R.id.text_view)

        chooseTerm!!.setOnClickListener(this)

    }

    /**
     * 重写后退键，当弹出popupWindow时点击后退键不会关闭当前活动
     */
    override fun onResume() {
        super.onResume()
        view!!.isFocusableInTouchMode = true
        view!!.requestFocus()
        view!!.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action === KeyEvent.ACTION_UP) {
                if (popupWindow != null && popupWindow!!.isShowing()) {
                    popupWindow!!.dismiss()
                    true//当fragment消费了点击事件后，返回true，activity中的点击事件就不会执行了
                }
            }
            false//当fragmenet没有消费点击事件，返回false，activity中继续执行对应的逻辑
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            choose_term.id->{
                numberPicker!!.value = currentTerm

                // 强制隐藏键盘
                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(view!!.windowToken, 0)

                // 填充布局并设置弹出窗体的宽,高
                popupWindow = PopupWindow(pickerView,
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                // 设置弹出窗体可点击
                popupWindow!!.setFocusable(true)
                // 设置弹出窗体动画效果
                popupWindow!!.setAnimationStyle(R.style.AnimBottom)
                // 触屏位置如果在选择框外面则销毁弹出框
                popupWindow!!.setOutsideTouchable(true)
                // 设置弹出窗体的背景
                popupWindow!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                popupWindow!!.showAtLocation(pickerView,
                        Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)

                // 设置背景透明度
                val lp = activity!!.window.attributes
                lp.alpha = 0.5f
                activity!!.window.attributes = lp

                // 添加窗口关闭事件
                popupWindow!!.setOnDismissListener(PopupWindow.OnDismissListener {
                    val lp = activity!!.window.attributes
                    lp.alpha = 1f
                    activity!!.window.attributes = lp
                })
            }
            btnConfirm!!.id->{
                currentTerm = numberPicker!!.value
                chooseTerm!!.text = "   "+term[currentTerm]+"   "
                popupWindow!!.dismiss()
            }
            btnCancel!!.id->{
                popupWindow!!.dismiss()
            }
        }
    }

    /**
     * 初始化RecyclerView
     */
    private fun initRecyclerView() {
        adapter = MyAdapter(dataList, context!!)

        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = adapter
    }

    /**
     * 初始化滚动框布局
     */
    private fun initNumberPicker() {
        pickerView = LayoutInflater.from(context).inflate(R.layout.design_custom_number_picker, null)
        btnConfirm = pickerView!!.findViewById(R.id.btn_confirm)
        btnCancel = pickerView!!.findViewById(R.id.btn_cancel)
        numberPicker = pickerView!!.findViewById(R.id.numberPicker)
        numberPicker!!.displayedValues = term
        numberPicker!!.maxValue = 7
        numberPicker!!.minValue = 0
        numberPicker!!.setFocusable(false)
        numberPicker!!.setFocusableInTouchMode(false)
        numberPicker!!.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS)

        btnConfirm!!.setOnClickListener(this)
        btnCancel!!.setOnClickListener(this)
        setNumberPickerDividerColor(numberPicker!!)
    }

    /**
     * 自定义滚动框分隔线颜色
     */
    private fun setNumberPickerDividerColor(number: NumberPicker) {
        val pickerFields = NumberPicker::class.java.declaredFields
        for (pf in pickerFields) {
            if (pf.name == "mSelectionDivider") {
                pf.isAccessible = true
                try {
                    //设置分割线的颜色值
                    pf.set(number, ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorPurpleFade)))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                break
            }
        }
    }

    inner class MyAdapter(dataList: List<Grade>?, context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        private val context = context
        private val dataList = dataList

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (!dataList!!.isEmpty()){
                textView!!.text = ""
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.grade_item, parent, false)
            return ViewHolder(v = view)
        }

        override fun getItemCount(): Int = dataList?.size ?: 0

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val cjText : TextView = v.findViewById(R.id.cj) // 成绩
            val jdText : TextView = v.findViewById(R.id.jd) // 绩点
        }
    }

}