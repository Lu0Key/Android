package cn.edu.sdu.online.isdu.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews

import cn.edu.sdu.online.isdu.R
import com.alibaba.fastjson.JSON

/**
 * Implementation of App Widget functionality.
 */
class DailyArrangementWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
//        for (appWidgetId in appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId)
//        }

        val list = JSON.parseArray(context.getSharedPreferences("schedule", Context.MODE_PRIVATE)
                                        .getString("schedule", "[]"))

        for (id in appWidgetIds) {
            val theWidget = ComponentName(context, DailyArrangementWidget::class.java)
            val remoteViews = RemoteViews(context.packageName, R.layout.daily_arrangement_widget)

            val intent = Intent(context, ScheduleListService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)

            remoteViews.setViewVisibility(R.id.text_view, if (list.isEmpty()) View.VISIBLE else View.GONE)
            remoteViews.setViewVisibility(R.id.list_view, if (list.isNotEmpty()) View.VISIBLE else View.GONE)

            remoteViews.setRemoteAdapter(R.id.list_view, intent)
            appWidgetManager.updateAppWidget(theWidget, remoteViews)
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        val mgr = AppWidgetManager.getInstance(context)
        val cn = ComponentName(context!!, DailyArrangementWidget::class.java)
        mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),
                R.id.list_view)
        onUpdate(context, mgr, mgr.getAppWidgetIds(cn))
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        context.startService(Intent(context, ScheduleListService::class.java))
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        context.stopService(Intent(context, ScheduleListService::class.java))
    }

}

