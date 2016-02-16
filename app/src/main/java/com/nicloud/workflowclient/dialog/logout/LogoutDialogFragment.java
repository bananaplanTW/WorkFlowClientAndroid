package com.nicloud.workflowclient.dialog.logout;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.nicloud.workflowclient.R;

/**
 * Created by logicmelody on 2016/2/16.
 */
public class LogoutDialogFragment extends DialogFragment {

    public interface OnLogoutListener {
        void onLogoutYes();
        void onLogoutNo();
    }

    public OnLogoutListener mOnLogoutListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnLogoutListener) {
            mOnLogoutListener = (OnLogoutListener) activity;

        } else if (getTargetFragment() instanceof OnLogoutListener) {
            mOnLogoutListener = (OnLogoutListener) getTargetFragment();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(getString(R.string.dialog_log_out));
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mOnLogoutListener.onLogoutNo();
            }
        });
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mOnLogoutListener.onLogoutYes();
            }
        });

        return builder.create();
    }
}
