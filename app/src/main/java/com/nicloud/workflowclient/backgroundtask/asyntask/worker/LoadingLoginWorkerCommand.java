package com.nicloud.workflowclient.backgroundtask.asyntask.worker;

import android.content.Context;

import com.nicloud.workflowclient.data.data.Worker;
import com.nicloud.workflowclient.main.WorkingData;
import com.nicloud.workflowclient.utility.utils.LoadingDataUtils;
import com.nicloud.workflowclient.backgroundtask.asyntask.restful.GetRequestAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by daz on 10/20/15.
 */
public class LoadingLoginWorkerCommand implements IWorkerActionCommand, GetRequestAsyncTask.OnFinishGettingDataListener {

    public interface OnLoadingLoginWorker {
        void onLoadingLoginWorkerSuccessful();
        void onLoadingLoginWorkerFailed(boolean isFailCausedByInternet);
    }

    private Context mContext;

    private GetRequestAsyncTask mGetRequestAsyncTask;
    private OnLoadingLoginWorker mOnLoadingLoginWorker;


    public LoadingLoginWorkerCommand(Context context, OnLoadingLoginWorker onLoadingLoginWorker) {
        mContext = context;
        mOnLoadingLoginWorker = onLoadingLoginWorker;
    }


    @Override
    public void execute() {
        LoadingLoginWorkerStrategy strategy = new LoadingLoginWorkerStrategy();
        mGetRequestAsyncTask = new GetRequestAsyncTask(mContext, strategy, this);
        mGetRequestAsyncTask.execute();
    }


    @Override
    public void onFinishGettingData() {
        JSONObject result = mGetRequestAsyncTask.getResult();

        if (result != null) {
            WorkingData.getInstance(mContext).setLoginWorker(Worker.retrieveWorkerFromJson(result));

            try {
                WorkingData.setMembership(result.getString("membership"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mOnLoadingLoginWorker.onLoadingLoginWorkerSuccessful();

        } else {
            mOnLoadingLoginWorker.onLoadingLoginWorkerFailed(false);
        }
    }

    @Override
    public void onFailGettingData(boolean isFailCausedByInternet) {
        mOnLoadingLoginWorker.onLoadingLoginWorkerFailed(isFailCausedByInternet);
    }
}
