package com.nicloud.workflowclientandroid.data.connectserver.worker;

import android.content.Context;

import com.nicloud.workflowclientandroid.data.connectserver.restful.PostRequestAsyncTask;
import com.nicloud.workflowclientandroid.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 10/20/15.
 */
public class UserLoggingInCommand implements IWorkerActionCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    public interface OnFinishLoggedInListener {
        void onLoggedInSucceed(String userId, String authToken);
        void onLoggedInFailed(boolean isFailCausedByInternet);
    }

    private Context mContext;

    private OnFinishLoggedInListener mOnFinishLoggedInListener;
    private PostRequestAsyncTask mPostRequestAsyncTask;
    private String mUsername;
    private String mPassword;

    private long mTime = 0L;


    public UserLoggingInCommand (Context context, String username, String password, OnFinishLoggedInListener onFinishLoggedInListener) {
        mContext = context;
        mUsername = username;
        mPassword = password;
        mOnFinishLoggedInListener = onFinishLoggedInListener;
    }

    @Override
    public void execute() {
        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("username", mUsername);
        bodies.put("password", mPassword);

        UserLoggingInStrategy userLoggingInStrategy = new UserLoggingInStrategy(bodies);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, userLoggingInStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {
        JSONObject result = mPostRequestAsyncTask.getResult();
        if (result != null) {
            try {
                long waitingTime = System.currentTimeMillis() - mTime;

                if (waitingTime < LoginActivity.NICLOUD_LOGO_DISPLAYING_INTERVAL) {
                    try {
                        Thread.sleep(LoginActivity.NICLOUD_LOGO_DISPLAYING_INTERVAL - waitingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                mOnFinishLoggedInListener.onLoggedInSucceed(result.getString("userId"), result.getString("authToken"));

            } catch (JSONException e) {
                e.printStackTrace();
                mOnFinishLoggedInListener.onLoggedInFailed(false);
            }
        } else {
            mOnFinishLoggedInListener.onLoggedInFailed(false);
        }
    }

    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {
        mOnFinishLoggedInListener.onLoggedInFailed(true);
    }
}
