package com.example.linkgame.utils;

import android.graphics.Point;

import com.example.linkgame.View.Piece;
import com.example.linkgame.game.GameService;

import java.util.ArrayList;
import java.util.List;

/**
 * 连接信息类
 */
public class LinkInfo {
    /**
     * 创建一个集合用于保存连接点
     */
//    private List<Point> points = new ArrayList<Point>();
    private List<int[]> points = new ArrayList<>();

    /**
     * 通配构造器
     *
     * @param pIndex 第一个与最后一个是p1 与 p2, 中间的都是是转折点
     */
    public LinkInfo(int[]... pIndex) {
        for (int i = 0; i < pIndex.length; i++) {
            points.add(pIndex[i]);
        }
    }

    /**
     * 提供第一个构造器, 表示两个Point可以直接相连, 没有转折点
     *
     * @param p1 p1在Piece[][]中的的索引
     * @param p2 p2在Piece[][]中的的索引
     */
    public LinkInfo(int[] p1, int[] p2) {
        // 加到集合中去
        points.add(p1);
        points.add(p2);
    }


    /**
     * 提供第二个构造器, 表示三个Point可以相连, p2是p1与p3之间的转折点
     *
     * @param p1
     * @param p2
     * @param p3
     */
    public LinkInfo(int[] p1, int[] p2, int[] p3) {
        points.add(p1);
        points.add(p2);
        points.add(p3);
    }

    /**
     * 提供第三个构造器, 表示四个Point可以相连, p2, p3是p1与p4的转折点
     *
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     */
    public LinkInfo(int[] p1, int[] p2, int[] p3, int[] p4) {
        points.add(p1);
        points.add(p2);
        points.add(p3);
        points.add(p4);
    }

    /**
     * @return 连接集合
     */
    public List<int[]> getLinkPointsIndex() {
        return points;
    }

    /**
     * 返回连接集合, 但必须将 int[] 类型的索引值 转换为 Point 类型的点
     *
     * @param gameService
     * @return
     */
    public List<Point> getLinkPoints(GameService gameService, float parentWidth, float parentHeight) {
        List<Point> list = new ArrayList<>();
        Piece[][] pieceArr = gameService.getPieceArr();
        for (int i = 0; i < points.size(); i++) {
            // t是一个PieceArr 的索引集合 t[0]为x的值, t[1] 为y的值
            int[] t = points.get(i);
            // 防止 数组下标越界
            if(t[0]>pieceArr.length){
                t[0]--;
            }
            if(t[1] > pieceArr[0].length){
                t[1]--;
            }

            // piece是一个正在屏幕上显示的某一个图
            Piece piece = pieceArr[t[0]][t[1]];

//            System.out.println("当前的点索引为: " + Arrays.toString(t) + "该pieceArr值为: " + pieceArr[t[0]][t[1]] + "pieceArr长度: " + pieceArr.length); //todo test

            // p 是一个点, 在绘制连接线的时候, 会用到
            Point p;
            // 如果这个坐标没有图片, 就计算一下标准点的位置
            if (piece == null) {
                // x: (索引+0.5)* (parentWidth/GameConfig.PIECE_X_SUM)
                p = new Point(
                        ((int) ((t[0] + 0.5) * (parentWidth / SizeUtils.dp2Px(GameConf.PIECE_X_SUM)))),
                        (int) ((t[1] + 0.5) * (parentHeight / SizeUtils.dp2Px(GameConf.PIECE_Y_SUM)))
                );
            } else {
                // 将该图片的中心点坐标添加到 List<Point> 中去
                p = piece.getCenter();
            }

            System.out.println("第"+i+"个点的坐标是"+p.toString());   //TODO

            list.add(p);
        }
        return list;
    }
}
