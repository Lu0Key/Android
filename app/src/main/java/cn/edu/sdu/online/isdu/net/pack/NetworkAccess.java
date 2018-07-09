package cn.edu.sdu.online.isdu.net.pack;


import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    public static void buildRequest(String url, String str, Callback callback) {
        MediaType mediaType = MediaType.parse("text/html; charset=utf-8");
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, str))
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void buildRequest(String url, List<String> key, List<String> value, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();

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


}
