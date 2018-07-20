package cn.edu.sdu.online.isdu.ui.fragments.search

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.LazyLoadFragment
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.ui.activity.MyHomePageActivity
import cn.edu.sdu.online.isdu.util.ImageManager
import cn.edu.sdu.online.isdu.util.Logger
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/18
 *
 * 搜索用户的Fragment
 ****************************************************
 */

class SearchUserFragment : LazyLoadFragment() {
    private var mAdapter: MyAdapter? = null
    private var loadingLayout: View? = null
    private var recyclerView: RecyclerView? = null
    private var blankView: TextView? = null
    private var dataList = ArrayList<User>()
    private var search : String? = null
    private var isLoadComplete = false
    private var isLoading = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_user, container, false)
        initView(view)
        initRecyclerView()

        return view
    }

    private fun initView(view: View) {
        loadingLayout = view.findViewById(R.id.loading_layout)
        recyclerView = view.findViewById(R.id.recycler_view)
        blankView = view.findViewById(R.id.blank_view)
    }

    private fun initRecyclerView() {
        mAdapter = MyAdapter()
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = mAdapter
    }

    fun setSearch(search: String?){
        this.search = search
        if(userVisibleHint && isLoadComplete){
            isLoadComplete = false
            loadData()
        }
    }

    override fun loadData() {
        super.loadData()
        if(search != null){
            isLoading = true
            onLoading()
            var url = ServerInfo.searchUserbyNickName(search)
            NetworkAccess.buildRequest(url, object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    activity!!.runOnUiThread {
                        Logger.log(e)
                        Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onResponse(call: Call?, response: Response?) {
                    val json = response?.body()?.string()
                    try {
                        dataList.clear()
                        if (json == "[]") {
                        } else {
                            val jsonArray = JSONArray(json)
                            for (k in 0 until jsonArray.length()) {
                                val obj = jsonArray.getJSONObject(k)
                                val item = User(
                                        obj.getString("nickname"),
                                        obj.getString("studentnumber"),
                                        obj.getString("avatar"),
                                        obj.getString("sign"),
                                        obj.getInt("id")
                                )
                                dataList.add(item)
                            }
                        }
                    } catch (e: Exception) {
                        Logger.log(e)
                        activity!!.runOnUiThread {
                            Toast.makeText(context, "网络错误\n服务器无响应", Toast.LENGTH_SHORT).show()
                        }
                    }
                    activity!!.runOnUiThread {
                        isLoadComplete = true
                        publishData()
                    }
                }
            })
        }
    }

    override fun isLoadComplete(): Boolean {
        return isLoadComplete
    }

    override fun publishData() {
        if(dataList.size != 0){
            recyclerView!!.visibility = View.VISIBLE
            loadingLayout!!.visibility = View.GONE
            blankView!!.visibility = View.GONE
        }else{
            recyclerView!!.visibility = View.GONE
            loadingLayout!!.visibility = View.GONE
            blankView!!.visibility = View.VISIBLE
        }
        if(mAdapter != null){
            mAdapter!!.notifyDataSetChanged()
        }
    }

    fun onLoading(){
        recyclerView!!.visibility = View.GONE
        loadingLayout!!.visibility = View.VISIBLE
        blankView!!.visibility = View.GONE
    }

    inner class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.searchuser_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = dataList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            var user : User? = null
//            var i = 0
//            while(dataSet.iterator().hasNext()&& i<=position && dataSet.size>0){
//                user = dataSet.iterator().next()
//                i++
//            }
            val user = dataList[position]
            val bmp = ImageManager.convertStringToBitmap(user.avatarString)
            holder.circleImageView.setImageBitmap(bmp)
            holder.userName.text = user.nickName
            holder.userSign.text = user.selfIntroduce
            holder.btnfollow.setOnClickListener {
                Log.w("click","follow")
            }
            holder.itemLayout.setOnClickListener {
                //(activity as SearchActivity).editSearch!!.setText("")
                //clear()
                startActivity(Intent(context, MyHomePageActivity::class.java).putExtra("id", user.uid))
            }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var circleImageView: CircleImageView = view.findViewById(R.id.circle_image_view)
            var userName: TextView = view.findViewById(R.id.user_name)
            var userSign: TextView = view.findViewById(R.id.user_sign)
            var btnfollow: TextView = view.findViewById(R.id.btn_follow)
            var itemLayout: View = view.findViewById(R.id.item_layout)
        }
    }
}