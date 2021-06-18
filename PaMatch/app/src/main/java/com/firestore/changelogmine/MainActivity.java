package com.firestore.changelogmine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.onesignal.OneSignal;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NetworkChangeReceiver.NetworkReceiverListener {

    BroadcastReceiver broadcastReceiver;
    FragmentTransaction fragmentTransaction;
    private static MainActivity instance;
    private static final String ONESIGNAL_APP_ID = "ef225ffc-9164-487b-88f1-c0db76f918d2";
    private String url = "http://inspirnlfm.ru/DD8GbTy3";
    private ParseText pText = new ParseText(url);
    private boolean web = false;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_main);

        hideNavigation();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new LoadingFragment());
        fragmentTransaction.commit();

        broadcastReceiver = new NetworkChangeReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        NetworkChangeReceiver.networkReceiverListener = this;

        instance = this;

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        pText.execute();
    }

    protected void unregisterNetwork(){
        try{
            unregisterReceiver(broadcastReceiver);
        }
        catch (IllegalArgumentException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("onlinetag") != null && web) {
            if(OnlineFragment.mWebView.canGoBack()){
                OnlineFragment.mWebView.goBack();
            }
            else {
                Fragment fr = new OnlineFragment(url);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fr, "onlinetag");
                fragmentTransaction.commit();
                Log.d("-----","Online");
            }
        }
        else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigation();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigation();
    }

    private void hideNavigation(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetwork();
    }

    @Override
    public void onNetworkConnectionChanged(Boolean isConnected) {
    }

    protected void ChooseFragment() {

        String str = "";
        try {
            str = pText.getLoadedStr().split("<body>")[1].split("</body>")[0].replaceAll("\\s+", "");
            Log.d("--- KeyWord ---", str);
        } catch (Exception ignored) {
        }

        if (str.equals("jordford") || str.length() > 50) {
            web = true;
            Fragment fr = new OnlineFragment();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fr, "onlinetag");
            fragmentTransaction.commit();
            Log.d("-----","Online");
        }
        else {
            Fragment fr = new OfflineFlipperFragment();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fr, "offlinetag");
            fragmentTransaction.commit();
            Log.d("-----","Offline");
        }
    }
}