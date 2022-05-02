package com.myfavoritegame.football;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class HtmlJavaScriptInterface {

    public static FootballTasker footTasker;
    public static String urlWeb= "http://electragsw.ru/9FkF13cB";

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processHTML(String html) {
        Log.d("result HTML", html);
        Log.d("result HTML", String.valueOf(html.length()));

        footTasker = new FootballTasker(html);
        footTasker.execute();
    }
}