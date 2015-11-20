package com.nicloud.workflowclientandroid.data.connectserver.activity;

import android.content.Context;

import com.nicloud.workflowclientandroid.data.data.activity.ActivityDataFactory;
import com.nicloud.workflowclientandroid.data.data.activity.BaseData;
import com.nicloud.workflowclientandroid.data.data.observer.DataObserver;
import com.nicloud.workflowclientandroid.data.data.observer.DataSubject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by daz on 10/9/15.
 */
public class ActivityDataStore implements DataSubject, LoadingActivitiesAsyncTask.OnFinishLoadingDataListener {

    private Context mContext;
    private HashMap<String, LoadingActivitiesAsyncTask> loadingWorkerActivitiesAsyncTaskHashMap = new HashMap<>();
    private HashMap<String, LoadingActivitiesAsyncTask> loadingTaskActivitiesAsyncTaskHashMap = new HashMap<>();
    private HashMap<String, LoadingActivitiesAsyncTask> loadingTaskWarningActivitiesAsyncTaskHashMap = new HashMap<>();

    private List<DataObserver> mDataObservers = new ArrayList<>();

    private HashMap<String, ArrayList<BaseData>> mWorkerActivitiesCache = new HashMap<>();
    private HashMap<String, ArrayList<BaseData>> mTaskActivityesCache = new HashMap<>();
    private HashMap<String, ArrayList<BaseData>> mTaskWarningActivitiesCache = new HashMap<>();

    private volatile static ActivityDataStore sActivityData = null;

    public static final ActivityDataStore getInstance(Context context) {
        if (sActivityData == null) {
            synchronized (ActivityDataStore.class) {
                if (sActivityData == null) {
                    sActivityData = new ActivityDataStore(context);
                }
            }
        }
        return sActivityData;
    }
    private ActivityDataStore(Context context) {
        mContext = context;
    }


    /**
     * Load worker activities
     * @param workerId
     * @param limit
     */
    public void loadWorkerActivities(String workerId, int limit) {
        if (!loadingWorkerActivitiesAsyncTaskHashMap.containsKey(workerId)) {
            synchronized (ActivityDataStore.class) {
                if (!loadingWorkerActivitiesAsyncTaskHashMap.containsKey(workerId)) {
                    LoadingWorkerActivitiesStrategy loadingWorkerActivitiesStrategy = new LoadingWorkerActivitiesStrategy(workerId, limit);
                    LoadingActivitiesAsyncTask loadingWorkerActivitiesTask = new LoadingActivitiesAsyncTask(mContext, workerId, this, loadingWorkerActivitiesStrategy);
                    loadingWorkerActivitiesTask.execute();
                    loadingWorkerActivitiesAsyncTaskHashMap.put(workerId, loadingWorkerActivitiesTask);
                }
            }
        }
    }
    public ArrayList<BaseData> getWorkerActivities(String workerId) {
        return mWorkerActivitiesCache.get(workerId);
    }
    public boolean hasWorkerActivitiesCacheWithWorkerId(String workerId) {
        return mWorkerActivitiesCache.get(workerId) != null;
    }
    public void addWorkerActivity (String workerId, BaseData activity) {
        ArrayList<BaseData> workerActivities = mWorkerActivitiesCache.get(workerId);
        if (workerActivities != null) {
            synchronized (ActivityDataStore.class) {
                if (workerActivities != null) {
                    workerActivities.add(0, activity);
                }
            }
        }
    }
    private void removeLoadingWorkerActivitiesAsyncTaskFromHashMap(String workerId) {
        if (loadingWorkerActivitiesAsyncTaskHashMap.containsKey(workerId)) {
            synchronized (ActivityDataStore.class) {
                if (loadingWorkerActivitiesAsyncTaskHashMap.containsKey(workerId)) {
                    loadingWorkerActivitiesAsyncTaskHashMap.remove(workerId);
                }
            }
        }
    }
    private void putWorkerActivityDataArrayListToCache(String workerId, ArrayList<BaseData> activityDataArrayList) {
        synchronized (ActivityDataStore.class) {
            if (mWorkerActivitiesCache.containsKey(workerId)) {
                mWorkerActivitiesCache.remove(workerId);
            }
            mWorkerActivitiesCache.put(workerId, activityDataArrayList);
        }
    }


    /**
     * load task activities
     * @param taskId
     * @param limit
     */
    public void loadTaskActivities(String taskId, int limit) {
        if (!loadingTaskActivitiesAsyncTaskHashMap.containsKey(taskId)) {
            synchronized (ActivityDataStore.class) {
                if (!loadingTaskActivitiesAsyncTaskHashMap.containsKey(taskId)) {
                    LoadingTaskActivitiesStrategy loadingTaskActivitiesStrategy = new LoadingTaskActivitiesStrategy(taskId, limit);
                    LoadingActivitiesAsyncTask loadingWorkerActivitiesTask = new LoadingActivitiesAsyncTask(mContext, taskId, this, loadingTaskActivitiesStrategy);
                    loadingWorkerActivitiesTask.execute();
                    loadingTaskActivitiesAsyncTaskHashMap.put(taskId, loadingWorkerActivitiesTask);
                }
            }
        }
    }
    public ArrayList<BaseData> getTaskActivities(String taskId) {
        return mTaskActivityesCache.get(taskId);
    }
    public boolean hasTaskActivitiesCacheWithTaskId(String taskId) {
        return mTaskActivityesCache.get(taskId) != null;
    }
    public void addTaskActivity (String taskId, BaseData activity) {
        ArrayList<BaseData> taskActivities = mTaskActivityesCache.get(taskId);
        if (taskActivities != null) {
            synchronized (ActivityDataStore.class) {
                if (taskActivities != null) {
                    taskActivities.add(0, activity);
                }
            }
        }
    }
    private void removeLoadingTaskActivitiesAsyncTaskFromHashMap(String taskId) {
        if (loadingTaskActivitiesAsyncTaskHashMap.containsKey(taskId)) {
            synchronized (ActivityDataStore.class) {
                if (loadingTaskActivitiesAsyncTaskHashMap.containsKey(taskId)) {
                    loadingTaskActivitiesAsyncTaskHashMap.remove(taskId);
                }
            }
        }
    }
    private void putTaskActivityDataArrayListToCache(String taskId, ArrayList<BaseData> activityDataArrayList) {
        synchronized (ActivityDataStore.class) {
            if (mTaskActivityesCache.containsKey(taskId)) {
                mTaskActivityesCache.remove(taskId);
            }
            mTaskActivityesCache.put(taskId, activityDataArrayList);
        }
    }


    /**
     * load task warning activities
     * @param taskWarningId
     * @param limit
     */
    public void loadTaskWarningActivities(String taskWarningId, int limit) {
        if (!loadingTaskWarningActivitiesAsyncTaskHashMap.containsKey(taskWarningId)) {
            synchronized (ActivityDataStore.class) {
                if (!loadingTaskWarningActivitiesAsyncTaskHashMap.containsKey(taskWarningId)) {
                    LoadingTaskWarningActivitiesStrategy loadingTaskWarningActivitiesStrategy = new LoadingTaskWarningActivitiesStrategy(taskWarningId, limit);
                    LoadingActivitiesAsyncTask loadingWorkerActivitiesTask = new LoadingActivitiesAsyncTask(mContext, taskWarningId, this, loadingTaskWarningActivitiesStrategy);
                    loadingWorkerActivitiesTask.execute();
                    loadingTaskWarningActivitiesAsyncTaskHashMap.put(taskWarningId, loadingWorkerActivitiesTask);
                }
            }
        }
    }
    public ArrayList<BaseData> getTaskWarningActivities(String taskWarningId) {
        return mTaskWarningActivitiesCache.get(taskWarningId);
    }
    public boolean hasTaskWarningActivitiesCacheWithTaskWarningId(String taskWarningId) {
        return mTaskWarningActivitiesCache.get(taskWarningId) != null;
    }
    public void addTaskWarningActivity (String taskWarningId, BaseData activity) {
        ArrayList<BaseData> taskWarningActivities = mTaskWarningActivitiesCache.get(taskWarningId);
        if (taskWarningActivities != null) {
            synchronized (ActivityDataStore.class) {
                if (taskWarningActivities != null) {
                    taskWarningActivities.add(0, activity);
                }
            }
        }
    }
    private void removeLoadingTaskWarningActivitiesAsyncTaskFromHashMap(String taskWarningId) {
        if (loadingTaskWarningActivitiesAsyncTaskHashMap.containsKey(taskWarningId)) {
            synchronized (ActivityDataStore.class) {
                if (loadingTaskWarningActivitiesAsyncTaskHashMap.containsKey(taskWarningId)) {
                    loadingTaskWarningActivitiesAsyncTaskHashMap.remove(taskWarningId);
                }
            }
        }
    }
    private void putTaskWarningActivityDataArrayListToCache(String taskId, ArrayList<BaseData> activityDataArrayList) {
        synchronized (ActivityDataStore.class) {
            if (mTaskWarningActivitiesCache.containsKey(taskId)) {
                mTaskWarningActivitiesCache.remove(taskId);
            }
            mTaskWarningActivitiesCache.put(taskId, activityDataArrayList);
        }
    }


    /**
     * LoadingWorkerActivitiesAsyncTask.OnFinishLoadingData Callbacks
     */
    @Override
    public void onFinishLoadingData(String id, ILoadingActivitiesStrategy.ActivityCategory activityCategory, JSONArray activities) {
        switch (activityCategory) {
            case WORKER:
                removeLoadingWorkerActivitiesAsyncTaskFromHashMap(id);
                if (activities != null) {
                    ArrayList<BaseData> activityDataArrayList = parseActivityJSONArray(activities);
                    if (activityDataArrayList != null) {
                        putWorkerActivityDataArrayListToCache(id, activityDataArrayList);
                        notifyDataObservers();
                    }
                }
                break;
            case TASK:
                removeLoadingTaskActivitiesAsyncTaskFromHashMap(id);
                if (activities != null) {
                    ArrayList<BaseData> activityDataArrayList = parseActivityJSONArray(activities);
                    if (activityDataArrayList != null) {
                        putTaskActivityDataArrayListToCache(id, activityDataArrayList);
                        notifyDataObservers();
                    }
                }
                break;
            case WARNING:
                removeLoadingTaskWarningActivitiesAsyncTaskFromHashMap(id);
                if (activities != null) {
                    ArrayList<BaseData> activityDataArrayList = parseActivityJSONArray(activities);
                    if (activityDataArrayList != null) {
                        putTaskWarningActivityDataArrayListToCache(id, activityDataArrayList);
                        notifyDataObservers();
                    }
                }
                break;
        }

    }
    @Override
    public void onFailLoadingData(boolean isFailCausedByInternet) {

    }


    @Override
    public void registerDataObserver(DataObserver o) {
        synchronized (ActivityDataStore.class) {
            mDataObservers.add(o);
        }
    }
    @Override
    public void removeDataObserver(DataObserver o) {
        synchronized (ActivityDataStore.class) {
            int index = mDataObservers.indexOf(o);
            if (index >= 0) {
                mDataObservers.remove(index);
            }
        }
    }
    @Override
    public void notifyDataObservers() {
        synchronized (ActivityDataStore.class) {
            for (DataObserver dataObserver : mDataObservers) {
                dataObserver.updateData();
            }
        }
    }


    private ArrayList<BaseData> parseActivityJSONArray(JSONArray activities) {
        ArrayList<BaseData> parsedActivities = new ArrayList<>();

        int length = activities.length();

        try {
            for (int i = 0; i < length; i++) {
                JSONObject activity = activities.getJSONObject(i);
                BaseData activityData = ActivityDataFactory.genData(activity, mContext);
                if (activityData != null) {
                    parsedActivities.add(activityData);
                }
            }
            return parsedActivities;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
