package com.sanket_satpute_20.playme.project.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sanket_satpute_20.playme.project.service.BackService;

public class NotificationReceiver extends BroadcastReceiver {
    public static String ACTION = "ACTION";
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent serviceIntent = new Intent();
        serviceIntent.setAction("notify.changed.Song");
        switch (intent.getAction()) {
            case "ACTION_PREVIOUS":
                serviceIntent.putExtra(ACTION, "previous");
                break;
            case "ACTION_PLAY":
                serviceIntent.putExtra(ACTION, "play");
                break;
            case "ACTION_NEXT":
                serviceIntent.putExtra(ACTION, "next");
                break;
        }
        BackService back = new BackService();
        if (back.audioManager != null) {
            back.audioManager.requestAudioFocus(back.focusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(serviceIntent);
    }
}
