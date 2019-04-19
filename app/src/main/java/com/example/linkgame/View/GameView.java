package com.example.linkgame.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.linkgame.game.GameService;
import com.example.linkgame.game.impl.GameServiceImpl;
import com.example.linkgame.utils.ImageUtil;
import com.example.linkgame.utils.LinkInfo;


/**
 *  自定义View
 * 连连看游戏中的游戏主界面
 */
public class GameView extends View {
    /**
     * 游戏逻辑的实现类
     */
    private GameServiceImpl gameService;
    /**
     * 保存当前已经被选中的方块
     */
    private Piece selectedPiece;
    /**
     * 连接信息对象
     */
    private LinkInfo linkInfo;
    /**
     * 画笔Paint
     */
    private Paint paint;
    /**
     * 选中标识的图片对象
     */
    private Bitmap selectImage;

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     */
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.paint = new Paint();
        // 设置连接线的颜色
        this.paint.setColor(Color.RED);
        // 设置连接线的粗细
        this.paint.setStrokeWidth(3);
        // 初始化被选中的图片
        this.selectImage = ImageUtil.getSelectImage(context);
    }

    /**
     * 设置连接信息对象
     *
     * @param linkInfo
     */
    public void setLinkInfo(LinkInfo linkInfo) {
        this.linkInfo = linkInfo;
    }

    /**
     * 设置当前选中方块
     *
     * @param piece
     */
    public void setSelectedPiece(Piece piece) {
        this.selectedPiece = piece;
    }

    /**
     * 设置游戏逻辑的实现类
     *
     * @param gameService
     */
    public void setGameService(GameServiceImpl gameService) {
        this.gameService = gameService;
    }

    /**
     * 绘制图形的方法
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.gameService == null) {
            return;
        }
        Piece[][] pieces = gameService.getPieceArr();
        if (pieces != null) {
            // 遍历pieces二维数组
            for (int i = 0; i < pieces.length; i++) {
                for (int j = 0; j < pieces[i].length; j++) {
                    // 如果二维数组中该元素不为空（即有方块），将这个方块的图片画出来
                    if (pieces[i][j] != null) {
                        // 得到这个Piece对象
                        Piece piece = pieces[i][j];
                        if (piece.getPieceImage() != null) {
                            // 根据方块左上角X、Y座标绘制方块
                            canvas.drawBitmap(piece.getPieceImage().getImage(), piece.getBeginX(), piece.getBeginY(), null);
                        }
                    }
                }
            }
        }
        // 如果当前对象中有linkInfo对象, 即连接信息
        if (this.linkInfo != null) {
            // 绘制连接线
            drawLine(this.linkInfo, canvas);
            // 处理完后清空linkInfo对象
            this.linkInfo = null;
        }
        // 画选中标识的图片
        if (this.selectedPiece != null) {
            System.out.println(this.selectedPiece.getPieceImage().getImageId());    //todo 打印选中图片id
            canvas.drawBitmap(this.selectImage, this.selectedPiece.getBeginX(),
                    this.selectedPiece.getBeginY(), null);
        }
    }

    /**
     * 根据LinkInfo绘制连接线的方法。
     *
     * @param linkInfo 连接信息对象
     * @param canvas   画布
     */
    private void drawLine(LinkInfo linkInfo, Canvas canvas) {
        // todo 暂时不划线
//        // 获取LinkInfo中封装的所有连接点
//        List<Point> points = linkInfo.getLinkPoints(gameService, getWidth(), getHeight());
//
////         依次遍历linkInfo中的每个连接点
//        for (int i = 0; i < points.size() - 1; i++) {
//            // 获取当前连接点与下一个连接点
//            Point currentPoint = points.get(i);
//            Point nextPoint = points.get(i + 1);
//            // 绘制连线
//            canvas.drawLine(currentPoint.x, currentPoint.y, nextPoint.x,
//                    nextPoint.y, this.paint);
//        }
    }

    // 开始游戏方法
    public void startGame() {
        this.gameService.start();
        this.postInvalidate();
    }

}
