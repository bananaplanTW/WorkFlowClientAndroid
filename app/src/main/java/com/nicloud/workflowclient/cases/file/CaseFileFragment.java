package com.nicloud.workflowclient.cases.file;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
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
import com.nicloud.workflowclient.backgroundtask.receiver.FileCompletedReceiver;
import com.nicloud.workflowclient.backgroundtask.service.FileService;
import com.nicloud.workflowclient.cases.discussion.LoadPromptDiscussionReceiver;
import com.nicloud.workflowclient.cases.main.CaseFragment;
import com.nicloud.workflowclient.cases.main.OnSetCaseId;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.utility.DividerItemDecoration;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/25.
 */
public class CaseFileFragment extends Fragment implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, OnSetCaseId, FileCompletedReceiver.OnLoadFileCompletedListener {

    private static final String TAG = "CaseFileFragment";

    private static final int LOADER_ID = 386;
    private static final int REQUEST_IMAGE_CAPTURE = 10301;
    private static final int REQUEST_PICK_FILE = 10302;

    private static final String[] mProjection = new String[] {
            WorkFlowContract.File._ID,
            WorkFlowContract.File.FILE_ID,
            WorkFlowContract.File.FILE_NAME,
            WorkFlowContract.File.FILE_TYPE,
            WorkFlowContract.File.FILE_URL,
            WorkFlowContract.File.FILE_THUMB_URL,
            WorkFlowContract.File.OWNER_ID,
            WorkFlowContract.File.OWNER_NAME,
            WorkFlowContract.File.CASE_ID,
            WorkFlowContract.File.TASK_ID,
            WorkFlowContract.File.CREATED_TIME,
            WorkFlowContract.File.UPDATED_TIME
    };
    private static final int ID = 0;
    private static final int FILE_ID = 1;
    private static final int FILE_NAME = 2;
    private static final int FILE_TYPE = 3;
    private static final int FILE_URL = 4;
    private static final int FILE_THUMB_URL = 5;
    private static final int OWNER_ID = 6;
    private static final int OWNER_NAME = 7;
    private static final int CASE_ID = 8;
    private static final int TASK_ID = 9;
    private static final int CREATED_TIME = 10;
    private static final int UPDATED_TIME = 11;

    private static final String mSelection = WorkFlowContract.File.CASE_ID + " = ?";
    private static String[] mSelectionArgs;
    private static final String mSortOrder = WorkFlowContract.File.CREATED_TIME + " DESC";

    private Context mContext;

    private SwipeRefreshLayout mFileSwipeRefreshLayout;

    private RecyclerView mFileList;
    private LinearLayoutManager mFileListLayoutManager;
    private FileListAdapter mFileListAdapter;

    private TextView mAddFileButton;
    private TextView mAddPhotoButton;

    private String mCurrentPhotoPath;
    private String mCurrentFilePath;

    private TextView mNoFileText;

    private FileCompletedReceiver mFileCompletedReceiver;

    private List<FileItem> mFileListData = new ArrayList<>();

    private String mCaseId;


    @Override
    public void setCaseId(String caseId) {
        mCaseId = caseId;

        if (mSelectionArgs == null) {
            mSelectionArgs = new String[] {mCaseId};
        } else {
            mSelectionArgs[0] = mCaseId;
        }

        mContext.startService(FileService.generateLoadCaseFilesIntent(mContext, mCaseId));
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
        mContext.startService(FileService.generateLoadCaseFilesIntent(mContext, mCaseId));

        Loader<CursorLoader> loader = getLoaderManager().getLoader(LOADER_ID);
        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(LOADER_ID, null, this);

        } else {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(FileCompletedReceiver.ACTION_LOAD_FILES_COMPLETED);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mFileCompletedReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mFileCompletedReceiver);
    }

    private void initialize() {
        mFileCompletedReceiver = new FileCompletedReceiver(this);
        mCaseId = getArguments().getString(CaseFragment.EXTRA_CASE_ID);
        mSelectionArgs = new String[] {mCaseId};

        findViews();
        setupViews();
        setupSwipeRefreshLayout();
        setupFileList();
    }

    private void findViews() {
        mFileSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.file_list_swipe_refresh_container);
        mFileList = (RecyclerView) getView().findViewById(R.id.file_list);
        mNoFileText = (TextView) getView().findViewById(R.id.file_list_no_item_text);
        mAddFileButton = (TextView) getView().findViewById(R.id.add_file_button);
        mAddPhotoButton = (TextView) getView().findViewById(R.id.take_photo_button);
    }

    private void setupViews() {
        setNoFileTextVisibility();
        mAddFileButton.setOnClickListener(this);
        mAddPhotoButton.setOnClickListener(this);
    }

    private void setNoFileTextVisibility() {
        if (mFileListData.size() == 0) {
            mNoFileText.setVisibility(View.VISIBLE);
        } else {
            mNoFileText.setVisibility(View.GONE);
        }
    }

    private void setupSwipeRefreshLayout() {
        mFileSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mContext.startService(FileService.generateLoadCaseFilesIntent(mContext, mCaseId));
            }
        });

        mFileSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupFileList() {
        mFileListLayoutManager = new LinearLayoutManager(mContext);
        mFileListAdapter = new FileListAdapter(mContext, mFileListData);

        mFileList.addItemDecoration(new DividerItemDecoration(
                getResources().getDrawable(R.drawable.list_divider), false, true, false, 0));
        mFileList.setLayoutManager(mFileListLayoutManager);
        mFileList.setAdapter(mFileListAdapter);
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
            getParentFragment().startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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
            getParentFragment().
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
        Bitmap bitmap = Utils.scaleBitmap(mContext, photoFile.getAbsolutePath());
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

        //mContext.startService(UploadService.generateUploadTaskPhotoIntent(mContext, mTaskId, realPath));

        mCurrentPhotoPath = null;
    }

    private void onFilePicked(Intent intent) {
        Uri uri = intent.getData();
        String path = null;

        try {
            path = Utils.getPath(mContext, uri);
        } catch (URISyntaxException e) {
            Log.d(TAG, "File attach failed");
            e.printStackTrace();
        }

        mCurrentFilePath = path;
        syncingFileActivity();
    }

    private void syncingFileActivity() {
        if (Utils.isImage(mCurrentFilePath)) {
            //mContext.startService(UploadService.generateUploadTaskPhotoIntent(mContext, mTaskId, mCurrentFilePath));

        } else {
            //mContext.startService(UploadService.generateUploadTaskFileIntent(mContext, mTaskId, mCurrentFilePath));
        }

        mCurrentFilePath = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext, WorkFlowContract.File.CONTENT_URI,
                mProjection, mSelection, mSelectionArgs, mSortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return;

        setFileListData(cursor);
    }

    private void setFileListData(Cursor cursor) {
        mFileListData.clear();

        while (cursor.moveToNext()) {
            String fileId = cursor.getString(FILE_ID);
            String fileName = cursor.getString(FILE_NAME);
            String fileType = cursor.getString(FILE_TYPE);
            String fileUrl = cursor.getString(FILE_URL);
            String fileThumbUrl = cursor.getString(FILE_THUMB_URL);
            String ownerId = cursor.getString(OWNER_ID);
            String ownerName = cursor.getString(OWNER_NAME);
            String caseId = cursor.getString(CASE_ID);
            String taskId = cursor.getString(TASK_ID);
            long createdTime = cursor.getLong(CREATED_TIME);
            long updatedTime = cursor.getLong(UPDATED_TIME);

            mFileListData.add(new FileItem(fileId, fileName, fileType, fileUrl, fileThumbUrl,
                                           ownerId, ownerName, caseId, taskId,
                                           createdTime, updatedTime));
        }

        mFileListAdapter.notifyDataSetChanged();
        setNoFileTextVisibility();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onLoadFileCompleted(Intent intent) {
        mFileSwipeRefreshLayout.setRefreshing(false);
    }
}
