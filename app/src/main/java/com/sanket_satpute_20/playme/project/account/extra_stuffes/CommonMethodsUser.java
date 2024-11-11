package com.sanket_satpute_20.playme.project.account.extra_stuffes;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.DAILY_AD_WATCH_TIME_LIMIT_STR;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.RANDOM_NUMBERS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.RANDOM_STRINGS;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Calendar;
import java.util.Random;

public class CommonMethodsUser {

    private static FirebaseRemoteConfig f_config;

    private static void connectToFirebase() {
        if (f_config == null)
            f_config = FirebaseRemoteConfig.getInstance();
    }

    public static String createDailyTimeId() {
        Random random = new Random();
        StringBuilder date_str = new StringBuilder(Calendar.getInstance().getTime().toString().replace(" ", "-") + "~");
        for (int i = 0; i < 5; i++) {
            if (random.nextBoolean()) {
                date_str.append(RANDOM_STRINGS[random.nextInt(RANDOM_STRINGS.length - 1)]);
            } else {
                date_str.append(RANDOM_NUMBERS[random.nextInt(RANDOM_NUMBERS.length - 1)]);
            }
        }
        return date_str.append("-TIME$M").toString();
    }

    public static int dailyAdWatchTimeLimit() {
        if (f_config == null)
            connectToFirebase();
        return (f_config.getLong(DAILY_AD_WATCH_TIME_LIMIT_STR) < 1) ? 20 : (int) f_config.getLong(DAILY_AD_WATCH_TIME_LIMIT_STR);
    }

    public static String getFirebaseStoragePath() {
        return "gs://playme-android.appspot.com/";
    }
}
