package com.cmp354.ausvalet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_captain_home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_captain_home extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    static FirebaseFirestore db;
    static String id;
    String first;
    String last;
    String number;
    int points;
    boolean isCaptain;
    boolean isAvailable;

    //TODO: FOR DAIM, try to get the status for dropped
    boolean isDropped = false;

    static User customer;
    Request req;

    Car car;

    static TextView tv_requestNotice;
    static TextView tv_info;
    static Button btn_accept;
    static Button btn_continue;
    static Button btn_decline;

    String parkingLocation;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_captain_home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_captain_home.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_captain_home newInstance(String param1, String param2) {
        fragment_captain_home fragment = new fragment_captain_home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
        return inflater.inflate(R.layout.fragment_captain_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_requestNotice=view.findViewById(R.id.tv_requestNotice);
        tv_info=view.findViewById(R.id.tv_info);
        btn_accept=view.findViewById(R.id.btn_accept);
        btn_decline=view.findViewById(R.id.btn_decline);
        btn_continue=view.findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(this);
        btn_accept.setOnClickListener(this);
        btn_decline.setOnClickListener(this);
        tv_requestNotice.setText("No Current Requests :(");
        tv_info.setVisibility(View.GONE);
        btn_accept.setVisibility(View.GONE);
        btn_decline.setVisibility(View.GONE);
        btn_continue.setVisibility(View.GONE);


        id = getArguments().getString("id");
        first =getArguments().getString("first");
        last = getArguments().getString("last");
        number = getArguments().getString("number");
        points = getArguments().getInt("points");
        isAvailable = getArguments().getBoolean("isAvailable");
        isCaptain = getArguments().getBoolean("isCaptain");

        db=FirebaseFirestore.getInstance();
        //TODO when customer book make is available false
        db.collection("requests")
                .whereEqualTo("captainId", id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("daim", "Listen failed.", e);
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
//                            Log.d("daim",doc.toString());
                            if (doc.get("status").equals("requested")) {
                                //display and make buttons available
                                req=doc.toObject(Request.class);
                                //TODO display
                                db=FirebaseFirestore.getInstance();
                                Log.d("daimtest",req.getCustomerId());
                                db.collection("users")
                                        .whereEqualTo("id", req.getCustomerId())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        customer=document.toObject(User.class);
                                                        String str=tv_info.getText().toString()+customer.toString()+req.toString();
                                                        tv_info.setText(str);
                                                    }
                                                } else {
                                                    Log.d("daimtest", "Error getting user documents: ", task.getException());
                                                }
                                            }
                                        });

                                db.collection("cars")
                                        .whereEqualTo("id", req.getCustomerId())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        car=document.toObject(Car.class);
                                                        String str=tv_info.getText().toString()+car.toString();
                                                        tv_info.setText(str);
                                                    }
                                                } else {
                                                    Log.d("daimtest", "Error getting car documents: ", task.getException());
                                                    Log.d("daimtest", "Error getting car documents: ", task.getException());
                                                }
                                            }
                                        });





                                tv_requestNotice.setText("View Request");
                                tv_info.setVisibility(View.VISIBLE);
                                btn_accept.setVisibility(View.VISIBLE);
                                btn_decline.setVisibility(View.VISIBLE);
//                                btn_continue.setVisibility(View.VISIBLE);






                            }

                        }
                    }
                });

        db.collection("requests")
                .whereEqualTo("captainId", id)
//                .whereEqualTo("customerId",customer.getId())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                         @Override
                                         public void onEvent(@Nullable QuerySnapshot value,
                                                             @Nullable FirebaseFirestoreException e) {
                                             if (e != null) {
                                                 Log.w("daim", "Listen failed.", e);
                                                 return;
                                             }
                                             for (QueryDocumentSnapshot doc : value) {

                                                 Log.d("daim",doc.toString());
                                                 if (doc.get("isDropped").equals(true)) {
                                                     Log.d("daim","car dropped");
                                                     isDropped=true;


                                                 }
                                             }

                                         }
                                     }
                );

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btn_accept){//accept
            accept();
        }else if(v.getId()==R.id.btn_decline){//decline
            decline();
        }else{//btn_continue

            //TODO FOR ABDU
            if(isDropped == true){
                Intent i = new Intent(getActivity().getApplicationContext(), InstructActivity.class);
                i.putExtra("iText" , "Drive to the " + req.getParkingLocation());
                i.putExtra("btnText", "I have parked the car");
                startActivity(i);

                //TODO: Once
            }else{
                Toast.makeText(getActivity(),"Car is not at dropOff location",Toast.LENGTH_SHORT).show();
            }


        }

    }

    public void accept(){
        //TODO display gps button
        db.collection("requests")
                .whereEqualTo("captainId", id)//TODO change it to isCaptain
                .whereEqualTo("customerId", customer.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference ref=db.collection("requests").document(document.getId());
                                ref.update("status","accepted")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //TODO implement
                                                Log.d("daim","request accepted in captain fragment");
                                                btn_accept.setVisibility(View.GONE);
                                                btn_decline.setVisibility(View.GONE);
                                                btn_continue.setVisibility(View.VISIBLE);


                                            }
                                        });


                            }
                        }

                    }
                });
    }
    public static void decline(){


        //TODO remove view
        db.collection("requests")
                .whereEqualTo("captainId", id)//TODO change it to isCaptain
                .whereEqualTo("customerId", customer.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference ref=db.collection("requests").document(document.getId());
                                ref.update("status","declined")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //TODO implement
                                                Log.d("daim","request declined in captain fragment");
                                                tv_requestNotice.setText("No current Requests :(");
                                                tv_info.setVisibility(View.GONE);
                                                btn_accept.setVisibility(View.GONE);
                                                btn_decline.setVisibility(View.GONE);
                                                btn_continue.setVisibility(View.GONE);

                                                DocumentReference ref=db.collection("users").document(id);
                                                ref.update("available",true)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Log.d("daim","captain is available");
                                                            }
                                                        });


                                            }
                                        });


                            }
                        }

                    }
                });
    }


    public static void parked(){


        //TODO remove view
        db.collection("requests")
                .whereEqualTo("captainId", id)//TODO change it to isCaptain
                .whereEqualTo("customerId", customer.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference ref=db.collection("requests").document(document.getId());
                                ref.update("status","parked")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //TODO implement
                                                Log.d("daim","request parked in captain fragment");
                                                tv_requestNotice.setText("No current Requests :(");
                                                tv_info.setVisibility(View.GONE);
                                                btn_accept.setVisibility(View.GONE);
                                                btn_decline.setVisibility(View.GONE);
                                                btn_continue.setVisibility(View.GONE);
                                                DocumentReference ref=db.collection("users").document(id);
                                                ref.update("available",true)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Log.d("daim","captain is available");
                                                            }
                                                        });


                                            }
                                        });


                            }
                        }

                    }
                });
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        SharedPreferences sharedPref = getActivity().getSharedPreferences("sp_frag_cap", getContext().MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        // Save variables to SharedPreferences
//        editor.putString("id", id);
//        editor.putString("first", first);
//        editor.putString("last", last);
//        editor.putString("number", number);
//        editor.putInt("points", points);
//        editor.putBoolean("isCaptain", isCaptain);
//        editor.putBoolean("isAvailable", isAvailable);
//        editor.putBoolean("isDropped", isDropped);
//        editor.putString("tv_info",tv_info.getText().toString());
//        editor.putString("tv_requestNotice",tv_requestNotice.getText().toString());
//        editor.putInt("tv_info_display",tv_info.getVisibility());
//        editor.putInt("btn_accept_display",btn_accept.getVisibility());
//        editor.putInt("btn_decline_display",btn_decline.getVisibility());
//        editor.putInt("btn_continue_display",btn_continue.getVisibility());
//
//
//        // Add other variables as needed
//        editor.apply();
//
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.d("daim","onresume is called");
//        SharedPreferences sharedPref = getActivity().getSharedPreferences("sp_frag_cap", getContext().MODE_PRIVATE);
//        // Load variables from SharedPreferences
//        id = sharedPref.getString("id", "");
//        first = sharedPref.getString("first", "");
//        last = sharedPref.getString("last", "");
//        number = sharedPref.getString("number", "");
//        points = sharedPref.getInt("points", 0);
//        isCaptain = sharedPref.getBoolean("isCaptain", false);
//        isAvailable = sharedPref.getBoolean("isAvailable", false);
////        isDropped = sharedPref.getBoolean("isDropped", false);
////        tv_info.setText(sharedPref.getString("tv_info", ""));
//        tv_requestNotice.setText(sharedPref.getString("tv_requestNotice", "-1"));
//        tv_info.setVisibility(sharedPref.getInt("tv_info_display", 0));
//        btn_accept.setVisibility(sharedPref.getInt("btn_accept_display", 0));
//        btn_decline.setVisibility(sharedPref.getInt("btn_decline_display", 0));
//        btn_continue.setVisibility(sharedPref.getInt("btn_continue_display", 0));
//    }
}