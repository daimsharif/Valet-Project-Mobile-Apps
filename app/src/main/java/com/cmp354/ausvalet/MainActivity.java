package com.cmp354.ausvalet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
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
    static boolean isAvailable;
    static boolean isCaptain;

    FirebaseFirestore db;




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
                first = user.getFirst();
                last = user.getLast();
                number = user.getNumber();
                isAvailable = user.isAvailable();
                isCaptain = user.getCaptain();
            }
        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new fragment_home());

        binding.navView.setOnItemSelectedListener( item -> {
            switch(item.getItemId()){
                case R.id.frag_home:
                    replaceFragment(new fragment_home());
                    break;
                case R.id.frag_book:
                    replaceFragment(new fragment_booking());
                    break;
                case R.id.frag_profile:
                    replaceFragment(new fragment_profile());
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

}
