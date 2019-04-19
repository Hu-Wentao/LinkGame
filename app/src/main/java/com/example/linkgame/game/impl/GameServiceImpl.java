package com.example.linkgame.game.impl;

import android.graphics.Point;

import com.example.linkgame.View.Piece;
import com.example.linkgame.game.AbstractPanel;
import com.example.linkgame.game.GameService;
import com.example.linkgame.utils.GameConf;
import com.example.linkgame.utils.LinkInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 游戏逻辑的实现类
 */
public class GameServiceImpl {
    /**
     * 定义一个Piece[][]数组, 存放的是当前所显示的 图片
     * 使用 getPiecesArr() 来获取
     */
    private Piece[][] piecesArr;
    /**
     * 游戏配置对象
     */
    private GameConf config;

    /**
     * 构造方法
     *
     * @param config 游戏配置对象
     */
    public GameServiceImpl(GameConf config) {
        // 将游戏的配置对象设置本类中
        this.config = config;
    }

    public void start() {
        // 定义一个AbstractPanel对象
        AbstractPanel board;

        // todo 暂时只用水平面板
//        Random random = new Random();
//        // 获取一个随机数, 可取值0、1、2、3四值。
//        int index = random.nextInt(4);
//        // 随机生成AbstractPanel的子类实例

        int index = 1;  // 只使用水平面板
        switch (index) {
            case 0:
                // 0返回VerticalBoard(竖向)
                board = new VerticalPanel();
                break;
            case 1:
                // 1返回HorizontalBoard(横向)
                board = new HorizontalPanel();
                break;
            default:
                // 默认返回FullBoard
                board = new FullPanel();
                break;
        }
        // 初始化Piece[][]数组
        this.piecesArr = board.create(config);
    }

    public Piece[][] getPieceArr() {
        return this.piecesArr;
    }

    public boolean hasPieces() {
        // 遍历Piece[][]数组的每个元素
        for (int i = 0; i < piecesArr.length; i++) {
            for (int j = 0; j < piecesArr[i].length; j++) {
                // 只要任意一个数组元素不为null，也就是还剩有非空的Piece对象
                if (piecesArr[i][j] != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据触碰点的位置查找相应的方块
     *
     * @param touchX 鼠标点击的x座标
     * @param touchY 鼠标点击的y座标
     * @return
     */
    public Piece findPiece(float touchX, float touchY) {
        int[] arr = findPieceIndex(touchX, touchY);
        if (arr == null) {
            return null;
        }

        // 返回Piece[][]数组的指定元素
        System.out.println("指定的元素为数组Piece[][]中的 piece" + Arrays.toString(arr));     //todo
        return this.piecesArr[arr[0]][arr[1]];
    }

    /**
     * 根据传入的触点坐标返回对应的在数组中的元素
     *
     * @param touchX
     * @param touchY
     * @return
     */
    private int[] findPieceIndex(float touchX, float touchY) {
        /*
         * 由于在创建Piece对象的时候, 将每个Piece的开始座标加了
         * GameConf中设置的beginImageX、beginImageY值, 因此这里要减去这个值
         */
        int relativeX = (int) touchX - this.config.getBeginImageX();
        int relativeY = (int) touchY - this.config.getBeginImageY();
        /*
         * 如果鼠标点击的地方比board中第一张图片的开始x座标和开始y座标要小, 即没有找到相应的方块
         */
        if (relativeX < 0 || relativeY < 0) {
            return null;
        }
        /*
         * 获取relativeX座标在Piece[][]数组中的第一维的索引值 ，第二个参数为每张图片的宽
         */
        int indexX = getIndex(relativeX, GameConf.PIECE_WIDTH);
        /*
         * 获取relativeY座标在Piece[][]数组中的第二维的索引值 ，第二个参数为每张图片的高
         */
        int indexY = getIndex(relativeY, GameConf.PIECE_HEIGHT);
        // 这两个索引比数组的最小索引还小, 返回null
        if (indexX < 0 || indexY < 0) {
            return null;
        }
        // 这两个索引比数组的最大索引还大(或者等于), 返回null
        if (indexX >= this.config.getXSize()
                || indexY >= this.config.getYSize()) {
            return null;
        }
        return new int[]{indexX, indexY};
    }

    private int[] findPieceIndex(Point p) {
        return findPieceIndex(p.x, p.y);
    }



    /**
     * 工具方法：计算相对于Piece[][]数组的第一维 或第二维的索引值
     *
     * @param relative 座标
     * @param size     每张图片边的长或者宽
     * @return
     */
    private int getIndex(int relative, int size) {
        // 表示座标relative不在该数组中，数组下标从0开始
        int index = -1;
        /*
         * 让座标除以边长, 没有余数, 索引减1， 例如点了x座标为20, 边宽为10, 20 % 10 没有余数, index为1,
         * 即在数组中的索引为1(第二个元素)
         */
        if (relative % size == 0) {
            index = relative / size - 1;
        } else {
            /*
             * 有余数, 例如点了x座标为21, 边宽为10, 21 % 10有余数, index为2， 即在数组中的索引为2(第三个元素)
             */
            index = relative / size;
        }
        return index;
    }

    /**
     * 在Piece[][]数组内寻找适合的方块
     *
     * @param p1 第一个Piece对象
     * @param p2 第二个Piece对象
     * @return
     */
    public LinkInfo link(Piece p1, Piece p2) {
        // 两个Piece是同一个, 即选中了同一个方块, 返回null
        if (p1.equals(p2)) {
            return null;
        }
        // 如果p1的图片与p2的图片不相同, 则返回null
        if (!p1.isSameImage(p2)) {
            System.out.println("这两个图片被 isSameImage() 方法判定为 false, 直接取消了!   ");  //todo
            return null;
        }
        // 如果p2在p1的左边, 则需要重新执行本方法, 两个参数互换
        if (p2.getIndexX() < p1.getIndexX()) {
            return link(p2, p1);
        }
        // 获取p1的中心点
        Point p1Point = p1.getCenter();
        // 获取p2的中心点
        Point p2Point = p2.getCenter();
        // 获取点对应的数组坐标
//        int[] indexA = findPieceIndex(p1Point);   // 已被index 取代
//        int[] indexB = findPieceIndex(p2Point);

        //  注: int[数组A/数组B][X值/Y值]
        int[][] index = new int[][]{findPieceIndex(p1Point), findPieceIndex(p2Point)};

        // todo 修改下面的情况

        // 情况1：如果两个Piece在同一行，并且可以直接相连
//        if (indexA[1] == indexB[1]) {
        if (index[0][1] == index[1][1]) {   // todo 这是行还是列, 还有待验证
            // 它们在同一行并可以相连
            if (!isXOrYBlock(true,index)) {
                // 它们之间没有真接障碍, 没有转折点
                return new LinkInfo(index);
            }
        }
        // 情况2：如果两个Piece在同一列，并且可以直接相连
        if (index[0][0] == index[1][0]) {
            if (!isXOrYBlock(false, index)) {
                // 它们之间没有真接障碍, 没有转折点
                return new LinkInfo(index);
            }
        }
        /*
         * 情况3：两个Piece以两条线段相连，也就是有一个转折点的情况。 获取两个点的直角相连的点, 即只有一个转折点
         */
        int[] cornerPoint = getCornerPointIndex(index);

        // 它们之间有一个转折点
        if (cornerPoint != null) {
            return new LinkInfo(index[0], cornerPoint, index[1]);
        }
        /*
         * 情况4：两个Piece以三条线段相连，有两个转折点的情况。 该map的key存放第一个转折点,
         * value存放第二个转折点,map的size()说明有多少种可以连的方式
         */
        Map<int[], int[]> turns = getLinkPoints(index);
        // 它们之间有转折点
        if (turns.size() != 0) {
            // 获取p1和p2之间最短的连接信息
            return getShortcut(index[0], index[1], turns, getDistance(index[0], index[1]));
        }
        return null;
    }

    /**
     * 获取两个转折点的情况
     *
     * @return Map对象的每个key-value对代表一种连接方式， 其中key、value分别代表第1个、第2个连接点
     */
    private Map<int[], int[]> getLinkPoints(int[]... index) {
        Map<int[], int[]> result = new HashMap<>();

        // 获取以index[0]为中心的向上的通道
        List<int[]> p1UpChanel = getUpChanel(index[0], index[1][1] );
        // 获取以index[0]为中心的向右的通道
        List<int[]> p1RightChanel = getRightChanel(index[0], index[1][0] );
        // 获取以index[0]为中心的向下的通道
        List<int[]> p1DownChanel = getDownChanel(index[0], index[1][1] );
        // 获取以index[1]为中心的向下的通道
        List<int[]> p2DownChanel = getDownChanel(index[1], index[0][1] );
        // 获取以index[1]为中心的向左的通道
        List<int[]> p2LeftChanel = getLeftChanel( index[1], index[0][0] );
        // 获取以 index[1]为中心的向上的通道
        List<int[]> p2UpChanel = getUpChanel( index[1], index[0][1] );

        // 获取Board的最大高度
        int heightMax = (this.config.getYSize() + 1) 
                + this.config.getBeginImageY();
        // 获取Board的最大宽度
        int widthMax = (this.config.getXSize() + 1) 
                + this.config.getBeginImageX();
        /*
         * 先确定两个点的关系，如果  index[1]在index[0]的左上角或者左下角
         */
        if (isLeftUp(index[0],  index[1]) || isLeftDown(index[0],  index[1])) {
            // 参数换位, 调用本方法
            return getLinkPoints( index[1], index[0]  );
        }
        // 情况1：如果p1、p2位于同一行而不能直接相连，需要两个转折点，可以在上面相连也可以在下面相连
        if (index[0][1] == index[1][0]) {// 在同一行
            // 第1步: 向上遍历
            // 以p1的中心点向上遍历获取点集合
            p1UpChanel = getUpChanel(index[0], 0 );
            // 以p2的中心点向上遍历获取点集合
            p2UpChanel = getUpChanel( index[1], 0 );
            // 如果两个集合向上中有Y坐标相同，即在同一行，且之间没有障碍物
            Map<int[], int[]> upLinkPoints = getXLinkPoints(p1UpChanel,
                    p2UpChanel );

            // 第2步: 向下遍历, 不超过Board(有方块的地方)的边框
            // 以p1中心点向下遍历获取点集合
            p1DownChanel = getDownChanel(index[0], heightMax );
            // 以p2中心点向下遍历获取点集合
            p2DownChanel = getDownChanel( index[1], heightMax );
            // 如果两个集合向上中有Y坐标相同，即在同一行，且之间没有障碍物
            Map<int[], int[]> downLinkPoints = getXLinkPoints(p1DownChanel,
                    p2DownChanel );
            result.putAll(upLinkPoints);
            result.putAll(downLinkPoints);
        }
        // 情况2：p1、p2位于同一列不能直接相连，需要两个转折点，可以在左边相连也可以在右边相连
        if (index[0][0] == index[1][1]) {// 在同一列
            // 第1步:向左遍历
            // 以p1的中心点向左遍历获取点集合
            List<int[]> p1LeftChanel = getLeftChanel(index[0], 0 );
            // 以p2的中心点向左遍历获取点集合
            p2LeftChanel = getLeftChanel( index[1], 0 );
            // 如果两个集合向上中有X坐标相同，即在同一列，且之间没有障碍物
            Map<int[], int[]> leftLinkPoints = getYLinkPoints(p1LeftChanel,
                    p2LeftChanel );

            // 第2步:向右遍历, 不得超过Board的边框（有方块的地方）
            // 以p1的中心点向右遍历获取点集合
            p1RightChanel = getRightChanel(index[0], widthMax );
            // 以p2的中心点向右遍历获取点集合
            List<int[]> p2RightChanel = getRightChanel( index[1], widthMax);
            // 如果两个集合向上中有X坐标相同，即在同一列，且之间没有障碍物
            Map<int[], int[]> rightLinkPoints = getYLinkPoints(p1RightChanel,
                    p2RightChanel );
            result.putAll(leftLinkPoints);
            result.putAll(rightLinkPoints);
        }
        // 情况3: index[1]位于index[0]的右上角,分六种情况讨论
        if (isRightUp(index[0],  index[1])) {
            //第1步： 获取index[0]向上遍历,  index[1]向下遍历时横向可以连接的点
            Map<int[], int[]> upDownLinkPoints = getXLinkPoints(p1UpChanel,
                    p2DownChanel );
            /**********************************************************/
            //第2步：获取index[0]向右遍历,  index[1]向左遍历时纵向可以连接的点
            Map<int[], int[]> rightLeftLinkPoints = getYLinkPoints(
                    p1RightChanel, p2LeftChanel );
            /**********************************************************/
            // 获取以p1为中心的向上通道
            p1UpChanel = getUpChanel(index[0], 0 );
            // 获取以p2为中心的向上通道
            p2UpChanel = getUpChanel( index[1], 0 );
            //第3步： 获取index[0]向上遍历,  index[1]向上遍历时横向可以连接的点
            Map<int[], int[]> upUpLinkPoints = getXLinkPoints(p1UpChanel,
                    p2UpChanel );
            /**********************************************************/
            // 获取以p1为中心的向下通道
            p1DownChanel = getDownChanel(index[0], heightMax );
            // 获取以p2为中心的向下通道
            p2DownChanel = getDownChanel( index[1], heightMax );
            //第4步： 获取index[0]向下遍历,  index[1]向下遍历时横向可以连接的点
            Map<int[], int[]> downDownLinkPoints = getXLinkPoints(p1DownChanel,
                    p2DownChanel );
            /**********************************************************/
            // 获取以p1为中心的向右通道
            p1RightChanel = getRightChanel(index[0], widthMax );
            // 获取以p2为中心的向右通道
            List<int[]> p2RightChanel = getRightChanel( index[1], widthMax);
            //第5步：获取index[0]向右遍历,  index[1]向右遍历时纵向可以连接的点
            Map<int[], int[]> rightRightLinkPoints = getYLinkPoints(
                    p1RightChanel, p2RightChanel );
            /**********************************************************/
            // 获取以p1为中心的向左通道
            List<int[]> p1LeftChanel = getLeftChanel(index[0], 0 );
            // 获取以p2为中心的向左通道
            p2LeftChanel = getLeftChanel( index[1], 0 );
            //第6步： 获取index[0]向左遍历,  index[1]向左遍历时纵向可以连接的点
            Map<int[], int[]> leftLeftLinkPoints = getYLinkPoints(p1LeftChanel,
                    p2LeftChanel );
            /**********************************************************/
            result.putAll(upDownLinkPoints);
            result.putAll(rightLeftLinkPoints);
            result.putAll(upUpLinkPoints);
            result.putAll(downDownLinkPoints);
            result.putAll(rightRightLinkPoints);
            result.putAll(leftLeftLinkPoints);
        }
        // 情况4： index[1]位于index[0]的右下角,分六种情况讨论
        if (isRightDown(index[0],  index[1])) {
            //第1步： 获取index[0]向下遍历,  index[1]向上遍历时横向可连接的点
            Map<int[], int[]> downUpLinkPoints = getXLinkPoints(p1DownChanel,
                    p2UpChanel );
            /**********************************************************/
            //第2步： 获取index[0]向右遍历,  index[1]向左遍历时纵向可连接的点
            Map<int[], int[]> rightLeftLinkPoints = getYLinkPoints(
                    p1RightChanel, p2LeftChanel );
            /**********************************************************/
            // 获取以p1为中心的向上通道
            p1UpChanel = getUpChanel(index[0], 0 );
            // 获取以p2为中心的向上通道
            p2UpChanel = getUpChanel( index[1], 0 );
            //第3步： 获取index[0]向上遍历,  index[1]向上遍历时横向可连接的点
            Map<int[], int[]> upUpLinkPoints = getXLinkPoints(p1UpChanel,
                    p2UpChanel );
            /**********************************************************/
            // 获取以p1为中心的向下通道
            p1DownChanel = getDownChanel(index[0], heightMax );
            // 获取以p2为中心的向下通道
            p2DownChanel = getDownChanel( index[1], heightMax );
            //第4步： 获取index[0]向下遍历,  index[1]向下遍历时横向可连接的点
            Map<int[], int[]> downDownLinkPoints = getXLinkPoints(p1DownChanel,
                    p2DownChanel );
            /**********************************************************/
            // 获取以p1为中心的向左通道
            List<int[]> p1LeftChanel = getLeftChanel(index[0], 0 );
            // 获取以p2为中心的向左通道
            p2LeftChanel = getLeftChanel( index[1], 0 );
            //第5步： 获取index[0]向左遍历,  index[1]向左遍历时纵向可连接的点
            Map<int[], int[]> leftLeftLinkPoints = getYLinkPoints(p1LeftChanel,
                    p2LeftChanel );
            /**********************************************************/
            // 获取以p1为中心的向右通道
            p1RightChanel = getRightChanel(index[0], widthMax );
            // 获取以p2为中心的向右通道
            List<int[]> p2RightChanel = getRightChanel( index[1], widthMax);
            //第6步： 获取index[0]向右遍历,  index[1]向右遍历时纵向可以连接的点
            Map<int[], int[]> rightRightLinkPoints = getYLinkPoints(
                    p1RightChanel, p2RightChanel );
            /**********************************************************/
            result.putAll(downUpLinkPoints);
            result.putAll(rightLeftLinkPoints);
            result.putAll(upUpLinkPoints);
            result.putAll(downDownLinkPoints);
            result.putAll(leftLeftLinkPoints);
            result.putAll(rightRightLinkPoints);
        }
        return result;
    }

    /**
     * 获取p1和p2之间最短的连接信息
     *
     * @param p1
     * @param p2
     * @param turns         放转折点的map
     * @param shortDistance 两点之间的最短距离
     * @return p1和p2之间最短的连接信息
     */
    private LinkInfo getShortcut(int[] p1, int[] p2, Map<int[], int[]> turns, int shortDistance) {
        List<LinkInfo> infos = new ArrayList<>();
        // 遍历结果Map,
        for (int[] indexa : turns.keySet()) {
            int[]  indexb = turns.get(indexa);
            // 将转折点与选择点封装成LinkInfo对象, 放到List集合中
            infos.add(new LinkInfo(p1, indexa,  indexb, p2));
        }
        return getShortcut(infos, shortDistance);
    }

    /**
     * 从infos中获取连接线最短的那个LinkInfo对象
     *
     * @param infos
     * @return 连接线最短的那个LinkInfo对象
     */
    private LinkInfo getShortcut(List<LinkInfo> infos, int shortDistance) {
        int temp1 = 0;
        LinkInfo result = null;
        for (int i = 0; i < infos.size(); i++) {
            LinkInfo info = infos.get(i);
            // 计算出几个点的总距离
            int distance = countAll(info.getLinkPointsIndex());
            // 将循环第一个的差距用temp1保存
            if (i == 0) {
                temp1 = distance - shortDistance;
                result = info;
            }
            // 如果下一次循环的值比temp1的还小, 则用当前的值作为temp1
            if (distance - shortDistance < temp1) {
                temp1 = distance - shortDistance;
                result = info;
            }
        }
        return result;
    }

    /**
     * 计算List<int[]>中所有点的距离总和
     *
     * @param points 需要计算的连接点
     * @return 所有点的距离的总和
     */
    private int countAll(List<int[]> points) {
        int result = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            // 获取第i个点
            int[] pointA = points.get(i);
            // 获取第i + 1个点
            int[]  pointB = points.get(i + 1);
            // 计算第i个点与第i + 1个点的距离，并添加到总距离中
            result += getDistance(pointA,  pointB);
        }
        return result;
    }

    /**
     * 获取两个LinkPoint之间的最短距离
     *
     * @param p1 第一个点
     * @param p2 第二个点
     * @return 两个点的距离距离总和
     */
    private int getDistance(int[] p1, int[] p2) {
        int xDistance = Math.abs(p1[0] - p2[0]);
        int yDistance = Math.abs(p1[1] - p2[1]);
        return xDistance + yDistance;
    }

    /**
     * 遍历两个集合, 先判断第一个集合的元素的x座标与另一个集合中的元素x座标相同(纵向), 如果相同, 即在同一列, 再判断是否有障碍,
     * 没有则加到结果的Map中去
     *
     * @param p1Chanel
     * @param p2Chanel
     * @return
     */
    private Map<int[], int[]> getYLinkPoints(List<int[]> p1Chanel, List<int[]> p2Chanel ) {
        Map<int[], int[]> result = new HashMap<>();
        for (int i = 0; i < p1Chanel.size(); i++) {
            int[] temp1 = p1Chanel.get(i);
            for (int j = 0; j < p2Chanel.size(); j++) {
                int[] temp2 = p2Chanel.get(j);
                // 如果x座标相同(在同一列)
                if (temp1[0] == temp2[0]) {
                    // 没有障碍, 放到map中去
                    if (!isXOrYBlock(false,temp1, temp2 )) {
                        result.put(temp1, temp2);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 遍历两个集合, 先判断第一个集合的元素的y座标与另一个集合中的元素y座标相同(横向), 如果相同, 即在同一行, 再判断是否有障碍, 没有
     * 则加到结果的map中去
     *
     * @param p1Chanel
     * @param p2Chanel
     * @return 存放可以横向直线连接的连接点的键值对
     */
    private Map<int[], int[]> getXLinkPoints(List<int[]> p1Chanel, List<int[]> p2Chanel) {
        Map<int[], int[]> result = new HashMap<>();
        for (int i = 0; i < p1Chanel.size(); i++) {
            // 从第一通道中取一个点
            int[] temp1 = p1Chanel.get(i);
            // 再遍历第二个通道, 看下第二通道中是否有点可以与temp1横向相连
            for (int j = 0; j < p2Chanel.size(); j++) {
                int[] temp2 = p2Chanel.get(j);
                // 如果y座标相同(在同一行), 再判断它们之间是否有直接障碍
                if (temp1[1] == temp2[1]) {
                    if (!isXOrYBlock(true, temp1, temp2)) {
                        // 没有障碍则直接加到结果的map中
                        result.put(temp1, temp2);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 判断 index[1]是否在index[0]的左上角
     *
     * @return p2位于p1的左上角时返回true，否则返回false
     */
    private boolean isLeftUp(int[]... index) {
        return (index[1][0] < index[0][0] && index[1][1] < index[0][1]);
    }

    /**
     * 判断 index[1]是否在index[0]的左下角
     *
     * @return p2位于p1的左下角时返回true，否则返回false
     */
    private boolean isLeftDown(int[]... index) {
        return (index[1][0] < index[0][0] && index[1][1] > index[0][1]);
    }

    /**
     * 判断 index[1]是否在index[0]的右上角
     *
     * @return p2位于p1的右上角时返回true，否则返回false
     */
    private boolean isRightUp(int[]... index) {
        return (index[1][1] > index[0][0] && index[1][1] < index[0][1]);
    }

    /**
     * 判断 index[1]是否在index[0]的右下角
     *
     * @return p2位于p1的右下角时返回true，否则返回false
     */
    private boolean isRightDown(int[]... index ) {
        return (index[1][1] > index[0][0] && index[1][1] > index[0][1]);
    }

    /**
     * 获取两个不在同一行或者同一列的座标点的直角连接点, 即只有一个转折点
     *  index[0] 为第一个点索引, index[1] 为第二点索引
     * @return 两个不在同一行或者同一列的座标点的直角连接点
     */
    private int[] getCornerPointIndex(int[]... index) {

        // 先判断这两个点的位置关系, 如果index[1]在index[0]的左上角或者 index[1]在index[0]的左下角
        if (isLeftUp(index) || isLeftDown(index)) {
            // 参数换位, 重新调用本方法
            return getCornerPointIndex(index[1], index[0]);
        }
        // 获取p1向右的通道
        List<int[]> point1RightChanel = getRightChanel(index[0], index[1][0]);
        // 获取p1向上的通道
        List<int[]> point1UpChanel = getUpChanel(index[0], index[1][1] );
        // 获取p1向下的通道
        List<int[]> point1DownChanel = getDownChanel(index[0], index[1][1]);
        // 获取p2向下的通道
        List<int[]> point2DownChanel = getDownChanel(index[1], index[0][1]  );
        // 获取p2向左的通道
        List<int[]> point2LeftChanel = getLeftChanel(index[1], index[0][0] );
        // 获取p2向上的通道
        List<int[]> point2UpChanel = getUpChanel(index[1], index[0][1] );
        // 如果index[1]在index[0]的右上角
        if (isRightUp(index[0], index[1])) {
            // 获取p1向右和p2向下的交点
            int[] linkPoint1 = getWrapPoint(point1RightChanel, point2DownChanel);
            // 获取p1向上和p2向左的交点
            int[] linkPoint2 = getWrapPoint(point1UpChanel, point2LeftChanel);
            // 返回其中一个交点, 如果没有交点, 则返回null
            return (linkPoint1 == null) ? linkPoint2 : linkPoint1;
        }
        /**********************************************************/
        // 如果point2在index[0]的右下角
        if (isRightDown(index[0], index[1])) {
            // point2在index[0]的右下角
            // 获取p1向下和p2向左的交点
            int[] linkPoint1 = getWrapPoint(point1DownChanel, point2LeftChanel);
            // 获取p1向右和p2向下的交点
            int[] linkPoint2 = getWrapPoint(point1RightChanel, point2UpChanel);
            return (linkPoint1 == null) ? linkPoint2 : linkPoint1;
        }
        return null;
    }

    /**
     * 遍历两个通道, 获取它们的交点
     *
     * @param p1Chanel 第一个点的通道
     * @param p2Chanel 第二个点的通道
     * @return 两个通道有交点，返回交点，否则返回null
     */
    private int[] getWrapPoint(List<int[]> p1Chanel, List<int[]> p2Chanel) {
        for (int i = 0; i < p1Chanel.size(); i++) {
            int[] temp1 = p1Chanel.get(i);
            for (int j = 0; j < p2Chanel.size(); j++) {
                int[] temp2 = p2Chanel.get(j);
                if (temp1.equals(temp2)) {
                    // 如果两个List中有元素有同一个, 表明这两个通道有交点
                    return temp1;
                }
            }
        }
        return null;
    }

    /**
     * 判断两个x / y 索引相同的 对象之间是否有障碍, 以p1为中心向 右/ 下 遍历
     *
     * @param isX   是x相同还是y相同
     * @param index 索引[0] 表示indexA, [1] 表示indexB
     * @return 两个Piece之间有障碍返回true，否则返回false
     */
    private boolean isXOrYBlock(boolean isX, int[]... index) {
        int xOrY = isX?0:1;

        if (index[0][xOrY] < index[1][xOrY]) {
            // 如果p2在p1 左/上 边, 调换参数位置调用本方法
            return isXOrYBlock(isX, index[1], index[0]);
        }
        for (int i = index[0][xOrY] + 1; i < index[1][xOrY]; i++) {
            // index[0][1] 表示 [点A 或者点B] 的 [Y或X] 的值
            if (piecesArr[i][index[0][xOrY]] != null) { // 有障碍 // todo 需要保证已经被消除的图片, 在Piece[][] 中会赋值为null
                return true;
            }
        }
        return false;
    }

    /**
     * 判断两个y索引相同的 对象之间是否有障碍, 以p1为中心向右遍历
     *
     * @param indexA p1在Piece[][] 中的索引
     * @param indexB p2在Piece[][] 中的索引
     * @return 两个Piece之间有障碍返回true，否则返回false
     */
//    private boolean isXBlock(int[] indexA, int[] indexB) {
//        if (indexB[0] < indexA[0]) {
//            // 如果p2在p1左边, 调换参数位置调用本方法
//            return isXBlock(indexB, indexA);
//        }
//        for (int i = indexA[0] + 1; i < indexB[0]; i++) {
//            if (piecesArr[i][indexB[1]] != null) {// 有障碍
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * 判断GamePanel中的x, y座标中是否有Piece对象
     *
     * @param x
     * @param y
     * @return true 表示有该座标有piece对象 false 表示没有
     */
    private boolean hasPiece(int x, int y) {
//        if (findPiece(x, y) == null)
//            return false;
        // 方式 OOB 发生
        if(x>= piecesArr.length){
            System.out.println("x越界!当前x边界为" + piecesArr.length);   //todo
            x = piecesArr.length-1;
        }
        if(y>= piecesArr[x].length){
            System.out.println("y越界!当前x边界为" + piecesArr[x].length);   //todo
            y = piecesArr[x].length-1;
        }

        return piecesArr[x][y] == null;  // 此处发生数组越界
//        return piecesArr[y][x] == null;
    }

    /**
     * 给一个Point对象,返回它的左边通道
     *
     * @param p
     * @param min        向左遍历时最小的界限
     * @return 给定Point左边的通道
     */
    private List<int[]> getLeftChanel(int[] p, int min) {
        List<int[]> result = new ArrayList<>();
        // 获取向左通道, 由一个点向左遍历, 步长为Piece图片的宽
        for (int i = p[0] - 1; i >= min; i = i - 1) {
            // 遇到障碍, 表示通道已经到尽头, 直接返回
            if (hasPiece(i, p[1])) {
                return result;
            }
            result.add(new int[]{i, p[1]});
        }
        return result;
    }

    /**
     * 给一个Point对象, 返回它的右边通道
     *
     * @param p
     * @param max        向右时的最右界限
     * @return 给定Point右边的通道
     */
    private List<int[]> getRightChanel(int[] p, int max) {
        List<int[]> result = new ArrayList<>();
        // 获取向右通道, 由一个点向右遍历, 步长为Piece图片的宽
        for (int i = p[0] + 1; i <= max; i = i + 1) {
            // 遇到障碍, 表示通道已经到尽头, 直接返回
            if (hasPiece(i, p[1])) {
                return result;
            }
            result.add(new int[]{i, p[1]});
        }
        return result;
    }

    /**
     * 给一个Point对象, 返回它的上面通道
     *
     * @param p
     * @param min         向上遍历时最小的界限
     * @return 给定Point上面的通道
     */
    private List<int[]> getUpChanel(int[] p, int min) {
        List<int[]> result = new ArrayList<>();
        // 获取向上通道, 由一个点向右遍历
        for (int i = p[1] - 1; i >= min;  i = i - 1) {
            // 遇到障碍, 表示通道已经到尽头, 直接返回
            if (hasPiece(p[0], i)) {    // 传入的分别是 坐标x(p[0]), 坐标y(i)
                // 如果遇到障碍, 直接返回
                return result;
            }
            result.add(new int[]{p[0], i});
        }
        return result;
    }

    /**
     * 给一个Point对象, 返回它的下面通道
     *
     * @param p
     * @param max 向上遍历时的最大界限
     * @return 给定Point下面的通道
     */
    private List<int[]> getDownChanel(int[] p, int max) {
        List<int[]> result = new ArrayList<int[]>();
        // 获取向下通道, 由一个点向右遍历, 步长为Piece图片的高
        for (int i = p[1] + 1; i <= max; i = i + 1) {
            // 遇到障碍, 表示通道已经到尽头, 直接返回
            if (hasPiece(p[0], i)) {
                // 如果遇到障碍, 直接返回
                return result;
            }
            result.add(new int[]{p[0], i});
        }
        return result;
    }
}
