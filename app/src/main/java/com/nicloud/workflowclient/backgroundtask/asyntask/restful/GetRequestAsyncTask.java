package com.nicloud.workflowclient.backgroundtask.asyntask.restful;

import android.content.Context;
import android.os.AsyncTask;

import com.nicloud.workflowclient.utility.utils.RestfulUtils;

import org.json.JSONObject;

/**
 * Created by daz on 10/20/15.
 */
public class GetRequestAsyncTask extends AsyncTask<Void, Void, JSONObject> {

    public interface OnFinishGettingDataListener {
        void onFinishGettingData();
        void onFailGettingData(boolean isFailCausedByInternet);
    }

    private JSONObject mResult;
    private Context mContext;
    private IGetRequestStrategy mGetRequestStrategy;
    private OnFinishGettingDataListener mOnFinishGettingDataListener;

    public GetRequestAsyncTask (Context context, IGetRequestStrategy getRequestStrategy, OnFinishGettingDataListener onFinishGettingDataListener) {
        mContext = context;
        mGetRequestStrategy = getRequestStrategy;
        mOnFinishGettingDataListener = onFinishGettingDataListener;
    }

    public JSONObject getResult () {
        return mResult;
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        if (RestfulUtils.isConnectToInternet(mContext)) {
            return mGetRequestStrategy.get();
        } else {
            cancel(true);
        }

        return null;
    }
    @Override
    protected void onCancelled(JSONObject jsonObject) {
        super.onCancelled(jsonObject);
        mOnFinishGettingDataListener.onFailGettingData(true);
    }
    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        mResult = jsonObject;
        mOnFinishGettingDataListener.onFinishGettingData();
    }
}
