package com.cmp354.ausvalet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cmp354.ausvalet.databinding.ActivityMainBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    static String id;
    static String first;
    static String last;
    static String number;
    static int points;
    static boolean isAvailable;
    static boolean isCaptain;
    Bundle b;

    FirebaseFirestore db;

    BottomNavigationView bottomNavigationView;
    BottomNavigationItemView frag_home;


    @Override
    protected void onResumeFragments() {

        super.onResumeFragments();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //TODO: Hide Home for Captains (Captains can't book valets)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        id = i.getStringExtra("username");

        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
//                id=user.getId();
                first = user.getFirst();
                last = user.getLast();
                number = user.getNumber();
                isAvailable = user.getAvailable();
                isCaptain = user.getCaptain();
                points = user.getPoints();

                if(isCaptain){
                    Intent i = new Intent(MainActivity.this, CaptainService.class);
                    i.putExtra("captainId",id);
                    ContextCompat.startForegroundService(MainActivity.this,i);
                }

                b=new Bundle();

                b.putString("id",id);
                b.putString("first",first);
                b.putString("last",last);
                b.putString("number",number);
                b.putInt("points",points);
                b.putBoolean("isAvailable",isAvailable);
                b.putBoolean("isCaptain",isCaptain);
                Log.d("daim",b.getString("id")+"in main activity");
//                System.out.println(b.getString("id")+"in main activity");

                binding = ActivityMainBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());

                bottomNavigationView = findViewById(R.id.nav_view);
                bottomNavigationView.setSelectedItemId(R.id.frag_profile);

                frag_home = findViewById(R.id.frag_home);

//        if (isCaptain == true){
//            frag_home.setVisibility(View.VISIBLE);
//        }
//        else{
//            frag_home.setVisibility(View.GONE);
//        }
                Fragment f=new fragment_profile();
                f.setArguments(b);
                replaceFragment(f);



                binding.navView.setOnItemSelectedListener( item -> {
                    switch(item.getItemId()){
                        case R.id.frag_home:
                            Fragment f1=new fragment_home();
                            f1.setArguments(b);
                            replaceFragment(f1);
                            break;
                        case R.id.frag_book:
                            Fragment f2=new fragment_booking();
                            f2.setArguments(b);
                            replaceFragment(f2);
                            break;
                        case R.id.frag_profile:
                            Fragment f3=new fragment_profile();
                            f3.setArguments(b);
                            replaceFragment(f3);
                            break;
                    }
                    return true;
                });

            }
        });
        docRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("daim","failed");
            }
        });






    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

}
