package com.tasks.notes;

import android.app.DatePickerDialog;
import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tasks.notes.helpers.DateHelper.*;

public class CreateFilterPage extends Fragment implements
        ColorsHelper.SquareFactory {
    Filter mFilter = new Filter();

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

    public CreateFilterPage() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_filter, container, false);
        ButterKnife.bind(this, rootView);

        setOnLabelClickListener(mCreatedLabel, mCreatedFrom, mCreatedTo);
        setOnLabelClickListener(mEditedLabel, mEditedFrom, mEditedTo);
        setOnLabelClickListener(mViewedLabel, mViewedFrom, mViewedTo);

        setOnDateClickListeners();

        int margin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
        mColorSquares = ColorsHelper.makeSquares(this, mColorsLayout, margin);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterActivity context = ((FilterActivity) v.getContext());

                String name = mNameEditText.getText().toString();
                while (name.equals("") || context.getPreferences(Context.MODE_PRIVATE).getAll()
                        .containsKey(name)) {
                    Toast.makeText(context, getString(R.string.enter_unique_name), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                mFilter.setName(name);
                context.saveFilterToPrefs(mFilter);
                context.refreshSavedList();
                Toast.makeText(context, getString(R.string.filter_was_saved), Toast.LENGTH_SHORT)
                        .show();
            }
        });

        mApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterActivity context = ((FilterActivity) v.getContext());
                context.exitWithResult(mFilter);
            }
        });

        return rootView;
    }

    private void setOnLabelClickListener(TextView label, final TextView from, final TextView to) {
        label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.getVisibility() == View.GONE && to.getVisibility() == View.GONE) {
                    from.setVisibility(View.VISIBLE);
                    to.setVisibility(View.VISIBLE);
                } else {
                    from.setVisibility(View.GONE);
                    to.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setOnDateClickListeners() {
        mCreatedFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new OnCreatedFromListener(),
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        mCreatedTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new OnCreatedToListener(),
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        mEditedFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new OnEditedFromListener(),
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        mEditedTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new OnEditedToListener(),
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        mViewedFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new OnViewedFromListener(),
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        mViewedTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new OnViewedToListener(),
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
    }

    public ImageView makeColorSquare(final int color, final ImageView[] squares) {
        ImageView square = ColorsHelper.makeSquare(getContext(), color, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView s : squares) {
                    int color = ((ColorDrawable) s.getBackground()).getColor();
                    if (color == mFilter.getColor()) {
                        s.setImageDrawable(
                                getContext().getDrawable(R.drawable.frame));
                    }
                }

                ((ImageView) v).setImageDrawable(
                        getContext().getDrawable(R.drawable.frame_highlited));
                mFilter.setColor(color);

                ColorsHelper.updateSquares(getContext(), mColorSquares, color);
            }
        });

        return square;
    }

    public class OnCreatedFromListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            Date date = c.getTime();

            mFilter.setCreatedFrom(ISO8601_DATE_FORMAT.format(c.getTime()));
            mCreatedFrom.setText(HUMAN_READABLE_DATE_FORMAT.format(date));
        }
    }

    public class OnCreatedToListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            Date date = c.getTime();

            mFilter.setCreatedTo(ISO8601_DATE_FORMAT.format(c.getTime()));
            mCreatedTo.setText(HUMAN_READABLE_DATE_FORMAT.format(date));
        }
    }

    public class OnEditedFromListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            Date date = c.getTime();

            mFilter.setEditedFrom(ISO8601_DATE_FORMAT.format(date));
            mEditedFrom.setText(HUMAN_READABLE_DATE_FORMAT.format(date));
        }
    }

    public class OnEditedToListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            Date date = c.getTime();

            mFilter.setEditedTo(ISO8601_DATE_FORMAT.format(date));
            mEditedTo.setText(HUMAN_READABLE_DATE_FORMAT.format(date));
        }
    }

    public class OnViewedFromListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            Date date = c.getTime();

            mFilter.setViewedFrom(ISO8601_DATE_FORMAT.format(date));
            mViewedFrom.setText(HUMAN_READABLE_DATE_FORMAT.format(date));
        }
    }

    public class OnViewedToListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            Date date = c.getTime();

            mFilter.setViewedTo(ISO8601_DATE_FORMAT.format(date));
            mViewedTo.setText(HUMAN_READABLE_DATE_FORMAT.format(date));
        }
    }
}
