package com.littlefly.multifly;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class HtmlJSInterface {
    public static FlyTasker flyTasker;

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processHTML(String htmlStr) {
        Log.d("result HTML", htmlStr);
        Log.d("result HTML", String.valueOf(htmlStr.length()));

        flyTasker = new FlyTasker(htmlStr);
        flyTasker.execute();
    }
}
