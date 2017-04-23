package com.tasks.multiplescreens;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditItemActivity extends AppCompatActivity {
    public final static int COLORS_COUNT = 9;
    public static int COLOR_STEP = 360 / COLORS_COUNT;

    @BindView(R.id.edit_app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.edit_toolbar)
    Toolbar toolbar;
    @BindView(R.id.edit_name)
    EditText itemName;
    @BindView(R.id.edit_description)
    EditText itemDescription;
    @BindView(R.id.colors_layout)
    LinearLayout colorsLayout;

    private boolean isNewItem = true;
    private ItemContent item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getIntent().hasExtra(ItemContent.NAME)) {
            isNewItem = false;

            item = (ItemContent) getIntent().getSerializableExtra(ItemContent.NAME);
            initFromItem();

            findViewById(R.id.edit_delete).setVisibility(View.VISIBLE);
        } else {
            item = new ItemContent();
        }

        final ImageView[] squares = new ImageView[COLORS_COUNT + 1];
        int margin = (int) getResources().getDimension(R.dimen.margin);

        ImageView emptySquare = makeColorSquare(0, squares);
        addSquareToLayout(emptySquare, 0, 0, 0, 0);
        squares[0] = emptySquare;

        for (int i = 0; i < COLORS_COUNT; i++) {
            int color = Color.HSVToColor(new float[]{COLOR_STEP * i, 1, 1});
            ImageView square = makeColorSquare(color, squares);
            addSquareToLayout(square, margin, 0, 0, 0);
            squares[i + 1] = square;
        }

        for (ImageView s : squares) {
            int color = ((ColorDrawable) s.getBackground()).getColor();
            if (color == item.color) {
                s.setImageDrawable(getDrawable(R.drawable.frame_checked));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        item.name = getName();
        item.description = getDescription();
        outState.putParcelable(ItemContent.NAME, item);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        item = savedInstanceState.getParcelable(ItemContent.NAME);
    }

    private void initFromItem() {
        itemName.setText(item.name);
        if (item.description != null)
            itemDescription.setText(item.description);
        if (item.color != 0)
            appBarLayout.setBackgroundColor(item.color);
        else {
            appBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    private ImageView makeColorSquare(final int color, final ImageView[] squares) {
        ImageView square = new ImageView(this);

        square.setBackgroundColor(color);
        square.setImageDrawable(getDrawable(R.drawable.frame));

        square.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView s : squares) {
                    int color = ((ColorDrawable) s.getBackground()).getColor();
                    if (color == item.color) {
                        s.setImageDrawable(getDrawable(R.drawable.frame));
                    }
                }

                ((ImageView) v).setImageDrawable(getDrawable(R.drawable.frame_checked));
                item.color = color;
                initFromItem();
            }
        });

        return square;
    }

    private void addSquareToLayout(ImageView square,
                                   int left, int top, int right, int bottom) {
        int size = (int) getResources().getDimension(R.dimen.color_square_size);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        lp.setMargins(left, top, right, bottom);
        colorsLayout.addView(square, lp);
    }

    @OnClick(R.id.edit_exit)
    public void exit() {
        finish();
    }

    @OnClick(R.id.edit_save)
    public void trySaveAndExit() {
        if (getName().equals("")) {
            Toast.makeText(this, "Please enter item name", Toast.LENGTH_SHORT).show();
            return;
        }
        saveItem();
        finish();
    }

    @OnClick(R.id.edit_delete)
    public void deleteAndExit() {
        deleteItem();
        finish();
    }

    private void saveItem() {
        item.name = getName();
        item.description = getDescription();

        DatabaseHelper helper = new DatabaseHelper(this);
        if (isNewItem) {
            helper.insert(item);
        } else {
            helper.replace(item.id, item);
        }
    }

    private void deleteItem() {
        DatabaseHelper helper = new DatabaseHelper(this);
        helper.delete(item.id);
    }

    private String getDescription() {
        return itemDescription.getText().toString();
    }

    private String getName() {
        return itemName.getText().toString();
    }
}