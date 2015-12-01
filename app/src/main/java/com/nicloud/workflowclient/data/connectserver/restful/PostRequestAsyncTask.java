package com.nicloud.workflowclient.data.connectserver.restful;

import android.content.Context;
import android.os.AsyncTask;

import com.nicloud.workflowclient.data.utility.RestfulUtils;

import org.json.JSONObject;

/**
 * Created by daz on 10/11/15.
 */
public class PostRequestAsyncTask extends AsyncTask<Void, Void, JSONObject> {

    public interface OnFinishPostingDataListener {
        void onFinishPostingData();
        void onFailPostingData(boolean isFailCausedByInternet);
    }

    private JSONObject mResult;
    private Context mContext;
    private IPostRequestStrategy mPostRequestStrategy;
    private OnFinishPostingDataListener mOnFinishPostingDataListener;

    public PostRequestAsyncTask(Context context, IPostRequestStrategy postRequestStrategy, OnFinishPostingDataListener onFinishPostingDataListener) {
        mContext = context;
        mPostRequestStrategy = postRequestStrategy;
        mOnFinishPostingDataListener = onFinishPostingDataListener;
    }


    public JSONObject getResult () {
        return mResult;
    }


    @Override
    protected JSONObject doInBackground(Void... voids) {
        if (RestfulUtils.isConnectToInternet(mContext)) {
            return mPostRequestStrategy.post();
        } else {
            cancel(true);
        }

        return null;
    }
    @Override
    protected void onCancelled(JSONObject jsonObject) {
        super.onCancelled(jsonObject);
        mOnFinishPostingDataListener.onFailPostingData(true);
    }
    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        mResult = jsonObject;
        mOnFinishPostingDataListener.onFinishPostingData();
    }
}
