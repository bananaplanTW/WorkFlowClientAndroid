package com.nicloud.workflowclient.dialog.chooseworker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.service.TaskService;
import com.nicloud.workflowclient.data.data.Worker;
import com.nicloud.workflowclient.main.WorkingData;
import com.nicloud.workflowclient.utility.DividerItemDecoration;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ChooseWorkerActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "ChooseWorkerActivity_extra_task_id";
    public static final String EXTRA_OWNER_ID = "ChooseWorkerActivity_extra_owner_id";

    private RecyclerView mWorkerList;
    private LinearLayoutManager mWorkerListLayoutManager;
    private WorkerListAdapter mWorkerListAdapter;

    private List<ChooseWorkerItem> mWorkerListData = new ArrayList<>();

    private String mTaskId;
    private String mOwnerId;


    public class ChooseWorkerItem {

        public boolean isSelected = false;
        public Worker worker;


        public ChooseWorkerItem(boolean isSelected, Worker worker) {
            this.isSelected = isSelected;
            this.worker = worker;
        }
    }

    public static Intent generateChooseWorkerIntent(Context context, String taskId, String ownerId) {
        Intent intent = new Intent(context, ChooseWorkerActivity.class);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        intent.putExtra(EXTRA_OWNER_ID, ownerId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_worker);

        // Load worker

        initialize();
    }

    private void initialize() {
        mTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        mOwnerId = getIntent().getStringExtra(EXTRA_OWNER_ID);

        findViews();
        setupActionBar();
        setupWorkerList();
    }

    private void findViews() {
        mWorkerList = (RecyclerView) findViewById(R.id.worker_list);
    }

    private void setupActionBar() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolBar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.choose_worker_title));
    }

    private void setupWorkerList() {
        setWorkerListData();
        mWorkerListAdapter = new WorkerListAdapter(this, mWorkerListData);
        mWorkerListLayoutManager = new LinearLayoutManager(this);

        mWorkerList.addItemDecoration(new DividerItemDecoration(
                getResources().getDrawable(R.drawable.list_divider), false, true, false, 0));
        mWorkerList.setLayoutManager(mWorkerListLayoutManager);
        mWorkerList.setAdapter(mWorkerListAdapter);
    }

    private void setWorkerListData() {
        for (Worker worker : WorkingData.getInstance(this).getWorkers()) {
            mWorkerListData.add(new ChooseWorkerItem(Utils.isSameId(worker.id, mOwnerId), worker));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_choose_worker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        ChooseWorkerItem selectedItem = mWorkerListAdapter.getSelectedChooseWorkerItem();
        if (selectedItem != null && !Utils.isSameId(selectedItem.worker.id, mOwnerId)) {
            startService(TaskService.generateAssignTaskToWorkerIntent(this, mTaskId, selectedItem.worker.id));
        }

        super.onDestroy();
    }
}
