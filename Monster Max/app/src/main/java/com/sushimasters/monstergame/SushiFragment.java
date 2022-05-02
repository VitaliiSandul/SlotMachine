package com.sushimasters.monstergame;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;

public class SushiFragment extends Fragment implements View.OnClickListener{

    private ImageView multiplier,freeSpins;
    private ImageButton minusBetLevelButton, plusBetLevelButton, autoplayButton, stopButton, spinButton;
    private ImageButton maxBetButton, minusCoinValueButton, plusCoinValueButton, infoButton;
    private TextView betTxt, coinsTxt, linesTxt,betLevelTxt, coinValueTxt;
    private double bet = 0.5, coins = 100.00, coinValue = 1.0;
    private int lines = 10, betLevel = 1, multiplierInt = 1, freeSpinsInt = 0;
    private ViewFlipper[][] sushi;
    private List<Integer> sushiImagelist;
    private int five = 5, three = 3;
    private Thread[] sushiThread = new Thread[5];
    private Runnable[] sushiRunnable = new Runnable[5];
    private Runnable btnTurnOn, isWin;
    private boolean[] slotRun = new boolean[]{false, false, false, false, false};
    private boolean gameAuto = false, gameFin = false, gameStop = false;

    public SushiFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View sushiView = inflater.inflate(R.layout.fragment_sushi, container, false);

        initImgBtnTxt(sushiView);
        initFillViewFlippers(sushiView);
        initSushiThreads();
        startThreads();
        clickYes();

        return sushiView;
    }

    private void startThreads() {
        for (int i = 0; i < 5; i++){
            sushiThread[i].start();
        }
    }

    private void initSushiThreads() {
        for(int t = 0; t < 5; t++) {
            int finalT = t;
            sushiRunnable[finalT] = new Runnable() {
                @Override
                public void run() {
                    for(int i = 0; i < 3; i++) {
                        sushi[i][finalT].showNext();
                    }
                }
            };
        }

        for(int z = 0; z < 5; z++) {
            int finalZ = z;
            sushiThread[finalZ] = new Thread() {
//                Random random = new Random();
//                int [] speed = new int[3];

                @Override
                public void run() {
                while (!gameFin) {
                    while (slotRun[finalZ]) {
                        bet = Double.parseDouble(betTxt.getText().toString());
                        coins = Double.parseDouble(coinsTxt.getText().toString());
                        if (!(bet <= coins)) {
                            slotRun[finalZ] = false;
                            Helps.runOnUiThread(btnTurnOn);
                            break;
                        }

                        try {
                            int delay = 180;
                            while (!gameStop){
                                Helps.runOnUiThread(sushiRunnable[finalZ]);
                                sleep(delay + finalZ * 10 );
                            }

                        } catch (Exception ex) {
                        }

                        slotRun[finalZ] = false;
                        if(!isSlotsRunning()){
                            Helps.runOnUiThread(btnTurnOn);
                            Helps.runOnUiThread(isWin);
                        }
                    }
                }
                }
            };
        }

        btnTurnOn = new Runnable() {
            @Override
            public void run() {
                if (!isSlotsRunning()) {
                    clickYes();
                }
            }
        };

        isWin = new Runnable() {
            @Override
            public void run() {
                if (!isSlotsRunning()) {

                    int sushiImg1 = (sushi[1][0].getDisplayedChild()) % 10;
                    int sushiImg2 = (sushi[1][1].getDisplayedChild()+ 7) % 10;
                    int sushiImg3 = (sushi[1][2].getDisplayedChild()+ 4) % 10;
                    int sushiImg4 = (sushi[1][3].getDisplayedChild()+ 1) % 10;
                    int sushiImg5 = (sushi[1][4].getDisplayedChild()+8) % 10;

                    Log.d("Win Combination", "" + sushiImg1 + " " + sushiImg2 + " " + sushiImg3 + " " + sushiImg4 + " " + sushiImg5);

                    int coef = -1;

                    List<Integer> centerNums = new ArrayList<>();
                    Collections.addAll(centerNums, sushiImg1, sushiImg2, sushiImg3, sushiImg4, sushiImg5);
                    int repetitions = findRepetitions((ArrayList<Integer>) centerNums);

                    switch (repetitions) {
                        case  1:
                            coef = 2;
                            break;
                        case 2:
                            coef = 3;
                            break;
                        case 3:
                            coef = 4;
                            break;
                        case 4:
                            coef = 7;
                            break;
                        default:
                            coef = -1;
                            break;
                    }

                    if(coef < 1 && freeSpinsInt == 0){
                        coins -= bet;
                    }
                    else if(coef < 1 && freeSpinsInt > 0){
                        freeSpinsInt--;
                    }
                    else if(coef >= 1){
                        coins += bet * coef * multiplierInt;
                    }

                    if(coins <= 0) {
                        try {
                            sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        coins = 100.0;
                        Toast.makeText(getActivity(),"Take 100 extra points!", Toast.LENGTH_LONG).show();
                    }
                    if(bet > coins){
                        bet = coins;
                    }

                    if(coef > 3){
                        Random random = new Random();

                        int tempMultiplierInt = random.nextInt(3) + 1;
                        int tempFreeSpinsInt  = random.nextInt(9) + 1;
                        Log.d("MultiplierInt", String.valueOf(tempMultiplierInt));
                        Log.d("FreeSpinsInt", String.valueOf(tempFreeSpinsInt));

                        switch (tempMultiplierInt) {
                            case  1:
                                multiplierInt = 2;
                                multiplier.setImageResource(R.drawable.x2_multiplier_panel);
                                break;
                            case 2:
                                multiplierInt = 3;
                                multiplier.setImageResource(R.drawable.x3_multiplier_panel);
                                break;
                            case 3:
                                multiplierInt = 5;
                                multiplier.setImageResource(R.drawable.x5_multiplier_panel);
                                break;
                            default:
                                multiplierInt = 1;
                                multiplier.setVisibility(View.GONE);
                                break;
                        }

                        switch (tempFreeSpinsInt) {
                            case  1:
                                freeSpinsInt = 1;
                                freeSpins.setImageResource(R.drawable.free_spins_1_panel);
                                break;
                            case 2:
                                freeSpinsInt = 2;
                                freeSpins.setImageResource(R.drawable.free_spins_2_panel);
                                break;
                            case 3:
                                freeSpinsInt = 3;
                                freeSpins.setImageResource(R.drawable.free_spins_3_panel);
                                break;
                            case  4:
                                freeSpinsInt = 4;
                                freeSpins.setImageResource(R.drawable.free_spins_4_panel);
                                break;
                            case 5:
                                freeSpinsInt = 5;
                                freeSpins.setImageResource(R.drawable.free_spins_5_panel);
                                break;
                            case 6:
                                freeSpinsInt = 6;
                                freeSpins.setImageResource(R.drawable.free_spins_6_panel);
                                break;
                            case  7:
                                freeSpinsInt = 7;
                                freeSpins.setImageResource(R.drawable.free_spins_7_panel);
                                break;
                            case 8:
                                freeSpinsInt = 8;
                                freeSpins.setImageResource(R.drawable.free_spins_8_panel);
                                break;
                            case 9:
                                freeSpinsInt = 9;
                                freeSpins.setImageResource(R.drawable.free_spins_9_panel);
                                break;
                            default:
                                freeSpinsInt = 0;
                                freeSpins.setVisibility(View.GONE);
                                break;
                        }

                        multiplier.setVisibility(View.VISIBLE);
                        freeSpins.setVisibility(View.VISIBLE);
                    }

                    setBetTxt();
                    setCoinsTxt();

                    try {
                        sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(gameAuto){
                        spinFunc();
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                stopFunc();
                            }
                        }, 5000);
                    }
                }
            }
        };
    }

    private void initImgBtnTxt(View sv) {
        multiplier = sv.findViewById(R.id.multiplier);
        freeSpins = sv.findViewById(R.id.free_spins);
        minusBetLevelButton = sv.findViewById(R.id.minus_bet_level_button);
        plusBetLevelButton = sv.findViewById(R.id.plus_bet_level_button);
        autoplayButton  = sv.findViewById(R.id.autoplay_button);
        stopButton = sv.findViewById(R.id.stop_button);
        spinButton = sv.findViewById(R.id.spin_button);
        maxBetButton = sv.findViewById(R.id.max_bet_button);
        minusCoinValueButton = sv.findViewById(R.id.minus_coin_value_button);
        plusCoinValueButton = sv.findViewById(R.id.plus_coin_value_button);
        infoButton = sv.findViewById(R.id.info_button);
        betTxt = sv.findViewById(R.id.bet_txt);
        coinsTxt = sv.findViewById(R.id.coins_txt);
        linesTxt = sv.findViewById(R.id.lines_txt);
        betLevelTxt  = sv.findViewById(R.id.bet_level_txt);
        coinValueTxt = sv.findViewById(R.id.coin_value_txt);
    }

    private void initFillViewFlippers(View sv) {
        sushi = new ViewFlipper[][]{{sv.findViewById(R.id.sushi_00), sv.findViewById(R.id.sushi_01), sv.findViewById(R.id.sushi_02), sv.findViewById(R.id.sushi_03), sv.findViewById(R.id.sushi_04)},
                                    {sv.findViewById(R.id.sushi_10), sv.findViewById(R.id.sushi_11), sv.findViewById(R.id.sushi_12), sv.findViewById(R.id.sushi_13), sv.findViewById(R.id.sushi_14)},
                                    {sv.findViewById(R.id.sushi_20), sv.findViewById(R.id.sushi_21), sv.findViewById(R.id.sushi_22), sv.findViewById(R.id.sushi_23), sv.findViewById(R.id.sushi_24)} };

        sushiImagelist = new ArrayList<>(Arrays.asList(R.drawable.a, R.drawable.bonus,
                                                        R.drawable.hz, R.drawable.j,
                                                        R.drawable.k, R.drawable.light_bulb_fish,
                                                        R.drawable.octopus, R.drawable.q,
                                                        R.drawable.roll, R.drawable.wild));

        for (int j = 0; j < five; j++){
            for (int i = 0; i < three; i++) {

                sushi[i][j].setInAnimation(getActivity().getApplicationContext(), R.anim.slide_start);
                sushi[i][j].setOutAnimation(getActivity().getApplicationContext(), R.anim.slide_finish);

                for (int k = 0; k < sushiImagelist.size(); k++) {
                    ImageView sushiImage = new ImageView(getActivity());
                    sushiImage.setImageResource(sushiImagelist.get(k));
                    sushi[i][j].addView(sushiImage);
                }
                Collections.rotate(sushiImagelist, 1);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.minus_bet_level_button:
                minusBetLevelFunc();
                break;
            case R.id.plus_bet_level_button:
                plusBetLevelFunc();
                break;
            case R.id.autoplay_button:
                autoplayFunc();
                break;
            case R.id.spin_button:
                spinFunc();
                break;
            case R.id.stop_button:
                stopFunc();
                break;
            case R.id.max_bet_button:
                maxBetFunc();
                break;
            case R.id.minus_coin_value_button:
                minusCoinValueFunc();
                break;
            case R.id.plus_coin_value_button:
                plusCoinValueFunc();
                break;
            case R.id.info_button:
                infoFunc();
                break;
        }
    }

    private void minusBetLevelFunc() {
        betLevel--;
        if (betLevel <= 1)
            betLevel++;

        bet = 0.5 * betLevel;
        setBetTxt();
    }

    private void plusBetLevelFunc() {
        betLevel++;
        bet = 0.5 * betLevel;
        if (bet > coins){
            betLevel--;
            bet -= 0.5;
        }
        setBetTxt();
    }

    private void autoplayFunc() {
        if(!isSlotsRunning()){
            gameAuto = true;
            spinFunc();

            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopFunc();
                }
            }, 5000);
        }
        else {
            gameAuto = false;
        }
    }

    private void spinFunc() {
        spinButton.setVisibility(View.GONE);

        clickNo();
        freeSpins.setVisibility(View.GONE);

        if(freeSpinsInt > 0){
            freeSpinsInt--;
        }

        setBetTxt();
        setCoinsTxt();

        for(int i = 0; i < 5; i++){
            slotRun[i] = true;
        }

        gameStop = false;
    }

    private void stopFunc() {
        gameStop = true;
        spinButton.setVisibility(View.VISIBLE);
    }

    private void maxBetFunc() {
        bet = coins;
        betLevel = (int) (bet * 2);
        setBetTxt();
    }

    private void minusCoinValueFunc() {
        coinValue -= 0.1;
        if (coinValue <= 0.1)
            coinValue += 0.1;
        setCoinValueTxt();
    }

    private void plusCoinValueFunc() {
        coinValue += 0.1;
        if (coinValue > 10)
            bet -= 0.1;
        setCoinValueTxt();
    }


    private void infoFunc() {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.sushi_container, new PrivacyFragment(), "privacytag");
            transaction.commit();
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            //Testing Crushlytics
            //throw new RuntimeException("Test Crash");
        }
        else {
            Toast.makeText(getActivity(), "There is no internet connection", Toast.LENGTH_LONG).show();
        }
    }

    private void setBetTxt(){
        betTxt.setText(String.valueOf(bet));
        betLevelTxt.setText(String.valueOf(betLevel));
    }

    private void setCoinValueTxt(){
        coinValueTxt.setText(String.format("%.2f", coinValue));
    }

    private void setCoinsTxt(){
        coinsTxt.setText(String.valueOf(coins));
    }

    private boolean isSlotsRunning(){
        for(int m = 0; m < 5; m++){
            if(slotRun[m] == true){
                return true;
            }
        }
        return false;
    }

    private void clickYes() {
        minusBetLevelButton.setOnClickListener(this);
        plusBetLevelButton.setOnClickListener(this);
        autoplayButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        spinButton.setOnClickListener(this);
        maxBetButton.setOnClickListener(this);
        minusCoinValueButton.setOnClickListener(this);
        plusCoinValueButton.setOnClickListener(this);
        infoButton.setOnClickListener(this);
    }

    private void clickNo() {
        minusBetLevelButton.setOnClickListener(null);
        plusBetLevelButton.setOnClickListener(null);
        spinButton.setOnClickListener(null);
        maxBetButton.setOnClickListener(null);
        minusCoinValueButton.setOnClickListener(null);
        plusCoinValueButton.setOnClickListener(null);
    }

    private int findRepetitions(ArrayList<Integer> arrs){
        int res = 0;
        HashSet hs = new HashSet<Integer>();
        for (Integer num : arrs) {
            if (hs.add(num) == false) {
                res++;
            }
        }
        return res;
    }
}