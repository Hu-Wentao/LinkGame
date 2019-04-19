package com.example.linkgame.activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.linkgame.BuildConfig;
import com.example.linkgame.R;
import com.example.linkgame.fragments.GameFragment;
import com.example.linkgame.fragments.RankFragment;
import com.example.linkgame.fragments.StartFragment;

public class GameActivity extends AppCompatActivity {
    private long canQuitActivity;

    Fragment[] fragmentArr ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initPage();
        changePage(0);
//        initView();
    }

    private void initPage(){
        fragmentArr = new Fragment[]{new StartFragment(),
                new GameFragment(),
                new RankFragment()
        };
    }


    public void changePage(int index){
        Fragment fragment = fragmentArr[index];

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout_main, fragment)
                .commit();
    }



    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - canQuitActivity > 400) {
            canQuitActivity = System.currentTimeMillis();
            Toast.makeText(this, "双击返回键退出App", Toast.LENGTH_SHORT).show();
        } else {
            this.finish();
        }
    }


}
