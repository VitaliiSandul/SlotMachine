package com.likemusic.chillipop;

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

public class ChilliFragment extends Fragment implements View.OnClickListener{

    private static ChilliFragment instanceChilliFragment;
    static int aditionalMultiplier = 1;
    static double balance = 100.00;
    private ViewFlipper[][] vfImage;
    private TextView betTxt, xTxt, prizeTxt, balanceTxt, aditionalMultiplierTxt;
    private ImageView infoButton, minusButton, plusButton, stopAutoGameButton, autoGameButton, spinButton, bonusButton, youWon, hugewinCaption, megawinCaption;
    private String urlPrivPol = "https://sites.google.com/view/prolici";
    private double bet = 0.50, prize = 0.00;
    private Thread[] slotTrd;
    private Runnable[] number;
    private Runnable clickButtonsOn, winChecking;
    private boolean[] slotRotate;
    private boolean theEnd = false, autoGame = false;

    public ChilliFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chilli, container, false);

        fillViewFlippers(v);
        initComponents(v);
        setTextFont();
        slotTrd = new Thread[5];
        number = new Runnable[5];
        slotRotate = new boolean[]{false, false, false, false, false};
        initSlotThreads();
        startSlotThreads();
        click();

        instanceChilliFragment = this;

        setTxtBetBalancePrizeAditionalMultiplier();

        return v;
    }

    public static ChilliFragment getInstanceChilliFragment() {
        return instanceChilliFragment;
    }

    private void fillViewFlippers(View v) {
        vfImage = new ViewFlipper[][]{{v.findViewById(R.id.image1), v.findViewById(R.id.image4), v.findViewById(R.id.image7), v.findViewById(R.id.image10), v.findViewById(R.id.image13)},
                                    {v.findViewById(R.id.image2), v.findViewById(R.id.image5), v.findViewById(R.id.image8), v.findViewById(R.id.image11), v.findViewById(R.id.image14)},
                                    {v.findViewById(R.id.image3), v.findViewById(R.id.image6), v.findViewById(R.id.image9), v.findViewById(R.id.image12), v.findViewById(R.id.image15)} };


        List<Integer> images = new ArrayList<>(Arrays.asList(R.drawable.donkey, R.drawable.green_onions, R.drawable.green_sweet_pepper,
                                                            R.drawable.orange_chili_pepper, R.drawable.orange_sweet_pepper, R.drawable.pinata,
                                                            R.drawable.purple_onion, R.drawable.red_sweet_pepper, R.drawable.tomato,
                                                            R.drawable.yellow_chili_pepper));

        for (int j = 0; j < 5; j++){
            for (int i = 0; i < 3; i++) {

                vfImage[i][j].setInAnimation(getActivity().getApplicationContext(), R.anim.slide_top);
                vfImage[i][j].setOutAnimation(getActivity().getApplicationContext(), R.anim.slide_bottom);

                for (int n = 0; n < images.size(); n++) {
                    ImageView iv = new ImageView(getActivity());
                    iv.setImageResource(images.get(n));
                    vfImage[i][j].addView(iv);
                }
                Collections.rotate(images, 1);
            }
        }
    }



    private void initComponents(View v){
        betTxt = v.findViewById(R.id.bet_txt);
        xTxt = v.findViewById(R.id.x_txt);
        prizeTxt = v.findViewById(R.id.prize_txt);
        balanceTxt = v.findViewById(R.id.balance_txt);
        aditionalMultiplierTxt = v.findViewById(R.id.aditional_multiplier_txt);

        infoButton = v.findViewById(R.id.info_button);
        minusButton = v.findViewById(R.id.minus_button);
        plusButton = v.findViewById(R.id.plus_button);
        stopAutoGameButton = v.findViewById(R.id.stop_auto_game_button);
        autoGameButton = v.findViewById(R.id.auto_game_button);
        spinButton = v.findViewById(R.id.spin_button);
        bonusButton = v.findViewById(R.id.bonus_button);

        youWon = v.findViewById(R.id.you_won);
        hugewinCaption = v.findViewById(R.id.hugewin_caption);
        megawinCaption = v.findViewById(R.id.megawin_caption);
    }


    private void initSlotThreads() {

        for(int i = 0; i < 5; i++) {
            int finalI = i;
            number[finalI] = new Runnable() {
                @Override
                public void run() {

                    if(finalI %2 == 0) {
                        for(int i = 0; i < 3; i++) {
                            vfImage[i][finalI].showPrevious();
                        }
                    }
                    else{
                        for(int i = 0; i < 3; i++) {
                            vfImage[i][finalI].showNext();
                        }
                    }
                }
            };
        }

        for(int m = 0; m < 5; m++) {
            int finalM = m;
            slotTrd[finalM] = new Thread() {
                Random random = new Random();
                int [] speed = new int[3];

                @Override
                public void run() {
                    while (!theEnd) {
                        while (slotRotate[finalM]) {
                            bet = Double.parseDouble(betTxt.getText().toString());
                            balance = Double.parseDouble(balanceTxt.getText().toString());
                            if (!(balance >= bet)) {
                                slotRotate[finalM] = false;
                                UtilsHelp.runOnUiThread(clickButtonsOn);
                                break;
                            }

                            for(int i = 0; i < 3; i++) {
                                speed[i] = random.nextInt(5) + 5;
                            }

                            try {
                                int delay = 300;
                                for (int n = 0; n < 3; ++n) {
                                    for (int j = 0; j < speed[n]; ++j) {
                                        UtilsHelp.runOnUiThread(number[finalM]);
                                        sleep(delay);
                                    }
                                    delay += 50;
                                }
                            } catch (Exception ex) {
                            }

                            slotRotate[finalM] = false;
                            if(!checkSlotRotate()){
                                UtilsHelp.runOnUiThread(clickButtonsOn);
                                UtilsHelp.runOnUiThread(winChecking);
                            }
                        }
                    }
                }
            };
        }


        clickButtonsOn = new Runnable() {
            @Override
            public void run() {
                if (!checkSlotRotate()) {
                    click();
                }
            }
        };

        winChecking = new Runnable() {
            @Override
            public void run() {
                if (!checkSlotRotate()) {

                    int imgNum1 = (vfImage[1][0].getDisplayedChild()+ 0) % 10;
                    int imgNum2 = (vfImage[1][1].getDisplayedChild()+ 7) % 10;
                    int imgNum3 = (vfImage[1][2].getDisplayedChild()+ 4) % 10;
                    int imgNum4 = (vfImage[1][3].getDisplayedChild()+ 1) % 10;
                    int imgNum5 = (vfImage[1][4].getDisplayedChild()+ 8) % 10;

                    Log.d("-+- Numbers -+-", "" + imgNum1 + " " + imgNum2 + " " + imgNum3 + " " + imgNum4 + " " + imgNum5);

                    int multiply = -1;

                    List<Integer> nums = new ArrayList<>();
                    Collections.addAll(nums, imgNum1, imgNum2, imgNum3, imgNum4, imgNum5);
                    int similars = findSimilars((ArrayList<Integer>) nums);

                    switch (similars) {
                        case  1:
                            multiply = 3;
                            break;
                        case 2:
                            multiply = 5;
                            break;
                        case 3:
                            multiply = 7;
                            break;
                        case 4:
                            multiply = 10;
                            break;
                        default:
                            multiply = -1;
                            break;
                    }

                    aditionalMultiplier = Integer.parseInt(aditionalMultiplierTxt.getText().toString());
                    Log.d("--- Multiplier ---", "" + multiply);

                    if(multiply > 0){
                        prize = bet * multiply * aditionalMultiplier;
                        balance += prize;
                        prizeTxt.setText(""+ bet * multiply);
                    } else{
                        prize = 0;
                        balance -= bet;
                    }

                    if(balance <= 0) {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        balance = 1000.0;
                        Toast.makeText(getActivity(),"Take 20000 extra points!", Toast.LENGTH_SHORT).show();
                    }
                    if(bet > balance){
                        bet = balance;
                    }

                    setTxtBetBalancePrizeAditionalMultiplier();

                    if(multiply > 0 && multiply <= 5){
                        youWon.setVisibility(View.VISIBLE);
                    } else if(multiply > 5 && multiply <= 9){
                        hugewinCaption.setVisibility(View.VISIBLE);
                    } else if(multiply > 9){
                        megawinCaption.setVisibility(View.VISIBLE);
                    }

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    setTxtBetBalancePrizeAditionalMultiplier();
                    aditionalMultiplier = 1;

                    if(autoGame){
                        spinButtonClick();
                    }
                }
            }
        };
    }

    private void startSlotThreads(){
        for(int i = 0; i <5; i++){
            slotTrd[i].start();
        }
    }

    private int findSimilars(ArrayList<Integer> nums) {
        HashSet setSimilars = new HashSet<Integer>();
        int counter = 0;
        for (Integer num : nums) {
            if (setSimilars.add(num) == false) {
                counter++;
            }
        }
        return counter;
    }

    private boolean checkSlotRotate(){

        for(int i = 0; i < 5; i++){
            if(slotRotate[i] == true){
                return true;
            }
        }

        return false;
    }

    private void setTextFont() {
        Typeface westernFont = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Rawhide.otf");
        betTxt.setTypeface(westernFont);
        xTxt.setTypeface(westernFont);
        aditionalMultiplierTxt.setTypeface(westernFont);
        prizeTxt.setTypeface(westernFont);
        balanceTxt.setTypeface(westernFont);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.info_button:
                infoButtonClick();
                break;
            case R.id.minus_button:
                minusButtonClick();
                break;
            case R.id.plus_button:
                plusButtonClick();
                break;
            case R.id.stop_auto_game_button:
                stopAutoGameButtonClick();
                break;
            case R.id.auto_game_button:
                autoGameButtonClick();
                break;
            case R.id.spin_button:
                spinButtonClick();
                break;
            case R.id.bonus_button:
                bonusButtonClick();
                break;
        }
    }

    private void infoButtonClick() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new NetFragment(urlPrivPol), "infotag");
            ft.commit();
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            //Testing Crushlytics
            //throw new RuntimeException("app crushed");
        }
        else {
            Toast.makeText(getActivity(), "Can't load Privacy Policy, no Internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void minusButtonClick() {
        bet -= 0.50;
        if (bet <= 0)
            bet += 0.50;
        setTxtBetBalancePrizeAditionalMultiplier();
    }

    private void plusButtonClick() {
        bet += 0.50;
        if (bet > balance)
            bet -= 0.50;
        setTxtBetBalancePrizeAditionalMultiplier();
    }

    private void stopAutoGameButtonClick() {
        autoGameButton.setVisibility(View.VISIBLE);
        autoGame = false;
    }

    private void autoGameButtonClick() {
        autoGameButton.setVisibility(View.GONE);
        autoGame = true;
        if(!checkSlotRotate()){
            spinButtonClick();
        }
    }

    private void spinButtonClick() {
        unclick();
        turnOffPrizePicture();
        setTxtBetBalancePrizeAditionalMultiplier();
        for(int i = 0; i < 5; i++){
            slotRotate[i] = true;
        }
        prizeTxt.setText("0");
    }

    private void bonusButtonClick() {
        instanceChilliFragment = this;
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, new BonusFragment(), "bonustag");
        fragmentTransaction.commit();
    }

    private void click() {
        infoButton.setOnClickListener(this);
        minusButton.setOnClickListener(this);
        plusButton.setOnClickListener(this);
        autoGameButton.setOnClickListener(this);
        stopAutoGameButton.setOnClickListener(this);
        spinButton.setOnClickListener(this);
        bonusButton.setOnClickListener(this);
    }

    private void unclick() {
        minusButton.setOnClickListener(null);
        plusButton.setOnClickListener(null);
        spinButton.setOnClickListener(null);
        bonusButton.setOnClickListener(null);
    }

    private void setTxtBetBalancePrizeAditionalMultiplier(){
        betTxt.setText(String.valueOf(bet));
        balanceTxt.setText(String.valueOf(balance));
        prizeTxt.setText(String.valueOf(prize));
        aditionalMultiplierTxt.setText(String.valueOf(aditionalMultiplier));
    }

    private void turnOffPrizePicture(){
        youWon.setVisibility(View.GONE);
        hugewinCaption.setVisibility(View.GONE);
        megawinCaption.setVisibility(View.GONE);
    }
}