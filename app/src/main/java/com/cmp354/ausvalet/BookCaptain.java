package com.cmp354.ausvalet;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookCaptain extends AppCompatActivity implements View.OnClickListener {

    Spinner spinner_dropoff;
    Spinner spinner_parking;

    EditText et_special;

    ArrayList<String> dropOffLocations = new ArrayList<String>();
    ArrayList<String> parkingLocations = new ArrayList<String>();


    String driver_id;

    Button btn_book;
    Button btn_cancel;

    String captainId,customerId;

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_captain);

        Intent i = getIntent();
        driver_id = i.getStringExtra("username");
        captainId= i.getStringExtra("captainId");
        customerId= i.getStringExtra("customerId");
        Toast.makeText(getApplicationContext(), "cap"+i.getStringExtra("captainId")+"cust"+
                i.getStringExtra("customerId"), Toast.LENGTH_SHORT).show();

        spinner_dropoff = findViewById(R.id.spinner_dropoff);
        spinner_parking = findViewById(R.id.spinner_parking);
        et_special = findViewById(R.id.et_special);
        btn_book = findViewById(R.id.btn_book);

        btn_book.setOnClickListener(this);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);

        spinner_dropoff = findViewById(R.id.spinner_dropoff);
        spinner_parking = findViewById(R.id.spinner_parking);

        //TODO: Add more places

        dropOffLocations.add("ESB Building");
        dropOffLocations.add("Library Roundabout");
        dropOffLocations.add("Main Roundabout");

        parkingLocations.add("ESB Parking P21");
        parkingLocations.add("Free Football P5");
        parkingLocations.add("Free Physics P6");

        ArrayAdapter<String> drop_adapter =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, dropOffLocations);
        drop_adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        spinner_dropoff.setAdapter(drop_adapter);

        ArrayAdapter<String> parking_adapter =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, parkingLocations);
        parking_adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        spinner_parking.setAdapter(parking_adapter);

        db = FirebaseFirestore.getInstance();






    }

    @Override
    public void onClick(View v) {
        db = FirebaseFirestore.getInstance();


        db.collection("users")
                .whereEqualTo("captain", true)//TODO change it to isCaptain
                .whereEqualTo("id", captainId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        User captain = document.toObject(User.class);
                                        Log.d("daim",captain.toString());
                                        if (captain.getAvailable()) {
                                            Map<String, Object> req = new HashMap<>();
                                            req.put("captainId", captain.getId());
                                            req.put("customerId", customerId);
                                            req.put("status", "requested");
                                            db.collection("requests")
                                                    .add(req)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Log.d("daim", "request sent to captain");
                                                            Intent i = new Intent(BookCaptain.this, CustomerService.class);
                                                            i.putExtra("customerId",customerId);
                                                            i.putExtra("captainId",captainId);
                                                            ContextCompat.startForegroundService(BookCaptain.this,i);
                                                            btn_book.setVisibility(View.GONE);

                                                        }
                                                    });
                                        } else {
                                            //alert dialog to say sorry
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(BookCaptain.this);
                                            dialog.setTitle( "Sorry :(" )
                                                    .setMessage("The captain is no longer available.")
                                                    .setPositiveButton("Ok", null).show();
                                        }
                                        }




                                } else {
                                    Log.d("HOME", "Error getting documents: ", task.getException());
                                }
                            }
                        });

        switch (v.getId()){
            case R.id.btn_cancel:
                db = FirebaseFirestore.getInstance();


                db.collection("users")
                        .whereEqualTo("captain", true)//TODO change it to isCaptain
                        .whereEqualTo("id", captainId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        User captain = document.toObject(User.class);
                                        Log.d("daim",captain.toString());
                                        if (captain.getAvailable()) {
                                            Map<String, Object> req = new HashMap<>();
                                            req.put("captainId", captain.getId());
                                            req.put("customerId", customerId);
                                            req.put("status", "cancelled");
                                            db.collection("requests")
                                                    .add(req)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Log.d("daim", " cancel request sent to captain");
                                                            btn_book.setVisibility(View.VISIBLE);
                                                            btn_cancel.setVisibility(View.GONE);



                                                        }
                                                    });
                                        } else {
                                            //alert dialog to say sorry
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(BookCaptain.this);
                                            dialog.setTitle( "Sorry :(" )
                                                    .setMessage("The captain is no longer available.")
                                                    .setPositiveButton("Ok", null).show();
                                        }
                                    }




                                } else {
                                    Log.d("HOME", "Error getting documents: ", task.getException());
                                }
                            }
                        });


        }

    }


}