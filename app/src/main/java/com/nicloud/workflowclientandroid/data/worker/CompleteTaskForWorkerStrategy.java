package com.nicloud.workflowclientandroid.data.worker;

import android.util.Log;

import com.nicloud.workflowclientandroid.data.data.WorkingData;
import com.nicloud.workflowclientandroid.data.loading.LoadingDataUtils;
import com.nicloud.workflowclientandroid.data.restful.IPostRequestStrategy;
import com.nicloud.workflowclientandroid.data.utility.RestfulUtils;
import com.nicloud.workflowclientandroid.data.utility.URLUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 10/22/15.
 */
public class CompleteTaskForWorkerStrategy implements IPostRequestStrategy {

    private static final String TAG = CompleteTaskForWorkerStrategy.class.toString();

    private String mWorkerId;
    private String mTaskId;

    public CompleteTaskForWorkerStrategy (String workerId, String taskId) {
        mWorkerId = workerId;
        mTaskId = taskId;
    }

    @Override
    public JSONObject post() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-user-id", WorkingData.getUserId());
            headers.put("x-auth-token", WorkingData.getAuthToken());

            HashMap<String, String> bodies = new HashMap<>();
            bodies.put("ed", mWorkerId);
            bodies.put("td", mTaskId);

            String urlString = URLUtils.buildURLString(LoadingDataUtils.WorkingDataUrl.BASE_URL, LoadingDataUtils.WorkingDataUrl.EndPoints.COMPLETE_TASK, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    return jsonObject.getJSONObject("result");
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in CompleteTaskForWorkerStrategy()");
            e.printStackTrace();
        }
        return null;
    }
}
