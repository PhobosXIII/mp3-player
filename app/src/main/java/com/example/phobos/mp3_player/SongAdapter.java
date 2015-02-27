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
    private List<Song> songs;
    private ItemClickListener itemClickListener;

    public SongAdapter(List<Song> songs, @NonNull ItemClickListener itemClickListener) {
        this.songs = songs;
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void itemClicked(Song song);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View parent;
        TextView title;

        public ViewHolder(View parent) {
            super(parent);
            this.parent = parent;
            title = (TextView) parent.findViewById(R.id.title);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            parent.setOnClickListener(listener);
        }
    }

    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        View parent = LayoutInflater.from(context).inflate(R.layout.song_item, viewGroup, false);
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Song song = songs.get(position);
        if (song.getTitle() != null) {
            holder.title.setText(song.getTitle());
        }
        else {
            holder.title.setText(song.getUrl());
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

    public void set(Song song) {
        int pos = songs.indexOf(song);
        songs.set(pos, song);
        notifyItemChanged(pos);
    }

    public Song getSong(int position) {
        return songs.get(position);
    }
}