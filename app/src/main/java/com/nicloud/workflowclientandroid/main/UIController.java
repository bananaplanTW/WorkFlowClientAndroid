package com.nicloud.workflowclientandroid.main;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.nicloud.workflowclientandroid.R;


/**
 * Main component to control the UI
 *
 * @author Danny Lin
 * @since 2015.05.28
 *
 */
public class UIController {

    private AppCompatActivity mMainActivity;
    private ActionBar mActionBar;
    private Toolbar mToolbar;


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
        return false;
    }

    private void initialize() {
        findViews();
        initActionbar();
    }

    private void findViews() {
        mToolbar = (Toolbar) mMainActivity.findViewById(R.id.tool_bar);
    }

    private void initActionbar() {
        mMainActivity.setSupportActionBar(mToolbar);
        mActionBar = mMainActivity.getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
        }
    }
}
