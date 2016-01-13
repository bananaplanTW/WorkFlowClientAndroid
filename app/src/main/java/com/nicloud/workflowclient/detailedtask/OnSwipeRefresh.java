package com.nicloud.workflowclient.detailedtask;

import com.nicloud.workflowclient.data.data.activity.BaseData;

import java.util.List;

/**
 * Created by logicmelody on 2016/1/13.
 */
public interface OnSwipeRefresh {
    void swapData(List<BaseData> dataSet);
    void setSwipeRefreshLayout(boolean isRefresh);
}
