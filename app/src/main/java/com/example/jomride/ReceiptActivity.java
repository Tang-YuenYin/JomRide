package com.example.jomride;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

public class ReceiptActivity extends AppCompatActivity{
    //Arraylist for all the data
    //ArrayList<String> rideData;
    private RideData rideData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        //save ride data from the previous event
        //rideData=(ArrayList<String>) getIntent().getSerializableExtra("rideData");
        rideData=(RideData) getIntent().getSerializableExtra("rideData");

        //Set Text View in the Receipt
        setTextView();

        //Call button for following actions
        Button AProceed=findViewById(R.id.Btn_AProceed);
        AProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Ifeedback =new Intent(ReceiptActivity.this,FeedbackActivity.class);
                Ifeedback.putExtra("bike_code",rideData.getBike_code());
                startActivity(Ifeedback);

                finish();
            }
        });
    }

    //PopUp
    //to move from one activity to another and close this current activity
//    private void startNewActivity() {
//        //Move from here to home
//        Intent intent = new Intent(ReceiptActivity.this, HomeActivity.class);
//        startActivity(intent);
//        //Close current Activity so that will not ne backstackked
//        finish();
//    }
    public void setTextView()
    {
        //Set the values in the receipt
        TextView TV_ADateTime=findViewById(R.id.TV_ADateTime);
        TextView TV_Atotal=findViewById(R.id.TV_Atotal);
        TextView TV_Apoint=findViewById(R.id.TV_Apoint);
        TextView TV_Ainput_tranport=findViewById(R.id.TV_Ainput_tranport);
        TextView TV_Ainput_rate=findViewById(R.id.TV_Ainput_rate);
        TextView TV_Ainput_duration=findViewById(R.id.TV_Ainput_duration);
        TextView TV_Ainput_dist=findViewById(R.id.TV_Ainput_dist);
        TextView TV_Ainput_start=findViewById(R.id.TV_Ainput_start);
        TextView TV_Ainput_end=findViewById(R.id.TV_Ainput_end);

        TV_ADateTime.setText(rideData.getTimeDate());
        TV_Atotal.setText(String.format("RM %.2f",rideData.getPrice()));
        TV_Apoint.setText(String.format("%d pts",rideData.getPoint()));
        TV_Ainput_tranport.setText(String.format("%s ( %s )",rideData.getTransport(),rideData.getBike_code()));
        TV_Ainput_rate.setText(String.format("RM %.2f /min",rideData.getRate()));
        TV_Ainput_duration.setText(String.format("%.2f min",rideData.getDuration()));
        TV_Ainput_dist.setText(String.format("%.2f km",rideData.getDistance()));
        TV_Ainput_start.setText(rideData.getStart());
        TV_Ainput_end.setText(rideData.getEnd());

    }
//    @Override
//    //Override abstract method in the interface
//    public void onPopupDismissed() {
//        startNewActivity();
//    }
}

//pass array list from one activity to another iwth intenr
//https://stackoverflow.com/questions/18050030/intent-putextra-arraylistnamevaluepair
//https://www.tutorialspoint.com/how-to-pass-an-arraylist-to-another-activity-using-intents-in-android

//pass data from one activity to another with intent
//https://www.geeksforgeeks.org/how-to-send-data-from-one-activity-to-second-activity-in-android/
// https://medium.com/android-news/passing-data-between-activities-using-intent-in-android-85cb097f3016

//Add font
//https://developer.android.com/develop/ui/views/text-and-emoji/fonts-in-xml

//Pop up
//                PopupDismissListener dismissListener = new PopupDismissListener() {
//                    @Override
//                    //Move to another activity :Home Activity
//                    public void onPopupDismissed() {
//                        startNewActivity();
//                    }
//                };
//                //Can change this to the rating bar pop up
//                //Edit the popup class contructor to meet your requirement
//                PopUpClass ridereceipt=new PopUpClass(rideData,dismissListener);
////                PopUpClass ridereceipt=new PopUpClass(data);
//                //Edit rating display pop up method
//                ridereceipt.showRideHistoryPopupWindow(view);

