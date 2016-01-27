package com.nicloud.workflowclient.cases.discussion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
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
import com.nicloud.workflowclient.serveraction.service.UploadService;
import com.nicloud.workflowclient.utility.IMMResult;
import com.nicloud.workflowclient.utility.Utilities;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/25.
 */
public class CaseDiscussionFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "CaseDiscussionFragment";

    private static final int REQUEST_PICK_FILE = 100;

    private Context mContext;

    private RecyclerView mDiscussionList;
    private LinearLayoutManager mDiscussionListLayoutManager;
    private DiscussionListAdapter mDiscussionListAdapter;

    private EditText mDiscussionBox;
    private ImageView mDiscussionAddFileButton;
    private ImageView mDiscussionSendMessageButton;

    private List<Discussion> mDiscussionData = new ArrayList<>();

    private String mCaseId;
    private String mCurrentFilePath;

    private boolean hasTextInDiscussionBox = false;


    public void setCaseId(String caseId) {
        mCaseId = caseId;
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
    }

    private void initialize() {
        mCaseId = getArguments().getString(CaseFragment.EXTRA_CASE_ID);

        findViews();
        setupViews();
        setDiscussionData();
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

    private void setDiscussionData() {
        mDiscussionData.add(new Discussion("123123", "Good day", 1233332131));
        mDiscussionData.add(new Discussion("123123", "Good day", 1233332131));

        for (int i = 0 ; i < 100 ; i ++) {
            mDiscussionData.add(new Discussion("123123", "Good day", 1233332131));
        }
    }

    private void setupDiscussionList() {
        mDiscussionListLayoutManager = new LinearLayoutManager(mContext);
        mDiscussionListAdapter = new DiscussionListAdapter(mContext, mDiscussionData);

        mDiscussionList.setLayoutManager(mDiscussionListLayoutManager);
        mDiscussionList.setAdapter(mDiscussionListAdapter);
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

                break;
        }
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
            //mContext.startService(UploadService.generateUploadPhotoIntent(mContext, mTaskId, mCurrentFilePath));

        } else {
            //mContext.startService(UploadService.generateUploadFileIntent(mContext, mTaskId, mCurrentFilePath));
        }

        mCurrentFilePath = null;
    }
}
