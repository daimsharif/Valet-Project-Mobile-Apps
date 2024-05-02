package com.cmp354.ausvalet;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener{
    EditText et_password,et_username;
    Button btn_login,btn_signup;
    static FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth=FirebaseAuth.getInstance();
        et_password=findViewById(R.id.et_password);
        et_username=findViewById(R.id.et_username);
        btn_login=findViewById(R.id.btn_login);
        btn_signup=findViewById(R.id.btn_signup);
        btn_login.setOnClickListener(this);
        btn_signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_login){
            if(et_password.getText().toString().equals(""))return;
            mAuth.signInWithEmailAndPassword(et_username.getText().toString(),
                            et_password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Firebase", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                i.putExtra("id", user.getEmail().split("@")[0]);
                                Log.d("LOGIN", user.getEmail().split("@")[0]);
                                startActivity(i);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.d("Firebase", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else if (v.getId()==R.id.btn_signup) {
            Toast.makeText(getApplicationContext(), "Clicked on the SignUp", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(i);
        }
    }
}