package com.nicloud.workflowclient.detailedtask.main;

import com.nicloud.workflowclient.data.activity.BaseData;

import java.util.List;

/**
 * Created by logicmelody on 2016/1/13.
 */
public interface OnSwipeRefresh {
    void swapData(List<BaseData> dataSet);
    void setSwipeRefreshLayout(boolean isRefresh);
}
