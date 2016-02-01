package com.nicloud.workflowclient.detailedtask.main;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
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
import com.nicloud.workflowclient.backgroundtask.receiver.TaskCompletedReceiver;
import com.nicloud.workflowclient.backgroundtask.service.TaskService;
import com.nicloud.workflowclient.data.connectserver.activity.ILoadingActivitiesStrategy;
import com.nicloud.workflowclient.data.connectserver.activity.LoadingActivitiesAsyncTask;
import com.nicloud.workflowclient.data.connectserver.activity.LoadingTaskActivitiesStrategy;
import com.nicloud.workflowclient.data.data.activity.ActivityDataFactory;
import com.nicloud.workflowclient.data.data.activity.BaseData;
import com.nicloud.workflowclient.tasklist.main.Task;
import com.nicloud.workflowclient.detailedtask.checklist.CheckListFragment;
import com.nicloud.workflowclient.detailedtask.filelog.FileLogFragment;
import com.nicloud.workflowclient.detailedtask.taskinfo.TaskInfoFragment;
import com.nicloud.workflowclient.detailedtask.textlog.TextLogFragment;
import com.nicloud.workflowclient.dialog.DisplayDialogFragment;
import com.nicloud.workflowclient.backgroundtask.service.ActionService;
import com.nicloud.workflowclient.backgroundtask.receiver.UploadCompletedReceiver;
import com.nicloud.workflowclient.backgroundtask.service.UploadService;
import com.nicloud.workflowclient.utility.MainTabContentFactory;
import com.nicloud.workflowclient.utility.utils.DbUtils;
import com.nicloud.workflowclient.utility.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailedTaskActivity extends AppCompatActivity implements TabHost.OnTabChangeListener,
        LoadingActivitiesAsyncTask.OnFinishLoadingDataListener, OnRefreshDetailedTask,
        UploadCompletedReceiver.OnUploadCompletedListener, DisplayDialogFragment.OnDialogActionListener,
        ViewPager.OnPageChangeListener, TaskCompletedReceiver.OnLoadTaskCompletedListener {

    public static final String EXTRA_TASK_ID = "DetailedTaskActivity_extra_task_id";

    private static final int REQUEST_ADD_LOG = 32;
    private static final int TASK_LOG_LIMIT = 15;

    private static final class FragmentPosition {
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
    private TaskInfoFragment mTaskInfoFragment;
    private CheckListFragment mCheckListFragment;
    private TextLogFragment mTextLogFragment;
    private FileLogFragment mFileLogFragment;

    private ActionBar mActionBar;
    private Toolbar mToolbar;

    private UploadCompletedReceiver mUploadCompletedReceiver;

    private TextView mTaskName;
    private TextView mCaseName;

    private TabHost mDetailedTaskTabHost;

    private ViewPager mDetailedTaskViewPager;
    private DetailedTaskPagerAdapter mDetailedTaskPagerAdapter;
    private List<Fragment> mFragmentList = new ArrayList<>();

    private Task mTask;

    private ArrayList<BaseData> mTextDataSet = new ArrayList<>();
    private ArrayList<BaseData> mFileDataSet = new ArrayList<>();


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
        mTask = DbUtils.getTaskById(this, getIntent().getStringExtra(EXTRA_TASK_ID));
        mUploadCompletedReceiver = new UploadCompletedReceiver(this);
        findViews();
        setupActionBar();
        setupTabs();
        setupFragments();
        setupViewPager();
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
        mDetailedTaskViewPager = (ViewPager) findViewById(R.id.detailed_task_viewpager);
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
                .setContent(new MainTabContentFactory(this)));
    }

    private View getTabView(String tag) {
        View tabView = LayoutInflater.from(this).inflate(R.layout.tab, null);
        TextView tabText = (TextView) tabView.findViewById(R.id.tab_text);

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

    private void setupFragments() {
        mTaskInfoFragment = (TaskInfoFragment) mFragmentManager.findFragmentByTag(FragmentTag.TASK_INFO);
        if (mTaskInfoFragment == null) {
            mTaskInfoFragment = new TaskInfoFragment();
            addFragmentBundle(mTaskInfoFragment);
        }

        mCheckListFragment = (CheckListFragment) mFragmentManager.findFragmentByTag(FragmentTag.CHECK_LIST);
        if (mCheckListFragment == null) {
            mCheckListFragment = new CheckListFragment();
            addFragmentBundle(mCheckListFragment);
        }

        mTextLogFragment = (TextLogFragment) mFragmentManager.findFragmentByTag(FragmentTag.TEXT_LOG);
        if (mTextLogFragment == null) {
            mTextLogFragment = new TextLogFragment();
            addFragmentBundle(mTextLogFragment);
        }

        mFileLogFragment = (FileLogFragment) mFragmentManager.findFragmentByTag(FragmentTag.FILE_LOG);
        if (mFileLogFragment == null) {
            mFileLogFragment = new FileLogFragment();
            addFragmentBundle(mFileLogFragment);
        }

        mFragmentList.add(mTaskInfoFragment);
        mFragmentList.add(mCheckListFragment);
        mFragmentList.add(mTextLogFragment);
        mFragmentList.add(mFileLogFragment);
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

    private void setupViewPager() {
        mDetailedTaskPagerAdapter = new DetailedTaskPagerAdapter(mFragmentManager, this, mFragmentList);

        mDetailedTaskViewPager.setAdapter(mDetailedTaskPagerAdapter);
        mDetailedTaskViewPager.setOnPageChangeListener(this);
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
        mDetailedTaskViewPager.setCurrentItem(mDetailedTaskTabHost.getCurrentTab());
    }

    @Override
    public void onFinishLoadingData(String id, ILoadingActivitiesStrategy.ActivityCategory category, JSONArray activities) {
        if (activities == null) return;

        setTaskLogData(parseActivityJSONArray(activities));
        updateListAccordingToTab();
        ((OnSwipeRefresh) mFragmentList.get(mDetailedTaskTabHost.getCurrentTab())).setSwipeRefreshLayout(false);
    }

    private void updateListAccordingToTab() {
        switch (mDetailedTaskTabHost.getCurrentTab()) {
            case FragmentPosition.TASK_INFO:
                mTaskInfoFragment.swapData(null);
                break;

            case FragmentPosition.CHECK:
                mCheckListFragment.swapData(null);
                break;

            case FragmentPosition.TEXT:
                mTextLogFragment.swapData(mTextDataSet);
                break;

            case FragmentPosition.FILE:
                mFileLogFragment.swapData(mFileDataSet);
                break;
        }

        mDetailedTaskPagerAdapter.notifyDataSetChanged();
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
        Utils.showInternetConnectionWeakToast(this);
        ((OnSwipeRefresh) mFragmentList.get(mDetailedTaskTabHost.getCurrentTab())).setSwipeRefreshLayout(false);
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
        startService(TaskService.generateLoadTaskByIdIntent(this, mTask.id));
    }

    @Override
    public void onUploadCompletedListener(Intent intent) {
        String fromAction = intent.getStringExtra(UploadService.ExtraKey.FROM_ACTION);
        boolean isUploadSuccessful = intent.getBooleanExtra(UploadService.ExtraKey.UPLOAD_SUCCESSFUL, false);

        if ((UploadService.UploadAction.TASK_TEXT.equals(fromAction) ||
             UploadService.UploadAction.TASK_FILE.equals(fromAction)) && isUploadSuccessful) {
            loadTaskActivities();

        } else if (UploadService.UploadAction.TASK_CHECK_ITEM.equals(fromAction) && isUploadSuccessful) {
            startService(TaskService.generateLoadTaskByIdIntent(this, mTask.id));
        }
    }

    @Override
    public void onCompleteTaskOk(String taskId) {
        Intent intent = new Intent(this, ActionService.class);
        intent.setAction(ActionService.ServerAction.COMPLETE_TASK);
        intent.putExtra(ActionService.ExtraKey.TASK_ID, taskId);
        intent.putExtra(ActionService.ExtraKey.TASK_NAME, DbUtils.getTaskNameById(this, taskId));

        startService(intent);
        Utils.dismissDialog(mFragmentManager);
        finish();
    }

    @Override
    public void onCompleteTaskCancel() {
        Utils.dismissDialog(mFragmentManager);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mDetailedTaskTabHost.setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onLoadTaskCompleted(Intent intent) {
        updateListAccordingToTab();
        ((OnSwipeRefresh) mFragmentList.get(mDetailedTaskTabHost.getCurrentTab())).setSwipeRefreshLayout(false);
    }
}
