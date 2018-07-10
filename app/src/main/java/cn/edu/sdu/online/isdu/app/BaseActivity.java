package cn.edu.sdu.online.isdu.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cn.edu.sdu.online.isdu.util.Logger;

public abstract class BaseActivity extends AppCompatActivity
        implements Thread.UncaughtExceptionHandler {

    protected MyBroadcastReceiver myBroadcastReceiver;

    protected abstract void prepareBroadcastReceiver();

    protected abstract void unRegBroadcastReceiver();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(this);
        prepareBroadcastReceiver();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Logger.log(e);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegBroadcastReceiver();
    }

    protected static class MyBroadcastReceiver extends BroadcastReceiver {

        private Activity activity;

        protected MyBroadcastReceiver(Activity activity) {this.activity = activity;}

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
