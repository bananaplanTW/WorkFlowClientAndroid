package com.nicloud.workflowclient.detailedtask.tasklog;

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
import com.nicloud.workflowclient.data.data.activity.BaseData;
import com.nicloud.workflowclient.utility.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2015/12/8.
 */
public class TaskLogFragment extends Fragment {

    public static final String EXTRA_TASK_LOG = "extra_task_log";

    public interface OnRefreshTaskLog {
        void onRefreshTaskLog();
    }

    private Context mContext;

    private SwipeRefreshLayout mTaskLogSwipeRefreshLayout;
    private RecyclerView mTaskLogListView;
    private LinearLayoutManager mTaskLogLayoutManager;
    private TaskLogListAdapter mTaskLogListAdapter;

    private List<BaseData> mDataSet = new ArrayList<>();

    private TextView mNoLogText;

    private OnRefreshTaskLog mOnRefreshTaskLog;


    public void swapTaskLogData(List<BaseData> dataSet) {
        mDataSet.clear();
        mDataSet.addAll(dataSet);
        mTaskLogListAdapter.notifyDataSetChanged();

        setNoLogTextVisibility();
    }

    public void refresh() {
        mTaskLogListAdapter.notifyDataSetChanged();
    }

    public void setSwipeRefreshLayout(boolean isRefresh) {
        mTaskLogSwipeRefreshLayout.setRefreshing(isRefresh);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mOnRefreshTaskLog = (OnRefreshTaskLog) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_log, container, false);
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayList<BaseData> dataSet = getArguments().getParcelableArrayList(EXTRA_TASK_LOG);
        mDataSet.clear();
        mDataSet.addAll(dataSet);
        initialize();
    }

    private void initialize() {
        findViews();
        setupTaskLogList();
        setupSwipeRefreshLayout();
        setNoLogTextVisibility();
    }

    private void setNoLogTextVisibility() {
        if (mDataSet.size() == 0) {
            //mTaskLogListView.setVisibility(View.GONE);
            mNoLogText.setVisibility(View.VISIBLE);

        } else {
            //mTaskLogListView.setVisibility(View.VISIBLE);
            mNoLogText.setVisibility(View.GONE);
        }
    }

    private void findViews() {
        mTaskLogSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.task_log_swipe_refresh_container);
        mTaskLogListView = (RecyclerView) getView().findViewById(R.id.task_log_list);
        mNoLogText = (TextView) getView().findViewById(R.id.task_log_no_log_text);
    }

    private void setupTaskLogList() {
        mTaskLogLayoutManager = new LinearLayoutManager(mContext);
        mTaskLogListAdapter = new TaskLogListAdapter(mContext, mDataSet);

        mTaskLogListView.setLayoutManager(mTaskLogLayoutManager);
        mTaskLogListView.addItemDecoration(
                new DividerItemDecoration(getResources().getDrawable(R.drawable.list_divider), false, true, false, 0));
        mTaskLogListView.setAdapter(mTaskLogListAdapter);
    }

    private void setupSwipeRefreshLayout() {
        mTaskLogSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mOnRefreshTaskLog.onRefreshTaskLog();
            }
        });

        mTaskLogSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
}
