package cn.edu.sdu.online.isdu.util.download;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.List;

import cn.edu.sdu.online.isdu.R;
import cn.edu.sdu.online.isdu.app.MyApplication;
import cn.edu.sdu.online.isdu.ui.activity.DownloadActivity;
import cn.edu.sdu.online.isdu.util.NotificationUtil;

public class Download {

    public static int totalDownloadingCounts = 0;
    private static Activity activity;

    public static void init(Activity a) {
        activity = a;
    }

    public static void updateNotification(DownloadItem downloadItem) {
        Intent intentActivity = new Intent(activity, DownloadActivity.class);
        switch (downloadItem.getStatus()) {
            case DownloadItem.TYPE_SUCCESS:
                downloadItem.notification = new NotificationUtil.Builder(activity)
                        .setNotifyId(downloadItem.getNotifyId())
                        .setPriority(NotificationUtil.PRIORITY_MAX)
                        .setVibrate(false)
                        .setTitle("下载完成")
                        .setMessage("点击查看 " + downloadItem.getFileName())
                        .setSmallIcon(R.mipmap.ic_alpha_not)
                        .build();
                NotificationUtil.send(downloadItem.getNotifyId(), downloadItem.notification);
                break;
            case DownloadItem.TYPE_CANCELED:
                downloadItem.notification = new NotificationUtil.Builder(activity)
                        .setNotifyId(downloadItem.getNotifyId())
                        .setPriority(NotificationUtil.PRIORITY_MAX)
                        .setTitle("下载被取消")
                        .setVibrate(false)
                        .setMessage("取消下载 " + downloadItem.getFileName())
                        .setSmallIcon(R.mipmap.ic_alpha_not)
                        .build();
                NotificationUtil.send(downloadItem.getNotifyId(), downloadItem.notification);
                break;
            case DownloadItem.TYPE_DOWNLOADING:
                downloadItem.notification = new NotificationUtil.Builder(activity)
                        .setNotifyId(downloadItem.getNotifyId())
                        .setPriority(NotificationUtil.PRIORITY_LOW)
                        .setTitle("下载中 " + downloadItem.getProgress() + "%")
                        .setMessage("正在下载 " + downloadItem.getFileName())
                        .setProgress(100, downloadItem.getProgress(), false)
                        .setSmallIcon(R.mipmap.ic_alpha_not)
                        .setIntent(PendingIntent.getActivity(activity, 0, intentActivity, 0))
                        .setOnGoing(true)
                        .build();
                NotificationUtil.send(downloadItem.getNotifyId(), downloadItem.notification);
                break;
            case DownloadItem.TYPE_FAILED:
                downloadItem.notification = new NotificationUtil.Builder(activity)
                        .setNotifyId(downloadItem.getNotifyId())
                        .setPriority(NotificationUtil.PRIORITY_MAX)
                        .setTitle("下载失败")
                        .setVibrate(false)
                        .setMessage("下载失败 " + downloadItem.getFileName())
                        .setSmallIcon(R.mipmap.ic_alpha_not)
                        .build();
                NotificationUtil.send(downloadItem.getNotifyId(), downloadItem.notification);
                break;
            case DownloadItem.TYPE_PAUSED:
                downloadItem.notification = new NotificationUtil.Builder(activity)
                        .setNotifyId(downloadItem.getNotifyId())
                        .setPriority(NotificationUtil.PRIORITY_MAX)
                        .setTitle("下载暂停")
                        .setVibrate(false)
                        .setMessage("暂停下载 " + downloadItem.getFileName())
                        .setSmallIcon(R.mipmap.ic_alpha_not)
                        .build();
                NotificationUtil.send(downloadItem.getNotifyId(), downloadItem.notification);
                break;
        }
    }

}
