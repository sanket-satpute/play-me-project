package com.sanket_satpute_20.playme.project.recycler_views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.model.ChangeLogApp;

import java.util.ArrayList;

public class ChangeLogParentRecycle extends RecyclerView.Adapter<ChangeLogParentRecycle.ChangeLogParentHolder> {

    Context context;
    ArrayList<ChangeLogApp> change_log_data;

    public ChangeLogParentRecycle(Context context, ArrayList<ChangeLogApp> change_log_data) {
        this.context = context;
        this.change_log_data = change_log_data;
    }

    @NonNull
    @Override
    public ChangeLogParentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_change_log_parent_recycler_view_item, parent, false);
        return new ChangeLogParentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChangeLogParentHolder holder, int position) {
        holder.date_txt.setText(change_log_data.get(position).getDate());
        holder.version_name_txt.setText(change_log_data.get(position).getVersion_name());

        if (change_log_data.get(position).getChange_log_futures().length > 0) {
            ChangeLogChildRecycle adapter = new ChangeLogChildRecycle(change_log_data.get(position).getChange_log_futures(), context);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            holder.recyclerView.setAdapter(adapter);
            holder.recyclerView.setLayoutManager(layoutManager);
        }
    }

    @Override
    public int getItemCount() {
        return change_log_data.size();
    }

    public static class ChangeLogParentHolder extends RecyclerView.ViewHolder {

        TextView date_txt, version_name_txt;
        RecyclerView recyclerView;
        public ChangeLogParentHolder(@NonNull View itemView) {
            super(itemView);
            date_txt = itemView.findViewById(R.id.date_txt);
            version_name_txt = itemView.findViewById(R.id.version_name);
            recyclerView = itemView.findViewById(R.id.recycler_view);
        }
    }
}
