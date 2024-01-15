package com.example.jomride;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;


public class PopUpClass{
    //private ArrayList<String> rideHistory=new ArrayList<>();
    private RideData rideData;
    private PopupDismissListener dismissListener;

    private HistoryObject object;

    private HistoryData data;
    //This is for single history display in the ride history list
    public PopUpClass(RideData ridedata)
    {
        this.rideData=ridedata;
    }

    //PopUp for Transaction History
    public PopUpClass(HistoryObject object)
    {
        this.object=object;
    }

    //This is for after ride
    public PopUpClass(RideData ridedata,PopupDismissListener listener)
    {
        this.rideData=ridedata;
        dismissListener=listener;
    }

    //Can addd own constructor for ratng pop up

    //get rideData ArrayList
//   // public ArrayList<String> getRideHistory() {
//        return rideHistory;
//    }


    //Ride PopUpWindow display method
    public void showRideHistoryPopupWindow(View view)
    {
        //Create a View object through inflater
        LayoutInflater inflater = (LayoutInflater)view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View RidepopUp = inflater.inflate(R.layout.ride_popup,null);

       //Specify the length and width through constants
        int width=LinearLayout.LayoutParams.MATCH_PARENT;
        int height=LinearLayout.LayoutParams.MATCH_PARENT;

        //Lets tap outside the popup also dismiss it
        Boolean focusable = true;

        //Create a window with our parameters
        PopupWindow popupWindow = new PopupWindow(RidepopUp , width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements
        TextView TV_date=RidepopUp.findViewById(R.id.TV_Date);
        TextView TV_price=RidepopUp.findViewById(R.id.TV_total);
        TextView TV_point=RidepopUp.findViewById(R.id.TV_point);
        TextView TV_transport=RidepopUp.findViewById(R.id.TV_input_tranport);
        TextView TV_rate=RidepopUp.findViewById(R.id.TV_input_rate);
        TextView TV_duration=RidepopUp.findViewById(R.id.TV_input_duration);
        TextView TV_distance=RidepopUp.findViewById(R.id.TV_input_dist);
        TextView TV_start=RidepopUp.findViewById(R.id.TV_input_start);
        TextView TV_end=RidepopUp.findViewById(R.id.TV_input_end);

        //Set text
        TV_date.setText(rideData.getTimeDate());
        TV_price.setText(String.format("RM %.2f",rideData.getPrice()));
        TV_point.setText(String.format("%d pts",rideData.getPoint()));
        TV_transport.setText(rideData.getTransport());
        TV_rate.setText(String.format("RM %.2f /min",rideData.getRate()));
        TV_duration.setText(String.format("%.2f min",rideData.getDuration()));
        TV_distance.setText(String.format("%.2f km",rideData.getDistance()));
        TV_start.setText(rideData.getStart());
        TV_end.setText(rideData.getEnd());

        //Initialize button
        Button BtnOk=RidepopUp.findViewById(R.id.Btn_Exit_receipt);
        BtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Close the window when clicked
                popupWindow.dismiss();
            }
        });


        //Handler for clicking on the inactive zone of the window

        RidepopUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }

        });

        // Set the dismiss listener
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // Notify the listener when the popup is dismissed and check if there is a listener
                if (dismissListener != null) {
                    dismissListener.onPopupDismissed();

                }
            }
        });
    }

    //Ride PopUpWindow display method
    public void showTransactionHistoryPopupWindow(View view)
    {
        //Create a View object through inflater
        LayoutInflater inflater = (LayoutInflater)view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View TransactionpopUp = inflater.inflate(R.layout.transaction_popup,null);

        //Specify the length and width through constants
        int width=LinearLayout.LayoutParams.MATCH_PARENT;
        int height=LinearLayout.LayoutParams.MATCH_PARENT;

        //Lets tap outside the popup also dismiss it
        Boolean focusable = true;

        //Create a window with our parameters
        PopupWindow popupWindow = new PopupWindow(TransactionpopUp , width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements
        TextView TV_date=TransactionpopUp.findViewById(R.id.TV_HDate);
        TextView TV_price=TransactionpopUp.findViewById(R.id.TV_HTotal);
        TextView TV_ID=TransactionpopUp.findViewById(R.id.TV_HID);
        TextView TV_description=TransactionpopUp.findViewById(R.id.TV_HDescription);
        TextView TV_type=TransactionpopUp.findViewById(R.id.TV_HType);

        //Set text
        data=object.getData();
        TV_date.setText(data.getDateTime());
        TV_price.setText(data.getAmount());
        TV_ID.setText(object.getTransaction_id());
        TV_description.setText(data.getDescription());
        TV_type.setText(data.getType());

        //Initialize button
        Button BtnOk=TransactionpopUp.findViewById(R.id.Btn_HOk);
        BtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Close the window when clicked
                popupWindow.dismiss();
            }
        });


        //Handler for clicking on the inactive zone of the window

        TransactionpopUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }

        });

    }}

//PopUp view
//https://medium.com/@evanbishop/popupwindow-in-android-tutorial-6e5a18f49cc7

//OndismissListener
//https://www.tabnine.com/code/java/methods/android.widget.PopupWindow/setOnDismissListener
//https://developer.android.com/reference/android/widget/PopupWindow.OnDismissListener
