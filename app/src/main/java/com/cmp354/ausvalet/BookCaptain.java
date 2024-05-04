package com.cmp354.ausvalet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BookCaptain extends AppCompatActivity implements View.OnClickListener {

    Spinner spinner_dropoff;
    Spinner spinner_parking;

    EditText et_special;

    ArrayList<String> dropOffLocations = new ArrayList<String>();
    ArrayList<String> parkingLocations = new ArrayList<String>();




    Button btn_book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_captain);

        Intent i = getIntent();
        Toast.makeText(getApplicationContext(), i.getStringExtra("username"), Toast.LENGTH_SHORT).show();

        spinner_dropoff = findViewById(R.id.spinner_dropoff);
        spinner_parking = findViewById(R.id.spinner_parking);
        et_special = findViewById(R.id.et_special);
        btn_book = findViewById(R.id.btn_book);

        btn_book.setOnClickListener(this);

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






    }

    @Override
    public void onClick(View v) {

    }
}