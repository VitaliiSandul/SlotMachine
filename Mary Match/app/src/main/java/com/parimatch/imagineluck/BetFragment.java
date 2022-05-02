package com.parimatch.imagineluck;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ViewFlipper;

import java.util.List;

public class BetFragment extends Fragment implements View.OnClickListener{

    private ImageButton okButton, closeButton;
    private ImageButton [] pressed;
    private ImageButton [] unpressed;

    private int[] bet = {2, 5, 10, 20, 25, 100, 200, 500, 2000};

    public BetFragment() {  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bet, container, false);

        initButtons(v);
        click();
        setPressedButton(0);

        return v;
    }

    private void initButtons(View v){
        okButton = v.findViewById(R.id.ok_button);
        closeButton = v.findViewById(R.id.close_button);

        pressed = new ImageButton []{v.findViewById(R.id.pressed_2), v.findViewById(R.id.pressed_5), v.findViewById(R.id.pressed_10),
                                    v.findViewById(R.id.pressed_20), v.findViewById(R.id.pressed_50), v.findViewById(R.id.pressed_100),
                                    v.findViewById(R.id.pressed_200), v.findViewById(R.id.pressed_500), v.findViewById(R.id.pressed_inf)};

        unpressed = new ImageButton []{v.findViewById(R.id.unpressed_2), v.findViewById(R.id.unpressed_5), v.findViewById(R.id.unpressed_10),
                                    v.findViewById(R.id.unpressed_20), v.findViewById(R.id.unpressed_50), v.findViewById(R.id.unpressed_100),
                                    v.findViewById(R.id.unpressed_200), v.findViewById(R.id.unpressed_500), v.findViewById(R.id.unpressed_inf)};
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_button:
                backToChristmasFragment();
                break;
            case R.id.close_button:
                backToChristmasFragment();
                break;
            case R.id.pressed_2:
                chooseBet(0);
                break;
            case R.id.pressed_5:
                chooseBet(1);
                break;
            case R.id.pressed_10:
                chooseBet(2);
                break;
            case R.id.pressed_20:
                chooseBet(3);
                break;
            case R.id.pressed_50:
                chooseBet(4);
                break;
            case R.id.pressed_100:
                chooseBet(5);
                break;
            case R.id.pressed_200:
                chooseBet(6);
                break;
            case R.id.pressed_500:
                chooseBet(7);
                break;
            case R.id.pressed_inf:
                chooseBet(8);
                break;
            case R.id.unpressed_2:
            case R.id.unpressed_5:
            case R.id.unpressed_10:
            case R.id.unpressed_20:
            case R.id.unpressed_50:
            case R.id.unpressed_100:
            case R.id.unpressed_200:
            case R.id.unpressed_500:
            case R.id.unpressed_inf:
                setMinBet();
                break;
        }
    }

    private void click() {
        okButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);

        for(int i = 0; i < pressed.length; i++){
            pressed[i].setOnClickListener(this);
        }

        for(int i = 0; i < unpressed.length; i++){
            unpressed[i].setOnClickListener(this);
        }
    }

    private void chooseBet(int index) {
        if(index != 8){
            ChristmasFragment.bet = bet[index];
        }
        else {
            ChristmasFragment.bet = ChristmasFragment.balance;
        }

        setPressedButton(index);
    }

    private void setMinBet(){
        ChristmasFragment.bet = bet[0];
        setPressedButton(0);
    }

    private void setPressedButton(int position) {
        for(int i = 0; i < unpressed.length; i++){
            unpressed[i].setVisibility(View.GONE);
        }
        for(int i = 0; i < pressed.length; i++){
            pressed[i].setVisibility(View.VISIBLE);
        }
        unpressed[position].setVisibility(View.VISIBLE);
        pressed[position].setVisibility(View.GONE);
    }

    private void backToChristmasFragment(){
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        FragmentTransaction fTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fTransaction.replace(R.id.christmas_container, new ChristmasFragment(),"christmastag");
        fTransaction.commit();
    }
}