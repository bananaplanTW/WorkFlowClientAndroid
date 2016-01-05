package com.nicloud.workflowclient.messagechat;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.nicloud.workflowclient.R;

public class MessageChatActivity extends AppCompatActivity {

    public static final String EXTRA_WORKER_ID = "extra_worker_id";
    public static final String EXTRA_WORKER_NAME = "extra_worker_name";

    private String mWorkerId;
    private String mWorkerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_chat);
        initialize();
    }

    private void initialize() {
        mWorkerId = getIntent().getStringExtra(EXTRA_WORKER_ID);
        mWorkerName = getIntent().getStringExtra(EXTRA_WORKER_NAME);
        setupActionBar();
    }

    private void setupActionBar() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mWorkerName);
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
