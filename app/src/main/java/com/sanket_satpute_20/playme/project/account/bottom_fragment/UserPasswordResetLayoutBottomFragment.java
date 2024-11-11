package com.sanket_satpute_20.playme.project.account.bottom_fragment;

import static com.sanket_satpute_20.playme.project.account.activity.ResetPasswordActivity.EMAIL_ADDRESS;
import static com.sanket_satpute_20.playme.project.account.activity.ResetPasswordActivity.EMAIL_NOT_SEND_ERROR;
import static com.sanket_satpute_20.playme.project.account.activity.ResetPasswordActivity.EMAIL_SEND_ACTION_COMPLETE;
import static com.sanket_satpute_20.playme.project.account.activity.ResetPasswordActivity.IS_EMAIL_SEND;
import static com.sanket_satpute_20.playme.project.account.activity.ResetPasswordActivity.PASSWORD_RESET_BY_EMAIL_LINK;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sanket_satpute_20.playme.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UserPasswordResetLayoutBottomFragment extends BottomSheetDialogFragment {

    FloatingActionButton reset_password_fab;
    TextView send_email_link_txt, send_or_not_status_txt, send_or_not_helper_txt;
    ProgressBar email_sending_progress;

    boolean isEmailSending = false;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(EMAIL_SEND_ACTION_COMPLETE)) {
                    boolean isEmailSend = intent.getBooleanExtra(IS_EMAIL_SEND, false);
                    if (isEmailSend) {
                        String emailAddress = intent.getStringExtra(EMAIL_ADDRESS);
                        String strStatus = "Email Link Send";
                        send_or_not_status_txt.setText(strStatus);
                        send_or_not_status_txt.setTextColor(Color.GREEN);
                        String strMsg = "password reset link send to your " + emailAddress + " email. check inbox";
                        send_or_not_helper_txt.setText(strMsg);
                        emailSendEndAnim();
                    } else {
                        String errorEmailMsg = intent.getStringExtra(EMAIL_NOT_SEND_ERROR);
                        String str = "Email Link Not Send";
                        send_or_not_status_txt.setText(str);
                        send_or_not_status_txt.setTextColor(Color.RED);
                        send_or_not_helper_txt.setText(errorEmailMsg);
                        emailSendEndAnim();
                    }
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog);
        View view = inflater.inflate(R.layout.fragment_user_password_reset_layout_bottom, container, false);
        isEmailSending = false;
        initViews(view);
        onClick();
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialog);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(EMAIL_SEND_ACTION_COMPLETE);
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver);
    }

    private void initViews(View view) {
        reset_password_fab = view.findViewById(R.id.send_password_reset_email_link_fab);
        send_email_link_txt = view.findViewById(R.id.send_email_link_txt);
        send_or_not_status_txt = view.findViewById(R.id.send_email_link_status_txt);
        send_or_not_helper_txt = view.findViewById(R.id.send_email_link_status_helper_txt);
        email_sending_progress = view.findViewById(R.id.send_reset_password_email_progress);
    }

    private void onClick() {
        reset_password_fab.setOnClickListener(view -> {
            if (!isEmailSending) {
                starterAnimate();
                isEmailSending = true;
            }
        });
    }

//    animation
    private void starterAnimate() {
        ObjectAnimator sendEmailLinkAlpha = ObjectAnimator.ofFloat(send_email_link_txt, "alpha", 1f, 0f);
        ObjectAnimator sendEmailLinkTranslate = ObjectAnimator.ofFloat(send_email_link_txt, "translationY", 0f, -10f);
        AnimatorSet sendEmailLinkAnim = new AnimatorSet();
        sendEmailLinkAnim.playTogether(sendEmailLinkAlpha, sendEmailLinkTranslate);
        sendEmailLinkAnim.setDuration(250);
        sendEmailLinkAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                email_sending_progress.setAlpha(0f);
                email_sending_progress.setVisibility(View.VISIBLE);

                ObjectAnimator progressAlpha = ObjectAnimator.ofFloat(email_sending_progress, "alpha", 0f, 1f);
                progressAlpha.setDuration(300);
                progressAlpha.start();

                send_or_not_status_txt.setAlpha(0);
                send_or_not_status_txt.setTranslationX(-30);
                send_or_not_status_txt.setVisibility(View.VISIBLE);

                String emailSendingStr = "Email Sending...";
                send_or_not_status_txt.setText(emailSendingStr);
                ObjectAnimator statusTranslationX = ObjectAnimator.ofFloat(send_or_not_status_txt, "translationX", -30, 0);
                ObjectAnimator statusAlpha = ObjectAnimator.ofFloat(send_or_not_status_txt, "alpha", 0, 1f);
                AnimatorSet statusAnim = new AnimatorSet();
                statusAnim.playTogether(statusAlpha, statusTranslationX);
                statusAnim.setDuration(300);
                statusAnim.start();

                Intent intent = new Intent();
                intent.setAction(PASSWORD_RESET_BY_EMAIL_LINK);
                LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
            }
        });
        sendEmailLinkAnim.start();
    }

    private void emailSendEndAnim() {
        send_or_not_helper_txt.setAlpha(0f);
        send_or_not_helper_txt.setTranslationY(-30f);
        send_or_not_helper_txt.setVisibility(View.VISIBLE);

        ObjectAnimator helperAlpha = ObjectAnimator.ofFloat(send_or_not_helper_txt, "alpha", 0f, 0.7f);
        ObjectAnimator helperTranslateY = ObjectAnimator.ofFloat(send_or_not_helper_txt, "translationY", -30, 0f);
        AnimatorSet helperAnim = new AnimatorSet();
        helperAnim.playTogether(helperAlpha, helperTranslateY);
        helperAnim.setDuration(400);
        helperAnim.start();

        ObjectAnimator progressAlpha = ObjectAnimator.ofFloat(email_sending_progress, "alpha", 1f, 0f);
        progressAlpha.setDuration(250);
        progressAlpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                email_sending_progress.setVisibility(View.INVISIBLE);
                ObjectAnimator sendEmailLinkAlpha = ObjectAnimator.ofFloat(send_email_link_txt, "alpha", 0f, 1f);
                ObjectAnimator sendEmailLinkTranslateY = ObjectAnimator.ofFloat(send_email_link_txt, "translationY", send_email_link_txt.getTranslationY(), 0f);
                AnimatorSet sendEmailLinkAnim = new AnimatorSet();
                sendEmailLinkAnim.playTogether(sendEmailLinkAlpha, sendEmailLinkTranslateY);
                sendEmailLinkAnim.setDuration(400);
                sendEmailLinkAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        new Handler().postDelayed(UserPasswordResetLayoutBottomFragment.this::dismiss, 10000);
                    }
                });
                sendEmailLinkAnim.start();
            }
        });
        progressAlpha.start();
    }

}
