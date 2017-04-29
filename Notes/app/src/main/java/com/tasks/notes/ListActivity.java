package com.tasks.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.tasks.notes.helpers.DatabaseHelper;
import com.tasks.notes.helpers.ImportExportHelper;

import java.io.Serializable;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity {
    private final DatabaseHelper databaseHelper = new DatabaseHelper(this);

    @BindView(R.id.list_notes)
    RecyclerView notesList;
    @BindView(R.id.list_floating_add_button)
    FloatingActionButton floatingAddButton;
    @BindView(R.id.bottom_sheet_sort)
    BottomSheetLayout bottomSheet;

    MenuSheetView menuSheetView;

    private Comparator<Note> mDataComparator = Note.BY_CREATED_DESCENDING_COMPARATOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(toolbar);

        notesList.setLayoutManager(new LinearLayoutManager(this));

        menuSheetView = new MenuSheetView(
                this, MenuSheetView.MenuType.LIST, "Sort...", new MenuSheetView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_sheet_sort_by_name:
                        mDataComparator = Note.BY_NAME_COMPARATOR;
                        break;
                    case R.id.bottom_sheet_sort_by_created:
                        mDataComparator = Note.BY_CREATED_DESCENDING_COMPARATOR;
                        break;
                    case R.id.bottom_sheet_sort_by_edited:
                        mDataComparator = Note.BY_EDITED_DESCENDING_COMPARATOR;
                        break;
                    case R.id.bottom_sheet_sort_by_viewed:
                        mDataComparator = Note.BY_VIEWED_DESCENDING_COMPARATOR;
                        break;
                }
                if (bottomSheet.isSheetShowing()) {
                    bottomSheet.dismissSheet();
                }
                onResume();
                showFloatingButton(300);
                return true;
            }
        });
        menuSheetView.inflateMenu(R.menu.bottom_sheet_sort);

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
        final Note[] data = databaseHelper.getData(mDataComparator);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                return true;
            case R.id.menu_sort:
                hideFloatingButton(150);
                bottomSheet.showWithSheetView(menuSheetView);
                return true;
            case R.id.menu_filter:
                return true;
            case R.id.menu_import:
                return true;
            case R.id.menu_export:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showFloatingButton(int duration) {
        floatingAddButton.animate().scaleX(1).scaleY(1).setDuration(duration).start();
    }

    private void hideFloatingButton(int duration) {
        floatingAddButton.animate().scaleX(0).scaleY(0).setDuration(duration).start();
    }
}
