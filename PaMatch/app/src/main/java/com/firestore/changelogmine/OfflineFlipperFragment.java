package com.firestore.changelogmine;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static android.text.InputType.TYPE_NULL;

public class OfflineFlipperFragment extends Fragment{

    ViewFlipper[][] viewFlipper;
    List<Integer> imageList;
    int row = 4;
    int col = 5;

    Thread slotThread1, slotThread2, slotThread3, slotThread4, slotThread5;
    Runnable number1, number2, number3, number4, number5, enableButtonsSpinAutoplayBetMaxBet, checkWin;
    boolean startSlot1 = false, startSlot2 = false, startSlot3 = false, startSlot4 = false, startSlot5 = false, finishGame = false, autoplay = false;
    ImageView btnSpin, btnAutoplay, btnMaxBet, btnSoundOn, btnSoundOff,btnTurnOff, btnInfo;
    EditText txtBet;
    TextView txtFreePlay, txtBonus;
    HashMap<String,Integer> bonusCombinations = new HashMap<>();
    static boolean isSoundOn = true;
    String urlPriivacyPolicy = "https://sites.google.com/view/ppriivacypolicy";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_offline_flipper, container, false);

        //prevent from going into portrait mode
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        initViewFlippers(v);
        initControls(v);
        initThreads();
        initButtonSpinListener();
        initButtonAutoplayListener();
        initButtonMaxBetListener();
        initButtonTurnOffListener();
        initButtonSoundOnOffListener();
        initButtonInfoListener();
        slotThread1.start();
        slotThread2.start();
        slotThread3.start();
        slotThread4.start();
        slotThread5.start();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isSoundOn) {
            if (!new isMyServiceRunning(getActivity().getApplicationContext()).isRunning(MusicService.class)) {
                getActivity().startService(new Intent(getActivity().getApplicationContext(), MusicService.class));
            }
            else {
                MusicService.mediaPlayer.start();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (new isMyServiceRunning(getActivity().getApplicationContext()).isRunning(MusicService.class) ) {
            MusicService.mediaPlayer.pause();
        }
    }

    private void initViewFlippers(View v) {
        imageList = new ArrayList<>(Arrays.asList(R.drawable.b, R.drawable.bar, R.drawable.granat, R.drawable.kivi, R.drawable.lime,
                R.drawable.marakuya, R.drawable.n, R.drawable.o, R.drawable.orange, R.drawable.s,
                R.drawable.seven, R.drawable.u, R.drawable.watermelon, R.drawable.wild));

        viewFlipper = new ViewFlipper[][]{{v.findViewById(R.id.viewflipper00), v.findViewById(R.id.viewflipper10), v.findViewById(R.id.viewflipper20), v.findViewById(R.id.viewflipper30)},
                {v.findViewById(R.id.viewflipper01), v.findViewById(R.id.viewflipper11), v.findViewById(R.id.viewflipper21), v.findViewById(R.id.viewflipper31)},
                {v.findViewById(R.id.viewflipper02), v.findViewById(R.id.viewflipper12), v.findViewById(R.id.viewflipper22), v.findViewById(R.id.viewflipper32)},
                {v.findViewById(R.id.viewflipper03), v.findViewById(R.id.viewflipper13), v.findViewById(R.id.viewflipper23), v.findViewById(R.id.viewflipper33)},
                {v.findViewById(R.id.viewflipper04), v.findViewById(R.id.viewflipper14), v.findViewById(R.id.viewflipper24), v.findViewById(R.id.viewflipper34)}
        };

        for (int i = 0; i < col; i++)
        {
            for (int j = 0; j < row; j++)
            {
                viewFlipper[i][j].setInAnimation(getActivity().getApplicationContext(), R.anim.slide_in_from_top);
                viewFlipper[i][j].setOutAnimation(getActivity().getApplicationContext(), R.anim.slide_out_to_bottom);

                Log.d(String.valueOf(i), String.valueOf(j));

                for (int k = 0; k < imageList.size(); k++) {
                    ImageView imageView = new ImageView(getActivity().getApplicationContext());
                    imageView.setImageResource(imageList.get(k));
                    viewFlipper[i][j].addView(imageView);
                }
                Collections.rotate(imageList.subList(0, imageList.size() - 1), -1);
            }
            Collections.rotate(imageList.subList(0, imageList.size() - 1), -4);
            Log.d("---", "---");
        }
    }

    private void initThreads() {

        number1 = new Runnable() {
            @Override
            public void run() {
                viewFlipper[0][0].showNext();
                viewFlipper[0][1].showNext();
                viewFlipper[0][2].showNext();
                viewFlipper[0][3].showNext();
            }
        };
        number2 = new Runnable() {
            @Override
            public void run() {
                viewFlipper[1][0].showNext();
                viewFlipper[1][1].showNext();
                viewFlipper[1][2].showNext();
                viewFlipper[1][3].showNext();
            }
        };
        number3 = new Runnable() {
            @Override
            public void run() {
                viewFlipper[2][0].showNext();
                viewFlipper[2][1].showNext();
                viewFlipper[2][2].showNext();
                viewFlipper[2][3].showNext();
            }
        };
        number4 = new Runnable() {
            @Override
            public void run() {
                viewFlipper[3][0].showNext();
                viewFlipper[3][1].showNext();
                viewFlipper[3][2].showNext();
                viewFlipper[3][3].showNext();
            }
        };
        number5 = new Runnable() {
            @Override
            public void run() {
                viewFlipper[4][0].showNext();
                viewFlipper[4][1].showNext();
                viewFlipper[4][2].showNext();
                viewFlipper[4][3].showNext();
            }
        };

        enableButtonsSpinAutoplayBetMaxBet = new Runnable() {
            @Override
            public void run() {
                if (!startSlot1 && !startSlot2 && !startSlot3 && !startSlot4 && !startSlot5) {

                    txtBet.setFocusable(true);
                    txtBet.setFocusableInTouchMode(true);
                    txtBet.setInputType(InputType.TYPE_CLASS_TEXT);

                    btnAutoplay.setClickable(true);
                    btnMaxBet.setClickable(true);
                    btnSpin.setClickable(true);
                }
            }
        };

        checkWin = new Runnable() {
            @Override
            public void run() {
                if (!startSlot1 && !startSlot2 && !startSlot3 && !startSlot4 && !startSlot5) {
                    int currentBet = Integer.parseInt(txtBet.getText().toString());
                    int currentBalance = Integer.parseInt(txtFreePlay.getText().toString());

                    int num00 = viewFlipper[0][0].getDisplayedChild();
                    int num01 = viewFlipper[1][0].getDisplayedChild();
                    int num02 = viewFlipper[2][0].getDisplayedChild();
                    int num03 = viewFlipper[3][0].getDisplayedChild();
                    int num04 = viewFlipper[4][0].getDisplayedChild();
                    List<Integer> list1 = Arrays.asList(num00, num01, num02, num03, num04);

                    int bonus = 0;
                    bonus = repeatedNumber(list1) >= 2 ?  repeatedNumber(list1) * currentBet : 0;

                    if(bonus > 0){
                        currentBalance += bonus;
                    }
                    else{
                        currentBalance -=currentBet;
                        txtFreePlay.setText(currentBalance+"");

                        if(currentBalance <= 0) {
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            currentBalance = 10000;
                            bonus = currentBalance;
                            Toast.makeText(getActivity(),"Today is your lucky day! Take bonus 10000!", Toast.LENGTH_SHORT).show();
                        }
                        if(currentBet > currentBalance){
                            currentBet = currentBalance;
                            txtBet.setText(currentBet + "");
                            Toast.makeText(getActivity(),"Bet can't be more than freeplay!", Toast.LENGTH_SHORT).show();
                        }
                    }


                    // To check combination another variant
//                     num0 = 0;
//                     num1 = 1;
//                     num2 = 2;
//                     num3 = 3;
//                     num4 = 4;

//                    String currentCombination = num0 + "" + num1 + num2 + num3 + num4 ;
//
//                    Integer multiplier = bonusCombinations.get(currentCombination);
//                    if (multiplier != null) {
//
//                        bonus = currentBet * bonusCombinations.get(currentCombination);
//                        currentBalance += bonus;
//
//                    }
//                    else {
//                        currentBalance -=currentBet;
//                        txtFreePlay.setText(currentBalance+"");
//
//                        if(currentBalance <= 0) {
//                            try {
//                                Thread.sleep(1500);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            currentBalance = 10000;
//                            bonus = currentBalance;
//                            Toast.makeText(getActivity(),"Today is your lucky day! Take bonus 10000!", Toast.LENGTH_SHORT).show();
//                        }
//                        if(currentBet > currentBalance){
//                            currentBet = currentBalance;
//                            txtBet.setText(currentBet + "");
//                            Toast.makeText(getActivity(),"Bet can't be more than freeplay!", Toast.LENGTH_SHORT).show();
//                        }
//                    }

                    txtBonus.setText(bonus + "");
                    txtFreePlay.setText(currentBalance+"");
                }
            }
        };

        slotThread1 = new Thread() {
            Random r = new Random();
            int speed1,speed2,speed3;
            int i;

            @Override
            public void run() {
                while (!finishGame) {
                    while (startSlot1) {
                        int currentBet = Integer.parseInt(txtBet.getText().toString());
                        int currentBalance = Integer.parseInt(txtFreePlay.getText().toString());
                        if(!(currentBalance > 0 && currentBalance >= currentBet)){
                            startSlot1 = false;
                            Utils.runOnUiThread(enableButtonsSpinAutoplayBetMaxBet);
                            break;
                        }


                        speed1 = r.nextInt(30) + 5;
                        speed2 = r.nextInt(30) + 5;
                        speed3 = r.nextInt(30) + 5;

                        try {
                            for (i = 0; i < speed1; ++i) {
                                Utils.runOnUiThread(number1);
                                sleep(100);
                            }
                            for (i = 0; i < speed2; ++i) {
                                Utils.runOnUiThread(number1);
                                sleep(200);
                            }
                            for (i = 0; i < speed3; ++i) {
                                Utils.runOnUiThread(number1);
                                sleep(300);
                            }
                        } catch (Exception ex) {
                        }

                        startSlot1 = false;
                        Utils.runOnUiThread(enableButtonsSpinAutoplayBetMaxBet);
                        Utils.runOnUiThread(checkWin);
                        checkAutoPlay();
                    }
                }
            }
        };

        slotThread2 = new Thread() {
            Random r = new Random();
            int speed1,speed2,speed3;
            int i;

            @Override
            public void run() {
                while (!finishGame) {
                    while (startSlot2) {
                        int currentBet = Integer.parseInt(txtBet.getText().toString());
                        int currentBalance = Integer.parseInt(txtFreePlay.getText().toString());

                        if(!(currentBalance > 0 && currentBalance >= currentBet)){
                            startSlot2 = false;
                            Utils.runOnUiThread(enableButtonsSpinAutoplayBetMaxBet);
                            break;
                        }

                        speed1 = r.nextInt(30);
                        speed2 = r.nextInt(30);
                        speed3 = r.nextInt(30);

                        try {
                            for (i = 0; i < speed1; ++i) {
                                Utils.runOnUiThread(number2);
                                sleep(100);
                            }
                            for (i = 0; i < speed2; ++i) {
                                Utils.runOnUiThread(number2);
                                sleep(200);
                            }
                            for (i = 0; i < speed3; ++i) {
                                Utils.runOnUiThread(number2);
                                sleep(300);
                            }
                        } catch (Exception ex) {
                        }
                        startSlot2 = false;
                        Utils.runOnUiThread(enableButtonsSpinAutoplayBetMaxBet);
                        Utils.runOnUiThread(checkWin);
                        checkAutoPlay();
                    }
                }
            }
        };

        slotThread3 = new Thread() {
            Random r = new Random();
            int speed1,speed2,speed3;
            int i;

            @Override
            public void run() {
                while (!finishGame) {
                    while (startSlot3) {
                        int currentBet = Integer.parseInt(txtBet.getText().toString());
                        int currentBalance = Integer.parseInt(txtFreePlay.getText().toString());
                        if(!(currentBalance > 0 && currentBalance >= currentBet)){
                            startSlot3 = false;
                            Utils.runOnUiThread(enableButtonsSpinAutoplayBetMaxBet);
                            break;
                        }

                        speed1 = r.nextInt(30);
                        speed2 = r.nextInt(30);
                        speed3 = r.nextInt(30);

                        try {
                            for (i = 0; i < speed1; ++i) {
                                Utils.runOnUiThread(number3);
                                sleep(100);
                            }
                            for (i = 0; i < speed2; ++i) {
                                Utils.runOnUiThread(number3);
                                sleep(200);
                            }
                            for (i = 0; i < speed3; ++i) {
                                Utils.runOnUiThread(number3);
                                sleep(300);
                            }
                        } catch (Exception ex) {
                        }
                        startSlot3 = false;
                        Utils.runOnUiThread(enableButtonsSpinAutoplayBetMaxBet);
                        Utils.runOnUiThread(checkWin);
                        checkAutoPlay();
                    }
                }
            }
        };

        slotThread4 = new Thread() {
            Random r = new Random();
            int speed1,speed2,speed3;
            int i;

            @Override
            public void run() {
                while (!finishGame) {
                    while (startSlot4) {
                        int currentBet = Integer.parseInt(txtBet.getText().toString());
                        int currentBalance = Integer.parseInt(txtFreePlay.getText().toString());
                        if(!(currentBalance > 0 && currentBalance >= currentBet)){
                            startSlot4 = false;
                            Utils.runOnUiThread(enableButtonsSpinAutoplayBetMaxBet);
                            break;
                        }

                        speed1 = r.nextInt(30);
                        speed2 = r.nextInt(30);
                        speed3 = r.nextInt(30);

                        try {
                            for (i = 0; i < speed1; ++i) {
                                Utils.runOnUiThread(number4);
                                sleep(100);
                            }
                            for (i = 0; i < speed2; ++i) {
                                Utils.runOnUiThread(number4);
                                sleep(200);
                            }
                            for (i = 0; i < speed3; ++i) {
                                Utils.runOnUiThread(number4);
                                sleep(300);
                            }
                        } catch (Exception ex) {
                        }
                        startSlot4 = false;
                        Utils.runOnUiThread(enableButtonsSpinAutoplayBetMaxBet);
                        Utils.runOnUiThread(checkWin);
                        checkAutoPlay();
                    }
                }
            }
        };

        slotThread5 = new Thread() {
            Random r = new Random();
            int speed1,speed2,speed3;
            int i;

            @Override
                public void run() {
                while (!finishGame) {
                    while (startSlot5) {
                        int currentBet = Integer.parseInt(txtBet.getText().toString());
                        int currentBalance = Integer.parseInt(txtFreePlay.getText().toString());

                        if(!(currentBalance > 0 && currentBalance >= currentBet)){
                            startSlot5 = false;
                            Utils.runOnUiThread(enableButtonsSpinAutoplayBetMaxBet);
                            break;
                        }

                        speed1 = r.nextInt(30);
                        speed2 = r.nextInt(30);
                        speed3 = r.nextInt(30);

                        try {
                            for (i = 0; i < speed1; ++i) {
                                Utils.runOnUiThread(number5);
                                sleep(100);
                            }
                            for (i = 0; i < speed2; ++i) {
                                Utils.runOnUiThread(number5);
                                sleep(200);
                            }
                            for (i = 0; i < speed3; ++i) {
                                Utils.runOnUiThread(number5);
                                sleep(300);
                            }
                        } catch (Exception ex) {
                        }
                        startSlot5 = false;
                        Utils.runOnUiThread(enableButtonsSpinAutoplayBetMaxBet);
                        Utils.runOnUiThread(checkWin);
                        checkAutoPlay();
                    }
                }
            }
        };
    }

    private void initControls(View v) {
        btnSpin = (ImageView) v.findViewById(R.id.spin);
        btnAutoplay = (ImageView) v.findViewById(R.id.autoplay);
        btnMaxBet = (ImageView) v.findViewById(R.id.maxbet);
        btnSoundOn = (ImageView) v.findViewById(R.id.soundon);
        btnSoundOff = (ImageView) v.findViewById(R.id.soundoff);
        btnTurnOff = (ImageView) v.findViewById(R.id.button_on_off);
        btnInfo = (ImageView) v.findViewById(R.id.info);
        txtBet =  (EditText) v.findViewById(R.id.txtbet);
        txtFreePlay = (TextView) v.findViewById(R.id.txtfreeplay);
        txtBonus =  (TextView) v.findViewById(R.id.txtbonus);

        bonusCombinations.put(getString(R.string.win1),getResources().getInteger(R.integer.win1));
        bonusCombinations.put(getString(R.string.win2),getResources().getInteger(R.integer.win2));
        bonusCombinations.put(getString(R.string.win3),getResources().getInteger(R.integer.win3));
        bonusCombinations.put(getString(R.string.win4),getResources().getInteger(R.integer.win4));
        bonusCombinations.put(getString(R.string.win5),getResources().getInteger(R.integer.win5));
        bonusCombinations.put(getString(R.string.win6),getResources().getInteger(R.integer.win6));
        bonusCombinations.put(getString(R.string.win7),getResources().getInteger(R.integer.win7));
    }

    private void initButtonSpinListener() {
        if (btnSpin != null) {
            btnSpin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int currentBet = Integer.parseInt(txtBet.getText().toString());
                    int currentBalance = Integer.parseInt(txtFreePlay.getText().toString());

                    if(currentBet <= currentBalance){
                        startSlot1 = true;
                        startSlot2 = true;
                        startSlot3 = true;
                        startSlot4 = true;
                        startSlot5 = true;

                        txtBet.setFocusable(false);
                        txtBet.setFocusableInTouchMode(false);
                        txtBet.setInputType(TYPE_NULL);

                        btnAutoplay.setClickable(false);
                        btnMaxBet.setClickable(false);
                        btnSpin.setClickable(false);
                    }
                    else {
                        Toast.makeText(getActivity(),"Bet can't be more than freeplay!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    private void initButtonAutoplayListener() {
        if (btnAutoplay != null) {
            btnAutoplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int currentBet = Integer.parseInt(txtBet.getText().toString());
                    int currentBalance = Integer.parseInt(txtFreePlay.getText().toString());

                    if(currentBet <= currentBalance){

                        if(!autoplay && !startSlot1 && !startSlot2 && !startSlot3 && !startSlot4 && !startSlot5){
                            startSlot1 = true;
                            startSlot2 = true;
                            startSlot3 = true;
                            startSlot4 = true;
                            startSlot5 = true;
                            autoplay = !autoplay;

                            txtBet.setFocusable(false);
                            txtBet.setFocusableInTouchMode(false);
                            txtBet.setInputType(TYPE_NULL);

                            btnMaxBet.setClickable(false);
                            btnSpin.setClickable(false);
                        }
                        else if(autoplay){
                            autoplay = !autoplay;
                        }
                    }
                    else {
                        Toast.makeText(getActivity(),"Bet can't be more than freeplay!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean checkSlotsIsNotRotating(){
        if(!startSlot1 && !startSlot2 && !startSlot3 && !startSlot4 && !startSlot5)
            return true;
        else
            return false;
    }

    private void checkAutoPlay(){
        if(checkSlotsIsNotRotating() && autoplay){

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            startSlot1 = true;
            startSlot2 = true;
            startSlot3 = true;
            startSlot4 = true;
            startSlot5 = true;

            txtBet.setFocusable(false);
            txtBet.setFocusableInTouchMode(false);
            txtBet.setInputType(TYPE_NULL);

            btnMaxBet.setClickable(false);
            btnSpin.setClickable(false);
        }
    }

    private void initButtonMaxBetListener() {
        if (btnMaxBet != null) {
            btnMaxBet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentBalance = Integer.parseInt(txtFreePlay.getText().toString());
                    txtBet.setText(currentBalance + "");
                }
            });
        }
    }

    private void initButtonTurnOffListener() {
        if (btnTurnOff != null) {
            btnTurnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishGame = true;
                    try {
                        slotThread1.join();
                        slotThread2.join();
                        slotThread3.join();
                        slotThread4.join();
                        slotThread5.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().finish();
                }
            });
        }
    }

    private void initButtonSoundOnOffListener() {

        btnSoundOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new isMyServiceRunning(getActivity().getApplicationContext()).isRunning(MusicService.class) ) {
                    MusicService.mediaPlayer.pause();
                }
                isSoundOn = false;
                btnSoundOn.setVisibility(View.GONE);
                btnSoundOff.setVisibility(View.VISIBLE);
            }
        });


        btnSoundOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSoundOn = true;

                if (isSoundOn && new isMyServiceRunning(getActivity().getApplicationContext()).isRunning(MusicService.class) ) {
                    MusicService.mediaPlayer.start();
                }
                btnSoundOff.setVisibility(View.GONE);
                btnSoundOn.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initButtonInfoListener(){
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new OnlineFragment(urlPriivacyPolicy);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_offline, fragment)
                                    .addToBackStack(OfflineFlipperFragment.class.getSimpleName());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    public int repeatedNumber(final List<Integer> list) {

        int count = 0;
        Collections.sort(list);

        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) == list.get(i + 1)) {
                count++;
            }
        }

        return count;
    }
}