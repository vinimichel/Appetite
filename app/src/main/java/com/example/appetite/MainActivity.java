package com.example.appetite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.example.appetite.adapter.NearbyViewAdapter;
import com.example.appetite.dataModels.NearbyRestaurants;
import com.google.android.material.navigation.NavigationBarView;

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
        // Platzhalterdaten hinzufügen
        restaurantsDataList.add(new NearbyRestaurants("Goldene Gans", "Fulda", 9.2, R.drawable.placeholder_img1));
        restaurantsDataList.add(new NearbyRestaurants("Bennos Bierbude", "Gersfeld", 9.2, R.drawable.placeholder_img2));
        restaurantsDataList.add(new NearbyRestaurants("Helgas Hexenhütte", "Fulda", 9.2, R.drawable.placeholder_img3));
        setRecyclerView(restaurantsDataList);
        setBottomNavigationItem();

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

    private void setBottomNavigationItem() {
        NavigationBarView bottomNavigationView = (NavigationBarView)findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.user:
                        return true;
                    case R.id.find_restaurant:
                        startActivity(new Intent(getApplicationContext(), MapActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.settings_tab:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        return true;
                }
                return false;
            }
        });
    }

    private void setRecyclerView(List<NearbyRestaurants> nearbyRecyclerList) {
        // Initialisierung des recycler der Daten über Restaurants in der nähe bereitstellt
        nearbyRecycler = findViewById(R.id.nearby_recycler);
        // Recycler soll horizontal angeordnet sein
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        // layout Manager zuweisen
        nearbyRecycler.setLayoutManager(layoutManager);
        // Durch Adapter werden Restaurant Daten an Views gebunden
        nearbyAdapter = new NearbyViewAdapter(this, nearbyRecyclerList);
        // Recycler ruft Methoden des Adapters auf
        nearbyRecycler.setAdapter(nearbyAdapter);

    }
}