package com.example.jomride;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startpage);

        Thread thread = new Thread(){
            @Override
            public void run(){
                try{
                    sleep(5000);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                finally {
                    Intent registerIntent = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(registerIntent);
                }
            }
        };
        thread.start();
    }
    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}
