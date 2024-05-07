package com.cmp354.ausvalet;

import static com.cmp354.ausvalet.MainActivity.id;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.cmp354.ausvalet.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private LocationListener locationListener;//mainly for onLocationChanged
    private LocationManager locationManager;//for requesting updates and checking if GPS is on
    private final long MIN_TIME = 1000; // min of 1 sec between GPS readings
    private final long MIN_DIST = 0; // if new reading with x meters then do not show it

    String driver_id;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        driver_id = i.getStringExtra("driver");

        db = FirebaseFirestore.getInstance();

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //2. did the user switch on GPS? If then open the settings
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable GPS!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            Toast.makeText(this, "GPS is enabled!", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    protected void onResume() {
        //1. get location manager object
        super.onResume();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.addMarker(new MarkerOptions().position(new LatLng(25.310338125326922,
//                55.491244819864185)).title("AUS"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(25.310338125326922, 55.491244819864185)));

        //3. setup call backs for the location
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(MapsActivity.this, "The location has changed", Toast.LENGTH_SHORT).show();
                mMap.clear();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


                mMap.addMarker(
                        new MarkerOptions().position(latLng).title(latLng.toString()));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition
                        (new CameraPosition.Builder()
                                /* Creates a builder for a camera position.*/
                                .target(new LatLng(location.getLatitude(),
                                        location.getLongitude()))
                                .zoom(16.5f) //0 is the whole world
                                .bearing(0) //north is 0
                                .tilt(25) //camera angle facing earth
                                .build()));
                Log.d("CMP354---", latLng.toString());


                String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(location.getLatitude(), location.getLongitude()));

                // Add the hash and the lat/lng to the document. We will use the hash
                // for queries and the lat/lng for distance comparisons.
                Map<String, Object> updates = new HashMap<>();
                updates.put("geohash", hash);
                updates.put("lat", location.getLatitude());
                updates.put("lng", location.getLongitude());


                DocumentReference ref = db.collection("locations").document(id);
                ref.set(updates).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Maps", "Location Updated to Firebase");
                            }
                        });

                DocumentReference docRef = db.collection("locations").document(driver_id);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        FMap map = documentSnapshot.toObject(FMap.class);
                        LatLng latLng = new LatLng(map.getLat(), map.getLng());
                        //TODO: Add a different marker
                        mMap.addMarker(
                            new MarkerOptions().position(latLng).title(latLng.toString()));
                    }
                });

            }


            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        } else {
            //5. now we are ready for requesting GPS updates
            Toast.makeText(this, LocationManager.GPS_PROVIDER, Toast.LENGTH_SHORT).show();
            locationManager.requestLocationUpdates(
                    "network", 5, 0, locationListener);
        }


    }

    //@SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //now we have the user permission, so let us start all over again
                Log.d("CMP354---", "Location permission granted");
                onMapReady(mMap);
            } else
                finish(); //no point continuing with the app
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
        Log.d("CMP354---", "locationManager.removeUpdates(locationListener)");
    }
}
