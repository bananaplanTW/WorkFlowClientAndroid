package com.nicloud.workflowclientandroid.address;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.nicloud.workflowclientandroid.address.FetchAddressIntentService.Constants;


/**
 * Created by logicmelody on 2015/11/14.
 */
public class AddressResultReceiver extends ResultReceiver {

    public interface OnReceiveListener {
        void onReceiveSuccessful(String message);
        void onReceiveFailed(String message);
    }

    private OnReceiveListener mOnReceiveListener;


    public AddressResultReceiver(Handler handler, OnReceiveListener listener) {
        super(handler);
        mOnReceiveListener = listener;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);

        String message = resultData.getString(Constants.KEY_RESULT_DATA);

        switch (resultCode) {
            case Constants.RESULT_SUCCESS:
                mOnReceiveListener.onReceiveSuccessful(message);
                break;

            case Constants.RESULT_FAILURE:
                mOnReceiveListener.onReceiveFailed(message);
                break;
        }
    }
}
