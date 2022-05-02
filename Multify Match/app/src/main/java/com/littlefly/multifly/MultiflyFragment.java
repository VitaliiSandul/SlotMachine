package com.littlefly.multifly;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

public class MultiflyFragment extends Fragment  implements View.OnClickListener{

    private String urlPP = "https://sites.google.com/view/privacyy-policyyy";
    private ImageView freeSpinImg;
    private ImageButton infoButton, minusButton, plusButton, maxBetButton, spinButton, stopAutoGameButton, autoGameButton;
    private TextView betTxt, freeSpinsTxt, winTxt, balanceTxt;
    private ViewFlipper[][] imgFly;
    private List<Integer> listImages;
    private double bet = 0.5, win = 0, balance = 100.00;
    private int freeSpins = 3;
    private boolean[] slotSpinner = new boolean[]{false, false, false, false, false};
    private boolean autoPlay = false, finish = false;
    private Thread[] flyThread = new Thread[5];
    private Runnable[] flyRunnable = new Runnable[5];
    private Runnable buttonsTurnOn, checkWin;

    public MultiflyFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mf = inflater.inflate(R.layout.fragment_multifly, container, false);

        initUIComponents(mf);
        setFont();
        fillImgFly();
        setBetWinBalanceFreeSpins();
        initFlyThreads();
        click();

        flyThread[0].start();
        flyThread[1].start();
        flyThread[2].start();
        flyThread[3].start();
        flyThread[4].start();

        return mf;
    }


    private void initUIComponents(View v) {
        freeSpinImg = v.findViewById(R.id.free_spin_img);
        infoButton = v.findViewById(R.id.info_button);
        minusButton = v.findViewById(R.id.minus_button);
        plusButton = v.findViewById(R.id.plus_button);
        maxBetButton = v.findViewById(R.id.max_bet_button);
        spinButton = v.findViewById(R.id.spin_button);
        stopAutoGameButton = v.findViewById(R.id.stop_auto_game_button);
        autoGameButton = v.findViewById(R.id.auto_game_button);
        betTxt = v.findViewById(R.id.bet_txt);
        freeSpinsTxt = v.findViewById(R.id.free_spins_txt);
        winTxt = v.findViewById(R.id.win_txt);
        balanceTxt = v.findViewById(R.id.balance_txt);

        imgFly = new ViewFlipper[][]{{v.findViewById(R.id.img_fly_00), v.findViewById(R.id.img_fly_01), v.findViewById(R.id.img_fly_02), v.findViewById(R.id.img_fly_03), v.findViewById(R.id.img_fly_04)},
                                    {v.findViewById(R.id.img_fly_10), v.findViewById(R.id.img_fly_11), v.findViewById(R.id.img_fly_12), v.findViewById(R.id.img_fly_13), v.findViewById(R.id.img_fly_14)},
                                    {v.findViewById(R.id.img_fly_20), v.findViewById(R.id.img_fly_21), v.findViewById(R.id.img_fly_22), v.findViewById(R.id.img_fly_23), v.findViewById(R.id.img_fly_24)} };


        listImages = new ArrayList<>(Arrays.asList(R.drawable.a, R.drawable.frog, R.drawable.j, R.drawable.k,
                                                    R.drawable.lizzard, R.drawable.nine, R.drawable.parrot, R.drawable.q,
                                                    R.drawable.scatter, R.drawable.ten, R.drawable.toucan, R.drawable.wild));

    }

    private void fillImgFly() {
        int currentIndex = 0;
        for (int j = 0; j < 5; j++){
            for (int i = 0; i < 3; i++) {

                imgFly[i][j].setInAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
                imgFly[i][j].setOutAnimation(getActivity().getApplicationContext(), R.anim.slide_down);

                for (int f = 0; f < listImages.size(); f++) {
                    ImageView img = new ImageView(getActivity());
                    img.setImageResource(listImages.get(currentIndex));
                    imgFly[i][j].addView(img);
                    currentIndex = (currentIndex + 1) % 11;
                }
                currentIndex++;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.minus_button:
                minusFunc();
                break;
            case R.id.plus_button:
                plusFunc();
                break;
            case R.id.max_bet_button:
                maxBetFunc();
                break;
            case R.id.spin_button:
                spinFunc();
                break;
            case R.id.auto_game_button:
                autoGameFunc();
                break;
            case R.id.stop_auto_game_button:
                stopAutoGameFunc();
                break;
            case R.id.info_button:
                infoFunc();
                break;
        }
    }

    private void click() {
        infoButton.setOnClickListener(this);
        minusButton.setOnClickListener(this);
        plusButton.setOnClickListener(this);
        maxBetButton.setOnClickListener(this);
        spinButton.setOnClickListener(this);
        autoGameButton.setOnClickListener(this);
        stopAutoGameButton.setOnClickListener(this);
    }

    private void unclick() {
        minusButton.setOnClickListener(null);
        plusButton.setOnClickListener(null);
        spinButton.setOnClickListener(null);
        maxBetButton.setOnClickListener(null);
    }

    private void minusFunc() {
        bet -= 0.50;
        if (bet <= 0)
            bet += 0.50;
        setBetWinBalanceFreeSpins();
    }

    private void plusFunc() {
        bet += 0.50;
        if (bet > balance)
            bet -= 0.50;
        setBetWinBalanceFreeSpins();
    }

    private void maxBetFunc() {
        bet = balance;
        setBetWinBalanceFreeSpins();
    }

    private void spinFunc() {

        unclick();
        freeSpinImg.setVisibility(View.GONE);
        win = 0;

        if(freeSpins > 0){
            freeSpins--;
        }

        setBetWinBalanceFreeSpins();

        for(int i = 0; i < 5; i++){
            slotSpinner[i] = true;
        }
    }

    private void autoGameFunc() {
        autoGameButton.setVisibility(View.GONE);
        autoPlay = true;
        if(!isSinnersMoving()){
            spinFunc();
        }
    }

    private void stopAutoGameFunc() {
        autoGameButton.setVisibility(View.VISIBLE);
        autoPlay = false;
    }

    private void infoFunc() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.multifly_container, new PrivPolFragment(urlPP), "pptag");
            ft.commit();
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            //Testing Crushlytics
            //throw new RuntimeException("Test Crash");
        }
        else {
            Toast.makeText(getActivity(), "Can't load Privacy Policy, no Internet connection", Toast.LENGTH_LONG).show();
        }
    }

    private void setBetWinBalanceFreeSpins() {
        betTxt.setText(String.valueOf(bet));
        winTxt.setText(String.valueOf(win));
        balanceTxt.setText(String.valueOf(balance));
        freeSpinsTxt.setText(String.valueOf(freeSpins));
    }

    private boolean isSinnersMoving(){
        for(int index = 0; index < 5; index++){
            if(slotSpinner[index] == true){
                return true;
            }
        }
        return false;
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/font1_14062.otf");
        betTxt.setTypeface(font);
        freeSpinsTxt.setTypeface(font);
        winTxt.setTypeface(font);
        balanceTxt.setTypeface(font);
    }

    private void initFlyThreads() {

        for(int k = 0; k < 5; k++) {
            int finalK = k;
            flyRunnable[finalK] = new Runnable() {
                @Override
                public void run() {

                    if(finalK == 0 || finalK == 4 ) {
                        for(int i = 0; i < 3; i++) {
                            imgFly[i][finalK].showNext();
                        }
                    }
                    else{
                        for(int i = 0; i < 3; i++) {
                            imgFly[i][finalK].showPrevious();
                        }
                    }
                }
            };
        }

        for(int m = 0; m < 5; m++) {
            int finalM = m;
            flyThread[finalM] = new Thread() {
                Random random = new Random();
                int [] speed = new int[3];

                @Override
                public void run() {
                    while (!finish) {
                        while (slotSpinner[finalM]) {
                            bet = Double.parseDouble(betTxt.getText().toString());
                            balance = Double.parseDouble(balanceTxt.getText().toString());
                            if (!(balance >= bet)) {
                                slotSpinner[finalM] = false;
                                Helpfulness.runOnUiThread(buttonsTurnOn);
                                break;
                            }

                            for(int i = 0; i < 3; i++) {
                                speed[i] = random.nextInt(7) + 7;
                            }

                            try {
                                int delay = 250;
                                for (int n = 0; n < 3; ++n) {
                                    for (int j = 0; j < speed[n]; ++j) {
                                        Helpfulness.runOnUiThread(flyRunnable[finalM]);
                                        sleep(delay);
                                    }
                                    delay += 70;
                                }
                            } catch (Exception ex) {
                            }

                            slotSpinner[finalM] = false;
                            if(!isSinnersMoving()){
                                Helpfulness.runOnUiThread(buttonsTurnOn);
                                Helpfulness.runOnUiThread(checkWin);
                            }
                        }
                    }
                }
            };
        }


        buttonsTurnOn = new Runnable() {
            @Override
            public void run() {
                if (!isSinnersMoving()) {
                    click();
                }
            }
        };

        checkWin = new Runnable() {
            @Override
            public void run() {
                if (!isSinnersMoving()) {

                    int iFly5 = (imgFly[1][0].getDisplayedChild()+ 3) % 11;
                    int iFly4 = (imgFly[1][1].getDisplayedChild()+ 9) % 11;
                    int iFly3 = (imgFly[1][2].getDisplayedChild()+ 4) % 11;
                    int iFly2 = (imgFly[1][3].getDisplayedChild()+ 10) % 11;
                    int iFly1 = (imgFly[1][4].getDisplayedChild()+ 5) % 11;

                    Log.d("Win Combination", "" + iFly5 + " " + iFly4 + " " + iFly3 + " " + iFly2 + " " + iFly1);

                    int coefficient = -1;

                    List<Integer> centralLineNumbers = new ArrayList<>();
                    Collections.addAll(centralLineNumbers, iFly1, iFly2, iFly3, iFly4, iFly5);
                    int meetAgains = findMeetAgains((ArrayList<Integer>) centralLineNumbers);

                    switch (meetAgains) {
                        case  1:
                            coefficient = 2;
                            break;
                        case 2:
                            coefficient = 3;
                            break;
                        case 3:
                            coefficient = 4;
                            break;
                        case 4:
                            coefficient = 7;
                            break;
                        default:
                            coefficient = -1;
                            break;
                    }

                    freeSpins = Integer.parseInt(freeSpinsTxt.getText().toString());

                    if(coefficient > 0){
                        win = bet * coefficient;
                        balance += win;
                    }
                    else if(freeSpins > 0){
                        win = 0;
                    }
                    else{
                        win = 0;
                        balance -= bet;
                    }

                    if(balance <= 0) {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        balance = 1000.0;
                        Toast.makeText(getActivity(),"Take 1000 extra points!", Toast.LENGTH_LONG).show();
                    }
                    if(bet > balance){
                        bet = balance;
                    }


                    if(coefficient > 3){
                        freeSpinImg.setVisibility(View.VISIBLE);
                        freeSpins += 10;
                    }

                    setBetWinBalanceFreeSpins();


                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(autoPlay){
                        spinFunc();
                    }
                }
            }
        };
    }

    private int findMeetAgains(ArrayList<Integer> numbers){
        HashSet setSimilars = new HashSet<Integer>();
        int sumMeetAgains = 0;
        for (Integer number : numbers) {
            if (setSimilars.add(number) == false) {
                sumMeetAgains++;
            }
        }
        return sumMeetAgains;
    }
}