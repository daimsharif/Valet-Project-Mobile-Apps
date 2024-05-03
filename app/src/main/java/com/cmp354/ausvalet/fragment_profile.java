package com.cmp354.ausvalet;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_profile extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_profile.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_profile newInstance(String param1, String param2) {
        fragment_profile fragment = new fragment_profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    TextView tv_dispName;
    TextView tv_dispID;
    TextView tv_dispPhone;
    TextView tv_dispEmail;
    TextView tv_profAvaliable;

    Button btn_profCar;
    Button btn_capDetails;

    Switch sw_available;

    String id;
    String first;
    String last;
    String number;
    boolean isCaptain;
    boolean isAvailable;

    FirebaseFirestore db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tv_dispEmail = getView().findViewById(R.id.tv_dispEmail);
        tv_dispID = getView().findViewById(R.id.tv_dispID);
        tv_dispPhone = getView().findViewById(R.id.tv_dispPhone);
        tv_dispName = getView().findViewById(R.id.tv_dispName);
        tv_profAvaliable = getView().findViewById(R.id.tv_profAvaliable);

        btn_capDetails = getView().findViewById(R.id.btn_capDetails);
        btn_profCar = getView().findViewById(R.id.btn_profCar);

        sw_available = getView().findViewById(R.id.sw_avaliable);

        id = MainActivity.id;
        first = MainActivity.first;
        last = MainActivity.last;
        number = MainActivity.number;
        isAvailable = MainActivity.isAvailable;
        isCaptain = MainActivity.isCaptain;

        tv_dispEmail.setText(id + "@aus.edu");
        tv_dispID.setText(id + "");
        tv_dispPhone.setText(number + "");
        tv_dispName.setText(first + " " + last);

        if (isCaptain == true){
            Toast.makeText(getActivity().getApplicationContext(), "He's a captain!", Toast.LENGTH_SHORT).show();
            btn_profCar.setVisibility(View.GONE);
            sw_available.setChecked(isAvailable);
            btn_capDetails.setVisibility(View.VISIBLE);
            sw_available.setVisibility(View.VISIBLE);
            tv_profAvaliable.setVisibility(View.VISIBLE);
        }else{
            btn_profCar.setVisibility(View.VISIBLE);
            btn_capDetails.setVisibility(View.GONE);
            sw_available.setVisibility(View.GONE);
            tv_profAvaliable.setVisibility(View.GONE);
        }

        btn_capDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), CaptainDetails.class);
                i.putExtra("username", id);
                startActivity(i);
            }
        });

        btn_profCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), AddCar.class);
                i.putExtra("username", id);
                startActivity(i);
            }
        });

        sw_available.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Switch has been switched!", Toast.LENGTH_SHORT).show();

                String documentId = id;
                String collectionPath = "users";

                DocumentReference docRef = FirebaseFirestore.getInstance()
                        .collection(collectionPath)
                        .document(documentId);

                Map<String, Object> updates = new HashMap<>();
                updates.put("available", sw_available.isChecked());

                docRef.update(updates)
                        .addOnSuccessListener(aVoid -> {
                            // Update successful
                        })
                        .addOnFailureListener(e -> {
                            // Update failed
                        });

            }
        });






    }

}