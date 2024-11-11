package com.sanket_satpute_20.playme.project.extra_stuffes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sanket_satpute_20.playme.project.activity.LockScreenActivity;

public class LockScreenBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MMVC", "In Recive");
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON) && false) {
            Log.d("MMVC", "In if");
            Intent fullScreenIntent = new Intent(context, LockScreenActivity.class);
            fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(fullScreenIntent);
        }
    }
}
