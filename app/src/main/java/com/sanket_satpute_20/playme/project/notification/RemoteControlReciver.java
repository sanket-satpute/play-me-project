package com.sanket_satpute_20.playme.project.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

public class RemoteControlReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context.getApplicationContext(), "EVENT!!", Toast.LENGTH_SHORT).show();
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode()) {
                Toast.makeText(context.getApplicationContext(), "EVENT PLAY", Toast.LENGTH_SHORT).show();
            }
            else if (KeyEvent.KEYCODE_MEDIA_PAUSE == event.getKeyCode()) {
                Toast.makeText(context.getApplicationContext(), "EVENT PAUSE", Toast.LENGTH_SHORT).show();
            }
            else if (KeyEvent.KEYCODE_MEDIA_NEXT == event.getKeyCode()) {
                Toast.makeText(context.getApplicationContext(), "EVENT NEXT", Toast.LENGTH_SHORT).show();
            }
            else if (KeyEvent.KEYCODE_MEDIA_PREVIOUS == event.getKeyCode()) {
                Toast.makeText(context.getApplicationContext(), "EVENT PREVIOUS", Toast.LENGTH_SHORT).show();
            }
            else if (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == event.getKeyCode()) {
                Toast.makeText(context.getApplicationContext(), "EVENT PLAY PAUSE", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
