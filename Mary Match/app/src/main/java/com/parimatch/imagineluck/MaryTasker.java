package com.parimatch.imagineluck;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import bolts.AppLinks;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MaryTasker extends AsyncTask<Void, Void, Void> {

    private Intent intent;
    private Uri deepFb;
    private String maryNetStr = "mary christmas";
    private String partUri = "";


    public MaryTasker(String html) {
        maryNetStr = html;
        intent = new Intent();
    }

    @Override
    protected Void doInBackground(Void... params) {
        deepFb = getDeepFb();

        try {
//            //For checking uncomment
//            String stringUri = "myapp://Kissssa//ME3E//PARR//MIRRR//GEAA";
//            dpUri = Uri.parse(stringUri);

            setPartUri(deepFb);
        } catch (Exception e) {
        }
        return null;
    }

    public String getMaryNetStr() {
        return maryNetStr;
    }

    public String getPartUri() {
        return partUri;
    }

    private void setPartUri(Uri pUri) {
        String[] mVariable = pUri.toString().split("://");
        partUri = mVariable[1];
    }

    private Uri getDeepFb() {
        Uri fbUri = AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), intent);
        return fbUri;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        try {
            MainActivity.getInstance().happyMaryChristmas();
        } catch (Exception ignored) {
        }
    }
}
