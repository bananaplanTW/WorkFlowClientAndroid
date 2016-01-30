package com.nicloud.workflowclient.detailedtask.checklist;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.activity.BaseData;
import com.nicloud.workflowclient.detailedtask.main.DetailedTaskActivity;
import com.nicloud.workflowclient.detailedtask.main.OnRefreshDetailedTask;
import com.nicloud.workflowclient.detailedtask.main.OnSwipeRefresh;
import com.nicloud.workflowclient.backgroundtask.service.ActionService;
import com.nicloud.workflowclient.backgroundtask.receiver.ActionCompletedReceiver;
import com.nicloud.workflowclient.backgroundtask.service.UploadService;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.utility.DividerItemDecoration;
import com.nicloud.workflowclient.utility.utils.DbUtils;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2015/12/8.
 */
public class CheckListFragment extends Fragment implements OnSwipeRefresh, View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 387;

    private static final String[] mProjection = new String[] {
            WorkFlowContract.CheckList._ID,
            WorkFlowContract.CheckList.CHECK_NAME,
            WorkFlowContract.CheckList.IS_CHECKED,
            WorkFlowContract.CheckList.TASK_ID,
            WorkFlowContract.CheckList.POSITION
    };
    private static final int ID = 0;
    private static final int CHECK_NAME = 1;
    private static final int IS_CHECKED = 2;
    private static final int TASK_ID = 3;
    private static final int POSITION = 4;

    private static final String mSelection = WorkFlowContract.CheckList.TASK_ID + " = ?";
    private static String[] mSelectionArgs;
    private static String mSortOrder = WorkFlowContract.CheckList.POSITION;

    private Context mContext;

    private SwipeRefreshLayout mCheckListSwipeRefreshLayout;
    private RecyclerView mCheckList;
    private LinearLayoutManager mCheckListLayoutManager;
    private CheckListAdapter mCheckListAdapter;

    private TextView mNoCheckItemText;

    private EditText mAddCheckItemBox;
    private TextView mAddCheckItemButton;

    private String mTaskId;

    private List<CheckItem> mDataSet = new ArrayList<>();

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
        return inflater.inflate(R.layout.fragment_check_list, container, false);
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
        setupCheckList();
        setupSwipeRefreshLayout();
    }

    private void findViews() {
        mCheckList = (RecyclerView) getView().findViewById(R.id.check_list);
        mCheckListSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.check_list_swipe_refresh_container);
        mAddCheckItemBox = (EditText) getView().findViewById(R.id.add_check_item_box);
        mAddCheckItemButton = (TextView) getView().findViewById(R.id.add_check_item_button);
        mNoCheckItemText = (TextView) getView().findViewById(R.id.check_list_no_item_text);
    }

    private void setupViews() {
        setNoCheckItemTextVisibility();
        mAddCheckItemButton.setOnClickListener(this);
    }

    private void setupCheckList() {
        mCheckListLayoutManager = new LinearLayoutManager(mContext);
        mCheckListAdapter = new CheckListAdapter(mContext, mTaskId, mDataSet);

        mCheckList.addItemDecoration(new DividerItemDecoration(
                getResources().getDrawable(R.drawable.list_divider), false, true, false, 0));
        mCheckList.setLayoutManager(mCheckListLayoutManager);
        mCheckList.setAdapter(mCheckListAdapter);
    }

    private void setupSwipeRefreshLayout() {
        mCheckListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mOnRefreshDetailedTask.onRefreshDetailedTask();
            }
        });

        mCheckListSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setNoCheckItemTextVisibility() {
        if (mDataSet.size() == 0) {
            mNoCheckItemText.setVisibility(View.VISIBLE);
        } else {
            mNoCheckItemText.setVisibility(View.GONE);
        }
    }

    @Override
    public void swapData(List<BaseData> dataSet) {

    }

    @Override
    public void setSwipeRefreshLayout(boolean isRefreshing) {
        mCheckListSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_check_item_button:
                String checkItem = mAddCheckItemBox.getText().toString();
                if (TextUtils.isEmpty(checkItem)) return;

                mContext.startService(UploadService.generateUploadCheckItemIntent(mContext, mTaskId, checkItem));
                mAddCheckItemBox.setText("");
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext, WorkFlowContract.CheckList.CONTENT_URI,
                mProjection, mSelection, mSelectionArgs, mSortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return;

        setCheckListData(cursor);
    }

    private void setCheckListData(Cursor cursor) {
        mDataSet.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(ID);
            String checkName = cursor.getString(CHECK_NAME);
            boolean isChecked = cursor.getInt(IS_CHECKED) == 1;
            String taskId = cursor.getString(TASK_ID);
            int position = cursor.getInt(POSITION);

            mDataSet.add(new CheckItem(checkName, taskId, isChecked, position));
        }

        mCheckListAdapter.notifyDataSetChanged();
        setNoCheckItemTextVisibility();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
