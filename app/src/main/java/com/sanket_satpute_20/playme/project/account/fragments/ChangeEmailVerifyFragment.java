package com.sanket_satpute_20.playme.project.account.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sanket_satpute_20.playme.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ChangeEmailVerifyFragment extends Fragment {

    public static final String EMAIL_ACTION_CHANGE_2_OTP = "action.email_change_2.OTP.completed";
    public static final String ACTION_CHANGE_EMAIL_VERIFY_FRAGMENT_DESTROY = "action.change_email_verify_fragment.DESTROY";
    public static final String OTP_CODE_EXTRAS = "OTP_CODE_EXTRAS";
    public static final String EMAIL_ACTION_CHANGE_2 = "EMAIL_ACTION_CHANGE_2";
    public static final String ACTION_CHANGE_EMAIL_VERIFY_FRAGMENT_ERROR = "action.change_email_verify_fragment.ERROR";


    TextInputEditText otp_edit_1, otp_edit_2, otp_edit_3, otp_edit_4, otp_edit_5, otp_edit_6;
    MaterialButton verify_btn;
    TextView resend_txt, did_not_receive_otp_timer, verify_helper_text;
    ImageView back_pressed;
    ProgressBar verify_btn_progress;

    String otp_code, user_email_new;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(ACTION_CHANGE_EMAIL_VERIFY_FRAGMENT_ERROR)) {
                    verify_btn_progress.setVisibility(View.GONE);
                }
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_email_verify, container, false);
        initViews(view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            user_email_new = bundle.getString("USER_EMAIL_NEW");
        } else {
            user_email_new = "NONE";
        }
        String helper_ms = "We have sent the OTP " + user_email_new;
        verify_helper_text.setText(helper_ms);
        onClick();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CHANGE_EMAIL_VERIFY_FRAGMENT_ERROR);
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver);
    }

    private void initViews(View view) {
        otp_edit_1 = view.findViewById(R.id.verify_box_1);
        otp_edit_2 = view.findViewById(R.id.verify_box_2);
        otp_edit_3 = view.findViewById(R.id.verify_box_3);
        otp_edit_4 = view.findViewById(R.id.verify_box_4);
        otp_edit_5 = view.findViewById(R.id.verify_box_5);
        otp_edit_6 = view.findViewById(R.id.verify_box_6);
        verify_btn = view.findViewById(R.id.verify_otp_btn);
        resend_txt = view.findViewById(R.id.resend_txt);
        back_pressed = view.findViewById(R.id.back_pressed);
        verify_btn_progress = view.findViewById(R.id.verify_btn_progress);
        verify_helper_text = view.findViewById(R.id.verify_helper_text);
        did_not_receive_otp_timer = view.findViewById(R.id.did_not_recive_txt);
    }

    private void onClick() {
        back_pressed.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(ACTION_CHANGE_EMAIL_VERIFY_FRAGMENT_DESTROY);
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
        });

        otp_edit_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    otp_edit_1.clearFocus();
                    otp_edit_2.requestFocus();
                    otp_edit_2.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp_edit_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    otp_edit_2.clearFocus();
                    otp_edit_3.requestFocus();
                    otp_edit_3.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp_edit_3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    otp_edit_3.clearFocus();
                    otp_edit_4.requestFocus();
                    otp_edit_4.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp_edit_4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    otp_edit_4.clearFocus();
                    otp_edit_5.requestFocus();
                    otp_edit_5.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp_edit_5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    otp_edit_5.clearFocus();
                    otp_edit_6.requestFocus();
                    otp_edit_6.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp_edit_6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    otp_edit_6.clearFocus();
                    if (String.valueOf(otp_edit_1.getText()).trim().length() == 0) {
                        otp_edit_1.requestFocus();
                        otp_edit_1.setCursorVisible(true);
                    } else if (String.valueOf(otp_edit_2.getText()).trim().length() == 0) {
                        otp_edit_2.requestFocus();
                        otp_edit_2.setCursorVisible(true);
                    } else if (String.valueOf(otp_edit_3.getText()).trim().length() == 0) {
                        otp_edit_3.requestFocus();
                        otp_edit_3.setCursorVisible(true);
                    } else if (String.valueOf(otp_edit_4.getText()).trim().length() == 0) {
                        otp_edit_4.requestFocus();
                        otp_edit_4.setCursorVisible(true);
                    } else if (String.valueOf(otp_edit_5.getText()).trim().length() == 0) {
                        otp_edit_5.requestFocus();
                        otp_edit_5.setCursorVisible(true);
                    } else {
                        startBtnVerify();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        resend_txt.setOnClickListener(view -> {
            Toast.makeText(requireActivity(), "Sending OTP...", Toast.LENGTH_SHORT).show();
            Intent broadIntent = new Intent();
            broadIntent.setAction(EMAIL_ACTION_CHANGE_2);
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(broadIntent);
        });

        verify_btn.setOnClickListener(view -> startBtnVerify());
    }

    private void startBtnVerify() {
        String n1 = String.valueOf(otp_edit_1.getText()).trim();
        String n2 = String.valueOf(otp_edit_2.getText()).trim();
        String n3 = String.valueOf(otp_edit_3.getText()).trim();
        String n4 = String.valueOf(otp_edit_4.getText()).trim();
        String n5 = String.valueOf(otp_edit_5.getText()).trim();
        String n6 = String.valueOf(otp_edit_6.getText()).trim();
        if (n1.length() > 0 && n2.length() > 0 && n3.length() > 0 && n4.length() > 0 && n5.length() > 0 && n6.length() > 0) {
            otp_code = n1 + n2 + n3 + n4 + n5 + n6;
            verify_btn_progress.setVisibility(View.VISIBLE);
            verifyOTPCode(otp_code);
        } else {
            verify_btn_progress.setVisibility(View.GONE);
            Toast.makeText(requireActivity(), "Enter Full OTP", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyOTPCode(String otpCode) {
        Intent intent = new Intent();
        intent.setAction(EMAIL_ACTION_CHANGE_2_OTP);
        intent.putExtra(OTP_CODE_EXTRAS, otpCode);
        LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
    }

}