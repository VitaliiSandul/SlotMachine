package com.myfavoritegame.football;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.onesignal.OneSignal;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String ONESIGNAL_APP_ID = "4611c893-0924-431c-8cbb-a93fc60a18f2";
    private String urlWeb;
    private String checkStr = "";
    private TextView score1Txt, score2Txt, goalTxt, nogoalTxt, team1Txt, team2Txt;
    private int score1 = 0, score2 = 0, kicks = 0;
    private SeekBar horizontalSeekbar, verticalSeekbar;
    private ImageView info, start, ball, ballWhiteStand, wall, targetSmallBall, smallBall, goalkeeper, gifAnimation;
    private ConstraintLayout cl;
    private WebView netWeb;
    private String urlPol= "https://sites.google.com/view/prol";
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;
    public static final int REQUEST_SELECT_FILE = 100;
    private static MainActivity instance;
    private Display display;
    private Point size;
    private int width;
    private int height;
    private int targetX = 0;
    private int targetY = 0;
    private boolean isPM = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideNavig();
        loadGifAnimation();
        initElements();
        setSeekBars();
        clickIn();
        setNetWeb();
        urlWeb = HtmlJavaScriptInterface.urlWeb;
        instance = this;

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        checkInternetConnection();

    }

    private void hideNavig() {
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavig();
    }

    private void setSeekBars() {
        horizontalSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                targetSmallBall.setX(width * seekBar.getProgress()/100 + 30);
                targetX = seekBar.getProgress();

                Log.d("--- X ---", String.valueOf(seekBar.getProgress()));
            }
        });


        verticalSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                targetSmallBall.setY(height * seekBar.getProgress()/100 + 30);
                targetY = seekBar.getProgress();
                Log.d("--- Y ---", String.valueOf(seekBar.getProgress()));
            }
        });
    }

    private void getHtmlFromWV(){
        WebSettings settings = netWeb.getSettings();
        // This method requires JavaScript to be enabled
        settings.setJavaScriptEnabled(true); // Register the interface class just now to a JavaScript interface called HTMLOUT
        netWeb.addJavascriptInterface(new HtmlJavaScriptInterface(), "HTMLOUT");

        // Must set WebViewClient before loadUrl
        netWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Here you can filter the url
                netWeb.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
            }
        });

        // start loading URL
        netWeb.loadUrl(urlWeb);
        netWeb.setVisibility(View.GONE);
    }

    private void initElements() {
        score1Txt = findViewById(R.id.score1_txt);
        score2Txt = findViewById(R.id.score2_txt);
        goalTxt = findViewById(R.id.goal_txt);
        nogoalTxt = findViewById(R.id.nogoal_txt);
        team1Txt = findViewById(R.id.team1_txt);
        team2Txt = findViewById(R.id.team2_txt);
        horizontalSeekbar = findViewById(R.id.horizontal_seekbar);
        verticalSeekbar = findViewById(R.id.vertical_seekbar);
        info = findViewById(R.id.info);
        start = findViewById(R.id.start);
        ball = findViewById(R.id.ball);
        ballWhiteStand = findViewById(R.id.ball_white_stand);
        wall = findViewById(R.id.wall);
        targetSmallBall = findViewById(R.id.target_small_ball);
        smallBall = findViewById(R.id.small_ball);
        goalkeeper = findViewById(R.id.goalkeeper);
        netWeb = findViewById(R.id.netweb);
        cl =  findViewById(R.id.cl);

        goalkeeper.setImageResource(R.drawable.goalkeeper);
        wall.setImageResource(R.drawable.wall1);
    }

    private void loadGifAnimation(){
        gifAnimation = (ImageView) findViewById(R.id.gif_animation);
        gifAnimation.setBackgroundResource(R.drawable.load_anim);
        AnimationDrawable anim = (AnimationDrawable) gifAnimation.getBackground();
        anim.start();
        gifAnimation.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.info:
                infoFunction();
                break;
            case R.id.start:
                startFunction();
                break;
        }
    }

    private void clickIn() {
        info.setOnClickListener(this);
        start.setOnClickListener(this);
    }

    private void clickOut() {
        info.setOnClickListener(null);
        start.setOnClickListener(null);
    }

    private void startFunction() {
        clickOut();
        kicks++;
        float targetSmallBallX = targetSmallBall.getX();
        float targetSmallBallY = targetSmallBall.getY();
        float ballX = ball.getX();
        float ballY = ball.getY();

        targetSmallBall.setVisibility(View.GONE);
        ballWhiteStand.setVisibility(View.GONE);

        Animation scale= new ScaleAnimation(1, (float) 0.50, 1, (float) 0.50, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(1000);
        Animation slideUp = new TranslateAnimation(0, -(ballX - targetSmallBallX),0, -(ballY -targetSmallBallY));
        slideUp.setDuration(1000);
        slideUp.setFillAfter(true);
        AnimationSet animSet = new AnimationSet(true);
        animSet.setFillEnabled(true);
        animSet.addAnimation(scale);
        animSet.addAnimation(slideUp);
        ball.startAnimation(animSet);
        ball.setVisibility(View.GONE);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if((targetX >=32 && targetX <=34 && targetY >=40 && targetY <= 75)
                        || (targetX >=32 && targetX <=88 && targetY >=39 && targetY <= 40)
                        || (targetX >=52 && targetX <=68 && targetY >=40 && targetY <= 75)){

                    if(kicks %2 == 1){
                        score1++;
                    }
                    else{
                        score2++;
                    }

                    goalTxt.setVisibility(View.VISIBLE);
                }
                else{
                    nogoalTxt.setVisibility(View.VISIBLE);
                    if(targetX > 34 && targetX < 52 && targetY > 39 && targetY < 75){

                        goalkeeper.setImageResource(R.drawable.goalkeeper_ball);
                        goalkeeper.requestLayout();
                    }
                    else if(targetX > 68 && targetX < 100 && targetY > 39 && targetY < 100){
                        wall.setImageResource(R.drawable.wall);
                    }
                }

                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override public void run() {
                        goalTxt.setVisibility(View.GONE);
                        nogoalTxt.setVisibility(View.GONE);

                        refreshScore();
                        targetSmallBall.setVisibility(View.VISIBLE);
                        ballWhiteStand.setVisibility(View.VISIBLE);
                        ball.setVisibility(View.VISIBLE);
                        goalkeeper.setImageResource(R.drawable.goalkeeper);
                        wall.setImageResource(R.drawable.wall1);

                        if(kicks %2 == 1){
                            team2Txt.setVisibility(View.VISIBLE);
                            team1Txt.setVisibility(View.GONE);
                        }
                        else{
                            team2Txt.setVisibility(View.GONE);
                            team1Txt.setVisibility(View.VISIBLE);
                        }
                        clickIn();
                    }
                }, 1000);
            }
        });
    }

    private void infoFunction() {
        netWeb.loadUrl(urlPol);
        netWeb.setVisibility(View.VISIBLE);

        //for testing Crashlytics
        //throw new RuntimeException("Test Crash");
    }

    private void findDisplayWidthHeight() {
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        width = (int) (size.x * 0.8);
        height = (int) (size.y * 0.5);
    }

    private void refreshScore(){
        if(score1 < 10){
            score1Txt.setText("0" + score1);
        }
        else{
            score1Txt.setText(String.valueOf(score1));
        }

        if(score2 < 10){
            score2Txt.setText("0" + score2);
        }
        else{
            score2Txt.setText(String.valueOf(score2));
        }
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public void loadNext(){

        try {
            checkStr = HtmlJavaScriptInterface.footTasker.getWebNetStr();
        } catch (Exception ex) {
        }

        if (checkStr.length() > 70) {
            isPM = true;

            if (!HtmlJavaScriptInterface.footTasker.getPieceUri().equals("")) {
                urlWeb += "?subId1=FOOTYBALL&subId2=FOOTYBALL&subId3=FOOTYBALL&subId4=FOOTYBALL&subId5=FOOTYBALL";
                String parts[] = HtmlJavaScriptInterface.footTasker.getPieceUri().split("//");

                for (int i = 0; i < parts.length; i++) {
                    urlWeb = urlWeb.replaceFirst("FOOTYBALL", parts[i]);
                }
                urlWeb = urlWeb.replaceAll("FOOTYBALL", "");
            }
            Log.d("--- Web URL ---", urlWeb);

            gifAnimation.setVisibility(View.GONE);
            cl.setVisibility(View.GONE);
            netWeb.setVisibility(View.VISIBLE);

        }
        else {
            gifAnimation.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            cl.setVisibility(View.VISIBLE);
            findDisplayWidthHeight();

            targetX = horizontalSeekbar.getProgress();
            targetY = verticalSeekbar.getProgress();
            team1Txt.setVisibility(View.VISIBLE);
        }
    }

    private void setNetWeb(){
        WebSettings ws = netWeb.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setSupportZoom(false);
        ws.setAllowFileAccess(true);
        ws.setAllowContentAccess(true);

        netWeb.setWebViewClient(new WebViewClient());
        netWeb.setWebChromeClient(new WebChromeClient());

        CookieSyncManager.createInstance(netWeb.getContext());
        CookieSyncManager.getInstance().sync();

        netWeb.getSettings().setJavaScriptEnabled(true);
        netWeb.getSettings().setDomStorageEnabled(true);
        netWeb.requestFocus(View.FOCUS_DOWN);

        netWeb.setWebChromeClient(new WebChromeClient()
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

    @Override
    public void onBackPressed() {
        if (!isPM) {
            netWeb.setVisibility(View.GONE);
            cl.setVisibility(View.VISIBLE);
        }
        else if (netWeb.canGoBack()) {
            netWeb.goBack();
        }
        else{
            super.onBackPressed();
        }
    }

    private void checkInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            getHtmlFromWV();
        }
        else{
            gifAnimation.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            cl.setVisibility(View.VISIBLE);
            findDisplayWidthHeight();

            targetX = horizontalSeekbar.getProgress();
            targetY = verticalSeekbar.getProgress();
            team1Txt.setVisibility(View.VISIBLE);
        }
    }
}