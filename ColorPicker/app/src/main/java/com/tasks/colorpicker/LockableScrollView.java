package com.tasks.colorpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class LockableScrollView extends HorizontalScrollView {
    private boolean touchable = true;

    public LockableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollEnabled(boolean enabled) {
        touchable = enabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (touchable) return super.onTouchEvent(e);
                return touchable;
            default:
                return super.onTouchEvent(e);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!touchable) return false;
        else return super.onInterceptTouchEvent(e);
    }
}
