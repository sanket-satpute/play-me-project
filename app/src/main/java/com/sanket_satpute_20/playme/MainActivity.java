package com.sanket_satpute_20.playme;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static com.sanket_satpute_20.playme.project.account.extra_stuffes.CommonMethodsUser.dailyAdWatchTimeLimit;
import static com.sanket_satpute_20.playme.project.account.service.TimerIntentService.FINAL_TIME_EXTRAS;
import static com.sanket_satpute_20.playme.project.activity.AlbumListActivity.ALBUM_FINISHING_REQUEST_CODE;
import static com.sanket_satpute_20.playme.project.activity.ArtistSongsActivity.ARTIST_FINISHING_REQUEST_CODE;
import static com.sanket_satpute_20.playme.project.activity.SelectMultipleActivity.SELECT_MULTIPLE_DELETION_REQUEST_CODE;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BSheetSongsMoreFragment.UPDATE_SONG_TAG_REQUEST;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BSheetSongsMoreFragment.delete_song_removing_position;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BottomDialogAlbumFragment.DELETING_MUSIC_FILES_OF_ALBUM;
import static com.sanket_satpute_20.playme.project.extra_stuffes.CommonMethods.REQUEST_PERMISSION_DELETE;
import static com.sanket_satpute_20.playme.project.extra_stuffes.CommonMethods.REQUEST_PERMISSION_DELETE_ALBUM;
import static com.sanket_satpute_20.playme.project.extra_stuffes.CommonMethods.getCurrentVersion;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.NOTIFICATION_TYPE;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.RANDOM_NUMBERS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.RANDOM_STRINGS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__ALLOW_HEAD_SET_CONTROLS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__IS_GESTURE_PLAY_SONG_ON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__IS_PLAY_ME_BASS_BOOST_ON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__IS_PLAY_ME_EQUALIZER_ON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__IS_PLAY_ME_LOUDNESS_ENHANCER_ON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__IS_PLAY_ME_VIRTUALIZER_ON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__LOCK_SCREEN_PLAY_LISTENER_ON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__PAUSE_WHEN_BLUETOOTH_DISCONNECTED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__PAUSE_WHEN_HEADSET_PLUGGED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__PLAY_WHEN_BLUETOOTH_CONNECTED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__PLAY_WHEN_HEADSET_INSERTED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.ad_values_map;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isPaused;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isTimerServiceRunning;
import static com.sanket_satpute_20.playme.project.fragments.SearchFragment.VOICE_RECOGNITION_REQUEST_CODE;
import static com.sanket_satpute_20.playme.project.service.BackService.CURRENT_PLAYING_SONG_PATH;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPATH;
import static com.sanket_satpute_20.playme.project.service.BackService.song;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanket_satpute_20.playme.project.account.data.model.MTime;
import com.sanket_satpute_20.playme.project.account.service.TimerIntentService;
import com.sanket_satpute_20.playme.project.activity.CoreSettingActivity;
import com.sanket_satpute_20.playme.project.activity.PlayActivity;
import com.sanket_satpute_20.playme.project.activity.ScanSongsActivity;
import com.sanket_satpute_20.playme.project.activity.SelectMultipleActivity;
import com.sanket_satpute_20.playme.project.extra_stuffes.CheckInternet;
import com.sanket_satpute_20.playme.project.extra_stuffes.Constants;
import com.sanket_satpute_20.playme.project.fragments.BottomPlayFragment;
import com.sanket_satpute_20.playme.project.fragments.HomeFragment;
import com.sanket_satpute_20.playme.project.fragments.SearchFragment;
import com.sanket_satpute_20.playme.project.fragments.SettingFragment;
import com.sanket_satpute_20.playme.project.receivers.MyAppReceiver;
import com.sanket_satpute_20.playme.project.service.BackService;

import java.lang.reflect.Type;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    public static final String USER_FETCHED_COMPLETE = "USER_FETCHED_COMPLETE.SETTING_FRAGMENT";
    public static final String USER_FETCHING_IS_FAILED = "USER_FETCHING_IS_FAILED";
    public static final String CURRENT_USER_IS_AVAILABLE = "CURRENT_USER_IS_AVAILABLE";
    public static final String USER_FETCHING_FAILED_MSG = "USER_FETCHING_FAILED_MSG";

    /*  SETTING TAB ITEMS HOME FRAGMENT */
    public static final String HOME_CHECKED = "HOME_CHECKED";
    public static final String SONG_CHECKED = "SONG_CHECKED";
    public static final String ALBUM_CHECKED = "ALBUM_CHECKED";
    public static final String ARTIST_CHECKED = "ARTIST_CHECKED";
    public static final String PLAYLIST_CHECKED = "PLAYLIST_CHECKED";

    public static final String STORING_DATA = "STORING_DATA";
    public static final String LAST_PLAYED_LIST = "LAST_PLAYED_LIST";
    public static final String WHICH_ACT_STRING = "WHICH_ACT_STRING";
    public static final String ROUND_PLAY_ACT_BOTTOM = "ROUND_PLAY_ACT_BOTTOM";
    public static final String ROUND_ACT_EXPANDER_ENABLE_DISABLE = "ROUND_ACT_EXPANDER_ENABLE_DISABLE";
    public static final String ROUND_BACKGROUND_IS_ENABLE = "ROUND_BACKGROUND_IS_ENABLE";
    public static final String ROUND_ACT_BACKGROUND_TYPE = "ROUND_ACT_BACKGROUND_TYPE";
    public static final String THEME_TYPE_PREFERENCE = "THEME_TYPE_PREFERENCE";
    public static final String VISULIZER_COLOR = "VISULIZER_COLOR";
    public static final String VISULIZER_SPEED = "VISULIZER_SPEED";
    public static final String VISULIZER_POSITION = "VISULIZER_POSITION";
    public static final String IS_SQUARE_PLAYER_VISIBLE = "IS_SQUARE_PLAYER_VISIBLE";
    public static final String RECENT_STORING_SONG_FOR_DAYS = "RECENT_STORING_SONG_FOR_DAYS";
    public static final String MOST_PLAYED_STORING_SONG_FOR_DAYS = "MOST_PLAYED_STORING_SONG_FOR_DAYS";
    public static final String RECENT_STORING_DAYS_PREFERENCE = "RECENT_STORING_DAYS_PREFERENCE";
    public static final String MOST_PLAYED_STORING_DAYS_PREFERENCE = "MOST_PLAYED_STORING_DAYS_PREFERENCE";
    public static final String LAST_OPENED = "LAST_OPENED";
    public static final String SELECTED_INDEX_OF_HOME_FRAG = "SELECTED_INDEX_OF_HOME_FRAG";
    public static final String SELECTED_TAB_NAME_HOME_FRAG = "SELECTED_TAB_NAME_HOME_FRAG";
    public static final String NOTIFICATION_TYPE_PREFERENCE = "NOTIFICATION_TYPE_PREFERENCE";
    public static final String RECENT_SEEK_POSITION_SONG = "RECENT_SEEK_POSITION_SONG";
//    headset finals
    public static final String HEADSET_PLAY_WHEN_INSERTED = "HEADSET_PLAY_WHEN_INSERTED";
    public static final String HEADSET_PAUSE_WHEN_REMOVED = "HEADSET_PAUSE_WHEN_REMOVED";
    public static final String HEADSET_PLAY_WHEN_BLUETOOTH_CONNECTED = "HEADSET_PLAY_WHEN_BLUETOOTH_CONNECTED";
    public static final String HEADSET_PAUSE_WHEN_BLUETOOTH_DISCONNECTED = "HEADSET_PAUSE_WHEN_BLUETOOTH_DISCONNECTED";
    public static final String HEADSET_ALLOW_HEADSET_CONTROLS = "HEADSET_ALLOW_HEADSET_CONTROLS";
//    Lock screen playback on/off
    public static final String LOCK_SCREEN_PLAY = "LOCK_SCREEN_PLAY";
//    gesture play songs
    public static final String GESTURE_PLAY_SONG = "GESTURE_PLAY_SONG";
//    play me sound effects
    public static final String SOUND_EFFECT_EQUALIZER_PLAY_ME = "SOUND_EFFECT_EQUALIZER_PLAY_ME";
    public static final String SOUND_EFFECT_BASS_BOOST_PLAY_ME = "SOUND_EFFECT_BASS_BOOST_PLAY_ME";
    public static final String SOUND_EFFECT_VIRTUALIZER_PLAY_ME = "SOUND_EFFECT_VIRTUALIZER_PLAY_ME";
    public static final String SOUND_EFFECT_LOUDNESS_ENHANCER_PLAY_ME = "SOUND_EFFECT_LOUDNESS_ENHANCER_PLAY_ME";

    int selectedPage;

    /*  theme   */
    public static final String ACCENT_COLOR_PREFERENCE = "ACCENT_COLOR_PREFERENCE";

    public static final String FAVOURITE_SONGS = "FEVOURITESONGS";
    public static final String FAVOURITE_ALBUM = "FAVOURITE_ALBUM";
    public static final String FAVOURITE_ARTIST = "FAVOURITE_ARTIST";
    public static final String FAVOURITE_PLAYLIST = "FAVOURITE_PLAYLIST";
    public static final String PLAYLIST_DATA = "PLAYLISTDATA";
    public static ArrayList<com.sanket_satpute_20.playme.project.model.Albums> albumFilesList = new ArrayList<>();
    public static ArrayList<com.sanket_satpute_20.playme.project.model.MusicFiles> favouriteList = new ArrayList<>();
    public static ArrayList<com.sanket_satpute_20.playme.project.model.Albums> favouriteAlbums = new ArrayList<>();
    public static ArrayList<com.sanket_satpute_20.playme.project.model.Artists> favouriteArtists = new ArrayList<>();
    public static ArrayList<com.sanket_satpute_20.playme.project.model.Playlist> favouritePlaylists = new ArrayList<>();
    public static ArrayList<com.sanket_satpute_20.playme.project.model.Artists> artistFiles = new ArrayList<>();
    public static ArrayList<com.sanket_satpute_20.playme.project.model.MusicFiles> musicFiles;

    public static RewardedInterstitialAd rewardedInterstitialAdMainAct;

    SharedPreferences theme_preference;
    SharedPreferences.Editor theme_preference_editor;

    public static boolean isPermissionGranted = false;

    LinearLayout layout;
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    RelativeLayout frameLayout;
    ConstraintLayout activity_background;

    public static int default_color;

    com.sanket_satpute_20.playme.project.fragments.BottomPlayFragment bottomFragment;

    com.sanket_satpute_20.playme.project.service.BackService service;

//    NetworkStateReciver networkReciver;

    String path;
    int latest_version, current_version;

    FirebaseRemoteConfig f_config;
    FirebaseFirestore f_store;
    FirebaseAuth f_auth;

    TimerIntentService timer_service;

    boolean isThisActivityRunning = false;

    Intent broad_intent_to_artist_frag_resumed, broad_intent_to_album_frag_resumed, broad_intent_song_recycler_items_removed_selection;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case "all_loaded.Start_Second.Activity":
                        if (!getSupportFragmentManager().isDestroyed()) {
                            bottomNavigationView.setSelectedItemId(R.id.home);
                            getSupportFragmentManager().beginTransaction().replace(R.id.linear_layout, new HomeFragment())
                                    .commit();
                        }
                        break;
                    case "action.broadcast.SONG_NOT_FOUND.Down_the_bottom_player":
                        setExitBottomAnimation();
                        break;
                    case "song.mp3.changed":
                        if (frameLayout.getVisibility() == View.GONE)
                            setBottomAnimation();
                        break;
                    case "intent.artist_fragment_resumed.To_MAIN":
                        if (broad_intent_to_artist_frag_resumed != null) {
                            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(broad_intent_to_artist_frag_resumed);
                            broad_intent_to_artist_frag_resumed = null;
                        }
                        break;
                    case "intent.album_fragment_resumed.To_MAIN":
                        if (broad_intent_to_album_frag_resumed != null) {
                            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(broad_intent_to_album_frag_resumed);
                            broad_intent_to_album_frag_resumed = null;
                        }
                        break;
                    case "action_to_main.SONG_FRAGMENT_RESUMED":
                        if (broad_intent_song_recycler_items_removed_selection != null) {
                            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(broad_intent_song_recycler_items_removed_selection);
                            broad_intent_song_recycler_items_removed_selection = null;
                        }
                        break;
                }
            }
        }
    };

    private final ServiceConnection timer_service_binding = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TimerIntentService.TimerBinder binder = (TimerIntentService.TimerBinder) iBinder;
            timer_service = binder.getService();
            if (isTimerServiceRunning && isPaused) {
                timer_service.resumeTimer();
                Log.d("time_executed", "Calling from here");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            timer_service = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getThemeOfUI();
        setContentView(R.layout.activity_main);
        setDefaultCurrentSong();
        setFullScreen();
        initViews();
        setBackgroundTheme();
        registerInternetReceiver();
        loadRewardedAd();

        setClickListener();

        f_store = FirebaseFirestore.getInstance();
        f_auth = FirebaseAuth.getInstance();

        checkUser();

        Constants.isInternetOn = CheckInternet.checkingInternet(this);

        if (com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isInternetOn) {
            current_version = getCurrentVersion(getApplicationContext());
            f_config = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .build();
            f_config.setConfigSettingsAsync(configSettings);
            f_config.fetchAndActivate().addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    String latest_version_string = f_config.getString("latest_version_code").trim();
                    if (!latest_version_string.isEmpty()) {
                        latest_version = Integer.parseInt(latest_version_string);
                        if (current_version < latest_version) {
                            Log.d("version_cpp", "My version - " + latest_version);
//                            showUpdateDialog();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Make Sure Your Internet is On", Toast.LENGTH_SHORT).show();
        }

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getAction() != null) {
                if (intent.getAction().equalsIgnoreCase("open.setting.fragment")) {
                    try {
                        bottomNavigationView.setSelectedItemId(R.id.setting);
                        getSupportFragmentManager().beginTransaction().replace(R.id.linear_layout, new com.sanket_satpute_20.playme.project.fragments.SettingFragment())
                                .commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isThisActivityRunning = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction("all_loaded.Start_Second.Activity");
        filter.addAction("action.broadcast.SONG_NOT_FOUND.Down_the_bottom_player");
        filter.addAction("song.mp3.changed");
        filter.addAction("intent.artist_fragment_resumed.To_MAIN");
        filter.addAction("intent.album_fragment_resumed.To_MAIN");
        filter.addAction("action_to_main.SONG_FRAGMENT_RESUMED");
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(receiver, filter);

        Intent back_service = new Intent(MainActivity.this, com.sanket_satpute_20.playme.project.service.BackService.class); // 1st BackService binding
        MainActivity.this.bindService(back_service, this, BIND_AUTO_CREATE);
        Intent timer_service_2 = new Intent(MainActivity.this, com.sanket_satpute_20.playme.project.account.service.TimerIntentService.class);  // 2nd TimerService binding
        MainActivity.this.bindService(timer_service_2, timer_service_binding, BIND_AUTO_CREATE);
        if (com.sanket_satpute_20.playme.project.activity.ThemesActivity.is_theme_changed) {
            com.sanket_satpute_20.playme.project.activity.ThemesActivity.is_theme_changed = false;
            recreate();
        }

        if (com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR == 0)
            com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR = 0xfff3a243;
        if (musicFiles == null) {
            Intent broadcast_intent = new Intent();
            broadcast_intent.setAction("fetching.songs");
            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(broadcast_intent);
            musicFiles = new ArrayList<>();
            Intent app_starter_service = new Intent(MainActivity.this, com.sanket_satpute_20.playme.project.service.AppStarterIntentService.class);
            app_starter_service.putExtra("PERMISSION_GRANTED", true);
            startService(app_starter_service);
        }

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                default_color = 0xddffffff;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                default_color = 0xdd000000;
                break;
        }

        ColorStateList color_list = new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        },
                new int[]{
                        default_color,
                        com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR
                });
        bottomNavigationView.setItemIconTintList(color_list);
        bottomNavigationView.setItemTextColor(color_list);

        doExtraWork();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (frameLayout.getVisibility() == View.VISIBLE)
            setExitBottomAnimation();
        MainActivity.this.unbindService(this);
        isThisActivityRunning = false;
        if (timer_service != null) {
            timer_service.pauseTimer();
            try {
                MainActivity.this.unbindService(timer_service_binding);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomNavigationView.getSelectedItemId() == R.id.home) {
            super.onBackPressed();
        } else if (bottomNavigationView.getSelectedItemId() == R.id.search) {
            onItemSelected(R.id.home);
            bottomNavigationView.setSelectedItemId(R.id.home);
        } else if (bottomNavigationView.getSelectedItemId() == R.id.setting) {
            onItemSelected(R.id.home);
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterInternetReceiver();
//        if (timer_service != null) {
//            timer_service.timeIsPaused();
//            unbindService(timer_service_binding);
//        }
        setData();
    }

    private void setDefaultCurrentSong() {
        SharedPreferences preferences = getSharedPreferences("PLAYING", MODE_PRIVATE);
        if (preferences != null)
            CURRENT_PLAYING_SONG_PATH = preferences.getString(SONGPATH, null);
    }

    private void connectToFirebase() {
        if (f_auth == null)
            f_auth = FirebaseAuth.getInstance();
        if (f_store == null)
            f_store = FirebaseFirestore.getInstance();
        if (f_config == null)
            f_config = FirebaseRemoteConfig.getInstance();
    }

    private void loadRewardedAd() {
        RewardedInterstitialAd.load(MainActivity.this, Objects.requireNonNull(ad_values_map.get("ad_watch_btn_reward_ad")),
                new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                        Log.d("TAG-AD", "Ad was loaded.");
                        rewardedInterstitialAdMainAct = ad;
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.d("TAG-AD", loadAdError.toString());
                        rewardedInterstitialAdMainAct = null;
                    }
                });
    }

    private void checkUser() {
        Intent user_fetched_complete_intent = new Intent();
        user_fetched_complete_intent.setAction(USER_FETCHED_COMPLETE);

        if(f_auth.getCurrentUser() != null) {
            f_store.collection("users").document(f_auth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        MTime time;
                        String acc_creation_time;
                        Log.d("fetching_user_data", "success " + documentSnapshot);
                        currentUser = documentSnapshot.toObject(com.sanket_satpute_20.playme.project.account.data.model.MUser.class);
                        if (currentUser != null) {
                            currentUser.setActive(true);
                            f_store.collection("users").document(f_auth.getCurrentUser().getUid()).set(currentUser);
                            if (currentUser.getmTime() != null) {
                                int daily_ad_watch_time_limit;
                                if (f_config == null)
                                    connectToFirebase();
                                daily_ad_watch_time_limit = dailyAdWatchTimeLimit();
                                acc_creation_time = String.valueOf(new Date(System.currentTimeMillis()));
                                if (currentUser.getmTime().size() > 0) {
                                    time = currentUser.getmTime().get(currentUser.getmTime().size() - 1);
                                    if (acc_creation_time.trim().equalsIgnoreCase(time.getDaily_limit_date().trim())) {
//                                        same day
                                        Log.d("isSameDay", "Yes Same Day");
                                        timeServiceIsStarted();
                                    } else {
//                                        another day
                                        Log.d("isSameDay", "No Another Day");
                                        Log.d("MMk", "First M_TIME " + daily_ad_watch_time_limit);
                                        MTime nextDayTime = new MTime(createDailyTimeId(), currentUser.getmTime().size(), 0L, 10800000L, "Incomplete", acc_creation_time, false, daily_ad_watch_time_limit);
                                        ArrayList<MTime> mTimeList = currentUser.getmTime();
                                        mTimeList.add(mTimeList.size(), nextDayTime);
                                        currentUser.setmTime(mTimeList);
                                        f_store.collection("users").document(f_auth.getCurrentUser().getUid())
                                                .set(currentUser)
                                                .addOnCompleteListener(task -> {
                                                    Log.d("updated_user", String.valueOf(task.isSuccessful()));
                                                    timeServiceIsStarted();
                                                }).addOnFailureListener(e -> Log.d("updated_user", "Failure Occur " + e.getMessage()));
                                    }
                                } else {
                                    Log.d("isSameDay", "Same Day But New User");
                                    Log.d("MMk", "Second M_TIME " + daily_ad_watch_time_limit);
                                    MTime nextDayTime = new MTime(createDailyTimeId(), 0L, 0L, 10800000L, "Incomplete", acc_creation_time, false, daily_ad_watch_time_limit);
                                    ArrayList<MTime> mTimeList = currentUser.getmTime();
                                    mTimeList.add(nextDayTime);
                                    currentUser.setmTime(mTimeList);
                                    f_store.collection("users").document(f_auth.getCurrentUser().getUid())
                                            .set(currentUser)
                                            .addOnCompleteListener(task -> {
                                                Log.d("updated_user", String.valueOf(task.isSuccessful()));
                                                timeServiceIsStarted();
                                            }).addOnFailureListener(e -> Log.d("updated_user", "Failure Occur " + e.getMessage()));
                                }
                            } else {
                                currentUser.setmTime(new ArrayList<>());
                                f_store.collection("users").document(f_auth.getCurrentUser().getUid()).set(currentUser);
                                checkUser();
                            }

                            user_fetched_complete_intent.putExtra(USER_FETCHING_IS_FAILED, false);
                            user_fetched_complete_intent.putExtra(CURRENT_USER_IS_AVAILABLE, currentUser != null);
                            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(user_fetched_complete_intent);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.d("fetching_user_data", "failed : " + e.getMessage());
                        user_fetched_complete_intent.putExtra(USER_FETCHING_IS_FAILED, true);
                        user_fetched_complete_intent.putExtra(CURRENT_USER_IS_AVAILABLE, false);
                        user_fetched_complete_intent.putExtra(USER_FETCHING_FAILED_MSG, e.getMessage());
                        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(user_fetched_complete_intent);
                    });
        }
    }

    private void timeServiceIsStarted() {
        if (currentUser.getmTime() == null)
            currentUser.setmTime(new ArrayList<>());
        if (currentUser.getmTime().size() <= 0) {
            int daily_ad_watch_time_limit;
            if (f_config == null)
                connectToFirebase();
            daily_ad_watch_time_limit = dailyAdWatchTimeLimit();
            Log.d("MMk", "Third M_TIME " + daily_ad_watch_time_limit);
            currentUser.getmTime().add(new MTime(createDailyTimeId(), 0, 0, com.sanket_satpute_20.playme.project.account.service.TimerIntentService.CONSTANT_FINAL_TIME,
                    "Pending", String.valueOf(new Date(System.currentTimeMillis())), false, daily_ad_watch_time_limit));
        }
        Intent intent = new Intent(MainActivity.this, com.sanket_satpute_20.playme.project.account.service.TimerIntentService.class);
        intent.putExtra(FINAL_TIME_EXTRAS, currentUser.getmTime().get(currentUser.getmTime().size() - 1).getDaily_limit());
        try {
            startService(intent);
        }
        catch (IllegalStateException e) {
            startForegroundService(intent);
        }
    }

    private String createDailyTimeId() {
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

    private final BroadcastReceiver internet_broadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                ConnectivityManager connection = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connection.getActiveNetworkInfo();
                com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isInternetOn = info != null && info.isConnectedOrConnecting();
            }
        }
    };

    private void registerInternetReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(internet_broadcast, filter);
        }
    }

    private void unRegisterInternetReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            unregisterReceiver(internet_broadcast);
        }
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_menu);
        layout = findViewById(R.id.linear_layout);
        toolbar = findViewById(R.id.toolbar);
        frameLayout = findViewById(R.id.framelayout_bottom_play);
        activity_background = findViewById(R.id.main_activity_background);

        // extra Code
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("PlayMe");
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int id_scan_songs = R.id.scan_song;
        final int id_select_multiple = R.id.select_multiple;
        final int id_core_setting = R.id.core_setting;
        int id = item.getItemId();

        if (id == R.id.scan_song) {
            Intent scan_song_intent = new Intent(this, ScanSongsActivity.class);
            startActivity(scan_song_intent);
        } else if (id == R.id.select_multiple) {
            Intent intent = new Intent(MainActivity.this, SelectMultipleActivity.class);
            ((Activity) MainActivity.this).startActivityForResult(intent, SELECT_MULTIPLE_DELETION_REQUEST_CODE);
        } else if (id == R.id.core_setting) {
            Intent core_setting_intent = new Intent(this, CoreSettingActivity.class);
            startActivity(core_setting_intent);
        } else {
            Log.d("a", "un common Selected");
        }

        return true;
    }

    private void setClickListener() {
        bottomNavigationView.setSelectedItemId(R.id.home);
        getSupportFragmentManager().beginTransaction().replace(R.id.linear_layout, new com.sanket_satpute_20.playme.project.fragments.HomeFragment())
                .commit();
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int i = item.getItemId();
            onItemSelected(i);
            return true;
        });
    }

    private void onItemSelected(int i) {
        Fragment fragment = null;
        final int home_id = R.id.home;
        final int search_id = R.id.search;
        final int setting_id = R.id.setting;

        if (i == home_id) {
            fragment = new HomeFragment();
            selectedPage = R.id.home;
        } else if (i == search_id) {
            fragment = new SearchFragment();
            selectedPage = R.id.search;
        } else if (i == setting_id) {
            fragment = new SettingFragment();
            selectedPage = R.id.setting;
        }

        if (fragment == null)
            fragment = new HomeFragment();
        if (!getSupportFragmentManager().isDestroyed()) {
            try {
                getSupportFragmentManager().beginTransaction().replace(R.id.linear_layout, fragment)
                        .commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setData() {

        SharedPreferences preferences = getSharedPreferences(STORING_DATA, MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        Gson gson = new Gson();
        String last_played_list = gson.toJson(com.sanket_satpute_20.playme.project.service.BackService.song);
        String playlist_array = gson.toJson(com.sanket_satpute_20.playme.project.model.PlaylistArray.ref);
        String fev_song_list = gson.toJson(favouriteList);
        String fev_artist_list = gson.toJson(favouriteArtists);
        String fev_album_list = gson.toJson(favouriteAlbums);
        String fev_playlist = gson.toJson(favouritePlaylists);
        String m_play_for_day = gson.toJson(com.sanket_satpute_20.playme.project.service.BackService.most_played_songs);
        String r_play_for_day = gson.toJson(com.sanket_satpute_20.playme.project.service.BackService.recentPlayed);
        edit.putString(LAST_PLAYED_LIST, last_played_list);
        edit.apply();
        edit.putString(PLAYLIST_DATA, playlist_array);
        edit.apply();
        edit.putString(FAVOURITE_SONGS, fev_song_list);
        edit.apply();
        edit.putString(FAVOURITE_ARTIST, fev_artist_list);
        edit.apply();
        edit.putString(FAVOURITE_ALBUM, fev_album_list);
        edit.apply();
        edit.putString(FAVOURITE_PLAYLIST, fev_playlist);
        edit.apply();
        edit.putString(WHICH_ACT_STRING, String.valueOf(com.sanket_satpute_20.playme.project.activity.PlayActivity.which_play_activity));
        edit.apply();
        edit.putString(ROUND_PLAY_ACT_BOTTOM, String.valueOf(com.sanket_satpute_20.playme.project.fragments.PlayOptionFragment.is_which_bottom));
        edit.apply();
        edit.putBoolean(HOME_CHECKED, com.sanket_satpute_20.playme.project.fragments.HomeFragment.home_checked);
        edit.apply();
        edit.putBoolean(SONG_CHECKED, com.sanket_satpute_20.playme.project.fragments.HomeFragment.song_checked);
        edit.apply();
        edit.putBoolean(ALBUM_CHECKED, com.sanket_satpute_20.playme.project.fragments.HomeFragment.album_checked);
        edit.apply();
        edit.putBoolean(ARTIST_CHECKED, com.sanket_satpute_20.playme.project.fragments.HomeFragment.artist_checked);
        edit.apply();
        edit.putBoolean(PLAYLIST_CHECKED, com.sanket_satpute_20.playme.project.fragments.HomeFragment.playlist_checked);
        edit.apply();
        edit.putBoolean(ROUND_ACT_EXPANDER_ENABLE_DISABLE, com.sanket_satpute_20.playme.project.fragments.PlayOptionFragment.expand_visible_disable);
        edit.apply();
        edit.putBoolean(ROUND_BACKGROUND_IS_ENABLE, com.sanket_satpute_20.playme.project.activity.PlayActivity.isRoundBackgroundOn);
        edit.apply();
        edit.putString(ROUND_ACT_BACKGROUND_TYPE, String.valueOf(com.sanket_satpute_20.playme.project.activity.PlayActivity.which_act_background_round));
        edit.apply();
        edit.putInt(ACCENT_COLOR_PREFERENCE, com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR);
        edit.apply();
        edit.putString(THEME_TYPE_PREFERENCE, com.sanket_satpute_20.playme.project.service.AppStarterIntentService.THEME_TYPE);
        edit.apply();
        edit.putInt(VISULIZER_COLOR, com.sanket_satpute_20.playme.project.activity.PlayerDesignActivity.vis_color);
        edit.apply();
        edit.putInt(VISULIZER_SPEED, com.sanket_satpute_20.playme.project.activity.PlayerDesignActivity.vis_speed);
        edit.apply();
        edit.putInt(VISULIZER_POSITION, com.sanket_satpute_20.playme.project.activity.PlayerDesignActivity.visulizer_position);
        edit.apply();
        edit.putBoolean(com.sanket_satpute_20.playme.project.extra_stuffes.Constants.IS_ON_SKIP_100_KB_PREFERENCE, com.sanket_satpute_20.playme.project.activity.ScanSongsActivity.isOnSkip100kb);
        edit.apply();
        edit.putBoolean(com.sanket_satpute_20.playme.project.extra_stuffes.Constants.IS_ON_SKIP_30_S_PREFERENCE, com.sanket_satpute_20.playme.project.activity.ScanSongsActivity.isOnSkip30s);
        edit.apply();
        edit.putBoolean(IS_SQUARE_PLAYER_VISIBLE, com.sanket_satpute_20.playme.project.activity.PlayerDesignActivity.is_visulizer_constraint_visible);
        edit.apply();
        edit.putString(MOST_PLAYED_STORING_SONG_FOR_DAYS, m_play_for_day);
        edit.apply();
        edit.putString(RECENT_STORING_SONG_FOR_DAYS, r_play_for_day);
        edit.apply();
        edit.putInt(SELECTED_INDEX_OF_HOME_FRAG, com.sanket_satpute_20.playme.project.fragments.HomeFragment.selected_index);
        edit.apply();
        edit.putString(SELECTED_TAB_NAME_HOME_FRAG, com.sanket_satpute_20.playme.project.fragments.HomeFragment.tab_name);
        edit.apply();
        edit.putString(NOTIFICATION_TYPE_PREFERENCE, NOTIFICATION_TYPE);
        edit.apply();
        if (service != null) {
            edit.putLong(RECENT_SEEK_POSITION_SONG, service.getCurrentPosition());
            edit.apply();
        }
        edit.putString(LAST_OPENED, String.valueOf(LocalDate.now()));
        edit.apply();
        edit.putBoolean(HEADSET_PLAY_WHEN_INSERTED, __PLAY_WHEN_HEADSET_INSERTED);
        edit.apply();
        edit.putBoolean(HEADSET_PAUSE_WHEN_REMOVED, __PAUSE_WHEN_HEADSET_PLUGGED);
        edit.apply();
        edit.putBoolean(HEADSET_PLAY_WHEN_BLUETOOTH_CONNECTED, __PLAY_WHEN_BLUETOOTH_CONNECTED);
        edit.apply();
        edit.putBoolean(HEADSET_PAUSE_WHEN_BLUETOOTH_DISCONNECTED, __PAUSE_WHEN_BLUETOOTH_DISCONNECTED);
        edit.apply();
        edit.putBoolean(HEADSET_ALLOW_HEADSET_CONTROLS, __ALLOW_HEAD_SET_CONTROLS);
        edit.apply();
        edit.putBoolean(LOCK_SCREEN_PLAY, __LOCK_SCREEN_PLAY_LISTENER_ON);
        edit.apply();
        edit.putBoolean(GESTURE_PLAY_SONG, __IS_GESTURE_PLAY_SONG_ON);
        edit.apply();
        edit.putBoolean(SOUND_EFFECT_EQUALIZER_PLAY_ME, __IS_PLAY_ME_EQUALIZER_ON);
        edit.apply();
        edit.putBoolean(SOUND_EFFECT_BASS_BOOST_PLAY_ME, __IS_PLAY_ME_BASS_BOOST_ON);
        edit.apply();
        edit.putBoolean(SOUND_EFFECT_VIRTUALIZER_PLAY_ME, __IS_PLAY_ME_VIRTUALIZER_ON);
        edit.apply();
        edit.putBoolean(SOUND_EFFECT_LOUDNESS_ENHANCER_PLAY_ME, __IS_PLAY_ME_LOUDNESS_ENHANCER_ON);
        edit.apply();
    }

    private float getAlpha(float alphaMax) {
        if (alphaMax > 0)
            return Math.abs(MAX_SWIPE_DISTANCE - alphaMax) / MAX_SWIPE_DISTANCE;
        return Math.abs((alphaMax != 0) ? MAX_SWIPE_DISTANCE / (MAX_SWIPE_DISTANCE - (alphaMax * 5))  : 1);
    }

    private static final int MIN_SWIPE_DISTANCE = 100;
    private static final int MAX_SWIPE_DISTANCE = 1000;

    private float startX;
    private float startY;

    Fragment fragment;
    @SuppressLint("ClickableViewAccessibility")
    private void doExtraWork() {
        SharedPreferences preferences = getSharedPreferences("PLAYING", MODE_PRIVATE);
        path = preferences.getString(BackService.SONGPATH, null);

        bottomFragment = new BottomPlayFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.framelayout_bottom_play, bottomFragment);
        transaction.commit();

        if (frameLayout != null) {
            frameLayout.setOnClickListener(view -> {});

            if (path != null && !path.equalsIgnoreCase("Not Found")) {
                if (song != null) {
                    if (song.size() > 0) {
                        setBottomAnimation();

                        frameLayout.setOnTouchListener((v, event) -> {
                            fragment = getSupportFragmentManager().findFragmentById(R.id.framelayout_bottom_play);
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN :
                                    startX = event.getX();
                                    startY = event.getY();
                                    if (fragment instanceof BottomPlayFragment && __IS_GESTURE_PLAY_SONG_ON) {
                                        if (((BottomPlayFragment) fragment).animator != null && ((BottomPlayFragment) fragment).animator.isRunning()) {
                                            ((BottomPlayFragment) fragment).animator.cancel();
                                        }
                                    }
                                    break;
                                case MotionEvent.ACTION_MOVE :
                                    if (fragment instanceof BottomPlayFragment && __IS_GESTURE_PLAY_SONG_ON) {
                                        float currentX = event.getX();
                                        float deltaX = currentX - startX;
                                        float alpha = getAlpha(deltaX);
                                        ((BottomPlayFragment) fragment).gestureSwipingIsOn((int) deltaX, alpha);
                                    }
                                    return true;
                                case MotionEvent.ACTION_UP :
                                    float endX = event.getX();
                                    float endY = event.getY();

                                    float distanceX = endX - startX;
                                    float distanceY = endY - startY;

                                    if (Math.abs(distanceX) > Math.abs(distanceY)) {
                                        if (__IS_GESTURE_PLAY_SONG_ON) {
                                            if (Math.abs(distanceX) > MIN_SWIPE_DISTANCE && Math.abs(distanceX) < MAX_SWIPE_DISTANCE) {
                                                if (distanceX > 0) {
                                                    // Swipe left, change to previous song
//                                                    previous
                                                    if (fragment instanceof BottomPlayFragment) {
                                                        ((BottomPlayFragment) fragment).gesturePreviousChanged();
                                                    }
                                                } else {
                                                    // Swipe right, change to next song
//                                                    next
                                                    if (fragment instanceof BottomPlayFragment) {
                                                        ((BottomPlayFragment) fragment).gestureNextChanged();
                                                    }
                                                }
                                            } else {
//                                                restore
                                                if (fragment instanceof BottomPlayFragment) {
                                                    ((BottomPlayFragment) fragment).animateResetGesture();
                                                }
                                            }
                                        }
                                    } else if (Math.abs(distanceY) > MIN_SWIPE_DISTANCE && Math.abs(distanceY) < MAX_SWIPE_DISTANCE) {
                                        if (__IS_GESTURE_PLAY_SONG_ON) {
                                            if (distanceY > 0) {
                                                // Swipe down, stop the song
                                                service.pause();
                                                setExitBottomAnimation();
//                                                stop
                                            } else if (fragment instanceof BottomPlayFragment) {
//                                                up but not move reset animation
                                                ((BottomPlayFragment) fragment).animateResetGesture();
                                            }
                                        }
                                    } else {
//                                        clicked
                                        Intent intent = new Intent(this, PlayActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.play_act_up, R.anim.no_animation);
                                    }
                                    break;
                            }
                            return false;
                        });
                    }
                }
            } else {
                frameLayout.setVisibility(View.GONE);
            }
        }
    }

    private void setBottomAnimation() {
        if (frameLayout.getTranslationY() > 1)
            frameLayout.setTranslationY(frameLayout.getHeight());
        frameLayout.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(frameLayout,
                "translationY", frameLayout.getHeight(), 0);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    private void setExitBottomAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(frameLayout,
                "translationY", 0, frameLayout.getHeight());
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                frameLayout.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private void showUpdateDialog() {
        Button skip, update;

        View view = LayoutInflater.from(this).inflate(R.layout.app_update_layout, null);
        MaterialAlertDialogBuilder dialogue = new MaterialAlertDialogBuilder(this);
        dialogue.setView(view);
        AlertDialog alert = dialogue.create();
        alert.show();
        alert.setCanceledOnTouchOutside(false);

        skip = view.findViewById(R.id.skip_btn);
        update = view.findViewById(R.id.update_btn);

        skip.setOnClickListener(v -> alert.dismiss());
        update.setOnClickListener((v) -> {
            try {
                Uri marketUri = Uri.parse("market://details?id=" + MainActivity.this.getPackageName());
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
            } catch (ActivityNotFoundException e) {
                Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id="+ MainActivity.this.getPackageName());
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
            }

        });
    }

    private void setFullScreen() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(params);

        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
            );
        } else {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
            );
        }
    }

    private void getThemeOfUI() {
        if (theme_preference == null)
            theme_preference = getSharedPreferences(com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_PREFERENCE, MODE_PRIVATE);
        com.sanket_satpute_20.playme.project.service.AppStarterIntentService.THEME_TYPE = theme_preference.getString(com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_TYPE_PREFERENCE, com.sanket_satpute_20.playme.project.activity.ThemesActivity.IMAGE_THEME);

        try {
            if (com.sanket_satpute_20.playme.project.activity.ThemesActivity.NORMAL_THEME.equals(com.sanket_satpute_20.playme.project.service.AppStarterIntentService.THEME_TYPE)) {
                MainActivity.this.setTheme(R.style.Theme_PlayMe);
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM);
            } else {
                MainActivity.this.setTheme(R.style.Theme_PlayMe_Another);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.this.setTheme(R.style.Theme_PlayMe);
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private void setBackgroundTheme() {
        if (theme_preference == null)
            theme_preference = getSharedPreferences(com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_PREFERENCE, MODE_PRIVATE);
        com.sanket_satpute_20.playme.project.service.AppStarterIntentService.THEME_TYPE = theme_preference.getString(com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_TYPE_PREFERENCE, com.sanket_satpute_20.playme.project.activity.ThemesActivity.IMAGE_THEME);
        switch (com.sanket_satpute_20.playme.project.service.AppStarterIntentService.THEME_TYPE) {
            case com.sanket_satpute_20.playme.project.activity.ThemesActivity.SOLID_THEME:
                setSolidTheme(theme_preference.getInt(com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_SOLID_SELECTED_COLOR, 0));
                break;
            case com.sanket_satpute_20.playme.project.activity.ThemesActivity.IMAGE_THEME:
                setImageTheme(theme_preference.getString(com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_IMAGE_SELECTED_IMAGE, null));
                break;
            case com.sanket_satpute_20.playme.project.activity.ThemesActivity.GRADIENT_THEME:
                Gson gson = new Gson();
                GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                String theme_child_gradient = theme_preference.getString(com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_GRADIENT_SELECTED_GRADIENT, null);
                switch (theme_preference.getString(com.sanket_satpute_20.playme.project.extra_stuffes.Constants.GRADIENT_THEME_ORIENTATION, "T_TO_B")) {
                    case "B_TO_T":
                        orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                        break;
                    case "L_TO_R":
                        orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                        break;
                    case "R_TO_L":
                        orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                        break;
                    case "TR_TO_BL":
                        orientation = GradientDrawable.Orientation.TR_BL;
                        break;
                    case "TL_TO_BR":
                        orientation = GradientDrawable.Orientation.TL_BR;
                        break;
                    case "BR_TO_TL":
                        orientation = GradientDrawable.Orientation.BR_TL;
                        break;
                    case "BL_TO_TR":
                        orientation = GradientDrawable.Orientation.BL_TR;
                        break;
                }
                if (theme_child_gradient != null) {
                    Type type_gradient = new TypeToken<List<int[]>>() {
                    }.getType();
                    List<int[]> gradient = gson.fromJson(theme_child_gradient, type_gradient);
                    if (gradient != null) {
                        int[] arr_grad = (gradient.size() > 0) ? gradient.get(0) : null;
                        setGradientTheme(orientation, arr_grad);
                    } else {
                        setGradientTheme(orientation, null);
                    }
                } else{
                    setGradientTheme(orientation, null);
                }
                break;
            default:
//                setNormalTheme(theme_preference.getString(THEME_NORMAL_SELECTED_NORMAL, "NIGHT_MODE_SYSTEM"));
                break;
        }
    }

    private void setSolidTheme(int color) {
        if (color != 0) {
            activity_background.setBackgroundColor(color);
        } else {
//            recreate
            settingDefaultNormalTheme();
        }
    }

    private void setImageTheme(String path) {
        if (path != null) {
            Bitmap bitmap = com.sanket_satpute_20.playme.project.extra_stuffes.CacheImageManager.getImage(MainActivity.this, path);
            if (bitmap != null) {
                activity_background.setBackgroundColor(Color.parseColor("#00000000"));
                activity_background.setBackground(new BitmapDrawable(getResources(), bitmap));
            } else {
//                recreate
                settingDefaultNormalTheme();
            }
        } else {
//            set normal theme recreate activity
            settingDefaultNormalTheme();
        }
    }

    private void setGradientTheme(GradientDrawable.Orientation orientation, int []gradient_colors) {
        if (orientation == null)
            orientation = GradientDrawable.Orientation.TOP_BOTTOM;
        if (gradient_colors != null) {
            activity_background.setBackgroundColor(Color.parseColor("#00000000"));
            GradientDrawable gradientDrawable = new GradientDrawable(orientation, gradient_colors);
            activity_background.setBackground(gradientDrawable);
        } else {
//            recreate
            settingDefaultNormalTheme();
        }
    }

    @Override
    protected void onNightModeChanged(int mode) {
        super.onNightModeChanged(mode);
        Log.d("ggv", "night mode changed");
    }


//    private void setNormalTheme(String normal_theme_type) {
//        switch (normal_theme_type) {
//            case "NIGHT_MODE_OFF" :
//                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
//                break;
//            case "NIGHT_MODE_ON" :
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                break;
//            case "NIGHT_MODE_AUTO" :
//                DateFormat dfTime = new SimpleDateFormat("HH", Locale.getDefault());
//                int time = Integer.parseInt(dfTime.format(Calendar.getInstance().getTime()));
//                if (time >= 19 || time <= 7) {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                } else {
//                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
//                }
//                break;
//            case "NIGHT_MODE_SYSTEM" :
//            default:
//                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM);
//                break;
//        }
//    }

    private void settingDefaultNormalTheme () {
        if (theme_preference == null)
            theme_preference = getSharedPreferences(com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_PREFERENCE, MODE_PRIVATE);
        if (theme_preference_editor == null)
            theme_preference_editor = theme_preference.edit();
        theme_preference_editor.putString(THEME_TYPE_PREFERENCE, com.sanket_satpute_20.playme.project.activity.ThemesActivity.NORMAL_THEME);
        theme_preference_editor.apply();
        theme_preference_editor.putString(com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_NORMAL_SELECTED_NORMAL, "NIGHT_MODE_SYSTEM");
        theme_preference_editor.apply();
        getThemeOfUI();
        setBackgroundTheme();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        com.sanket_satpute_20.playme.project.service.BackService.LocalBinder binder = (com.sanket_satpute_20.playme.project.service.BackService.LocalBinder) iBinder;
        service = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

//    private void setRemoteSetting() {
//        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//        ComponentName reciverComponent = new ComponentName(this, RemoteControlReciver.class);
//        audioManager.registerMediaButtonEventReceiver(reciverComponent);
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PERMISSION_DELETE :
                if (resultCode != 0)
                    sendBroadcastForSongDeleted();
                else    // User denied permission, notify the adapter
                    Toast.makeText(this, "Not Deleted", Toast.LENGTH_SHORT).show();
            case REQUEST_PERMISSION_DELETE_ALBUM:
                if (resultCode != 0) {
                    Intent broad_deleted_songs_removing = new Intent(this, MyAppReceiver.class);
                    broad_deleted_songs_removing.setAction("songs_deleted.SONG_IS_DELETED.Multiple_Album_Artist.Action");
                    broad_deleted_songs_removing.putExtra("DELETED_SONG_LIST_REMOVABLE", DELETING_MUSIC_FILES_OF_ALBUM);
                    broad_deleted_songs_removing.putExtra("DELETED_SONG_LIST_TYPE", "AlbumsMultiple");
                    MainActivity.this.sendBroadcast(broad_deleted_songs_removing);
                    DELETING_MUSIC_FILES_OF_ALBUM = null;
                    Toast.makeText(this, "album deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "failed to delete album", Toast.LENGTH_SHORT).show();
                }
                break;
            case UPDATE_SONG_TAG_REQUEST:
                if (resultCode != 0)
                    Toast.makeText(this, "Updated text", Toast.LENGTH_SHORT).show();
                else    // User denied permission, notify the adapter
                    Toast.makeText(this, "Not Updated text", Toast.LENGTH_SHORT).show();
            case ARTIST_FINISHING_REQUEST_CODE :
                if (data != null) {
                    broad_intent_to_artist_frag_resumed = new Intent();
                    broad_intent_to_artist_frag_resumed.setAction("songs_is_removed_OR_Full_Artist_is_removed.DELETED.Action");
                    broad_intent_to_artist_frag_resumed.putExtra("REMOVED_ALL", data.getBooleanExtra("REMOVED_ALL", false));
                    broad_intent_to_artist_frag_resumed.putExtra("ARTIST_REMOVAL_POSITION", data.getIntExtra("RETURNING_VALUE", -1));
                }
                break;
            case ALBUM_FINISHING_REQUEST_CODE :
                if (data != null) {
                    broad_intent_to_album_frag_resumed = new Intent();
                    broad_intent_to_album_frag_resumed.setAction("songs_is_removed_OR_Full_Album_is_removed.DELETED.Action");
                    broad_intent_to_album_frag_resumed.putExtra("REMOVED_ALL", true);
                    broad_intent_to_album_frag_resumed.putExtra("REMOVED_ALBUM_POSITION", data.getIntExtra("RETURNING_VALUE", -1));
                }
                break;
            case SELECT_MULTIPLE_DELETION_REQUEST_CODE :
                if (data != null) {
                    broad_intent_song_recycler_items_removed_selection = new Intent();
                    broad_intent_song_recycler_items_removed_selection.setAction("song_is_removed.FROM_SELECTION_SOME_SONGS_OR_ALL_ARE.Removed_For_All_Fragment");
                    broad_intent_song_recycler_items_removed_selection.putExtra("ITEMS_ARE_REMOVED", data.getBooleanExtra("ITEMS_ARE_REMOVED", true));
                }
                break;
            case VOICE_RECOGNITION_REQUEST_CODE :
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.linear_layout);
                if (fragment instanceof SearchFragment && data != null) {
                    ArrayList<String> fun = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    ((SearchFragment) fragment).voiceRecognitionActivated(fun);
                }
                break;
            case 19012 :
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void sendBroadcastForSongDeleted() {
        Intent broad_item_removed = new Intent(MainActivity.this, MyAppReceiver.class);
        broad_item_removed.setAction("song_recycler.SONG_IS_DELETED.Action");
        broad_item_removed.putExtra("DELETED_ITEM_POSITION_EXTRAS", delete_song_removing_position);
        sendBroadcast(broad_item_removed);
//        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(broad_item_removed);

        delete_song_removing_position = -1;

        Toast.makeText(MainActivity.this, "File deleted", Toast.LENGTH_SHORT).show();
    }
}