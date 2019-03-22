package com.stomhong.test.play;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.dingmouren.layoutmanagergroup.viewpager.ViewPagerLayoutManager;
import com.stomhong.test.R;

import java.util.ArrayList;
import java.util.List;

public class PlayFullscreenActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    List<String> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_play_fullscreen);
        init();
    }

    private void init() {
       mRecyclerView = findViewById(R.id.recyclerView);

       for (int i = 0;i<10;i++){
           mDatas.add("");
       }
       mAdapter = new PlayAdapter(this,mDatas);
       mRecyclerView.setAdapter(mAdapter);
       mRecyclerView.setLayoutManager(new ViewPagerLayoutManager(this,ViewPagerLayoutManager.VERTICAL));
    }
}
