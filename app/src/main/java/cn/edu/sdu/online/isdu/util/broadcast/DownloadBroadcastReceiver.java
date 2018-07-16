package cn.edu.sdu.online.isdu.util.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import cn.edu.sdu.online.isdu.util.NotificationUtil;
import cn.edu.sdu.online.isdu.util.download.Download;
import cn.edu.sdu.online.isdu.util.download.DownloadItem;

public class DownloadBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int notifyId = intent.getIntExtra("notify_id", 0);
        Log.d("AAA", "onReceive " + notifyId);
        if (intent.getAction() != null && notifyId != 0) {
            switch (intent.getAction()) {
                case "download_start":
                    Download.get(notifyId).startDownload();
                    break;
                case "download_pause":
                    Download.get(notifyId).pauseDownload();
                    break;
                case "download_cancel":
                    Download.get(notifyId).cancelDownload();
                    break;
                case "download_open":

                    break;
            }
        }

    }
}
