package com.nicloud.workflowclient.main.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.connectserver.worker.CheckInOutCommand;
import com.nicloud.workflowclient.data.connectserver.worker.LoadingLoginWorkerCommand;
import com.nicloud.workflowclient.data.connectserver.worker.LoadingWorkerAvatarCommand;
import com.nicloud.workflowclient.data.connectserver.worker.PauseTaskForWorkerCommand;
import com.nicloud.workflowclient.data.connectserver.worker.ShiftTaskCommand;
import com.nicloud.workflowclient.data.data.data.Task;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.data.data.observer.DataObserver;
import com.nicloud.workflowclient.data.connectserver.task.LoadingWorkerTasks;
import com.nicloud.workflowclient.data.connectserver.task.LoadingWorkerTasks.OnFinishLoadingDataListener;
import com.nicloud.workflowclient.data.connectserver.worker.CompleteTaskForWorkerCommand;
import com.nicloud.workflowclient.dialog.DisplayDialogFragment;
import com.nicloud.workflowclient.login.LoginActivity;
import com.nicloud.workflowclient.main.tasklist.TasksListAdapter;
import com.nicloud.workflowclient.main.tasklist.TasksListAdapter.ItemViewType;
import com.nicloud.workflowclient.main.tasklist.TasksListItem;
import com.nicloud.workflowclient.utility.DividerItemDecoration;
import com.nicloud.workflowclient.utility.Utilities;
import com.parse.ParsePush;

import java.util.ArrayList;
import java.util.List;


/**
 * Main component to control the UI
 *
 * @author Danny Lin
 * @since 2015.05.28
 *
 */
public class UIController implements View.OnClickListener, DataObserver,
        ShiftTaskCommand.OnFinishShiftTaskListener, TasksListAdapter.OnPauseWipTaskListener,
        PauseTaskForWorkerCommand.OnPauseTaskForWorkerListener, CheckInOutCommand.OnMainCheckInOutStatusListener,
        LoadingLoginWorkerCommand.OnLoadingLoginWorker {

    private static final String TAG = "UIController";

    private AppCompatActivity mMainActivity;
    private ActionBar mActionBar;
    private Toolbar mToolbar;

    private ImageView mActionBarWorkerAvatar;
    private TextView mActionBarWorkerName;
    private TextView mActionBarWorkerFactoryName;

    private FloatingActionButton mFab;

    private RecyclerView mTasksList;
    private LinearLayoutManager mTasksListManager;
    private TasksListAdapter mTasksListAdapter;
    private List<TasksListItem> mTasksDataSet = new ArrayList<>();

    private FragmentManager mFragmentManager;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private OnFinishLoadingDataListener mOnFinishLoadingDataListener = new OnFinishLoadingDataListener() {
        @Override
        public void onFinishLoadingData() {
            if (mFirstLaunch) {
                forceHideRefreshSpinner();
                mFirstLaunch = false;
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
            }

            //mFab.show();
            WorkingData.getInstance(mMainActivity).updateData();
        }

        @Override
        public void onFailLoadingData(boolean isFailCausedByInternet) {
            showInternetConnectionWeakToast();
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
        WorkingData.getInstance(mMainActivity).registerMinuteReceiver(mMainActivity);
        WorkingData.getInstance(mMainActivity).registerDataObserver(this);
    }

    public void onStop() {
        WorkingData.getInstance(mMainActivity).unregisterMinuteReceiver(mMainActivity);
        WorkingData.getInstance(mMainActivity).removeDataObserver(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        mMainActivity.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                ParsePush.unsubscribeInBackground("user_" + WorkingData.getUserId());
                //ParseUtils.removeLoginWorkerToParse();

                WorkingData.getInstance(mMainActivity).resetTasks();
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

    public void onCompleteTaskOk(String taskId) {
        final String completedTaskName = WorkingData.getInstance(mMainActivity).getWipTask().name;

        CompleteTaskForWorkerCommand completeTaskForWorkerCommand =
                new CompleteTaskForWorkerCommand(mMainActivity, WorkingData.getUserId(), taskId, new CompleteTaskForWorkerCommand.OnCompleteTaskForWorkerListener() {
                    @Override
                    public void onFinishCompleteTask() {
                        loadWorkerTasks();

                        Toast.makeText(mMainActivity,
                                String.format(mMainActivity.getString(R.string.complete_task), completedTaskName),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailCompleteTask() {
                        showInternetConnectionWeakToast();
                    }
                });

        completeTaskForWorkerCommand.execute();

        Utilities.dismissDialog(mFragmentManager);
    }

    public void onCompleteTaskCancel() {
        Utilities.dismissDialog(mFragmentManager);
    }

    public void onChooseTaskStartWork(String taskId) {
        if (WorkingData.getInstance(mMainActivity).getWipTask() == null) {
            ShiftTaskCommand shiftTaskCommand = new ShiftTaskCommand(mMainActivity, taskId, this);
            shiftTaskCommand.execute();

        } else {
            Toast.makeText(mMainActivity,
                    mMainActivity.getString(R.string.dialog_complete_pause_task), Toast.LENGTH_SHORT).show();
        }

        Utilities.dismissDialog(mFragmentManager);
    }

    public void onChooseTaskLog(String taskId) {
        Utilities.goToTaskLogActivity(mMainActivity, taskId);
        Utilities.dismissDialog(mFragmentManager);
    }

    public void onCheck(Location currentLocation, String currentAddress,
                        CheckInOutCommand.OnDialogCheckInOutStatusListener onDialogCheckInOutStatusListener) {
        CheckInOutCommand checkInOutCommand
                = new CheckInOutCommand(mMainActivity, currentLocation, currentAddress, onDialogCheckInOutStatusListener, this);
        checkInOutCommand.execute();
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
        mActionBarWorkerFactoryName.setText(WorkingData.getInstance(mMainActivity).getLoginWorker().factoryName);
        mFab.setOnClickListener(this);
    }

    private void findViews() {
        mToolbar = (Toolbar) mMainActivity.findViewById(R.id.tool_bar);
        mActionBarWorkerAvatar = (ImageView) mMainActivity.findViewById(R.id.action_bar_worker);
        mActionBarWorkerName = (TextView) mMainActivity.findViewById(R.id.action_bar_worker_name);
        mActionBarWorkerFactoryName = (TextView) mMainActivity.findViewById(R.id.action_bar_worker_factory_name);
        mFab = (FloatingActionButton) mMainActivity.findViewById(R.id.fab);
        mTasksList = (RecyclerView) mMainActivity.findViewById(R.id.tasks_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mMainActivity.findViewById(R.id.task_log_swipe_refresh_container);
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
        mTasksListAdapter = new TasksListAdapter(mMainActivity, mFragmentManager, mTasksDataSet, this);

        mTasksList.setLayoutManager(mTasksListManager);
        mTasksList.addItemDecoration(
                new DividerItemDecoration(mMainActivity.getResources().getDrawable(R.drawable.list_divider),
                        true, true, true, mMainActivity.getResources().getDimensionPixelSize(R.dimen.tasks_list_padding_bottom)));
        mTasksList.setAdapter(mTasksListAdapter);
    }

    private void setupSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                loadingLoginWorker();
                loadWorkerAvatar();
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
        loadWorkerAvatar();
    }

    private void loadWorkerTasks() {
        new LoadingWorkerTasks(mMainActivity, mOnFinishLoadingDataListener).execute();
    }

    private void loadWorkerAvatar() {
        Drawable avatar = WorkingData.getInstance(mMainActivity).getLoginWorker().avatar;
        String s = WorkingData.getInstance(mMainActivity).getLoginWorker().avatarUrl;

        if (TextUtils.isEmpty(s)) {
            if (avatar == null) {
                mActionBarWorkerAvatar.setImageResource(R.drawable.ic_worker);
            } else {
                mActionBarWorkerAvatar.setImageDrawable(avatar);
            }

            return;
        }

        Uri.Builder avatarBuilder = Uri.parse(LoadingDataUtils.sBaseUrl).buildUpon();
        avatarBuilder.path(s);
        Uri avatarUri = avatarBuilder.build();

        LoadingWorkerAvatarCommand loadingWorkerAvatarCommand
                = new LoadingWorkerAvatarCommand(mMainActivity, avatarUri, mActionBarWorkerAvatar);
        loadingWorkerAvatarCommand.execute();
    }

    private void loadingLoginWorker() {
        LoadingLoginWorkerCommand loadingLoginWorkerCommand = new LoadingLoginWorkerCommand(mMainActivity, this);
        loadingLoginWorkerCommand.execute();
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
        //mTasksDataSet.add(new TasksListItem(new Task(mMainActivity.getString(R.string.wip_task)), ItemViewType.TITLE));
        //mTasksDataSet.add(new TasksListItem(WorkingData.getInstance(mMainActivity).getWipTask(), ItemViewType.WIP_TASK));

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
                Utilities.showDialog(mFragmentManager, DisplayDialogFragment.DialogType.CHECK_IN_OUT, null);
                break;
        }
    }

    @Override
    public void updateData() {
        setScheduledTasksData();
        mTasksListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onShiftTaskFinished() {
        loadWorkerTasks();
    }

    @Override
    public void onShiftTaskFailed() {
        showInternetConnectionWeakToast();
    }

    private void showInternetConnectionWeakToast() {
        mSwipeRefreshLayout.setRefreshing(false);
        Utilities.showInternetConnectionWeakToast(mMainActivity);
    }

    @Override
    public void onPauseWipTask(String taskId) {
        PauseTaskForWorkerCommand pauseTaskForWorkerCommand = new PauseTaskForWorkerCommand(mMainActivity, taskId, this);
        pauseTaskForWorkerCommand.execute();
    }

    @Override
    public void onFinishPauseTask() {
        loadWorkerTasks();
    }

    @Override
    public void onFailPauseTask() {
        showInternetConnectionWeakToast();
    }

    @Override
    public void onCheckInOutFinished() {
        loadWorkerTasks();
    }

    @Override
    public void onCheckInOutFailed() {
        showInternetConnectionWeakToast();
    }

    @Override
    public void onLoadingLoginWorkerSuccessful() {
        mActionBarWorkerName.setText(WorkingData.getInstance(mMainActivity).getLoginWorker().name);
        mActionBarWorkerFactoryName.setText(WorkingData.getInstance(mMainActivity).getLoginWorker().factoryName);
        loadWorkerTasks();
    }

    @Override
    public void onLoadingLoginWorkerFailed(boolean isFailCausedByInternet) {
        showInternetConnectionWeakToast();
    }
}
