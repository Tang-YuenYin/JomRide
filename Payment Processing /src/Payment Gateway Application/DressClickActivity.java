package com.example.jomride;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


public class DressClickActivity extends AppCompatActivity {

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

    //for wallet
    private DatabaseReference walletReference;

    //for wallethistory
    private DatabaseReference historyreference;

    private String userId="2";

    private String item;

    private String total;

    private HistoryData data;
    private String Publishablekey="pk_test_51OIUjYL3FJxKGQiObTXCvka3SMOwPHbnyYcoGg9ATr0W4rx0KXcgygNrwmmAWKj7sxY0ZAxB6uqvmdX8gj70UZIs0050vVLyUs";
    private String secretkey="sk_test_51OIUjYL3FJxKGQiOH1282OddiIKRFCoz458rlDHIpnYrUbMYIx5zuhR1TJKjxa9EXgGusnZxGs4GIZ72nx4A3Kkp00MYkgTGEG";
    private String customerId;
    private String EphericalKey;
    private String clientSecret;
    PaymentSheet paymentSheet;
    PaymentSheet.CustomerConfiguration customerConfig;
    String TPamount="1000";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dress_click);

        //Get authentication+current id
           mAuth=FirebaseAuth.getInstance();
           userId=mAuth.getCurrentUser().getUid();

        //Initialize database
        database=FirebaseDatabase.getInstance();

        data=(HistoryData) getIntent().getSerializableExtra("iteminfo");

        item=data.getDescription();

        PaymentConfiguration.init(this, Publishablekey);
        paymentSheet = new PaymentSheet(this, result -> {
            onPaymentResult(result);
        });


        Button BtnBuyNow=findViewById(R.id.Btn_buynow);
        BtnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total=data.getAmount();
            TPamount=String.format("%d",(int)(Double.parseDouble(total)*100));
                getCustomerId();
                PaymentFlow();
                addUsageData(total);
            }
        });
    }
    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log.d(TAG, "Canceled");
            Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Log.e(TAG, "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Display for example, an order confirmation screen
            Log.d(TAG, "Completed");
            //Can add pop up here 
            Toast.makeText(this,"Payment Completed",Toast.LENGTH_SHORT).show();
            addUsageData(total);
        }
    }

    private void getCustomerId()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj=new JSONObject(response);
                            customerId=obj.getString("id");
                              //Toast.makeText(DressClickActivity.this,customerId, Toast.LENGTH_SHORT).show();
                            getEphericlKey(customerId);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + secretkey);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getEphericlKey(String customerId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj=new JSONObject(response);
                            EphericalKey=obj.getString("id");
                             // Toast.makeText(DressClickActivity.this,EphericalKey, Toast.LENGTH_SHORT).show();
                            getClientSecret(customerId,EphericalKey);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + secretkey);
                header.put("Stripe-Version","2023-10-16");
                return header;
            }
            @Override
            public Map<String,String>getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("customer",customerId);

                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void getClientSecret(String customerId, String ephericalKey) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents?amount="+TPamount,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj=new JSONObject(response);
                            clientSecret=obj.getString("client_secret");
                           //  Toast.makeText(DressClickActivity.this,clientSecret, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + secretkey);
                return header;
            }
            @Override
            public Map<String,String>getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("customer",customerId);
                params.put("currency","myr");
                params.put("automatic_payment_methods[enabled]","true");
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void PaymentFlow() {
        if(customerId!=null&&EphericalKey!=null&&clientSecret!=null){
            customerConfig=new PaymentSheet.CustomerConfiguration(customerId,EphericalKey);
            PaymentSheet.Configuration configuration = new PaymentSheet.Configuration("JomRide");
            configuration.setCustomer(customerConfig);
            paymentSheet.presentWithPaymentIntent(clientSecret,
                    configuration);}
        else
        {
            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
        }
    }


    private void addUsageData(String total)
    {
        historyreference=database.getReference().child("History").child("transaction");
        HuserReference=database.getReference().child("User").child(userId).child("transaction");
        //Usage data
        String type="Ride";
        String utotal=String.format("RM %s",total);
        HistoryData wud=new HistoryData(setDateTime(),item,"Shop",utotal);
        //get id
        String usageHistory_id= historyreference.push().getKey();
        HuserReference.child(usageHistory_id).setValue(true);
        historyreference.child(usageHistory_id).setValue(wud);
    }

    private String setDateTime()
    {
        LocalDateTime dateTime= LocalDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, YYYY - hh:mm a");
        String dateTimeStr = formatter.format(dateTime);
        return dateTimeStr;
    }
}
