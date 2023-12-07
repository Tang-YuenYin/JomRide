//This activity just to show how the receipt navigate and access wallet
package com.example.jomride;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button BtnWallet=findViewById(R.id.BtnWallet);
        BtnWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentWallet = new Intent(HomeActivity.this,WalletActivity.class);
                startActivity(intentWallet);
            }
        });

    }
}
