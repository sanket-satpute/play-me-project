package com.sanket_satpute_20.playme.project.application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

public class MyApp extends Application {

    private static boolean isApplicationDestroyed = true;

    @Override
    public void onCreate() {
        super.onCreate();

        isApplicationDestroyed = false;

        NotificationManager mgr =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
                mgr.getNotificationChannel(getApplicationContext().getPackageName())==null) {
            mgr.createNotificationChannel(new NotificationChannel(getApplicationContext().getPackageName(),
                    "My Foreground Service", NotificationManager.IMPORTANCE_DEFAULT));
        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d("mvcpg", "terminated");
        isApplicationDestroyed = true;
//        stopService(gestureServiceIntent);
    }

    public static boolean getApplicationState() {
        return isApplicationDestroyed;
    }
}
