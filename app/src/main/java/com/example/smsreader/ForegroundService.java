package com.example.smsreader;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ForegroundService extends Service{

    private static final String TAG = "ForegroundService";
    public static final String MAIN_SERVICE_NOTIFICATION_CHANNEL_ID = "my_foreground_service_channel_id";
    public static final int MAIN_SERVICE_NOTIFICATION_ID = 375740436;
    public IBinder mLocalBinder;
    private NotificationManager mNotificationManager;

    private SmsReceiver smsReceiver;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mLocalBinder = new ServiceBinder(this);
        return mLocalBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        smsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    MAIN_SERVICE_NOTIFICATION_CHANNEL_ID,
                    "MAIN_SERVICE_NOTIFICATION_CHANNEL_ID",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        Notification mainServiceNotification = new NotificationCompat.Builder(getApplicationContext(), MAIN_SERVICE_NOTIFICATION_CHANNEL_ID)
                .setContentTitle("SMS Reader") // TODO WRITE TEXT ACCORDING TO YOUR NEED
                .setContentText("Checking For New SMS...") // TODO WRITE TEXT ACCORDING TO YOUR NEED
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .build();

        startForeground(MAIN_SERVICE_NOTIFICATION_ID, mainServiceNotification);

        return START_STICKY;
    }
}
