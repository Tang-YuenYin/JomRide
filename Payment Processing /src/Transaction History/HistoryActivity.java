package com.example.jomride;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;
import static androidx.navigation.Navigation.findNavController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HistoryActivity extends AppCompatActivity {
    //call authentictaion
    private FirebaseAuth mAuth;

    //call database
    private FirebaseDatabase database;
    private String userId="2";
    private ArrayList<HistoryObject>transactionlist;
    private ArrayList<HistoryObject>filteredlist;
    private TransactionHistoryAdapter adapter;
    private TransactionHistoryAdapter Fadapter;
    private HistoryObject obj;
    private TextView pickDate;
    private Button Btncalender,Btnfilter;
    private RecyclerView recyclerView;
    private Calendar cal;
    private Context context;

    private Date datepicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //Get authentication+current id
        //   mAuth=FirebaseAuth.getInstance();
        //   userId=mAuth.getCurrentUser().getUid();

        //Initialize database
        database=FirebaseDatabase.getInstance();
        transactionlist=new ArrayList<>();
        filteredlist=new ArrayList<>();
        
        //get id for views
        context=this;
        Btncalender=findViewById(R.id.BtnCalender);
        Btnfilter=findViewById(R.id.Btn_filter);
        pickDate=findViewById(R.id.TV_pickDate);

        //Get List
        recyclerView =findViewById(R.id.RV_transactionhistory);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter=new TransactionHistoryAdapter(transactionlist,HistoryActivity.this);
        recyclerView.setAdapter(adapter);
        getUserHistoryIds();
        adapter.notifyDataSetChanged();

        Button BtnBackHistory=findViewById(R.id.BtnBack_History);
        BtnBackHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBackHome=new Intent(HistoryActivity.this,WalletActivity.class);
                startActivity(intentBackHome);
                finish();
            }
        });
        Btncalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance(); // used for initializing the date picker

                DatePickerDialog datePickerDialog = new DatePickerDialog(context
                        ,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                // get user's selected year, month, and day here
                                cal.set(year,month,day);
                                SimpleDateFormat sdf=new SimpleDateFormat("MMM dd, yyyy ");
                                pickDate.setText(sdf.format(cal.getTime()));
                                datepicker=cal.getTime();

                                String targetDate=pickDate.getText().toString();

                                if(targetDate.isEmpty())
                                    Btnfilter.setEnabled(false);
                                else
                                    Btnfilter.setEnabled(true);
                            }
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                );

                datePickerDialog.show();
            }
        });

        Btnfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filteredlist.clear();
                Fadapter=new TransactionHistoryAdapter(filteredlist,HistoryActivity.this);
                recyclerView.setAdapter(Fadapter);
                filterData(pickDate.getText().toString());
                Fadapter.notifyDataSetChanged();
            }
        });
    }

    private void getUserHistoryIds() {
        DatabaseReference userHistoryDb=database.getReference().child("User").child(userId).child("transaction");
        userHistoryDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for (DataSnapshot history : snapshot.getChildren()){
                        transactionlist=fetchChildInformation(history.getKey());
                        Log.w(TAG, history.getKey());
                    }
                    }

                }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });

    }

    private ArrayList<HistoryObject> fetchChildInformation(String tkey)
    {
        DatabaseReference historyDb=database.getReference().child("History").child("transaction").child(tkey);
        historyDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    HistoryData dt=snapshot.getValue(HistoryData.class);
                    if (dt != null) {
                        obj = new HistoryObject(tkey, dt);
                        transactionlist.add(obj);
                        Log.w(TAG, dt.getDescription());
                        //adapter.addItem(obj);
                        adapter.notifyDataSetChanged();
                    }else {
                        Log.w(TAG, "HistoryData is null");
                    }
                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
        return transactionlist;
    }

    private void filterData(String date)
    {
        for(HistoryObject obj:transactionlist)
        {
            HistoryData dt=obj.getData();
            String[]dateTime=dt.getDateTime().split("-");
            if(dateTime[0].equals(date))
            {
                filteredlist.add(obj);
                Fadapter.notifyDataSetChanged();
            }


        }
    }

    //        //Set Navigation
//        NavHostFragment host= (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.NHFMain);
//        navController = host.getNavController();
//        //setup navigation bar
//        setupBottomNavMenu(navController);
//    //link selected option
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        try{
//
//            findNavController(this,R.id.NHFMain).navigate(item.getItemId());
//            return true;
//        }
//        catch(Exception e)
//        {
//            return super.onOptionsItemSelected(item);
//        }
//    }
//
//    private void setupBottomNavMenu(NavController navController)
//    {
//        BottomNavigationView bottomNav= findViewById(R.id.bottomNavigationView);
//        NavigationUI.setupWithNavController(bottomNav,navController);
//        bottomNav.setOnItemSelectedListener(this::onOptionsItemSelected);
//
//    }
}
