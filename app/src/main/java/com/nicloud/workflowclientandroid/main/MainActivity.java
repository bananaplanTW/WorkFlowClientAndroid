package com.nicloud.workflowclientandroid.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import com.nicloud.workflowclientandroid.R;

public class MainActivity extends AppCompatActivity {

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
}
