package com.likemusic.chillipop;

import android.os.Handler;
import android.os.Looper;

public class UtilsHelp {
    public static void runOnUiThread(Runnable runnable){
        final Handler UIHandler = new Handler(Looper.getMainLooper());
        UIHandler.post(runnable);
    }
}
