package com.example.appetite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ReservationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        Intent i = getIntent();
    }

    public void launchFoodMenu(View v) {
        Intent i = new Intent(this, FoodMenuActivity.class);
        startActivity(i);
    }
}