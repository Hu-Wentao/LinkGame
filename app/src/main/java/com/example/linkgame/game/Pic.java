package com.example.linkgame.game;

import android.graphics.drawable.Drawable;

/**
 * 封装一个Drawable 和 int(表示textDrawableTag)
 *
 * @Author: hu.wentao@outlook.com
 * @Date: 2019/4/21
 */
public class Pic {
    public Drawable drawable;   // 图片
    public int tag;             // 标识图片的tag

    public Pic(Drawable drawable, int tag) {
        this.drawable = drawable;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "tag为: "+this.tag;
    }
}
