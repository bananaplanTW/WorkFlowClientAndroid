package com.nicloud.workflowclient.cases.caseinfo;

import android.content.Context;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.ImageView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.service.GeneralService;
import com.nicloud.workflowclient.cases.main.CaseFragment;
import com.nicloud.workflowclient.cases.main.OnSetCaseId;
import com.nicloud.workflowclient.data.data.Case;
import com.nicloud.workflowclient.data.data.Worker;
import com.nicloud.workflowclient.dialog.activity.AddWorkerToCaseActivity;
import com.nicloud.workflowclient.main.WorkingData;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2016/2/14.
 */
public class CaseInfoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        OnSetCaseId, View.OnClickListener {

    private static final String[] mProjection = new String[] {
            WorkFlowContract.Case._ID,
            WorkFlowContract.Case.CASE_ID,
            WorkFlowContract.Case.CASE_NAME,
            WorkFlowContract.Case.OWNER_ID,
            WorkFlowContract.Case.WORKER_IDS,
            WorkFlowContract.Case.DESCRIPTION,
            WorkFlowContract.Case.IS_COMPLETED,
            WorkFlowContract.Case.UPDATED_TIME
    };
    private static final int ID = 0;
    private static final int CASE_ID = 1;
    private static final int CASE_NAME = 2;
    private static final int OWNER_ID = 3;
    private static final int WORKER_IDS = 4;
    private static final int DESCRIPTION = 5;
    private static final int IS_COMPLETED = 6;
    private static final int UPDATED_TIME = 7;

    private static final String mSelection = WorkFlowContract.Case.CASE_ID + " = ?";
    private static String[] mSelectionArgs;

    private static final int LOADER_ID = 927;

    private Context mContext;

    private RecyclerView mWorkerAvatarList;
    private LinearLayoutManager mWorkerAvatarListLayoutManager;
    private WorkerAvatarAdapter mWorkerAvatarAdapter;

    private EditText mCaseDescription;
    private ImageView mAddWorkerButton;

    private List<Worker> mWorkerListData = new ArrayList<>();

    private String mCaseId;

    private String mOriginalDescription;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_case_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void initialize() {
        mCaseId = getArguments().getString(CaseFragment.EXTRA_CASE_ID);
        mSelectionArgs = new String[] {mCaseId};

        findViews();
        setupViews();
        setupWorkerAvatarList();
    }

    private void findViews() {
        mWorkerAvatarList = (RecyclerView) getView().findViewById(R.id.worker_avatar_list);
        mCaseDescription = (EditText) getView().findViewById(R.id.case_description);
        mAddWorkerButton = (ImageView) getView().findViewById(R.id.add_worker_button);
    }

    private void setupViews() {
        mAddWorkerButton.setOnClickListener(this);
    }

    private void setupWorkerAvatarList() {
        mWorkerAvatarAdapter = new WorkerAvatarAdapter(mContext, mWorkerListData);
        mWorkerAvatarListLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

        mWorkerAvatarList.setLayoutManager(mWorkerAvatarListLayoutManager);
        mWorkerAvatarList.setAdapter(mWorkerAvatarAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        updateCaseDescription();
    }

    private void updateCaseDescription() {
        String currentDescription = TextUtils.isEmpty(mCaseDescription.getText().toString()) ?
                                                      "" : mCaseDescription.getText().toString();
        if (mOriginalDescription.equals(currentDescription)) return;

        mContext.startService(
                GeneralService.generateUpdateCaseDescriptionIntent(mContext, mCaseId, currentDescription));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext, WorkFlowContract.Case.CONTENT_URI,
                                mProjection, mSelection, mSelectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) return;

        cursor.moveToFirst();

        Case aCase = new Case(cursor.getString(CASE_ID),
                              cursor.getString(CASE_NAME),
                              cursor.getString(OWNER_ID),
                              Utils.unpackStrings(cursor.getString(WORKER_IDS)),
                              cursor.getString(DESCRIPTION),
                              cursor.getInt(IS_COMPLETED) == 1,
                              cursor.getLong(UPDATED_TIME));

        setDescription(aCase);
        setAddWorkerVisibility(aCase);
        setWorkerAvatar(aCase);
    }

    private void setDescription(Case aCase) {
        if (TextUtils.isEmpty(aCase.description)) {
            mCaseDescription.setHint(getString(R.string.case_info_no_description));
            mCaseDescription.setText("");
            mOriginalDescription = "";

        } else {
            mCaseDescription.setText(aCase.description);
            mOriginalDescription = aCase.description;
        }
    }

    private void setAddWorkerVisibility(Case aCase) {
        mAddWorkerButton.setVisibility(Utils.isSameId(WorkingData.getUserId(), aCase.ownerId) ?
                                       View.VISIBLE : View.GONE);
    }

    private void setWorkerAvatar(Case aCase) {
        mWorkerListData.clear();

        for (String workerId : aCase.workerIdList) {
            mWorkerListData.add(WorkingData.getInstance(mContext).getWorkerById(workerId));
        }

        mWorkerAvatarAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void setCaseId(String caseId) {
        updateCaseDescription();
        mCaseId = caseId;

        mWorkerListData.clear();
        mWorkerAvatarAdapter.notifyDataSetChanged();

        if (mSelectionArgs == null) {
            mSelectionArgs = new String[] {caseId};
        } else {
            mSelectionArgs[0] = caseId;
        }

        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_worker_button:
                mContext.startActivity(AddWorkerToCaseActivity.generateAddWorkerToCaseDialogIntent(mContext, mCaseId));

                break;
        }
    }
}
