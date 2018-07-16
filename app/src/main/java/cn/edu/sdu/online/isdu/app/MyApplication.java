package cn.edu.sdu.online.isdu.app;

import android.app.Application;
import android.content.Context;

import com.youngfeng.snake.Snake;

import org.litepal.LitePal;

import cn.edu.sdu.online.isdu.util.Settings;
import cn.edu.sdu.online.isdu.util.download.Download;

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);
        Snake.init(this);

        Settings.load(context);
    }

    public static Context getContext() {
        return context;
    }
}
