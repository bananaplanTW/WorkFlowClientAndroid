package com.nicloud.workflowclientandroid.data.worker;

import android.content.Context;

import com.nicloud.workflowclientandroid.data.restful.IPostRequestStrategy;
import com.nicloud.workflowclientandroid.data.restful.PostRequestAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by daz on 11/17/15.
 */
public class CheckinCommand implements IWorkerActionCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    public interface OnFinishCheckinStatusListener {
        void onFinished();
        void onFailed();
    }

    private Context mContext;

    private PostRequestAsyncTask mPostRequestAsyncTask;
    private OnFinishCheckinStatusListener mOnFinishCheckinStatusListener;

    public CheckinCommand (Context context, OnFinishCheckinStatusListener onFinishCheckinStatusListener) {
        mContext = context;
        mOnFinishCheckinStatusListener = onFinishCheckinStatusListener;
    }


    @Override
    public void execute() {
        CheckinStrategy checkinStrategy = new CheckinStrategy();
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, checkinStrategy, this);
        mPostRequestAsyncTask.execute();
    }



    @Override
    public void onFinishPostingData() {
        JSONObject result = mPostRequestAsyncTask.getResult();
        if (result != null) {
            mOnFinishCheckinStatusListener.onFinished();
        } else {
            mOnFinishCheckinStatusListener.onFailed();
        }
    }
    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {
        mOnFinishCheckinStatusListener.onFailed();
    }
}
