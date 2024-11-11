package com.sanket_satpute_20.playme.project.notification;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.NOTIFICATION_ID;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.sanket_satpute_20.playme.project.service.BackService;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

public class MusicPlayerNotificationListener implements PlayerNotificationManager.NotificationListener {

    BackService service;
    Context context;

    public MusicPlayerNotificationListener(BackService service, Context context) {
        this.service = service;
        this.context = context;
    }

    @Override
    public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
        service.stopForeground(true);
        BackService.isForegroundService = false;
    }

    @Override
    public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
        if (ongoing && !BackService.isForegroundService) {
            ContextCompat.startForegroundService(context, new Intent(context, this.getClass()));
            service.startForeground(NOTIFICATION_ID, notification);
            BackService.isForegroundService = true;
        }
    }
}
