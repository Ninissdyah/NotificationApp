package com.example.notificationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button notify;
    private NotificationManager mNotificationManager;
    private final static String CHANNEL_ID = "primary-channel";
    private int NOTIFICATION_ID = 0;

    private final static String ACTION_UPDATE_NOTIF = "action-update-notif";
    private NotificationReceiver mReceiver = new NotificationReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notify = findViewById(R.id.notify_btn);

//        assign untuk mendapatkan systemservice
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //ini untuk android versi terbaru, mungkin ada bbrp versi lm yg tidak bs memunculkan fitur ini
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //tipe importance menandakan penting/tidaknya notif
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "app-notification", NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }


        findViewById(R.id.notify_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });

        findViewById(R.id.update_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNotification();
            }
        });

        findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNotificationManager.cancel(NOTIFICATION_ID);
            }
        });

        //memanggil register
        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIF));
    }

    //method update
    private void updateNotification() {
        //akses gambar
        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1);
        //mendapatkan notifnya
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(androidImage).setBigContentTitle("Notification updated!"));
        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

//    buat notif builder nya
    private NotificationCompat.Builder getNotificationBuilder() {

//        nambahin lagi tampilan yg akan muncul ketika notif di klik
        Intent notificationIntent = new Intent(this, MainActivity2.class);
        //memberikan user experience lbh baik yg bekerja di act terttentu
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("New Notification").setContentText("This is content text").setSmallIcon(R.drawable.ic_android).setContentIntent(notificationPendingIntent);

        return notifyBuilder;
    }

    private void sendNotification() {
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIF); //tombol update
        //mendapatkan bc nya
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
//attach notif yg update notif
        notifyBuilder.addAction(R.drawable.ic_android, "Update Notification", updatePendingIntent);

        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

    //        tiap kali me register harus di unregister
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(mReceiver);
//    }

    public class NotificationReceiver extends BroadcastReceiver {
        public NotificationReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ACTION_UPDATE_NOTIF)) {
                updateNotification();
            }
        }


    }
}