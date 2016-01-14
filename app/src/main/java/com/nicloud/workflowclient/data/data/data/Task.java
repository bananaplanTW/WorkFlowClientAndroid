package com.nicloud.workflowclient.data.data.data;

import android.content.Context;
import android.content.res.Resources;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.utility.Utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data in a task item
 *
 * @author Danny Lin
 * @since 2015.06.13
 */
public class Task extends IdData {

    public enum Status {
        PENDING, UNCLAIMED, WIP, PAUSE, DONE, WARNING, STOP, CANCEL, IN_REVIEW
    }

    public String description;
    public String caseId;
    public String caseName;
    public String workerId;
    public String equipmentId;

    public Date startDate;  // The starting working date of this task
    public Date dueDate;
    public Date assignDate;

    public long currentStartTime = 0L;
    public long expectedTime = 0L;
    public long startTime = 0L;  // The starting time of this working section
    public long spentTime = 0L;
    public long nextNotifyTime = 0L;


    public boolean isDelayed = false;

    public Status status = Task.Status.UNCLAIMED;

    public List<TaskWarning> taskWarnings = new ArrayList<>();
    public ArrayList<CheckItem> checkList = new ArrayList<>();


    public Task(String title) {
        this.name = title;
    }

    public Task(String id,
                String name,
                String description,
                String caseName,
                String caseId,
                String workerId,
                String equipmentId,
                Status status,
                Date assignDate,
                Date startDate,
                Date dueDate,
                long expectedTime,
                long startTime,
                long spentTime,
                long lastUpdatedTime,
                boolean isDelayed,
                ArrayList<CheckItem> checkList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.caseName = caseName;
        this.caseId = caseId;
        this.workerId = workerId;
        this.equipmentId = equipmentId;
        this.status = status;
        this.expectedTime = expectedTime;
        this.startTime = startTime;
        this.spentTime = spentTime;
        this.assignDate = assignDate;
        this.startDate = startDate;
        this.dueDate = dueDate;
        //this.taskWarnings = taskWarnings;
        this.lastUpdatedTime = lastUpdatedTime;
        this.isDelayed = isDelayed;
        this.checkList = checkList;

        if (this.taskWarnings == null) {
            this.taskWarnings = new ArrayList<>();
        }
    }

    public Task(String id, String name) {
        this.id = id;
        this.name = name;
        this.taskWarnings = new ArrayList<>();
    }

    public int getUnSolvedWarningCount() {
        int count = 0;
        for (TaskWarning taskWarning : taskWarnings) {
            if (taskWarning.status == TaskWarning.Status.OPENED) {
                count++;
            }
        }
        return count;
    }

    public void update(Task task) {
        this.name = task.name;
        this.name = task.description;
        this.caseName = task.caseName;
        this.caseId = task.caseId;
        this.workerId = task.workerId;
        this.equipmentId = task.equipmentId;
        this.status = task.status;
        this.expectedTime = task.expectedTime;
        this.startTime = task.startTime;
        this.spentTime = task.spentTime;
        this.assignDate = task.assignDate;
        this.startDate = task.startDate;
        this.dueDate = task.dueDate;
        //this.taskWarnings = task.taskWarnings;
        this.lastUpdatedTime = task.lastUpdatedTime;
        this.isDelayed = task.isDelayed;
        this.checkList = task.checkList;
    }

    public long getWorkingTime() {
        return Status.WIP.equals(status) ? System.currentTimeMillis()-startTime+spentTime : spentTime;
    }

    public static Status convertStringToStatus(String status) {
        Status result = Status.UNCLAIMED;

        if ("pending".equals(status)) {
            result = Status.PENDING;

        } else if ("unclaimed".equals(status)) {
            result = Status.UNCLAIMED;

        } else if ("wip".equals(status)) {
            result = Status.WIP;

        } else if ("pause".equals(status)) {
            result = Status.PAUSE;

        } else if ("done".equals(status)) {
            result = Status.DONE;

        } else if ("exception".equals(status)) {
            result = Status.WARNING;

        } else if ("stop".equals(status)) {
            result = Status.STOP;

        } else if ("cancel".equals(status)) {
            result = Status.CANCEL;

        } else if ("inreview".equals(status)) {
            result = Status.IN_REVIEW;
        }

        return result;
    }

    public static String getTaskStatusString(final Context context, final Task item) {
        String r = "";
        Resources resources = context.getResources();

        switch (item.status) {
            case PENDING:
                r = resources.getString(R.string.task_status_pending);
                break;
            case UNCLAIMED:
                r = resources.getString(R.string.task_status_unclaimed);
                break;
            case PAUSE:
                r = resources.getString(R.string.task_status_pause);
                break;
            case WIP:
                r = resources.getString(R.string.task_status_wip);
                break;
            case DONE:
                if (item.dueDate != null) {
                    r = Utilities.timestamp2Date(item.dueDate, Utilities.DATE_FORMAT_MD) + " ";
                }
                r += resources.getString(R.string.task_status_finished);
                break;
            case IN_REVIEW:
                r = resources.getString(R.string.task_status_in_review);
                break;
            case WARNING:
                r = resources.getString(R.string.task_status_warning);
                break;
            case STOP:
                r = resources.getString(R.string.task_status_stop);
                break;
            case CANCEL:
                r = resources.getString(R.string.task_status_cancel);
                break;

            default:
                break;
        }

        return r;
    }
}
