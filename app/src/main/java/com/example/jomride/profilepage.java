package com.example.jomride;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

public class profilepage extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    ImageView profileImage;
    TextView FullName, Email, Phone;
    ImageButton BtnLogout, BtnNotification;
    Button BtnEditProfile;
    String userID;
    DatabaseReference reference;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);

        FullName = findViewById(R.id.TVName);
        Phone = findViewById(R.id.TVPhone);
        Email = findViewById(R.id.TVEmail);
        profileImage = findViewById(R.id.profileImage);
        BtnLogout = findViewById(R.id.BtnLogout);
        BtnEditProfile = findViewById(R.id.BtnEditProfile);
        ImageButton Back = findViewById(R.id.back);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        reference = database.getReference("users").child(userID);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profilepage.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    FullName.setText(userProfile.getName());
                    Phone.setText(userProfile.getPhone());
                    Email.setText(userProfile.getEmail());

                    // Retrieve download URL from intent
                    String downloadUrl = getIntent().getStringExtra("downloadUrl");

                    // Load and display profile image using Picasso
                    if (downloadUrl != null && !downloadUrl.isEmpty()) {
                        Picasso.get().load(downloadUrl).into(profileImage);

                        // Update the imageUrl in the user profile
                        userProfile.setImageUrl(downloadUrl);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(profilepage.this, "Error retrieving user data", Toast.LENGTH_SHORT).show();
            }
        });

        BtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent logoutIntent = new Intent(profilepage.this, LoginActivity.class);
                startActivity(logoutIntent);
                finish();
                Toast.makeText(profilepage.this, "Logout Successful!", Toast.LENGTH_SHORT).show();
            }
        });

        BtnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), editprofile.class);
                i.putExtra("name", FullName.getText().toString());
                i.putExtra("email", Email.getText().toString());
                i.putExtra("phone", Phone.getText().toString());
                startActivity(i);
            }
        });


    }

    public void Back(View view){
        startNewActivity(HomeActivity.class);
    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(profilepage.this, cls);
        startActivity(intent);
    }
}
