package com.nicloud.workflowclient.tasklist.my;

import android.content.Context;

import com.nicloud.workflowclient.backgroundtask.service.TaskService;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.tasklist.main.TaskListFragmentBase;

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
        return WorkFlowContract.Task.WORKER_ID + " = ?";
    }

    @Override
    public String[] getSelectionArgs() {
        return new String[] {WorkingData.getUserId()};
    }

    @Override
    public void loadTasks(Context context) {
        context.startService(TaskService.generateLoadMyTasksIntent(context, false));
    }
}
