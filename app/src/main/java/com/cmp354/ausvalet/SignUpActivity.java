package com.cmp354.ausvalet;

import static android.content.ContentValues.TAG;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_first;
    EditText et_last;
    EditText et_num;
    EditText et_id;
    EditText et_pswd;

    RadioGroup rg_userType;
    RadioButton rb_customer;
    RadioButton rb_captain;

    Button btn_uploadID;
    Button btn_next;

    User user;
    Uri imagePath;

    private final int GALLERY_REQ_CODE = 1000;
    private final String TAG = "SignUpActivity";
    static FirebaseFirestore db;
    static FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        et_first = findViewById(R.id.et_first);
        et_last = findViewById(R.id.et_last);
        et_num = findViewById(R.id.et_num);
        et_id = findViewById(R.id.et_id);
        et_pswd = findViewById(R.id.et_pswd);

        rg_userType = findViewById(R.id.rg_userType);
        rb_customer = findViewById(R.id.rb_customer);
        rb_captain = findViewById(R.id.rb_captain);

        btn_uploadID = findViewById(R.id.btn_uploadID);
        btn_next = findViewById(R.id.btn_next);

        btn_uploadID.setOnClickListener(this);
        btn_next.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_uploadID:
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);
                break;
            case R.id.btn_next:
                //TODO: To add to the user Class
                CollectionReference users = db.collection("users");

                Map<String, Object> user = new HashMap<>();

                String first = et_first.getText().toString();
                String last = et_last.getText().toString();
                String number = et_num.getText().toString();
                String id = et_id.getText().toString();
                String pswd = et_pswd.getText().toString();

                //Validate First Name
                if (!first.equals(""))
                    user.put("first", first);
                else{
                    Toast.makeText(getApplicationContext(), "Entire a valid First Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validate Last Name
                if (!last.equals(""))
                    user.put("last", last);
                else{
                    Toast.makeText(getApplicationContext(), "Entire a valid Last Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!number.equals(""))
                    //TODO: Implement Regex to validate the number
                    user.put("number", number);
                else{
                    Toast.makeText(getApplicationContext(), "Entire a valid Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!id.equals(""))
                    //TODO: Implement Regex to validate AUS ID
                    user.put("id", id);
                else{
                    Toast.makeText(getApplicationContext(), "Entire a valid AUS ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(rb_captain.isChecked()){
                    user.put("isCaptain", true);
                }
                else{
                    user.put("isCaptain", false);
                }

                if (imagePath != null)
                {
                    // Create a storage reference from our app
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://ausvalet.appspot.com");

                    // Create a reference to "mountains.jpg"
                    StorageReference idRef = storageRef.child(id + ".jpeg");

                    // Create a reference to 'images/mountains.jpg'
                    StorageReference idImagesRef = storageRef.child("aus_id/" + id + "_aus_id.jpeg");

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

                users.document(et_id.getText().toString()).set(user);

                Log.d(TAG, "User has been added to firebase");


                Login.mAuth.createUserWithEmailAndPassword(id + "@aus.edu", pswd)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(getApplicationContext(), "Create User With Email : success\nPlease login",
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = Login.mAuth.getCurrentUser();
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    i.putExtra("username", user.getEmail());
                                    startActivity(i);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


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