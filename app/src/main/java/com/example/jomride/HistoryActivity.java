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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HistoryActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private String userId;
    private ArrayList<HistoryObject> transactionlist;
    private ArrayList<HistoryObject> filteredList;
    private TransactionHistoryAdapter adapter;
    private TransactionHistoryAdapter Fadapter;
    private HistoryObject obj;
    private TextView pickDate;
    private Button Btnfilter;
    private ImageView Btncalender;
    private RecyclerView recyclerView;
    private Calendar cal;
    private Context context;

    private Date datepicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance();
        transactionlist = new ArrayList<>();
        filteredList = new ArrayList<>();

        context = this;
        Btncalender = findViewById(R.id.BtnCalendar);
        Btnfilter = findViewById(R.id.Btn_filter);
        pickDate = findViewById(R.id.TV_pickDate);

        recyclerView = findViewById(R.id.RV_transactionhistory);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TransactionHistoryAdapter(transactionlist, HistoryActivity.this);
        recyclerView.setAdapter(adapter);

        getUserHistoryIds();
        adapter.notifyDataSetChanged();;

        ImageButton BtnBackHistory = findViewById(R.id.BtnBack_History);
        BtnBackHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBackHome = new Intent(HistoryActivity.this, WalletActivity.class);
                startActivity(intentBackHome);
                finish();
            }
        });

        Btncalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                cal.set(year, month, day);
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
                                pickDate.setText(sdf.format(cal.getTime()));
                                datepicker = cal.getTime();

                                String targetDate = pickDate.getText().toString();

                                if (targetDate.isEmpty())
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
                filteredList.clear();
                Fadapter = new TransactionHistoryAdapter(filteredList, HistoryActivity.this);
                recyclerView.setAdapter(Fadapter);
                filterData(pickDate.getText().toString());
                Fadapter.notifyDataSetChanged();
            }
        });
    }

    private void getUserHistoryIds() {
        DatabaseReference userHistoryDb = database.getReference().child("User").child(userId).child("transaction");
        userHistoryDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot history : snapshot.getChildren()) {
                        fetchChildInformation(history.getKey());
                        Log.w(TAG, history.getKey());
                    }
                    Log.d(TAG, "Size of transactionlist after fetching data: " + transactionlist.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }

    private void fetchChildInformation(String tkey) {
        DatabaseReference historyDb = database.getReference().child("History").child("transaction").child(tkey);
        historyDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HistoryData dt = snapshot.getValue(HistoryData.class);
                    if (dt != null) {
                        obj = new HistoryObject(tkey, dt);
                        transactionlist.add(0, obj);
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "Added data to transactionlist. Description: " + dt.getDescription());
                    } else {
                        Log.w(TAG, "HistoryData is null");
                    }
                } else {
                    Log.d(TAG, "Snapshot does not exist for key: " + tkey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }


    private void filterData(String targetDate) {
        if ("Pick Date".equals(targetDate)) {
            // Handle the case where the user hasn't picked a date
            Toast.makeText(this, "Please pick a date", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<HistoryObject> filteredList = new ArrayList<>();
        for (HistoryObject obj : transactionlist) {
            HistoryData dt = obj.getData();
            String dateTime = dt.getDateTime(); // Assuming dateTime is a string
            String[] dateTimeParts = dateTime.split(" - ");
            String datePart = dateTimeParts[0];

            // Convert both dates to the same format for comparison
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
            try {
                Date targetDateFormatted = sdf.parse(targetDate);
                Date datePartFormatted = sdf.parse(datePart);

                Log.d(TAG, "Target Date: " + sdf.format(targetDateFormatted));
                Log.d(TAG, "Date Part: " + sdf.format(datePartFormatted));

                // Compare the formatted dates
                if (datePartFormatted.equals(targetDateFormatted)) {
                    filteredList.add(obj);
                    Fadapter.notifyDataSetChanged();
                }
            } catch (ParseException e) {
                e.printStackTrace();
                // Handle the case where parsing fails (e.g., invalid date format)
                Toast.makeText(this, "Error parsing date", Toast.LENGTH_SHORT).show();
            }
        }
        Fadapter.updateData(filteredList);
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
