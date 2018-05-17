package com.in2event.in2eventscan.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.in2event.in2eventscan.R;
import com.in2event.in2eventscan.utils.MyPreference;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginSuccessActivity extends AppCompatActivity {

    private CircleImageView eventLogoImageView;
    private TextView eventNameLabel;
    private TextView eventCityAndDatesLabel;
    private Button startScanningButton;
    private MyPreference myPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);
        eventLogoImageView = findViewById(R.id.eventLogoImageView);
        eventNameLabel = findViewById(R.id.eventNameLabel);
        eventCityAndDatesLabel = findViewById(R.id.eventCityAndDatesLabel);
        startScanningButton = findViewById(R.id.startScanningButton);

        myPref = new MyPreference(this);
        Glide.with(this).load(myPref.getEventLogo()).into(eventLogoImageView);
        eventNameLabel.setText(myPref.getEventName());
        eventCityAndDatesLabel.setText(String.format("%s - %s", myPref.getEventCity(), myPref.getEventDates()));

        startScanningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginSuccessActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
