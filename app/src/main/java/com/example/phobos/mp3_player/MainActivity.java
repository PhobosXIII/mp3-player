package com.example.phobos.mp3_player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements SongAdapter.ItemClickListener {
        private ProgressBar progressBar;
        private RecyclerView list;
        private FloatingActionButton fab;
        private MediaPlayer mediaPlayer;

        private BroadcastReceiver listReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<String> urls = intent.getStringArrayListExtra(DownloadService.LIST);
                ArrayList<Song> songs = new ArrayList<>();
                for (String url : urls) {
                    songs.add(new Song(url));
                }
                populate(songs);
            }
        };

        private BroadcastReceiver songReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String fileName = intent.getStringExtra(DownloadService.FILE_NAME);
                String songUrl = intent.getStringExtra(DownloadService.FILE_URL);
                String songTitle = intent.getStringExtra(DownloadService.FILE_TITLE);
                Song song = new Song(songUrl, fileName, songTitle);
                ((SongAdapter)list.getAdapter()).set(song);

            }
        };

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            list = (RecyclerView) view.findViewById(R.id.list);
            fab = (FloatingActionButton) view.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer.isPlaying()) {
                        fab.setIcon(R.drawable.ic_play_arrow_white_24dp);
                        mediaPlayer.reset();
                    }
                    else {
                        fab.setIcon(R.drawable.ic_stop_white_24dp);
                        Song song = ((SongAdapter)list.getAdapter()).getSong(0);
                        play(song);
                    }
                }
            });
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            DownloadService.downloadList(getActivity());
        }

        @Override
        public void onStart() {
            super.onStart();
            mediaPlayer = new MediaPlayer();
        }

        @Override
        public void onResume() {
            super.onResume();
            IntentFilter ifList = new IntentFilter(DownloadService.ACTION_GET_LIST);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listReceiver, ifList);
            IntentFilter ifSong = new IntentFilter(DownloadService.ACTION_DOWNLOAD_SONG);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(songReceiver, ifSong);
            DownloadService.getList(getActivity());
        }

        @Override
        public void onPause() {
            super.onPause();
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listReceiver);
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(songReceiver);
        }

        @Override
        public void onStop() {
            super.onStop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        @Override
        public void itemClicked(Song song) {
            play(song);
            if (mediaPlayer.isPlaying()) {
                fab.setIcon(R.drawable.ic_stop_white_24dp);
            }
            else {
                fab.setIcon(R.drawable.ic_play_arrow_white_24dp);
            }
        }

        private void populate(List<Song> songs) {
            progressBar.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            list.setLayoutManager(layoutManager);
            list.setItemAnimator(new DefaultItemAnimator());
            DividerItemDecoration divider = new DividerItemDecoration(getResources()
                    .getDrawable(R.drawable.list_divider));
            list.addItemDecoration(divider);
            SongAdapter adapter = new SongAdapter(songs, this);
            list.setAdapter(adapter);

            for(Song song : songs) {
                DownloadService.downloadSong(getActivity(), song.getUrl());
            }
        }

        private void play(Song song) {
            mediaPlayer.reset();
            Uri uri = Uri.parse(DownloadService.getSongPath(song.getFileName()));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(getActivity(), uri);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
        }
    }
}
