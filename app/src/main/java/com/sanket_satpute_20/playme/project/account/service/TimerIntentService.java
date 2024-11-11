package com.sanket_satpute_20.playme.project.account.service;

import static com.sanket_satpute_20.playme.project.account.extra_stuffes.CommonMethodsUser.dailyAdWatchTimeLimit;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.FINAL_TIME;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.RANDOM_NUMBERS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.RANDOM_STRINGS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.TRANSACTION_STATUS_FAILED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.TRANSACTION_STATUS_SUCCESS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isCanceled;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isPaused;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isTimerServiceRunning;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.timer;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.used_time;

import android.Manifest;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.activity.NotificationInboxActivity;
import com.sanket_satpute_20.playme.project.account.data.model.MTime;
import com.sanket_satpute_20.playme.project.account.data.model.MTransactions;
import com.sanket_satpute_20.playme.project.account.data.model.MUser;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class TimerIntentService extends IntentService {

    private static final String ACTION_UPDATE_PAYMENT_COMPLETE = "action_update_payment_COMPLETED";
    public final TimerBinder binder = new TimerBinder();

    public static final String FINAL_TIME_EXTRAS = "FINAL_TIME_EXTRAS";

    EventListener<DocumentSnapshot> eventListener;
    DocumentReference f_db;

    public static long CURRENT_TIME, REMAINING_TIME = 10800000;
    public static final int CONSTANT_FINAL_TIME = 10800000;
    private final int TICK_TIME = 1000;//real 60000;

    FirebaseFirestore f_store;
    FirebaseAuth f_auth;
    FirebaseUser f_user;
    FirebaseRemoteConfig f_config;

    MUser updatedPaymentTransactionUser;

    public TimerIntentService() {
        super("TimerIntentService");
        if (f_store == null || f_user == null)
            connectToFirebase();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (f_user != null) {
                eventListener = (value, error) -> {
                    if (value != null && value.exists()) {
                        updatedPaymentTransactionUser = value.toObject(MUser.class);
                        if (updatedPaymentTransactionUser != null) {
                            Log.d("Happen", "User is not Null");
                            updateUserPaymentsTransaction(updatedPaymentTransactionUser);
                        } else {
                            Log.d("Happen", "User is Null");
//                            not set
                            failedToUpdateUserPaymentsTransaction();
                        }
                    } else {
                        failedToUpdateUserPaymentsTransaction();
                        Log.d("Happen", "value is Null or not exist");
                    }
                    if (error != null) {
                        Log.d("Happen", "Error Formed : " + error.getMessage());
                        failedToUpdateUserPaymentsTransaction();
                    }
                };
                f_db = f_store.collection("users").document(f_user.getUid());
                f_db.addSnapshotListener(eventListener);
            } else {
                Log.d("Happen", "Main User is Null");
//            send a massage to me
                failedToUpdateUserPaymentsTransaction();
            }
        }, 0);

    }

    int m;

    private void failedToUpdateUserPaymentsTransaction() {
        Log.d("happen", "failed " + m++);

    }

    private void connectToFirebase() {
        if (f_store == null)
            f_store = FirebaseFirestore.getInstance();
        if (f_auth == null)
            f_auth = FirebaseAuth.getInstance();
        if (f_user == null)
            f_user = f_auth.getCurrentUser();
        if (f_config == null)
            f_config = FirebaseRemoteConfig.getInstance();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (currentUser != null) {

                if (f_store == null || f_auth == null || f_user == null || f_config == null)
                    connectToFirebase();
                FINAL_TIME = intent.getLongExtra(FINAL_TIME_EXTRAS, CONSTANT_FINAL_TIME);
                if (FINAL_TIME < 0)
                    FINAL_TIME = CONSTANT_FINAL_TIME;
                initTimer(FINAL_TIME);
                Date today_date = new Date(System.currentTimeMillis());

                ArrayList<MTime> time_list = currentUser.getmTime();
                if (time_list == null)
                    time_list = new ArrayList<>();
                if (time_list.size() < 1) {
                    MTime newTime = new MTime(createDailyTimeId(), 0, 0, CONSTANT_FINAL_TIME,
                            "Pending", String.valueOf(today_date), false, dailyAdWatchTimeLimit());
                    time_list.add(0, newTime);
                }
                String recent_date = time_list.get(currentUser.getmTime().size() - 1).getDaily_limit_date();
                if (!(recent_date.trim().equalsIgnoreCase(today_date.toString().trim()))) {
                    int daily_ad_watch_time_limit = dailyAdWatchTimeLimit();
                    MTime currentMTime = new MTime(createDailyTimeId(), 0, 0, TimerIntentService.CONSTANT_FINAL_TIME,
                            "Pending", String.valueOf(new Date(System.currentTimeMillis())), false, daily_ad_watch_time_limit);
                    currentUser.getmTime().add(currentUser.getmTime().size(), currentMTime);
                }
                used_time = currentUser.getmTime().get(currentUser.getmTime().size() - 1).getUsed_time_app();

                start();
            }
        } else {
            Log.d("PlayMe_App", "Intent is null");
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

    private synchronized void initTimer(long final_time) {
        Log.d("time_executed", final_time + " ~!!:!!~ " + " First Time");
        isCanceled = false;
        isPaused = false;
        isTimerServiceRunning = true;

        timer = new CountDownTimer(final_time, TICK_TIME) {
            @Override
            public void onTick(long l) {
                REMAINING_TIME = l;
                CURRENT_TIME = CONSTANT_FINAL_TIME - REMAINING_TIME;
                Log.d("time_executed", CURRENT_TIME + " --:-- " + used_time + " --:-- " + (final_time + used_time));
//                Log.d("time_executed", "Running : Final - " + final_time + " Current " + CURRENT_TIME);
                Log.d("time_executed", "Running : Min - " + getMins(REMAINING_TIME) + " : " + getSecs(REMAINING_TIME));
            }

            @Override
            public void onFinish() {
                Log.d("time_executed", "Finish");
                timeIsPaused();
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timeIsPaused();
//        isTimerServiceRunning = false;
        Log.d("time_executed: ", "Saved data Service");
    }

    public void timeIsPaused() {
        if (currentUser != null) {
            MTime nextDayTime;
            ArrayList<MTime> time_list = currentUser.getmTime();
            if (time_list == null)
                time_list = new ArrayList<>();
            if (time_list.size() < 1)
                nextDayTime = new MTime();
            else
                nextDayTime = time_list.get(currentUser.getmTime().size() - 1);
            nextDayTime.setTime_date_count(currentUser.getmTime().size() - 1);
            nextDayTime.setUsed_time_app(CURRENT_TIME);
            nextDayTime.setDaily_limit(REMAINING_TIME);
            nextDayTime.setDaily_limit_status((REMAINING_TIME <= TICK_TIME) ? "Success" : "Incomplete");

            if (time_list.size() < 1)
                time_list.set(currentUser.getmTime().size(), nextDayTime);
            else
                time_list.set(currentUser.getmTime().size() - 1, nextDayTime);
            currentUser.setmTime(time_list);
            currentUser.setActive(false);
            if (f_auth.getCurrentUser() != null) {
                f_store.collection("users").document(f_auth.getCurrentUser().getUid())
                        .set(currentUser);
            }
        }
        isCanceled = true;
    }

    public void start() {
        timer.start();
    }

    public void pauseTimer() {
        if (timer != null) {
            isPaused = true;
            timer.cancel();
            Log.d("time_executed: ", "Time Paused " + " Used Time is " + used_time);
        }
    }

    public void resumeTimer() {
        if (timer != null)
            Log.d("time_executed: ", "Time Resumed");
        timer = null;
        isPaused = false;
        initTimer(REMAINING_TIME);
        start();
    }

    public long getCurrentTime() {
        return CURRENT_TIME;
    }

    //    service related
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class TimerBinder extends Binder {
        public TimerIntentService getService() {
            return TimerIntentService.this;
        }
    }

    private long getMins(long timeStamp) {
        return (timeStamp / 1000) / 60;
    }

    private long getSecs(long timeStamp) {
        return (timeStamp / 1000) % 60;
    }


    //    Notification
    private void updateUserPaymentsTransaction(MUser updatedUser) {
        ArrayList<MTransactions> transactionList = updatedUser.getmTransactions();
        if (currentUser != null) {
            if (currentUser.getmTransactions() != null && transactionList != null && updatedUser.getIsNotifyMsgAvailable()) {
                notifyPaymentIsComplete(currentUser.getmTransactions(), transactionList);
                currentUser.setIsNotifyMsgAvailable(updatedUser.getIsNotifyMsgAvailable());
                currentUser.setmTransactions(transactionList);
                currentUser.setTotalMoney(updatedUser.getTotalMoney());
                Intent updatedPaymentTransactionUserBroadcast = new Intent();
                updatedPaymentTransactionUserBroadcast.setAction(ACTION_UPDATE_PAYMENT_COMPLETE);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(updatedPaymentTransactionUserBroadcast);
//        if payment really received then give a notification with a massage like your money transferred earn more user more grow much more money
                Log.d("Happen", "Yes Received or failed");
            }
        }
    }

    private void notifyPaymentIsComplete(ArrayList<MTransactions> oldTransactionList, ArrayList<MTransactions> newTransactionList) {
        for (int i = (newTransactionList.size() - 1); i >= 0; i--) {
            boolean isNotifyApplicable = newTransactionList.get(i).getTransaction_status()
                    .equalsIgnoreCase(oldTransactionList.get(i).getTransaction_status());
            if (!isNotifyApplicable) { // notify
                if (newTransactionList.get(i).getTransaction_status().equalsIgnoreCase(TRANSACTION_STATUS_SUCCESS)
                        || newTransactionList.get(i).getTransaction_status().equalsIgnoreCase(TRANSACTION_STATUS_FAILED)) {
                    notifyTransactionMsg(newTransactionList.get(i));
                    Log.d("Happen", "Notification is Applicable : " + newTransactionList.get(i).getTransaction_money());
                }
            } else {    // break
                Log.d("Happen", "DO BREAK Notification is Not Applicable : " + newTransactionList.get(i).getTransaction_money());
                break;
            }
        }
    }

    Bitmap notifyImage;

    private void notifyTransactionMsg(MTransactions transaction) {
//        encourage for create more money and use app more time with all functionality
        String notifyTitle, notifyMsg, summery_txt;

//        Congratulations! You've unlocked new opportunities to earn money today!

        notifyTitle = "Transaction " + transaction.getTransaction_status();
        notifyMsg = transaction.getMsg();

        getNotificationBigPictures(transaction.getTransaction_status());
        if (transaction.getTransaction_status().equalsIgnoreCase(TRANSACTION_STATUS_FAILED)) {
            summery_txt = "We apologize, but the transaction of " + transaction.getTransaction_money() + " rupees has failed due to technical issues. " +
                    "The amount has been returned to your account, and you can withdraw it at a later time." +
                    " Please keep using our app to discover new ways to earn more money.";
        } else {
            summery_txt = "Congratulations! Your payment of " + transaction.getTransaction_money() + " rupees has been successfully processed. " +
                    "Keep using our app to unlock more earning opportunities and earn even more money!";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "Payment Complete ID")
                .setSmallIcon(R.mipmap.ic_app_logo)
                .setContentTitle(notifyTitle)
                .setContentText(summery_txt)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setChannelId(getApplicationContext().getPackageName());

        if (notifyImage != null) {
            builder.setLargeIcon(notifyImage);

            Bitmap bigPictureBitmap = notifyImage;
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                    .bigPicture(bigPictureBitmap)
                    .bigLargeIcon(bigPictureBitmap);

            builder.setStyle(bigPictureStyle);
        } else {
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                    .bigText(summery_txt)
                    .setSummaryText("Payment Complete");
            builder.setStyle(bigTextStyle);
        }
        builder.setSubText(notifyMsg);

        final int flag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;

        Intent intent = new Intent(getApplicationContext(), NotificationInboxActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, flag);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(105, builder.build());
    }

    private void getNotificationBigPictures(String status) {
        Drawable drawable;
        if (status.equalsIgnoreCase(TRANSACTION_STATUS_SUCCESS)) {
            drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.updated_encourage_earn_more_money, null);
        } else {
            drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.update_regret_money_lost, null);
        }
        if (drawable != null) {
            notifyImage = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(notifyImage);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
    }
}