package com.tasks.colorpicker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.BindView;


public class MainActivity extends Activity {
    @BindView(R.id.colorImage)
    ImageView colorImage;
    @BindView(R.id.colorTextHSV)
    TextView hsvText;
    @BindView(R.id.colorTextRGB)
    TextView rgbText;
    @BindView(R.id.border_indicator)
    ImageView borderIndicator;
    @BindView(R.id.square_scroll_view)
    LockableScrollView squareScrollView;
    @BindView(R.id.square_layout)
    LinearLayout squareLayout;

    ColorSquare[] squares = new ColorSquare[COLORS_COUNT];
    public static int COLORS_COUNT = 16;
    public static int COLOR_STEP = 360 / (COLORS_COUNT - 1);
    private static String MAIN_COLOR_TAG = "main_color";
    private static String COLORS_TAG = "squares_colors";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final int[] colors = new int[COLORS_COUNT];
        for (int i = 0; i < COLORS_COUNT; i++) {
            ColorSquare square = new ColorSquare(this, null, 0, R.style.ColorSquare);

            int color = Color.HSVToColor(new float[]{COLOR_STEP * i, 1, 1});
            square.setDefaultColor(color);
            square.setBackgroundColor(color);

            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(convertDpToPx(this, 72), convertDpToPx(this, 72));
            int margin = convertDpToPx(this, 18);
            lp.setMargins(margin, margin, margin, margin);
            squareLayout.addView(square, lp);

            squares[i] = square;
            colors[i] = color;
        }

        PaintDrawable pd = new PaintDrawable();
        pd.setShape(new RectShape());
        pd.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(0, 0, width, 0, colors, null, Shader.TileMode.CLAMP);
            }
        });
        squareLayout.setBackground(pd);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int color = ((ColorDrawable) colorImage.getDrawable()).getColor();
        outState.putInt(MAIN_COLOR_TAG, color);

        int[] colors = new int[COLORS_COUNT];
        for (int i = 0; i < COLORS_COUNT; i++)
            colors[i] = squares[i].getBackgroundColor();

        outState.putIntArray(COLORS_TAG, colors);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        refresh(savedInstanceState.getInt(MAIN_COLOR_TAG, 0));

        int[] colors = savedInstanceState.getIntArray(COLORS_TAG);
        for (int i = 0; i < COLORS_COUNT; i++)
            squares[i].setBackgroundColor(colors[i]);
    }

    public void refresh(int color) {
        colorImage.setImageDrawable(new ColorDrawable(color));
        rgbText.setText(colorToRgbString(color));
        hsvText.setText(colorToHsvString(color));
    }

    public void setBorderIndicatorColor(int color) {
        borderIndicator.setBackgroundColor(color);
    }

    public void setSquareScrollViewEnabled(boolean scrollable) {
        squareScrollView.setScrollEnabled(scrollable);
    }

    private static String colorToRgbString(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return String.format("RGB(%s, %s, %s)", r, g, b);
    }

    private static String colorToHsvString(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return String.format("HSV(%s, %s%%, %s%%)",
                (int) hsv[0], (int) (hsv[1] * 100), (int) (hsv[2] * 100));
    }

    private static int convertDpToPx(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

}
