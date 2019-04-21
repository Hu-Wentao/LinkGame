package com.example.linkgame.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.List;

public class GameFragment extends Fragment implements View.OnClickListener {
    // 处理游戏暂停还是继续
    private boolean isGamePause = false;
    // 布局
    private View v;

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //-初始化 GridLayout, ImageViewArr 数据------------------------------------------
    /**
     * 该数组的索引 与ImageView 在GridLayout中的位置 直接相关
     */
    public static ImageView[] sImageViewArr;

    /**
     * 初始化 网格布局 和 网格ImageView数据 并 加载数据(会清空网格原有的数据, 故原则上只使用一次)
     *
     * @param rows      行数
     * @param cols      列数
     * @param gameStyle 游戏模式 GameService.STYLE_
     */
    public void loadDataToLayout(int rows, int cols, int gameStyle) {
        // 初始化数据
        int allViewNum = cols * rows;
        mGridLayout.removeAllViews();
        // 设置layout 网格 行, 列
        mGridLayout.setRowCount(rows);
        mGridLayout.setColumnCount(cols);


        // 初始化 ImageViewArr
        sImageViewArr = new ImageView[allViewNum];

        System.out.println("当前布局下应当获取的随机图片张数:" + GameService.getNeedDrawableNum(SharedData.getInt(SharedData.CURRENT_GAME_TYPE, 0), rows, cols));   //todo


        List<Pic> list = GameService.getCurrentDrawableList(true,
                GameService.getNeedDrawableNum(
                        SharedData.getInt(SharedData.CURRENT_GAME_TYPE, 0),
                        rows, cols)
        ); // 获取随机的图片LIst

        // 将图片设置为ImageView的 background, 然后添加进Layout
        int currentDrawableIndex = 0;
        for (int i = 0; i < allViewNum; i++) {
            // 配置参数
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = 0;
            layoutParams.height = 0;
            // 创建控件
            sImageViewArr[i] = new ImageView(getContext());


            Drawable tmp;
//            if (GameService.needSetTextImg(i, gameStyle, rows, cols) && currentDrawableIndex<list.size()) {   //如果出现错误, 可以使用本行代码查看出错后的图片排布
            if (GameService.needSetTextImg(i, gameStyle, rows, cols)) {
                Pic pic = list.get(currentDrawableIndex++);
                tmp = pic.drawable;
//                System.out.println("添加ImageView ,tag为: " + pic.tag);    //todo del
                sImageViewArr[i].setTag(R.id.PicTag, pic.tag);
            } else {
                tmp = getContext().getDrawable(R.drawable.img_blank);
            }
            // 配置ImageViewArr 背景图, 索引号
            sImageViewArr[i].setBackground(tmp);
            sImageViewArr[i].setTag(R.id.imageViewIndex, i);

            //--将ImageView 添加到 点击监听器----------------------------------------------------------
            sImageViewArr[i].setOnClickListener(new View.OnClickListener() {
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
            mGridLayout.addView(sImageViewArr[i], layoutParams);
        }

    }

    //--- ImageView 点击监听事件
    private void onImgClick(View v) {
        Object picTag;
        if ((picTag = v.getTag(R.id.PicTag)) == null) {
            picTag = -1;   // 表示当前点击的是空白的图片
        }

        GameService.onImageClick((int) v.getTag(R.id.imageViewIndex), (int) picTag, getContext());
    }


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
                handleGamePlayOrPause(((GameActivity) getActivity()).mGameHandler);
                break;
            // TODO: 2019/4/21 游戏结算页
            default:
                if (BuildConfig.DEBUG) Log.d("swR+GameFragment", "未处理的点击事件...");
        }
    }

    /**
     *
     */
    private void handleGamePlayOrPause(Handler handler) {
        isGamePause = !isGamePause;
        handler.sendEmptyMessage(
                isGamePause ? GameActivity.MSG_WHAT_PAUSE : GameActivity.MSG_WHAT_PLAY);
        // 配置按钮图标
        mPauseOrPlayImageView.setImageResource(
                (isGamePause ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause));
    }


    @Override
    public void onPause() {
        super.onPause();
//        handleGamePlayOrPause();
    }
}
