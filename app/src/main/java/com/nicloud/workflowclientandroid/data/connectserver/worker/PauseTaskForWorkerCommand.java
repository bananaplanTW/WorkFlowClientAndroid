package com.nicloud.workflowclientandroid.data.connectserver.worker;

import android.content.Context;

import com.nicloud.workflowclientandroid.data.connectserver.restful.PostRequestAsyncTask;


/**
 * Created by daz on 10/22/15.
 */
public class PauseTaskForWorkerCommand implements IWorkerActionCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    public interface OnPauseTaskForWorkerListener {
        void onFinishPauseTask();
        void onFailPauseTask();
    }

    private PostRequestAsyncTask mPostRequestAsyncTask;

    private OnPauseTaskForWorkerListener mOnPauseTaskForWorkerListener;

    private Context mContext;
    private String mTaskId;


    public PauseTaskForWorkerCommand(Context context, String taskId, OnPauseTaskForWorkerListener listener) {
        mContext = context;
        mTaskId = taskId;
        mOnPauseTaskForWorkerListener = listener;
    }

    @Override
    public void execute() {
        PauseTaskForWorkerStrategy pauseTaskForWorkerStrategy = new PauseTaskForWorkerStrategy(mTaskId);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, pauseTaskForWorkerStrategy, this);
        mPostRequestAsyncTask.execute();
    }

    @Override
    public void onFinishPostingData() {
        mOnPauseTaskForWorkerListener.onFinishPauseTask();
    }

    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {
        mOnPauseTaskForWorkerListener.onFailPauseTask();
    }
}
