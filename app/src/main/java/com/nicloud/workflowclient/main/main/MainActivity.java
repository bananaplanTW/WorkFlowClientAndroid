package com.nicloud.workflowclient.main.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.location.Location;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.worker.CheckInOutCommand;
import com.nicloud.workflowclient.dialog.DisplayDialogFragment;

public class MainActivity extends AppCompatActivity implements DisplayDialogFragment.OnDialogActionListener {

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
    public void onCompleteTaskCancel() {
        mUIController.onCompleteTaskCancel();
    }

    @Override
    public void onCompleteTaskOk(String taskId) {
        mUIController.onCompleteTaskOk(taskId);
    }

    @Override
    public void onChooseTaskStartWork(String taskId) {
        mUIController.onChooseTaskStartWork(taskId);
    }

    @Override
    public void onChooseTaskLog(String taskId) {
        mUIController.onChooseTaskLog(taskId);
    }

    @Override
    public void onCheck(Location currentLocation, String currentAddress,
                        CheckInOutCommand.OnDialogCheckInOutStatusListener onDialogCheckInOutStatusListener) {
        mUIController.onCheck(currentLocation, currentAddress, onDialogCheckInOutStatusListener);
    }
}
