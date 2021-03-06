package com.example.piyush.rhythm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by piyush on 22/4/18.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.Songholder> {
    private ArrayList<Songinfo> songs=new ArrayList<Songinfo>();
    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public SongAdapter(Context context,ArrayList<Songinfo>songs){
        this.context=context;
        this.songs=songs;
    }

    public interface OnItemClickListener {
        void onItemClick(Button b,View v,Songinfo obj,int position);
    }
    public void setOnItemClickListener(OnItemClickListener mItemClickListener){
        this.mOnItemClickListener=mItemClickListener;
    }

    @Override
    public Songholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myview = LayoutInflater.from(context).inflate(R.layout.row_song,parent,false);
        return new Songholder(myview);
    }

    @Override
    public void onBindViewHolder(final Songholder holder, final int position) {
        final Songinfo c =songs.get(position);
        holder.songname.setText(c.getSongname());
        holder.artistname.setText(c.getArtistname());
        holder.btnaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener!=null)
                    mOnItemClickListener.onItemClick(holder.btnaction,v,c,position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class Songholder extends RecyclerView.ViewHolder {
        TextView songname,artistname;
        Button btnaction;

        public Songholder(View itemView) {
            super(itemView);
            songname = (TextView)itemView.findViewById(R.id.songname);
            artistname = (TextView)itemView.findViewById(R.id.artistname);
            btnaction = (Button) itemView.findViewById(R.id.btnaction);
        }
    }
}
