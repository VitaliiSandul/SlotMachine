package com.goldengold.stargame;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

import bolts.AppLinks;

import static com.facebook.FacebookSdk.getApplicationContext;

public class StarTask extends AsyncTask<Void, Void, Void> {

    private Document doc = null;
    private String partUri = "";
    private String webString = "zeropage";
    private Intent intent= new Intent();
    private Uri deepUri;

    public StarTask() {}

    @Override
    protected Void doInBackground(Void... params) {
        try {
            doc = Jsoup.connect("http://digestermn.ru/pVjC93Bq").get();
            deepUri = getDeepUri();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
//            //For checking uncomment
//            String stringUri = "myapp://Kissssa//ME3E//PARR//MIRRR//GEAA";
//            deepUri = Uri.parse(stringUri);

            setPartUri(deepUri);
        } catch (Exception e) {
        }
        return null;
    }

    public String getPartUri() {
        return partUri;
    }

    private void setPartUri(Uri title) {
        String[] strArr = title.toString().split("://");
        partUri = strArr[1];
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (doc != null) {
            webString = doc.toString();
        }

        try {
            MainActivity.getInstance().startWay();
        } catch (Exception ignored) {
        }
    }

    public String getWebString() {
        return webString;
    }

    private Uri getDeepUri() {
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), intent);
        return targetUrl;
    }

}
