package com.nicloud.workflowclient.detailedtask.taskinfo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.service.GeneralService;
import com.nicloud.workflowclient.data.activity.BaseData;
import com.nicloud.workflowclient.data.data.Task;
import com.nicloud.workflowclient.detailedtask.main.DetailedTaskActivity;
import com.nicloud.workflowclient.detailedtask.main.OnSwipeRefresh;
import com.nicloud.workflowclient.dialog.fragment.DatePickerFragment;
import com.nicloud.workflowclient.dialog.fragment.DisplayDialogFragment;
import com.nicloud.workflowclient.utility.utils.DbUtils;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/13.
 */
public class TaskInfoFragment extends Fragment implements OnSwipeRefresh, View.OnClickListener,
        DatePickerDialog.OnDateSetListener {

    private static final int DIALOG_REQUEST_FROM_TASK_INFO = 13;

    private Context mContext;

    private EditText mTaskDescription;
    private TextView mTaskDueDate;
    private TextView mCompleteTaskButton;
    private ImageView mRemoveDueDateButton;

    private String mTaskId;
    private Task mTask;

    private String mOriginalDescription;
    private Date mOriginalDueDate = new Date();
    private Date mCurrentDueDate = new Date();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTaskId = getArguments().getString(DetailedTaskActivity.EXTRA_TASK_ID);
        mTask = DbUtils.getTaskById(mContext, mTaskId);
        initialize();
    }

    private void initialize() {
        findViews();
        setTaskInfo();
        setupViews();
        setRemoveDueDateButtonVisibility();
    }

    private void findViews() {
        mTaskDescription = (EditText) getView().findViewById(R.id.task_description);
        mTaskDueDate = (TextView) getView().findViewById(R.id.task_due_date);
        mCompleteTaskButton = (TextView) getView().findViewById(R.id.complete_task_button);
        mRemoveDueDateButton = (ImageView) getView().findViewById(R.id.remove_due_date_button);
    }

    private void setTaskInfo() {
        if (TextUtils.isEmpty(mTask.description)) {
            mTaskDescription.setHint(mContext.getString(R.string.task_info_task_no_description));
            mOriginalDescription = "";
        } else {
            mTaskDescription.setText(mTask.description);
            mOriginalDescription = mTask.description;
        }

        if (mTask.dueDate.getTime() == -1L) {
            mTaskDueDate.setText(mContext.getString(R.string.task_info_task_no_due_date));
            mOriginalDueDate.setTime(-1L);
            mCurrentDueDate.setTime(-1L);
        } else {
            mTaskDueDate.setText(Utils.timestamp2Date(mTask.dueDate, Utils.DATE_FORMAT_YMD));
            mOriginalDueDate.setTime(mTask.dueDate.getTime());
            mCurrentDueDate.setTime(mTask.dueDate.getTime());
        }
    }

    private void setupViews() {
        mCompleteTaskButton.setOnClickListener(this);
        mTaskDueDate.setOnClickListener(this);
        mRemoveDueDateButton.setOnClickListener(this);
    }

    private void setRemoveDueDateButtonVisibility() {
        mRemoveDueDateButton.setVisibility(mCurrentDueDate.getTime() == -1L ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        updateTaskDescription();
        updateTaskDueDate();
    }

    private void updateTaskDescription() {
        String taskDescription =
                TextUtils.isEmpty(mTaskDescription.getText().toString()) ? "" : mTaskDescription.getText().toString();
        if (mOriginalDescription.equals(taskDescription)) return;

        mContext.startService(GeneralService.generateUpdateTaskDescriptionIntent(mContext, mTaskId, taskDescription));
    }

    private void updateTaskDueDate() {
        if (mCurrentDueDate.equals(mOriginalDueDate)) return;

        mContext.startService(GeneralService.
                generateUpdateTaskDueDateIntent(mContext, mTaskId, mCurrentDueDate.getTime()));
    }

    @Override
    public void swapData(List<BaseData> dataSet) {
        mTask = DbUtils.getTaskById(mContext, mTaskId);
        setTaskInfo();
    }

    @Override
    public void setSwipeRefreshLayout(boolean isRefresh) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.task_due_date:
                showDatePicker();
                break;

            case R.id.complete_task_button:
                Utils.showDialog(getFragmentManager(), DisplayDialogFragment.DialogType.COMPLETE_TASK, mTask.id);
                break;

            case R.id.remove_due_date_button:
                mCurrentDueDate.setTime(-1L);
                setRemoveDueDateButtonVisibility();
                mTaskDueDate.setText(mContext.getString(R.string.task_info_task_no_due_date));

                break;
        }
    }

    private void showDatePicker() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setTargetFragment(this, DIALOG_REQUEST_FROM_TASK_INFO);
        if (mTask.dueDate.getTime() > -1L) {
            Bundle bundle = new Bundle();
            Calendar c = Calendar.getInstance();
            c.setTime(mTask.dueDate);

            bundle.putInt(DatePickerFragment.EXTRA_DATE_YEAR, c.get(Calendar.YEAR));
            bundle.putInt(DatePickerFragment.EXTRA_DATE_MONTH, c.get(Calendar.MONTH));
            bundle.putInt(DatePickerFragment.EXTRA_DATE_DAY, c.get(Calendar.DAY_OF_MONTH));

            datePickerFragment.setArguments(bundle);
        }

        datePickerFragment.show(getChildFragmentManager(), DatePickerFragment.TAG_FRAGMENT_DATE_PICKER);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCurrentDueDate.setTime(new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime().getTime());
        setRemoveDueDateButtonVisibility();
        mTaskDueDate.setText(String.format(mContext.getString(R.string.date_format_yyyy_mm_dd),
                String.valueOf(year), String.valueOf(monthOfYear+1), String.valueOf(dayOfMonth)));
    }
}
