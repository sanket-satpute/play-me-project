package com.sanket_satpute_20.playme.project.recycler_views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.model.Playlist;

import java.util.ArrayList;

public class PlaylistRecycleCreatePlaylist extends RecyclerView.Adapter<PlaylistRecycleCreatePlaylist.PlaylistHolderCreatePlaylist> {

    Context context;
    ArrayList<Playlist> playlists_array;
    int[] selected_item_set = null;
    ArrayList<MusicFiles> music_files = new ArrayList<>();
    MusicFiles music;

    boolean is_list = false;

    public PlaylistRecycleCreatePlaylist(Context context, ArrayList<Playlist> playlists_array, MusicFiles music) {
        this.context = context;
        this.playlists_array = playlists_array;
        this.music = music;
        selected_item_set = new int[playlists_array.size()];
        for (int i: selected_item_set) {
            selected_item_set[i] = 0;
        }
    }

    public PlaylistRecycleCreatePlaylist(Context context, ArrayList<Playlist> playlists_array, ArrayList<MusicFiles> music_files) {
        this.context = context;
        this.playlists_array = playlists_array;
        this.music_files = music_files;
        this.is_list = true;
        selected_item_set = new int[playlists_array.size()];
        for (int i: selected_item_set) {
            selected_item_set[i] = 0;
        }
    }

    @NonNull
    @Override
    public PlaylistHolderCreatePlaylist onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_playlist_item, parent, false);
        return new PlaylistHolderCreatePlaylist(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistHolderCreatePlaylist holder, int position) {
        holder.option.setVisibility(View.GONE);
        holder.checkBox.setVisibility(View.VISIBLE);

        holder.playlist_name.setText(playlists_array.get(position).getPlaylist_name());

        holder.itemView.setOnClickListener(view -> selectPlaylists(position, holder));
        holder.checkBox.setOnClickListener(view -> selectPlaylists(position, holder));

    }

    private void selectPlaylists(int position, PlaylistHolderCreatePlaylist holder) {
        if (selected_item_set[position] == 1) {
            selected_item_set[position] = 0;
        }
        else {
            selected_item_set[position] = 1;
        }
        holder.checkBox.setChecked(selected_item_set[position] != 0);
    }

    @Override
    public int getItemCount() {
        return playlists_array.size();
    }

    public int[] returnSelectedList() {
        return selected_item_set;
    }

    public static class PlaylistHolderCreatePlaylist extends RecyclerView.ViewHolder {

        MaterialCheckBox checkBox;
        ImageView option;
        TextView playlist_name;

        public PlaylistHolderCreatePlaylist(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.playlist_check_box);
            option = itemView.findViewById(R.id.playlist_option);
            playlist_name = itemView.findViewById(R.id.playlist_name);
        }
    }
}
