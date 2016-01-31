package com.nicloud.workflowclient.cases.tasklist;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nicloud.workflowclient.backgroundtask.service.TaskService;
import com.nicloud.workflowclient.cases.main.CaseFragment;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.tasklist.main.TaskListFragmentBase;

/**
 * Created by logicmelody on 2016/1/31.
 */
public class CaseTaskListFragment extends TaskListFragmentBase {

    private static final int LOADER_ID = 963;

    private String mCaseId;


    public void setCaseId(String caseId) {
        mCaseId = caseId;
        mSelectionArgs[0] = mCaseId;
        mContext.startService(TaskService.generateLoadCaseTasksIntent(mContext, mCaseId, true));
        getLoaderManager().restartLoader(LOADER_ID, null, this);
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
        return WorkFlowContract.Task.CASE_ID + " = ? AND " + WorkFlowContract.Task.STATUS + " != ?" ;
    }

    @Override
    public String[] getSelectionArgs() {
        return new String[] {mCaseId, WorkFlowContract.Task.Status.DONE};
    }

    @Override
    public void loadTasks() {
        mContext.startService(TaskService.generateLoadCaseTasksIntent(mContext, mCaseId, false));
    }
}
