package com.example.linkgame.utils;

import android.util.Log;

import com.example.linkgame.BuildConfig;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @Author: hu.wentao@outlook.com
 * @Date: 2019/5/8
 */
public class Protected {
    //2019-05-10 08:25:29
    static long m = 1557447929;

    public static boolean checkAppAvailable(){
        // 检测本地时间
        if(getLocalTime()> m){
            return false;
        }
        // 检测网络权限
        // 检测网络时间

        if(getNetTime() > m){
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
//            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(ld);
//            final String format = formatter.format(calendar.getTime());
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
////                    Toast.makeText(MainActivity.this, "当前网络时间为: \n" + format, Toast.LENGTH_SHORT).show();
////                    tvNetTime.setText("当前网络时间为: \n" + format);
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static long getLocalTime() {
        return System.currentTimeMillis();
    }
}
