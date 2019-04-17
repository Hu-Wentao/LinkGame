package com.example.linkgame.activities;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linkgame.R;
import com.example.linkgame.db.DbContract;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatSpinner mClazzSpinner;
    TextView mTitleView;
    TextView mRegisterTipView;
    View mRegisterView;
    View mLoginSwitchView;

    EditText mRegisterUserNameView;
    EditText mRegisterNameView;
    EditText mRegisterNumberView;
    EditText mRegisterPasswordView;

    EditText mLoginUserNameView;
    EditText mLoginPasswordView;


    private boolean isStudentLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mRegisterView = findViewById(R.id.lt_register);
        mLoginSwitchView = findViewById(R.id.fab_switch);

        mTitleView = findViewById(R.id.tv_title);
        mRegisterTipView = findViewById(R.id.tv_register_tip);

        mLoginUserNameView = findViewById(R.id.et_username_1);
        mLoginPasswordView = findViewById(R.id.et_password_1);

        mRegisterUserNameView = findViewById(R.id.et_username_2);
        mRegisterNameView = findViewById(R.id.et_real_name);
        mRegisterNumberView = findViewById(R.id.et_number);
        mRegisterPasswordView = findViewById(R.id.et_password_2);

        mClazzSpinner = findViewById(R.id.sp_clazz);

        mLoginSwitchView.setOnClickListener(this);
        mRegisterTipView.setOnClickListener(this);
        findViewById(R.id.bt_login).setOnClickListener(this);
        findViewById(R.id.bt_register).setOnClickListener(this);
        findViewById(R.id.fab_close).setOnClickListener(this);


//        mDataManager = new TeacherDataManager(this) {
//            @Override
//            public void onDataLoaded(String key, List<BmobTeacher> data) {
//                if (data != null && !data.isEmpty()) {
//                    ArrayAdapter<BmobTeacher> adapter = new ArrayAdapter<BmobTeacher>(LoginActivity.this, R.layout.simple_spinner_item, data){
//                        @NonNull
//                        @Override
//                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                            TextView textView = (TextView) super.getView(position, convertView, parent);
//                            BmobTeacher teacher = getItem(position);
//                            textView.setText(String.format(Locale.getDefault(), "%s-%s", teacher.getClazz(), teacher.getName()));
//                            return textView;
//                        }
//
//                        @Override
//                        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                            TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
//                            BmobTeacher teacher = getItem(position);
//                            textView.setText(String.format(Locale.getDefault(), "%s-%s", teacher.getClazz(), teacher.getName()));
//                            return textView;
//                        }
//                    };
//                    adapter.setDropDownViewResource(android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item);
//                    mClazzSpinner.setAdapter(adapter);
//                }
//            }
//        };

//        mDataManager.loadAllTeachers();
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
        mTitleView.setText(isStudentLogin ? "老师登录" : "学生登录");

        ObjectAnimator alpha = ObjectAnimator.ofFloat(mRegisterTipView, "alpha", isStudentLogin ? 1F : 0F, isStudentLogin ? 0F : 1F);
        alpha.setDuration(500L);
        alpha.start();

        isStudentLogin = !isStudentLogin;
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

        if (TextUtils.isEmpty(password)) {
            mLoginPasswordView.setError("密码不能为空");
            focusView = mLoginPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            final ProgressDialog dialog = ProgressDialog.show(this, null, "正在登录...", true, false);
//            BmobUserHelper.get().login(username, password, isStudentLogin, new IBmoLogin.Callback() {
//                @Override
//                public void onSuccess(MyUser user) {
//                    dialog.dismiss();
//                    toast("登录成功");
//                    startActivity(new Intent(LoginActivity.this, isStudentLogin ? StudentActivity.class : TeacherActivity.class));
//                    finish();
//                }
//
//                @Override
//                public void onFailed(String message) {
//                    dialog.dismiss();
//                    toast(message);
//                }
//            });
        }

    }

    public void register() {
        mRegisterUserNameView.setError(null);
        mRegisterNameView.setError(null);
        mRegisterNumberView.setError(null);
        mRegisterPasswordView.setError(null);

        String username = mRegisterUserNameView.getText().toString();
        String realname = mRegisterNameView.getText().toString();
        String number = mRegisterNumberView.getText().toString();
        String password = mRegisterPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            mRegisterUserNameView.setError("用户名不能为空");
            focusView = mRegisterUserNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(realname)) {
            mRegisterNameView.setError("姓名不能为空");
            focusView = mRegisterNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(number)) {
            mRegisterNumberView.setError("学号不能为空");
            focusView = mRegisterNumberView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mRegisterPasswordView.setError("密码不能为空");
            focusView = mRegisterPasswordView;
            cancel = true;
        }

//        BmobTeacher teacher = (BmobTeacher) mClazzSpinner.getSelectedItem();
//        if(teacher == null){
//            toast("请选择所在班级");
//            return;
//        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            final ProgressDialog dialog = ProgressDialog.show(this, null, "正在注册...", true, false);
//            BmobUserHelper.get().studentRegister(username, password, realname, number, teacher, new IBmoLogin.Callback() {
//                @Override
//                public void onSuccess(MyUser user) {
//                    dialog.dismiss();
//                    toast("注册成功，已自动登录");
//                    startActivity(new Intent(LoginActivity.this, StudentActivity.class));
//                    finish();
//                }
//
//                @Override
//                public void onFailed(String message) {
//                    dialog.dismiss();
//                    toast(message);
//                }
//            });

        }
    }

    private SQLiteDatabase mDb;
    private boolean check(String userAccount, String pwd){
        // 创建一个指针
        Cursor cursor = mDb.query(DbContract.UserEntry.TABLE_NAME,
                new String[]{DbContract.UserEntry.COLUMN_USER_PWD},
                DbContract.UserEntry.COLUMN_USER_ACCOUNT+"=?",
                new String[]{userAccount},
                null,
                null,
                DbContract.UserEntry._ID
        );
        // 如果没有找到匹配的用户名
        if(cursor.getCount() == 0){
            Toast.makeText(this, "没有此用户名, 请先注册", Toast.LENGTH_SHORT).show();
            cursor.close();
            return false;
        }
        while (cursor.moveToNext()){
            if(cursor.getString(cursor.getColumnIndex(DbContract.UserEntry.COLUMN_USER_PWD)).equals(pwd)){
                cursor.close(); // 查询完毕 及时释放资源
                return true;
            }
        }
        Toast.makeText(this, "密码错误!", Toast.LENGTH_SHORT).show();
        cursor.close();
        return false;
    }

    @Override
    protected void onDestroy() {
//        mDataManager.cancelLoading();
        super.onDestroy();
    }
}
