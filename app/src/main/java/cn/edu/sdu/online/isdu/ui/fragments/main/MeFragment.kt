package cn.edu.sdu.online.isdu.ui.fragments.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.edu.sdu.online.isdu.R
import cn.edu.sdu.online.isdu.app.LazyLoadFragment
import cn.edu.sdu.online.isdu.bean.Schedule
import cn.edu.sdu.online.isdu.bean.User
import cn.edu.sdu.online.isdu.net.AccountOp
import cn.edu.sdu.online.isdu.net.ServerInfo
import cn.edu.sdu.online.isdu.net.pack.NetworkAccess
import cn.edu.sdu.online.isdu.ui.activity.*
import cn.edu.sdu.online.isdu.ui.design.button.ImageButton
import cn.edu.sdu.online.isdu.util.*
import cn.edu.sdu.online.isdu.ui.fragments.MyBookFragment
import cn.edu.sdu.online.isdu.util.EnvVariables
import cn.edu.sdu.online.isdu.util.FileUtil
import cn.edu.sdu.online.isdu.util.ImageManager
import cn.edu.sdu.online.isdu.util.Logger
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_me.*
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/6/15
 *
 * 主页个人中心碎片
 ****************************************************
 */

class MeFragment : Fragment(), View.OnClickListener, Serializable {

    /* 八宫格按钮 */
    private var btnSchedule: ImageButton? = null
    private var btnLibrary: ImageButton? = null
    private var btnBus: ImageButton? = null
    private var btnCalender: ImageButton? = null
    private var btnExam: ImageButton? = null
    private var btnStudyroom: ImageButton? = null
    private var btnGrade: ImageButton? = null
    private var btnCloud: ImageButton? = null
    private var btnMsg: TextView? = null
    private var btnFavorite: TextView? = null
    private var btnHistory: TextView? = null
    private var btnFollow: TextView? = null
    private var todayScheduleLayout: View? = null
    private var imgArrowForward: ImageView? = null
    private var btnDownload: View? = null
    private var txtDownload: TextView? = null
    private var btnSettings: View? = null

    private var functionButtonLayout: LinearLayout? = null

    private var circleImageView: CircleImageView? = null
    private var userName: TextView? = null
    private var userId: TextView? = null

    private var emptySymbol: TextView? = null
    /* TodoList */
    private var recyclerView: RecyclerView? = null
    private var adapter: TodoAdapter? = null
    private var todoList: MutableList<Schedule> = ArrayList()

    private var personalInformationLayout: ConstraintLayout? = null // 个人信息入口，进入个人主页

    private var broadcastReceiver: UserSyncBroadcastReceiver? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_me, container, false)

        initView(view)
        initRecyclerView()

        prepareBroadcastReceiver()

        loadUserInformation()
        return view
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            btn_bus.id -> {
                startActivity(Intent(activity, SchoolBusActivity::class.java))
            }
            btn_calender.id -> {
                startActivity(Intent(activity, CalendarActivity::class.java))
            }
            btn_cloud.id -> {
                startActivity(Intent(activity, CloudActivity::class.java))
            }
            btn_exam.id -> {
                startActivity(Intent(activity, ExamActivity::class.java))
            }
            btn_grade.id -> {
                startActivity(Intent(activity, GradeActivity::class.java))
            }
            btn_library.id -> {
                startActivity(Intent(activity, LibraryActivity::class.java))
            }
            btn_schedule.id -> {
                startActivity(Intent(activity, ScheduleActivity::class.java))
            }
            btn_studyroom.id -> {
                startActivity(Intent(activity, StudyRoomActivity::class.java))
            }
            btn_msg.id -> {
                startActivity(Intent(activity, MessageActivity::class.java))
            }
            btn_my_favorite.id -> {
                startActivity(Intent(activity, CollectActivity::class.java))
            }
            btn_history.id -> {
                startActivity(Intent(activity, HistoryActivity::class.java))
            }
            btn_follow.id -> {

            }
            personal_information_layout.id -> {
                if (User.staticUser.studentNumber != null && User.staticUser.studentNumber != "") {
                    startActivity(Intent(activity, MyHomePageActivity::class.java))
                } else {
                    startActivity(Intent(activity, LoginActivity::class.java))
                }
            }
            today_schedule.id -> {
                startActivity(Intent(activity, ScheduleActivity::class.java))
            }
            btn_download.id -> {
                startActivity(Intent(activity, DownloadActivity::class.java))
            }
            btn_settings.id -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserInformation()
        loadSchedule()
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegBroadcastReceiver()
    }

    /**
     * 初始化View
     */
    private fun initView(view: View) {
        circleImageView = view.findViewById(R.id.circle_image_view)
        userName = view.findViewById(R.id.user_name)
        userId = view.findViewById(R.id.user_id)
        recyclerView = view.findViewById(R.id.todo_recycler_view)
        btnBus = view.findViewById(R.id.btn_bus)
        btnCalender = view.findViewById(R.id.btn_calender)
        btnCloud = view.findViewById(R.id.btn_cloud)
        btnExam = view.findViewById(R.id.btn_exam)
        btnGrade = view.findViewById(R.id.btn_grade)
        btnLibrary = view.findViewById(R.id.btn_library)
        btnSchedule = view.findViewById(R.id.btn_schedule)
        btnStudyroom = view.findViewById(R.id.btn_studyroom)
        btnMsg = view.findViewById(R.id.btn_msg)
        btnFavorite = view.findViewById(R.id.btn_my_favorite)
        btnHistory = view.findViewById(R.id.btn_history)
        btnFollow = view.findViewById(R.id.btn_follow)
        personalInformationLayout = view.findViewById(R.id.personal_information_layout)
        todayScheduleLayout = view.findViewById(R.id.today_schedule)
        imgArrowForward = view.findViewById(R.id.img_arrow_forward)
        emptySymbol = view.findViewById(R.id.empty_symbol)
        functionButtonLayout = view.findViewById(R.id.function_button)
        btnDownload = view.findViewById(R.id.btn_download)
        txtDownload = view.findViewById(R.id.txt_download)
        btnSettings = view.findViewById(R.id.btn_settings)

        btnBus!!.setOnClickListener(this)
        btnCalender!!.setOnClickListener(this)
        btnCloud!!.setOnClickListener(this)
        btnExam!!.setOnClickListener(this)
        btnGrade!!.setOnClickListener(this)
        btnLibrary!!.setOnClickListener(this)
        btnStudyroom!!.setOnClickListener(this)
        btnSchedule!!.setOnClickListener(this)
        todayScheduleLayout!!.setOnClickListener(this)
        btnDownload!!.setOnClickListener(this)
        btnSettings!!.setOnClickListener(this)

        btnMsg!!.setOnClickListener(this)
        btnFavorite!!.setOnClickListener(this)
        btnHistory!!.setOnClickListener(this)
        btnFollow!!.setOnClickListener(this)
        personalInformationLayout!!.setOnClickListener(this)
    }

    private fun prepareBroadcastReceiver() {
        if (broadcastReceiver == null) {
            val intentFilter = IntentFilter(AccountOp.ACTION_SYNC_USER_INFO)
            broadcastReceiver = UserSyncBroadcastReceiver(this)
            AccountOp.localBroadcastManager.registerReceiver(broadcastReceiver!!,
                    intentFilter)
        }
    }

    private fun unRegBroadcastReceiver() {
        if (broadcastReceiver != null)
            AccountOp.localBroadcastManager.unregisterReceiver(broadcastReceiver!!)
    }

    /**
     * 加载用户信息
     */
    private fun loadUserInformation() {
        if (User.staticUser == null) User.staticUser = User.load()
        if (User.staticUser.studentNumber != null && User.staticUser.studentNumber != "") {
            // 加载登录后信息
            val user = User.staticUser
            circleImageView?.setImageBitmap(ImageManager.convertStringToBitmap(user.avatarString))
            userName?.text = user.nickName
            userId?.text = "学号:${user.studentNumber}"
            userId?.visibility = View.VISIBLE
            imgArrowForward?.visibility = View.VISIBLE
            functionButtonLayout?.visibility = View.VISIBLE
        } else {
            // 加载未登录信息
            circleImageView?.setImageResource(R.mipmap.ic_launcher)
            userName?.text = "点击以登录"
            userId?.text = ""
            userId?.visibility = View.GONE
            imgArrowForward?.visibility = View.GONE
            functionButtonLayout?.visibility = View.GONE
        }
    }

    private fun initRecyclerView() {
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        adapter = TodoAdapter()
        recyclerView!!.adapter = adapter

        if (adapter!!.itemCount == 0) {
            recyclerView!!.visibility = View.GONE
            emptySymbol!!.visibility = View.VISIBLE
        } else {
            recyclerView!!.visibility = View.VISIBLE
            emptySymbol!!.visibility = View.GONE
        }
    }

    /**
     * 加载今日安排
     */
    private fun loadSchedule() {
        if (User.staticUser == null) User.staticUser = User.load()
        if (User.staticUser.studentNumber != null)
        NetworkAccess.cache(ServerInfo.getScheduleUrl(User.staticUser.uid)) { success, cachePath ->
            if (success) {
                val jsonString = FileUtil.getStringFromFile(cachePath)
                try {
                    Schedule.localScheduleList =
                            Schedule.loadCourse(JSONObject(jsonString).getJSONArray("obj"))

                    if (EnvVariables.currentWeek == -1)
                        EnvVariables.currentWeek = EnvVariables.calculateWeekIndex(System.currentTimeMillis())
                    todoList = Schedule.localScheduleList[EnvVariables.currentWeek - 1][EnvVariables.getCurrentDay() - 1]

                    activity!!.runOnUiThread {
                        if (todoList.isEmpty()) {
                            recyclerView!!.visibility = View.GONE
                            emptySymbol!!.visibility = View.VISIBLE
                        } else {
                            recyclerView!!.visibility = View.VISIBLE
                            emptySymbol!!.visibility = View.GONE
                        }
                        adapter!!.notifyDataSetChanged()
                    }

                } catch (e: JSONException) {
                    Logger.log(e)
                }
            }
        }

    }


    inner class TodoAdapter : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

        override fun getItemCount(): Int = todoList.size


            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val item = todoList[position]
                holder.todoIndex.text = (position + 1).toString()
                holder.todoName.text = item.scheduleName
                holder.todoTime.text = item.startTime.toString()
                holder.todoLocation.text = item.scheduleLocation
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)

                return ViewHolder(view)
            }

            inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                var todoIndex = view.findViewById<TextView>(R.id.todo_index)
                var todoName = view.findViewById<TextView>(R.id.todo_name)
                var todoTime = view.findViewById<TextView>(R.id.todo_time)
                var todoLocation = view.findViewById<TextView>(R.id.todo_location)
            }

    }

    class UserSyncBroadcastReceiver(meFragment: MeFragment) : BroadcastReceiver() {
        private val fragmentMe = meFragment
        override fun onReceive(context: Context?, intent: Intent?) {
            fragmentMe.loadUserInformation()
//            if (intent!!.action == AccountOp.ACTION_SYNC_USER_INFO) {
//                val data = intent.extras.getString("result")
//                Toast.makeText(context, data, Toast.LENGTH_SHORT).show()
//            }
        }
    }
}