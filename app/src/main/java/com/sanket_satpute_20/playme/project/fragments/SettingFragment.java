package com.sanket_satpute_20.playme.project.fragments;

import static com.sanket_satpute_20.playme.MainActivity.CURRENT_USER_IS_AVAILABLE;
import static com.sanket_satpute_20.playme.MainActivity.USER_FETCHED_COMPLETE;
import static com.sanket_satpute_20.playme.MainActivity.USER_FETCHING_FAILED_MSG;
import static com.sanket_satpute_20.playme.MainActivity.USER_FETCHING_IS_FAILED;
import static com.sanket_satpute_20.playme.MainActivity.rewardedInterstitialAdMainAct;
import static com.sanket_satpute_20.playme.project.account.service.AdWatchAfterTimeIntentService.isAdWatchAfterServiceRunning;
import static com.sanket_satpute_20.playme.project.account.service.TimerIntentService.CONSTANT_FINAL_TIME;
import static com.sanket_satpute_20.playme.project.account.extra_stuffes.CommonMethodsUser.createDailyTimeId;
import static com.sanket_satpute_20.playme.project.account.extra_stuffes.CommonMethodsUser.dailyAdWatchTimeLimit;
import static com.sanket_satpute_20.playme.project.account.extra_stuffes.CommonMethodsUser.getFirebaseStoragePath;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.APP_USING_TIME_REWARD_VALUE_STR;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.USER_IMAGE_BITMAP_CONST;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.ad_values_map;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isInternetOn;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isUsingSystemEqulizer;
import static com.sanket_satpute_20.playme.project.service.BackService.currentTime;
import static com.sanket_satpute_20.playme.project.service.BackService.recentPlayed;
import static com.sanket_satpute_20.playme.project.service.BackService.recentPlayed_duration;
import static com.sanket_satpute_20.playme.project.service.BackService.timer_set;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.audiofx.AudioEffect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.activity.CreateAccountActivity;
import com.sanket_satpute_20.playme.project.account.activity.EditProfileActivity;
import com.sanket_satpute_20.playme.project.account.activity.LoginActivity;
import com.sanket_satpute_20.playme.project.account.activity.RedeemMoneyActivity;
import com.sanket_satpute_20.playme.project.account.activity.RewardEarnedActivity;
import com.sanket_satpute_20.playme.project.account.activity.ShowUserDetailActivity;
import com.sanket_satpute_20.playme.project.account.bottom_fragment.PlayMeUseAccountDocumentationBottomFragment;
import com.sanket_satpute_20.playme.project.account.data.model.MTime;
import com.sanket_satpute_20.playme.project.account.data.model.MUser;
import com.sanket_satpute_20.playme.project.account.data.model.UMoney;
import com.sanket_satpute_20.playme.project.account.extra_stuffes.SaveUserImageCache;
import com.sanket_satpute_20.playme.project.account.service.AdWatchAfterTimeIntentService;
import com.sanket_satpute_20.playme.project.account.service.TimerIntentService;
import com.sanket_satpute_20.playme.project.activity.AboutActivity;
import com.sanket_satpute_20.playme.project.activity.CoreSettingActivity;
import com.sanket_satpute_20.playme.project.activity.EqulizerActivity;
import com.sanket_satpute_20.playme.project.activity.FeedbackActivity;
import com.sanket_satpute_20.playme.project.activity.ScanSongsActivity;
import com.sanket_satpute_20.playme.project.activity.ThemesActivity;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.SleepTimerLayoutFragment;
import com.sanket_satpute_20.playme.project.service.BackService;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//notAUserButWatchedAd()    - update method set custom ui
public class SettingFragment extends Fragment implements ServiceConnection {

    String[] slogans_for_offline_user = {"Unlock all of PlayMe's amazing features and start earning money today – sign in or sign up now!",
            "Don't miss out on PlayMe's money-making opportunities – sign in or sign up to access all features!",
            "Ready to earn money with PlayMe? Sign in or sign up now to unlock all the amazing features!",
            "Get the most out of PlayMe and start earning cash today – sign in or sign up to access everything!",
            "Join PlayMe's community of money-makers – sign in or sign up to access all the features and start earning!"};
    String[] headers_for_offline_user = {"Join the fun!", "Access features!", "Log in now!", "Experience everything!", "Get started today!"};

    public static final String GOOGLE_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=";

    protected RelativeLayout themes_setting, equalizer_setting, core_setting, sleep_timer_setting, scan_song_setting
            ,feedback, share_player, rate_us, about;
    SwitchCompat sleeper;

    TimerIntentService timerService;
    BackService service;
    AdWatchAfterTimeIntentService adWatchService;

//    RelativeLayout ad_layout;

    TextView t_counter, song_count, song_duration, most_played, recent_album;

//    Voice Recognition Related
    FloatingActionButton start_voice_recognition;
    TextView text_of_recognition;

//    user account related
    TextView user_name_txt, money_txt, coins_txt, documentation_txt, documentation_not_logged, not_logged_slogan_txt, not_logged_header_txt;
    MaterialButton sign_in_btn, sign_up_btn, watch_ad, watch_not_logged, redeem_coin_to_money, show_account, using_time_progress_complete_btn;
    ConstraintLayout signed_acc_layout, unsigned_acc_layout, fetching_data_layout, failed_to_load_layout;
    LinearProgressIndicator app_using_time_progress;
    CircularProgressIndicator user_dashboard_image_loading_progress;
//    failed
    TextView failed_msg_txt;
    ImageView failed_to_load_img, user_profile_outer, user_profile_picture;

    boolean isUserProgressCalled = false;

    //    user profile dialog
    Uri savedImageUri;

    //    final
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 110;
    public final String deleting_str = "Deleting";
    public final String deleted_str = "Deleted";
    public final String failed_str = "Failed";

    private boolean isUserCheckingFailed = false;
    private String failedMsg;
    boolean isImageDownloadedCompleted, isImageSqueezing = false;

    String[] month_names = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    FirebaseAuth f_auth;
    FirebaseFirestore f_db;
    FirebaseUser f_user;
    FirebaseRemoteConfig f_config;

//    AdMob Ad's
//    public static AdView adView;

    AlertDialog alert_ad_dialog;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent){
            switch (intent.getAction()) {
                case "player.android.timer.action.performed":
                        if(timer_set){
                            timerIsOn();
                        }else{
                            timerIsOff();
                        }
                    break;
                case ConnectivityManager.CONNECTIVITY_ACTION:
                        ConnectivityManager connectivityManager =
                                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                        isInternetOn = activeNetworkInfo != null && activeNetworkInfo.isConnected();
                        Log.d("isInternetOn", "" + isInternetOn);
//                        doExtra();
                    break;
                case USER_FETCHED_COMPLETE:
                        if (intent.getExtras() != null) {
                            if (intent.getBooleanExtra(USER_FETCHING_IS_FAILED, true)) {
//                                failed show failed dialog
                                showUserFetchFailed(intent.getStringExtra(USER_FETCHING_FAILED_MSG));
                            } else {
//                                success
                                if (intent.getBooleanExtra(CURRENT_USER_IS_AVAILABLE, false)) {
                                    showUserFetchedSuccess();
                                } else {
                                    showDontHaveUserLogOrCreateAC();
                                }
                            }
                        }
                    break;
            }
        }
    };

    private final ServiceConnection ad_watch_service_connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AdWatchAfterTimeIntentService.AdWatchAfterTimeBinder binder = (AdWatchAfterTimeIntentService.AdWatchAfterTimeBinder) service;
            adWatchService = binder.getService();
            if (currentUser != null)
                checkUserAdWatchAvailable();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            adWatchService = null;
        }
    };

    private final ServiceConnection timer_service_connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TimerIntentService.TimerBinder binder = (TimerIntentService.TimerBinder) iBinder;
            timerService = binder.getService();
            checkAccountIsAvailable();
            if (!isUserProgressCalled)
                whichUserTimeProgress();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            timerService = null;
        }
    };

    private void whichUserTimeProgress() {
        if (currentUser != null) {
            isUserProgressCalled = true;
            if (timerService.getCurrentTime() >= TimerIntentService.CONSTANT_FINAL_TIME - 2000
                    && !(currentUser.getmTime().get(currentUser.getmTime().size() - 1).isDailyRewardCollected())) {
                setUserUsingTimeRewardBtn();
                Log.d("ssvpg", "if : " + (timerService.getCurrentTime() >= TimerIntentService.CONSTANT_FINAL_TIME) +
                        " " + (!(currentUser.getmTime().get(currentUser.getmTime().size() - 1).isDailyRewardCollected())));
            } else {
                Log.d("ssvpg", "else : " + (timerService.getCurrentTime() >= TimerIntentService.CONSTANT_FINAL_TIME) +
                        " " + (!(currentUser.getmTime().get(currentUser.getmTime().size() - 1).isDailyRewardCollected())));
                setUserProgressBar();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initViews(view);
        setOnClick();
        isInternetOn = isNetworkAvailable(requireActivity());
//        doExtra();
        return view;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onResume() {
        super.onResume();
        isUserProgressCalled = false;
//      first broadcast
        Intent intent = new Intent(requireActivity(), BackService.class);
        requireActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
        Intent timer_service_intent = new Intent(requireActivity(), TimerIntentService.class);
        requireActivity().bindService(timer_service_intent, timer_service_connection, Context.BIND_AUTO_CREATE);
        registerInternetBroadcast();
        animateValue();
        sleeper.setChecked(timer_set);
        recent_album.setText(String.valueOf(BackService.recent_album.size()));
        most_played.setText(String.valueOf(BackService.most_played_songs.size()));
        if (timer_set)
            timerIsOn();
        else
            timerIsOff();
        Intent ad_watch_service_intent = new Intent(requireActivity(), AdWatchAfterTimeIntentService.class);
        requireActivity().bindService(ad_watch_service_intent, ad_watch_service_connection, Context.BIND_AUTO_CREATE);
//        if (adView != null)
//            adView.resume();
//        else
//            doExtra();
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unbindService(this);
        requireActivity().unbindService(timer_service_connection);
        unRegisterInternetBroadcast();
//        if (adView != null)
//            adView.pause();
        if (isAdWatchAfterServiceRunning && adWatchService != null) {
            try {
                requireActivity().unbindService(ad_watch_service_connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void registerInternetBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("player.android.timer.action.performed");
        filter.addAction(USER_FETCHED_COMPLETE);
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(receiver, filter);
    }

    private void unRegisterInternetBroadcast() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver);
    }

    private void initViews(View view) {
        themes_setting = view.findViewById(R.id.themes_setting);
        equalizer_setting = view.findViewById(R.id.equlizer_setting);
        sleep_timer_setting = view.findViewById(R.id.sleeptimer_setting);
        scan_song_setting = view.findViewById(R.id.scan_song_setting);
        sleeper = view.findViewById(R.id.sleeper);
        t_counter = view.findViewById(R.id.t_counter);
        core_setting = view.findViewById(R.id.core_setting);
        song_count = view.findViewById(R.id.ms_played_time);
        song_duration = view.findViewById(R.id.ls_time);
        feedback = view.findViewById(R.id.feedback);
        share_player = view.findViewById(R.id.share_player);
        rate_us = view.findViewById(R.id.rate_us);
        about = view.findViewById(R.id.about);
        most_played = view.findViewById(R.id.most_played_count_txt);
        recent_album = view.findViewById(R.id.album_recent_played_count);

//        user account related
        show_account = view.findViewById(R.id.show_account_btn);
        sign_up_btn = view.findViewById(R.id.sign_up_btn);
        sign_in_btn = view.findViewById(R.id.sign_in_btn);
        watch_ad = view.findViewById(R.id.watch_btn);
        user_name_txt = view.findViewById(R.id.user_name_txt);
        redeem_coin_to_money = view.findViewById(R.id.redeem_coin_to_money);
        documentation_txt = view.findViewById(R.id.documentation_txt);
        money_txt = view.findViewById(R.id.money);
        coins_txt = view.findViewById(R.id.coin);
        app_using_time_progress = view.findViewById(R.id.using_time_progress);
        using_time_progress_complete_btn = view.findViewById(R.id.using_time_progress_complete_btn);
        signed_acc_layout = view.findViewById(R.id.logged_in_screen);
        unsigned_acc_layout = view.findViewById(R.id.logged_out_screen);
        fetching_data_layout = view.findViewById(R.id.fetching_data_layout);
        failed_to_load_layout = view.findViewById(R.id.failed_to_load_data_layout);
        failed_msg_txt = view.findViewById(R.id.failed_to_load_msg);
        failed_to_load_img = view.findViewById(R.id.failed_to_load_img);
        documentation_not_logged = view.findViewById(R.id.not_logged_documentation_txt);
        watch_not_logged = view.findViewById(R.id.watch_without_sign_in_btn);
        user_profile_outer = view.findViewById(R.id.user_profile_outer);
        user_profile_picture = view.findViewById(R.id.user_picture);
        not_logged_slogan_txt = view.findViewById(R.id.not_logged_helper_txt);
        not_logged_header_txt = view.findViewById(R.id.not_logged_txt);
        user_dashboard_image_loading_progress = view.findViewById(R.id.user_dashboard_image_loading_progress);

//        AdMob Ad's
//        ad_layout = view.findViewById(R.id.ad_relative);

//        Voice Recognition
//        start_voice_recognition = view.findViewById(R.id.start_listening_play_me_assistance_fab);
        text_of_recognition = view.findViewById(R.id.text_of_recognition);
    }

//    private void doExtra() {
//        MobileAds.initialize(requireActivity(), initializationStatus -> loadAd());
//    }

    public void setOnClick() {
        equalizer_setting.setOnClickListener(view -> {
//            if (isUsingSystemEqulizer) {
//                try {
//                    Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
//                    intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, service.getAudioSessionId());
//                    intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, requireContext().getPackageName());
//                    intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
//                    launchSystemEqualizerActivity.launch(intent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Toast.makeText(requireActivity(), "Failed to start Equalizer", Toast.LENGTH_SHORT).show();
//                }
//            } else {
                Intent intent = new Intent(requireActivity(), EqulizerActivity.class);
                startActivity(intent);
//            }
        });

        themes_setting.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), ThemesActivity.class);
            requireActivity().startActivity(intent);
        });

        scan_song_setting.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), ScanSongsActivity.class);
            requireActivity().startActivity(intent);
        });

        sleep_timer_setting.setOnClickListener(view -> setSleepTimer());

        sleeper.setOnClickListener(view -> {
            if (timer_set) {
                timer_set = false;
                service.stopTimer();
                timerIsOff();
            } else {
                setSleepTimer();
            }
        });

        core_setting.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), CoreSettingActivity.class);
            requireActivity().startActivity(intent);
        });

        show_account.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), ShowUserDetailActivity.class);
            startActivity(intent);
        });

//        start_voice_recognition.setOnClickListener(view -> {
//            CustomRecognizer recognizer = new CustomRecognizer(requireActivity());
//            recognizer.StartListening();
//        });

        sign_up_btn.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), CreateAccountActivity.class);
            startActivity(intent);
        });

        sign_in_btn.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
        });

        redeem_coin_to_money.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), RedeemMoneyActivity.class);
            startActivity(intent);
        });

        watch_ad.setOnClickListener(view -> loadRewardedAd());

        using_time_progress_complete_btn.setOnClickListener(view -> collectDailyPoints());

        failed_to_load_layout.setOnClickListener(view -> {
            ObjectAnimator animLowAlpha = ObjectAnimator.ofFloat(failed_to_load_img, "rotation", 0f, 360f);
            animLowAlpha.setDuration(900);
            animLowAlpha.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    signed_acc_layout.setVisibility(View.GONE);
                    failed_to_load_layout.setVisibility(View.GONE);
                    fetching_data_layout.setVisibility(View.VISIBLE);
                    unsigned_acc_layout.setVisibility(View.GONE);
                    checkAccountIsAvailable();
                }
            });
            animLowAlpha.start();
        });

        watch_not_logged.setOnClickListener(view -> loadRewardedAd());

        documentation_not_logged.setOnClickListener(view -> {
//            call the bottom sheet to show what is it and how to use
            PlayMeUseAccountDocumentationBottomFragment bottomDocumentationFragment =
                    new PlayMeUseAccountDocumentationBottomFragment();
            bottomDocumentationFragment.show(requireActivity().getSupportFragmentManager(), bottomDocumentationFragment.getTag());
        });

        documentation_txt.setOnClickListener(view -> {
//            call the bottom sheet to show what is it and how to use
            PlayMeUseAccountDocumentationBottomFragment bottomDocumentationFragment =
                    new PlayMeUseAccountDocumentationBottomFragment();
            bottomDocumentationFragment.show(requireActivity().getSupportFragmentManager(), bottomDocumentationFragment.getTag());
        });

        user_profile_picture.setOnClickListener(view -> {
//            set animation of profile image squeeze release or rounding
            if (!isImageSqueezing) {
                isImageSqueezing = true;
                showSqueezingAnimation();
            }
//            after that set animation of dialog view name is coming from up to down like
        });

        feedback.setOnClickListener(view -> sendFeedback());
        share_player.setOnClickListener(view -> shareApp());
        rate_us.setOnClickListener(view -> launchInAppReview());
        about.setOnClickListener(view -> aboutMethod());
    }

    @SuppressLint("SetTextI18n")
    private void progressTracker(float progress) {
        if (timer_set)t_counter.setText(("0" + getHours((int) progress) + " : " +
                getMins((int) progress) + " : " +
                getSecs((int)progress)));
        else
            t_counter.setText(R.string.off_txt);
    }

    public int getHours(int time) {
        return ((time / (60*60*1000)) % 24);
    }

    public String getMins(int time) {
        int mm = ((time / (1000*60)) % 60);
        if (mm >= 0 && mm <= 9)
            return "0"+mm;
        return String.valueOf((time / (1000*60) % 60));
    }

    public String getSecs(int time) {
        int ss = (time / 1000) % 60;
        if (ss >= 0 && ss <= 9)
            return "0"+ss;
        return String.valueOf((time / 1000) % 60);
    }

    private void timerIsOn() {
        Handler handler = new Handler();
        Runnable thread;
        thread = new Runnable() {
            @Override
            public void run() {
                if(timer_set && currentTime >= 0) {
                    progressTracker(currentTime);
                    handler.postDelayed(this, 1000);
                }
            }
        };
        requireActivity().runOnUiThread(thread);
    }

    private void timerIsOff() {
        sleeper.setChecked(false);
        t_counter.setText(R.string.off_txt);
    }

    public void animateValue() {
        ValueAnimator song = ValueAnimator.ofInt(0, recentPlayed.size());
        song.setDuration(300);
        song.addUpdateListener(valueAnimator -> song_count.setText(valueAnimator.getAnimatedValue().toString()));

        float t = Float.parseFloat(getMinutes(recentPlayed_duration));
        ValueAnimator time = ValueAnimator.ofFloat(.0f, t);
        time.setDuration(300);
        time.addUpdateListener(valueAnimator -> song_duration.setText(valueAnimator.getAnimatedValue().toString()));

        AnimatorSet animator = new AnimatorSet();
        animator.play(song).with(time);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    public String getMinutes(int time) {
        return (time / (1000*60) % 60)+"."+((time / 1000) % 60);
    }

//    Account Related
    private void checkAccountIsAvailable() {
        if (currentUser != null) {
            showUserFetchedSuccess();
        } else {
            if(checkForUser()) {
                checkAccountIsAvailable();
            } else {
                if (isUserCheckingFailed)
                    showUserFetchFailed(failedMsg);
                else
                    showDontHaveUserLogOrCreateAC();
            }
        }
    }

    Handler handler = new Handler();
    int i = 0;

    private void setUserProgressBar() {
        if (currentUser != null) {
            app_using_time_progress.setMax(CONSTANT_FINAL_TIME);
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (currentUser != null && timerService != null) {
                        long current_time = timerService.getCurrentTime();
                        app_using_time_progress.setProgress((int) current_time);
                        if (i < 1
                                && !(currentUser.getmTime().get(currentUser.getmTime().size() - 1).isDailyRewardCollected())
                                && current_time >= CONSTANT_FINAL_TIME) {
                            startUsingTimeProgressFullAnim();
                            i++;
                        }
                        /*if (current_time <= CONSTANT_FINAL_TIME) {        //      discontinue due to directly implementing else block
                            handler.postDelayed(this, 1000);
                        }*/
                        else {
                            handler.postDelayed(this, 1000);
                        }
                    }
                }
            });
        }
    }

//    user related
    private boolean checkForUser() {
        signed_acc_layout.setVisibility(View.GONE);
        failed_to_load_layout.setVisibility(View.GONE);
        fetching_data_layout.setVisibility(View.VISIBLE);
        unsigned_acc_layout.setVisibility(View.GONE);

        FirebaseAuth f_auth;
        FirebaseUser f_user;
        CollectionReference f_fire_store;
        f_auth = FirebaseAuth.getInstance();
        f_user = f_auth.getCurrentUser();
        f_fire_store = FirebaseFirestore.getInstance().collection("users");
        if (f_user != null) {
            Task<DocumentSnapshot> u_ref = f_fire_store.document(f_user.getUid()).get();
            u_ref.addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.getData() != null) {
                    Log.d("detail_user", documentSnapshot.getData().toString());
                    currentUser = documentSnapshot.toObject(MUser.class);
                }
                isUserCheckingFailed = false;
            }).addOnFailureListener(e ->  {
                isUserCheckingFailed = true;
                failedMsg = e.getMessage();
            });
        }
        return (currentUser != null);
    }

    private void setUserUsingTimeRewardBtn() {
        using_time_progress_complete_btn.setVisibility(View.VISIBLE);
        app_using_time_progress.setVisibility(View.INVISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(() -> startUsingTimeBtnTiltAnim(2.5f), new Random().nextInt(5000));
    }

    private void showUserFetchedSuccess() {
        String full_name;

        full_name = currentUser.getFull_name();
        user_name_txt.setText(full_name);
        coins_txt.setText(String.valueOf(currentUser.getTotalCoins()));
        money_txt.setText(String.valueOf(currentUser.getTotalMoney()));

        UserImageLoading user_background_image_load = new UserImageLoading(true, user_profile_picture);
        user_background_image_load.execute();

        checkUserAdWatchAvailable();

        if (!isUserProgressCalled && timerService != null)
            whichUserTimeProgress();

        signed_acc_layout.setVisibility(View.VISIBLE);
        failed_to_load_layout.setVisibility(View.GONE);
        fetching_data_layout.setVisibility(View.GONE);
        unsigned_acc_layout.setVisibility(View.GONE);
    }

    private void showUserFetchFailed(String msg) {
        signed_acc_layout.setVisibility(View.GONE);
        failed_to_load_layout.setVisibility(View.VISIBLE);
        fetching_data_layout.setVisibility(View.GONE);
        unsigned_acc_layout.setVisibility(View.GONE);
        if (msg != null) {
            failed_msg_txt.setText(msg);
        } else {
            failed_msg_txt.setVisibility(View.GONE);
        }
    }

    private void showDontHaveUserLogOrCreateAC() {
        signed_acc_layout.setVisibility(View.GONE);
        failed_to_load_layout.setVisibility(View.GONE);
        fetching_data_layout.setVisibility(View.GONE);
        unsigned_acc_layout.setVisibility(View.VISIBLE);
        not_logged_slogan_txt.setText(slogans_for_offline_user[new Random().nextInt(slogans_for_offline_user.length)]);
        not_logged_header_txt.setText(headers_for_offline_user[new Random().nextInt(headers_for_offline_user.length)]);
    }

    private void checkUserAdWatchAvailable() {
        if (isAdWatchAfterServiceRunning) {
            Log.d("TAG-AD", "Timer is on");
            int ad_watch_btn_disable_color = Color.parseColor("#39d9d9d9");
//            set button not clickable and dark-gray color and show time
//            add the broadcast receiver to check is time up if yes then convert button to clickable
            watch_ad.setClickable(false);
            watch_ad.setAlpha(0.6f);
            watch_ad.setBackgroundColor(ad_watch_btn_disable_color);
            watch_ad.setTextColor(Color.BLACK);
            setUserAdWatchTimer();
        } else {
            Log.d("TAG-AD", "Timer is off");
            int ad_watch_btn_enable_color = Color.parseColor("#DACC50");
//            set button clickable and normal color
            watch_ad.setClickable(true);
            String watch_str = "WATCH";
            watch_ad.setText(watch_str);
            watch_ad.setAlpha(1f);
            watch_ad.setBackgroundColor(ad_watch_btn_enable_color);
            watch_ad.setTextColor(redeem_coin_to_money.getTextColors());
        }
    }

    private void setUserAdWatchTimer() {
        Handler handler = new Handler();
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String watch_btn_timer_str = "ERROR";
                if (adWatchService != null) {
                    long time_remain = adWatchService.getRemainTime();
                    int hours = getHours((int) time_remain);
                    String mins = getMins((int) time_remain);
                    String secs = getSecs((int) time_remain);
                    if (hours > 0)
                        watch_btn_timer_str = hours + ":" + mins + ":" + secs + " H";
                    else if (Integer.parseInt(mins) > 0)
                        watch_btn_timer_str = TimeUnit.MILLISECONDS.toMinutes(time_remain) + ":" + secs + " MIN";
                    else
                        watch_btn_timer_str = secs + " SEC";
                    watch_ad.setText(watch_btn_timer_str);
                    if (time_remain > 1000) {
                        Log.d("TAG_AD", "IT IS ON " + time_remain);
                        handler.postDelayed(this, 1000);
                    }
                    else {
                        Log.d("TAG_AD", "IT IS OFF ");
                        watch_btn_timer_str = "WATCH";
                        int ad_watch_btn_enable_color = Color.parseColor("#DACC50");
                        watch_ad.setClickable(true);
                        watch_ad.setAlpha(1f);
                        watch_ad.setText(watch_btn_timer_str);
                        watch_ad.setBackgroundColor(ad_watch_btn_enable_color);
                        watch_ad.setTextColor(redeem_coin_to_money.getTextColors());
                    }
                } else {
                    watch_ad.setText(watch_btn_timer_str);
                }
            }
        });
    }

    public void notAUserButWatchedAd() {
//        give got it button
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.ad_watched_not_user_dialog_layout, null);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(requireActivity(), R.style.RoundedCornerAlertDialogSmall);
        dialog.setView(view);
        MaterialButton got_it_btn = view.findViewById(R.id.got_it_btn);
        AlertDialog alert = dialog.create();
        alert.setCancelable(false);
        alert.show();
        got_it_btn.setOnClickListener(v -> alert.dismiss());
    }

    private Bitmap setUserProfilePicture() {
        try {
            USER_IMAGE_BITMAP_CONST = SaveUserImageCache.getImage(requireActivity(), "user_profile_image");
            if (USER_IMAGE_BITMAP_CONST != null && !requireActivity().isDestroyed()) {
                return USER_IMAGE_BITMAP_CONST;
            } else if (currentUser.getProfilePicturePath() != null && !requireActivity().isDestroyed()) {
                isImageDownloadedCompleted = false;
                squeezeAnimation(1000, null, true);
                user_profile_picture.setAlpha(0.6f);
                StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(
                        getFirebaseStoragePath() +
                                currentUser.getProfilePicturePath());

                final long ONE_MEGABYTE = 1024 * 1024 * 5;

                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                    user_profile_picture.setAlpha(1f);
                    isImageDownloadedCompleted = true;
                    USER_IMAGE_BITMAP_CONST = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    SaveUserImageCache.putImage(requireActivity(), "user_profile_image", USER_IMAGE_BITMAP_CONST);
                }).addOnFailureListener(exception -> {
                    user_profile_picture.setAlpha(1f);
                    isImageDownloadedCompleted = true;
                    Toast.makeText(requireActivity(), "Failed to load Image", Toast.LENGTH_SHORT).show();
                });
                return USER_IMAGE_BITMAP_CONST;
            } else {
                currentUser.setProfilePicturePath(null);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    int myDailyEarnedPoints = 0;
    private void collectDailyPoints() {
        if (currentUser != null) {
            if (!(currentUser.getmTime().get(currentUser.getmTime().size() - 1).isDailyRewardCollected())) {

                String collect_reward_str = "Collect Reward";

                TextView dailyEarnedPoints;
                MaterialButton collectRewardBtn;
                CircularProgressIndicator collect_reward_progress;
                LottieAnimationView two_dots_animation;
                View v = LayoutInflater.from(requireActivity()).inflate(R.layout.app_using_time_reward_layout, null);
                MaterialAlertDialogBuilder alert_builder = new MaterialAlertDialogBuilder(requireActivity(), R.style.RoundedCornerAlertDialog)
                        .setView(v);
                AlertDialog dialog = alert_builder.create();
                dialog.setCancelable(false);
                dialog.show();

                if (f_config == null)
                    connectToFirebase();
                myDailyEarnedPoints = (int) f_config.getLong(APP_USING_TIME_REWARD_VALUE_STR);

                dailyEarnedPoints = v.findViewById(R.id.points_txt);
                collectRewardBtn = v.findViewById(R.id.collect_reward_btn);
                two_dots_animation = v.findViewById(R.id.daily_points_celebration_two_dots_lottie_animation);
                collect_reward_progress = v.findViewById(R.id.collect_reward_btn_progress);

                dailyEarnedPoints.setText(String.valueOf(myDailyEarnedPoints));
                two_dots_animation.addAnimatorListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        two_dots_animation.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        two_dots_animation.setVisibility(View.GONE);
                    }
                });
                collectRewardBtn.setOnClickListener(view -> {
                    collect_reward_progress.setVisibility(View.VISIBLE);
                    collectRewardBtn.setText("");
                    currentUser.setTotalCoins(currentUser.getTotalCoins() + myDailyEarnedPoints);
                    UMoney uMoney;
                    MTime mTime;
                    String todayDate = String.valueOf(new Date(System.currentTimeMillis()));
                    String mTimeLastDate = currentUser.getmTime().get(currentUser.getmTime().size() - 1).getDaily_limit_date();
                    if (todayDate.trim().equalsIgnoreCase(mTimeLastDate.trim())) {
                        currentUser.getmTime().get(currentUser.getmTime().size() - 1).setDailyRewardCollected(true);
//                        uTime set all fields set usage time completed
                    } else {
                        mTime = new MTime(createDailyTimeId(), currentUser.getmTime().size(), 10800000L,
                                0L, "Incomplete", todayDate, true, dailyAdWatchTimeLimit());
                        currentUser.getmTime().add(currentUser.getmTime().size(), mTime);
//                        create new object of uTime set all fields set usage time completed
                    }
                    if (currentUser.getuMoney() == null) {
                        ArrayList<UMoney> uMoneyList = new ArrayList<>();
                        uMoneyList.add(0, new UMoney("", myDailyEarnedPoints, 0, todayDate,
                                String.valueOf(new Time(System.currentTimeMillis()))));
                        currentUser.setuMoney(uMoneyList);
                    } else if (currentUser.getuMoney().size() <= 0) {
                        currentUser.getuMoney().add(0, new UMoney("", myDailyEarnedPoints, 0, todayDate,
                                String.valueOf(new Time(System.currentTimeMillis()))));
                    }
                    String lastDate = currentUser.getuMoney().get(currentUser.getuMoney().size() - 1).getMoneyDate();
                    if (todayDate.trim().equalsIgnoreCase(lastDate.trim())) {
                        uMoney = currentUser.getuMoney().get(currentUser.getuMoney().size() - 1);
                        uMoney.setCoins(uMoney.getCoins() + myDailyEarnedPoints);
                        currentUser.getuMoney().set(currentUser.getuMoney().size() - 1, uMoney);
                    } else {
                        uMoney = new UMoney("", myDailyEarnedPoints, 0, todayDate,
                                String.valueOf(new Time(System.currentTimeMillis())));
                        currentUser.getuMoney().add(currentUser.getuMoney().size(), uMoney);
                    }
                    if (f_auth == null || f_db == null || f_config == null)
                        connectToFirebase();
                    if (f_user == null)
                        getUser();
                    f_db.collection("users").document(f_user.getUid()).set(currentUser)
                            .addOnSuccessListener(unused -> {   // saved data successful
                                collect_reward_progress.setVisibility(View.GONE);
                                collectRewardBtn.setText(collect_reward_str);
                                dialog.dismiss();
                                startCoinGrowAnim(myDailyEarnedPoints);
                                startUsingTimeBtnToProgress();
                            }).addOnFailureListener(e -> {      // saving data failed
                                collect_reward_progress.setVisibility(View.GONE);
                                collectRewardBtn.setText(collect_reward_str);
                                if (todayDate.trim().equals(lastDate)) {
                                    currentUser.getuMoney().get(currentUser.getuMoney().size() - 1)
                                            .setCoins(currentUser.getuMoney().get(currentUser.getuMoney().size() - 1)
                                                    .getCoins() - myDailyEarnedPoints);
                                } else {
                                    currentUser.getuMoney().remove(currentUser.getuMoney().size() - 1);
                                } if (todayDate.trim().equalsIgnoreCase(mTimeLastDate.trim())) {
                                    currentUser.getmTime().get(currentUser.getmTime().size() - 1).setDailyRewardCollected(false);
                                }
                                currentUser.setTotalCoins(currentUser.getTotalCoins() - myDailyEarnedPoints);
                                dialog.dismiss();
                            });
                });
            } else {
                Toast.makeText(requireActivity(), "Today's Reward Already Collected", Toast.LENGTH_SHORT).show();
                startUsingTimeBtnToProgress();
            }
        }
    }

    //    dialog methods

    private void adLoadingAlertDialog() {
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.loading_alert_dialog_layout, null);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(requireActivity(), R.style.RoundedCornerAlertDialogSmall);
        dialog.setView(view);
        alert_ad_dialog = dialog.create();
        alert_ad_dialog.show();
        alert_ad_dialog.setCancelable(false);
    }

    int setMaxImageUploadedProgress = 0;

    private void showUserProfileDialog() {
        try {
            if (currentUser != null && !requireActivity().isDestroyed()) {
                MaterialButton change_image_btn, remove_image_btn, edit_info_btn, hide_btn, set_image_btn;
                TextView coin_t, money_t, mobile_no_t, email_t, username_t, upi_id_t, dob_t, age_t, user_full_name_t,
                        image_uploading_percentage, email_txt, username_txt, upi_id_txt, dob_txt, age_txt, joined_date_str, joined_txt;
                ImageView user_profile_image;
                ProgressBar image_uploading_downloading_progress;

                View view = LayoutInflater.from(requireActivity()).inflate(R.layout.user_image_click_show_user_profile_layout, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
                alert.setView(view);
                AlertDialog dialog = alert.create();

                user_profile_image = view.findViewById(R.id.user_profile_image);
                change_image_btn = view.findViewById(R.id.change_or_set_user_profile_picture);
                remove_image_btn = view.findViewById(R.id.remove_user_profile_picture);
                set_image_btn = view.findViewById(R.id.set_image_btn);
                image_uploading_downloading_progress = view.findViewById(R.id.image_uploading_and_downloading_progress);
                user_full_name_t = view.findViewById(R.id.user_full_name);
                coin_t = view.findViewById(R.id.coin_txt);
                money_t = view.findViewById(R.id.money_txt);
                mobile_no_t = view.findViewById(R.id.mob_no);
                email_t = view.findViewById(R.id.email);
                username_t = view.findViewById(R.id.username);
                upi_id_t = view.findViewById(R.id.upi_id);
                dob_t = view.findViewById(R.id.dob);
                age_t = view.findViewById(R.id.age);
                edit_info_btn = view.findViewById(R.id.edit_profile_btn);
                hide_btn = view.findViewById(R.id.hide_profile_btn);
                joined_date_str = view.findViewById(R.id.joined_date_txt);
                joined_txt = view.findViewById(R.id.joined_txt);
                image_uploading_percentage = view.findViewById(R.id.image_uploading_percentage);

                email_txt = view.findViewById(R.id.email_txt);
                username_txt = view.findViewById(R.id.username_txt);
                upi_id_txt = view.findViewById(R.id.upi_id_txt);
                dob_txt = view.findViewById(R.id.dob_txt);
                age_txt = view.findViewById(R.id.age_txt);

                String full_name_str = currentUser.getFull_name();
                String coins_str = String.valueOf(currentUser.getTotalCoins());
                String money_str = String.valueOf(currentUser.getTotalMoney());
                String mobile_no_str = String.valueOf(currentUser.getPhone_number());
                String email_str = currentUser.getEmail_id();
                String username_str = currentUser.getUser_name();
                String upi_str = currentUser.getUpiID();
                String dob_str = currentUser.getBirthDate();
                String joined_date = currentUser.getAccountCreationDate();

                Bitmap user_image = SaveUserImageCache.getImage(requireActivity(), "user_profile_image");
                if (user_image != null) {
                    change_image_btn.setVisibility(View.VISIBLE);
                    remove_image_btn.setVisibility(View.VISIBLE);
                    set_image_btn.setVisibility(View.GONE);
                    Glide.with(this)
                            .asBitmap()
                            .error(R.drawable.orange_man_user_profile_picture)
                            .load(user_image)
                            .into(user_profile_image);
                    if (savedImageUri != null && currentUser.getProfilePicturePath() == null && f_auth.getCurrentUser() != null) {
                        change_image_btn.setVisibility(View.GONE);
                        remove_image_btn.setVisibility(View.GONE);
                        image_uploading_downloading_progress.setVisibility(View.VISIBLE);
                        image_uploading_percentage.setVisibility(View.VISIBLE);
                        image_uploading_downloading_progress.setIndeterminate(false);
                        user_profile_image.setAlpha(0.6f);
                        currentUser.setProfilePicturePath("image/" + f_auth.getCurrentUser().getUid() + "/profilePicture/image.png");
                        StorageReference f_storage = FirebaseStorage.getInstance().getReference();
                        f_storage.child("image/" + f_auth.getCurrentUser().getUid() + "/profilePicture/image.png")
                                .putFile(savedImageUri)
                                .addOnProgressListener(snapshot -> {
                                    String uploadedImagePercent = "0%";
                                    if (snapshot.getBytesTransferred() > 0) {
                                        uploadedImagePercent = (int) (((float) snapshot.getBytesTransferred() / (float) snapshot.getTotalByteCount()) * 100.0f) + "%";
                                    }
                                    Log.d("data_uploaded", "Max : " + snapshot.getTotalByteCount() + " Uploaded : " + snapshot.getBytesTransferred() + "  % : " + uploadedImagePercent);
                                    image_uploading_percentage.setText(uploadedImagePercent);
                                    image_uploading_downloading_progress.setProgress((int) snapshot.getBytesTransferred());
                                    if (setMaxImageUploadedProgress == 0) {
                                        image_uploading_downloading_progress.setMax((int) snapshot.getTotalByteCount());
                                        setMaxImageUploadedProgress++;
                                    }
                                    if (snapshot.getBytesTransferred() >= snapshot.getTotalByteCount()) {
                                        change_image_btn.setVisibility(View.VISIBLE);
                                        remove_image_btn.setVisibility(View.VISIBLE);
                                        image_uploading_downloading_progress.setVisibility(View.GONE);
                                        image_uploading_percentage.setVisibility(View.GONE);
                                        user_profile_image.setAlpha(1f);
                                        setMaxImageUploadedProgress = 0;
                                    }
                                })
                                .continueWithTask(task -> {
                                    if (!task.isSuccessful()) {
                                        dialog.dismiss();
                                    }
                                    return f_storage.getDownloadUrl();
                                })
                                .addOnSuccessListener(taskSnapshot -> {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    f_storage.getDownloadUrl()
                                            .addOnSuccessListener(uri ->
                                                    database.getReference().child("user_profile_picture.png")
                                                            .setValue(uri.toString()).addOnSuccessListener(unused -> {
                                                                change_image_btn.setVisibility(View.VISIBLE);
                                                                remove_image_btn.setVisibility(View.VISIBLE);
                                                                image_uploading_downloading_progress.setVisibility(View.GONE);
                                                                image_uploading_percentage.setVisibility(View.GONE);
                                                                user_profile_image.setAlpha(1f);
                                                                Toast.makeText(requireActivity(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                                                Toast.makeText(requireActivity(), "Details Updated Successfully", Toast.LENGTH_SHORT).show();
                                                                setMaxImageUploadedProgress = 0;
                                                            }).addOnFailureListener(e -> {
                                                                e.printStackTrace();
                                                                change_image_btn.setVisibility(View.VISIBLE);
                                                                remove_image_btn.setVisibility(View.VISIBLE);
                                                                image_uploading_downloading_progress.setVisibility(View.GONE);
                                                                image_uploading_percentage.setVisibility(View.GONE);
                                                                user_profile_image.setAlpha(1f);
                                                                setMaxImageUploadedProgress = 0;
                                                            }))
                                            .addOnFailureListener(e -> {
                                                e.printStackTrace();
                                                change_image_btn.setVisibility(View.VISIBLE);
                                                remove_image_btn.setVisibility(View.VISIBLE);
                                                image_uploading_downloading_progress.setVisibility(View.GONE);
                                                image_uploading_percentage.setVisibility(View.GONE);
                                                user_profile_image.setAlpha(1f);
                                                setMaxImageUploadedProgress = 0;
                                            });
                                }).addOnFailureListener(e -> {
                                    e.printStackTrace();
                                    change_image_btn.setVisibility(View.VISIBLE);
                                    remove_image_btn.setVisibility(View.VISIBLE);
                                    image_uploading_downloading_progress.setVisibility(View.GONE);
                                    image_uploading_percentage.setVisibility(View.GONE);
                                    user_profile_image.setAlpha(1f);
                                    setMaxImageUploadedProgress = 0;
                                });
                    }
                } else if (currentUser.getProfilePicturePath() != null && !requireActivity().isDestroyed()) {
                    change_image_btn.setVisibility(View.VISIBLE);
                    remove_image_btn.setVisibility(View.VISIBLE);
                    set_image_btn.setVisibility(View.GONE);
                    change_image_btn.setClickable(false);
                    remove_image_btn.setClickable(false);
                    image_uploading_downloading_progress.setIndeterminate(true);
                    user_profile_image.setAlpha(0.6f);
                    image_uploading_downloading_progress.setVisibility(View.VISIBLE);
                    StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(
                            getFirebaseStoragePath() +
                                    currentUser.getProfilePicturePath());
                    final long ONE_MEGABYTE = 1024 * 1024 * 5;
                    islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                        change_image_btn.setClickable(true);
                        remove_image_btn.setClickable(true);
                        Glide.with(requireActivity())
                                .load(bytes)
                                .error(R.drawable.orange_man_user_profile_picture)
                                .into(user_profile_image);
                        user_profile_image.setAlpha(1f);
                        image_uploading_downloading_progress.setVisibility(View.GONE);
                        Bitmap userBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        SaveUserImageCache.putImage(requireActivity(), "user_profile_image", userBitmap);
                    }).addOnFailureListener(exception -> {
                        change_image_btn.setVisibility(View.VISIBLE);
                        remove_image_btn.setVisibility(View.VISIBLE);
                        set_image_btn.setVisibility(View.GONE);
                        user_profile_image.setAlpha(1f);
                        image_uploading_downloading_progress.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "Failed to load Image", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    change_image_btn.setVisibility(View.GONE);
                    remove_image_btn.setVisibility(View.GONE);
                    set_image_btn.setVisibility(View.VISIBLE);
                    currentUser.setProfilePicturePath(null);
                    user_profile_image.setImageResource(R.drawable.orange_man_user_profile_picture);
                    //            set default user image
                }

                if (joined_date != null) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            DateTimeFormatter formatterJoiningDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDate formattedJoinedDateStr = LocalDate.parse(joined_date, formatterJoiningDate);
                            String fullJoinedDate = formattedJoinedDateStr.getDayOfMonth() + " " +
                                    month_names[formattedJoinedDateStr.getMonthValue() - 1] + " " +
                                    formattedJoinedDateStr.getYear();
                            joined_date_str.setText(fullJoinedDate);
                        } else {
                            joined_date_str.setText(joined_date);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        joined_date_str.setText(joined_date);
                    }
                } else {
                    joined_txt.setVisibility(View.GONE);
                    joined_date_str.setVisibility(View.GONE);
                }

                user_full_name_t.setText(full_name_str);
                coin_t.setText(coins_str);
                money_t.setText(money_str);
                mobile_no_t.setText(mobile_no_str);
                if (email_str != null) {
                    email_t.setText(email_str);
                } else {
                    email_txt.setVisibility(View.GONE);
                    email_t.setVisibility(View.GONE);
                }

                if (username_str != null) {
                    username_t.setText(username_str);
                } else {
                    username_t.setVisibility(View.GONE);
                    username_txt.setVisibility(View.GONE);
                }

                if (upi_str != null) {
                    upi_id_t.setText(upi_str);
                } else {
                    upi_id_t.setVisibility(View.GONE);
                    upi_id_txt.setVisibility(View.GONE);
                }

                if (dob_str != null) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            String full_date_of_birth;
                            LocalDate birthDate = LocalDate.parse(dob_str);
                            full_date_of_birth = birthDate.getDayOfMonth() + " " + month_names[birthDate.getMonthValue() - 1] + " " + birthDate.getYear();
                            dob_t.setText(full_date_of_birth);
                            String years_old_str = Period.between(birthDate, LocalDate.now()).getYears() + " Year's Old";
                            age_t.setText(years_old_str);
                        } else {
                            dob_t.setVisibility(View.GONE);
                            dob_txt.setVisibility(View.GONE);
                            age_t.setVisibility(View.GONE);
                            age_txt.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        dob_t.setVisibility(View.GONE);
                        dob_txt.setVisibility(View.GONE);
                        age_t.setVisibility(View.GONE);
                        age_txt.setVisibility(View.GONE);
                    }
                } else {
                    dob_t.setVisibility(View.GONE);
                    dob_txt.setVisibility(View.GONE);
                    age_t.setVisibility(View.GONE);
                    age_txt.setVisibility(View.GONE);
                }
                dialog.show();

                hide_btn.setOnClickListener(v -> dialog.dismiss());
                edit_info_btn.setOnClickListener(v -> {
                    dialog.dismiss();
                    Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
                    startActivity(intent);
                });
                set_image_btn.setOnClickListener(v -> dialogSetImageDialog(dialog));
                change_image_btn.setOnClickListener(v -> dialogSetImageDialog(dialog));
                remove_image_btn.setOnClickListener(v -> dialogRemoveImageDialog(dialog));
            } else {
                Toast.makeText(requireActivity(), "User is Not Found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isImageDeletedFromCache = false;
    private void dialogRemoveImageDialog(AlertDialog recentDialog) {
        recentDialog.dismiss();

        MaterialButton deleteBtn, skipBtn;
        ProgressBar deleteBtnProgress;
        TextView deleteBtnTxt;

        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.delete_user_profile_picture_layout, null);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(requireActivity(), R.style.RoundedCornerAlertDialog);
        dialog.setView(view);
        dialog.setCancelable(false);

        deleteBtn = view.findViewById(R.id.delete_btn);
        skipBtn = view.findViewById(R.id.skip_btn);
        deleteBtnProgress = view.findViewById(R.id.delete_btn_progressbar);
        deleteBtnTxt = view.findViewById(R.id.delete_btn_txt);

        AlertDialog alert = dialog.create();
        alert.show();

        deleteBtn.setOnClickListener(v -> {
            deleteBtnTxt.setText(deleting_str);
            deleteBtnProgress.setVisibility(View.VISIBLE);
            File cacheDir = requireActivity().getCacheDir();
            File imageFile = new File(cacheDir, "user_profile_image");
            if (imageFile.exists()) {
                if (imageFile.delete()) {
                    isImageDeletedFromCache = true;
                    Toast.makeText(requireActivity(), "File is Deleted", Toast.LENGTH_SHORT).show();
                }
            }
            if (currentUser.getProfilePicturePath() != null) {
                StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(currentUser.getProfilePicturePath());
                imageRef.delete().addOnSuccessListener(aVoid -> {
                    deleteBtnProgress.setVisibility(View.GONE);

                    UserImageLoading user_background_image_load = new UserImageLoading(true, user_profile_picture);
                    user_background_image_load.execute();

                    deleteBtnTxt.setText(deleted_str);
                    if (isImageDeletedFromCache)
                        Toast.makeText(requireActivity(), "Image is Deleted", Toast.LENGTH_SHORT).show();
                    currentUser.setProfilePicturePath(null);
                    alert.dismiss();
                    showUserProfileDialog();
                }).addOnFailureListener(e -> {
                    deleteBtnProgress.setVisibility(View.GONE);
                    deleteBtnTxt.setText(failed_str);
                    Toast.makeText(requireActivity(), "Image Not Deleted", Toast.LENGTH_SHORT).show();
                    alert.dismiss();
                    showUserProfileDialog();
                });
            } else {
                alert.dismiss();
                showUserProfileDialog();
            }
        });

        skipBtn.setOnClickListener(v -> {
            alert.dismiss();
            showUserProfileDialog();
        });
    }

    private void dialogSetImageDialog(AlertDialog recentDialog) {
        recentDialog.dismiss();

        MaterialButton camera_btn, gallery_btn;
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.edit_or_choose_user_profile_picture_dialog_layout, null);
        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(requireActivity(), R.style.RoundedCornerAlertDialog);
        alert.setView(view);

        camera_btn = view.findViewById(R.id.open_camera_btn);
        gallery_btn = view.findViewById(R.id.open_gallery_btn);

        AlertDialog dialog = alert.create();
        dialog.show();

        camera_btn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{
                        Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                launchCameraImageChooserActivity.launch(intent);
                dialog.dismiss();
            }
        });

        gallery_btn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            launchGalleryImageChooserActivity.launch(intent);
            dialog.dismiss();
        });
    }

    //    on result activity
    ActivityResultLauncher<Intent> launchSystemEqualizerActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
//              don't do anything
            });

    ActivityResultLauncher<Intent> launchCameraImageChooserActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Bitmap userProfilePictureBitmap = (Bitmap) data.getExtras().get("data");
                        savedImageUri = SaveUserImageCache.putImage(requireActivity(), "user_profile_image", userProfilePictureBitmap);
                        currentUser.setProfilePicturePath(null);
                        showUserProfileDialog();

                        UserImageLoading user_background_image_load = new UserImageLoading(true, user_profile_picture);
                        user_background_image_load.execute();
                    }
                }
            });

    ActivityResultLauncher<Intent> launchGalleryImageChooserActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        try {
                            Bitmap userProfilePictureBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                            savedImageUri = SaveUserImageCache.putImage(requireActivity(), "user_profile_image", userProfilePictureBitmap);
                            currentUser.setProfilePicturePath(null);
                            showUserProfileDialog();

                            UserImageLoading user_background_image_load = new UserImageLoading(true, user_profile_picture);
                            user_background_image_load.execute();
                        } catch (IOException e) {
                            Toast.makeText(requireActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            });


    /*  Extra Methods   */
    private boolean isRotateAvailable() {
        Random random = new Random();
        return random.nextBoolean();
    }

    private int repeatTime() {
        int minimum = 1;
        int maximum = 5;
        return (int) (minimum + (Math.random() * ((maximum - minimum) + 1)));
    }

    private void sendFeedback() {
        Intent intent = new Intent(requireActivity(), FeedbackActivity.class);
        requireActivity().startActivity(intent);
    }

    private void shareApp() {
        int app_id = requireActivity().getApplicationInfo().labelRes;
        final String app_pkg_name = requireActivity().getPackageName();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, requireActivity().getString(app_id));
        String text = "Install this cool application: ";
        String link = "https://play.google.com/store/apps/details?id=" + app_pkg_name;
        intent.putExtra(Intent.EXTRA_TEXT, text + " " + link);
        startActivity(Intent.createChooser(intent, "Share link: "));
    }

    private void rateUsMethod() {
        try {
            Uri marketUri = Uri.parse("market://details?id=" + requireActivity().getPackageName());
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);
        } catch (ActivityNotFoundException e) {
            Uri marketUri = Uri.parse(GOOGLE_PLAY_STORE_URL + requireActivity().getPackageName());
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);
        }
    }

    private void launchInAppReview() {
        ReviewManager manager = ReviewManagerFactory.create(requireActivity());
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We got the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(requireActivity(), reviewInfo);
                flow.addOnCompleteListener(reviewFlowTask -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown.
                    SharedPreferences preferences = requireActivity().getSharedPreferences("PlayMe", Context.MODE_PRIVATE);
                    if (preferences.getBoolean("isRated", false)) {
                        rateUsMethod();
                    } else {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isRated", true);
                        editor.apply();
                        showThankYouDialog();
                    }
                });
            } else {
                // There was some problem, fall back to the default rating method
                rateUsMethod();
            }
        });
    }

    private void showThankYouDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Thank You!")
                .setMessage("Thank you for rating our app! ")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void aboutMethod() {
        Intent intent = new Intent(requireActivity(), AboutActivity.class);
        requireActivity().startActivity(intent);
    }

//    Animations
    private void startCoinGrowAnim(int coinsEarned) {
        if (currentUser != null) {
            ValueAnimator coinGrowAnim = ValueAnimator.ofInt((int) (currentUser.getTotalCoins() - coinsEarned),
                    (int) currentUser.getTotalCoins());
            coinGrowAnim.setDuration(300);
            coinGrowAnim.setInterpolator(new AccelerateInterpolator());
            coinGrowAnim.addUpdateListener(animation -> coins_txt.setText(String.valueOf(animation.getAnimatedValue())));
            coinGrowAnim.start();
        }
    }

    private void startUsingTimeBtnToProgress() {
        ObjectAnimator btnScaleDownXAnim = ObjectAnimator.ofFloat(using_time_progress_complete_btn, "scaleX", 1f, 0.01f);
        ObjectAnimator btnScaleDownYAnim = ObjectAnimator.ofFloat(using_time_progress_complete_btn, "scaleY", 1f, 0.01f);
        ObjectAnimator btnAlphaAnim = ObjectAnimator.ofFloat(using_time_progress_complete_btn, "alpha", 1f, 0f);
        AnimatorSet btnAnim = new AnimatorSet();
        btnAnim.playTogether(btnScaleDownXAnim, btnScaleDownYAnim, btnAlphaAnim);
        btnAnim.setDuration(300);
        btnAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                using_time_progress_complete_btn.setVisibility(View.GONE);
                app_using_time_progress.setScaleX(0);
                app_using_time_progress.setScaleY(0);
                app_using_time_progress.setAlpha(0);
                app_using_time_progress.setVisibility(View.VISIBLE);
                ObjectAnimator progressScaleUpXAnim = ObjectAnimator.ofFloat(app_using_time_progress, "scaleX", 0f, 1f);
                ObjectAnimator progressScaleUpYAnim = ObjectAnimator.ofFloat(app_using_time_progress, "scaleY", 0f, 1f);
                ObjectAnimator progressAlphaAnim = ObjectAnimator.ofFloat(app_using_time_progress, "alpha", 0f, 1f);
                AnimatorSet progressAnim = new AnimatorSet();
                progressAnim.playTogether(progressScaleUpXAnim, progressScaleUpYAnim, progressAlphaAnim);
                progressAnim.setDuration(200);
                progressAnim.start();
            }
        });
        btnAnim.start();
    }

    private void startUsingTimeProgressFullAnim() {
        ValueAnimator progressFullAnim = ValueAnimator.ofInt(0, app_using_time_progress.getMax());
        progressFullAnim.setDuration(150);
        progressFullAnim.addUpdateListener(animation ->
                app_using_time_progress.setProgress(Integer.parseInt(String.valueOf(animation.getAnimatedValue()))));
        progressFullAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startUsingTimeProgressToBtn();
            }
        });
        progressFullAnim.start();
    }

    private void startUsingTimeProgressToBtn() {
        ObjectAnimator progressScaleDownXAnim = ObjectAnimator.ofFloat(app_using_time_progress, "scaleX", 1f, 0f);
        ObjectAnimator progressScaleDownYAnim = ObjectAnimator.ofFloat(app_using_time_progress, "scaleX", 1f, 0f);
        ObjectAnimator progressAlphaAnim = ObjectAnimator.ofFloat(app_using_time_progress, "alpha", 1f, 0f);
        AnimatorSet progressAnim = new AnimatorSet();
        progressAnim.playTogether(progressScaleDownXAnim, progressScaleDownYAnim, progressAlphaAnim);
        progressAnim.setDuration(300);
        progressAnim.setInterpolator(new AccelerateInterpolator());
        progressAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                app_using_time_progress.setVisibility(View.INVISIBLE);
                using_time_progress_complete_btn.setScaleX(0);
                using_time_progress_complete_btn.setScaleY(0);
                using_time_progress_complete_btn.setAlpha(0);
                using_time_progress_complete_btn.setVisibility(View.VISIBLE);
                ObjectAnimator btnScaleUpXAnim = ObjectAnimator.ofFloat(using_time_progress_complete_btn, "scaleX", 0f, 1f);
                ObjectAnimator btnScaleUpYAnim = ObjectAnimator.ofFloat(using_time_progress_complete_btn, "scaleY", 0f, 1f);
                ObjectAnimator btnAlphaAnim = ObjectAnimator.ofFloat(using_time_progress_complete_btn, "alpha", 0f, 1f);
                AnimatorSet btnAnim = new AnimatorSet();
                btnAnim.playTogether(btnScaleUpXAnim, btnScaleUpYAnim, btnAlphaAnim);
                btnAnim.setDuration(200);
                btnAnim.setInterpolator(new AccelerateInterpolator());
                btnAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        startTimeUsingBtnProgressUpDown();
                        startUsingTimeBtnTiltAnim(2.5f);
                    }
                });
                btnAnim.start();
            }
        });
        progressAnim.start();
    }

    private void startTimeUsingBtnProgressUpDown() {
        ObjectAnimator btnScaleUpXAnim = ObjectAnimator.ofFloat(using_time_progress_complete_btn, "scaleX",
                using_time_progress_complete_btn.getScaleX(), 1.05f);
        ObjectAnimator btnScaleUpYAnim = ObjectAnimator.ofFloat(using_time_progress_complete_btn, "scaleY",
                using_time_progress_complete_btn.getScaleY(), 1.05f);
        AnimatorSet btnScaleUpAnim = new AnimatorSet();
        btnScaleUpAnim.playTogether(btnScaleUpXAnim, btnScaleUpYAnim);
        btnScaleUpAnim.setDuration(90);
        btnScaleUpAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator btnScaleDownXAnim = ObjectAnimator.ofFloat(using_time_progress_complete_btn, "scaleX",
                        1.05f, 1f);
                ObjectAnimator btnScaleDownYAnim = ObjectAnimator.ofFloat(using_time_progress_complete_btn, "scaleY",
                        1.05f, 1f);
                AnimatorSet btnScaleDownAnim = new AnimatorSet();
                btnScaleDownAnim.playTogether(btnScaleDownXAnim, btnScaleDownYAnim);
                btnScaleDownAnim.setDuration(90);
                btnScaleDownAnim.start();
            }
        });
        btnScaleUpAnim.start();
    }

    int tiltTimes = 0;
    private void startUsingTimeBtnTiltAnim(float tiltValue) {
        ObjectAnimator rotateEndUp = ObjectAnimator.ofFloat(using_time_progress_complete_btn, "rotation", using_time_progress_complete_btn.getRotation(), -tiltValue);
        rotateEndUp.setDuration(150);
        rotateEndUp.setInterpolator(new AccelerateInterpolator());
        rotateEndUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ObjectAnimator rotateEndDown = ObjectAnimator.ofFloat(using_time_progress_complete_btn, "rotation", -tiltValue, tiltValue);
                    rotateEndDown.setDuration(150);
                    rotateEndDown.setInterpolator(new AccelerateInterpolator());
                    rotateEndDown.addListener(new AnimatorListenerAdapter() {
                       @Override
                       public void onAnimationEnd(Animator animation) {
                           super.onAnimationEnd(animation);
                           tiltTimes++;
                           if (tiltTimes > 1) {
                               ObjectAnimator rotateEnds = ObjectAnimator.ofFloat(using_time_progress_complete_btn, "rotation", tiltValue, 0f);
                               rotateEnds.setDuration(200);
                               rotateEnds.setInterpolator(new AccelerateInterpolator());
                               rotateEnds.start();
                               tiltTimes = 0;
                             } else {
                               startUsingTimeBtnTiltAnim(tiltValue / 1.5f);
                           }
                    }
                });
                rotateEndDown.start();
            }
        });
        rotateEndUp.start();
    }

    private void showSqueezingAnimation() {
        ObjectAnimator rotationAnim = null;
        if (isRotateAvailable()) {
            rotationAnim = ObjectAnimator.ofFloat(user_profile_outer, "rotation", 0f, 360f);
            rotationAnim.setDuration(3000);
            rotationAnim.setRepeatCount(10);
            rotationAnim.start();
        }
        squeezeAnimation(repeatTime(), rotationAnim, false);
    }

    AnimatorSet squeezingAnimationMin, squeezingAnimationMax;
    ObjectAnimator squeezingAnimationMaxX, squeezingAnimationMaxY, squeezingAnimationMinX, squeezingAnimationMinY;

    private void squeezeAnimation(int repeatTime, ObjectAnimator rotationAnimation, boolean isImageDownloading) {
        squeezingAnimationMaxX = ObjectAnimator.ofFloat(user_profile_outer, "scaleX", 1f, 1.1f);
        squeezingAnimationMaxY = ObjectAnimator.ofFloat(user_profile_outer, "scaleY", 1f, 1.1f);
        squeezingAnimationMax = new AnimatorSet();
        squeezingAnimationMax.playTogether(squeezingAnimationMaxY, squeezingAnimationMaxX);
        squeezingAnimationMax.setDuration(1000);
        squeezingAnimationMax.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                squeezingAnimationMinX = ObjectAnimator.ofFloat(user_profile_outer, "scaleX", 1.1f, 1f);
                squeezingAnimationMinY = ObjectAnimator.ofFloat(user_profile_outer, "scaleY", 1.1f, 1f);
                squeezingAnimationMin = new AnimatorSet();
                squeezingAnimationMin.playTogether(squeezingAnimationMinX, squeezingAnimationMinY);
                squeezingAnimationMin.setDuration(1000);
                squeezingAnimationMin.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (isImageDownloading) {
                            if (!isImageDownloadedCompleted) {
                                squeezeAnimation(repeatTime - 1, rotationAnimation, true);
                            } else {
                                animation.cancel();
                                squeezingAnimationMax.cancel();
                            }
                        } else {
                            if (repeatTime > 0) {
                                squeezeAnimation(repeatTime - 1, rotationAnimation, false);
                            } else {
                                if (rotationAnimation != null)
                                    rotationAnimation.cancel();
                                showUserProfileDialog();
                                user_profile_outer.setScaleX(1f);
                                user_profile_outer.setScaleY(1f);
                                isImageSqueezing = false;
                            }
                        }
                    }
                });
                squeezingAnimationMin.start();
            }
        });
        squeezingAnimationMax.start();
    }

//    Ad
//public void loadAd() {
//        try {
//            if (isInternetOn) {
//                Log.d("kvc", "if");
//                ad_layout.setVisibility(View.VISIBLE);
//                if (ad_layout.getChildCount() < 1) {
//                    adView = new AdView(requireActivity());
//                    AdRequest adRequest = new AdRequest.Builder().build();
//                    adView.setAdUnitId(Objects.requireNonNull(ad_values_map.get("ad_setting_fragment_banner_ad")));
//                    adView.setAdSize(AdSize.BANNER);
//                    adView.loadAd(adRequest);
//                    ad_layout.addView(adView);
//                }
//                if (adView != null) {
//                    adView.setAdListener(new AdListener() {
//                        @Override
//                        public void onAdClicked() {
//                            // Code to be executed when the user clicks on an ad.
//                            Log.d("kvc", "clicked");
//                        }
//
//                        @Override
//                        public void onAdClosed() {
//                            // Code to be executed when the user is about to return
//                            // to the app after tapping on an ad.
//                            Log.d("kvc", "closed");
//                        }
//
//                        @Override
//                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
//                            // Code to be executed when an ad request fails.
//                            Log.d("kvc", adError.getMessage());
//                            doExtra();
//                        }
//
//                        @Override
//                        public void onAdImpression() {
//                            // Code to be executed when an impression is recorded
//                            // for an ad.
//                            Log.d("kvc", "impression");
//                        }
//
//                        @Override
//                        public void onAdLoaded() {
//                            // Code to be executed when an ad finishes loading.
//                            Log.d("kvc", "load");
//                        }
//
//                        @Override
//                        public void onAdOpened() {
//                            // Code to be executed when an ad opens an overlay that
//                            // covers the screen.
//                            Log.d("kvc", "open");
//                        }
//                    });
//                }
//            } else {
//                Log.d("kvc", "else");
//                ad_layout.setVisibility(View.GONE);
//            }
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }
//    }

    boolean isUserEarnReward = false;
    public void loadRewardedAd() {

        if (rewardedInterstitialAdMainAct != null) {
            rewardedInterstitialAdMainAct.show(requireActivity(), rewardItem -> isUserEarnReward = true);
//            rewardedInterstitialAdMainAct.setServerSideVerificationOptions();
            rewardedInterstitialAdMainAct.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d("TAG-AD", "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d("TAG-AD", "Ad dismissed fullscreen content.");
                    rewardedInterstitialAdMainAct = null;

                    RewardedInterstitialAd.load(requireActivity(), Objects.requireNonNull(ad_values_map.get("ad_watch_btn_reward_ad")),
                            new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
                                @Override
                                public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                                    rewardedInterstitialAdMainAct = ad;
                                }
                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    rewardedInterstitialAdMainAct = null;
                                }
                            });

                    if (currentUser != null) {
                        if (isUserEarnReward) {
                            isUserEarnReward = false;
                            if (currentUser.getmTime() == null)
                                currentUser.setmTime(new ArrayList<>());
                            if (currentUser.getmTime().size() <= 0) {
                                int daily_ad_watch_time_limit = dailyAdWatchTimeLimit();
                                MTime firstMTime = new MTime(createDailyTimeId(), 0, 0, CONSTANT_FINAL_TIME,
                                        "Pending", String.valueOf(new Date(System.currentTimeMillis())), false, daily_ad_watch_time_limit);
                                currentUser.getmTime().add(currentUser.getmTime().size(), firstMTime);
                            }
                            int remainCount = currentUser.getmTime().get(currentUser.getmTime().size() - 1).getDailyAdWatchCountLimit() - 1;
                            currentUser.getmTime().get(currentUser.getmTime().size() - 1).setDailyAdWatchCountLimit(remainCount);
                            Intent service_starter_intent = new Intent(requireActivity(), AdWatchAfterTimeIntentService.class);

                            requireActivity().startService(service_starter_intent);
                            Intent intent = new Intent(requireActivity(), RewardEarnedActivity.class);
                            intent.putExtra("EARNED_REWARD", true);
                            startActivity(intent);
                        }
                    } else {
//                        user not but watched ad give a dialogue and tell them create your account join with PlayMe and earn money
                        notAUserButWatchedAd();
                    }
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    // Called when ad fails to show.
                    if (alert_ad_dialog != null)
                        alert_ad_dialog.dismiss();
                    Log.e("TAG-AD", "Ad failed to show fullscreen content.");
                    rewardedInterstitialAdMainAct = null;
                    Toast.makeText(requireActivity(), "Failed to load retry again.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d("TAG-AD", "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    if (alert_ad_dialog != null)
                        alert_ad_dialog.dismiss();
                    Log.d("TAG-AD", "Ad showed fullscreen content.");
                }
            });
        } else {
            adLoadingAlertDialog();
            RewardedInterstitialAd.load(requireActivity(), Objects.requireNonNull(ad_values_map.get("ad_watch_btn_reward_ad")),
                    new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                            rewardedInterstitialAdMainAct = ad;
                            loadRewardedAd();
                            Log.d("TAG-AD", "Ad was loaded. Setting");
                        }
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            if (alert_ad_dialog != null)
                                alert_ad_dialog.dismiss();
                            Log.d("TAG-AD", loadAdError + " . Setting");
                            rewardedInterstitialAdMainAct = null;
                            Toast.makeText(requireActivity(), "Failed to load " + loadAdError.getMessage().trim(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void connectToFirebase() {
        f_auth = FirebaseAuth.getInstance();
        f_db = FirebaseFirestore.getInstance();
        f_config = FirebaseRemoteConfig.getInstance();
    }

    private void getUser() {
        if (f_auth == null)
            connectToFirebase();
        f_user = f_auth.getCurrentUser();
    }

//    service back service
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        BackService.LocalBinder binder = (BackService.LocalBinder) iBinder;
        service = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    public void setSleepTimer() {
        SleepTimerLayoutFragment dialog = new SleepTimerLayoutFragment();
        dialog.show(requireActivity().getSupportFragmentManager(), "ABC");
    }

//    user dashboard image loading in background
//    async task
class UserImageLoading extends AsyncTask<Void, Void, Bitmap> {

    boolean isForDashboard;
    ImageView imgView;

    protected UserImageLoading(boolean isForDashboard, ImageView imgView) {
        this.isForDashboard = isForDashboard;
        this.imgView = imgView;
        if (isForDashboard) {
            imgView.setVisibility(View.GONE);
            user_dashboard_image_loading_progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        return (USER_IMAGE_BITMAP_CONST == null) ? setUserProfilePicture() : USER_IMAGE_BITMAP_CONST;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        try {
            if (!(requireActivity().isDestroyed())) {
                if (isForDashboard) {
                    imgView.setVisibility(View.VISIBLE);
                    user_dashboard_image_loading_progress.setVisibility(View.GONE);
                }
                if (imgView != null) {
                    Glide.with(requireActivity())
                            .asBitmap()
                            .load(bitmap)
                            .error(R.drawable.orange_man_user_profile_picture)
                            .into(imgView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
}