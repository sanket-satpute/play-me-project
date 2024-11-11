package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.APP_MAIL_ID;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sanket_satpute_20.playme.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class FeedbackActivity extends AppCompatActivity {

    ImageView back_pressed;
    TextInputEditText name, email, bio;
    MaterialButton submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initViews();
        onClick();
    }

    private void initViews() {
        back_pressed = findViewById(R.id.back_pressed);
        name = findViewById(R.id.full_name);
        email = findViewById(R.id.email);
        bio = findViewById(R.id.email_subject);
        submit = findViewById(R.id.submit);
    }

    private void onClick() {
        submit.setOnClickListener(view -> {
            try {
                if (!(Objects.requireNonNull(bio.getText()).toString().trim().isEmpty())) {
                    sendFeedback();
                } else {
                    Toast.makeText(this, "You Should too Feel Massage Box", Toast.LENGTH_SHORT).show();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });

        back_pressed.setOnClickListener(view -> onBackPressed());
    }

    private void sendFeedback() {
        String posting_user_mail = Objects.requireNonNull(email.getText()).toString() + "\n\n\n";
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("text/email");
        email.putExtra(Intent.EXTRA_EMAIL, new String[] {APP_MAIL_ID});
        email.putExtra(Intent.EXTRA_SUBJECT, Objects.requireNonNull(name.getText()).toString());
        email.putExtra(Intent.EXTRA_TEXT, posting_user_mail + Objects.requireNonNull(bio.getText()).toString());
        startActivity(Intent.createChooser(email, "Send Feedback..."));
        finish();
    }

}