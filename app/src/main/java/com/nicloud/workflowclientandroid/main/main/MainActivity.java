package com.nicloud.workflowclientandroid.main.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.nicloud.workflowclientandroid.R;
import com.nicloud.workflowclientandroid.dialog.DisplayDialogFragment;

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
    public void onChooseTaskStartWork() {
        mUIController.onChooseTaskStartWork();
    }

    @Override
    public void onChooseTaskLog(String taskId) {
        mUIController.onChooseTaskLog(taskId);
    }

    @Override
    public void onCheck() {
        mUIController.onCheck();
    }
}
