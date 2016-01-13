package com.nicloud.workflowclient.detailedtask.checklist;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.activity.BaseData;
import com.nicloud.workflowclient.data.data.data.CheckItem;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.detailedtask.DetailedTaskActivity;
import com.nicloud.workflowclient.detailedtask.OnRefreshDetailedTask;
import com.nicloud.workflowclient.detailedtask.OnSwipeRefresh;
import com.nicloud.workflowclient.serveraction.ActionService;
import com.nicloud.workflowclient.serveraction.ActionCompletedReceiver;
import com.nicloud.workflowclient.utility.DividerItemDecoration;
import com.nicloud.workflowclient.utility.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2015/12/8.
 */
public class CheckListFragment extends Fragment implements OnSwipeRefresh,
        ActionCompletedReceiver.OnServerActionCompletedListener {

    private Context mContext;
    private ActionCompletedReceiver mActionCompletedReceiver;

    private RecyclerView mCheckList;
    private LinearLayoutManager mCheckListLayoutManager;
    private CheckListAdapter mCheckListAdapter;

    private SwipeRefreshLayout mCheckListSwipeRefreshLayout;

    private TextView mNoCheckItemText;

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
        mTaskId = getArguments().getString(DetailedTaskActivity.EXTRA_TASK_ID);
        mDataSet.addAll(WorkingData.getInstance(mContext).getTask(mTaskId).checkList);
        mActionCompletedReceiver = new ActionCompletedReceiver(this);
        initialize();
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ActionService.ServerAction.CHECK_ITEM);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mActionCompletedReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mActionCompletedReceiver);
    }

    private void initialize() {
        findViews();
        setupCheckList();
        setupSwipeRefreshLayout();
        setNoCheckItemTextVisibility();
    }

    private void findViews() {
        mCheckList = (RecyclerView) getView().findViewById(R.id.check_list);
        mCheckListSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.check_list_swipe_refresh_container);
        mNoCheckItemText = (TextView) getView().findViewById(R.id.check_list_no_item_text);
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
    public void onServerActionCompleted(Intent intent) {
        String action = intent.getAction();
        boolean isActionSuccessful = intent.getBooleanExtra(ActionService.ExtraKey.ACTION_SUCCESSFUL, false);

        if (ActionService.ServerAction.CHECK_ITEM.equals(action)) {
            String taskId = intent.getStringExtra(ActionService.ExtraKey.TASK_ID);
            int index = intent.getIntExtra(ActionService.ExtraKey.CHECK_ITEM_INDEX, 0);
            boolean checked = intent.getBooleanExtra(ActionService.ExtraKey.CHECK_ITEM_CHECKED, false);

            if (isActionSuccessful) {
                WorkingData.getInstance(mContext).getTask(taskId).checkList.get(index).isChecked = checked;
            } else {
                Utilities.showInternetConnectionWeakToast(mContext);
            }

            mCheckListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void swapData(List<BaseData> dataSet) {
        mDataSet.clear();
        mDataSet.addAll(WorkingData.getInstance(mContext).getTask(mTaskId).checkList);
        mCheckListAdapter.notifyDataSetChanged();
    }

    @Override
    public void refresh() {

    }

    @Override
    public void setSwipeRefreshLayout(boolean isRefreshing) {
        mCheckListSwipeRefreshLayout.setRefreshing(isRefreshing);
    }
}
