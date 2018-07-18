package cn.edu.sdu.online.isdu.ui.fragments.search

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.ui.activity.MyHomePageActivity
import cn.edu.sdu.online.isdu.util.ImageManager
import de.hdodenhof.circleimageview.CircleImageView

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/18
 *
 * 搜索用户的Fragment
 ****************************************************
 */

class SearchUserFragment : Fragment() {
    private var mAdapter: MyAdapter? = null
    private var loadingLayout: View? = null
    private var recyclerView: RecyclerView? = null
    private var dataList = arrayListOf<User>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_post, container, false)
        initView(view)

        initRecyclerView()
        return view
    }

    private fun initView(view: View) {
        loadingLayout = view.findViewById(R.id.loading_layout)
        recyclerView = view.findViewById(R.id.recycler_view)
    }
    private fun initRecyclerView() {
        mAdapter = MyAdapter(dataList)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = mAdapter
    }
    private fun initData(){
        dataList.add(User.load("201700301044"))
        dataList.add(User.load("201700301044"))
    }

    fun initData(user : User){
        dataList.clear()
        dataList.add(user)
    }
    fun refresh(){
        mAdapter!!.notifyDataSetChanged()
    }

    fun clear(){
        dataList.clear();
        if(mAdapter!=null){
            mAdapter!!.notifyDataSetChanged()
        }
    }


    inner class MyAdapter(mDataList: List<User>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        private var mDataList = mDataList

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.searchuser_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = mDataList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val user = mDataList[position]
            val bmp = ImageManager.convertStringToBitmap(user.avatarString)
            holder.circleImageView!!.setImageBitmap(bmp)
            holder.userName!!.text = user.nickName
            holder.userSign!!.text = user.selfIntroduce
            holder.btnfollow!!.setOnClickListener {

                Log.w("click","follow")
            }
            holder.itemLayout!!.setOnClickListener {
                Log.i("id",user.studentNumber)
                user.save(this@SearchUserFragment.activity)
                startActivity(Intent(context, MyHomePageActivity::class.java)
                        .putExtra("id", user.uid))
            }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var circleImageView: CircleImageView? = view.findViewById(R.id.circle_image_view)
            var userName: TextView? = view.findViewById(R.id.user_name)
            var userSign: TextView? = view.findViewById(R.id.user_sign)
            var btnfollow: TextView? = view.findViewById(R.id.btn_follow)
            var itemLayout: View = view.findViewById(R.id.item_layout)
        }
    }
}