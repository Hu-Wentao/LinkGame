package com.example.linkgame.activities;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linkgame.BuildConfig;
import com.example.linkgame.R;
import com.example.linkgame.data.DbContract;
import com.example.linkgame.data.SharedData;
import com.example.linkgame.data.UserDbHelper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView mTitleView;
    TextView mRegisterTipView;
    View mRegisterView;
    View mLoginSwitchView;

    EditText mRegisterUserNameView;
    EditText mRegisterPasswordView;
    RelativeLayout mLoginPwdLayoutView;

    EditText mLoginUserNameView;
    EditText mLoginPasswordView;

    Button mLoginButton;


    private boolean isVisitorLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_switch:
                switchLoginView();
                break;
            case R.id.tv_register_tip:
                toggleRegisterView(true);
                break;
            case R.id.fab_close:
                toggleRegisterView(false);
                break;
            case R.id.bt_login:
                login();
                break;
            case R.id.bt_register:
                register();
                break;
        }
    }

    public void switchLoginView() {
        switchLoginView(isVisitorLogin = !isVisitorLogin);
    }

    void switchLoginView(boolean isVisitorLogin) {
        mTitleView.setText(isVisitorLogin ? "游客登录" : "用户登录");

        ObjectAnimator alpha = ObjectAnimator.ofFloat(mRegisterTipView, "alpha", isVisitorLogin ? 1F : 0F, isVisitorLogin ? 0F : 1F);
        alpha.setDuration(500L);
        alpha.start();

        if (isVisitorLogin) {
            //游客登录无需密码
            mLoginPasswordView.setVisibility(View.GONE);
            mLoginPasswordView.setVisibility(View.GONE);
            mLoginButton.setText("直接进入");
        } else {
            mLoginPasswordView.setVisibility(View.VISIBLE);
            mLoginPasswordView.setVisibility(View.VISIBLE);
            mLoginButton.setText("登录");
        }
    }

    public void toggleRegisterView(boolean isOpen) {
        if (isOpen && !mRegisterView.isShown()) {
            mRegisterView.setVisibility(View.VISIBLE);
        }
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mRegisterView, "translationY", isOpen ? -mRegisterView.getHeight() : 0F, isOpen ? 0F : -mRegisterView.getHeight());
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mLoginSwitchView, "scaleX", isOpen ? 1F : 0F, isOpen ? 0F : 1F);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mLoginSwitchView, "scaleY", isOpen ? 1F : 0F, isOpen ? 0F : 1F);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(500L);
        set.playTogether(scaleX, scaleY, translationY);
        set.start();
    }

    /**
     * 登录
     */
    public void login() {
        mLoginUserNameView.setError(null);
        mLoginPasswordView.setError(null);

        String username = mLoginUserNameView.getText().toString();
        String password = mLoginPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            mLoginUserNameView.setError("用户名不能为空");
            focusView = mLoginUserNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password) && !isVisitorLogin) {
            mLoginPasswordView.setError("密码不能为空");
            focusView = mLoginPasswordView;
            cancel = true;
        }

        if (BuildConfig.DEBUG)
            Log.d("LoginActivity", "当前cancel:" + cancel + "isVisitorLogin: " + isVisitorLogin);

        if (cancel) {
            focusView.requestFocus();
        } else {
            // 如果是访客登录, 则直接开始
            if (isVisitorLogin || check(username, password)) {
                SharedData.setCurrentAccount(username);
                Intent intent = new Intent(this, GameActivity.class);

                intent.putExtra(SharedData.INTENT_MSG, SharedData.INTENT_TO_START);   //todo extra 问题
                startActivity(new Intent(this, GameActivity.class));
                this.finish();
            }
        }

    }

    /**
     * 注册
     */
    public void register() {
        mRegisterUserNameView.setError(null);
        mRegisterPasswordView.setError(null);

        String userName = mRegisterUserNameView.getText().toString();
        String password = mRegisterPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(userName)) {
            mRegisterUserNameView.setError("用户名不能为空");
            focusView = mRegisterUserNameView;
            cancel = true;
        } else { // 检测用户名是否重复
            Cursor cursor = mReadableDb.query(DbContract.UserEntry.TABLE_NAME,
                    new String[]{DbContract.UserEntry.COLUMN_USER_PWD},
                    DbContract.UserEntry.COLUMN_USER_ACCOUNT + "=?",
                    new String[]{userName},
                    null,
                    null,
                    DbContract.UserEntry._ID
            );
            // 如果找到相同的用户名
            if (cursor.getCount() != 0) {
                Toast.makeText(this, "用户名已存在, 请重新输入, 或直接登录", Toast.LENGTH_SHORT).show();
                cursor.close();
                focusView = mRegisterUserNameView;
                cancel = true;
            }
        }


        if (TextUtils.isEmpty(password)) {
            mRegisterPasswordView.setError("密码不能为空");
            focusView = mRegisterPasswordView;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {
            addNewUser(userName, password);
            SharedData.setCurrentAccount(userName);
//            startActivity(new Intent(this, GameActivity.class));
            toggleRegisterView(false);  // 关闭 注册面板

        }
    }

    //----------数据库操作
    private SQLiteDatabase mReadableDb;
    private SQLiteDatabase mWritableDb;

    /**
     * 检测用户名密码是否正确
     *
     * @param userName 用户名
     * @param pwd      密码
     * @return 是否登录
     */
    private boolean check(String userName, String pwd) {
        // 创建一个指针
        Cursor cursor = mReadableDb.query(DbContract.UserEntry.TABLE_NAME,
                new String[]{DbContract.UserEntry.COLUMN_USER_PWD},
                DbContract.UserEntry.COLUMN_USER_ACCOUNT + "=?",
                new String[]{userName},
                null,
                null,
                DbContract.UserEntry._ID
        );
        // 如果没有找到匹配的用户名
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "没有此用户名, 请先注册", Toast.LENGTH_SHORT).show();
            cursor.close();
            return false;
        }
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex(DbContract.UserEntry.COLUMN_USER_PWD)).equals(pwd)) {
                cursor.close(); // 查询完毕 及时释放资源
                Toast.makeText(this, "登录成功!", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        Toast.makeText(this, "密码错误!", Toast.LENGTH_SHORT).show();
        cursor.close();
        return false;
    }

    /**
     * 将新注册的用户信息写入数据库
     */
    private long addNewUser(String userAccount, String pwd) {
        ContentValues cv = new ContentValues();
        cv.put(DbContract.UserEntry.COLUMN_USER_ACCOUNT, userAccount);
        cv.put(DbContract.UserEntry.COLUMN_USER_PWD, pwd);
        return mWritableDb.insert(DbContract.UserEntry.TABLE_NAME, null, cv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        setContentView(R.layout.activity_login);

        mRegisterView = findViewById(R.id.lt_register);
        mLoginSwitchView = findViewById(R.id.fab_switch);

        mTitleView = findViewById(R.id.tv_title);
        mRegisterTipView = findViewById(R.id.tv_register_tip);

        mLoginUserNameView = findViewById(R.id.et_username_1);
        mLoginPasswordView = findViewById(R.id.et_password_1);
        mLoginPwdLayoutView = findViewById(R.id.layout_pwd);

        mRegisterUserNameView = findViewById(R.id.et_username_2);
        mRegisterPasswordView = findViewById(R.id.et_password_2);

        mLoginButton = findViewById(R.id.bt_login);

        mLoginSwitchView.setOnClickListener(this);
        mRegisterTipView.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        findViewById(R.id.bt_register).setOnClickListener(this);
        findViewById(R.id.fab_close).setOnClickListener(this);

        // 数据
        mReadableDb = new UserDbHelper(this).getReadableDatabase();
        mWritableDb = new UserDbHelper(this).getWritableDatabase();
    }
}
