package com.example.linkgame.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.linkgame.MyApplication;
import com.example.linkgame.R;
import com.example.linkgame.View.PieceImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;


/**
 * // 注意, 资源图片为 从0开始到 519, 其中偶数为中文图, 奇数为英文图
 * 图片资源工具类, 主要用于读取游戏图片资源值
 */
public class ImageUtil {
    /**
     * 存储资源文件夹的所有图片资源
     */
    private static Drawable[] drawableArr;


    /**
     * 将图片ID集合转换PieceImage对象集合，PieceImage封装了图片ID与图片本身
     *
     * @return size个PieceImage对象的集合
     */
    public static List<PieceImage> getPlayImages(int size) {

        // 获取图片ID组成的集合
        Object[] randomPicIndexList = getRandomPicIndexList(size);

        List<PieceImage> result = new ArrayList<PieceImage>();
        // 遍历每个图片ID
        for (Object value : randomPicIndexList) {
            Integer val = (Integer) value;
//            System.out.println("###加载图片###"+val +(val%2==0?"\t中文":""));    //todo test
            // 加载图片
            Bitmap bm = drawableToBitmap(getAllDrawable()[val]);
            // 封装图片ID与图片本身
            PieceImage pieceImage = new PieceImage(bm, val);
            result.add(pieceImage);
        }

        return result;
    }


    // 为 drawableList赋值
    private static Drawable[] getAllDrawable() {
        if (drawableArr == null) {
            drawableArr = new Drawable[GameConf.ALL_IMG_NUM * 2];
            // 从drawable文件夹里面添加名称为 pic_[0, length).png 的图片资源到 drawableArr
            for (int i = 0; i < drawableArr.length; i++) {
                drawableArr[i] = MyApplication.getContext().getDrawable(
                        MyApplication.getContext().getResources().getIdentifier(
                                "pic_" + i, "drawable", MyApplication.getPkgName()));
            }
        }
        return drawableArr;
    }


    /**
     * 从drawable目录中中获取size个图片资源ID(以p_为前缀的资源名称), 其中size为游戏数量
     *
     * @param size 需要获取的图片ID的数量
     * @return size个图片ID的集合
     */
    private static Object[] getRandomPicIndexList(int size) {
        if (size % 2 != 0) {
            // 如果该数除2有余数，将size加1
            size += 1;
        }
        List<Integer> playImageValues = new ArrayList<>(size);
        // 为List添加 size对 中文+英文 图, 共计size个
        Random random = new Random();
        for (int i = 0; i < size/2; i++) {  // 此处 size/2 因为一个循环中添加了2个元素
            int t = random.nextInt(GameConf.ALL_IMG_NUM);
            playImageValues.add(t * 2);     // 添加一个 中文图的index
            playImageValues.add(t * 2 + 1); // 添加一个 中文图对应的英文图的index
        }
        //将所有图片ID随机“洗牌”
        Collections.shuffle(playImageValues);
        return playImageValues.toArray();


//        //  如果需要不重复的中英文图片index组, 则使用下面的代码
//        HashSet<Integer> playImageValues = new HashSet<>(size);
//        Random random = new Random();
//        while (playImageValues.size() < size) {
//            int t = random.nextInt(GameConf.ALL_IMG_NUM);
//            playImageValues.add(t * 2);     // 添加一个 中文图的index
//            playImageValues.add(t * 2 + 1); // 添加一个 中文图对应的英文图的i
//        }
//        return playImageValues.toArray();
    }


    /**
     * 将Drawable转换为Bitmap
     */
    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
//        Bitmap bitmap = Bitmap.createBitmap(
//                drawable.getIntrinsicWidth(),
//                drawable.getIntrinsicHeight(),
//                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//                        : Bitmap.Config.RGB_565);
//        Canvas canvas = new Canvas(bitmap);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
//                drawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(GameConf.PIECE_WIDTH, GameConf.PIECE_HEIGHT, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, GameConf.PIECE_WIDTH, GameConf.PIECE_HEIGHT);
        drawable.draw(canvas);
        return bitmap;
    }


    /**
     * 获取选中标识的图片
     *
     * @param context
     * @return 选中标识的图片
     */
    public static Bitmap getSelectImage(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.selected);
    }
}
