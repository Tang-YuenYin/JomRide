package com.example.rewardmainpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpMembership extends AppCompatActivity {

    private EditText fullName;
    private EditText birthday;
    private EditText icPassportNum;
    private EditText address;
    private EditText occupation;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_membership);
        fullName = findViewById(R.id.fullName);
        birthday = findViewById(R.id.birthday);
        icPassportNum = findViewById(R.id.icPassportNum);
        address = findViewById(R.id.address);
        occupation = findViewById(R.id.occupation);
        intent = new Intent(this, YourInformation.class);
    }

    public void Submit(View view){
        intent.putExtra("fullname", fullName.getText().toString());
        intent.putExtra("birthday", birthday.getText().toString());
        intent.putExtra("icPassportNum", icPassportNum.getText().toString());
        intent.putExtra("address", address.getText().toString());
        intent.putExtra("occupation", occupation.getText().toString());
        Toast.makeText(getApplicationContext(), "Sign Up successful!", Toast.LENGTH_SHORT).show();
        openYourInfo();
    }

    public void Back(View view){
        finish();
    }

    public void openYourInfo(){
        startActivity(intent);

    }
}
