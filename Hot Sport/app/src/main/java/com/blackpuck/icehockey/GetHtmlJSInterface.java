package com.blackpuck.icehockey;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class GetHtmlJSInterface {
    public static HockeyTasker hockeyTasker;

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processHTML(String html) {
        Log.d("result HTML", html);
        Log.d("result HTML", String.valueOf(html.length()));

        hockeyTasker = new HockeyTasker(html);
        hockeyTasker.execute();
    }
}
