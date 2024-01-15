package com.example.jomride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WatsonsActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private String userId;
    private FirebaseAuth mAuth;
    private DatabaseReference membershipRef;
    private TextView pointText;
    private DatabaseReference pointRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watsons);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        pointText = findViewById(R.id.point);
        pointRef = FirebaseDatabase.getInstance().getReference("Wallet").child(userId).child("point");
        pointRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalPoint = snapshot.getValue(Integer.class);
                pointText.setText("Points available: "+totalPoint);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_reward) {
                // Navigate to Reward activity
                startNewActivity(RewardMainPage.class);
                return true;
            } else if (item.getItemId() == R.id.nav_membership) {
                // Navigate to Membership activity
                startNewActivity(Membership.class);
                return true;
            } else if (item.getItemId() == R.id.nav_cashback) {
                // Navigate to Cashback activity
                startNewActivity(CashBack.class);
                return true;
            } else {
                return false;
            }
        });
        // Initialize the redeem button and set its click listener
        Button redeemButton = findViewById(R.id.redeemwatsons);

        redeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int redeemedPoints = 500; // Number of points to redeem
                deductPointsFromDatabase(redeemedPoints);
            }
        });
    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(WatsonsActivity.this, cls);
        startActivity(intent);
    }

    private void deductPointsFromDatabase(int redeemedPoints) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        pointRef = database.getReference("Wallet").child(userId).child("point");

        pointRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int currentPoints = snapshot.getValue(Integer.class);
                    if(currentPoints >= redeemedPoints) {
                        int newTotalPoints = currentPoints - redeemedPoints;

                        // Update the total points in the database
                        pointRef.setValue(newTotalPoints).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                Log.d("Database", "Points deducted successfully");
                                showToast("Redeem successful");
                            } else {
                                Log.e("Database", "Error deducting points", task.getException());
                            }
                        });
                    }
                    else{
                        Toast.makeText(WatsonsActivity.this, "Insufficient Points", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", "Error fetching total points", error.toException());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void Back(View view){
        finish();
    }
}






