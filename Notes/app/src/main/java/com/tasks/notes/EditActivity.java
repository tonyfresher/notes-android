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
            findViewById(R.id.edit_delete).setVisibility(View.VISIBLE);
        } else {
            note = new Note();
        }

        initFromNote();
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

        changeActivityColor(note.getColor());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exit();
    }

    private void initFromNote() {
        noteName.setText(note.getName());

        if (note.getDescription() != null)
            noteDescription.setText(note.getDescription());

        changeActivityColor(note.getColor());
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
                    if (color == note.getColor()) {
                        s.setImageDrawable(getDrawable(R.drawable.frame));
                    }
                }

                ((ImageView) v).setImageDrawable(getDrawable(R.drawable.frame_highlited));
                note.setColor(color);
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
    protected void exit() {
        if (!isNewNote)
            databaseHelper.refreshViewedDate(note.getId(),
                    getNowString());
        finish();
    }

    @OnClick(R.id.edit_save)
    protected void trySaveAndExit() {
        saveNote();
        finish();

        Toast.makeText(getApplicationContext(), getString(R.string.note_saved), Toast.LENGTH_SHORT)
                .show();
    }

    @OnClick(R.id.edit_delete)
    protected void deleteAndExit() {
        deleteNote();
        finish();

        Toast.makeText(getApplicationContext(), getString(R.string.note_deleted), Toast.LENGTH_SHORT)
                .show();
    }

    private void saveNote() {
        note.setName(getNameFromEditText());
        note.setDescription(getDescriptionFromEditText());

        String now = getNowString();
        note.setEdited(now);
        note.setViewed(now);

        if (isNewNote) {
            databaseHelper.insert(note);
        } else {
            databaseHelper.replace(note.getId(), note);
        }
    }

    private void deleteNote() {
        databaseHelper.delete(note.getId());
    }

    private String getDescriptionFromEditText() {
        return noteDescription.getText().toString();
    }

    private String getNameFromEditText() {
        return noteName.getText().toString();
    }

    private static String getNowString() {
        return Note.ISO8601_DATE_FORMAT.format(new Date());
    }
}