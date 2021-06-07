package com.example.casinoslotmachine;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MusicService extends Service {
    //create mediaPlayer instance
    static MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.casinomusic);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.release();
    }
}