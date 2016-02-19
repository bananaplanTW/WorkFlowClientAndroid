package com.nicloud.workflowclient.dialog.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.receiver.GeneralCompletedReceiver;
import com.nicloud.workflowclient.backgroundtask.service.GeneralService;
import com.nicloud.workflowclient.data.data.Worker;
import com.nicloud.workflowclient.main.MainApplication;
import com.nicloud.workflowclient.workerlist.WorkerListAdapter;
import com.nicloud.workflowclient.main.WorkingData;
import com.nicloud.workflowclient.workerlist.WorkerListItem;

import java.util.ArrayList;
import java.util.List;

public class AddWorkerToCaseActivity extends AppCompatActivity implements View.OnClickListener,
        GeneralCompletedReceiver.OnGeneralCompletedListener {

    public static final String EXTRA_CASE_ID = "AddWorkerToCaseActivity_extra_case_id";

    private RecyclerView mWorkerList;
    private WorkerListAdapter mWorkerListAdapter;

    private List<WorkerListItem> mWorkerListData = new ArrayList<>();

    private EditText mWorkerEmailBox;
    private TextView mAddWorkerButton;
    private ProgressBar mAddWorkerProgressBar;

    private String mCaseId;

    private GeneralCompletedReceiver mGeneralCompletedReceiver;


    public static Intent generateAddWorkerToCaseDialogIntent(Context context, String caseId) {
        Intent intent = new Intent(context, AddWorkerToCaseActivity.class);
        intent.putExtra(EXTRA_CASE_ID, caseId);

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
        mGeneralCompletedReceiver = new GeneralCompletedReceiver(this);

        findViews();
        setupViews();
        setupActionBar();
        setupWorkerList();
    }

    private void findViews() {
        mWorkerEmailBox = (EditText) findViewById(R.id.worker_email_box);
        mWorkerList = (RecyclerView) findViewById(R.id.worker_list);
        mAddWorkerButton = (TextView) findViewById(R.id.add_worker_button);
        mAddWorkerProgressBar = (ProgressBar) findViewById(R.id.add_worker_progress_bar);
    }

    private void setupViews() {
        mAddWorkerButton.setOnClickListener(this);
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

        mWorkerList.setLayoutManager(new LinearLayoutManager(this));
        mWorkerList.setAdapter(mWorkerListAdapter);
    }

    private void setWorkerListData() {
        for (Worker worker : WorkingData.getInstance(this).getWorkers()) {
            mWorkerListData.add(new WorkerListItem(false, worker));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(GeneralCompletedReceiver.ACTION_GENERAL_COMPLETED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mGeneralCompletedReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGeneralCompletedReceiver);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_worker_button:
                String workerEmail = mWorkerEmailBox.getText().toString();
                if (TextUtils.isEmpty(workerEmail)) return;

                startService(GeneralService.generateAddWorkerToCaseIntent(this, mCaseId, workerEmail));
                setAddingWorker(true);

                break;
        }
    }

    private void setAddingWorker(boolean isAddingWorker) {
        if (isAddingWorker) {
            mWorkerEmailBox.setEnabled(false);
            mAddWorkerButton.setClickable(false);
            mAddWorkerButton.startAnimation(MainApplication.sFadeOutAnimation);
            mAddWorkerButton.setVisibility(View.INVISIBLE);
            mAddWorkerProgressBar.setVisibility(View.VISIBLE);
            mAddWorkerProgressBar.startAnimation(MainApplication.sFadeInAnimation);

        } else {
            mWorkerEmailBox.setEnabled(true);
            mAddWorkerButton.setClickable(true);
            mAddWorkerButton.startAnimation(MainApplication.sFadeInAnimation);
            mAddWorkerButton.setVisibility(View.VISIBLE);
            mAddWorkerProgressBar.startAnimation(MainApplication.sFadeOutAnimation);
            mAddWorkerProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onGeneralCompleted(Intent intent) {
        setAddingWorker(false);
    }
}
