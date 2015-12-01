package com.nicloud.workflowclient.data.connectserver.worker;

import android.util.Log;

import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.connectserver.restful.IPostRequestStrategy;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.data.utility.RestfulUtils;
import com.nicloud.workflowclient.data.utility.URLUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 10/22/15.
 */
public class PauseTaskForWorkerStrategy implements IPostRequestStrategy {

    private static final String TAG = PauseTaskForWorkerStrategy.class.toString();

    private String mTaskId;

    public PauseTaskForWorkerStrategy(String taskId) {
        mTaskId = taskId;
    }

    @Override
    public JSONObject post() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-user-id", WorkingData.getUserId());
            headers.put("x-auth-token", WorkingData.getAuthToken());

            HashMap<String, String> bodies = new HashMap<>();
            bodies.put("td", mTaskId);

            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl, LoadingDataUtils.WorkingDataUrl.EndPoints.PAUSE_TASK, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    return jsonObject.getJSONObject("result");
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in PauseTaskForWorkerStrategy()");
            e.printStackTrace();
        }
        return null;
    }
}
