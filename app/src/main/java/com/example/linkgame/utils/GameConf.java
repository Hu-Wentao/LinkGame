package com.example.linkgame.utils;

import android.content.Context;

/**
 * 保存游戏配置的对象
 */
public class GameConf {
    /**
     * 图片资源的数量 // 资源图片为 从0开始到 519, 其中偶数为中文图, 奇数为英文图
     */
    public static final int ALL_IMG_NUM = 259;
    /**
     * X轴有几个方块
     */
    public static final int PIECE_X_SUM = 5;
    /**
     * Y轴有几个方块
     */
    public static final int PIECE_Y_SUM = 8;
    /**
     * 从哪里开始画第一张图片出现的x座标
     */
    public static final int BEGIN_IMAGE_X = 25;
    /**
     * 从哪里开始画第一张图片出现的y座标
     */
    public static final int BEGIN_IMAGE_Y = 50;

    /**
     * 连连看的每个方块的图片的宽   启动的时候赋值
     */
    public static int PIECE_WIDTH;
    /**
     * 连连看的每个方块的图片的高   启动的时候赋值
     */
    public static int PIECE_HEIGHT;
    /**
     * 默认一局游戏的时长（100秒）.
     */
    public static int DEFAULT_TIME = 100;
    /**
     * Piece[][]数组第一维的长度
     */
    private int xSize;
    /**
     * Piece[][]数组第二维的长度
     */
    private int ySize;
    /**
     * Board中第一张图片出现的x座标
     */
    private int beginImageX;
    /**
     * Board中第一张图片出现的y座标
     */
    private int beginImageY;
    /**
     * 记录游戏的总时间, 单位是秒
     */
    private long gameTime;
    /**
     * 应用上下文
     */
    private Context context;

    /**
     * 提供一个参数构造器
     *
     * @param xSize       Piece[][]数组第一维长度
     * @param ySize       Piece[][]数组第二维长度
     * @param beginImageX Board中第一张图片出现的x座标
     * @param beginImageY Board中第一张图片出现的y座标
     * @param gameTime    设置每局的时间, 单位是豪秒
     */
    public GameConf(int xSize, int ySize, int beginImageX, int beginImageY,
                    long gameTime) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.beginImageX = beginImageX;
        this.beginImageY = beginImageY;
        this.gameTime = gameTime;
        this.context = context;
    }

    /**
     * @return 游戏的总时间
     */
    public long getGameTime() {
        return gameTime;
    }

    /**
     * @return Piece[][]数组第一维的长度
     */
    public int getXSize() {
        return xSize;
    }

    /**
     * @return Piece[][]数组第二维的长度
     */
    public int getYSize() {
        return ySize;
    }

    /**
     * @return Board中第一张图片出现的x座标
     */
    public int getBeginImageX() {
        return beginImageX;
    }

    /**
     * @return Board中第一张图片出现的y座标
     */
    public int getBeginImageY() {
        return beginImageY;
    }

}
