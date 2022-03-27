package com.smartcitytraveller.mobile.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.smartcitytraveller.mobile.MainActivity;
import com.smartcitytraveller.mobile.R;
import com.smartcitytraveller.mobile.api.dto.ProfileDto;
import com.smartcitytraveller.mobile.database.DbHandler;
import com.smartcitytraveller.mobile.database.FirestoreHandler;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class FirebaseMessageReceiver extends FirebaseMessagingService {
    private static final String TAG = FirebaseMessageReceiver.class.getSimpleName();

    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            try {
                DbHandler dbHandler = new DbHandler(getBaseContext());
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getData());
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        ProfileDto profileDTO = sharedPreferencesManager.getProfile();
        FirestoreHandler firestoreHandler = new FirestoreHandler();
        if (profileDTO.getId() != null) {
            firestoreHandler.saveFCMToken(profileDTO.getId(), token);
        }
    }

    // Method to display the notifications
    public void showNotification(String title, String message, Map<String, String> data) {
        // Pass the intent to switch to the MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        // Assign channel ID
        String channel_id = "notification_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.drawable.notifications)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000,
                        1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        builder = builder.setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.notifications);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "notification_channel", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }
}