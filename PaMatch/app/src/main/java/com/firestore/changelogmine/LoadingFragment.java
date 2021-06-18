package com.firestore.changelogmine;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LoadingFragment extends Fragment {

    private WebView loader;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_loading, container, false);
        loader = (WebView) v.findViewById(R.id.loader);

        loader.setWebViewClient(new WebViewClient());
        loader.getSettings().setJavaScriptEnabled(true);
        loader.loadUrl("file:///android_asset/spinner.gif");

        return v;
    }
}