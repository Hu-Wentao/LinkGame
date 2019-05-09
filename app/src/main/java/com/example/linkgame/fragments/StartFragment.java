package com.example.linkgame.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.linkgame.BuildConfig;
import com.example.linkgame.R;
import com.example.linkgame.activities.GameActivity;
import com.example.linkgame.activities.LoginActivity;
import com.example.linkgame.db.SharedData;
import com.example.linkgame.game.GameService;

public class StartFragment extends Fragment implements View.OnClickListener{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_start, container, false);
        initView(v);

        return v;
    }
    private void initView(View v){
        v.findViewById(R.id.btn_gameType1).setOnClickListener(this);
        v.findViewById(R.id.btn_gameType2).setOnClickListener(this);

        v.findViewById(R.id.btn_about).setOnClickListener(this);
        v.findViewById(R.id.btn_logout).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Message t = Message.obtain();

        switch (v.getId()) {

            case R.id.btn_about:
                ((GameActivity)getActivity()).changePage(2);
                return;
            case R.id.btn_gameType2:
                t.what = GameActivity.MSG_WHAT_START_NEW_GAME;
                t.arg1 = GameService.STYLE_FILL;
                break;
            case R.id.btn_gameType1:
                t.what = GameActivity.MSG_WHAT_START_NEW_GAME;
                t.arg1 = GameService.STYLE_HORIZONTAL;
                break;
            case R.id.btn_logout:
                // 退出账号
                SharedData.setCurrentAccount(null);
//                // 切换Activity
//                startActivity(new Intent(getActivity(), LoginActivity.class));
                // 改为直接退出游戏
                getActivity().finish();
                return;
            default:
                if (BuildConfig.DEBUG) Log.d("swR+GameActivity", "未处理的点击事件...");
        }
        ((GameActivity)getActivity()).mGameHandler.sendMessage(t);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
