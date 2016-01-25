package com.nicloud.workflowclient.detailedtask.filelog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.activity.BaseData;
import com.nicloud.workflowclient.detailedtask.main.DetailedTaskActivity;
import com.nicloud.workflowclient.detailedtask.main.OnRefreshDetailedTask;
import com.nicloud.workflowclient.detailedtask.main.OnSwipeRefresh;
import com.nicloud.workflowclient.serveraction.service.UploadService;
import com.nicloud.workflowclient.utility.DividerItemDecoration;
import com.nicloud.workflowclient.utility.Utilities;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/12.
 */
public class FileLogFragment extends Fragment implements OnSwipeRefresh, View.OnClickListener {

    private static final String TAG = "FileLogFragment";

    public static final String EXTRA_FILE_LOG = "extra_file_log";

    private static final int REQUEST_IMAGE_CAPTURE = 10001;
    private static final int REQUEST_PICK_FILE = 10002;

    private Context mContext;

    private SwipeRefreshLayout mFileLogSwipeRefreshLayout;

    private RecyclerView mFileLogList;
    private LinearLayoutManager mFileLogListLayoutManager;
    private FileLogListAdapter mFileLogListAdapter;
    private List<BaseData> mFileLogData = new ArrayList<>();

    private TextView mAddFileButton;
    private TextView mAddPhotoButton;

    private String mCurrentPhotoPath;
    private String mCurrentFilePath;

    private TextView mNoFileText;

    private String mTaskId;

    private OnRefreshDetailedTask mOnRefreshDetailedTask;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mOnRefreshDetailedTask = (OnRefreshDetailedTask) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_log, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTaskId = getArguments().getString(DetailedTaskActivity.EXTRA_TASK_ID);
        ArrayList<BaseData> dataSet = getArguments().getParcelableArrayList(EXTRA_FILE_LOG);
        mFileLogData.clear();
        mFileLogData.addAll(dataSet);
        initialize();
    }

    private void initialize() {
        findViews();
        setupViews();
        setupSwipeRefreshLayout();
        setupFileLogList();
    }

    private void findViews() {
        mFileLogSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.file_log_swipe_refresh_container);
        mFileLogList = (RecyclerView) getView().findViewById(R.id.file_log_list);
        mNoFileText = (TextView) getView().findViewById(R.id.file_log_list_no_item_text);
        mAddFileButton = (TextView) getView().findViewById(R.id.add_file_button);
        mAddPhotoButton = (TextView) getView().findViewById(R.id.take_photo_button);
    }

    private void setupViews() {
        setNoFileTextVisibility();
        mAddFileButton.setOnClickListener(this);
        mAddPhotoButton.setOnClickListener(this);
    }

    private void setNoFileTextVisibility() {
        if (mFileLogData.size() == 0) {
            mNoFileText.setVisibility(View.VISIBLE);
        } else {
            mNoFileText.setVisibility(View.GONE);
        }
    }

    private void setupSwipeRefreshLayout() {
        mFileLogSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mOnRefreshDetailedTask.onRefreshDetailedTask();
            }
        });

        mFileLogSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupFileLogList() {
        mFileLogListLayoutManager = new LinearLayoutManager(mContext);
        mFileLogListAdapter = new FileLogListAdapter(mContext, mFileLogData);

        mFileLogList.addItemDecoration(new DividerItemDecoration(
                getResources().getDrawable(R.drawable.list_divider), false, true, false, 0));
        mFileLogList.setLayoutManager(mFileLogListLayoutManager);
        mFileLogList.setAdapter(mFileLogListAdapter);
    }

    @Override
    public void swapData(List<BaseData> dataSet) {
        mFileLogData.clear();
        mFileLogData.addAll(dataSet);
        mFileLogListAdapter.notifyDataSetChanged();

        setNoFileTextVisibility();
    }

    @Override
    public void setSwipeRefreshLayout(boolean isRefresh) {
        mFileLogSwipeRefreshLayout.setRefreshing(isRefresh);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_file_button:
                pickupFile();
                break;

            case R.id.take_photo_button:
                capturePhoto();
                break;
        }
    }

    private void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) == null) {
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                onPhotoCaptured();

                break;

            case REQUEST_PICK_FILE:
                onFilePicked(data);

                break;

            default:
                break;
        }
    }

    private void onPhotoCaptured() {
        // compress photo
        File photoFile = new File(mCurrentPhotoPath.replace("file:", ""));
        Bitmap bitmap = Utilities.scaleBitmap(mContext, photoFile.getAbsolutePath());
        if (bitmap == null) return;

        scanPhotoToGallery();
        syncingPhotoActivity();
    }

    private void scanPhotoToGallery() {
        if (!TextUtils.isEmpty(mCurrentPhotoPath)) {  // trigger media scanner
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);

            mediaScanIntent.setData(contentUri);
            mContext.sendBroadcast(mediaScanIntent);
        }
    }

    private void syncingPhotoActivity() {
        String realPath = mCurrentPhotoPath.substring(mCurrentPhotoPath.indexOf(':') + 1);

        mContext.startService(UploadService.generateUploadPhotoIntent(mContext, mTaskId, realPath));

        mCurrentPhotoPath = null;
    }

    private void onFilePicked(Intent intent) {
        Uri uri = intent.getData();
        String path = null;

        try {
            path = Utilities.getPath(mContext, uri);
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
            mContext.startService(UploadService.generateUploadPhotoIntent(mContext, mTaskId, mCurrentFilePath));

        } else {
            mContext.startService(UploadService.generateUploadFileIntent(mContext, mTaskId, mCurrentFilePath));
        }

        mCurrentFilePath = null;
    }
}
