package com.example.jomride;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ShopActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        ImageButton clip = findViewById(R.id.clip);
        clip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopActivity.this,ClipActivity.class);
                startActivity(intent);
            }
        });

        ImageButton armband = findViewById(R.id.armband);
        armband.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopActivity.this,ArmbandActivity.class);
                startActivity(intent);
            }
        });

        ImageButton helmet = findViewById(R.id.helmet);
        helmet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopActivity.this,HelmetActivity.class);
                startActivity(intent);
            }
        });

        ImageButton sunglasses = findViewById(R.id.sunglasses);
        sunglasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopActivity.this,SunglassesActivity.class);
                startActivity(intent);
            }
        });

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(ShopActivity.this, HomeActivity.class);
               startActivity(intent);

            }
        });
    }
}
