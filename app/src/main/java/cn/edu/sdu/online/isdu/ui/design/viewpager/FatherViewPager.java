package cn.edu.sdu.online.isdu.ui.design.viewpager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class FatherViewPager extends ViewPager {
    public FatherViewPager(@NonNull Context context) {
        super(context);
    }

    public FatherViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v != this && v instanceof ViewPager) {
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("Jzz", "FatherViewPager->dispatchTouchEvent ev=" + ev.getAction());
        if (ev.getAction() == 3) return false;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("Jzz", "FatherViewPager->onInterceptTouchEvent ev=" + ev.getAction());
        if (ev.getAction() == 3) return false;
        return super.onInterceptTouchEvent(ev);
    }
}
