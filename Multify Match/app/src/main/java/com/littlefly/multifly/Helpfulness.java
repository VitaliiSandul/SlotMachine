package com.littlefly.multifly;

import android.os.Handler;
import android.os.Looper;

public class Helpfulness {
    public static void runOnUiThread(Runnable runnable){
        final Handler UIHandler = new Handler(Looper.getMainLooper());
        UIHandler.post(runnable);
    }
}
