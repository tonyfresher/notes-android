package com.tasks.colorpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class LockableScrollView extends HorizontalScrollView {
    private boolean scrollEnabled = true;

    public LockableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollEnabled(boolean enabled) {
        scrollEnabled = enabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (scrollEnabled) return super.onTouchEvent(e);
                return scrollEnabled;
            default:
                return super.onTouchEvent(e);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!scrollEnabled) return false;
        else return super.onInterceptTouchEvent(e);
    }
}
