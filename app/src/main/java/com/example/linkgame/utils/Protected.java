package com.example.linkgame.utils;

import android.content.Context;
import android.util.Log;

import com.example.linkgame.BuildConfig;

import java.net.URL;
import java.net.URLConnection;

/**
 * 软件保护机制
 * @Author: hu.wentao@outlook.com
 * @Date: 2019/5/8
 */
public class Protected {
    //2019-05-11 000
    static long m = 1557504000000L;

    public static boolean checkAppAvailable(){
        // 检测本地时间
        if(getLocalTime()> m){
            if (BuildConfig.DEBUG) Log.d("Protected", "软件保护机制已开启, 本地时间验证过期, 注意检查Protected.java");
            return false;
        }
        // 检测网络权限
        Long netTime = getNetTime();
        if(netTime == null){
            if (BuildConfig.DEBUG) Log.d("Protected", "软件保护机制已开启, 网络未连接, 注意检查Protected.java");
            return false;
        }
        // 检测网络时间
        if(netTime > m){
            if (BuildConfig.DEBUG) Log.d("Protected", "软件保护机制已开启, 网络时间验证已过期, 注意检查Protected.java");
            return false;
        }
        return true;
    }



    private static Long getNetTime() {
        URL url = null;//取得资源对象
        try {
            url = new URL("http://www.baidu.com");
            //url = new URL("http://www.ntsc.ac.cn");//中国科学院国家授时中心
            //url = new URL("http://www.bjtime.cn");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            long ld = uc.getDate(); //取得网站日期时间

            if (BuildConfig.DEBUG) Log.d("Protected", "通过工具获取网络时间:" + ld);
            return ld;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static long getLocalTime() {

        return System.currentTimeMillis();
    }
}
