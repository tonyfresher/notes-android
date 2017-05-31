package com.tasks.notes;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tasks.notes.classes.Filter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterFragment extends Fragment {

    interface OnFilterChosenListener {
        void onFilterChosen(Filter filter);
    }

    public static final String TAG = "filter_fragment";

    public static FilterFragment newInstance(OnFilterChosenListener listener) {
        Bundle args = new Bundle();
        FilterFragment fragment = new FilterFragment();
        fragment.setOnFilterChosenListener(listener);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.filter_toolbar)
    Toolbar toolbar;
    @BindView(R.id.filter_tabs)
    TabLayout tabLayout;
    @BindView(R.id.filter_container)
    ViewPager viewPager;
    @BindView(R.id.filter_exit)
    ImageView exitButton;

    private OnFilterChosenListener onFilterChosenListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filter, container, false);
        ButterKnife.bind(this, rootView);

        FilterPagerAdapter filterPagerAdapter =
                new FilterPagerAdapter(getChildFragmentManager());

        viewPager.setAdapter(filterPagerAdapter);
        viewPager.setCurrentItem(1);

        tabLayout.setupWithViewPager(viewPager);

        getChildFragmentManager().beginTransaction()
                .add(R.id.filter_container, FilterSavedFragment.newInstance(), FilterSavedFragment.TAG)
                .add(R.id.filter_container, FilterEditFragment.newInstance(), FilterEditFragment.TAG)
                .commit();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }

    public void setOnFilterChosenListener(OnFilterChosenListener onFilterChosenListener) {
        this.onFilterChosenListener = onFilterChosenListener;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
    }

    public void refreshSavedList() {
        FilterSavedFragment filterSavedFragment =
                (FilterSavedFragment) getChildFragmentManager()
                        .findFragmentByTag(FilterSavedFragment.TAG);
        filterSavedFragment.refreshList();
    }

    public void exitWithFilter(Filter result) {
        exit();
        onFilterChosenListener.onFilterChosen(result);
    }

    public void setFilter(Filter filter) {
        viewPager.setCurrentItem(1);
        FilterEditFragment filterEditFragment =
                (FilterEditFragment) getChildFragmentManager()
                        .findFragmentByTag(FilterEditFragment.TAG);
        filterEditFragment.initFromFilter(filter);
    }

    @OnClick(R.id.filter_exit)
    protected void exit() {
        ((MainActivity) getActivity()).removeFragment(this);
    }


    private class FilterPagerAdapter extends FragmentPagerAdapter {

        FilterPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FilterSavedFragment.newInstance();
                case 1:
                    return FilterEditFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.saved_filters_tab);
                case 1:
                    return getString(R.string.create_filter_tab);
            }
            return null;
        }
    }
}