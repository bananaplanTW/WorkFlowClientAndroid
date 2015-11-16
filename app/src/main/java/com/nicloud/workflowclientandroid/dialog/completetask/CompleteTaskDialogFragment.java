package com.nicloud.workflowclientandroid.dialog.completetask;

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
public class CompleteTaskDialogFragment extends DialogFragment {

    public static final String TAG_COMPLETE_TASK_DIALOG = "tag_complete_task_dialog";

    public interface OnCompleteTaskActionListener {
        void onCompleteTaskCancel();
        void onCompleteTaskOk();
    }

    private OnCompleteTaskActionListener mOnCompleteTaskActionListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnCompleteTaskActionListener) {
            mOnCompleteTaskActionListener = (OnCompleteTaskActionListener) activity;

        } else if (getTargetFragment() instanceof OnCompleteTaskActionListener) {
            mOnCompleteTaskActionListener = (OnCompleteTaskActionListener) getTargetFragment();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new CompleteTaskDialog(getActivity(), mOnCompleteTaskActionListener);
    }
}
