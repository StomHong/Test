package com.stomhong.test.play;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.dingmouren.layoutmanagergroup.viewpager.OnViewPagerListener;
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
           mDatas.add("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4");
       }
       mAdapter = new PlayAdapter(this,mDatas);
       mRecyclerView.setAdapter(mAdapter);
        ViewPagerLayoutManager  viewPagerLayoutManager = new ViewPagerLayoutManager(this,ViewPagerLayoutManager.VERTICAL);
       mRecyclerView.setLayoutManager(viewPagerLayoutManager);
       viewPagerLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
           @Override
           public void onInitComplete() {

           }

           @Override
           public void onPageRelease(boolean b, int i) {

           }

           @Override
           public void onPageSelected(int i, boolean b) {

           }
       });
    }
}
