package cn.edu.sdu.online.isdu.ui.design.viewpager;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ChildViewPager extends ViewPager {

    public ChildViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private float mLastMotionX;
    private boolean flag = false;

    public boolean dispatchTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                flag = true;
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                // 若连续快速滑动，则不触发ACTION_DOWN和ACTION_UP，直接进入ACTION_MOVE处理
                // 即还处于动画中时一定进入的是ACTION_MOVE
                if (flag) {
                    if (x - mLastMotionX > 5 && getCurrentItem() == 0) {
                        flag = false;
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    if (x - mLastMotionX < -5
                            && getCurrentItem() == getAdapter().getCount() - 1) {
                        flag = false;
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

}