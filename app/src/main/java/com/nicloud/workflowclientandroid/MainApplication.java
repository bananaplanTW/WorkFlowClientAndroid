package com.nicloud.workflowclientandroid;

import android.app.Application;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


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
        sFadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        sFadeOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
    }
}
