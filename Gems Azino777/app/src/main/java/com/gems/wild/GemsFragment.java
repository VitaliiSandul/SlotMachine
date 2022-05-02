package com.gems.wild;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.text.InputType.TYPE_NULL;
import static java.lang.Thread.sleep;

public class GemsFragment extends Fragment {

    private ImageButton info, auto, spin;
    private TextView balanceText, bonusText;
    private EditText betText;
    private ViewFlipper[][] gems;
    private Runnable[] gemsRunnable;
    private Runnable winnerCheck, clickEnable;
    private Thread[] gemsThread;
    private boolean roller[] = {false, false, false, false, false};
    private double bet = 2.0, bonus = 0.0, balance = 1000.00;
    private boolean autoGame = false;
    private String privUrl = "https://sites.google.com/view/privacyypoliicyy";

    public GemsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View gemView = inflater.inflate(R.layout.fragment_gems, container, false);

        initTextButtonsImages(gemView);
        fillGems();
        initGemsThreads();
        initInfoBtn();
        initSpinBtn();
        initAutoBtn();
        StartThreads();

        return gemView;
    }

    private void initTextButtonsImages(View v) {
        info = v.findViewById(R.id.info);
        auto = v.findViewById(R.id.auto);
        spin = v.findViewById(R.id.spin);
        balanceText = v.findViewById(R.id.balance_text);
        bonusText = v.findViewById(R.id.bonus_text);
        betText = v.findViewById(R.id.bet_text);

        gems = new ViewFlipper[][]{{v.findViewById(R.id.gems_00), v.findViewById(R.id.gems_01), v.findViewById(R.id.gems_02), v.findViewById(R.id.gems_03), v.findViewById(R.id.gems_04)},
                                    {v.findViewById(R.id.gems_10), v.findViewById(R.id.gems_11), v.findViewById(R.id.gems_12), v.findViewById(R.id.gems_13), v.findViewById(R.id.gems_14)},
                                    {v.findViewById(R.id.gems_20), v.findViewById(R.id.gems_21), v.findViewById(R.id.gems_22), v.findViewById(R.id.gems_23), v.findViewById(R.id.gems_24)} };
    }


    private void fillGems() {
        List<Integer> gemsImgList = new ArrayList<>(Arrays.asList(R.drawable.blue, R.drawable.green, R.drawable.orange,
                                                                    R.drawable.pink, R.drawable.purple, R.drawable.red,
                                                                    R.drawable.rose, R.drawable.seawave, R.drawable.wild));
        for (int j = 0; j < 5; j++){
            for (int i = 0; i < 3; i++) {

                gems[i][j].setInAnimation(getActivity().getApplicationContext(), R.anim.slide_in);
                gems[i][j].setOutAnimation(getActivity().getApplicationContext(), R.anim.slide_out);

                for (int k = 0; k < gemsImgList.size(); k++) {
                    ImageView img = new ImageView(getActivity());
                    img.setImageResource(gemsImgList.get(k));
                    gems[i][j].addView(img);
                }
                Collections.rotate(gemsImgList, 1);
            }
        }
    }

    private void initGemsThreads() {
        gemsRunnable = new Runnable[5];
        for(int i = 0; i < 5; i++) {
            int finalI = i;
            gemsRunnable[i] = new Runnable() {
                @Override
                public void run() {
                    for(int t = 0; t < 3; t++){
                        gems[t][finalI].showNext();
                    }
                }
            };
        }

        gemsThread = new Thread[5];
        for(int i = 0; i < 5; i++) {
            int finalI = i;
            gemsThread[i] = new Thread() {
                @Override
                public void run() {
                    Random rand = new Random();
                    while (true) {
                        while (roller[finalI]) {
                            bet = parseSafeBet();
                            balance = parseSafeBalance();
                            if (balance <= bet) {
                                roller[finalI] = false;
                                Profitability.runOnUiThread(clickEnable);
                                break;
                            }
                            int speed = rand.nextInt(5) + 15;
                            try {
                                for (int j = 0; j < speed; ++j) {
                                    Profitability.runOnUiThread(gemsRunnable[finalI]);
                                    sleep(200);
                                }
                            } catch (Exception ex) {
                            }

                            roller[finalI] = false;
                            if (checkRollerStop()){
                                Profitability.runOnUiThread(clickEnable);
                                Profitability.runOnUiThread(winnerCheck);
                            }
                        }
                    }
                }
            };
        }

        winnerCheck = new Runnable() {
            @Override
            public void run() {
                Log.d("stop slots", String.valueOf(checkRollerStop()));
                int gNum1 = (gems[1][0].getDisplayedChild()+ 0) % 9;
                int gNum2 = (gems[1][1].getDisplayedChild()+ 6) % 9;
                int gNum3 = (gems[1][2].getDisplayedChild()+ 3) % 9;
                int gNum4 = (gems[1][3].getDisplayedChild()+ 0) % 9;
                int gNum5 = (gems[1][4].getDisplayedChild()+ 6) % 9;

                Log.d("check combinatoin", "" + gNum1 + " " + gNum2 + " " + gNum3 + " " + gNum4 + " " + gNum5);

                int mult = -1;

                List<Integer> gemsNums = new ArrayList<>();
                Collections.addAll(gemsNums, gNum1, gNum2, gNum3, gNum4, gNum5);
                int counterparts = findCounterparts((ArrayList<Integer>) gemsNums);

                switch (counterparts) {
                    case 1:
                        mult = 1;
                        break;
                    case 2:
                        mult = 2;
                        break;
                    case 3:
                        mult = 3;
                        break;
                    case 4:
                        mult = 5;
                        break;
                    default:
                        mult = -1;
                        break;
                }

                bonus = bet * mult;
                balance += bet * mult;
                if(mult > 0){
                    bonusText.setText(String.format("%.2f", bonus));
                }

                if(balance <= 0) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    balance = 1000.0;
                    Toast.makeText(getActivity(),"You have 1000 points!", Toast.LENGTH_LONG).show();
                }
                if(bet > balance){
                    bet = balance;
                }

                balanceText.setText(String.format("%.2f", balance));
                betText.setText(String.format("%.2f", bet));

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(autoGame){
                    spinRollers();
                }
            }
        };

        clickEnable = new Runnable() {
            @Override
            public void run() {
                if (checkRollerStop()) {
                    betText.setFocusable(true);
                    betText.setFocusableInTouchMode(true);
                    betText.setInputType(InputType.TYPE_CLASS_TEXT);
                    spin.setClickable(true);
                }
            }
        };
    }

    private void StartThreads(){
        for(int i = 0; i < 5; i++) {
            gemsThread[i].start();
        }
    }

    private Double parseSafeBet(){
        Double safetyBet = bet;
        try {
            safetyBet = Double.parseDouble(betText.getText().toString().replace(',', '.'));
        }
        catch(Exception ex){}
        return safetyBet;
    }

    private Double parseSafeBalance(){
        Double safetybalance = balance;
        try {
            safetybalance = Double.parseDouble(balanceText.getText().toString().replace(',', '.'));
        }
        catch(Exception ex){}
        return safetybalance;
    }

    private boolean checkRollerStop(){
        boolean stop = true;
        for(int i = 0 ; i < 5; i++){
            if(roller[i] == true){
                return false;
            }
        }
        return stop;
    }

    private int findCounterparts(ArrayList<Integer> list) {
        int c = 0;
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i) == list.get(j)) {
                    c++;
                }
            }
        }
        return c;
    }

    private void initInfoBtn() {
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager conMan = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                if(conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {


                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.anim_gems_wild_container, new WildFragment(privUrl), "infotag");
                    transaction.commit();
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                    //throw new RuntimeException("Test Crash");
                }
                else{
                    Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initSpinBtn() {
        spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinRollers();
            }
        });
    }

    private void initAutoBtn() {
        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcAutoPlay();
            }
        });
    }

    private void funcAutoPlay(){
        if(checkRollerStop()){
            autoGame = true;
            spinRollers();
        }
        else {
            autoGame = false;
        }
    }

    private void spinRollers() {
        bet = parseSafeBet();
        balance = parseSafeBalance();

        if(bet <= balance){
            for(int i = 0; i < 5; i++) {
                roller[i] = true;
            }
            betText.setFocusable(false);
            betText.setFocusableInTouchMode(false);
            betText.setInputType(TYPE_NULL);
            spin.setClickable(false);
            bonus = 0.0;
            bonusText.setText(String.format("%.2f", bonus));
        }
        else {
            Toast.makeText(getActivity(),"Bet can't be more than balance!", Toast.LENGTH_LONG).show();
        }
    }
}