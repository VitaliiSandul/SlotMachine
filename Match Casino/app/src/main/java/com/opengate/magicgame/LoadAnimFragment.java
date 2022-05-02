package com.opengate.magicgame;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LoadAnimFragment extends Fragment {

    private WebView animator;
    public LoadAnimFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_load_anim, container, false);
        animator = (WebView) v.findViewById(R.id.animator);

        animator.setWebViewClient(new WebViewClient());
        animator.getSettings().setJavaScriptEnabled(true);
        animator.loadUrl("file:///android_asset/yellow_loading.gif");
        return v;
    }
}