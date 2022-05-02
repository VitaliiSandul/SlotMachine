package com.blackpuck.icehockey;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import bolts.AppLinks;
import static com.facebook.FacebookSdk.getApplicationContext;

public class HockeyTasker extends AsyncTask<Void, Void, Void> {

    private Intent intent;
    private Uri fbDeepUri;
    private String webHockeyStr = "hockey puck";
    private String sectionUri = "";


    public HockeyTasker(String html) {
        webHockeyStr = html;
        intent = new Intent();
    }

    @Override
    protected Void doInBackground(Void... params) {
        fbDeepUri = getFbDeepUri();

        try {
//            //For checking uncomment
//            String stringUri = "myapp://Kissssa//ME3E//PARR//MIRRR//GEAA";
//            dpUri = Uri.parse(stringUri);

            setSectionUri(fbDeepUri);
        } catch (Exception e) {
        }
        return null;
    }
    private Uri getFbDeepUri() {
        Uri fbUri = AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), intent);
        return fbUri;
    }

    public String getWebHockeyStr() {
        return webHockeyStr;
    }

    public String getSectionUri() {
        return sectionUri;
    }

    private void setSectionUri(Uri str) {
        String[] tmp = str.toString().split("://");
        sectionUri = tmp[1];
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        try {
            MainActivity.getInstance().hockeyChoice();
        } catch (Exception ignored) {
        }
    }
}
