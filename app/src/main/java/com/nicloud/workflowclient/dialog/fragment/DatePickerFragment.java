package com.nicloud.workflowclient.dialog.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment {

    public static final String TAG_FRAGMENT_DATE_PICKER = "tag_fragment_date_picker";

    public static final String EXTRA_DATE_YEAR = "extra_date_year";
    public static final String EXTRA_DATE_MONTH = "extra_date_month";
    public static final String EXTRA_DATE_DAY = "extra_date_day";

    private OnDateSetListener mOnDateSetListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnDateSetListener) {
            mOnDateSetListener = (OnDateSetListener) activity;

        } else if (getTargetFragment() instanceof OnDateSetListener) {
            mOnDateSetListener = (OnDateSetListener) getTargetFragment();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = getArguments() == null ?
                c.get(Calendar.YEAR) : getArguments().getInt(EXTRA_DATE_YEAR, c.get(Calendar.YEAR));

        int month = getArguments() == null ?
                c.get(Calendar.MONTH) : getArguments().getInt(EXTRA_DATE_MONTH, c.get(Calendar.MONTH));

        int day = getArguments() == null ?
                c.get(Calendar.DAY_OF_MONTH) : getArguments().getInt(EXTRA_DATE_DAY, c.get(Calendar.DAY_OF_MONTH));

        return new DatePickerDialog(getActivity(), mOnDateSetListener, year, month, day);
    }
}
