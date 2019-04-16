package com.example.linkgame.panel.impl;

import com.example.linkgame.View.Piece;
import com.example.linkgame.panel.AbstractPanel;
import com.example.linkgame.utils.GameConf;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建矩阵分布的游戏区域，矩阵排列的方块会填满二维数组的每个数组元素，只是把四周留空而已
 */
public class FullPanel extends AbstractPanel {
    @Override
    protected List<Piece> createPieces(GameConf config, Piece[][] pieces) {
        // 创建一个Piece集合, 该集合里面存放初始化游戏时所需的Piece对象
        List<Piece> notNullPieces = new ArrayList<Piece>();
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[i].length; j++) {
                // 先构造一个Piece对象, 只设置它在Piece[][]数组中的索引值，
                // 所需要的PieceImage由其父类负责设置。
                Piece piece = new Piece(i, j);
                // 添加到Piece集合中
                notNullPieces.add(piece);
            }
        }
        return notNullPieces;
    }
}
