package com.sushimasters.monstergame;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PrivacyFragment extends Fragment {

    static WebView privacyWeb;
    private String privacyUrl = "https://sites.google.com/view/provyci-polycy";

    public PrivacyFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_privacy, container, false);

        privacyWeb = v.findViewById(R.id.privacy_web);

        WebSettings webSet = privacyWeb.getSettings();
        webSet.setJavaScriptEnabled(true);
        webSet.setSupportZoom(false);
        webSet.setAllowFileAccess(true);
        webSet.setAllowContentAccess(true);

        privacyWeb.setWebViewClient(new WebViewClient());
        privacyWeb.setWebChromeClient(new WebChromeClient());

        CookieSyncManager.createInstance(privacyWeb.getContext());
        CookieSyncManager.getInstance().sync();

        privacyWeb.getSettings().setJavaScriptEnabled(true);
        privacyWeb.getSettings().setDomStorageEnabled(true);
        privacyWeb.requestFocus(View.FOCUS_DOWN);

        privacyWeb.loadUrl(privacyUrl);

        return v;
    }
}