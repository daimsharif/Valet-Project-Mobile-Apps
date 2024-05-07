package com.cmp354.ausvalet;

import static com.cmp354.ausvalet.MainActivity.id;
import static com.cmp354.ausvalet.MainActivity.isCaptain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class InstructActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_iNotice;
    Button btn_iDone;

    String instructions;
    String buttonText;

    private LocationListener locationListener;//mainly for onLocationChanged


    String driver_id;

    private LocationManager locationManager;//for requesting updates and checking if GPS is on
    private final long MIN_TIME = 1000; // min of 1 sec between GPS readings
    private final long MIN_DIST = 0; // if new reading with x meters then do not show it


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruct);

        Intent i = getIntent();
        instructions = i.getStringExtra("iText");
        buttonText = i.getStringExtra("btnText");
        driver_id = i.getStringExtra("driver_id");

        tv_iNotice = findViewById(R.id.tv_iNotice);
        btn_iDone = findViewById(R.id.btn_iDone);

        tv_iNotice.setText(instructions + "");
        btn_iDone.setText(buttonText + "");

        btn_iDone.setOnClickListener(this);

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
    public void onClick(View v) {
        if (isCaptain == true) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Do captain related activity
            // Basically send notification to the customer

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    Toast.makeText(getApplicationContext(), "Step 2", Toast.LENGTH_SHORT).show();
                    // Add the hash and the lat/lng to the document. We will use the hash
                    // for queries and the lat/lng for distance comparisons.
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("lat", location.getLatitude());
                    updates.put("lng", location.getLongitude());


                    DocumentReference ref = db.collection("locations").document(id);
                    ref.set(updates).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Maps", "Failed :(");

                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("Maps", "Location Updated to Firebase");
                                    fragment_captain_home.parked();
                                    finish();
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
                locationManager.requestLocationUpdates("network", 1000, 1, locationListener);

            }

        }
        else{
            // Do customer related activity
            // Show the map view of the driver
//            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
//            i.putExtra("driver", driver_id);
//            startActivity(i);

        }





    }

    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //now we have the user permission, so let us start all over again
                Log.d("CMP354---", "Location permission granted");
            } else
                finish(); //no point continuing with the app
        }
    }

}