package com.sanket_satpute_20.playme.project.account.fragments;

import static com.sanket_satpute_20.playme.project.account.activity.ResetPasswordActivity.CURRENT_PASSWORD_EXTRAS;
import static com.sanket_satpute_20.playme.project.account.activity.ResetPasswordActivity.CURRENT_PASSWORD_RESET_ACTION;
import static com.sanket_satpute_20.playme.project.account.activity.ResetPasswordActivity.CURRENT_PASSWORD_TRY_ANOTHER_WAY;
import static com.sanket_satpute_20.playme.project.account.activity.ResetPasswordActivity.ERROR_CURRENT_PASSWORD_ACTION;
import static com.sanket_satpute_20.playme.project.account.activity.ResetPasswordActivity.ERROR_CURRENT_PASSWORD_EXTRA;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanket_satpute_20.playme.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPassword_CurrentPasswordFragment extends Fragment {

    TextView try_another_way;
    TextInputLayout password_outer;
    TextInputEditText password_edit_txt;
    MaterialButton reset_password_btn;

    FloatingActionButton option_suggestion;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                switch (intent.getAction()) {
                    case ERROR_CURRENT_PASSWORD_ACTION:
                        String errorMSG = intent.getStringExtra(ERROR_CURRENT_PASSWORD_EXTRA);
                        password_outer.setError(errorMSG);
                        break;
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password_current_password, container, false);
        initViews(view);
        onClick();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ERROR_CURRENT_PASSWORD_ACTION);
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver);
    }

    private void initViews(View view) {
        try_another_way = view.findViewById(R.id.try_another_way);
        password_outer = view.findViewById(R.id.current_password_outer);
        password_edit_txt = view.findViewById(R.id.current_password);
        reset_password_btn = view.findViewById(R.id.reset_password_btn);
        option_suggestion = view.findViewById(R.id.option_suggestion);
    }

    private void onClick() {
        reset_password_btn.setOnClickListener(view -> {
            String current_pass = String.valueOf(password_edit_txt.getText());
            if (current_pass.trim().length() > 0) {
                password_outer.setError(null);
                Intent intent = new Intent();
                intent.putExtra(CURRENT_PASSWORD_EXTRAS, current_pass.trim());
                intent.setAction(CURRENT_PASSWORD_RESET_ACTION);
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
            } else {
                password_outer.setError("please enter your password");
            }
        });

        try_another_way.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(CURRENT_PASSWORD_TRY_ANOTHER_WAY);
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
        });

        option_suggestion.setOnClickListener(view -> {
            MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(requireActivity(), R.style.DialogCorners);
            alert.setTitle("How To ?")
                    .setMessage("Enter your password inside that box and click to reset password button. " +
                            "the password which you have used to create account. if you have forgotten the password, you can reset the password by another way by clicking the ' Try Another Way ' below.")
                    .setPositiveButton("Got it", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = alert.create();
            dialog.show();
        });
    }
}