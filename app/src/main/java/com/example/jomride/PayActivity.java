package com.example.jomride;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PayActivity extends AppCompatActivity {

    private DatabaseReference walletRef;
    //for wallet

    //for wallethistory
    private DatabaseReference historyreference;
    private DatabaseReference HuserReference;
    private FirebaseAuth mAuth;
    private ImageView Back;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        walletRef = FirebaseDatabase.getInstance().getReference().child("Wallet").child(userId).child("balance");
        String price = getIntent().getStringExtra("PRICE_EXTRA");
        String numericPrice = price.replace("RM ", "");  // Removes the "RM " part
        double amount = Double.parseDouble(numericPrice);
        Button BtnPay = findViewById(R.id.BtnPay);
        BtnPay.setText("Pay "+price);
        String item = getIntent().getStringExtra("item");
        TextView TVItem = findViewById(R.id.TVItem);
        TVItem.setText("Item Type: "+item);
        BtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deductFromWallet(amount);
                saveTransactionToHistory(amount, "Wallet");

            }
        });

        Back = findViewById(R.id.back);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void deductFromWallet(final double amountToDeduct) {

        walletRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double currentBalance = Double.parseDouble(dataSnapshot.getValue().toString());
                    if (currentBalance >= amountToDeduct) {
                        double updatedBalance = currentBalance - amountToDeduct;
        //                String balance = Double.toString(updatedBalance);
                        String tbalance = String.format("%.2f", updatedBalance);
                        walletRef.setValue(tbalance);
                        Toast.makeText(PayActivity.this, "Payment successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PayActivity.this,PaySuccessful.class);
                        startActivity(intent);

                        // Navigate to PaySuccessful Activity or any other activity
                    } else {
                        Toast.makeText(PayActivity.this, "Insufficient balance!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PayActivity.this, "Balance not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PayActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTransactionToHistory(double amount, String transactionType) {
        // Step 1: Get the User's Unique ID
        String userId = mAuth.getCurrentUser().getUid();
        String item = getIntent().getStringExtra("item");

        // Step 2: Generate a Unique Transaction ID
        String transactionId = FirebaseDatabase.getInstance().getReference().child("User").child(userId).child("transaction").push().getKey();

        // Step 3: Create a HistoryData Object
        String currentDateTime = getCurrentDateTime(); // Get the current date-time in the required format
        String description = item; // You can customize this description if needed
        String type = "Wallet"; // This could be "Credit" or "Debit" based on the context
        String formattedAmount = String.valueOf(amount); // Convert the amount to string

        HistoryData historyData = new HistoryData(currentDateTime, description, type, "-RM"+formattedAmount);
//
//        // Step 4: Create a HistoryObject Object
//        HistoryObject historyObject = new HistoryObject(transactionId, historyData);
//
//        // Step 5: Save to Firebase
//        DatabaseReference userTransactionRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId).child("transaction").child(transactionId);
//        userTransactionRef.setValue(historyObject);
        historyreference=FirebaseDatabase.getInstance().getReference().child("History").child("transaction");
        HuserReference=FirebaseDatabase.getInstance().getReference().child("User").child(userId).child("transaction");

        //get id
        String usageHistory_id= historyreference.push().getKey();
        HuserReference.child(usageHistory_id).setValue(true);
        historyreference.child(usageHistory_id).setValue(historyData);
    }

    private String getCurrentDateTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy - hh:mm a");
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }
}
