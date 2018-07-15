package cn.edu.sdu.online.isdu.util.download;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.ThreadPoolExecutor;

import cn.edu.sdu.online.isdu.interfaces.DownloadOperation;
import cn.edu.sdu.online.isdu.util.Logger;
import cn.edu.sdu.online.isdu.util.NotificationUtil;
import cn.edu.sdu.online.isdu.util.Settings;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask implements DownloadOperation {

    private DownloadItem downloadItem;
    private String url;
    private String fileName;
    private int notifyId;

    private boolean isPaused = false;
    private boolean isCanceled = false;

    public DownloadTask(DownloadItem downloadItem) {
        this.downloadItem = downloadItem;
        this.url = downloadItem.getDownloadUrl();
        this.fileName = downloadItem.getFileName();
    }

    @Override
    public void startDownload() {
        if (downloadItem == null) return;


            // 在新线程中执行
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 构建下载文件
                    File file = null;
                    RandomAccessFile savedFile = null;
                    InputStream is = null;

                    try {
                        file = new File(Settings.DEFAULT_DOWNLOAD_LOCATION + fileName);

                        if (!file.exists()) {
                            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                            file.createNewFile();
                        }
                        // 检查本地文件
                        long downloadedLength = file.length();
                        long totalLength = getContentLength(url);

                        if (totalLength == 0) {
                            downloadItem.setStatus(DownloadItem.TYPE_FAILED);
                            if (downloadItem.downloadListener != null) {
                                downloadItem.downloadListener.onFailed();
                                Logger.log("URL:" + url + "\nInvalid Url.");
                            }
                            Download.updateNotification(downloadItem);
                            return;
                        }

                        if (downloadedLength == totalLength) {
                            downloadItem.setStatus(DownloadItem.TYPE_SUCCESS);
                            if (downloadItem.downloadListener != null) {
                                downloadItem.downloadListener.onSuccess();
                            }
                            Download.updateNotification(downloadItem);
                            return;
                        }

                        // 构建网络访问器
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .addHeader("RANGER", "bytes=" + downloadedLength + "-")
                                .url(url)
                                .build();

                        Response response = client.newCall(request).execute();
                        if (response != null) {
                            is = response.body().byteStream();
                            savedFile = new RandomAccessFile(file, "rw");
                            savedFile.seek(downloadedLength);

                            // 还原变量
                            isPaused = false;
                            isCanceled = false;
                            downloadItem.setStatus(DownloadItem.TYPE_DOWNLOADING);

                            Download.totalDownloadingCounts++;

                            int len = 0;
                            byte[] b = new byte[10240];
                            while ((len = is.read(b)) != -1) {
                                savedFile.write(b, 0, b.length);
                                downloadedLength += len;

                                int progress = (int) (downloadedLength * 100 / totalLength);
                                downloadItem.setProgress(progress);
                                if (downloadItem.downloadListener != null)
                                    downloadItem.downloadListener.onProgress(progress);

                                Download.updateNotification(downloadItem);

                                if (isPaused || isCanceled) break;
                            }

                            if (!isPaused && !isCanceled) {
                                Download.totalDownloadingCounts--;
                                downloadItem.setStatus(DownloadItem.TYPE_SUCCESS);
                                if (downloadItem.downloadListener != null)
                                    downloadItem.downloadListener.onSuccess();
                                Download.updateNotification(downloadItem);
                            }

                            response.body().close();
                        }


                    } catch (Exception e) {
                        Logger.log(e);
                    } finally {
                        try {
                            if (is != null) is.close();
                            if (savedFile != null) savedFile.close();
                        } catch (Exception e) {
                            Logger.log(e);
                        }
                    }
                }
            }).start();

    }

    @Override
    public void pauseDownload() {
        if (downloadItem == null) return;
        Download.totalDownloadingCounts--;
        isPaused = true;
        downloadItem.setStatus(DownloadItem.TYPE_PAUSED);
        Download.updateNotification(downloadItem);
    }

    @Override
    public void cancelDownload() {
        if (downloadItem == null) return;
        Download.totalDownloadingCounts--;
        isCanceled = true;
        downloadItem.setStatus(DownloadItem.TYPE_CANCELED);
        Download.updateNotification(downloadItem);
    }

    private long getContentLength(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }
}
