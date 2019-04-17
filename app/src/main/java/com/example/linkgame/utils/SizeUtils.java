package com.example.linkgame.utils;

import android.util.TypedValue;

/**
 * 单位转换工具
 */
public class SizeUtils {
    public static int dp2Px(int dpi) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpi, MyApplication.getContext().getResources().getDisplayMetrics());
    }

    public static int px2Dp(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, MyApplication.getContext().getResources().getDisplayMetrics());
    }

    public static int sp2Px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, MyApplication.getContext().getResources().getDisplayMetrics());
    }

    public static int px2Sp(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, MyApplication.getContext().getResources().getDisplayMetrics());
    }
}
