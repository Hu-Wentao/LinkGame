package com.example.linkgame.game;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.example.linkgame.BuildConfig;
import com.example.linkgame.R;

import java.util.LinkedList;
import java.util.Arrays;
import java.util.Random;

import static android.content.ContentValues.TAG;

/**
 * 用于操作 layout 中ImageView (或许能够与 Pic 类 合并)
 *
 * @Author: hu.wentao@outlook.com
 * @Date: 2019/4/22
 */
public class ViewOp {
    // layout 中的 ImageView 控件数组, 该数组的索引 与ImageView 在GridLayout中的位置 直接相关
    private static ImageView[] viewArr;
    // 每个ImageView控件 对应的 Tag
    private static Integer[] picTagArr;       // 目前 ImageView 元素仅需要设置 viewIndex 的tag, 用于点击事件, 以确定被点击的控件

    //======== test ======================================================================
    public static void testShowTag() {
        String s = "";
        for (int i = 0; i < picTagArr.length; i++) {
            if (i % (Config.GRID_COLS) == 0) {
                s += "\n";
            }
            if (picTagArr[i] != null) {
                s += "  " + picTagArr[i] + (picTagArr[i] < 10 ? "   " : "  ");
            } else {
                s += "  " + picTagArr[i];
            }
        }
        Log.d(TAG, "testShowTag:" + s
//                + "\n tagArr长度:" + picTagArr.length
        );
    }
    //======== bind = 绑定 Pic数组 与 ImageView 数组 的关系=====================================================================

    /**
     * 绑定 Pic数组 与 ImageView 数组 的关系
     *
     * @param viewIndex ImageView数组索引
     * @param p         如果p为 null , 则表示这个控件不绑定任何pic , 应当将其 背景设为 img_blank
     */
    public static void bindPicToView(int viewIndex, Pic p) {
        if(p == null){
            viewArr[viewIndex].setBackgroundResource(R.drawable.img_blank);
            return;
        }
        viewArr[viewIndex].setBackground(p.drawable);   // 绑定背景图片
        picTagArr[viewIndex] = p.tag;   // 绑定tag
    }


    //======== set ======================================================================

    // 初始化数组
    public static void initViewArr(int viewArrSize) {
        viewArr = new ImageView[viewArrSize];
        picTagArr = new Integer[viewArrSize];
    }

    // 为view元素赋值
    public static void setView(int index, ImageView view) {
        viewArr[index] = view;
    }

    // 为view添加Tag
    private static void setTag(int index, int tag) {
        if (BuildConfig.DEBUG) Log.d(TAG, "为" + index + "号view添加picTag:" + tag);
        picTagArr[index] = tag;
    }

    // 同时添加 Background
    private static void setBackground(int index, Drawable bg) {
        viewArr[index].setBackground(bg);
    }

    // 目前用途:  设置tag; 设置点击事件;
    public static ImageView get(int index) {
        return viewArr[index];
    }

    // 将view backGround设为 img_blank, 并且删除picTag
    static void setBlank(int index) {
        viewArr[index].setImageDrawable(null);
        viewArr[index].setBackgroundResource(R.drawable.img_blank);
        picTagArr[index] = null;
    }

    // 将指定 view 设为选中, 或取消选中
    static void setSelect(int index, boolean setSelect) {
        if (setSelect) {
            viewArr[index].setImageResource(R.drawable.selected);
        } else {
            viewArr[index].setImageDrawable(null);
        }
    }

    //======== get ======================================================================
    static int getPicTag(int index) {
        return picTagArr[index];
    }

    //======== is ======================================================================
    // true: 该控件有picTag
    public static boolean hasPicTag(int index) {
        return picTagArr[index] != null;
    }

    // true: 两个 view 的tag 是中英对应的
    static boolean isPicTagMatch(int viewIndexA, int viewIndexB) {
        int tagA = picTagArr[viewIndexA], tagB = picTagArr[viewIndexB];
        return tagA + (tagA % 2 == 0 ? 1 : -1) == tagB;
    }

    /**
     * 重新排布layout的view
     * <p>
     * * 1. 清空 ViewArr 的 picTag,
     * 将 background 均设为 img_blank
     * 将 ImageDrawable 均设为 null
     * <p>
     * * 2. 重新设置View  的图片资源
     */
    public static void resetViewArrSrc() {
        LinkedList<Pic> list = GameService.getCurrentDrawableList(-1);

        for (int i = 0; i < picTagArr.length; i++) {
            if (picTagArr[i] != null) {
                viewArr[i].setBackgroundResource(R.drawable.img_blank);
                viewArr[i].setImageDrawable(null);
                picTagArr[i] = null;
            }
        }

        Random r = new Random();
        for (int i = 0; i < list.size(); i++) {
            int j = r.nextInt(Config.GRID_COLS * Config.GRID_ROWS);
            if (picTagArr[i] == null) {
                i--;
                continue;
            }
            setBackground(j, list.get(i).drawable);
            setTag(j, list.get(i).tag);
        }
    }
}
