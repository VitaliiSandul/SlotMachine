package com.littlefly.multifly;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import bolts.AppLinks;
import static com.facebook.FacebookSdk.getApplicationContext;

public class FlyTasker  extends AsyncTask<Void, Void, Void> {

    private Intent intent;
    private String uriPortion = "";
    private Uri deep;
    private String webPage = "";

    public FlyTasker(String html) {
        webPage = html;
        intent = new Intent();
    }

    @Override
    protected Void doInBackground(Void... params) {
        deep = getDeepUr();

        try {
//            //For checking uncomment
//            String stringUri = "myapp://Kissssa//ME3E//PARR//MIRRR//GEAA";
//            deep = Uri.parse(stringUri);

            setUriPortion(deep);
        } catch (Exception e) {
        }
        return null;
    }

    public String getUriPortion() {
        return uriPortion;
    }

    private void setUriPortion(Uri str) {
        String[] strMas = str.toString().split("://");
        uriPortion = strMas[1];
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        try {
            MainActivity.getInstance().startFly();
        } catch (Exception ignored) {
        }
    }

    private Uri getDeepUr() {
        Uri ur = AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), intent);
        return ur;
    }

    public String getWebPage() {
        return webPage;
    }
}
