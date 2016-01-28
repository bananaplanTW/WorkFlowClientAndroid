package com.nicloud.workflowclient.data.data.data;

import android.content.Context;

import com.nicloud.workflowclient.data.data.observer.DataObserver;
import com.nicloud.workflowclient.data.data.observer.DataSubject;
import com.nicloud.workflowclient.utility.Utilities;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ben on 2015/7/18.
 */
public final class WorkingData implements DataSubject {

    private static final String TAG = "WorkingData";

    public static final String SHARED_PREFERENCE_KEY = "workflow";
    public static final String COMPANY_ACCOUNT = "company_account";
    public static final String USER_ID = "userId";
    public static final String AUTH_TOKEN = "authToken";

    private volatile static WorkingData sWorkingData = null;
    private static int sDataIdCount = -1;

    private Context mContext;

    private List<DataObserver> mDataObservers = new ArrayList<>();

    private static String sUserId;
    private static String sAuthToken;

    private Worker mLoginWorker;

    // TODO: Implement DB
    private List<Task> mTasks = new ArrayList<>();
    private List<Case> mCases = new ArrayList<>();
    private List<Worker> mWorkers = new ArrayList<>();

    private boolean mHasLoadedTasks = false;


    public static WorkingData getInstance(Context context) {
        if (sWorkingData == null) {
            synchronized (WorkingData.class) {
                if (sWorkingData == null) {
                    sWorkingData = new WorkingData(context);
                }
            }
        }

        return sWorkingData;
    }

    private WorkingData(Context context) {
        mContext = context;
    }

    public static void resetAccount () {
        sUserId = "";
        sAuthToken = "";
    }

    public static void setUserId(String userId) {
        sUserId = userId;
    }

    public static void setAuthToken(String authToken) {
        sAuthToken = authToken;
    }

    public static String getUserId () {
        return sUserId;
    }

    public static String getAuthToken() {
        return sAuthToken;
    }

    public void setLoginWorker(Worker worker) {
        mLoginWorker = worker;
    }

    public Worker getLoginWorker() {
        return mLoginWorker;
    }

    public void addTask(Task task) {
        mTasks.add(task);
    }

    public void addAllTasks(List<Task> scheduledTasks) {
        mTasks.addAll(scheduledTasks);
    }

    public void clearTasks() {
        mTasks.clear();
    }

    public void removeTask(int position) {
        mTasks.remove(position);
    }

    public List<Task> getTasks() {
        return mTasks;
    }

    public Task getTask(String taskId) {
        for (Task task : mTasks) {
            if (Utilities.isSameId(task.id, taskId)) {
                return task;
            }
        }

        return null;
    }

    public void resetTasks() {
        mTasks.clear();
    }

    public void updateTask(Task task, String taskId) {
        for (Task scheduledTask : mTasks) {
            if (Utilities.isSameId(scheduledTask.id, taskId)) {
                scheduledTask.update(task);
                return;
            }
        }
    }

    public void addCase(Case aCase) {
        mCases.add(aCase);
    }

    public void updateCase(String caseId, Case aCase) {
        getCaseById(caseId).update(aCase);
    }

    public Case getCaseById(String caseId) {
        for (Case aCase : mCases) {
            if (aCase.id.equals(caseId)) return aCase;
        }

        return null;
    }

    public List<Case> getCases() {
        return mCases;
    }

    public boolean hasCase(String caseId) {
        for (Case aCase : mCases) {
            if (aCase.id.equals(caseId)) return true;
        }

        return false;
    }

    public void clearCases() {
        mCases.clear();
    }

    public void addWorker(Worker worker) {
        mWorkers.add(worker);
    }

    public void updateWorker(String workerId, Worker worker) {
        getWorkerById(workerId).update(worker);
    }

    public Worker getWorkerById(String workerId) {
        if (getUserId().equals(workerId)) {
            return mLoginWorker;
        }

        for (Worker worker : mWorkers) {
            if (worker.id.equals(workerId)) return worker;
        }

        return null;
    }

    public List<Worker> getWorkers() {
        return mWorkers;
    }

    public boolean hasWorker(String workerId) {
        for (Worker worker : mWorkers) {
            if (worker.id.equals(workerId)) return true;
        }

        return false;
    }

    public void clearWorkers() {
        mWorkers.clear();
    }

    public boolean hasLoadedTasks() {
        return mHasLoadedTasks;
    }

    public void setHasLoadedTasks(boolean hasLoadedTasks) {
        mHasLoadedTasks = hasLoadedTasks;
    }


    public void updateData() {
        notifyDataObservers();
    }

    @Override
    public void registerDataObserver(DataObserver o) {
        mDataObservers.add(o);
    }

    @Override
    public void removeDataObserver(DataObserver o) {
        int index = mDataObservers.indexOf(o);
        if (index >= 0) {
            mDataObservers.remove(index);
        }
    }

    @Override
    public void notifyDataObservers() {
        for (DataObserver dataObserver : mDataObservers) {
            dataObserver.updateData();
        }
    }
}
