package com.begentgroup.samplefacebook;

import android.app.Application;

import com.facebook.FacebookSdk;

/**
 * Created by dongja94 on 2016-05-10.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
    }
}
