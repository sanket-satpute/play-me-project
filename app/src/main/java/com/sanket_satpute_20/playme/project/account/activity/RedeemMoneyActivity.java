package com.sanket_satpute_20.playme.project.account.activity;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.ad_values_map;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isPaused;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isTimerServiceRunning;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.data.model.MUser;
import com.sanket_satpute_20.playme.project.account.data.model.UMoney;
import com.sanket_satpute_20.playme.project.account.service.TimerIntentService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class RedeemMoneyActivity extends AppCompatActivity implements ServiceConnection {

    TimerIntentService timer_service;

    TextView exchange_able_coins, exchanged_money, remaining_coins, total_money, total_money_earned_txt;
    MaterialButton exchange_btn;
    ImageView back_pressed;
    RelativeLayout total_money_earned_layout;

    FirebaseRemoteConfig f_config;
    FirebaseFirestore f_db;
    FirebaseAuth f_auth;

//    ad's
    RelativeLayout ad_layout;
//    AdView bannerAd;

    AlertDialog alert_user_fetch_dialog;
    MaterialAlertDialogBuilder material_user_fetch_dialog;

    int orange_color = Color.parseColor("#ffa500");
    int normal_color = Color.parseColor("#399a9a9a");

    private final String not_enough_coins = "Not Enough Coins";

    int currencyValue;

    long user_coins, user_money, remain_coin, convert_money, convert_coin;

    boolean moneyAssignedToUser = false, convertBtnClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransparentStatusBar();
        setContentView(R.layout.activity_redeem_money);
        initViews();
        onClick();
        setAllMoney();
//        if (bannerAd == null) {
//            loadAd();
//        } else {
//            bannerAd.resume();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (bannerAd == null) {
//            loadAd();
//        } else {
//            bannerAd.resume();
//        }
        Intent timerServiceIntent = new Intent(RedeemMoneyActivity.this, TimerIntentService.class);
        bindService(timerServiceIntent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (bannerAd != null)
//            bannerAd.pause();
        if (timer_service != null) {
            try {
                timer_service.pauseTimer();
                unbindService(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (convertBtnClicked && !moneyAssignedToUser) {
            saveDataToFirebase();
        }
    }

    private void setTransparentStatusBar() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(params);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void initViews() {
        exchange_able_coins = findViewById(R.id.total_coins);
        exchanged_money = findViewById(R.id.total_money);
        remaining_coins = findViewById(R.id.remaining_coins);
        total_money = findViewById(R.id.totle_money);
        exchange_btn = findViewById(R.id.exchange_btn);
        back_pressed = findViewById(R.id.back_pressed);
        total_money_earned_layout = findViewById(R.id.total_money_earned_layout);
        total_money_earned_txt = findViewById(R.id.money);
        ad_layout = findViewById(R.id.ad_layout);
    }

    private void onClick() {
        exchange_btn.setOnClickListener(view -> {
//            exchange coins after that set animation to all
            convertBtnClicked = true;
            setValuesWithAnimation();
            saveDataToFirebase();
        });

        back_pressed.setOnClickListener(view -> finish());
    }

    private void setAllMoney() {
        if (currentUser == null)
            fetchUser();
        currencyValue();
        long user_coins = currentUser.getTotalCoins();
        long user_money = currentUser.getTotalMoney();
        long remain_coin = user_coins % currencyValue;
        long convert_money = user_coins / currencyValue;
        long convert_coin = user_coins - remain_coin;

        if (currentUser.getTotalEarnedMoneyFromPlayMe() >= 10) {
            total_money_earned_layout.setVisibility(View.VISIBLE);
            total_money_earned_txt.setText(String.valueOf(currentUser.getTotalEarnedMoneyFromPlayMe()));
        } else {
            total_money_earned_layout.setVisibility(View.GONE);
        }

        if (convert_coin > 0) {
            exchange_btn.setText(getResources().getString(R.string.exchange));
            exchange_btn.setTextColor(Color.WHITE);
            exchange_btn.setClickable(true);
            exchange_btn.setBackgroundTintList(ColorStateList.valueOf(orange_color));
        } else {
            exchange_btn.setText(not_enough_coins);
            exchange_btn.setTextColor(Color.BLACK);
            exchange_btn.setClickable(false);
            exchange_btn.setBackgroundTintList(ColorStateList.valueOf(normal_color));
        }

        remaining_coins.setText(String.valueOf(remain_coin));
        total_money.setText(String.valueOf(user_money));
        exchange_able_coins.setText(String.valueOf(convert_coin));
        exchanged_money.setText(String.valueOf(convert_money));
    }

    private void fetchUser() {
        View fetching_view = LayoutInflater.from(RedeemMoneyActivity.this).inflate(R.layout.user_fetch_layout, null);
        material_user_fetch_dialog = new MaterialAlertDialogBuilder(RedeemMoneyActivity.this)
                .setView(fetching_view);
        alert_user_fetch_dialog = material_user_fetch_dialog.create();
        alert_user_fetch_dialog.show();
        Toast.makeText(this, "Wait Until User is Fetched", Toast.LENGTH_SHORT).show();

        if (f_auth == null || f_db == null)
            setFirebase();
        if (f_auth.getCurrentUser() != null) {
            f_db.collection("users").document(f_auth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        currentUser = documentSnapshot.toObject(MUser.class);
                        if (alert_user_fetch_dialog != null)
                            alert_user_fetch_dialog.dismiss();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(RedeemMoneyActivity.this, "Please try again " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        if (alert_user_fetch_dialog != null)
                            alert_user_fetch_dialog.dismiss();
                        finish();
                    });
        } else {
            Toast.makeText(this, "Please SignIn or Create Account", Toast.LENGTH_SHORT).show();
            if (alert_user_fetch_dialog != null)
                alert_user_fetch_dialog.dismiss();
            finish();
        }
    }

    private void setFirebase() {
        if (f_auth == null)
            f_auth = FirebaseAuth.getInstance();
        if (f_db == null)
            f_db = FirebaseFirestore.getInstance();
    }

    private void currencyValue() {
        f_config = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        f_config.setConfigSettingsAsync(configSettings);
        String currencyValueStr = "currencyValue";
        currencyValue = (int) f_config.getLong(currencyValueStr);
        if (currencyValue == 0) {
            currencyValue = 100;
        }
    }

//    extra method
//    create user money id
    private String generateMoneyId() {
        String moneyID;
        UUID uuid = UUID.randomUUID();
        moneyID = uuid.toString() + "~ID$MONEY";
        return moneyID;
    }

    private void setValuesWithAnimation() {
        user_coins = currentUser.getTotalCoins();
        user_money = currentUser.getTotalMoney();
        remain_coin = user_coins % currencyValue;
        convert_money = user_coins / currencyValue;
        convert_coin = user_coins - remain_coin;

        ValueAnimator convert_coin_anim = ValueAnimator.ofInt((int) convert_coin, 0);
        convert_coin_anim.addUpdateListener(animation -> exchange_able_coins.setText(animation.getAnimatedValue().toString()));
        convert_coin_anim.setDuration(600);
        convert_coin_anim.start();

        ValueAnimator convert_money_anim = ValueAnimator.ofInt((int) convert_money, 0);
        convert_coin_anim.addUpdateListener(animation -> exchanged_money.setText(animation.getAnimatedValue().toString()));
        convert_money_anim.setDuration(600);
        convert_money_anim.start();

        ValueAnimator convert_total_money_anim = ValueAnimator.ofInt((int) user_money, (int) (convert_money + user_money));
        convert_total_money_anim.addUpdateListener(animation -> total_money.setText(animation.getAnimatedValue().toString()));
        convert_total_money_anim.setDuration(600);
        convert_total_money_anim.start();

        ValueAnimator btn_anim = ValueAnimator.ofArgb(orange_color, normal_color);
        btn_anim.addUpdateListener(animation -> exchange_btn.setBackgroundTintList(ColorStateList.valueOf((Integer) animation.getAnimatedValue())));
        btn_anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                exchange_btn.setText(not_enough_coins);
                exchange_btn.setTextColor(Color.BLACK);
                exchange_btn.setClickable(false);
            }
        });
        btn_anim.start();
    }

    private void saveDataToFirebase() {
        if (currentUser != null) {
            currentUser.setTotalMoney(currentUser.getTotalMoney() + convert_money);
            currentUser.setTotalCoins(remain_coin);
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
                        uMoney.setMoney((int) (currentUser.getuMoney().get(state).getMoney() + convert_money));
                    } else {
//                            another day
                        uMoney = new UMoney(generateMoneyId(), 0, (int) convert_money, date, time);
                        currentUser.getuMoney().add(state + 1, uMoney);
                    }
                } else {
                    uMoney = new UMoney(generateMoneyId(), 0, (int) convert_money, date, time);
                    currentUser.getuMoney().add(0, uMoney);
                }
                if (f_auth == null || f_db == null)
                    setFirebase();
                if (f_auth.getCurrentUser() != null) {
                    f_db.collection("users").document(f_auth.getCurrentUser().getUid())
                            .set(currentUser)
                            .addOnSuccessListener(unused -> Log.d("playMe", "Added Coins Successfully"))
                            .addOnFailureListener(e -> Toast.makeText(RedeemMoneyActivity.this, "Some Failure Occurred", Toast.LENGTH_SHORT).show());
                }
                moneyAssignedToUser = true;
            } else {
                ArrayList<UMoney> money_list = new ArrayList<>();
                currentUser.setuMoney(money_list);
                saveDataToFirebase();
            }
        } else {
            finish();
        }
    }

//    private void loadAd() {
//        if (ad_layout.getChildCount() < 1) {
//            bannerAd = new AdView(RedeemMoneyActivity.this);
//            AdRequest adRequest = new AdRequest.Builder().build();
//            bannerAd.setAdUnitId(Objects.requireNonNull(ad_values_map.get("ad_redeem_money_banner_ad")));
//            bannerAd.setAdSize(AdSize.BANNER);
//            bannerAd.loadAd(adRequest);
//            ad_layout.addView(bannerAd);
//        }
//    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        TimerIntentService.TimerBinder binder = (TimerIntentService.TimerBinder) service;
        timer_service = binder.getService();
        if (isTimerServiceRunning && isPaused) {
            timer_service.resumeTimer();
            Log.d("time_executed", "Calling from here Another");
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        timer_service = null;
    }
}