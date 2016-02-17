package com.nicloud.workflowclient.dialog.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.service.GeneralService;
import com.nicloud.workflowclient.utility.utils.Utils;

public class CreateCaseActivity extends AppCompatActivity {

    private EditText mCreateCaseBox;


    public static Intent generateCreateCaseIntent(Context context) {
        Intent intent = new Intent(context, CreateCaseActivity.class);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_case);
        initialize();
    }

    private void initialize() {
        findViews();
        setupActionBar();
        setupViews();
    }

    private void findViews() {
        mCreateCaseBox = (EditText) findViewById(R.id.create_case_box);
    }

    private void setupActionBar() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolBar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.create_case));
    }

    private void setupViews() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_case, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Utils.hideSoftKeyboard(this);
                finish();
                return true;

            case R.id.action_cancel:
                Utils.hideSoftKeyboard(this);
                finish();
                return true;

            case R.id.action_ok:
                Utils.hideSoftKeyboard(this);
                addCase();
                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addCase() {
        String caseName = mCreateCaseBox.getText().toString();
        if (TextUtils.isEmpty(caseName.trim())) return;

        startService(GeneralService.generateCreateCaseIntent(this, caseName));
    }
}
