package com.example.linkgame.fragments;

import android.content.Context;
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
import com.example.linkgame.game.GameService;
import com.example.linkgame.game.impl.GameServiceImpl;
import com.example.linkgame.utils.GameConf;
import com.example.linkgame.utils.SizeUtils;

public class GameFragment extends Fragment implements View.OnClickListener {


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
        initData();

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

    //-初始化数据------------------------------------------
//    // todo 此处待优化 GameConfig类
//    // 适配不同的屏幕，dp转为px
//    int beginImageX = SizeUtils.dp2Px(GameConf.BEGIN_IMAGE_X);
//    int beginImageY = SizeUtils.dp2Px(GameConf.BEGIN_IMAGE_Y);
//    private GameConf config = new GameConf(GameConf.PIECE_X_SUM, GameConf.PIECE_Y_SUM, beginImageX, beginImageY, GameConf.DEFAULT_TIME);
//    private GameService gameService = new GameServiceImpl(config);

    // 初始化要加载的数据
    private void initData() {

    }


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
                // todo 暂停与开始
                break;
            default:
                if (BuildConfig.DEBUG) Log.d("swR+GameFragment", "未处理的点击事件...");
        }
    }
}
