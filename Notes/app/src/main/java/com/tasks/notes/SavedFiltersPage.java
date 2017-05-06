package com.tasks.notes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tasks.notes.adapters.FiltersAdapter;
import com.tasks.notes.classes.Filter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SavedFiltersPage extends Fragment {
    @BindView(R.id.filter_saved_filters_list)
    RecyclerView mSavedFiltersList;

    public SavedFiltersPage() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_saved_filters, container, false);
        ButterKnife.bind(this, rootView);

        mSavedFiltersList.setLayoutManager(new LinearLayoutManager(getContext()));

        FilterActivity context = ((FilterActivity) container.getContext());
        Filter[] filters = context.getFiltersFromPrefs();
        refreshList(filters);

        return rootView;
    }

    public void refreshList(final Filter[] filters) {
        FiltersAdapter adapter = new FiltersAdapter(filters, new FiltersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                FilterActivity context = ((FilterActivity) v.getContext());
                context.exitWithResult(filters[position]);
            }
        });
        mSavedFiltersList.setAdapter(adapter);
    }
}