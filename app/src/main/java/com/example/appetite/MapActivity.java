package com.example.appetite;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import java.util.List;


public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {


    private MapboxMap mapboxMap;     // Schnittstelle zur Map
    private MapView mapView; // Zugriff auf Android Mapbox SDK Methoden
    Style style;     // Mapstyle

    private PermissionsManager permissionsManager;

    // wird benötigt um die Android SDK zu nutzen
    private final static String MAPBOX_TOKEN = "pk.eyJ1IjoidmluaW1pY2hlbCIsImEiOiJjbGFqdWNvYmkwZmZhM3JuMzIxYzl2Z3h4In0.bmACrWyEcA6772uD758XPw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, MAPBOX_TOKEN); // Konfiguration des Mapbox Tokens
        setContentView(R.layout.activity_map);
        Intent i = getIntent();
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this); // Callback Objekt wenn die Map geladen hat ist dieses Objekt
    }

    public void launchReservation(View v) {
        Intent i = new Intent(this, ReservationActivity.class);
        startActivity(i);
    }


    // wird aufgerufen wenn Map fertiggeladen hat
    // legt Mapstyle fest
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        // erzeugen und setzen eines Mapstyles mit URL
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/vinimichel/clap8w2r0002614ktc3lxzitn"), style -> onStyleLoaded(style));
    }

    // wird aufgerufen wenn Style geladen ist
    private void onStyleLoaded(Style style) {
        mapboxMap.addOnMapClickListener(MapActivity.this);
        this.style = style;
        enableLocationComponent(style);
    }

    // Funktion zeigt Standort mithilfe des LocationComponents an
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check ob der Standort ermittelt werden darf, wenn nicht wird danach gefragt
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // LocationComponent Instanz herausgreifen
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Aktiviere Standortobjekt mit gewähltem Style
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            // Objekt sichtbar machen
            locationComponent.setLocationComponentEnabled(true);

            // Kameramodus setzen
            locationComponent.setCameraMode( CameraMode.TRACKING,
                    2500L /*duration -> Dauer des zooms*/ ,
                    13.0 /*zoom*/,
                    null /*bearing, use current/determine based on the tracking mode*/,
                    45.0 /*tilt*/,
                    null /*transition listener*/);

            // Kompass als Standortsymbol
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            // Um Erlaubnis zur Ermittlung des Standorts bitten
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /** Aufgerufen wenn User Button drückt.
     * Rezentriet Karte auf den derzeitigen Standort des Geräts */
    public void recenterLocation(View view) {
        if (style != null) {
            enableLocationComponent(style);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(style -> enableLocationComponent(style));
        } else {
            Toast.makeText(this, "Permissions wurden nicht gewährt", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        // LatLng in Bilschirmpixel umwandeln und nur  coordinates to screen pixel and only query the rendered features.
        final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);

        // gerenderte Features auf Pixel abfragen (nur Features von "fulda-restaurants"-layer
        List<Feature> features = mapboxMap.queryRenderedFeatures(pixel, "fulda-restaurants");
        // Restaurant info Popup Layout herausgreifen
        RelativeLayout popupLayout = findViewById(R.id.place_info_layout);

        // prüfen ob Restaurants an Punkt gefunden wurden
        if (features.size() > 0) {
            // Erstes Feature aus Feature Liste herausgreifen
            // Im Idealfall befindet sich auf einem Punkt nur ein Restaurant/Feature
            Feature feature = features.get(0);

            // Versichern das Feature/Restaurant Eigenschaften hat
            if (feature.properties() != null) {
                // Textfeld auf dem Namen des Restaurants zu finden ist herausgreifen
                TextView restaurantNameField = findViewById(R.id.restaurant_name);
                // json Eintrag mit Namen in String konvertieren und in Feld darstellen
                restaurantNameField.setText(feature.getProperty("title").getAsString());
                Log.d(TAG, String.format("restaurant title = %s", feature.getProperty("title").getAsString()));

                // Textfeld mit Bescreibung wählen und json Daten auf Feld ausgeben
                TextView restaurantDescriptionField = findViewById(R.id.restaurant_description);
                restaurantDescriptionField.setText(feature.getProperty("description").getAsString());
                Log.d(TAG, String.format("restaurant_ description = %s", feature.getProperty("description").getAsString()));
                
                // falls Popup mit Informationen sichtbar machen
                popupLayout.setVisibility(View.VISIBLE);
            } else {
                Log.d(TAG, "Achtung das Restaurant hat keine abrufbaren Eigenschaften!");
            }
        } else {
            // falls kein Restaurant an Pixel gefunden -> Popup mit Infos verschwindent
            popupLayout.setVisibility(View.GONE);
        }
        return true;
    }


    // Lifecycle Methoden der Karte

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}

