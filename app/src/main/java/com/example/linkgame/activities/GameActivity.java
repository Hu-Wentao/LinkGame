package com.example.linkgame.activities;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.linkgame.BuildConfig;
import com.example.linkgame.R;
import com.example.linkgame.db.SharedData;
import com.example.linkgame.fragments.GameFragment;
import com.example.linkgame.fragments.RankFragment;
import com.example.linkgame.fragments.StartFragment;
import com.example.linkgame.game.Config;
import com.example.linkgame.game.ViewOp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    public static final int
            MSG_WHAT_START_NEW_GAME = 0,
            MSG_WHAT_PAUSE_OR_PLAY = 1,
    //            MSG_WHAT_PLAY = 2,
    MSG_WHAT_OVER = 3;
    public static final int
            MSG_WHAT_REFRESH = 11,
            MSG_WHAT_INTERVAL = 12,
            MSG_WHAT_RE_LAYOUT = 13;
    public static final int
            MSG_WHAT_SHOW_INDEX_LINK = 21,
            MSG_WHAT_HIDE_INDEX_LINK = 22;

    public Handler mGameHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_START_NEW_GAME:    // 开始新游戏
                    System.out.println("进入 MSG_WHAT_START_NEW_GAME"); //todo del

                    SharedData.setInt(SharedData.CURRENT_GAME_TYPE, msg.arg1); // 当前游戏模式
                    changePage(1);
                    // 开始计时器
                    GameTimer.start(Config.GAME_TIME, mGameHandler);
                    break;
                case MSG_WHAT_PAUSE_OR_PLAY:    // 游戏暂停 或 继续
                    System.out.println("收到 暂停或继续 message"); //todo
                    // 在离开app 或 点击暂停按钮 时自动调用,
                    boolean setPause = msg.arg1 == 1;
                    GameFragment f = (GameFragment) msg.obj;
                    if (setPause) {
                        GameTimer.pause();  // 暂停计时器
                    } else {
                        GameTimer.play(mGameHandler);
                    }
                    f.handlePlayOrPauseBtn(setPause);

                    break;
//                case MSG_WHAT_PLAY:     // 游戏继续
//                    // 点击开始按钮时调用
//                    //  取消暂停状态
//                    // 继续计时器
//                    GameTimer.play(mGameHandler);
//                    break;
                case MSG_WHAT_OVER:     // 游戏结算
                    int score = GameTimer.getRemainTime();
                    // todo 显示结算页面
                    System.out.println("发送了Game Over"); //TODO DEL

                    // 结束计时器
                    GameTimer.cancel();
                    break;
                //-------------------
                case MSG_WHAT_INTERVAL:  // 游戏定时间隔(在pause 之外,每隔1s 触发一次)
                    // 自动刷新 TextView
                    handleCountDownText(msg.arg1);
                    break;
                case MSG_WHAT_REFRESH:  // 用户操作导致更新( 考虑在方块消除之后, 发送该消息)
                    // todo 刷新游戏分数
                    // todo 刷新界面

                    break;
                case MSG_WHAT_RE_LAYOUT:  // 游戏手动重启(当游戏无法进行下去的时候)
                    System.out.println("执行 游戏view 重排方法");   //todo
                    // 获取当前游戏内剩余的中英文图, 重新放置它们的位置
                    ViewOp.resetViewArrSrc();

                    break;
                //--------------
                case MSG_WHAT_SHOW_INDEX_LINK:
                    List indexList = (ArrayList) msg.obj;
                    if (BuildConfig.DEBUG) Log.d("GameActivity", "收到了显示路径的消息\n路径: "+Arrays.toString(indexList.toArray()));
                    // todo 调用GameFragment 中的相应方法, 展示路径
                    break;
                case MSG_WHAT_HIDE_INDEX_LINK:
                    break;
                default:
                    return false;
            }
            return false;
        }
    });

    /**
     * 可暂停的倒计时器
     */
    static class GameTimer {
        private static CountDownTimer timer;
        private static long saveMillisUntilFinished;

        private static CountDownTimer getTimer(long gameTime, final Handler handler) {
            return new CountDownTimer(gameTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if(millisUntilFinished<1000){
                        onFinish();
                        return;
                    }
                    saveMillisUntilFinished = millisUntilFinished;
//                    System.out.println("定时器发送了消息!");//todo del
                    // 发送一个消息
                    Message.obtain(handler, GameActivity.MSG_WHAT_INTERVAL, (int) (millisUntilFinished / 1000), 0).sendToTarget();
                }

                @Override
                public void onFinish() {
                    handler.sendEmptyMessage(GameActivity.MSG_WHAT_OVER);
                }
            };
        }

        public static int getRemainTime() {
            return (int) (saveMillisUntilFinished / 1000);
        }

        // 开始
        public static void start(long gameTime, final Handler handler) {
            if (timer == null) {
                timer = getTimer(gameTime, handler);
            }
            System.out.println("计时器开始!"); // TODO: 2019/4/21 del
            timer.start();
        }

        // 暂停
        public static void pause() {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            System.out.println("计时器暂停!"); // TODO: 2019/4/21 del

        }

        // 恢复
        public static void play(Handler handler) {
            // TODO: 2019/4/21 尝试不加 900ms
            start(saveMillisUntilFinished , handler);
            System.out.println("计时器恢复!"); // TODO: 2019/4/21 del
        }

        // 取消计时器
        public static void cancel() {
            saveMillisUntilFinished = 0;
//            System.out.println("计时器取消!"); // TODO: 2019/4/21 del

            timer.cancel();
        }
    }
    //------------------------------------------------------------

    /**
     * @param remainTime 当前游戏还剩多长时间
     */
    public void handleCountDownText(int remainTime) {
        ((GameFragment) fragmentArr[1]).mCountdownTextView.setText(("剩余: " + remainTime + "秒"));
    }

    //------------------------------------------------------------
    private long pressBackCache;
    Fragment[] fragmentArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initPage();
        changePage(0);
    }

    private void initPage() {
        fragmentArr = new Fragment[]{new StartFragment(),
                new GameFragment(),
                new RankFragment()
        };
    }


    public void changePage(int index) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout_main, fragmentArr[index])
                .commit();
    }


    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - pressBackCache > 400) {
            pressBackCache = System.currentTimeMillis();
            Toast.makeText(this, "双击返回键退出App", Toast.LENGTH_SHORT).show();
        } else {
            this.finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fragmentArr[1].onPause();   // 间接使用  暂停游戏 方法
    }
}
