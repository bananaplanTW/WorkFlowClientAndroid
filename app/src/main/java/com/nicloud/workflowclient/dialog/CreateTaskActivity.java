package com.nicloud.workflowclient.dialog;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.service.GeneralService;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CreateTaskActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 923;

    private static final String[] mProjection = new String[] {
            WorkFlowContract.Case.CASE_ID,
            WorkFlowContract.Case.CASE_NAME
    };
    private static final int CASE_ID = 0;
    private static final int CASE_NAME = 1;

    private static final String mSortOrder = WorkFlowContract.Case.CASE_NAME;

    private EditText mCreateTaskName;

    private Spinner mCaseSpinner;
    private CaseSpinnerAdapter mCaseSpinnerAdapter;

    private List<CaseSpinnerItem> mCaseSpinnerData = new ArrayList<>();


    private class CaseSpinnerItem {

        public String name;
        public String id;


        public CaseSpinnerItem(String name, String id) {
            this.name = name;
            this.id = id;
        }
    }

    private class CaseSpinnerAdapter extends ArrayAdapter<CaseSpinnerItem> {

        private Context mContext;
        private List<CaseSpinnerItem> mDataSet;


        private class ViewHolder {

            public TextView caseName;

            public ViewHolder(View v) {
                caseName = (TextView) v.findViewById(R.id.case_name);
            }
        }

        public CaseSpinnerAdapter(Context context, int resource, List<CaseSpinnerItem> data) {
            super(context, resource, data);
            mContext = context;
            mDataSet = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder;

            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.case_spinner_item, parent, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.caseName.setText(mDataSet.get(position).name);

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder;

            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.case_spinner_item, parent, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.caseName.setText(mDataSet.get(position).name);

            return view;
        }
    }

    public static Intent generateCreateTaskIntent(Context context) {
        Intent intent = new Intent(context, CreateTaskActivity.class);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        startService(GeneralService.generateLoadCasesIntent(this));
        getLoaderManager().initLoader(LOADER_ID, null, this);
        initialize();
    }

    private void initialize() {
        findViews();
        setupActionBar();
        setupCaseSpinner();
    }

    private void findViews() {
        mCreateTaskName = (EditText) findViewById(R.id.create_task_name);
        mCaseSpinner = (Spinner) findViewById(R.id.case_spinner);
    }

    private void setupActionBar() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolBar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.create_task));
    }

    private void setupCaseSpinner() {
        mCaseSpinnerAdapter = new CaseSpinnerAdapter(this, R.layout.case_spinner_item, mCaseSpinnerData);

        mCaseSpinner.setAdapter(mCaseSpinnerAdapter);
        mCaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_task, menu);
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
                addTask();
                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addTask() {
        String taskName = mCreateTaskName.getText().toString();
        String caseId = ((CaseSpinnerItem) mCaseSpinner.getSelectedItem()).id;

        if (TextUtils.isEmpty(taskName.trim()) || TextUtils.isEmpty(caseId)) return;

        startService(GeneralService.generateCreateTaskIntent(this, taskName, caseId));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, WorkFlowContract.Case.CONTENT_URI, mProjection, null, null, mSortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return;

        setCaseData(cursor);
    }

    private void setCaseData(Cursor cursor) {
        mCaseSpinnerData.clear();

        mCaseSpinnerData.add(new CaseSpinnerItem(getString(R.string.hint_please_choose_case), ""));

        while (cursor.moveToNext()) {
            String caseName = cursor.getString(CASE_NAME);
            String caseId = cursor.getString(CASE_ID);

            mCaseSpinnerData.add(new CaseSpinnerItem(caseName, caseId));
        }

        mCaseSpinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
