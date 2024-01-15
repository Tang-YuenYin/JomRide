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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ClipActivity extends AppCompatActivity {

    private Button BtnBuy;
    private Dialog mDialog;
    private ImageButton Back;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);

        BtnBuy = findViewById(R.id.BtnBuy);
        mDialog = new Dialog(this);
        Back = findViewById(R.id.back);

        BtnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                showPopupDialog();
            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClipActivity.this,ShopActivity.class);
                startActivity(intent);
            }
        });
    }
    private void showPopupDialog(){
        mDialog.setContentView(R.layout.activity_codeclip);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText etCode = mDialog.findViewById(R.id.ETCode);
        Button btnConfirm = mDialog.findViewById(R.id.BtnConfirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCode = etCode.getText().toString().trim();

                if (isValidCode(enteredCode)) {
                    String price = ((TextView)findViewById(R.id.price)).getText().toString();
                    Intent intent = new Intent(ClipActivity.this, PayActivity.class);
                    intent.putExtra("item", "Bicycle Skirt Clip");
                    intent.putExtra("PRICE_EXTRA",price);
                    startActivity(intent);
                    mDialog.dismiss(); // Close the dialog
                } else {
                    Toast.makeText(ClipActivity.this, "Invalid code. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mDialog.show();
    }

    private boolean isValidCode(String code) {
        return code.equals("C101") || code.equals("C102") || code.equals("C103") || code.equals("C104") || code.equals("C105");
    }
}
