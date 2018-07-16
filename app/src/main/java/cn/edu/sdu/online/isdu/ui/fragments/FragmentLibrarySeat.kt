package cn.edu.sdu.online.isdu.ui.fragments


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.bean.LibrarySeat
import cn.edu.sdu.online.isdu.ui.design.button.LibraryRadioImageButton

/**
 ****************************************************
 * @author Cola_Mentos
 * Last Modifier: Cola_Mentos
 * Last Modify Time: 2018/7/16
 *
 * 图书馆御座查询碎片
 ****************************************************
 */

class FragmentLibrarySeat : Fragment() {

    private var radioButtons = ArrayList<LibraryRadioImageButton>()
    private var recyclerView : RecyclerView ?= null
    private var adapter: FragmentLibrarySeat.MyAdapter? = null
    private val dataList: MutableList<LibrarySeat> = java.util.ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_library_seat, container, false)
        initView(view)
        initRecyclerView()
        return view
    }

    private fun initView(view : View){

        recyclerView = view.findViewById(R.id.recycler_view)
        radioButtons.add(view.findViewById(R.id.radio_zhongxin))
        radioButtons.add(view.findViewById(R.id.radio_jiangzhen))
        radioButtons.add(view.findViewById(R.id.radio_honglou))
        radioButtons.add(view.findViewById(R.id.radio_baotuq))
        radioButtons.add(view.findViewById(R.id.radio_gongxue))
        radioButtons.add(view.findViewById(R.id.radio_xinglong))

        for (i in 0..5) {
            radioButtons[i].setOnClickListener {
                if (radioButtons[i].isSelected)
                    cancelAllClick()
                else {
                    cancelAllClick()
                    radioButtons[i].isSelected = true
                }
            }
        }
    }

    private fun cancelAllClick() {
        for (i in 0..5)
            radioButtons[i].isSelected = false
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
     * recyclerView容器类
     */
    internal inner class MyAdapter(dataList: List<LibrarySeat>?, context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        private val context = context
        private val dataList = dataList

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (position == 0){
                holder.line.visibility = View.GONE
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.seat_item, parent, false)
            return ViewHolder(v = view)
        }

        override fun getItemCount(): Int = 3//dataList?.size ?: 0

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val line : View = v.findViewById(R.id.line)
        }
    }

}
