package com.sanket_satpute_20.playme.project.account.activity;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.ad_values_map;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isPaused;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isTimerServiceRunning;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.data.model.MTransactions;
import com.sanket_satpute_20.playme.project.account.data.model.MUser;
import com.sanket_satpute_20.playme.project.account.recycler.NotificationRecycler;
import com.sanket_satpute_20.playme.project.account.recycler.TransactionRecycler;
import com.sanket_satpute_20.playme.project.account.service.TimerIntentService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class NotificationInboxActivity extends AppCompatActivity implements ServiceConnection {

    TimerIntentService timer_service;

    FirebaseAuth f_auth;
    FirebaseFirestore f_db;

    AlertDialog alert_user_fetch_dialog;
    MaterialAlertDialogBuilder material_user_fetch_dialog;

    public static final String ACTION_INBOX = "ACTION_INBOX";
    public static final String ACTION_TRANSACTION = "ACTION_TRANSACTION";

    MaterialButton notification_btn, transaction_btn;
    SearchView search_view_notification;
    RecyclerView recycler_view;
    TextView not_available;

    ArrayList<MTransactions> transactions_array, notification_array;

    int blue_color = Color.parseColor("#5DC9E2");
    int low_opacity = Color.parseColor("#505DC9E2");
//    str
    String no_transaction_str = "No Transaction's", no_notification_str = "No Notification's";

    int on_btn_value;

//    ad's
    RelativeLayout ad_layout;
//    AdView bannerAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransparentStatusBar();
        setContentView(R.layout.activity_notification_inbox);
        initView();
        String action = getIntent().getAction();
        if (action != null) {
            if (action.equals(ACTION_INBOX)) {
                setInbox();
                on_btn_value = 0;
            } else if (action.equals(ACTION_TRANSACTION)) {
                setTransaction();
                on_btn_value = 1;
            } else {
                setInbox();
                on_btn_value = 0;
            }
        }
        onClick();
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
        Intent timerServiceIntent = new Intent(NotificationInboxActivity.this, TimerIntentService.class);
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
        notification_btn = findViewById(R.id.notification_inbox_btn);
        transaction_btn = findViewById(R.id.transaction_inbox_btn);
        search_view_notification = findViewById(R.id.search_for_inbox_massages);
        recycler_view = findViewById(R.id.recycler_view_notification_and_massages);
        not_available = findViewById(R.id.no_available_txt);
        ad_layout = findViewById(R.id.ad_layout);
    }

    private void onClick() {
        notification_btn.setOnClickListener(view -> {
            notification_btn.setBackgroundTintList(ColorStateList.valueOf(blue_color));
            notification_btn.setTextColor(Color.WHITE);
            transaction_btn.setBackgroundTintList(ColorStateList.valueOf(low_opacity));
            transaction_btn.setTextColor(blue_color);
            on_btn_value = 0;
            if (search_view_notification.getQuery().toString().trim().equalsIgnoreCase("") ||
                    search_view_notification.getQuery().toString().trim().length() <= 0) {
                setInbox();
            } else {
                checkNotificationQuery(search_view_notification.getQuery().toString());
            }
        });

        transaction_btn.setOnClickListener(view -> {
            notification_btn.setBackgroundTintList(ColorStateList.valueOf(low_opacity));
            notification_btn.setTextColor(blue_color);
            transaction_btn.setBackgroundTintList(ColorStateList.valueOf(blue_color));
            transaction_btn.setTextColor(Color.WHITE);
            on_btn_value = 1;
            if (search_view_notification.getQuery().toString().trim().equalsIgnoreCase("") ||
                    search_view_notification.getQuery().toString().trim().length() <= 0) {
                setTransaction();
            } else {
                checkTransactionQuery(search_view_notification.getQuery().toString());
            }
        });

        search_view_notification.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (on_btn_value == 0)
                    checkNotificationQuery(query);
                else
                    checkTransactionQuery(query);
                return true;
            }
        });
    }

    private void checkNotificationQuery(String query) {
        String cpy_query;
        ArrayList<MTransactions> queriedNotifications = new ArrayList<>();
        if (query != null) {
            if (notification_array == null)
                notification_array = fetchNotification();
            if (currentUser == null)
                fetchUser();
            if (notification_array.size() > 0) {
                cpy_query = query.trim().toLowerCase();
                if (cpy_query.equalsIgnoreCase("") || cpy_query.length() <= 0) {
                    if (notification_array == null)
                        notification_array = fetchNotification();
                    if (notification_array.size() > 0) {
                        recycler_view.setVisibility(View.VISIBLE);
                        not_available.setVisibility(View.GONE);
                        setNotificationRecycler(notification_array);
                    } else {
                        recycler_view.setVisibility(View.GONE);
                        not_available.setVisibility(View.VISIBLE);
                        not_available.setText(no_notification_str);
                    }
                } else {
                    for (MTransactions transaction : notification_array) {
                        String userNameEqual = currentUser.getFull_name().toLowerCase();
                        String statusEqual = transaction.getTransaction_status().toLowerCase();
                        String dateEqual = transaction.getMsgDate().toLowerCase();
                        String timeEqual = transaction.getMsgTime().toLowerCase();
                        if (cpy_query.contains(userNameEqual) || cpy_query.contains(statusEqual)
                                || cpy_query.contains(dateEqual) || cpy_query.contains(timeEqual)) {
                            queriedNotifications.add(transaction);
                        }
                    }
                    if (queriedNotifications.size() > 0) {
                        recycler_view.setVisibility(View.VISIBLE);
                        not_available.setVisibility(View.GONE);
                        setNotificationRecycler(queriedNotifications);
                    } else {
                        String no_matching_found_str = "No " + "'" + query.trim() + "'" + " Matching Found";
                        recycler_view.setVisibility(View.GONE);
                        not_available.setVisibility(View.VISIBLE);
                        not_available.setText(no_matching_found_str);
                    }
                }
            }
        } else {
            if (transactions_array == null)
                transactions_array = fetchTransaction();
            if (transactions_array.size() > 0) {
                recycler_view.setVisibility(View.VISIBLE);
                not_available.setVisibility(View.GONE);
                setTransactionRecycler(transactions_array);
            } else {
                recycler_view.setVisibility(View.GONE);
                not_available.setVisibility(View.VISIBLE);
                not_available.setText(no_transaction_str);
            }
        }
    }

    private void checkTransactionQuery(String query) {
        String cpy_query;
        ArrayList<MTransactions> queriedTransactions = new ArrayList<>();
        if (query != null) {
            if (transactions_array == null)
                transactions_array = fetchTransaction();
            if (transactions_array.size() > 0) {
                cpy_query = query.trim().toLowerCase();
                if (cpy_query.equalsIgnoreCase("") || cpy_query.length() <= 0) {
                    if (transactions_array == null)
                        transactions_array = fetchTransaction();
                    if (transactions_array.size() > 0) {
                        recycler_view.setVisibility(View.VISIBLE);
                        not_available.setVisibility(View.GONE);
                        setTransactionRecycler(transactions_array);
                    } else {
                        recycler_view.setVisibility(View.GONE);
                        not_available.setVisibility(View.VISIBLE);
                        not_available.setText(no_transaction_str);
                    }
                } else {
                    for (MTransactions transaction : transactions_array) {
                        String moneyEqual = String.valueOf(transaction.getTransaction_money()).toLowerCase();
                        String dateEqual = transaction.getTransaction_date().toLowerCase();
                        String timeEqual = transaction.getTransaction_time().toLowerCase();

                        if (cpy_query.contains(moneyEqual) || cpy_query.contains(dateEqual)
                                || cpy_query.contains(timeEqual)) {
                            queriedTransactions.add(transaction);
                        }
                    }
                    if (queriedTransactions.size() > 0) {
                        recycler_view.setVisibility(View.VISIBLE);
                        not_available.setVisibility(View.GONE);
                        setTransactionRecycler(queriedTransactions);
                    } else {
                        String no_matching_found_str = "No " + "'" + query.trim() + "'" + " Matching Found";
                        recycler_view.setVisibility(View.GONE);
                        not_available.setVisibility(View.VISIBLE);
                        not_available.setText(no_matching_found_str);
                    }
                }
            }
        } else {
            if (transactions_array == null)
                transactions_array = fetchTransaction();
            if (transactions_array.size() > 0) {
                recycler_view.setVisibility(View.VISIBLE);
                not_available.setVisibility(View.GONE);
                setTransactionRecycler(transactions_array);
            } else {
                recycler_view.setVisibility(View.GONE);
                not_available.setVisibility(View.VISIBLE);
                not_available.setText(no_transaction_str);
            }
        }
    }

    private ArrayList<MTransactions> fetchNotification() {
        ArrayList<MTransactions> transactionsMsg = currentUser.getmTransactions();
        ArrayList<MTransactions> copyTransactionMsg = new ArrayList<>();
        if (transactionsMsg != null) {
            for (MTransactions tranMSG : transactionsMsg) {
                if (tranMSG.getMsg() != null)
                    copyTransactionMsg.add(tranMSG);
            }
        }
        if (copyTransactionMsg.size() > 0)
            Collections.reverse(copyTransactionMsg);
        return copyTransactionMsg;
    }

    private ArrayList<MTransactions> fetchTransaction() {
        ArrayList<MTransactions> transactionList;
        if (currentUser == null)
            fetchUser();
        if (currentUser.getmTransactions() != null)
            transactionList = new ArrayList<>(currentUser.getmTransactions());
        else
            transactionList = new ArrayList<>();
        Collections.reverse(transactionList);
        return transactionList;
    }

    private void setInbox() {
        if (currentUser == null)
            fetchUser();
        notification_array = fetchNotification();
        if (currentUser.getIsNotifyMsgAvailable()) {
            currentUser.setIsNotifyMsgAvailable(false);
        }
        if (notification_array.size() > 0) {
            not_available.setVisibility(View.GONE);
            recycler_view.setVisibility(View.VISIBLE);
            setNotificationRecycler(notification_array);
        } else {
            not_available.setVisibility(View.VISIBLE);
            recycler_view.setVisibility(View.GONE);
            not_available.setText(no_notification_str);
        }
    }

    private void setTransaction() {
        if (currentUser == null)
            fetchUser();
        transactions_array = fetchTransaction();
        if (transactions_array.size() > 0) {
            not_available.setVisibility(View.GONE);
            recycler_view.setVisibility(View.VISIBLE);
            setTransactionRecycler(transactions_array);
        } else {
            not_available.setVisibility(View.VISIBLE);
            recycler_view.setVisibility(View.GONE);
            not_available.setText(no_transaction_str);
        }
    }

//    set Recycler
    private void setNotificationRecycler(ArrayList<MTransactions> list) {
        NotificationRecycler adapter = new NotificationRecycler(NotificationInboxActivity.this, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(NotificationInboxActivity.this);
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(layoutManager);
    }

    private void setTransactionRecycler(ArrayList<MTransactions> list) {
        TransactionRecycler adapter = new TransactionRecycler(NotificationInboxActivity.this, list, true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(NotificationInboxActivity.this);
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(layoutManager);
    }

//    fetch user
    private void fetchUser() {
        View fetching_view = LayoutInflater.from(NotificationInboxActivity.this).inflate(R.layout.user_fetch_layout, null);
        material_user_fetch_dialog = new MaterialAlertDialogBuilder(NotificationInboxActivity.this)
                .setView(fetching_view);
        alert_user_fetch_dialog = material_user_fetch_dialog.create();
        alert_user_fetch_dialog.show();
        Toast.makeText(this, "Wait Until User is Fetched", Toast.LENGTH_SHORT).show();

        if (f_auth == null || f_db == null)
            connectToFirebase();
        if (f_auth.getCurrentUser() != null) {
            f_db.collection("users").document(f_auth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        currentUser = documentSnapshot.toObject(MUser.class);
                        if (alert_user_fetch_dialog != null)
                            alert_user_fetch_dialog.dismiss();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(NotificationInboxActivity.this, "Please try again " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void connectToFirebase() {
        if (f_auth == null)
            f_auth = FirebaseAuth.getInstance();
        if (f_db == null)
            f_db = FirebaseFirestore.getInstance();
    }

//    transparent status bar
    private void setTransparentStatusBar() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(params);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    //    ads
//    private void loadAd() {
//        if (ad_layout.getChildCount() < 1) {
//            bannerAd = new AdView(this);
//            AdRequest adRequest = new AdRequest.Builder().build();
//            bannerAd.setAdUnitId(Objects.requireNonNull(ad_values_map.get("ad_notification_or_inbox_banner_ad")));
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