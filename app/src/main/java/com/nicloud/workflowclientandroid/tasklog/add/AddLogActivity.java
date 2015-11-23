package com.nicloud.workflowclientandroid.tasklog.add;

import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nicloud.workflowclientandroid.data.connectserver.tasklog.LeaveATextCommentToTaskCommand;
import com.nicloud.workflowclientandroid.main.main.MainApplication;
import com.nicloud.workflowclientandroid.R;
import com.nicloud.workflowclientandroid.googlelocation.AddressResultReceiver;
import com.nicloud.workflowclientandroid.googlelocation.FetchAddressIntentService;
import com.nicloud.workflowclientandroid.utility.Utilities;


public class AddLogActivity extends AppCompatActivity implements View.OnClickListener,
        AddressResultReceiver.OnReceiveListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        LeaveATextCommentToTaskCommand.OnLeaveATextCommentListener {

    public static final String EXTRA_TASK_ID = "AddLogActivity_extra_task_id";

    private ActionBar mActionBar;
    private Toolbar mToolbar;

    private EditText mEditContent;
    private TextView mLocation;
    private ProgressBar mLocationProgressBar;

    private ImageView mCameraButton;
    private ImageView mUploadButton;
    private TextView mRecordButton;

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private AddressResultReceiver mReceiver;
    private LocationRequest mLocationRequest;

    private String mTaskId;

    private boolean mFirstReceiveLocation = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_log);
        initialize();
    }

    private void initialize() {
        mTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        findViews();
        setupActionBar();
        setupViews();
        setupFetchingAddress();
    }

    private void findViews() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mEditContent = (EditText) findViewById(R.id.add_log_edit_content);
        mLocation = (TextView) findViewById(R.id.location_text);
        mLocationProgressBar = (ProgressBar) findViewById(R.id.location_progress_bar);
        mCameraButton = (ImageView) findViewById(R.id.add_log_camera_button);
        mUploadButton = (ImageView) findViewById(R.id.add_log_upload_button);
        mRecordButton = (TextView) findViewById(R.id.add_log_button);
    }

    private void setupActionBar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViews() {
        mCameraButton.setOnClickListener(this);
        mUploadButton.setOnClickListener(this);
        mRecordButton.setOnClickListener(this);
    }

    private void setupFetchingAddress() {
        buildGoogleApiClient();
        createLocationRequest();
        mReceiver = new AddressResultReceiver(new Handler(), this);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_log_camera_button:
                break;

            case R.id.add_log_upload_button:
                break;

            case R.id.add_log_button:
                String editContent = mEditContent.getText().toString();

                if (TextUtils.isEmpty(editContent)) break;

                LeaveATextCommentToTaskCommand leaveATextCommentToTaskCommand =
                        new LeaveATextCommentToTaskCommand(this, mTaskId, editContent, this);
                leaveATextCommentToTaskCommand.execute();

                mEditContent.setText("");

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
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mReceiver);
        intent.putExtra(FetchAddressIntentService.Constants.EXTRA_LOCATION_DATA, mCurrentLocation);
        startService(intent);
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
            mLocationProgressBar.startAnimation(MainApplication.sFadeOutAnimation);
            mLocation.startAnimation(MainApplication.sFadeInAnimation);

            mLocationProgressBar.setVisibility(View.GONE);
            mLocation.setVisibility(View.VISIBLE);

            mFirstReceiveLocation = false;
        }

        mLocation.setText(message);
    }

    @Override
    public void onReceiveFailed(String message) {

    }

    @Override
    public void onFinishLeaveATextComment() {
        setResult(RESULT_OK);
        Toast.makeText(this, getString(R.string.add_log_complete_text), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailLeaveATextComment(boolean isFailCausedByInternet) {

    }
}
