package com.nicloud.workflowclient.googlelocation;

import android.content.Context;
import android.location.Location;
import android.os.Handler;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by logicmelody on 2015/12/2.
 */
public class CurrentAddress {

    public GoogleApiClient mGoogleApiClient;
    public Location mCurrentLocation;
    public LocationRequest mLocationRequest;
    public AddressResultReceiver mReceiver;


    public CurrentAddress(Context context, AddressResultReceiver.OnReceiveListener onReceiveListener,
                          GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                          GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mReceiver = new AddressResultReceiver(new Handler(), onReceiveListener);
    }
}
