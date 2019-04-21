package com.example.linkgame.game;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;

import com.example.linkgame.R;
import com.example.linkgame.activities.GameActivity;
import com.example.linkgame.fragments.GameFragment;
import com.example.linkgame.utils.ImageUtil;
import com.example.linkgame.utils.LinkUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: hu.wentao@outlook.com
 * @Date: 2019/4/19
 */
public class GameService {
    //---------------------------------------------------
    public static final int
            STYLE_HORIZONTAL = 0,
            STYLE_FILL = 2;


    //-处理ImageView 的点击事件--------------------------------------------------
    // 保存已经被选择的View, -1表示当前没有View被选中
    private static int savedViewIndex = -1;

    /**
     * 处理ImageView 的点击事件
     *
     * @param viewIndex ImageView 在数组中的位置
     * @param picTag    如果不为-1, 则表示 文字图片资源 的后缀
     */
    public static void onImageClick(int viewIndex, int picTag, Context context) {
        // 如果被点击的view 没有textTag, 则 取消已经被选中的view
        if (picTag == -1) {
            setSelectedView(-1);
            return;
        }

        if (!isTextTagMatch(picTag)) {
            // 如果不对应, 则退出, 并将最新的view 设为选中
            setSelectedView(viewIndex);
        } else {// 如果前后两个view 的 textTag是匹配的
            List<Integer> indexLink;
            if ((indexLink = LinkUtils.getConnectLink(GameFragment.sImageViewArr, savedViewIndex, viewIndex)) == null) {
                // 如果两个view不能进行连接
                setSelectedView(viewIndex);
            } else {
                // 两个view可以进行连接
                int savedTag = picTag + (picTag % 2 == 0 ? 1 : -1);
                if (indexLink.size() > 2) {
                    // 发送 Message 显示连接线 (如果 indexLink 长度等于2, 则不发送)
                    Message.obtain(((GameActivity) context).mGameHandler, GameActivity.MSG_WHAT_SHOW_INDEX_LINK, indexLink).sendToTarget();
                    // 发送 延迟Message 清除连接线(可以在此播放音效)
                    Message m = Message.obtain();
                    m.what = GameActivity.MSG_WHAT_HIDE_INDEX_LINK;
                    m.obj = indexLink;
                    ((GameActivity) context).mGameHandler.sendMessageDelayed(m, 300);
                }
                // 将savedViewIndex ,viewIndex 的图,tag 都设为空
                GameFragment.sImageViewArr[savedViewIndex].setBackground(new ColorDrawable(0xFFFFFF));
                GameFragment.sImageViewArr[savedViewIndex].setTag(R.id.PicTag, null);
                GameFragment.sImageViewArr[viewIndex].setBackground(new ColorDrawable(0xFFFFFF));
                GameFragment.sImageViewArr[viewIndex].setTag(R.id.PicTag, null);


                // 从 list中移除被消除的 Pic
                removeByPicTag(savedTag);
                removeByPicTag(picTag);
                // 检测 sCurrentDrawableList 中是否还有元素, 如果没有,则 游戏结束
                if (sCurrentDrawableList.isEmpty()) {
                    // TODO: 2019/4/21 尝试使用MyApplication 来发送Handler
                    ((GameActivity) context).mGameHandler.sendEmptyMessage(GameActivity.MSG_WHAT_OVER);
                }
                // 移除
                GameFragment.sImageViewArr[savedViewIndex].setImageDrawable(null);
                GameFragment.sImageViewArr[savedViewIndex].setImageResource(R.drawable.img_blank);
                savedViewIndex = -1;
                setSelectedView(-1);
            }
        }
    }
//==================================================================================================


//==================================================================================================

    // 判断这两个view的 textTag 是否是对应的
    private static boolean isTextTagMatch(int currentPicTag) {
        // 如果当前没有已被选中的图, 则跳过match
        if (savedViewIndex == -1) {
            return false;
        }
        // 已被选中的view 的tag值
        int t = (int) GameFragment.sImageViewArr[savedViewIndex].getTag(R.id.PicTag);
        return t + (t % 2 == 0 ? 1 : -1) == currentPicTag;
    }
    // 设置被选中的View, 如果 index为 -1 表示 清除已经被选中的 view 标识

    /**
     * 处理 ImageView 的选中与未选中
     *
     * @param viewIndex 当前正在被点击的ImageView 的 index
     */
    private static void setSelectedView(int viewIndex) {
        if (savedViewIndex != -1) { // 如果当前已经有一个 view 被设为选中状态
            if (viewIndex == -1) {  // 如果当前被点击的view 没有textTag
                // 取消已经被选中的view
                GameFragment.sImageViewArr[savedViewIndex].setImageDrawable(null);
                savedViewIndex = -1;
            } else {  // 如果当前被点击的view 有textTag
                // 删除原来的被选中的图的选中状态, 将当前设为选中状态
                GameFragment.sImageViewArr[savedViewIndex].setImageDrawable(null);
                GameFragment.sImageViewArr[viewIndex].setImageResource(R.drawable.selected);
                savedViewIndex = viewIndex;
            }
        } else { // 如果当前没有 view 被设为选中状态
            if (viewIndex != -1) { // 如果当前被点击的view 有textTag
                GameFragment.sImageViewArr[viewIndex].setImageResource(R.drawable.selected);
                savedViewIndex = viewIndex;
            }
        }
    }


    //---------------------------------------------------

    /**
     * 根据当前布局类型, 返回初始的需要的随机图片的张数
     *
     * @param STYLE       布局类型
     * @param rowsAndCols 行数, 列数
     * @return 该布局所需的随机图片数
     */
    public static int getNeedDrawableNum(int STYLE, int... rowsAndCols) {
        switch (STYLE) {
            case STYLE_HORIZONTAL:
                return rowsAndCols[1] * (rowsAndCols[0] / 2 + (rowsAndCols[0] % 2));
            case STYLE_FILL:
                return (rowsAndCols[0] + rowsAndCols[1] - 1) * 2;
            default:
                throw new RuntimeException("未知STYLE" + STYLE);
        }
    }

    //为layout里的特定index view设置 图片, 非特定的设置默认图(白色填充)

    /**
     * 返回指定索引号 位置 是填入获取的随机图,还是填入空白图
     *
     * @param currentIndex 指定索引号
     * @param STYLE        何种类型的布局
     * @param rowsAndCols  当前指定的行数, 列数
     * @return 是否填入随机图(如果是否, 则应当填入空白图)
     */
    public static boolean needSetTextImg(int currentIndex, int STYLE, int... rowsAndCols) {
        switch (STYLE) {
            case STYLE_HORIZONTAL:
                return (currentIndex / (rowsAndCols[1])) % 2 == 0;   // 获得横线式排版
            case STYLE_FILL:
                // 确保第一行为空 || 确保最后一行为空 || 确保第一列为空 || 确保最后一列为空
                return !(currentIndex / (rowsAndCols[1]) == 0 || currentIndex / (rowsAndCols[1]) == rowsAndCols[0] - 1 ||
                        currentIndex % (rowsAndCols[1]) == 0 || currentIndex % (rowsAndCols[1]) == rowsAndCols[1] - 1); // 获取填充式(不包括四周)排版
            default:
                return true;
        }
    }

    //---------------------------------------------------
    /**
     * 可以考虑换成 ParseArray
     * <p>
     * 当前在 Layout中显示的 所有非空的图片,(其下标不表示指定图片在Layout中的位置)
     * 其数量由 指定的行数,列数 与 STYLE 共同确定
     */
    private static ArrayList<Pic> sCurrentDrawableList;

    /**
     * 获取当前正在显示的 文字图片, 如果当前没有, 则自动生成随机的
     *
     * @param isRefresh   是否刷新当前已保存的数组
     * @param defaultSize 如果isRefresh == true 或者当前没有正在显示的图片数组的话,
     *                    则自动产生 defaultSize个元素
     *                    图片数量(必须是偶数)(已在内部方法中完成检验步骤)
     * @return Drawable数组
     */
    public static ArrayList<Pic> getCurrentDrawableList(boolean isRefresh, int defaultSize) {
        if (sCurrentDrawableList == null || isRefresh) {
            sCurrentDrawableList = new ArrayList<>();
            Collections.addAll(sCurrentDrawableList, ImageUtil.getRandomDrawableArr(defaultSize));  // 将数组全部添加到
            Collections.shuffle(sCurrentDrawableList);  // 打乱图片顺序
        }
        return sCurrentDrawableList;
    }

    /**
     * 通过 tag 删除list中指定元素
     *
     * @param tag picTag
     */
    public static void removeByPicTag(int tag) {
        Iterator<Pic> it = sCurrentDrawableList.iterator();
        while (it.hasNext()) {
            if (it.next().tag == tag) {
                it.remove();
                return;
            }
        }
    }
}
