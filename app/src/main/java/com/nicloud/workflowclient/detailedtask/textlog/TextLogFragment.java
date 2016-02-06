package com.nicloud.workflowclient.detailedtask.textlog;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.activity.BaseData;
import com.nicloud.workflowclient.data.data.data.TaskTextLog;
import com.nicloud.workflowclient.detailedtask.main.DetailedTaskActivity;
import com.nicloud.workflowclient.detailedtask.main.OnRefreshDetailedTask;
import com.nicloud.workflowclient.detailedtask.main.OnSwipeRefresh;
import com.nicloud.workflowclient.backgroundtask.service.UploadService;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.utility.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/11.
 */
public class TextLogFragment extends Fragment implements OnSwipeRefresh, View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_TEXT_LOG = "extra_text_log";

    private static final int LOADER_ID = 843;

    private static final String[] mProjection = new String[] {
            WorkFlowContract.TaskTextLog._ID,
            WorkFlowContract.TaskTextLog.TASK_TEXT_LOG_ID,
            WorkFlowContract.TaskTextLog.TASK_ID,
            WorkFlowContract.TaskTextLog.OWNER_ID,
            WorkFlowContract.TaskTextLog.OWNER_NAME,
            WorkFlowContract.TaskTextLog.OWNER_AVATAR_URL,
            WorkFlowContract.TaskTextLog.CREATED_TIME,
            WorkFlowContract.TaskTextLog.UPDATED_TIME,
            WorkFlowContract.TaskTextLog.CONTENT
    };
    private static final int ID = 0;
    private static final int TASK_TEXT_LOG_ID = 1;
    private static final int TASK_ID = 2;
    private static final int OWNER_ID = 3;
    private static final int OWNER_NAME = 4;
    private static final int OWNER_AVATAR_URL = 5;
    private static final int CREATED_TIME = 6;
    private static final int UPDATED_TIME = 7;
    private static final int CONTENT = 8;

    private static String mSelection = WorkFlowContract.TaskTextLog.TASK_ID + " = ?";
    private static String[] mSelectionArgs;
    private static final String mSortOrder = WorkFlowContract.TaskTextLog.CREATED_TIME + " DESC";

    private Context mContext;

    private SwipeRefreshLayout mTextLogSwipeRefreshLayout;

    private RecyclerView mTextLogList;
    private LinearLayoutManager mTextLogListLayoutManager;
    private TextLogAdapter mTextLogAdapter;
    private List<TaskTextLog> mTextDataSet = new ArrayList<>();

    private EditText mTextLogBox;
    private TextView mAddTextLogButton;

    private TextView mNoTextLog;

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
        return inflater.inflate(R.layout.fragment_text_log, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void initialize() {
        mTaskId = getArguments().getString(DetailedTaskActivity.EXTRA_TASK_ID);
        mSelectionArgs = new String[] {mTaskId};

        findViews();
        setupViews();
        setupSwipeRefreshLayout();
        setupTextLogList();
    }

    private void findViews() {
        mTextLogSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.text_log_swipe_refresh_container);
        mTextLogList = (RecyclerView) getView().findViewById(R.id.text_log_list);
        mTextLogBox = (EditText) getView().findViewById(R.id.add_text_log_box);
        mNoTextLog = (TextView) getView().findViewById(R.id.text_log_list_no_item_text);
        mAddTextLogButton = (TextView) getView().findViewById(R.id.add_text_log_button);
    }

    private void setupViews() {
        setNoTextLogVisibility();
        mAddTextLogButton.setOnClickListener(this);
    }

    private void setNoTextLogVisibility() {
        if (mTextDataSet.size() == 0) {
            mNoTextLog.setVisibility(View.VISIBLE);
        } else {
            mNoTextLog.setVisibility(View.GONE);
        }
    }

    private void setupSwipeRefreshLayout() {
        mTextLogSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mOnRefreshDetailedTask.onRefreshDetailedTask();
            }
        });

        mTextLogSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupTextLogList() {
        mTextLogListLayoutManager = new LinearLayoutManager(mContext);
        mTextLogAdapter = new TextLogAdapter(mContext, mTextDataSet);

        mTextLogList.addItemDecoration(new DividerItemDecoration(
                getResources().getDrawable(R.drawable.list_divider), false, true, false, 0));
        mTextLogList.setLayoutManager(mTextLogListLayoutManager);
        mTextLogList.setAdapter(mTextLogAdapter);
    }

    @Override
    public void swapData(List<BaseData> dataSet) {
    }

    @Override
    public void setSwipeRefreshLayout(boolean isRefresh) {
        mTextLogSwipeRefreshLayout.setRefreshing(isRefresh);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_text_log_button:
                addTextLog();
                break;
        }
    }

    private void addTextLog() {
        String editContent = mTextLogBox.getText().toString();
        if (TextUtils.isEmpty(editContent.trim())) return;

        mContext.startService(UploadService.generateUploadTaskTextIntent(mContext, mTaskId, editContent));
        mTextLogBox.setText("");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext, WorkFlowContract.TaskTextLog.CONTENT_URI,
                                mProjection, mSelection, mSelectionArgs, mSortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null) return;

        mTextDataSet.clear();

        while (cursor.moveToNext()) {
            String taskTextLogId = cursor.getString(TASK_TEXT_LOG_ID);
            String taskId = cursor.getString(TASK_ID);
            String ownerId = cursor.getString(OWNER_ID);
            String ownerName = cursor.getString(OWNER_NAME);
            String ownerAvatarUrl = cursor.getString(OWNER_AVATAR_URL);
            long createdTime = cursor.getLong(CREATED_TIME);
            long updatedTime = cursor.getLong(UPDATED_TIME);
            String content = cursor.getString(CONTENT);

            mTextDataSet.add(new TaskTextLog(taskTextLogId, taskId, ownerId, ownerName, ownerAvatarUrl,
                                             createdTime, updatedTime, content));
        }

        mTextLogAdapter.notifyDataSetChanged();
        setNoTextLogVisibility();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
