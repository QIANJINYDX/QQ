package com.example.qq.util;

import android.app.Application;

public class BaseApplication extends Application {
    public static BaseApplication baseApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
    }

    public static BaseApplication getInstance(){
        return baseApplication;
    }
}
