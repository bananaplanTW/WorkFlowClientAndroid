package com.nicloud.workflowclient.googlelocation;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.nicloud.workflowclient.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by logicmelody on 2015/11/13.
 */
public class FetchAddressIntentService extends IntentService {

    private static final String TAG = "FetchAddressIS";

    public static final class Constants {
        public static final int RESULT_SUCCESS = 0;
        public static final int RESULT_FAILURE = 1;
        public static final String PACKAGE_NAME = "com.nicloud.workflowclientandroid";
        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
        public static final String KEY_RESULT_DATA = PACKAGE_NAME + ".RESULT_DATA_KEY";
        public static final String EXTRA_LOCATION_DATA = PACKAGE_NAME + ".EXTRA_LOCATION_DATA";
    }

    private ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(Constants.EXTRA_LOCATION_DATA);
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        // Make sure that the location data was really sent over through an extra. If it wasn't,
        // send an error error message and return.
        if (location == null) {
            errorMessage = getString(R.string.no_location_data_provided);
            Log.wtf(TAG, errorMessage);
            deliverResultToReceiver(Constants.RESULT_FAILURE, errorMessage);

            return;
        }

        // Errors could still arise from using the Geocoder (for example, if there is no
        // connectivity, or if the Geocoder is given illegal location data). Or, the Geocoder may
        // simply not have an address for a location. In all these cases, we communicate with the
        // receiver using a resultCode indicating failure. If an address is found, we use a
        // resultCode indicating success.

        // The Geocoder used in this sample. The Geocoder's responses are localized for the given
        // Locale, which represents a specific geographical or linguistic region. Locales are used
        // to alter the presentation of information such as numbers or dates to suit the conventions
        // in the region they describe.
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses = null;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            // In this sample, we get just a single address.
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }

            deliverResultToReceiver(Constants.RESULT_FAILURE, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using {@code getAddressLine},
            // join them, and send them to the thread. The {@link android.location.address}
            // class provides other options for fetching address details that you may prefer
            // to use. Here are some examples:
            // getLocality() ("Mountain View", for example)
            // getAdminArea() ("CA", for example)
            // getPostalCode() ("94043", for example)
            // getCountryCode() ("US", for example)
            // getCountryName() ("United States", for example)

            StringBuilder addressString = new StringBuilder();
            addressString
                    .append(address.getAdminArea())
                    .append(address.getLocality())
                    .append(address.getThoroughfare());

            Log.d(TAG, "CountryCode = " + address.getCountryCode());  // TW
            Log.d(TAG, "CountryName = " + address.getCountryName());  // 台灣
            Log.d(TAG, "PostalCode = " + address.getPostalCode());  // 104
            Log.d(TAG, "AdminArea = " + address.getAdminArea());  // 台北市
            Log.d(TAG, "Locality = " + address.getLocality());  // 中山區
            Log.d(TAG, "Thoroughfare = " + address.getThoroughfare()); // 民生西路3巷
            Log.d(TAG, "SubThoroughfare = " + address.getSubThoroughfare());  // 9號

            deliverResultToReceiver(Constants.RESULT_SUCCESS, addressString.toString());
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_RESULT_DATA, message);
        mReceiver.send(resultCode, bundle);
    }
}
