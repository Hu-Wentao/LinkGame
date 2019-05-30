package com.example.linkgame.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.linkgame.utils.MyApplication;

/**
 * @Author: hu.wentao@outlook.com
 * @Date: 2019/4/17
 */
public class SharedData {
    // 应用常量
    public static final String USER_INFO = "user_info";             // 作为SharedPreference的文件名
    public static final String CURRENT_ACCOUNT = "current_account"; // 当前用户名的 key
    public static final String LOGIN_STATUS = "login_Status";       // 是否登录的key
    public static final String CURRENT_GAME_TYPE = "current_game_type"; // 当前游戏类型


    public static final String INTENT_MSG = "intent_message";   // login 跳到 game 的时候, 指定的intent 的extra name
    public static final String INTENT_TO_START = "startFragment";   // 跳转到 startFragment
    //----------------------------------
    private static volatile SharedPreferences preferences;

    private static SharedPreferences getPreferences() {
        SharedPreferences temp = preferences;
        if (temp == null) {
            synchronized (SharedData.class) {
                temp = preferences;
                if (temp == null) {
                    temp = preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                }
            }
        }
        return preferences;
    }


    public static void setInt(String key, int val) {
        getPreferences().edit().putInt(key, val).apply();
    }

    public static int getInt(String key, int defaultVal) {
        return getPreferences().getInt(key, defaultVal);
    }


    private static boolean setLoggingStatus(boolean b) {
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        return sharedPreferences.edit()
                .putBoolean(LOGIN_STATUS, b)
                .commit();
    }

    private static boolean getLoggingStatus() {
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(LOGIN_STATUS, false)) {  // 默认为未登录
            if (getCurrentAccount() == null) { // 如果返回已登录, 但是当前账户为null, 则仍为 未登录
                setLoggingStatus(false);
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * 设置当前的用户名, (如果用户成功登陆的话)
     * 如果 userAccount == null 表示注销当前用户
     */
    public static boolean setCurrentAccount(String userAccount) {
        SharedPreferences sharePreference = MyApplication.getContext().getApplicationContext().getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);

        setLoggingStatus(userAccount != null);
        return sharePreference.edit()
                .putString(CURRENT_ACCOUNT, userAccount)
                .commit();
    }

    /**
     * 获取当前的用户名, 如果为null, 则表示当前没有登陆
     */
    public static String getCurrentAccount() {
        SharedPreferences sharedPreferences = MyApplication.getContext().getApplicationContext().getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        return sharedPreferences.getString(CURRENT_ACCOUNT, null);
    }
}
