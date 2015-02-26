package com.example.phobos.mp3_player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private ProgressBar progressBar;

        private BroadcastReceiver downloadListReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                populate();
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
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            DownloadService.downloadList(getActivity());
        }

        @Override
        public void onResume() {
            super.onResume();
            IntentFilter ifActiveRoutes = new IntentFilter(DownloadService.ACTION_LIST);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(downloadListReceiver, ifActiveRoutes);
        }

        @Override
        public void onPause() {
            super.onPause();
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(downloadListReceiver);
        }

        private void populate() {
            progressBar.setVisibility(View.GONE);
        }
    }
}
