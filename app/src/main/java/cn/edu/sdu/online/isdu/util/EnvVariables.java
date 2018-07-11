package cn.edu.sdu.online.isdu.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.edu.sdu.online.isdu.net.ServerInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EnvVariables {

    public static int startWeek = 1;
    public static int endWeek = 20;
    public static int currentWeek = -1;

    public static long firstWeekTimeMillis = 0;

    public static void init(final Context context) {
        SharedPreferences sp = context.getSharedPreferences("env_variables", Context.MODE_PRIVATE);
        if (sp.getInt("start_week", 0) == 0) {
            // 未进行信息同步
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(ServerInfo.url + "envVariables")
                    .get()
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Logger.log(e);
                    startWeek = 1;
                    endWeek = 20;
                    firstWeekTimeMillis = 0;
                    save(context);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        startWeek = jsonObject.getInt("start_week");
                        endWeek = jsonObject.getInt("end_week");
                        firstWeekTimeMillis = jsonObject.getLong("first_week_time_millis");
                        currentWeek = calculateWeekIndex(System.currentTimeMillis());
                        save(context);
                    } catch (Exception e) {
                        Logger.log(e);
                    }
                }
            });
        } else {
            startWeek = sp.getInt("start_week", 1);
            endWeek = sp.getInt("end_week", 20);
            firstWeekTimeMillis = sp.getLong("first_week_time_millis", 0);

            currentWeek = calculateWeekIndex(System.currentTimeMillis());
        }
    }

    private static void save(Context context) {
        SharedPreferences.Editor editor =
                context.getSharedPreferences("env_variables", Context.MODE_PRIVATE).edit();
        editor.putInt("start_week", startWeek);
        editor.putInt("end_week", endWeek);
        editor.putLong("first_week_time_millis", firstWeekTimeMillis);
        editor.apply();
    }

    public static int calculateWeekIndex(long timeMillis) {
        long delta = (timeMillis - firstWeekTimeMillis) / 1000L;
        int daily = 24 * 60 * 60;
        int days = (int) (delta / daily);
        return (days / 7) + 1;
    }

}
