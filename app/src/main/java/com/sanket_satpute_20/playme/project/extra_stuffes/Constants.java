package com.sanket_satpute_20.playme.project.extra_stuffes;

import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;

import com.sanket_satpute_20.playme.project.account.data.model.MUser;
import com.sanket_satpute_20.playme.project.model.ChangeLogApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Constants {

    /*  Developer   */
    public static final String DEVELOPER_MAIL_ID = "ssatpute738@gmail.com";
    public static final String APP_MAIL_ID = "playmeplayermail@gmail.com";

    public static boolean isInternetOn = false;

    public static final String NOTIFICATION_CHANNEL_ID = "music";
    public static final int NOTIFICATION_ID = 1;

    public static final String IS_ON_SKIP_100_KB_PREFERENCE = "IS_ON_SKIP_100_KB_PREFERENCE";
    public static final String IS_ON_SKIP_30_S_PREFERENCE = "IS_ON_SKIP_30_S_PREFERENCE";

//    equlizer
    public static boolean isUsingSystemEqulizer = true;

//    Notification Constant
    public static final String ACTION_PREVIOUS = "ACTION_PREVIOUS";
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_NEXT = "ACTION_NEXT";

//    speed and pitch
    public static boolean iSSpeedON = false, iSPitchON = false;
    public static int pitchI = 5, speedI = 5;
    public static float pitchF = 1.0f, speedF = 1.0f;

//    most played recycle constants
    public static int stat_position = -1;

//    song store for how much days then remove from list
    public static int recent_storing_days, most_played_storing_days;

//    Theme
//    preferences
    public static final String THEME_PREFERENCE = "THEME_PREFERENCE";
    public static final String THEME_TYPE_PREFERENCE = "THEME_TYPE_PREFERENCE";
    public static final String THEME_NORMAL_SELECTED_NORMAL = "THEME_NORMAL_SELECTED_NORMAL";
    public static final String THEME_SOLID_SELECTED_COLOR = "THEME_SOLID_SELECTED_COLOR";
    public static final String THEME_GRADIENT_SELECTED_GRADIENT = "THEME_GRADIENT_SELECTED_GRADIENT";
    public static final String THEME_IMAGE_SELECTED_IMAGE = "THEME_IMAGE_SELECTED_IMAGE";
    public static final String GRADIENT_THEME_ORIENTATION = "GRADIENT_THEME_ORIENTATION";
    public static final String IS_PREVIOUS_THEME_NOW = "IS_PREVIOUS_THEME_NOW";

    /*  for already solid selected  */
    public static int SOLID_THEME_SELECTED_COLOR;
    public static Bitmap IMAGE_THEME_SELECTED_IMAGE;
    public static GradientDrawable GRADIENT_THEME_SELECTED_GRADIENT_COLOR;
    public static String NORMAL_THEME_SELECTED_WHICH;

    /*  Notification    */
    public static String NOTIFICATION_TYPE = "NEW";

    /*  String form */
//    IMAGE_THEME_SELECTED_IMAGE_STRING_FORM, GRADIENT_THEME_SELECTED_GRADIENT_COLOR_STRING_FORM;


    //    change log array
    public static ArrayList<ChangeLogApp> change_log_data = new ArrayList<>();

    public static final String ADD_CHANGE_LOG_ARRAY = "ADD_CHANGE_LOG_ARRAY";

    public static long SONG_SEEK_CURRENT_POSITION = 0L;
    public static long recent_duration = 0L;

//    timer service
//    Count Down Timer Reference
    public static CountDownTimer timer;
    public static boolean isPaused = true;
    public static boolean isCanceled = true;
    public static boolean isTimerServiceRunning = false;
    public static long FINAL_TIME = 0L;
    public static long used_time = 0;

//    headset control's setting
    public static boolean __PLAY_WHEN_HEADSET_INSERTED;
    public static boolean __PAUSE_WHEN_HEADSET_PLUGGED;
    public static boolean __PLAY_WHEN_BLUETOOTH_CONNECTED;
    public static boolean __PAUSE_WHEN_BLUETOOTH_DISCONNECTED;
    public static boolean __ALLOW_HEAD_SET_CONTROLS;
    public static boolean _isBluetoothConnected;

//    listener      --> headset
    public static boolean __HEADPHONE_LISTENER_ALREADY_STARTED;

//    listener      --> screen state listener for showing screen when suddenly screen on by user
    public static boolean __LOCK_SCREEN_PLAY_LISTENER_ON;

//    gesture play ---  swipe to change next song
    public static boolean __IS_GESTURE_PLAY_SONG_ON;


//    SOUND EFFECT'S CONSTANT
    public static boolean __IS_PLAY_ME_EQUALIZER_ON;
    public static boolean __IS_PLAY_ME_BASS_BOOST_ON;
    public static boolean __IS_PLAY_ME_VIRTUALIZER_ON;
    public static boolean __IS_PLAY_ME_LOUDNESS_ENHANCER_ON;

    //    ACCOUNT RELATED
    public static MUser currentUser = null;
    public static String CURRENCY_TYPE = null;
    public static boolean isShowUserDetailActServiceBinded = false;
    public static final String TRANSACTION_STATUS_FAILED = "Failed";
    public static final String TRANSACTION_STATUS_SUCCESS = "Success";
    public static final String TRANSACTION_STATUS_PENDING = "Pending";
    public static final String TRANSACTION_STATUS_IN_PROGRESS = "Processing";
    public static final String TRANSACTION_PAYMENT_STAGE_1 = "PAYMENT CONFIRMED";
    public static final String TRANSACTION_PAYMENT_STAGE_2 = "PAYMENT SHIPPED";
    public static final String TRANSACTION_PAYMENT_STAGE_3 = "PAYMENT DELIVERED";
    public static final String DOLLAR_OR_RUPEE_RUPEE = "INR";
    public static final String DOLLAR_OR_RUPEE_DOLLAR = "USD";
    public static double rupeeValueByDollar;

//    REMOTE CONFIG CONSTANT STRINGS
    public static final String APP_USING_TIME_REWARD_VALUE_STR = "appUsingTimeRewardValue";
    public static final String DAILY_AD_WATCH_TIME_LIMIT_STR = "dailyAdWatchTimeLimit";

    public static final char[] RANDOM_STRINGS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H' ,'I', 'J', 'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    public static final char[] RANDOM_NUMBERS = {'0', '1', '2', '3', '4', '5', '6', '7' ,'8', '9'};
    public static final String[] DAYS_OF_WEEK = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    public static final String[] month_names = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};


    /********************************************************************************
    **                              User Related                                 **
    ********************************************************************************/
    public static Bitmap USER_IMAGE_BITMAP_CONST;

//    ad-mob
    public static Map<String, String> real_ad_values_map = new HashMap<>();
    public static Map<String, String> fake_ad_values_map = new HashMap<>();

    public static Map<String, String> ad_values_map;

    static {
//        real
//        real_ad_values_map.put("ad_setting_fragment_banner_ad", "ca-app-pub-8179179142434773/9701804356");
        real_ad_values_map.put("ad_watch_btn_reward_ad", "ca-app-pub-8179179142434773/2751220954");
//        real_ad_values_map.put("ad_show_user_detail_banner_ad", "ca-app-pub-8179179142434773/7433250737");
//        real_ad_values_map.put("ad_redeem_money_banner_ad", "ca-app-pub-8179179142434773/5223495141");
//        real_ad_values_map.put("ad_notification_or_inbox_banner_ad", "ca-app-pub-8179179142434773/9817346273");
//        real_ad_values_map.put("ad_wallet_banner_ad", "ca-app-pub-8179179142434773/9955294681");
//        real_ad_values_map.put("ad_my_reward_banner_ad", "ca-app-pub-8179179142434773/7510747132");
//        real_ad_values_map.put("ad_edit_user_detail_interstitial_ad", "ca-app-pub-8179179142434773/6427893461");


//        fake
//        fake_ad_values_map.put("ad_setting_fragment_banner_ad", "ca-app-pub-3940256099942544/6300978111");
        fake_ad_values_map.put("ad_watch_btn_reward_ad", "ca-app-pub-3940256099942544/5354046379");
//        fake_ad_values_map.put("ad_show_user_detail_banner_ad", "ca-app-pub-3940256099942544/6300978111");
//        fake_ad_values_map.put("ad_redeem_money_banner_ad", "ca-app-pub-3940256099942544/6300978111");
//        fake_ad_values_map.put("ad_notification_or_inbox_banner_ad", "ca-app-pub-3940256099942544/6300978111");
//        fake_ad_values_map.put("ad_wallet_banner_ad", "ca-app-pub-3940256099942544/6300978111");
//        fake_ad_values_map.put("ad_my_reward_banner_ad", "ca-app-pub-3940256099942544/6300978111");
//        fake_ad_values_map.put("ad_edit_user_detail_interstitial_ad", "ca-app-pub-3940256099942544/1033173712");


//        assigning map which you want
        ad_values_map = real_ad_values_map;
    }

}
