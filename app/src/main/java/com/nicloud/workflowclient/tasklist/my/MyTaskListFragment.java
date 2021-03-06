package com.nicloud.workflowclient.tasklist.my;

import com.nicloud.workflowclient.backgroundtask.service.TaskService;
import com.nicloud.workflowclient.main.WorkingData;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.tasklist.main.TaskListAdapter;
import com.nicloud.workflowclient.tasklist.main.TaskListFragmentBase;
import com.nicloud.workflowclient.tasklist.main.TaskListItem;

import java.util.List;

/**
 * Created by logicmelody on 2015/12/21.
 */
public class MyTaskListFragment extends TaskListFragmentBase {

    private static final int LOADER_ID = 736;

    @Override
    public int getLoaderId() {
        return LOADER_ID;
    }

    @Override
    public String getSelection() {
        return WorkFlowContract.Task.WORKER_ID + " = ? AND " + WorkFlowContract.Task.STATUS + " != ?" ;
    }

    @Override
    public String[] getSelectionArgs() {
        return new String[] {WorkingData.getUserId(), WorkFlowContract.Task.Status.DONE};
    }

    @Override
    public TaskListAdapter getTaskListAdapter(List<TaskListItem> dataSet) {
        return new TaskListAdapter(mContext, getFragmentManager(), dataSet, true);
    }

    @Override
    public void loadTasks() {
        mContext.startService(TaskService.generateLoadMyTasksIntent(mContext, false));
    }
}
