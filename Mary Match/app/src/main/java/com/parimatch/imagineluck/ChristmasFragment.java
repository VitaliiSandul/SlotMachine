package com.parimatch.imagineluck;

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

public class ChristmasFragment extends Fragment implements View.OnClickListener{

    private ViewFlipper[][] mcsFlipper;
    private TextView txtBalance, txtProfit, txtBet;
    private ImageButton infoButton, autoSpinButton, stopAutoSpinButton, spinButton, fastSpinButton, betButton;
    private ImageView bigWin, giantWin;
    public static int balance = 1000, profit = 0, bet = 2;
    private Runnable btnsOn, verifyWin;
    private Thread[] threadArr;
    private Runnable[] runnableArr;
    private boolean[] slotVolution;
    private boolean autoplay = false, fin = false;

    public ChristmasFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_christmas, container, false);
        fillFlippers(v);
        initViews(v);

        threadArr = new Thread[5];
        runnableArr = new Runnable[5];
        slotVolution = new boolean[]{false, false, false, false, false};

        initThreadArr();
        startThreadArr();
        click();

        setTxtBalanceProfitBet();

        return v;
    }

    private void initThreadArr() {
        for(int z = 0; z < 5; z++) {
            int finalZ = z;
            runnableArr[finalZ] = new Runnable() {
                @Override
                public void run() {
                    for(int i = 0; i < 3; i++) {
                        mcsFlipper[i][finalZ].showPrevious();
                    }
                }
            };
        }

        for(int w = 0; w < 5; w++) {
            int finalW = w;
            threadArr[finalW] = new Thread() {
                int[] velocity = new int[3];
                Random rnd = new Random();

                @Override
                public void run() {
                    while (true)
                    {
                        while (slotVolution[finalW]) {

                            try{
                                bet = convertTextToInt(txtBet.getText().toString());
                                balance = convertTextToInt(txtBalance.getText().toString());
                            }
                            catch (Exception ex){}

                            if (!(balance >= bet)) {
                                slotVolution[finalW] = false;
                                UiUtils.runOnUiThread(btnsOn);
                                break;
                            }

                            for(int i = 0; i < 3; i++) {
                                velocity[i] = rnd.nextInt(10) + 7;
                            }

                            try {
                                int pause = 300;
                                for (int n = 0; n < 3; ++n) {
                                    for (int j = 0; j < velocity[n]; ++j) {
                                        UiUtils.runOnUiThread(runnableArr[finalW]);
                                        sleep(pause);
                                    }
                                    pause += 50;
                                }
                            } catch (Exception ex) {
                            }

                            slotVolution[finalW] = false;
                            if(!checkSlotVolution()){
                                UiUtils.runOnUiThread(btnsOn);
                                UiUtils.runOnUiThread(verifyWin);
                            }
                        }
                    }
                }
            };
        }

        btnsOn = new Runnable() {
            @Override
            public void run() {
                if (!checkSlotVolution()) {
                    click();
                }
            }
        };

        verifyWin = new Runnable() {
            @Override
            public void run() {
                if (!checkSlotVolution()) {

                    int flip1 = (mcsFlipper[1][0].getDisplayedChild()+ 0) % 13;
                    int flip2 = (mcsFlipper[1][1].getDisplayedChild()+ 10) % 13;
                    int flip3 = (mcsFlipper[1][2].getDisplayedChild()+ 7) % 13;
                    int flip4 = (mcsFlipper[1][3].getDisplayedChild()+ 4) % 13;
                    int flip5 = (mcsFlipper[1][4].getDisplayedChild()+ 1) % 13;

                    Log.d("-+- Numbers -+-", "" + flip1 + " " + flip2 + " " + flip3 + " " + flip4 + " " + flip5);

                    int multKoef = -1;

                    List<Integer> listCombination = new ArrayList<>();
                    Collections.addAll(listCombination, flip1, flip2, flip3, flip4, flip5);
                    int coincidences = checkСoincidences((ArrayList<Integer>) listCombination);

                    switch (coincidences) {
                        case  1:
                            multKoef = 2;
                            break;
                        case 2:
                            multKoef = 4;
                            break;
                        case 3:
                            multKoef = 6;
                            break;
                        case 4:
                            multKoef = 10;
                            break;
                        default:
                            multKoef = -1;
                            break;
                    }


                    if(multKoef > 0){
                        profit = bet * multKoef;
                        balance += profit;
                    } else{
                        profit = 0;
                        balance -= bet;
                    }

                    if(balance <= 0) {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        balance = 1000;
                        Toast.makeText(getActivity(),"Try again. You have 1000 extra points!", Toast.LENGTH_SHORT).show();
                    }
                    if(bet > balance){
                        bet = balance;
                    }

                    setTxtBalanceProfitBet();

                    if(multKoef > 0 && multKoef <= 4){
                        bigWin.setVisibility(View.VISIBLE);
                    } else if(multKoef > 4 && multKoef <= 10){
                        giantWin.setVisibility(View.VISIBLE);
                    }

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(autoplay){
                        spinClick();
                    }
                }
            }
        };
    }

    private int checkСoincidences(ArrayList<Integer> list) {
        HashSet hs = new HashSet<Integer>();
        int countCons = 0;
        for (Integer lst : list) {
            if (hs.add(lst) == false) {
                countCons++;
            }
        }
        return countCons;
    }

    private boolean checkSlotVolution() {
        for(int i = 0; i < 5; i++){
            if(slotVolution[i] == true){
                return true;
            }
        }
        return false;
    }

    private int convertTextToInt(String txt) {
        return Integer.parseInt(txt.substring(0, txt.length() - 3));
    }

    private void startThreadArr() {
        for(int z = 0; z <5; z++){
            threadArr[z].start();
        }
    }

    private void initViews(View v) {
        txtBalance = v.findViewById(R.id.txt_balance);
        txtProfit = v.findViewById(R.id.txt_profit);
        txtBet = v.findViewById(R.id.txt_bet);

        infoButton = v.findViewById(R.id.info_button);
        autoSpinButton = v.findViewById(R.id.autospin_button);
        stopAutoSpinButton = v.findViewById(R.id.stop_autospin_button);
        spinButton = v.findViewById(R.id.spin_button);
        fastSpinButton = v.findViewById(R.id.fast_spin_button);
        betButton = v.findViewById(R.id.bet_button);

        bigWin = v.findViewById(R.id.big_win);
        giantWin = v.findViewById(R.id.giant_win);
    }

    private void fillFlippers(View v) {
        mcsFlipper = new ViewFlipper[][]{{v.findViewById(R.id.msc_vf_00), v.findViewById(R.id.msc_vf_01), v.findViewById(R.id.msc_vf_02), v.findViewById(R.id.msc_vf_03), v.findViewById(R.id.msc_vf_04)},
                                    {v.findViewById(R.id.msc_vf_10), v.findViewById(R.id.msc_vf_11), v.findViewById(R.id.msc_vf_12), v.findViewById(R.id.msc_vf_13), v.findViewById(R.id.msc_vf_14)},
                                    {v.findViewById(R.id.msc_vf_20), v.findViewById(R.id.msc_vf_21), v.findViewById(R.id.msc_vf_22), v.findViewById(R.id.msc_vf_23), v.findViewById(R.id.msc_vf_24)} };

        List<Integer> imgFlipper = new ArrayList<>(Arrays.asList(R.drawable.blue_box, R.drawable.candy_cane, R.drawable.clubs_tree_toy, R.drawable.ginger_bread,
                                                                 R.drawable.green_box, R.drawable.hearts_tree_toy, R.drawable.lollipop, R.drawable.purple_box,
                                                                 R.drawable.rabbit, R.drawable.red_box, R.drawable.rhombus_tree_toy, R.drawable.spades_tree_toy,
                                                                 R.drawable.yellow_box));

        for (int j = 0; j < 5; j++){
            for (int i = 0; i < 3; i++) {

                mcsFlipper[i][j].setInAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
                mcsFlipper[i][j].setOutAnimation(getActivity().getApplicationContext(), R.anim.slide_top);

                for (int k = 0; k < imgFlipper.size(); k++) {
                    ImageView img = new ImageView(getActivity());
                    img.setImageResource(imgFlipper.get(k));
                    mcsFlipper[i][j].addView(img);
                }
                Collections.rotate(imgFlipper, 1);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info_button:
                infoClick();
                break;
            case R.id.autospin_button:
                autoSpinClick();
                break;
            case R.id.stop_autospin_button:
                stopAutoSpinClick();
                break;
            case R.id.spin_button:
                spinClick();
                break;
            case R.id.fast_spin_button:
                fastSpinClick();
                break;
            case R.id.bet_button:
                betClick();
                break;
        }
    }

    private void click() {
        infoButton.setOnClickListener(this);
        autoSpinButton.setOnClickListener(this);
        stopAutoSpinButton.setOnClickListener(this);
        spinButton.setOnClickListener(this);
        fastSpinButton.setOnClickListener(this);
        betButton.setOnClickListener(this);
    }

    private void unclick() {
        spinButton.setOnClickListener(null);
        fastSpinButton.setOnClickListener(null);
        betButton.setOnClickListener(null);
    }

    private void betClick() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.christmas_container, new BetFragment(), "bettag");
        ft.commit();
    }

    private void fastSpinClick() {
        Handler h = new Handler();
        spinClick();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 5; i++){
                    slotVolution[i] = true;
                }
            }
        }, 1000);
    }

    private void spinClick() {
        unclick();
        bigWin.setVisibility(View.GONE);
        giantWin.setVisibility(View.GONE);
        profit = 0;
        setTxtBalanceProfitBet();

        for(int i = 0; i < 5; i++){
            slotVolution[i] = true;
        }
    }

    private void stopAutoSpinClick() {
        autoSpinButton.setVisibility(View.VISIBLE);
        stopAutoSpinButton.setVisibility(View.GONE);
        autoplay = false;
    }

    private void autoSpinClick() {
        autoSpinButton.setVisibility(View.GONE);
        stopAutoSpinButton.setVisibility(View.VISIBLE);
        autoplay = true;
        if(!checkSlotVolution()){
            spinClick();
        }
    }

    private void infoClick() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.christmas_container, new InfoFragment(), "infotag");
            ft.commit();
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        else {
            Toast.makeText(getActivity(), "Turn on Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTxtBalanceProfitBet(){
        txtBalance.setText(String.valueOf(balance) + ".00");
        txtProfit.setText(String.valueOf(profit) + ".00");
        txtBet.setText(String.valueOf(bet) + ".00");
    }
}