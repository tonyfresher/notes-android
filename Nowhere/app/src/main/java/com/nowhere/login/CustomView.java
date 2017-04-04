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

public class CustomView extends View {
    private String text;
    private int color;
    private GestureDetector detector;

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.CustomView, 0, 0);

        try {
            text = a.getString(R.styleable.CustomView_text);
            color = a.getInt(R.styleable.CustomView_color, 0);
        } finally {
            a.recycle();
        }

        detector = new GestureDetector(CustomView.this.getContext(), new Listener());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setColor(color);
        p.setTextSize(15);
        canvas.drawText(text, 0, 0, p);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = detector.onTouchEvent(event);
        if (result && event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
            color = Color.parseColor("#656565");
            invalidate();
            requestLayout();
        }
        return result;
    }

    class Listener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}
