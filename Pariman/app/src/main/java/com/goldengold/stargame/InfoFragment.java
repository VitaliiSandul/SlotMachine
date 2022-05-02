package com.goldengold.stargame;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class InfoFragment extends Fragment {
    public static WebView wv;
    public InfoFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View infoView = inflater.inflate(R.layout.fragment_info, container, false);

        wv = (WebView) infoView.findViewById(R.id.wv);

        wv.setWebViewClient(new WebViewClient());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl("https://sites.google.com/view/prpolic");

        return infoView;
    }
}