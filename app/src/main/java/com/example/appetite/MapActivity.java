package com.example.appetite;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import java.util.List;
import static android.content.ContentValues.TAG;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {

    // interface to map
    private MapboxMap mapboxMap;
    // accessing Android Mapbox SDK methods
    private MapView mapView;
    // map style
    Style style;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";

    private PermissionsManager permissionsManager;

    // necessary to use Android SDK
    private final static String MAPBOX_TOKEN = "pk.eyJ1IjoidmluaW1pY2hlbCIsImEiOiJjbGFqdWNvYmkwZmZhM3JuMzIxYzl2Z3h4In0.bmACrWyEcA6772uD758XPw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // configuring mapbox token
        Mapbox.getInstance(this, MAPBOX_TOKEN);
        setContentView(R.layout.activity_map);
        Intent i = getIntent();
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        // callback object when map is loaded
        mapView.getMapAsync(this);
    }

    public void launchReservation(View v) {
        Intent i = new Intent(this, ReservationActivity.class);
        startActivity(i);
    }

    // called when map is done loading and sets map style
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/vinimichel/clap8w2r0002614ktc3lxzitn"), style -> onStyleLoaded(style));
    }

    // called when style is loaded
    private void onStyleLoaded(Style style) {
        mapboxMap.addOnMapClickListener(MapActivity.this);
        this.style = style;
        initSearchFab();

        // adding layer icon to map
        style.addImage(symbolIconId, BitmapFactory.decodeResource(
                MapActivity.this.getResources(), R.drawable.blue_marker_view));

        // adding new, empty GeoJson source
        setUpSource(style);

        // setting new layer to map
        setupLayer(style);
        enableLocationComponent(style);
    }

    // shows location with LocationComponents
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // checks whether location can be used - if set to no, asks
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // activate location object with chosen style
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode( CameraMode.TRACKING,
                    2500L /*duration of zoom*/ ,
                    13.0 /*zoom*/,
                    null /*bearing, use current/determine based on the tracking mode*/,
                    45.0 /*tilt*/,
                    null /*transition listener*/);

            // compass as location symbol
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            // ask for permission for location
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    // called when user presses button, positions map to device location
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
        // I don't quite understand the above comment, signed S.M.
        final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);

        // query rendered features on pixels (only "fulda-restaurants" layer)
        List<Feature> features = mapboxMap.queryRenderedFeatures(pixel, "fulda-restaurants");
        // grab restaurant information popup layout
        ConstraintLayout popupLayout = findViewById(R.id.place_info_layout);

        // checks whether restaurants have been found on coordinates
        if (features.size() > 0) {
            Feature feature = features.get(0);
            // ideal case: only one restaurant/feature on coordinate

            // making sure restaurant/restaurant has properties
            if (feature.properties() != null) {
                TextView restaurantNameField = findViewById(R.id.restaurant_name);
                // convert json entry with name to string and display as field
                restaurantNameField.setText(feature.getProperty("title").getAsString());
                Log.d(TAG, String.format("restaurant title = %s", feature.getProperty("title").getAsString()));

                // choose text field with description and print json data on field
                TextView restaurantDescriptionField = findViewById(R.id.restaurant_description);
                restaurantDescriptionField.setText(feature.getProperty("description").getAsString());
                Log.d(TAG, String.format("restaurant_ description = %s", feature.getProperty("description").getAsString()));
                
                popupLayout.setVisibility(View.VISIBLE);
            } else {
                Log.d(TAG, "Achtung, das Restaurant hat keine abrufbaren Eigenschaften!");
            }
        } else {
            // if no restaurant is found on pixels
            popupLayout.setVisibility(View.GONE);
        }
        return true;
    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    // add layer
    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
            iconImage(symbolIconId),
            iconOffset(new Float[] {0f, -8f})
        ));
    }

    // activate onClickListener for search array
    private void initSearchFab() {
        findViewById(R.id.PLZTextfield).setOnClickListener(new View.OnClickListener() {
            // new search activity on button press
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : MAPBOX_TOKEN)
                        .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#FFFFFF"))
                        .limit(10)
                        .build(PlaceOptions.MODE_CARDS))
                        .build(MapActivity.this);
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

            /* adding new GeoJson FeatureCollection and feature with CarmenFeature chosen above,
            then jump to newly added feature */
            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                            new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }
                    // set text field of search field to location
                    EditText searchField = (EditText)findViewById(R.id.PLZTextfield);
                    searchField.setText(selectedCarmenFeature.placeName(), TextView.BufferType.EDITABLE);

                    // hover camera on selected feature
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

    // lifecycle methods of map

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
