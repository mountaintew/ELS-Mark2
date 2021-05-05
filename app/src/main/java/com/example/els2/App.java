package com.example.els2;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_1_ID = "iceNotif";
    public static final String CHANNEL_2_ID = "locNotif";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "In Case of Emergency",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("In case of Emergency");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Location Services",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setDescription("Location Services");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);

        }
    }
}
