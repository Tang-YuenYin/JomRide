package com.example.jomride;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TransportActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);

        // Retrieving the string extra with key "Destination" from the Intent that started this activity
        String message = getIntent().getStringExtra("Destination");
        // Finding the TextView with the ID R.id.TVResult in the layout
        TextView TVResult = findViewById(R.id.TVResult);
        // Setting the text of the TextView TVResult to the retrieved message
        TVResult.setText(message);

        //Bicycle Image Button - jump to BicycleActivity
        ImageButton bicycle = findViewById(R.id.bicycle);
        bicycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransportActivity.this,BicycleActivity.class);
                intent.putExtra("Destination", message);
                startActivity(intent);
            }
        });

        //Disabled Bike Image Button - jump to DisabledActivity
        ImageButton disabled = findViewById(R.id.disabledBike);
        disabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransportActivity.this,DisabledActivity.class);
                intent.putExtra("Destination", message);
                startActivity(intent);
            }
        });

        //Scooter Image Button - jump to ScooterActivity
        ImageButton scooter = findViewById(R.id.scooter);
        scooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransportActivity.this, ScooterActivity.class);
                intent.putExtra("Destination", message);
                startActivity(intent);
            }
        });

        //BikePooling Image Button - jump to BikepoolActivity
        ImageButton carpool = findViewById(R.id.carpool);
        carpool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransportActivity.this, BikepoolActivity.class);
                intent.putExtra("Destination", message);
                startActivity(intent);
            }
        });

        //Back Button - go back to LocationActivity
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransportActivity.this,LocationActivity.class);
                startActivity(intent);
            }
        });
    }
}
