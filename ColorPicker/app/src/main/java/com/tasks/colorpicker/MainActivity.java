package com.tasks.colorpicker;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
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

    private static String MAIN_COLOR = "main_color";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        int[] ids = new int[]{
                R.id.square1, R.id.square2, R.id.square3, R.id.square4,
                R.id.square5, R.id.square6, R.id.square7, R.id.square8,
                R.id.square9, R.id.square10, R.id.square11, R.id.square12,
                R.id.square13, R.id.square14, R.id.square15, R.id.square16
        };
        ColorSquare[] squares = new ColorSquare[16];
        final int[] colors = new int[16];

        for (int i = 0; i < 16; i++) {
            squares[i] = (ColorSquare) findViewById(ids[i]);
            colors[i] = Color.HSVToColor(new float[]{360 / 15 * i, 1, 1});
        }

        for (int i = 0; i < 16; i++) {
            squares[i].setDefaultColor(colors[i]);
            squares[i].setBackgroundColor(colors[i]);
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
        outState.putInt(MAIN_COLOR, color);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        refresh(savedInstanceState.getInt(MAIN_COLOR, 0));
    }

    public void refresh(int color) {
        colorImage.setImageDrawable(new ColorDrawable(color));
        rgbText.setText(colorToRgbString(color));
        hsvText.setText(colorToHsvString(color));
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
}
