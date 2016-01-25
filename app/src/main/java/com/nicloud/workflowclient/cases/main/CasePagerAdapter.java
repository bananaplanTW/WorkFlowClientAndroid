package com.nicloud.workflowclient.cases.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by logicmelody on 2016/1/25.
 */
public class CasePagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    private List<Fragment> mFragmentList;


    public CasePagerAdapter(FragmentManager fm, Context mContext, List<Fragment> fragmentList) {
        super(fm);
        this.mContext = mContext;
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
