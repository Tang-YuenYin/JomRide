//Top up Balance
package com.example.jomride;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.EphemeralKey;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.model.Customer;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.EphemeralKeyCreateParams;
import com.stripe.param.PaymentIntentCreateParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//https://firebase.google.com/docs/database/android/read-and-write#java

//Stripe integration
//https://www.youtube.com/watch?v=-1dX7mEV80M
//https://www.youtube.com/watch?v=O4T025cEmjg
//https://stripe.com/docs/payments/accept-a-payment?platform=android&ui=payment-sheet#android-customization
public class TopUpActivity extends AppCompatActivity {

    private EditText ETamount;
    private double amount;
    private FirebaseAuth mAuth;

    //call database
    private FirebaseDatabase database;

    private DatabaseReference wallethistoryRef;
    DatabaseReference walletRef;
    private String userId="2";

    private WalletData walletdata;

    private DatabaseReference HuserReference;
    private DatabaseReference historyreference;

    //Payment gateway data
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
        setContentView(R.layout.activity_top_up);
        readWalletData();
        //Get for altering balance
        //Get authentication+current id
           mAuth=FirebaseAuth.getInstance();
           userId=mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();

        //input amount to top up
        ETamount=findViewById(R.id.ETND_amount);
        Button Btntopup=findViewById(R.id.BtnTopUp);
        Button BtnComfirm=findViewById(R.id.Btn_Comfirm);
        Btntopup.setEnabled(false);;

        PaymentConfiguration.init(this, Publishablekey);
        paymentSheet = new PaymentSheet(this, result -> {
            onPaymentResult(result);
        });

          ETamount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ETamount.getText().clear();
                Btntopup.setEnabled(false);;
            }
        });

        BtnComfirm.setOnClickListener(new View.OnClickListener() {

                                          @Override
                                          public void onClick(View v) {
                                              if (TextUtils.isEmpty(ETamount.getText().toString()))
                                                  Toast.makeText(TopUpActivity.this, " Please enter a value", Toast.LENGTH_SHORT).show();
                                              else {
                                                  String rawamount = ETamount.getText().toString();
                                                  amount = Double.parseDouble(rawamount);
                                                  int Iamount=(int)amount*100;
                                                  TPamount = String.format("%d", Iamount );
                                                  getCustomerId();
                                                  Btntopup.setEnabled(true);


                                              }
                                          }
                                      }
                );
       // getCustomerId();
        Btntopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currentCredit=Double.parseDouble(walletdata.getBalance());
                double limit=Math.max(10.0,20.0-currentCredit);
                if(amount>=limit)
                {
                    PaymentFlow();
                    String tbalance=String.format("%.2f",getTotalBalance(amount));
                    walletdata=new WalletData(tbalance,walletdata.getPoint());
                    walletRef.setValue(walletdata);
                    addUsageData(amount);
                }
                else{
                    ETamount.getText().clear();
                    Toast toast = Toast.makeText(TopUpActivity.this ," Only can top up value more than \nRM "+limit, Toast.LENGTH_SHORT);
                    toast.show();
                }}
        });



        //go back to wallet home
        Button BtnBackHome=findViewById(R.id.BtnBack_WalletTopUpHome);
        BtnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBackHome=new Intent(TopUpActivity.this,WalletActivity.class);
                startActivity(intentBackHome);
                finish();
            }
        });
    }

    //Display the payment result
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
            Toast.makeText(this,"Completed",Toast.LENGTH_SHORT).show();
        }
    }

    //Get customer id
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
                          //  Toast.makeText(TopUpActivity.this,customerId, Toast.LENGTH_SHORT).show();
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

    //Get epherical key
    private void getEphericlKey(String customerId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj=new JSONObject(response);
                            EphericalKey=obj.getString("id");
                        //    Toast.makeText(TopUpActivity.this,EphericalKey, Toast.LENGTH_SHORT).show();
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

    //get client secret
    private void getClientSecret(String customerId, String ephericalKey) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents?amount="+TPamount,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj=new JSONObject(response);
                            clientSecret=obj.getString("client_secret");
                           // Toast.makeText(TopUpActivity.this,clientSecret, Toast.LENGTH_SHORT).show();

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

    //Call payment intent
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

    //add up to balance
    private double getTotalBalance(double amount)
    {
        return Double.parseDouble(walletdata.getBalance())+amount;
    }
//read the wallet data
    private void readWalletData()
    {
        //Get authentication+current id
           mAuth=FirebaseAuth.getInstance();
           userId=mAuth.getCurrentUser().getUid()
        database= FirebaseDatabase.getInstance();
        walletRef=database.getReference().child("Wallet").child(userId);
        walletRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                     @Override
                                                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                         walletdata = dataSnapshot.getValue(WalletData.class);

                                                     }

                                                     @Override
                                                     public void onCancelled(@NonNull DatabaseError error) {
                                                         Log.w(TAG, "loadPost:onCancelled", error.toException());
                                                     }
                                                 });
    }

    //Transaction History
    private void addUsageData(double amount)
    {
        historyreference=database.getReference().child("History").child("transaction");
        HuserReference=database.getReference().child("User").child(userId).child("transaction");
        //Usage data
        String type="Top Up";
        String utotal=String.format("+ RM %.2f",amount);
        HistoryData wud=new HistoryData(setDateTime(),type,"wallet",utotal);
        //get id
        String history_id= historyreference.push().getKey();
        HuserReference.child(history_id).setValue(true);
        historyreference.child(history_id).setValue(wud);
    }

    private String setDateTime()
    {
        LocalDateTime dateTime= LocalDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, YYYY - hh:mm a");
        String dateTimeStr = formatter.format(dateTime);
        return dateTimeStr;
    }

}
