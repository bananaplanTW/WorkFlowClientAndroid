package com.nicloud.workflowclient.data.connectserver.worker;

import android.content.Context;

import com.nicloud.workflowclient.data.connectserver.restful.GetRequestAsyncTask;
import com.nicloud.workflowclient.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by daz on 10/20/15.
 */
public class CheckLoggedInStatusCommand implements IWorkerActionCommand, GetRequestAsyncTask.OnFinishGettingDataListener {

    public interface OnFinishCheckingLoggedInStatusListener {
        void onLoggedIn();
        void onLoggedOut(boolean isFailCausedByInternet);
    }

    private Context mContext;

    private GetRequestAsyncTask mGetRequestAsyncTask;
    private OnFinishCheckingLoggedInStatusListener mOnFinishCheckingLoggedInStatusListener;

    private long mTime = 0L;


    public CheckLoggedInStatusCommand (Context context, OnFinishCheckingLoggedInStatusListener onFinishCheckingLoggedInStatusListener) {
        mContext = context;
        mOnFinishCheckingLoggedInStatusListener = onFinishCheckingLoggedInStatusListener;
    }


    @Override
    public void execute() {
        mTime = System.currentTimeMillis();

        CheckLoggedInStatusStrategy checkLoggedInStatusStrategy = new CheckLoggedInStatusStrategy();
        mGetRequestAsyncTask = new GetRequestAsyncTask(mContext, checkLoggedInStatusStrategy, this);
        mGetRequestAsyncTask.execute();
    }


    @Override
    public void onFinishGettingData() {
        JSONObject result = mGetRequestAsyncTask.getResult();
        try {
            if (result != null && result.getString("ud") != null) {
                long waitingTime = System.currentTimeMillis() - mTime;

                if (waitingTime < LoginActivity.NICLOUD_LOGO_DISPLAYING_INTERVAL) {
                    try {
                        Thread.sleep(LoginActivity.NICLOUD_LOGO_DISPLAYING_INTERVAL - waitingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                mOnFinishCheckingLoggedInStatusListener.onLoggedIn();

            } else {
                mOnFinishCheckingLoggedInStatusListener.onLoggedOut(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            mOnFinishCheckingLoggedInStatusListener.onLoggedOut(false);
        }
    }
    @Override
    public void onFailGettingData(boolean isFailCausedByInternet) {
        mOnFinishCheckingLoggedInStatusListener.onLoggedOut(isFailCausedByInternet);
    }
}
