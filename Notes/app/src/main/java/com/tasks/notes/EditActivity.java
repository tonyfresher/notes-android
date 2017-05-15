package com.tasks.notes;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tasks.notes.classes.Note;
import com.tasks.notes.helpers.ColorsHelper;
import com.tasks.notes.helpers.DatabaseHelper;

import org.joda.time.DateTime;

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
    @BindView(R.id.edit_image_url)
    EditText mNoteImageUrl;
    @BindView(R.id.edit_image)
    ImageView mNoteImage;

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

        mNoteImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if ("".equals(text)) {
                    mNoteImage.setVisibility(View.GONE);
                } else {
                    tryLoadPicture(s.toString());
                }
            }
        });

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

        if (mNote.getImageUrl() != null && !mNote.getImageUrl().isEmpty()) {
            mNoteImageUrl.setText(mNote.getImageUrl());
            tryLoadPicture(mNote.getImageUrl());
        }

        changeActivityColor(mNote.getColor());
    }

    private void tryLoadPicture(String url) {
        mNoteImage.setVisibility(View.VISIBLE);
        Picasso.with(this)
                .load(url)
                .error(R.drawable.wrong_image)
                .into(mNoteImage);
    }

    private void changeActivityColor(int color) {
        mAppBarLayout.setBackgroundColor(color);
        mScrollView.setBackgroundColor(color);
        ColorsHelper.updateSquares(this, mColorSquares, color);
    }

    public ImageView makeColorSquare(final int color, final ImageView[] squares) {
        return ColorsHelper.makeSquare(this, color, v -> {
            for (ImageView s : squares) {
                int color1 = ((ColorDrawable) s.getBackground()).getColor();
                if (color1 == mNote.getColor()) {
                    s.setImageDrawable(getDrawable(R.drawable.frame));
                }
            }

            ((ImageView) v).setImageDrawable(getDrawable(R.drawable.frame_highlited));
            mNote.setColor(color);
            changeActivityColor(color);
        });
    }

    @OnClick(R.id.edit_exit)
    protected void exit() {
        if (!mIsNewNote)
            mDatabaseHelper.refreshViewedDate(mNote.getId(),
                    ISO8601_DATE_FORMAT.print(new DateTime()));

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
        if (!mNoteTitle.getText().toString().isEmpty()) {
            mNote.setTitle(mNoteTitle.getText().toString());
        }
        if (!mNoteDescription.getText().toString().isEmpty()) {
            mNote.setDescription(mNoteDescription.getText().toString());
        }
        if (!mNoteImageUrl.getText().toString().isEmpty()) {
            mNote.setImageUrl(mNoteImageUrl.getText().toString());
        }

        String now = ISO8601_DATE_FORMAT.print(new DateTime());
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
}