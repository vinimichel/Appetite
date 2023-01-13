package com.example.appetite;


import static android.content.ContentValues.TAG;
import static com.mapbox.turf.TurfConstants.UNIT_KILOMETERS;
import static java.lang.Math.round;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.tilequery.TilequeryCriteria;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

import android.graphics.Color;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.appetite.adapter.NearbyViewAdapter;
import com.example.appetite.dataModels.NearbyRestaurants;
import com.mapbox.api.tilequery.MapboxTilequery;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.turf.TurfMeasurement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.material.navigation.NavigationBarView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView nearbyRecycler;
    NearbyViewAdapter nearbyAdapter;
    private final static String MAPBOX_TOKEN = "pk.eyJ1IjoidmluaW1pY2hlbCIsImEiOiJjbGFqdWNvYmkwZmZhM3JuMzIxYzl2Z3h4In0.bmACrWyEcA6772uD758XPw";
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    String cultureCategory;
    Point selectedPosition;
    Button focusedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Home");
        initRecyclerView();
        cultureCategory = "all";
        focusedButton = (Button)findViewById(R.id.button2);
        // Platzhalter Startposition
        Point testPoint = Point.fromLngLat(9.685242, 50.550657);
        // tilequery bauen
        buildTilequeryRequest(testPoint);
        setBottomNavigationItem();
        initSearchFab();
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        // layout Manager zuweisen
        nearbyRecycler.setLayoutManager(layoutManager);
        // Durch Adapter werden Restaurant Daten an Views gebunden
        nearbyAdapter = new NearbyViewAdapter(this, nearbyRecyclerList);
        // Recycler ruft Methoden des Adapters auf
        nearbyRecycler.setAdapter(nearbyAdapter);
    }

    private void initRecyclerView() {
        nearbyRecycler = findViewById(R.id.nearby_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        nearbyRecycler.setLayoutManager(layoutManager);
    }

    private void setRecyclerViewData(List<Feature> features, Point deviceLocation) {
        // Nearby Restaurant Daten Felder initialisieren
        List<NearbyRestaurants> restaurantsDataList = new ArrayList<>();
        // Platzhalterdaten
        for (Feature feature : features) {
            restaurantsDataList.add(new NearbyRestaurants(feature, deviceLocation));
        }
        nearbyAdapter = new NearbyViewAdapter(this, restaurantsDataList);
        // Adapter auf die Daten setzen
        nearbyRecycler.setAdapter(nearbyAdapter);

    }

    private void buildTilequeryRequest(Point position) {
        //SymbolLayer layer = new SymbolLayer("restaurant-features","vinimichel.clcevvkko01bw23qh5gmszly4-9cpxg");
        // tilequery mit tilequery api bauen
        MapboxTilequery tilequery = MapboxTilequery.builder()
                // Zugangstoken
                .accessToken(MAPBOX_TOKEN)
                // Id des Restaurant-Tileset
                .tilesetIds("vinimichel.clcevvkko01bw23qh5gmszly4-9cpxg")
                // Punkt von dem Abgefragt werden soll
                .query(Point.fromLngLat(position.longitude(), position.latitude()))
                // Wie groß darf der Radius der Restaurants maximal sein
                .radius(10000)
                // wie viele Ergebnisse/Restaurants sollen angezeigt werden
                .limit(50)
                .build();
        // Callback bei eintreffen der Ergebnisse festlegen
        tilequery.enqueueCall(new Callback<FeatureCollection>() {
            @Override
            public void onResponse(Call<FeatureCollection> call, Response<FeatureCollection> response) {
                if (response.body() != null) {
                    // wenn Ergebnisse gefunden setzen wir FeatureCollection
                    FeatureCollection responseFeatureCollection = response.body();
                    processTilequeryResults(responseFeatureCollection.features(), position);

                }
            }
            @Override
            public void onFailure(Call<FeatureCollection> call, Throwable throwable) {
                Log.d(TAG, " Tilequering: Could not retrieve data from api");
            }
        });
    }

    private void processTilequeryResults(List<Feature> features, Point position) {
        if (cultureCategory.equals("all")) {
            setRecyclerViewData(features, position);
        } else {
            List<Feature> selectedFeatures = new ArrayList<Feature>();
            for (Feature feature : features) {
                if (feature.getProperty("category").getAsString().equals(cultureCategory)) {
                    selectedFeatures.add(feature);
                }
            }
            setRecyclerViewData(selectedFeatures, position);
        }
    }

    public void setFilter(View v) {
        // check if this button was pressed before
        if ((Button)v != focusedButton) {
            focusedButton.clearFocus();
            // set focusoble must be turned off for previous button otherwise the onclick listener doesn't work
            focusedButton.setFocusable(false);
            //change focused button to new one
            focusedButton = (Button)v;
            focusedButton.setFocusableInTouchMode(true);
            focusedButton.requestFocus();
            // remember cultural category
            cultureCategory = (String)v.getTag();
        }
        buildTilequeryRequest(Point.fromLngLat(9.685242, 50.550657));
    }

    // onClick Listener für Suchfeld aktivieren
    private void initSearchFab() {
        Point testPoint = Point.fromLngLat(9.685242, 50.550657);
        buildTilequeryRequest(testPoint);
        Mapbox.getInstance(this, MAPBOX_TOKEN); // Konfiguration des Mapbox Tokens
        findViewById(R.id.restaurant_search_field).setOnClickListener(new View.OnClickListener() {
            // neue Suchaktivität öffnen wenn Button gedrückt
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : MAPBOX_TOKEN)
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#FFFFFF"))
                                .limit(10)
                                .build(PlaceOptions.MODE_CARDS))
                        .build(MainActivity.this);
                // Activity öffnen von der man nach dem öffnen ein Resultat wünscht welches mit onActivityResult behandelt wird
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // CarmenFeature der ausgewählten Location wählen
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
        }
    }
}
