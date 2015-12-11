package com.nicloud.workflowclient.detailedtask.addlog;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.location.LocationServices;
import com.nicloud.workflowclient.data.connectserver.activity.LeaveAFileCommentToTaskCommand;
import com.nicloud.workflowclient.data.connectserver.tasklog.LeaveAPhotoCommentToTaskCommand;
import com.nicloud.workflowclient.data.connectserver.tasklog.LeaveATextCommentToTaskCommand;
import com.nicloud.workflowclient.data.connectserver.tasklog.OnLeaveCommentListener;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.googlelocation.CurrentAddress;
import com.nicloud.workflowclient.googlelocation.GoogleLocationUtils;
import com.nicloud.workflowclient.main.main.MainApplication;
import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.googlelocation.AddressResultReceiver;
import com.nicloud.workflowclient.googlelocation.FetchAddressIntentService;
import com.nicloud.workflowclient.serveraction.ActionService;
import com.nicloud.workflowclient.serveraction.UploadCompletedReceiver;
import com.nicloud.workflowclient.serveraction.UploadService;
import com.nicloud.workflowclient.utility.Utilities;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AddLogActivity extends AppCompatActivity implements View.OnClickListener,
        AddressResultReceiver.OnReceiveListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, OnLeaveCommentListener,
        UploadCompletedReceiver.OnUploadCompletedListener {

    private static final String TAG = "AddLogActivity";

    public static final String EXTRA_TASK_ID = "AddLogActivity_extra_task_id";

    private static final int REQUEST_IMAGE_CAPTURE = 10001;
    private static final int REQUEST_PICK_FILE = 10002;

    private ActionBar mActionBar;
    private Toolbar mToolbar;

    private UploadCompletedReceiver mUploadCompletedReceiver;

    private ImageView mWorkerPhoto;
    private EditText mEditContent;
    private TextView mLocation;
    private ProgressBar mLocationProgressBar;

    private ImageView mCameraButton;
    private ImageView mUploadButton;
    private TextView mRecordButton;

    private ProgressDialog mProgressDialog;

    private CurrentAddress mCurrentAddress;

    private String mTaskId;

    private String mCurrentPhotoPath;
    private String mCurrentFilePath;

    private boolean mFirstReceiveLocation = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_log);
        initialize();
    }

    private void initialize() {
        mTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        mUploadCompletedReceiver = new UploadCompletedReceiver(this);
        findViews();
        setupActionBar();
        setupViews();
        setupFetchingAddress();
    }

    private void findViews() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mWorkerPhoto = (ImageView) findViewById(R.id.add_log_worker_photo);
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
        if (WorkingData.getInstance(this).getLoginWorker().avatar != null) {
            mWorkerPhoto.setImageDrawable(WorkingData.getInstance(this).getLoginWorker().avatar);
        }

        mCameraButton.setOnClickListener(this);
        mUploadButton.setOnClickListener(this);
        mRecordButton.setOnClickListener(this);
    }

    private void setupFetchingAddress() {
        mCurrentAddress = new CurrentAddress(this, this, this, this);
        GoogleLocationUtils.showLocationEnabledDialog(this,
                mCurrentAddress.mLocationRequest, mCurrentAddress.mGoogleApiClient);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(UploadService.UploadAction.UPLOAD_COMPLETED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mUploadCompletedReceiver, intentFilter);

        mCurrentAddress.mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mUploadCompletedReceiver);
        if (mCurrentAddress.mGoogleApiClient.isConnected()) {
            mCurrentAddress.mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCurrentAddress.mGoogleApiClient.isConnected()) {
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
                capturePhoto();
                break;

            case R.id.add_log_upload_button:
                pickupFile();
                break;

            case R.id.add_log_button:
                String editContent = mEditContent.getText().toString();
                if (TextUtils.isEmpty(editContent)) break;

                startService(UploadService.generateUploadTextIntent(this, mTaskId, mEditContent.getText().toString()));

                mEditContent.setText("");

                break;
        }
    }

    private void displayProgressDialog(String title) {
        mProgressDialog = ProgressDialog.show(this, title, getString(R.string.add_log_wait));
    }

    private void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) == null) {
            Log.d(TAG, "No Activity to handle ACTION_IMAGE_CAPTURE intent");
        }

        File photoFile = null;

        try {
            photoFile = createImageFile();

        } catch (IOException ex) {
            Log.d(TAG, "Create image file failed");
            ex.printStackTrace();
        }

        if (photoFile != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir + "/" + timeStamp + ".jpg");

        if (!image.createNewFile()) throw new IOException();

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        return image;
    }

    private void pickupFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), REQUEST_PICK_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            Log.d(TAG, "No Activity to handle file");
            ex.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mCurrentAddress.mGoogleApiClient, mCurrentAddress.mLocationRequest, this);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mCurrentAddress.mGoogleApiClient, this);
    }

    private void startFetchAddressIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mCurrentAddress.mReceiver);
        intent.putExtra(FetchAddressIntentService.Constants.EXTRA_LOCATION_DATA, mCurrentAddress.mCurrentLocation);
        startService(intent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mCurrentAddress.mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentAddress.mCurrentLocation = location;

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
    public void onFinishLeaveComment() {
        setResult(RESULT_OK);
        mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    @Override
    public void onFailLeaveComment(boolean isFailCausedByInternet) {
        Toast.makeText(this, getString(R.string.add_log_failed), Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                onPhotoCaptured();

                break;

            case REQUEST_PICK_FILE:
                onFilePicked(data);
                displayProgressDialog(getString(R.string.add_log_uploading_file));

                break;

            default:
                break;
        }
    }

    private void onPhotoCaptured() {
        // compress photo
        File photoFile = new File(mCurrentPhotoPath.replace("file:", ""));
        Bitmap bitmap = Utilities.scaleBitmap(this, photoFile.getAbsolutePath());
        if (bitmap == null) return;

//        String ownerId = WorkingData.getUserId();
//        PhotoData photo = (PhotoData) DataFactory.genData(ownerId, BaseData.TYPE.PHOTO);
//
//        photo.time = Calendar.getInstance().getTime();
//        photo.uploader = ownerId;
//        photo.fileName = mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/') + 1);
//        photo.photo = new BitmapDrawable(getResources(), bitmap);
//        photo.filePath = Uri.parse(mCurrentPhotoPath);
//        addRecord(getSelectedWorker(), photo);

        scanPhotoToGallery();
        syncingPhotoActivity();
    }

    private void scanPhotoToGallery() {
        if (!TextUtils.isEmpty(mCurrentPhotoPath)) {  // trigger media scanner
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);

            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
        }
    }

    private void syncingPhotoActivity() {
        String realPath = mCurrentPhotoPath.substring(mCurrentPhotoPath.indexOf(':') + 1);

        startService(UploadService.generateUploadPhotoIntent(this, mTaskId, realPath));

        mCurrentPhotoPath = null;
    }

    private void onFilePicked(Intent intent) {
        Uri uri = intent.getData();
        String path = null;

        try {
            path = Utilities.getPath(this, uri);
        } catch (URISyntaxException e) {
            Log.d(TAG, "File attach failed");
            e.printStackTrace();
        }

//        if (TextUtils.isEmpty(path)) return;
//
//        String ownerId = WorkingData.getUserId();
//        FileData file = (FileData) DataFactory.genData(ownerId, BaseData.TYPE.FILE);
//
//        file.uploader = ownerId;
//        file.time = Calendar.getInstance().getTime();
//        file.fileName = path.substring(path.lastIndexOf('/') + 1);
//        file.filePath = uri;
//        addRecord(getSelectedWorker(), file);

        mCurrentFilePath = path;
        syncingFileActivity();
    }

    private void syncingFileActivity() {
        if (Utilities.isImage(mCurrentFilePath)) {
            LeaveAPhotoCommentToTaskCommand leaveAPhotoCommentToTaskCommand =
                    new LeaveAPhotoCommentToTaskCommand(this, mTaskId, mCurrentFilePath, this);
            leaveAPhotoCommentToTaskCommand.execute();
        } else {
            LeaveAFileCommentToTaskCommand leaveAFileCommentToTaskCommand =
                    new LeaveAFileCommentToTaskCommand(this, mTaskId, mCurrentFilePath, this);
            leaveAFileCommentToTaskCommand.execute();
        }

        mCurrentFilePath = null;
    }

    @Override
    public void onUploadCompletedListener(Intent intent) {
        boolean isUploadSuccessful = intent.getBooleanExtra(UploadService.ExtraKey.UPLOAD_SUCCESSFUL, false);

        if (isUploadSuccessful) {
            setResult(RESULT_OK);
        }
    }
}
