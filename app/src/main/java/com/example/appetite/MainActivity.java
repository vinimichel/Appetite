package com.example.appetite;

import androidx.annotation.NonNull;
import static android.content.ContentValues.TAG;
import static com.mapbox.turf.TurfConstants.UNIT_KILOMETERS;
import static java.lang.Math.round;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import android.os.Bundle;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import com.example.appetite.adapter.NearbyViewAdapter;
import com.example.appetite.dataModels.NearbyRestaurants;
import com.google.android.material.navigation.NavigationBarView;
import com.mapbox.api.tilequery.MapboxTilequery;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.turf.TurfMeasurement;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView nearbyRecycler;
    NearbyViewAdapter nearbyAdapter;
    private final static String MAPBOX_TOKEN = "pk.eyJ1IjoidmluaW1pY2hlbCIsImEiOiJjbGFqdWNvYmkwZmZhM3JuMzIxYzl2Z3h4In0.bmACrWyEcA6772uD758XPw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Home");
        initRecyclerView();
        // Platzhalter Startposition
        Point testPoint = Point.fromLngLat(9.685242, 50.550657);
        // tilequery bauen
        buildTilequeryRequest(testPoint);

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


    private void initRecyclerView() {
        nearbyRecycler = findViewById(R.id.nearby_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        nearbyRecycler.setLayoutManager(layoutManager);
    }

    private void setRecyclerViewData(List<Feature> features, Point deviceLocationPoint) {
        // Nearby Restaurant Daten Felder initialisieren
        List<NearbyRestaurants> restaurantsDataList = new ArrayList<>();
        // Platzhalterdaten
        for (Feature feature : features) {
            Point restaurantLngLat = (Point)feature.geometry();
            double distanceBetweenDeviceAndTarget = TurfMeasurement.distance(deviceLocationPoint,
                    Point.fromLngLat(restaurantLngLat.longitude(), restaurantLngLat.latitude()), UNIT_KILOMETERS);
            restaurantsDataList.add(new NearbyRestaurants(feature.getProperty("title").getAsString(), "Fulda", round(distanceBetweenDeviceAndTarget*100.0)/100.0, R.drawable.placeholder_img1));
        }
        nearbyAdapter = new NearbyViewAdapter(this, restaurantsDataList);
        // Adapter auf die Daten setzen
        nearbyRecycler.setAdapter(nearbyAdapter);
    }

    private void buildTilequeryRequest(Point position) {
        // tilequery mit tilequery api bauen
        MapboxTilequery tilequery = MapboxTilequery.builder()
                // Zugangstoken
                .accessToken(MAPBOX_TOKEN)
                // Id des Restaurant-Tileset
                .tilesetIds("vinimichel.clap8mndw0p0m27qlcnqkd1c6-3kiyv")
                // Punkt von dem Abgefragt werden soll
                .query(Point.fromLngLat(position.longitude(), position.latitude()))
                // Wie groß darf der Radius der Restaurants maximal sein
                .radius(10000)
                // wie viele Ergebnisse/Restaurants sollen angezeigt werden
                .limit(10)
                .build();
        // Callback bei eintreffen der Ergebnisse festlegen
        tilequery.enqueueCall(new Callback<FeatureCollection>() {
            @Override
            public void onResponse(Call<FeatureCollection> call, Response<FeatureCollection> response) {
                if (response.body() != null) {
                    // wenn Ergebnisse gefunden setzen wir FeatureCollection
                    FeatureCollection responseFeatureCollection = response.body();
                    List<Feature> features  = responseFeatureCollection.features();
                    // recyclerView mit Daten logen
                    setRecyclerViewData(features, position);
                    Log.d(TAG, "Tilequering " + features.toString());
                }
            }
            @Override
            public void onFailure(Call<FeatureCollection> call, Throwable throwable) {
                Log.d(TAG, " Tilequering: Could not retrieve data from api");
            }
        });
    }
}