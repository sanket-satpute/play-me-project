package com.sanket_satpute_20.playme.project.account.activity;

import static com.sanket_satpute_20.playme.project.account.extra_stuffes.CommonMethodsUser.getFirebaseStoragePath;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.DAYS_OF_WEEK;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.RANDOM_STRINGS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.TRANSACTION_PAYMENT_STAGE_1;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.TRANSACTION_PAYMENT_STAGE_2;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.TRANSACTION_PAYMENT_STAGE_3;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.TRANSACTION_STATUS_PENDING;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.TRANSACTION_STATUS_SUCCESS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.USER_IMAGE_BITMAP_CONST;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isPaused;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isTimerServiceRunning;

import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.data.model.MTransactionTime;
import com.sanket_satpute_20.playme.project.account.data.model.MTransactions;
import com.sanket_satpute_20.playme.project.account.data.model.MUser;
import com.sanket_satpute_20.playme.project.account.data.model.PaymentUser;
import com.sanket_satpute_20.playme.project.account.data.model.UMoney;
import com.sanket_satpute_20.playme.project.account.extra_stuffes.CustomBarChartRender;
import com.sanket_satpute_20.playme.project.account.extra_stuffes.SaveUserImageCache;
import com.sanket_satpute_20.playme.project.account.recycler.TransactionRecycler;
import com.sanket_satpute_20.playme.project.account.service.TimerIntentService;

import java.sql.Date;
import java.sql.Time;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class WalletActivity extends AppCompatActivity implements ServiceConnection {

    TimerIntentService timer_service;

    int default_minimum_money = 25;
    int minimum_money;
    String rupee_string_str;

    ImageView user_profile_picture;
    TextView total_money, currency_type, no_transaction_avail;
    MaterialButton exchange_btn, withdraw_btn;
    RecyclerView r_t_recycler_view;
    BarChart barChart;
    CircularProgressIndicator user_dashboard_image_loading_progress;

    ArrayList<MTransactions> transactions_array;

    AlertDialog alert_user_fetch_dialog;
    MaterialAlertDialogBuilder material_user_fetch_dialog;

    FirebaseAuth f_auth;
    FirebaseFirestore f_db;
    FirebaseRemoteConfig f_config;

    ArrayList<String> xAxisLabel = new ArrayList<>();
    ArrayList<BarEntry> values = new ArrayList<>();
    List<GradientColor> gradientFills = new ArrayList<>();

    int default_color = Color.parseColor("#d9d9d9");

    public final String  minimumWithdrawalMoney = "minimumWithdrawalMoney";
    String rupee_str;

//    ad's
    RelativeLayout ad_layout;
//    AdView bannerAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        initView();
        onClick();
        doExtra();
//        if (bannerAd != null)
//            bannerAd.resume();
//        else
//            loadAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (bannerAd != null)
//            bannerAd.resume();
//        else
//            loadAd();
        Intent timerServiceIntent = new Intent(WalletActivity.this, TimerIntentService.class);
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

    private void initView() {
        user_profile_picture = findViewById(R.id.user_profile_picture);
        total_money = findViewById(R.id.total_money);
        currency_type = findViewById(R.id.currency_type_txt);
        exchange_btn = findViewById(R.id.exchange_btn);
        withdraw_btn = findViewById(R.id.withdraw_btn);
        r_t_recycler_view = findViewById(R.id.recent_transaction_recycler_view);
        no_transaction_avail = findViewById(R.id.no_transaction_txt);
        barChart = findViewById(R.id.graph_view);
        user_dashboard_image_loading_progress = findViewById(R.id.user_dashboard_image_loading_progress);
        ad_layout = findViewById(R.id.ad_layout);

        rupee_str = WalletActivity.this.getResources().getString(R.string.rupee);
    }

    private void onClick() {
        exchange_btn.setOnClickListener(view -> {
            Intent intent = new Intent(WalletActivity.this, RedeemMoneyActivity.class);
            startActivity(intent);
        });

        withdraw_btn.setOnClickListener(view -> withdrawMoneyDialog());
    }

    private void doExtra() {
        if (currentUser == null)
            fetchUser();
//        set user image

        NumberFormat moneyFormatter = NumberFormat.getInstance();
        String total_money_str = rupee_str + " " + moneyFormatter.format(currentUser.getTotalMoney());
        total_money.setText(total_money_str);
        currency_type.setText(getResources().getString(R.string.inr));
        setBarChart();
        setRecycle();

        UserImageLoading background_image_loading = new UserImageLoading(true, user_profile_picture);
        background_image_loading.execute();
    }

    private ArrayList<MTransactions> fetchTransactions() {
        ArrayList<MTransactions> list;
        if (currentUser.getmTransactions() != null)
            list = new ArrayList<>(currentUser.getmTransactions());
        else
            list = new ArrayList<>();
        Collections.reverse(list);
        return list;
    }

    private void fetchUser() {
        View fetching_view = LayoutInflater.from(WalletActivity.this).inflate(R.layout.user_fetch_layout, null);
        material_user_fetch_dialog = new MaterialAlertDialogBuilder(WalletActivity.this)
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
                        Toast.makeText(WalletActivity.this, "Please try again " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void setRecycle() {
        if (currentUser == null)
            getCurrentUser();
        transactions_array = fetchTransactions();

        if (transactions_array.size() > 0) {
            r_t_recycler_view.setVisibility(View.VISIBLE);
            no_transaction_avail.setVisibility(View.GONE);
            TransactionRecycler transaction_adapter = new TransactionRecycler(WalletActivity.this, transactions_array, false);
            LinearLayoutManager l_manager = new LinearLayoutManager(WalletActivity.this);
            r_t_recycler_view.setLayoutManager(l_manager);
            r_t_recycler_view.setAdapter(transaction_adapter);
        } else {
            r_t_recycler_view.setVisibility(View.GONE);
            no_transaction_avail.setVisibility(View.VISIBLE);
            String no_trans_avail_str = "No Transaction Available";
            no_transaction_avail.setText(no_trans_avail_str);
        }
    }

    private Bitmap setUserPicture() {
        USER_IMAGE_BITMAP_CONST = SaveUserImageCache.getImage(WalletActivity.this, "user_profile_image");
        if (USER_IMAGE_BITMAP_CONST != null) {
            return USER_IMAGE_BITMAP_CONST;
        } else if (currentUser.getProfilePicturePath() != null) {
            user_profile_picture.setAlpha(0.6f);
            StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(
                    getFirebaseStoragePath() +
                            currentUser.getProfilePicturePath());

            final long ONE_MEGABYTE = 1024 * 1024 * 5;

            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                USER_IMAGE_BITMAP_CONST = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                SaveUserImageCache.putImage(WalletActivity.this, "user_profile_image", USER_IMAGE_BITMAP_CONST);
            }).addOnFailureListener(exception -> {
                user_profile_picture.setAlpha(1f);
                Toast.makeText(WalletActivity.this, "Failed to load Image", Toast.LENGTH_SHORT).show();
            });
        } else {
            currentUser.setProfilePicturePath(null);
            user_profile_picture.setImageResource(R.drawable.orange_man_user_profile_picture);
        }
        return USER_IMAGE_BITMAP_CONST;
    }

    private void getCurrentUser() {
        if (currentUser == null) {
            if (f_auth == null || f_db == null)
                setFirebase();
            if (f_auth.getCurrentUser() != null) {
                f_db.collection("users").document(f_auth.getCurrentUser().getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            currentUser = documentSnapshot.toObject(MUser.class);
                            if (alert_user_fetch_dialog != null)
                                alert_user_fetch_dialog.dismiss();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(WalletActivity.this, "Please try again " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    }

    private void setFirebase() {
        if (f_auth == null)
            f_auth = FirebaseAuth.getInstance();
        if (f_db == null)
            f_db = FirebaseFirestore.getInstance();
        if (f_config == null)
            f_config = FirebaseRemoteConfig.getInstance();
    }

//    graph view
    private void setBarChart() {
        setBarChartColor();

        String today = String.valueOf(new Date(System.currentTimeMillis()));
        if (currentUser == null)
            fetchUser();
        ArrayList<UMoney> u_money_list = currentUser.getuMoney();
        if (u_money_list != null) {
            if (u_money_list.size() >= 3) {
                int size = Math.min(u_money_list.size(), 7);
                int max = 0;
                xAxisLabel.clear();
                values.clear();
                int start_color = Color.parseColor("#FFA500");
                int end_color = ContextCompat.getColor(this, R.color.red_orange);
                for (int i = 1; i <= size; i++) {
                    UMoney money = u_money_list.get(u_money_list.size() - i);
                    if (today.trim().equalsIgnoreCase(money.getMoneyDate().trim())) {
                        xAxisLabel.add("today");
                    } else {
                        try {
                            String date_str;
                            java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(money.getMoneyDate());
                            if (date != null) {
                                if (size > 5) {
                                    date_str = date.getDate() + " / " + (date.getMonth() + 1);
                                } else {
                                    date_str = date.getDate() + " / " + capitalizeString(DAYS_OF_WEEK[date.getDay()]);
                                }
                            }
                            else
                                date_str = "N/A";
                            xAxisLabel.add(date_str);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            xAxisLabel.add("N/A");
                        }
                    }
                    if (max < money.getCoins())
                        max = money.getCoins();
                    values.add(new BarEntry(size - i, money.getCoins()));
                    gradientFills.add(new GradientColor(start_color, end_color));
                }

                barChart.setVisibility(View.VISIBLE);
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

                barChart.getXAxis().setGranularity(1f);
                barChart.getXAxis().setGranularityEnabled(true);
                barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                barChart.getXAxis().setTextColor(default_color);

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
                l.setTextColor(default_color);
                barChart.getXAxis().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return xAxisLabel.get((xAxisLabel.size() - 1) - ((int) value));
                    }
                });

                setData();

                barChart.getLegend().setTextColor(default_color);
                barChart.getAxisLeft().setTextColor(default_color);
            } else {
                barChart.setVisibility(View.GONE);
            }
        } else {
            currentUser.setmTime(new ArrayList<>());
            setBarChart();
        }
    }

    private void setData() {
        BarDataSet set1;
        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, null);
            set1.setDrawIcons(false);
            set1.setValueTextColor(default_color);

            set1.setGradientColors(gradientFills);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(12f);
            data.setBarWidth(0.5f);

            barChart.setData(data);
            barChart.getBarData().setValueTextColor(default_color);
        }
    }

    private void setBarChartColor() {
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                default_color = Color.parseColor("#ffffff");
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                default_color = Color.parseColor("#000000");
                break;
        }
    }

    private String capitalizeString(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    AlertDialog alert_dialog;
    MaterialAlertDialogBuilder material_alert;
    View view;
    MaterialButton cancel_btn, withdraw_btn_of_withdraw_card;
    TextView withdraw_msg, withdraw_money_txt, loading_txt, title_txt;
    CircularProgressIndicator loading_progress;

    private void withdrawMoneyDialog() {

        view = LayoutInflater.from(WalletActivity.this).inflate(R.layout.withdraw_money_layout, null);
        material_alert = new MaterialAlertDialogBuilder(WalletActivity.this, R.style.WithdrawDialogStyle);
        material_alert.setView(view);

        cancel_btn = view.findViewById(R.id.cancel_btn);
        withdraw_btn_of_withdraw_card = view.findViewById(R.id.withdraw_btn);
        withdraw_msg = view.findViewById(R.id.msg_txt);
        withdraw_money_txt = view.findViewById(R.id.withdrawal_amount_txt);
        title_txt = view.findViewById(R.id.title_txt);
        loading_progress = view.findViewById(R.id.progress_bar_loading);
        loading_txt = view.findViewById(R.id.loading_txt);

        loading_progress.setVisibility(View.GONE);
        loading_txt.setVisibility(View.GONE);

        String withdrawal_true_msg = "Verify that the information you have provided is accurate; if it is not, go to your profile," +
                " click on options, amend your information, and then withdraw money to avoid having it transferred to someone else's account.";
        String withdrawal_money_str, not_enough_money = "Not Enough Money", withdraw = "Withdraw";

        if (f_config == null)
            setFirebase();

        minimum_money = (int) f_config.getLong(minimumWithdrawalMoney);
        if (minimum_money == 0)
            minimum_money = default_minimum_money;

        String withdrawal_false_msg = "make sure you have at least \u20B9 " + minimum_money + " to withdraw";
        if (currentUser.getTotalMoney() >= minimum_money) {     // you can withdraw
            withdraw_msg.setText(withdrawal_true_msg);
            withdraw_msg.setTextColor(Color.parseColor("#FFFF00"));
            withdrawal_money_str = "Withdraw \u20B9 " + currentUser.getTotalMoney();
            withdraw_money_txt.setText(withdrawal_money_str);
            withdraw_btn_of_withdraw_card.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffa500")));
            withdraw_btn_of_withdraw_card.setText(withdraw);
            withdraw_btn_of_withdraw_card.setClickable(true);
        } else {                                                // you can not withdraw
            withdraw_msg.setText(withdrawal_false_msg);
            withdraw_msg.setTextColor(Color.RED);
            withdrawal_money_str = "Can not Withdraw \u20B9 " + currentUser.getTotalMoney();
            withdraw_money_txt.setText(withdrawal_money_str);
            withdraw_btn_of_withdraw_card.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#a9a9a9")));
            withdraw_btn_of_withdraw_card.setText(not_enough_money);
            withdraw_btn_of_withdraw_card.setClickable(false);
        }

        alert_dialog = material_alert.create();
        alert_dialog.show();
        alert_dialog.setCancelable(false);

        cancel_btn.setOnClickListener(v -> alert_dialog.dismiss());
        withdraw_btn_of_withdraw_card.setOnClickListener(v -> {
            title_txt.setVisibility(View.GONE);
            withdraw_msg.setVisibility(View.GONE);
            withdraw_money_txt.setVisibility(View.GONE);
            loading_progress.setVisibility(View.VISIBLE);
            loading_txt.setVisibility(View.VISIBLE);
            withdraw_btn_of_withdraw_card.setClickable(false);
            cancel_btn.setClickable(false);
            withdraw_btn_of_withdraw_card.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#a9a9a9")));
            requestMoneyWithdrawal();
        });
    }

    PaymentUser paymentUserPending = null;

    private void requestMoneyWithdrawal() {

        if (minimum_money == 0)
            minimum_money = default_minimum_money;
        if (currentUser.getTotalMoney() >= minimum_money && f_auth.getCurrentUser() != null) {
            f_db.collection("payment").document("pending").get()
                    .addOnSuccessListener(documentSnapshot -> {
                        paymentUserPending = documentSnapshot.toObject(PaymentUser.class);
                        ArrayList<String> pendingIds;
                        if (paymentUserPending != null) {
                            pendingIds = paymentUserPending.getUserDocumentId();
                            if (!pendingIds.contains(f_auth.getCurrentUser().getUid()))
                                pendingIds.add(pendingIds.size(), f_auth.getCurrentUser().getUid());
                        } else {
                            paymentUserPending = new PaymentUser();
                            pendingIds = new ArrayList<>();
                            pendingIds.add(0, f_auth.getCurrentUser().getUid());
                        }
                        paymentUserPending.setUserDocumentId(pendingIds);
                        savePayment(paymentUserPending, currentUser.getTotalMoney());
                    }).addOnFailureListener(e -> Toast.makeText(WalletActivity.this, "Withdrawal Fail by : "+e.getMessage(), Toast.LENGTH_SHORT).show());
//            after success
        } else {
            alert_dialog.dismiss();
            Toast.makeText(this, "Not Enough Money", Toast.LENGTH_SHORT).show();
        }
    }

    ArrayList<MTransactionTime> transactions_times;
    ArrayList<MTransactions> transactions;

    private void savePayment(PaymentUser paymentUser, long withdrawalMoney) {
        f_db.collection("payment").document("pending").set(paymentUser)
                .addOnSuccessListener(unused -> {
                    currentUser.setWithdrawingMoney(withdrawalMoney);
                    currentUser.setTotalMoney(currentUser.getTotalMoney() - withdrawalMoney);
                     transactions = currentUser.getmTransactions();
                    if (transactions == null)
                        transactions = new ArrayList<>();
//                    create transactions
//                    id
                    int min = 0;
                    int max = RANDOM_STRINGS.length - 1;
                    int position = (int) (min + (Math.random() * ((max - min) + 1)));
                    StringBuilder transactionID = new StringBuilder("#" + position);
                    for (int i = 0; i < 10; i++) {
                        transactionID.append(RANDOM_STRINGS[(int) (min + (long) (Math.random() * ((max - min) + 1)))]);
                    }

                    String payment_execution_date = String.valueOf(new Date(System.currentTimeMillis()));
                    String payment_execution_time = String.valueOf(new Time(System.currentTimeMillis()));

                    transactions_times = new ArrayList<>();
                    transactions_times.add(0, new MTransactionTime(TRANSACTION_PAYMENT_STAGE_1, payment_execution_time,
                            payment_execution_date, TRANSACTION_STATUS_SUCCESS));
                    transactions_times.add(1, new MTransactionTime(TRANSACTION_PAYMENT_STAGE_2, null,
                            null, TRANSACTION_STATUS_PENDING));
                    transactions_times.add(2, new MTransactionTime(TRANSACTION_PAYMENT_STAGE_3, null,
                            null, TRANSACTION_STATUS_PENDING));


//                    date, time , money, status, m_transaction_time_array
                    MTransactions mTransaction = new MTransactions(transactionID.toString(), payment_execution_date, payment_execution_time,
                            withdrawalMoney, TRANSACTION_STATUS_PENDING, transactions_times, null, null, null);
                    transactions.add(transactions.size(), mTransaction);

                    f_db.collection("users").document(Objects.requireNonNull(f_auth.getCurrentUser()).getUid()).set(currentUser)
                            .addOnSuccessListener(unused1 -> {
                                Toast.makeText(this, "Request Sent For Withdraw", Toast.LENGTH_SHORT).show();
                                if (alert_dialog != null)
                                    alert_dialog.dismiss();
                                reduceBalanceAnima((int) withdrawalMoney);
                                setRecycle();
                            }).addOnFailureListener(e -> {
                                currentUser.setTotalMoney(withdrawalMoney);
                                currentUser.setWithdrawingMoney(currentUser.getWithdrawingMoney() - withdrawalMoney);
                                paymentUser.getUserDocumentId().remove(paymentUser.getUserDocumentId().size() - 1);
                                if (payment_execution_date.equalsIgnoreCase(transactions.get(transactions.size() - 1).getTransaction_date())) {
                                    transactions.remove(transactions.size() - 1);
                                }
                                f_db.collection("payment").document("pending").set(paymentUser);
                                Toast.makeText(WalletActivity.this, "Withdrawal Failed by : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                if (alert_dialog != null)
                                    alert_dialog.dismiss();
                            }).addOnCanceledListener(() -> {
                                currentUser.setTotalMoney(withdrawalMoney);
                                currentUser.setWithdrawingMoney(currentUser.getWithdrawingMoney() - withdrawalMoney);
                                paymentUser.getUserDocumentId().remove(paymentUser.getUserDocumentId().size() - 1);
                                if (payment_execution_date.equalsIgnoreCase(transactions.get(transactions.size() - 1).getTransaction_date())) {
                                    transactions.remove(transactions.size() - 1);
                                }
                                f_db.collection("payment").document("pending").set(paymentUser);
                                Toast.makeText(WalletActivity.this, "Withdrawal Cancel", Toast.LENGTH_SHORT).show();
                                if (alert_dialog != null)
                                    alert_dialog.dismiss();
                            });
                }).addOnFailureListener(e -> {
                    Toast.makeText(WalletActivity.this, "Withdrawal Failed by : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    if (alert_dialog != null)
                        alert_dialog.dismiss();
                }).addOnCanceledListener(() -> {
                    Toast.makeText(WalletActivity.this, "Withdrawal Cancel", Toast.LENGTH_SHORT).show();
                    if (alert_dialog != null)
                        alert_dialog.dismiss();
                });
    }

    private void reduceBalanceAnima(int reducedMoney) {
        if (currentUser == null)
            fetchUser();
        ValueAnimator anim = ValueAnimator.ofInt(reducedMoney, (int) currentUser.getTotalMoney());
        anim.addUpdateListener(animation -> {
            rupee_string_str = rupee_str + " " + animation.getAnimatedValue().toString();
            total_money.setText(rupee_string_str);
        });
        anim.start();
    }

    //    ads
//    private void loadAd() {
//        if (ad_layout.getChildCount() < 1) {
//            bannerAd = new AdView(this);
//            AdRequest adRequest = new AdRequest.Builder().build();
//            bannerAd.setAdUnitId(Objects.requireNonNull(ad_values_map.get("ad_wallet_banner_ad")));
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

    //    user dashboard image loading in background
//    asynctask
    class UserImageLoading extends AsyncTask<Void, Void, Bitmap> {

        boolean isForDashboard;
        ImageView imgView;

        UserImageLoading(boolean isForDashboard, ImageView imgView) {
            this.isForDashboard = isForDashboard;
            this.imgView = imgView;
            if (isForDashboard) {
                user_dashboard_image_loading_progress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return (USER_IMAGE_BITMAP_CONST == null) ? setUserPicture() : USER_IMAGE_BITMAP_CONST;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            try {
                if (!(WalletActivity.this.isDestroyed())) {
                    if (isForDashboard) {
                        user_dashboard_image_loading_progress.setVisibility(View.GONE);
                    }
                    if (imgView != null) {
                        Glide.with(WalletActivity.this)
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