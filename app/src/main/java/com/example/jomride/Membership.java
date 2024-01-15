package com.example.jomride;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Membership extends AppCompatActivity {
    private ArrayList<String> rideId;
    private MemberData membership;
    private BottomNavigationView bottomNavigationView;
    private DatabaseReference memberRef;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();

        if (currentUser != null) {
            // Assuming you have a "users" node in your database
            databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userId);
        }

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


        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkMembershipEligibility();
            }
        });
    }

    private void checkMembershipEligibility() {
        memberRef = databaseReference.child("membership");
        memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer rideCount = snapshot.child("rideCount").getValue(Integer.class);
                Double totalRideFare = snapshot.child("totalRideFare").getValue(Double.class);

                if (rideCount != null && totalRideFare != null) {
                    if (rideCount >= 10 || totalRideFare >= 100) {
                        Toast.makeText(Membership.this, "Eligible", Toast.LENGTH_SHORT).show();
                        startNewActivity(SignUpMembership.class);
                    } else {
                        Toast.makeText(Membership.this, "Not Eligible", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case when rideCount or totalRideFare is null
                    Toast.makeText(Membership.this, "Error: Ride count or total ride fare is null", Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    public void Back(View view) {
        startNewActivity(HomeActivity.class);
    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(Membership.this, cls);
        startActivity(intent);
    }

}
