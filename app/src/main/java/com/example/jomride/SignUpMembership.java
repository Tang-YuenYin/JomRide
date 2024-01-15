package com.example.jomride;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class SignUpMembership extends AppCompatActivity {
    
    private Button submitButton;
    private EditText fullNameEditText, icPassportEditText, addressEditText, occupationEditText;
    private TextView datePickerButton;
    private FirebaseAuth mAuth;
    private String selectedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_membership);

        // Initialize your views
        fullNameEditText = findViewById(R.id.fullName);
        icPassportEditText = findViewById(R.id.icPassportNum);
        addressEditText = findViewById(R.id.address);
        occupationEditText = findViewById(R.id.occupation);


        datePickerButton = findViewById(R.id.datePickerButton);
        datePickerButton.setOnClickListener(v -> showDatePickerDialog());

        submitButton = findViewById(R.id.Submit);
        submitButton.setOnClickListener(v -> {
            String fullName = fullNameEditText.getText().toString();
            String icPassport = icPassportEditText.getText().toString();
            String address = addressEditText.getText().toString();
            String occupation = occupationEditText.getText().toString();
            String selectedDate = datePickerButton.getText().toString();
            if (!fullName.isEmpty() && !icPassport.isEmpty() && !address.isEmpty() && !occupation.isEmpty() && !selectedDate.isEmpty()) {
                submit(fullName, icPassport, address, occupation, selectedDate);
            }
            else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void submit(String fullName, String icPassport, String address, String occupation, String selectedDate) {
        saveDataToDatabase(fullName, icPassport, address, occupation, selectedDate);
        Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();
        startNewActivity(YourInformation.class);
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Here, 'year', 'month', and 'dayOfMonth' are the selected date components
                    selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                    //Display the selected date on the textview
                    datePickerButton.setText(selectedDate);

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

    public void Back(View view){
        startNewActivity(Membership.class);
    }
    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(SignUpMembership.this, cls);
        startActivity(intent);
    }
}
