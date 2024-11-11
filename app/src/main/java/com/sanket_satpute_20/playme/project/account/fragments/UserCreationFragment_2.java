package com.sanket_satpute_20.playme.project.account.fragments;

import static com.sanket_satpute_20.playme.project.account.activity.CreateAccountActivity.RETURN_USER_CREATION_SECOND_PAGE_DONE;
import static com.sanket_satpute_20.playme.project.account.activity.CreateAccountActivity.USER_CREATION_SECOND_PAGE_DONE;
import static com.sanket_satpute_20.playme.project.account.activity.CreateAccountActivity.USER_FIRST_NAME;
import static com.sanket_satpute_20.playme.project.account.activity.CreateAccountActivity.USER_LAST_NAME;
import static com.sanket_satpute_20.playme.project.account.activity.CreateAccountActivity.USER_PHONE;
import static com.sanket_satpute_20.playme.project.account.activity.CreateAccountActivity.USER_UPI_ID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sanket_satpute_20.playme.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserCreationFragment_2 extends Fragment {


    TextInputEditText first_name, last_name, upi_id, phone_no;
    TextInputLayout first_name_outer, last_name_outer, upi_id_outer, phone_outer;

    int box_stroke_main_width = 5;

    String upi_helper_text_1 = "make sure the upi id is yours either the money will be credited to another persons account";
    String phone_no_regex = "(0|91)?[6-9][0-9]{9}";

    private final BroadcastReceiver broadcast_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(USER_CREATION_SECOND_PAGE_DONE)) {
                broadcastSendStarter();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_creation_2, container, false);
        initViews(view);
        eventHandler();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(broadcast_receiver, new IntentFilter(USER_CREATION_SECOND_PAGE_DONE));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcast_receiver);
    }

    private void initViews(View view) {
        first_name = view.findViewById(R.id.first_name);
        last_name = view.findViewById(R.id.last_name);
        upi_id = view.findViewById(R.id.upi_id);
        phone_no = view.findViewById(R.id.phone_no);
        first_name_outer = view.findViewById(R.id.first_name_outer);
        last_name_outer = view.findViewById(R.id.last_name_outer);
        upi_id_outer = view.findViewById(R.id.upi_id_outer);
        phone_outer = view.findViewById(R.id.phone_no_outer);
    }

    private void eventHandler() {
        upi_id_outer.setHelperText(upi_helper_text_1);

        first_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (first_name_outer.getError() != null) {
                    if (s.length() > 0) {
                        first_name_outer.setError(null);
                        first_name_outer.setBoxStrokeWidth(0);
                    } else {
                        first_name_outer.setError("first name can't be empty");
                        first_name_outer.setBoxStrokeWidth(box_stroke_main_width);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        last_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (last_name_outer.getError() != null) {
                    if (s.length() > 0) {
                        last_name_outer.setError(null);
                        last_name_outer.setBoxStrokeWidth(0);
                    } else {
                        last_name_outer.setError("last name can't be empty");
                        last_name_outer.setBoxStrokeWidth(box_stroke_main_width);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phone_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (phone_outer.getError() != null) {
                    if (s.length() > 0) {
                        if (isPhoneValid(String.valueOf(s))) {
                            phone_outer.setError(null);
                            phone_outer.setBoxStrokeWidth(0);
                        } else {
                            phone_outer.setError("phone number is not valid");
                            phone_outer.setBoxStrokeWidth(box_stroke_main_width);
                        }
                    } else {
                        phone_outer.setError("phone number can't be empty");
                        phone_outer.setBoxStrokeWidth(box_stroke_main_width);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void broadcastSendStarter() {
        if (first_name.getText() != null) {
            if (!(first_name.getText().toString().length() > 0)) {
                first_name_outer.setError("first name can't be empty.");
                first_name_outer.setBoxStrokeWidth(box_stroke_main_width);
            } else {
                first_name_outer.setError(null);
                first_name_outer.setBoxStrokeWidth(0);
            }
        }

        if (last_name.getText() != null) {
            if (!(last_name.getText().toString().length() > 0)) {
                last_name_outer.setError("last name can't be empty.");
                last_name_outer.setBoxStrokeWidth(box_stroke_main_width);
            } else {
                last_name_outer.setError(null);
                last_name_outer.setBoxStrokeWidth(0);
            }
        }

        if (phone_no.getText() != null) {
            if (!(last_name.getText().toString().length() > 0)) {
                phone_outer.setError("phone number can't be empty.");
                phone_outer.setBoxStrokeWidth(box_stroke_main_width);
            } else {
                if (isPhoneValid(String.valueOf(phone_no.getText()))) {
                    phone_outer.setError(null);
                    phone_outer.setBoxStrokeWidth(0);
                } else {
                    phone_outer.setError("phone number is not valid");
                    phone_outer.setBoxStrokeWidth(box_stroke_main_width);
                }
            }
        }

        if (phone_outer.getError() == null && last_name_outer.getError() == null && first_name_outer.getError() == null) {
            createAccountBroadcast();
        } else {
            Toast.makeText(requireActivity(), "Please correct the detail's first", Toast.LENGTH_SHORT).show();
        }
    }

    private void createAccountBroadcast() {
        Intent intent = new Intent();
        intent.putExtra(USER_FIRST_NAME, String.valueOf(first_name.getText()).trim());
        intent.putExtra(USER_LAST_NAME, String.valueOf(last_name.getText()).trim());
        intent.putExtra(USER_PHONE, Long.valueOf(String.valueOf(phone_no.getText()).trim()));
        intent.putExtra(USER_UPI_ID, String.valueOf(upi_id.getText()).trim());
        intent.setAction(RETURN_USER_CREATION_SECOND_PAGE_DONE);
        LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
    }

    private boolean isPhoneValid(String number) {
        Pattern pattern = Pattern.compile(phone_no_regex);
        Matcher matcher = pattern.matcher(number);
        return (matcher.find() && matcher.group().equals(number));
    }
}