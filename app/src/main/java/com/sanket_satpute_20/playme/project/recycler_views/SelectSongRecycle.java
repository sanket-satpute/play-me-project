package com.sanket_satpute_20.playme.project.recycler_views;

import static com.sanket_satpute_20.playme.project.activity.SelectMultipleActivity.selected_files;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.model.MusicFiles;

import java.util.ArrayList;

public class SelectSongRecycle extends RecyclerView.Adapter<SelectSongRecycle.SelectSongHolder> {

    MaterialCheckBox check_all;
    Context context;
    public ArrayList<MusicFiles> selection_files;
    public SelectSongHolder holder;
    public static final String IS_LIST_FULL = "IS_LIST_FULL";

    public SelectSongRecycle (Context context, ArrayList<MusicFiles> selection_files, MaterialCheckBox check_all) {
        this.context = context;
        this.selection_files = selection_files;
        this.check_all = check_all;
    }

    @NonNull
    @Override
    public SelectSongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_song_select_item, parent, false);
        return new SelectSongHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectSongHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d("hay_1", "position : " + position);
        this.holder = holder;
        holder.song_name.setText(selection_files.get(position).getTitle());
        holder.artist_name.setText(selection_files.get(position).getArtist());
        holder.check.setChecked(selected_files.contains(selection_files.get(position)));

        holder.itemView.setOnClickListener(view -> {
            if (selected_files.contains(selection_files.get(position))) {
                selection_files.get(position).setIsSelected(false);
                selected_files.remove(selection_files.get(position));
                holder.check.setChecked(false);
            } else {
                selection_files.get(position).setIsSelected(true);
                selected_files.add(selection_files.get(position));
                holder.check.setChecked(true);
            }
            checkedClick();
        });

        holder.check.setOnClickListener(view -> {
            if (selected_files.contains(selection_files.get(position))) {
                selection_files.get(position).setIsSelected(false);
                selected_files.remove(selection_files.get(position));
                holder.check.setChecked(false);
            } else {
                selection_files.get(position).setIsSelected(true);
                selected_files.add(selection_files.get(position));
                holder.check.setChecked(true);
            }
            checkedClick();
        });
    }

    @Override
    public int getItemCount() {
        return selection_files.size();
    }

    public static class SelectSongHolder extends RecyclerView.ViewHolder {

        TextView song_name, artist_name;
        public MaterialCheckBox check;
        ImageView song_src;

        public SelectSongHolder(@NonNull View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.song_name);
            artist_name = itemView.findViewById(R.id.song_artist);
            check = itemView.findViewById(R.id.check);
            song_src = itemView.findViewById(R.id.song_src);
        }
    }

    private void checkedClick() {
        Intent intent = new Intent();
        intent.setAction("select_multiple.song.broadcast");
        intent.putExtra(IS_LIST_FULL, selected_files.size() > 0);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
