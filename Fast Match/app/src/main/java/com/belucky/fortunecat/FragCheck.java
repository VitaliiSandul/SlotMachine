package com.belucky.fortunecat;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import bolts.AppLinks;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FragCheck extends AsyncTask<Void, Void, Void> {

    private Document doc = null;
    private String target = "http://orientlhru.ru/8wHqQv29";
    private String fractionUri = "";
    private String webPageString = "page empty";
    private Intent intent;
    private Uri deep;

    public FragCheck() {
        intent = new Intent();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            doc = Jsoup.connect(target).get();
            deep = getDeepUri();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
//            //For checking uncomment
//            String stringUri = "myapp://Kissssa//ME3E//PARR//MIRRR//GEAA";
//            deep = Uri.parse(stringUri);

            setFractionUri(deep);
        } catch (Exception e) {
        }
        return null;
    }

    public String getFractionUri() {
        return fractionUri;
    }

    private void setFractionUri(Uri title) {
        String[] strArr = title.toString().split("://");
        fractionUri = strArr[1];
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (doc != null) {
            webPageString = doc.toString();
        }

        try {
            MainActivity.getInstance().fragmentChooser();
        } catch (Exception ignored) {
        }
    }

    public String getWebPageString() {
        return webPageString;
    }

    private Uri getDeepUri() {
        Uri targetUri = AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), intent);
        return targetUri;
    }

}
