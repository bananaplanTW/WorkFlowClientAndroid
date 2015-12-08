package com.nicloud.workflowclient.detailedtask.tasklog;

import android.content.Context;
import android.content.Intent;
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
import com.nicloud.workflowclient.data.connectserver.activity.ILoadingActivitiesStrategy;
import com.nicloud.workflowclient.data.connectserver.activity.LoadingActivitiesAsyncTask;
import com.nicloud.workflowclient.data.connectserver.activity.LoadingTaskActivitiesStrategy;
import com.nicloud.workflowclient.data.data.activity.ActivityDataFactory;
import com.nicloud.workflowclient.data.data.activity.BaseData;
import com.nicloud.workflowclient.utility.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2015/12/8.
 */
public class TaskLogFragment extends Fragment {

    private Context mContext;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mDetailedTaskListView;
    private LinearLayoutManager mDetailedTaskListViewLayoutManager;
    private DetailedTaskListAdapter mDetailedTaskListAdapter;

    private List<BaseData> mTextDataSet = new ArrayList<>();
    private List<BaseData> mPhotoDataSet = new ArrayList<>();
    private List<BaseData> mFileDataSet = new ArrayList<>();

    private TextView mNoLogText;

    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_log, container, false);
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
    }

//    private void loadTaskActivities() {
//        LoadingTaskActivitiesStrategy loadingTaskActivitiesStrategy =
//                new LoadingTaskActivitiesStrategy(mTask.id, TASK_LOG_LIMIT);
//        LoadingActivitiesAsyncTask loadingWorkerActivitiesTask =
//                new LoadingActivitiesAsyncTask(this, mTask.id, this, loadingTaskActivitiesStrategy);
//        loadingWorkerActivitiesTask.execute();
//    }

//    private void setupRecordLog() {
//        mDetailedTaskListViewLayoutManager = new LinearLayoutManager(this);
//        mDetailedTaskListAdapter = new DetailedTaskListAdapter(this);
//
//        mDetailedTaskListView.setLayoutManager(mDetailedTaskListViewLayoutManager);
//        mDetailedTaskListView.addItemDecoration(
//                new DividerItemDecoration(getResources().getDrawable(R.drawable.list_divider), false, true, false, 0));
//        mDetailedTaskListView.setAdapter(mDetailedTaskListAdapter);
//    }
//
//    private void setupSwipeRefreshLayout() {
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//
//            @Override
//            public void onRefresh() {
//                //loadTaskActivities();
//            }
//        });
//
//        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
//    }
//
//    @Override
//    public void onFinishLoadingData(String id, ILoadingActivitiesStrategy.ActivityCategory category, JSONArray activities) {
//        if (activities == null) return;
//
//        setTaskLogData(parseActivityJSONArray(activities));
//        mSwipeRefreshLayout.setRefreshing(false);
//    }
//
//    private void setTaskLogData(ArrayList<BaseData> logData) {
//        mTextDataSet.clear();
//        mPhotoDataSet.clear();
//        mFileDataSet.clear();
//
//        for (BaseData data : logData) {
//            switch (data.type) {
//                case RECORD:
//                    mTextDataSet.add(data);
//                    break;
//
//                case PHOTO:
//                    mPhotoDataSet.add(data);
//                    break;
//
//                case FILE:
//                    mFileDataSet.add(data);
//                    break;
//            }
//        }
//
//        updateDetailedTaskListAccordingToTab();
//    }
//
//    private void updateDetailedTaskListAccordingToTab() {
//        switch (mSelectedTabPosition) {
//            case TabPosition.TEXT:
//                mDetailedTaskListAdapter.swapDataSet(mTextDataSet);
//
//                break;
//
//            case TabPosition.PHOTO:
//                mDetailedTaskListAdapter.swapDataSet(mPhotoDataSet);
//
//                break;
//
//            case TabPosition.FILE:
//                mDetailedTaskListAdapter.swapDataSet(mFileDataSet);
//
//                break;
//        }
//
//        setNoLogTextVisibility();
//    }
//
//    private void setNoLogTextVisibility() {
//        if (mDetailedTaskListAdapter.getItemCount() == 0) {
//            mNoLogText.setVisibility(View.VISIBLE);
//
//        } else {
//            mNoLogText.setVisibility(View.GONE);
//        }
//    }
//
//    private ArrayList<BaseData> parseActivityJSONArray(JSONArray activities) {
//        ArrayList<BaseData> parsedActivities = new ArrayList<>();
//
//        int length = activities.length();
//
//        try {
//            for (int i = 0; i < length; i++) {
//                JSONObject activity = activities.getJSONObject(i);
//                BaseData activityData = ActivityDataFactory.genData(activity, this, this);
//                if (activityData != null) {
//                    parsedActivities.add(activityData);
//                }
//            }
//            return parsedActivities;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    public void onFailLoadingData(boolean isFailCausedByInternet) {
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case REQUEST_ADD_LOG:
//                if (RESULT_OK != resultCode) return;
//                loadTaskActivities();
//
//                break;
//        }
//    }
//
//    @Override
//    public void onFinishLoadImage() {
//        mDetailedTaskListAdapter.notifyDataSetChanged();
//    }

}
