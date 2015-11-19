package com.nicloud.workflowclientandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.nicloud.workflowclientandroid.R;
import com.nicloud.workflowclientandroid.dialog.DisplayDialogFragment.OnDialogActionListener;

/**
 * Do not use this class directly, if you want to display the dialog, use DisplayDialogFragment.
 *
 * @author Danny Lin
 * @since 2015/11/4.
 */
public class CompleteTaskDialog extends Dialog implements View.OnClickListener {

    private TextView mOkButton;
    private TextView mCancelButton;

    private OnDialogActionListener mOnDialogActionListener;

    private String mCompletedTaskId;


    public CompleteTaskDialog(Context context, String completedTaskId, OnDialogActionListener listener) {
        super(context);
        mCompletedTaskId = completedTaskId;
        mOnDialogActionListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_complete_task);
        initialize();
    }

    private void initialize() {
        findViews();
        setupButton();
    }

    private void findViews() {
        mOkButton = (TextView) findViewById(R.id.complete_task_dialog_ok_button);
        mCancelButton = (TextView) findViewById(R.id.complete_task_dialog_cancel_button);
    }

    private void setupButton() {
        mOkButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mOnDialogActionListener == null) return;

        switch (v.getId()) {
            case R.id.complete_task_dialog_ok_button:
                mOnDialogActionListener.onCompleteTaskOk(mCompletedTaskId);
                break;

            case R.id.complete_task_dialog_cancel_button:
                mOnDialogActionListener.onCompleteTaskCancel();
                break;
        }
    }
}
