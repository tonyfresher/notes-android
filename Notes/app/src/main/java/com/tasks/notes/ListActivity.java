package com.tasks.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListActivity extends AppCompatActivity {
    @BindView(R.id.list_notes)
    RecyclerView notesList;
    @BindView(R.id.list_floating_add_button)
    FloatingActionButton floatingAddButton;
    @BindView(R.id.bottom_sheet_sort)
    LinearLayout bottomSheet;

    BottomSheetBehavior sheetBehavior;

    private Comparator<Note> mDataComparator = Note.BY_CREATED_COMPARATOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(toolbar);

        notesList.setLayoutManager(new LinearLayoutManager(this));

        sheetBehavior = BottomSheetBehavior.from(bottomSheet);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_EXPANDED == newState ||
                        BottomSheetBehavior.STATE_DRAGGING == newState) {
                    floatingAddButton.animate().scaleX(0).scaleY(0).setDuration(300).start();
                } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    floatingAddButton.animate().scaleX(1).scaleY(1).setDuration(300).start();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        floatingAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        final Note[] data = getData(mDataComparator);
        ContentAdapter adapter = new ContentAdapter(data, new ContentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(v.getContext(), EditActivity.class);
                intent.putExtra(Note.NAME, (Serializable) data[position]);
                startActivity(intent);
            }
        });
        notesList.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort:
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.bottom_sheet_sort_by_name)
    public void compareByName() {
        mDataComparator = Note.BY_NAME_COMPARATOR;
        onResume();
    }

    @OnClick(R.id.bottom_sheet_sort_by_name)
    protected void compareByCreated() {
        mDataComparator = Note.BY_CREATED_COMPARATOR;
        onResume();
    }

    @OnClick(R.id.bottom_sheet_sort_by_edited)
    protected void compareByEdited() {
        mDataComparator = Note.BY_EDITED_COMPARATOR;
        onResume();
    }

    @OnClick(R.id.bottom_sheet_sort_by_name)
    protected void compareByViewed() {
        mDataComparator = Note.BY_VIEWED_COMPARATOR;
        onResume();
    }

    private Note[] getData(@NonNull Comparator<Note> comparator) {
        DatabaseHelper helper = new DatabaseHelper(this);
        Note[] data = helper.getAllItems();

        Arrays.sort(data, comparator);
        return data;
    }
}
