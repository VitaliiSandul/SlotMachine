package com.sushimasters.monstergame;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class JSInterface {
    public static SushiTask tasker;

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processHTML(String htm) {
        tasker = new SushiTask(htm);
        tasker.execute();
    }
}