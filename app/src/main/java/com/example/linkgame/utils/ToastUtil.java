package com.example.linkgame.utils;

import android.widget.Toast;

public class ToastUtil {

    public static void toast(String msg){
        Toast.makeText(MyApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void longToast(String msg){
        Toast.makeText(MyApplication.getContext(), msg, Toast.LENGTH_LONG).show();
    }

}
