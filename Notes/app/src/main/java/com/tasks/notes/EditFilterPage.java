package com.tasks.notes;

import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tasks.notes.classes.Filter;
import com.tasks.notes.helpers.ColorsHelper;
import com.tasks.notes.helpers.DateHelper;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tasks.notes.helpers.DateHelper.*;

public class EditFilterPage extends Fragment implements
        ColorsHelper.SquareFactory {
    private Filter mFilter = new Filter();

    @BindView(R.id.filter_name)
    EditText mNameEditText;

    @BindView(R.id.filter_created_label)
    TextView mCreatedLabel;
    @BindView(R.id.filter_created_from)
    TextView mCreatedFrom;
    @BindView(R.id.filter_created_to)
    TextView mCreatedTo;

    @BindView(R.id.filter_edited_label)
    TextView mEditedLabel;
    @BindView(R.id.filter_edited_from)
    TextView mEditedFrom;
    @BindView(R.id.filter_edited_to)
    TextView mEditedTo;

    @BindView(R.id.filter_viewed_label)
    TextView mViewedLabel;
    @BindView(R.id.filter_viewed_from)
    TextView mViewedFrom;
    @BindView(R.id.filter_viewed_to)
    TextView mViewedTo;

    @BindView(R.id.filter_colors_layout)
    LinearLayout mColorsLayout;
    ImageView[] mColorSquares;

    @BindView(R.id.filter_save_button)
    Button mSaveButton;
    @BindView(R.id.filter_apply_button)
    Button mApplyButton;

    public EditFilterPage() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_filter, container, false);
        ButterKnife.bind(this, rootView);

        setOnLabelClickListener(mCreatedLabel, mCreatedFrom, mCreatedTo);
        setOnLabelClickListener(mEditedLabel, mEditedFrom, mEditedTo);
        setOnLabelClickListener(mViewedLabel, mViewedFrom, mViewedTo);

        setOnDateClickListeners();

        int margin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
        mColorSquares = ColorsHelper.makeSquares(this, mColorsLayout, margin);

        mSaveButton.setOnClickListener(v -> {
            FilterActivity context = ((FilterActivity) v.getContext());

            String name = mNameEditText.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(context, getString(R.string.enter_name), Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            mFilter.setName(name);
            context.saveFilterToPrefs(mFilter);
            context.refreshSavedList();
            Toast.makeText(context, getString(R.string.filter_was_saved), Toast.LENGTH_SHORT)
                    .show();
        });

        mApplyButton.setOnClickListener(v -> {
            FilterActivity context = ((FilterActivity) v.getContext());
            context.exitWithResult(mFilter);
        });

        return rootView;
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

    private void setOnDateClickListeners() {
        mCreatedFrom.setOnClickListener(v -> {
            LocalDate now = new LocalDate();
            new DatePickerDialog(v.getContext(),
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        DateTime d = new DateTime(year, month, dayOfMonth, 0, 0);
                        mFilter.setCreatedFrom(ISO8601_DATE_FORMAT.print(d));
                        mCreatedFrom.setText(HUMAN_READABLE_DATE_FORMAT.print(d));
                    },
                    now.getYear(), now.getMonthOfYear(), now.getDayOfMonth())
                    .show();
        });

        mCreatedTo.setOnClickListener(v -> {
            LocalDate now = new LocalDate();
            new DatePickerDialog(v.getContext(),
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        DateTime d = new DateTime(year, month, dayOfMonth, 0, 0);
                        mFilter.setCreatedTo(ISO8601_DATE_FORMAT.print(d));
                        mCreatedTo.setText(HUMAN_READABLE_DATE_FORMAT.print(d));
                    },
                    now.getYear(), now.getMonthOfYear(), now.getDayOfMonth())
                    .show();
        });

        mEditedFrom.setOnClickListener(v -> {
            LocalDate now = new LocalDate();
            new DatePickerDialog(v.getContext(),
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        DateTime d = new DateTime(year, month, dayOfMonth, 0, 0);
                        mFilter.setEditedFrom(ISO8601_DATE_FORMAT.print(d));
                        mEditedFrom.setText(HUMAN_READABLE_DATE_FORMAT.print(d));
                    },
                    now.getYear(), now.getMonthOfYear(), now.getDayOfMonth())
                    .show();
        });

        mEditedTo.setOnClickListener(v -> {
            LocalDate now = new LocalDate();
            new DatePickerDialog(v.getContext(),
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        DateTime d = new DateTime(year, month, dayOfMonth, 0, 0);
                        mFilter.setEditedTo(ISO8601_DATE_FORMAT.print(d));
                        mEditedTo.setText(HUMAN_READABLE_DATE_FORMAT.print(d));
                    },
                    now.getYear(), now.getMonthOfYear(), now.getDayOfMonth())
                    .show();
        });

        mViewedFrom.setOnClickListener(v -> {
            LocalDate now = new LocalDate();
            new DatePickerDialog(v.getContext(),
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        DateTime d = new DateTime(year, month, dayOfMonth, 0, 0);
                        mFilter.setViewedFrom(ISO8601_DATE_FORMAT.print(d));
                        mViewedFrom.setText(HUMAN_READABLE_DATE_FORMAT.print(d));
                    },
                    now.getYear(), now.getMonthOfYear(), now.getDayOfMonth())
                    .show();
        });

        mViewedTo.setOnClickListener(v -> {
            LocalDate now = new LocalDate();
            new DatePickerDialog(v.getContext(),
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        DateTime d = new DateTime(year, month, dayOfMonth, 0, 0);
                        mFilter.setViewedTo(ISO8601_DATE_FORMAT.print(d));
                        mViewedTo.setText(HUMAN_READABLE_DATE_FORMAT.print(d));
                    },
                    now.getYear(), now.getMonthOfYear(), now.getDayOfMonth())
                    .show();
        });
    }

    public ImageView makeColorSquare(final int color, final ImageView[] squares) {
        return ColorsHelper.makeSquare(getContext(), color, v -> {
            for (ImageView s : squares) {
                int color1 = ((ColorDrawable) s.getBackground()).getColor();
                if (color1 == mFilter.getColor()) {
                    s.setImageDrawable(
                            getContext().getDrawable(R.drawable.frame));
                }
            }

            ((ImageView) v).setImageDrawable(
                    getContext().getDrawable(R.drawable.frame_highlited));
            mFilter.setColor(color);

            ColorsHelper.updateSquares(getContext(), mColorSquares, color);
        });
    }

    public void initFromFilter(Filter filter) {
        mNameEditText.setText(filter.getName());
        ColorsHelper.updateSquares(getContext(), mColorSquares, filter.getColor());
        mCreatedFrom.setText(toHumanReadableString(filter.getCreatedFrom()));
        mCreatedFrom.setVisibility(View.VISIBLE);
        mCreatedTo.setText(toHumanReadableString(filter.getCreatedTo()));
        mCreatedTo.setVisibility(View.VISIBLE);
        mEditedFrom.setText(toHumanReadableString(filter.getEditedFrom()));
        mEditedFrom.setVisibility(View.VISIBLE);
        mEditedTo.setText(toHumanReadableString(filter.getEditedTo()));
        mEditedTo.setVisibility(View.VISIBLE);
        mViewedFrom.setText(toHumanReadableString(filter.getViewedFrom()));
        mViewedFrom.setVisibility(View.VISIBLE);
        mViewedTo.setText(toHumanReadableString(filter.getViewedTo()));
        mViewedTo.setVisibility(View.VISIBLE);

        mFilter = filter;
    }

    private String toHumanReadableString(String iso8601) {
        if (iso8601 == null) return null;
        DateTime date = new DateTime(iso8601);
        return HUMAN_READABLE_DATE_FORMAT.print(date);
    }
}
