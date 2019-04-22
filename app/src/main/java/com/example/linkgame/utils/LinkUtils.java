package com.example.linkgame.utils;

import android.util.Log;
import android.widget.Toast;

import com.example.linkgame.BuildConfig;
import com.example.linkgame.game.Config;
import com.example.linkgame.game.ViewOp;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

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
     * @param startIndex 被点击的view的index
     * @param endIndex   已设为 被选中 状态的index
     * @return 连接路径, 如果为null, 则表示这两个view 无法连接
     */
    public static ArrayList<Integer> getConnectLink( int startIndex, int endIndex) {
        // 确保 startIndex < endIndex (即 start 在 end 的 上 方)
        if (startIndex > endIndex)
            return getConnectLink(endIndex, startIndex);
        // 转换为二维索引 [0] 是行, [1] 是列
        int[] s2Index = convert(startIndex), e2Index = convert(endIndex);
        // 1. 排除两个方块 左右, 上下 相邻的情况( 表达式表示: 如果在 同一行, 且相邻的两个索引)
        if (s2Index[0] == e2Index[0] && (s2Index[1] + e2Index[1]) % 2 == 1 ||
                s2Index[1] == e2Index[1] && (s2Index[0] + e2Index[0] % 2 == 1)) {
            return new ArrayList<>(Arrays.asList(startIndex, endIndex));
        }
        // 创建一个临时变量, 临时保存路径点
        ArrayList<Integer> list;

        System.out.println("尝试一线连接...");    //todo
        // 2. 0折连接(一线连)
        if (s2Index[0] == e2Index[0] || s2Index[1] == e2Index[1]) { // 同行或同列
            if ((list = getDirectChannel( s2Index, e2Index)) != null)
                return list;
        }

        System.out.println("尝试两线连接...");    //todo
        // 3. 1折连接(两线连), 寻找与 s2Index 平行 的折点 再向下到终点 || 向下找 与sArr同列 的折点  再向右
        if ((list = getTowLineLink(s2Index, e2Index)) != null) {
            return list;
        }
        // 4. 2折连接(三线连)
        // 获取 下, 上 左, 右 方向上的通道, 然后做
        if ((list = getThreeLineLink(s2Index, e2Index)) != null) {
            return list;
        }
        if (BuildConfig.DEBUG) Log.d(TAG, "getConnectLink: 尝试了所有方法后, 返回了null, 无法找到有效路径");
        return null;
    }

    /**
     * 封装 2折连接,
     * 剪枝:
     * 判断起始和结束点 是否在同行 同列, 在同行,则只求左右方向的位置, 在同列同理
     * <p>
     * 常规:
     * 先求出 s2Index 在 下,上,左,右 方向上的所有可能, 再进行 1折连接
     *
     * @param s2Index 起始点
     * @param e2Index 结束点
     * @return ...
     */
    private static ArrayList<Integer> getThreeLineLink(int[] s2Index, int[] e2Index) {
        // 0, 1, 2, 3 分别代表 下, 上, 左, 右
        int[] dirs;
        // 1 判断 起始,结束 是否在 同行, 同列
        if (s2Index[0] == e2Index[0]) {         //同行
            dirs = new int[]{0, 1};
        } else if (s2Index[1] == e2Index[1]) {  // 同列
            dirs = new int[]{2, 3};
        } else {                                // 不在同行,同列
            dirs = new int[]{0, 1, 2, 3};
        }

        // 从 起始点 开始,向 dirs 各方向探测
        for (int dir : dirs) {
            int[] check = new int[]{s2Index[0], s2Index[1]};
            // 更新探测点的坐标
            switch (dir) {
                case 0: // 下
                    check[0]++;
                    break;
                case 1: // 上
                    check[0]--;
                    break;
                case 2: // 左
                    check[1]--;
                    break;
                case 3: // 右
                    check[1]++;
                    break;
            }
            do {
                // 检测 check 是否越界
                int ch = convert(check);
                if (ch < 0 || ch > Config.GRID_ROWS * Config.GRID_COLS) {
                    break;
                }
                // 检测 check 是否有障碍
                if(ViewOp.hasPicTag(ch)){ // 有tag 表示有障碍
//                if(imgArr[ch].getTag(R.id.PicTag) != null){ // 有tag 表示有障碍
                    break;
                }
                if (BuildConfig.DEBUG) Log.d(TAG, "进行3折连接内的2折连接, check为: "+ch);
                ArrayList<Integer> tmp;
                if((tmp = getTowLineLink(check, e2Index)) != null){
                    return tmp;
                }

            } while (ViewOp.hasPicTag(convert(check)));  //  探测点判定为空, 则再次向该方向探测
//            } while (imgArr[convert(check)].getTag(R.id.PicTag) == null);  //  探测点判定为空, 则再次向该方向探测
        }
        return null;
    }


    /**
     * 封装 1折点 连接, 要求输入的 起始点 与 结束点 不在一条直线上
     *
     * @param s2Index 起始点 索引
     * @param e2Index 结束点 索引
     * @return ...
     */
    private static ArrayList<Integer> getTowLineLink(int[] s2Index, int[] e2Index) {
        ArrayList<Integer> list;

        if (ViewOp.hasPicTag(convert(s2Index[0], e2Index[1]))) {  // 如果 上侧 的 折点 是空白的
            if (BuildConfig.DEBUG) Log.d(TAG, "两线 连接: 上边的折点是空白的");
            if ((list = getMorePointChannel(s2Index, new int[]{s2Index[0], e2Index[1]}, e2Index)) != null)
                return list;
        } else if (ViewOp.hasPicTag(convert(e2Index[0], s2Index[1]))) {// 如果 下边 的折点是空白的
            if (BuildConfig.DEBUG) Log.d(TAG, "两线 连接: 下边的折点是空白的");
            if (BuildConfig.DEBUG) ViewOp.testShowTag();
            if ((list = getMorePointChannel( s2Index, new int[]{e2Index[0], s2Index[1]}, e2Index)) != null)
                return list;
        }
        return null;
    }

    /**
     * 获取通道 ( 改良版), 便于于批量迭代
     * 可以考虑修改之后 替代 private static ArrayList<Integer> getChannel(ImageView[] imgArr, int[]... points)
     *
     * @param pStart 起始点
     * @param pEnd   结束点
     * @param points 起始 与 结束点之间的点
     * @return null 表示 pStart 与 p End 无法通过 points 相连
     */
    private static ArrayList<Integer> getMorePointChannelPlus(int[] pStart, int[] pEnd, int[]... points) {
        // first 表示 start 与 第一个中间点的连线, last表示 最后一个中间点 与 end 的连线
        ArrayList<Integer> first, last;
        if ((first = getDirectChannel(pStart, points[0])) == null) {
            return null;
        }
        if ((last = getDirectChannel(points[points.length - 1], pEnd)) == null) {
            return null;
        }

        ArrayList<Integer> tmp;
        if ((tmp = getMorePointChannel(points)) == null) {
            return null;
        }

        first.addAll(tmp);
        first.addAll(last);
        return first;
    }

    /**
     * 获取 多个 可以直线相连的点 的有效路径
     * 本方法将会检测 多个点 之间的线 是否合法(是否是通路)
     *
     * @param points 可以直线相连的点 的有效路径
     * @return null 表示线路上有障碍
     */
    private static ArrayList<Integer> getMorePointChannel(int[]... points) {
        ArrayList<Integer> init = new ArrayList<>();
        for (int i = 0; i < points.length - 1; ) {   // 不要改动本行内容
            ArrayList<Integer> tmp;
            if ((tmp = getDirectChannel(points[i], points[++i])) != null)
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
    private static ArrayList<Integer> getDirectChannel(int[] start, int[] end) {
        if (convert(start) > convert(end)) {
            return getDirectChannel(end, start);
        }

        if (start[1] == end[1]) { // 同列 // todo 有BUG
//            System.out.println(end[1] + " " + start[1]);//todo del
            Integer[] tmp = new Integer[end[0] - start[0] - 1];
            boolean acceptReturn = true;
            for (int i = start[1] + 1, j = 0; i < start[1]; i++) {
                if (ViewOp.hasPicTag(convert(start[0], i))) {
//                if (imgArr[convert(start[0], i)].getTag(R.id.PicTag) != null) {
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
                if (ViewOp.hasPicTag(i)) {
//                if (imgArr[i].getTag(R.id.PicTag) != null) {
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

    // 二维索引转一维索引
    private static int convert(int... index) {
        return index[0] * Config.GRID_COLS + index[1];
    }

}
