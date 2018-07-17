package cn.edu.sdu.online.isdu.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.bean.Book
import cn.edu.sdu.online.isdu.ui.activity.SearchBookActivity
import cn.edu.sdu.online.isdu.ui.design.dialog.ProgressDialog
import kotlinx.android.synthetic.main.fragment_my_book.*
import java.util.ArrayList
/**
 ****************************************************
 * @author Cola_Mentos
 * Last Modifier: Cola_Mentos
 * Last Modify Time: 2018/7/16
 *
 * 图书馆馆藏查询碎片
 ****************************************************
 */

class MyBookFragment : Fragment(), View.OnClickListener {


    private var recyclerView: RecyclerView?= null
    private var adapter: MyBookFragment.MyAdapter? = null
    private val dataList: MutableList<Book> = ArrayList()
    private var searchBar : LinearLayout?= null
    private var progressDialog : ProgressDialog ?=  null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_book, container, false)
        initView(view)
        initRecyclerView()

        return (view)
    }

    private fun initView(view : View){
        recyclerView = view.findViewById(R.id.recycler_view)
        searchBar = view.findViewById(R.id.search_bar)

        searchBar!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            search_bar.id->{
                startActivity(Intent(activity!!,SearchBookActivity::class.java))
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
        progressDialog = ProgressDialog(context,false)
        //progressDialog!!.setMessage("正在加载中...")
        //progressDialog!!.setButton(null,null)
        //progressDialog!!.show()
    }


    /**
     * recyclerView容器类
     */
    internal inner class MyAdapter(dataList: List<Book>?, context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

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
                            R.layout.book_item, parent, false)
            return ViewHolder(v = view)
        }

        override fun getItemCount(): Int = 2 //dataList?.size ?: 0

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val bookName : TextView = v.findViewById(R.id.book_name) // 借阅书名
            val idNumber : TextView = v.findViewById(R.id.id_number) // 借阅号
            val bookPlace : TextView = v.findViewById(R.id.book_place) // 借阅地点
            val borrowDate : TextView = v.findViewById(R.id.borrow_date) //借阅日期
            val backDate : TextView = v.findViewById(R.id.back_date) // 应还日期
            val remainDays : TextView = v.findViewById(R.id.remain_days) // 剩余天数
            val borrowTimes : TextView = v.findViewById(R.id.borrow_times) // 续借次数
            val line : View = v.findViewById(R.id.separate_line) // 分割线
        }
    }

}
