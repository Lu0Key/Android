package cn.edu.sdu.online.isdu.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.interfaces.DownloadListener
import cn.edu.sdu.online.isdu.ui.activity.DownloadActivity
import cn.edu.sdu.online.isdu.util.download.Download
import cn.edu.sdu.online.isdu.util.download.DownloadItem

class DownloadingFragment : Fragment() {
    private var btnStartAll: View? = null
    private var btnPauseAll: View? = null
    private var recyclerView: RecyclerView? = null

    var adapter = ItemAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_downloading, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)
        btnStartAll = view.findViewById(R.id.btn_start_all)
        btnPauseAll = view.findViewById(R.id.btn_pause_all)

        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = adapter

        btnStartAll!!.setOnClickListener {
            for (item in Download.getDownloadingIdList()) {
                if (Download.get(item).status != DownloadItem.TYPE_DOWNLOADING
                        || Download.get(item).status != DownloadItem.TYPE_NEW_INSTANCE) {
                    Download.get(item).startDownload()
                }
            }
            adapter.notifyDataSetChanged()
        }

        btnPauseAll!!.setOnClickListener {
            for (item in Download.getDownloadingIdList()) {
                if (Download.get(item).status == DownloadItem.TYPE_DOWNLOADING
                        || Download.get(item).status == DownloadItem.TYPE_NEW_INSTANCE)
                    Download.get(item).pauseDownload()
            }
            adapter.notifyDataSetChanged()
        }
    }

    /**
     * 直接从Download获取下载列表
     */
    inner class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_download, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = Download.getDownloadingIdList().size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = Download.get(Download.getDownloadingIdList()[position])

            item.setExternalListener(object : DownloadListener {
                override fun onProgress(progress: Int) {
                    activity?.runOnUiThread {
                        holder.btnStartPause.text = "暂停下载"
                        holder.btnStartPause.setOnClickListener {
                            item.pauseDownload()
                        }
                        holder.txtProgress.visibility = View.VISIBLE
                        holder.progressBar.visibility = View.VISIBLE
                        holder.txtStatus.text = "正在下载 ${item.fileLengthDetector.speed}"
                        holder.progressBar.progress = item.progress
                        holder.txtProgress.text = "${item.progress}%"
                        holder.itemLayout.setOnClickListener {
                            item.pauseDownload()
                        }
                    }
                }

                override fun onSuccess() {
                    activity?.runOnUiThread {
                        holder.txtStatus.text = "下载成功，点击打开"
                        holder.itemLayout.setOnClickListener {

                        }
                        holder.txtProgress.visibility = View.GONE
                        holder.progressBar.visibility = View.GONE

                        adapter.notifyDataSetChanged()
                        ((activity as DownloadActivity).mFragments[1] as DownloadedFragment)
                                .adapter
                                .notifyDataSetChanged()
                    }
                }

                override fun onFailed() {
                    activity?.runOnUiThread {
                        holder.btnStartPause.text = "重试"
                        holder.btnStartPause.setOnClickListener {
                            item.startDownload()
                        }
                        holder.txtProgress.visibility = View.GONE
                        holder.progressBar.visibility = View.GONE
                        holder.txtStatus.text = "下载失败，点击重试"
                        holder.itemLayout.setOnClickListener {
                            item.startDownload()
                        }
                    }
                }

                override fun onPaused() {
                    activity?.runOnUiThread {
                        holder.btnStartPause.text = "开始下载"
                        holder.btnStartPause.setOnClickListener {
                            item.startDownload()
                        }
                        holder.txtProgress.visibility = View.VISIBLE
                        holder.progressBar.visibility = View.VISIBLE
                        holder.txtStatus.text = "下载暂停"
                        holder.itemLayout.setOnClickListener {
                            holder.txtStatus.text = "正在开始"
                            item.startDownload()
                        }
                    }
                }

                override fun onCanceled() {
                    activity?.runOnUiThread {
                        holder.btnStartPause.text = "重新下载"
                        holder.btnStartPause.setOnClickListener {
                            item.startDownload()
                        }
                        holder.txtProgress.visibility = View.GONE
                        holder.progressBar.visibility = View.GONE
                        holder.txtStatus.text = "下载已取消，点击重新下载"
                        holder.itemLayout.setOnClickListener {
                            item.startDownload()
                        }
                    }
                }
            })

            holder.btnClear.visibility = View.GONE

            holder.fileName.text = item.fileName

            holder.progressBar.progress = item.progress
            holder.txtProgress.text = "${item.progress}%"

            when (item.status) {
                DownloadItem.TYPE_PAUSED -> {
                    holder.btnStartPause.text = "开始下载"
                    holder.btnStartPause.setOnClickListener {
                        item.startDownload()
                    }
                    holder.txtProgress.visibility = View.VISIBLE
                    holder.progressBar.visibility = View.VISIBLE
                    holder.txtStatus.text = "下载暂停"
                    holder.itemLayout.setOnClickListener {
                        item.startDownload()
                    }
                }
                DownloadItem.TYPE_DOWNLOADING -> {
                    holder.btnStartPause.text = "暂停下载"
                    holder.btnStartPause.setOnClickListener {
                        item.pauseDownload()
                    }
                    holder.txtStatus.text = "正在下载"
                    holder.itemLayout.setOnClickListener {
                        item.pauseDownload()
                    }
                }
                DownloadItem.TYPE_CANCELED -> {
                    holder.btnStartPause.text = "重新下载"
                    holder.btnStartPause.setOnClickListener {
                        item.startDownload()
                    }
                    holder.txtProgress.visibility = View.GONE
                    holder.progressBar.visibility = View.GONE
                    holder.txtStatus.text = "下载已取消，点击重新下载"
                    holder.itemLayout.setOnClickListener {
                        item.startDownload()
                    }
                }
                DownloadItem.TYPE_FAILED -> {
                    holder.btnStartPause.text = "重试"
                    holder.btnStartPause.setOnClickListener {
                        item.startDownload()
                    }
                    holder.txtProgress.visibility = View.GONE
                    holder.progressBar.visibility = View.GONE
                    holder.txtStatus.text = "下载失败，点击重试"
                    holder.itemLayout.setOnClickListener {
                        item.startDownload()
                    }
                }
                DownloadItem.TYPE_SUCCESS -> {
                    holder.txtStatus.text = "下载成功，点击打开"
                    holder.txtProgress.visibility = View.GONE
                    holder.progressBar.visibility = View.GONE
                    holder.itemLayout.setOnClickListener {
                    }
                }
                DownloadItem.TYPE_NEW_INSTANCE -> {
                    holder.btnStartPause.text = "暂停下载"
                    holder.btnStartPause.setOnClickListener {
                        item.pauseDownload()
                    }
                    holder.txtStatus.text = "准备下载"
                    holder.itemLayout.setOnClickListener {
                        item.pauseDownload()
                    }
                }
            }

            holder.btnCancel.setOnClickListener {
                item.cancelDownload()
            }

        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var fileName: TextView = view.findViewById(R.id.file_name)
            var itemLayout: View = view.findViewById(R.id.item_layout)
            var progressBar: ProgressBar = view.findViewById(R.id.progress_bar)
            var txtProgress: TextView = view.findViewById(R.id.txt_progress)
            var txtStatus: TextView = view.findViewById(R.id.item_status)
            var btnStartPause: TextView = view.findViewById(R.id.btn_start_pause)
            var btnCancel: TextView = view.findViewById(R.id.btn_cancel)
            var btnClear: TextView = view.findViewById(R.id.btn_clear)
        }
    }
}
