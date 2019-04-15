package com.example.linkgame;

import android.app.Application;
import android.content.Context;

/**
 * @Author: hu.wentao@outlook.com
 * @Date: 2019/4/15
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
    public static String getPkgName(){
        return context.getPackageName();
    }
}
