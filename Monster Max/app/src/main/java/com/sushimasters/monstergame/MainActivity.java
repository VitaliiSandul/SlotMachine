package com.sushimasters.monstergame;

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

    private FrameLayout sushiContainer;
    private WebView sushiWeb;
    private ImageView sushiAnimation;
    private static MainActivity instance;
    private static final String ONESIGNAL_APP_ID = "fdaaa5c1-222a-40f1-95f9-bdcb9217ba88";
    private final static int FILECHOOSER_RESULTCODE = 1;
    public static final int REQUEST_SELECT_FILE = 100;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    private boolean isSushiWeb = false;
    private String sushiUrl = "http://tellaputjd.ru/FTRY9ZBZ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavBarButtonsOff();
        InitMainControls();
        sushiMonstersAnimation();
        setSushiWeb();
        instance = this;

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        isNetConnection();
    }

    private void setSushiWeb() {
        WebSettings wSet = sushiWeb.getSettings();
        wSet.setJavaScriptEnabled(true);
        wSet.setSupportZoom(false);
        wSet.setAllowFileAccess(true);
        wSet.setAllowContentAccess(true);

        sushiWeb.setWebViewClient(new WebViewClient());
        sushiWeb.setWebChromeClient(new WebChromeClient());

        CookieSyncManager.createInstance(sushiWeb.getContext());
        CookieSyncManager.getInstance().sync();

        sushiWeb.getSettings().setJavaScriptEnabled(true);
        sushiWeb.getSettings().setDomStorageEnabled(true);
        sushiWeb.requestFocus(View.FOCUS_DOWN);

        sushiWeb.setWebChromeClient(new WebChromeClient()
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


    private void isNetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            getSushiWebHtml();
        }
        else{
            getSushiGame();
        }
    }

    private void getSushiGame() {
        sushiAnimation.setVisibility(View.GONE);
        sushiWeb.setVisibility(View.GONE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.add(R.id.sushi_container, new SushiFragment(),"sushitag");
        fTransaction.commit();
        sushiContainer.setVisibility(View.VISIBLE);
    }

    private void getSushiWebHtml() {
        WebSettings settings = sushiWeb.getSettings();
        settings.setJavaScriptEnabled(true);
        sushiWeb.addJavascriptInterface(new JSInterface(), "HTMLOUT");

        sushiWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                sushiWeb.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
            }
        });

        sushiWeb.loadUrl(sushiUrl);
        sushiWeb.setVisibility(View.GONE);
    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        NavBarButtonsOff();
    }

    private void NavBarButtonsOff() {
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void InitMainControls(){
        sushiContainer = findViewById(R.id.sushi_container);
        sushiWeb = findViewById(R.id.sushi_web);
        sushiAnimation = findViewById(R.id.sushi_animation);
    }

    private void sushiMonstersAnimation() {
        sushiAnimation.setBackgroundResource(R.drawable.sushi_anim);
        AnimationDrawable sushiLoading = (AnimationDrawable) sushiAnimation.getBackground();
        sushiLoading.start();
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public void sushiStart(){
        String sushiWebHtml = "";

        try {
            sushiWebHtml = JSInterface.tasker.getPagePar();
        } catch (Exception ex) {
        }

        if (sushiWebHtml.length() > 85) {
            isSushiWeb = true;

            if (!JSInterface.tasker.getKusochekUri().equals("")) {
                sushiUrl += "?subId1=SUSHI&subId2=SUSHI&subId3=SUSHI&subId4=SUSHI&subId5=SUSHI";
                String temps[] = JSInterface.tasker.getKusochekUri().split("//");

                for (int k = 0; k < temps.length; k++) {
                    sushiUrl = sushiUrl.replaceFirst("SUSHI", temps[k]);
                }
                sushiUrl = sushiUrl.replaceAll("SUSHI", "");
            }
            Log.d("final sushi url", sushiUrl);

            sushiAnimation.setVisibility(View.GONE);
            sushiContainer.setVisibility(View.GONE);
            sushiWeb.setVisibility(View.VISIBLE);
        }
        else {
            getSushiGame();
        }
    }

    @Override
    public void onBackPressed() {
        if(!isSushiWeb && getSupportFragmentManager().findFragmentByTag("privacytag") != null){
            sushiAnimation.setVisibility(View.GONE);
            sushiWeb.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
            fTransaction.replace(R.id.sushi_container, new SushiFragment(),"sushitag");
            fTransaction.commit();
            sushiContainer.setVisibility(View.VISIBLE);
        }
        else if (isSushiWeb && sushiWeb.canGoBack()) {
            sushiWeb.goBack();
        }
        else{
            super.onBackPressed();
        }
    }
}