package com.sanket_satpute_20.playme.project.account.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.sanket_satpute_20.playme.MainActivity;
import com.sanket_satpute_20.playme.R;

public class AdWatchAfterTimeIntentService extends Service {
// write firebase code to get time from firebase
    protected CountDownTimer timer;
    private long remain_time = 5000;

    public static final String AD_WATCH_DELAY_BETWEEN_TWO_AD_KEY = "adWatchDelayBetweenTwoAd";

    FirebaseRemoteConfig f_config;

    public static boolean isAdWatchAfterServiceRunning = false;

    private final AdWatchAfterTimeBinder binder = new AdWatchAfterTimeBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        f_config = FirebaseRemoteConfig.getInstance();
        Long delay_time_in_millis = f_config.getLong(AD_WATCH_DELAY_BETWEEN_TWO_AD_KEY);
        timer = new CountDownTimer(Math.max(30000, delay_time_in_millis), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                isAdWatchAfterServiceRunning = true;
                remain_time = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                isAdWatchAfterServiceRunning = false;
                taskIsCompleted();
                AdWatchAfterTimeIntentService.this.stopSelf();
            }
        };
        timer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isAdWatchAfterServiceRunning = false;
    }

    private void taskIsCompleted() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction("open.setting.fragment");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(intent);

        final int flag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, flag);

        String long_text = "Great news! Time is up. You can watch the next ad and earn money. By watching ads," +
                " you help us keep our app free for everyone. Thank you for supporting us and enjoy your reward!";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, String.valueOf(82))
                .setSmallIcon(R.drawable.triangle_app_logo_notification)
                .setContentTitle("Time's up! Watch an ad and earn money")
                .setContentText(long_text)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setChannelId(getApplicationContext().getPackageName());

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText(long_text)
                .setSummaryText("Time's up!");
        builder.setStyle(bigTextStyle);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(81, builder.build());
    }

    public long getRemainTime() {
        return remain_time;
    }

    //    service related
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class AdWatchAfterTimeBinder extends Binder {
        public AdWatchAfterTimeIntentService getService()
        {
            return AdWatchAfterTimeIntentService.this;
        }
    }
}