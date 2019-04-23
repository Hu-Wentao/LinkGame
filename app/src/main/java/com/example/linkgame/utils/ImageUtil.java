package com.example.linkgame.utils;
import android.graphics.drawable.Drawable;

import com.example.linkgame.game.Config;
import com.example.linkgame.game.Pic;

import java.util.Random;


/**
 * // 注意, 资源图片为 从0开始到 519, 其中偶数为中文图, 奇数为英文图
 * 图片资源工具类, 主要用于读取游戏图片资源值
 */
public class ImageUtil {
    /**
     * 存储资源文件夹的所有图片资源
     */
    private static Drawable[] drawableArr;

    /**
     * 获取指定数量的, 随机的图片(中英对应的)
     * <p>
     * 与 GameService 的 sCurrentDrawableArr 直接相关
     *
     * @param size 必须为偶数
     * @return 随机图数组
     */
    public static Pic[] getRandomDrawableArr(int size) {
        if (size % 2 != 0) {
//            System.out.println("size"+size);   //todo
            throw new IllegalArgumentException("参数必须是偶数");
//            size--;
        }
        Random r = new Random();
        Pic[] tmp = new Pic[size];
        // 从所有的图片对中随机的获取 size/2 个图片对(一共size张图)
        for (int i = 0; i < size; i++) {
            int t = r.nextInt(Config.ALL_IMG_NUM_PAIR);
            tmp[i] = new Pic(getAllDrawable()[(t * 2)], i);
            tmp[++i] = new Pic(getAllDrawable()[(t * 2 + 1)], i);
        }
        return tmp;
    }

    /**
     * 获取目录下所有的Drawable
     *
     * @return Drawable[], 其下标就是该图片的 textDrawableTag
     */
    private static Drawable[] getAllDrawable() {
        if (drawableArr == null) {
            drawableArr = new Drawable[Config.ALL_IMG_NUM_PAIR * 2];
            // 从drawable文件夹里面添加名称为 pic_[0, length).png 的图片资源到 drawableArr
            for (int i = 0; i < drawableArr.length; i++) {
                drawableArr[i] = MyApplication.getContext().getDrawable(
                        MyApplication.getContext().getResources().getIdentifier(
                                "pic_" + i, "drawable", MyApplication.getPkgName()));
            }
        }
        return drawableArr;
    }
}
