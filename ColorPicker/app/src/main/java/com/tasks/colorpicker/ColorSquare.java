package com.tasks.colorpicker;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageButton;

public class ColorSquare extends ImageButton {
    private int defaultColor;
    private GestureDetector gestureDetector;

    public ColorSquare(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void setDefaultColor(int color) {
        defaultColor = color;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return super.onTouchEvent(e);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean isLongPressed;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int color = ((ColorDrawable) getBackground()).getColor();
            ((MainActivity) getContext()).refresh(color);
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            setBackgroundColor(defaultColor);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            isLongPressed = true;

            Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(100);
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (isLongPressed)
                setBackgroundColor(0);
            return true;
        }
    }
}
