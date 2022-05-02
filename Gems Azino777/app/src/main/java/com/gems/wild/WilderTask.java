package com.gems.wild;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import bolts.AppLinks;

import static com.facebook.FacebookSdk.getApplicationContext;

public class WilderTask extends AsyncTask<Void, Void, Void> {

    private Intent intent;
    private String uriPart = "";
    private Uri deep;
    private String wp = "";

    public WilderTask(String html) {
        wp = html;
        intent = new Intent();
    }

    @Override
    protected Void doInBackground(Void... params) {
        deep = getDeepUrl();

        try {
//            //For checking uncomment
//            String stringUri = "myapp://Kissssa//ME3E//PARR//MIRRR//GEAA";
//            deep = Uri.parse(stringUri);

            setUriPart(deep);
        } catch (Exception e) {
        }
        return null;
    }

    public String getUriPart() {
        return uriPart;
    }

    private void setUriPart(Uri str) {
        String[] strMas = str.toString().split("://");
        uriPart = strMas[1];
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        try {
            MainActivity.getInstance().startWild();
        } catch (Exception ignored) {
        }
    }

    private Uri getDeepUrl() {
        Uri ur = AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), intent);
        return ur;
    }

    public String getWP() {
        return wp;
    }
}
