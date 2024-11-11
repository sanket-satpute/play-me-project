package com.sanket_satpute_20.playme.project.account.recycler;

import static com.sanket_satpute_20.playme.project.account.activity.TrackPaymentActivity.PARCELABLE_PAYMENT_SINGLE_OBJECT;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.TRANSACTION_STATUS_FAILED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.TRANSACTION_STATUS_IN_PROGRESS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.TRANSACTION_STATUS_PENDING;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.TRANSACTION_STATUS_SUCCESS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.activity.TrackPaymentActivity;
import com.sanket_satpute_20.playme.project.account.data.model.MTransactions;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Random;

public class TransactionRecycler extends RecyclerView.Adapter<TransactionRecycler.TransactionHolder> {

    ArrayList<MTransactions> transactions;
    Context context;
    String full_name, date_and_time, status, plus = "+ ", minus = "- ", rupee, sign;
    boolean isBlack;
    boolean isDay;

    int black_color = Color.parseColor("#7a7a7a");
    int white_color = Color.parseColor("#cacaca");

    public TransactionRecycler(Context context, ArrayList<MTransactions> transactions, boolean isBlack) {
        this.context = context;
        this.transactions = transactions;
        this.isBlack = isBlack;
        full_name = currentUser.getFull_name();
        rupee = context.getResources().getString(R.string.rupee);
        switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                isDay = false;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                isDay = true;
                break;
        }
    }

    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_transaction_view_item, parent, false);
        return new TransactionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHolder holder, int position) {
//        does not set the user profile picture
        sign = (transactions.get(position).getTransaction_status().equalsIgnoreCase(TRANSACTION_STATUS_FAILED) ||
                transactions.get(position).getTransaction_status().equalsIgnoreCase("Fail"))
                ? minus : plus;
        if (full_name == null)
            full_name = currentUser.getFull_name();
        if (rupee == null)
            rupee = context.getResources().getString(R.string.rupee);
        String trans_money = sign + rupee + transactions.get(position).getTransaction_money();
        holder.username.setText(full_name);
        date_and_time = transactions.get(position).getTransaction_date() + " | " + transactions.get(position).getTransaction_time().substring(0, 5);
        holder.money.setText(trans_money);
        status = transactions.get(position).getTransaction_status();
        holder.date_and_time.setText(date_and_time);

        if (isBlack || isDay) {  // color all black
            holder.money.setTextColor(black_color);
            holder.username.setTextColor(black_color);
            holder.date_and_time.setTextColor(black_color);
        } else {        // color all white
            holder.money.setTextColor(white_color);
            holder.username.setTextColor(white_color);
            holder.date_and_time.setTextColor(white_color);
        }

        holder.progress.setMax(3);
        if (status.trim().equalsIgnoreCase(TRANSACTION_STATUS_PENDING)) {
            holder.progress.setProgress(1);
            holder.progress.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
            if (randomCount() % 2 == 0) {
                holder.payment_picture.setImageResource(R.drawable.growth_incoming_money_user_paid_1);
            } else {
                holder.payment_picture.setImageResource(R.drawable.profits_incoming_money_user_paid_2);
            }
        } else if (status.trim().equalsIgnoreCase(TRANSACTION_STATUS_IN_PROGRESS)) {
            holder.progress.setProgress(2);
            holder.progress.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
            if (randomCount() % 2 == 0) {
                holder.payment_picture.setImageResource(R.drawable.growth_incoming_money_user_paid_1);
            } else {
                holder.payment_picture.setImageResource(R.drawable.profits_incoming_money_user_paid_2);
            }
        } else if (status.trim().equalsIgnoreCase(TRANSACTION_STATUS_SUCCESS)) {      //  success
            holder.progress.setProgress(3);
            holder.progress.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
            holder.money.setTextColor(Color.GREEN);
            if (randomCount() % 2 == 0) {
                holder.payment_picture.setImageResource(R.drawable.growth_incoming_money_user_paid_1);
            } else {
                holder.payment_picture.setImageResource(R.drawable.profits_incoming_money_user_paid_2);
            }
        } else {        // fail
            holder.progress.setProgress(3);
            holder.progress.setProgressTintList(ColorStateList.valueOf(Color.RED));
            holder.money.setTextColor(Color.RED);
            holder.payment_picture.setImageResource(R.drawable.incoming_money_user_not_paid);
        }

        holder.profile_card.setCardBackgroundColor(generateRandomColor());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, TrackPaymentActivity.class);
            intent.putExtra(PARCELABLE_PAYMENT_SINGLE_OBJECT, transactions.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class TransactionHolder extends RecyclerView.ViewHolder {

        ImageView payment_picture;
        TextView username, date_and_time, money;
        ProgressBar progress;
        MaterialCardView profile_card;
        public TransactionHolder(@NonNull View itemView) {
            super(itemView);
            payment_picture = itemView.findViewById(R.id.user_profile_picture);
            username = itemView.findViewById(R.id.user_name);
            date_and_time = itemView.findViewById(R.id.transaction_date);
            money = itemView.findViewById(R.id.transaction_money);
            progress = itemView.findViewById(R.id.payment_status_progress);
            profile_card = itemView.findViewById(R.id.user_profile_picture_card);
        }
    }

    private int randomCount() {
        Random random = new Random();
        return random.nextInt();
    }

    private int generateRandomColor() {
        return ((int)(Math.random()*16777215)) | (0xFF << 24);
    }
}
