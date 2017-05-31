package com.tasks.notes;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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

public class EditFragment extends Fragment implements
        OnBackPressedListener, SquareFactory {

    interface OnItemAddedListener {
        void onItemAdded(Note note);
    }

    interface OnItemStateChangedListener {
        void onItemChanged(Note note, int position);

        void onItemRemoved(int position);
    }

    public static final String TAG = "edit_fragment";

    private static final String ARG_NOTE = "note";
    private static final String ARG_POSITION = "position";

    public static EditFragment newInstance(OnItemAddedListener listener) {
        Bundle args = new Bundle();
        EditFragment fragment = new EditFragment();
        fragment.setArguments(args);
        fragment.setOnAddListener(listener);
        return fragment;
    }

    public static EditFragment newInstance(Note note, int position,
                                           OnItemStateChangedListener listener) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_NOTE, note);
        args.putInt(ARG_POSITION, position);
        EditFragment fragment = new EditFragment();
        fragment.setArguments(args);
        fragment.setOnChangeListener(listener);
        return fragment;
    }

    @BindView(R.id.edit_scroll_view)
    ScrollView scrollView;
    @BindView(R.id.edit_app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.edit_toolbar)
    Toolbar toolbar;
    @BindView(R.id.edit_title)
    EditText noteTitle;
    @BindView(R.id.edit_description)
    EditText noteDescription;
    @BindView(R.id.edit_colors_layout)
    LinearLayout colorsLayout;
    private ImageView[] colorSquares;
    @BindView(R.id.edit_image_url)
    EditText noteImageUrl;
    @BindView(R.id.edit_image)
    ImageView noteImage;

    private boolean isNewNote = true;

    private Note note;
    private int notePosition;
    private OnItemAddedListener onAddListener;
    private OnItemStateChangedListener onChangeListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);
        ButterKnife.bind(this, rootView);

        int margin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
        colorSquares = ColorsHelper.makeSquares(this, colorsLayout, margin);

        changeActivityColor(0);

        if (getArguments().containsKey(ARG_NOTE)) {
            isNewNote = false;
            note = getArguments().getParcelable(ARG_NOTE);
            notePosition = getArguments().getInt(ARG_POSITION);
            rootView.findViewById(R.id.edit_delete).setVisibility(View.VISIBLE);
        } else {
            isNewNote = true;
            note = new Note();
        }

        noteImageUrl.addTextChangedListener(new TextWatcher() {

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
                    noteImage.setVisibility(View.GONE);
                } else {
                    tryLoadPicture(s.toString());
                }
            }
        });

        initFromNote();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }

    public void setOnAddListener(OnItemAddedListener onAddListener) {
        this.onAddListener = onAddListener;
    }

    public void setOnChangeListener(OnItemStateChangedListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_NOTE, note);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            note = savedInstanceState.getParcelable(ARG_NOTE);

            changeActivityColor(note.getColor());
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
    }

    @Override
    public void onBackPressed() {
        finish(false);
    }

    private void initFromNote() {
        noteTitle.setText(note.getTitle());

        if (note.getDescription() != null)
            noteDescription.setText(note.getDescription());

        if (note.getImageUrl() != null && !note.getImageUrl().isEmpty()) {
            noteImageUrl.setText(note.getImageUrl());
            tryLoadPicture(note.getImageUrl());
        }

        changeActivityColor(note.getColor());
    }

    private void tryLoadPicture(String url) {
        noteImage.setVisibility(View.VISIBLE);
        Picasso.with(getContext())
                .load(url)
                .error(R.drawable.wrong_image)
                .into(noteImage);
    }

    private void changeActivityColor(int color) {
        appBarLayout.setBackgroundColor(color);
        scrollView.setBackgroundColor(color);
        ColorsHelper.updateSquares(getContext(), colorSquares, color);
    }

    public ImageView makeColorSquare(final int color, final ImageView[] squares) {
        return ColorsHelper.makeSquare(getContext(), color, v -> {
            for (ImageView s : squares) {
                int color1 = ((ColorDrawable) s.getBackground()).getColor();
                if (color1 == note.getColor()) {
                    s.setImageDrawable(getContext().getDrawable(R.drawable.frame));
                }
            }

            ((ImageView) v).setImageDrawable(getContext().getDrawable(R.drawable.frame_highlited));
            note.setColor(color);
            changeActivityColor(color);
        });
    }

    @OnClick(R.id.edit_exit)
    protected void exit() {
        finish(false);
    }

    @OnClick(R.id.edit_delete)
    protected void deleteAndExit() {
        finish(true);
    }

    private void finish(boolean noteShouldBeDeleted) {
        if (noteShouldBeDeleted) {
            deleteNote();
            onChangeListener.onItemRemoved(notePosition);
        } else {
            saveNote();
            if (isNewNote) {
                onAddListener.onItemAdded(note);
            } else {
                onChangeListener.onItemChanged(note, notePosition);
            }
        }

        ((MainActivity) getActivity()).removeFragment(this);
    }

    private void saveNote() {
        if (!noteTitle.getText().toString().isEmpty()) {
            note.setTitle(noteTitle.getText().toString());
        }
        if (!noteDescription.getText().toString().isEmpty()) {
            note.setDescription(noteDescription.getText().toString());
        }
        if (!noteImageUrl.getText().toString().isEmpty()) {
            note.setImageUrl(noteImageUrl.getText().toString());
        }

        String now = ISO8601_DATE_FORMAT.print(new DateTime());
        note.setEdited(now);
        note.setViewed(now);

        if (isNewNote) {
            getDatabaseHelper().insertAsync(note);
        } else {
            getDatabaseHelper().replaceAsync(note.getId(), note);
        }
    }

    private void deleteNote() {
        getDatabaseHelper().deleteAsync(note.getId());
    }

    private DatabaseHelper getDatabaseHelper() {
        return DatabaseHelper.getInstance(getContext());
    }
}