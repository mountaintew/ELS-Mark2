package com.example.els2;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackgroundService  extends Service {

    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // do your jobs here
        return super.onStartCommand(intent, flags, startId);
    }

}
