package cn.edu.sdu.online.isdu.util.download;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.LitePalSupport;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.edu.sdu.online.isdu.app.MyApplication;
import cn.edu.sdu.online.isdu.interfaces.DownloadListener;
import cn.edu.sdu.online.isdu.interfaces.DownloadOperation;
import cn.edu.sdu.online.isdu.ui.design.dialog.OptionDialog;
import cn.edu.sdu.online.isdu.util.FileUtil;
import cn.edu.sdu.online.isdu.util.Logger;
import cn.edu.sdu.online.isdu.util.NotificationUtil;
import cn.edu.sdu.online.isdu.util.Settings;

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/15
 *
 * 下载内容Bean
 *
 *
 ****************************************************
 */

public class DownloadItem extends LitePalSupport implements DownloadOperation {
    /* 下载任务状态 */
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;
    public static final int TYPE_NEW_INSTANCE = 4;
    public static final int TYPE_DOWNLOADING = 5;

    /* 下载URL */
    private String downloadUrl;
    /* 文件名，不予修改 */
    private String fileName;
    /* 任务状态 */
    private int status;
    private int progress;
    private int notifyId;
    private long size;

    private DownloadAsyncTask downloadAsyncTask;
    public FileLengthDetector fileLengthDetector;

    public DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            setStatus(TYPE_DOWNLOADING);
            setProgress(progress);
            Download.save();
            if (externalListener != null)
                externalListener.onProgress(progress);
            Download.buildNotification(DownloadItem.this);
        }

        @Override
        public void onSuccess() {
            setStatus(TYPE_SUCCESS);
            Download.save();
            if (externalListener != null)
                externalListener.onSuccess();
            Download.buildNotification(DownloadItem.this);

            if (fileLengthDetector != null)
                fileLengthDetector.stop();
            fileLengthDetector = null;
        }

        @Override
        public void onFailed() {
            setStatus(TYPE_FAILED);
            Download.save();
            if (externalListener != null)
                externalListener.onFailed();
            Download.buildNotification(DownloadItem.this);

            if (fileLengthDetector != null)
                fileLengthDetector.stop();
            fileLengthDetector = null;
        }

        @Override
        public void onPaused() {
            setStatus(TYPE_PAUSED);
            Download.save();
            if (externalListener != null)
                externalListener.onPaused();
            Download.buildNotification(DownloadItem.this);

            if (fileLengthDetector != null)
                fileLengthDetector.stop();
            fileLengthDetector = null;
        }

        @Override
        public void onCanceled() {
            setStatus(TYPE_CANCELED);
            Download.save();
            if (externalListener != null)
                externalListener.onCanceled();
            Download.buildNotification(DownloadItem.this);

            if (fileLengthDetector != null)
                fileLengthDetector.stop();
            fileLengthDetector = null;
        }
    };

    private DownloadListener externalListener;

    public DownloadItem(String downloadUrl) {
        setDownloadUrl(downloadUrl);
    }

    public DownloadItem() {
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

    public void setExternalListener(DownloadListener externalListener) {
        this.externalListener = externalListener;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFormattedSize() {
        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            double spd = ((double) size) / (1024.0);
            return new DecimalFormat(".00").format(spd) + "KB";
        } else {
            double spd = ((double) size) / (1024.0 * 1024.0);
            return new DecimalFormat(".00").format(spd) + "MB";
        }

    }

    @Override
    public void startDownload() {
        // 在Download中注册
        if (!Download.downloadList.contains(this)) {
            Download.add(this);
        } else {
            notifyId = Download.downloadList.get(Download.downloadList.indexOf(this)).notifyId;
        }

        if (downloadAsyncTask != null) {
            downloadAsyncTask.pauseDownload();
            downloadAsyncTask = null;
        }
        setStatus(TYPE_NEW_INSTANCE);
        downloadAsyncTask = new DownloadAsyncTask(downloadListener);
        downloadAsyncTask.execute(notifyId);

        fileLengthDetector = new FileLengthDetector(Settings.DEFAULT_DOWNLOAD_LOCATION + getFileName());
        new Thread(fileLengthDetector).start();
    }

    @Override
    public void pauseDownload() {
        if (externalListener != null)
            externalListener.onPaused();
        if (downloadAsyncTask != null) {
            downloadAsyncTask.pauseDownload();
            downloadAsyncTask = null;
        }
    }

    @Override
    public void cancelDownload() {
        if (externalListener != null)
            externalListener.onCanceled();
        if (downloadAsyncTask != null) {
            downloadAsyncTask.cancelDownload();
            downloadAsyncTask = null;
        }
        // 删除文件
        String fileName = Settings.DEFAULT_DOWNLOAD_LOCATION + getFileName();
        File file = new File(fileName);
        if (file.exists()) file.delete();

        NotificationUtil.cancel(notifyId);
    }

    public void open() {
        FileUtil.openFiles(Settings.DEFAULT_DOWNLOAD_LOCATION + getFileName());
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && (obj instanceof DownloadItem) &&
                (((DownloadItem) obj).downloadUrl.equals(this.downloadUrl)) &&
                (((DownloadItem) obj).fileName.equals(this.fileName));
    }

}
