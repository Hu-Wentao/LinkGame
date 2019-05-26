package com.example.linkgame.activities;

import android.content.Intent;
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
import com.example.linkgame.fragments.AboutFragment;
import com.example.linkgame.fragments.GameFragment;
import com.example.linkgame.fragments.RuleFragment;
import com.example.linkgame.fragments.StartFragment;
import com.example.linkgame.game.BackgroundMusic;
import com.example.linkgame.game.Config;
import com.example.linkgame.game.GameService;
import com.example.linkgame.game.ViewOp;

import java.util.Arrays;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private static final String BG_MUSIC = "game_bg.mp3";
    // 背景音乐
    private BackgroundMusic bgMusic;

    public static final int
            MSG_WHAT_START_NEW_GAME = 0,
            MSG_WHAT_PAUSE = 1,
            MSG_WHAT_PLAY = 2,
            MSG_WHAT_OVER = 3;
    public static final int
//            MSG_WHAT_REFRESH = 11,
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
//                    if (BuildConfig.DEBUG) Log.d("GameActivity", "进入 MSG_WHAT_START_NEW_GAME");
                    int currentGameType = msg.arg1;

                    // 开启音乐
                    if (BuildConfig.DEBUG) Log.d("GameActivity", "新游戏开始, 开启背景音乐....");
                    bgMusic.playBackgroundMusic(BG_MUSIC, true);

                    SharedData.setInt(SharedData.CURRENT_GAME_TYPE, currentGameType); // 当前游戏模式
                    changePage(1);

                    // 开始计时器
                    GameTimer.start((currentGameType == 0 ? Config.NORMAL_GAME_TIME : Config.HARD_GAME_TIME), mGameHandler, true);
                    break;
                case MSG_WHAT_PAUSE:    // 游戏暂停 或 继续
                    if (BuildConfig.DEBUG) Log.d("GameActivity", "收到 暂停 message");
                    // 在离开app 或 点击暂停按钮 时自动调用,
                    ((GameFragment) msg.obj).handlePlayOrPauseBtn(true);
                    // 暂停音乐
                    bgMusic.pauseBackgroundMusic();

                    GameTimer.pause();  // 暂停计时器
                    break;
                case MSG_WHAT_PLAY:     // 游戏继续
                    if (BuildConfig.DEBUG) Log.d("GameActivity", "收到 继续 message");
                    // 点击开始按钮时调用

                    // 继续音乐
                    bgMusic.resumeBackgroundMusic();

                    //  取消暂停状态
                    ((GameFragment) msg.obj).handlePlayOrPauseBtn(false);

                    // 继续计时器
                    GameTimer.play(mGameHandler);
                    break;
                case MSG_WHAT_OVER:     // 游戏结算
                    if (BuildConfig.DEBUG) Log.d("GameActivity", "发送了Game Over");
                    // 是否完成游戏

                    handleGameOver(GameService.getCurrentDrawableList(-1).isEmpty() && GameTimer.getRemainTime() > 0);
                    // 结束背景音乐
//                    bgMusic.stopBackgroundMusic();
                    bgMusic.end();
                    // 结束计时器
                    GameTimer.cancel();
                    break;
                //-------------------
                case MSG_WHAT_INTERVAL:  // 游戏定时间隔(在pause 之外,每隔1s 触发一次)
                    // 自动刷新 TextView
                    handleCountDownText(msg.arg1);
                    break;
//                case MSG_WHAT_REFRESH:  // 用户操作导致更新(考虑在方块消除之后, 发送该消息)
//                    //  刷新游戏分数
//                    //  刷新界面
//                    break;
                case MSG_WHAT_RE_LAYOUT:  // 游戏手动重启(当游戏无法进行下去的时候)
                    if (BuildConfig.DEBUG) Log.d("GameActivity", "MSG_WHAT_RE_LAYOUT");
                    // 获取当前游戏内剩余的中英文图, 重新放置它们的位置
                    ViewOp.resetViewArrSrc();
                    break;
                //--------------
                case MSG_WHAT_SHOW_INDEX_LINK:
                    List indexList = (List) msg.obj;
                    if (BuildConfig.DEBUG)
                        Log.d("GameActivity", "收到了显示路径的消息\n路径: " + Arrays.toString(indexList.toArray()));
                    // 调用GameFragment 中的相应方法, 展示路径
                    ViewOp.setFlag(true, indexList);
                    break;
                case MSG_WHAT_HIDE_INDEX_LINK:
                    List list = (List) msg.obj;
                    // 隐藏显示的路径, 考虑用 canvas
                    ViewOp.setFlag(false, list);
                    break;
                default:
                    if (BuildConfig.DEBUG) Log.d("GameActivity", "其他消息");

                    Toast.makeText(GameActivity.this, "请打开网络连接", Toast.LENGTH_SHORT).show();
                    GameActivity.super.finish();
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
        private static long saveMillisUntilFinished = -2;

        private static CountDownTimer getTimer(long gameTime, final Handler handler) {
            return new CountDownTimer(gameTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished < 1000) {
                        onFinish();
                        return;
                    }
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

        static int getRemainTime() {
            return (int) (saveMillisUntilFinished / 1000);
        }

        // 开始
        public static void start(long gameTime, final Handler handler, boolean isNewGame) {
            timer = getTimer(gameTime, handler);
            if (isNewGame) {
                GameService.savedViewIndex = -1;
            }
            timer.start();
        }

        // 暂停
        public static void pause() {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }

        // 恢复
        public static void play(Handler handler) {
            if (BuildConfig.DEBUG) Log.d("GameTimer", "正在恢复倒计时...");
            start(saveMillisUntilFinished, handler, false);
        }

        // 取消计时器
        public static void cancel() {
            saveMillisUntilFinished = -2;
            timer.cancel();
        }
    }
    //------------------------------------------------------------

    public void handleGameOver(boolean isSuccess) {
        ((GameFragment) fragmentArr[1]).handleGameResultPage(isSuccess);
    }

    /**
     * 只能放在这里, 不要移动到GameFragment中
     *
     * @param remainTime 当前游戏还剩多长时间
     */
    public void handleCountDownText(int remainTime) {
        ((GameFragment) fragmentArr[1]).mCountdownTextView.setText(("剩余: " + remainTime + "秒"));
    }

    //------------------------------------------------------------
    private long pressBackCache;
    Fragment[] fragmentArr;
//    ArrayList<Fragment> fragmentArr = new ArrayList<>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        bgMusic = new

                BackgroundMusic();

        initPage();

        changePage(0);
    }

    private void initPage() {
        fragmentArr = new Fragment[4];
        fragmentArr[0] = new StartFragment();
        fragmentArr[2] = new AboutFragment();
        fragmentArr[3] = new RuleFragment();
    }


    public void changePage(int index) {
        if (index == 0) {
            bgMusic.stopBackgroundMusic();
        }
        if (index == 1) {
            fragmentArr[1] = new GameFragment();
        }

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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = getIntent().getAction();

        if (BuildConfig.DEBUG) Log.d("GameActivity", "进入onNewIntent方法.... action:" + action);
//        if(SharedData.INTENT_TO_START.equals(action))
        // 在这里进行 "重新进入app时, 自动继续上局游戏"
        changePage(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fragmentArr[1] != null) {
            fragmentArr[1].onPause();   // 间接使用  暂停游戏 方法
        }
    }
}
