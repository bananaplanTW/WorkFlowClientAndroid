package com.nicloud.workflowclient.dialog.activity;

import android.app.DatePickerDialog;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.service.GeneralService;
import com.nicloud.workflowclient.data.data.Worker;
import com.nicloud.workflowclient.dialog.fragment.DatePickerFragment;
import com.nicloud.workflowclient.main.WorkingData;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class CreateTaskActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final int LOADER_ID = 923;

    private static final String[] mProjection = new String[] {
            WorkFlowContract.Case.CASE_ID,
            WorkFlowContract.Case.CASE_NAME
    };
    private static final int CASE_ID = 0;
    private static final int CASE_NAME = 1;

    private static final String mSortOrder = WorkFlowContract.Case.CASE_NAME;

    private EditText mCreateTaskName;
    private TextView mCreateTaskDueDate;

    private Spinner mCaseSpinner;
    private CaseSpinnerAdapter mCaseSpinnerAdapter;

    private Spinner mWorkerSpinner;
    private WorkerSpinnerAdapter mWorkerSpinnerAdapter;

    private long mPickedDueDate = -1L;

    private List<CaseSpinnerItem> mCaseSpinnerData = new ArrayList<>();
    private List<Worker> mWorkerSpinnerDate = new ArrayList<>();


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

    private class WorkerSpinnerAdapter extends ArrayAdapter<Worker> {

        private Context mContext;
        private List<Worker> mDataSet;


        private class ViewHolder {

            public ImageView avatar;
            public TextView name;

            public ViewHolder(View v) {
                avatar = (ImageView) v.findViewById(R.id.worker_avatar);
                name = (TextView) v.findViewById(R.id.worker_name);
            }
        }

        public WorkerSpinnerAdapter(Context context, int resource, List<Worker> data) {
            super(context, resource, data);
            mContext = context;
            mDataSet = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getViewAndDropDownView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getViewAndDropDownView(position, convertView, parent);
        }

        private View getViewAndDropDownView(int position, View convertView, ViewGroup parent) {
            Worker worker = mDataSet.get(position);
            View view = convertView;
            ViewHolder holder;

            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.worker_item, parent, false);
                view.setPadding(0 ,0 ,0 ,0);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (TextUtils.isEmpty(worker.id)) {
                holder.name.setText(getString(R.string.hint_choose_worker));
                holder.avatar.setImageResource(R.drawable.ic_worker_black);

            } else {
                holder.name.setText(worker.name);
                Utils.setWorkerAvatarImage(CreateTaskActivity.this, worker,
                                           holder.avatar, R.drawable.ic_worker_black);
            }

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
        startService(GeneralService.generateLoadCasesAndWorkersIntent(this, true));
        getLoaderManager().initLoader(LOADER_ID, null, this);
        initialize();
    }

    private void initialize() {
        findViews();
        setupViews();
        setupActionBar();
        setupCaseSpinner();
        setupWorkerSpinner();
    }

    private void findViews() {
        mCreateTaskName = (EditText) findViewById(R.id.create_task_name);
        mCaseSpinner = (Spinner) findViewById(R.id.case_spinner);
        mWorkerSpinner = (Spinner) findViewById(R.id.worker_spinner);
        mCreateTaskDueDate = (TextView) findViewById(R.id.create_task_due_date);
    }

    private void setupViews() {
        mCreateTaskDueDate.setOnClickListener(this);
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
    }

    private void setupWorkerSpinner() {
        mWorkerSpinnerAdapter = new WorkerSpinnerAdapter(this, R.layout.worker_item, mWorkerSpinnerDate);
        mWorkerSpinner.setAdapter(mWorkerSpinnerAdapter);
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
        String workerId = ((Worker) mWorkerSpinner.getSelectedItem()).id;

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
        setWorkerData();
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

    private void setWorkerData() {
        mWorkerSpinnerDate.clear();

        mWorkerSpinnerDate.add(new Worker());
        for (Worker worker : WorkingData.getInstance(this).getWorkers()) {
            mWorkerSpinnerDate.add(worker);
        }

        mWorkerSpinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_task_due_date:
                showDatePicker();

                break;
        }
    }

    private void showDatePicker() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), DatePickerFragment.TAG_FRAGMENT_DATE_PICKER);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mPickedDueDate = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime().getTime();
        mCreateTaskDueDate.setText(String.format(getString(R.string.date_format_yyyy_mm_dd),
                                   String.valueOf(year), String.valueOf(monthOfYear+1), String.valueOf(dayOfMonth)));
    }
}
