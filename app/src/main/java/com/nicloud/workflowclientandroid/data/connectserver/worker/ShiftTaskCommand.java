package com.nicloud.workflowclientandroid.data.connectserver.worker;

import android.content.Context;
import android.location.Location;

import com.nicloud.workflowclientandroid.data.connectserver.restful.PostRequestAsyncTask;

import org.json.JSONObject;

/**
 * Created by daz on 11/17/15.
 */
public class ShiftTaskCommand implements IWorkerActionCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    public interface OnFinishShiftTaskListener {
        void onShiftTaskFinished();
        void onShiftTaskFailed();
    }

    private Context mContext;

    private String mShiftTaskId;

    private PostRequestAsyncTask mPostRequestAsyncTask;
    private OnFinishShiftTaskListener mOnFinishShiftTaskListener;


    public ShiftTaskCommand(Context context, String shiftTaskId, OnFinishShiftTaskListener listener) {
        mContext = context;
        mShiftTaskId = shiftTaskId;
        mOnFinishShiftTaskListener = listener;
    }

    @Override
    public void execute() {
        ShiftTaskStrategy shiftTaskStrategy = new ShiftTaskStrategy(mShiftTaskId);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, shiftTaskStrategy, this);
        mPostRequestAsyncTask.execute();
    }

    @Override
    public void onFinishPostingData() {
        JSONObject result = mPostRequestAsyncTask.getResult();
        if (result != null) {
            mOnFinishShiftTaskListener.onShiftTaskFinished();
        } else {
            mOnFinishShiftTaskListener.onShiftTaskFailed();
        }
    }

    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {
        mOnFinishShiftTaskListener.onShiftTaskFailed();
    }
}
