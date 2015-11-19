package com.nicloud.workflowclientandroid.tasklog.log;

import android.content.Context;
import android.content.Intent;
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

import com.nicloud.workflowclientandroid.R;
import com.nicloud.workflowclientandroid.data.data.Task;
import com.nicloud.workflowclientandroid.data.data.WorkingData;
import com.nicloud.workflowclientandroid.tasklog.add.AddLogActivity;
import com.nicloud.workflowclientandroid.utility.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class TaskLogActivity extends AppCompatActivity implements TabHost.OnTabChangeListener {

    public static final String EXTRA_TASK_ID = "TaskLogActivity_extra_task_id";

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

    private RecyclerView mTaskLogListView;
    private LinearLayoutManager mTaskLogListViewLayoutManager;
    private TaskLogListAdapter mTaskLogListAdapter;
    private List<LogItem> mLogListDataSet = new ArrayList<>();

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
    }

    private void initialize() {
        mTask = WorkingData.getInstance(this).getTask(getIntent().getStringExtra(EXTRA_TASK_ID));
        findViews();
        setupActionBar();
        setupTabs();
        setupRecordLog();
    }

    private void findViews() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mLogTaskName = (TextView) findViewById(R.id.task_log_task_name);
        mLogCaseName = (TextView) findViewById(R.id.task_log_case_name);
        mTaskLogTabHost = (TabHost) findViewById(R.id.task_log_tab_host);
        mTaskLogListView = (RecyclerView) findViewById(R.id.task_log_list);
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
        setRecordLogData();

        mTaskLogListViewLayoutManager = new LinearLayoutManager(this);
        mTaskLogListAdapter = new TaskLogListAdapter(this, mLogListDataSet);

        mTaskLogListView.setLayoutManager(mTaskLogListViewLayoutManager);
        mTaskLogListView.addItemDecoration(
                new DividerItemDecoration(getResources().getDrawable(R.drawable.list_divider), false, true));
        mTaskLogListView.setAdapter(mTaskLogListAdapter);
    }

    private void setRecordLogData() {
        mLogListDataSet.add(new LogItem("Danny", "多益公司的進項與之前不同 下次審查要注意", "2015/11/12 12:06 pm"));
    }

    private void addTab(String tag) {
        View tabView = getTabView(tag);
        mTaskLogTabHost.addTab(mTaskLogTabHost.newTabSpec(tag).setIndicator(getTabView(tag))
                .setContent(new LogTabContentFactory(this)));
    }

    private View getTabView(String tag) {
        View tabView = LayoutInflater.from(this).inflate(R.layout.task_log_tab, null);
        TextView tabText = (TextView) tabView.findViewById(R.id.record_log_tab_text);

        String text = "";
        if(TabTag.TEXT.equals(tag)) {
            text = getString(R.string.record_log_tab_text);
        } else if(TabTag.PHOTO.equals(tag)) {
            text = getString(R.string.record_log_tab_photo);
        } else if(TabTag.FILE.equals(tag)) {
            text = getString(R.string.record_log_tab_file);
        }

        tabText.setText(text);

        return tabView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_record_log, menu);
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
        startActivity(new Intent(this, AddLogActivity.class));
    }

    @Override
    public void onTabChanged(String tabId) {

    }
}
