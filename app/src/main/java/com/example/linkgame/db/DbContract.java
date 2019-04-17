package com.example.linkgame.db;

import android.provider.BaseColumns;

public class DbContract {
    // 用户信息
    public static final class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_USER_ACCOUNT = "userName";
        public static final String COLUMN_USER_PWD = "userPwd";
    }

}
