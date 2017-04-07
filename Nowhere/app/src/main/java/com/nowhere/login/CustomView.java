package com.nowhere.login;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class CustomView extends View {
    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Toast.makeText(getContext(), "CustomView constructor called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Toast.makeText(getContext(), "CustomView.onDraw() called", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Toast.makeText(getContext(), "CustomView.onTouchEvent() called", Toast.LENGTH_SHORT).show();
        return false;
    }
}
