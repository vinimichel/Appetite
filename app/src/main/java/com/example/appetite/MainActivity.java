package com.example.appetite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.appetite.adapter.NearbyViewAdapter;
import com.example.appetite.dataModels.NearbyRestaurants;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView nearbyRecycler;
    NearbyViewAdapter nearbyAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Home");
        List<NearbyRestaurants> restaurantsDataList = new ArrayList<>();
        // Platzhalterdaten
        restaurantsDataList.add(new NearbyRestaurants("Goldene Gans", "Fulda", 9.2, R.drawable.placeholder_img1));
        restaurantsDataList.add(new NearbyRestaurants("Bennos Bierbude", "Gersfeld", 9.2, R.drawable.placeholder_img2));
        restaurantsDataList.add(new NearbyRestaurants("Helgas Hexenhütte", "Fulda", 9.2, R.drawable.placeholder_img3));
        setRecyclerView(restaurantsDataList);
    }

    public void launchSettings(View v) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }
    public void launchRegister(View v) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    public void launchMap(View v) {
        Intent i = new Intent(this, MapActivity.class);
        startActivity(i);
    }

    private void setRecyclerView(List<NearbyRestaurants> nearbyRecyclerList) {
        nearbyRecycler = findViewById(R.id.nearby_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        nearbyRecycler.setLayoutManager(layoutManager);
        nearbyAdapter = new NearbyViewAdapter(this, nearbyRecyclerList);
        nearbyRecycler.setAdapter(nearbyAdapter);

    }
}