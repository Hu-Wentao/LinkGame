package com.example.linkgame.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.example.linkgame.game.Config;
import com.example.linkgame.game.impl.GameService;

public class GameFragment extends Fragment implements View.OnClickListener {
    // 处理游戏暂停还是继续
    private boolean isGamePause = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_game, container, false);
        initView(v);
//        loadDataToLayout();   // 通过Activity的 hand
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

    //-初始化 GridLayout 数据------------------------------------------

    /**
     * 初始化 网格布局并 加载数据(会清空网格原有的数据, 故原则上只使用一次)
     *
     * @param cols
     * @param rows
     */
    public void loadDataToLayout(int cols, int rows) {
        // 初始化数据
        if (mGridLayout != null)
            mGridLayout.removeAllViews();
        int allViewNum = Config.GRID_COLS * Config.GRID_ROWS;
        Drawable[] tmp =
                GameService.getCurrentDrawableArr(true, allViewNum);
        //todo 将图片设置为ImageView的 background
        for (int i = 0; i < allViewNum; i++) {
            if(GameService.setImg
        }

    }

    //-


    //-初始化控件------------------------------------------
    private GridLayout mGridLayout;
    private TextView mCountdownTextView;    // todo 倒计时时需要修改
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
                handleGamePlayOrPause();
                break;
            default:
                if (BuildConfig.DEBUG) Log.d("swR+GameFragment", "未处理的点击事件...");
        }
    }

    /**
     *
     */
    private void handleGamePlayOrPause() {
        isGamePause = !isGamePause;
        ((GameActivity) getActivity()).mGameHandler
                .sendEmptyMessage(
                        isGamePause ? GameActivity.MSG_WHAT_PAUSE : GameActivity.MSG_WHAT_PLAY);
        // 配置按钮图标
        mPauseOrPlayImageView.setImageResource(
                (isGamePause ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause));
    }

    /**
     *
     * @param remainTime 当前游戏还剩多长时间
     */
    public void handleCountDownText(int remainTime){
        mCountdownTextView.setText(("剩余: "+remainTime + "秒"));
    }


    @Override
    public void onPause() {
        super.onPause();
        handleGamePlayOrPause();
    }
}
