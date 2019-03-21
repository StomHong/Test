package com.stomhong.test.play;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stomhong.test.R;

import java.util.ArrayList;
import java.util.List;

public class PlayAdapter extends RecyclerView.Adapter<PlayAdapter.ViewHolder> {

    List<String> mDatas = new ArrayList<>();
    Context mContext;

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
        holder.mPositionTv.setText(position + "");
        Log.d("PlayAdapter",position + "==============");
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView mPositionTv;

        public ViewHolder(View itemView) {
            super(itemView);
            mPositionTv = itemView.findViewById(R.id.tv_position);
        }
    }
}
