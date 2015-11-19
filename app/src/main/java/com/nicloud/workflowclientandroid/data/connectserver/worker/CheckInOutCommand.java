package com.nicloud.workflowclientandroid.data.connectserver.worker;

import android.content.Context;
import android.location.Location;

import com.nicloud.workflowclientandroid.data.connectserver.restful.PostRequestAsyncTask;

import org.json.JSONObject;

/**
 * Created by daz on 11/17/15.
 */
public class CheckInOutCommand implements IWorkerActionCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    public interface OnFinishCheckinStatusListener {
        void onCheckInOutFinished();
        void onCheckInOutFailed();
    }

    private Context mContext;
    private Location mCurrentLocation;

    private PostRequestAsyncTask mPostRequestAsyncTask;
    private OnFinishCheckinStatusListener mOnFinishCheckinStatusListener;


    public CheckInOutCommand(Context context, Location currentLoaction, OnFinishCheckinStatusListener onFinishCheckinStatusListener) {
        mContext = context;
        mCurrentLocation = currentLoaction;
        mOnFinishCheckinStatusListener = onFinishCheckinStatusListener;
    }


    @Override
    public void execute() {
        CheckInOutStrategy checkInOutStrategy = new CheckInOutStrategy(mCurrentLocation);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, checkInOutStrategy, this);
        mPostRequestAsyncTask.execute();
    }

    @Override
    public void onFinishPostingData() {
        JSONObject result = mPostRequestAsyncTask.getResult();
        if (result != null) {
            mOnFinishCheckinStatusListener.onCheckInOutFinished();
        } else {
            mOnFinishCheckinStatusListener.onCheckInOutFailed();
        }
    }
    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {
        mOnFinishCheckinStatusListener.onCheckInOutFailed();
    }
}
