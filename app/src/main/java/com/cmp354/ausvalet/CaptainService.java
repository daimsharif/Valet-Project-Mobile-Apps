package com.cmp354.ausvalet;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CaptainService extends Service {
    public CaptainService() {
    }

    private Timer timer;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    FirebaseFirestore db;
    String captainId;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("daim","captain service started");
        captainId=intent.getStringExtra("captainId");
        db=FirebaseFirestore.getInstance();



        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,"Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);

        Intent notificationIntent = new Intent(this, Login.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(
                this, CHANNEL_ID)
                .setContentTitle("Waiting for your next booking")
                .setContentText("waiting for customer to book..")
                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentIntent(pendingIntent)
                .setOngoing(true) //sticky notification
                .build();

        startForeground(1, notification);

        db.collection("requests")
                .whereEqualTo("captainId", captainId)
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
                            if (doc.get("status").equals("requested")) {
                                Log.d("daim","request found");
                                createStatusNotification("You Have been Booked..","Open the app to Accept or Reject");
                                new CountDownTimer(5000,1000){

                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        Log.d("daim","count down finished");
                                    }
                                };
                                stopForeground(true);
                                stopSelf();
                            }

                        }
                    }
                });





        return START_REDELIVER_INTENT;
    }
    public void createStatusNotification(String title,String text){
        Log.d("daim",title+"notification created");
        // create the intent for the notification
        Intent notificationIntent = new Intent(this, Login.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // create the pending intent
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 100, notificationIntent, flags);

        // create the variables for the notification
        int icon = R.drawable.ic_launcher_background;
        CharSequence tickerText = title;
        CharSequence contentTitle = getText(R.string.app_name);
        CharSequence contentText = text;
        NotificationChannel notificationChannel =
                new NotificationChannel("22", "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager manager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);


        // create the notification and set its data
        Notification notification = new NotificationCompat
                .Builder(this, "22")
                .setSmallIcon(icon)
                .setTicker(tickerText)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setChannelId("22")
                .build();

        final int NOTIFICATION_ID = 10;
        manager.notify(NOTIFICATION_ID, notification);

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //----------------------------------------------------------------------------------------------


    @Override
    public void onDestroy() {
        Log.d("CMP354", "Captain Service destroyed, and timer stopped !");

    }
}