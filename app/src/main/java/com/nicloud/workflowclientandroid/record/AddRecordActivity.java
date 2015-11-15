package com.nicloud.workflowclientandroid.record;

import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.nicloud.workflowclientandroid.R;
import com.nicloud.workflowclientandroid.address.AddressResultReceiver;
import com.nicloud.workflowclientandroid.address.FetchAddressIntentService;

public class AddRecordActivity extends AppCompatActivity implements View.OnClickListener,
        AddressResultReceiver.OnReceiveListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private ActionBar mActionBar;
    private Toolbar mToolbar;

    private TextView mRecordTaskName;
    private TextView mRecordCaseName;

    private EditText mRecordEditContent;
    private TextView mRecordLocation;
    private ProgressBar mRecordLocationProgressBar;

    private ImageView mRecordCameraButton;
    private ImageView mRecordUploadButton;
    private TextView mRecordButton;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private AddressResultReceiver mReceiver;

    private Animation mFadeInAnimation;
    private Animation mFadeOutAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
        initialize();
    }

    private void initialize() {
        mFadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        mFadeOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        findViews();
        setupActionBar();
        setupViews();
        setupFetchingAddress();
    }

    private void findViews() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mRecordTaskName = (TextView) findViewById(R.id.add_record_task_name);
        mRecordCaseName = (TextView) findViewById(R.id.add_record_case_name);
        mRecordEditContent = (EditText) findViewById(R.id.add_record_edit_content);
        mRecordLocation = (TextView) findViewById(R.id.location_text);
        mRecordLocationProgressBar = (ProgressBar) findViewById(R.id.location_progress_bar);
        mRecordCameraButton = (ImageView) findViewById(R.id.add_record_camera_button);
        mRecordUploadButton = (ImageView) findViewById(R.id.add_record_upload_button);
        mRecordButton = (TextView) findViewById(R.id.add_record_record_button);
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
        mRecordTaskName.setText("伺服器服務開發");
        mRecordCaseName.setText("流程管理專案");
        mRecordCameraButton.setOnClickListener(this);
        mRecordUploadButton.setOnClickListener(this);
        mRecordButton.setOnClickListener(this);
    }

    private void setupFetchingAddress() {
        buildGoogleApiClient();
        mReceiver = new AddressResultReceiver(new Handler(), this);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
            case R.id.add_record_camera_button:
                break;

            case R.id.add_record_upload_button:
                break;

            case R.id.add_record_record_button:
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (!Geocoder.isPresent()) return;

        if (mLastLocation != null) {
            startFetchAddressIntentService();
        }
    }

    private void startFetchAddressIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mReceiver);
        intent.putExtra(FetchAddressIntentService.Constants.EXTRA_LOCATION_DATA, mLastLocation);
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
    public void onReceiveSuccessful(String message) {
        mRecordLocationProgressBar.startAnimation(mFadeOutAnimation);
        mRecordLocation.startAnimation(mFadeInAnimation);

        mRecordLocationProgressBar.setVisibility(View.GONE);
        mRecordLocation.setVisibility(View.VISIBLE);

        mRecordLocation.setText(message);
    }

    @Override
    public void onReceiveFailed(String message) {

    }
}
