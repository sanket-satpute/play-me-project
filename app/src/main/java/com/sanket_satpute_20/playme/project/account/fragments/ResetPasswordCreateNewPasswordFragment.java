package com.sanket_satpute_20.playme.project.account.fragments;

import static com.sanket_satpute_20.playme.project.account.activity.ResetPasswordActivity.ERROR_NEW_PASSWORD_EXTRA;
import static com.sanket_satpute_20.playme.project.account.activity.ResetPasswordActivity.NEW_PASSWORD_RESET_ACTION;
import static com.sanket_satpute_20.playme.project.account.activity.ResetPasswordActivity.NEW_PASSWORD_SET_ACTION;
import static com.sanket_satpute_20.playme.project.account.activity.ResetPasswordActivity.NEW_PASSWORD_SET_NEW_PASSWORD_EXTRAS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sanket_satpute_20.playme.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPasswordCreateNewPasswordFragment extends Fragment {

    TextInputLayout new_password_outer, confirm_password_outer;
    TextInputEditText new_password, confirm_password;
    MaterialButton reset_password_btn;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (NEW_PASSWORD_RESET_ACTION.equals(intent.getAction())) {
                    String errorMSG = intent.getStringExtra(ERROR_NEW_PASSWORD_EXTRA);
                    Log.d("mmky", "error generated " + errorMSG);
                    new_password_outer.setError(errorMSG);
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password_create_new_password, container, false);
        initViews(view);
        onClick();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(NEW_PASSWORD_RESET_ACTION);
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver);
    }

    private void initViews(View view) {
        new_password_outer = view.findViewById(R.id.new_password_outer);
        confirm_password_outer = view.findViewById(R.id.confirm_password_outer);
        new_password = view.findViewById(R.id.new_password);
        confirm_password = view.findViewById(R.id.confirm_password);
        reset_password_btn = view.findViewById(R.id.reset_password_btn);
    }

    private void onClick() {
        new_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    new_password_outer.setHelperText(null);
                } else {
                    new_password_outer.setHelperText("must be at least 6 character's");
                }
                if (new_password_outer.getError() != null) {
                    if (s.length() > 0) {
                        if (s.length() > 5) {
                            if (String.valueOf(s).contains(" ")) {
                                new_password_outer.setError("new password contain spaces");
                                new_password_outer.setBoxStrokeWidth(5);
                            } else {
                                new_password_outer.setError(null);
                                new_password_outer.setBoxStrokeWidth(0);
                                String c_password = String.valueOf(confirm_password.getText()).trim();
                                if (s.equals(c_password)) {
                                    confirm_password_outer.setError(null);
                                    confirm_password_outer.setBoxStrokeWidth(0);
                                }
                            }
                        } else {
                            new_password_outer.setError("new password is too short, minimum 6 character's required");
                            new_password_outer.setBoxStrokeWidth(5);
                        }
                    } else {
                        new_password_outer.setError("new password can't be empty");
                        new_password_outer.setBoxStrokeWidth(5);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    confirm_password_outer.setHelperText(null);
                } else {
                    confirm_password_outer.setHelperText("both password must be match");
                }
                if (confirm_password_outer.getError() != null) {
                    if (s.length() > 0) {
                        String n_password = String.valueOf(new_password.getText()).trim();
                        if (n_password.contentEquals(s)) {
                            confirm_password_outer.setError(null);
                            confirm_password_outer.setBoxStrokeWidth(0);
                        } else {
                            confirm_password_outer.setError("password doesn't match to the new password");
                            confirm_password_outer.setBoxStrokeWidth(5);
                        }
                    } else {
                        confirm_password_outer.setError("confirm password can't be empty");
                        confirm_password_outer.setBoxStrokeWidth(5);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        reset_password_btn.setOnClickListener(view -> {
            String n_password = String.valueOf(new_password.getText()).trim();
            String c_password = String.valueOf(confirm_password.getText()).trim();
            if (n_password.length() > 0) {
                if (n_password.length() > 5) {
                    if (n_password.contains(" ")) {
                        new_password_outer.setError("new password contain spaces");
                        new_password_outer.setBoxStrokeWidth(5);
                    } else {
                        new_password_outer.setError(null);
                        new_password_outer.setBoxStrokeWidth(0);
                    }
                } else {
                    new_password_outer.setError("new password is too short, minimum 6 character's required");
                    new_password_outer.setBoxStrokeWidth(5);
                }
            } else {
                new_password_outer.setError("new password can't be empty");
                new_password_outer.setBoxStrokeWidth(5);
            }
            if (c_password.length() > 0) {
                if (n_password.equals(c_password)) {
                    confirm_password_outer.setError(null);
                    confirm_password_outer.setBoxStrokeWidth(0);
                } else {
                    confirm_password_outer.setError("password doesn't match to the new password");
                    confirm_password_outer.setBoxStrokeWidth(5);
                }
            } else {
                confirm_password_outer.setError("confirm password can't be empty");
                confirm_password_outer.setBoxStrokeWidth(5);
            }

            if (new_password_outer.getError() == null && confirm_password_outer.getError() == null) {
                Intent intent = new Intent();
                intent.putExtra(NEW_PASSWORD_SET_NEW_PASSWORD_EXTRAS, n_password);
                intent.setAction(NEW_PASSWORD_SET_ACTION);
                LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
            } else {
                Toast.makeText(requireActivity(), "please correct the error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}