package com.firestore.changelogmine;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ParseText extends AsyncTask<Void, Void, Void> {

    private String url;
    private Document doc = null;
    private String loadedStr;

    public ParseText(String url) {
        this.url = url;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        Log.d("onPostExecute", "--------------------onPostExecute--------------------");

        if (doc != null) {
            loadedStr = doc.toString();
            Log.d("--- loadedStr ---", loadedStr);
        }
        else{
            loadedStr = "emptypage";
        }

        try {
            MainActivity.getInstance().ChooseFragment();
        } catch (Exception ignored) {
        }

    }

    public String getLoadedStr() {
        return loadedStr;
    }

}
