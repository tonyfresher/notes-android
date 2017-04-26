package com.tasks.notes;

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

public class EditNoteActivity extends AppCompatActivity {
    public final static int COLORS_COUNT = 9;
    public static int COLOR_STEP = 360 / COLORS_COUNT;

    @BindView(R.id.edit_app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.edit_toolbar)
    Toolbar toolbar;
    @BindView(R.id.edit_name)
    EditText noteName;
    @BindView(R.id.edit_description)
    EditText noteDescription;
    @BindView(R.id.colors_layout)
    LinearLayout colorsLayout;

    private boolean isNewNote = true;
    private NoteContent note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getIntent().hasExtra(NoteContent.NAME)) {
            isNewNote = false;

            note = (NoteContent) getIntent().getSerializableExtra(NoteContent.NAME);
            initFromNote();

            findViewById(R.id.edit_delete).setVisibility(View.VISIBLE);
        } else {
            note = new NoteContent();
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
            if (color == note.color) {
                s.setImageDrawable(getDrawable(R.drawable.frame_checked));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        note.name = getName();
        note.description = getDescription();
        outState.putParcelable(NoteContent.NAME, note);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        note = savedInstanceState.getParcelable(NoteContent.NAME);
    }

    private void initFromNote() {
        noteName.setText(note.name);
        
        if (note.description != null)
            noteDescription.setText(note.description);

        appBarLayout.setBackgroundColor((note.color != 0) ?
                note.color : ContextCompat.getColor(this, R.color.colorPrimary));
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

                ((ImageView) v).setImageDrawable(getDrawable(R.drawable.frame_checked));
                note.color = color;
                appBarLayout.setBackgroundColor((note.color != 0) ?
                        note.color : ContextCompat.getColor(v.getContext(), R.color.colorPrimary));
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
            Toast.makeText(this, "Please enter note name", Toast.LENGTH_SHORT).show();
            return;
        }
        saveItem();
        finish();

        Toast.makeText(getApplicationContext(), getString(R.string.note_saved), Toast.LENGTH_SHORT)
                .show();
    }

    @OnClick(R.id.edit_delete)
    public void deleteAndExit() {
        deleteItem();
        finish();

        Toast.makeText(getApplicationContext(), getString(R.string.note_deleted), Toast.LENGTH_SHORT)
                .show();
    }

    private void saveItem() {
        note.name = getName();
        note.description = getDescription();

        DatabaseHelper helper = new DatabaseHelper(this);
        if (isNewNote) {
            helper.insert(note);
        } else {
            helper.replace(note.id, note);
        }
    }

    private void deleteItem() {
        DatabaseHelper helper = new DatabaseHelper(this);
        helper.delete(note.id);
    }

    private String getDescription() {
        return noteDescription.getText().toString();
    }

    private String getName() {
        return noteName.getText().toString();
    }
}