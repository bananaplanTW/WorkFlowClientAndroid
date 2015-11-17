package com.nicloud.workflowclientandroid.main;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nicloud.workflowclientandroid.MainApplication;
import com.nicloud.workflowclientandroid.R;
import com.nicloud.workflowclientandroid.data.data.Task;
import com.nicloud.workflowclientandroid.data.loading.LoadingDataTask;
import com.nicloud.workflowclientandroid.data.loading.LoadingDataTask.OnFinishLoadingDataListener;
import com.nicloud.workflowclientandroid.data.worker.CheckinCommand;
import com.nicloud.workflowclientandroid.main.tasklist.TasksListAdapter;
import com.nicloud.workflowclientandroid.main.tasklist.TasksListAdapter.ItemViewType;
import com.nicloud.workflowclientandroid.main.tasklist.TasksListItem;
import com.nicloud.workflowclientandroid.utility.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


/**
 * Main component to control the UI
 *
 * @author Danny Lin
 * @since 2015.05.28
 *
 */
public class UIController implements View.OnClickListener {

    private AppCompatActivity mMainActivity;
    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private View mActionBarTitleContainer;

    private FloatingActionButton mFab;

    private RecyclerView mTasksList;
    private LinearLayoutManager mTasksListManager;
    private TasksListAdapter mTasksListAdapter;
    private List<TasksListItem> mTasksDataSet = new ArrayList<>();

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private OnFinishLoadingDataListener mOnFinishLoadingDataListener = new OnFinishLoadingDataListener() {
        @Override
        public void onFinishLoadingData() {
            if (mFirstLaunch) {
                forceHideRefreshSpinner();

                mActionBarTitleContainer.startAnimation(MainApplication.sFadeInAnimation);
                mActionBarTitleContainer.setVisibility(View.VISIBLE);

                mFab.show();

                mFirstLaunch = false;
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
            }

            setScheduledTasksData();
            mTasksListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailLoadingData(boolean isFailCausedByInternet) {

        }
    };

    private boolean mFirstLaunch = true;


    public UIController(AppCompatActivity activity) {
        mMainActivity = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        initialize();
    }

    public void onStart() {
    }

    public void onStop() {
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        mMainActivity.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            default:
                return false;
        }
    }

    public void onCompleteTaskOk() {
        mTasksListAdapter.onCompleteTaskOk();
    }

    public void onCompleteTaskCancel() {
        mTasksListAdapter.onCompleteTaskCancel();
    }

    public void onChooseTaskCancel() {
        mTasksListAdapter.onChooseTaskCancel();
    }

    public void onChooseTaskStartWork() {
        mTasksListAdapter.onChooseTaskStartWork();
    }

    public void onChooseTaskLog() {
        mTasksListAdapter.onChooseTaskLog();
    }

    private void initialize() {
        findViews();
        setupViews();
        setupActionbar();
        setupTasksList();
        setupSwipeRefresh();
        loadData();
    }

    private void setupViews() {
        mFab.setOnClickListener(this);
    }

    private void findViews() {
        mToolbar = (Toolbar) mMainActivity.findViewById(R.id.tool_bar);
        mActionBarTitleContainer = mMainActivity.findViewById(R.id.action_bar_title_container);
        mFab = (FloatingActionButton) mMainActivity.findViewById(R.id.fab);
        mTasksList = (RecyclerView) mMainActivity.findViewById(R.id.tasks_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mMainActivity.findViewById(R.id.swipe_refresh_container);
    }

    private void setupActionbar() {
        mMainActivity.setSupportActionBar(mToolbar);
        mActionBar = mMainActivity.getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void setupTasksList() {
        mTasksListManager = new LinearLayoutManager(mMainActivity);
        mTasksListAdapter = new TasksListAdapter(mMainActivity, mMainActivity.getSupportFragmentManager(), mTasksDataSet);

        mTasksList.setLayoutManager(mTasksListManager);
        mTasksList.addItemDecoration(
                new DividerItemDecoration(mMainActivity.getResources().getDrawable(R.drawable.list_divider), false, true));
        mTasksList.setAdapter(mTasksListAdapter);
    }

    private void setupSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //fetchTimelineAsync(0);
                new LoadingDataTask(mMainActivity, mOnFinishLoadingDataListener).execute();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void loadData() {
        forceShowRefreshSpinner();
        new LoadingDataTask(mMainActivity, mOnFinishLoadingDataListener).execute();
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

    private void setScheduledTasksData() {
        mTasksDataSet.clear();

        // WIP task
        mTasksDataSet.add(new TasksListItem(new Task(mMainActivity.getString(R.string.wip_task)), ItemViewType.TITLE));
        mTasksDataSet.add(new TasksListItem(new Task("伺服器服務開發", "流程管理專案"), ItemViewType.WIP_TASK));

        // Scheduled task
        mTasksDataSet.add(new TasksListItem(new Task(mMainActivity.getString(R.string.next_task)), ItemViewType.TITLE));
        mTasksDataSet.add(new TasksListItem(new Task("檢查伺服器", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("報表處理", "會計系統專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("開發iOS", "手機開發專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("設計UI", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("跑客戶", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("跑客戶", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("跑客戶", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("跑客戶", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("跑客戶", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("跑客戶", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("跑客戶", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("跑客戶", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("跑客戶", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("跑客戶", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("跑客戶", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("跑客戶", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("跑客戶", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("跑客戶", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
        mTasksDataSet.add(new TasksListItem(new Task("跑客戶", "流程管理專案"), ItemViewType.SCHEDULED_TASK));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                CheckinCommand checkinCommand = new CheckinCommand(mMainActivity, new CheckinCommand.OnFinishCheckinStatusListener() {
                    @Override
                    public void onFinished() {
                        Toast.makeText(mMainActivity, "Success!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed() {
                        Toast.makeText(mMainActivity, "fail!", Toast.LENGTH_SHORT).show();
                    }
                });
                checkinCommand.execute();
                break;
        }
    }
}
