package com.example.appetite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationBarView;

public class MyRestaurant extends AppCompatActivity {

    Button editButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_restaurant);
        setBottomNavigationItem();

        TextView[] txt = new TextView[7];

        txt[0] = findViewById(R.id.editRestName);
        txt[1] = findViewById(R.id.editDescription);
        txt[2] = findViewById(R.id.editLongitude);
        txt[3] = findViewById(R.id.editLatitude);
        txt[4] = findViewById(R.id.editCity);
        txt[5] = findViewById(R.id.editAddress);
        txt[6] = findViewById(R.id.editZip);

        editButton = findViewById(R.id.editBtn);
        editButton.setOnClickListener(view -> {
            for (int i = 0; i < txt.length; i++) txt[i].setEnabled(true);
            saveButton.setEnabled(true);
        });

        saveButton = findViewById(R.id.saveBtn);
        saveButton.setOnClickListener(view -> {
            for (int i = 0; i < txt.length; i++) txt[i].setEnabled(false);
            saveButton.setEnabled(false);
            // Save the text to a database or file here
        });

        for (int i = 0; i < txt.length; i++) txt[i].setEnabled(false);
        saveButton.setEnabled(false);

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