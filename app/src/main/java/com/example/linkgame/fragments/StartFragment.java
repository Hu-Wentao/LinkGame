package com.example.linkgame.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.linkgame.BuildConfig;
import com.example.linkgame.R;
import com.example.linkgame.activities.GameActivity;

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
        v.findViewById(R.id.btn_mainGame).setOnClickListener(this);
        v.findViewById(R.id.btn_rank).setOnClickListener(this);
//        findViewById(R.id.btn_setting).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_mainGame:
                ((GameActivity)getActivity()).changePage(1);
                break;
            case R.id.btn_rank:
                ((GameActivity)getActivity()).changePage(2);
                break;
//            case R.id.btn_setting:
//                break;
            default:
                if (BuildConfig.DEBUG) Log.d("swR+GameActivity", "未处理的点击事件...");
        }
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
