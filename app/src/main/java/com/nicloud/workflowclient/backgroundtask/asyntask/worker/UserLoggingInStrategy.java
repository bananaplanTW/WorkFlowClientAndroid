package com.nicloud.workflowclient.backgroundtask.asyntask.worker;

import android.util.Log;

import com.nicloud.workflowclient.utility.utils.LoadingDataUtils;
import com.nicloud.workflowclient.backgroundtask.asyntask.restful.IPostRequestStrategy;
import com.nicloud.workflowclient.utility.utils.RestfulUtils;
import com.nicloud.workflowclient.utility.utils.URLUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 10/20/15.
 */
public class UserLoggingInStrategy implements IPostRequestStrategy {

    private static String TAG = UserLoggingInStrategy.class.toString();
    HashMap<String, String> mBodies = new HashMap<>();

    public UserLoggingInStrategy (HashMap<String, String> bodies) {
        mBodies = bodies;
    }

    @Override
    public JSONObject post() {
        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                                                       LoadingDataUtils.WorkingDataUrl.EndPoints.LOGIN, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, null, mBodies);
            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    return jsonObject.getJSONObject("data");
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in LoadingWorkerActivitiesStrategy()");
            e.printStackTrace();
        }
        return null;
    }
}
