package com.nicloud.workflowclientandroid.main;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nicloud.workflowclientandroid.R;


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
    }

    private void findViews() {
        mToolbar = (Toolbar) mMainActivity.findViewById(R.id.tool_bar);
        mWipTaskCard = mMainActivity.findViewById(R.id.wip_task_card);
        mWipTaskPauseButton = (TextView) mMainActivity.findViewById(R.id.wip_task_card_pause_button);
        mWipTaskCompleteButton = (TextView) mMainActivity.findViewById(R.id.wip_task_card_complete_button);
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

    @Override
    public void onClick(View v) {

    }
}
