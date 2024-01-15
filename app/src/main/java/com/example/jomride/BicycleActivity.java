package com.example.jomride;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BicycleActivity extends AppCompatActivity {

    private Button BtnRent;
    private Button review;
    private Dialog mDialog;
    private ImageView Back;

    private Intent countingIntent;
    private Intent feedbackIntent;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bicycle);

        BtnRent = findViewById(R.id.BtnRent);
        review = findViewById(R.id.review);
        mDialog = new Dialog(this);
        Back = findViewById(R.id.back);

        countingIntent = new Intent(BicycleActivity.this, CountingActivity.class);
        feedbackIntent = new Intent(BicycleActivity.this, FeedbackListActivity.class);

        BtnRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                showPopupDialog();
            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BicycleActivity.this,TransportActivity.class);
                startActivity(intent);
            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupDialogReview();

            }
        });
    }

    private void showPopupDialog(){
        mDialog.setContentView(R.layout.activity_codebicycle);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText etCode = mDialog.findViewById(R.id.ETCode);
        Button btnConfirm = mDialog.findViewById(R.id.BtnConfirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCode = etCode.getText().toString().trim();
                if (isValidCode(enteredCode)) {
                    Intent get = getIntent();
                    String message = get.getStringExtra("Destination");
                    countingIntent.putExtra("bikeCode", enteredCode);
                    countingIntent.putExtra("Destination", message);
                    // Start UnlockedActivity with startActivityForResult
                    Intent intent = new Intent(BicycleActivity.this, UnlockedActivity.class);
                    startActivityForResult(intent, 1); // '1' is the requestCode, you can use any unique value
                    // Pass the bike_code value to CountingActivity

                    // Close the dialog
                    mDialog.dismiss();
                } else {
                    Toast.makeText(BicycleActivity.this, "Invalid code. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mDialog.show();
    }

    private void showPopupDialogReview(){
        mDialog.setContentView(R.layout.activity_codebicycle);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText etCode = mDialog.findViewById(R.id.ETCode);
        Button btnConfirm = mDialog.findViewById(R.id.BtnConfirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCode = etCode.getText().toString().trim();
                if (isValidCode(enteredCode)) {
                    feedbackIntent.putExtra("bikeCode", enteredCode);
                    // Start UnlockedActivity with startActivityForResult
                    startActivityForResult(feedbackIntent, 1); // '1' is the requestCode, you can use any unique value
                    // Pass the bike_code value to CountingActivity

                    // Close the dialog
                    mDialog.dismiss();
                } else {
                    Toast.makeText(BicycleActivity.this, "Invalid code. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { // Assuming requestCode 1 is for UnlockedActivity
            if (resultCode == RESULT_OK) {
                // UnlockedActivity finished successfully
                countingIntent.putExtra("VehicleType", "Bicycle");
                startActivity(countingIntent);
            } else {
                // Handle the case where UnlockedActivity did not finish successfully
            }
        }
    }
    private boolean isValidCode(String code) {
        return code.equals("B101") || code.equals("B102") || code.equals("B103") || code.equals("B104") || code.equals("B105");
    }
}
