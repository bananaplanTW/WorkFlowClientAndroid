package com.nicloud.workflowclient.mainmenu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.service.GeneralService;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.utility.utils.LoadingDataUtils;
import com.nicloud.workflowclient.backgroundtask.asyntask.worker.LoadingWorkerAvatar;
import com.nicloud.workflowclient.data.data.Case;
import com.nicloud.workflowclient.main.WorkingData;
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
        LoaderManager.LoaderCallbacks<Cursor> {

    public interface OnClickMainMenuItemListener {
        void onClickMainMenuItem(MainMenuItem item);
    }

    public static class MainMenuItemType {
        public static final int MY_TASKS = 0;
        public static final int CASE = 1;
    }

    private static final int LOADER_ID = 536;

    private static final String[] mProjection = new String[] {
            WorkFlowContract.Case._ID,
            WorkFlowContract.Case.CASE_ID,
            WorkFlowContract.Case.CASE_NAME,
            WorkFlowContract.Case.OWNER_ID,
            WorkFlowContract.Case.DESCRIPTION,
            WorkFlowContract.Case.IS_COMPLETED,
            WorkFlowContract.Case.UPDATED_TIME
    };
    private static final int ID = 0;
    private static final int CASE_ID = 1;
    private static final int CASE_NAME = 2;
    private static final int OWNER_ID = 3;
    private static final int DESCRIPTION = 4;
    private static final int IS_COMPLETED = 5;
    private static final int UPDATED_TIME = 6;

    private static final String mSortOrder = WorkFlowContract.Case.CASE_NAME;

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
        mContext.startService(GeneralService.generateLoadCasesIntent(mContext));
        initialize();
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void initialize() {
        findViews();
        loadWorkerAvatar();
        setupWorkerViews();
        initMainMenuListData();
        setupMainMenuList();
    }

    private void findViews() {
        mWorkerAvatar = (ImageView) getView().findViewById(R.id.main_menu_worker_avatar);
        mWorkerName = (TextView) getView().findViewById(R.id.main_menu_worker_name);
        mWorkerDepartment = (TextView) getView().findViewById(R.id.main_menu_worker_department_name);
        mLogoutButton = (TextView) getView().findViewById(R.id.main_menu_logout);
        mMainMenuList = (RecyclerView) getView().findViewById(R.id.main_menu_list);
    }

    private void loadWorkerAvatar() {
        Utils.setWorkerAvatarImage(mContext, WorkingData.getInstance(mContext).getLoginWorker(),
                                   mWorkerAvatar, R.drawable.ic_worker_white);
    }

    private void setupWorkerViews() {
        mWorkerName.setText(WorkingData.getInstance(mContext).getLoginWorker().name);
        mWorkerDepartment.setText(WorkingData.getInstance(mContext).getLoginWorker().departmentName);

        mLogoutButton.setOnClickListener(this);
    }

    private void initMainMenuListData() {
        mDataSet.clear();

        mDataSet.add(new MainMenuItem(MainMenuItemType.MY_TASKS,
                mContext.getString(R.string.main_menu_my_tasks),
                null, MainMenuListAdapter.ItemViewType.ITEM, true));

        mDataSet.add(new MainMenuItem(-1, "", null, MainMenuListAdapter.ItemViewType.EMPTY, false));

        mDataSet.add(new MainMenuItem(MainMenuItemType.CASE,
                mContext.getString(R.string.main_menu_cases),
                null, MainMenuListAdapter.ItemViewType.CASE_TITLE, false));
    }

    private void setupMainMenuList() {
        mMainMenuListLayoutManager = new LinearLayoutManager(mContext);
        mMainMenuListAdapter = new MainMenuListAdapter(mContext, mDataSet, mOnClickMainMenuItemListener);

        mMainMenuList.setLayoutManager(mMainMenuListLayoutManager);
        mMainMenuList.setAdapter(mMainMenuListAdapter);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext, WorkFlowContract.Case.CONTENT_URI, mProjection, null, null, mSortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return;

        setMainMenuListData(cursor);
    }

    private void setMainMenuListData(Cursor cursor) {
        initMainMenuListData();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(ID);
            String caseId = cursor.getString(CASE_ID);
            String caseName = cursor.getString(CASE_NAME);
            String ownerId = cursor.getString(OWNER_ID);
            String description = cursor.getString(DESCRIPTION);
            boolean isCompleted = cursor.getInt(IS_COMPLETED) == 1;
            long updatedTime = cursor.getLong(UPDATED_TIME);

            Case aCase = new Case(caseId, caseName, ownerId, description, isCompleted, updatedTime);

            mDataSet.add(new MainMenuItem(MainMenuItemType.CASE, caseName, aCase,
                                          MainMenuListAdapter.ItemViewType.CASE, false));
        }

        mMainMenuListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
