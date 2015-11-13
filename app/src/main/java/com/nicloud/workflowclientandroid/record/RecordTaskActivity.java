package com.nicloud.workflowclientandroid.record;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclientandroid.R;

public class RecordTaskActivity extends AppCompatActivity implements View.OnClickListener {

    private ActionBar mActionBar;
    private Toolbar mToolbar;

    private TextView mRecordTaskName;
    private TextView mRecordCaseName;

    private ImageView mRecordCameraButton;
    private ImageView mRecordUploadButton;
    private TextView mRecordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_task);
        initialize();
    }

    private void initialize() {
        findViews();
        setupActionBar();
        setupViews();
    }

    private void findViews() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mRecordTaskName = (TextView) findViewById(R.id.record_task_task_name);
        mRecordCaseName = (TextView) findViewById(R.id.record_task_case_name);
        mRecordCameraButton = (ImageView) findViewById(R.id.record_task_camera_button);
        mRecordUploadButton = (ImageView) findViewById(R.id.record_task_upload_button);
        mRecordButton = (TextView) findViewById(R.id.record_task_record_button);
    }

    private void setupActionBar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViews() {
        mRecordTaskName.setText("伺服器服務開發");
        mRecordCaseName.setText("流程管理專案");
        mRecordCameraButton.setOnClickListener(this);
        mRecordUploadButton.setOnClickListener(this);
        mRecordButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_task_camera_button:
                break;

            case R.id.record_task_upload_button:
                break;

            case R.id.record_task_record_button:
                break;
        }
    }
}
