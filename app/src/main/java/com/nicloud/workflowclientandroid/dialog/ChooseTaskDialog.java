package com.nicloud.workflowclientandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.nicloud.workflowclientandroid.R;
import com.nicloud.workflowclientandroid.data.data.Task;
import com.nicloud.workflowclientandroid.data.data.WorkingData;
import com.nicloud.workflowclientandroid.dialog.DisplayDialogFragment.OnDialogActionListener;

/**
 * Do not use this class directly, if you want to display the dialog, use DisplayDialogFragment.
 *
 * @author Danny Lin
 * @since 2015/11/4.
 */
public class ChooseTaskDialog extends Dialog implements View.OnClickListener {

    private TextView mTaskName;

    private TextView mStartWorkButton;
    private TextView mTaskLogButton;

    private OnDialogActionListener mOnDialogActionListener;

    private Task mTask;


    public ChooseTaskDialog(Context context, String taskId, OnDialogActionListener listener) {
        super(context);
        mOnDialogActionListener = listener;
        mTask = WorkingData.getInstance(context).getScheduledTask(taskId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_choose_task);
        initialize();
    }

    private void initialize() {
        findViews();
        setupViews();
        setupButton();
    }

    private void findViews() {
        mTaskName = (TextView) findViewById(R.id.dialog_choose_task_task_name);
        mStartWorkButton = (TextView) findViewById(R.id.dialog_choose_task_start_work_button);
        mTaskLogButton = (TextView) findViewById(R.id.dialog_choose_task_log_button);
    }

    private void setupViews() {
        if (mTask == null) return;

        mTaskName.setText(mTask.name);
    }

    private void setupButton() {
        mStartWorkButton.setOnClickListener(this);
        mTaskLogButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mOnDialogActionListener == null) return;

        switch (v.getId()) {
            case R.id.dialog_choose_task_start_work_button:
                mOnDialogActionListener.onChooseTaskStartWork();
                break;

            case R.id.dialog_choose_task_log_button:
                mOnDialogActionListener.onChooseTaskLog(mTask.id);
                break;
        }
    }
}
