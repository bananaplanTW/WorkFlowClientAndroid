package com.nicloud.workflowclient.cases.discussion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.cases.main.CaseFragment;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.serveraction.service.CaseDiscussionService;
import com.nicloud.workflowclient.utility.IMMResult;
import com.nicloud.workflowclient.utility.Utilities;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/25.
 */
public class CaseDiscussionFragment extends Fragment implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "CaseDiscussionFragment";

    public static final int REQUEST_PICK_FILE = 143;
    private static final int LOADER_ID = 101;

    private static final String[] mProjection = new String[] {
            WorkFlowContract.Discussion._ID,
            WorkFlowContract.Discussion.DISCUSSION_ID,
            WorkFlowContract.Discussion.CASE_ID,
            WorkFlowContract.Discussion.WORKER_ID,
            WorkFlowContract.Discussion.WORKER_NAME,
            WorkFlowContract.Discussion.WORKER_AVATAR_URI,
            WorkFlowContract.Discussion.CONTENT,
            WorkFlowContract.Discussion.TYPE,
            WorkFlowContract.Discussion.FILE_NAME,
            WorkFlowContract.Discussion.FILE_URI,
            WorkFlowContract.Discussion.FILE_THUMB_URI,
            WorkFlowContract.Discussion.CREATED_TIME,
    };
    private static final int ID = 0;
    private static final int DISCUSSION_ID = 1;
    private static final int CASE_ID = 2;
    private static final int WORKER_ID = 3;
    private static final int WORKER_NAME = 4;
    private static final int WORKER_AVATAR_URI = 5;
    private static final int CONTENT = 6;
    private static final int TYPE = 7;
    private static final int FILE_NAME = 8;
    private static final int FILE_URI = 9;
    private static final int FILE_THUMB_URI = 10;
    private static final int CREATED_TIME = 11;

    private static final String mSelection = WorkFlowContract.Discussion.CASE_ID + " = ?";

    private static String[] mSelectionArgs;

    private static final String mSortOrder = WorkFlowContract.Discussion.CREATED_TIME;

    private Context mContext;

    private RecyclerView mDiscussionList;
    private LinearLayoutManager mDiscussionListLayoutManager;
    private DiscussionListAdapter mDiscussionListAdapter;

    private EditText mDiscussionBox;
    private ImageView mDiscussionAddFileButton;
    private ImageView mDiscussionSendMessageButton;

    private List<DiscussionItem> mDiscussionData = new ArrayList<>();

    private String mCaseId;
    private String mCurrentFilePath;

    private boolean hasTextInDiscussionBox = false;
    private int mFirstVisibleItemPosition = 0;
    private int mFirstVisibleItemOffset = 0;
    private int mPreviousDiscussionCount = 0;
    private boolean mIsNeedToScrollLast = true;
    private boolean mIsLoadingBeforeMessages = false;


    public void setCaseId(String caseId) {
        mCaseId = caseId;
        mSelectionArgs[0] = mCaseId;
        loadDiscussionFirstLaunch();
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
        return inflater.inflate(R.layout.fragment_case_discussion, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
        loadDiscussionFirstLaunch();
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void initialize() {
        mCaseId = getArguments().getString(CaseFragment.EXTRA_CASE_ID);
        mSelectionArgs = new String[] {mCaseId};

        findViews();
        setupViews();
        setupDiscussionList();
    }

    private void findViews() {
        mDiscussionList = (RecyclerView) getView().findViewById(R.id.discussion_list);
        mDiscussionBox = (EditText) getView().findViewById(R.id.discussion_box);
        mDiscussionAddFileButton = (ImageView) getView().findViewById(R.id.discussion_add_file_button);
        mDiscussionSendMessageButton = (ImageView) getView().findViewById(R.id.discussion_send_message_button);
    }

    private void setupViews() {
        mDiscussionBox.requestFocus();
        mDiscussionBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    mDiscussionSendMessageButton.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.hide));
                    mDiscussionAddFileButton.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.reveal));

                    mDiscussionSendMessageButton.setVisibility(View.GONE);
                    mDiscussionAddFileButton.setVisibility(View.VISIBLE);

                    hasTextInDiscussionBox = false;

                } else {
                    if (hasTextInDiscussionBox) return;

                    mDiscussionSendMessageButton.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.reveal));
                    mDiscussionAddFileButton.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.hide));

                    mDiscussionSendMessageButton.setVisibility(View.VISIBLE);
                    mDiscussionAddFileButton.setVisibility(View.GONE);

                    hasTextInDiscussionBox = true;
                }
            }
        });
        mDiscussionBox.setOnClickListener(this);

        mDiscussionAddFileButton.setOnClickListener(this);
        mDiscussionSendMessageButton.setOnClickListener(this);
    }

    private boolean isDiscussionListScrollToLastItem() {
        return mDiscussionListLayoutManager.findLastVisibleItemPosition() == mDiscussionData.size() - 1;
    }

    private boolean isSoftKeyboardShown(InputMethodManager imm, View v) {
        IMMResult result = new IMMResult();
        int res;

        imm.showSoftInput(v, 0, result);

        // if keyboard doesn't change, handle the key press
        res = result.getResult();
        if (res == InputMethodManager.RESULT_UNCHANGED_SHOWN ||
                res == InputMethodManager.RESULT_UNCHANGED_HIDDEN) {

            return true;
        } else {
            return false;
        }
    }

    private void setupDiscussionList() {
        mDiscussionListLayoutManager = new LinearLayoutManager(mContext);
        mDiscussionListAdapter = new DiscussionListAdapter(mContext, mDiscussionData);

        mDiscussionList.setLayoutManager(mDiscussionListLayoutManager);
        mDiscussionList.setAdapter(mDiscussionListAdapter);
    }

    private void loadDiscussionFirstLaunch() {
        if (getDiscussionCount() == 0) {
            mContext.startService(CaseDiscussionService.generateLoadDiscussionNormalIntent(mContext, mCaseId));

        } else {
            mContext.startService(
                    CaseDiscussionService.generateLoadDiscussionFromIntent(mContext, mCaseId, getLastDiscussionTime()));
        }
    }

    private int getDiscussionCount() {
        Cursor cursor = null;
        int messageCount = 0;

        try {
            cursor = mContext.getContentResolver().query(WorkFlowContract.Discussion.CONTENT_URI,
                    mProjection, mSelection, mSelectionArgs, null);
            if (cursor != null) {
                messageCount = cursor.getCount();
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return messageCount;
    }

    private long getLastDiscussionTime() {
        Cursor cursor = null;
        long lastMessageDate = 0L;

        try {
            cursor = mContext.getContentResolver().query(WorkFlowContract.Discussion.CONTENT_URI,
                    mProjection, mSelection, mSelectionArgs, mSortOrder);
            if (cursor != null) {
                cursor.moveToLast();
                lastMessageDate = cursor.getLong(CREATED_TIME);
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return lastMessageDate;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.discussion_box:
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (isDiscussionListScrollToLastItem() && isSoftKeyboardShown(imm, mDiscussionBox)) {
                    mDiscussionList.scrollToPosition(mDiscussionData.size() - 1);
                }

                break;

            case R.id.discussion_add_file_button:
                pickupFile();

                break;

            case R.id.discussion_send_message_button:
                sendMessage();

                break;
        }
    }

    private void sendMessage() {
        String message = mDiscussionBox.getText().toString();
        if (TextUtils.isEmpty(message)) return;

        mDiscussionBox.setText(null);

        mContext.startService(CaseDiscussionService.generateSendMessageIntent(mContext, mCaseId, message));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_PICK_FILE:
                onFilePicked(data);
                break;

            default:
                break;
        }
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
            mContext.startService(CaseDiscussionService.generateSendImageIntent(mContext, mCaseId, mCurrentFilePath));

        } else {
            mContext.startService(CaseDiscussionService.generateSendFileIntent(mContext, mCaseId, mCurrentFilePath));
        }

        mCurrentFilePath = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader
                = new CursorLoader(mContext, WorkFlowContract.Discussion.CONTENT_URI,
                mProjection, mSelection, mSelectionArgs, mSortOrder);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return;

        setDiscussionData(cursor);
    }

    private void setDiscussionData(Cursor cursor) {
        mDiscussionData.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(ID);
            String discussionId = cursor.getString(DISCUSSION_ID);
            String caseId = cursor.getString(CASE_ID);
            String workerId = cursor.getString(WORKER_ID);
            String workerName = cursor.getString(WORKER_NAME);
            String workerAvatarUri = cursor.getString(WORKER_AVATAR_URI);
            String content = cursor.getString(CONTENT);
            String fileName = cursor.getString(FILE_NAME);
            String fileUri = cursor.getString(FILE_URI);
            String fileThumbUri = cursor.getString(FILE_THUMB_URI);
            String type = cursor.getString(TYPE);
            long createdTime = cursor.getLong(CREATED_TIME);

            mDiscussionData.add(new DiscussionItem(discussionId, caseId, workerId, workerName, workerAvatarUri,
                                                   content, fileName, fileUri, fileThumbUri, type, createdTime));
        }

        mDiscussionListAdapter.notifyDataSetChanged();

        if (mIsNeedToScrollLast) {
            mDiscussionList.scrollToPosition(mDiscussionData.size() - 1);
        }

        mIsNeedToScrollLast = true;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
