package com.nicloud.workflowclientandroid.data.connectserver.worker;

import android.util.Log;

import com.nicloud.workflowclientandroid.data.data.data.WorkingData;
import com.nicloud.workflowclientandroid.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclientandroid.data.connectserver.restful.IGetRequestStrategy;
import com.nicloud.workflowclientandroid.data.utility.RestfulUtils;
import com.nicloud.workflowclientandroid.data.utility.URLUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 10/20/15.
 */
public class CheckLoggedInStatusStrategy implements IGetRequestStrategy {

    private static final String TAG = CheckLoggedInStatusStrategy.class.toString();

    @Override
    public JSONObject get() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-user-id", WorkingData.getUserId());
            headers.put("x-auth-token", WorkingData.getAuthToken());

            String urlString = URLUtils.buildURLString(LoadingDataUtils.WorkingDataUrl.BASE_URL, LoadingDataUtils.WorkingDataUrl.EndPoints.LOGIN_STATUS, null);
            String responseJSONString = RestfulUtils.restfulGetRequest(urlString, headers);
            JSONObject responseJSON = new JSONObject(responseJSONString);
            if (responseJSON.getString("status").equals("success")) {
                return responseJSON.getJSONObject("result");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception in CheckLoggedInStatusStrategy()");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
