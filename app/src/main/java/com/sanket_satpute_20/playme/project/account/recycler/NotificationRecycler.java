package com.sanket_satpute_20.playme.project.account.recycler;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.TRANSACTION_STATUS_FAILED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.TRANSACTION_STATUS_SUCCESS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.activity.WalletActivity;
import com.sanket_satpute_20.playme.project.account.data.model.MTransactions;
import com.sanket_satpute_20.playme.project.account.extra_stuffes.SaveUserImageCache;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificationRecycler extends RecyclerView.Adapter<NotificationRecycler.NotificationHolder> {

    Context context;
    ArrayList<MTransactions> transactionsNotificationList;
    Bitmap user_image;
    int success_color = Color.GREEN;
    int failed_color = Color.RED;

    public static final String READ_MORE_STR = "read more.";
    public static final String READ_LESS_STR = "read less.";

    int maxLines = 3;

    public NotificationRecycler(Context context, ArrayList<MTransactions> transactionsNotificationList) {
        this.context = context;
        this.transactionsNotificationList = transactionsNotificationList;
        this.user_image = SaveUserImageCache.getImage(context, "user_profile_image");
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_notification_inbox_item, parent, false);
        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        String money_status = transactionsNotificationList.get(position).getTransaction_status();
        holder.user_name.setText(currentUser.getFull_name());
        holder.payment_status.setText(money_status);
        holder.msg.setText(transactionsNotificationList.get(position).getMsg());
        holder.starterColorCard.setCardBackgroundColor(generateRandomColor());

        if (user_image != null) {
            Glide.with(context)
                    .asBitmap()
                    .error(R.drawable.orange_man_user_profile_picture)
                    .load(user_image)
                    .into(holder.user_image);
        } else {
            holder.user_image.setImageResource(R.drawable.orange_man_user_profile_picture);
        }

        if (money_status.equals(TRANSACTION_STATUS_SUCCESS)) {
            holder.payment_status.setTextColor(success_color);
        } else if (money_status.equals(TRANSACTION_STATUS_FAILED)) {
            holder.payment_status.setTextColor(failed_color);
        }

        ViewTreeObserver treeObserver = holder.msg.getViewTreeObserver();
        treeObserver.addOnGlobalLayoutListener(() -> {
            int l_count1 = holder.msg.getLineCount();
            if (l_count1 > maxLines) {
                holder.read_more.setVisibility(View.VISIBLE);
            }
        });

        holder.read_more.setOnClickListener(view -> {
            if (holder.msg.getMaxLines() == maxLines) {
                holder.msg.setMaxLines(Integer.MAX_VALUE);
                holder.read_more.setText(READ_LESS_STR);
            } else {
                holder.msg.setMaxLines(maxLines);
                holder.read_more.setText(READ_MORE_STR);
            }
        });

        String full_date = transactionsNotificationList.get(position).getMsgDate() +
                " " +transactionsNotificationList.get(position).getMsgTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date oldDate = dateFormat.parse(full_date);
            System.out.println(oldDate);
            Date currentDate = new Date();
            if (oldDate != null) {
                long diff = currentDate.getTime() - oldDate.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                String time;
                if (days > 0) {
                    time = days + "d";
                    holder.time.setText(time);
                } else if (hours > 0) {
                    time = hours + "h";
                    holder.time.setText(time);
                } else if (minutes > 5) {
                    time = minutes + "m";
                    holder.time.setText(time);
                } else {
                    time = "just now";
                    holder.time.setText(time);
                }
            } else {    // null date
                holder.time.setText(transactionsNotificationList.get(position).getMsgDate());
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.time.setText(transactionsNotificationList.get(position).getMsgDate());
        }
    }

    @Override
    public int getItemCount() {
        return transactionsNotificationList.size();
    }

    public static class NotificationHolder extends RecyclerView.ViewHolder {

        MaterialCardView starterColorCard;
        ImageView user_image;
        TextView user_name, payment_status, msg, read_more, time;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
            starterColorCard = itemView.findViewById(R.id.card_starter);
            user_image = itemView.findViewById(R.id.user_profile_picture);
            msg = itemView.findViewById(R.id.information_inbox_msg_txt);
            time = itemView.findViewById(R.id.inbox_notification_time_txt);
            read_more = itemView.findViewById(R.id.read_more_txt);
            user_name = itemView.findViewById(R.id.user_name_txt);
            payment_status = itemView.findViewById(R.id.transfer_status_txt);
        }
    }

    private int generateRandomColor() {
        return ((int)(Math.random()*16777215)) | (0xFF << 24);
    }


}
