package com.example.jomride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RewardMainPage extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private String userId;
    private DatabaseReference membershipRef;
    private TextView totalPoint;
    private DatabaseReference walletRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewardmain);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();

        membershipRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("membership");

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_reward) {
                // Navigate to Reward activity
                startNewActivity(RewardMainPage.class);
                return true;
            } else if (item.getItemId() == R.id.nav_membership) {
                // Navigate to Membership activity
                membershipRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isMember = (boolean) snapshot.child("isMember").getValue();
                        if (isMember) {
                            startNewActivity(YourInformation.class);
                        } else {
                            startNewActivity(Membership.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return true;
            } else if (item.getItemId() == R.id.nav_cashback) {
                // Navigate to Cashback activity
                startNewActivity(CashBack.class);
                return true;
            } else {
                return false;
            }
        });





        // Initialize TextView
        totalPoint = findViewById(R.id.totalPoints);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        // Initialize Firebase Database Reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        walletRef = database.getReference("Wallet").child(userId);

        // Retrieve and sum up the points from each ride
        retrieveAndSumPoints();

        // Setting up CardView onClickListeners
        setupCardViewListeners();


    }

    private void setupCardViewListeners() {
        // CardView for Discount10
        findViewById(R.id.discount10).setOnClickListener(view -> startNewActivity(Discount10DetailsActivity.class));

        // CardView for Discount25
        findViewById(R.id.discount25).setOnClickListener(view -> startNewActivity(Discount25Details.class));

        // CardView for RentFree
        findViewById(R.id.rentfree).setOnClickListener(view -> startNewActivity(Rent1free1.class));

        // CardView for Zus
        findViewById(R.id.zus).setOnClickListener(view -> startNewActivity(ZusActivity.class));

        // CardView for Tealive
        findViewById(R.id.tealive).setOnClickListener(view -> startNewActivity(TealiveActivity.class));

        // CardView for Watsons
        findViewById(R.id.watsons).setOnClickListener(view -> startNewActivity(WatsonsActivity.class));

        // CardView for Armband
        findViewById(R.id.armband).setOnClickListener(view -> startNewActivity(ArmbandRewardActivity.class));

        // CardView for Skirtclip
        findViewById(R.id.skirtclip).setOnClickListener(view -> startNewActivity(SkirtclipActivity.class));
    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(RewardMainPage.this, cls);
        startActivity(intent);
    }

    public void retrieveAndSumPoints() {
        walletRef.child("point").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int point = snapshot.getValue(Integer.class);
                    runOnUiThread(() -> totalPoint.setText(String.valueOf(point)+" points"));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log error
                Log.e("FirebaseError", "Error fetching data", error.toException());
                // Optionally, display a Toast or other UI feedback here
            }
        });
    }

    public void Back(View view){
        startNewActivity(HomeActivity.class);
    }


}


