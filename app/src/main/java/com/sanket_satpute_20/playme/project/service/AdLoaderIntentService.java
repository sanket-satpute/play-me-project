package com.sanket_satpute_20.playme.project.service;

import android.app.IntentService;
import android.content.Intent;

public class AdLoaderIntentService extends IntentService {

    public static final String AD_FROM_SETTING_FRAG = "AD_FROM_SETTING_FRAG";
    public static final String AD_ID = "ca-app-pub-3940256099942544/6300978111";

    public AdLoaderIntentService() {
        super(AdLoaderIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (AD_FROM_SETTING_FRAG.equals(action)) {
            }
        }
    }
}