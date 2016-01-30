package com.nicloud.workflowclient.tasklist.my;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.receiver.TaskCompletedReceiver;
import com.nicloud.workflowclient.backgroundtask.service.TaskService;
import com.nicloud.workflowclient.tasklist.main.Task;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.tasklist.main.TaskListAdapter;
import com.nicloud.workflowclient.tasklist.main.TaskListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by logicmelody on 2015/12/21.
 */
public class MyTaskListFragment extends Fragment implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, TaskCompletedReceiver.OnLoadTaskCompletedListener {

    private static final int LOADER_ID = 573;

    private static final String[] mProjection = new String[] {
            WorkFlowContract.Task._ID,
            WorkFlowContract.Task.TASK_ID,
            WorkFlowContract.Task.TASK_NAME,
            WorkFlowContract.Task.TASK_DESCRIPTION,
            WorkFlowContract.Task.CASE_ID,
            WorkFlowContract.Task.CASE_NAME,
            WorkFlowContract.Task.WORKER_ID,
            WorkFlowContract.Task.DUE_DATE,
            WorkFlowContract.Task.UPDATED_TIME
    };
    private static final int ID = 0;
    private static final int TASK_ID = 1;
    private static final int TASK_NAME = 2;
    private static final int TASK_DESCRIPTION = 3;
    private static final int CASE_ID = 4;
    private static final int CASE_NAME = 5;
    private static final int WORKER_ID = 6;
    private static final int DUE_DATE = 7;
    private static final int UPDATED_TIME = 8;

    private static final String mSelection = WorkFlowContract.Task.WORKER_ID + " = ?";

    private static String[] mSelectionArgs;

    private static final String mSortOrder = WorkFlowContract.Task.DUE_DATE;

    private Context mContext;
    private FragmentManager mFm;

    private FloatingActionButton mFab;

    private RecyclerView mTaskList;
    private LinearLayoutManager mTaskListManager;
    private TaskListAdapter mTaskListAdapter;

    private TextView mNoTaskText;

    private TaskCompletedReceiver mTaskCompletedReceiver;

    private List<TaskListItem> mTaskDataSet = new ArrayList<>();

    private boolean mNeedRefresh = false;

    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();

        if (!WorkingData.getInstance(mContext).hasLoadedTasks()) {
            loadDataInFirstLaunch();
        }

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(TaskCompletedReceiver.ACTION_LOAD_TASKS_COMPLETED);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mTaskCompletedReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mTaskCompletedReceiver);
    }

    private void initialize() {
        mFm = getFragmentManager();
        mSelectionArgs = new String[] {WorkingData.getUserId()};
        mTaskCompletedReceiver = new TaskCompletedReceiver(this);

        findViews();
        setupViews();
        setupTasksList();
        setupSwipeRefresh();
    }

    private void findViews() {
        mFab = (FloatingActionButton) getView().findViewById(R.id.fab);
        mTaskList = (RecyclerView) getView().findViewById(R.id.task_list);
        mNoTaskText = (TextView) getView().findViewById(R.id.task_list_no_task_text);
        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.task_list_swipe_refresh_container);
    }

    private void setupViews() {
        mFab.setOnClickListener(this);
    }

    private void setNoTaskTextVisibility() {
        if (mTaskDataSet.size() == 0) {
            mNoTaskText.setVisibility(View.VISIBLE);
        } else {
            mNoTaskText.setVisibility(View.GONE);
        }
    }

    private void setupTasksList() {
        mTaskListManager = new LinearLayoutManager(mContext);
        mTaskListAdapter = new TaskListAdapter(mContext, mFm, mTaskDataSet);

        mTaskList.setLayoutManager(mTaskListManager);
        mTaskList.setAdapter(mTaskListAdapter);

        mTaskList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (!mNeedRefresh) break;

                        if (!isFirstItemOnTheTopOfTheList()) {
                            mNeedRefresh = false;
                            mSwipeRefreshLayout.setEnabled(false);
                        }

                        break;

                    case MotionEvent.ACTION_UP:
                        if (isFirstItemOnTheTopOfTheList()) {
                            mNeedRefresh = true;
                            mSwipeRefreshLayout.setEnabled(true);
                        }

                        break;
                }

                return false;
            }
        });
    }

    private boolean isFirstItemOnTheTopOfTheList() {
        return mTaskListManager.findFirstVisibleItemPosition() == 0;
    }

    private void setupSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mContext.startService(TaskService.generateLoadMyTasksIntent(mContext, false));
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void loadDataInFirstLaunch() {
        forceShowRefreshSpinner();
        mContext.startService(TaskService.generateLoadMyTasksIntent(mContext, true));
    }

    /**
     * Use for the first time we enter the app.
     * If we want to trigger the refresh spinner programmatically, we need to use this method.
     */
    private void forceShowRefreshSpinner() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    /**
     * Use for the first time we enter the app.
     * To remove the refresh spinner triggered by forceShowRefreshSpinner().
     */
    private void forceHideRefreshSpinner() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader
                = new CursorLoader(mContext, WorkFlowContract.Task.CONTENT_URI,
                mProjection, mSelection, mSelectionArgs, mSortOrder);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return;

        setTaskListData(cursor);
    }

    private void setTaskListData(Cursor cursor) {
        mTaskDataSet.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(ID);
            String taskId = cursor.getString(TASK_ID);
            String taskName = cursor.getString(TASK_NAME);
            String taskDescription = cursor.getString(TASK_DESCRIPTION);
            String caseId = cursor.getString(CASE_ID);
            String caseName = cursor.getString(CASE_NAME);
            String workerId = cursor.getString(WORKER_ID);
            long dueDate = cursor.getLong(DUE_DATE);
            long lastUpdatedTime = cursor.getLong(UPDATED_TIME);

            Task task = new Task(taskId, taskName, taskDescription, caseName,
                                 caseId, workerId, new Date(dueDate), null, lastUpdatedTime);

            mTaskDataSet.add(new TaskListItem(task, false, false));
        }

        Collections.sort(mTaskDataSet, new Comparator<TaskListItem>() {
            @Override
            public int compare(TaskListItem taskItem1, TaskListItem taskItem2) {
                if (taskItem1.task.dueDate.getTime() == -1L && taskItem2.task.dueDate.getTime() != -1L) {
                    return 1;

                } else if (taskItem1.task.dueDate.getTime() != -1L && taskItem2.task.dueDate.getTime() == -1L) {
                    return -1;

                } else if (taskItem1.task.dueDate.getTime() == -1L && taskItem2.task.dueDate.getTime() == -1L) {
                    return -((int) (taskItem1.task.lastUpdatedTime - taskItem2.task.lastUpdatedTime));

                } else {
                    return (int) (taskItem1.task.dueDate.getTime() - taskItem2.task.dueDate.getTime());
                }
            }
        });

        TaskListItem previousTaskItem = null;
        for (int i = 0 ; i < mTaskDataSet.size() ; i++) {
            TaskListItem taskItem = mTaskDataSet.get(i);
            if (i == 0) {
                taskItem.showDueDate = true;
                previousTaskItem = taskItem;
                continue;

            } else if (i == mTaskDataSet.size() - 1) {
                taskItem.showDueDateUnderline = true;
                break;

            } else if (!previousTaskItem.task.dueDate.equals(taskItem.task.dueDate)) {
                taskItem.showDueDate = true;
                previousTaskItem.showDueDateUnderline = true;
            }

            previousTaskItem = taskItem;
        }

        mTaskListAdapter.notifyDataSetChanged();
        setNoTaskTextVisibility();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onLoadTaskCompleted(Intent intent) {
        String from = intent.getStringExtra(TaskCompletedReceiver.EXTRA_FROM);

        if (TaskCompletedReceiver.From.MY_TASK_FIRST.equals(from)) {
            forceHideRefreshSpinner();
            WorkingData.getInstance(mContext).setHasLoadedTasks(true);

        } else if (TaskCompletedReceiver.From.MY_TASK.equals(from)) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
