package com.example.linkgame.game;

import android.graphics.drawable.Drawable;

/**
 * 封装一个Drawable 和 int(表示textDrawableTag)
 *
 * @Author: hu.wentao@outlook.com
 * @Date: 2019/4/21
 */
public class Pic {
    public Drawable drawable;
    public int tag;

    public Pic(Drawable drawable, int tag) {
        this.drawable = drawable;
        this.tag = tag;
    }
}
