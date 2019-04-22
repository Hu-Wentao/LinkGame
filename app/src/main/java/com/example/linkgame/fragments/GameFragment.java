package com.example.linkgame.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.linkgame.BuildConfig;
import com.example.linkgame.R;
import com.example.linkgame.activities.GameActivity;
import com.example.linkgame.db.SharedData;
import com.example.linkgame.game.Config;
import com.example.linkgame.game.Pic;
import com.example.linkgame.game.GameService;
import com.example.linkgame.game.ViewOp;

import java.util.ArrayList;
import java.util.List;

public class GameFragment extends Fragment implements View.OnClickListener {
    // 处理游戏暂停还是继续
    private boolean isGamePause = false;
    // 布局
    private View v;
    //
    private Handler mGameHandler;


    //-初始化 GridLayout, ImageViewArr 数据------------------------------------------
//    /**
//     * 该数组的索引 与ImageView 在GridLayout中的位置 直接相关
//     */
//    private static ImageView[] sImageViewArr;
//    /**
//     * 保存 sImageViewArr 中对应控件的 tag, 注意, 在 sImageViewArr被修改时, 该变量需要同步修改
//     */
//    private static int[] sViewTagArr;
//    // 同时操作 sImageViewArr 与 sViewTagArr
//    public static void operateViewArr(int index){
//
//    }
    /**
     * 初始化 网格布局 和 网格ImageView数据 并 加载数据(会清空网格原有的数据, 故原则上只使用一次)
     *
     * @param rows      行数
     * @param cols      列数
     * @param gameStyle 游戏模式 GameService.STYLE_
     */
    public void loadDataToLayout(int rows, int cols, int gameStyle) {
        mGridLayout.removeAllViews();
        // 设置layout 网格 行, 列
        mGridLayout.setRowCount(rows);
        mGridLayout.setColumnCount(cols);
        // 初始化 ImageViewArr
        ViewOp.initViewArr(cols*rows);
//        sImageViewArr = new ImageView[cols * rows];

        System.out.println("当前布局下应当获取的随机图片张数:" + GameService.getNeedDrawableNum(SharedData.getInt(SharedData.CURRENT_GAME_TYPE, 0), rows, cols));   //todo
        // 获取随机的图片LIst
        List<Pic> list = GameService.getCurrentDrawableList(GameService.getNeedDrawableNum(
                SharedData.getInt(SharedData.CURRENT_GAME_TYPE, 0),
                rows, cols)
        );

        // 将图片设置为ImageView的 background, 然后添加进Layout
        int currentDrawableIndex = 0;
        for (int i = 0; i < rows * cols; i++) {
            // 配置参数
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = 0;
            layoutParams.height = 0;
            // 创建控件
            ViewOp.setView(i, new ImageView(getContext()));
//            sImageViewArr[i] = new ImageView(getContext());
            Drawable tmp;
//            if (GameService.needSetTextImg(i, gameStyle, rows, cols) && currentDrawableIndex<list.size()) {   //如果出现错误, 可以使用本行代码查看出错后的图片排布
            if (GameService.needSetTextImg(i, gameStyle, rows, cols)) {
                Pic pic = list.get(currentDrawableIndex++);
                tmp = pic.drawable;
//                System.out.println("添加ImageView ,tag为: " + pic.tag);    //todo del
                ViewOp.setTag(i, pic.tag);
//                sImageViewArr[i].setTag(R.id.PicTag, pic.tag);
            } else {
                tmp = getContext().getDrawable(R.drawable.img_blank);
            }
            // 配置ImageViewArr 背景图, 索引号
            ViewOp.setBackground(i, tmp);
//            ViewOp.setTag(i, i);
//            sImageViewArr[i].setBackground(tmp);
            ViewOp.get(i).setTag(R.id.imageViewIndex, i);  // todo imageViewIndex 考虑替代(picTag已替代)
//            sImageViewArr[i].setTag(R.id.imageViewIndex, i);

            //--将ImageView 添加到 点击监听器----------------------------------------------------------
            ViewOp.get(i).setOnClickListener(new View.OnClickListener() {
//            sImageViewArr[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onImgClick(v);
                }
            });

            //--将ImageView 添加到Layout中----------------------------------------------------------
            // 设置行列下标，和比重
            layoutParams.rowSpec = GridLayout.spec((i) / cols, 1f);
            layoutParams.columnSpec = GridLayout.spec((i) % cols, 1f);

            // 设置边距
            layoutParams.setMargins(
                    Config.GRID_LAYOUT_VIEW_MARGIN[0],
                    Config.GRID_LAYOUT_VIEW_MARGIN[1],
                    Config.GRID_LAYOUT_VIEW_MARGIN[2],
                    Config.GRID_LAYOUT_VIEW_MARGIN[3]);
            mGridLayout.addView(ViewOp.get(i), layoutParams);
//            mGridLayout.addView(sImageViewArr[i], layoutParams);
        }

    }

    //--- ImageView 点击监听事件
    private void onImgClick(View v) {
        GameService.onImageClick((int) v.getTag(R.id.imageViewIndex), mGameHandler);
    }
    //----------------------------------------------------



    //-初始化控件------------------------------------------
    private GridLayout mGridLayout;
    public TextView mCountdownTextView;
    private ImageView mPauseOrPlayImageView;


    private void initView(View v) {
        mGridLayout = v.findViewById(R.id.gridlayout_game);
        mCountdownTextView = v.findViewById(R.id.tv_countdown);
        mPauseOrPlayImageView = v.findViewById(R.id.iv_pause);
        mPauseOrPlayImageView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_pause:
                // 暂停与开始
                Message.obtain(mGameHandler, GameActivity.MSG_WHAT_PAUSE_OR_PLAY, isGamePause ? 1 : 0, -1, this).sendToTarget();
                break;
            case R.id.iv_reLayout:
                Message.obtain(mGameHandler, GameActivity.MSG_WHAT_RE_LAYOUT, this).sendToTarget();
                break;
            // TODO: 2019/4/21 游戏结算页
            default:
                if (BuildConfig.DEBUG) Log.d("swR+GameFragment", "未处理的点击事件...");
        }
    }

    /**
     *
     */
    public void handlePlayOrPauseBtn(boolean setPause) {
        isGamePause = !setPause;
        // 配置按钮图标
        mPauseOrPlayImageView.setImageResource(
                (isGamePause ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause));
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_game, container, false);
        initView(v);
        int gameStyle = SharedData.getInt(SharedData.CURRENT_GAME_TYPE, 0);
        loadDataToLayout(Config.GRID_ROWS, Config.GRID_COLS, gameStyle);   // 通过Activity的 hand
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mGameHandler = ((GameActivity) context).mGameHandler;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
        Message.obtain(mGameHandler, GameActivity.MSG_WHAT_PAUSE_OR_PLAY, 1, -1, this);
    }
}
