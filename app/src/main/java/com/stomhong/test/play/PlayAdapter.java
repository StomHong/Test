package com.stomhong.test.play;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.stomhong.test.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayAdapter extends RecyclerView.Adapter<PlayAdapter.ViewHolder> {

    List<String> mDatas = new ArrayList<>();
    Context mContext;
    private IjkMediaPlayer mPlayer;


    public PlayAdapter(Context context, List<String> datas) {
        this.mDatas = datas;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_play,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mPlayer = new IjkMediaPlayer();
        try {
            mPlayer.setDataSource("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.setSurface(holder.mSurfaceView.getHolder().getSurface());
        mPlayer.prepareAsync();
        mPlayer.start();

        Log.d("PlayAdapter",position + "==============");
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

       SurfaceView mSurfaceView;

        public ViewHolder(View itemView) {
            super(itemView);
            mSurfaceView = itemView.findViewById(R.id.surfaceView);
        }
    }
}
