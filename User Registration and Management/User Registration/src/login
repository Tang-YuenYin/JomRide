package com.example.jomrideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    EditText Email, Password;
    Button BtnLogin;
    TextView registerlink;
    FirebaseAuth mAuth;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = findViewById(R.id.login_Email);
        Password = findViewById(R.id.login_Password);
        BtnLogin = findViewById(R.id.BtnLogin);
        registerlink = findViewById(R.id.register_link);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(login.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("User Login");
                    loadingBar.setMessage("Please wait, while we are checking your credentials.");
                    loadingBar.show();
                    LoginUser(email , password);
                }
            }
        });

        registerlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(login.this, register.class);
                startActivity(regIntent);
            }
        });
    }

    private void LoginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(login.this, "User Login Successfully.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                    Intent userIntent = new Intent(login.this, profilepage.class);
                    startActivity(userIntent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(login.this, "Login Unsuccessful. Please try again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        });
    }
}
