package com.sanket_satpute_20.playme.project.activity;


import static com.sanket_satpute_20.playme.MainActivity.isPermissionGranted;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.REQUEST_CODE;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sanket_satpute_20.playme.MainActivity;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.service.AppStarterIntentService;

public class WelcomeActivity extends AppCompatActivity {

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals("all_loaded.Start_Second.Activity")) {
                    Handler handler = new Handler();
                    handler.postDelayed(() -> startAnotherActivity(), 0);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        permission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("all_loaded.Start_Second.Activity");
        LocalBroadcastManager.getInstance(WelcomeActivity.this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(WelcomeActivity.this).unregisterReceiver(receiver);
    }

    private void permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE ) !=
                PackageManager.PERMISSION_GRANTED)  ||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE ) !=
                        PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO ) !=
                        PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_SETTINGS ) !=
                        PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WAKE_LOCK ) !=
                        PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_SETTINGS,
                            Manifest.permission.WAKE_LOCK},
                    REQUEST_CODE);
        } else {
            Log.d("acp", "Welcome 1");
            isPermissionGranted = true;
            Intent starter_service = new Intent(WelcomeActivity.this, AppStarterIntentService.class);
            starter_service.putExtra("PERMISSION_GRANTED", isPermissionGranted);
            startService(starter_service);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0)
        {
            if((grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    || (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    || (grantResults[2] == PackageManager.PERMISSION_GRANTED)
                    || (grantResults[3] == PackageManager.PERMISSION_GRANTED)
                    || (grantResults[4] == PackageManager.PERMISSION_GRANTED))
            {
                Log.d("acp", "Welcome 2");
                isPermissionGranted = true;
                Intent starter_service = new Intent(WelcomeActivity.this, AppStarterIntentService.class);
                starter_service.putExtra("PERMISSION_GRANTED", isPermissionGranted);
                startService(starter_service);
            }
            else
            {
                ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WRITE_SETTINGS,
                                Manifest.permission.WAKE_LOCK
                        },
                        REQUEST_CODE);
            }
        } /*else {
            ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_SETTINGS},
                    REQUEST_CODE);
        }*/
    }

    private void startAnotherActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.no_animation);
        WelcomeActivity.this.finish();
    }
}