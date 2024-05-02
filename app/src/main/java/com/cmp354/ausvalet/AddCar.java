package com.cmp354.ausvalet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddCar extends AppCompatActivity implements View.OnClickListener{

    EditText et_make;
    EditText et_model;
    EditText et_year;
    EditText et_plate;

    RadioGroup rg_carType;
    RadioButton rb_automatic;
    RadioButton rb_manual;

    Button btn_uploadCarID;
    Button btn_done;

    Uri imagePath;

    String id;

    private final int GALLERY_REQ_CODE = 1000;
    private final String TAG = "SignUpActivity";

    static FirebaseFirestore db;
    static FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);


        et_make = findViewById(R.id.et_make);
        et_model = findViewById(R.id.et_model);
        et_year = findViewById(R.id.et_year);
        et_plate = findViewById(R.id.et_plate);

        rg_carType = findViewById(R.id.rg_carType);
        rb_automatic = findViewById(R.id.rb_automatic);
        rb_manual = findViewById(R.id.rb_manual);

        btn_uploadCarID = findViewById(R.id.btn_uploadCarID);
        btn_done = findViewById(R.id.btn_capDone);

        btn_uploadCarID.setOnClickListener(this);
        btn_done.setOnClickListener(this);

        db = SignUpActivity.db.getInstance();
        storage = SignUpActivity.storage.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        id = intent.getStringExtra("username");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_uploadCarID:
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);
                break;
            case R.id.btn_capDone:
                //TODO: To add to the car Class
                CollectionReference users = db.collection("cars");

                Map<String, Object> car = new HashMap<>();

                String make = et_make.getText().toString();
                String model = et_model.getText().toString();
                String year = et_year.getText().toString();
                String plate = et_plate.getText().toString();

                //Validate First Name
                if (!make.equals(""))
                    car.put("make", make);
                else{
                    Toast.makeText(getApplicationContext(), "Entire a valid Make Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validate Last Name
                if (!model.equals(""))
                    car.put("model", model);
                else{
                    Toast.makeText(getApplicationContext(), "Entire a valid Model Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!year.equals(""))
                    //TODO: Implement Regex to validate the number
                    car.put("year", year);
                else{
                    Toast.makeText(getApplicationContext(), "Entire a valid Year", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!plate.equals(""))
                    //TODO: Implement Regex to validate Plate
                    car.put("plate", plate);
                else{
                    Toast.makeText(getApplicationContext(), "Entire a valid Plate", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!id.equals(""))
                    //TODO: Implement Regex to validate Plate
                    car.put("id", id);
                else{
                    Toast.makeText(getApplicationContext(), "Entire a valid ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(rb_automatic.isChecked()){
                    car.put("isAutomatic", true);
                }
                else{
                    car.put("isAutomatic", false);
                }

                if (imagePath != null)
                {
                    // Create a storage reference from our app
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://ausvalet.appspot.com");

                    // Create a reference to "mountains.jpg"
                    StorageReference idRef = storageRef.child(id + "_car_id.jpeg");

                    // Create a reference to 'images/mountains.jpg'
                    StorageReference idImagesRef = storageRef.child("car_id/" + id + "_car_id.jpeg");

                    // While the file names are the same, the references point to different files
                    idRef.getName().equals(idImagesRef.getName());    // true
                    idRef.getPath().equals(idImagesRef.getPath());    // false

                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagePath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = idRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Choose a valid ID Image", Toast.LENGTH_SHORT).show();
                    return;
                }

                users.document(id).set(car);

                Log.d(TAG, "Car has been added to firebase");

                Log.d(TAG, "Adding Car is completed!");

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("username", id);
                startActivity(i);


                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE) {
                // Do nothing
                imagePath = data.getData();
            }
        }
    }
}