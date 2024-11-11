package com.sanket_satpute_20.playme.project.account.activity;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.data.model.MTransactionTime;
import com.sanket_satpute_20.playme.project.account.data.model.MTransactions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.divider.MaterialDivider;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TrackPaymentActivity extends AppCompatActivity {

    String processing_str = "Processing", pending_str = "Pending", success_str = "Success", failed_str = "Failed";
    String[] month_names = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    public static final String PARCELABLE_PAYMENT_SINGLE_OBJECT = "PARCELABLE_PAYMENT_SINGLE_OBJECT";

    int start_color = Color.parseColor("#ffa500");
    int end_color = Color.RED;
    int orange_color = start_color;
    int red_color = end_color;

    String payment_status;

    ImageView back_pressed, first_stage, second_stage, third_stage;
    MaterialDivider first_divider, second_divider;
    TextView order_id_txt, order_id, request_date, confirm_payment_txt, shipped_txt, delivered_txt, confirm_payment_date_txt,
    shipped_payment_date_txt, delivered_payment_date_txt, order_status_txt, order_status;
    MaterialCardView blur_stage_card_first, blur_stage_card_second, blur_stage_card_third;

    MTransactions mTransactions;
    ArrayList<MTransactionTime> mTransactionTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_payment);
        getDataMethod();
        initViews();
        onClick();
        starterMethod();
    }

    private void initViews() {
        back_pressed = findViewById(R.id.back_pressed);
        first_stage = findViewById(R.id.first_stage);
        second_stage = findViewById(R.id.second_stage);
        third_stage = findViewById(R.id.third_stage);
        first_divider = findViewById(R.id.first_divider);
        second_divider = findViewById(R.id.second_divider);
        order_id_txt = findViewById(R.id.order_id_txt);
        order_id = findViewById(R.id.order_id);
        request_date = findViewById(R.id.request_date);
        confirm_payment_txt = findViewById(R.id.first_stage_title);
        shipped_txt = findViewById(R.id.second_stage_title);
        delivered_txt = findViewById(R.id.third_stage_title);
        confirm_payment_date_txt = findViewById(R.id.first_stage_date);
        shipped_payment_date_txt = findViewById(R.id.second_stage_date);
        delivered_payment_date_txt = findViewById(R.id.third_stage_date);
        blur_stage_card_first = findViewById(R.id.first_stage_blur_card);
        blur_stage_card_second = findViewById(R.id.second_stage_blur_card);
        blur_stage_card_third = findViewById(R.id.third_stage_blur_card);
        order_status_txt = findViewById(R.id.order_status_txt);
        order_status = findViewById(R.id.order_status);
    }

    private void getDataMethod() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        } else {
            mTransactions = (MTransactions) intent.getSerializableExtra(PARCELABLE_PAYMENT_SINGLE_OBJECT);
            if (mTransactions != null)
                mTransactionTime = mTransactions.getTransaction_time_list();
            else
                finish();
        }
    }

    private void onClick() {
        back_pressed.setOnClickListener(view -> finish());
    }

    private void starterMethod() {
        if (mTransactionTime == null || mTransactions == null) {
            finish();
        } else {        // set data to the views
            String order_id_str = mTransactions.getTransaction_id();
            order_id.setText(order_id_str);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String date_str;
                DateTimeFormatter d_t_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate d = LocalDate.parse(mTransactions.getTransaction_date(), d_t_formatter);
                int doj_date = d.getDayOfMonth();
                Month doj_month = d.getMonth();
                int doj_year = d.getYear();
                date_str = doj_date + " " + month_names[doj_month.getValue() - 1] + " " + doj_year;
                request_date.setText(date_str);
            } else {
                request_date.setText(mTransactions.getTransaction_date());
            }

            payment_status = mTransactions.getTransaction_status();
            order_status.setText(payment_status);

            if (!(payment_status.equalsIgnoreCase(failed_str))) {   //          not fail
                //            setting stages
//            first stage
                String transaction_status_first_stage = mTransactionTime.get(0).getTransaction_status();
                if (transaction_status_first_stage.equalsIgnoreCase(failed_str)) {//                failed
                    first_stage.setImageResource(R.drawable.track_payment_circle_active);
                    first_stage.setImageTintList(ColorStateList.valueOf(red_color));
                    confirm_payment_date_txt.setText(failed_str);
                    confirm_payment_date_txt.setTextColor(red_color);
                } else if (transaction_status_first_stage.equalsIgnoreCase(pending_str)) {//        pending
                    first_stage.setImageResource(R.drawable.track_payment_circle_in_active);
                    first_stage.setImageTintList(ColorStateList.valueOf(orange_color));
                    confirm_payment_date_txt.setText(pending_str);
                } else if (transaction_status_first_stage.equalsIgnoreCase(processing_str)) {//     processing
                    first_stage.setImageResource(R.drawable.track_payment_circle_in_active);
                    first_stage.setImageTintList(ColorStateList.valueOf(orange_color));
                    confirm_payment_date_txt.setText(processing_str);
                } else {                                                                     //     success
                    first_stage.setImageResource(R.drawable.track_payment_circle_active);
                    first_stage.setImageTintList(ColorStateList.valueOf(orange_color));
                    DateTimeFormatter formatter;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate d = LocalDate.parse(mTransactionTime.get(0).getTransaction_status_date(), formatter);
                        String full_date = month_names[d.getMonthValue() - 1] + " " + d.getDayOfMonth() + "/" + (d.getYear() % 2000);
                        confirm_payment_date_txt.setText(full_date);
                    } else {
                        confirm_payment_date_txt.setText(currentUser.getAccountCreationDate());
                    }
                }

                //            second stage
                String transaction_status_second_stage = mTransactionTime.get(1).getTransaction_status();
                if (transaction_status_second_stage.equalsIgnoreCase(failed_str) ) {//                failed
                    second_stage.setImageResource(R.drawable.track_payment_circle_active);
                    second_stage.setImageTintList(ColorStateList.valueOf(red_color));
                    shipped_payment_date_txt.setText(failed_str);
                    shipped_payment_date_txt.setTextColor(red_color);
                } else if (transaction_status_second_stage.equalsIgnoreCase(pending_str)) {//        pending
                    second_stage.setImageResource(R.drawable.track_payment_circle_in_active);
                    second_stage.setImageTintList(ColorStateList.valueOf(orange_color));
                    shipped_payment_date_txt.setText(pending_str);
                } else if (transaction_status_second_stage.equalsIgnoreCase(processing_str)) {//     processing
                    second_stage.setImageResource(R.drawable.track_payment_circle_in_active);
                    second_stage.setImageTintList(ColorStateList.valueOf(orange_color));
                    shipped_payment_date_txt.setText(processing_str);
                } else {                                                                     //     success
                    second_stage.setImageResource(R.drawable.track_payment_circle_active);
                    second_stage.setImageTintList(ColorStateList.valueOf(orange_color));
                    DateTimeFormatter formatter;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate d = LocalDate.parse(mTransactionTime.get(1).getTransaction_status_date(), formatter);
                        String full_date = month_names[d.getMonthValue() - 1] + " " + d.getDayOfMonth() + "/" + (d.getYear() % 2000);
                        shipped_payment_date_txt.setText(full_date);
                    } else {
                        shipped_payment_date_txt.setText(currentUser.getAccountCreationDate());
                    }
                }

                //            third stage
                String transaction_status_third_stage = mTransactionTime.get(2).getTransaction_status();
                if (transaction_status_third_stage.equalsIgnoreCase(failed_str)) {//                failed
                    third_stage.setImageResource(R.drawable.track_payment_circle_active);
                    third_stage.setImageTintList(ColorStateList.valueOf(red_color));
                    delivered_payment_date_txt.setText(failed_str);
                    delivered_payment_date_txt.setTextColor(red_color);
                } else if (transaction_status_third_stage.equalsIgnoreCase(pending_str)) {//        pending
                    third_stage.setImageResource(R.drawable.track_payment_circle_in_active);
                    third_stage.setImageTintList(ColorStateList.valueOf(orange_color));
                    delivered_payment_date_txt.setText(pending_str);
                } else if (transaction_status_third_stage.equalsIgnoreCase(processing_str)) {//     processing
                    third_stage.setImageResource(R.drawable.track_payment_circle_in_active);
                    third_stage.setImageTintList(ColorStateList.valueOf(orange_color));
                    delivered_payment_date_txt.setText(processing_str);
                } else {                                                                     //     success
                    third_stage.setImageResource(R.drawable.track_payment_circle_active);
                    third_stage.setImageTintList(ColorStateList.valueOf(orange_color));
                    DateTimeFormatter formatter;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate d = LocalDate.parse(mTransactionTime.get(2).getTransaction_status_date(), formatter);
                        String full_date = month_names[d.getMonthValue() - 1] + " " + d.getDayOfMonth() + "/" + (d.getYear() % 2000);
                        delivered_payment_date_txt.setText(full_date);
                    } else {
                        delivered_payment_date_txt.setText(currentUser.getAccountCreationDate());
                    }
                }

//                animation for cards
                if (transaction_status_third_stage.equalsIgnoreCase(success_str))
                    thirdCardAnim();
                else if (transaction_status_second_stage.equalsIgnoreCase(success_str))
                    secondCardAnim();
                else
                    firstCardAnim();

            } else {                                                //          fail
                order_status.setTextColor(red_color);
                blur_stage_card_first.setCardBackgroundColor(red_color);
                blur_stage_card_second.setCardBackgroundColor(red_color);
                blur_stage_card_third.setCardBackgroundColor(red_color);
                confirm_payment_date_txt.setText(failed_str);
                shipped_payment_date_txt.setText(failed_str);
                delivered_payment_date_txt.setText(failed_str);
                first_stage.setImageResource(R.drawable.track_payment_circle_active);
                second_stage.setImageResource(R.drawable.track_payment_circle_active);
                third_stage.setImageResource(R.drawable.track_payment_circle_active);
                first_stage.setImageTintList(ColorStateList.valueOf(red_color));
                second_stage.setImageTintList(ColorStateList.valueOf(red_color));
                third_stage.setImageTintList(ColorStateList.valueOf(red_color));
                first_divider.setDividerColor(red_color);
                second_divider.setDividerColor(red_color);
                confirm_payment_date_txt.setTextColor(red_color);
                shipped_payment_date_txt.setTextColor(red_color);
                delivered_payment_date_txt.setTextColor(red_color);
                firstCardAnim();
                secondCardAnim();
                thirdCardAnim();
            }

        }
    }

    private void firstCardAnim() {
        ObjectAnimator inc_x = ObjectAnimator.ofFloat(blur_stage_card_first, "scaleX", 1f, 1.2f);
        ObjectAnimator inc_y = ObjectAnimator.ofFloat(blur_stage_card_first, "scaleY", 1f, 1.2f);
        AnimatorSet inc = new AnimatorSet();
        inc.playTogether(inc_x, inc_y);
        inc.setDuration(1000);
        inc.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator dec_x = ObjectAnimator.ofFloat(blur_stage_card_first, "scaleX", 1.2f, 1f);
                ObjectAnimator dec_y = ObjectAnimator.ofFloat(blur_stage_card_first, "scaleY", 1.2f, 1f);
                AnimatorSet dec = new AnimatorSet();
                dec.playTogether(dec_x, dec_y);
                dec.setDuration(1000);
                dec.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        firstCardAnim();
                    }
                });
                dec.start();
            }
        });
        inc.start();
    }

    private void secondCardAnim() {
        ObjectAnimator inc_x = ObjectAnimator.ofFloat(blur_stage_card_second, "scaleX", 1f, 1.2f);
        ObjectAnimator inc_y = ObjectAnimator.ofFloat(blur_stage_card_second, "scaleY", 1f, 1.2f);
        AnimatorSet inc = new AnimatorSet();
        inc.playTogether(inc_x, inc_y);
        inc.setDuration(1000);
        inc.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator dec_x = ObjectAnimator.ofFloat(blur_stage_card_second, "scaleX", 1.2f, 1f);
                ObjectAnimator dec_y = ObjectAnimator.ofFloat(blur_stage_card_second, "scaleY", 1.2f, 1f);
                AnimatorSet dec = new AnimatorSet();
                dec.playTogether(dec_x, dec_y);
                dec.setDuration(1000);
                dec.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        secondCardAnim();
                    }
                });
                dec.start();
            }
        });
        inc.start();
    }

    private void thirdCardAnim() {
        ObjectAnimator inc_x = ObjectAnimator.ofFloat(blur_stage_card_third, "scaleX", 1f, 1.2f);
        ObjectAnimator inc_y = ObjectAnimator.ofFloat(blur_stage_card_third, "scaleY", 1f, 1.2f);
        AnimatorSet inc = new AnimatorSet();
        inc.playTogether(inc_x, inc_y);
        inc.setDuration(1000);
        inc.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator dec_x = ObjectAnimator.ofFloat(blur_stage_card_third, "scaleX", 1.2f, 1f);
                ObjectAnimator dec_y = ObjectAnimator.ofFloat(blur_stage_card_third, "scaleY", 1.2f, 1f);
                AnimatorSet dec = new AnimatorSet();
                dec.playTogether(dec_x, dec_y);
                dec.setDuration(1000);
                dec.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        thirdCardAnim();
                    }
                });
                dec.start();
            }
        });
        inc.start();
    }
}