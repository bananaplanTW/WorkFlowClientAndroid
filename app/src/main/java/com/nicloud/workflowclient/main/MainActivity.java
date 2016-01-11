package com.nicloud.workflowclient.main;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.data.Worker;
import com.nicloud.workflowclient.mainmenu.MainMenuFragment;
import com.nicloud.workflowclient.mainmenu.MainMenuItem;
import com.nicloud.workflowclient.messagemenu.MessageMenuFragment;
import com.nicloud.workflowclient.tasklist.TasksListFragment;

public class MainActivity extends AppCompatActivity implements TasksListFragment.OnRefreshInTasksList,
        MainMenuFragment.OnClickMainMenuItemListener, MessageMenuFragment.OnClickMessageMenuWorkerListener {

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
    public void onRefreshInTasksList() {
        mUIController.onRefreshInTaskList();
    }

    @Override
    public void onClickMainMenuItem(MainMenuItem item) {
        mUIController.onClickMainMenuItem(item);
    }

    @Override
    public void onClickMessageMenuWorker(Worker worker) {
        mUIController.onClickMessageMenuWorker(worker);
    }
}
