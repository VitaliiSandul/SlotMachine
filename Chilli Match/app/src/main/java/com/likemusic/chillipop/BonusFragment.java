package com.likemusic.chillipop;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class BonusFragment extends Fragment implements View.OnClickListener{

    private TextView x2Txt, x3Txt, x5Txt, x7Txt, x10Txt;
    private ImageButton buyx2, buyx3, buyx5, buyx7, buyx10, closeButton;

    public BonusFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bonus, container, false);
        initComponents(v);
        setTextFont();

        buyx2.setOnClickListener(this);
        buyx3.setOnClickListener(this);
        buyx5.setOnClickListener(this);
        buyx7.setOnClickListener(this);
        buyx10.setOnClickListener(this);
        closeButton.setOnClickListener(this);

        return v;
    }

    private void initComponents(View v){
        x2Txt = v.findViewById(R.id.x2_txt);
        x3Txt = v.findViewById(R.id.x3_txt);
        x5Txt = v.findViewById(R.id.x5_txt);
        x7Txt = v.findViewById(R.id.x7_txt);
        x10Txt = v.findViewById(R.id.x10_txt);

        buyx2 = v.findViewById(R.id.buyx2);
        buyx3 = v.findViewById(R.id.buyx3);
        buyx5 = v.findViewById(R.id.buyx5);
        buyx7 = v.findViewById(R.id.buyx7);
        buyx10 = v.findViewById(R.id.buyx10);
        closeButton = v.findViewById(R.id.close_button);
    }

    private void setTextFont() {
        Typeface westernFont = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Rawhide.otf");
        x2Txt.setTypeface(westernFont);
        x3Txt.setTypeface(westernFont);
        x5Txt.setTypeface(westernFont);
        x7Txt.setTypeface(westernFont);
        x10Txt.setTypeface(westernFont);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buyx2:
                if(ChilliFragment.balance - 1.0 > 0){
                    ChilliFragment.balance -=1.0;
                    ChilliFragment.aditionalMultiplier = 2;
                    Toast.makeText(getActivity(),"Текущий множитель - х2", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(),"Не достаточно денег", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buyx3:
                if(ChilliFragment.balance - 1.5 > 0){
                    ChilliFragment.balance -=1.5;
                    ChilliFragment.aditionalMultiplier = 3;
                    Toast.makeText(getActivity(),"Текущий множитель - х3", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(),"Не достаточно денег", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buyx5:
                if(ChilliFragment.balance - 2.5 > 0){
                    ChilliFragment.balance -=2.5;
                    ChilliFragment.aditionalMultiplier = 5;
                    Toast.makeText(getActivity(),"Текущий множитель - х5", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(),"Не достаточно денег", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buyx7:
                if(ChilliFragment.balance - 3.5 > 0){
                    ChilliFragment.balance -=3.5;
                    ChilliFragment.aditionalMultiplier = 7;
                    Toast.makeText(getActivity(),"Текущий множитель - х7", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(),"Не достаточно денег", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buyx10:
                if(ChilliFragment.balance - 5.0 > 0){
                    ChilliFragment.balance -=5.0;
                    ChilliFragment.aditionalMultiplier = 10;
                    Toast.makeText(getActivity(),"Текущий множитель - х10", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(),"Не достаточно денег", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.close_button:
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.container, ChilliFragment.getInstanceChilliFragment(), "chillitag");
                fragmentTransaction.commit();
                break;
        }
    }
}