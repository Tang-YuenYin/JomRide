package com.example.jomride;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EndActivity extends AppCompatActivity {
    private Intent feedbackActivity;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        feedbackActivity = new Intent(EndActivity.this, FeedbackActivity.class);
        //BikeCode
        String bikeCode = getIntent().getStringExtra("bikeCode");
        String pointEarned = getIntent().getStringExtra("PointsEarned");
        TextView TVBikeCode = findViewById(R.id.TV_Apoint);
        TVBikeCode.setText("+"+pointEarned+" pts");
        feedbackActivity.putExtra("bikeCode", bikeCode);

        double totalPrice = getIntent().getDoubleExtra("TotalPrice", 0.0);
        TextView TVPrice = findViewById(R.id.TV_Atotal);
        TVPrice.setText("Total Price: RM " + String.format("%.2f", totalPrice));

        String vehicleType = getIntent().getStringExtra("VehicleType");
        TextView TVTransport = findViewById(R.id.TV_Ainput_tranport);
        TVTransport.setText("Vehicle Type: "+vehicleType);

        String dateTime = getCurrentDateTime();
        TextView TVDateTime = findViewById(R.id.TV_ADateTime);
        TVDateTime.setText(dateTime);

        long timeElapsed = getIntent().getLongExtra("TimeElapsed",0);
        TextView TVElapsedTime = findViewById(R.id.TV_Ainput_duration);
        TVElapsedTime.setText("Duration: "+timeElapsed+" (seconds)");

        double ratePerMinute = getIntent().getDoubleExtra("RatePerMinute",0.0);
        TextView TVRate = findViewById(R.id.TV_Ainput_rate);
        TVRate.setText("Rate: RM"+ratePerMinute+"0 /min");

        String message = getIntent().getStringExtra("Destination");
        TextView TVStartPoint = findViewById(R.id.TV_Ainput_start);
        TVStartPoint.setText("Start Point: "+message);

        Button BtnOk = findViewById(R.id.Btn_AProceed);
        BtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(feedbackActivity);
            }
        });
    }

    private String getCurrentDateTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Format: MMM d, yyyy - hh:mm a
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy - hh:mm a");
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }
}
