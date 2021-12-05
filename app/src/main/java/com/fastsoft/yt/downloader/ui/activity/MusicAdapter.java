package com.fastsoft.yt.downloader.ui.activity;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import java.util.List;
import com.fastsoft.yt.downloader.dl.models.VideoData;
import android.widget.TextView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.fastsoft.yt.downloader.dl.models.MusicFile;

import com.fastsoft.yt.downloader.R;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    Context context;
    List<MusicFile> vd;
    ItemClickInterface ic;

    int lastPosition = -1;
    public MusicAdapter(Context context,List<MusicFile> vd,ItemClickInterface ic){
        this.context = context;
        this.vd = vd;
        this.ic = ic;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup p1, int p2) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,p1,false);
        return new ViewHolder(v);
    }
    private void setAnimation(View viewToAnimate, int position)
    {
          if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.format.setText(vd.get(position).getFormat().toUpperCase());
        holder.kbits.setText(vd.get(position).getKbits());
        setAnimation(holder.itemView,position);

        setClick(holder.itemView,position);

    }
    @Override
    public void onViewDetachedFromWindow(final ViewHolder holder)
    {
        ((ViewHolder)holder).clearAnimation();
    }
    @Override
    public int getItemCount() {
        return vd.size();
    }
    public void setClick(View v,final int position){
        v.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View p1) {

                    if(ic != null){
                        ic.itemClick(vd.get(position).getFormat(),vd.get(position).getKbits(),vd.get(position).getUrl());
                    }
                }

            });
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView format;
        TextView kbits;

        public ViewHolder(View itemView){
            super(itemView);

            format = itemView.findViewById(R.id.list_item_text);
            kbits = itemView.findViewById(R.id.list_item_subtitle);
        }
        public void clearAnimation()
        {
            itemView.clearAnimation();
        }
    }

}

