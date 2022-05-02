package com.blackpuck.icehockey;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Random;

public class HockeyFragment extends Fragment implements View.OnClickListener {

    private SeekBar seekbar;
    private ImageView target1, target2, target3, cirleSighting, puck, goalkeeper, btnStart;
    private TextView score1Txt, score2Txt, goalTxt, nogoalTxt;
    private int score1 = 0, score2 = 0, width = 0, height = 0;
    private float targetPuckX = 0, targetPuckY = 0, puckX = 0, puckY = 0;

    public HockeyFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hockey, container, false);

        initViews(v);
        setSeekbar();
        btnStart.setOnClickListener(this);

        return v;
    }

    private void initViews(View v) {
        seekbar = v.findViewById(R.id.seekbar);
        target1 = v.findViewById(R.id.target1);
        target2 = v.findViewById(R.id.target2);
        target3 = v.findViewById(R.id.target3);
        cirleSighting = v.findViewById(R.id.cirle_sighting);
        puck = v.findViewById(R.id.puck);
        goalkeeper = v.findViewById(R.id.goalkeeper);
        btnStart = v.findViewById(R.id.btn_start);
        score1Txt = v.findViewById(R.id.score1_txt);
        score2Txt = v.findViewById(R.id.score2_txt);
        goalTxt = v.findViewById(R.id.goal_txt);
        nogoalTxt = v.findViewById(R.id.nogoal_txt);

        goalkeeper.setImageResource(R.drawable.hockey_goalkeeper_center_0);
    }

    private void setSeekbar() {
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if(seekBar.getProgress() >= 0 && seekBar.getProgress() <= 33){
                    width = (int) target1.getX()-18;
                    height = (int) target1.getY()-18;
                }
                else if(seekBar.getProgress() >33 && seekBar.getProgress() <= 66){
                    width = (int) target2.getX()-18;
                    height = (int) target2.getY()-18;
                }
                else if(seekBar.getProgress() >66 && seekBar.getProgress() <= 100){
                    width = (int) target3.getX()-18;
                    height = (int) target3.getY()-18;
                }
                cirleSighting.setX(width);
                cirleSighting.setY(height);


                Log.d("--- Y ---", String.valueOf(seekBar.getProgress()));
            }
        });
    }



    private void startFunc() {
        Log.d("startFunc()", "started");

        unclick();

        targetPuckX = cirleSighting.getX();
        targetPuckY = cirleSighting.getY();
        puckX = puck.getX();
        puckY = puck.getY();

        target1.setVisibility(View.GONE);
        target2.setVisibility(View.GONE);
        target3.setVisibility(View.GONE);
        cirleSighting.setVisibility(View.GONE);


        Animation scale= new ScaleAnimation(1, (float) 0.50, 1, (float) 0.50, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(1000);
        Animation slideUp = new TranslateAnimation(0, -(puckX - targetPuckX),0, -(puckY -targetPuckY));
        slideUp.setDuration(1000);
        slideUp.setFillAfter(true);
        AnimationSet animSet = new AnimationSet(true);
        animSet.setFillEnabled(true);
        animSet.addAnimation(scale);
        animSet.addAnimation(slideUp);
        puck.startAnimation(animSet);
        puck.setVisibility(View.GONE);

        Utils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler h = new Handler();
                Random random = new Random();

                int goalkeeperPosition = random.nextInt(3) + 1;

                Log.d("Goalkeeper Random pos", String.valueOf(goalkeeperPosition));
                Log.d("seekbar progress", String.valueOf(seekbar.getProgress()));

                if(seekbar.getProgress() >= 0 && seekbar.getProgress() <= 33 && goalkeeperPosition == 1){

                    h.postDelayed(new Runnable() {
                          @Override
                          public void run() {
                              goalkeeper.setImageResource(R.drawable.hockey_goalkeeper_left_1);
                          }
                    }, 50);

                    score2++;
                    nogoalTxt.setVisibility(View.VISIBLE);
                }
                else if(seekbar.getProgress() > 33 && seekbar.getProgress() <= 66 && goalkeeperPosition == 2){

                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goalkeeper.setImageResource(R.drawable.hockey_goalkeeper_center_1);
                        }
                    }, 50);

                    score2++;
                    nogoalTxt.setVisibility(View.VISIBLE);
                }
                else if(seekbar.getProgress() > 66 && seekbar.getProgress() <= 100 && goalkeeperPosition == 3){
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goalkeeper.setImageResource(R.drawable.hockey_goalkeeper_right_1);
                        }
                    }, 50);

                    score2++;
                    nogoalTxt.setVisibility(View.VISIBLE);
                }
                else{
                    goalTxt.setVisibility(View.VISIBLE);
                    score1++;

                    if(goalkeeperPosition == 1){
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                goalkeeper.setImageResource(R.drawable.hockey_goalkeeper_left_0);
                            }
                        }, 50);

                    }
                    else if(goalkeeperPosition == 2){
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                goalkeeper.setImageResource(R.drawable.hockey_goalkeeper_center_0);
                            }
                        }, 50);

                    }
                    else if(goalkeeperPosition == 3){
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                goalkeeper.setImageResource(R.drawable.hockey_goalkeeper_right_0);
                            }
                        }, 50);

                    }
                }
            }
        });


        Utils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler h2 = new Handler();
                h2.postDelayed(new Runnable() {
                    @Override public void run() {
                        goalTxt.setVisibility(View.GONE);
                        nogoalTxt.setVisibility(View.GONE);

                        refreshScore();
                        target1.setVisibility(View.VISIBLE);
                        target2.setVisibility(View.VISIBLE);
                        target3.setVisibility(View.VISIBLE);
                        cirleSighting.setVisibility(View.VISIBLE);
                        puck.setVisibility(View.VISIBLE);
                        goalkeeper.setImageResource(R.drawable.hockey_goalkeeper_center_0);

                        click();
                    }
                }, 2000);
            }
        });

    }

    private void refreshScore(){
        if(score1 < 10){
            score1Txt.setText("0" + score1);
        }
        else{
            score1Txt.setText(String.valueOf(score1));
        }

        if(score2 < 10){
            score2Txt.setText("0" + score2);
        }
        else{
            score2Txt.setText(String.valueOf(score2));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                startFunc();
                break;
        }
    }

    private void click() {
        btnStart.setOnClickListener(this);
    }

    private void unclick() {
        btnStart.setOnClickListener(null);
    }
}