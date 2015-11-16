package com.nicloud.workflowclientandroid.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Use this class to display CompleteTaskDialog,
 * don't forget to pass OnConfirmDialogActionListener.
 *
 * @author Danny Lin
 * @since 2015/11/4.
 */
public class DisplayDialogFragment extends DialogFragment {

    public static final String TAG_DISPLAY_DIALOG_FRAGMENT = "tag_display_dialog_dialog_fragment";
    public static final String EXTRA_DIALOG_TYPE = "extra_dialog_type";

    public static final class DialogType {
        public static final int COMPLETE_TASK = 0;
        public static final int CHOOSE_TASK = 1;
        public static final int CHECK_IN_OUT = 2;
    }

    public interface OnCompleteTaskActionListener {
        void onCompleteTaskCancel();
        void onCompleteTaskOk();
    }

    private OnCompleteTaskActionListener mOnCompleteTaskActionListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        switch (getArguments().getInt(EXTRA_DIALOG_TYPE)) {
            case DialogType.COMPLETE_TASK:
                if (activity instanceof OnCompleteTaskActionListener) {
                    mOnCompleteTaskActionListener = (OnCompleteTaskActionListener) activity;

                } else if (getTargetFragment() instanceof OnCompleteTaskActionListener) {
                    mOnCompleteTaskActionListener = (OnCompleteTaskActionListener) getTargetFragment();
                }
                break;

            case DialogType.CHOOSE_TASK:
                if (activity instanceof OnCompleteTaskActionListener) {
                    mOnCompleteTaskActionListener = (OnCompleteTaskActionListener) activity;

                } else if (getTargetFragment() instanceof OnCompleteTaskActionListener) {
                    mOnCompleteTaskActionListener = (OnCompleteTaskActionListener) getTargetFragment();
                }
                break;

            case DialogType.CHECK_IN_OUT:
                if (activity instanceof OnCompleteTaskActionListener) {
                    mOnCompleteTaskActionListener = (OnCompleteTaskActionListener) activity;

                } else if (getTargetFragment() instanceof OnCompleteTaskActionListener) {
                    mOnCompleteTaskActionListener = (OnCompleteTaskActionListener) getTargetFragment();
                }
                break;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        switch (getArguments().getInt(EXTRA_DIALOG_TYPE)) {
            case DialogType.COMPLETE_TASK:
                return new CompleteTaskDialog(getActivity(), mOnCompleteTaskActionListener);

            case DialogType.CHOOSE_TASK:
                return null;

            case DialogType.CHECK_IN_OUT:
                return null;

            default:
                return new CompleteTaskDialog(getActivity(), mOnCompleteTaskActionListener);
        }
    }
}
