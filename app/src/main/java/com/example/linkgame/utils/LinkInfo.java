package com.example.linkgame.utils;

import android.graphics.Point;

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
     * @param pIndex 第一个与最后一个是p1 与 p2, 中间的都是是转折点
     */
    public LinkInfo(int[]... pIndex){
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
    public List<int[]> getLinkPoints() {

        return points;
    }
}
