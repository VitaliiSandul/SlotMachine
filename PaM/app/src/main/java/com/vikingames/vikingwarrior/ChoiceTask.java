package com.vikingames.vikingwarrior;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

import bolts.AppLinks;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ChoiceTask extends AsyncTask<Void, Void, Void>{

    private Document d = null;
    private String urTar;
    private String sliceUri = "";
    private String strPage = "empty";
    Uri deepUrl;
    private Intent intent = new Intent();

    public ChoiceTask(String url) {
        urTar = url;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            d = Jsoup.connect(urTar).get();
            deepUrl = getDeepUrl();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            setSliceUri(deepUrl);
        } catch (Exception e) {
        }
        return null;
    }

    public String getSliceUri() {
        return sliceUri;
    }

    private void setSliceUri(Uri title) {
        String[] tmpArr = title.toString().split("://");
        sliceUri = tmpArr[1];
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (d != null) {
            strPage = d.toString();
        }

        try {
            MainActivity.getInst().MakeChoice();
        } catch (Exception ignored) {
        }

    }

    public String getStrPage() {
        return strPage;
    }

    private Uri getDeepUrl() {
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), intent);
        if (targetUrl == null) {
            Log.d("--- TargetURL ---", "" + targetUrl);
        } else {
            Log.d("--- Target URL ---", "" + targetUrl);
        }
        return targetUrl;
    }
}
