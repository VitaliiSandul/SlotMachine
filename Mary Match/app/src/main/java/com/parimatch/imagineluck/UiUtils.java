package com.parimatch.imagineluck;

import android.os.Handler;
import android.os.Looper;

public class UiUtils {
    public static void runOnUiThread(Runnable rnbl){
        final Handler UIHandler = new Handler(Looper.getMainLooper());
        UIHandler.post(rnbl);
    }
}
