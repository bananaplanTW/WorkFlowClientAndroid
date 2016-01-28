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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.task.LoadingWorkerTasks;
import com.nicloud.workflowclient.data.data.data.Task;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.data.data.observer.DataObserver;
import com.nicloud.workflowclient.utility.DividerItemDecoration;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2015/12/21.
 */
public class TasksListFragment extends Fragment implements DataObserver, View.OnClickListener {

    public interface OnRefreshInTasksList {
        void onRefreshInTasksList();
    }

    private Context mContext;
    private FragmentManager mFm;

    private FloatingActionButton mFab;

    private RecyclerView mTasksList;
    private LinearLayoutManager mTasksListManager;
    private TasksListAdapter mTasksListAdapter;
    private List<Task> mTasksDataSet = new ArrayList<>();

    private TextView mNoTaskText;

    private boolean mNeedRefresh = false;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private OnRefreshInTasksList mOnRefreshInTasksList;

    private LoadingWorkerTasks.OnFinishLoadingDataListener mOnFinishLoadingDataListener = new LoadingWorkerTasks.OnFinishLoadingDataListener() {
        @Override
        public void onFinishLoadingData() {
            if (!WorkingData.getInstance(mContext).hasLoadedTasks()) {
                forceHideRefreshSpinner();
                WorkingData.getInstance(mContext).setHasLoadedTasks(true);

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


    public void loadWorkerTasks() {
        new LoadingWorkerTasks(mContext, mOnFinishLoadingDataListener).execute();
    }

    private void showInternetConnectionWeakToast() {
        mSwipeRefreshLayout.setRefreshing(false);
        Utils.showInternetConnectionWeakToast(mContext);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mOnRefreshInTasksList = (OnRefreshInTasksList) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks_list, container, false);
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

        if (!WorkingData.getInstance(mContext).hasLoadedTasks()) {
            loadDataInFirstLaunch();
        } else {
            setScheduledTasksData();
        }
    }

    private void findViews() {
        mFab = (FloatingActionButton) getView().findViewById(R.id.fab);
        mTasksList = (RecyclerView) getView().findViewById(R.id.tasks_list);
        mNoTaskText = (TextView) getView().findViewById(R.id.tasks_list_no_task_text);
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.task_log_swipe_refresh_container);
    }

    private void setupViews() {
        mFab.setOnClickListener(this);
    }

    private void setNoTaskTextVisibility() {
        if (mTasksDataSet.size() == 0) {
            mNoTaskText.setVisibility(View.VISIBLE);
        } else {
            mNoTaskText.setVisibility(View.GONE);
        }
    }

    private void setupTasksList() {
        mTasksListManager = new LinearLayoutManager(mContext);
        mTasksListAdapter = new TasksListAdapter(mContext, mFm, mTasksDataSet);

        mTasksList.setLayoutManager(mTasksListManager);
        mTasksList.addItemDecoration(
                new DividerItemDecoration(mContext.getResources().getDrawable(R.drawable.list_divider),
                        true, true, true, mContext.getResources().getDimensionPixelSize(R.dimen.tasks_list_padding_bottom)));
        mTasksList.setAdapter(mTasksListAdapter);

        mTasksList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (!mNeedRefresh) break;

                        if (!isFirstItemOnTheTopOfTheList()) {
                            mNeedRefresh = false;
                            mSwipeRefreshLayout.setEnabled(false);
                        }

                        break;

                    case MotionEvent.ACTION_UP:
                        if (isFirstItemOnTheTopOfTheList()) {
                            mNeedRefresh = true;
                            mSwipeRefreshLayout.setEnabled(true);
                        }

                        break;
                }

                return false;
            }
        });
    }

    private boolean isFirstItemOnTheTopOfTheList() {
        return mTasksListManager.findFirstVisibleItemPosition() == 0;
    }

    private void setupSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mOnRefreshInTasksList.onRefreshInTasksList();
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

    @Override
    public void updateData() {
        setScheduledTasksData();
    }

    private void setScheduledTasksData() {
        mTasksDataSet.clear();

        for (Task task : WorkingData.getInstance(mContext).getTasks()) {
            mTasksDataSet.add(task);
        }

        mTasksListAdapter.notifyDataSetChanged();
        setNoTaskTextVisibility();
    }

    @Override
    public void onClick(View v) {

    }
}
