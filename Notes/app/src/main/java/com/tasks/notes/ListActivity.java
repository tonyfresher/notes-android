package com.tasks.notes;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.google.gson.JsonParseException;
import com.tasks.notes.adapters.NotesAdapter;
import com.tasks.notes.classes.Filter;
import com.tasks.notes.classes.Note;
import com.tasks.notes.helpers.DatabaseHelper;
import com.tasks.notes.helpers.FileSystemHelper;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_READ = 1;
    private static final int REQUEST_PERMISSION_WRITE = 2;
    private static final int RESULT_FILE_TO_READ = 42;
    private static final int RESULT_FILTER = 24;

    private final DatabaseHelper mDatabaseHelper = new DatabaseHelper(this);
    private Comparator<Note> mDataComparator = Note.BY_CREATED_DESCENDING_COMPARATOR;
    private boolean mAreNotesFiltered = false;

    @BindView(R.id.list_notes)
    RecyclerView mNotesList;
    @BindView(R.id.list_add_floating_button)
    FloatingActionButton mAddFloatingButton;
    @BindView(R.id.bottom_sheet_sort)
    BottomSheetLayout mBottomSheet;
    MenuSheetView mMenuSheetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(toolbar);

        mNotesList.setLayoutManager(new LinearLayoutManager(this));

        mMenuSheetView = new MenuSheetView(
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
                if (mBottomSheet.isSheetShowing()) {
                    mBottomSheet.dismissSheet();
                }

                Note[] notes = mDatabaseHelper.getData(mDataComparator);
                refreshList(notes);

                showFloatingButton(300);
                return true;
            }
        });
        mMenuSheetView.inflateMenu(R.menu.bottom_sheet_sort);

        mAddFloatingButton.setOnClickListener(new View.OnClickListener() {
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
        if (!mAreNotesFiltered) {
            Note[] notes = mDatabaseHelper.getData(mDataComparator);
            refreshList(notes);
        }
        mAreNotesFiltered = false;
    }

    private void refreshList(final Note[] notes) {
        NotesAdapter adapter = new NotesAdapter(notes, new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(v.getContext(), EditActivity.class);
                intent.putExtra(Note.INTENT_EXTRA, (Serializable) notes[position]);
                startActivity(intent);
            }
        });
        mNotesList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_list, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Note[] notes = mDatabaseHelper.getData(mDataComparator);
                List<Note> suitable = new ArrayList<>();
                for (Note n : notes) {
                    if (n.getTitle().contains(newText) || n.getDescription().contains(newText))
                        suitable.add(n);
                }

                refreshList(suitable.toArray(new Note[suitable.size()]));
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                return true;
            case R.id.menu_sort:
                hideFloatingButton(150);
                mBottomSheet.showWithSheetView(mMenuSheetView);
                return true;
            case R.id.menu_filter:
                Intent intent = new Intent(this, FilterActivity.class);
                startActivityForResult(intent, RESULT_FILTER);
                return true;
            case R.id.menu_import:
                requestImportFile();
                return true;
            case R.id.menu_export:
                tryExport();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case RESULT_FILE_TO_READ:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    tryImport(resultData.getData());
                }
                return;
            case RESULT_FILTER:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    Filter filter = resultData.getExtras().getParcelable(Filter.INTENT_EXTRA);

                    Note[] notes = mDatabaseHelper.getData(mDataComparator);
                    List<Note> filtered = new ArrayList<>();

                    for (Note n : notes) {
                        if (filter.check(n)) {
                            filtered.add(n);
                        }
                    }

                    refreshList(
                            filtered.toArray(new Note[filtered.size()]));
                    mAreNotesFiltered = true;
                }
                return;
        }
    }

    private void tryImport(Uri uri) {
        if (isPermissionAllowed(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            try {
                FileSystemHelper.importNotes(this, uri);
                showToast(getString(R.string.successfully_imported), Toast.LENGTH_SHORT);
            } catch (IllegalAccessException e) {
                showErrorDialog(getString(R.string.cant_read));
            } catch (IOException | JsonParseException e) {
                showErrorDialog(getString(R.string.wrong_file));
            }
        } else {
            requestPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_READ);
        }
    }

    private void tryExport() {
        if (isPermissionAllowed(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            try {
                String file =
                        FileSystemHelper.exportNotes(mDatabaseHelper.getData(mDataComparator));
                showToast(getString(R.string.successfully_exported_to) + file, Toast.LENGTH_SHORT);
            } catch (IllegalAccessException e) {
                showErrorDialog(getString(R.string.cant_write));
            } catch (IOException e) {
                showErrorDialog(getString(R.string.wrong_file));
            }
        } else {
            requestPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_WRITE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_READ:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestImportFile();
                } else {
                    showErrorDialog(getString(R.string.cant_read));
                }
                return;
            case REQUEST_PERMISSION_WRITE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tryExport();
                } else {
                    showErrorDialog(getString(R.string.cant_write));
                }
                return;
            }
        }
    }

    private void requestImportFile() {
        Intent exportIntent = new Intent(Intent.ACTION_GET_CONTENT);
        exportIntent.addCategory(Intent.CATEGORY_OPENABLE);
        exportIntent.setType("*/*");
        startActivityForResult(exportIntent, RESULT_FILE_TO_READ);
    }

    private boolean isPermissionAllowed(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission, int request) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, request);
    }

    private void showFloatingButton(int duration) {
        mAddFloatingButton.animate().scaleX(1).scaleY(1).setDuration(duration).start();
    }

    private void hideFloatingButton(int duration) {
        mAddFloatingButton.animate().scaleX(0).scaleY(0).setDuration(duration).start();
    }

    private void showToast(String message, int length) {
        Toast.makeText(this, message, length).show();
    }

    private void showErrorDialog(String message) {
        String error = getString(R.string.error);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(error)
                .setMessage(message)
                .setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}