package com.nicloud.workflowclient.parse;

import android.content.Context;
import android.content.Intent;

import com.nicloud.workflowclient.R;
import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by logicmelody on 2015/12/3.
 */
public class ParsePushReceiver extends ParsePushBroadcastReceiver {

    @Override
    protected int getSmallIconId(Context context, Intent intent) {
        return R.drawable.ic_nicloud_notification;
    }
}
