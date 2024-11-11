package com.sanket_satpute_20.playme.project.account.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sanket_satpute_20.playme.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangeEmailEnterEmailFragment extends Fragment {

    public static final String EMAIL_EXTRAS = "EMAIL_EXTRAS";
    public static final String EMAIL_ACTION_CHANGE_1 = "email_action.change_email.fragment_1";
    public static final String CHANGE_EMAIL_ERROR_ACTION = "email_action.change_email.fragment_1.error";
    public static final String CHANGE_EMAIL_ERROR_MSG_EXTRAS = "CHANGE_EMAIL_ERROR_msg_extras";

    ColorStateList ok_btn_color = ColorStateList.valueOf(Color.parseColor("#ffa500"));
    ColorStateList error_btn_color = ColorStateList.valueOf(Color.parseColor("#b9b9b9"));

    TextView helper_text, verify_extra_txt, verify_btn_txt;
    TextInputEditText new_email;
    TextInputLayout new_email_outer;
    MaterialButton verify_email_btn;
    ProgressBar verify_email_progress;

    int ERROR_STROKE_WIDTH = 5;
    String requiredStr = "Required *";

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction() != null) {
                    if (intent.getAction().equals(CHANGE_EMAIL_ERROR_ACTION)) {
                        String error_msg = intent.getStringExtra(CHANGE_EMAIL_ERROR_MSG_EXTRAS);
                        verify_email_progress.setVisibility(View.GONE);
                        if (error_msg != null) {
                            new_email_outer.setError(error_msg);
                        } else {
                            new_email_outer.setError("email ID is invalid.");
                        }
                    }
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_email_enter_email, container, false);
        initViews(view);
        onClick();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CHANGE_EMAIL_ERROR_ACTION);
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver);
    }

    private void initViews(View view) {
        helper_text = view.findViewById(R.id.change_email_helper_text);
        verify_extra_txt = view.findViewById(R.id.verify_extra_msg_txt);
        verify_btn_txt = view.findViewById(R.id.verify_email_btn_txt);
        new_email = view.findViewById(R.id.new_email);
        new_email_outer = view.findViewById(R.id.new_email_outer);
        verify_email_btn = view.findViewById(R.id.verify_email_btn);
        verify_email_progress = view.findViewById(R.id.verify_email_btn_progress);
    }

    private void onClick() {
        new_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (new_email_outer.getError() != null) {
                    if (s.length() > 0) {
                        if (s.toString().contains(" ")) {
                            new_email_outer.setError("email contains spaces.");
                            new_email_outer.setBoxStrokeWidth(ERROR_STROKE_WIDTH);
                            isVerifyBtnAvailable();
                        } else {
                            if (isValidEmail(s)) {
                                new_email_outer.setError(null);
                                new_email_outer.setBoxStrokeWidth(0);
                                isVerifyBtnAvailable();
                            } else {
                                new_email_outer.setError("email ID is invalid.");
                                new_email_outer.setBoxStrokeWidth(ERROR_STROKE_WIDTH);
                                isVerifyBtnAvailable();
                            }
                        }
                    } else {
                        new_email_outer.setError(requiredStr);
                        new_email_outer.setBoxStrokeWidth(ERROR_STROKE_WIDTH);
                        isVerifyBtnAvailable();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        verify_email_btn.setOnClickListener(view -> {
            String emailStr = String.valueOf(new_email.getText()).trim();
            if (emailStr.length() > 0) {
                if (emailStr.contains(" ")) {
                    new_email_outer.setError("email contains spaces.");
                    new_email_outer.setBoxStrokeWidth(ERROR_STROKE_WIDTH);
                    isVerifyBtnAvailable();
                } else {
                    if (isValidEmail(emailStr)) {
                        new_email_outer.setError(null);
                        new_email_outer.setBoxStrokeWidth(0);
                        emailOtpRequestSent(emailStr);
                    } else {
                        new_email_outer.setError("email ID is invalid.");
                        new_email_outer.setBoxStrokeWidth(ERROR_STROKE_WIDTH);
                        isVerifyBtnAvailable();
                    }
                }
            } else {
                new_email_outer.setError(requiredStr);
                new_email_outer.setBoxStrokeWidth(ERROR_STROKE_WIDTH);
                isVerifyBtnAvailable();
            }
        });
    }

    private void isVerifyBtnAvailable() {
        if (new_email_outer.getError() == null) {
            verify_email_btn.setClickable(true);
            verify_email_btn.setBackgroundTintList(ok_btn_color);
        } else {
            verify_email_btn.setClickable(false);
            verify_email_btn.setBackgroundTintList(error_btn_color);
        }
    }

    private void emailOtpRequestSent(String emailForOtp) {
        verify_email_progress.setVisibility(View.VISIBLE);
        Intent intent = new Intent();
        intent.setAction(EMAIL_ACTION_CHANGE_1);
        intent.putExtra(EMAIL_EXTRAS, emailForOtp);
        LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
    }

    public boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}