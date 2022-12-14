package com.example.appetite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;

import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.appetite.adapter.NearbyViewAdapter;
import com.example.appetite.dataModels.NearbyRestaurants;
import com.mapbox.api.tilequery.MapboxTilequery;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView nearbyRecycler;
    NearbyViewAdapter nearbyAdapter;
    private MapboxMap mapboxMap;     // Schnittstelle zur Map
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
        // Initialisierung des recycler der Daten über Restaurants in der nähe bereitstellt
        nearbyRecycler = findViewById(R.id.nearby_recycler);
        // Recycler soll horizontal angeordnet sein
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        // layout Manager zuweisen
        nearbyRecycler.setLayoutManager(layoutManager);
        // Durch Adapter werden Restaurant Daten an Views gebunden
        nearbyAdapter = new NearbyViewAdapter(this, nearbyRecyclerList);
        // Recycler ruft Methoden des Adapters auf
        nearbyRecycler.setAdapter(nearbyAdapter);

    }

    private void buildTilequeryRequest(Point position) {
        MapboxTilequery tilequery = MapboxTilequery.builder()
                //.accessToken(getString(R.string.access_token))
                .tilesetIds("mapbox.mapbox-streets-v7")
                .query(Point.fromLngLat(position.longitude(), position.latitude()))
                .radius(50)
                .limit(10)
                .geometry("polygon")
                .dedupe(true)
                .layers("building")
                .build();
        tilequery.enqueueCall(new Callback<FeatureCollection>() {
            @Override
            public void onResponse(Call<FeatureCollection> call, Response<FeatureCollection> response) {
                if (response.body() != null) {
                    FeatureCollection responseFeatureCollection = response.body();
                    responseFeatureCollection.features();
                }
            }

            @Override
            public void onFailure(Call<FeatureCollection> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, "Could not retrieve data from api", Toast.LENGTH_SHORT).show();
            }
        });
    }
}