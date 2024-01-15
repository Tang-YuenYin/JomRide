package com.example.jomride;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.Calendar;

public class YourInformation extends AppCompatActivity {

    private EditText fullNameEditText, icPassportEditText, addressEditText, occupationEditText;
    private TextView birthdayTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String selectedDate;
    private Button submitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_information);

        // Initialize your views
        fullNameEditText = findViewById(R.id.fullName);
        icPassportEditText = findViewById(R.id.icPassportNum);
        addressEditText = findViewById(R.id.address);
        occupationEditText = findViewById(R.id.occupation);
        birthdayTextView = findViewById(R.id.birthday);

        birthdayTextView.setOnClickListener(v -> showDatePickerDialog());
        submitButton = findViewById(R.id.submit);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("membership");

        // Retrieve user information from Firebase and display it
        getUserInformation();
        editUserInfo();
    }

    private void getUserInformation() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve user information
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String icPassport = snapshot.child("icPassport").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String occupation = snapshot.child("occupation").getValue(String.class);
                    String birthday = snapshot.child("birthday").getValue(String.class);

                    // Display information in EditText and TextView
                    fullNameEditText.setText(fullName);
                    icPassportEditText.setText(icPassport);
                    addressEditText.setText(address);
                    occupationEditText.setText(occupation);
                    birthdayTextView.setText(birthday);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void editUserInfo() {
        submitButton.setOnClickListener(v -> {
            String fullName = fullNameEditText.getText().toString();
            String icPassport = icPassportEditText.getText().toString();
            String address = addressEditText.getText().toString();
            String occupation = occupationEditText.getText().toString();
            String selectedDate = birthdayTextView.getText().toString();
            if (!fullName.isEmpty() && !icPassport.isEmpty() && !address.isEmpty() && !occupation.isEmpty() && !selectedDate.isEmpty()) {
                submit(fullName, icPassport, address, occupation, selectedDate);
            }
        });
    }

    private void submit(String fullName, String icPassport, String address, String occupation, String selectedDate) {
        saveDataToDatabase(fullName, icPassport, address, occupation, selectedDate);
        Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();
        startNewActivity(HomeActivity.class);
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Here, 'year', 'month', and 'dayOfMonth' are the selected date components
                    selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                    //Display the selected date on the textview
                    birthdayTextView.setText(selectedDate);

                    // You can then display this date in a TextView or directly save it to Firebase
                },
                // Set default date to 20 years back for simplicity (adjust as needed)
                Calendar.getInstance().get(Calendar.YEAR) - 20,
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );

        // Set the maximum date to today's date (optional)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void saveDataToDatabase(String fullName, String icPassport, String address, String occupation, String selectedDate) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference membershipRef = database.getReference("User").child(userId).child("membership");
        // Save user information to the database
        membershipRef.child("fullName").setValue(fullName);
        membershipRef.child("icPassport").setValue(icPassport);
        membershipRef.child("address").setValue(address);
        membershipRef.child("occupation").setValue(occupation);
        membershipRef.child("birthday").setValue(selectedDate);
        membershipRef.child("isMember").setValue(true);
    }




    // Method to update user information in the database
    private void updateUserInfo() {
        String fullName = fullNameEditText.getText().toString();
        String icPassport = icPassportEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String occupation = occupationEditText.getText().toString();
        String birthday = birthdayTextView.getText().toString();

        // Update user information in the database
        userRef.child("fullName").setValue(fullName);
        userRef.child("icPassport").setValue(icPassport);
        userRef.child("address").setValue(address);
        userRef.child("occupation").setValue(occupation);
        userRef.child("birthday").setValue(birthday);

        Toast.makeText(this, "Information updated successfully", Toast.LENGTH_SHORT).show();
    }

    // Button click to update user information
    public void updateInformation(View view) {
        updateUserInfo();
    }

    // Button click to go back
    public void Back(View view) {
        finish();
    }
    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(YourInformation.this, cls);
        startActivity(intent);
    }
}
