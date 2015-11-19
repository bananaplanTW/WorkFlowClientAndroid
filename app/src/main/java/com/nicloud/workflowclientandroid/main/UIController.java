package com.nicloud.workflowclientandroid.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nicloud.workflowclientandroid.R;
import com.nicloud.workflowclientandroid.data.data.Task;
import com.nicloud.workflowclientandroid.data.data.WorkingData;
import com.nicloud.workflowclientandroid.data.loading.LoadingWorkerTasks;
import com.nicloud.workflowclientandroid.data.loading.LoadingWorkerTasks.OnFinishLoadingDataListener;
import com.nicloud.workflowclientandroid.dialog.DisplayDialogFragment;
import com.nicloud.workflowclientandroid.login.LoginActivity;
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

    private TextView mActionBarWorkerName;
    private TextView mActionBarSubtitle;

    private FloatingActionButton mFab;

    private RecyclerView mTasksList;
    private LinearLayoutManager mTasksListManager;
    private TasksListAdapter mTasksListAdapter;
    private List<TasksListItem> mTasksDataSet = new ArrayList<>();

    private FragmentManager mFragmentManager;
    private DisplayDialogFragment mDisplayDialogFragment;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private OnFinishLoadingDataListener mOnFinishLoadingDataListener = new OnFinishLoadingDataListener() {
        @Override
        public void onFinishLoadingData() {
            if (mFirstLaunch) {
                forceHideRefreshSpinner();
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
            case R.id.action_logout:
                //ParsePush.unsubscribeInBackground("user_" + WorkingData.getUserId());

                WorkingData.resetAccount();
                SharedPreferences sharedPreferences = mMainActivity.getSharedPreferences(WorkingData.SHARED_PREFERENCE_KEY, 0);
                sharedPreferences.edit().remove(WorkingData.USER_ID).remove(WorkingData.AUTH_TOKEN).commit();

                mMainActivity.startActivity(new Intent(mMainActivity, LoginActivity.class));
                mMainActivity.finish();

                return true;

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

    public void onCheck() {
//        CheckinCommand checkinCommand = new CheckinCommand(mMainActivity, new CheckinCommand.OnFinishCheckinStatusListener() {
//            @Override
//            public void onFinished() {
//                Toast.makeText(mMainActivity, "Success!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailed() {
//                Toast.makeText(mMainActivity, "fail!", Toast.LENGTH_SHORT).show();
//            }
//        });
//        checkinCommand.execute();
    }

    private void initialize() {
        mFragmentManager = mMainActivity.getSupportFragmentManager();
        findViews();
        setupViews();
        setupActionbar();
        setupTasksList();
        setupSwipeRefresh();
        loadDataInFirstLaunch();
    }

    private void setupViews() {
        mActionBarWorkerName.setText(WorkingData.getInstance(mMainActivity).getLoginWorker().name);
        mActionBarSubtitle.setText(WorkingData.getInstance(mMainActivity).getLoginWorker().address);
        mFab.setOnClickListener(this);
    }

    private void findViews() {
        mToolbar = (Toolbar) mMainActivity.findViewById(R.id.tool_bar);
        mActionBarWorkerName = (TextView) mMainActivity.findViewById(R.id.action_bar_worker_name);
        mActionBarSubtitle = (TextView) mMainActivity.findViewById(R.id.action_bar_subtitle);
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
        mTasksListAdapter = new TasksListAdapter(mMainActivity, mFragmentManager, mTasksDataSet);

        mTasksList.setLayoutManager(mTasksListManager);
        mTasksList.addItemDecoration(
                new DividerItemDecoration(mMainActivity.getResources().getDrawable(R.drawable.list_divider), false, true));
        mTasksList.setAdapter(mTasksListAdapter);
    }

    private void setupSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new LoadingWorkerTasks(mMainActivity, mOnFinishLoadingDataListener).execute();
            }

        });

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void loadDataInFirstLaunch() {
        forceShowRefreshSpinner();
        new LoadingWorkerTasks(mMainActivity, mOnFinishLoadingDataListener).execute();
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
        mTasksDataSet.add(new TasksListItem(WorkingData.getInstance(mMainActivity).getWipTask(), ItemViewType.WIP_TASK));

        // Scheduled task
        mTasksDataSet.add(new TasksListItem(new Task(mMainActivity.getString(R.string.next_task)), ItemViewType.TITLE));
        for (Task scheduledTask : WorkingData.getInstance(mMainActivity).getScheduledTasks()) {
            mTasksDataSet.add(new TasksListItem(scheduledTask, ItemViewType.SCHEDULED_TASK));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                showDialog(DisplayDialogFragment.DialogType.CHECK);
                break;
        }
    }

    private void showDialog(int type) {
        mDisplayDialogFragment =
                (DisplayDialogFragment) mFragmentManager.findFragmentByTag(DisplayDialogFragment.TAG_DISPLAY_DIALOG_FRAGMENT);
        if (mDisplayDialogFragment == null) {
            mDisplayDialogFragment = new DisplayDialogFragment();
        }

        if (mDisplayDialogFragment.isAdded()) return;

        Bundle bundle = new Bundle();
        switch (type) {
            case DisplayDialogFragment.DialogType.COMPLETE_TASK:
                bundle.putInt(DisplayDialogFragment.EXTRA_DIALOG_TYPE, DisplayDialogFragment.DialogType.COMPLETE_TASK);
                break;

            case DisplayDialogFragment.DialogType.CHOOSE_TASK:
                bundle.putInt(DisplayDialogFragment.EXTRA_DIALOG_TYPE, DisplayDialogFragment.DialogType.CHOOSE_TASK);
                break;

            case DisplayDialogFragment.DialogType.CHECK:
                bundle.putInt(DisplayDialogFragment.EXTRA_DIALOG_TYPE, DisplayDialogFragment.DialogType.CHECK);
                break;
        }

        mDisplayDialogFragment.setArguments(bundle);
        mDisplayDialogFragment.show(mFragmentManager, DisplayDialogFragment.TAG_DISPLAY_DIALOG_FRAGMENT);
    }

    private void dismissDialog() {
        if (mDisplayDialogFragment == null) return;
        mDisplayDialogFragment.dismiss();
        mDisplayDialogFragment = null;
    }
}
