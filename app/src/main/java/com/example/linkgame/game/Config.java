package com.example.linkgame.game;

/**
 * @Author: hu.wentao@outlook.com
 * @Date: 2019/4/20
 */
public class Config {

    /**
     * 网格布局的列数 与 行数
     */
    public static final int
            GRID_ROWS = 9,  // 行数
            GRID_COLS = 6;  // 列数

    /**
     * 图片资源的数量 奇数, 偶数 序号的图片为一对 该参数表示的是 图片的对数(总数/2)
     *  总数应当小于 GRID_ROWS * GRID_COLS
     */
    public static final int ALL_IMG_NUM_PAIR = 31;

    /**
     * 一局游戏的时长
     */
    public static final int NORMAL_GAME_TIME = 120 * 1000;
    public static final int HARD_GAME_TIME = 80 * 1000;

    /**
     * 布局中View 的Margin
     */
    public static final int[] GRID_LAYOUT_VIEW_MARGIN = {16, 24, 16, 24};
}
