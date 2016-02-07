package com.nicloud.workflowclient.backgroundtask.asyntask.tasklog;

import android.util.Log;

import com.nicloud.workflowclient.utility.utils.LoadingDataUtils;
import com.nicloud.workflowclient.backgroundtask.asyntask.restful.IPostRequestStrategy;
import com.nicloud.workflowclient.utility.utils.RestfulUtils;
import com.nicloud.workflowclient.utility.utils.URLUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 10/15/15.
 */
public class PostLogStrategy implements IPostRequestStrategy {

    private static final String TAG = PostLogStrategy.class.toString();

    private HashMap<String, String> mHeaders;
    private HashMap<String, String> mBodies;
    private String mEndPoint;

    public PostLogStrategy(String endPoint, HashMap<String, String> headers, HashMap<String, String> bodies) {
        mEndPoint = endPoint;
        mHeaders = headers;
        mBodies = bodies;
    }

    @Override
    public JSONObject post() {
        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl, mEndPoint, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, mHeaders, mBodies);
            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    return jsonObject.getJSONObject("result");
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in LoadingWorkerActivitiesStrategy()");
            e.printStackTrace();
        }
        return null;
    }
}
