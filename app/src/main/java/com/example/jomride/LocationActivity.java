package com.example.jomride;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LocationActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Initializing a Spinner object named DDLDestination
        Spinner DDLDestination = (Spinner) findViewById(R.id.DDLDestination);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.DestinationSlots, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        DDLDestination.setAdapter(adapter);

        //Submit button - jump to TransportActivity
        Button ClickDest = findViewById(R.id.BtnDest);
        ClickDest.setOnClickListener(ButtonDest);

        //Back Button - go back to HomeActivity
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(ButtonBack);
    }

    //Method for Submit Button
    private View.OnClickListener ButtonDest = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Initializing a Spinner object named DDLDestination
            Spinner DDLDestination = findViewById(R.id.DDLDestination);

            // Get the selected item from the spinner
            String selectedDestination = DDLDestination.getSelectedItem().toString();

            if (selectedDestination.equals("None")) {  //If user do not select any pick up point
                Toast.makeText(LocationActivity.this, "Please select a destination.", Toast.LENGTH_SHORT).show();
            } else  {
                // Create an Intent and pass the selected destination to the next activity
                Intent intent = new Intent(LocationActivity.this, TransportActivity.class);
                intent.putExtra("Destination", selectedDestination);
                startActivity(intent);
            }
        }
    };

    //Method for Back Button
    private View.OnClickListener ButtonBack = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(LocationActivity.this,HomeActivity.class);
            startActivity(intent);
        }
    };
}

