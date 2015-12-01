package com.nicloud.workflowclient.data.connectserver.activity;

import android.util.Log;

import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.utility.RestfulUtils;
import com.nicloud.workflowclient.data.utility.URLUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 10/11/15.
 */
public class LoadingWorkerActivitiesStrategy implements ILoadingActivitiesStrategy {
    private static final String TAG = LoadingWorkerActivitiesStrategy.class.toString();

    private String mWorkerId;
    private int mLimit;
    private ActivityCategory activityCategory = ActivityCategory.WORKER;

    public LoadingWorkerActivitiesStrategy(String workerId, int limit) {
        mWorkerId = workerId;
        mLimit = limit;
    }

    @Override
    public JSONArray load() {
        try {
            HashMap<String, String> queries = new HashMap<>();
            queries.put("employeeId", mWorkerId);
            queries.put("limit", "" + mLimit);
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl, LoadingDataUtils.WorkingDataUrl.EndPoints.WORKER_ACTIVITIES, queries);
            String responseJSONString = RestfulUtils.getJsonStringFromUrl(urlString);
            JSONObject responseJSON = new JSONObject(responseJSONString);
            if (responseJSON.getString("status").equals("success")) {
                return responseJSON.getJSONArray("result");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception in LoadingWorkerActivitiesStrategy()");
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public ActivityCategory getCategory() {
        return activityCategory;
    }
}
