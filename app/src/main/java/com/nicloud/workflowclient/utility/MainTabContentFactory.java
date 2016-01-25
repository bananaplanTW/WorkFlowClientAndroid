package com.nicloud.workflowclient.utility;

import android.content.Context;
import android.view.View;
import android.widget.TabHost;

/**
 * Created by logicmelody on 2016/1/25.
 */
public class MainTabContentFactory implements TabHost.TabContentFactory  {

    private Context mContext;


    public MainTabContentFactory(Context context) {
        mContext = context;
    }

    @Override
    public View createTabContent(String tag) {
        View v = new View(mContext);
        v.setMinimumWidth(0);
        v.setMinimumHeight(0);
        v.setVisibility(View.GONE);
        return v;
    }
}
