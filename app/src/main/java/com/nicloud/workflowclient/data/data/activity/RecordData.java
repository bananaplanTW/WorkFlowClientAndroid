package com.nicloud.workflowclient.data.data.activity;

/**
 * Created by Ben on 2015/8/29.
 */
public class RecordData extends BaseData {
    public String reporter;
    public String reporterName;

    public String description = "";

    public RecordData(BaseData.TYPE type) {
        super(type);
    }
}
