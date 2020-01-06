package com.example.android.notificationscheduler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationJobService extends JobService {

    //Notification chanel ID
    public static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    NotificationManager mNotificationManager;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        //Creates a Notification Channel
        createNotificationChannel();


        //Set Up the notification content intent to launch the app when clicked
        Intent contentIntent = new Intent(this, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("Job Service")
                .setContentText("Your Job ran to completion")
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_job_running)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        mNotificationManager.notify(0, builder.build());

        //set return to true so tht the job will be rescheduled if it fails
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }


    /*
     * Creates a Notification Channel for OREO and higher
     */
    public void createNotificationChannel() {

        //define notification manager object
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //Create notification channel for Oreo and higher, so check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //create notification channel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Job Service Notification", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.CYAN);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notifications from 3MenSolution");

            mNotificationManager.createNotificationChannel(notificationChannel);
        }

    }
}
