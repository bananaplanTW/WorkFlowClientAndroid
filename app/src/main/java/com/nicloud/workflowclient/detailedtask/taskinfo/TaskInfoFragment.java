package com.nicloud.workflowclient.detailedtask.taskinfo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.activity.BaseData;
import com.nicloud.workflowclient.data.data.data.Task;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.detailedtask.DetailedTaskActivity;
import com.nicloud.workflowclient.detailedtask.OnSwipeRefresh;

import java.util.List;

/**
 * Created by logicmelody on 2016/1/13.
 */
public class TaskInfoFragment extends Fragment implements OnSwipeRefresh {

    private Context mContext;

    private TextView mTaskDescription;

    private Task mTask;


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
        String taskId = getArguments().getString(DetailedTaskActivity.EXTRA_TASK_ID);
        mTask = WorkingData.getInstance(mContext).getTask(taskId);
        initialize();
    }

    private void initialize() {
        findViews();
        setTaskInfo();
    }

    private void findViews() {
        mTaskDescription = (TextView) getView().findViewById(R.id.task_description);
    }

    private void setTaskInfo() {
        mTaskDescription.setText(mTask.description);

    }

    @Override
    public void swapData(List<BaseData> dataSet) {

    }

    @Override
    public void setSwipeRefreshLayout(boolean isRefresh) {

    }
}
