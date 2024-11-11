package com.sanket_satpute_20.playme.project.account.activity;


import static com.sanket_satpute_20.playme.project.account.extra_stuffes.JavaMailAPI.action_otp_sent_completed;
import static com.sanket_satpute_20.playme.project.account.extra_stuffes.JavaMailAPI.otp_sent_success_or_fail_error_msg_extra;
import static com.sanket_satpute_20.playme.project.account.extra_stuffes.JavaMailAPI.otp_sent_success_or_fail_extra;
import static com.sanket_satpute_20.playme.project.account.fragments.ChangeEmailEnterEmailFragment.CHANGE_EMAIL_ERROR_ACTION;
import static com.sanket_satpute_20.playme.project.account.fragments.ChangeEmailEnterEmailFragment.CHANGE_EMAIL_ERROR_MSG_EXTRAS;
import static com.sanket_satpute_20.playme.project.account.fragments.ChangeEmailEnterEmailFragment.EMAIL_ACTION_CHANGE_1;
import static com.sanket_satpute_20.playme.project.account.fragments.ChangeEmailEnterEmailFragment.EMAIL_EXTRAS;
import static com.sanket_satpute_20.playme.project.account.fragments.ChangeEmailVerifyFragment.ACTION_CHANGE_EMAIL_VERIFY_FRAGMENT_DESTROY;
import static com.sanket_satpute_20.playme.project.account.fragments.ChangeEmailVerifyFragment.ACTION_CHANGE_EMAIL_VERIFY_FRAGMENT_ERROR;
import static com.sanket_satpute_20.playme.project.account.fragments.ChangeEmailVerifyFragment.EMAIL_ACTION_CHANGE_2;
import static com.sanket_satpute_20.playme.project.account.fragments.ChangeEmailVerifyFragment.EMAIL_ACTION_CHANGE_2_OTP;
import static com.sanket_satpute_20.playme.project.account.fragments.ChangeEmailVerifyFragment.OTP_CODE_EXTRAS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.extra_stuffes.JavaMailAPI;
import com.sanket_satpute_20.playme.project.account.fragments.ChangeEmailEnterEmailFragment;
import com.sanket_satpute_20.playme.project.account.fragments.ChangeEmailVerifyFragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.Random;

//cg2vuko2paac70dvr44g.YQQAsX2hMzKWX8V5uocfPN - API SECRET
public class ChangeEmailActivity extends AppCompatActivity {

    FirebaseAuth f_auth;
    FirebaseUser f_user;
    FirebaseFirestore f_db;

    String OTP, newEmailAddress;

    JavaMailAPI mail_api;

    ChangeEmailVerifyFragment change_email_verify_fragment;

    //    Views
    RelativeLayout change_email_layout;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                switch (intent.getAction()) {
                    case EMAIL_ACTION_CHANGE_1:
                        String emailExtra = intent.getStringExtra(EMAIL_EXTRAS);
                        if (emailExtra != null) {
                            newEmailAddress = emailExtra;
                            OTP = getRandomOtpGenerator();
                            mail_api = new JavaMailAPI(ChangeEmailActivity.this, emailExtra, OTP);
                            mail_api.execute();
                        } else {
                            Intent broadErrorInt = new Intent();
                            broadErrorInt.setAction(CHANGE_EMAIL_ERROR_ACTION);
                            broadErrorInt.putExtra(CHANGE_EMAIL_ERROR_MSG_EXTRAS, "email can't be empty");
                            LocalBroadcastManager.getInstance(ChangeEmailActivity.this).sendBroadcast(broadErrorInt);
                        }
                        break;
                    case EMAIL_ACTION_CHANGE_2:
                        if (newEmailAddress != null) {
                            OTP = getRandomOtpGenerator();
                            mail_api = new JavaMailAPI(ChangeEmailActivity.this, newEmailAddress, OTP);
                            mail_api.execute();
                        } else {
                            Toast.makeText(ChangeEmailActivity.this, "Email Not Found", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case action_otp_sent_completed:
                        boolean isSend = intent.getBooleanExtra(otp_sent_success_or_fail_extra, false);
                        if (isSend) {
                            Toast.makeText(ChangeEmailActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                            if (f_user == null)
                                connectToFirebase();
                            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail_id(), currentUser.getPassword());
                            f_user.reauthenticate(credential).addOnSuccessListener(unused -> {
                                if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
                                    FragmentTransaction f_transaction = getSupportFragmentManager().beginTransaction();
                                    change_email_verify_fragment = new ChangeEmailVerifyFragment();
                                    Bundle arguments = new Bundle();
                                    arguments.putString("USER_EMAIL_NEW", newEmailAddress);
                                    change_email_verify_fragment.setArguments(arguments);
                                    f_transaction.replace(R.id.change_email_layout, change_email_verify_fragment);
                                    f_transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                    f_transaction.addToBackStack("verifyEmailFragment");
                                    f_transaction.commit();
                                }
                            }).addOnFailureListener(e -> {
                                Intent broadErrorInt = new Intent();
                                broadErrorInt.setAction(CHANGE_EMAIL_ERROR_ACTION);
                                broadErrorInt.putExtra(CHANGE_EMAIL_ERROR_MSG_EXTRAS, e.getMessage());
                                LocalBroadcastManager.getInstance(ChangeEmailActivity.this).sendBroadcast(broadErrorInt);
                            });
                        } else {
                            String error_msg = intent.getStringExtra(otp_sent_success_or_fail_error_msg_extra);
                            Intent broadErrorInt = new Intent();
                            broadErrorInt.setAction(CHANGE_EMAIL_ERROR_ACTION);
                            broadErrorInt.putExtra(CHANGE_EMAIL_ERROR_MSG_EXTRAS, error_msg);
                            LocalBroadcastManager.getInstance(ChangeEmailActivity.this).sendBroadcast(broadErrorInt);
                        }
                        break;
                    case ACTION_CHANGE_EMAIL_VERIFY_FRAGMENT_DESTROY:
                        onBackPressed();
                        break;
                    case EMAIL_ACTION_CHANGE_2_OTP:
                        String enteredOTP = intent.getStringExtra(OTP_CODE_EXTRAS);
                        if (enteredOTP != null) {
                            if (OTP.equalsIgnoreCase(enteredOTP)) {
                                setEmailSuccessfully();
                            } else {
                                Intent errorBroad = new Intent();
                                errorBroad.setAction(ACTION_CHANGE_EMAIL_VERIFY_FRAGMENT_ERROR);
                                LocalBroadcastManager.getInstance(ChangeEmailActivity.this).sendBroadcast(errorBroad);
                                Toast.makeText(ChangeEmailActivity.this, "OTP is not match, please try again", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Intent errorBroad = new Intent();
                            errorBroad.setAction(ACTION_CHANGE_EMAIL_VERIFY_FRAGMENT_ERROR);
                            LocalBroadcastManager.getInstance(ChangeEmailActivity.this).sendBroadcast(errorBroad);
                            Toast.makeText(ChangeEmailActivity.this, "OTP is not match", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        initViews();
        doExtra();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(EMAIL_ACTION_CHANGE_1);
        filter.addAction(action_otp_sent_completed);
        filter.addAction(EMAIL_ACTION_CHANGE_2_OTP);
        filter.addAction(ACTION_CHANGE_EMAIL_VERIFY_FRAGMENT_DESTROY);
        filter.addAction(EMAIL_ACTION_CHANGE_2);
        LocalBroadcastManager.getInstance(ChangeEmailActivity.this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(ChangeEmailActivity.this).unregisterReceiver(receiver);
    }

    private void initViews() {
        change_email_layout = findViewById(R.id.change_email_layout);
    }

    private void doExtra() {
        FragmentTransaction f_transaction = getSupportFragmentManager().beginTransaction();
        f_transaction.replace(R.id.change_email_layout, new ChangeEmailEnterEmailFragment());
        f_transaction.commit();
    }

    private String getRandomOtpGenerator() {
        return new DecimalFormat("000000").format(new Random().nextInt(999999));
    }

    private void connectToFirebase() {
        if (f_auth == null)
            f_auth = FirebaseAuth.getInstance();
        if (f_db == null)
            f_db = FirebaseFirestore.getInstance();
        if (f_user == null)
            f_user = f_auth.getCurrentUser();
    }

    /*private boolean isEmailAlreadyAvail(String email) {
        *//*if (f_auth == null )
            connectToFirebase();
        f_auth.*//*
    }*/

    private void setEmailSuccessfully() {
        if (f_auth == null || f_db == null || f_user == null)
            connectToFirebase();
        if (f_user != null && newEmailAddress != null) {
            f_user.updateEmail(newEmailAddress).addOnSuccessListener(unused2 -> {
                currentUser.setEmail_id(newEmailAddress);
                f_db.collection("users").document(f_user.getUid())
                        .set(currentUser)
                        .addOnSuccessListener(unused1 -> {
                            Toast.makeText(ChangeEmailActivity.this, "Updated Email Address", Toast.LENGTH_SHORT).show();
                            finish();
                        }).addOnFailureListener(e -> {
                            e.printStackTrace();
                            Toast.makeText(ChangeEmailActivity.this, "Updated Email Address", Toast.LENGTH_SHORT).show();
                            finish();
                        });
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                Toast.makeText(ChangeEmailActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                finish();
            });
        } else {
            Toast.makeText(this, "Failed to change. Restart App and try again", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}