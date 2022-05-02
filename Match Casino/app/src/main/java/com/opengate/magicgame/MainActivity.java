package com.opengate.magicgame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.FacebookSdk;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static android.text.InputType.TYPE_NULL;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    private ImageView info, auto, spin;
    private TextView txtBalance, txtBonus, txtStavka;
    private ConstraintLayout magicGate;
    private FrameLayout fragContainer;
    private int intStavka, intBalance;
    private ViewFlipper[][] vf;
    private List<Integer> imgList;
    private Thread[] slThread = new Thread[5];
    private Runnable[] slot = new Runnable[5];
    private Runnable clickableOn, finalCheckVictory;
    private boolean gameOver = false, autoSlots = false;
    private boolean[] startSl = {false, false, false, false, false};
    FragmentTransaction ft;
    WebUrl wUrl;
    private static MainActivity instance;
    private Checker checker;
    private static final String ONESIGNAL_APP_ID = "2eda2d04-6772-4d4c-83a0-7276b9a5b2d6";
    String way;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initElements();
        hideNav();

        ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frag_container, new LoadAnimFragment(), "loading");
        ft.commit();

        addImgToVF();
        initThreads();
        initBtnSpin();
        initBtnAuto();
        initBtnInfo();
        wUrl = new WebUrl();

        for(int i = 0; i < 5; i++) {
            slThread[i].start();
        }
        instance = this;

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        checker = new Checker(wUrl.getTarget());
        checker.execute();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("webfrag") != null && way.equals("right")) {
            if(WebFragment.wView.canGoBack()){
                WebFragment.wView.goBack();
            }
            else {
                Fragment fr = new WebFragment(wUrl.getTarget());
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frag_container, fr, "webfrag");
                ft.commit();
                Log.d("-----","webfrag");
            }
        }
        else if(getSupportFragmentManager().findFragmentByTag("infopolicy") != null){
            magicGate.setVisibility(View.VISIBLE);
            fragContainer.setVisibility(View.GONE);
        }
        else
            super.onBackPressed();
    }

    public static MainActivity getInstance() {
        return instance;
    }

    private void initBtnInfo() {
        if (info != null) {
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(internetConnection()){
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.frag_container, new WebFragment(wUrl.getPrivatePolicy()), "infopolicy");
                        ft.commit();

                        fragContainer.setVisibility(View.VISIBLE);
                        magicGate.setVisibility(View.GONE);
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Please TURN ON Internet", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    private void initBtnSpin() {
        if (spin != null) {
            spin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spinSlots();
                }
            });
        }
    }

    private void initBtnAuto() {
        if (auto != null) {
            auto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    autoPlaySlots();
                }
            });
        }
    }

    private void initThreads() {
        for(int i = 0; i < 5; i++) {
            int finalI = i;
            slot[finalI] = new Runnable() {
                @Override
                public void run() {

                    if(finalI %2 == 0) {
                        vf[0][finalI].showPrevious();
                        vf[1][finalI].showPrevious();
                        vf[2][finalI].showPrevious();
                    }
                    else{
                        vf[0][finalI].showNext();
                        vf[1][finalI].showNext();
                        vf[2][finalI].showNext();
                    }
                }
            };
        }

        for(int i = 0; i < 5; i++) {
            int finalI = i;
            slThread[finalI] = new Thread() {
                Random rand = new Random();
                int sp1, sp2, sp3;

                @Override
                public void run() {
                    while (!gameOver) {
                        while (startSl[finalI]) {
                            intStavka = parseStavka();
                            intBalance = Integer.parseInt(txtBalance.getText().toString());
                            if (!(intBalance >= intStavka)) {
                                startSl[finalI] = false;
                                runOnUiThread(clickableOn);
                                break;
                            }

                            sp1 = rand.nextInt(10) + 5;
                            sp2 = rand.nextInt(10) + 5;
                            sp3 = rand.nextInt(10) + 5;

                            try {
                                for (int j = 0; j < sp1; ++j) {
                                    runOnUiThread(slot[finalI]);
                                    sleep(100);
                                }
                                for (int j = 0; j < sp2; ++j) {
                                    runOnUiThread(slot[finalI]);
                                    sleep(200);
                                }
                                for (int j = 0; j < sp3; ++j) {
                                    runOnUiThread(slot[finalI]);
                                    sleep(300);
                                }
                            } catch (Exception ex) {
                            }

                            startSl[finalI] = false;
                            runOnUiThread(clickableOn);
                            runOnUiThread(finalCheckVictory);
                        }
                    }
                }
            };
        }


        clickableOn = new Runnable() {
            @Override
            public void run() {
                if (!startSl[0] && !startSl[1] && !startSl[2] && !startSl[3] && !startSl[4]) {

                    txtStavka.setFocusable(true);
                    txtStavka.setFocusableInTouchMode(true);
                    txtStavka.setInputType(InputType.TYPE_CLASS_TEXT);
                    spin.setClickable(true);
                }
            }
        };

        finalCheckVictory= new Runnable() {
            @Override
            public void run() {
                if (!startSl[0] && !startSl[1] && !startSl[2] && !startSl[3] && !startSl[4]) {

                    int number1 = (vf[1][0].getDisplayedChild()+ 0) % 14;
                    int number2 = (vf[1][1].getDisplayedChild()+ 11) % 14;
                    int number3 = (vf[1][2].getDisplayedChild()+ 8) % 14;
                    int number4 = (vf[1][3].getDisplayedChild()+ 5) % 14;
                    int number5 = (vf[1][4].getDisplayedChild()+ 2) % 14;

                    Log.d("--- Picture Numbers ---", "" + number1 + " " + number2 + " " + number3 + " " + number4 + " " + number5);

                    int mult = -1;

                    List<Integer> numbers = new ArrayList<>();
                    Collections.addAll(numbers, number1, number2, number3, number4, number5);
                    int dups = chechDuplicate((ArrayList<Integer>) numbers);

                    switch (dups) {
                        case  1:
                            mult = 2;
                            break;
                        case 2:
                            mult = 3;
                            break;
                        case 3:
                            mult = 5;
                            break;
                        case 4:
                            mult = 7;
                            break;
                        default:
                            mult = -1;
                            break;
                    }

                    Log.d("--- Multiplier ---", "" + mult);

                    intBalance += intStavka * mult;
                    if(mult > 0){
                        txtBonus.setText(""+ intStavka * mult);
                    }

                    if(intBalance <= 0) {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        intBalance = 10000;
                        Toast.makeText(MainActivity.this,"You have 10000 points!", Toast.LENGTH_SHORT).show();
                    }
                    if(intStavka > intBalance){
                        intStavka = intBalance;
                    }

                    setStavkaBalanceText();

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(autoSlots){
                        spinSlots();
                    }
                }
            }
        };
    }

    private void addImgToVF() {
        imgList = new ArrayList<>(Arrays.asList(R.drawable.a, R.drawable.bells, R.drawable.bonus1, R.drawable.castle, R.drawable.coin,
                                                R.drawable.dragon, R.drawable.j, R.drawable.jackpot, R.drawable.k, R.drawable.q,
                                                R.drawable.symbo4, R.drawable.symbol1, R.drawable.symbol3, R.drawable.ten));

        for (int j = 0; j < 5; j++){
            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < imgList.size(); k++) {
                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setImageResource(imgList.get(k));
                    vf[i][j].addView(imageView);
                }
                Collections.rotate(imgList, 1);
            }
        }
    }

    private void initElements() {
        info = (ImageView) findViewById(R.id.info);
        auto = (ImageView) findViewById(R.id.auto);
        spin = (ImageView) findViewById(R.id.spin);
        txtBalance = (TextView) findViewById(R.id.txt_balance);
        txtBonus = (TextView) findViewById(R.id.txt_bonus);
        txtStavka = (TextView) findViewById(R.id.txt_stavka);
        magicGate = (ConstraintLayout) findViewById(R.id.magicgateslots);
        fragContainer = (FrameLayout) findViewById(R.id.frag_container);

        vf = new ViewFlipper[][]{{findViewById(R.id.vf00), findViewById(R.id.vf01), findViewById(R.id.vf02), findViewById(R.id.vf03), findViewById(R.id.vf04)},
                {findViewById(R.id.vf10), findViewById(R.id.vf11), findViewById(R.id.vf12), findViewById(R.id.vf13), findViewById(R.id.vf14)},
                {findViewById(R.id.vf20), findViewById(R.id.vf21), findViewById(R.id.vf22), findViewById(R.id.vf23), findViewById(R.id.vf24)} };
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNav();
    }

    private void hideNav(){
        View dv = getWindow().getDecorView();
        dv.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void setStavkaBalanceText() {
        txtStavka.setText("" + intStavka);
        txtBalance.setText("" + intBalance);
    }

    private int chechDuplicate(ArrayList<Integer> arr) {

        int counter = 0;
        for (int i = 0; i < arr.size(); i++) {
            for (int j = i + 1; j < arr.size(); j++) {
                if (arr.get(i) == arr.get(j)) {
                    counter++;
                }
            }
        }
        return counter;
    }

    private void autoPlaySlots() {

        if(!autoSlots && !startSl[0] && !startSl[1] && !startSl[2] && !startSl[3] && !startSl[4]){
            autoSlots = true;
            spinSlots();
        }
        else {
            autoSlots = false;
        }
    }

    private void spinSlots() {
        intStavka = parseStavka();
        intBalance = Integer.parseInt(txtBalance.getText().toString());

        if(intStavka <= intBalance){
            for(int i = 0; i < 5; i++) {
                startSl[i] = true;
            }

            txtStavka.setFocusable(false);
            txtStavka.setFocusableInTouchMode(false);
            txtStavka.setInputType(TYPE_NULL);
            spin.setClickable(false);
            txtBonus.setText("0");
        }
        else {
            Toast.makeText(MainActivity.this,"Change BET, it can't be more than balance!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean internetConnection(){
        boolean internetConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            internetConnected = true;
        }
        else
            internetConnected = false;

        return internetConnected;
    }

    private int parseStavka(){
        int bet = 100;
        try {
            bet = Integer.parseInt(txtStavka.getText().toString());
        }
        catch(Exception ex){}
        return bet;
    }

    public void chooseWay() {
        String strParse = "";
        try {
            strParse = checker.getPageString().split("<body>")[1].split("</body>")[0].replaceAll("\\s+", "");
        } catch (Exception ex) {
        }

        if (strParse.equals(wUrl.getTdsWord()) || strParse.length() > 47) {
            way = "right";

            if (!checker.getCutUri().equals("")) {
                wUrl.setTarget(wUrl.getTarget() + "?subId1=TRATATA&subId2=TRATATA&subId3=TRATATA&subId4=TRATATA&subId5=TRATATA");
                String parts[] = checker.getCutUri().split("//");
                String tmpUrl = wUrl.getTarget();
                for (int i = 0; i < parts.length; i++) {
                    tmpUrl = tmpUrl.replaceFirst("TRATATA", parts[i]);
                }
                tmpUrl = tmpUrl.replaceAll("TRATATA", "");
                wUrl.setTarget(tmpUrl);
            }
            Log.d("--- Target URL ---", wUrl.getTarget());

            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frag_container, new WebFragment(wUrl.getTarget()), "webfrag");
            ft.commit();
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            magicGate.setVisibility(View.VISIBLE);
            fragContainer.setVisibility(View.GONE);

        }

    }
}