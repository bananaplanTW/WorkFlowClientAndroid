package com.nicloud.workflowclient.cases.tasklist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nicloud.workflowclient.backgroundtask.service.TaskService;
import com.nicloud.workflowclient.cases.main.CaseFragment;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.tasklist.main.TaskListFragmentBase;

/**
 * Created by logicmelody on 2016/1/31.
 */
public class CaseTaskListFragment extends TaskListFragmentBase {

    private static final int LOADER_ID = 963;

    private String mCaseId;


    public void setCaseId(String caseId) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mCaseId = getArguments().getString(CaseFragment.EXTRA_CASE_ID);
        }

        super.onActivityCreated(savedInstanceState);
    }

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
