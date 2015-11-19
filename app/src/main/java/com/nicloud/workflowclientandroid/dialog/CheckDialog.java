package com.nicloud.workflowclientandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nicloud.workflowclientandroid.main.main.MainApplication;
import com.nicloud.workflowclientandroid.R;
import com.nicloud.workflowclientandroid.googlelocation.AddressResultReceiver;
import com.nicloud.workflowclientandroid.googlelocation.FetchAddressIntentService;
import com.nicloud.workflowclientandroid.dialog.DisplayDialogFragment.OnDialogActionListener;
import com.nicloud.workflowclientandroid.utility.Utilities;

/**
 * Dialog for checking in/out
 *
 * Do not use this class directly, if you want to display the dialog, use DisplayDialogFragment.
 *
 * @author Danny Lin
 * @since 2015/11/4.
 */
public class CheckDialog extends Dialog implements View.OnClickListener,
        AddressResultReceiver.OnReceiveListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Context mContext;

    private OnDialogActionListener mOnDialogActionListener;

    private TextView mCheckDate;
    private TextView mCheckInTime;
    private TextView mCheckOutTime;
    private TextView mCheckButton;
    private TextView mCurrentTime;
    private TextView mLocation;

    private ProgressBar mRecordLocationProgressBar;

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private AddressResultReceiver mReceiver;
    private LocationRequest mLocationRequest;

    private boolean mFirstReceiveLocation = true;


    public CheckDialog(Context context, OnDialogActionListener listener) {
        super(context);
        mContext = context;
        mOnDialogActionListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_check);
        initialize();
    }

    private void initialize() {
        findViews();
        setupViews();
        setupCheckButton();
        setupFetchingAddress();
    }

    private void findViews() {
        mCheckDate = (TextView) findViewById(R.id.check_date);
        mCheckInTime = (TextView) findViewById(R.id.check_in_time);
        mCheckOutTime = (TextView) findViewById(R.id.check_out_time);
        mCheckButton = (TextView) findViewById(R.id.check_button);
        mCurrentTime = (TextView) findViewById(R.id.check_current_time);
        mLocation = (TextView) findViewById(R.id.location_text);
        mRecordLocationProgressBar = (ProgressBar) findViewById(R.id.location_progress_bar);
    }

    private void setupViews() {
        mCheckDate.setText(Utilities.millisecondsToMMDD(mContext, System.currentTimeMillis()));
        mCurrentTime.setText(Utilities.millisecondsToHHMMA(System.currentTimeMillis()));
    }

    private void setupCheckButton() {
        mCheckButton.setOnClickListener(this);
    }

    private void setupFetchingAddress() {
        buildGoogleApiClient();
        createLocationRequest();
        mReceiver = new AddressResultReceiver(new Handler(), this);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnDialogActionListener == null) return;

        switch (v.getId()) {
            case R.id.check_button:
                mOnDialogActionListener.onCheck();

                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void startFetchAddressIntentService() {
        Intent intent = new Intent(mContext, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mReceiver);
        intent.putExtra(FetchAddressIntentService.Constants.EXTRA_LOCATION_DATA, mCurrentLocation);
        mContext.startService(intent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        if (!Geocoder.isPresent()) return;

        startFetchAddressIntentService();
    }

    @Override
    public void onReceiveSuccessful(String message) {
        if (mFirstReceiveLocation) {
            mRecordLocationProgressBar.startAnimation(MainApplication.sFadeOutAnimation);
            mLocation.startAnimation(MainApplication.sFadeInAnimation);

            mRecordLocationProgressBar.setVisibility(View.GONE);
            mLocation.setVisibility(View.VISIBLE);

            mFirstReceiveLocation = false;
        }

        mLocation.setText(message);
    }

    @Override
    public void onReceiveFailed(String message) {

    }
}
