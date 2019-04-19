package com.example.linkgame.activities;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.linkgame.R;
import com.example.linkgame.fragments.GameFragment;
import com.example.linkgame.fragments.RankFragment;
import com.example.linkgame.fragments.StartFragment;
import com.example.linkgame.utils.GameConf;

public class GameActivity extends AppCompatActivity {

    public static final int
            MSG_WHAT_START = 0,
            MSG_WHAT_PAUSE = 1,
            MSG_WHAT_PLAY = 2,
            MSG_WHAT_OVER = 3;
    public static final int
            MSG_WHAT_REFRESH = 11,
            MSG_WHAT_INTERVAL = 12,
            MSG_WHAT_RESTART = 13;


    private Handler mGameHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_START:    // 开始游戏
                    // 开始计时器
                    GameTimer.start(GameConf.DEFAULT_TIME, mGameHandler);
                    // todo 加载界面
                    break;
                case MSG_WHAT_PAUSE:    // 游戏暂停
                    // todo 在离开app 或 点击暂停按钮 时自动调用,
                    // 暂停计时器
                    GameTimer.pause();
                    break;
                case MSG_WHAT_PLAY:     // 游戏继续
                    //todo 点击开始按钮时调用
                    // 继续计时器
                    GameTimer.play(mGameHandler);
                    break;
                case MSG_WHAT_OVER:     // 游戏结算
                    // todo 显示结算页面
                    // 结束计时器
                    GameTimer.cancel();
                    break;
                //-------------------
                case MSG_WHAT_INTERVAL:  // 游戏定时间隔
                    // todo 自动刷新 TextView
                    break;
                case MSG_WHAT_REFRESH:  // 用户操作导致更新
                    // todo 刷新游戏分数
                    // todo 刷新界面
                    break;
                case MSG_WHAT_RESTART:  // 游戏手动重启(当游戏无法进行下去的时候)
                    // todo 保存当前游戏内剩余的中英文图, 重新放置它们的位置
                    break;
                default:
                    return false;
            }
            return true;
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
                    saveMillisUntilFinished = millisUntilFinished;
                    // 发送一个消息
                    Message.obtain(handler, GameActivity.MSG_WHAT_INTERVAL, (int) (millisUntilFinished / 1000), 0).sendToTarget();
                }

                @Override
                public void onFinish() {
                    handler.sendEmptyMessage(GameActivity.MSG_WHAT_OVER);
                }
            };
        }

        // 开始
        public static void start(long gameTime, final Handler handler) {
            if (timer == null) {
                timer = getTimer(gameTime, handler);
            }
            timer.start();
        }

        // 暂停
        public static void pause() {
            timer.cancel();
        }

        // 恢复
        public static void play(Handler handler) {
            start(saveMillisUntilFinished + 900, handler);
        }

        // 取消计时器
        public static void cancel() {
            saveMillisUntilFinished = 0;
            timer.cancel();
        }
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
//        initView();
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


}
