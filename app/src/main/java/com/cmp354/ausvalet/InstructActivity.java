package com.cmp354.ausvalet;

import static com.cmp354.ausvalet.MainActivity.isCaptain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InstructActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_iNotice;
    Button btn_iDone;

    String instructions;
    String buttonText;

    String driver_id;

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
    }

    @Override
    public void onClick(View v) {
        if(isCaptain == true){
            // Do captain related activity
            // Basically send notification to the customer
        }
        else{
            // Do customer related activity
            // Show the map view of the driver
            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
            i.putExtra("driver", driver_id);
            startActivity(i);

        }
    }
}