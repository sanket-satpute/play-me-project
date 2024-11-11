package com.sanket_satpute_20.playme.project.account.activity;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.data.model.MUser;

import com.sanket_satpute_20.playme.project.account.service.TimerIntentService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity implements ServiceConnection {

    int error_box_size = 5;

    TextInputEditText email_id, password;
    TextInputLayout email_outer, password_outer;
    MaterialButton log_in;
    TextView sign_up;
    ProgressBar sign_in_progress;

    FirebaseAuth f_auth;
    FirebaseFirestore f_db;

    TimerIntentService timer_service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        connectToFirebase();
        initViews();
        onClick();
    }

    private void initViews() {
        email_id = findViewById(R.id.email_id);
        password = findViewById(R.id.password);
        log_in = findViewById(R.id.sign_in_button);
        sign_up = findViewById(R.id.sign_up_txt);
        email_outer = findViewById(R.id.email_id_outer);
        password_outer = findViewById(R.id.password_outer);
        sign_in_progress = findViewById(R.id.sign_in_btn_progress);
    }

    private void onClick() {
        log_in.setOnClickListener(view -> {
            String email_temp = String.valueOf(email_id.getText()).trim();
            String password_temp = String.valueOf(password.getText()).trim();
            if (email_temp.length() > 0) {
                email_outer.setError(null);
                email_outer.setBoxStrokeWidth(0);
            } else {
                email_outer.setError("email can't be empty");
                email_outer.setBoxStrokeWidth(error_box_size);
            }

            if (password_temp.length() > 0) {
                password_outer.setError(null);
                password_outer.setBoxStrokeWidth(0);
            } else {
                password_outer.setError("password can't be empty");
                password_outer.setBoxStrokeWidth(error_box_size);
            }

            if (email_outer.getError() == null && password_outer.getError() == null) {
                sign_in_progress.setVisibility(View.VISIBLE);
                userSignIn(email_temp, password_temp);
            } else {
                Toast.makeText(this, "Please correct the detail's", Toast.LENGTH_SHORT).show();
            }
        });

        sign_up.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void userSignIn(String e, String p) {
        if (f_auth == null || f_db == null)
            connectToFirebase();

        f_auth.signInWithEmailAndPassword(e, p)
                .addOnSuccessListener(authResult -> {
                    if (f_auth.getCurrentUser() != null) {
                        f_db.collection("users").document(f_auth.getCurrentUser().getUid()).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    sign_in_progress.setVisibility(View.GONE);
                                    currentUser = documentSnapshot.toObject(MUser.class);
                                    if (currentUser != null) {
                                        Intent timer_intent = new Intent(LoginActivity.this, TimerIntentService.class);
                                        startService(timer_intent);
                                        bindService(timer_intent, LoginActivity.this, BIND_AUTO_CREATE);
                                        Intent intent = new Intent(LoginActivity.this, ShowUserDetailActivity.class);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(LoginActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Unable to Sign in", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(e12 -> {
                                    sign_in_progress.setVisibility(View.GONE);
                                    if (e12.getMessage() != null) {
                                        Toast.makeText(this, ""+e12.getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(this, "Failed to Sign In", Toast.LENGTH_SHORT).show();
                                    }
                                    finish();
                                });
                    } else {
                        sign_in_progress.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e1 -> {
                    sign_in_progress.setVisibility(View.GONE);
                    if (e1.getMessage() != null) {
                        if (e1.getMessage().trim().contains("email") || e1.getMessage().trim().contains("Email")) {
                            email_outer.setError(e1.getMessage());
                            email_outer.setBoxStrokeWidth(error_box_size);
                        } else if (e1.getMessage().trim().contains("password") || e1.getMessage().trim().contains("Password")) {
                            password_outer.setError(e1.getMessage());
                            email_outer.setBoxStrokeWidth(error_box_size);
                        } else if (e1.getMessage().trim().contains("no user")) {
                            email_outer.setError("there is no user with these email");
                            email_outer.setBoxStrokeWidth(error_box_size);
                        } else {
                            Toast.makeText(this, "Failed to Sign In \n" + e1.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Credential Sign In", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCanceledListener(() -> sign_in_progress.setVisibility(View.GONE));
    }

    private void connectToFirebase() {
        f_auth = FirebaseAuth.getInstance();
        f_db = FirebaseFirestore.getInstance();
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