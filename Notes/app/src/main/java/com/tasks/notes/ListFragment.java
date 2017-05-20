package com.tasks.notes;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.tasks.notes.adapters.NotesAdapter;
import com.tasks.notes.classes.Filter;
import com.tasks.notes.classes.Note;
import com.tasks.notes.helpers.DatabaseHelper;
import com.tasks.notes.helpers.FileSystemHelper;
import com.tasks.notes.helpers.HandyTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListFragment extends Fragment {

    public static ListFragment newInstance() {
        Bundle args = new Bundle();
        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static final int REQUEST_PERMISSION_READ = 1;
    private static final int REQUEST_PERMISSION_WRITE = 2;
    private static final int RESULT_FILE_TO_READ = 42;
    private static final int RESULT_FILTER = 24;

    private Comparator<Note> mDataComparator = Note.BY_CREATED_DESCENDING_COMPARATOR;

    @BindView(R.id.list_notes)
    RecyclerView mNotesRecyclerView;
    @BindView(R.id.list_add_floating_button)
    FloatingActionButton mAddFloatingButton;
    @BindView(R.id.bottom_sheet_sort)
    BottomSheetLayout mBottomSheet;
    MenuSheetView mMenuSheetView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, rootView);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.list_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        setHasOptionsMenu(true);

        mNotesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mMenuSheetView = new MenuSheetView(
                getContext(), MenuSheetView.MenuType.LIST, "Sort...", item -> {
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

            HandyTask<Comparator<Note>, List<Note>> get = getDatabaseHelper().getOrderedItemsTask();
            get.setOnPostExecute(result -> {
                refreshList(result);
                return null;
            });
            get.execute(mDataComparator);

            showFloatingButton();
            return true;
        });
        mMenuSheetView.inflateMenu(R.menu.menu_sort_bottom_sheet);

        mAddFloatingButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditActivity.class);
            startActivity(intent);
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        HandyTask<Comparator<Note>, List<Note>> get = getDatabaseHelper().getOrderedItemsTask();
        get.setOnPostExecute(result -> {
            refreshList(result);
            hideKeyboard();
            return null;
        });
        get.execute(mDataComparator);
    }

    private void refreshList(List<Note> notes) {
        NotesAdapter adapter = new NotesAdapter(notes, (v, position) -> {
            Intent intent = new Intent(v.getContext(), EditActivity.class);
            intent.putExtra(Note.INTENT_EXTRA, (Serializable) notes.get(position));
            startActivity(intent);
        });
        mNotesRecyclerView.setAdapter(adapter);
    }

    private void hideKeyboard() {
        Activity a = getActivity();
        InputMethodManager imm = (InputMethodManager) a.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = a.getCurrentFocus();
        if (view == null) {
            view = new View(a);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list, menu);

        MenuItem searchView = menu.findItem(R.id.list_menu_search);
        searchView.setOnMenuItemClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, FilteredFragment.newInstance())
                    .addToBackStack(null)
                    .commit();

            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.list_menu_sort:
                hideFloatingButton();
                mBottomSheet.showWithSheetView(mMenuSheetView);
                return true;
            case R.id.list_menu_filter:
                Intent intent = new Intent(getActivity(), FilterActivity.class);
                startActivityForResult(intent, RESULT_FILTER);
                return true;
            case R.id.list_menu_import:
                requestImportFile();
                return true;
            case R.id.list_menu_export:
                tryExport();
                return true;
            case R.id.list_menu_create10000:
                insert100000();
                return true;
            case R.id.list_menu_clear_all:
                clearAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case RESULT_FILE_TO_READ:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    tryImport(resultData.getData());
                }
                return;
            case RESULT_FILTER:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    Filter resultFilter = resultData.getExtras().getParcelable(Filter.INTENT_EXTRA);

                    HandyTask<Object, List<Note>> task =
                            new HandyTask<>(params -> {
                                Filter filter = (Filter) params[0];
                                Comparator<Note> comparator = (Comparator<Note>) params[1];

                                List<Note> notes = getDatabaseHelper().getOrderedItems(comparator);
                                List<Note> filtered = new ArrayList<>();

                                for (Note n : notes) {
                                    if (filter.check(n)) {
                                        filtered.add(n);
                                    }
                                }

                                return filtered;
                            });

                    task.setOnPostExecute(result -> {
                        FilteredFragment fragment = FilteredFragment.newInstance(result);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .add(R.id.main_fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                        return null;
                    });

                    task.execute(resultFilter, mDataComparator);
                }
                return;
        }

    }

    private void tryImport(Uri uri) {
        if (isPermissionAllowed(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            HandyTask<Object, String> task = FileSystemHelper.importNotesTask();

            task.setOnPostExecute(result -> {
                showToast(result, Toast.LENGTH_SHORT);
                return null;
            });

            task.execute(getContext(), uri);
        } else {
            requestPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_READ);
        }
    }

    private void tryExport() {
        if (isPermissionAllowed(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            HandyTask<Comparator<Note>, List<Note>> get = getDatabaseHelper().getOrderedItemsTask();

            get.setOnPostExecute(result -> {
                HandyTask<Object, String> export = FileSystemHelper.exportNotesTask();
                export.setOnPostExecute(status -> {
                    showToast(status, Toast.LENGTH_SHORT);
                    return null;
                });

                export.execute(getContext(), result);
                return null;
            });

            get.execute(mDataComparator);
        } else {
            requestPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_WRITE);
        }
    }

    private void requestImportFile() {
        Intent exportIntent = new Intent(Intent.ACTION_GET_CONTENT);
        exportIntent.addCategory(Intent.CATEGORY_OPENABLE);
        exportIntent.setType("*/*");
        startActivityForResult(exportIntent, RESULT_FILE_TO_READ);
    }

    private void insert100000() {
    }

    private void clearAll() {
        HandyTask<Void, Void> task = getDatabaseHelper().dropTableTask();
        task.setOnPostExecute(result -> {
            onResume();
            return null;
        });
        task.execute();
    }

    public DatabaseHelper getDatabaseHelper() {
        return ((MainActivity) getActivity()).getDatabaseHelper();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
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

    private boolean isPermissionAllowed(String permission) {
        return ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission, int request) {
        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, request);
    }

    private void showFloatingButton() {
        mAddFloatingButton.animate()
                .scaleX(1).scaleY(1).setDuration(300)
                .start();
    }

    private void hideFloatingButton() {
        mAddFloatingButton.animate()
                .scaleX(0).scaleY(0).setDuration(150)
                .start();
    }

    private void showToast(String message, int length) {
        Toast.makeText(getContext(), message, length).show();
    }

    private void showErrorDialog(String message) {
        String error = getString(R.string.error);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(error)
                .setMessage(message)
                .setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}