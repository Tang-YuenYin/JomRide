package com.example.jomride;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CashBack extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference membershipRef;
    private String userId;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private CashbackAdapter cashbackAdapter;
    private List<Cashbackinfo> cashbackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_back);


        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        membershipRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("membership");

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_reward) {
                // Navigate to Reward activity
                startNewActivity(RewardMainPage.class);
                return true;
            } else if (item.getItemId() == R.id.nav_membership) {
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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cashbackList = new ArrayList<>();
        cashbackAdapter = new CashbackAdapter(this, cashbackList);
        recyclerView.setAdapter(cashbackAdapter);

        loadCashbackHistory();
    }

    private void loadCashbackHistory() {
        DatabaseReference userHistoryDb = FirebaseDatabase.getInstance().getReference("User").child(userId).child("cashback");
        userHistoryDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot history : snapshot.getChildren()) {
                        fetchChildInformation(history.getKey());
                        Log.w(TAG, history.getKey());
                    }
                    Log.d(TAG, "Size of cashbacklist after fetching data: " + cashbackList.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }

    private void fetchChildInformation(String ckey) {
        DatabaseReference cashbackDb = FirebaseDatabase.getInstance().getReference("History").child("cashback").child(ckey);
        cashbackDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Raw DataSnapshot content: " + snapshot.getValue());
                if (snapshot.exists()) {
                    Cashbackinfo dt = snapshot.getValue(Cashbackinfo.class);
                    if (dt != null) {
                        cashbackList.add(dt);
                        cashbackAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Added data to cashbacklist. Description: " + dt.getDateTime());
                    } else {
                        Log.w(TAG, "CashbackInfo is null");
                    }
                } else {
                    Log.d(TAG, "Snapshot does not exist for key: " + ckey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }


    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(CashBack.this, cls);
        startActivity(intent);
    }

    public void Back(View view){
        finish();
    }
}