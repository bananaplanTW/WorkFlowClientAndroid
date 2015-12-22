package com.nicloud.workflowclient.mainmenu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.connectserver.worker.LoadingWorkerAvatarCommand;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.login.LoginActivity;
import com.parse.ParsePush;

/**
 * Created by logicmelody on 2015/12/21.
 */
public class MainMenuFragment extends Fragment implements View.OnClickListener {

    private Context mContext;

    private ImageView mWorkerAvatar;
    private TextView mWorkerName;
    private TextView mWorkerDepartment;

    private TextView mLogoutButton;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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
        setupWorkerViews();
        loadWorkerAvatar();
    }

    private void findViews() {
        mWorkerAvatar = (ImageView) getView().findViewById(R.id.main_menu_worker_avatar);
        mWorkerName = (TextView) getView().findViewById(R.id.main_menu_worker_name);
        mWorkerDepartment = (TextView) getView().findViewById(R.id.main_menu_worker_department_name);
        mLogoutButton = (TextView) getView().findViewById(R.id.main_menu_logout);
    }

    private void setupWorkerViews() {
        mWorkerName.setText(WorkingData.getInstance(mContext).getLoginWorker().name);
        mWorkerDepartment.setText(WorkingData.getInstance(mContext).getLoginWorker().departmentName);

        mLogoutButton.setOnClickListener(this);
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

        LoadingWorkerAvatarCommand loadingWorkerAvatarCommand
                = new LoadingWorkerAvatarCommand(mContext, avatarUri, mWorkerAvatar);
        loadingWorkerAvatarCommand.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_menu_logout:
                ParsePush.unsubscribeInBackground("user_" + WorkingData.getUserId());
                //ParseUtils.removeLoginWorkerToParse();

                WorkingData.getInstance(mContext).resetTasks();
                WorkingData.resetAccount();

                SharedPreferences sharedPreferences = mContext.getSharedPreferences(WorkingData.SHARED_PREFERENCE_KEY, 0);
                sharedPreferences.edit().remove(WorkingData.USER_ID).remove(WorkingData.AUTH_TOKEN).commit();

                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                getActivity().finish();

                break;
        }
    }
}