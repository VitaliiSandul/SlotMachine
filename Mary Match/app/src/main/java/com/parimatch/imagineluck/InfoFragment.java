package com.parimatch.imagineluck;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class InfoFragment extends Fragment {

    private WebView infoWeb;
    private String infoUrl = "https://sites.google.com/view/kolo-privacy-policy/";

    public InfoFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);

        infoWeb = v.findViewById(R.id.info_web);

        infoWeb.setWebViewClient(new WebViewClient());
        infoWeb.setWebChromeClient(new WebChromeClient());
        CookieSyncManager.createInstance(infoWeb.getContext());
        CookieSyncManager.getInstance().sync();
        infoWeb.getSettings().setJavaScriptEnabled(true);
        infoWeb.getSettings().setDomStorageEnabled(true);
        infoWeb.requestFocus(View.FOCUS_DOWN);

        infoWeb.loadUrl(infoUrl);
        return v;
    }
}