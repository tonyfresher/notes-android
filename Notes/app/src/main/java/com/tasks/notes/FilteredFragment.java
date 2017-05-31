package com.tasks.notes;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.tasks.notes.classes.Note;
import com.tasks.notes.helpers.DatabaseHelper;
import com.tasks.notes.helpers.HandyTask;
import com.tasks.notes.helpers.NotificationEnvelope;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilteredFragment extends Fragment {

    public static final String TAG = "filtered_fragment";

    private static final int NOTIFICATION_ID_SEARCH = 8;

    private static final String ARG_FILTERED_NOTES = "filtered_notes";

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

    @BindView(R.id.filtered_toolbar)
    Toolbar toolbar;
    @BindView(R.id.filtered_exit)
    ImageView exitButton;
    @BindView(R.id.filtered_search_view)
    SearchView searchView;
    @BindView(R.id.filtered_notes)
    RecyclerView notesRecyclerView;

    private final List<Note> notesList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filtered, container, false);
        ButterKnife.bind(this, rootView);

        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()) {
                    return true;
                }

                final NotificationEnvelope notification = new NotificationEnvelope(
                        getContext(), NOTIFICATION_ID_SEARCH, "Search...", true);

                HandyTask<String, List<Note>> search = getDatabaseHelper().searchBySubstringTask();
                search.setOnPreExecute(v -> {
                    notification.start();
                    return null;
                });
                search.setOnPostExecute(result -> {
                    notification.close();
                    notesList.clear();
                    notesList.addAll(result);
                    notesRecyclerView.getAdapter().notifyDataSetChanged();
                    return null;
                });
                search.execute(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        EditText searchEditText =
                ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        searchEditText.setHintTextColor(Color.LTGRAY);
        searchEditText.setTextColor(Color.BLACK);

        if (getArguments().containsKey(ARG_FILTERED_NOTES)) {
            notesList.addAll(getArguments().getParcelableArrayList(ARG_FILTERED_NOTES));
        } else {
            searchView.requestFocus();
        }

        NotesAdapter adapter = new NotesAdapter(notesList, (v, position) -> {
            EditFragment fragment = EditFragment.newInstance(
                    notesList.get(position), position, new EditFragment.OnItemStateChangedListener() {
                        @Override
                        public void onItemChanged(Note note, int position) {
                            ListFragment parentFragment = (ListFragment) getActivity()
                                    .getSupportFragmentManager()
                                    .findFragmentByTag(ListFragment.TAG);
                            parentFragment.refreshList();
                            notesList.set(position, note);
                            notesRecyclerView.getAdapter().notifyItemChanged(position);
                        }

                        @Override
                        public void onItemRemoved(int position) {
                            ListFragment parentFragment = (ListFragment) getActivity()
                                    .getSupportFragmentManager()
                                    .findFragmentByTag(ListFragment.TAG);
                            parentFragment.refreshList();
                            notesList.remove(position);
                            notesRecyclerView.getAdapter().notifyItemRemoved(position);
                        }
                    });

            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        notesRecyclerView.setAdapter(adapter);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
    }

    @OnClick(R.id.filtered_exit)
    protected void exit() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(this)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .commit();
    }

    private DatabaseHelper getDatabaseHelper() {
        return DatabaseHelper.getInstance(getContext());
    }
}
