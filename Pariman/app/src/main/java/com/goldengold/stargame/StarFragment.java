package com.goldengold.stargame;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static android.text.InputType.TYPE_NULL;
import static java.lang.Thread.sleep;

public class StarFragment extends Fragment {

    private ImageButton infoBtn, autoBtn, spinBtn;
    private TextView balanceTxt, bonusTxt, stavkaTxt;
    private ViewFlipper[][] vfImage;
    private List<Integer> symbol;
    private int numThreads = 5;
    private Thread[] thread;
    private Runnable[] runnable;
    private Runnable clickActive, checkCombination;
    private boolean finish = false, autoSpin = false;
    private boolean rotation[] = {false, false, false, false, false};
    private int speed = 5;
    private int currentBet = 100, currentBalance = 5000;

    public StarFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View starView = inflater.inflate(R.layout.fragment_star, container, false);

        initializeComponents(starView);
        initializeViewFlipers(starView);
        initializeThreadsRunnables();
        initializeButtonsClick();

        for(int i = 0; i < numThreads; i++) {
            thread[i].start();
        }

        return starView;
    }

    private void initializeButtonsClick() {
        initializeSpinButtonClick();
        initializeAutoButtonClick();
        initializeInfoButtonClick();
    }


    private void initializeThreadsRunnables() {

        runnable = new Runnable[numThreads];
        for(int i = 0; i < numThreads; i++) {
            int finalI = i;
            runnable[finalI] = new Runnable() {
                @Override
                public void run() {

                    if(finalI == 1 || finalI == 2 || finalI == 3) {
                        vfImage[0][finalI].showPrevious();
                        vfImage[1][finalI].showPrevious();
                        vfImage[2][finalI].showPrevious();
                    }
                    else{
                        vfImage[0][finalI].showNext();
                        vfImage[1][finalI].showNext();
                        vfImage[2][finalI].showNext();
                    }
                }
            };
        }


        thread = new Thread[numThreads];
        for(int i = 0; i < numThreads; i++) {
            int finalI = i;
            thread[finalI] = new Thread() {


                @Override
                public void run() {
                    Random random = new Random();
                    while (!finish) {
                        while (rotation[finalI]) {
                            currentBet = getBetSafety();
                            currentBalance = Integer.parseInt(balanceTxt.getText().toString());
                            if (currentBalance <= currentBet) {
                                rotation[finalI] = false;
                                Utils.runOnUiThread(clickActive);
                                break;
                            }

                            speed = random.nextInt(15) + 5;
                            try {
                                for (int j = 0; j < speed; ++j) {
                                    Utils.runOnUiThread(runnable[finalI]);
                                    sleep(300);
                                }
                            } catch (Exception ex) {
                            }

                            rotation[finalI] = false;
                            if (checkStopRotation()){
                                Utils.runOnUiThread(clickActive);
                                Utils.runOnUiThread(checkCombination);
                            }
                        }
                    }
                }
            };
        }


        clickActive = new Runnable() {
            @Override
            public void run() {
                if (checkStopRotation()) {
                    stavkaTxt.setFocusable(true);
                    stavkaTxt.setFocusableInTouchMode(true);
                    stavkaTxt.setInputType(InputType.TYPE_CLASS_TEXT);
                    spinBtn.setClickable(true);
                }
            }
        };

        checkCombination = new Runnable() {
            @Override
            public void run() {
                Log.d("checkStopRotation()", String.valueOf(checkStopRotation()));
                int number1 = (vfImage[1][0].getDisplayedChild()+ 0) % 10;
                int number2 = (vfImage[1][1].getDisplayedChild()+ 7) % 10;
                int number3 = (vfImage[1][2].getDisplayedChild()+ 4) % 10;
                int number4 = (vfImage[1][3].getDisplayedChild()+ 1) % 10;
                int number5 = (vfImage[1][4].getDisplayedChild()+ 8) % 10;

                Log.d("--- Picture Numbers ---", "" + number1 + " " + number2 + " " + number3 + " " + number4 + " " + number5);

                int baseMultiplier = -1;

                List<Integer> numbers = new ArrayList<>();
                Collections.addAll(numbers, number1, number2, number3, number4, number5);
                int duplicates = duplicate((ArrayList<Integer>) numbers);

                switch (duplicates) {
                    case 1:
                        baseMultiplier = 2;
                        break;
                    case 2:
                        baseMultiplier = 5;
                        break;
                    case 3:
                        baseMultiplier = 7;
                        break;
                    case 4:
                        baseMultiplier = 10;
                        break;
                    default:
                        baseMultiplier = -1;
                        break;
                }

                Log.d("--- Multiplier ---", "" + baseMultiplier);

                currentBalance += currentBet * baseMultiplier;
                if(baseMultiplier > 0){
                    bonusTxt.setText(""+ currentBet * baseMultiplier);
                }

                if(currentBalance <= 0) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    currentBalance = 5000;
                    Toast.makeText(getActivity(),"You have 5000 points!", Toast.LENGTH_SHORT).show();
                }
                if(currentBalance < currentBet){
                    currentBet = currentBalance;
                }

                balanceTxt.setText("" + currentBalance);
                stavkaTxt.setText("" + currentBet);

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(autoSpin){
                    spin();
                }
            }
        };
    }

    private void initializeInfoButtonClick() {

        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                if(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                    Fragment fr = new InfoFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.star_container, fr, "infotag");
                    fragmentTransaction.commit();
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                    //throw new RuntimeException("Test Crash");
                }
                else{
                    Toast.makeText(getActivity(), "Please TURN ON Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeSpinButtonClick() {
        spinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spin();
            }
        });
    }

    private void initializeAutoButtonClick() {
        autoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoPlay();
            }
        });
    }

    private void spin() {
        currentBet = getBetSafety();
        currentBalance = Integer.parseInt(balanceTxt.getText().toString());

        if(currentBet <= currentBalance){
            for(int i = 0; i < numThreads; i++) {
                rotation[i] = true;
            }
            stavkaTxt.setFocusable(false);
            stavkaTxt.setFocusableInTouchMode(false);
            stavkaTxt.setInputType(TYPE_NULL);
            spinBtn.setClickable(false);
            bonusTxt.setText("0");
        }
        else {
            Toast.makeText(getActivity(),"BET can't be more than balance!", Toast.LENGTH_SHORT).show();
        }
    }

    private int getBetSafety() {
        int safetyBet = 50;
        try {
            safetyBet = Integer.parseInt(stavkaTxt.getText().toString());
        }
        catch(Exception ex){}
        return safetyBet;
    }

    private void initializeComponents(View v) {
        infoBtn = (ImageButton) v.findViewById(R.id.info_btn);
        autoBtn = (ImageButton) v.findViewById(R.id.auto_btn);
        spinBtn = (ImageButton) v.findViewById(R.id.spin_btn);
        balanceTxt = (TextView) v.findViewById(R.id.balance_txt);
        bonusTxt = (TextView) v.findViewById(R.id.bonus_txt);
        stavkaTxt = (TextView) v.findViewById(R.id.stavka_txt);
    }

    private void initializeViewFlipers(View v) {
        vfImage = new ViewFlipper[][]{{v.findViewById(R.id.vfImage00), v.findViewById(R.id.vfImage01), v.findViewById(R.id.vfImage02), v.findViewById(R.id.vfImage03), v.findViewById(R.id.vfImage04)},
                {v.findViewById(R.id.vfImage10), v.findViewById(R.id.vfImage11), v.findViewById(R.id.vfImage12), v.findViewById(R.id.vfImage13), v.findViewById(R.id.vfImage14)},
                {v.findViewById(R.id.vfImage20), v.findViewById(R.id.vfImage21), v.findViewById(R.id.vfImage22), v.findViewById(R.id.vfImage23), v.findViewById(R.id.vfImage24)} };

        symbol = new ArrayList<>(Arrays.asList(R.drawable.a, R.drawable.eyeglases, R.drawable.gold_star, R.drawable.j, R.drawable.k,
                R.drawable.microphone, R.drawable.q, R.drawable.record, R.drawable.ten, R.drawable.turntable));

        int rd =3, st = 5;
        for (int j = 0; j < st; j++){
            for (int i = 0; i < rd; i++) {
                for (int k = 0; k < symbol.size(); k++) {
                    ImageView img = new ImageView(getActivity());
                    img.setImageResource(symbol.get(k));
                    vfImage[i][j].addView(img);
                }
                Collections.rotate(symbol, 1);
            }
        }
    }

    private int duplicate(ArrayList<Integer> arr) {

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

    private void autoPlay() {

        if(checkStopRotation()){
            autoSpin = true;
            spin();
        }
        else {
            autoSpin = false;
        }
    }

    private boolean checkStopRotation(){
        boolean stopRotation = true;
        for(int i = 0 ; i < numThreads; i++){
            if(rotation[i] == true){
                return false;
            }
        }
        return stopRotation;
    }


}