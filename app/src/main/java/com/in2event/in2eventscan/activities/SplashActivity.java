package com.in2event.in2eventscan.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.in2event.in2eventscan.R;
import com.in2event.in2eventscan.utils.MyPreference;

public class SplashActivity extends AppCompatActivity {

    private static final int DELAY = 2000;
    private final Handler mHandler = new Handler();

    private MyPreference myPreference;

    private final Runnable mStartActivity = new Runnable() {
        @Override
        public void run() {
            if(myPreference.getAccessToken() != null){
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }else{
                Intent i = new Intent(SplashActivity.this, StartActivity.class);
                startActivity(i);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myPreference = new MyPreference(this);

        setContentView(R.layout.activity_splash);

        mHandler.postDelayed(mStartActivity, DELAY);
    }
}