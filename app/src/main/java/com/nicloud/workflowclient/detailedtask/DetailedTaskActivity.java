package com.nicloud.workflowclient.detailedtask;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.activity.ILoadingActivitiesStrategy;
import com.nicloud.workflowclient.data.connectserver.activity.LoadingActivitiesAsyncTask;
import com.nicloud.workflowclient.data.connectserver.activity.LoadingTaskActivitiesStrategy;
import com.nicloud.workflowclient.data.connectserver.task.LoadingTaskById;
import com.nicloud.workflowclient.data.data.activity.ActivityDataFactory;
import com.nicloud.workflowclient.data.data.activity.BaseData;
import com.nicloud.workflowclient.data.data.data.Task;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.detailedtask.checklist.CheckListFragment;
import com.nicloud.workflowclient.detailedtask.filelog.FileLogFragment;
import com.nicloud.workflowclient.detailedtask.taskinfo.TaskInfoFragment;
import com.nicloud.workflowclient.detailedtask.textlog.TextLogFragment;
import com.nicloud.workflowclient.serveraction.UploadCompletedReceiver;
import com.nicloud.workflowclient.serveraction.UploadService;
import com.nicloud.workflowclient.utility.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailedTaskActivity extends AppCompatActivity implements TabHost.OnTabChangeListener,
        LoadingActivitiesAsyncTask.OnFinishLoadingDataListener, OnRefreshDetailedTask,
        LoadingTaskById.OnFinishLoadingTaskByIdListener, UploadCompletedReceiver.OnUploadCompletedListener {

    public static final String EXTRA_TASK_ID = "DetailedTaskActivity_extra_task_id";

    private static final int REQUEST_ADD_LOG = 32;
    private static final int TASK_LOG_LIMIT = 15;

    private static final class TabPosition {
        public static final int TASK_INFO = 0;
        public static final int CHECK = 1;
        public static final int TEXT = 2;
        public static final int FILE = 3;
    }

    private static final class TabTag {
        public static final String TASK_INFO = "tag_tab_task_info";
        public static final String CHECK = "tag_tab_check";
        public static final String TEXT = "tag_tab_text";
        public static final String FILE = "tag_tab_file";
    }

    private static final class FragmentTag {
        public static final String TASK_INFO = "tag_fragment_task_info";
        public static final String CHECK_LIST = "tag_fragment_check_list";
        public static final String TEXT_LOG = "tag_fragment_text_log";
        public static final String FILE_LOG = "tag_fragment_file_log";
    }

    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;

    private ActionBar mActionBar;
    private Toolbar mToolbar;

    private UploadCompletedReceiver mUploadCompletedReceiver;

    private TextView mTaskName;
    private TextView mCaseName;

    private TabHost mDetailedTaskTabHost;

    private Task mTask;

    private ArrayList<BaseData> mTextDataSet = new ArrayList<>();
    private ArrayList<BaseData> mFileDataSet = new ArrayList<>();


    private class DetailedTaskTabContentFactory implements TabHost.TabContentFactory {

        private Context mContext;

        public DetailedTaskTabContentFactory(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            v.setVisibility(View.GONE);
            return v;
        }
    }

    public static Intent generateActivityIntent(Context context, String taskId) {
        Intent intent = new Intent(context, DetailedTaskActivity.class);
        intent.putExtra(EXTRA_TASK_ID, taskId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_task);
        initialize();
    }

    private void initialize() {
        mFragmentManager = getSupportFragmentManager();
        mTask = WorkingData.getInstance(this).getTask(getIntent().getStringExtra(EXTRA_TASK_ID));
        mUploadCompletedReceiver = new UploadCompletedReceiver(this);
        findViews();
        setupActionBar();
        setupTabs();
        setupInitFragment();
        loadTaskActivities();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(UploadService.UploadAction.UPLOAD_COMPLETED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mUploadCompletedReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mUploadCompletedReceiver);
    }

    private void findViews() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mTaskName = (TextView) findViewById(R.id.detailed_task_task_name);
        mCaseName = (TextView) findViewById(R.id.detailed_task_case_name);
        mDetailedTaskTabHost = (TabHost) findViewById(R.id.detailed_task_tab_host);
    }

    private void setupActionBar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mTaskName.setText(mTask.name);
        mCaseName.setText(mTask.caseName);
    }

    private void setupTabs() {
        mDetailedTaskTabHost.setup();
        addTab(TabTag.TASK_INFO);
        addTab(TabTag.CHECK);
        addTab(TabTag.TEXT);
        addTab(TabTag.FILE);
        mDetailedTaskTabHost.setOnTabChangedListener(this);
    }

    private void addTab(String tag) {
        mDetailedTaskTabHost.addTab(mDetailedTaskTabHost.newTabSpec(tag).setIndicator(getTabView(tag))
                .setContent(new DetailedTaskTabContentFactory(this)));
    }

    private View getTabView(String tag) {
        View tabView = LayoutInflater.from(this).inflate(R.layout.detailed_task_tab, null);
        TextView tabText = (TextView) tabView.findViewById(R.id.detailed_task_tab_text);

        String text = "";
        if(TabTag.TASK_INFO.equals(tag)) {
            text = getString(R.string.detailed_task_tab_task_info);
        } else if(TabTag.CHECK.equals(tag)) {
            text = getString(R.string.detailed_task_tab_check_list);
        } else if(TabTag.TEXT.equals(tag)) {
            text = getString(R.string.detailed_task_tab_text);
        } else if(TabTag.FILE.equals(tag)) {
            text = getString(R.string.detailed_task_tab_file);
        }

        tabText.setText(text);

        return tabView;
    }

    private void setupInitFragment() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        TaskInfoFragment taskInfoFragment
                = (TaskInfoFragment) mFragmentManager.findFragmentByTag(FragmentTag.TASK_INFO);
        if (taskInfoFragment == null) {
            taskInfoFragment = new TaskInfoFragment();
            addFragmentBundle(taskInfoFragment);

            fragmentTransaction.add(R.id.detailed_task_content, taskInfoFragment, FragmentTag.TASK_INFO);
        }

        mCurrentFragment = taskInfoFragment;
        fragmentTransaction.commit();
    }

    private void loadTaskActivities() {
        LoadingTaskActivitiesStrategy loadingTaskActivitiesStrategy =
                new LoadingTaskActivitiesStrategy(mTask.id, TASK_LOG_LIMIT);
        LoadingActivitiesAsyncTask loadingWorkerActivitiesTask =
                new LoadingActivitiesAsyncTask(this, mTask.id, this, loadingTaskActivitiesStrategy);
        loadingWorkerActivitiesTask.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        switch (mDetailedTaskTabHost.getCurrentTab()) {
            case TabPosition.TASK_INFO:
                if (mCurrentFragment instanceof TaskInfoFragment) return;
                replaceTo(TaskInfoFragment.class, FragmentTag.TASK_INFO);

                break;

            case TabPosition.CHECK:
                if (mCurrentFragment instanceof CheckListFragment) return;
                replaceTo(CheckListFragment.class, FragmentTag.CHECK_LIST);

                break;

            case TabPosition.TEXT:
                if (mCurrentFragment instanceof TextLogFragment) return;
                replaceTo(TextLogFragment.class, FragmentTag.TEXT_LOG);

                break;

            case TabPosition.FILE:
                if (mCurrentFragment instanceof FileLogFragment) return;
                replaceTo(FileLogFragment.class, FragmentTag.FILE_LOG);

                break;
        }
    }

    private void replaceTo(Class<?> fragmentClass, String fragmentTag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Fragment fragment = mFragmentManager.findFragmentByTag(fragmentTag);
        if (fragment == null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                addFragmentBundle(fragment);

                fragmentTransaction.replace(R.id.detailed_task_content, fragment, fragmentTag);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        mCurrentFragment = fragment;
        fragmentTransaction.commit();
    }

    private void addFragmentBundle(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TASK_ID, mTask.id);

        if (fragment instanceof TextLogFragment) {
            bundle.putParcelableArrayList(TextLogFragment.EXTRA_TEXT_LOG, mTextDataSet);

        } else if (fragment instanceof FileLogFragment) {
            bundle.putParcelableArrayList(FileLogFragment.EXTRA_FILE_LOG, mFileDataSet);
        }

        fragment.setArguments(bundle);
    }

    @Override
    public void onFinishLoadingData(String id, ILoadingActivitiesStrategy.ActivityCategory category, JSONArray activities) {
        if (activities == null) return;

        setTaskLogData(parseActivityJSONArray(activities));
        updateListAccordingToTab();
        ((OnSwipeRefresh) mCurrentFragment).setSwipeRefreshLayout(false);
    }

    private void updateListAccordingToTab() {
        switch (mDetailedTaskTabHost.getCurrentTab()) {
            case TabPosition.CHECK:
                ((OnSwipeRefresh) mCurrentFragment).swapData(null);
                break;

            case TabPosition.TEXT:
                ((OnSwipeRefresh) mCurrentFragment).swapData(mTextDataSet);
                break;

            case TabPosition.FILE:
                ((OnSwipeRefresh) mCurrentFragment).swapData(mFileDataSet);
                break;
        }
    }

    private void setTaskLogData(ArrayList<BaseData> logData) {
        mTextDataSet.clear();
        mFileDataSet.clear();

        for (BaseData data : logData) {
            switch (data.type) {
                case RECORD:
                    mTextDataSet.add(data);
                    break;

                case PHOTO:
                case FILE:
                    mFileDataSet.add(data);
                    break;
            }
        }
    }

    @Override
    public void onFailLoadingData(boolean isFailCausedByInternet) {
        Utilities.showInternetConnectionWeakToast(this);
        ((OnSwipeRefresh) mCurrentFragment).setSwipeRefreshLayout(false);
    }

    private ArrayList<BaseData> parseActivityJSONArray(JSONArray activities) {
        ArrayList<BaseData> parsedActivities = new ArrayList<>();

        int length = activities.length();

        try {
            for (int i = 0; i < length; i++) {
                JSONObject activity = activities.getJSONObject(i);
                BaseData activityData = ActivityDataFactory.genData(activity, this);
                if (activityData != null) {
                    parsedActivities.add(activityData);
                }
            }
            return parsedActivities;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ADD_LOG:
                if (RESULT_OK != resultCode) return;
                loadTaskActivities();

                break;
        }
    }

    @Override
    public void onRefreshDetailedTask() {
        loadTaskActivities();
        new LoadingTaskById(this, mTask.id, this).execute();
    }

    @Override
    public void onFinishLoadingTaskById() {
        updateListAccordingToTab();
        ((OnSwipeRefresh) mCurrentFragment).setSwipeRefreshLayout(false);
    }

    @Override
    public void onFailLoadingTaskById(boolean isFailCausedByInternet) {
        Utilities.showInternetConnectionWeakToast(this);
        ((OnSwipeRefresh) mCurrentFragment).setSwipeRefreshLayout(false);
    }

    @Override
    public void onUploadCompletedListener(Intent intent) {
        String fromAction = intent.getStringExtra(UploadService.ExtraKey.FROM_ACTION);
        boolean isUploadSuccessful = intent.getBooleanExtra(UploadService.ExtraKey.UPLOAD_SUCCESSFUL, false);

        if ((UploadService.UploadAction.TEXT.equals(fromAction) || UploadService.UploadAction.FILE.equals(fromAction))
                && isUploadSuccessful) {
            loadTaskActivities();

        } else if (UploadService.UploadAction.CHECK_ITEM.equals(fromAction) && isUploadSuccessful) {
            new LoadingTaskById(this, mTask.id, this).execute();
        }
    }
}
