package com.nicloud.workflowclientandroid.data.data;


/**
 * @author Danny Lin
 * @since 2015/7/30.
 */
public class TaskWarning extends IdData {

    public enum Status {
        OPENED, CLOSED
    }

    public String taskId;
    public String caseId;
    public String workerId;
    public String managerId;
    public String description;

    public long spentTime;

    public Status status = Status.OPENED;


    public TaskWarning(
            String id,
            String name,
            String caseId,
            String taskId,
            String workerId,
            String managerId,
            Status status,
            long spentTime,
            long lastUpdatedTime) {
        this.id = id;
        this.name = name;
        this.caseId = caseId;
        this.taskId = taskId;
        this.workerId = workerId;
        this.managerId = managerId;
        this.status = status;
        this.spentTime = spentTime;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public TaskWarning(String name, Status status) {
        this.name = name;
        this.status = status;
    }

    public void update(TaskWarning taskWarning) {
        this.name = taskWarning.name;
        this.caseId = taskWarning.caseId;
        this.taskId = taskWarning.taskId;
        this.workerId = taskWarning.workerId;
        this.managerId = taskWarning.managerId;
        this.status = taskWarning.status;
        this.spentTime = taskWarning.spentTime;
        this.lastUpdatedTime = taskWarning.lastUpdatedTime;
    }

    public static Status convertStringToStatus(String status) {
        Status result = Status.OPENED;

        if ("OPENED".equals(status)) {
            result = Status.OPENED;
        } else if ("CLOSE".equals(status)) {
            result = Status.CLOSED;
        }

        return result;
    }
}
