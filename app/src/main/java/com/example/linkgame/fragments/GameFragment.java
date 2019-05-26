package com.example.linkgame.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.List;

public class GameFragment extends Fragment implements View.OnClickListener {
    // 处理游戏暂停还是继续
    public static boolean isGamePause = false;
    // 布局
    private View v;
    //
    private Handler mGameHandler;



    //-初始化 GridLayout, ImageViewArr 数据------------------------------------------
    /**
     * 初始化 网格布局 和 网格ImageView数据 并 加载数据(会清空网格原有的数据, 故原则上只使用一次)
     *
     * @param rows      行数
     * @param cols      列数
     * @param gameStyle 游戏模式 GameService.STYLE_
     */
    public void initGame(int rows, int cols, int gameStyle) {
        mGridLayout.removeAllViews();
        // 设置layout 网格 行, 列
        mGridLayout.setRowCount(rows);
        mGridLayout.setColumnCount(cols);
        // 初始化 ImageViewArr
        ViewOp.initViewArr(cols*rows);

        // 获取随机的图片List
        List<Pic> picList = GameService.getCurrentDrawableList(GameService.getNeedDrawableNum(
                SharedData.getInt(SharedData.CURRENT_GAME_TYPE, 0),
                rows, cols)
        );
        if (BuildConfig.DEBUG) Log.d("GameFragment", "随机图片list picList.size : " + picList.size());

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

            //绑定 Pic数组 与 ImageView 数组 的关系
            if (GameService.needSetTextImg(i, gameStyle, rows, cols)) {
                ViewOp.bindPicToView(i, picList.get(currentDrawableIndex++));
            } else {
                ViewOp.bindPicToView(i, null);
            }
            ViewOp.get(i).setTag(R.id.imageViewIndex, i);
            //--将ImageView 添加到 点击监听器--------------------------------------------------------
            ViewOp.get(i).setOnClickListener(new View.OnClickListener() {
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
    private ImageView mReLayoutImageView;


    private ConstraintLayout mGameResultPage;   // 游戏结算页
//    private TextView mShowScoreText;
    private Button mGoRankBtn;


    private void init(View v) {
        mGridLayout = v.findViewById(R.id.gridlayout_game);
        mCountdownTextView = v.findViewById(R.id.tv_countdown);
        mPauseOrPlayImageView = v.findViewById(R.id.iv_pause);
        mPauseOrPlayImageView.setOnClickListener(this);
        mReLayoutImageView = v.findViewById(R.id.iv_reLayout);
        mReLayoutImageView.setOnClickListener(this);

        v.findViewById(R.id.iv_backToStart).setOnClickListener(this);   // 回到开始页 按钮
        mGameResultPage = v.findViewById(R.id.constrain_game_result);
//        mShowScoreText = v.findViewById(R.id.tv_show_score);
        mGoRankBtn = v.findViewById(R.id.btn_go_rank);
        mGoRankBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_backToStart:
                ((GameActivity)getActivity()).changePage(0);
                Message.obtain(mGameHandler, GameActivity.MSG_WHAT_OVER).sendToTarget();
                break;    // 此处应该不需要break
            case R.id.iv_pause:
                // 暂停与开始
                isGamePause = !isGamePause;
                if(isGamePause){
                    Message.obtain(mGameHandler, GameActivity.MSG_WHAT_PAUSE, this).sendToTarget();
                }else {
                    Message.obtain(mGameHandler, GameActivity.MSG_WHAT_PLAY, this).sendToTarget();
                }
                break;
            case R.id.iv_reLayout:
                if (BuildConfig.DEBUG) Log.d("GameFragment", "重排布局 被点击了");
                Message.obtain(mGameHandler, GameActivity.MSG_WHAT_RE_LAYOUT, this).sendToTarget();
                break;
//            case R.id.btn_go_rank:
//                // 进入分数排行页, (保存分数应该在 游戏结束Message 页进行)
//                ((GameActivity)getActivity()).changePage(2);
//                break;
            default:
                if (BuildConfig.DEBUG) Log.d("swR+GameFragment", "未处理的点击事件...");
        }
    }

    public void handleGameResultPage( boolean isSuccess){
        //  显示 成功/失败 对话框
        showNormalDialog(isSuccess);
    }


    public void handlePlayOrPauseBtn(boolean setPause) {
        // 配置按钮图标
        mPauseOrPlayImageView.setImageResource(
                (setPause ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause));
    }

    private void showNormalDialog( boolean isSuccess){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        Context context = getContext();
        if(context == null){
            if (BuildConfig.DEBUG) Log.w("GameFragment", "无法获取到Context, 可能是因为游戏尚未结束就返回主页导致的");
            return;
        }
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setIcon(isSuccess? R.drawable.ic_dialog_success : R.drawable.ic_dialog_lost);
        normalDialog.setTitle("游戏"+(isSuccess?"成功":"失败"));
//        normalDialog.setMessage("你要点击哪一个按钮呢?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 返回 开始页面
                        ((GameActivity)getActivity()).changePage(0);    // 回到启动页
                    }
                });
        // 显示
        normalDialog.show();
    }

//--------------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_game, container, false);
        init(v);
        int gameStyle = SharedData.getInt(SharedData.CURRENT_GAME_TYPE, 0);
        if (BuildConfig.DEBUG) Log.d("GameFragment", "gameStyle:" + gameStyle); //todo=====

        initGame(Config.GRID_ROWS, Config.GRID_COLS, gameStyle);   // 通过Activity的 hand
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
    }
}
