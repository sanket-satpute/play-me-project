package com.sanket_satpute_20.playme.project.account.activity;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.DAILY_AD_WATCH_TIME_LIMIT_STR;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.RANDOM_NUMBERS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.RANDOM_STRINGS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.data.model.MTime;
import com.sanket_satpute_20.playme.project.account.data.model.MTransactions;
import com.sanket_satpute_20.playme.project.account.data.model.MUser;
import com.sanket_satpute_20.playme.project.account.data.model.UMoney;
import com.sanket_satpute_20.playme.project.account.extra_stuffes.UserAccountCreatedSendMail;
import com.sanket_satpute_20.playme.project.account.fragments.UserCreationFragment_1;
import com.sanket_satpute_20.playme.project.account.fragments.UserCreationFragment_2;
import com.sanket_satpute_20.playme.project.account.service.TimerIntentService;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class CreateAccountActivity extends AppCompatActivity implements ServiceConnection {

    TimerIntentService timer_service;

    CountDownTimer count_down_timer;

    public static final String USER_USER_NAME = "USER_USER_NAME";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_PASSWORD = "USER_PASSWORD";
    public static final String USER_FIRST_NAME = "USER_FIRST_NAME";
    public static final String USER_LAST_NAME = "USER_LAST_NAME";
    public static final String USER_PHONE = "USER_PHONE";
    public static final String USER_UPI_ID = "USER_UPI_ID";
    private static final String USER_PROFILE_PICTURE_URL = "USER_PROFILE_PICTURE_URL";


    public static final String USER_CREATION_FIRST_PAGE_DONE = "user_creation_first.page.done";
    public static final String USER_CREATION_SECOND_PAGE_DONE = "user_creation_second.page.done";
    public static final String RETURN_USER_CREATION_FIRST_PAGE_DONE = "return_user_creation_first.page.done";
    public static final String RETURN_USER_CREATION_SECOND_PAGE_DONE = "return_user_creation_second.page.done";

    ProgressBar sign_up_btn_progress;
    RelativeLayout sign_up_layout;
    MaterialButton create_account;
    RelativeLayout sign_in_layout;

    FirebaseFirestore f_db = null;
    FirebaseAuth f_auth = null;
    FirebaseRemoteConfig f_config;

    MUser user = null;
    String temp_pass, temp_email, temp_userName;

    boolean isUserCreated = false, isUserSaved = false;

    private final BroadcastReceiver broadcast_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(RETURN_USER_CREATION_FIRST_PAGE_DONE)) {
                    count_down_timer = new CountDownTimer(60000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (millisUntilFinished == 10000) {
                                Toast.makeText(CreateAccountActivity.this, "Hold on connection is very poor", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFinish() {
                            sign_up_btn_progress.setVisibility(View.GONE);
                            Toast.makeText(CreateAccountActivity.this, "Please Try Again Session Time Outed", Toast.LENGTH_SHORT).show();
                        }
                    }.start();
                    sign_up_btn_progress.setVisibility(View.VISIBLE);
                    createAuthenticationWithUser(intent);
                } else if (intent.getAction().equals(RETURN_USER_CREATION_SECOND_PAGE_DONE)) {
                    count_down_timer = new CountDownTimer(60000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (millisUntilFinished == 10000) {
                                Toast.makeText(CreateAccountActivity.this, "Hold on connection is very poor", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFinish() {
                            sign_up_btn_progress.setVisibility(View.GONE);
                            Toast.makeText(CreateAccountActivity.this, "Please Try Again Session Time Outed", Toast.LENGTH_SHORT).show();
                        }
                    }.start();
                    sign_up_btn_progress.setVisibility(View.VISIBLE);
                    createUserWithData(intent);
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.sign_up_detail_relative_layout);
        if (!(fragment instanceof UserCreationFragment_1))
            finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isUserCreated && !isUserSaved) {
            if (f_auth == null || f_db == null)
                connectToFirebase();
            FirebaseUser f_user = f_auth.getCurrentUser();
            if (f_user != null) {
                f_user.delete();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        initViews();
        FragmentTransaction f_transaction = getSupportFragmentManager().beginTransaction();
        f_transaction.replace(R.id.sign_up_detail_relative_layout, new UserCreationFragment_1());
        f_transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        f_transaction.commit();
        onClick();

        connectToFirebase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RETURN_USER_CREATION_FIRST_PAGE_DONE);
        filter.addAction(RETURN_USER_CREATION_SECOND_PAGE_DONE);
        LocalBroadcastManager.getInstance(CreateAccountActivity.this).registerReceiver(broadcast_receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(CreateAccountActivity.this).unregisterReceiver(broadcast_receiver);
    }

    private void initViews() {
        sign_up_layout = findViewById(R.id.sign_up_detail_relative_layout);
        create_account = findViewById(R.id.create_account_btn);
        sign_in_layout = findViewById(R.id.sign_in_layout);
        sign_up_btn_progress = findViewById(R.id.sign_up_btn_progress);
    }

    private void onClick() {

        create_account.setOnClickListener(view -> {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.sign_up_detail_relative_layout);
            Intent broadcast_intent = new Intent();
            if (fragment instanceof UserCreationFragment_1) {
                broadcast_intent.setAction(USER_CREATION_FIRST_PAGE_DONE);
                LocalBroadcastManager.getInstance(CreateAccountActivity.this).sendBroadcast(broadcast_intent);
            } else {
                broadcast_intent.setAction(USER_CREATION_SECOND_PAGE_DONE);
                LocalBroadcastManager.getInstance(CreateAccountActivity.this).sendBroadcast(broadcast_intent);
            }
        });

        sign_in_layout.setOnClickListener(view -> {
            Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void sendEmailVerificationLink() {
        String user_mail = null;
        if (f_auth == null)
            connectToFirebase();
        FirebaseUser user = f_auth.getCurrentUser();
        if (user != null)
            user_mail = user.getEmail();
        UserAccountCreatedSendMail userAccountGreet = new UserAccountCreatedSendMail((user_mail == null) ? currentUser.getEmail_id() : user_mail, currentUser.getFull_name());
        userAccountGreet.execute();
    }

    private void createAuthenticationWithUser(Intent intent) {
        String email, password, username;
        email = intent.getStringExtra(USER_EMAIL);
        password = intent.getStringExtra(USER_PASSWORD);
        username = intent.getStringExtra(USER_USER_NAME);
        f_auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    isUserCreated = true;
                    count_down_timer.cancel();
                    temp_userName = username;
                    temp_email = email;
                    temp_pass = password;
                    FragmentTransaction f_transaction = getSupportFragmentManager().beginTransaction();
                    f_transaction.replace(R.id.sign_up_detail_relative_layout, new UserCreationFragment_2());
                    f_transaction.addToBackStack("SecondFragmentUserCreate");
                    f_transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    f_transaction.commit();
                    sign_up_btn_progress.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    isUserCreated = false;
                    count_down_timer.cancel();

                    sign_up_btn_progress.setVisibility(View.GONE);
                    Fragment current_fragment = getSupportFragmentManager().findFragmentById(R.id.sign_up_detail_relative_layout);
                    if (!(current_fragment instanceof UserCreationFragment_1)) {
                        FragmentManager f_manager = getSupportFragmentManager();
                        if (!f_manager.isDestroyed()) {
                            FragmentTransaction f_transaction = f_manager.beginTransaction();
                            f_transaction.replace(R.id.sign_up_detail_relative_layout, new UserCreationFragment_1());
                            f_transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                            f_transaction.commit();
                            Toast.makeText(CreateAccountActivity.this, "Failed Due to : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (e.getMessage() != null) {
                            if (e.getMessage().equals("The email address is already in use by another account.")) {
                                AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                                if (f_auth.getCurrentUser() != null) {
                                    f_auth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            isUserCreated = true;
                                            count_down_timer.cancel();
                                            temp_userName = username;
                                            temp_email = email;
                                            temp_pass = password;
                                            FragmentManager f_manager = getSupportFragmentManager();
                                            if (!f_manager.isDestroyed()) {
                                                FragmentTransaction f_transaction = f_manager.beginTransaction();
                                                f_transaction.replace(R.id.sign_up_detail_relative_layout, new UserCreationFragment_2());
                                                f_transaction.addToBackStack("SecondFragmentUserCreate");
                                                f_transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                                f_transaction.commit();
                                                sign_up_btn_progress.setVisibility(View.GONE);
                                            }
                                        } else {
                                            Toast.makeText(CreateAccountActivity.this, "Some Error. Exit, And Try Again", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                            else if (e.getMessage().contains("email") || e.getMessage().contains("Email")) {
                                Intent error_broad_intent = new Intent();
                                error_broad_intent.setAction("ERROR_MSG_EMAIL_ACTION");
                                error_broad_intent.putExtra("ERROR_MSG_EMAIL", e.getMessage());
                                LocalBroadcastManager.getInstance(CreateAccountActivity.this).sendBroadcast(error_broad_intent);
                            } else {
                                Toast.makeText(CreateAccountActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .addOnCanceledListener(() -> {
                    isUserCreated = false;
                    count_down_timer.cancel();
                    Log.d("mcom", "Cancel Sign in Request ");
                });
    }

    private void createUserWithData(Intent intent) {
        if (f_auth == null || f_db == null)
            connectToFirebase();
        if (f_auth != null) {

            int daily_ad_watch_time_limit;
            String f_name , l_name, full_name, user_id, user_name, email_id, password, acc_creation_date, account_creation_time, upiID, profilePicturePath;
            long user_count_no, phone_no, totalMoney, totalCoins, totalEarnedMoneyFromPlayMe;
            byte[] picture;
            ArrayList<UMoney> uMoney;
            ArrayList<MTransactions> transactions;
            ArrayList<MTime> user_using_time;

            acc_creation_date = String.valueOf(new Date(System.currentTimeMillis()));

            Time time = new Time(Time.getCurrentTimezone());
            time.setToNow();

            account_creation_time = time.format("%k:%M:%S");

            if (f_config == null)
                connectToFirebase();
            daily_ad_watch_time_limit = (f_config.getLong(DAILY_AD_WATCH_TIME_LIMIT_STR) < 1) ? 20 : (int) f_config.getLong(DAILY_AD_WATCH_TIME_LIMIT_STR);

            f_name = intent.getStringExtra(USER_FIRST_NAME);
            l_name = intent.getStringExtra(USER_LAST_NAME);
            phone_no = intent.getLongExtra(USER_PHONE, 0L);
            upiID = intent.getStringExtra(USER_UPI_ID);
            profilePicturePath = intent.getStringExtra(USER_PROFILE_PICTURE_URL);//not given
            full_name = f_name + " " + l_name;
            user_count_no = 0;
            user_id = createUserId();
            totalMoney = 0;
            totalCoins = 0;
            totalEarnedMoneyFromPlayMe = 0;
            user_name = temp_userName;
            email_id = temp_email;
            password = temp_pass;
            picture = null;
            uMoney = new ArrayList<>();
            transactions = new ArrayList<>();
            user_using_time = new ArrayList<>();
            user_using_time.add(new MTime(createDailyTimeId(), 0, 0, TimerIntentService.CONSTANT_FINAL_TIME, "Pending",
                    acc_creation_date, false, daily_ad_watch_time_limit));

            user = new MUser(f_name, l_name, full_name,user_count_no, user_id, user_name, email_id, phone_no, picture, password, uMoney, transactions,
                    user_using_time, acc_creation_date, account_creation_time, upiID, totalMoney, totalCoins, totalEarnedMoneyFromPlayMe, profilePicturePath,
                    true, false, 0, false, null);

            f_db.collection("users")
                    .document(Objects.requireNonNull(f_auth.getCurrentUser()).getUid())
                    .set(user)
                    .addOnSuccessListener(documentReference -> {
                        // start timer service
                        isUserSaved = true;
                        currentUser = user;
                        sendEmailVerificationLink();
                        Intent timer_intent = new Intent(CreateAccountActivity.this, TimerIntentService.class);
                        bindService(timer_intent, this, BIND_AUTO_CREATE);
                        Intent i = new Intent(CreateAccountActivity.this, ShowUserDetailActivity.class);
                        startActivity(i);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        isUserSaved = false;
                        if (f_auth == null)
                            connectToFirebase();
                        FirebaseUser f_user = f_auth.getCurrentUser();
                        if (f_user != null) {
                            boolean isFailed = f_user.delete()
                                    .isComplete();
                            Log.d("PlayMe_AppFire", "After Failed User Deleted : " + isFailed);
                        }
                    })
                    .addOnCanceledListener(() -> {
                        isUserSaved = false;
                        if (f_auth == null)
                            connectToFirebase();
                        FirebaseUser f_user = f_auth.getCurrentUser();
                        if (f_user != null) {
                            boolean isFailed = f_user.delete()
                                    .isComplete();
                            Log.d("PlayMe_AppFire", "After Failed User Deleted : " + isFailed);
                        }
                    });
        } else {
            FragmentManager f_manager = getSupportFragmentManager();
            if (!f_manager.isDestroyed()) {
                FragmentTransaction f_transaction = getSupportFragmentManager().beginTransaction();
                f_transaction.replace(R.id.sign_up_detail_relative_layout, new UserCreationFragment_1());
                f_transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                f_transaction.commit();
                Toast.makeText(this, "Not Able To Create Account Try After Some Time", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String createUserId() {
        return UUID.randomUUID().toString();
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

    private void connectToFirebase() {
        if (f_db == null)
            f_db = FirebaseFirestore.getInstance();
        if (f_auth == null)
            f_auth = FirebaseAuth.getInstance();
        if (f_config == null)
            f_config = FirebaseRemoteConfig.getInstance();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        TimerIntentService.TimerBinder binder = (TimerIntentService.TimerBinder) service;
        timer_service = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        timer_service = null;
    }
}