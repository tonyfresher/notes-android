package com.tasks.notes.helpers;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tasks.notes.R;

public abstract class ColorButtonCreator extends Fragment {

    public static final int DEFAULT_NOTE_COLOR = Color.parseColor("#ffffff");
    public static final int COLORS_COUNT = 9;

    private static final int COLOR_STEP = 360 / COLORS_COUNT;
    private static final float COLOR_LIGHTNESS = 0.3f;

    protected abstract void onColorButtonClick(View v);

    protected ImageView createColorButton(final int color) {
        ImageView colorButton = new ImageView(getContext());
        colorButton.setBackgroundColor(color);
        colorButton.setImageDrawable(getContext().getDrawable(R.drawable.frame));
        colorButton.setOnClickListener(v -> onColorButtonClick(v));

        return colorButton;
    }

    protected ImageView[] createColorButtons(LinearLayout layout, int margin) {
        ImageView[] squares = new ImageView[COLORS_COUNT + 1];
        ImageView defaultSquare = createColorButton(DEFAULT_NOTE_COLOR);
        addColorButtonToLayout(layout, defaultSquare, 0, 0, 0, 0);
        squares[0] = defaultSquare;

        for (int i = 0; i < COLORS_COUNT; i++) {
            int color = Color.HSVToColor(new float[]{COLOR_STEP * i, COLOR_LIGHTNESS, 1});
            ImageView square = createColorButton(color);
            addColorButtonToLayout(layout, square, margin, 0, 0, 0);
            squares[i + 1] = square;
        }

        return squares;
    }

    protected void updateColorButtons(ImageView[] buttons, int color) {
        for (ImageView square : buttons) {
            int squareColor = ((ColorDrawable) square.getBackground()).getColor();
            Drawable frame = (squareColor == color) ?
                    getContext().getDrawable(R.drawable.frame_highlited) :
                    getContext().getDrawable(R.drawable.frame);
            square.setImageDrawable(frame);
        }
    }

    protected static void addColorButtonToLayout(LinearLayout layout, ImageView button,
                                                 int left, int top, int right, int bottom) {
        int size = (int) layout.getContext()
                .getResources().getDimension(R.dimen.color_square_size);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        lp.setMargins(left, top, right, bottom);
        layout.addView(button, lp);
    }
}
