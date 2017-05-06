package com.tasks.notes;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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

import com.tasks.notes.classes.Note;
import com.tasks.notes.helpers.ColorsHelper;
import com.tasks.notes.helpers.DatabaseHelper;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tasks.notes.helpers.ColorsHelper.*;
import static com.tasks.notes.helpers.DateHelper.ISO8601_DATE_FORMAT;

public class EditActivity extends AppCompatActivity implements SquareFactory {
    private final DatabaseHelper mDatabaseHelper = new DatabaseHelper(this);
    private boolean mIsNewNote = true;
    private Note mNote;

    @BindView(R.id.edit_scroll_view)
    ScrollView mScrollView;
    @BindView(R.id.edit_app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.edit_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.edit_title)
    EditText mNoteTitle;
    @BindView(R.id.edit_description)
    EditText mNoteDescription;
    @BindView(R.id.edit_colors_layout)
    LinearLayout mColorsLayout;
    ImageView[] mColorSquares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        int margin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
        mColorSquares = ColorsHelper.makeSquares(this, mColorsLayout, margin);

        changeActivityColor(0);

        if (getIntent().hasExtra(Note.INTENT_EXTRA)) {
            mIsNewNote = false;
            mNote = (Note) getIntent().getSerializableExtra(Note.INTENT_EXTRA);
            findViewById(R.id.edit_delete).setVisibility(View.VISIBLE);
        } else {
            mNote = new Note();
        }

        initFromNote();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Note.INTENT_EXTRA, mNote);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mNote = savedInstanceState.getParcelable(Note.INTENT_EXTRA);

        changeActivityColor(mNote.getColor());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exit();
    }

    private void initFromNote() {
        mNoteTitle.setText(mNote.getTitle());

        if (mNote.getDescription() != null)
            mNoteDescription.setText(mNote.getDescription());

        changeActivityColor(mNote.getColor());
    }

    private void changeActivityColor(int color) {
        mAppBarLayout.setBackgroundColor(color);
        mScrollView.setBackgroundColor(color);
        ColorsHelper.updateSquares(this, mColorSquares, color);
    }

    public ImageView makeColorSquare(final int color, final ImageView[] squares) {
        ImageView square = ColorsHelper.makeSquare(this, color, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView s : squares) {
                    int color = ((ColorDrawable) s.getBackground()).getColor();
                    if (color == mNote.getColor()) {
                        s.setImageDrawable(getDrawable(R.drawable.frame));
                    }
                }

                ((ImageView) v).setImageDrawable(getDrawable(R.drawable.frame_highlited));
                mNote.setColor(color);
                changeActivityColor(color);
            }
        });

        return square;
    }

    @OnClick(R.id.edit_exit)
    protected void exit() {
        if (!mIsNewNote)
            mDatabaseHelper.refreshViewedDate(mNote.getId(),
                    ISO8601_DATE_FORMAT.format(new Date()));

        setResult(RESULT_CANCELED);
        finish();
    }

    @OnClick(R.id.edit_save)
    protected void trySaveAndExit() {
        saveNote();
        setResult(RESULT_OK);
        finish();

        Toast.makeText(getApplicationContext(), getString(R.string.note_saved), Toast.LENGTH_SHORT)
                .show();
    }

    @OnClick(R.id.edit_delete)
    protected void deleteAndExit() {
        deleteNote();
        setResult(RESULT_OK);
        finish();

        Toast.makeText(getApplicationContext(), getString(R.string.note_deleted), Toast.LENGTH_SHORT)
                .show();
    }

    private void saveNote() {
        mNote.setTitle(getTitleFromEditText());
        mNote.setDescription(getDescriptionFromEditText());

        String now = ISO8601_DATE_FORMAT.format(new Date());
        mNote.setEdited(now);
        mNote.setViewed(now);

        if (mIsNewNote) {
            mDatabaseHelper.insert(mNote);
        } else {
            mDatabaseHelper.replace(mNote.getId(), mNote);
        }
    }

    private void deleteNote() {
        mDatabaseHelper.delete(mNote.getId());
    }

    private String getDescriptionFromEditText() {
        return mNoteDescription.getText().toString();
    }

    private String getTitleFromEditText() {
        return mNoteTitle.getText().toString();
    }
}