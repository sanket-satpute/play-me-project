package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.NOTIFICATION_TYPE;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__LOCK_SCREEN_PLAY_LISTENER_ON;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.sanket_satpute_20.playme.R;

public class LockScreenAndNotificationActivity extends AppCompatActivity {

    int REQUEST_OVERLAY_PERMISSION = 1503;

    ImageView back_pressed;
    RelativeLayout use_retro_notification_relative, use_standard_notification_relative;
    MaterialRadioButton use_retro_notification_radio_btn, use_standard_notification_radio_btn;
    TextView use_retro_notification_txt, use_standard_notification_txt, lock_screen_helper_txt;
    SwitchCompat lock_screen_play_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen_and_notification);
        initViews();
        onClick();
        doExtra();
    }

    private void initViews() {
        back_pressed = findViewById(R.id.back_pressed);
        use_retro_notification_relative = findViewById(R.id.use_retro_notification_relative);
        use_standard_notification_relative = findViewById(R.id.use_standard_notification_relative);
        use_retro_notification_radio_btn = findViewById(R.id.use_retro_notification_radio);
        use_standard_notification_radio_btn = findViewById(R.id.use_standard_notification_radio);
        use_retro_notification_txt = findViewById(R.id.use_retro_notification_txt);
        use_standard_notification_txt = findViewById(R.id.use_standard_notification_txt);
        lock_screen_play_switch = findViewById(R.id.lock_screen_play_switch);
        lock_screen_helper_txt = findViewById(R.id.lock_screen_helper_txt);
    }

    private void onClick() {
        back_pressed.setOnClickListener(view -> onBackPressed());
        use_retro_notification_relative.setOnClickListener(view -> setNotificationSelector(false, true));
        use_standard_notification_relative.setOnClickListener(view -> setNotificationSelector(true, true));
        use_retro_notification_radio_btn.setOnClickListener(view -> setNotificationSelector(false, true));
        use_standard_notification_radio_btn.setOnClickListener(view -> setNotificationSelector(true, true));
        use_retro_notification_txt.setOnClickListener(view -> setNotificationSelector(false, true));
        use_standard_notification_txt.setOnClickListener(view -> setNotificationSelector(true, true));

        lock_screen_play_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                startOverlayRequest();
            } else {
                lockScreenOperationChanged(isChecked, false);
            }
        });
    }

    private void doExtra() {
        setNotificationSelector(NOTIFICATION_TYPE.equals("NEW"), false);
        lockScreenOperationChanged(__LOCK_SCREEN_PLAY_LISTENER_ON, true);
    }

    private void startOverlayRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            ((Activity) this).startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                // Permission granted, proceed with displaying the lock screen overlay
                lockScreenOperationChanged(true, false);
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                // Permission denied, handle the situation accordingly (e.g., show a message)
                lockScreenOperationChanged(false, true);
            }
        }
    }

    private void setNotificationSelector(boolean isStandardNotification, boolean isStarter) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (isStandardNotification) {
                use_retro_notification_radio_btn.setChecked(false);
                use_standard_notification_radio_btn.setChecked(true);
                use_retro_notification_txt.setAlpha(0.6f);
                use_standard_notification_txt.setAlpha(1f);
                NOTIFICATION_TYPE = "NEW";
            } else {
                use_retro_notification_radio_btn.setChecked(true);
                use_standard_notification_radio_btn.setChecked(false);
                use_retro_notification_txt.setAlpha(1f);
                use_standard_notification_txt.setAlpha(0.6f);
                NOTIFICATION_TYPE = "OLD";
            }
        } else {
            use_retro_notification_radio_btn.setChecked(true);
            use_standard_notification_radio_btn.setChecked(false);
            use_retro_notification_txt.setAlpha(1f);
            use_standard_notification_txt.setAlpha(0.6f);
            use_retro_notification_txt.setEnabled(false);
            use_retro_notification_radio_btn.setEnabled(false);
            use_retro_notification_relative.setEnabled(false);
            NOTIFICATION_TYPE = "OLD";
        }

        if (isStarter) {
            Intent intent_changed_notification = new Intent();
            intent_changed_notification.setAction("action.NOTIFICATION_ACTION_CHANGED.from_lock_screen_and_notification_activity");
            LocalBroadcastManager.getInstance(LockScreenAndNotificationActivity.this).sendBroadcast(intent_changed_notification);
        }
    }

    private void lockScreenOperationChanged(boolean isOn, boolean isStarter) {
        if (isOn) {
            lock_screen_play_switch.setChecked(true);
            lock_screen_helper_txt.setVisibility(View.VISIBLE);
        } else {
            lock_screen_play_switch.setChecked(false);
            lock_screen_helper_txt.setVisibility(View.GONE);
        }
        if (!isStarter) {
            __LOCK_SCREEN_PLAY_LISTENER_ON = isOn;
            Intent changed_lock_screen_functionality = new Intent();
            changed_lock_screen_functionality.setAction("action.LOCK_SCREEN_PLAY_LISTENER_CHANGED.from_LockScreenAndNotificationActivity");
            LocalBroadcastManager.getInstance(LockScreenAndNotificationActivity.this).sendBroadcast(changed_lock_screen_functionality);
        }
    }
}