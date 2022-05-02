package com.sushimasters.monstergame;

import android.os.Handler;
import android.os.Looper;

public class Helps {
    public static void runOnUiThread(Runnable runnable){
        final Handler UIHandler = new Handler(Looper.getMainLooper());
        UIHandler.post(runnable);
    }
}
