package com.sanket_satpute_20.playme.project.account.fragments;

import static com.sanket_satpute_20.playme.project.account.activity.CreateAccountActivity.RETURN_USER_CREATION_FIRST_PAGE_DONE;
import static com.sanket_satpute_20.playme.project.account.activity.CreateAccountActivity.USER_CREATION_FIRST_PAGE_DONE;
import static com.sanket_satpute_20.playme.project.account.activity.CreateAccountActivity.USER_EMAIL;
import static com.sanket_satpute_20.playme.project.account.activity.CreateAccountActivity.USER_PASSWORD;
import static com.sanket_satpute_20.playme.project.account.activity.CreateAccountActivity.USER_USER_NAME;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sanket_satpute_20.playme.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

public class UserCreationFragment_1 extends Fragment {

    TextInputLayout username_outer, email_outer, password_outer, confirm_password_outer;
    TextInputEditText user_name, email, password, confirm_password;

    int error_box_width = 5;
    String user_name_sanket_satpute_20 = "eg. your_name@123";

    private final BroadcastReceiver broadcast_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(USER_CREATION_FIRST_PAGE_DONE)) {
                sendCreateAccountBroadcast();
            } else if (intent.getAction().equals("ERROR_MSG_EMAIL_ACTION")) {
                email_outer.setError(intent.getStringExtra("ERROR_MSG_EMAIL"));
                email_outer.setBoxStrokeWidth(error_box_width);
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_creation_1, container, false);
        initViews(view);
        eventHandler();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(USER_CREATION_FIRST_PAGE_DONE);
        filter.addAction("ERROR_MSG_EMAIL_ACTION");
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(broadcast_receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcast_receiver);
    }

    private void initViews(View view) {
        username_outer = view.findViewById(R.id.user_name_outer);
        email_outer = view.findViewById(R.id.email_outer);
        password_outer = view.findViewById(R.id.password_outer);
        confirm_password_outer = view.findViewById(R.id.confirm_password_outer);

        user_name = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        confirm_password = view.findViewById(R.id.confirm_password);
    }

    private void eventHandler() {

        username_outer.setHelperText(user_name_sanket_satpute_20);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (email_outer.getError() != null) {
                    if (s.length() > 0) {
//                    verify email
                        if (verifyEmail(String.valueOf(s).trim())) {
//                        send link to verify email
                            email_outer.setError(null);
                            email_outer.setBoxStrokeWidth(0);
                        } else {
                            email_outer.setError("invalid email id.");
                            email_outer.setBoxStrokeWidth(error_box_width);
                        }
                    } else {
                        email_outer.setError("email can't be empty");
                        email_outer.setBoxStrokeWidth(error_box_width);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password_outer.getError() != null) {
                    if (password.getText() != null) {
                        if (password.getText().length() > 0) {
                            if (password.getText().length() < 6) {
                                password_outer.setError("password is to short. length must be 6 character's");
                                password_outer.setBoxStrokeWidth(error_box_width);
                            } else {
                                String confirm_pass = String.valueOf(confirm_password.getText());
                                if (confirm_pass.trim().equals(s.toString().trim())) {
                                    confirm_password_outer.setError(null);
                                }
                                password_outer.setError(null);
                                password_outer.setBoxStrokeWidth(0);
                            }
                        } else {
                            password_outer.setError("password can't be empty.");
                            password_outer.setBoxStrokeWidth(error_box_width);
                        }
                    } else {
                        password_outer.setError("password is required.");
                        password_outer.setBoxStrokeWidth(error_box_width);
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
                if (confirm_password_outer.getError() != null) {
                    if (s.length() > 0) {
                        if (password.getText() != null) {
                            String real_password = password.getText().toString().trim();
                            if (real_password.equals(s.toString().trim())) {
                                confirm_password_outer.setError(null);
                                confirm_password_outer.setBoxStrokeWidth(0);
                            } else {
                                confirm_password_outer.setError("both password must be match.");
                                confirm_password_outer.setBoxStrokeWidth(error_box_width);
                            }
                        }
                    } else {
                        confirm_password_outer.setError("password can't be empty.");
                        confirm_password_outer.setBoxStrokeWidth(error_box_width);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void sendCreateAccountBroadcast() {
        if (email.getText() != null) {
            if (email.getText().length() > 0) {
//                verify email
                if (verifyEmail(email.getText().toString().trim())) {
//                    send link to verify email
                    email_outer.setError(null);
                    email_outer.setBoxStrokeWidth(0);
                } else {
                    email_outer.setError("invalid email id.");
                    email_outer.setBoxStrokeWidth(error_box_width);
                }
            } else {
                email_outer.setError("email can't be empty");
                email_outer.setBoxStrokeWidth(error_box_width);
            }
        } else {
            email_outer.setError("email is required.");
            email_outer.setBoxStrokeWidth(error_box_width);
        }

        if (password.getText() != null) {
            if (password.getText().length() > 0) {
                if (password.getText().length() < 6) {
                    password_outer.setError("password is to short. length must be 6 character's");
                    password_outer.setBoxStrokeWidth(error_box_width);
                } else {
                    String confirm_pass = String.valueOf(confirm_password.getText());
                    if (confirm_pass.trim().equals(password.getText().toString().trim())) {
                        confirm_password_outer.setError(null);
                    }
                    password_outer.setError(null);
                    password_outer.setBoxStrokeWidth(0);
                }
            } else {
                password_outer.setError("password can't be empty");
                password_outer.setBoxStrokeWidth(error_box_width);
            }
        } else {
            password_outer.setError("password is required.");
            password_outer.setBoxStrokeWidth(error_box_width);
        }

        if (confirm_password.getText() != null) {
            if (password.getText() != null) {
                String real_password = password.getText().toString().trim();
                if (real_password.equals(confirm_password.getText().toString().trim())) {
                    if (password_outer.getError() == null) {
                        confirm_password_outer.setError(null);
                        confirm_password_outer.setBoxStrokeWidth(0);
                    }
                } else {
                    confirm_password_outer.setError("both password must be match.");
                    confirm_password_outer.setBoxStrokeWidth(error_box_width);
                }
            } else {
                password_outer.setError("password is required.");
                password_outer.setBoxStrokeWidth(error_box_width);
            }
        } else {
            confirm_password_outer.setError("password is required.");
            confirm_password_outer.setBoxStrokeWidth(error_box_width);
        }

        if (username_outer.getError() == null && email_outer.getError() == null &&
                password_outer.getError() == null && confirm_password_outer.getError() == null) {
            createAccountBroadcast();
        } else {
            Toast.makeText(requireActivity(), "Please correct the detail's first", Toast.LENGTH_SHORT).show();
        }
    }

    private void createAccountBroadcast() {
        if (email.getText() != null && password.getText() != null) {
            if (!(email.getText().toString().isEmpty()) && !(password.getText().toString().isEmpty())) {
                Intent intent = new Intent();
                intent.setAction(RETURN_USER_CREATION_FIRST_PAGE_DONE);
                intent.putExtra(USER_USER_NAME, (user_name.getText() != null) ? user_name.getText().toString().trim() : null);
                intent.putExtra(USER_EMAIL, email.getText().toString().trim());
                intent.putExtra(USER_PASSWORD, password.getText().toString().trim());
                LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
            }
        }
    }

    private void verifyEmailWithLink(String email) {
        FirebaseAuth f_auth = FirebaseAuth.getInstance();
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                        .setUrl("https://www.sanket_satpute_20.com/checkout?cartId=1234")
                                .setHandleCodeInApp(true)
                                        .setAndroidPackageName("com.sanket_satpute_20.playme", true, "21")
                                                .setDynamicLinkDomain("coolapp.page.link").build();
        f_auth.sendSignInLinkToEmail(email, actionCodeSettings);
        Toast.makeText(requireActivity(), "Email is Send", Toast.LENGTH_SHORT).show();
    }

    private boolean verifyEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}