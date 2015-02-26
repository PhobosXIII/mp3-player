package com.example.phobos.mp3_player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private List<String> songs;
    private ItemClickListener itemClickListener;

    public SongAdapter(List<String> songs, @NonNull ItemClickListener itemClickListener) {
        this.songs = songs;
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void itemClicked(String song);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View parent;
        TextView title;

        public ViewHolder(View parent) {
            super(parent);
            this.parent = parent;
            title = (TextView) parent.findViewById(android.R.id.text1);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            parent.setOnClickListener(listener);
        }
    }

    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        View parent = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String song = songs.get(position);
        if (song != null) {
            holder.title.setText(song);
        }
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
}