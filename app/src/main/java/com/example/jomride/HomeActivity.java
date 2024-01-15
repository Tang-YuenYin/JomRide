package com.example.jomride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private DatabaseReference memberRef;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String userId;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();

        if (currentUser != null) {
            // Assuming you have a "users" node in your database
            databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userId);
        }

        //Button to bring the user to next page to select the pick up point
        Button WhereToGo = findViewById(R.id.WhereToGo);
        WhereToGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,LocationActivity.class);
                startActivity(intent);
            }
        });

        //Bottom Navigation Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.DestHome) { //Home Page
                    return true;
                } else if (item.getItemId() == R.id.DestShop) {  //Shop Page
                    startActivity(new Intent(HomeActivity.this, ShopActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.DestWallet) {  //Wallet Page
                    startActivity(new Intent(HomeActivity.this, WalletActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.DestOffer) {  //Offer Page
                    startActivity(new Intent(HomeActivity.this, RewardMainPage.class));
                    return true;
                } else if (item.getItemId() == R.id.DestProfile) {  //Profile Page
                    startActivity(new Intent(HomeActivity.this, profilepage.class));
                    return true;
                }
                return false;
            }
        });

        //Side Menu
        drawerLayout = findViewById(R.id.drawer_layout);
        // Menu button to open the drawer
        ImageButton menuButton = findViewById(R.id.menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
    }

    //call openDrawer method
    private void openDrawer() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
            NavigationView sideNav = findViewById(R.id.sideNav);
            sideNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.DestHome) {  //Home Page
                        return true;
                    } else if (item.getItemId() == R.id.DestShop) {  //Shop Page
                        // Handle Shop click
                        startActivity(new Intent(HomeActivity.this, ShopActivity.class));
                        return true;
                    } else if (item.getItemId() == R.id.DestWallet) {  //Wallet Page
                        startActivity(new Intent(HomeActivity.this, WalletActivity.class));
                        return true;
                    } else if (item.getItemId() == R.id.DestOffer) {  //Offer Page
                        startActivity(new Intent(HomeActivity.this, RewardMainPage.class));
                        return true;
                    } else if (item.getItemId() == R.id.DestProfile) {  //Profile Page
                        startActivity(new Intent(HomeActivity.this, profilepage.class));
                        return true;
                    }
                    return false;
                }
            });
        } else {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;

        // Add a marker and move the camera
        LatLng latLng = new LatLng(3.1282, 101.6507);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                .title("My Current Location");
        myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        myMap.addMarker(markerOptions);
    }

    private void checkMemberStatus(){
        memberRef = databaseReference.child("membership");
        memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isMember = snapshot.child("isMember").getValue(Boolean.class);
                if(isMember){
                    Intent intent = new Intent(HomeActivity.this,YourInformation.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(HomeActivity.this,SignUpMembership.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}