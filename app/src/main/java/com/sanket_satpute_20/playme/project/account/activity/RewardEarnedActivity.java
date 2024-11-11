package com.sanket_satpute_20.playme.project.account.activity;

import static com.sanket_satpute_20.playme.MainActivity.rewardedInterstitialAdMainAct;
import static com.sanket_satpute_20.playme.project.account.service.AdWatchAfterTimeIntentService.isAdWatchAfterServiceRunning;
import static com.sanket_satpute_20.playme.project.account.service.TimerIntentService.CONSTANT_FINAL_TIME;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.APP_USING_TIME_REWARD_VALUE_STR;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.DAILY_AD_WATCH_TIME_LIMIT_STR;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.ad_values_map;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isPaused;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isTimerServiceRunning;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.data.model.MTime;
import com.sanket_satpute_20.playme.project.account.data.model.MUser;
import com.sanket_satpute_20.playme.project.account.data.model.UMoney;
import com.sanket_satpute_20.playme.project.account.extra_stuffes.CustomScratchView;
import com.sanket_satpute_20.playme.project.account.service.AdWatchAfterTimeIntentService;
import com.sanket_satpute_20.playme.project.account.service.TimerIntentService;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class RewardEarnedActivity extends AppCompatActivity implements ServiceConnection {

    public static final String DAILY_AD_WATCH_QUOTA_STR = "Used 100% Daily Watch Quota";

//    ad's
    RelativeLayout ad_layout;
//    AdView bannerAd;

    AlertDialog alert_ad_dialog;

    boolean adWatchServiceIsBinded = false;

    AdWatchAfterTimeIntentService adWatchService;
    TimerIntentService timerService;

    CustomScratchView scratch_card;
    ConstraintLayout parent_view, scratch_view_constraint_layout;

//    firebase
    private FirebaseAuth f_auth;
    private FirebaseFirestore f_db;
    private FirebaseRemoteConfig f_config;
    private FirebaseUser f_user;

//    views
    MaterialCardView transaction_material_card, redeem_material_card, wallet_material_card, scratch_dialog_card, remain_time_card;
    TextView scratch_on_card_txt, continue_time_txt, points_scored_txt, total_coins_txt, repeat_watch_txt,
            time_reward_helper_txt, watch_reward_helper_txt, congratulation_txt;
    BlurView blurView;
    MaterialButton collect_reward_btn, watch_again_btn;
    ImageView back_pressed;
    MaterialCardView claim_time_reward_btn;
    LinearProgressIndicator app_time_used_progress_bar, watch_progress_bar;
    CircularProgressIndicator ad_progress_loader;

    boolean card_is_not_dropped = false, moneyAssignedToUser = false;

    private int points;

    private final ServiceConnection appUsingTimeServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerIntentService.TimerBinder binder = (TimerIntentService.TimerBinder) service;
            timerService = binder.getService();
            setUserProgressBar();
            if (isTimerServiceRunning && isPaused) {
                timerService.resumeTimer();
                Log.d("time_executed", "Calling from here Another");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            timerService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moneyAssignedToUser = false;
        setContentView(R.layout.activity_reward_earned);
        initViews();
        onClick();


        Intent intent = getIntent();
        boolean forEarningReward = intent.getBooleanExtra("EARNED_REWARD", false);
        
        if (forEarningReward) {
            DisplayMetrics matrices = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(matrices);
            int height = matrices.heightPixels;
            scratch_dialog_card.setTranslationY((height + scratch_dialog_card.getHeight()));

            showRewardCardAnimation(scratch_dialog_card);
            setScratchCard();
        } else {
            setUserDailyAdWatchLeftProgress();
            checkTimeIsOnOrOff();
            startDailyTimeUsageAnimation(false);
        }
        setUserDailyAdWatchLeftProgress();
//        if (bannerAd != null)
//            bannerAd.resume();
//        else
//            loadBannerAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent timerServiceIntent = new Intent(RewardEarnedActivity.this, TimerIntentService.class);
        bindService(timerServiceIntent, appUsingTimeServiceConnection, BIND_AUTO_CREATE);
        Intent adWatchServiceIntent = new Intent(RewardEarnedActivity.this, AdWatchAfterTimeIntentService.class);
        bindService(adWatchServiceIntent, this, BIND_AUTO_CREATE);
//        if (bannerAd != null)
//            bannerAd.resume();
//        else
//            loadBannerAd();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adWatchService != null && adWatchServiceIsBinded)
            unbindService(RewardEarnedActivity.this);
        if (timerService != null) {
            try {
                timerService.pauseTimer();
                unbindService(appUsingTimeServiceConnection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        if (bannerAd != null)
//            bannerAd.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!moneyAssignedToUser)
            setMoneyToUser();
    }

    private void initViews() {
//        scratch card views
        scratch_dialog_card = findViewById(R.id.scratch_surprise_card);
        continue_time_txt = findViewById(R.id.continue_txt);
        points_scored_txt = findViewById(R.id.points);
        collect_reward_btn = findViewById(R.id.collect_btn);
        scratch_card = findViewById(R.id.card_main);
        scratch_on_card_txt = findViewById(R.id.scratch_on_card_txt);
        scratch_view_constraint_layout = findViewById(R.id.scratch_view_constraint_layout);
        congratulation_txt = findViewById(R.id.congratulation_txt);

//        parent views
        transaction_material_card = findViewById(R.id.transaction_card);
        redeem_material_card = findViewById(R.id.redeem_card);
        wallet_material_card = findViewById(R.id.wallet_card);
        parent_view = findViewById(R.id.my_reward_parent_layout);
        blurView = findViewById(R.id.blurView);
        back_pressed = findViewById(R.id.back_pressed);
        total_coins_txt = findViewById(R.id.current_points);
        repeat_watch_txt = findViewById(R.id.remain_time_to_watch);
        watch_again_btn = findViewById(R.id.watch_again_btn);
        remain_time_card = findViewById(R.id.remain_time_card);
        claim_time_reward_btn = findViewById(R.id.claim_time_reward_btn);
        app_time_used_progress_bar = findViewById(R.id.app_using_daily_time_reward_progress_bar);
        watch_progress_bar = findViewById(R.id.ad_watch_daily_limit_progress_bar);
        time_reward_helper_txt = findViewById(R.id.time_reward_msg);
        watch_reward_helper_txt = findViewById(R.id.watch_reward_msg);
        ad_progress_loader = findViewById(R.id.progress_before_ad_load);
        ad_layout = findViewById(R.id.ad_layout);
    }

    private void checkTimeIsOnOrOff() {
        if (isAdWatchAfterServiceRunning) {     // timer is on
            remain_time_card.setVisibility(View.VISIBLE);
            watch_again_btn.setVisibility(View.GONE);
        } else {                                // timer is off
            remain_time_card.setVisibility(View.INVISIBLE);
            watch_again_btn.setVisibility(View.VISIBLE);
        }
    }

    private void onClick() {
        transaction_material_card.setOnClickListener(view -> {
            Intent intent = new Intent(RewardEarnedActivity.this, NotificationInboxActivity.class);
            startActivity(intent);
            finish();
        });

        redeem_material_card.setOnClickListener(view -> {
            Intent intent = new Intent(RewardEarnedActivity.this, RedeemMoneyActivity.class);
            startActivity(intent);
            finish();
        });

        wallet_material_card.setOnClickListener(view -> {
            Intent intent = new Intent(RewardEarnedActivity.this, WalletActivity.class);
            startActivity(intent);
            finish();
        });

        back_pressed.setOnClickListener(view -> finish());

        collect_reward_btn.setOnClickListener(view -> afterScratch());

        watch_again_btn.setOnClickListener(view -> {
            if (isAdWatchAfterServiceRunning) {
                Toast.makeText(this, "Wait for some time", Toast.LENGTH_SHORT).show();
                checkTimeIsOnOrOff();
            } else {
                moneyAssignedToUser = false;
                loadAd();
            }
        });

        claim_time_reward_btn.setOnClickListener(view -> claimDailyUsingTimeReward());
    }

//    extra method
//    create user money id
    private String generateMoneyId() {
        String moneyID;
        UUID uuid = UUID.randomUUID();
        moneyID = uuid.toString() + "~ID$MONEY";
        return moneyID;
    }

    private void setScratchCard() {
        if (currentUser != null) {
            float radius = 20f;
            View decorView = getWindow().getDecorView();
            ViewGroup rootView = decorView.findViewById(android.R.id.content);
            Drawable windowBackground = decorView.getBackground();

            blurView.setupWith(rootView, new RenderScriptBlur(this)) // or RenderEffectBlur
                    .setFrameClearDrawable(windowBackground) // Optional
                    .setBlurRadius(radius);
            handleListeners();

            blurView.setVisibility(View.VISIBLE);
            scratch_dialog_card.setVisibility(View.VISIBLE);

            card_is_not_dropped = true;
            parent_view.setClickable(false);
            transaction_material_card.setClickable(false);
            redeem_material_card.setClickable(false);
            wallet_material_card.setClickable(false);
            parent_view.setAlpha(0.3f);

//        set points
            if (f_config == null)
                connectToFirebase();
            long points_in_between = f_config.getLong("ad_watch_maximum_points");
            if (points_in_between < 0)
                points_in_between = 8;
            points = new Random().nextInt((int) points_in_between);
            if (points <= 0)
                points = 1;
            points_scored_txt.setText(String.valueOf(points));
            continue_time_txt.setVisibility(View.GONE);
        } else {
            finish();
        }
    }

    private void scratch() {
        if (true) {
            scratch_card.setVisibility(View.INVISIBLE);
            continue_time_txt.setVisibility(View.VISIBLE);
            new CountDownTimer(30000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String time_str = "Continue in " + TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + "s";
                    continue_time_txt.setText(time_str);
                }

                @Override
                public void onFinish() {
                    {
                        if (card_is_not_dropped)
                            afterScratch();
                    }
                }
            }.start();
        } else {
            scratch_card.setVisibility(View.VISIBLE);
        }
    }

    private void handleListeners() {
        scratch_card.setOnScratchListener((scratchCard, visiblePercent) -> {
            if (visiblePercent > 0.8) {
                scratch();
                setScratchedTransition(scratch_dialog_card);
            } if (visiblePercent > 0.1) {
                scratch_on_card_txt.setVisibility(View.GONE);
            }
        });
    }

    private void afterScratch() {
        card_is_not_dropped = false;
//        animation end scratch card
        hideRewardCardAnimation(scratch_dialog_card);
//        remove scratch card stuff
        parent_view.setClickable(true);
        transaction_material_card.setClickable(true);
        redeem_material_card.setClickable(true);
        wallet_material_card.setClickable(true);
        setUserDailyAdWatchLeftProgress();
    }

//    Extra Methods
    private void setMoneyToUser() {
        if (!moneyAssignedToUser) {
            if (currentUser != null) {
                currentUser.setTotalCoins(currentUser.getTotalCoins() + points);
                if (currentUser.getuMoney() != null) {
                    int state;
                    UMoney uMoney;
                    String date = String.valueOf(new Date(System.currentTimeMillis()));
                    String time = String.valueOf(new Time(System.currentTimeMillis()));
                    if (currentUser.getuMoney().size() > 0)
                    {   // inside size check whether it is same day or another day
                        state = currentUser.getuMoney().size() - 1;
                        String pastDate = currentUser.getuMoney().get(state).getMoneyDate();
                        if (pastDate.trim().equalsIgnoreCase(date.trim())) {
//                            same day
                            uMoney = currentUser.getuMoney().get(state);
                            uMoney.setCoins(currentUser.getuMoney().get(state).getCoins() + points);
                        } else {
//                            another day
                            uMoney = new UMoney(generateMoneyId(), points, 0, date, time);
                            currentUser.getuMoney().add(state + 1, uMoney);
                        }
                    } else {
                        uMoney = new UMoney(generateMoneyId(), points, 0, date, time);
                        currentUser.getuMoney().add(0, uMoney);
                    }
                    if (f_auth == null || f_db == null)
                        connectToFirebase();
                    if (f_auth.getCurrentUser() != null) {
                        f_db.collection("users").document(f_auth.getCurrentUser().getUid())
                                .set(currentUser).addOnSuccessListener(unused -> Log.d("playMe", "Added Coins Successfully")).addOnFailureListener(e -> {
                                    Toast.makeText(RewardEarnedActivity.this, "Some Failure Occurred", Toast.LENGTH_SHORT).show();
                                    Snackbar.make(parent_view, String.valueOf(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                                });
                    }
                    moneyAssignedToUser = true;
                } else {
                    ArrayList<UMoney> money_list = new ArrayList<>();
                    currentUser.setuMoney(money_list);
                    setMoneyToUser();
                }
            } else {
//                fetch user if it is available if not then finish activity
                connectToFirebase();
                if (f_auth.getCurrentUser() != null) {
                    f_db.collection("users").document(f_auth.getCurrentUser().getUid()).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                currentUser = documentSnapshot.toObject(MUser.class);
                                setMoneyToUser();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(RewardEarnedActivity.this, "Please try again " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            });
                } else {
                    Toast.makeText(this, "Please SignIn or Create Account", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private long getMin(long milli) {
        long min = ((milli / (1000*60)) % 60);
        if (min >= 0 && min <= 9)
            return min;
        return (milli / (1000*60) % 60);
    }

    private long getSec(long milli) {
        return ((milli / 1000) % 60);
    }

    private long getHrs(long millis) {return TimeUnit.MILLISECONDS.toHours(millis);}

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

    int min, sec;
    String time_str;

    private void doExtra() {
        Handler handler = new Handler();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                min = (int) getMin(adWatchService.getRemainTime());
                sec = (int) getSec(adWatchService.getRemainTime());
                if (min > 0) {
                    time_str = "repeat watch : " + min + ":" + sec + "min";
                } else {
                    time_str = "repeat watch : " + sec + "s";
                }
                repeat_watch_txt.setText(time_str);

                if (isAdWatchAfterServiceRunning)
                    handler.postDelayed(this, 1000);
                if (min <= 0 && sec <= 1){
                    remain_time_card.setVisibility(View.INVISIBLE);
                    watch_again_btn.setVisibility(View.VISIBLE);
                    if (currentUser.getmTime().get(currentUser.getmTime().size() - 1).getDailyAdWatchCountLimit() < 0) {
                        watch_again_btn.setClickable(false);
                        watch_again_btn.setTextColor(Color.parseColor("#d9d9d9"));
                        watch_again_btn.setTextColor(Color.BLACK);
                        watch_again_btn.setText(DAILY_AD_WATCH_QUOTA_STR);
                    }
                }
            }
        });
    }

    private void setUserDailyAdWatchLeftProgress() {
        connectToFirebase();
        int daily_ad_watch_time_limit = (f_config.getLong(DAILY_AD_WATCH_TIME_LIMIT_STR) < 1) ? 50 : (int) f_config.getLong(DAILY_AD_WATCH_TIME_LIMIT_STR);
        if (currentUser != null) {
            if (currentUser.getmTime().size() > 0) {
                MTime mTimeLastDate = currentUser.getmTime().get(currentUser.getmTime().size() - 1);
                int dailyAdWatchLimit = mTimeLastDate.getDailyAdWatchCountLimit();
                watch_progress_bar.setMax(daily_ad_watch_time_limit);
                watch_progress_bar.setProgress(dailyAdWatchLimit);
                String watch_helper_str = dailyAdWatchLimit + " daily ad watch left.";
                watch_reward_helper_txt.setText(watch_helper_str);

                if (currentUser.getmTime().get(currentUser.getmTime().size() - 1).getDailyAdWatchCountLimit() < 0) {
                    watch_again_btn.setClickable(false);
                    watch_again_btn.setTextColor(Color.parseColor("#d9d9d9"));
                    watch_again_btn.setTextColor(Color.BLACK);
                    watch_again_btn.setText(DAILY_AD_WATCH_QUOTA_STR);
                }
            } else {
                String unable_to_fetch_str = "Unable to fetch data try again.";
                watch_progress_bar.setMax(daily_ad_watch_time_limit);
                watch_progress_bar.setProgress(daily_ad_watch_time_limit);
                watch_reward_helper_txt.setText(unable_to_fetch_str);
            }
        } else {
            String unable_to_fetch_str = "Unable to fetch data try again.";
            watch_progress_bar.setMax(daily_ad_watch_time_limit);
            watch_progress_bar.setProgress(daily_ad_watch_time_limit);
            watch_reward_helper_txt.setText(unable_to_fetch_str);
        }
    }

    private void setUserProgressBar() {
        Handler handler = new Handler();
        if (currentUser != null) {
            app_time_used_progress_bar.setMax(CONSTANT_FINAL_TIME);
            if (timerService.getCurrentTime() >= CONSTANT_FINAL_TIME) {
                app_time_used_progress_bar.setVisibility(View.GONE);
                time_reward_helper_txt.setVisibility(View.GONE);
                claim_time_reward_btn.setVisibility(View.VISIBLE);
            } else if (currentUser.getmTime().get(currentUser.getmTime().size() - 1).isDailyRewardCollected()) {
                app_time_used_progress_bar.setVisibility(View.VISIBLE);
                time_reward_helper_txt.setVisibility(View.VISIBLE);
                claim_time_reward_btn.setVisibility(View.GONE);
                app_time_used_progress_bar.setProgress(CONSTANT_FINAL_TIME);
            } else {
                app_time_used_progress_bar.setVisibility(View.VISIBLE);
                time_reward_helper_txt.setVisibility(View.VISIBLE);
                claim_time_reward_btn.setVisibility(View.GONE);
            }
            RewardEarnedActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String usingTimeHelperMsg;
                    int current_time = (int) timerService.getCurrentTime();
                    int remainingTime = CONSTANT_FINAL_TIME - current_time;
                    if (current_time <= CONSTANT_FINAL_TIME) {
                        if (remainingTime > 3600000) {
                            usingTimeHelperMsg = "only " + getHrs(remainingTime) + " hours is remain to collect daily reward.";
                        } else if (remainingTime > 60000) {
                            usingTimeHelperMsg = "only " + getMin(remainingTime) + " minutes is remain to collect daily reward.";
                        } else if (remainingTime > 1000) {
                            usingTimeHelperMsg = "only " + getSec(remainingTime) + " seconds is remain to collect daily reward.";
                        } else {
                            usingTimeHelperMsg = "no more time to remain to collect daily reward";
                        }
                        app_time_used_progress_bar.setProgress(current_time);
                        time_reward_helper_txt.setText(usingTimeHelperMsg);
                        handler.postDelayed(this, 30000);
                    } else if (currentUser.getmTime().get(currentUser.getmTime().size() - 1).isDailyRewardCollected()) {
                        app_time_used_progress_bar.setProgress(CONSTANT_FINAL_TIME);
                    } else {
                        app_time_used_progress_bar.setVisibility(View.GONE);
                        time_reward_helper_txt.setVisibility(View.GONE);
                        claim_time_reward_btn.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    int myDailyEarnedPoints = 0;
    private void claimDailyUsingTimeReward() {
        if (currentUser != null) {
            if (!(currentUser.getmTime().get(currentUser.getmTime().size() - 1).isDailyRewardCollected())) {

                TextView dailyEarnedPoints;
                MaterialButton collectRewardBtn;
                LottieAnimationView two_dots_animation;
                View v = LayoutInflater.from(RewardEarnedActivity.this).inflate(R.layout.app_using_time_reward_layout, null);
                MaterialAlertDialogBuilder alert_builder = new MaterialAlertDialogBuilder(RewardEarnedActivity.this, R.style.RoundedCornerAlertDialog)
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

                    currentUser.setTotalCoins(currentUser.getTotalCoins() + myDailyEarnedPoints);
                    UMoney uMoney;
                    MTime mTime;
                    String todayDate = String.valueOf(new Date(System.currentTimeMillis()));
                    String mTimeLastDate = currentUser.getmTime().get(currentUser.getmTime().size() - 1).getDaily_limit_date();
                    if (todayDate.trim().equalsIgnoreCase(mTimeLastDate.trim())) {
                        currentUser.getmTime().get(currentUser.getmTime().size() - 1).setDailyRewardCollected(true);
//                        uTime set all fields set usage time completed
                    } /*else {
                        mTime = new MTime("date_time_id", currentUser.getmTime().size(), ,
                                10800000L, "Incomplete", todayDate, true);
                        currentUser.getmTime().add(currentUser.getmTime().size(), mTime);
//                        create new object of uTime set all fields set usage time completed
                    }*/
                    if (currentUser.getuMoney() == null) {
                        ArrayList<UMoney> uMoneyList = new ArrayList<>();
                        uMoneyList.add(0, new UMoney(generateMoneyId(), myDailyEarnedPoints, 0, todayDate,
                                String.valueOf(new Time(System.currentTimeMillis()))));
                        currentUser.setuMoney(uMoneyList);
                    } else if (currentUser.getuMoney().size() <= 0) {
                        currentUser.getuMoney().add(0, new UMoney(generateMoneyId(), myDailyEarnedPoints, 0, todayDate,
                                String.valueOf(new Time(System.currentTimeMillis()))));
                    }
                    String lastDate = currentUser.getuMoney().get(currentUser.getuMoney().size() - 1).getMoneyDate();
                    if (todayDate.trim().equalsIgnoreCase(lastDate.trim())) {
                        uMoney = currentUser.getuMoney().get(currentUser.getuMoney().size() - 1);
                        uMoney.setCoins(uMoney.getCoins() + myDailyEarnedPoints);
                        currentUser.getuMoney().set(currentUser.getuMoney().size() - 1, uMoney);
                    } else {
                        uMoney = new UMoney(generateMoneyId(), myDailyEarnedPoints, 0, todayDate,
                                String.valueOf(new Time(System.currentTimeMillis())));
                        currentUser.getuMoney().add(currentUser.getuMoney().size(), uMoney);
                    }
                    if (f_auth == null || f_db == null || f_config == null)
                        connectToFirebase();
                    if (f_user == null)
                        getUser();
                    f_db.collection("users").document(f_user.getUid()).set(currentUser)
                            .addOnSuccessListener(unused -> {   // saved data successful
                                dialog.dismiss();
//                                animation increasing coins in main coin
                                startDailyTimeUsageAnimation(true);
                                setUserProgressBar();
                            }).addOnFailureListener(e -> {      // saving data failed
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
                Toast.makeText(RewardEarnedActivity.this, "Already Reward Collected", Toast.LENGTH_SHORT).show();
                setUserProgressBar();
            }
        }
    }

    boolean isUserEarnReward = false;
    //    Ad Related
    private void loadAd() {
        if (rewardedInterstitialAdMainAct != null) {
            rewardedInterstitialAdMainAct.show(RewardEarnedActivity.this, rewardItem -> isUserEarnReward = true);
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

                    String ad_id = ad_values_map.get("ad_watch_btn_reward_ad");
                    if (ad_id != null) {
                        RewardedInterstitialAd.load(RewardEarnedActivity.this, ad_id,
                                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                                        rewardedInterstitialAdMainAct = ad;
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        rewardedInterstitialAdMainAct = null;
                                    }
                                });
                    }

                    if (currentUser != null) {
                        if (isUserEarnReward) {
                            isUserEarnReward = false;
                            currentUser.getmTime().get(currentUser.getmTime().size() - 1).setDailyAdWatchCountLimit(
                                    currentUser.getmTime().get(currentUser.getmTime().size() - 1).getDailyAdWatchCountLimit() - 1);
                            Intent service_starter_intent = new Intent(RewardEarnedActivity.this, AdWatchAfterTimeIntentService.class);
                            startService(service_starter_intent);

//                            watched complete ad
                            showRewardCardAnimation(scratch_dialog_card);
                            setScratchCard();
                            checkTimeIsOnOrOff();
                            setUserDailyAdWatchLeftProgress();
                        }
                    } else {
//                        user not but watched ad give a dialogue and tell them create your account join with PlayMe and earn money
                        finish();
                    }
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    // Called when ad fails to show.
                    if (alert_ad_dialog != null)
                        alert_ad_dialog.dismiss();
                    Log.e("TAG-AD", "Ad failed to show fullscreen content.");
                    rewardedInterstitialAdMainAct = null;
                    Toast.makeText(RewardEarnedActivity.this, "Failed to load retry again.", Toast.LENGTH_SHORT).show();
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
            String ad_id = ad_values_map.get("ad_watch_btn_reward_ad");
            if (ad_id != null) {
                RewardedInterstitialAd.load(RewardEarnedActivity.this, ad_id,
                        new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                                rewardedInterstitialAdMainAct = ad;
                                loadAd();
                                Log.d("TAG-AD", "Ad was loaded. Setting");
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error.
                                if (alert_ad_dialog != null)
                                    alert_ad_dialog.dismiss();
                                Log.d("TAG-AD", loadAdError.getMessage() + " . Setting");
                                rewardedInterstitialAdMainAct = null;
                                Toast.makeText(RewardEarnedActivity.this, "Failed to load " + loadAdError.getMessage().trim(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void adLoadingAlertDialog() {
        View view = LayoutInflater.from(RewardEarnedActivity.this).inflate(R.layout.loading_alert_dialog_layout, null);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(RewardEarnedActivity.this, R.style.RoundedCornerAlertDialogSmall);
        dialog.setView(view);
        alert_ad_dialog = dialog.create();
        alert_ad_dialog.show();
        alert_ad_dialog.setCancelable(false);
    }

//    Animation
    private void startDailyTimeUsageAnimation(boolean afterAdWatch) {
        int currentlyCoins;
        if (afterAdWatch)
            currentlyCoins = Integer.parseInt(String.valueOf(total_coins_txt.getText()));
        else
            currentlyCoins = 0;
        ValueAnimator increaseCoins = ValueAnimator.ofInt(currentlyCoins, (int) currentUser.getTotalCoins());
        increaseCoins.addUpdateListener(animation -> total_coins_txt.setText(animation.getAnimatedValue().toString()));
        increaseCoins.setInterpolator(new AccelerateInterpolator());
        increaseCoins.start();
    }

    private void setScratchedTransition(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f);
        AnimatorSet scale_inc = new AnimatorSet();
        scale_inc.playTogether(scaleX, scaleY);
        scale_inc.setInterpolator(new AccelerateInterpolator());
        scale_inc.setDuration(150);
        scale_inc.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator scaleXDec = ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 1f);
                ObjectAnimator scaleYDec = ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 1f);
                AnimatorSet scale_dec = new AnimatorSet();
                scale_dec.playTogether(scaleXDec, scaleYDec);
                scale_dec.setInterpolator(new DecelerateInterpolator());
                scale_dec.setDuration(200);
                scale_dec.start();
            }
        });
        scale_inc.setStartDelay(100);
        scale_inc.start();
    }

    private void hideRewardCardAnimation(View view) {
        DisplayMetrics matrices = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(matrices);
        int height = matrices.heightPixels;

        ObjectAnimator translateY = ObjectAnimator.ofFloat(view, "translationY", 0, (height+view.getHeight()));
        translateY.setDuration(300);
        translateY.setInterpolator(new AccelerateInterpolator());
        translateY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
                blurView.setVisibility(View.GONE);
                ObjectAnimator alpha_parent = ObjectAnimator.ofFloat(parent_view, "alpha", 0.3f, 1f);
                alpha_parent.setDuration(300);
                alpha_parent.setInterpolator(new LinearInterpolator());
                alpha_parent.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ValueAnimator point_inc_anim = ValueAnimator.ofInt((int)currentUser.getTotalCoins(), (int)currentUser.getTotalCoins()+points);
                        point_inc_anim.setDuration(400);
                        point_inc_anim.addUpdateListener(animation1 -> total_coins_txt.setText(animation1.getAnimatedValue().toString()));
                        point_inc_anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                setMoneyToUser();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                super.onAnimationCancel(animation);
                                setMoneyToUser();
                            }
                        });
                        point_inc_anim.start();
                    }
                });
                alpha_parent.start();
            }
        });
        translateY.start();
    }

    private void showRewardCardAnimation(View view) {

        ObjectAnimator translateY = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), 0);
        translateY.setDuration(300);
        translateY.setInterpolator(new AccelerateInterpolator());
        translateY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                blurView.setVisibility(View.VISIBLE);
                ObjectAnimator alpha_parent = ObjectAnimator.ofFloat(parent_view, "alpha", 1f, 0.3f);
                alpha_parent.setDuration(300);
                alpha_parent.setInterpolator(new LinearInterpolator());
                alpha_parent.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
//                        hello
                    }
                });
                alpha_parent.start();
            }
        });
        translateY.start();
    }

    //    ads
//    private void loadBannerAd() {
//        if (ad_layout.getChildCount() < 1) {
//            bannerAd = new AdView(this);
//            AdRequest adRequest = new AdRequest.Builder().build();
//            bannerAd.setAdUnitId(Objects.requireNonNull(ad_values_map.get("ad_my_reward_banner_ad")));
//            bannerAd.setAdSize(AdSize.BANNER);
//            bannerAd.loadAd(adRequest);
//            ad_layout.addView(bannerAd);
//        }
//    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        AdWatchAfterTimeIntentService.AdWatchAfterTimeBinder binder =
                (AdWatchAfterTimeIntentService.AdWatchAfterTimeBinder) service;
        adWatchService = binder.getService();
        adWatchServiceIsBinded = true;
        doExtra();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        adWatchService = null;
        adWatchServiceIsBinded = false;
    }
}