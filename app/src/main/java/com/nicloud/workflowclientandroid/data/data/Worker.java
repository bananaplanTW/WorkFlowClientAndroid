package com.nicloud.workflowclientandroid.data.data;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.nicloud.workflowclientandroid.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


/**
 * @author Danny Lin
 * @since 2015/6/27.
 */
public class Worker extends IdData {

    public enum Status {
        WIP, PENDING, PAUSE, STOP, OFF
    }

    public static class PaymentClassification {
        String type;
        double base;
        double hourlyPayment;
        double overtimeBase;

        public PaymentClassification(String type, double base, double hourlyPayment, double overtimeBase) {
            this.type = type;
            this.base = base;
            this.hourlyPayment = hourlyPayment;
            this.overtimeBase = overtimeBase;
        }
    }

    public String factoryId;

    public String jobTitle;
    public String address;
    public String phone;
    public int score;
    public Status status = Status.WIP;
    public PaymentClassification paymentClassification;

    public long checkInTime = 0L;

    public String wipTaskId;
    private Task wipTask;

    public List<String> scheduledTaskIds = new ArrayList<>();
    private List<Task> scheduledTasks = new ArrayList<>();

    public List<Task> completedTasks = new ArrayList<>();
    public List<Task> warningTasks = new ArrayList<>();

    //public ArrayList<BaseData> records = new ArrayList<>();
    //public HashMap<String, WorkerTimeCard> timeCards = new HashMap<>();
    public boolean isOvertime = false;

    //private List<WorkerAttendance> attendanceList = new ArrayList<>();

    public Drawable avatar;


    public Worker(
            String id,
            String name,
            String factoryId,
            String wipTaskId,
            String address,
            String phone,
            long checkInTime,
            int score,
            boolean isOvertime,
            Status status,
            PaymentClassification payment,
            List<String> scheduledTaskIds,
            long lastUpdatedTime) {
        this.id = id;
        this.name = name;
        this.factoryId = factoryId;
        this.wipTaskId = wipTaskId;
        this.address = address;
        this.phone = phone;
        this.checkInTime = checkInTime;
        this.score = score;
        this.isOvertime = isOvertime;
        this.status = status;
        this.paymentClassification = payment;
        this.scheduledTaskIds = scheduledTaskIds;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Worker(final Context context, String id, String name, String title) {
        this(context, id, name, title, new ArrayList<Task>());
    }

    public Worker(final Context context, String id, String name, String jobTitle, List<Task> scheduledTasks) {
        this.id = id;
        this.name = name;
        this.jobTitle = jobTitle;
        this.scheduledTasks = scheduledTasks;
    }

    public void update(Worker worker) {
        this.name = worker.name;
        this.factoryId = worker.factoryId;
        this.wipTaskId = worker.wipTaskId;
        this.address = worker.address;
        this.phone = worker.phone;
        this.checkInTime = worker.checkInTime;
        this.score = worker.score;
        this.isOvertime = worker.isOvertime;
        this.status = worker.status;
        this.paymentClassification = worker.paymentClassification;
        this.scheduledTaskIds = worker.scheduledTaskIds;
        this.lastUpdatedTime = worker.lastUpdatedTime;
    }

    public void setWipTask(Task task) {
        wipTaskId = task == null ? "" : task.id;
        wipTask = task;
    }

    public Task getWipTask() {
        return wipTask;
    }

    public void setScheduledTasks(List<Task> tasks) {
        List<String> tasksIds = new ArrayList<>();
        for (Task task : tasks) {
            tasksIds.add(task.id);
        }

        scheduledTaskIds = tasksIds;
        scheduledTasks = tasks;
    }

    public void addScheduledTask(Task task) {
        if (!scheduledTaskIds.contains(task.id)) {
            scheduledTaskIds.add(task.id);
        }

        if (!scheduledTasks.contains(task)) {
            scheduledTasks.add(task);
        }
    }

    public void addAllScheduleTasks(List<Task> tasks) {
        List<String> tasksIds = new ArrayList<>();
        for (Task task : tasks) {
            tasksIds.add(task.id);
        }

        scheduledTaskIds.addAll(tasksIds);
        scheduledTasks.addAll(tasks);
    }

    public void removeScheduleTask(Task task) {
        scheduledTaskIds.remove(task.id);
        scheduledTasks.remove(task);
    }

    public void clearScheduleTasks() {
        scheduledTaskIds.clear();
        scheduledTasks.clear();
    }

//    public List<WorkerAttendance> getAttendanceList() {
//        return attendanceList;
//    }
//
//    public void addAttendance(WorkerAttendance attendance) {
//        for (WorkerAttendance workerAttendance : attendanceList) {
//            if (workerAttendance.id.equals(attendance.id)) return;
//        }
//
//        attendanceList.add(attendance);
//    }

    public List<Task> getScheduledTasks() {
        return scheduledTasks;
    }

    public boolean hasWipTask() {
        return this.wipTask != null;
    }

    public boolean hasScheduledTasks() {
        return this.scheduledTasks != null && this.scheduledTasks.size() != 0;
    }

    public static Status convertStringToStatus(String status) {
        Status result = Status.OFF;

        if ("wip".equals(status)) {
            result = Status.WIP;

        } else if ("pending".equals(status)) {
            result = Status.PENDING;

        } else if ("pause".equals(status)) {
            result = Status.PAUSE;

        } else if ("stop".equals(status)) {
            result = Status.STOP;

        } else if ("off".equals(status)) {
            result = Status.OFF;

        }

        return result;
    }

    public static String getWorkerStatusString(Context context, Status status) {
        String statusString = "";

        switch (status) {
            case OFF:
                statusString = context.getString(R.string.worker_status_off);
                break;
            case STOP:
                statusString = context.getString(R.string.worker_status_stop);
                break;
            case PAUSE:
                statusString = context.getString(R.string.worker_status_pause);
                break;
            case PENDING:
                statusString = context.getString(R.string.worker_status_pending);
                break;
            case WIP:
                statusString = context.getString(R.string.worker_status_wip);
                break;
        }

        return statusString;
    }
}