package com.nicloud.workflowclient.tasklist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.task.LoadingWorkerTasks;
import com.nicloud.workflowclient.data.data.data.Task;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.data.data.observer.DataObserver;
import com.nicloud.workflowclient.utility.DividerItemDecoration;
import com.nicloud.workflowclient.utility.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2015/12/21.
 */
public class TaskListFragment extends Fragment implements DataObserver, View.OnClickListener {

    public interface OnRefreshInTaskList {
        void onRefreshInTaskList();
    }

    private Context mContext;
    private FragmentManager mFm;

    private FloatingActionButton mFab;

    private RecyclerView mTasksList;
    private LinearLayoutManager mTasksListManager;
    private TasksListAdapter mTasksListAdapter;
    private List<TasksListItem> mTasksDataSet = new ArrayList<>();

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean mFirstLaunch = true;

    private OnRefreshInTaskList mOnRefreshInTaskList;

    private LoadingWorkerTasks.OnFinishLoadingDataListener mOnFinishLoadingDataListener = new LoadingWorkerTasks.OnFinishLoadingDataListener() {
        @Override
        public void onFinishLoadingData() {
            if (mFirstLaunch) {
                forceHideRefreshSpinner();
                mFirstLaunch = false;
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
            }

            //mFab.show();
            WorkingData.getInstance(mContext).updateData();
        }

        @Override
        public void onFailLoadingData(boolean isFailCausedByInternet) {
            showInternetConnectionWeakToast();
        }
    };


    private void showInternetConnectionWeakToast() {
        mSwipeRefreshLayout.setRefreshing(false);
        Utilities.showInternetConnectionWeakToast(mContext);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mOnRefreshInTaskList = (OnRefreshInTaskList) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        WorkingData.getInstance(mContext).registerDataObserver(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        WorkingData.getInstance(mContext).removeDataObserver(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        mFm = getFragmentManager();
        findViews();
        setupViews();
        setupTasksList();
        setupSwipeRefresh();
        loadDataInFirstLaunch();
    }

    private void findViews() {
        mFab = (FloatingActionButton) getView().findViewById(R.id.fab);
        mTasksList = (RecyclerView) getView().findViewById(R.id.tasks_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.task_log_swipe_refresh_container);
    }

    private void setupViews() {
        mFab.setOnClickListener(this);
    }

    private void setupTasksList() {
        mTasksListManager = new LinearLayoutManager(mContext);
        mTasksListAdapter = new TasksListAdapter(mContext, mFm, mTasksDataSet);

        mTasksList.setLayoutManager(mTasksListManager);
        mTasksList.addItemDecoration(
                new DividerItemDecoration(mContext.getResources().getDrawable(R.drawable.list_divider),
                        true, true, true, mContext.getResources().getDimensionPixelSize(R.dimen.tasks_list_padding_bottom)));
        mTasksList.setAdapter(mTasksListAdapter);
    }

    private void setupSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mOnRefreshInTaskList.onRefreshInTaskList();
                loadWorkerTasks();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void loadDataInFirstLaunch() {
        forceShowRefreshSpinner();
        loadWorkerTasks();
    }

    /**
     * Use for the first time we enter the app.
     * If we want to trigger the refresh spinner programmatically, we need to use this method.
     */
    private void forceShowRefreshSpinner() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    /**
     * Use for the first time we enter the app.
     * To remove the refresh spinner triggered by forceShowRefreshSpinner().
     */
    private void forceHideRefreshSpinner() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadWorkerTasks() {
        new LoadingWorkerTasks(mContext, mOnFinishLoadingDataListener).execute();
    }

    @Override
    public void updateData() {
        setScheduledTasksData();
        mTasksListAdapter.notifyDataSetChanged();
    }

    private void setScheduledTasksData() {
        mTasksDataSet.clear();

        // WIP task
        //mTasksDataSet.add(new TasksListItem(new Task(mMainActivity.getString(R.string.wip_task)), ItemViewType.TITLE));
        //mTasksDataSet.add(new TasksListItem(WorkingData.getInstance(mMainActivity).getWipTask(), ItemViewType.WIP_TASK));

        // Scheduled task
        mTasksDataSet.add(new TasksListItem(new Task(mContext.getString(R.string.next_task)), TasksListAdapter.ItemViewType.TITLE));
        for (Task scheduledTask : WorkingData.getInstance(mContext).getScheduledTasks()) {
            mTasksDataSet.add(new TasksListItem(scheduledTask, TasksListAdapter.ItemViewType.SCHEDULED_TASK));
        }
    }

    @Override
    public void onClick(View v) {

    }
}
