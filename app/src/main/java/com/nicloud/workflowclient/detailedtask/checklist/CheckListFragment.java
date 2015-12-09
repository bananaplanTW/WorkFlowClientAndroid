package com.nicloud.workflowclient.detailedtask.checklist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.data.CheckItem;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.detailedtask.DetailedTaskActivity;
import com.nicloud.workflowclient.detailedtask.OnRefreshDetailedTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2015/12/8.
 */
public class CheckListFragment extends Fragment {

    private Context mContext;

    private RecyclerView mCheckList;
    private LinearLayoutManager mCheckListLayoutManager;
    private CheckListAdapter mCheckListAdapter;

    private SwipeRefreshLayout mCheckListSwipeRefreshLayout;

    private TextView mNoCheckItemText;

    private String mTaskId;

    private List<CheckItem> mDataSet = new ArrayList<>();

    private OnRefreshDetailedTask mOnRefreshDetailedTask;


    public void refresh() {
        mDataSet.clear();
        mDataSet.addAll(WorkingData.getInstance(mContext).getTask(mTaskId).checkList);
        mCheckListAdapter.notifyDataSetChanged();
    }

    public void setSwipeRefreshLayout(boolean isRefreshing) {
        mCheckListSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

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
        initialize();
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
        mCheckListAdapter = new CheckListAdapter(mContext, mDataSet);

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
}
