package com.sushimasters.monstergame;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import bolts.AppLinks;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SushiTask extends AsyncTask<Void, Void, Void> {

    private String pagePar = "";
    private Intent intent;
    private String kusochekUri = "";
    private Uri deepFace;

    public SushiTask(String html) {
        pagePar = html;
        intent = new Intent();
    }

    @Override
    protected Void doInBackground(Void... params) {
        deepFace = getDeepFaceUri();

        try {
//            //For checking uncomment
//            String stringUri = "myapp://Kissssa//ME3E//PARR//MIRRR//GEAA";
//            deepFace = Uri.parse(stringUri);

            setKusochekUri(deepFace);
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        try {
            MainActivity.getInstance().sushiStart();
        } catch (Exception ignored) {
        }
    }

    private void setKusochekUri(Uri str) {
        String[] temp = str.toString().split("://");
        kusochekUri = temp[1];
    }

    public String getKusochekUri() {
        return kusochekUri;
    }

    private Uri getDeepFaceUri() {
        Uri ur = AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), intent);
        return ur;
    }

    public String getPagePar() {
        return pagePar;
    }
}

