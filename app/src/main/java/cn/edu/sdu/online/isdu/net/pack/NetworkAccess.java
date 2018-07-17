package cn.edu.sdu.online.isdu.net.pack;


import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.edu.sdu.online.isdu.net.ServerInfo;
import cn.edu.sdu.online.isdu.util.Logger;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkAccess {

    public static void buildRequest(String url, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    @Deprecated
    public static void buildRequest(String url, String str, Callback callback) {
        MediaType mediaType = MediaType.parse("text/html; charset=utf-8");
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, str))
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void buildRequest(String url, List<String> key, List<String> value, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        FormBody.Builder formBody = new FormBody.Builder();
        for (int i = 0; i < key.size(); i++) {
            formBody.add(key.get(i), value.get(i));
        }
        RequestBody requestBody = formBody.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void buildRequest(String url, String key, String value, Callback callback) {
        List<String> keys = new ArrayList<>(); keys.add(key);
        List<String> values = new ArrayList<>(); values.add(value);
        buildRequest(url, keys, values, callback);
    }

    public static void cache(String url, @Nullable final OnCacheFinishListener listener) {
        File cacheDir = new File(Environment.getExternalStorageDirectory() + "/iSDU/cache");
        if (!cacheDir.exists()) {
            if (!cacheDir.getParentFile().exists()) cacheDir.getParentFile().mkdirs();
            cacheDir.mkdir();
        }

        String s = (url.substring((ServerInfo.url).length(), url.length()));
        char[] chars = s.toLowerCase().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '/')
                chars[i] = '_';
            if (chars[i] == '?')
                chars[i] = '.';
            if (chars[i] == '&')
                chars[i] = '_';
        }


        final File cacheFile = new File(cacheDir.getAbsolutePath() + "/" + new String(chars));

        if (cacheFile.exists()) {
            if (listener != null)
                listener.onFinish(true, cacheFile.getAbsolutePath());
        } else try {
            cacheFile.createNewFile();
        } catch (IOException e) {
            Logger.log(e);
        }

        buildRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.log(e);
                if (listener != null)
                    listener.onFinish(false, null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    FileWriter fw = new FileWriter(cacheFile);
                    fw.write(response.body().string());
                    fw.close();
                    if (listener != null)
                        listener.onFinish(true, cacheFile.getAbsolutePath());
                } catch (Exception e) {
                    Logger.log(e);
                }
            }
        });

    }

    public interface OnCacheFinishListener {
        void onFinish(boolean success, String cachePath);
    }

}
