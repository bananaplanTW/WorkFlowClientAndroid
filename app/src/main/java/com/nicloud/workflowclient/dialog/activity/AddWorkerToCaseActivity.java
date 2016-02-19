package com.nicloud.workflowclient.dialog.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.nicloud.workflowclient.main.MainApplication;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.utility.utils.Utils;
import com.nicloud.workflowclient.workerlist.WorkerListAdapter;
import com.nicloud.workflowclient.main.WorkingData;
import com.nicloud.workflowclient.workerlist.WorkerListItem;

import java.util.ArrayList;
import java.util.List;

public class AddWorkerToCaseActivity extends AppCompatActivity implements View.OnClickListener,
        GeneralCompletedReceiver.OnGeneralCompletedListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_CASE_ID = "AddWorkerToCaseActivity_extra_case_id";

    private static final int LOADER_ID = 66;

    private static final String[] mProjection = {
            WorkFlowContract.Case.WORKER_IDS
    };
    private static final int WORKER_IDS = 0;

    private static final String mSelection = WorkFlowContract.Case.CASE_ID + " = ?";
    private static String[] mSelectionArgs;


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
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void initialize() {
        mCaseId = getIntent().getStringExtra(EXTRA_CASE_ID);
        mGeneralCompletedReceiver = new GeneralCompletedReceiver(this);
        mSelectionArgs = new String[] {mCaseId};

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
        mWorkerListAdapter = new WorkerListAdapter(this, mWorkerListData);

        mWorkerList.setLayoutManager(new LinearLayoutManager(this));
        mWorkerList.setAdapter(mWorkerListAdapter);
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

        if (intent.getBooleanExtra(GeneralService.ExtraKey.ACTION_SUCCESSFUL, false)) {
            mWorkerEmailBox.setText("");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, WorkFlowContract.Case.CONTENT_URI,
                                mProjection, mSelection, mSelectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return;

        mWorkerListData.clear();
        cursor.moveToFirst();

        List<String> mWorkerList = Utils.unpackStrings(cursor.getString(WORKER_IDS));
        for (String workerId : mWorkerList) {
            if (!WorkingData.getInstance(this).hasWorker(workerId)) continue;

            mWorkerListData.add(new WorkerListItem(false, WorkingData.getInstance(this).getWorkerById(workerId)));
        }

        mWorkerListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
