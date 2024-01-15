package com.example.jomride;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TransportActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);

        Intent intent = getIntent();
        String message = intent.getStringExtra("Destination");
        TextView TVResult = findViewById(R.id.TVResult);
        TVResult.setText(message);

        ImageButton bicycle = findViewById(R.id.bicycle);
        bicycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransportActivity.this,BicycleActivity.class);
                intent.putExtra("Destination", message);
                startActivity(intent);
            }
        });

        ImageButton disabled = findViewById(R.id.disabledBike);
        disabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransportActivity.this,DisabledActivity.class);
                intent.putExtra("Destination", message);
                startActivity(intent);
            }
        });

        ImageButton scooter = findViewById(R.id.scooter);
        scooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransportActivity.this, ScooterActivity.class);
                intent.putExtra("Destination", message);
                startActivity(intent);
            }
        });

        ImageButton carpool = findViewById(R.id.carpool);
        carpool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransportActivity.this, BikepoolActivity.class);
                intent.putExtra("Destination", message);
                startActivity(intent);
            }
        });

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
