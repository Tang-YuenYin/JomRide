//Wallet activity
package com.example.jomride;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WalletActivity extends AppCompatActivity {
    //call authentictaion
    private FirebaseAuth mAuth;

    //call database
    private FirebaseDatabase database;

    private String userId="2";
    private WalletData walletdata;
    private TextView balance;
    private TextView point;
   private  DatabaseReference walletRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        balance=findViewById(R.id.TV_Winput_balance);
        point=findViewById(R.id.TV_Winput_point);

        //Display balance and point in the wallet
       readWalletData();



        //Send to top up page
        Button BtnTopUp=findViewById(R.id.Btn_topup);
        BtnTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTopUp=new Intent(WalletActivity.this,TopUpActivity.class);
                startActivity(intentTopUp);
            }
        });

        //Back to home page
        Button BtnBackHome=findViewById(R.id.BtnBack_WalletHome);
        BtnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBackHome=new Intent(WalletActivity.this,HomeActivity.class);
                startActivity(intentBackHome);
            }
        });

        //Go to History
        Button BtnHistory=findViewById(R.id.Btn_History);
        BtnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHistory=new Intent(WalletActivity.this,HistoryActivity.class);
                startActivity(intentHistory);
            }
        });
//
    }

    private void readWalletData()
    {
        //Get authentication+current id
           mAuth=FirebaseAuth.getInstance();
           userId=mAuth.getCurrentUser().getUid()
        database= FirebaseDatabase.getInstance();
        walletRef=database.getReference().child("Wallet").child(userId);
        walletRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                walletdata = dataSnapshot.getValue(WalletData.class);
                if (walletdata != null) {
                    balance.setText(walletdata.getBalance());
                    point.setText(String.format("%d pts",walletdata.getPoint()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }

}
