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

    @BindView(R.id.square_scroll)
    LockableScrollView squareScrollView;

    @BindView(R.id.square1)
    ColorSquare square1;
    @BindView(R.id.square2)
    ColorSquare square2;
    @BindView(R.id.square3)
    ColorSquare square3;
    @BindView(R.id.square4)
    ColorSquare square4;
    @BindView(R.id.square5)
    ColorSquare square5;
    @BindView(R.id.square6)
    ColorSquare square6;
    @BindView(R.id.square7)
    ColorSquare square7;
    @BindView(R.id.square8)
    ColorSquare square8;
    @BindView(R.id.square9)
    ColorSquare square9;
    @BindView(R.id.square10)
    ColorSquare square10;
    @BindView(R.id.square11)
    ColorSquare square11;
    @BindView(R.id.square12)
    ColorSquare square12;
    @BindView(R.id.square13)
    ColorSquare square13;
    @BindView(R.id.square14)
    ColorSquare square14;
    @BindView(R.id.square15)
    ColorSquare square15;
    @BindView(R.id.square16)
    ColorSquare square16;

    private static String MAIN_COLOR = "main_color";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ColorSquare[] squares = new ColorSquare[]{
                square1, square2, square3, square4,
                square5, square6, square7, square8,
                square9, square10, square11, square12,
                square13, square14, square15, square16
        };

        final int[] colors = new int[16];
        for (int i = 0; i < 16; i++) {
            colors[i] = Color.HSVToColor(new float[]{360 / 15 * i, 1, 1});
        }

        for (int i = 0; i < 16; i++) {
            squares[i].setDefaultColor(colors[i]);
            squares[i].setBackgroundColor(colors[i]);
        }
        /*PaintDrawable pd = new PaintDrawable();
        pd.setShape(new RectShape());
        pd.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(0, 0, width, 0, colors, null, Shader.TileMode.CLAMP);
            }
        });
        squareScrollView.setBackground(pd);*/
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
        ColorDrawable d = new ColorDrawable(color);
        colorImage.setImageDrawable(d);
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
                (int) hsv[0], (int) hsv[1] * 100, (int) hsv[2] * 100);
    }
}
