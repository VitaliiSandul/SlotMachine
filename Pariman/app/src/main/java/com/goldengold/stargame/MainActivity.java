package com.goldengold.stargame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {

    private static final String ONESIGNAL_APP_ID = "8a7d36a3-1bdf-4cf8-9f5e-036292f587b9";
    private WebView mobView;
    private static MainActivity instance;
    private String finUrl = "http://digestermn.ru/pVjC93Bq";
    private StarTask st = new StarTask();
    private boolean starWay = false;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;
    public static final int REQUEST_SELECT_FILE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideNavigationButtons();
        mobView = (WebView) findViewById(R.id.mobView);
        loadStartGif();
        setMobView();
        instance = this;

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();



        st.execute();
    }

    @Override
    public void onBackPressed() {
        if (starWay) {
            if(mobView.canGoBack()){
                mobView.goBack();
            }
            else {
                super.onBackPressed();
            }
        }
        else if (getSupportFragmentManager().findFragmentByTag("infotag") != null && !starWay) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Fragment frag = new StarFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.star_container, frag, "startag");
            fragmentTransaction.commit();
        }
        else
            super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationButtons();
    }

    private void hideNavigationButtons() {
        View dec = getWindow().getDecorView();
        dec.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void loadStartGif(){


        mobView.setWebViewClient(new WebViewClient());
        mobView.setWebChromeClient(new WebChromeClient());
        CookieSyncManager.createInstance(mobView.getContext());
        CookieSyncManager.getInstance().sync();
        mobView.getSettings().setJavaScriptEnabled(true);
        mobView.getSettings().setDomStorageEnabled(true);
        mobView.requestFocus(View.FOCUS_DOWN);

        mobView.loadUrl("file:///android_asset/anim.gif");
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public void startWay(){
        String tmpStr = "";
        try {
            tmpStr = st.getWebString().split("<body>")[1].split("</body>")[0].replaceAll("\\s+", "");
        } catch (Exception ex) {
        }

        if (tmpStr.equals("kulmoli") || tmpStr.length() > 43) {
            starWay = true;


            if (!st.getPartUri().equals("")) {
                finUrl += "?subId1=TETRIS&subId2=TETRIS&subId3=TETRIS&subId4=TETRIS&subId5=TETRIS";
                String strmas[] = st.getPartUri().split("//");

                for (int i = 0; i < strmas.length; i++) {
                    finUrl = finUrl.replaceFirst("TETRIS", strmas[i]);
                }
                finUrl = finUrl.replaceAll("TETRIS", "");

            }
            Log.d("--- finUrl ---", finUrl);

            mobView.loadUrl(finUrl);

        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Fragment fr = new StarFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.star_container, fr, "startag");
            fragmentTransaction.commit();
        }
    }

    private void setMobView(){
        WebSettings mWebSettings = mobView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(false);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowContentAccess(true);

        mobView.setWebViewClient(new WebViewClient());
        mobView.setWebChromeClient(new WebChromeClient());

        CookieSyncManager.createInstance(mobView.getContext());
        CookieSyncManager.getInstance().sync();

        mobView.getSettings().setJavaScriptEnabled(true);
        mobView.getSettings().setDomStorageEnabled(true);
        mobView.requestFocus(View.FOCUS_DOWN);

        mobView.setWebChromeClient(new WebChromeClient()
        {
            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            // For Lollipop 5.0+ Devices
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                try
                {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e)
                {
                    uploadMessage = null;
                    Toast.makeText(getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
            {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }
}