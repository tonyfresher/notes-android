package com.tasks.notes.helpers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tasks.notes.R;

public class ColorsHelper {
    public interface SquareFactory {
        ImageView makeColorSquare(final int color, final ImageView[] squares);
    }

    public static final int COLORS_COUNT = 9;
    public static final int COLOR_STEP = 360 / COLORS_COUNT;
    public static final float COLOR_LIGHTNESS = 0.3f;

    public final static int DEFAULT_NOTE_COLOR = Color.parseColor("#ffffff");

    public static ImageView makeSquare(Context context, final int color, View.OnClickListener listener) {
        ImageView square = new ImageView(context);
        square.setBackgroundColor(color);
        square.setImageDrawable(context.getDrawable(R.drawable.frame));
        square.setOnClickListener(listener);

        return square;
    }

    private static void addSquareToLayout(LinearLayout layout, ImageView square,
                                         int left, int top, int right, int bottom) {
        int size = (int) layout.getContext()
                .getResources().getDimension(R.dimen.color_square_size);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        lp.setMargins(left, top, right, bottom);
        layout.addView(square, lp);
    }

    public static void updateSquares(Context context, ImageView[] squares, int color) {
        for (ImageView square : squares) {
            int squareColor = ((ColorDrawable) square.getBackground()).getColor();
            Drawable frame = (squareColor == color) ?
                    context.getDrawable(R.drawable.frame_highlited) :
                    context.getDrawable(R.drawable.frame);
            square.setImageDrawable(frame);
        }
    }

    public static ImageView[] makeSquares(SquareFactory factory, LinearLayout layout, int margin) {
        ImageView[] squares = new ImageView[COLORS_COUNT + 1];
        ImageView defaultSquare = factory.makeColorSquare(DEFAULT_NOTE_COLOR, squares);
        addSquareToLayout(layout, defaultSquare, 0, 0, 0, 0);
        squares[0] = defaultSquare;

        for (int i = 0; i < COLORS_COUNT; i++) {
            int color = Color.HSVToColor(new float[]{COLOR_STEP * i, COLOR_LIGHTNESS, 1});
            ImageView square = factory.makeColorSquare(color, squares);
            addSquareToLayout(layout, square, margin, 0, 0, 0);
            squares[i + 1] = square;
        }

        return squares;
    }
}
