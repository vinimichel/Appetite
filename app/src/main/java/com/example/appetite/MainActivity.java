package com.example.appetite;

import static android.content.ContentValues.TAG;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appetite.adapter.NearbyViewAdapter;
import com.example.appetite.dataModels.NearbyRestaurants;
import com.google.android.material.navigation.NavigationBarView;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.tilequery.MapboxTilequery;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView nearbyRecycler;
    NearbyViewAdapter nearbyAdapter;
    private final static String MAPBOX_TOKEN = "pk.eyJ1IjoidmluaW1pY2hlbCIsImEiOiJjbGFqdWNvYmkwZmZhM3JuMzIxYzl2Z3h4In0.bmACrWyEcA6772uD758XPw";
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Home");
        initRecyclerView();
        // placeholder starting position
        Point testPoint = Point.fromLngLat(9.685242, 50.550657);
        buildTilequeryRequest(testPoint);
        setBottomNavigationItem();
        initSearchFab();
    }

    private void setBottomNavigationItem() {
        NavigationBarView bottomNavigationView = (NavigationBarView)findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        return true;
                    case R.id.find_restaurant:
                        startActivity(new Intent(getApplicationContext(), MapActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.reservations_tab:
                        startActivity(new Intent(getApplicationContext(), ReservationActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.settings_tab:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    private void setRecyclerView(List<NearbyRestaurants> nearbyRecyclerList) {
        // initialize recycler providing data about nearby restaurants
        nearbyRecycler = findViewById(R.id.nearby_recycler);
        // set recyclerView to horizontal
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        nearbyRecycler.setLayoutManager(layoutManager);
        // bind restaurant data to views using adapter
        nearbyAdapter = new NearbyViewAdapter(this, nearbyRecyclerList);
        // call adapter methods using recycler
        nearbyRecycler.setAdapter(nearbyAdapter);
    }

    private void initRecyclerView() {
        nearbyRecycler = findViewById(R.id.nearby_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        nearbyRecycler.setLayoutManager(layoutManager);
    }

    private void setRecyclerViewData(List<Feature> features, Point deviceLocation) {
        // initialize ArrayList of nearby restaurants
        List<NearbyRestaurants> restaurantsDataList = new ArrayList<>();
        for (Feature feature : features) {
            restaurantsDataList.add(new NearbyRestaurants(feature, deviceLocation));
        }
        nearbyAdapter = new NearbyViewAdapter(this, restaurantsDataList);
        // set adapter with restaurant data to recycler
        nearbyRecycler.setAdapter(nearbyAdapter);
    }

    private void buildTilequeryRequest(Point position) {
        // build tile query with tile query API
        MapboxTilequery tilequery = MapboxTilequery.builder()
            .accessToken(MAPBOX_TOKEN)
            .tilesetIds("vinimichel.clcevvkko01bw23qh5gmszly4-9cpxg")
            // point of query
            .query(Point.fromLngLat(position.longitude(), position.latitude()))
            // MAX radius of restaurants
            .radius(10000)
            // how many results/restaurants are shown
            .limit(10)
            .build();
        // callback on results
        tilequery.enqueueCall(new Callback<FeatureCollection>() {
            @Override
            public void onResponse(Call<FeatureCollection> call, Response<FeatureCollection> response) {
                if (response.body() != null) {
                    // if results found, set FeatureCollection
                    FeatureCollection responseFeatureCollection = response.body();
                    List<Feature> features  = responseFeatureCollection.features();
                    // log recyclerView with data
                    setRecyclerViewData(features, position);
                    Log.d(TAG, "Tilequering " + features);
                }
            }

            @Override
            public void onFailure(Call<FeatureCollection> call, Throwable throwable) {
                Log.d(TAG, "Tilequering: Could not retrieve data from API");
            }
        });
    }

    // activate onClickListener for search
    private void initSearchFab() {
        // configuration of mapbox token
        Mapbox.getInstance(this, MAPBOX_TOKEN);
        findViewById(R.id.restaurant_search_field).setOnClickListener(new View.OnClickListener() {
            // open new search activity when button pressed
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : MAPBOX_TOKEN)
                    .placeOptions(PlaceOptions.builder()
                    .backgroundColor(Color.parseColor("#FFFFFF"))
                    .limit(10)
                    .build(PlaceOptions.MODE_CARDS))
                    .build(MainActivity.this);
                // open activity of which you want a result to be handled with onActivityResult
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            // select CarmenFeature of chosen location
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
        }
    }

}
