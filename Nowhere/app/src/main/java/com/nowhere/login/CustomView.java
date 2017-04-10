package com.nowhere.login;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Toast.makeText(getContext(), "CustomView.onMeasure() called", Toast.LENGTH_SHORT).show();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Toast.makeText(getContext(), "CustomView.onLayout() called", Toast.LENGTH_SHORT).show();
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        Toast.makeText(getContext(), "CustomView.onFocusChanged() called", Toast.LENGTH_SHORT).show();
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }
}
