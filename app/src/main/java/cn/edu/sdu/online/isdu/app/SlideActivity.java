package cn.edu.sdu.online.isdu.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.youngfeng.snake.Snake;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.lang.reflect.Field;

import cn.edu.sdu.online.isdu.R;

@SuppressLint("Registered")
@EnableDragToClose
public class SlideActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Snake.host(this);
        overridePendingTransition(R.anim.snake_slide_in_right, R.anim.snake_slide_out_left);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.snake_slide_in_left, R.anim.snake_slide_out_right);
    }

    @Override
    protected void prepareBroadcastReceiver() {

    }

    @Override
    protected void unRegBroadcastReceiver() {

    }
}
