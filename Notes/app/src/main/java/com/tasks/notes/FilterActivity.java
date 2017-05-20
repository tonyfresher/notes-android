package com.tasks.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.gson.JsonParseException;
import com.tasks.notes.classes.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tasks.notes.helpers.FileSystemHelper.GSON_SERIALIZER;

public class FilterActivity extends AppCompatActivity {
    private SavedFiltersFragment mSavedFiltersFragment;
    private EditFilterFragment mEditFilterFragment;

    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.container)
    ViewPager mViewPager;
    @BindView(R.id.filter_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.filter_exit)
    ImageView mExitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mExitButton.setOnClickListener(v -> finish());

        FilterPagerAdapter filterPagerAdapter =
                new FilterPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(filterPagerAdapter);
        mViewPager.setCurrentItem(1);

        mTabLayout.setupWithViewPager(mViewPager);

        mSavedFiltersFragment = new SavedFiltersFragment();
        mEditFilterFragment = new EditFilterFragment();
    }

    public void refreshSavedList() {
        mSavedFiltersFragment.refreshList();
    }

    public void exitWithResult(Filter result) {
        Intent intent = new Intent();
        intent.putExtra(Filter.INTENT_EXTRA, result);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void setFilter(Filter filter) {
        mViewPager.setCurrentItem(1);
        mEditFilterFragment.initFromFilter(filter);
    }

    public class FilterPagerAdapter extends FragmentPagerAdapter {

        public FilterPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mSavedFiltersFragment;
                case 1:
                    return mEditFilterFragment;
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