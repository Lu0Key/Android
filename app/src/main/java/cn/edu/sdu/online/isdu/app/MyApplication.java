package cn.edu.sdu.online.isdu.app;

import android.app.Application;
import android.content.Context;

import com.youngfeng.snake.Snake;

import org.litepal.LitePal;

import cn.edu.sdu.online.isdu.util.EnvVariables;

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);
        Snake.init(this);
        EnvVariables.init(context);
    }

    public static Context getContext() {
        return context;
    }
}
