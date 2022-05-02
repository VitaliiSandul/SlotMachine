package com.opengate.magicgame;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import bolts.AppLinks;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Checker extends AsyncTask<Void, Void, Void> {

    private String tUrl;
    private String cutUri = "";
    private String pageString = "nothing";
    private Document doc = null;
    Uri uriDeep;
    private Intent intent;

    public Checker(String url) {
        tUrl = url;
        intent = new Intent();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            doc = Jsoup.connect(tUrl).get();
            uriDeep = useDeepUri();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
//            //For checking uncomment
//            String stringUri = "myapp://Kissssa//ME3E//PARR//MIRRR//GEAA";
//            uriDeep = Uri.parse(stringUri);

            setCutUri(uriDeep);
        } catch (Exception e) {
        }
        return null;
    }

    public String getCutUri() {
        return cutUri;
    }

    private void setCutUri(Uri title) {
        String[] mas = title.toString().split("://");
        cutUri = mas[1];
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (doc != null) {
            pageString = doc.toString();
        }

        try {
            MainActivity.getInstance().chooseWay();
        } catch (Exception ignored) {
        }
    }

    public String getPageString() {
        return pageString;
    }

    private Uri useDeepUri() {
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), intent);
        if (targetUrl == null) {
            Log.d("--- Target URL ---", "" + targetUrl);
        } else {
            Log.d("--- Target URL ---", "" + targetUrl);
        }

        return targetUrl;
    }
}
