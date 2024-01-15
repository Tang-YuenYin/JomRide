package com.example.jomride;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.C;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CountingActivity extends AppCompatActivity {
    private Button BtnStop;
    private Dialog mDialog;
    private TextView TVCounting;
    private Handler handler;
    private long elapsedTimeMillis = 0;
    private DatabaseReference ridesRef;
    private DatabaseReference rideRef;
    private DatabaseReference transRef;
    private DatabaseReference tranRef;
    private DatabaseReference cashbackRef;
    private DatabaseReference cashbacksRef;
    private DatabaseReference memberRef;
    private Intent endActivity;

    String message;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counting);


        Intent intent = getIntent();
        message = intent.getStringExtra("Destination");
        TVCounting = findViewById(R.id.TVCounting);
        BtnStop = findViewById(R.id.BtnStop);
        mDialog = new Dialog(this);
        handler = new Handler();
        startTimer();
        BtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupDialog();
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        ridesRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId).child("ride");
        transRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId).child("transaction");
        cashbackRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("cashback");
        String rideId = ridesRef.push().getKey();
        String transactionId = transRef.push().getKey();
        String cashbackId = cashbackRef.push().getKey();
        ridesRef.child(rideId).setValue(true);
        transRef.child(transactionId).setValue(true);
        cashbackRef.child(cashbackId).setValue(true);
        rideRef = FirebaseDatabase.getInstance().getReference().child("History").child("ride").child(rideId);
        tranRef = FirebaseDatabase.getInstance().getReference().child("History").child("transaction").child(transactionId);
        cashbacksRef = FirebaseDatabase.getInstance().getReference("History").child("cashback").child(cashbackId);
        memberRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("membership");
        endActivity = new Intent(CountingActivity.this, EndActivity.class);


    }

    private void showPopupDialog() {
        mDialog.setContentView(R.layout.confirm_end);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnyes = mDialog.findViewById(R.id.BtnYes);
        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(endActivity);
                mDialog.dismiss();
                stopTimer();
                String vehicleType = getIntent().getStringExtra("VehicleType");
                double pricePerMinute = getPricePerMinute(vehicleType);
                showPriceAndVehicle(pricePerMinute);


            }
        });
        mDialog.show();
    }

    private double getPricePerMinute(String vehicleType) {
        switch (vehicleType) {
            case "Bicycle":
                return 0.40;
            case "DisabledBike":
                return 1.00;
            case "Scooter":
                return 0.70;
            case "BikePooling":
                return 1.50;
            default:
                return 0.0; // Default to 0 if unknown vehicle type
        }
    }

    private void startTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                elapsedTimeMillis += 1000;
                updateTimerText();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void stopTimer() {
        handler.removeCallbacksAndMessages(null);
    }

    private void updateTimerText() {
        long minutes = elapsedTimeMillis / (1000 * 60);
        long seconds = (elapsedTimeMillis % (1000 * 60)) / 1000;
        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        TVCounting.setText("Time Elapsed: " + timeLeftFormatted);
    }

    private void showPriceAndVehicle(double pricePerMinute) {
        double totalPrice = Math.round((elapsedTimeMillis / (60.0 * 1000) * pricePerMinute)*100)/100.0;
        endActivity.putExtra("TotalPrice", totalPrice);
        int pointsEarned = calculatePointsForPrice(totalPrice);

        // Deduct balance from user's wallet
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Wallet").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                WalletData userData = dataSnapshot.getValue(WalletData.class);
                if (userData != null) {
                    double currentBalance = Double.parseDouble(userData.getBalance());
                    double newBalance = currentBalance - totalPrice;
                    String formattedNewBalance = String.format("%.2f", newBalance);
                    userData.setBalance(formattedNewBalance);

                    // Update balance in database
                    dataSnapshot.getRef().setValue(userData);

                    // Update points in database
                    updatePointsInDatabase(pointsEarned);

                    // Save ride data
                    saveData(totalPrice, pointsEarned);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "showPriceAndVehicle:onCancelled", error.toException());
            }
        });


        // Retrieve vehicleType from the intent received in the showPopupDialog method
        String vehicleType = getIntent().getStringExtra("VehicleType");
        String bikeCode = getIntent().getStringExtra("bikeCode");
        String message = getIntent().getStringExtra("Destination");
        endActivity.putExtra("bikeCode", bikeCode);

        // Add "VehicleType" to the intent
        endActivity.putExtra("VehicleType", vehicleType);
        String pointss = Integer.toString(pointsEarned);
        endActivity.putExtra("PointsEarned", pointss);

        endActivity.putExtra("TimeElapsed", elapsedTimeMillis / 1000);

        endActivity.putExtra("RatePerMinute", pricePerMinute);
        endActivity.putExtra("Destination", message);
        endActivity.putExtra("Date", getCurrentDateTime());
        startActivity(endActivity);
    }


    private void updatePointsInDatabase(int pointsEarned) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Wallet").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                WalletData userData = dataSnapshot.getValue(WalletData.class);
                if (userData != null) {
                    int currentPoints = userData.getPoint();
                    int newPoints = currentPoints + pointsEarned;
                    userData.setPoint(newPoints);

                    // Update the points in the database
                    dataSnapshot.getRef().setValue(userData);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "updatePointsInDatabase:onCancelled", error.toException());
            }
        });
    }

    private int calculatePointsForPrice(double totalPrice) {
        // Assuming 1 point for every dollar spent
        return (int) totalPrice*100;
    }

    private void saveData(double totalPrice, int pointsEarned) {
        String vehicleType = getIntent().getStringExtra("VehicleType");
        String bikeCode = getIntent().getStringExtra("bikeCode");

        // Formatting totalPrice to two decimal places
        String formattedTotalPrice = String.format("%.2f", totalPrice);

        RideData ride = new RideData(
                getCurrentDateTime(),
                Double.parseDouble(formattedTotalPrice),
                (int)pointsEarned,
                vehicleType,
                getPricePerMinute(vehicleType),
                0.0,  // distance, update if you have it
                elapsedTimeMillis / (60.0 * 1000),  // duration in minutes
                "",   // start location, update if you have it
                "",   // end location, update if you have it
                bikeCode  // bike code, update if you have it
        );

        HistoryData transaction = new HistoryData(
                getCurrentDateTime(),
                "0.3km "+Double.toString( Math.round((elapsedTimeMillis / (60.0 * 1000))*100.0)/100.0)+" min ride",
                "wallet",
                "-RM"+formattedTotalPrice);

        //member calculation
        memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int rideCount = snapshot.child("rideCount").getValue(Integer.class);
                rideCount++;
                double totalRideFare = snapshot.child("totalRideFare").getValue(Double.class);
                totalRideFare += totalPrice;
                memberRef.child("rideCount").setValue(rideCount);
                memberRef.child("totalRideFare").setValue(totalRideFare);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tranRef.setValue(transaction);
        rideRef.setValue(ride);

        // Assuming a simple cashback rule: 5% cashback on the total ride fare
        double cashbackPercentage = 5.0;
        double cashbackAmount = (cashbackPercentage / 100.0) * totalPrice;

        // Formatting cashbackAmount to two decimal places
        String formattedCashbackAmount = String.format("%.2f", cashbackAmount);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Wallet").child(userId).child("balance");
        if(totalPrice >= 0.05){
            // Add the cashback to the user's wallet
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Check if the data snapshot has a value
                    if (dataSnapshot.exists()) {
                        // Retrieve the balance directly (assuming it's stored as a String)
                        String balance = dataSnapshot.getValue(String.class);
                        double currentBalance = Double.parseDouble(balance);
                        double newBalance = currentBalance + cashbackAmount;
                        String finalBalance = String.format("%.2f", newBalance);
                        userRef.setValue(finalBalance);
                        // Display a toast message about the cashback
                        Toast.makeText(CountingActivity.this, "You've earned RM" + formattedCashbackAmount + " cashback!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "saveData:onCancelled", error.toException());
                }
            });
            Cashbackinfo cashbackinfo = new Cashbackinfo(cashbackAmount, getCurrentDateTime());
            cashbacksRef.setValue(cashbackinfo);
        }


    }
    private String getCurrentDateTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Format: MMM d, yyyy - hh:mm a
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy - hh:mm a");
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

}
