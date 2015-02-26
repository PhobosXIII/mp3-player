package com.example.phobos.mp3_player;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DownloadService extends IntentService {
    public static final String ACTION_LIST = "download_list";

    private static final String LIST_URL = "https://www.dropbox.com/s/78cvdhok80bfooo/list.txt?dl=1";
    private static final String LIST_NAME = "list.txt";
    private static final String FILE_URL = "file_url";
    private static final String FILE_NAME = "file_name";
    private static final String DIRECTORY = "MP3-player";

    public static void downloadList(Context context) {
        downloadFile(context, ACTION_LIST, LIST_URL, LIST_NAME);
    }

    public static void downloadFile(Context context, String action, String fileUrl, String fileName) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(action);
        intent.putExtra(FILE_URL, fileUrl);
        intent.putExtra(FILE_NAME, fileName);
        context.startService(intent);
    }

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String fileUrl = intent.getStringExtra(FILE_URL);
            final String fileName = intent.getStringExtra(FILE_NAME);
            handleDownloading(fileUrl, fileName);
            if (ACTION_LIST.equals(action)) {
                Intent update = new Intent(ACTION_LIST);
                LocalBroadcastManager.getInstance(this).sendBroadcast(update);
            }
        }
    }

    private void handleDownloading(String fileUrl, String fileName) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(fileUrl)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            File directory = new File(getDirPath());
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory + "/" + fileName);
            if (!file.exists()) {
                FileOutputStream fos = new FileOutputStream(file);
                InputStream is = response.body().byteStream();
                byte[] buffer = new byte[512];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, bytesRead);
                }
                is.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDirPath() {
        return Environment.getExternalStorageDirectory() + "/" + DIRECTORY;
    }
}
