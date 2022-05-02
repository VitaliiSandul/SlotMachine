package com.parimatch.imagineluck;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class JSHInterface {
    public static MaryTasker tasker;

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processHTML(String html) {
        tasker = new MaryTasker(html);
        tasker.execute();
    }
}
