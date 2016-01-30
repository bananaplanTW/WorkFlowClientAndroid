package com.nicloud.workflowclient.data.connectserver.worker;

import android.location.Location;
import android.util.Log;

import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.utility.utils.LoadingDataUtils;
import com.nicloud.workflowclient.data.connectserver.restful.IPostRequestStrategy;
import com.nicloud.workflowclient.utility.utils.RestfulUtils;
import com.nicloud.workflowclient.utility.utils.URLUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 11/17/15.
 */
public class CheckInOutStrategy implements IPostRequestStrategy {

    private static final String TAG = CheckInOutStrategy.class.toString();

    private Location mCurrentLocation;
    private String mCurrentAddress;


    public CheckInOutStrategy(Location currentLocation, String currentAddress) {
        mCurrentLocation = currentLocation;
        mCurrentAddress = currentAddress;
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
            bodies.put("ad", mCurrentAddress);

            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl, LoadingDataUtils.WorkingDataUrl.EndPoints.CHECKIN_OUT, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                Log.d(TAG, "CheckInOutStrategy responseString = " + responseString);

                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    return jsonObject.getJSONObject("result");
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in CheckInOutStrategy()");
            e.printStackTrace();
        }
        return null;
    }
}
