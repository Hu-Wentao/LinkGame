package com.example.linkgame.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 3;

    public UserDbHelper( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + DbContract.UserEntry.TABLE_NAME + " (" +
                DbContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbContract.UserEntry.COLUMN_USER_ACCOUNT + " TEXT NOT NULL, " +          // 用户名 使用 account
                DbContract.UserEntry.COLUMN_USER_PWD + " TEXT NOT NULL" +
                ");";
        // 执行语句
        db.execSQL(SQL_CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 更新数据库操作, 如果数据库版本更新了, 以下的做法是删除已有的数据库
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.UserEntry.TABLE_NAME);
        onCreate(db);
    }
}
