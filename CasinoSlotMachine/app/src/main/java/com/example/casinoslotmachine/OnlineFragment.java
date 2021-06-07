package com.example.casinoslotmachine;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OnlineFragment extends Fragment {

    private WebView mWebView;
    private String url = "https://www.pinup.casino/ru/?lang=lang&st=j3lkf5wp&s1=&s2=&s3=&s4=&s5=&source=https://pinup-casino.net.ua/&pc=30&options=%7Boptions%7D&form_key=%7B_form_key%7D&trId=c2qedp1ct2h2o4tlktpg&popup=registration";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_online, container, false);
        mWebView = (WebView) v.findViewById(R.id.webview);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());

        mWebView.loadUrl(url);

        return v;
    }
}