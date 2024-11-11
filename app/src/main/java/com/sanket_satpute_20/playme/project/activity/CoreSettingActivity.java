package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.project.enums.ShakeWhich.NEXTPLAYNORMAL;
import static com.sanket_satpute_20.playme.project.enums.ShakeWhich.OPTIONPLAY;
import static com.sanket_satpute_20.playme.project.enums.ShakeWhich.shakeWhichTo;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__ALLOW_HEAD_SET_CONTROLS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__HEADPHONE_LISTENER_ALREADY_STARTED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__PAUSE_WHEN_BLUETOOTH_DISCONNECTED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__PAUSE_WHEN_HEADSET_PLUGGED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__PLAY_WHEN_BLUETOOTH_CONNECTED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__PLAY_WHEN_HEADSET_INSERTED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.most_played_storing_days;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.recent_storing_days;
import static com.sanket_satpute_20.playme.project.service.BackService.AUTOPLAYSONGS;
import static com.sanket_satpute_20.playme.project.service.BackService.CORESETTING;
import static com.sanket_satpute_20.playme.project.service.BackService.FORCE_THRESHOLD;
import static com.sanket_satpute_20.playme.project.service.BackService.GESTUREPLAY;
import static com.sanket_satpute_20.playme.project.service.BackService.SHAKETOPLAY;
import static com.sanket_satpute_20.playme.project.service.BackService.isAutoNextPlayOn;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__IS_GESTURE_PLAY_SONG_ON;
import static com.sanket_satpute_20.playme.project.service.BackService.isShakeOn;
import static com.sanket_satpute_20.playme.project.service.BackService.shakeWhich;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.enums.ShakeWhich;

public class CoreSettingActivity extends AppCompatActivity {

    String toast_for_days = "Days should be in between 0 to 60";

    public static final String SHAKERWHICHSETTING = "SHAKERWHICHSETTING";
    public static final String WHICHSHAKESETTED = "WHICHSHAKESETTED";
    public static final String FORCE_THRESHOLD_SEEK = "FORCE_THRESHOLD_SEEK";
    SharedPreferences shared, preferencesShake;


    ImageView back_pressed, normal_shake_img, option_shake_img, refresh_btn;
    SwitchCompat autoplay, gesture_play, shake_to_play_switch, play_when_plugged_in, pause_when_plugged_out,
            play_when_bluetooth_connect, pause_when_bluetooth_disconnected, headset_control_allow;
    RelativeLayout shake_to_play;
    ConstraintLayout shaker_layout;
    RelativeLayout normal_play_shake, option_play_shake, player_activity, main_activity, lock_and_notification;
    SeekBar force_seekbar;

    EditText recent_s_days_e, most_played_s_days_e;
    int max_days = 60, min_days = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_setting);
        initViews();
        doExtra();
        onclick();

//        force_seekbar.setMax(1050);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkActivity();
    }

    private void initViews() {
        back_pressed = findViewById(R.id.back_pressed);
        autoplay = findViewById(R.id.autoplay);
        gesture_play = findViewById(R.id.gesture_play);
        shake_to_play_switch = findViewById(R.id.shake_to_play_switch);
        play_when_plugged_in = findViewById(R.id.play_when_inserted);
        pause_when_plugged_out = findViewById(R.id.pause_when_plugged);
        play_when_bluetooth_connect = findViewById(R.id.bluetooth_auto_start_device_connected);
        pause_when_bluetooth_disconnected = findViewById(R.id.bluetooth_auto_stop_device_connected);
        headset_control_allow = findViewById(R.id.headset_control_allow);
        shake_to_play = findViewById(R.id.shake_to_play);
        shaker_layout = findViewById(R.id.shaker_layout);
        normal_play_shake = findViewById(R.id.normal_play_shake);
        option_play_shake = findViewById(R.id.option_play_shake);
        normal_shake_img = findViewById(R.id.normal_shake_img);
        option_shake_img = findViewById(R.id.option_shake_img);
        refresh_btn = findViewById(R.id.refresh_btn);
        force_seekbar = findViewById(R.id.shake_force_seekbar);

        player_activity = findViewById(R.id.player_activity_design);
        main_activity = findViewById(R.id.main_activity_design);
        lock_and_notification = findViewById(R.id.lock_screen_and_notify);

        recent_s_days_e = findViewById(R.id.recent_storing_days);
        most_played_s_days_e = findViewById(R.id.most_played_storing_days);
    }

    BluetoothAdapter bluetoothAdapter;
    private void doExtra() {
        if (shared == null)
            shared = getSharedPreferences(CORESETTING, MODE_PRIVATE);
        autoplay.setChecked(shared.getBoolean(AUTOPLAYSONGS, true));

        play_when_plugged_in.setChecked(__PLAY_WHEN_HEADSET_INSERTED);
        pause_when_plugged_out.setChecked(__PAUSE_WHEN_HEADSET_PLUGGED);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            play_when_bluetooth_connect.setEnabled(false);
            pause_when_bluetooth_disconnected.setEnabled(false);
            play_when_bluetooth_connect.setAlpha(0.3f);
            pause_when_bluetooth_disconnected.setAlpha(0.3f);
        } else {
            play_when_bluetooth_connect.setChecked(__PLAY_WHEN_BLUETOOTH_CONNECTED);
            pause_when_bluetooth_disconnected.setChecked(__PAUSE_WHEN_BLUETOOTH_DISCONNECTED);
        }

        headset_control_allow.setChecked(__ALLOW_HEAD_SET_CONTROLS);
    }

    private void onclick() {
        back_pressed.setOnClickListener(view -> onBackPressed());

        autoplay.setOnCheckedChangeListener((compoundButton, b) -> {
            isAutoNextPlayOn = b;
            setWithSharedPref(AUTOPLAYSONGS, b);
        });

        gesture_play.setOnCheckedChangeListener((compoundButton, b) -> {
            __IS_GESTURE_PLAY_SONG_ON = b;
            setWithSharedPref(GESTUREPLAY, b);
            Intent broad_intent = new Intent();
            broad_intent.setAction("action.broadcast.GESTURE_PLAY_UPDATED_AND_RECEIVED");
            LocalBroadcastManager.getInstance(CoreSettingActivity.this).sendBroadcast(broad_intent);
        });

        shake_to_play_switch.setOnCheckedChangeListener((compoundButton, b) -> {
            isShakeOn = b;
            setWithSharedPref(SHAKETOPLAY, b);
            Intent intent = new Intent();
            intent.setAction("action.shake.updated");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            if (isShakeOn) {
                shaker_layout.setVisibility(View.VISIBLE);
            } else {
                shaker_layout.setVisibility(View.GONE);
            }
        });

        play_when_plugged_in.setOnClickListener(v -> {
            __PLAY_WHEN_HEADSET_INSERTED = play_when_plugged_in.isChecked();
            boolean anyOneIsOn = __PLAY_WHEN_HEADSET_INSERTED || __PAUSE_WHEN_HEADSET_PLUGGED ||
                    __PLAY_WHEN_BLUETOOTH_CONNECTED || __PAUSE_WHEN_BLUETOOTH_DISCONNECTED;
            if (anyOneIsOn)
                sendBroadcastToBackgroundServiceOfHeadphonesStart();
            else
                sendBroadcastToBackgroundServiceOfHeadphonesStop();
        });
        pause_when_plugged_out.setOnClickListener(v -> {
            __PAUSE_WHEN_HEADSET_PLUGGED = pause_when_plugged_out.isChecked();
            boolean anyOneIsOn = __PLAY_WHEN_HEADSET_INSERTED || __PAUSE_WHEN_HEADSET_PLUGGED ||
                    __PLAY_WHEN_BLUETOOTH_CONNECTED || __PAUSE_WHEN_BLUETOOTH_DISCONNECTED;
            if (anyOneIsOn)
                sendBroadcastToBackgroundServiceOfHeadphonesStart();
            else
                sendBroadcastToBackgroundServiceOfHeadphonesStop();
        });
        play_when_bluetooth_connect.setOnClickListener(v -> {
            __PLAY_WHEN_BLUETOOTH_CONNECTED = play_when_bluetooth_connect.isChecked();
            boolean anyOneIsOn = __PLAY_WHEN_HEADSET_INSERTED || __PAUSE_WHEN_HEADSET_PLUGGED ||
                    __PLAY_WHEN_BLUETOOTH_CONNECTED || __PAUSE_WHEN_BLUETOOTH_DISCONNECTED;
            if (anyOneIsOn)
                sendBroadcastToBackgroundServiceOfHeadphonesStart();
            else
                sendBroadcastToBackgroundServiceOfHeadphonesStop();
        });
        pause_when_bluetooth_disconnected.setOnClickListener(v -> {
            __PAUSE_WHEN_BLUETOOTH_DISCONNECTED = pause_when_bluetooth_disconnected.isChecked();
            boolean anyOneIsOn = __PLAY_WHEN_HEADSET_INSERTED || __PAUSE_WHEN_HEADSET_PLUGGED ||
                    __PLAY_WHEN_BLUETOOTH_CONNECTED || __PAUSE_WHEN_BLUETOOTH_DISCONNECTED;
            if (anyOneIsOn)
                sendBroadcastToBackgroundServiceOfHeadphonesStart();
            else
                sendBroadcastToBackgroundServiceOfHeadphonesStop();
        });

        headset_control_allow.setOnClickListener(v -> {
            __ALLOW_HEAD_SET_CONTROLS = headset_control_allow.isChecked();
            Intent intent = new Intent();
            intent.setAction("action.from_core_setting_activity_To_BackService.ALLOW_HEAD_SET_CONTROLS.Changed");
            LocalBroadcastManager.getInstance(CoreSettingActivity.this).sendBroadcast(intent);
        });

        force_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                FORCE_THRESHOLD = i;
                setSeekToPref(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        refresh_btn.setOnClickListener(view -> {
            FORCE_THRESHOLD = 350;
            force_seekbar.setProgress(350);
            setSeekToPref(350);
        });

        normal_shake_img.setOnClickListener(view -> {
            normal_shake_img.setBackgroundResource(R.drawable.rect_shape_selected);
            option_shake_img.setBackgroundResource(R.drawable.rect_shape_not_selected);
            setWhichShakeTo(NEXTPLAYNORMAL);
        });

        option_shake_img.setOnClickListener(view -> {
            normal_shake_img.setBackgroundResource(R.drawable.rect_shape_not_selected);
            option_shake_img.setBackgroundResource(R.drawable.rect_shape_selected);
            setWhichShakeTo(OPTIONPLAY);
        });

        player_activity.setOnClickListener(view -> startPlayerActivityDesign());
        main_activity.setOnClickListener(view -> startMainActivityDesign());
        lock_and_notification.setOnClickListener(view -> startLockAndNotification());

        recent_s_days_e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();
                if (!(text.contains(",") || text.contains("."))) {
                    if (text.length() > 0) {
                        int n = Integer.parseInt(text);
                        if (n <= max_days || min_days >= n) {
                            recent_storing_days = n;
                        } else {
                            Toast.makeText(CoreSettingActivity.this, toast_for_days, Toast.LENGTH_SHORT).show();
                            if (n < min_days - 1) {
                                recent_s_days_e.setText(String.valueOf(min_days));
                                recent_storing_days = min_days;
                            } else {
                                recent_s_days_e.setText(String.valueOf(max_days));
                                recent_storing_days = max_days;
                            }
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        recent_s_days_e.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                if (recent_s_days_e.getText().length() <= 0 || (recent_s_days_e.getText().toString().contains(",") || recent_s_days_e.getText().toString().contains("."))) {
                    recent_s_days_e.setText(String.valueOf(min_days));
                    recent_storing_days = min_days;
                }
            }
        });

        most_played_s_days_e.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                if (most_played_s_days_e.getText().length() <= 0 || (most_played_s_days_e.getText().toString().contains(",") || most_played_s_days_e.getText().toString().contains("."))) {
                    most_played_s_days_e.setText(String.valueOf(min_days));
                    most_played_storing_days = min_days;
                }
            }
        });

        most_played_s_days_e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();
                if (!(text.contains(",") || text.contains("."))) {
                    if (text.length() > 0) {
                        int n = Integer.parseInt(text);
                        if (n <= max_days || min_days >= n) {
                            most_played_storing_days = n;
                        } else {
                            Toast.makeText(CoreSettingActivity.this, toast_for_days, Toast.LENGTH_SHORT).show();
                            if (n < min_days - 1) {
                                most_played_s_days_e.setText(String.valueOf(min_days));
                                most_played_storing_days = min_days;
                            } else {
                                most_played_s_days_e.setText(String.valueOf(max_days));
                                most_played_storing_days = max_days;
                            }
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void setWithSharedPref(String obj_name, Boolean value) {
        shared = getSharedPreferences(CORESETTING, MODE_PRIVATE);
        SharedPreferences.Editor preferences = shared.edit();
        preferences.putBoolean(obj_name, value);
        preferences.apply();
    }

    private void checkActivity() {
        autoplay.setChecked(getCheckerActivity(AUTOPLAYSONGS));
        gesture_play.setChecked(getCheckerActivity(GESTUREPLAY));
        shake_to_play_switch.setChecked(getCheckerActivity(SHAKETOPLAY));
        if (getCheckerActivity(SHAKETOPLAY)) {
            shaker_layout.setVisibility(View.VISIBLE);
            if (getWhichShake().equals(NEXTPLAYNORMAL)) {
                normal_shake_img.setBackgroundResource(R.drawable.rect_shape_selected);
                option_shake_img.setBackgroundResource(R.drawable.rect_shape_not_selected);
            } else {
                normal_shake_img.setBackgroundResource(R.drawable.rect_shape_not_selected);
                option_shake_img.setBackgroundResource(R.drawable.rect_shape_selected);
            }
        }
        else
            shaker_layout.setVisibility(View.GONE);

        force_seekbar.setProgress(getSeekToPref());

        recent_s_days_e.setText(String.valueOf(recent_storing_days));
        most_played_s_days_e.setText(String.valueOf(most_played_storing_days));
    }

    private boolean getCheckerActivity(String obj_name) {
        boolean b = obj_name.equals(AUTOPLAYSONGS);
        if (shared == null)
            shared = getSharedPreferences(CORESETTING, MODE_PRIVATE);
        return shared.getBoolean(obj_name, b);
    }

    private void setWhichShakeTo(ShakeWhich shaker) {
        shakeWhich = shaker;
        preferencesShake = getSharedPreferences(SHAKERWHICHSETTING, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesShake.edit();
        editor.putString(WHICHSHAKESETTED, shaker.toString());
        editor.apply();
    }

    private ShakeWhich getWhichShake() {
        if (preferencesShake == null)
            preferencesShake = getSharedPreferences(SHAKERWHICHSETTING, MODE_PRIVATE);
        String result_enum = preferencesShake.getString(WHICHSHAKESETTED, NEXTPLAYNORMAL.toString());
        return shakeWhichTo(result_enum);
    }

    private void setSeekToPref(int progress) {
        if (shared == null)
            shared = getSharedPreferences(CORESETTING, MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(FORCE_THRESHOLD_SEEK, progress);
        editor.apply();
    }

    private int getSeekToPref() {
        int a;
        if (shared == null)
            shared = getSharedPreferences(CORESETTING, MODE_PRIVATE);
        a = shared.getInt(FORCE_THRESHOLD_SEEK, 350);
        return a;
    }

    private void startPlayerActivityDesign() {
        Intent intent = new Intent(CoreSettingActivity.this, PlayerDesignActivity.class);
        startActivity(intent);
    }

    private void startMainActivityDesign() {
        Intent intent = new Intent(CoreSettingActivity.this, MainDesignActivity.class);
        startActivity(intent);
    }

    private void startLockAndNotification() {
        Intent intent = new Intent(CoreSettingActivity.this, LockScreenAndNotificationActivity.class);
        startActivity(intent);
    }

    private void sendBroadcastToBackgroundServiceOfHeadphonesStart() {
        if (!__HEADPHONE_LISTENER_ALREADY_STARTED) {
            Intent broad_intent_for_back_service_to_start_listener = new Intent();
            broad_intent_for_back_service_to_start_listener.setAction("action.broadcast_from_core_settings_activity_TO.BackService.StartEarphoneListener");
            LocalBroadcastManager.getInstance(CoreSettingActivity.this).sendBroadcast(broad_intent_for_back_service_to_start_listener);
        }
    }

    private void sendBroadcastToBackgroundServiceOfHeadphonesStop() {
        if (__HEADPHONE_LISTENER_ALREADY_STARTED) {
            Intent broad_intent_for_back_service_to_stop_listener = new Intent();
            broad_intent_for_back_service_to_stop_listener.setAction("action.broadcast_from_core_settings_activity_TO.BackService.StopEarphoneListener");
            LocalBroadcastManager.getInstance(CoreSettingActivity.this).sendBroadcast(broad_intent_for_back_service_to_stop_listener);
        }
    }
}