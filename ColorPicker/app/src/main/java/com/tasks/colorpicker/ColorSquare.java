package com.tasks.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageButton;

public class ColorSquare extends ImageButton {
    private int defaultColor;
    private GestureDetector gestureDetector;
    private boolean isInEditMode = false;
    private float prevX;
    private float prevY;

    public ColorSquare(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public ColorSquare(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void setDefaultColor(int color) {
        defaultColor = color;
    }

    public int getBackgroundColor() {
        return ((ColorDrawable) getBackground()).getColor();
    }

    private MainActivity getMainActivity() {
        return (MainActivity) getContext();
    }

    private void changeColor(float dX, float dY) {
        if (Math.abs(dX) < 1e-5 && Math.abs(dY) < 1e-5)
            return;

        float[] def = new float[3];
        Color.colorToHSV(defaultColor, def);

        int oldColor = getBackgroundColor();
        float[] hsv = new float[3];
        Color.colorToHSV(oldColor, hsv);

        if (Math.abs(dX) >= Math.abs(dY))
            hsv[0] += dX / 10;
        else
            hsv[2] += dY / 100;

        int border = MainActivity.COLOR_STEP / 2;
        if (hsv[0] <= def[0] - border || hsv[0] >= def[0] + border
                || hsv[2] > 1 || hsv[2] < 1E-2) {
            getMainActivity().setBorderIndicatorColor(ContextCompat.getColor(getContext(), R.color.red));
            return;
        }

        getMainActivity().setBorderIndicatorColor(ContextCompat.getColor(getContext(), R.color.white));

        int newColor = Color.HSVToColor(hsv);
        setBackgroundColor(newColor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (isInEditMode && e.getAction() == MotionEvent.ACTION_MOVE) {
            changeColor(e.getX() - prevX, prevY - e.getY());
            prevX = e.getX();
            prevY = e.getY();
        } else
            gestureDetector.onTouchEvent(e);

        return true;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            prevX = e.getX();
            prevY = e.getY();
            getMainActivity().setSquareScrollViewEnabled(true);
            isInEditMode = false;
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            int color = getBackgroundColor();
            getMainActivity().refresh(color);
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            setBackgroundColor(defaultColor);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            getMainActivity().setSquareScrollViewEnabled(false);
            isInEditMode = true;
            Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(100);
        }
    }
}
