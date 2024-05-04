package com.cmp354.ausvalet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class BookCaptain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_captain);

        Intent i = getIntent();
        Toast.makeText(getApplicationContext(), i.getStringExtra("username"), Toast.LENGTH_SHORT).show();
    }
}