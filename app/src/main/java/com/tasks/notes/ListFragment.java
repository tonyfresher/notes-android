package com.tasks.notes;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.tasks.notes.helpers.AsyncTaskBuilder;
import com.tasks.notes.infrastructure.NotesAdapter;
import com.tasks.notes.domain.Filter;
import com.tasks.notes.domain.Note;
import com.tasks.notes.helpers.NotificationWrapper;
import com.tasks.notes.infrastructure.OnItemStateChangedListener;
import com.tasks.notes.storage.AsyncStorageProvider;
import com.tasks.notes.storage.DatabaseProvider;
import com.tasks.notes.helpers.ImportExportService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListFragment extends Fragment {

    public static final String TAG = "list_fragment";

    public static ListFragment newInstance() {
        Bundle args = new Bundle();
        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.list_toolbar)
    Toolbar toolbar;
    @BindView(R.id.list_notes)
    RecyclerView notesRecyclerView;
    @BindView(R.id.list_add_floating_button)
    FloatingActionButton addFloatingButton;
    @BindView(R.id.list_bottom_sheet)
    BottomSheetLayout bottomSheet;
    private MenuSheetView menuSheetView;

    private static final int REQUEST_PERMISSION_READ = 0;
    private static final int REQUEST_PERMISSION_WRITE = 1;
    private static final int RESULT_FILE_TO_READ = 2;

    private static final int NOTIFICATION_ID_FILTER = 3;
    private static final int NOTIFICATION_ID_CREATE_100000 = 4;
    private static final int NOTIFICATION_ID_REFRESH = 5;

    private Comparator<Note> notesComparator = Note.BY_CREATED_DESCENDING_COMPARATOR;
    private final ArrayList<Note> notesList = new ArrayList<>();

    private ImportExportService importExportHelper;

    private AsyncStorageProvider getDatabaseProvider() {
        return DatabaseProvider.getInstance(getContext());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        importExportHelper = ImportExportService.getInstance(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        importExportHelper.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, rootView);

        NotesAdapter adapter = new NotesAdapter(notesList, (v, position) -> {
            EditFragment fragment = EditFragment.newInstance(
                    notesList.get(position), position, new OnItemStateChangedListener() {
                        @Override
                        public void onItemChanged(Note note, int position) {
                            if (notesComparator.equals(Note.BY_NAME_COMPARATOR) ||
                                    notesComparator.equals(Note.BY_CREATED_DESCENDING_COMPARATOR)) {
                                notesList.set(position, note);
                                notesRecyclerView.getAdapter().notifyItemChanged(position);
                            }
                            refreshList();
                            notesRecyclerView.getAdapter().notifyDataSetChanged();
                        }

                        @Override
                        public void onItemRemoved(int position) {
                            notesList.remove(position);
                            notesRecyclerView.getAdapter().notifyItemRemoved(position);
                        }
                    });
            ((MainActivity) getActivity()).addFragment(fragment, EditFragment.TAG);
        });
        notesRecyclerView.setAdapter(adapter);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        refreshList();

        menuSheetView = new MenuSheetView(
                getContext(), MenuSheetView.MenuType.LIST, "Sort...", item -> {
            switch (item.getItemId()) {
                case R.id.bottom_sheet_sort_by_name:
                    notesComparator = Note.BY_NAME_COMPARATOR;
                    break;
                case R.id.bottom_sheet_sort_by_created:
                    notesComparator = Note.BY_CREATED_DESCENDING_COMPARATOR;
                    break;
                case R.id.bottom_sheet_sort_by_edited:
                    notesComparator = Note.BY_EDITED_DESCENDING_COMPARATOR;
                    break;
                case R.id.bottom_sheet_sort_by_viewed:
                    notesComparator = Note.BY_VIEWED_DESCENDING_COMPARATOR;
                    break;
            }
            if (bottomSheet.isSheetShowing()) {
                bottomSheet.dismissSheet();
            }
            showFloatingButton();
            refreshList();

            return true;
        });
        menuSheetView.inflateMenu(R.menu.menu_list_bottom_sheet);

        return rootView;
    }

    public void refreshList() {
        final NotificationWrapper notification = new NotificationWrapper(
                getContext(), NOTIFICATION_ID_REFRESH, "Refreshing", true);

        getDatabaseProvider()
                .getGetAllTask()
                .setOnPreExecute(v -> {
                    notification.start();
                    notesList.clear();
                    return null;
                })
                .setOnPostExecute(result -> {
                    notification.close();
                    notesList.addAll(result);
                    Collections.sort(notesList, notesComparator);
                    notesRecyclerView.getAdapter().notifyDataSetChanged();
                    return null;
                })
                .execute();
    }

    @OnClick(R.id.list_add_floating_button)
    protected void startEditFragment() {
        EditFragment editFragment = EditFragment.newInstance(note -> {
            notesList.add(note);
            Collections.sort(notesList, notesComparator);
            notesRecyclerView.getAdapter().notifyDataSetChanged();
        });
        ((MainActivity) getActivity()).addFragment(editFragment, EditFragment.TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list, menu);

        MenuItem searchView = menu.findItem(R.id.list_menu_search);
        searchView.setOnMenuItemClickListener(v -> {
            FilteredFragment filteredFragment = FilteredFragment.newInstance();
            ((MainActivity) getActivity()).addFragment(filteredFragment, FilteredFragment.TAG);
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.list_drawer_menu_sort:
                hideFloatingButton();
                bottomSheet.showWithSheetView(menuSheetView);
                return true;
            case R.id.list_drawer_menu_filter:
                startFilterFragment();
                return true;
            case R.id.list_drawer_menu_import:
                requestImportFile();
                return true;
            case R.id.list_drawer_menu_export:
                tryExport();
                return true;
            case R.id.list_drawer_menu_create100000:
                createNotes(100000);
                return true;
            case R.id.list_drawer_menu_clear_all:
                clearAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startFilterFragment() {
        FilterFragment filterFragment = FilterFragment.newInstance(resultFilter -> {
            final NotificationWrapper notification = new NotificationWrapper(
                    getContext(), NOTIFICATION_ID_FILTER, "Filtration", true);

            AsyncTask<Object, Integer, List<Note>> filterTask = new AsyncTaskBuilder<>(
                    params -> {
                        Filter filter = (Filter) params[0];
                        Comparator<Note> comparator = (Comparator<Note>) params[1];

                        List<Note> notes = getDatabaseProvider().getAll();
                        Collections.sort(notes, comparator);
                        int count = notes.size();

                        List<Note> filtered = new ArrayList<>();

                        for (int i = 0; i < count; i++) {
                            if (filter.check(notes.get(i))) {
                                filtered.add(notes.get(i));
                            }
                        }

                        return filtered;
                    })
                    .setOnPreExecute(v -> {
                        notification.start();
                        return null;
                    })
                    .setOnPostExecute(result -> {
                        notification.close();
                        FilteredFragment filteredFragment = FilteredFragment.newInstance(result);
                        ((MainActivity) getActivity()).addFragment(filteredFragment, FilteredFragment.TAG);
                        return null;
                    })
                    .execute(resultFilter, notesComparator);
        });

        ((MainActivity) getActivity()).addFragment(filterFragment, FilterFragment.TAG);
    }

    private void tryImport(Uri uri) {
        if (isPermissionAllowed(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            importExportHelper.sendImportMessage(uri);
        } else {
            requestPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_READ);
        }
    }

    private void tryExport() {
        if (isPermissionAllowed(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            getDatabaseProvider()
                    .getGetAllTask()
                    .setOnPostExecute(result -> {
                        importExportHelper.sendExportMessage(result);
                        return null;
                    })
                    .execute();
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

    private void createNotes(int count) {
        final NotificationWrapper notification = new NotificationWrapper(
                getContext(), NOTIFICATION_ID_CREATE_100000, "Insertion", false);

        AsyncTask<Void, Integer, Void> insert = new AsyncTaskBuilder<Void, Void>(
                params -> {
                    Note note = new Note("Foo", "Bar");

                    List<Note> notes = new ArrayList<>(count / 100);
                    for (int i = 0; i < count / 100; i++) {
                        notes.add(note);
                    }

                    for (int i = 0; i <= 100; i++) {
                        for (int j = 0; j < notes.size(); j++) {
                            notes.get(j).setDescription(Integer.toString(j));
                        }
                        getDatabaseProvider().saveMany(notes);
                        notification.update(i);
                    }

                    return null;
                })
                .setOnPreExecute(v -> {
                    notification.start();
                    return null;
                })
                .setOnPostExecute(result -> {
                    notification.complete(getString(R.string.completed));
                    refreshList();
                    return null;
                })
                .execute();
    }

    private void clearAll() {
        getDatabaseProvider()
                .getDeleteAllTask()
                .setOnPostExecute(result -> {
                    refreshList();
                    return null;
                })
                .execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == RESULT_FILE_TO_READ &&
                resultCode == Activity.RESULT_OK &&
                resultData != null) {
            tryImport(resultData.getData());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
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
        addFloatingButton.animate()
                .scaleX(1).scaleY(1).setDuration(300)
                .start();
    }

    private void hideFloatingButton() {
        addFloatingButton.animate()
                .scaleX(0).scaleY(0).setDuration(150)
                .start();
    }

    private void showErrorDialog(String message) {
        String error = getString(R.string.error);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(error)
                .setMessage(message)
                .setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}