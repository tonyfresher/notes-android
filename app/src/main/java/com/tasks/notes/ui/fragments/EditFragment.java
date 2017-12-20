package com.tasks.notes.ui.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
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
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tasks.notes.R;
import com.tasks.notes.data.model.Note;
import com.tasks.notes.ui.MainActivity;
import com.tasks.notes.ui.infrastructure.ColorButtonCreator;
import com.tasks.notes.infrastructure.OnBackPressedListener;
import com.tasks.notes.infrastructure.OnItemAddedListener;
import com.tasks.notes.infrastructure.OnItemStateChangedListener;
import com.tasks.notes.data.storage.*;
import com.tasks.notes.ui.infrastructure.OnBackPressedListener;
import com.tasks.notes.ui.infrastructure.OnItemAddedListener;
import com.tasks.notes.ui.infrastructure.OnItemStateChangedListener;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tasks.notes.utility.DateFormats.ISO8601_DATE_FORMAT;

public class EditFragment extends ColorButtonCreator
        implements OnBackPressedListener {

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
    LinearLayout colorButtonsLayout;
    private ImageView[] colorButtons;
    @BindView(R.id.edit_image_url)
    EditText noteImageUrl;
    @BindView(R.id.edit_image)
    ImageView noteImage;

    private boolean isNewNote = true;

    private Note note;
    private int notePosition;
    private OnItemAddedListener onAddListener;
    private OnItemStateChangedListener onChangeListener;

    private AsyncStorageProvider getDatabaseProvider() {
        return DatabaseProvider.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);
        ButterKnife.bind(this, rootView);

        int margin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
        colorButtons = createColorButtons(colorButtonsLayout, margin);

        changeActivityColor(Color.TRANSPARENT);

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
    public boolean onBackPressed() {
        saveNote();
        callBack(false);
        return false;
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
        updateColorButtons(colorButtons, color);
    }

    @Override
    public void onColorButtonClick(View v) {
        for (ImageView b : colorButtons) {
            int backgroundColor = ((ColorDrawable) b.getBackground()).getColor();
            if (backgroundColor == note.getColor()) {
                b.setImageDrawable(getContext().getDrawable(R.drawable.frame));
            }
        }

        ((ImageView) v).setImageDrawable(getContext().getDrawable(R.drawable.frame_highlited));
        int backgroundColor = ((ColorDrawable) v.getBackground()).getColor();
        note.setColor(backgroundColor);
        changeActivityColor(backgroundColor);
    }

    @OnClick(R.id.edit_exit)
    protected void exit() {
        callBack(false);
        finish();
    }

    @OnClick(R.id.edit_delete)
    protected void deleteAndExit() {
        deleteNote();
        callBack(true);
        finish();
    }

    @OnClick(R.id.edit_save)
    protected void saveAndExit() {
        saveNote();
        Toast.makeText(getContext(), getString(R.string.note_saved), Toast.LENGTH_SHORT)
                .show();
        callBack(false);
        finish();
    }

    private void callBack(boolean noteWasDeleted) {
        if (noteWasDeleted) {
            onChangeListener.onItemRemoved(notePosition);
        } else {
            if (isNewNote) {
                onAddListener.onItemAdded(note);
            } else {
                onChangeListener.onItemChanged(note, notePosition);
            }
        }
    }

    private void finish() {
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
            getDatabaseProvider().saveAsync(note);
        } else {
            getDatabaseProvider().replaceAsync(note.getId(), note);
        }
    }

    private void deleteNote() {
        getDatabaseProvider().deleteAsync(note.getId());
    }
}