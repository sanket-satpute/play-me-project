package com.sanket_satpute_20.playme.project.recycler_views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;

public class ChangeLogChildRecycle extends RecyclerView.Adapter<ChangeLogChildRecycle.ChangeLogChildHolder> {

    String[] future_list;
    Context context;

    public ChangeLogChildRecycle(String[] future_list, Context context) {
        this.future_list = future_list;
        this.context = context;
    }

    @NonNull
    @Override
    public ChangeLogChildHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_change_log_child_data_item, parent, false);
        return new ChangeLogChildHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChangeLogChildHolder holder, int position) {
        holder.future_text.setText(future_list[position]);
    }

    @Override
    public int getItemCount() {
        return future_list.length;
    }

    public static class ChangeLogChildHolder extends RecyclerView.ViewHolder {

        TextView future_text;

        public ChangeLogChildHolder(@NonNull View itemView) {
            super(itemView);
            future_text = itemView.findViewById(R.id.future_name);
        }
    }
}
