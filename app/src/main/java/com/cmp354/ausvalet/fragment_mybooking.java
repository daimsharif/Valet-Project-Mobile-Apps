package com.cmp354.ausvalet;

import android.content.Intent;
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
 * Use the {@link fragment_mybooking#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_mybooking extends Fragment implements View.OnClickListener{
    TextView tv_bk_title,tv_bk_info,tv_bk_stat;
    Button btn_dropped,btn_loc,btn_clear;
    FirebaseFirestore db;
    String id;
    String first;
    String last;
    String number;
    int points;
    boolean isCaptain;
    boolean isAvailable;
    Request req;
    User customer;
    Boolean isParked=false;

    Car car;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_mybooking() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_mybooking.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_mybooking newInstance(String param1, String param2) {
        fragment_mybooking fragment = new fragment_mybooking();
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
        return inflater.inflate(R.layout.fragment_mybooking, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_bk_info=view.findViewById(R.id.tv_bk_info);
        tv_bk_stat=view.findViewById(R.id.tv_bk_stat);
        tv_bk_title=view.findViewById(R.id.tv_bk_title);
        btn_clear=view.findViewById(R.id.btn_clear);
        btn_dropped=view.findViewById(R.id.btn_dropped);
        btn_loc =view.findViewById(R.id.btn_loc);
        btn_loc.setOnClickListener(this);
        btn_dropped.setOnClickListener(this);
        btn_clear.setOnClickListener(this);

        tv_bk_title.setText("No Current Bookings :(");
        tv_bk_stat.setVisibility(View.GONE);
        tv_bk_info.setVisibility(View.GONE);
        btn_clear.setVisibility(View.GONE);
        btn_dropped.setVisibility(View.GONE);
        btn_loc.setVisibility(View.GONE);


        id = getArguments().getString("id");
        first =getArguments().getString("first");
        last = getArguments().getString("last");
        number = getArguments().getString("number");
        points = getArguments().getInt("points");
        isAvailable = getArguments().getBoolean("isAvailable");
        isCaptain = getArguments().getBoolean("isCaptain");

        db= FirebaseFirestore.getInstance();
        //TODO view only if there is a accepted booking
        db.collection("requests")
                .whereEqualTo("customerId", id)
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
                            if (doc.get("status").equals("accepted")&&doc.get("isDropped").equals(false)) {
                                //display and make buttons available
                                req=doc.toObject(Request.class);
                                //TODO display
                                db=FirebaseFirestore.getInstance();
                                Log.d("daimtest",req.getCustomerId());
                                db.collection("users")
                                        .whereEqualTo("id", req.getCaptainId())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        customer=document.toObject(User.class);
                                                        String str=tv_bk_info.getText().toString()+"Captain Details:\n"+customer.toString()+"Parking Details:\n"+req.toString();
                                                        tv_bk_info.setText(str);
                                                    }
                                                } else {
                                                    Log.d("daimtest", "Error getting user documents: ", task.getException());
                                                }
                                            }
                                        });


                                tv_bk_title.setText("Current Booking");
                                tv_bk_info.setVisibility(View.VISIBLE);
                                btn_dropped.setVisibility(View.VISIBLE);
//                                btn_decline.setVisibility(View.VISIBLE);

                            }

                        }
                    }
                });

        db.collection("requests")
//                .whereEqualTo("captainId", req.getCaptainId())
                .whereEqualTo("customerId",id)
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
                                                 if (doc.get("status").equals("parked")) {
                                                     Log.d("daim","car dropped");
                                                     isParked=true;
                                                     btn_loc.setVisibility(View.VISIBLE);
                                                     btn_clear.setVisibility(View.VISIBLE);


                                                 }
                                             }

                                         }
                                     }
                );
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_dropped){
            updateStatus();
        }if(v.getId()==R.id.btn_loc){
            //TODO abdu display final car destination
            Intent i=new Intent(getActivity(),MapsActivity.class);
            i.putExtra("captainId",req.getCaptainId());
            startActivity(i);
        }if(v.getId()==R.id.btn_clear){
            clearScreen();
        }

    }

    private void clearScreen() {
        db=FirebaseFirestore.getInstance();
        Log.d("daimtest",req.getCustomerId());
        db.collection("requests")
                .whereEqualTo("captainId", req.getCaptainId())
                .whereEqualTo("customerId", req.getCustomerId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                if(doc.get("status").equals("parked")){
                                    Log.d("daim","Car is parked!");
                                    new CountDownTimer(2000, 1000) {
                                        public void onFinish() {
                                            //let 2sec finish
                                        }

                                        public void onTick(long millisUntilFinished) {
                                            db.collection("requests").document(doc.getId())
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("daim", "request successfully deleted!");
                                                            tv_bk_title.setText("No Current Bookings :(");
                                                            tv_bk_stat.setVisibility(View.GONE);
                                                            tv_bk_info.setVisibility(View.GONE);
                                                            btn_clear.setVisibility(View.GONE);
                                                            btn_dropped.setVisibility(View.GONE);
                                                            btn_loc.setVisibility(View.GONE);
                                                            MainActivity.canBook=true;

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("daim", "Error deleting request", e);
                                                        }
                                                    });
                                        }
                                    }.start();

                                }
                            }
                        } else {
                            Log.d("daimtest", "Error getting user documents: ", task.getException());
                        }
                    }
                });
    }

    private void updateStatus() {
        db.collection("requests")
                .whereEqualTo("captainId", req.getCaptainId())//TODO change it to isCaptain
                .whereEqualTo("customerId", req.getCustomerId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference ref=db.collection("requests").document(document.getId());
                                ref.update("isDropped",true)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //TODO implement
                                                btn_dropped.setVisibility(View.GONE);
                                                tv_bk_stat.setText("Your captain is currently parking the car!");
                                                tv_bk_stat.setVisibility(View.VISIBLE);
                                            }
                                        });


                            }
                        }

                    }
                });
    }
}