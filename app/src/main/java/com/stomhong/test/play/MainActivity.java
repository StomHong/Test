package com.stomhong.test.play;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.stomhong.test.R;

public class MainActivity extends AppCompatActivity {

    VideoView mVideoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mVideoView = findViewById(R.id.videoView);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.start();
            }
        });
        String videoUrl = "android.resource://" + getPackageName() + "/" + R.raw.wechat;

//        mVideoView.setVideoPath(videoUrl);
        mVideoView.setVideoPath("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4");
        mVideoView.start();
    }
}
