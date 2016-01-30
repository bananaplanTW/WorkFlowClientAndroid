package com.nicloud.workflowclient.mainmenu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.connectserver.cases.LoadingCases;
import com.nicloud.workflowclient.data.connectserver.worker.LoadingWorkerAvatar;
import com.nicloud.workflowclient.data.data.data.Case;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.login.LoginActivity;
import com.nicloud.workflowclient.provider.contentprovider.WorkFlowDatabaseHelper;
import com.nicloud.workflowclient.utility.utils.Utils;
import com.parse.ParsePush;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2015/12/21.
 */
public class MainMenuFragment extends Fragment implements View.OnClickListener,
        LoadingCases.OnFinishLoadingCasesListener {

    public interface OnClickMainMenuItemListener {
        void onClickMainMenuItem(MainMenuItem item);
    }

    public static class MainMenuItemType {
        public static final int MY_TASKS = 0;
        public static final int CASE = 1;
    }

    private Context mContext;

    private ImageView mWorkerAvatar;
    private TextView mWorkerName;
    private TextView mWorkerDepartment;

    private TextView mLogoutButton;

    private RecyclerView mMainMenuList;
    private LinearLayoutManager mMainMenuListLayoutManager;
    private MainMenuListAdapter mMainMenuListAdapter;
    private List<MainMenuItem> mDataSet = new ArrayList<>();

    private OnClickMainMenuItemListener mOnClickMainMenuItemListener;


    public void clearSelectedMainMenuItem() {
        mMainMenuListAdapter.clearSelectedMainMenuItem();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mOnClickMainMenuItemListener = (OnClickMainMenuItemListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_menu, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        findViews();
        loadWorkerAvatar();
        setupWorkerViews();
        setupMainMenuList();
        loadCases();
    }

    private void findViews() {
        mWorkerAvatar = (ImageView) getView().findViewById(R.id.main_menu_worker_avatar);
        mWorkerName = (TextView) getView().findViewById(R.id.main_menu_worker_name);
        mWorkerDepartment = (TextView) getView().findViewById(R.id.main_menu_worker_department_name);
        mLogoutButton = (TextView) getView().findViewById(R.id.main_menu_logout);
        mMainMenuList = (RecyclerView) getView().findViewById(R.id.main_menu_list);
    }

    private void loadWorkerAvatar() {
        Drawable avatar = WorkingData.getInstance(mContext).getLoginWorker().avatar;
        String s = WorkingData.getInstance(mContext).getLoginWorker().avatarUrl;

        if (TextUtils.isEmpty(s)) {
            if (avatar == null) {
                mWorkerAvatar.setImageResource(R.drawable.ic_worker_white);
            } else {
                mWorkerAvatar.setImageDrawable(avatar);
            }

            return;
        }

        Uri.Builder avatarBuilder = Uri.parse(LoadingDataUtils.sBaseUrl).buildUpon();
        avatarBuilder.path(s);
        Uri avatarUri = avatarBuilder.build();

        LoadingWorkerAvatar loadingWorkerAvatar
                = new LoadingWorkerAvatar(mContext, avatarUri, mWorkerAvatar,
                WorkingData.getInstance(mContext).getLoginWorker(), R.drawable.ic_worker_white);
        loadingWorkerAvatar.execute();
    }

    private void setupWorkerViews() {
        mWorkerName.setText(WorkingData.getInstance(mContext).getLoginWorker().name);
        mWorkerDepartment.setText(WorkingData.getInstance(mContext).getLoginWorker().departmentName);

        mLogoutButton.setOnClickListener(this);
    }

    private void setupMainMenuList() {
        mMainMenuListLayoutManager = new LinearLayoutManager(mContext);
        mMainMenuListAdapter = new MainMenuListAdapter(mContext, mDataSet, mOnClickMainMenuItemListener);

        mMainMenuList.setLayoutManager(mMainMenuListLayoutManager);
        mMainMenuList.setAdapter(mMainMenuListAdapter);
    }

    private void loadCases() {
        new LoadingCases(mContext, this).execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_menu_logout:
                ParsePush.unsubscribeInBackground("user_" + WorkingData.getUserId());
                //ParseUtils.removeLoginWorkerToParse();

                WorkFlowDatabaseHelper.deleteAllTablesData(mContext);
                WorkingData.resetAccount();

                SharedPreferences sharedPreferences = mContext.getSharedPreferences(WorkingData.SHARED_PREFERENCE_KEY, 0);
                sharedPreferences.edit().remove(WorkingData.USER_ID).remove(WorkingData.AUTH_TOKEN).commit();

                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                getActivity().finish();

                break;
        }
    }

    @Override
    public void onFinishLoadingCases() {
        setMainMenuListData();
    }

    private void setMainMenuListData() {
        mDataSet.clear();

        mDataSet.add(new MainMenuItem(MainMenuItemType.MY_TASKS,
                mContext.getString(R.string.main_menu_my_tasks),
                null, MainMenuListAdapter.ItemViewType.ITEM, true));

        mDataSet.add(new MainMenuItem(-1, "", null, MainMenuListAdapter.ItemViewType.EMPTY, false));

        mDataSet.add(new MainMenuItem(MainMenuItemType.CASE,
                mContext.getString(R.string.main_menu_cases),
                null, MainMenuListAdapter.ItemViewType.CASE_TITLE, false));

        for (Case aCase : WorkingData.getInstance(mContext).getCases()) {
            mDataSet.add(new MainMenuItem(MainMenuItemType.CASE, aCase.name, aCase,
                    MainMenuListAdapter.ItemViewType.CASE, false));
        }

        mMainMenuListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailLoadingCases(boolean isFailCausedByInternet) {
        Utils.showInternetConnectionWeakToast(mContext);
    }
}
