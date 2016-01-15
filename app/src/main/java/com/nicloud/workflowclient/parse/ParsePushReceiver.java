package com.nicloud.workflowclient.parse;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.messagechat.LoadPromptMessageReceiver;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by logicmelody on 2015/12/3.
 */
public class ParsePushReceiver extends ParsePushBroadcastReceiver {

    public static final class PushType {
        public static final String MESSAGE = "message";
    }

    private static final class JsonKey {
        public static final String TYPE = "type";
        public static final String SENDER_ID = "senderId";
    }

    private static final String EXTRA_JSON_DATA = "com.parse.Data";


    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        try {
            JSONObject json = new JSONObject(intent.getExtras().getString(EXTRA_JSON_DATA));

            String type = json.getString(JsonKey.TYPE);

            if (PushType.MESSAGE.equals(type)) {
                intent.setAction(LoadPromptMessageReceiver.ACTION_LOAD_PROMPT_MESSAGE);
                intent.putExtra(LoadPromptMessageReceiver.EXTRA_SENDER_ID, json.getString(JsonKey.SENDER_ID));
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getSmallIconId(Context context, Intent intent) {
        return R.drawable.ic_nicloud_notification;
    }
}
