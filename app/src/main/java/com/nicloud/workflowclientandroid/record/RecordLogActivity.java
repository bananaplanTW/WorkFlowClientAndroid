package com.nicloud.workflowclientandroid.record;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.nicloud.workflowclientandroid.R;

public class RecordLogActivity extends AppCompatActivity {

    private ActionBar mActionBar;
    private Toolbar mToolbar;

    private TextView mRecordTaskName;
    private TextView mRecordCaseName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_log);
        initialize();
    }

    private void initialize() {
        findViews();
        setupActionBar();
        setupViews();
    }

    private void findViews() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mRecordTaskName = (TextView) findViewById(R.id.add_record_task_name);
        mRecordCaseName = (TextView) findViewById(R.id.add_record_case_name);
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
        startActivity(new Intent(this, AddRecordActivity.class));
    }
}
