package com.tasks.notes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import static com.tasks.notes.helpers.ImportExportHelper.GSON_SERIALIZER;

public class FilterSavedFragment extends Fragment {

    public static final String TAG = "filter_saved_fragment";

    public static FilterSavedFragment newInstance() {
        Bundle args = new Bundle();
        FilterSavedFragment fragment = new FilterSavedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.filter_filter_saved_list)
    RecyclerView savedFiltersRecyclerView;

    private final List<Filter> savedFilters = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filter_saved, container, false);
        ButterKnife.bind(this, rootView);

        FiltersAdapter adapter = new FiltersAdapter(savedFilters, (v, position) -> {
            FilterFragment filterFragment = (FilterFragment) getParentFragment();
            filterFragment.setFilter(savedFilters.get(position));
        });
        savedFiltersRecyclerView.setAdapter(adapter);
        savedFiltersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        refreshList();

        return rootView;
    }

    public void refreshList() {
        for (Filter f : getFiltersFromPrefs()) {
            if (!savedFilters.contains(f)) {
                savedFilters.add(f);
                savedFiltersRecyclerView.getAdapter()
                        .notifyDataSetChanged();
            }
        }
    }

    private List<Filter> getFiltersFromPrefs() {
        Map<String, ?> fromPrefs = getActivity().getPreferences(Context.MODE_PRIVATE).getAll();
        List<Filter> filters = new ArrayList<>();
        for (Map.Entry<String, ?> entry : fromPrefs.entrySet()) {
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