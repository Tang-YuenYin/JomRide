package com.example.rewardmainpage;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class YourInformation extends AppCompatActivity {

    private TextView fullNameTextView;
    private TextView birthdayTextView;
    private TextView icPassportNumTextView;
    private TextView addressTextView;
    private TextView occupationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_information);

        // Retrieve data from SignUpMembership class
        Intent intent = getIntent();
        String receivedFullName = intent.getStringExtra("fullname");
        String receivedBirthday = intent.getStringExtra("birthday");
        String receivedIcPassportNum = intent.getStringExtra("icPassportNum");
        String receivedAddress = intent.getStringExtra("address");
        String receivedOccupation = intent.getStringExtra("occupation");

        System.out.println(receivedAddress);

        // Find TextViews in your layout
        fullNameTextView = findViewById(R.id.fullName);
        birthdayTextView = findViewById(R.id.birthday);
        icPassportNumTextView = findViewById(R.id.icPassportNum);
        addressTextView = findViewById(R.id.address);
        occupationTextView = findViewById(R.id.occupation);

        // Set the retrieved data to the TextViews
        fullNameTextView.setText(receivedFullName);
        birthdayTextView.setText(receivedBirthday);
        icPassportNumTextView.setText(receivedIcPassportNum);
        addressTextView.setText(receivedAddress);
        occupationTextView.setText(receivedOccupation);
    }

    public void Back(View view){
        finish();
    }
}
