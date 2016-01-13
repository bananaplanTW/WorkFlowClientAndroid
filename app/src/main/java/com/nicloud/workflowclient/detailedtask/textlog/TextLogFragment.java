package com.nicloud.workflowclient.detailedtask.textlog;

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

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.activity.BaseData;
import com.nicloud.workflowclient.detailedtask.OnRefreshDetailedTask;
import com.nicloud.workflowclient.utility.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/11.
 */
public class TextLogFragment extends Fragment {

    public static final String EXTRA_TEXT_LOG = "extra_text_log";

    private Context mContext;

    private SwipeRefreshLayout mTextLogSwipeRefreshLayout;

    private RecyclerView mTextLogList;
    private LinearLayoutManager mTextLogListLayoutManager;
    private TextLogAdapter mTextLogAdapter;
    private List<BaseData> mTextDataSet = new ArrayList<>();

    private OnRefreshDetailedTask mOnRefreshDetailedTask;


    public void swapData(List<BaseData> dataSet) {
        mTextDataSet.clear();
        mTextDataSet.addAll(dataSet);
        mTextLogAdapter.notifyDataSetChanged();

        //setNoLogTextVisibility();
    }

    public void refresh() {
        mTextLogAdapter.notifyDataSetChanged();
    }

    public void setSwipeRefreshLayout(boolean isRefresh) {
        mTextLogSwipeRefreshLayout.setRefreshing(isRefresh);
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
        return inflater.inflate(R.layout.fragment_text_log, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        ArrayList<BaseData> dataSet = getArguments().getParcelableArrayList(EXTRA_TEXT_LOG);
        mTextDataSet.clear();
        mTextDataSet.addAll(dataSet);
        findViews();
        setupSwipeRefreshLayout();
        setupTextLogList();
    }

    private void findViews() {
        mTextLogSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.text_log_swipe_refresh_container);
        mTextLogList = (RecyclerView) getView().findViewById(R.id.text_log_list);
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
}
