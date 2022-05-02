package com.blackpuck.icehockey;

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

    private ImageView spinner;
    private FrameLayout hockeyContainer;
    private WebView hockeyWeb;
    private String hockeyWebStr = "";
    private String hockeyWebUrl = "http://dishoufstz.ru/mD57kMb2";
    private static final String ONESIGNAL_APP_ID = "173f78b9-351c-44ec-b9e1-be233f85e06d";
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;
    public static final int REQUEST_SELECT_FILE = 100;
    private static MainActivity instance;
    private boolean isHockeyWeb = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideNavBarButtons();
        initComponents();
        setSpinner();
        setHockeyWeb();
        instance = this;

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        checkConnection();

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
//        fTransaction.replace(R.id.hockey_container, new HockeyFragment(),"hockeytag");
//        fTransaction.commit();
//
//        spinner.setVisibility(View.GONE);
//        hockeyWeb.setVisibility(View.GONE);
//        hockeyContainer.setVisibility(View.VISIBLE);
    }

    private void setSpinner() {
        spinner.setBackgroundResource(R.drawable.spinner_animation);
        AnimationDrawable loading = (AnimationDrawable) spinner.getBackground();
        loading.start();
        spinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavBarButtons();
    }

    private void initComponents(){
        spinner = findViewById(R.id.spinner);
        hockeyContainer = findViewById(R.id.hockey_container);
        hockeyWeb = findViewById(R.id.hockey_web);
    }

    private void hideNavBarButtons() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void setHockeyWeb() {
        WebSettings hockeyWebSettings = hockeyWeb.getSettings();
        hockeyWebSettings.setJavaScriptEnabled(true);
        hockeyWebSettings.setSupportZoom(false);
        hockeyWebSettings.setAllowFileAccess(true);
        hockeyWebSettings.setAllowContentAccess(true);

        hockeyWeb.setWebViewClient(new WebViewClient());
        hockeyWeb.setWebChromeClient(new WebChromeClient());

        CookieSyncManager.createInstance(hockeyWeb.getContext());
        CookieSyncManager.getInstance().sync();

        hockeyWeb.getSettings().setJavaScriptEnabled(true);
        hockeyWeb.getSettings().setDomStorageEnabled(true);
        hockeyWeb.requestFocus(View.FOCUS_DOWN);

        hockeyWeb.setWebChromeClient(new WebChromeClient()
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

    public static MainActivity getInstance() {
        return instance;
    }

    private void checkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            getHockeyWebHtml();
        }
        else{
            fillHockeyContainer();
        }
    }

    private void getHockeyWebHtml() {
        WebSettings settings = hockeyWeb.getSettings();
        settings.setJavaScriptEnabled(true);
        hockeyWeb.addJavascriptInterface(new GetHtmlJSInterface(), "HTMLOUT");

        hockeyWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                hockeyWeb.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
            }
        });

        hockeyWeb.loadUrl(hockeyWebUrl);
        hockeyWeb.setVisibility(View.GONE);
    }

    public void hockeyChoice(){
        try {
            hockeyWebStr = GetHtmlJSInterface.hockeyTasker.getWebHockeyStr();
        } catch (Exception ex) {
        }

        if (hockeyWebStr.length() > 100) {
            isHockeyWeb = true;

            if (!GetHtmlJSInterface.hockeyTasker.getSectionUri().equals("")) {
                hockeyWebUrl += "?subId1=HOCKEY&subId2=HOCKEY&subId3=HOCKEY&subId4=HOCKEY&subId5=HOCKEY";
                String components[] = GetHtmlJSInterface.hockeyTasker.getSectionUri().split("//");

                for (int i = 0; i < components.length; i++) {
                    hockeyWebUrl = hockeyWebUrl.replaceFirst("HOCKEY", components[i]);
                }
                hockeyWebUrl = hockeyWebUrl.replaceAll("HOCKEY", "");
            }
            Log.d("--- URL ---", hockeyWebUrl);

            spinner.setVisibility(View.GONE);
            hockeyContainer.setVisibility(View.GONE);
            hockeyWeb.setVisibility(View.VISIBLE);
        }
        else {
            fillHockeyContainer();
        }
    }

    private void fillHockeyContainer(){
        hockeyWeb.setVisibility(View.GONE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.add(R.id.hockey_container, new HockeyFragment(),"hockeytag");
        fTransaction.commit();

        spinner.setVisibility(View.GONE);
        hockeyContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (isHockeyWeb && hockeyWeb.canGoBack()) {
            hockeyWeb.goBack();
        }
        else{
            super.onBackPressed();
        }
    }
}