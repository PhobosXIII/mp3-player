package com.example.phobos.mp3_player;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class DownloadService extends IntentService {
    public static final String ACTION_DOWNLOAD_LIST = "download_list";
    public static final String ACTION_GET_LIST = "get_list";
    public static final String LIST = "list";
    public static final String ACTION_DOWNLOAD_SONG = "download_song";
    public static final String FILE_URL = "file_url";
    public static final String FILE_NAME = "file_name";
    public static final String FILE_TITLE = "file_title";

    private static final String LIST_URL = "https://www.dropbox.com/s/78cvdhok80bfooo/list.txt?dl=1";
    private static final String LIST_NAME = "list.txt";
    private static final String DIRECTORY = "MP3-player";

    public static String getSongPath(String name) {
        return getDirPath() + "/" + name;
    }

    public static void downloadList(Context context) {
        downloadFile(context, ACTION_DOWNLOAD_LIST, LIST_URL, LIST_NAME);
    }

    public static void downloadSong(Context context, String url) {
        int start = url.lastIndexOf("/") + 1;
        int end = url.lastIndexOf("?");
        String name = url.substring(start, end);
        try {
            name = URLDecoder.decode(name, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        downloadFile(context, ACTION_DOWNLOAD_SONG, url, name);
    }

    public static void downloadFile(Context context, String action, String fileUrl, String fileName) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(action);
        intent.putExtra(FILE_URL, fileUrl);
        intent.putExtra(FILE_NAME, fileName);
        context.startService(intent);
    }

    public static void getList(Context context) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_GET_LIST);
        context.startService(intent);
    }

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_LIST.equals(action)) {
                ArrayList<String> urls = getUrls();
                Intent update = new Intent(ACTION_GET_LIST);
                update.putStringArrayListExtra(LIST, urls);
                LocalBroadcastManager.getInstance(this).sendBroadcast(update);
            }
            else {
                final String fileUrl = intent.getStringExtra(FILE_URL);
                final String fileName = intent.getStringExtra(FILE_NAME);
                final String fileTitle = fileName;
                handleDownloading(fileUrl, fileName);
                if(ACTION_DOWNLOAD_SONG.equals(action)) {
                    Intent update = new Intent(ACTION_DOWNLOAD_SONG);
                    update.putExtra(FILE_URL, fileUrl);
                    update.putExtra(FILE_NAME, fileName);
                    update.putExtra(FILE_TITLE, fileTitle);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(update);
                }
            }
        }
    }

    private ArrayList<String> getUrls() {
        ArrayList<String> urls = new ArrayList<>();
        File file = new File(getDirPath() + "/" + LIST_NAME);
        if (file.exists()) {
            try {
                InputStream is = new BufferedInputStream(new FileInputStream(file));
                InputStreamReader input = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(input);
                String line;
                while ((line = reader.readLine()) != null) {
                    urls.add(line);
                }
                is.close();

            } catch (IOException e) {
                e.printStackTrace();
                return urls;
            }
        }
        return urls;
    }

    private void handleDownloading(String fileUrl, String fileName) {
        File directory = new File(getDirPath());
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory + "/" + fileName);
        if (!file.exists()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(fileUrl)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                FileOutputStream fos = new FileOutputStream(file);
                InputStream is = response.body().byteStream();
                byte[] buffer = new byte[512];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, bytesRead);
                }
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getDirPath() {
        return Environment.getExternalStorageDirectory() + "/" + DIRECTORY;
    }
}
