package com.stomhong.test.play;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.stomhong.test.R;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IJKPlayerActivity extends AppCompatActivity {

    IjkMediaPlayer mPlayer;
    SurfaceView mSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijkplayer);

        init();
    }

    private void init() {
        mSurfaceView = findViewById(R.id.surfaceView);
        mPlayer = new IjkMediaPlayer();
        String videoUrl = "android.resource://" + getPackageName() + "/" + R.raw.wechat;
        try {
            mPlayer.setDataSource("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        mPlayer.prepareAsync();
        mPlayer.start();
    }
}
