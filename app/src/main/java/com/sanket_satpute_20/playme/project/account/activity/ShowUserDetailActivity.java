package com.sanket_satpute_20.playme.project.account.activity;

import static com.sanket_satpute_20.playme.project.account.activity.NotificationInboxActivity.ACTION_INBOX;
import static com.sanket_satpute_20.playme.project.account.extra_stuffes.CommonMethodsUser.getFirebaseStoragePath;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.APP_USING_TIME_REWARD_VALUE_STR;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.CURRENCY_TYPE;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.DOLLAR_OR_RUPEE_DOLLAR;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.DOLLAR_OR_RUPEE_RUPEE;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.USER_IMAGE_BITMAP_CONST;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isPaused;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isShowUserDetailActServiceBinded;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isTimerServiceRunning;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.month_names;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.rupeeValueByDollar;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.REQUEST_CODE;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.data.model.MTime;
import com.sanket_satpute_20.playme.project.account.data.model.MUser;
import com.sanket_satpute_20.playme.project.account.data.model.UMoney;
import com.sanket_satpute_20.playme.project.account.extra_stuffes.CustomBarChartRender;
import com.sanket_satpute_20.playme.project.account.extra_stuffes.SaveUserImageCache;
import com.sanket_satpute_20.playme.project.account.service.TimerIntentService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Time;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//growBarForTodayTimeUsedAppCompletedSuccess      - not implemented correctly
// clear all if conditions
public class ShowUserDetailActivity extends AppCompatActivity implements ServiceConnection {

    FirebaseAuth f_auth;
    FirebaseFirestore f_db;
    FirebaseUser f_user;
    DocumentReference user_ref;
    FirebaseRemoteConfig f_config;

    MaterialAlertDialogBuilder material_user_fetch_dialog;
    AlertDialog alert_user_fetch_dialog;

//    final
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 110;
    public final String deleting_str = "Deleting";
    public final String deleted_str = "Deleted";
    public final String failed_str = "Failed";
    public final int selected_item_color = Color.parseColor("#ffa500");

//    Service related
    TimerIntentService time_service;

//    local_variables
    public static final String DEFAULT_USER_NAME = "Guest";
    public final String HELLO_MSG = "Hello, ";
    String rupeeValueByDollarStr = "rupeeValueByDollar";

    int default_color = Color.parseColor("#9f9f9f");

    boolean isImageSqueezing = false;

//    views
    TextView user_name, user_money, user_email_or_no, inr, usd, edit_profile_txt, change_email_txt, change_password_txt, sign_out_txt, which_currency_txt,
    user_used_time, user_remain_time, earning_period_txt, app_using_time_txt, modify_bar_txt;
    ImageView user_profile_pic, user_profile_outer, user_notification;
    MaterialCardView is_notification_available;
    MaterialCardView[] user_option_cards = new MaterialCardView[4];
    MaterialButton currency_convert_btn, collect_using_time_reward_btn;
    ConstraintLayout user_options, chart_layout;
    MaterialCardView currency_dropdown_card, user_dropdown_menu_card, payment_card, wallet_card, reward_card, modify_bar_chart_dropdown_card;
    LinearLayout currency_dropdown_layout, using_time_layout;
    ProgressBar user_used_time_progress_bar, loading_bars_chart_progress;
    CircularProgressIndicator user_dashboard_image_loading_progress;
    BarChart barChart;
    LineChart lineChart;
    RelativeLayout remainTimeLayout;
    HorizontalScrollView horizontal_scroll_boxes;
    FloatingActionButton scroll_boxes_fab;

//    modify bar drop down view's
    TextView /*days*/ days_3, days_5, days_7, days_auto, /*weeks*/ weeks_3, weeks_5, weeks_7, weeks_auto, /*months*/ months_3,
        months_5, months_7, months_auto, /*years*/ years_3, years_5, years_7, years_auto,
    bar_chart_radio_btn_txt, line_chart_radio_btn_txt;
    RadioGroup chart_type_radio_group;
    RadioButton bar_chart_radio_btn, line_chart_radio_btn;
    RelativeLayout bar_chart_radio_btn_relative_layout, line_chart_radio_btn_relative_layout;

//    user profile dialog
    Uri savedImageUri;

    boolean isImageDownloadedCompleted = false;

//    AdView bannerAd;
    RelativeLayout ad_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransparentStatusBar();
        setContentView(R.layout.activity_show_user_detail);
        initViews();
        connectToFirebase();
        onClick();
//        if (bannerAd != null)
//            bannerAd.resume();
//        else
//            setBannerAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doExtra();
        Intent intent = new Intent(ShowUserDetailActivity.this, TimerIntentService.class);
        if (isTimerServiceRunning) {
            bindService(intent, this, BIND_AUTO_CREATE);
        } else {
            startService(intent);
        }
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                default_color = Color.parseColor("#ffffff");
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                default_color = Color.parseColor("#000000");
                break;
        }
//        if (bannerAd != null)
//            bannerAd.resume();
//        else
//            setBannerAd();
        starterMethod();
        getModifyBarSelectedItems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (user_dropdown_menu_card.getVisibility() == View.VISIBLE)
            user_dropdown_menu_card.setVisibility(View.GONE);
        if (currency_dropdown_card.getVisibility() == View.VISIBLE)
            currency_dropdown_card.setVisibility(View.GONE);
        if (modify_bar_chart_dropdown_card.getVisibility() == View.VISIBLE)
            modify_bar_chart_dropdown_card.setVisibility(View.GONE);
        if (time_service != null)
            time_service.pauseTimer();
        try {
            if (isShowUserDetailActServiceBinded)
                unbindService(this);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            if (bannerAd != null)
//                bannerAd.pause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if((grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                launchCameraImageChooserActivity.launch(intent);
            }
            else
            {
                ActivityCompat.requestPermissions(ShowUserDetailActivity.this, new String[]{
                        Manifest.permission.CAMERA}, REQUEST_CODE);
            }
        }
    }

    //    extra methods
    private void setTransparentStatusBar() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(params);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void initViews() {
        user_name = findViewById(R.id.user_name);
        user_money = findViewById(R.id.totle_money);
        user_email_or_no = findViewById(R.id.mobile_or_email);
        user_profile_outer = findViewById(R.id.user_profile_picture_card_outer);
        user_profile_pic = findViewById(R.id.user_profile_picture);
        user_notification = findViewById(R.id.user_notification);
        is_notification_available = findViewById(R.id.user_notification_is_available);
        user_option_cards[0] = findViewById(R.id.user_option_1);
        user_option_cards[1] = findViewById(R.id.user_option_2);
        user_option_cards[2] = findViewById(R.id.user_option_3);
        user_option_cards[3] = findViewById(R.id.user_option_4);
        currency_convert_btn = findViewById(R.id.currency_convert_btn);
        user_options = findViewById(R.id.user_options);
        inr = findViewById(R.id.inr_txt);
        usd = findViewById(R.id.usd_txt);
        currency_dropdown_card = findViewById(R.id.currency_dropdown);
        currency_dropdown_layout = findViewById(R.id.currency_dropdown_layout);
        user_dropdown_menu_card = findViewById(R.id.user_pop_up_menu);
        edit_profile_txt = findViewById(R.id.edit_profile_txt);
        change_email_txt = findViewById(R.id.change_email_txt);
        change_password_txt = findViewById(R.id.change_password_txt);
        sign_out_txt = findViewById(R.id.sign_out_txt);
        which_currency_txt = findViewById(R.id.which_currency_txt);
        user_used_time = findViewById(R.id.used_time_txt);
        user_remain_time = findViewById(R.id.remaining_time_txt);
        user_used_time_progress_bar = findViewById(R.id.app_use_time_progress);
        payment_card = findViewById(R.id.payments_service_card);
        wallet_card = findViewById(R.id.wallet_service_card);
        reward_card = findViewById(R.id.reward_service_card);
        barChart = findViewById(R.id.bar_chart);
        lineChart = findViewById(R.id.line_chart);
        chart_layout = findViewById(R.id.chart_layout);
        earning_period_txt = findViewById(R.id.earning_period_txt);
        using_time_layout = findViewById(R.id.using_time_layout);
        collect_using_time_reward_btn = findViewById(R.id.collect_time_reward);
        remainTimeLayout = findViewById(R.id.second_layout);
        app_using_time_txt = findViewById(R.id.app_using_time_txt);
        modify_bar_txt = findViewById(R.id.modify_bar_txt);
        modify_bar_chart_dropdown_card = findViewById(R.id.modify_bar_dropdown);
        loading_bars_chart_progress = findViewById(R.id.chart_loading_progress);
        ad_layout = findViewById(R.id.ad_layout);
        horizontal_scroll_boxes = findViewById(R.id.horizontal_scroll_service_layout);
        scroll_boxes_fab = findViewById(R.id.fab_for_scrolling);
        user_dashboard_image_loading_progress = findViewById(R.id.user_dashboard_image_loading_progress);

//        modify_bars drop down
        days_3 = findViewById(R.id.days_3_days);
        days_5 = findViewById(R.id.days_5_days);
        days_7 = findViewById(R.id.days_7_days);
        days_auto = findViewById(R.id.days_how_much_less_7_days);

        weeks_3 = findViewById(R.id.weeks_3_weeks);
        weeks_5 = findViewById(R.id.weeks_5_weeks);
        weeks_7 = findViewById(R.id.weeks_7_weeks);
        weeks_auto = findViewById(R.id.weeks_how_much_less_7_weeks);

        months_3 = findViewById(R.id.months_3_months);
        months_5 = findViewById(R.id.months_5_months);
        months_7 = findViewById(R.id.months_7_months);
        months_auto = findViewById(R.id.months_how_much_less_7_months);

        years_3 = findViewById(R.id.years_3_years);
        years_5 = findViewById(R.id.years_5_years);
        years_7 = findViewById(R.id.years_7_years);
        years_auto = findViewById(R.id.years_how_much_less_7_years);

        chart_type_radio_group = findViewById(R.id.select_bar_chart_radio_group);
        bar_chart_radio_btn = findViewById(R.id.bar_chart_radio_btn);
        line_chart_radio_btn = findViewById(R.id.line_chart_radio_btn);
        bar_chart_radio_btn_txt = findViewById(R.id.bar_chart_radio_btn_txt);
        line_chart_radio_btn_txt = findViewById(R.id.line_chart_radio_btn_txt);
        bar_chart_radio_btn_relative_layout = findViewById(R.id.bar_chart_radio_btn_relative_layout);
        line_chart_radio_btn_relative_layout = findViewById(R.id.line_chart_radio_btn_relative_layout);
    }

    public void doExtra() {
        startOptionAnimation();
    }

    public void onClick() {
        user_profile_pic.setOnClickListener(view -> {
//            set animation of profile image squeeze release or rounding
            if (!isImageSqueezing && !(user_dropdown_menu_card.getVisibility() == View.VISIBLE)) {
                isImageSqueezing = true;
                showSqueezingAnimation();
            }
//            after that set animation of dialog view name is coming from up to down like
        });

        currency_convert_btn.setOnClickListener(view -> {
            if (user_dropdown_menu_card.getVisibility() == View.VISIBLE)
                user_dropdown_menu_card.setVisibility(View.GONE);
            if (modify_bar_chart_dropdown_card.getVisibility() == View.VISIBLE)
                modify_bar_chart_dropdown_card.setVisibility(View.GONE);
            popUpMenuCardAnimation(currency_dropdown_card);
        });

        usd.setOnClickListener(view -> {
            usd.setBackgroundColor(0xc1c1c1);
            popUpMenuCardAnimation(currency_dropdown_card);
            CURRENCY_TYPE = "USD";
            currency_convert_btn.setText(CURRENCY_TYPE);
            setUserMoney(CURRENCY_TYPE);
        });

        inr.setOnClickListener(view -> {
            inr.setBackgroundColor(0xc1c1c1);
            popUpMenuCardAnimation(currency_dropdown_card);
            CURRENCY_TYPE = "INR";
            currency_convert_btn.setText(CURRENCY_TYPE);
            setUserMoney(CURRENCY_TYPE);
        });
        
        user_notification.setOnClickListener(view -> {
            Intent intent = new Intent(ShowUserDetailActivity.this, NotificationInboxActivity.class);
            intent.setAction(ACTION_INBOX);
            startActivity(intent);
        });

        user_options.setOnClickListener(view -> {
            if (currency_dropdown_card.getVisibility() == View.VISIBLE)
                currency_dropdown_card.setVisibility(View.GONE);
            if (modify_bar_chart_dropdown_card.getVisibility() == View.VISIBLE)
                modify_bar_chart_dropdown_card.setVisibility(View.GONE);
            popUpMenuCardAnimation(user_dropdown_menu_card);
        });

        edit_profile_txt.setOnClickListener(view -> {
            edit_profile_txt.setBackgroundColor(0xc1c1c1);
            popUpMenuCardAnimation(user_dropdown_menu_card);
            Intent intent = new Intent(ShowUserDetailActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        change_email_txt.setOnClickListener(view -> {
            edit_profile_txt.setBackgroundColor(0xc1c1c1);
            popUpMenuCardAnimation(user_dropdown_menu_card);
            Intent intent = new Intent(ShowUserDetailActivity.this, ChangeEmailActivity.class);
            startActivity(intent);
        });

        change_password_txt.setOnClickListener(view -> {
            change_password_txt.setBackgroundColor(0xc1c1c1);
            popUpMenuCardAnimation(user_dropdown_menu_card);
            Intent intent = new Intent(ShowUserDetailActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        sign_out_txt.setOnClickListener(view -> {
            sign_out_txt.setBackgroundColor(0xc1c1c1);
            popUpMenuCardAnimation(user_dropdown_menu_card);
            signOutCurrentUserDialog();
        });

        payment_card.setOnClickListener(view -> {
            Intent intent = new Intent(ShowUserDetailActivity.this, NotificationInboxActivity.class);
            intent.setAction(ACTION_INBOX);
            startActivity(intent);
        });

        wallet_card.setOnClickListener(view -> {
            Intent intent = new Intent(ShowUserDetailActivity.this, WalletActivity.class);
            startActivity(intent);
        });

        reward_card.setOnClickListener(view -> {
            Intent intent = new Intent(ShowUserDetailActivity.this, RewardEarnedActivity.class);
            intent.putExtra("EARNED_REWARD", false);
            startActivity(intent);
        });
        
        modify_bar_txt.setOnClickListener(view -> {
            if (user_dropdown_menu_card.getVisibility() == View.VISIBLE)
                user_dropdown_menu_card.setVisibility(View.GONE);
            if (currency_dropdown_card.getVisibility() == View.VISIBLE)
                currency_dropdown_card.setVisibility(View.GONE);
            popUpMenuCardAnimation(modify_bar_chart_dropdown_card);
            if (modify_bar_chart_dropdown_card.getVisibility() == View.VISIBLE)
                checkIsAvailableModifyBarItems();
        });

        scroll_boxes_fab.setOnClickListener(view -> horizontal_scroll_boxes.fullScroll(HorizontalScrollView.FOCUS_RIGHT));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Code for API level 23 or higher
            // For sanket_satpute_20, use setOnScrollChangeListener method
            horizontal_scroll_boxes.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                // Check if the view is fully scrolled to the right
                if (!v.canScrollHorizontally(1)) {
                    horizontalScrolledRightForFab();
                }
                // Check if the view is fully scrolled to the left
                if (!v.canScrollHorizontally(-1)) {
                    horizontalScrolledLeftForFab();
                }
            });
        } else {
            // Code for API level lower than 23
            // For sanket_satpute_20, use ViewTreeObserver to detect scrolling
            horizontal_scroll_boxes.getViewTreeObserver().addOnScrollChangedListener(() -> {
                int scrollX = horizontal_scroll_boxes.getScrollX();
                int maxScrollX = horizontal_scroll_boxes.getChildAt(0).getMeasuredWidth() - horizontal_scroll_boxes.getMeasuredWidth();

                if (scrollX == maxScrollX) {        //  full right  disappear fab
                    horizontalScrolledRightForFab();
                } else if (scrollX <= (maxScrollX / 2)) {                 //  full left   appear fab
                    horizontalScrolledLeftForFab();
                }
            });
        }

        onClickSetModifyBarDropDown();

        collect_using_time_reward_btn.setOnClickListener(view -> collectDailyPoints());

    }

    private void horizontalScrolledLeftForFab() {
        scroll_boxes_fab.setVisibility(View.VISIBLE);
//                Animation
        ObjectAnimator bXA = ObjectAnimator.ofFloat(scroll_boxes_fab, "scaleX", 0f, 1.1f);
        ObjectAnimator bYA = ObjectAnimator.ofFloat(scroll_boxes_fab, "scaleY", 0f, 1.1f);
        ObjectAnimator aA = ObjectAnimator.ofFloat(scroll_boxes_fab, "alpha", 0f, 1f);
        AnimatorSet bA = new AnimatorSet();
        bA.playTogether(bXA, bYA, aA);
        bA.setDuration(250);
        bA.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator bXA_2 = ObjectAnimator.ofFloat(scroll_boxes_fab, "scaleX", 1.1f, 1f);
                ObjectAnimator bYA_2 = ObjectAnimator.ofFloat(scroll_boxes_fab, "scaleY", 1.1f, 1f);
                AnimatorSet bA_2 = new AnimatorSet();
                bA_2.playTogether(bXA_2, bYA_2);
                bA_2.setDuration(170);
                bA_2.start();
            }
        });
        bA.start();
    }

    private void horizontalScrolledRightForFab() {
        ObjectAnimator sXA = ObjectAnimator.ofFloat(scroll_boxes_fab, "scaleX", 1f, 0.1f);
        ObjectAnimator sYA = ObjectAnimator.ofFloat(scroll_boxes_fab, "scaleY", 1f, 0.1f);
        ObjectAnimator aA = ObjectAnimator.ofFloat(scroll_boxes_fab, "alpha", 1f, 0f);
        AnimatorSet a = new AnimatorSet();
        a.playTogether(sXA, sYA, aA);
        a.setDuration(200);
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                scroll_boxes_fab.setScaleX(0);
                scroll_boxes_fab.setScaleY(0);
            }
        });
        a.start();
    }

    TextView default_selected_textview;

    private void onClickSetModifyBarDropDown() {
        days_3.setOnClickListener(view -> clickedTextview(days_3, default_selected_textview));
        days_5.setOnClickListener(view -> clickedTextview(days_5, default_selected_textview));
        days_7.setOnClickListener(view -> clickedTextview(days_7, default_selected_textview));
        days_auto.setOnClickListener(view -> clickedTextview(days_auto, default_selected_textview));
        weeks_3.setOnClickListener(view -> clickedTextview((TextView) view, default_selected_textview));
        weeks_5.setOnClickListener(view -> clickedTextview((TextView) view, default_selected_textview));
        weeks_7.setOnClickListener(view -> clickedTextview((TextView) view, default_selected_textview));
        weeks_auto.setOnClickListener(view -> clickedTextview((TextView) view, default_selected_textview));
        months_3.setOnClickListener(view -> clickedTextview((TextView) view, default_selected_textview));
        months_5.setOnClickListener(view -> clickedTextview((TextView) view, default_selected_textview));
        months_7.setOnClickListener(view -> clickedTextview((TextView) view, default_selected_textview));
        months_auto.setOnClickListener(view -> clickedTextview((TextView) view, default_selected_textview));
        years_3.setOnClickListener(view -> clickedTextview((TextView) view, default_selected_textview));
        years_5.setOnClickListener(view -> clickedTextview((TextView) view, default_selected_textview));
        years_7.setOnClickListener(view -> clickedTextview((TextView) view, default_selected_textview));
        years_auto.setOnClickListener(view -> clickedTextview((TextView) view, default_selected_textview));

        bar_chart_radio_btn.setOnClickListener(view -> chartChangedSelected(bar_chart_radio_btn.getId()));
        line_chart_radio_btn.setOnClickListener(view -> chartChangedSelected(line_chart_radio_btn.getId()));
        bar_chart_radio_btn_relative_layout.setOnClickListener(view -> chartChangedSelected(bar_chart_radio_btn.getId()));
        line_chart_radio_btn_relative_layout.setOnClickListener(view -> chartChangedSelected(line_chart_radio_btn.getId()));
    }

    SharedPreferences readable_shared_pref;
    SharedPreferences.Editor editable_shared_pref;

    private void clickedTextview(TextView newEnabledText, TextView disabledText) {
        if (disabledText != null) {
            disabledText.setTextColor(newEnabledText.getCurrentTextColor());
            disabledText.setAlpha(0.6f);
        }
        newEnabledText.setTextColor(selected_item_color);
        newEnabledText.setAlpha(1f);
        default_selected_textview = newEnabledText;
        String bar_set_by_value = default_selected_textview.getText().toString().replace(" ", "_").trim();
        if (readable_shared_pref == null)
            readable_shared_pref = getSharedPreferences("MODIFY_BARS", MODE_PRIVATE);
        if (editable_shared_pref == null)
            editable_shared_pref = readable_shared_pref.edit();
        editable_shared_pref.putString("BARS_SET_BY", bar_set_by_value);
        editable_shared_pref.apply();
        setChart();
    }

    private void chartChangedSelected(int chartID) {
        if (chartID == R.id.line_chart_radio_btn) {
            line_chart_radio_btn.setChecked(true);
            bar_chart_radio_btn.setChecked(false);
            barChart.setVisibility(View.GONE);
            lineChart.setVisibility(View.VISIBLE);
        } else {
            line_chart_radio_btn.setChecked(false);
            bar_chart_radio_btn.setChecked(true);
        }
        if (readable_shared_pref == null)
            readable_shared_pref = getSharedPreferences("MODIFY_BARS", MODE_PRIVATE);
        if (editable_shared_pref == null)
            editable_shared_pref = readable_shared_pref.edit();
        editable_shared_pref.putInt("KEY_CHART_IS_ON", chartID);
        editable_shared_pref.apply();
        setChart();
    }

    private void checkIsAvailableModifyBarItems() {
        if (currentUser != null) {
            if (currentUser.getuMoney() != null) {
//                days
                if (currentUser.getuMoney().size() > 3) {
                    days_3.setClickable(true);
                    days_3.setAlpha(0.6f);
                    days_auto.setClickable(true);
                    days_auto.setAlpha(0.6f);
                } else {
                    days_3.setClickable(false);
                    days_3.setAlpha(0.3f);
                    days_auto.setClickable(false);
                    days_auto.setAlpha(0.3f);
                }
                if (currentUser.getuMoney().size() > 5) {
                    days_5.setClickable(true);
                    days_5.setAlpha(0.6f);
                } else {
                    days_5.setClickable(false);
                    days_5.setAlpha(0.3f);
                }
                if (currentUser.getuMoney().size() > 7) {
                    days_7.setClickable(true);
                    days_7.setAlpha(0.6f);
                } else {
                    days_7.setClickable(false);
                    days_7.setAlpha(0.3f);
                }
//                weeks
                if (currentUser.getuMoney().size() > (7 * 3)) {
                    weeks_3.setClickable(true);
                    weeks_3.setAlpha(0.6f);
                    weeks_auto.setClickable(true);
                    weeks_auto.setAlpha(0.6f);
                } else {
                    weeks_3.setClickable(false);
                    weeks_3.setAlpha(0.3f);
                    weeks_auto.setClickable(false);
                    weeks_auto.setAlpha(0.3f);
                }
                if (currentUser.getuMoney().size() > (7 * 5)) {
                    weeks_5.setClickable(true);
                    weeks_5.setAlpha(0.6f);
                } else {
                    weeks_5.setClickable(false);
                    weeks_5.setAlpha(0.3f);
                }
                if (currentUser.getuMoney().size() > (7 * 7)) {
                    weeks_7.setClickable(true);
                    weeks_7.setAlpha(0.6f);
                } else {
                    weeks_7.setClickable(false);
                    weeks_7.setAlpha(0.3f);
                }
//                months
                if (currentUser.getuMoney().size() > (30 * 3)) {
                    months_3.setClickable(true);
                    months_3.setAlpha(0.6f);
                    months_auto.setClickable(true);
                    months_auto.setAlpha(0.6f);
                } else {
                    months_3.setClickable(false);
                    months_3.setAlpha(0.3f);
                    months_auto.setClickable(false);
                    months_auto.setAlpha(0.3f);
                }
                if (currentUser.getuMoney().size() > (30 * 5)) {
                    months_5.setClickable(true);
                    months_5.setAlpha(0.6f);
                } else {
                    months_5.setClickable(false);
                    months_5.setAlpha(0.3f);
                }
                if (currentUser.getuMoney().size() > (30 * 7)) {
                    months_7.setClickable(true);
                    months_7.setAlpha(0.6f);
                } else {
                    months_7.setClickable(false);
                    months_7.setAlpha(0.3f);
                }
//                years
                if (currentUser.getuMoney().size() > (365 * 3)) {
                    years_3.setClickable(true);
                    years_3.setAlpha(0.6f);
                    years_auto.setClickable(true);
                    years_auto.setAlpha(0.6f);
                } else {
                    years_3.setClickable(false);
                    years_3.setAlpha(0.3f);
                    years_auto.setClickable(false);
                    years_auto.setAlpha(0.3f);
                }
                if (currentUser.getuMoney().size() > (365 * 5)) {
                    years_5.setClickable(true);
                    years_5.setAlpha(0.6f);
                } else {
                    years_5.setClickable(false);
                    years_5.setAlpha(0.3f);
                }
                if (currentUser.getuMoney().size() > (365 * 7)) {
                    years_7.setClickable(true);
                    years_7.setAlpha(0.6f);
                } else {
                    years_7.setClickable(false);
                    years_7.setAlpha(0.3f);
                }
            }
        }
    }

    private void getModifyBarSelectedItems() {
//        get set bars by shared
        if (readable_shared_pref == null)
            readable_shared_pref = getSharedPreferences("MODIFY_BARS", MODE_PRIVATE);
        String selected_bar_by = readable_shared_pref.getString("BARS_SET_BY", "auto_days");
        if (selected_bar_by.equals(days_3.getText().toString().trim().replace(" ", "_"))) {
            days_3.setTextColor(selected_item_color);
            days_3.setAlpha(1f);
            default_selected_textview = days_3;
        } else if (selected_bar_by.equals(days_5.getText().toString().trim().replace(" ", "_"))) {
            days_5.setTextColor(selected_item_color);
            days_5.setAlpha(1f);
            default_selected_textview = days_5;
        } else if (selected_bar_by.equals(days_7.getText().toString().trim().replace(" ", "_"))) {
            days_7.setTextColor(selected_item_color);
            days_7.setAlpha(1f);
            default_selected_textview = days_7;
        } else if (selected_bar_by.equals(days_auto.getText().toString().trim().replace(" ", "_"))) {
            days_auto.setTextColor(selected_item_color);
            days_auto.setAlpha(1f);
            default_selected_textview = days_auto;
        } else if (selected_bar_by.equals(weeks_3.getText().toString().trim().replace(" ", "_"))) {
            weeks_3.setTextColor(selected_item_color);
            weeks_3.setAlpha(1f);
            default_selected_textview = weeks_3;
        } else if (selected_bar_by.equals(weeks_5.getText().toString().trim().replace(" ", "_"))) {
            weeks_5.setTextColor(selected_item_color);
            weeks_5.setAlpha(1f);
            default_selected_textview = weeks_5;
        } else if (selected_bar_by.equals(weeks_7.getText().toString().trim().replace(" ", "_"))) {
            weeks_7.setTextColor(selected_item_color);
            weeks_7.setAlpha(1f);
            default_selected_textview = weeks_7;
        } else if (selected_bar_by.equals(weeks_auto.getText().toString().trim().replace(" ", "_"))) {
            weeks_auto.setTextColor(selected_item_color);
            weeks_auto.setAlpha(1f);
            default_selected_textview = weeks_auto;
        } else if (selected_bar_by.equals(months_3.getText().toString().trim().replace(" ", "_"))) {
            months_3.setTextColor(selected_item_color);
            months_3.setAlpha(1f);
            default_selected_textview = months_3;
        } else if (selected_bar_by.equals(months_5.getText().toString().trim().replace(" ", "_"))) {
            months_5.setTextColor(selected_item_color);
            months_5.setAlpha(1f);
            default_selected_textview = months_5;
        } else if (selected_bar_by.equals(months_7.getText().toString().trim().replace(" ", "_"))) {
            months_7.setTextColor(selected_item_color);
            months_7.setAlpha(1f);
            default_selected_textview = months_7;
        } else if (selected_bar_by.equals(months_auto.getText().toString().trim().replace(" ", "_"))) {
            months_auto.setTextColor(selected_item_color);
            months_auto.setAlpha(1f);
            default_selected_textview = months_auto;
        } else if (selected_bar_by.equals(years_3.getText().toString().trim().replace(" ", "_"))) {
            years_3.setTextColor(selected_item_color);
            years_3.setAlpha(1f);
            default_selected_textview = years_3;
        } else if (selected_bar_by.equals(years_5.getText().toString().trim().replace(" ", "_"))) {
            years_5.setTextColor(selected_item_color);
            years_5.setAlpha(1f);
            default_selected_textview = years_5;
        } else if (selected_bar_by.equals(years_7.getText().toString().trim().replace(" ", "_"))) {
            years_7.setTextColor(selected_item_color);
            years_7.setAlpha(1f);
            default_selected_textview = years_7;
        } else if (selected_bar_by.equals(years_auto.getText().toString().trim().replace(" ", "_"))) {
            years_auto.setTextColor(selected_item_color);
            years_auto.setAlpha(1f);
            default_selected_textview = years_auto;
        } else {
            days_auto.setTextColor(selected_item_color);
            years_auto.setAlpha(1f);
            default_selected_textview = years_auto;
        }

//        set radio button which typ charts on
        int chartsID = readable_shared_pref.getInt("KEY_CHART_IS_ON", R.id.bar_chart_radio_btn);
        if (chartsID == R.id.line_chart_radio_btn) {
            line_chart_radio_btn.setChecked(true);
            bar_chart_radio_btn.setChecked(false);
        } else {
            line_chart_radio_btn.setChecked(false);
            bar_chart_radio_btn.setChecked(true);
        }
    }

    private boolean isRotateAvailable() {
        Random random = new Random();
        return random.nextBoolean();
    }

    private int repeatTime() {
        int minimum = 1;
        int maximum = 5;
        return (int) (minimum + (Math.random() * ((maximum - minimum) + 1)));
    }

    private void starterMethod() {
        if (currentUser != null) {
            if (currentUser.getFirst_name() != null) {
                user_name.setText(String.format("%s%s", HELLO_MSG, currentUser.getFirst_name()));
            } else
                user_name.setText(String.format("%s%s", HELLO_MSG, DEFAULT_USER_NAME));

            if (CURRENCY_TYPE == null) {
                CURRENCY_TYPE = DOLLAR_OR_RUPEE_RUPEE;
            }
            setUserMoney(CURRENCY_TYPE);
            currency_convert_btn.setText(CURRENCY_TYPE);

            UserImageLoading background_img_loading = new UserImageLoading(true, user_profile_pic);
            background_img_loading.execute();

            if (currentUser.getIsNotifyMsgAvailable())
                is_notification_available.setVisibility(View.VISIBLE);
            else
                is_notification_available.setVisibility(View.GONE);
            String payment_info ;
            if (currentUser.getUpiID() != null) {
                payment_info = currentUser.getUpiID();
            } else if (currentUser.getPhone_number() != 0L) {
                payment_info = String.valueOf(currentUser.getPhone_number());
            } else {
                payment_info = currentUser.getEmail_id();
            }
            user_email_or_no.setText(payment_info);
            setChart();
        } else {
            View fetching_view = LayoutInflater.from(ShowUserDetailActivity.this).inflate(R.layout.user_fetch_layout, null);
            material_user_fetch_dialog = new MaterialAlertDialogBuilder(ShowUserDetailActivity.this)
                    .setView(fetching_view);
            alert_user_fetch_dialog = material_user_fetch_dialog.create();
            alert_user_fetch_dialog.show();
            Toast.makeText(this, "Wait Until User is Fetched", Toast.LENGTH_SHORT).show();
            fetchUser();
        }
    }

    private void signOutCurrentUserDialog() {
        MaterialButton cancel_sign_out, yes_sign_out;

        View view = LayoutInflater.from(ShowUserDetailActivity.this).inflate(R.layout.sign_out_user_layout, null);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(ShowUserDetailActivity.this, R.style.RoundedCornerAlertDialog)
                .setView(view);
        AlertDialog alert = dialog.create();
        alert.show();
        alert.setCancelable(false);

        cancel_sign_out = view.findViewById(R.id.cancel_sign_out_btn);
        yes_sign_out = view.findViewById(R.id.yes_sign_out_btn);

        cancel_sign_out.setOnClickListener(v -> alert.dismiss());
        yes_sign_out.setOnClickListener(v -> {
            alert.dismiss();
            signOutCurrentUser();
        });
    }


    private Bitmap setUserProfilePicture() {
        try {
            USER_IMAGE_BITMAP_CONST = SaveUserImageCache.getImage(ShowUserDetailActivity.this, "user_profile_image");
            if (USER_IMAGE_BITMAP_CONST != null) {
                return USER_IMAGE_BITMAP_CONST;
            } else if (currentUser.getProfilePicturePath() != null) {
                isImageDownloadedCompleted = false;
                squeezeAnimation(1000, null, true);
                user_profile_pic.setAlpha(0.6f);
                StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(
                        getFirebaseStoragePath() +
                                currentUser.getProfilePicturePath());

                final long ONE_MEGABYTE = 1024 * 1024 * 5;

                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                    user_profile_pic.setAlpha(1f);
                    isImageDownloadedCompleted = true;
                    USER_IMAGE_BITMAP_CONST = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    SaveUserImageCache.putImage(ShowUserDetailActivity.this, "user_profile_image", USER_IMAGE_BITMAP_CONST);
                }).addOnFailureListener(exception -> {
                    user_profile_pic.setAlpha(1f);
                    isImageDownloadedCompleted = true;
                    Toast.makeText(ShowUserDetailActivity.this, "Failed to load Image", Toast.LENGTH_SHORT).show();
                    USER_IMAGE_BITMAP_CONST = null;
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

    private void fetchUser() {
        if (f_auth.getCurrentUser() != null) {
            f_db.collection("users").document(f_auth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        currentUser = documentSnapshot.toObject(MUser.class);
                        if (alert_user_fetch_dialog != null)
                            alert_user_fetch_dialog.dismiss();
                        starterMethod();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(ShowUserDetailActivity.this, "Please try again " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    int i = 0;
    String time_str = null, remain_time_str;
    private void setUserUsingTime() {
        String hr_text = "hr ", min_text = "min", used_text = " used";
        user_used_time_progress_bar.setMax(TimerIntentService.CONSTANT_FINAL_TIME);
        Handler handler = new Handler();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                long usage_time = time_service.getCurrentTime();
                user_used_time_progress_bar.setProgress((int) usage_time);
                time_str = getHrs(usage_time) + hr_text + getMin(usage_time) + min_text + used_text;
                user_used_time.setText(time_str);
                if (usage_time <= TimerIntentService.CONSTANT_FINAL_TIME) {
                    remain_time_str = getHrs(TimerIntentService.CONSTANT_FINAL_TIME - usage_time) + hr_text + getMin(TimerIntentService.CONSTANT_FINAL_TIME - usage_time) + min_text;
                    user_remain_time.setText(remain_time_str);
                    handler.postDelayed(this, 1000);
                }
                else {
                    String time_str = "0" + hr_text + "0" + min_text;
                    user_remain_time.setText(time_str);
                    if (i < 1 && !(currentUser.getmTime().get(currentUser.getmTime().size() - 1).isDailyRewardCollected())) {
                        usingTimeProgressComplete();
                        i++;
                    }
                    else
                        handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void setUserUsingTimeRewardBtn() {
        collect_using_time_reward_btn.setVisibility(View.VISIBLE);
        using_time_layout.setVisibility(View.INVISIBLE);
        app_using_time_txt.setVisibility(View.GONE);
        Handler handler = new Handler();
        handler.postDelayed(() -> usedTimeTiltAnim(2.7f), new Random().nextInt(5000));
    }

    private void setUserMoney(String currentType) {
        NumberFormat moneyFormatter = NumberFormat.getInstance();
        if (currentType.equalsIgnoreCase(DOLLAR_OR_RUPEE_RUPEE)) {
            which_currency_txt.setText(R.string.rupee);
            if (currentUser != null)
                user_money.setText(moneyFormatter.format(currentUser.getTotalMoney()));
            else
                user_money.setText(String.valueOf(0));
        } else if (currentType.equalsIgnoreCase(DOLLAR_OR_RUPEE_DOLLAR)) {
            which_currency_txt.setText(R.string.dollar);
            if (currentUser != null) {
                if (rupeeValueByDollar == 0) {
                    FirebaseRemoteConfig f_config = FirebaseRemoteConfig.getInstance();
                    rupeeValueByDollar = f_config.getDouble(rupeeValueByDollarStr);
                }
                double rupee_value_in_dollar = currentUser.getTotalMoney() / rupeeValueByDollar;
                BigDecimal decimals = new BigDecimal(String.valueOf(rupee_value_in_dollar));
                if (rupee_value_in_dollar < 1.0) {
                    user_money.setText(String.valueOf(decimals.setScale(2, RoundingMode.HALF_UP)));
                } else if (rupee_value_in_dollar < 10.0) {
                    user_money.setText(String.valueOf(decimals.setScale(1, RoundingMode.HALF_UP)));
                } else {
                    user_money.setText(moneyFormatter.format(Math.round(rupee_value_in_dollar)));
                }
            } else {
                user_money.setText(String.valueOf(0));
            }
        }
    }

    private void signOutCurrentUser() {
        if (f_auth.getCurrentUser() != null) {
            if (currentUser != null) {
                f_db.collection("users").document(f_auth.getCurrentUser().getUid())
                        .set(currentUser);
            }
            try {
                Intent timer_intent_service = new Intent(ShowUserDetailActivity.this, TimerIntentService.class);
                stopService(timer_intent_service);
            } catch (Exception e) { e.printStackTrace(); }
            f_auth.signOut();
            currentUser = null;
            Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to Sign Out", Toast.LENGTH_SHORT).show();
        }
    }

    private void setChart() {
        if (currentUser.getuMoney() != null) {
            if (currentUser.getuMoney().size() > 2) {

                earning_period_txt.setVisibility(View.VISIBLE);
                modify_bar_txt.setVisibility(View.VISIBLE);
                modify_bar_txt.setClickable(true);
                chart_layout.setVisibility(View.VISIBLE);

                if (readable_shared_pref == null)
                    readable_shared_pref = getSharedPreferences("MODIFY_BARS", MODE_PRIVATE);

                int defaultChartID = readable_shared_pref.getInt("KEY_CHART_IS_ON", R.id.bar_chart_radio_btn);
                String set_bars_by_type = readable_shared_pref.getString("BARS_SET_BY", "auto_days");

                if (defaultChartID == R.id.line_chart_radio_btn) {      // line chart
                    lineChart.setVisibility(View.VISIBLE);
                    barChart.setVisibility(View.GONE);
                    setLineChart(currentUser.getuMoney(), set_bars_by_type);
                } else {            // bar chart
                    lineChart.setVisibility(View.GONE);
                    barChart.setVisibility(View.VISIBLE);
                    setBarChart(currentUser.getuMoney(), set_bars_by_type);
                }
            } else {
                earning_period_txt.setVisibility(View.GONE);
                modify_bar_txt.setVisibility(View.GONE);
                modify_bar_txt.setClickable(false);
                chart_layout.setVisibility(View.GONE);
            }
        } else {
            currentUser.setuMoney(new ArrayList<>());
            setChart();
        }
    }

    private void setLineChartData(ArrayList<String> xAxisData) {
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < xAxisData.size(); i++) {
            int val = Integer.parseInt(xAxisData.get(i));
            values.add(new Entry(i, val));
        }

        int start_color = Color.parseColor("#FFA500");
        int end_color = ContextCompat.getColor(this, R.color.red_orange);
        LineDataSet setL;
        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            setL = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            setL.setValues(values);
            setL.setColor(start_color);
            setL.setCircleColor(end_color);
            setL.setLineWidth(2f);
            setL.setCircleRadius(5f);
            setL.setDrawValues(true);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            setL = new LineDataSet(values, null);
            setL.setDrawIcons(false);
            setL.setValueTextColor(default_color);
            setL.setColor(start_color);
            setL.setCircleColor(end_color);
            setL.setLineWidth(2f);
            setL.setCircleRadius(5f);
            setL.setDrawValues(true);
            ArrayList<ILineDataSet> dataSetsLine = new ArrayList<>();
            dataSetsLine.add(setL);
            LineData data = new LineData(dataSetsLine);
            data.setValueTextSize(12f);

            lineChart.setData(data);
            lineChart.getLineData().setValueTextColor(default_color);
        }
    }

    private void setLineChart(ArrayList<UMoney> u_money_list, String set_bars_by_type) {

        lineChart.getDescription().setEnabled(false); // description label
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false); // remove horizontal grid
        lineChart.setDrawBorders(false); // draw a rectangle border around bar
        lineChart.getXAxis().setDrawGridLines(false); // remove vertical middle grid lines
        //            barChart.getXAxis().setEnabled(false); // remove upper border and upper values
        lineChart.getAxisRight().setDrawAxisLine(false); // right border line hide
        lineChart.getAxisLeft().setDrawAxisLine(false); // left border line hide
        lineChart.getAxisRight().setEnabled(false); // right side extra values
        lineChart.getXAxis().setDrawLimitLinesBehindData(false);
        lineChart.getAxisRight().setDrawLimitLinesBehindData(false);
        lineChart.getXAxis().setDrawAxisLine(false); // upper border line hide

        Map<Integer, ArrayList<String>> result_list = getDataDates(u_money_list, set_bars_by_type.charAt(0), set_bars_by_type, "");
        ArrayList<String> xAxisLabel = result_list.get(0);
        ArrayList<String> xAxisLabelValues = result_list.get(1);

        lineChart.getXAxis().setGranularity(1f);
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // BOTTOM
        lineChart.getXAxis().setSpaceMin(0.5f);
        lineChart.getXAxis().setTextColor(default_color);
        if (xAxisLabel != null) {
            lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        } else {
            Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
        }

        lineChart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        Legend l = lineChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.NONE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(10f);
        if (xAxisLabelValues != null)
            setLineChartData(xAxisLabelValues);
        lineChart.invalidate();

        lineChart.getLegend().setTextColor(default_color);
        lineChart.getAxisLeft().setTextColor(default_color);

    }

    private void setBarChart(ArrayList<UMoney> u_money_list, String set_bars_by_type) {
        CustomBarChartRender barChartRender = new CustomBarChartRender(barChart, barChart.getAnimator(), barChart.getViewPortHandler());
        barChartRender.setRadius(20);
        barChart.setRenderer(barChartRender);

        barChart.setDrawValueAboveBar(true);
        barChart.setDrawBarShadow(false);
        barChart.getDescription().setEnabled(false); // description label
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false); // remove horizontal grid
        barChart.setDrawBorders(false); // draw a rectangle border around bar
        barChart.getXAxis().setDrawGridLines(false); // remove vertical middle grid lines
        //            barChart.getXAxis().setEnabled(false); // remove upper border and upper values
        barChart.getAxisRight().setDrawAxisLine(false); // right border line hide
        barChart.getAxisLeft().setDrawAxisLine(false); // left border line hide
        barChart.getAxisRight().setEnabled(false); // right side extra values
        barChart.getXAxis().setDrawLimitLinesBehindData(false);
        barChart.getAxisRight().setDrawLimitLinesBehindData(false);
        barChart.setHighlightFullBarEnabled(false);
        barChart.getXAxis().setDrawAxisLine(false); // upper border line hide

        Map<Integer, ArrayList<String>> result_list = getDataDates(u_money_list, set_bars_by_type.charAt(0), set_bars_by_type, "");
        ArrayList<String> xAxisLabel = result_list.get(0);
        ArrayList<String> xAxisLabelValues = result_list.get(1);

        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // BOTTOM
        barChart.getXAxis().setTextColor(default_color);
        if (xAxisLabel != null) {
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        } else {
            Toast.makeText(ShowUserDetailActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
        }

//                barChart.getXAxis().setDrawGridLines(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.NONE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(10f);
        if (xAxisLabelValues != null)
            setData(xAxisLabelValues);
        barChart.invalidate();

        barChart.getLegend().setTextColor(default_color);
        barChart.getAxisLeft().setTextColor(default_color);
    }

    private Map<Integer, ArrayList<String>> getDataDates(ArrayList<UMoney> u_money_list, char how_much_char, String set_bars_by_type, String what_type) {
        Date today_date = new Date(System.currentTimeMillis());
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");

        Map<Integer, ArrayList<String>> listMap = new HashMap<>();
        ArrayList<String> newListBarsData = new ArrayList<>();
        ArrayList<String> newListBarsValue = new ArrayList<>();

        if (set_bars_by_type.contains("weeks")) {
            // setting all
            if (u_money_list.size() > 10) {
                int recent_week = -1;
                String week_str = null;

                int how_much = Character.getNumericValue(how_much_char);
                int m = ((u_money_list.size() > (how_much * 7)) ? how_much * 7 : 0);
                int length_to_cover = (m > 0) ? u_money_list.size() - m: 0;

                Calendar calendar = Calendar.getInstance();
                for (int i = (u_money_list.size() - 1); i >= length_to_cover; i--) {
                    try {
                        java.util.Date date = date_format.parse(u_money_list.get(i).getMoneyDate());
                        int coins = u_money_list.get(i).getCoins();

                        if (date != null) {
                            int date_of_date = date.getDate();
                            int last_date = date.getDate();
                            calendar.setTime(date);
                            int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);

                            if (weekNumber == recent_week) {
                                int recent_coins = Integer.parseInt(newListBarsValue.get(newListBarsValue.size() - 1));
                                newListBarsValue.set(newListBarsValue.size() - 1, String.valueOf(Math.round(coins + recent_coins)));
                            } else {
                                if (week_str != null)
                                    if (week_str.contains("-"))
                                        week_str = null;
                                if (week_str == null) {
                                    week_str = " " + date_of_date;
                                } else {
                                    week_str = last_date + " -" + week_str ;
                                    newListBarsData.add(newListBarsData.size(), week_str);
                                    newListBarsValue.add(newListBarsValue.size(), String.valueOf(Math.round(coins)));
                                    recent_week = weekNumber;
                                }
                            }
                        } else {
//                            handle null
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
//                        handle error
                    }
                }
            } else {
//                reset the values
                Toast.makeText(this, Character.getNumericValue(how_much_char) + " years is not completed", Toast.LENGTH_SHORT).show();
            }
        } else if (set_bars_by_type.contains("months")) {
            if (u_money_list.size() > 50) {
                int last_month = -1, last_year = -1;
                int how_much = Character.getNumericValue(how_much_char);
                int m = (u_money_list.size() > (30 * how_much)) ? (30 * how_much) : 0;
                int length_to_cover = (m > 0) ? u_money_list.size() - m: 0;
                for (int i = u_money_list.size() - 1; i > length_to_cover; i--) {
                    try {
                        java.util.Date date = date_format.parse(u_money_list.get(i).getMoneyDate());
                        if (date != null) {
                            int month = date.getMonth();
                            int year = date.getYear();
                            int coins = u_money_list.get(i).getCoins();
                            if (last_month == month && last_year == year) {
                                int recent_coins = Integer.parseInt(newListBarsValue.get(newListBarsValue.size() - 1));
                                newListBarsValue.set(newListBarsValue.size() - 1, String.valueOf(Math.round(recent_coins + coins)));
                            } else {
                                newListBarsData.add(newListBarsData.size(), month_names[month].toUpperCase());
                                newListBarsValue.add(newListBarsValue.size(), String.valueOf(Math.round(coins)));
                                last_month = month;
                                last_year = year;
                            }
                        } else {
//                            reset the values
//                            do null work
                        }
                    } catch (ParseException e) {
//                        reset the values
//                        do error work
                        e.printStackTrace();
                    }
                }
            } else {
//                reset the values
                Toast.makeText(this, Character.getNumericValue(how_much_char) + " months not completed.", Toast.LENGTH_SHORT).show();
            }
        } else if (set_bars_by_type.contains("years")) {
            if (700 < u_money_list.size()) {
                int last_year = -1;
                int how_much = Character.getNumericValue(how_much_char);
                int m = (u_money_list.size() > (365 * how_much)) ? (365 * how_much) : 0;
                int length_to_cover = (m > 0) ? u_money_list.size() - m: 0;
                for (int i = (u_money_list.size() - 1); i > length_to_cover; i--) {
                    try {
                        java.util.Date date = date_format.parse(u_money_list.get(i).getMoneyDate());
                        if (date != null) {
                            int year = date.getYear();
                            int coins = u_money_list.get(i).getCoins();
                            if (year == last_year) {
                                int recent_coins = Integer.parseInt(newListBarsValue.get(newListBarsValue.size() - 1));
                                newListBarsValue.set(newListBarsValue.size() - 1, String.valueOf((Math.round(recent_coins + coins))));
                            } else {
                                newListBarsData.add(newListBarsData.size(), String.valueOf(year));
                                newListBarsValue.add(newListBarsValue.size(), String.valueOf(Math.round(coins)));
                                last_year = year;
                            }
                        } else {
//                            reset the values
//                            error null value
                        }
                    } catch (Exception e) {
//                        reset the values
//                        error code optimization
                        e.printStackTrace();
                    }
                }
            } else {
//                reset the values
                Toast.makeText(this, Character.getNumericValue(how_much_char) + " years is not completed", Toast.LENGTH_SHORT).show();
            }
        } else {
            int how_much = Character.getNumericValue(how_much_char);
            /* - 1*/
            int length_to_cover = (how_much == 7 || how_much == 3 || how_much == 5) ? how_much : (Math.min(u_money_list.size(), 7));

            for (int i = (u_money_list.size() - 1); i >= (u_money_list.size() - length_to_cover); i--) {
                String date_str = u_money_list.get(i).getMoneyDate();
                newListBarsValue.add(String.valueOf(Math.round(u_money_list.get(i).getCoins())));
                if (date_str.equalsIgnoreCase(today_date.toString()))
                    newListBarsData.add("today");
                else {
                    try {
                        java.util.Date date = date_format.parse(date_str);
                        if (date != null) {
                            String full_date = date.getDate() + " / " + ((how_much > 5) ? date.getMonth() + 1 : month_names[date.getMonth()].toUpperCase());
                            newListBarsData.add(full_date);
                        }
                        else
                            newListBarsData.add("N/A");
                    } catch (ParseException e) {
                        newListBarsData.add("N/A");
                        e.printStackTrace();
                    }
                }
            }
        }
        Collections.reverse(newListBarsData);
        Collections.reverse(newListBarsValue);

        listMap.put(0, newListBarsData);
        listMap.put(1, newListBarsValue);
        return listMap;
    }

    BarDataSet setB;
    private void setData(ArrayList<String> x_axis_data_list) {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < x_axis_data_list.size(); i++) {
            int val = Math.round(Float.parseFloat(x_axis_data_list.get(i)));
            Log.d("values_m", ""+val);
            values.add(new BarEntry(i, val));
        }

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            setB = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            setB.setValues(values);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            setB = new BarDataSet(values, null);
            setB.setDrawIcons(false);
            setB.setValueTextColor(default_color);
            int start_color = Color.parseColor("#FFA500");
            int end_color = ContextCompat.getColor(this, R.color.red_orange);
//            int endColor5 = ContextCompat.getColor(this, android.R.color.holo_orange_dark);
            List<GradientColor> gradientFills = new ArrayList<>();
            gradientFills.add(new GradientColor(start_color, end_color));
            setB.setGradientColors(gradientFills);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(setB);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(12f);
            data.setBarWidth(0.5f);

            barChart.setData(data);
            barChart.getBarData().setValueTextColor(default_color);
        }
    }

    int setMaxImageUploadedProgress = 0;
//    dialog methods
    private void showUserProfileDialog() {
        try {
            if (currentUser != null) {
                MaterialButton change_image_btn, remove_image_btn, edit_info_btn, hide_btn, set_image_btn;
                TextView coin_t, money_t, mobile_no_t, email_t, username_t, upi_id_t, dob_t, age_t, user_full_name_t,
                        image_uploading_percentage, email_txt, username_txt, upi_id_txt, dob_txt, age_txt, joined_date_str, joined_txt;
                ImageView user_profile_image;
                ProgressBar image_uploading_downloading_progress;

                View view = LayoutInflater.from(ShowUserDetailActivity.this).inflate(R.layout.user_image_click_show_user_profile_layout, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
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

                Bitmap user_image = SaveUserImageCache.getImage(ShowUserDetailActivity.this, "user_profile_image");
                if (user_image != null && !this.isDestroyed()) {
                    change_image_btn.setVisibility(View.VISIBLE);
                    remove_image_btn.setVisibility(View.VISIBLE);
                    set_image_btn.setVisibility(View.GONE);
                    Glide.with(this)
                            .asBitmap()
                            .error(R.drawable.orange_man_user_profile_picture)
                            .load(user_image)
                            .into(user_profile_image);
                    if (!this.isDestroyed() && savedImageUri != null && currentUser.getProfilePicturePath() == null && f_auth.getCurrentUser() != null) {
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
                                                                Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                                                Toast.makeText(this, "Details Updated Successfully", Toast.LENGTH_SHORT).show();
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
                } else if (currentUser.getProfilePicturePath() != null && !this.isDestroyed()) {
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
                        Glide.with(ShowUserDetailActivity.this)
                                .load(bytes)
                                .error(R.drawable.orange_man_user_profile_picture)
                                .into(user_profile_image);
                        user_profile_image.setAlpha(1f);
                        image_uploading_downloading_progress.setVisibility(View.GONE);
                        Bitmap userBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        SaveUserImageCache.putImage(ShowUserDetailActivity.this, "user_profile_image", userBitmap);
                    }).addOnFailureListener(exception -> {
                        change_image_btn.setVisibility(View.VISIBLE);
                        remove_image_btn.setVisibility(View.VISIBLE);
                        set_image_btn.setVisibility(View.GONE);
                        user_profile_image.setAlpha(1f);
                        image_uploading_downloading_progress.setVisibility(View.GONE);
                        Toast.makeText(ShowUserDetailActivity.this, "Failed to load Image", Toast.LENGTH_SHORT).show();
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
                                    month_names[formattedJoinedDateStr.getMonthValue() - 1].toUpperCase() + " " +
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
                            full_date_of_birth = birthDate.getDayOfMonth() + " " + month_names[birthDate.getMonthValue() - 1].toUpperCase() + " " + birthDate.getYear();
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
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                hide_btn.setOnClickListener(v -> dialog.dismiss());
                edit_info_btn.setOnClickListener(v -> {
                    dialog.dismiss();
                    Intent intent = new Intent(ShowUserDetailActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                });
                set_image_btn.setOnClickListener(v -> dialogSetImageDialog(dialog));
                change_image_btn.setOnClickListener(v -> dialogSetImageDialog(dialog));
                remove_image_btn.setOnClickListener(v -> dialogRemoveImageDialog(dialog));
            } else {
                Toast.makeText(this, "User is Not Found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int myDailyEarnedPoints = 0;
    String collect_reward_str = "Collect Reward";
    private void collectDailyPoints() {
        if (currentUser != null) {
            if (!(currentUser.getmTime().get(currentUser.getmTime().size() - 1).isDailyRewardCollected())) {
                TextView dailyEarnedPoints;
                MaterialButton collectRewardBtn;
                CircularProgressIndicator collect_daily_reward_progress;
                LottieAnimationView two_dots_animation;
                View v = LayoutInflater.from(ShowUserDetailActivity.this).inflate(R.layout.app_using_time_reward_layout, null);
                MaterialAlertDialogBuilder alert_builder = new MaterialAlertDialogBuilder(ShowUserDetailActivity.this, R.style.RoundedCornerAlertDialog)
                        .setView(v);
                AlertDialog dialog = alert_builder.create();
                dialog.setCancelable(false);
                dialog.show();

                if (f_config == null)
                    connectToFirebase();
                myDailyEarnedPoints = (int) f_config.getLong(APP_USING_TIME_REWARD_VALUE_STR);

                dailyEarnedPoints = v.findViewById(R.id.points_txt);
                collectRewardBtn = v.findViewById(R.id.collect_reward_btn);
                collect_daily_reward_progress = v.findViewById(R.id.collect_reward_btn_progress);
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
                    collectRewardBtn.setText("");
                    collect_daily_reward_progress.setVisibility(View.VISIBLE);
                    currentUser.setTotalCoins(currentUser.getTotalCoins() + myDailyEarnedPoints);
                    UMoney uMoney;
                    MTime mTime;
                    String todayDate = String.valueOf(new Date(System.currentTimeMillis()));
                    String mTimeLastDate = currentUser.getmTime().get(currentUser.getmTime().size() - 1).getDaily_limit_date();
                    if (todayDate.trim().equalsIgnoreCase(mTimeLastDate.trim())) {
                        currentUser.getmTime().get(currentUser.getmTime().size() - 1).setDailyRewardCollected(true);
//                        uTime set all fields set usage time completed
                    }
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
                        fetchUser();
                    f_db.collection("users").document(f_user.getUid()).set(currentUser)
                            .addOnSuccessListener(unused -> {   // saved data successful
                                collect_daily_reward_progress.setVisibility(View.GONE);
                                collectRewardBtn.setText(collect_reward_str);
                                dialog.dismiss();
                                growBarForTodayTimeUsedAppCompletedSuccess();
//                                disappear collect reward button and appear the daily time progress bar
                                compressTimeRewardBtnAnim();
                            }).addOnFailureListener(e -> {      // saving data failed
                                collect_daily_reward_progress.setVisibility(View.GONE);
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
//                                disappear collect reward button and appear the daily time progress bar
                                compressTimeRewardBtnAnim();
                            });
                });
            } else {
                Toast.makeText(ShowUserDetailActivity.this, "Already Reward Collected", Toast.LENGTH_SHORT).show();
                compressTimeRewardBtnAnim();
            }
        }
    }

    private void dialogSetImageDialog(AlertDialog recentDialog) {
        recentDialog.dismiss();

        MaterialButton camera_btn, gallery_btn;
        View view = LayoutInflater.from(ShowUserDetailActivity.this).inflate(R.layout.edit_or_choose_user_profile_picture_dialog_layout, null);
        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(ShowUserDetailActivity.this, R.style.RoundedCornerAlertDialog);
        alert.setView(view);

        camera_btn = view.findViewById(R.id.open_camera_btn);
        gallery_btn = view.findViewById(R.id.open_gallery_btn);

        AlertDialog dialog = alert.create();
        dialog.show();

        camera_btn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ShowUserDetailActivity.this, new String[]{
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

    boolean isImageDeletedFromCache = false;
    private void dialogRemoveImageDialog(AlertDialog recentDialog) {
        recentDialog.dismiss();

        MaterialButton deleteBtn, skipBtn;
        ProgressBar deleteBtnProgress;
        TextView deleteBtnTxt;

        View view = LayoutInflater.from(ShowUserDetailActivity.this).inflate(R.layout.delete_user_profile_picture_layout, null);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(ShowUserDetailActivity.this, R.style.RoundedCornerAlertDialog);
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
            File cacheDir = getCacheDir();
            File imageFile = new File(cacheDir, "user_profile_image");
            if (imageFile.exists()) {
                if (imageFile.delete()) {
                    isImageDeletedFromCache = true;
                    Toast.makeText(this, "File is Deleted", Toast.LENGTH_SHORT).show();
                }
            }
            if (currentUser.getProfilePicturePath() != null) {
                StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(currentUser.getProfilePicturePath());
                imageRef.delete().addOnSuccessListener(aVoid -> {
                    deleteBtnProgress.setVisibility(View.GONE);

                    UserImageLoading background_img_loading = new UserImageLoading(true, user_profile_pic);
                    background_img_loading.execute();

                    deleteBtnTxt.setText(deleted_str);
                    if (isImageDeletedFromCache)
                        Toast.makeText(this, "Image is Deleted", Toast.LENGTH_SHORT).show();
                    currentUser.setProfilePicturePath(null);
                    alert.dismiss();
                    showUserProfileDialog();
                }).addOnFailureListener(e -> {
                    deleteBtnProgress.setVisibility(View.GONE);
                    deleteBtnTxt.setText(failed_str);
                    Toast.makeText(this, "Image Not Deleted", Toast.LENGTH_SHORT).show();
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

    private void growBarForTodayTimeUsedAppCompletedSuccess() {
        if (setB != null && barChart != null) {
            int coins = currentUser.getuMoney().get(currentUser.getuMoney().size() - 1).getCoins();
Log.d("moneyX", ""+coins);
            setB.addEntry(new BarEntry(setB.getEntryCount(), coins));
            setB.notifyDataSetChanged();
            barChart.notifyDataSetChanged();
            barChart.invalidate();
            barChart.animate();

//            not implemented
        }
    }

//    on result activity
ActivityResultLauncher<Intent> launchCameraImageChooserActivity = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Bitmap userProfilePictureBitmap = (Bitmap) data.getExtras().get("data");
                    savedImageUri = SaveUserImageCache.putImage(ShowUserDetailActivity.this, "user_profile_image", userProfilePictureBitmap);
                    currentUser.setProfilePicturePath(null);
                    showUserProfileDialog();

                    UserImageLoading background_img_loading = new UserImageLoading(true, user_profile_pic);
                    background_img_loading.execute();
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
                            Bitmap userProfilePictureBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            savedImageUri = SaveUserImageCache.putImage(ShowUserDetailActivity.this, "user_profile_image", userProfilePictureBitmap);
                            currentUser.setProfilePicturePath(null);
                            showUserProfileDialog();

                            UserImageLoading background_img_loading = new UserImageLoading(true, user_profile_pic);
                            background_img_loading.execute();
                        } catch (IOException e) {
                            Toast.makeText(ShowUserDetailActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void connectToFirebase() {
        if (f_auth == null)
            f_auth = FirebaseAuth.getInstance();
        if (f_db == null)
            f_db = FirebaseFirestore.getInstance();
        if (f_config == null)
            f_config = FirebaseRemoteConfig.getInstance();
        if (f_user == null)
            f_user = f_auth.getCurrentUser();
        if (user_ref == null && f_user != null)
            user_ref = f_db.collection("users").document(f_user.getUid());
    }

//    Extra Methods

    private long getHrs(long milli) {
        return TimeUnit.MILLISECONDS.toHours(milli);
    }

    private long getMin(long milli) {
        long min = ((milli / (1000*60)) % 60);
        if (min >= 0 && min <= 9)
            return min;
        return (milli / (1000*60) % 60);
    }

    private float getFloatRandom() {
        Random random = new Random();
        return random.nextFloat() * ((float) 1.4 - (float) 0.7) + (float) 0.7;
    }

    private int getIntRandom(int how) {
        Random random = new Random();
        return random.nextInt(how);
    }

    private String generateMoneyId() {
        String moneyID;
        UUID uuid = UUID.randomUUID();
        moneyID = uuid.toString() + "~ID$MONEY";
        return moneyID;
    }

    //    Animations
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
        squeezingAnimationMaxX = ObjectAnimator.ofFloat(user_profile_outer, "scaleX", 1f, 0.9f);
        squeezingAnimationMaxY = ObjectAnimator.ofFloat(user_profile_outer, "scaleY", 1f, 0.9f);
        squeezingAnimationMax = new AnimatorSet();
        squeezingAnimationMax.playTogether(squeezingAnimationMaxY, squeezingAnimationMaxX);
        squeezingAnimationMax.setDuration(1000);
        squeezingAnimationMax.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                squeezingAnimationMinX = ObjectAnimator.ofFloat(user_profile_outer, "scaleX", 0.9f, 1f);
                squeezingAnimationMinY = ObjectAnimator.ofFloat(user_profile_outer, "scaleY", 0.9f, 1f);
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

    private void startOptionAnimation() {
        float size = getFloatRandom();
        MaterialCardView card = user_option_cards[getIntRandom(4)];
        ObjectAnimator cardAnimatorX = ObjectAnimator.ofFloat(card, "scaleX", card.getScaleX(), size);
        ObjectAnimator cardAnimatorY = ObjectAnimator.ofFloat(card, "scaleY", card.getScaleY(), size);
        cardAnimatorX.setDuration(getIntRandom(500));
        cardAnimatorY.setDuration(getIntRandom(500));
        AnimatorSet animationSet = new AnimatorSet();
        animationSet.playTogether(cardAnimatorX, cardAnimatorY);
        animationSet.start();
        cardAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startOptionAnimation();
            }
        });
    }

    private void popUpMenuCardAnimation(MaterialCardView card) {
        float trans_value = 35;
        if (card.getVisibility() == View.GONE) {
            card.setVisibility(View.VISIBLE);
            card.setAlpha(0f);
            card.setTranslationY(-trans_value);
            ObjectAnimator card_visible_trans_y = ObjectAnimator.ofFloat(card, "translationY", -trans_value, 0f);
            ObjectAnimator card_visible_alpha = ObjectAnimator.ofFloat(card, "alpha", 0f, 1f);
            AnimatorSet animation = new AnimatorSet();
            animation.playTogether(card_visible_trans_y, card_visible_alpha);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.start();
        } else {
            card.setAlpha(1f);
            card.setTranslationY(0f);
            ObjectAnimator card_visible_trans_y = ObjectAnimator.ofFloat(card, "translationY", 0f, -trans_value);
            ObjectAnimator card_visible_alpha = ObjectAnimator.ofFloat(card, "alpha", 1f, 0f);
            AnimatorSet animation = new AnimatorSet();
            animation.playTogether(card_visible_trans_y, card_visible_alpha);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
            animation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    card.setVisibility(View.GONE);
                }
            });
        }
    }

    private void usingTimeProgressReAppearAnim() {
        remainTimeLayout.setAlpha(0f);
        remainTimeLayout.setTranslationY(-10f);
        user_used_time.setAlpha(0f);
        user_used_time.setTranslationY(-10f);
        user_used_time_progress_bar.setScaleY(0f);
        user_used_time_progress_bar.setScaleX(0f);
        app_using_time_txt.setVisibility(View.VISIBLE);
        using_time_layout.setVisibility(View.VISIBLE);

        ObjectAnimator remainTimeAlpha = ObjectAnimator.ofFloat(remainTimeLayout, "alpha", 0f, 1f);
        ObjectAnimator remainTimeTransY = ObjectAnimator.ofFloat(remainTimeLayout, "translationY" , -10f, 0f);

        ObjectAnimator usedTimeAlpha = ObjectAnimator.ofFloat(user_used_time, "alpha" , 0f, 1f);
        ObjectAnimator usedTimeTransY = ObjectAnimator.ofFloat(user_used_time, "translationY" , -10f, 0f);

        AnimatorSet hideAndTransY = new AnimatorSet();
        hideAndTransY.playTogether(remainTimeAlpha, remainTimeTransY, usedTimeAlpha, usedTimeTransY);
        hideAndTransY.setInterpolator(new FastOutSlowInInterpolator());
        hideAndTransY.setDuration(500);
        hideAndTransY.start();

        ObjectAnimator progressScaleXUp = ObjectAnimator.ofFloat(user_used_time_progress_bar, "scaleX", 0f , 1.0f);
        ObjectAnimator progressScaleYUp = ObjectAnimator.ofFloat(user_used_time_progress_bar, "scaleY", 0f , 1.0f);
        AnimatorSet progressScaleUp = new AnimatorSet();
        progressScaleUp.playTogether(progressScaleXUp, progressScaleYUp);
        progressScaleUp.setDuration(600);
        progressScaleUp.setInterpolator(new AccelerateInterpolator());
        progressScaleUp.start();
    }

    private void usingTimeProgressComplete() {
        ValueAnimator progressCompleteRestartToFull = ValueAnimator.ofInt(0, user_used_time_progress_bar.getMax());
        progressCompleteRestartToFull.addUpdateListener(animation -> user_used_time_progress_bar.setProgress(Integer.parseInt(animation.getAnimatedValue().toString())));
        progressCompleteRestartToFull.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator progressScaleX = ObjectAnimator.ofFloat(user_used_time_progress_bar, "scaleX", 1f, 1.09f);
                ObjectAnimator progressScaleY = ObjectAnimator.ofFloat(user_used_time_progress_bar, "scaleY", 1f, 1.09f);
                AnimatorSet progressScale = new AnimatorSet();
                progressScale.playTogether(progressScaleX, progressScaleY);
                progressScale.setDuration(200);
                progressScale.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ObjectAnimator remainTimeAlpha = ObjectAnimator.ofFloat(remainTimeLayout, "alpha", 1f, 0f);
                        ObjectAnimator remainTimeTransY = ObjectAnimator.ofFloat(remainTimeLayout, "translationY", 0f, -10f);

                        ObjectAnimator usedTimeAlpha = ObjectAnimator.ofFloat(user_used_time, "alpha", 1f, 0f);
                        ObjectAnimator usedTimeTransY = ObjectAnimator.ofFloat(user_used_time, "translationY", 0f, -10f);

                        AnimatorSet hideAndTransY = new AnimatorSet();
                        hideAndTransY.playTogether(remainTimeAlpha, remainTimeTransY, usedTimeAlpha, usedTimeTransY);
                        hideAndTransY.setInterpolator(new FastOutSlowInInterpolator());
                        hideAndTransY.setDuration(500);
                        hideAndTransY.start();

                        ObjectAnimator progressScaleXDown = ObjectAnimator.ofFloat(user_used_time_progress_bar, "scaleX", 1.02f, 0f);
                        ObjectAnimator progressScaleYDown = ObjectAnimator.ofFloat(user_used_time_progress_bar, "scaleY", 1.02f, 0f);
                        AnimatorSet progressScaleDown = new AnimatorSet();
                        progressScaleDown.playTogether(progressScaleXDown, progressScaleYDown);
                        progressScaleDown.setDuration(600);
                        progressScaleDown.setInterpolator(new AccelerateInterpolator());
                        progressScaleDown.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                using_time_layout.setVisibility(View.INVISIBLE);
                                expandTimeRewardBtnAnim();
                            }
                        });
                        progressScaleDown.start();
                    }
                });
                progressScale.start();
            }
        });
        progressCompleteRestartToFull.setDuration(400);
        progressCompleteRestartToFull.setInterpolator(new AccelerateInterpolator());
        progressCompleteRestartToFull.start();
    }

    private void compressTimeRewardBtnAnim() {
        ObjectAnimator btnExpandX = ObjectAnimator.ofFloat(collect_using_time_reward_btn, "scaleX", 1f, 0f);
        ObjectAnimator btnExpandY = ObjectAnimator.ofFloat(collect_using_time_reward_btn, "scaleY", 1f, 0f);

        ObjectAnimator btnExpandAlpha = ObjectAnimator.ofFloat(collect_using_time_reward_btn, "alpha",  1f, 0.2f);

        AnimatorSet btnAnim = new AnimatorSet();
        btnAnim.playTogether(btnExpandX, btnExpandY, btnExpandAlpha);
        btnAnim.setInterpolator(new FastOutSlowInInterpolator());
        btnAnim.setDuration(300);
        btnAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                collect_using_time_reward_btn.setVisibility(View.GONE);
                usingTimeProgressReAppearAnim();
            }
        });
        btnAnim.start();
    }

    private void expandTimeRewardBtnAnim() {
        collect_using_time_reward_btn.setScaleX(0f);
        collect_using_time_reward_btn.setScaleY(0f);
        collect_using_time_reward_btn.setAlpha(0.2f);
        app_using_time_txt.setVisibility(View.INVISIBLE);
        collect_using_time_reward_btn.setVisibility(View.VISIBLE);
        ObjectAnimator btnExpandX = ObjectAnimator.ofFloat(collect_using_time_reward_btn, "scaleX", 0f, 1f);
        ObjectAnimator btnExpandY = ObjectAnimator.ofFloat(collect_using_time_reward_btn, "scaleY", 0f, 1f);

        ObjectAnimator btnExpandAlpha = ObjectAnimator.ofFloat(collect_using_time_reward_btn, "alpha", 0.2f, 1f);

        AnimatorSet btnAnim = new AnimatorSet();
        btnAnim.playTogether(btnExpandX, btnExpandY, btnExpandAlpha);
        btnAnim.setInterpolator(new FastOutSlowInInterpolator());
        btnAnim.setDuration(300);
        btnAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                usedTimeTiltAnim(2.7f);
            }
        });
        btnAnim.start();
    }

    int tiltTimes = 0;
    private void usedTimeTiltAnim(float tiltValue) {
        ObjectAnimator rotateEndUp = ObjectAnimator.ofFloat(collect_using_time_reward_btn, "rotation", collect_using_time_reward_btn.getRotation(), -tiltValue);
        rotateEndUp.setDuration(150);
        rotateEndUp.setInterpolator(new AccelerateInterpolator());
        rotateEndUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator rotateEndDown = ObjectAnimator.ofFloat(collect_using_time_reward_btn, "rotation", -tiltValue, tiltValue);
                rotateEndDown.setDuration(150);
                rotateEndDown.setInterpolator(new AccelerateInterpolator());
                rotateEndDown.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        tiltTimes++;
                        if (tiltTimes > 1) {
                            ObjectAnimator rotateEnds = ObjectAnimator.ofFloat(collect_using_time_reward_btn, "rotation", tiltValue, 0f);
                            rotateEnds.setDuration(250);
                            rotateEnds.setInterpolator(new AccelerateInterpolator());
                            rotateEnds.start();
                            tiltTimes = 0;
                        } else {
                            usedTimeTiltAnim(tiltValue / 1.5f);
                        }
                    }
                });
                rotateEndDown.start();
            }
        });
        rotateEndUp.start();
    }

//    ads
//    private void setBannerAd() {
//        if (ad_layout.getChildCount() < 1) {
//            bannerAd = new AdView(this);
//            AdRequest adRequest = new AdRequest.Builder().build();
//            bannerAd.setAdUnitId(Objects.requireNonNull(ad_values_map.get("ad_show_user_detail_banner_ad")));
//            bannerAd.setAdSize(AdSize.BANNER);
//            bannerAd.loadAd(adRequest);
//            ad_layout.addView(bannerAd);
//        }
//    }

//  Services
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        isShowUserDetailActServiceBinded = true;
        TimerIntentService.TimerBinder binder = (TimerIntentService.TimerBinder) service;
        time_service = binder.getService();
        if (time_service.getCurrentTime() >= TimerIntentService.CONSTANT_FINAL_TIME - 2000
                && !(currentUser.getmTime().get(currentUser.getmTime().size() - 1).isDailyRewardCollected())) {
            setUserUsingTimeRewardBtn();
            Log.d("MMK", "if");
        } else {
            setUserUsingTime();
            Log.d("MMK", "else");
        }
        if (isTimerServiceRunning && isPaused) {
            time_service.resumeTimer();
            Log.d("time_executed", "Calling from here Another");
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isShowUserDetailActServiceBinded = false;
        time_service = null;
    }

//    background image loading
//    async task for low devices
    class UserImageLoading extends AsyncTask<Void, Void, Bitmap> {

        boolean isForDashboard;
        ImageView imgView;

        UserImageLoading(boolean isForDashboard, ImageView imgView) {
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
                if (!(ShowUserDetailActivity.this.isDestroyed())) {
                    if (isForDashboard) {
                        imgView.setVisibility(View.VISIBLE);
                        user_dashboard_image_loading_progress.setVisibility(View.GONE);
                    }
                    if (imgView != null) {
                        Glide.with(ShowUserDetailActivity.this)
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