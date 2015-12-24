package com.nicloud.workflowclient.main.main;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.mainmenu.MainMenuFragment;
import com.nicloud.workflowclient.messagemenu.MessageMenuFragment;
import com.nicloud.workflowclient.tasklist.TaskListFragment;

public class MainActivity extends AppCompatActivity implements TaskListFragment.OnRefreshInTaskList,
        MainMenuFragment.OnClickMainMenuItemListener, MessageMenuFragment.OnClickMessageMenuItemListener {

    private UIController mUIController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUIController = new UIController(this);
        mUIController.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mUIController.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mUIController.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mUIController.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUIController.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mUIController.isLeftDrawerOpened()) {
            mUIController.closeLeftDrawer();

        } else if (mUIController.isRightDrawerOpened()) {
            mUIController.closeRightDrawer();

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRefreshInTaskList() {
        mUIController.onRefreshInTaskList();
    }

    @Override
    public void onClickMainMenuItem(int itemId) {
        mUIController.onClickMainMenuItem(itemId);
    }

    @Override
    public void onClickMessageMenuItem(String itemId) {
        mUIController.onClickMessageMenuItem(itemId);
    }
}
