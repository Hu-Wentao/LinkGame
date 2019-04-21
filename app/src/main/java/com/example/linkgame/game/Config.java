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
     * 图片资源的数量 // 资源图片为 从0开始到 519, 其中偶数为中文图, 奇数为英文图
     */
    public static final int ALL_IMG_NUM_PAIR = 259;

    /**
     * 一局游戏的时长
     */
    public static final int GAME_TIME = 120 * 1000;

    /**
     * 布局中View 的Margin
     */
    public static final int[] GRID_LAYOUT_VIEW_MARGIN = {16, 24, 16, 24};
}
