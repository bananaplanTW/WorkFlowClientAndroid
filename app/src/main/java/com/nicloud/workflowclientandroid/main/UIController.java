package com.nicloud.workflowclientandroid.main;

import android.os.Bundle;
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
import com.nicloud.workflowclientandroid.data.Task;

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

    private View mWipTaskCard;
    private TextView mWipTaskPauseButton;
    private TextView mWipTaskCompleteButton;

    private RecyclerView mScheduledTasksList;
    private LinearLayoutManager mScheduledTasksListManager;
    private ScheduledTasksAdapter mScheduledTasksAdapter;
    private List<Task> mScheduledTasksDataSet = new ArrayList<>();



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

    private void initialize() {
        findViews();
        setupActionbar();
        setupViews();
        setupScheduledTasksList();
    }

    private void findViews() {
        mToolbar = (Toolbar) mMainActivity.findViewById(R.id.tool_bar);
        mWipTaskCard = mMainActivity.findViewById(R.id.wip_task_card);
        mWipTaskPauseButton = (TextView) mMainActivity.findViewById(R.id.wip_task_card_pause_button);
        mWipTaskCompleteButton = (TextView) mMainActivity.findViewById(R.id.wip_task_card_complete_button);
        mScheduledTasksList = (RecyclerView) mMainActivity.findViewById(R.id.scheduled_tasks_list);
    }

    private void setupActionbar() {
        mMainActivity.setSupportActionBar(mToolbar);
        mActionBar = mMainActivity.getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void setupViews() {
        mWipTaskCard.setOnClickListener(this);
        mWipTaskPauseButton.setOnClickListener(this);
        mWipTaskCompleteButton.setOnClickListener(this);
    }

    private void setupScheduledTasksList() {
        setScheduledTasksData();
        mScheduledTasksListManager = new LinearLayoutManager(mMainActivity);
        mScheduledTasksAdapter = new ScheduledTasksAdapter(mMainActivity, mScheduledTasksDataSet);

        mScheduledTasksList.setLayoutManager(mScheduledTasksListManager);
        mScheduledTasksList.addItemDecoration(new ScheduledTaskCardDecoration(mMainActivity));
        mScheduledTasksList.setAdapter(mScheduledTasksAdapter);
    }

    private void setScheduledTasksData() {
        mScheduledTasksDataSet.add(new Task("檢查伺服器"));
        mScheduledTasksDataSet.add(new Task("開發Android"));
        mScheduledTasksDataSet.add(new Task("開發iOS"));
        mScheduledTasksDataSet.add(new Task("設計UI"));
        mScheduledTasksDataSet.add(new Task("跑客戶"));
    }

    @Override
    public void onClick(View v) {

    }
}
