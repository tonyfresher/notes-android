package com.tasks.notes;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tasks.notes.domain.Filter;
import com.tasks.notes.helpers.ColorButtonCreator;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tasks.notes.helpers.DateFormats.*;
import static com.tasks.notes.helpers.ImportExportService.GSON_SERIALIZER;

public class FilterEditFragment extends ColorButtonCreator {

    public static final String TAG = "filter_edit_fragment";

    public static FilterEditFragment newInstance() {
        Bundle args = new Bundle();
        FilterEditFragment fragment = new FilterEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.filter_edit_name)
    EditText nameEditText;

    @BindView(R.id.filter_created_label)
    TextView createdLabel;
    @BindView(R.id.filter_edit_created_from)
    TextView createdFrom;
    @BindView(R.id.filter_edit_created_to)
    TextView createdTo;

    @BindView(R.id.filter_edit_edited_label)
    TextView editedLabel;
    @BindView(R.id.filter_edit_edited_from)
    TextView editedFrom;
    @BindView(R.id.filter_edit_edited_to)
    TextView editedTo;

    @BindView(R.id.filter_edit_viewed_label)
    TextView viewedLabel;
    @BindView(R.id.filter_edit_viewed_from)
    TextView viewedFrom;
    @BindView(R.id.filter_edit_viewed_to)
    TextView viewedTo;

    @BindView(R.id.filter_edit_colors_layout)
    LinearLayout colorButtonsLayout;
    private ImageView[] colorButtons;

    private Filter filter = new Filter();

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filter_edit, container, false);
        ButterKnife.bind(this, rootView);

        setOnLabelClickListener(createdLabel, createdFrom, createdTo);
        setOnLabelClickListener(editedLabel, editedFrom, editedTo);
        setOnLabelClickListener(viewedLabel, viewedFrom, viewedTo);

        int margin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
        colorButtons = createColorButtons(colorButtonsLayout, margin);

        return rootView;
    }

    @OnClick(R.id.filter_edit_save_button)
    protected void save() {
        String name = nameEditText.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.enter_name), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        filter.setName(name);
        saveFilterToPrefs(filter);
        ((FilterFragment) getParentFragment()).refreshSavedList();

        Toast.makeText(getContext(), getString(R.string.filter_was_saved), Toast.LENGTH_SHORT)
                .show();
    }

    @OnClick(R.id.filter_edit_apply_button)
    protected void apply() {
        ((FilterFragment) getParentFragment()).exitWithFilter(filter);
    }

    private void setOnLabelClickListener(TextView label, final TextView from, final TextView to) {
        label.setOnClickListener(v -> {
            if (from.getVisibility() == View.GONE && to.getVisibility() == View.GONE) {
                from.setVisibility(View.VISIBLE);
                to.setVisibility(View.VISIBLE);
            } else {
                from.setVisibility(View.GONE);
                to.setVisibility(View.GONE);
            }
        });
    }

    @OnClick(R.id.filter_edit_created_from)
    protected void onCreatedFromClick(View v) {
        LocalDate now = new LocalDate();
        new DatePickerDialog(v.getContext(),
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    DateTime d = new DateTime(year, month, dayOfMonth, 0, 0);
                    filter.setCreatedFrom(ISO8601_DATE_FORMAT.print(d));
                    createdFrom.setText(HUMAN_READABLE_DATE_FORMAT.print(d));
                },
                now.getYear(), now.getMonthOfYear(), now.getDayOfMonth())
                .show();
    }

    @OnClick(R.id.filter_edit_created_to)
    protected void onCreatedToClick(View v) {
        LocalDate now = new LocalDate();
        new DatePickerDialog(v.getContext(),
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    DateTime d = new DateTime(year, month, dayOfMonth, 0, 0);
                    filter.setCreatedTo(ISO8601_DATE_FORMAT.print(d));
                    createdTo.setText(HUMAN_READABLE_DATE_FORMAT.print(d));
                },
                now.getYear(), now.getMonthOfYear(), now.getDayOfMonth())
                .show();
    }

    @OnClick(R.id.filter_edit_edited_from)
    protected void onEditedFromClick(View v) {
        LocalDate now = new LocalDate();
        new DatePickerDialog(v.getContext(),
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    DateTime d = new DateTime(year, month, dayOfMonth, 0, 0);
                    filter.setEditedFrom(ISO8601_DATE_FORMAT.print(d));
                    editedFrom.setText(HUMAN_READABLE_DATE_FORMAT.print(d));
                },
                now.getYear(), now.getMonthOfYear(), now.getDayOfMonth())
                .show();
    }

    @OnClick(R.id.filter_edit_edited_to)
    protected void onEditedToClick(View v) {
        LocalDate now = new LocalDate();
        new DatePickerDialog(v.getContext(),
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    DateTime d = new DateTime(year, month, dayOfMonth, 0, 0);
                    filter.setEditedTo(ISO8601_DATE_FORMAT.print(d));
                    editedTo.setText(HUMAN_READABLE_DATE_FORMAT.print(d));
                },
                now.getYear(), now.getMonthOfYear(), now.getDayOfMonth())
                .show();
    }

    @OnClick(R.id.filter_edit_viewed_from)
    protected void onViewedFromClick(View v) {
        LocalDate now = new LocalDate();
        new DatePickerDialog(v.getContext(),
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    DateTime d = new DateTime(year, month, dayOfMonth, 0, 0);
                    filter.setViewedFrom(ISO8601_DATE_FORMAT.print(d));
                    viewedFrom.setText(HUMAN_READABLE_DATE_FORMAT.print(d));
                },
                now.getYear(), now.getMonthOfYear(), now.getDayOfMonth())
                .show();
    }

    @OnClick(R.id.filter_edit_viewed_to)
    protected void onViewedToClick(View v) {
        LocalDate now = new LocalDate();
        new DatePickerDialog(v.getContext(),
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    DateTime d = new DateTime(year, month, dayOfMonth, 0, 0);
                    filter.setViewedTo(ISO8601_DATE_FORMAT.print(d));
                    viewedTo.setText(HUMAN_READABLE_DATE_FORMAT.print(d));
                },
                now.getYear(), now.getMonthOfYear(), now.getDayOfMonth())
                .show();
    }

    @Override
    public void onColorButtonClick(View v) {
        for (ImageView b : colorButtons) {
            int color1 = ((ColorDrawable) b.getBackground()).getColor();
            if (color1 == filter.getColor()) {
                b.setImageDrawable(
                        getContext().getDrawable(R.drawable.frame));
            }
        }

        ((ImageView) v).setImageDrawable(
                getContext().getDrawable(R.drawable.frame_highlited));
        int backgroundColor = ((ColorDrawable) v.getBackground()).getColor();
        filter.setColor(backgroundColor);

        updateColorButtons(colorButtons, backgroundColor);
    }

    public void initFromFilter(Filter filter) {
        nameEditText.setText(filter.getName());
        updateColorButtons(colorButtons, filter.getColor());
        createdFrom.setText(toHumanReadableString(filter.getCreatedFrom()));
        createdFrom.setVisibility(View.VISIBLE);
        createdTo.setText(toHumanReadableString(filter.getCreatedTo()));
        createdTo.setVisibility(View.VISIBLE);
        editedFrom.setText(toHumanReadableString(filter.getEditedFrom()));
        editedFrom.setVisibility(View.VISIBLE);
        editedTo.setText(toHumanReadableString(filter.getEditedTo()));
        editedTo.setVisibility(View.VISIBLE);
        viewedFrom.setText(toHumanReadableString(filter.getViewedFrom()));
        viewedFrom.setVisibility(View.VISIBLE);
        viewedTo.setText(toHumanReadableString(filter.getViewedTo()));
        viewedTo.setVisibility(View.VISIBLE);

        this.filter = filter;
    }

    private void saveFilterToPrefs(Filter filter) {
        String json = GSON_SERIALIZER.toJson(filter);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        sharedPref.edit()
                .putString(filter.getName(), json)
                .apply();
    }

    private String toHumanReadableString(String iso8601) {
        if (iso8601 == null) return null;
        DateTime date = new DateTime(iso8601);
        return HUMAN_READABLE_DATE_FORMAT.print(date);
    }
}
