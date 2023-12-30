package com.example.jomrideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class register extends AppCompatActivity {

    EditText Name, Email, Password, Phone;
    Button BtnRegister;
    TextView loginlink;
    FirebaseAuth mAuth;
    ProgressDialog loadingBar;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Name = findViewById(R.id.register_Name);
        Email = findViewById(R.id.register_Email);
        Password = findViewById(R.id.register_Password);
        Phone = findViewById(R.id.register_Phone);
        BtnRegister = findViewById(R.id.BtnRegister);
        loginlink = findViewById(R.id.login_link);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(this);

        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = Name.getText().toString();
                String email = Email.getText().toString();
                String password = Password.getText().toString();
                String phone = Phone.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(phone)){
                    Toast.makeText(register.this, "Empty credentials!", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() < 6){
                    Toast.makeText(register.this, "Password too short!", Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingBar.setTitle("User Registration");
                    loadingBar.setMessage("Please wait, while we are register your data.");
                    loadingBar.show();
                    RegisterUser(name , email , password , phone);
                }
            }
        });

        loginlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logIntent = new Intent(register.this, login.class);
                startActivity(logIntent);
            }
        });
    }

    private void RegisterUser(String name, String email, String password, String phone) {

        mAuth.createUserWithEmailAndPassword(email , password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                HashMap<String , Object> map = new HashMap<>();
                map.put("name" , name);
                map.put("email", email);
                map.put("id" , mAuth.getCurrentUser().getUid());
                map.put("phone" , phone);

                reference.child("users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(register.this, "User Register Successfully.", Toast.LENGTH_SHORT).show();
                            Intent userIntent = new Intent(register.this, profilepage.class);
                            startActivity(userIntent);

                            loadingBar.dismiss();
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(register.this, "Registration Unsuccessful. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e("Registration Error", e.getMessage()); // Log the error for debugging
                loadingBar.dismiss();
            }
        });

    }
}
