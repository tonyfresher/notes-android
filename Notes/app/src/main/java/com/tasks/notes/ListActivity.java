package com.tasks.notes;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.tasks.notes.helpers.DatabaseHelper;
import com.tasks.notes.helpers.ImportExportHelper;

import java.io.Serializable;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ = 101;
    private static final int MY_PERMISSIONS_REQUEST_WRITE = 102;
    private static final int READ_REQUEST_CODE = 24;
    private final DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private Comparator<Note> mDataComparator = Note.BY_CREATED_DESCENDING_COMPARATOR;
    private String notesFile;

    @BindView(R.id.list_notes)
    RecyclerView notesList;
    @BindView(R.id.list_floating_add_button)
    FloatingActionButton floatingAddButton;
    @BindView(R.id.bottom_sheet_sort)
    BottomSheetLayout bottomSheet;
    MenuSheetView menuSheetView;

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
                Intent exportIntent = new Intent(Intent.ACTION_GET_CONTENT);
                exportIntent.addCategory(Intent.CATEGORY_OPENABLE);
                exportIntent.setType("application/json");
                startActivityForResult(exportIntent, READ_REQUEST_CODE);
                return true;
            case R.id.menu_export:
                if (!isPermissionAllowed(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestPermission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE);
                } else {
                    ImportExportHelper.exportNotes(this, databaseHelper.getData(mDataComparator));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK
                && resultData != null) {
            notesFile = resultData.getData().getPath();

            if (!isPermissionAllowed(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_READ);
            } else {
                Note[] notes = ImportExportHelper.importNotes(this, notesFile);
                for (Note note : notes) {
                    databaseHelper.insert(note);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImportExportHelper.exportNotes(this, databaseHelper.getData(mDataComparator));
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_READ:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Note[] notes = ImportExportHelper.importNotes(this, notesFile);
                    for (Note note : notes) {
                        databaseHelper.insert(note);
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    private boolean isPermissionAllowed(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission, int request) {
        if (!isPermissionAllowed(permission)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permission},
                    request);
        }
    }

    private void showFloatingButton(int duration) {
        floatingAddButton.animate().scaleX(1).scaleY(1).setDuration(duration).start();
    }

    private void hideFloatingButton(int duration) {
        floatingAddButton.animate().scaleX(0).scaleY(0).setDuration(duration).start();
    }
}
