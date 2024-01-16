package com.example.jomride;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class RatingActivity extends AppCompatActivity {
    // Reference to the Firebase Database
    private DatabaseReference feedbackRef;
    // Load the Firebase credentials and initialize the app
    FileInputStream serviceAccount;

    {
        try {
            serviceAccount = new FileInputStream("path/to/serviceAccountKey.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
//
//    FirebaseOptions options = new FirebaseOptions.Builder()
//            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//            .setDatabaseUrl("https://jomrideapp-default-rtdb.firebaseio.com")
//            .build();
//
//FirebaseApp.initializeApp(options);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        //Connect with UI Widget RateBarFeedback, TVRating, ETFeedback,BtnFeedback
        RatingBar RatingBarFeedback = findViewById(R.id.RatingBarFeedback);
        TextView TVRating = findViewById(R.id.TVRating);
        EditText ETFeedback = findViewById(R.id.ETFeedback);//needed if want to collect the feedback later
        Button BtnFeedback = findViewById(R.id.BtnFeedback);

        // Initialize Firebase Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        feedbackRef = database.getReference().child("feedback");

        //The button OnClickListener to toast a message and share the cashback code is user has entered the feedback.
        BtnFeedback.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Get the rating and feedback text
                float rating = RatingBarFeedback.getRating();
                String feedbackText = ETFeedback.getText().toString();

                // Create a Feedback object (you can create a custom class for feedback)
                Feedback feedback = new Feedback(rating, feedbackText);

                // Push the feedback object to Firebase
                feedbackRef.push().setValue(feedback);

                // Show a thank you message
                String message = "Thank you for your feedback!";
                if(!ETFeedback.getText().toString().isEmpty())
                    message = message + "\nPlease enjoy your Cashback";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        //The rating bar OnRatingBarChangeListener to change the rating whenever it is used by the user.
        RatingBarFeedback.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            @Override
            public void onRatingChanged(RatingBar RatingBar, float rating, boolean fromUser){
                TVRating.setText("You have rated " + rating);
            }
        });
    }
}

