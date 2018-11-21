package com.in2event.in2eventscan.activities;

import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.in2event.in2eventscan.R;

public class ScanResultActivity extends AppCompatActivity {

    private ImageView resultImageView;
    private TextView messageLabel;
    private TextView customerLabel;
    private TextView productLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        resultImageView = findViewById(R.id.resultImageView);
        messageLabel = findViewById(R.id.messageLabel);
        customerLabel = findViewById(R.id.customerLabel);
        productLabel = findViewById(R.id.productLabel);

        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String response = getIntent().getStringExtra("response");
        String message = getIntent().getStringExtra("message");
        String customer = getIntent().getStringExtra("customer");
        String product = getIntent().getStringExtra("product");
        ConstraintLayout layout = findViewById(R.id.container);

        switch (response) {
            case "SUCCESS":
                layout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_success));
                resultImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_success));
                break;
            case "WARNING":
                layout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_warning));
                resultImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_warning));
                break;
            case "ERROR":
                layout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_failed));
                resultImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_failed));
                break;
            default:
                break;
        }

        messageLabel.setText(message);
        customerLabel.setText(customer);
        productLabel.setText(product);
    }
}
