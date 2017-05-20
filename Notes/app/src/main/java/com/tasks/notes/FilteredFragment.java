package com.tasks.notes;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.tasks.notes.adapters.NotesAdapter;
import com.tasks.notes.classes.Filter;
import com.tasks.notes.classes.Note;
import com.tasks.notes.helpers.DatabaseHelper;
import com.tasks.notes.helpers.HandyTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilteredFragment extends Fragment {

    private static final String ARG_FILTERED_NOTES = "filtered_notes";
    @BindView(R.id.filtered_exit)
    ImageView mExitButton;
    @BindView(R.id.filtered_search_view)
    SearchView mSearchView;
    @BindView(R.id.filtered_notes)
    RecyclerView mNotesRecyclerView;

    private final List<Note> mNotes = new ArrayList<>();

    public static FilteredFragment newInstance() {
        Bundle args = new Bundle();
        FilteredFragment fragment = new FilteredFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static FilteredFragment newInstance(List<Note> filteredNotes) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_FILTERED_NOTES, (ArrayList<Note>) filteredNotes);
        FilteredFragment fragment = new FilteredFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filtered, container, false);
        ButterKnife.bind(this, rootView);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.filtered_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        setHasOptionsMenu(true);

        mExitButton.setOnClickListener(v ->
                getActivity().getSupportFragmentManager().popBackStack());

        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    return true;
                }

                HandyTask<String, List<Note>> search = getDatabaseHelper().searchBySubstringTask();
                search.setOnPostExecute(result -> {
                    mNotes.clear();
                    mNotes.addAll(result);
                    mNotesRecyclerView.getAdapter().notifyDataSetChanged();
                    return null;
                });
                search.execute(newText);

                return true;
            }
        });

        EditText searchEditText =
                ((EditText)mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        searchEditText.setHintTextColor(Color.LTGRAY);
        searchEditText.setTextColor(Color.BLACK);

        if (getArguments().containsKey(ARG_FILTERED_NOTES)) {
            mNotes.addAll(getArguments().getParcelableArrayList(ARG_FILTERED_NOTES));
        } else {
            mSearchView.requestFocus();
        }

        NotesAdapter adapter = new NotesAdapter(mNotes, (v, position) -> {
            Intent intent = new Intent(v.getContext(), EditActivity.class);
            intent.putExtra(Note.INTENT_EXTRA, (Serializable) mNotes.get(position));
            startActivity(intent);
        });
        mNotesRecyclerView.setAdapter(adapter);
        mNotesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
    }

    public DatabaseHelper getDatabaseHelper() {
        return ((MainActivity) getActivity()).getDatabaseHelper();
    }
}
