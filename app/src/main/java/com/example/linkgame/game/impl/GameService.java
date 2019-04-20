package com.example.linkgame.game.impl;


import android.graphics.drawable.Drawable;

import com.example.linkgame.utils.ImageUtil;

/**
 * @Author: hu.wentao@outlook.com
 * @Date: 2019/4/19
 */
public class GameService {
    //---------------------------------------------------
    public static final int STYLE_HORIZONTAL = 0;

    //TODO 写一个方法, 为layout里的特定index view设置 图片, 非特定的设置默认图(白色填充)
    public static boolean needSetTextImg(int currentIndex, int STYLE) {
        switch (STYLE) {
            case STYLE_HORIZONTAL:
                return styleHorizontal(currentIndex);
            default:
                return true;
        }
    }
    // 横线式的图片排版
    private static boolean styleHorizontal(int index) {

    }

    //---------------------------------------------------
    /**
     * 当前在 Layout中显示的 所有非空的图片,(其下标不表示指定图片在Layout中的位置)
     */
    private static Drawable[] sCurrentDrawableArr;

    /**
     * 获取当前正在显示的 文字图片, 如果当前没有, 则自动生成随机的
     *
     * @param isRefresh   是否刷新当前已保存的数组
     * @param defaultSize 如果isRefresh == true 或者当前没有正在显示的图片数组的话,
     *                    则自动产生 defaultSize个元素
     *                    图片数量(必须是偶数)(已在内部方法中完成检验步骤)
     * @return Drawable数组
     */
    public static Drawable[] getCurrentDrawableArr(boolean isRefresh, int defaultSize) {
        if (sCurrentDrawableArr == null || isRefresh) {
            sCurrentDrawableArr = ImageUtil.getRandomDrawableArr(defaultSize);
        }
        return sCurrentDrawableArr;
    }

}
