//This should be the end ride activity
package com.example.jomride;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

//implements PopupDismissListener
//must remember the implementation
//for this I am assuming that the end ride button and the interface after start ride will be in a new activity
public class MainActivity extends AppCompatActivity  {
    //test data
    String transport="bicycle";
    int point=5;
    double rate=0.2;
    double duration=6.0;
    double distance=9;
    String start="KK8";
    String end="FSKTM";

    //call authentictaion
    private FirebaseAuth mAuth;

    //call database
    private FirebaseDatabase database;

    //call realtime database reference for ride History
    private DatabaseReference rideHistoryRef;

    //point and balance need to be set as 0 after registration to check if balance got 10 a not
    //Or can prompt to remind user that they need to top up and wallet must have at least RM 10.00 to start a ride
    //save history under user, update balance and point in user
    private DatabaseReference RuserReference;
    private DatabaseReference HuserReference;
    HistoryData itemInfo;

    //for wallet
    private DatabaseReference walletReference;

    //for wallethistory
    private DatabaseReference historyreference;
    private String userId="2";

    private RideData ridedata;
    private WalletData walletdata;
    private double balance;
    private int Tpoint;

    private LocalDateTime starttime;

    private boolean rideDiscount=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get authentication+current id
        mAuth=FirebaseAuth.getInstance();
        userId=mAuth.getCurrentUser().getUid();

        //Initialize database
        database=FirebaseDatabase.getInstance();

        //Set balance and point as 0
        walletReference=database.getReference().child("Wallet").child(userId);

        //need put this after registration is done
//        WalletData walletdata=new WalletData("0.00",0);
//        walletReference.setValue(walletdata);
        
        readWalletData();
        //call button to bring to end ride receipt
        Button BtnEndRide=findViewById(R.id.Btn_EndRide);
        BtnEndRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Set everything and pass into the method

                //pass in argument of starttime from previous activity here
                starttime=LocalDateTime.of(2023,12,2,16,30);
                //starttime = (LocalDateTime) intent.getSerializableExtra("dateTime");

                duration = calduration(starttime);
                double total=getTotal(duration,rate);
                ridedata=new RideData(setDateTime(),total,getPoint(total),transport,rate,distance,duration,start,end);
               // ArrayList<String>data=saveRideHistory(transport,point,distance,duration,start,end,rate);

                //Deduct balance + add point
                //Update balance +point in database
                updateWalletData(total);

                //Upload data into database
                recordRide(ridedata);
                addUsageData(total);
                //https://www.geeksforgeeks.org/android-pass-parcelable-object-from-one-activity-to-another-using-putextra/
                //https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
                //Move from this activity to another and pass arguments
//                Intent intent = new Intent(MainActivity.this,ReceiptActivity.class);
//                intent.putExtra("rideData",ridedata);
//                startActivity(intent);
//                finish();

            }
        });

        //Test shop
        Button dressclip=findViewById(R.id.Btn_dressclip);
        dressclip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemInfo=new HistoryData(setDateTime(),"Dress Clip","favourite","2.90");
                Intent intentItem = new Intent(MainActivity.this,DressClickActivity.class);
                intentItem.putExtra("iteminfo",itemInfo);
                startActivity(intentItem);
            }
        });

    }


//record ride for receipt 
    private void recordRide(RideData data) {
        rideHistoryRef=database.getReference().child("History").child("ride");
        RuserReference=database.getReference().child("User").child(userId).child("ride");

        //get id
        String rideHistory_id=rideHistoryRef.push().getKey();
        RuserReference.child(rideHistory_id).setValue(true);
        rideHistoryRef.child(rideHistory_id).setValue(data);
    }

    //REference
    //https://beknazarsuranchiyev.medium.com/date-and-time-in-java-e012752da4be#:~:text=To%20work%20with%20dates%20only,now()%20method.
    //ZOneid:https://mkyong.com/java8/java-display-all-zoneid-and-its-utc-offset/
    //LocalDateTimeApplication(https://medium.com/@thilini_/working-with-time-zones-and-time-in-software-development-248234771c05
    private String setDateTime()
    {
        LocalDateTime dateTime= LocalDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, YYYY - hh:mm a");
        String dateTimeStr = formatter.format(dateTime);
        return dateTimeStr;
    }


    //get Duration
    private double calduration(LocalDateTime startTime) {
        LocalDateTime endTime= LocalDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"));
        long Sduration=Duration.between(startTime, endTime).getSeconds();
        double Rduration=Sduration/60.0;
        return Rduration;
    }

    //duration must be in minutes to work can use Duration class after get second then can can convert to minute
    //https://www.baeldung.com/java-period-duration#:~:text=Alternatively%2C%20we%20can%20obtain%20a,%3A00%22)%3B%20Duration.
    //For fare calculation , need pass in duration in minute and rate per minute
    private double getTotal(double duration, double rate)
    {
        //need pass in read discount call
        //set rideDIscount as true/false

        double percentage=0.15;

        if(rideDiscount==true){

            //after this can set value as false
            //Or want let them use how many time also can
            return duration*rate*(1-percentage)*100/100 ;}

        else
            return duration*rate*100/100;
    }

    //put point function here
    private int getPoint(double total)
    {
        int point=5;

        //Put the method content here

        return point;
    }

    //deduct balance from wallet
    private void updateWalletData(double total){
        if(walletdata!=null) {
            balance = Double.parseDouble(walletdata.getBalance()) - total;
            Tpoint = walletdata.getPoint() + getPoint(total);
            String Sbalance = String.format("%.2f", balance);

            WalletData walletData = new WalletData(Sbalance, Tpoint);
            walletReference.setValue(walletData);
            Intent intent = new Intent(MainActivity.this,ReceiptActivity.class);
            intent.putExtra("rideData",ridedata);
            startActivity(intent);
            finish();
        }else
        {
            Toast.makeText(this, "wallet not ready", Toast.LENGTH_SHORT).show();
        }


    }

    //for transaction history
    private void addUsageData(double total)
    {
        historyreference=database.getReference().child("History").child("transaction");
        HuserReference=database.getReference().child("User").child(userId).child("transaction");
        //Usage data
        String type="Ride";
        String utotal=String.format("-RM %.2f",total);
        HistoryData wud=new HistoryData(setDateTime(),String.format("%.2f km - %.2f min ride",distance,duration),"wallet",utotal);
        //get id
        String usageHistory_id= historyreference.push().getKey();
        HuserReference.child(usageHistory_id).setValue(true);
        historyreference.child(usageHistory_id).setValue(wud);
    }

    
    private void readWalletData()
    {
        walletReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                walletdata = dataSnapshot.getValue(WalletData.class);
//                if (walletdata != null) {
//                    balance=Double.parseDouble(walletdata.getBalance())-getTotal(duration,rate);
//                    Tpoint=walletdata.getPoint()+getPoint(getTotal(duration,rate));
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }
//    private void readWalletData(double total)
//    {
//        CountDownLatch latch = new CountDownLatch(1);
//        walletReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                walletdata = dataSnapshot.getValue(WalletData.class);
//                if (walletdata)
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w(TAG, "loadPost:onCancelled", error.toException());
//
//            }
//        });
//    }

}

//OnCLick for PopUp
//                // Initialize the dismissListener
//                //to trigger the action to be done after the popup is dismissed
//                PopupDismissListener dismissListener = new PopupDismissListener() {
//                    @Override
//                    public void onPopupDismissed() {
//                        startNewActivity();
//                    }
//                };
//                PopUpClass ridereceipt=new PopUpClass(data,dismissListener);
////                PopUpClass ridereceipt=new PopUpClass(data);
//                ridereceipt.showPopupWindow(view);

//to store all ride history in an Array List to easily pass to the popUp window
//It is stored in this sequence
//UserId,DateTime,total,point,transport type, rate, duration,distance
//    private ArrayList<String> saveRideHistory(String transportType,int point,double dist, double duration, String start, String end,double rate){
//        ArrayList<String>rideHistory=new ArrayList<>();
//       // rideHistory.add(getUserId(userId));//user id; need change this later
//        rideHistory.add(setDateTime());//date
//        rideHistory.add(String.format("RM %.2f",getTotal(duration,rate)));//total
//        rideHistory.add("+ "+getPoint(getTotal(duration,rate))+" pts ");//point can change point variable to a method to calculate points
//        rideHistory.add(getransportType(transport));//transport type
//        rideHistory.add(String.format("RM %.2f /min",getRate(rate)));//rate
//        rideHistory.add(String.format("%.2f min",getDuration(duration)));//duratiom
//        rideHistory.add(String.format("%.2f km",getDistance(distance)));//distance
//        rideHistory.add(getStartPoint(start));//start
//        rideHistory.add(getEndPoint(end));//end
//        return rideHistory;
//    }


//Create Has Map to store ride data
//        HashMap <String,Object>ridemap=new HashMap<>();
//        ridemap.put("datetime",data.get(0));
//        ridemap.put("total",data.get(1));
//        ridemap.put("point",data.get(2));
//        ridemap.put("transporttype",data.get(3));
//        ridemap.put("rate",data.get(4));
//        ridemap.put("duration",data.get(5));
//        ridemap.put("distance",data.get(6));
//        ridemap.put("start",data.get(7));
//        ridemap.put("end",data.get(8));
//for this need use updateChildren(ridemap);
