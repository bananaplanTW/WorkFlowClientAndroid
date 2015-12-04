package com.nicloud.workflowclient.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.data.connectserver.worker.CheckLoggedInStatusCommand;
import com.nicloud.workflowclient.data.connectserver.worker.LoadingLoginWorkerCommand;
import com.nicloud.workflowclient.data.connectserver.worker.UserLoggingInCommand;
import com.nicloud.workflowclient.main.main.MainActivity;
import com.nicloud.workflowclient.main.main.MainApplication;
import com.parse.ParsePush;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        CheckLoggedInStatusCommand.OnFinishCheckingLoggedInStatusListener,
        UserLoggingInCommand.OnFinishLoggedInListener, LoadingLoginWorkerCommand.OnLoadingLoginWorker {

    private static final String TAG = "LoginActivity";

    public static final long NICLOUD_LOGO_DISPLAYING_INTERVAL = 1000L;

    private SharedPreferences mSharedPreferences;

    private LinearLayout mLoginViewContainer;

    private EditText mCompanyAccountEditText;
    private EditText mAccountEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;

    private ImageView mNiCloudImage;
    private View mNICContainer;
    private Button mNICRetryButton;

    private String mCompanyAccount;
    private String mUserId;
    private String mAuthToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
    }

    private void initialize() {
        getSharedPreferences();
        findViews();
        setupViews();
        hideAllViews();
    }

    private void getSharedPreferences() {
        mSharedPreferences = getSharedPreferences(WorkingData.SHARED_PREFERENCE_KEY, 0);
        mCompanyAccount = mSharedPreferences.getString(WorkingData.COMPANY_ACCOUNT, "");
        mUserId = mSharedPreferences.getString(WorkingData.USER_ID, "");
        mAuthToken = mSharedPreferences.getString(WorkingData.AUTH_TOKEN, "");
    }

    private void findViews () {
        mNiCloudImage = (ImageView) findViewById(R.id.login_nicloud_image);
        mLoginViewContainer = (LinearLayout) findViewById(R.id.login_container);
        mCompanyAccountEditText = (EditText) findViewById(R.id.login_company_account);
        mAccountEditText = (EditText) findViewById(R.id.login_account_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.login_password_edit_text);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mNICContainer = findViewById(R.id.no_internet_connection_container);
        mNICRetryButton = (Button) findViewById(R.id.no_internet_connection_retry_button);
    }

    private void setupViews () {
        mCompanyAccountEditText.setText(mCompanyAccount);
        mLoginButton.setOnClickListener(this);
        mNICRetryButton.setOnClickListener(this);
    }

    private void hideAllViews () {
        mLoginViewContainer.setVisibility(View.GONE);
    }

    private void showAllViews () {
        mLoginViewContainer.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        LoadingDataUtils.sBaseUrl = "http://" + mCompanyAccount;
        WorkingData.setUserId(mUserId);
        WorkingData.setAuthToken(mAuthToken);

        checkLoggedInStatus();
    }

    private void checkLoggedInStatus() {
        if (TextUtils.isEmpty(mCompanyAccount) ||
            TextUtils.isEmpty(WorkingData.getUserId()) || TextUtils.isEmpty(WorkingData.getAuthToken())) {
            showAllViews();
            return;
        }

        CheckLoggedInStatusCommand checkLoggedInStatusCommand = new CheckLoggedInStatusCommand(this, this);
        checkLoggedInStatusCommand.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //overridePendingTransition(0, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                LoadingDataUtils.sBaseUrl = "http://" + mCompanyAccountEditText.getText().toString();
                String username = mAccountEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                UserLoggingInCommand userLoggingInCommand = new UserLoggingInCommand(this, username, password, this);
                userLoggingInCommand.execute();

                break;

            case R.id.no_internet_connection_retry_button:
                showNiCloudImage(true);
                checkLoggedInStatus();

                break;
        }
    }

    private void showNiCloudImage(boolean isDisplayed) {
        if (isDisplayed) {
            mNICContainer.startAnimation(MainApplication.sFadeOutAnimation);
            mNiCloudImage.startAnimation(MainApplication.sFadeInAnimation);
            mNICContainer.setVisibility(View.GONE);
            mNiCloudImage.setVisibility(View.VISIBLE);
        } else {
            mNICContainer.startAnimation(MainApplication.sFadeInAnimation);
            mNiCloudImage.startAnimation(MainApplication.sFadeOutAnimation);
            mNICContainer.setVisibility(View.VISIBLE);
            mNiCloudImage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoggedIn() {
        loadingLoginWorker();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoggedOut(boolean isFailCausedByInternet) {
        if (isFailCausedByInternet) {
            showNiCloudImage(false);
        } else {
            showAllViews();
        }
    }

    @Override
    public void onLoggedInSucceed(String userId, String authToken) {
        mSharedPreferences.edit()
                .putString(WorkingData.USER_ID, userId)
                .putString(WorkingData.AUTH_TOKEN, authToken)
                .putString(WorkingData.COMPANY_ACCOUNT, mCompanyAccountEditText.getText().toString())
                .apply();

        WorkingData.setUserId(userId);
        WorkingData.setAuthToken(authToken);

        //ParseUtils.registerLoginWorkerToParse(WorkingData.getInstance(mContext).getLoginWorker().id);
        ParsePush.subscribeInBackground("user_" + WorkingData.getUserId());

        loadingLoginWorker();
    }

    private void loadingLoginWorker() {
        LoadingLoginWorkerCommand loadingLoginWorkerCommand = new LoadingLoginWorkerCommand(this, this);
        loadingLoginWorkerCommand.execute();
    }

    @Override
    public void onLoggedInFailed(boolean isFailCausedByInternet) {
        if (isFailCausedByInternet) {
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.error_account_password), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoadingLoginWorkerSuccessful() {
        goToMainActivity();
    }

    @Override
    public void onLoadingLoginWorkerFailed(boolean isFailCausedByInternet) {
        showNiCloudImage(false);
    }
}
