package com.nicloud.workflowclient.data.connectserver.activity;

import android.util.Log;

import com.nicloud.workflowclient.utility.utils.LoadingDataUtils;
import com.nicloud.workflowclient.utility.utils.RestfulUtils;
import com.nicloud.workflowclient.utility.utils.URLUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 10/11/15.
 */
public class LoadingTaskActivitiesStrategy implements ILoadingActivitiesStrategy {
    private static final String TAG = LoadingTaskActivitiesStrategy.class.toString();

    private String mTaskId;
    private int mLimit;
    private ActivityCategory activityCategory = ActivityCategory.TASK;

    public LoadingTaskActivitiesStrategy(String taskId, int limit) {
        mTaskId = taskId;
        mLimit = limit;
    }

    @Override
    public JSONArray load() {
        try {
            HashMap<String, String> queries = new HashMap<>();
            queries.put("taskId", mTaskId);
            queries.put("limit", "" + mLimit);
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl, LoadingDataUtils.WorkingDataUrl.EndPoints.TASK_ACTIVITIES, queries);
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
