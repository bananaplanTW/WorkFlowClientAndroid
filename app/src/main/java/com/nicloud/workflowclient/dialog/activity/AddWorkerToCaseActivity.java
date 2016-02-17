package com.nicloud.workflowclient.dialog.activity;

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
import com.nicloud.workflowclient.backgroundtask.service.GeneralService;
import com.nicloud.workflowclient.data.data.Worker;
import com.nicloud.workflowclient.workerlist.WorkerListAdapter;
import com.nicloud.workflowclient.main.WorkingData;
import com.nicloud.workflowclient.workerlist.WorkerListItem;

import java.util.ArrayList;
import java.util.List;

public class AddWorkerToCaseActivity extends AppCompatActivity {

    public static final String EXTRA_CASE_ID = "AddWorkerToCaseActivity_extra_case_id";

    private RecyclerView mWorkerList;
    private LinearLayoutManager mWorkerListLayoutManager;
    private WorkerListAdapter mWorkerListAdapter;

    private List<WorkerListItem> mWorkerListData = new ArrayList<>();

    private String mCaseId;


    public static Intent generateAddWorkerToCaseDialogIntent(Context context) {
        Intent intent = new Intent(context, AddWorkerToCaseActivity.class);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_worker_to_case);
        startService(GeneralService.generateLoadCasesAndWorkersIntent(this, true));
        initialize();
    }

    private void initialize() {
        mCaseId = getIntent().getStringExtra(EXTRA_CASE_ID);

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
        actionBar.setTitle(getString(R.string.add_worker_to_case_title));
    }

    private void setupWorkerList() {
        setWorkerListData();
        mWorkerListAdapter = new WorkerListAdapter(this, mWorkerListData);
        mWorkerListLayoutManager = new LinearLayoutManager(this);

        mWorkerList.setLayoutManager(mWorkerListLayoutManager);
        mWorkerList.setAdapter(mWorkerListAdapter);
    }

    private void setWorkerListData() {
        for (Worker worker : WorkingData.getInstance(this).getWorkers()) {
            mWorkerListData.add(new WorkerListItem(false, worker));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
}
