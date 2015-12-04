package com.nicloud.workflowclient.tasklog.log;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.activity.ILoadingActivitiesStrategy;
import com.nicloud.workflowclient.data.connectserver.activity.LoadingActivitiesAsyncTask;
import com.nicloud.workflowclient.data.connectserver.activity.LoadingTaskActivitiesStrategy;
import com.nicloud.workflowclient.data.connectserver.tasklog.OnLoadImageListener;
import com.nicloud.workflowclient.data.data.activity.ActivityDataFactory;
import com.nicloud.workflowclient.data.data.activity.BaseData;
import com.nicloud.workflowclient.data.data.data.Task;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.tasklog.add.AddLogActivity;
import com.nicloud.workflowclient.utility.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskLogActivity extends AppCompatActivity implements TabHost.OnTabChangeListener,
        LoadingActivitiesAsyncTask.OnFinishLoadingDataListener, OnLoadImageListener {

    public static final String EXTRA_TASK_ID = "TaskLogActivity_extra_task_id";

    private static final int REQUEST_ADD_LOG = 32;
    private static final int TASK_LOG_LIMIT = 15;

    private static final class TabPosition {
        public static final int TEXT = 0;
        public static final int PHOTO = 1;
        public static final int FILE = 2;
    }

    private static final class TabTag {
        public static final String TEXT = "tag_tab_text";
        public static final String PHOTO = "tag_tab_photo";
        public static final String FILE = "tag_tab_file";
    }

    private ActionBar mActionBar;
    private Toolbar mToolbar;

    private TextView mLogTaskName;
    private TextView mLogCaseName;

    private TabHost mTaskLogTabHost;
    private int mSelectedTabPosition = TabPosition.TEXT;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mTaskLogListView;
    private LinearLayoutManager mTaskLogListViewLayoutManager;
    private TaskLogListAdapter mTaskLogListAdapter;

    private List<BaseData> mTextDataSet = new ArrayList<>();
    private List<BaseData> mPhotoDataSet = new ArrayList<>();
    private List<BaseData> mFileDataSet = new ArrayList<>();

    private TextView mNoLogText;

    private Task mTask;

    private class LogTabContentFactory implements TabHost.TabContentFactory {

        private Context mContext;

        public LogTabContentFactory(Context context) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_log);
        initialize();
        loadTaskActivities();
    }

    private void initialize() {
        mTask = WorkingData.getInstance(this).getTask(getIntent().getStringExtra(EXTRA_TASK_ID));
        findViews();
        setupActionBar();
        setupTabs();
        setupRecordLog();
        setupSwipeRefreshLayout();
    }

    private void loadTaskActivities() {
        LoadingTaskActivitiesStrategy loadingTaskActivitiesStrategy =
                new LoadingTaskActivitiesStrategy(mTask.id, TASK_LOG_LIMIT);
        LoadingActivitiesAsyncTask loadingWorkerActivitiesTask =
                new LoadingActivitiesAsyncTask(this, mTask.id, this, loadingTaskActivitiesStrategy);
        loadingWorkerActivitiesTask.execute();
    }

    private void findViews() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mLogTaskName = (TextView) findViewById(R.id.task_log_task_name);
        mLogCaseName = (TextView) findViewById(R.id.task_log_case_name);
        mTaskLogTabHost = (TabHost) findViewById(R.id.task_log_tab_host);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_container);
        mTaskLogListView = (RecyclerView) findViewById(R.id.task_log_list);
        mNoLogText = (TextView) findViewById(R.id.task_log_no_log_text);
    }

    private void setupActionBar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mLogTaskName.setText(mTask.name);
        mLogCaseName.setText(mTask.caseName);
    }

    private void setupTabs() {
        mTaskLogTabHost.setup();
        addTab(TabTag.TEXT);
        addTab(TabTag.PHOTO);
        addTab(TabTag.FILE);
        mTaskLogTabHost.setOnTabChangedListener(this);
    }

    private void setupRecordLog() {
        mTaskLogListViewLayoutManager = new LinearLayoutManager(this);
        mTaskLogListAdapter = new TaskLogListAdapter(this);

        mTaskLogListView.setLayoutManager(mTaskLogListViewLayoutManager);
        mTaskLogListView.addItemDecoration(
                new DividerItemDecoration(getResources().getDrawable(R.drawable.list_divider), false, true));
        mTaskLogListView.setAdapter(mTaskLogListAdapter);
    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                loadTaskActivities();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void addTab(String tag) {
        mTaskLogTabHost.addTab(mTaskLogTabHost.newTabSpec(tag).setIndicator(getTabView(tag))
                .setContent(new LogTabContentFactory(this)));
    }

    private View getTabView(String tag) {
        View tabView = LayoutInflater.from(this).inflate(R.layout.task_log_tab, null);
        TextView tabText = (TextView) tabView.findViewById(R.id.record_log_tab_text);

        String text = "";
        if(TabTag.TEXT.equals(tag)) {
            text = getString(R.string.task_log_tab_text);
        } else if(TabTag.PHOTO.equals(tag)) {
            text = getString(R.string.task_log_tab_photo);
        } else if(TabTag.FILE.equals(tag)) {
            text = getString(R.string.task_log_tab_file);
        }

        tabText.setText(text);

        return tabView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_add_record:
                goToAddRecordActivity();
                return true;

            default:
                return false;
        }
    }

    private void goToAddRecordActivity() {
        Intent intent = new Intent(this, AddLogActivity.class);
        intent.putExtra(AddLogActivity.EXTRA_TASK_ID, mTask.id);

        startActivityForResult(intent, REQUEST_ADD_LOG);
    }

    @Override
    public void onTabChanged(String tabId) {
        mSelectedTabPosition = mTaskLogTabHost.getCurrentTab();
        updateTaskLogListAccordingToTab();
    }

    @Override
    public void onFinishLoadingData(String id, ILoadingActivitiesStrategy.ActivityCategory category, JSONArray activities) {
        if (activities == null) return;

        setTaskLogData(parseActivityJSONArray(activities));
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void setTaskLogData(ArrayList<BaseData> logData) {
        mTextDataSet.clear();
        mPhotoDataSet.clear();
        mFileDataSet.clear();

        for (BaseData data : logData) {
            switch (data.type) {
                case RECORD:
                    mTextDataSet.add(data);
                    break;

                case PHOTO:
                    mPhotoDataSet.add(data);
                    break;

                case FILE:
                    mFileDataSet.add(data);
                    break;
            }
        }

        updateTaskLogListAccordingToTab();
    }

    private void updateTaskLogListAccordingToTab() {
        switch (mSelectedTabPosition) {
            case TabPosition.TEXT:
                mTaskLogListAdapter.swapDataSet(mTextDataSet);

                break;

            case TabPosition.PHOTO:
                mTaskLogListAdapter.swapDataSet(mPhotoDataSet);

                break;

            case TabPosition.FILE:
                mTaskLogListAdapter.swapDataSet(mFileDataSet);

                break;
        }

        setNoLogTextVisibility();
    }

    private void setNoLogTextVisibility() {
        if (mTaskLogListAdapter.getItemCount() == 0) {
            mNoLogText.setVisibility(View.VISIBLE);

        } else {
            mNoLogText.setVisibility(View.GONE);
        }
    }

    private ArrayList<BaseData> parseActivityJSONArray(JSONArray activities) {
        ArrayList<BaseData> parsedActivities = new ArrayList<>();

        int length = activities.length();

        try {
            for (int i = 0; i < length; i++) {
                JSONObject activity = activities.getJSONObject(i);
                BaseData activityData = ActivityDataFactory.genData(activity, this, this);
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
    public void onFailLoadingData(boolean isFailCausedByInternet) {

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
    public void onFinishLoadImage() {
        mTaskLogListAdapter.notifyDataSetChanged();
    }
}
