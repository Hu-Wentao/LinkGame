package com.example.linkgame.utils;

import android.util.Log;

import com.example.linkgame.BuildConfig;
import com.example.linkgame.game.Config;
import com.example.linkgame.game.ViewOp;

import java.util.LinkedList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


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
    public static LinkedList<Integer> getConnectLink(int startIndex, int endIndex) {
        // 确保 startIndex < endIndex (即 start 在 end 的 上 方)
        if (startIndex > endIndex)
            return getConnectLink(endIndex, startIndex);
        // 转换为二维索引 [0] 是行, [1] 是列
        int[] s2Index = convert(startIndex), e2Index = convert(endIndex);
//        // 1. 排除两个方块 左右, 上下 相邻的情况( 表达式表示: 如果在 同一行, 且相邻的两个索引)
//        if (s2Index[0] == e2Index[0] && (s2Index[1] + e2Index[1]) % 2 == 1 ||
//                s2Index[1] == e2Index[1] && (s2Index[0] + e2Index[0] % 2 == 1)) {
//            return new LinkedList<>(Arrays.asList(startIndex, endIndex));
//        }
        // 创建一个临时变量, 临时保存路径点
        LinkedList<Integer> list;

        if (BuildConfig.DEBUG) Log.d(TAG, "尝试一线连接...");
        // 2. 0折连接(一线连)
        if (s2Index[0] == e2Index[0] || s2Index[1] == e2Index[1]) { // 同行或同列
            if ((list = getDirectChannel(s2Index, e2Index)) != null)
                return list;
        }

        if (BuildConfig.DEBUG) Log.d(TAG, "尝试两线连接...");
        // 3. 1折连接(两线连), 寻找与 s2Index 平行 的折点 再向下到终点 || 向下找 与sArr同列 的折点  再向右
        if ((list = getTowLineLink(s2Index, e2Index)) != null) {    // 两线连
            return list;
        }
        if (BuildConfig.DEBUG) Log.d(TAG, "尝试三线连接...");
        // 4. 2折连接(三线连)
        // 获取 下, 上 左, 右 方向上的通道, 然后做
        if ((list = getThreeLineLink(s2Index, e2Index)) != null) {
            return list;
        }

        ViewOp.testShowTag();   //todo
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
    private static LinkedList<Integer> getThreeLineLink(int[] s2Index, int[] e2Index) {
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
            do {
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
                // 检测 check 是否越界
                int ch = convert(check);
                if (ch < 0 || ch > Config.GRID_ROWS * Config.GRID_COLS) {
                    break;
                }
                // 检测 check 是否有障碍
                if (ViewOp.hasPicTag(ch)) { // 有tag 表示有障碍
                    break;
                }
                if (BuildConfig.DEBUG) Log.d(TAG, "进行3线连接内的2线连接, check为: " + ch);
                LinkedList<Integer> path3 ;
                if ((path3 = getTowLineLink(check, e2Index)) != null) {     // 三线连接内的 两线连
                    path3.addFirst(convert(s2Index));
                    if (BuildConfig.DEBUG)
                        Log.d(TAG, "3线连接完成, 返回List为:" + Arrays.toString(path3.toArray()));    //todo
                    return path3;
                }

            } while (!ViewOp.hasPicTag(convert(check)));  //  探测点判定为空, 则再次向该方向探测
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
    private static LinkedList<Integer> getTowLineLink(int[] s2Index, int[] e2Index) {
        LinkedList<Integer> list;

        if (!ViewOp.hasPicTag(convert(s2Index[0], e2Index[1]))) {  // 如果 上侧 的 折点 是空白的       // checked
            if (BuildConfig.DEBUG)
                Log.d(TAG, "两线 连接: 上边的折点是空白的, 折点index: " + convert(s2Index[0], e2Index[1]));
            if ((list = getMorePointChannel(s2Index, new int[]{s2Index[0], e2Index[1]}, e2Index)) != null)
                return list;
        } else if (!ViewOp.hasPicTag(convert(e2Index[0], s2Index[1]))) {// 如果 下边 的折点是空白的    // checked
            if (BuildConfig.DEBUG)
                Log.d(TAG, "两线 连接: 下边的折点是空白的, 折点index: " + convert(s2Index[0], e2Index[1]));
            if ((list = getMorePointChannel(s2Index, new int[]{e2Index[0], s2Index[1]}, e2Index)) != null)
                return list;
        }
        return null;
    }

    /**
     * 获取 多个 可以直线相连的点 的有效路径
     * 本方法将会检测 多个点 之间的线 是否合法(是否是通路)
     *
     * @param points 可以直线相连的点 的有效路径
     * @return null 表示线路上有障碍
     */
    private static LinkedList<Integer> getMorePointChannel(int[]... points) {
        LinkedList<Integer> init = new LinkedList<>();
        for (int i = 0; i < points.length - 1; ) {   // 不要改动本行内容
            LinkedList<Integer> tmp;
            if ((tmp = getDirectChannel(points[i], points[++i])) != null) {
                if (BuildConfig.DEBUG) Log.d(TAG, "直连得到的tmp为:" + Arrays.toString(tmp.toArray()));
                init.addAll(tmp);
            } else {
                return null;
            }
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
    private static LinkedList<Integer> getDirectChannel(int[] start, int[] end) {    // todo 发现bug 如果两点相邻, 则无法正确判断
        if (convert(start) > convert(end)) {
            return getDirectChannel(end, start);
        }
        int xOrY;
        if (start[1] == end[1]) {        // 如果两点在同一列上
            xOrY = 0;
        } else if (start[0] == end[0]) {// 如果两点在同一行上
            xOrY = 1;
        } else {                        // 这两个点不在同一条直线上
            throw new IllegalArgumentException("参数输入错误, 请输入在同一条直线上的点");
        }
        //-----------------------------------------------------------------------------------------
        if (BuildConfig.DEBUG) Log.d(TAG, " test : 执行两点直连 方法..");
        // todo 存在问题: 如果两点相邻, 则返回出错
        // 从start处 向下探测
        int[] check = {start[0], start[1]};
        int ch = convert(check);

        LinkedList<Integer> path = new LinkedList<>();  // 将起始点加入 path
        path.add(ch);
        if (BuildConfig.DEBUG) Log.d(TAG, "当前path里有:" + Arrays.toString(path.toArray()));   //todo
        do {
            check[xOrY]++; // 探测点 向下 \ 向右 移动 1 格
            ch = convert(check);
            // 如果 探测点越界, 或者 探测点 有障碍
            if (ch < 0 || ch > Config.GRID_ROWS * Config.GRID_COLS ) {
                if (BuildConfig.DEBUG) Log.d(TAG, "探测点越界");
                return null;    // 探测点越界!
            }
            if(ViewOp.hasPicTag(convert(check)) && check[xOrY] != end[xOrY]){
                return null;    // 探测点遇到障碍, 且障碍点 不是终点
            }
            path.add(ch);   // 该探测点有效, 将该点加入 路径 path
        } while (check[xOrY] < end[xOrY]);    // 如果 探测点 在 结束点 上\左 方, 则继续探测

        return path;    // 返回由 探测点构成的 路径list
    }

    // 一维索引转二维索引
    private static int[] convert(int index) {
        return new int[]{index / Config.GRID_COLS, index % Config.GRID_COLS};
    }

    // 二维索引转一维索引
    private static int convert(int... index) {
        return index[0] * Config.GRID_COLS + index[1];  // 行号* (列数) + 列号
    }

}
