package com.example.jomride;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
String bike_code="B001";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button BtnFeedback=findViewById(R.id.BtnFeedbackPg);
        Button BtnFeedbackListpg=findViewById(R.id.BtnFeedbackListpg);
        BtnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent FbPgintent=new Intent( MainActivity.this,RatingActivity.class);
                FbPgintent.putExtra("bike_code",bike_code);
                startActivity(FbPgintent);

            }
        });
        BtnFeedbackListpg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LtPgintent=new Intent( MainActivity.this, FeedbackListActivity.class);
                LtPgintent.putExtra("bike_code",bike_code);
                startActivity(LtPgintent);

            }
        });
    }
}