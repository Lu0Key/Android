package cn.edu.sdu.online.isdu.app;

import android.annotation.SuppressLint;
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
public class SlideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(com.youngfeng.snake.R.anim.snake_slide_in_right, com.youngfeng.snake.R.anim.snake_slide_out_left);
        Snake.host(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(com.youngfeng.snake.R.anim.snake_slide_in_left, com.youngfeng.snake.R.anim.snake_slide_out_right);
    }
}
