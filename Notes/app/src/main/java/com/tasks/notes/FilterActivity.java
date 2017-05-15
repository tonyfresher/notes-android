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
    private SavedFiltersPage mSavedFiltersPage;
    private EditFilterPage mEditFilterPage;

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

        mSavedFiltersPage = new SavedFiltersPage();
        mEditFilterPage = new EditFilterPage();
    }

    public void refreshSavedList() {
        Filter[] filters = getFiltersFromPrefs();
        mSavedFiltersPage.refreshList(filters);
    }

    public void saveFilterToPrefs(Filter filter) {
        String json = GSON_SERIALIZER.toJson(filter);
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        sharedPref.edit()
                .putString(filter.getName(), json)
                .apply();
    }

    public Filter[] getFiltersFromPrefs() {
        Map<String, ?> fromPrefs = getPreferences(MODE_PRIVATE).getAll();
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

        return filters.toArray(new Filter[filters.size()]);
    }

    public void exitWithResult(Filter result) {
        Intent intent = new Intent();
        intent.putExtra(Filter.INTENT_EXTRA, result);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void setFilter(Filter filter) {
        mViewPager.setCurrentItem(1);
        mEditFilterPage.initFromFilter(filter);
    }

    public class FilterPagerAdapter extends FragmentPagerAdapter {

        public FilterPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mSavedFiltersPage;
                case 1:
                    return mEditFilterPage;
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