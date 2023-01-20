package com.example.appetite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;

public class MyRestaurant extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_restaurant);
        setBottomNavigationItem();
    }

    private void setBottomNavigationItem() {
        NavigationBarView bottomNavigationView = (NavigationBarView) findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.my_restaurant);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeBtn:
                        startActivity(new Intent(getApplicationContext(), MainOwner.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.my_restaurant:
                        return true;
                    case R.id.tables:
                        //startActivity(new Intent(getApplicationContext(), ReservationActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.settingsBtn:
                        //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }
}