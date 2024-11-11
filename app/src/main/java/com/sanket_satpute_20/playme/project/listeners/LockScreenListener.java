package com.sanket_satpute_20.playme.project.listeners;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__LOCK_SCREEN_PLAY_LISTENER_ON;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.util.Log;
import android.view.Display;

import com.sanket_satpute_20.playme.project.activity.LockScreenActivity;

public class LockScreenListener implements DisplayManager.DisplayListener {

    Context context;
    DisplayManager displayManager;

    public LockScreenListener(Context context) {
        this.context = context;
        displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
    }

    public boolean registerListener() {
        if (displayManager == null)
            displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        if (displayManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                displayManager.registerDisplayListener(this, null);
                return true;
            } else {
                // For API level 29 and 30, there's a deprecated method, but it will still work.
//                displayManager.registerDisplayListener(this, null);       it may not supported that's why do work repeatedly
                return false;
            }
        }
        return false;
    }

    public void unregisterListener() {
        if (displayManager == null)
            displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        if (displayManager != null) {
            displayManager.unregisterDisplayListener(this);
        }
    }

    @Override
    public void onDisplayAdded(int displayId) {

    }

    @Override
    public void onDisplayRemoved(int displayId) {

    }

    @Override
    public void onDisplayChanged(int displayId) {
        if (displayId == Display.DEFAULT_DISPLAY) {
            DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            if (displayManager != null) {
                Display display = displayManager.getDisplay(displayId);
                if (display.getState() == Display.STATE_ON && __LOCK_SCREEN_PLAY_LISTENER_ON) {
                    Intent lockScreenIntent = new Intent(context, LockScreenActivity.class);
                    lockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(lockScreenIntent);
                } else if (display.getState() == Display.STATE_OFF) {
                    Log.d("h20", "Locked : off");
                    // Screen is turned off
                    // Add your desired logic here
                }
            }
        }
    }
}
