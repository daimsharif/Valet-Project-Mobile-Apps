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

public class CaptainDetails extends AppCompatActivity implements View.OnClickListener {

    Button btn_uploadDL;
    Button btn_capDone;

    Uri imagePath;

    String id;

    private final int GALLERY_REQ_CODE = 1000;
    private final String TAG = "CaptainDetails";

    static FirebaseFirestore db;
    static FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captain_details);

        btn_capDone = findViewById(R.id.btn_capDone);
        btn_uploadDL = findViewById(R.id.btn_uploadDL);

        btn_capDone.setOnClickListener(this);
        btn_uploadDL.setOnClickListener(this);

        db = SignUpActivity.db.getInstance();
        storage = SignUpActivity.storage.getInstance();

        Intent i = getIntent();
        id = i.getStringExtra("username");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_uploadDL:
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);
                break;
            case R.id.btn_capDone:
                //TODO: To add to the car Class
                CollectionReference users = db.collection("cars");

                if (imagePath != null)
                {
                    // Create a storage reference from our app
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://ausvalet.appspot.com");

                    // Create a reference to "mountains.jpg"
                    StorageReference idRef = storageRef.child(id + "_dl.jpeg");

                    // Create a reference to 'images/mountains.jpg'
                    StorageReference idImagesRef = storageRef.child("dl/" + id + "_dl.jpeg");

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


                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("username", id);
                i.putExtra("isCaptain", false);
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