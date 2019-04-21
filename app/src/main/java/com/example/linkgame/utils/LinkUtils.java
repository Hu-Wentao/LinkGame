package com.example.linkgame.utils;

import android.media.Image;
import android.widget.ImageView;

import com.example.linkgame.R;
import com.example.linkgame.game.Config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 处理图片直接的连接问题
 *
 * @Author: hu.wentao@outlook.com
 * @Date: 2019/4/21
 */
public class LinkUtils {
    /**
     * 获取两个view 之间的 连接路径, 由view的索引构成, 包括起始点和结束点(savedIndex与index)
     *
     * @param imgArr     ImageView 集合
     * @param startIndex 被点击的view的index
     * @param endIndex   已设为 被选中 状态的index
     * @return 连接路径, 如果为null, 则表示这两个view 无法连接
     */
    public static ArrayList<Integer> getConnectLink(ImageView[] imgArr, int startIndex, int endIndex) {
        // 确保 startIndex < endIndex (即 start 在 end 的 上 方)
        if (startIndex > endIndex)
            return getConnectLink(imgArr, endIndex, startIndex);
        // 转换为二维索引 [0] 是行, [1] 是列
        int[] sArr = convert(startIndex), eArr = convert(endIndex);
        // 1. 排除两个方块 左右, 上下 相邻的情况( 表达式表示: 如果在 同一行, 且相邻的两个索引)
        if (sArr[0] == eArr[0] && (sArr[1] + eArr[1]) % 2 == 1 ||
                sArr[1] == eArr[1] && (sArr[0] + eArr[0] % 2 == 1)) {
            return new ArrayList<>(Arrays.asList(startIndex, endIndex));
        }
        // 创建一个临时变量, 临时保存路径点
        ArrayList<Integer> list;

        // 2. 0折连接(一线连)
        if (sArr[0] == eArr[0] || sArr[1] == eArr[1]) { // 同行或同列
            if ((list = getChannel(imgArr, sArr, eArr)) != null)
                return list;
        }
        // 3. 1折连接(两线连), 寻找与 sArr 平行 的折点 再向下到终点 || 向下找 与sArr同列 的折点  再向右
        if (imgArr[convert(sArr[0], eArr[1])].getTag(R.id.PicTag) != null) {  // 如果右边的折点是空白的
            if ((list = getChannel(imgArr, sArr, new int[]{sArr[0], eArr[1]}, eArr)) != null)
                return list;
        } else if (imgArr[convert(eArr[0], sArr[1])].getTag(R.id.packed) != null) {// 如果下边的折点是空白的
            if ((list = getChannel(imgArr, sArr, new int[]{eArr[0], sArr[1]}, eArr)) != null)
                return list;
        }
        // 4. 2折连接



        return null;
    }

    /**
     * 获取 多个 可以直线相连的点 的有效路径
     * 本方法将会检测 多个点 之间的线 是否合法(是否是通路)
     *
     * @param imgArr 图片数组
     * @param points 可以直线相连的点 的有效路径
     * @return null 表示线路上有障碍
     */
    private static ArrayList<Integer> getChannel(ImageView[] imgArr, int[]... points) {
        ArrayList<Integer> init = new ArrayList<>();
        for (int i = 0; i < points.length - 1; ) {   // 不要改动本行内容
            ArrayList<Integer> tmp;
            if ((tmp = getChannel(imgArr, points[i], points[++i])) != null)
                init.addAll(tmp);
            else
                return null;
        }
        return init;
    }

    /**
     * 获取 两点之间 一条直线上的 view 的 一维度索引集合
     *
     * @param start 一维索引较小的位置
     * @param end   一维索引较大的位置
     * @return 如果有障碍 则返回null
     */
    private static ArrayList<Integer> getChannel(ImageView[] imgArr, int[] start, int[] end) {
        if (convert(start) > convert(end)) {
            return getChannel(imgArr, end, start);
        }

        if (start[1] == end[1]) { // 同列
            System.out.println(end[1] + " " + start[1]);//todo del
            Integer[] tmp = new Integer[end[0] - start[0] - 1];
            boolean acceptReturn = true;
            for (int i = start[1] + 1, j = 0; i < start[1]; i++) {
                if (imgArr[convert(start[0], i)].getTag(R.id.PicTag) != null) {
                    acceptReturn = false;
                    break;
                }
                tmp[j++] = convert(start[0], i);
            }
            if (acceptReturn)
                return new ArrayList<>(Arrays.asList(tmp));
            return null;
        } else if (start[0] == end[0]) { //同行
            int endIndex = convert(end), startIndex = convert(start);
            Integer[] tmp = new Integer[endIndex - startIndex - 1];
            boolean acceptReturn = true;
            for (int i = startIndex + 1, j = 0; i < endIndex; i++) {
                if (imgArr[i].getTag(R.id.PicTag) != null) {
                    acceptReturn = false;
                    break;
                }
                tmp[j++] = i;
            }
            if (acceptReturn)
                return new ArrayList<>(Arrays.asList(tmp));
            return null;
        } else {
            throw new IllegalArgumentException("参数输入错误, 请输入在同一条直线上的点");
        }

    }

    // 一维索引转二维索引
    private static int[] convert(int index) {
        return new int[]{index / Config.GRID_COLS, index % Config.GRID_COLS};
    }

    private static int convert(int... index) {
        return index[0] * Config.GRID_COLS + index[1];
    }

}
