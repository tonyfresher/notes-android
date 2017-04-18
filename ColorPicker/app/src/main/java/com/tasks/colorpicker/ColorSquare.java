package com.tasks.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.Toast;

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
        if (Math.abs(dX) < 1e-5 || Math.abs(dY) < 1e-5)
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

        if (hsv[0] <= def[0] - 12 || hsv[0] >= def[0] + 12 || hsv[2] > 1 || hsv[2] < 0.01) {
            getMainActivity().borderIndicator
                    .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
            return;
        }

        getMainActivity().borderIndicator
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));

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
            prevX = 50;
            prevY = 50;
            getMainActivity().squareScrollView.setScrollEnabled(true);
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
            getMainActivity().squareScrollView.setScrollEnabled(false);
            isInEditMode = true;
            Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(100);
        }
    }
}
