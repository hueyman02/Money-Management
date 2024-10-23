package com.example.moneymanagement.Model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.moneymanagement.R;


public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if the app has the required permission
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, proceed with posting the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminder_channel_id")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Reminder")
                    .setContentText("Your reminder message here")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1, builder.build());
        } else {
            // Permission is not granted, handle the case accordingly
            // For example, show a message to the user or take appropriate action
            Toast.makeText(context, "Permission required to post notifications", Toast.LENGTH_SHORT).show();
        }
    }
}

