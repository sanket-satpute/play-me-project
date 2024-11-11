package com.sanket_satpute_20.playme.project.service;

import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.SeeChangeLogFragment.FROM_WHERE;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.change_log_data;

import android.app.IntentService;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sanket_satpute_20.playme.project.model.ChangeLogApp;

import java.util.ArrayList;

public class ChangeLogDataAdderIntentService extends IntentService {

    public ChangeLogDataAdderIntentService() {
        super(ChangeLogDataAdderIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(FROM_WHERE)) {
                if (intent.getStringExtra(FROM_WHERE).equals("ChangeLogFragment")) {
                    change_log_data = new ArrayList<>();
                    initialise();
                    Intent broad_intent = new Intent();
                    broad_intent.setAction("initialised_change_log");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broad_intent);
                }
            } else {
                if (change_log_data == null) {
                    change_log_data = new ArrayList<>();
                }
                initialise();
            }
        }
    }

    private void initialise() {
        String []version_1_0_arr = {"Published PlayMe App"};
        ChangeLogApp version_1_0 = new ChangeLogApp("30-08-2022", "1.0", version_1_0_arr);
        String []version_1_0_1_arr = {"Optimized Notification Bug's"};
        ChangeLogApp version_1_0_1 = new ChangeLogApp("02-09-2022", "1.0.1", version_1_0_1_arr);
        String []version_1_0_64_arr = {"Improved Performance", "Added New Design to search view", "Added Voice Search Feature"
                , "Improved Most Played UI", "Optimized Gesture Detection"};
        ChangeLogApp version_1_0_64 = new ChangeLogApp("17-09-2022", "1.0.64", version_1_0_64_arr);
        String []version_1_0_98_arr = {"Added Themes","Improved Performance", "Bug's Removed"};
        ChangeLogApp version_1_0_98 = new ChangeLogApp("10-05-2022", "1.0.64", version_1_0_98_arr);

        if (change_log_data.size() <= 0) {
            /*  Add New Here    */
            change_log_data.add(version_1_0_98);
            change_log_data.add(version_1_0_64);
            change_log_data.add(version_1_0_1);
            change_log_data.add(version_1_0);
        }
    }
}