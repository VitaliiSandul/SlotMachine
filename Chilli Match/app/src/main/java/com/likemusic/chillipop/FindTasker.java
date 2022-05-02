package com.likemusic.chillipop;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import bolts.AppLinks;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FindTasker extends AsyncTask<Void, Void, Void> {

    private Document docum = null;
    private String targetUrl;
    private String portionUri = "";
    private String siteStr = "tra-ta-ta";
    private Intent intent;
    private Uri deepUri;

    public FindTasker() { }

    public FindTasker(String url) {
        targetUrl = url;
        intent = new Intent();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            docum = Jsoup.connect(targetUrl).get();
            deepUri = getDeepUri();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
//            //For checking uncomment
//            String stringUri = "myapp://Kissssa//ME3E//PARR//MIRRR//GEAA";
//            deep = Uri.parse(stringUri);

            setPortionUri(deepUri);
        } catch (Exception e) {
        }
        return null;
    }

    public String getPortionUri() {
        return portionUri;
    }

    private void setPortionUri(Uri str) {
        String[] arr = str.toString().split("://");
        portionUri = arr[1];
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (docum != null) {
            siteStr = docum.toString();
        }

        try {
            MainActivity.getInstance().findFragment();
        } catch (Exception ignored) {
        }
    }

    public String getSiteStr() {
        return siteStr;
    }

    private Uri getDeepUri() {
        Uri tarUri = AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), intent);
        return tarUri;
    }

}
