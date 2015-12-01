package com.nicloud.workflowclient.main.main;

import android.app.Application;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.nicloud.workflowclient.R;
import com.parse.Parse;
import com.parse.ParseInstallation;


/**
 * @author Danny Lin
 * @since 2015/10/3.
 */
public class MainApplication extends Application {

    public static Animation sFadeInAnimation;
    public static Animation sFadeOutAnimation;


    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
    }

    private void initialize() {
        setupAnimations();
        setupParse();
    }

    private void setupAnimations() {
        sFadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        sFadeOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
    }

    private void setupParse() {
        Parse.initialize(this,
                getString(R.string.parse_nicloud_application_id), getString(R.string.parse_nicloud_application_client_key));
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
