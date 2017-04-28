package com.tasks.notes;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditActivity extends AppCompatActivity {
    public final static int COLORS_COUNT = 9;
    public static int COLOR_STEP = 360 / COLORS_COUNT;

    private final ImageView[] colorSquares = new ImageView[COLORS_COUNT + 1];
    private final DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private boolean isNewNote = true;
    private Note note;

    @BindView(R.id.edit_scroll_view)
    ScrollView scrollView;
    @BindView(R.id.edit_app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.edit_toolbar)
    Toolbar toolbar;
    @BindView(R.id.edit_name)
    EditText noteName;
    @BindView(R.id.edit_description)
    EditText noteDescription;
    @BindView(R.id.edit_colors_layout)
    LinearLayout colorsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        int margin = (int) getResources().getDimension(R.dimen.margin);

        ImageView whiteSquare = makeColorSquare(Note.DEFAULT_COLOR, colorSquares);
        addSquareToLayout(whiteSquare, 0, 0, 0, 0);
        colorSquares[0] = whiteSquare;

        for (int i = 0; i < COLORS_COUNT; i++) {
            int color = Color.HSVToColor(new float[]{COLOR_STEP * i, 0.15f, 1});
            ImageView square = makeColorSquare(color, colorSquares);
            addSquareToLayout(square, margin, 0, 0, 0);
            colorSquares[i + 1] = square;
        }

        changeActivityColor(0);

        if (getIntent().hasExtra(Note.NAME)) {
            isNewNote = false;
            note = (Note) getIntent().getSerializableExtra(Note.NAME);
            initFromNote();

            findViewById(R.id.edit_delete).setVisibility(View.VISIBLE);
        } else {
            note = new Note();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Note.NAME, note);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        note = savedInstanceState.getParcelable(Note.NAME);

        changeActivityColor(note.color);
    }

    private void initFromNote() {
        noteName.setText(note.name);

        if (note.description != null)
            noteDescription.setText(note.description);

        changeActivityColor(note.color);
    }

    private void changeActivityColor(int color) {
        appBarLayout.setBackgroundColor(color);
        scrollView.setBackgroundColor(color);

        for (ImageView square : colorSquares) {
            int squareColor = ((ColorDrawable) square.getBackground()).getColor();
            Drawable frame = (squareColor == color) ?
                    getDrawable(R.drawable.frame_highlited) : getDrawable(R.drawable.frame);
            square.setImageDrawable(frame);
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
                    if (color == note.color) {
                        s.setImageDrawable(getDrawable(R.drawable.frame));
                    }
                }

                ((ImageView) v).setImageDrawable(getDrawable(R.drawable.frame_highlited));
                note.color = color;
                changeActivityColor(color);
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
        if (!isNewNote)
            databaseHelper.refreshViewedDate(note.id,
                    Note.ISO8601_DATE_FORMAT.format(new Date()));
        finish();
    }

    @OnClick(R.id.edit_save)
    public void trySaveAndExit() {
        saveNote();
        finish();

        Toast.makeText(getApplicationContext(), getString(R.string.note_saved), Toast.LENGTH_SHORT)
                .show();
    }

    @OnClick(R.id.edit_delete)
    public void deleteAndExit() {
        deleteNote();
        finish();

        Toast.makeText(getApplicationContext(), getString(R.string.note_deleted), Toast.LENGTH_SHORT)
                .show();
    }

    private void saveNote() {
        note.name = getName();
        note.description = getDescription();

        Date now = new Date();
        note.edited = now;
        note.viewed = now;

        if (isNewNote) {
            databaseHelper.insert(note);
        } else {
            databaseHelper.replace(note.id, note);
        }
    }

    private void deleteNote() {
        databaseHelper.delete(note.id);
    }

    private String getDescription() {
        return noteDescription.getText().toString();
    }

    private String getName() {
        return noteName.getText().toString();
    }
}