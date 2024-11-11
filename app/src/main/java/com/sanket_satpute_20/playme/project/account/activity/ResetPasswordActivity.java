package com.sanket_satpute_20.playme.project.account.activity;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.bottom_fragment.UserPasswordResetLayoutBottomFragment;
import com.sanket_satpute_20.playme.project.account.fragments.ResetPasswordCreateNewPasswordFragment;
import com.sanket_satpute_20.playme.project.account.fragments.ResetPassword_CurrentPasswordFragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class ResetPasswordActivity extends AppCompatActivity {

//    actions
    public static final String CURRENT_PASSWORD_RESET_ACTION = "re_authentication_complete.current_password";
    public static final String CURRENT_PASSWORD_TRY_ANOTHER_WAY = "try_another_way.current_password";
    public static final String ERROR_CURRENT_PASSWORD_ACTION = "reset_error.current_password";
    public static final String NEW_PASSWORD_SET_ACTION = "set_password.new_password";
    public static final String NEW_PASSWORD_RESET_ACTION = "re_authentication_complete.new_password";
    public static final String PASSWORD_RESET_BY_EMAIL_LINK = "reset_password.byEmailLink";
    public static final String EMAIL_SEND_ACTION_COMPLETE = "email_send_action_complete.try_another_way";

    //    extras
    public static final String CURRENT_PASSWORD_EXTRAS = "CURRENT_PASSWORD_EXTRAS";
    public static final String ERROR_CURRENT_PASSWORD_EXTRA = "ERROR_CURRENT_PASSWORD_EXTRA";
    public static final String ERROR_NEW_PASSWORD_EXTRA = "ERROR_NEW_PASSWORD_EXTRA";
    public static final String NEW_PASSWORD_SET_NEW_PASSWORD_EXTRAS = "NEW_PASSWORD_SET_NEW_PASSWORD_EXTRAS";
    public static final String IS_EMAIL_SEND = "IS_EMAIL_SEND";
    public static final String EMAIL_ADDRESS = "EMAIL_ADDRESS";
    public static final String EMAIL_NOT_SEND_ERROR = "EMAIL_NOT_SEND_ERROR";

    RelativeLayout reset_password_parent_layout;

    public static FirebaseAuth f_auth;
    public FirebaseFirestore f_db;
    public static FirebaseUser user;

    UserPasswordResetLayoutBottomFragment bottomDialog;


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent broadErrorIntent = new Intent();
            if (intent != null) {
                switch (intent.getAction()) {
                    case CURRENT_PASSWORD_RESET_ACTION:
                        String currentPassword = intent.getStringExtra(CURRENT_PASSWORD_EXTRAS);
                        String email = user.getEmail();
                        if (currentPassword != null && email != null) {
                            AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);
                            user.reauthenticate(credential).addOnSuccessListener(unused -> {
                                FragmentTransaction f_transaction = getSupportFragmentManager().beginTransaction();
                                f_transaction.replace(R.id.reset_password_parent_layout, new ResetPasswordCreateNewPasswordFragment());
                                f_transaction.addToBackStack("second page");
                                f_transaction.commit();
                            }).addOnFailureListener(e -> {
                                broadErrorIntent.putExtra(ERROR_CURRENT_PASSWORD_EXTRA, e.getMessage());
                                broadErrorIntent.setAction(ERROR_CURRENT_PASSWORD_ACTION);
                                LocalBroadcastManager.getInstance(ResetPasswordActivity.this).sendBroadcast(broadErrorIntent);
                            });
                            break;
                        } else {
//                            send broadcast error cant be empty password
                            broadErrorIntent.putExtra(ERROR_CURRENT_PASSWORD_EXTRA, "password can't be empty");
                            broadErrorIntent.setAction(ERROR_CURRENT_PASSWORD_ACTION);
                            LocalBroadcastManager.getInstance(ResetPasswordActivity.this).sendBroadcast(broadErrorIntent);
                        }
                    case CURRENT_PASSWORD_TRY_ANOTHER_WAY:
                        bottomDialog = new UserPasswordResetLayoutBottomFragment();
                        bottomDialog.show(getSupportFragmentManager(), "ABC");
                        break;
                    case NEW_PASSWORD_SET_ACTION:
                        String new_password = intent.getStringExtra(NEW_PASSWORD_SET_NEW_PASSWORD_EXTRAS);
                        Log.d("mmky", "commed");
                        if (new_password != null) {
                            if (new_password.trim().length() > 5) {
                                user.updatePassword(new_password.trim()).addOnSuccessListener(unused -> {
                                    Log.d("mmky", "set new password successful");
                                    currentUser.setPassword(new_password.trim());
                                    Toast.makeText(ResetPasswordActivity.this, "Password Reset Successful", Toast.LENGTH_SHORT).show();
                                    finish();
                                }).addOnFailureListener(e -> {
                                    Log.d("mmky", "set new password failed");
                                    broadErrorIntent.putExtra(ERROR_NEW_PASSWORD_EXTRA, Arrays.toString(e.getStackTrace()));
                                    broadErrorIntent.setAction(NEW_PASSWORD_RESET_ACTION);
                                    LocalBroadcastManager.getInstance(ResetPasswordActivity.this).sendBroadcast(broadErrorIntent);
                                });
                            } else {
                                Log.d("mmky", "set password failed to short");
                                broadErrorIntent.putExtra(ERROR_NEW_PASSWORD_EXTRA, "password is too short, minimum 6 character's required");
                                broadErrorIntent.setAction(NEW_PASSWORD_RESET_ACTION);
                                LocalBroadcastManager.getInstance(ResetPasswordActivity.this).sendBroadcast(broadErrorIntent);
                            }
                        } else {
                            Log.d("mmky", "set password is null");
                            broadErrorIntent.putExtra(ERROR_NEW_PASSWORD_EXTRA, "password can't be empty");
                            broadErrorIntent.setAction(NEW_PASSWORD_RESET_ACTION);
                            LocalBroadcastManager.getInstance(ResetPasswordActivity.this).sendBroadcast(broadErrorIntent);
                        }
                        break;
                    case PASSWORD_RESET_BY_EMAIL_LINK:
                        if (f_auth == null || f_db == null)
                            connectToFirebase();
                        if (user == null)
                            user = getCurrentUser();
                        String user_email = user.getEmail();
                        if (user_email != null) {
                            f_auth.sendPasswordResetEmail(user_email).addOnSuccessListener(unused -> {
                                broadErrorIntent.putExtra(IS_EMAIL_SEND, true);
                                broadErrorIntent.putExtra(EMAIL_ADDRESS, user_email);
                                broadErrorIntent.setAction(EMAIL_SEND_ACTION_COMPLETE);
                                LocalBroadcastManager.getInstance(ResetPasswordActivity.this).sendBroadcast(broadErrorIntent);
                            }).addOnFailureListener(e -> {
                                broadErrorIntent.putExtra(IS_EMAIL_SEND, false);
                                broadErrorIntent.putExtra(EMAIL_NOT_SEND_ERROR, e.getMessage());
                                broadErrorIntent.setAction(EMAIL_SEND_ACTION_COMPLETE);
                                LocalBroadcastManager.getInstance(ResetPasswordActivity.this).sendBroadcast(broadErrorIntent);
                            });
                        } else {
                            broadErrorIntent.putExtra(IS_EMAIL_SEND, false);
                            broadErrorIntent.putExtra(EMAIL_NOT_SEND_ERROR, "user email not available");
                            broadErrorIntent.setAction(EMAIL_SEND_ACTION_COMPLETE);
                            LocalBroadcastManager.getInstance(ResetPasswordActivity.this).sendBroadcast(broadErrorIntent);
                        }
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initViews();
        user = getCurrentUser();
        if (user != null) {
            doExtra();
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CURRENT_PASSWORD_RESET_ACTION);
        filter.addAction(CURRENT_PASSWORD_TRY_ANOTHER_WAY);
        filter.addAction(NEW_PASSWORD_SET_ACTION);
        filter.addAction(PASSWORD_RESET_BY_EMAIL_LINK);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        if (bottomDialog != null) {
            bottomDialog.dismiss();
        }
    }

    private void initViews() {
        reset_password_parent_layout = findViewById(R.id.reset_password_parent_layout);
    }

    private void doExtra() {
        if (f_auth == null || f_db == null)
            connectToFirebase();
        FragmentTransaction f_transaction = getSupportFragmentManager().beginTransaction();
        f_transaction.replace(R.id.reset_password_parent_layout, new ResetPassword_CurrentPasswordFragment());
        f_transaction.commit();
    }

    private void connectToFirebase() {
        f_auth = FirebaseAuth.getInstance();
        f_db = FirebaseFirestore.getInstance();
    }

    private FirebaseUser getCurrentUser() {
        if (f_auth == null || f_db == null)
            connectToFirebase();
        return f_auth.getCurrentUser();
    }
}