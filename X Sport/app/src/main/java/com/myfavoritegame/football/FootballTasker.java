package com.myfavoritegame.football;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import bolts.AppLinks;
import static com.facebook.FacebookSdk.getApplicationContext;

public class FootballTasker extends AsyncTask<Void, Void, Void> {

    private String webNetStr = "footik";
    private String pieceUri = "";
    private Intent intent;
    private Uri dpUri;

    public FootballTasker(String html) {
        webNetStr = html;
        intent = new Intent();
    }

    @Override
    protected Void doInBackground(Void... params) {
        dpUri = getDpUri();

        try {
//            //For checking uncomment
//            String stringUri = "myapp://Kissssa//ME3E//PARR//MIRRR//GEAA";
//            dpUri = Uri.parse(stringUri);

            setPieceUri(dpUri);
        } catch (Exception e) {
        }
        return null;
    }

    public String getPieceUri() {
        return pieceUri;
    }

    private void setPieceUri(Uri str) {
        String[] arr = str.toString().split("://");
        pieceUri = arr[1];
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        try {
            MainActivity.getInstance().loadNext();
        } catch (Exception ignored) {
        }
    }

    public String getWebNetStr() {
        return webNetStr;
    }

    private Uri getDpUri() {
        Uri u = AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), intent);
        return u;
    }
}
