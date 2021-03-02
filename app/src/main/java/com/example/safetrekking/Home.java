package com.example.safetrekking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;


public class Home extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, Style.OnStyleLoaded, PermissionsListener {
    int idUPrefs;
    private MapView mapView;
    private MapboxMap mapboxMap = null;
    private Style style;
    private PermissionsManager permissionsManager;
    private FusedLocationProviderClient fusedLocationClient;
    public Location userLocation;
    public CameraPosition position;
    private Button saveB;
    private static final int REQUEST_CODE_AUTOCOMPLETE_START = 1;
    private static final int REQUEST_CODE_AUTOCOMPLETE_STOP = 2;
    public EditText edStart;
    public EditText edStop;

    private static final int PLACE_SELECTION_REQUEST_CODE_START = 5;
    private static final int PLACE_SELECTION_REQUEST_CODE_STOP = 6;

    private CarmenFeature Start;
    private CarmenFeature Stop;



    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //prima di settare content view creare istanza mapbpx
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));


        setContentView(R.layout.activity_home);

        //nascondo la title bar
        getSupportActionBar().hide();

        //recupero l'idU
        idUPrefs = getSharedPreferences("MySharedPreferences", 0).getInt("idu", 7);
        Log.d("home", idUPrefs + "");

        // CODICE BOTTOM MENU

        // inizializzo e assegno variabili
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // set home selected
        bottomNavigationView.setSelectedItemId(R.id.newHike);

        //item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.myHikes:
                        startActivity(new Intent(getApplicationContext(),
                                myHikes.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.newHike:
                        return true;

                    case R.id.freeHike:
                        startActivity(new Intent(getApplicationContext(),
                                freeHike.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.account:
                        startActivity(new Intent(getApplicationContext(),
                                MyAccount.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
        // FINE BOTTOM MENU

        //CODICE MAP

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //FINE CODICE MAP

        //LOCATION SERVICE
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d("corrente location  ", ""+location );
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            userLocation = location;
                        }
                    }
                });
        //NASCONDO BOTTONE
        saveB = findViewById(R.id.buttonSave);
        saveB.setVisibility(View.GONE);
        //codice per far riapparire bottone
        //saveB.setVisibility(View.VISIBLE);

        //---------------------

        //assegno click listener a elementi interfaccia
        edStart = findViewById(R.id.editTextStart);
        edStart.setOnClickListener(this);

        edStop = findViewById(R.id.editTextStop);
        edStop.setOnClickListener(this);

        ImageButton btStart = findViewById(R.id.editStartButton);
        btStart.setOnClickListener(this);

        ImageButton btStop = findViewById(R.id.editStopButton);
        btStop.setOnClickListener(this);

        ImageButton setMyLoc = findViewById(R.id.myLocationButton);
        setMyLoc.setOnClickListener(this);
    }

    // -------------------------------------------------------

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, this);
    }


    @Override
    public void onStyleLoaded(@NonNull Style style) {
        this.style = style;



        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
        enableLocationComponent(style);

        position = new CameraPosition.Builder()
                .target(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                .zoom(12)
                .tilt(20)
                .build();

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000);


    }



    //------------------------------------------------------------
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Enable the most basic pulsing styling by ONLY using
    // the `.pulseEnabled()` method
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .compassAnimationEnabled(true) //forse giusto!
                    .elevation(5)
                    .build();


        // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            Log.d("location componente", ""+ locationComponent);

        // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build());

        // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

        // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

        // Set the component's render mode
            //nell'es RENDER MODE NORMAL
            locationComponent.setRenderMode(RenderMode.NORMAL);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }
    //----------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }



    //disabilito back button
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editTextStart :
                Log.d("onclick", "click editetxt start");


                Intent intentSetStart = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(getString(R.string.mapbox_access_token))
                        .placeOptions(

                                PlaceOptions.builder()
                                        .backgroundColor(Color.parseColor("#EEEEEE"))
                                        .limit(10)
                                        //.addInjectedFeature(home)
                                        //.addInjectedFeature(work)
                                        .build(PlaceOptions.MODE_CARDS)

                        )
                        .build(this);
                intentSetStart.putExtra("type", 1);
                startActivityForResult(intentSetStart, REQUEST_CODE_AUTOCOMPLETE_START);


                break;
            case R.id.editTextStop:
                Log.d("onclick", "click editetxt stop");

                Intent intentSetStop = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(getString(R.string.mapbox_access_token))
                        .placeOptions(

                                PlaceOptions.builder()
                                        .backgroundColor(Color.parseColor("#EEEEEE"))
                                        .limit(10)
                                        //.addInjectedFeature(home)
                                        //.addInjectedFeature(work)
                                        .build(PlaceOptions.MODE_CARDS)

                        )
                        .build(this);
                intentSetStop.putExtra("type", 2);
                startActivityForResult(intentSetStop, REQUEST_CODE_AUTOCOMPLETE_STOP);


                break;
            case R.id.editStartButton:
                Log.d("onclick", "click button start");

                Intent intentStart = new PlacePicker.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken())
                        .placeOptions(
                                PlacePickerOptions.builder()
                                        .statingCameraPosition(
                                                new CameraPosition.Builder()
                                                        .target(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                                                        .zoom(16)
                                                        .build())
                                        .build())
                        .build(this);
                startActivityForResult(intentStart, PLACE_SELECTION_REQUEST_CODE_START);

                break;
            case R.id.editStopButton:
                Log.d("onclick", "click button stop");

                Intent intentStop = new PlacePicker.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken())
                        .placeOptions(
                                PlacePickerOptions.builder()
                                        .statingCameraPosition(
                                                new CameraPosition.Builder()
                                                        .target(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                                                        .zoom(16)
                                                        .build())
                                        .build())
                        .build(this);
                startActivityForResult(intentStop, PLACE_SELECTION_REQUEST_CODE_STOP);

                break;
            case R.id.myLocationButton:

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                Log.d("new location to set ", ""+location );
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    userLocation = location;
                                }
                            }
                        });
                position = new CameraPosition.Builder()
                        .target(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                        .zoom(12)
                        .tilt(20)
                        .build();

                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000);

            default:
                Log.d("onclick", "no");
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE_START ) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            //Log.d("onclick", "click su start e aggiorno: "+ feature.text());
            edStart.setText(feature.placeName());
            //Toast.makeText(this, feature.text(), Toast.LENGTH_LONG).show();

            Start = feature;
            //Log.d("onclick", "click su start e carmen start divetna: "+ Start);
        }
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE_STOP ){
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            //Log.d("onclick", "click su stop e aggiorno: "+ feature.text());
            edStop.setText(feature.text());
            //Toast.makeText(this, feature.text(), Toast.LENGTH_LONG).show();

            Stop = feature;
            //Log.d("onclick", "click su stop e carmen start divetna: "+ Stop);
        }

        if (requestCode == PLACE_SELECTION_REQUEST_CODE_START && resultCode == RESULT_OK){

            // Retrieve the information from the selected location's CarmenFeature

            CarmenFeature carmenFeature = PlacePicker.getPlace(data);
            edStart.setText(carmenFeature.text());
            Start = carmenFeature;
            //Log.d("onclick", "click su start e carmen start divetna: "+ Start);
        }
        if (requestCode == PLACE_SELECTION_REQUEST_CODE_STOP && resultCode == RESULT_OK){

            // Retrieve the information from the selected location's CarmenFeature

            CarmenFeature carmenFeature = PlacePicker.getPlace(data);
            edStop.setText(carmenFeature.text());

            Stop = carmenFeature;
            //Log.d("onclick", "click su stop e carmen start divetna: "+ Stop);
        }
        Log.d("onclick", "click su start e carmen start divetna: "+ Start);
        Log.d("onclick", "click su stop e carmen start divetna: "+ Stop);

    }



    @Override
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }



}