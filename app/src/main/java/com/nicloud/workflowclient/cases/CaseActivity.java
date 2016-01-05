package com.nicloud.workflowclient.cases;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.nicloud.workflowclient.R;

public class CaseActivity extends AppCompatActivity {

    public static final String EXTRA_CASE_ID = "extra_case_id";
    public static final String EXTRA_CASE_NAME = "extra_case_name";

    private String mCaseId;
    private String mCaseName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case);
        initialize();
    }

    private void initialize() {
        mCaseId = getIntent().getStringExtra(EXTRA_CASE_ID);
        mCaseName = getIntent().getStringExtra(EXTRA_CASE_NAME);
        setupActionBar();
    }

    private void setupActionBar() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mCaseName);
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
