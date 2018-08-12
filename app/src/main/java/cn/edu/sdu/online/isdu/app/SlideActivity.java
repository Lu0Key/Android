package cn.edu.sdu.online.isdu.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.edu.sdu.online.isdu.R;
import cn.edu.sdu.online.isdu.ui.design.snake.Snake;
import cn.edu.sdu.online.isdu.ui.design.snake.annotations.EnableDragToClose;

@SuppressLint("Registered")
@EnableDragToClose
public class SlideActivity extends NormActivity {

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

}
