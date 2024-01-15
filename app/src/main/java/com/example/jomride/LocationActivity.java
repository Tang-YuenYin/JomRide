package com.example.jomride;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    //call database
    private FirebaseDatabase database;

    private String userId;
    private WalletData walletdata;
    private Double finalBalance;
    private DatabaseReference walletRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        Spinner DDLDestination = (Spinner) findViewById(R.id.DDLDestination);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.DestinationSlots, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        DDLDestination.setAdapter(adapter);

        Button ClickDest = findViewById(R.id.BtnDest);
        ClickDest.setOnClickListener(ButtonDest);

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(ButtonBack);
    }

    private View.OnClickListener ButtonDest = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //get balance
                Spinner DDLDestination = findViewById(R.id.DDLDestination);

            // Get the selected item from the spinner
            String selectedDestination = DDLDestination.getSelectedItem().toString();

            if (selectedDestination.equals("None")) {
                Toast.makeText(LocationActivity.this, "Please select a destination.", Toast.LENGTH_SHORT).show();
            } else  {
                // Create an Intent and pass the selected destination to the next activity
                Intent intent = new Intent(LocationActivity.this, TransportActivity.class);
                intent.putExtra("Destination", selectedDestination);
                startActivity(intent);
            }
        }
    };

    private View.OnClickListener ButtonBack = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(LocationActivity.this,HomeActivity.class);
            startActivity(intent);
        }
    };




}

