package com.nicloud.workflowclientandroid.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.nicloud.workflowclientandroid.data.connectserver.worker.CheckInOutCommand;

/**
 * Use this class to display each dialog,
 * don't forget to pass OnDialogActionListener.
 *
 * @author Danny Lin
 * @since 2015/11/4.
 */
public class DisplayDialogFragment extends DialogFragment {

    public static final String TAG_DISPLAY_DIALOG_FRAGMENT = "tag_display_dialog_dialog_fragment";
    public static final String EXTRA_DIALOG_TYPE = "extra_dialog_type";
    public static final String EXTRA_TASK_ID = "extra_task_id";

    public static final class DialogType {
        public static final int COMPLETE_TASK = 0;
        public static final int CHOOSE_TASK = 1;
        public static final int CHECK_IN_OUT = 2;
    }

    public interface OnDialogActionListener {
        void onCompleteTaskCancel();
        void onCompleteTaskOk(String taskId);
        void onChooseTaskStartWork(String taskId);
        void onChooseTaskLog(String taskId);
        void onCheck(Location currentLocation, CheckInOutCommand.OnFinishCheckinStatusListener onFinishCheckinStatusListener);
    }

    private OnDialogActionListener mOnDialogActionListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnDialogActionListener) {
            mOnDialogActionListener = (OnDialogActionListener) activity;

        } else if (getTargetFragment() instanceof OnDialogActionListener) {
            mOnDialogActionListener = (OnDialogActionListener) getTargetFragment();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        switch (getArguments().getInt(EXTRA_DIALOG_TYPE)) {
            case DialogType.COMPLETE_TASK:
                return new CompleteTaskDialog(getActivity(), getArguments().getString(EXTRA_TASK_ID), mOnDialogActionListener);

            case DialogType.CHOOSE_TASK:
                return new ChooseTaskDialog(getActivity(), getArguments().getString(EXTRA_TASK_ID), mOnDialogActionListener);

            case DialogType.CHECK_IN_OUT:
                return new CheckInOutDialog(getActivity(), mOnDialogActionListener);

            default:
                return new CompleteTaskDialog(getActivity(), getArguments().getString(EXTRA_TASK_ID), mOnDialogActionListener);
        }
    }
}
