package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.project.extra_stuffes.CommonMethods.getVersionName;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.DEVELOPER_MAIL_ID;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.SeeChangeLogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class AboutActivity extends AppCompatActivity {

//    LINKS
    private static final String PRIVACY_POLICY_LINK = "https://newblog7901.blogspot.com/p/playme-privacy-policy.html";
    public static final String TERMS_CONDITION_LINK = "https://newblog7901.blogspot.com/p/playme-terms-conditions.html";

    ImageView back_pressed;
    TextView see_change_log, version, privacy_policy, terms_conditions;
    MaterialButton mail_us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initViews();
        onClick();
    }

    private void initViews() {
        back_pressed = findViewById(R.id.back_pressed);
        see_change_log = findViewById(R.id.see_change_log_btn);
        version = findViewById(R.id.app_version);
        mail_us = findViewById(R.id.mail_btn);
        privacy_policy = findViewById(R.id.privacy_policy_txt);
        terms_conditions = findViewById(R.id.terms_conditions_txt);
    }

    private void onClick() {
        version.setText(getVersionName(getApplicationContext()));

        back_pressed.setOnClickListener(view -> onBackPressed());
        mail_us.setOnClickListener(view -> showMailUsDilog());
        privacy_policy.setOnClickListener(view -> seePrivacyPolicy());
        terms_conditions.setOnClickListener(view -> seeTermsAndConditions());

        see_change_log.setOnClickListener(view -> {
            SeeChangeLogFragment seeChangeLogFragment = new SeeChangeLogFragment();
            seeChangeLogFragment.show(getSupportFragmentManager(), "ABC");
        });

    }

    private void showMailUsDilog() {
        TextInputEditText name, massage;
        MaterialButton submit_button;

        View view = LayoutInflater.from(this).inflate(R.layout.send_personal_mail_layout, null);
        MaterialAlertDialogBuilder dilog = new MaterialAlertDialogBuilder(this)
                .setView(view);
        AlertDialog alert = dilog.create();
        alert.show();

        name = view.findViewById(R.id.full_name);
        massage = view.findViewById(R.id.massage);
        submit_button = view.findViewById(R.id.submit);

        submit_button.setOnClickListener(v -> {
            if (name.getText() != null && massage.getText() != null) {
                if (name.getText().length() > 0 && massage.getText().length() > 0) {
                    massageSubmitted(name.getText().toString(), massage.getText().toString());
                    Handler handler = new Handler();
                    handler.postDelayed(alert::dismiss, 1000);
                } else {
                    if (name.getText().length() < 0 && massage.getText().length() < 0) {
                        Toast.makeText(this, "fill name and massage box", Toast.LENGTH_SHORT).show();
                        name.setFocusable(true);
                    } else if (name.getText().length() < 0 && massage.getText().length() > 0) {
                        Toast.makeText(this, "fill name box", Toast.LENGTH_SHORT).show();
                        name.setFocusable(true);
                    } else {
                        Toast.makeText(this, "fill massage box", Toast.LENGTH_SHORT).show();
                        massage.setFocusable(true);
                    }
                }
            }
        });
    }

    private void seePrivacyPolicy() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_LINK));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to open browser", Toast.LENGTH_SHORT).show();
        }
    }

    private void seeTermsAndConditions() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(TERMS_CONDITION_LINK));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to open browser", Toast.LENGTH_SHORT).show();
        }
    }

    private void massageSubmitted(String name, String msg) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("text/email");
        email.putExtra(Intent.EXTRA_EMAIL, new String[] {DEVELOPER_MAIL_ID});
        email.putExtra(Intent.EXTRA_SUBJECT, name);
        email.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(email, "Submit Query"));
    }
}