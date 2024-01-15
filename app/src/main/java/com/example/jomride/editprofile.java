package com.example.jomride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Base64;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class editprofile extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText profileFullName,profileEmail,profilePhone;
    ImageView profileImageView;
    TextView changeImage;
    Button saveBtn;
    String editName, editPhone, editEmail;
    DatabaseReference databaseReference;
    private Uri imageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        Intent data = getIntent();
        editName = data.getStringExtra("name");
        editEmail = data.getStringExtra("email");
        editPhone = data.getStringExtra("phone");

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        profileFullName = findViewById(R.id.profileFullName);
        profileEmail = findViewById(R.id.profileEmailAddress);
        profilePhone = findViewById(R.id.profilePhoneNo);
        profileImageView = findViewById(R.id.profileImageView);
        changeImage = findViewById(R.id.changeimage);
        saveBtn = findViewById(R.id.saveProfileInfo);

        showData();

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
                Intent intent = new Intent(editprofile.this, profilepage.class);
                startActivity(intent);
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                // Convert the selected image to a Bitmap
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Set the Bitmap to profileImageView
                profileImageView.setImageBitmap(bitmap);

                // Convert the Bitmap to a Base64 string
                String imageString = encodeImage(bitmap);

                // Upload the image to Firebase Storage
                uploadImage(imageString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void uploadImage(String imageString) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("profileImages")
                    .child(userId + ".jpeg");

            storageReference.putBytes(Base64.decode(imageString, Base64.DEFAULT))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL of the uploaded image
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();

                                    // Include the download URL in the intent
                                    Intent intent = new Intent(editprofile.this, profilepage.class);
                                    intent.putExtra("downloadUrl", downloadUrl);

                                    // Start the profilepage activity
                                    startActivity(intent);
                                }
                            });

                            Toast.makeText(editprofile.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(editprofile.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userReference = databaseReference.child(userId);

            if (isNameChanged()) {
                userReference.child("name").setValue(profileFullName.getText().toString());
            }

            if (isPhoneChanged()) {
                userReference.child("phone").setValue(profilePhone.getText().toString());
            }

            if (isEmailChanged()) {
                userReference.child("email").setValue(profileEmail.getText().toString());
            }

            Toast.makeText(editprofile.this, "Profile updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(editprofile.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNameChanged(){
        if(!editName.equals(profileFullName.getText().toString())){
            databaseReference.child("users").child("name").setValue(profileFullName.getText().toString());
            editName = profileFullName.getText().toString();
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isPhoneChanged(){
        if(!editPhone.equals(profilePhone.getText().toString())){
            databaseReference.child("users").child("phone").setValue(profilePhone.getText().toString());
            editPhone = profilePhone.getText().toString();
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isEmailChanged(){
        if(!editEmail.equals(profileEmail.getText().toString())){
            databaseReference.child("users").child("email").setValue(profileEmail.getText().toString());
            editEmail = profileEmail.getText().toString();
            return true;
        }
        else{
            return false;
        }
    }

    public void showData(){
        Intent intent = getIntent();

        editName = intent.getStringExtra("name");
        editPhone = intent.getStringExtra("phone");
        editEmail = intent.getStringExtra("email");

        profileFullName.setText(editName);
        profilePhone.setText(editPhone);
        profileEmail.setText(editEmail);
    }

    public void Back(View view){
        startNewActivity(HomeActivity.class);
    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(editprofile.this, cls);
        startActivity(intent);
    }
}