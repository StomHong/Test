package com.stomhong.test.play;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;
import com.stomhong.test.R;

public class ExoPlayerActivity extends AppCompatActivity {

    SimpleExoPlayer mSimpleExoPlayer;
    PlayerView mPlayerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer);
        init();
    }

    private void init() {
        mPlayerView = findViewById(R.id.playerView);
        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        mPlayerView.setPlayer(mSimpleExoPlayer);

        String videoUrl = "android.resource://" + getPackageName() + "/" + R.raw.wechat;

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Test"));
// This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(RawResourceDataSource.buildRawResourceUri(R.raw.wechat));
// Prepare the player with the source.
        mSimpleExoPlayer.prepare(videoSource);
        mSimpleExoPlayer.setPlayWhenReady(true);

        mSimpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSimpleExoPlayer.release();
    }
}
