package cn.edu.sdu.online.isdu.util.download;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sdu.online.isdu.app.MyApplication;
import cn.edu.sdu.online.isdu.interfaces.DownloadListener;
import cn.edu.sdu.online.isdu.interfaces.DownloadOperation;
import cn.edu.sdu.online.isdu.util.Logger;
import cn.edu.sdu.online.isdu.util.NotificationUtil;

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/15
 *
 * 下载内容Bean
 ****************************************************
 */

public class DownloadItem implements DownloadOperation {
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;
    public static final int TYPE_NEW_INSTANCE = 4;
    public static final int TYPE_DOWNLOADING = 5;

    private String downloadUrl;
    private String fileName;
    private int status;
    private int progress;
    private int notifyId;

    Notification notification;

    DownloadListener downloadListener;

    DownloadTask downloadTask;

    public DownloadItem(String downloadUrl) {
        setDownloadUrl(downloadUrl);
        status = TYPE_NEW_INSTANCE;
        notifyId = NotificationUtil.getNextId();
        Log.d("AAA", notifyId + "");
    }

    public int getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(int notifyId) {
        this.notifyId = notifyId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
        fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public static List<DownloadItem> getDownloadItemList() {
        SharedPreferences sp =
                MyApplication.getContext().getSharedPreferences("download_item", Context.MODE_PRIVATE);
        String jsonString = sp.getString("json", "");
        List<DownloadItem> downloadItems = new ArrayList<>();

        if (!"".equals(jsonString)) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    DownloadItem downloadItem = new DownloadItem(jsonObject.getString("downloadUrl"));
                    downloadItem.fileName = jsonObject.getString("fileName");
                    downloadItem.status = jsonObject.getInt("status");
                }
            } catch (Exception e) {
                Logger.log(e);
                return downloadItems;
            }

        }

        return downloadItems;
    }

    public static void saveDownloadItemList(List<DownloadItem> items) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(
                "download_item", Context.MODE_PRIVATE
        ).edit();

        JSONArray jsonArray = new JSONArray();
        for (DownloadItem item : items) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("downloadUrl", item.downloadUrl);
                jsonObject.put("fileName", item.fileName);
                jsonObject.put("status", item.status);
            } catch (JSONException e) {
                Logger.log(e);
            }
            jsonArray.put(jsonObject);
        }

        editor.putString("json", jsonArray.toString());
        editor.apply();
    }

    @Override
    public void startDownload() {
        if (downloadTask == null) {
            downloadTask = new DownloadTask(this);
        }
        downloadTask.startDownload();
    }

    @Override
    public void pauseDownload() {
        if (downloadTask != null) {
            downloadTask.pauseDownload();
            downloadTask = null;
        }
    }

    @Override
    public void cancelDownload() {
        if (downloadTask != null) {
            downloadTask.cancelDownload();
            downloadTask = null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof DownloadItem) &&
                (((DownloadItem) obj).downloadUrl.equals(this.downloadUrl)) &&
                (((DownloadItem) obj).fileName.equals(this.fileName));
    }
}
