package com.example.appetite;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.PointF;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appetite.dataModels.NearbyRestaurants;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
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
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;


public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {

    private MapboxMap mapboxMap;     // Schnittstelle zur Map
    private MapView mapView; // Zugriff auf Android Mapbox SDK Methoden
    Style style;     // Mapstyle
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    NearbyRestaurants selectedRestaurant;

    private PermissionsManager permissionsManager;
    ConstraintLayout popupLayout;

    // wird benötigt um die Android SDK zu nutzen
    private final static String MAPBOX_TOKEN = "pk.eyJ1IjoidmluaW1pY2hlbCIsImEiOiJjbGFqdWNvYmkwZmZhM3JuMzIxYzl2Z3h4In0.bmACrWyEcA6772uD758XPw";
    Point lastKnownLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, MAPBOX_TOKEN); // Konfiguration des Mapbox Tokens
        setContentView(R.layout.activity_map);
        Intent i = getIntent();
        popupLayout = findViewById(R.id.place_info_layout);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this); // Callback Objekt wenn die Map geladen hat ist dieses Objekt
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
        initSearchFab();

        // Layer Icon zur Map hinzufügen
        style.addImage(symbolIconId, BitmapFactory.decodeResource(
                MapActivity.this.getResources(), R.drawable.blue_marker_view));

        // neue leere GeoJson Quelle hinzufügen
        setUpSource(style);

        // neues Layer wird in die Map eingebunden
        setupLayer(style);
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

            lastKnownLocation = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),locationComponent.getLastKnownLocation().getLongitude());
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

            mapboxMap.getStyle(style -> onStyleLoaded(style));
        } else {
            Toast.makeText(this, "Could not grant permissions", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        // LatLng in Bilschirmpixel umwandeln und nur  coordinates to screen pixel and only query the rendered features.
        final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);

        // gerenderte Features auf Pixel abfragen (nur Features von "fulda-restaurants"-layer
        List<Feature> features = mapboxMap.queryRenderedFeatures(pixel, "restaurant-features");
        // Restaurant info Popup Layout herausgreifen

        Log.d(TAG, "I was here");

        // prüfen ob Restaurants an Punkt gefunden wurden
        if (features.size() > 0) {
            // Erstes Feature aus Feature Liste herausgreifen
            // Im Idealfall befindet sich auf einem Punkt nur ein Restaurant/Feature
            Feature feature = features.get(0);

            // Versichern das Feature/Restaurant Eigenschaften hat
            if (feature.properties() != null) {
                NearbyRestaurants restaurant = new NearbyRestaurants(feature, lastKnownLocation);
                setRestaurantPopUp(restaurant);
            } else {
                Log.d(TAG, "Achtung das Restaurant hat keine abrufbaren Eigenschaften!");
            }
        } else {
            // falls kein Restaurant an Pixel gefunden -> Popup mit Infos verschwindent
            popupLayout.setVisibility(View.GONE);
            selectedRestaurant = null;

        }
        return true;
    }

    public void openRestaurantDescription(View v) {
        if (selectedRestaurant != null) {
            Intent descriptionIntent = new Intent(this, RestaurantDescription.class);
            descriptionIntent.putExtra("restaurantData", selectedRestaurant);
            startActivity(descriptionIntent);
        }
    }

    private void setRestaurantPopUp(NearbyRestaurants restaurant) {
        selectedRestaurant = restaurant;
        // Textfeld auf dem Namen des Restaurants zu finden ist herausgreifen
        TextView restaurantNameField = findViewById(R.id.restaurant_name_on_map);
        // json Eintrag mit Namen in String konvertieren und in Feld darstellen
        restaurantNameField.setText(restaurant.getRestaurantName());

        TextView restaurantDescriptionField = findViewById(R.id.restaurant_address);
        restaurantDescriptionField.setText(restaurant.getCityName() + ", " + restaurant.getAddress());

        TextView distanceField = findViewById(R.id.distance_field);
        distanceField.setText(Double.toString(restaurant.getDistance())+ "km");
        // falls Popup mit Informationen sichtbar machen
        popupLayout.setVisibility(View.VISIBLE);

    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    // Layer hinzufügen
    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[] {0f, -8f})
        ));
    }

    // onClick Listener für Suchfeld aktivieren
    private void initSearchFab() {
        findViewById(R.id.PLZTextfield).setOnClickListener(new View.OnClickListener() {
            // neue Suchaktivität öffnen wenn Button gedrückt
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : MAPBOX_TOKEN)
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#FFFFFF"))
                                .limit(10)
                                .build(PlaceOptions.MODE_CARDS))
                        .build(MapActivity.this);
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



            // Neue GeoJson FeatureCollection hinzufügen und neues Feature mit oben gewählten CarmenFeature einfügen
            // Danach springen wir zum neu hinzugefügten Feature
            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }
                    // Textfeld des Suchfelds auf Location setzen
                    EditText searchField = (EditText)findViewById(R.id.PLZTextfield);
                    searchField.setText(selectedCarmenFeature.placeName(), TextView.BufferType.EDITABLE);;

                    // Kamera auf selektiertes Feature schweifen lassen
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);
                }
            }
        }
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

