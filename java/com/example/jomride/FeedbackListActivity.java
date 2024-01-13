package com.example.jomride;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedbackListActivity extends AppCompatActivity {

    private DatabaseReference feedbackRef;
    private List<Feedback> feedbackList;
    private List<Feedback> backupList;
    private FeedbackAdapter feedbackAdapter;
    private String bike_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);
        bike_code= getIntent().getStringExtra("bike_code");
        // Initialize Firebase Database reference
        feedbackRef = FirebaseDatabase.getInstance().getReference().child("feedback").child(bike_code);

        // Initialize RecyclerView and adapter
        RecyclerView recyclerView = findViewById(R.id.recyclerViewFeedback);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedbackList = new ArrayList<>();
        backupList= new ArrayList<>();
        feedbackAdapter = new FeedbackAdapter(feedbackList);
        recyclerView.setAdapter(feedbackAdapter);
        Button BtndisplayAll=findViewById(R.id.btnDisplayAll);
        // Retrieve all feedback initially
        retrieveFeedbackData();

        BtndisplayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveFeedbackData();
            }
        });
        // Buttons for filtering feedback by rating
        for (float i = 1.0f; i <= 5.0f; i += 1.0f) {
            addButtonForRating(i);

        }
    }

    private void retrieveFeedbackData() {
        feedbackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                feedbackList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Feedback feedback = snapshot.getValue(Feedback.class);
                    if (feedback != null) {
                        feedbackList.add(feedback);
                        backupList.add(feedback);
                    }
                }

                feedbackAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    private void filterFeedbackByRating(final float filterRating) {
        feedbackRef.orderByChild("rating").equalTo(filterRating).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                feedbackList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Feedback feedback = snapshot.getValue(Feedback.class);
                    if (feedback != null) {
                        feedbackList.add(feedback);
                    }
                }

                feedbackAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    private void addButtonForRating(final float rating) {
        Button button = new Button(this);
        button.setText(String.valueOf(rating));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterFeedbackByRating(rating);
            }
        });

        // Add the button to your layout
        // Assuming you have a LinearLayout with id 'ratingButtonsLayout' in your activity_feedback_list.xml
        ((LinearLayout) findViewById(R.id.ratingButtonsLayout)).addView(button);
    }
}
