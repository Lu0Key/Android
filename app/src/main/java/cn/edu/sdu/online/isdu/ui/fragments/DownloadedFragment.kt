package cn.edu.sdu.online.isdu.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.interfaces.DownloadListener
import cn.edu.sdu.online.isdu.util.download.Download
import cn.edu.sdu.online.isdu.util.download.DownloadItem

class DownloadedFragment : Fragment() {
    private var btnStartAll: LinearLayout? = null
    private var btnPauseAll: LinearLayout? = null
    private var recyclerView: RecyclerView? = null

    var adapter = ItemAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_downloaded, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)

        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = adapter
    }

    /**
     * 直接从Download获取下载列表
     */
    inner class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_download, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = Download.getDownloadedIdList().size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = Download.get(Download.getDownloadedIdList()[position])
            holder.fileName.text = item.fileName

            holder.progressBar.progress = item.progress
            holder.txtProgress.text = "${item.progress}%"


            holder.txtStatus.text = "下载成功，点击打开"
            holder.txtProgress.visibility = View.GONE
            holder.progressBar.visibility = View.GONE
            holder.itemLayout.setOnClickListener {
            }


        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var fileName: TextView = view.findViewById(R.id.file_name)
            var itemLayout: View = view.findViewById(R.id.item_layout)
            var progressBar: ProgressBar = view.findViewById(R.id.progress_bar)
            var txtProgress: TextView = view.findViewById(R.id.txt_progress)
            var txtStatus: TextView = view.findViewById(R.id.item_status)
        }
    }
}
