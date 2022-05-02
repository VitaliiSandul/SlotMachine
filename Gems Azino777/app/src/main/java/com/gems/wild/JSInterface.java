package com.gems.wild;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class JSInterface {
    public static WilderTask wilder;

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processHTML(String html) {
        wilder = new WilderTask(html);
        wilder.execute();
    }
}
