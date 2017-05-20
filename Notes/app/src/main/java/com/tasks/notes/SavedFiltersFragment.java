package com.tasks.notes;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonParseException;
import com.tasks.notes.adapters.FiltersAdapter;
import com.tasks.notes.classes.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tasks.notes.helpers.FileSystemHelper.GSON_SERIALIZER;

public class SavedFiltersFragment extends Fragment {
    @BindView(R.id.filter_saved_filters_list)
    RecyclerView mSavedFiltersRecyclerView;

    private final List<Filter> mSavedFilters = new ArrayList<>();

    public SavedFiltersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_saved_filters, container, false);
        ButterKnife.bind(this, rootView);

        mSavedFiltersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FiltersAdapter adapter = new FiltersAdapter(mSavedFilters, (v, position) -> {
            FilterActivity context = ((FilterActivity) v.getContext());
            context.setFilter(mSavedFilters.get(position));
        });
        mSavedFiltersRecyclerView.setAdapter(adapter);

        refreshList();

        return rootView;
    }

    public void refreshList() {
        for (Filter f : getFiltersFromPrefs()) {
            if (!mSavedFilters.contains(f)) {
                mSavedFilters.add(f);
                mSavedFiltersRecyclerView.getAdapter()
                        .notifyDataSetChanged();
            }
        }
    }

    public List<Filter> getFiltersFromPrefs() {
        Map<String, ?> fromPrefs = getActivity().getPreferences(Context.MODE_PRIVATE).getAll();
        List<Filter> filters = new ArrayList<>();
        for (Map.Entry entry : fromPrefs.entrySet()) {
            if (entry.getValue() instanceof String) {
                String json = (String) entry.getValue();
                try {
                    Filter f = GSON_SERIALIZER.fromJson(json, Filter.class);
                    filters.add(f);
                } catch (JsonParseException e) {
                }
            }
        }

        return filters;
    }
}