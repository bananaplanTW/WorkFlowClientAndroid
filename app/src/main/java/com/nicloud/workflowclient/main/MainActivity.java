package com.nicloud.workflowclient.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.data.Worker;
import com.nicloud.workflowclient.dialog.DisplayDialogFragment;
import com.nicloud.workflowclient.mainmenu.MainMenuFragment;
import com.nicloud.workflowclient.mainmenu.MainMenuItem;
import com.nicloud.workflowclient.messagemenu.MessageMenuFragment;

public class MainActivity extends AppCompatActivity implements MainMenuFragment.OnClickMainMenuItemListener,
        MessageMenuFragment.OnClickMessageMenuWorkerListener,
        DisplayDialogFragment.OnDialogActionListener {

    private UIController mUIController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUIController = new UIController(this);
        mUIController.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUIController.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUIController.onStop();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClickMainMenuItem(MainMenuItem item) {
        mUIController.onClickMainMenuItem(item);
    }

    @Override
    public void onClickMessageMenuWorker(Worker worker) {
        mUIController.onClickMessageMenuWorker(worker);
    }

    @Override
    public void onCompleteTaskOk(String taskId) {
        mUIController.onCompleteTaskOk(taskId);
    }

    @Override
    public void onCompleteTaskCancel() {
        mUIController.onCompleteTaskCancel();
    }
}
