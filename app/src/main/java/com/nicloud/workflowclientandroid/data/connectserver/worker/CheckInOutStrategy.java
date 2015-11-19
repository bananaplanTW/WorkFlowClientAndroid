package com.nicloud.workflowclientandroid.data.connectserver.worker;

import android.location.Location;
import android.util.Log;

import com.nicloud.workflowclientandroid.data.data.WorkingData;
import com.nicloud.workflowclientandroid.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclientandroid.data.connectserver.restful.IPostRequestStrategy;
import com.nicloud.workflowclientandroid.data.utility.RestfulUtils;
import com.nicloud.workflowclientandroid.data.utility.URLUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 11/17/15.
 */
public class CheckInOutStrategy implements IPostRequestStrategy {

    private static final String TAG = CheckInOutStrategy.class.toString();

    private Location mCurrentLocation;


    public CheckInOutStrategy(Location currentLocation) {
        mCurrentLocation = currentLocation;
    }

    @Override
    public JSONObject post() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-user-id", WorkingData.getUserId());
            headers.put("x-auth-token", WorkingData.getAuthToken());

            HashMap<String, String> bodies = new HashMap<>();
            bodies.put("lat", String.valueOf(mCurrentLocation.getLatitude()));
            bodies.put("lng", String.valueOf(mCurrentLocation.getLongitude()));

            String urlString = URLUtils.buildURLString(LoadingDataUtils.WorkingDataUrl.BASE_URL, LoadingDataUtils.WorkingDataUrl.EndPoints.CHECKIN_OUT, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    return jsonObject.getJSONObject("result");
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in AssigningTaskStrategy()");
            e.printStackTrace();
        }
        return null;
    }
}