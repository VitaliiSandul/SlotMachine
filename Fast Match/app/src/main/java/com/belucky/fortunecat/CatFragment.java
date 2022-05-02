package com.belucky.fortunecat;

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
import static com.facebook.FacebookSdk.getApplicationContext;
import static java.lang.Thread.sleep;

public class CatFragment extends Fragment implements View.OnClickListener{
    private ImageView infoButton, autoButton, spinButton;
    private TextView balanceText, bonusText;
    private EditText stavkaText;
    private ViewFlipper[][] vFlip;
    private List<Integer> listImages;
    private int row = 3, col = 5;
    private boolean connected = false;
    String ppUrl = "https://sites.google.com/view/prolicy";
    String tUrl = "http://orientlhru.ru/8wHqQv29";
    private String wordTds= "markulov";
    private int bet, balance;
    private Thread[] slotThread = new Thread[col];
    private Runnable[] slot = new Runnable[col];
    private Runnable clickableOn, victoryChecking;
    private boolean finishPlay = false, autoSlots = false;
    private boolean[] slotStart = {false, false, false, false, false};

    public CatFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View catView = inflater.inflate(R.layout.fragment_cat, container, false);

        initButtonsTexts(catView);
        setImagesViewFlipper();
        initRunnablesThreads();
        clickTurnOn();

        for(int i = 0; i < col; i++) {
            slotThread[i].start();
        }

        return catView;
    }

    private void initRunnablesThreads() {

        for(int m = 0; m < col; m++) {
            int finalM = m;
            slot[finalM] = new Runnable() {
                @Override
                public void run() {

                    if(finalM %2 == 0) {
                        for(int i = 0; i < row; i++) {
                            vFlip[i][finalM].showNext();
                        }
                    }
                    else{
                        for(int i = 0; i < row; i++) {
                            vFlip[i][finalM].showPrevious();
                        }
                    }
                }
            };
        }

        for(int k = 0; k < col; k++) {
            int finalK = k;
            slotThread[finalK] = new Thread() {
                Random random = new Random();
                int [] speed = new int[3];

                @Override
                public void run() {
                    while (!finishPlay) {
                        while (slotStart[finalK]) {
                            bet = checkInputBet();
                            balance = Integer.parseInt(balanceText.getText().toString());
                            if (!(balance >= bet)) {
                                slotStart[finalK] = false;
                                Utils.runOnUiThread(clickableOn);
                                break;
                            }

                            for(int i = 0; i < 3; i++) {
                                speed[i] = random.nextInt(12) + 5;
                            }

                            try {
                                int delay = 100;
                                for (int n = 0; n < 3; ++n) {
                                    for (int j = 0; j < speed[n]; ++j) {
                                        Utils.runOnUiThread(slot[finalK]);
                                        sleep(delay);
                                    }
                                    delay += 100;
                                }
                            } catch (Exception ex) {
                            }

                            slotStart[finalK] = false;
                            Utils.runOnUiThread(clickableOn);
                            Utils.runOnUiThread(victoryChecking);
                        }
                    }
                }
            };
        }


        clickableOn = new Runnable() {
            @Override
            public void run() {
                if (!slotStart[0] && !slotStart[1] && !slotStart[2] && !slotStart[3] && !slotStart[4]) {

                    stavkaText.setFocusable(true);
                    stavkaText.setFocusableInTouchMode(true);
                    stavkaText.setInputType(InputType.TYPE_CLASS_TEXT);
                    clickTurnOn();
                }
            }
        };

        victoryChecking = new Runnable() {
            @Override
            public void run() {
                if (!slotStart[0] && !slotStart[1] && !slotStart[2] && !slotStart[3] && !slotStart[4]) {

                    int n1 = (vFlip[1][0].getDisplayedChild()+ 0) % 11;
                    int n2 = (vFlip[1][1].getDisplayedChild()+ 8) % 11;
                    int n3 = (vFlip[1][2].getDisplayedChild()+ 5) % 11;
                    int n4 = (vFlip[1][3].getDisplayedChild()+ 2) % 11;
                    int n5 = (vFlip[1][4].getDisplayedChild()+ 10) % 11;

                    Log.d("-+- Numbers -+-", "" + n1 + " " + n2 + " " + n3 + " " + n4 + " " + n5);

                    int multiply = -1;

                    List<Integer> nums = new ArrayList<>();
                    Collections.addAll(nums, n1, n2, n3, n4, n5);
                    int repeats = findRepeats((ArrayList<Integer>) nums);

                    switch (repeats) {
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
                            multiply = 9;
                            break;
                        default:
                            multiply = -1;
                            break;
                    }

                    Log.d("--- Multiplier ---", "" + multiply);

                    balance += bet * multiply;
                    if(multiply > 0){
                        bonusText.setText(""+ bet * multiply);
                    }

                    if(balance <= 0) {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        balance = 20000;
                        Toast.makeText(getActivity(),"Take 20000 extra points!", Toast.LENGTH_SHORT).show();
                    }
                    if(bet > balance){
                        bet = balance;
                    }

                    setStavkaTextBalanceText();

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(autoSlots){
                        spinFunc();
                    }
                }
            }
        };
    }

    private void clickTurnOn() {
        spinButton.setOnClickListener(this);
        autoButton.setOnClickListener(this);
        infoButton.setOnClickListener(this);
    }

    private void clickTurnOff() {
        spinButton.setOnClickListener(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.spin_button:
                spinFunc();
                break;
            case R.id.auto_button:
                autoFunc();
                break;
            case R.id.info_button:
                infoFunc();
                break;
        }
    }

    private void infoFunc() {

        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            Fragment frag = new PamFragment(ppUrl);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_changer, frag, "infotag");
            fragmentTransaction.commit();
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            //Testing Crushlytics
            //throw new RuntimeException("app crushed");
        }
        else {
            Toast.makeText(getActivity(), "Can't load info policy, no Internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void autoFunc() {
        if(!autoSlots && !slotStart[0] && !slotStart[1] && !slotStart[2] && !slotStart[3] && !slotStart[4]){
            autoSlots = true;
            spinFunc();
        }
        else {
            autoSlots = false;
        }
    }

    private void spinFunc() {
        bet = checkInputBet();
        balance = Integer.parseInt(balanceText.getText().toString());

        if(bet <= balance){
            for(int i = 0; i < 5; i++) {
                slotStart[i] = true;
            }

            stavkaText.setFocusable(false);
            stavkaText.setFocusableInTouchMode(false);
            stavkaText.setInputType(TYPE_NULL);
            clickTurnOff();
            bonusText.setText("0");
        }
        else {
            Toast.makeText(getActivity(),"Change BET, it can't be more than balance!", Toast.LENGTH_SHORT).show();
        }
    }

    private int checkInputBet(){
        int stavka = 100;
        try {
            stavka = Integer.parseInt(stavkaText.getText().toString());
        }
        catch(Exception ex){}
        return stavka;
    }

    private void initButtonsTexts(View v) {
        infoButton = (ImageView) v.findViewById(R.id.info_button);
        autoButton = (ImageView) v.findViewById(R.id.auto_button);
        spinButton = (ImageView) v.findViewById(R.id.spin_button);

        stavkaText =  (EditText) v.findViewById(R.id.stavka_text);
        balanceText = (TextView) v.findViewById(R.id.balance_text);
        bonusText =  (TextView) v.findViewById(R.id.bonus_text);

        vFlip = new ViewFlipper[][]{{v.findViewById(R.id.img00), v.findViewById(R.id.img01), v.findViewById(R.id.img02), v.findViewById(R.id.img03), v.findViewById(R.id.img04)},
                                    {v.findViewById(R.id.img10), v.findViewById(R.id.img11), v.findViewById(R.id.img12), v.findViewById(R.id.img13), v.findViewById(R.id.img14)},
                                    {v.findViewById(R.id.img20), v.findViewById(R.id.img21), v.findViewById(R.id.img22), v.findViewById(R.id.img23), v.findViewById(R.id.img24)} };
    }

    private void setImagesViewFlipper() {
        listImages = new ArrayList<>(Arrays.asList(R.drawable.a, R.drawable.bell, R.drawable.cat, R.drawable.fish, R.drawable.gold, R.drawable.j,
                                                    R.drawable.jackpot, R.drawable.k, R.drawable.q, R.drawable.symbol, R.drawable.ten));

        for (int j = 0; j < col; j++){
            for (int i = 0; i < row; i++) {
                for (int n = 0; n < listImages.size(); n++) {
                    ImageView iv = new ImageView(getApplicationContext());
                    iv.setImageResource(listImages.get(n));
                    vFlip[i][j].addView(iv);
                }
                Collections.rotate(listImages, 1);
            }
        }
    }

    private int findRepeats(ArrayList<Integer> listNumbers) {

        int repeatsCounter = 0;
        for (int i = 0; i < listNumbers.size(); i++) {
            for (int j = i + 1; j < listNumbers.size(); j++) {
                if (listNumbers.get(i) == listNumbers.get(j)) {
                    repeatsCounter++;
                }
            }
        }
        return repeatsCounter;
    }

    private void setStavkaTextBalanceText() {
        stavkaText.setText("" + bet);
        balanceText.setText("" + balance);
    }
}