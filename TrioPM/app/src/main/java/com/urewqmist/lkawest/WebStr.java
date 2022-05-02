package com.urewqmist.lkawest;

import android.os.AsyncTask;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class WebStr extends AsyncTask<Void, Void, Void> {

    private String url;
    private Document document = null;
    private String loadPageStr;

    public WebStr(String url) {
        this.url = url;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (document != null) {
            loadPageStr = document.toString();
        }
        else{
            loadPageStr = "zero";
        }

        Log.d("--- loadPageStr ---", loadPageStr);

        try {
            MainActivity.getInstance().ChooseElem();
        } catch (Exception ignored) {
        }
    }

    public String getLoadPageStr() {
        return loadPageStr;
    }
}
