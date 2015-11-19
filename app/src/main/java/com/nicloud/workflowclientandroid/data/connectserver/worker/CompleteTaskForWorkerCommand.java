package com.nicloud.workflowclientandroid.data.connectserver.worker;

import android.content.Context;

import com.nicloud.workflowclientandroid.data.connectserver.restful.PostRequestAsyncTask;


/**
 * Created by daz on 10/22/15.
 */
public class CompleteTaskForWorkerCommand implements IWorkerActionCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    public interface OnCompleteTaskForWorkerListener {
        void onFinishCompleteTask();
        void onFailCompleteTask();
    }

    private PostRequestAsyncTask mPostRequestAsyncTask;

    private OnCompleteTaskForWorkerListener mOnCompleteTaskForWorkerListener;

    private Context mContext;
    private String mWorkerId;
    private String mTaskId;

    public CompleteTaskForWorkerCommand (Context context, String workerId, String taskId,
                                         OnCompleteTaskForWorkerListener listener) {
        mContext = context;
        mWorkerId = workerId;
        mTaskId = taskId;
        mOnCompleteTaskForWorkerListener = listener;
    }


    @Override
    public void execute() {
        CompleteTaskForWorkerStrategy completeTaskForWorkerStrategy = new CompleteTaskForWorkerStrategy(mWorkerId, mTaskId);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, completeTaskForWorkerStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {
        mOnCompleteTaskForWorkerListener.onFinishCompleteTask();
    }

    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {
        mOnCompleteTaskForWorkerListener.onFailCompleteTask();
    }
}
