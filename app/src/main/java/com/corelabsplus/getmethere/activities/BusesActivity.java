package com.corelabsplus.getmethere.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.corelabsplus.getmethere.R;
import com.corelabsplus.getmethere.utils.Bus;
import com.corelabsplus.getmethere.utils.MLatLng;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BusesActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener{

    private Toolbar toolbar;

    private static final String TAG = "MapsActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 11;
    private static final int PLACE_PICKER_REQUEST = 1;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    // private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    //private PlaceInfo mPlace;
    private Marker mMarker;

    BitmapDescriptor personMarker, busMarker;

    //    BUSES

    private List<Bus> buses = new ArrayList<>();

//    Stops


    //    DATABASE TABLES
    private String BUSES="buses", STOPS="stops";


//    DATABASE REFERENCES

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buses);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        personMarker = BitmapDescriptorFactory.fromResource(R.drawable.person_marker);
        busMarker = BitmapDescriptorFactory.fromResource(R.drawable.bus_marker);


        getLocationPermission();


    }

    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(MapsActivity.this, "Changed", Toast.LENGTH_SHORT).show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                buses.clear();

//                GETTING BUSSES
                for (DataSnapshot busSnapshot : dataSnapshot.child(BUSES).getChildren()){
                    String plate, agency,  dest, origin, lat, lng;
                    MLatLng mLatLng;
                    int seats;

                    plate = busSnapshot.child("plateNumber").getValue().toString();
                    agency = busSnapshot.child("Agency").getValue().toString();
                    seats = Integer.parseInt(String.valueOf(busSnapshot.child("seats").getValue()));
                    dest = busSnapshot.child("destination").getValue().toString();
                    origin = busSnapshot.child("origin").getValue().toString();
                    lat = busSnapshot.child("currentLocation").child("latitude").getValue().toString();
                    lng = busSnapshot.child("currentLocation").child("longitude").getValue().toString();

                    mLatLng = new MLatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    Bus theBus = new Bus(plate, agency, origin, dest, seats, mLatLng);
                    buses.add(theBus);
                    //Toast.makeText(MapsActivity.this, buses.get(0).Agency, Toast.LENGTH_SHORT).show();
                }

                setMarkers();


                //Toast.makeText(MapsActivity.this, String.valueOf(buses.size()), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BusesActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
//        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            init();
        }
    }

//    INITIALIZING GOOGLE MAPS

    private void init() {
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }



//    GETTING DEVICE LOCATION

    private void getDeviceLocation(){
//        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
//                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

//                            MarkerOptions options = new MarkerOptions()
//                                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
//                                    .title("Victor NH")
//                                    .icon(personMarker);
//
//                            mMap.addMarker(options).showInfoWindow();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "Victor NH");

                        }else{
//                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(BusesActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
//            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }


//    MOVING CAMERA

    private void moveCamera(LatLng latLng, float zoom, String title){
        title = "Victor NH";
//        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .icon(personMarker)
                .title(title);
        mMap.addMarker(options).showInfoWindow();

//        if(title.isEmpty()){
//            MarkerOptions options = new MarkerOptions()
//                    .position(latLng)
//                    .title(title);
//            mMap.addMarker(options).showInfoWindow();
//        }
//
//        else{
//            MarkerOptions options = new MarkerOptions()
//                    .position(latLng)
//                    .title(title);
//            mMap.addMarker(options).showInfoWindow();
//        }
    }

//    INIT MAPS

    private void initMap(){
//        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(BusesActivity.this);
    }



//    REQUESTING PERMISSIONS

    private void getLocationPermission(){
//        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


//    PERMISSION RESULTS

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
//                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    //Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void setMarkers(){
        getDeviceLocation();
        mMap.clear();
        //Toast.makeText(this, String.valueOf(restaurants.size()), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < buses.size(); i++) {
            MarkerOptions options = new MarkerOptions()
                    .title(buses.get(i).plateNumber + "\n" + buses.get(i).Agency)
                    .position(new LatLng(buses.get(i).currentLocation.getLatitude(), buses.get(i).currentLocation.getLongitude()))
                    .visible(true)
                    .icon(busMarker)
                    .snippet(String.valueOf("Seats: " + buses.get(i).seats));

            mMap.addMarker(options).showInfoWindow();
        }
    }
}
