package com.sanket_satpute_20.playme.project.recycler_views;

import static com.sanket_satpute_20.playme.MainActivity.favouritePlaylists;
import static com.sanket_satpute_20.playme.project.activity.InnerPlaylistActivity.FORWHAT;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.activity.AddToPlaylistActivity;
import com.sanket_satpute_20.playme.project.activity.InnerPlaylistActivity;
import com.sanket_satpute_20.playme.project.model.Playlist;
import com.sanket_satpute_20.playme.project.service.BackService;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class PlaylistRecycle extends RecyclerView.Adapter<PlaylistRecycle.PlaylistHolder> {

    public static final String PLAYLISTNAME = "PLAYLISTNAME";
    Context context;
    ArrayList<Playlist> playlists;
    BackService service = new BackService();

    public static final String PLAYLISTPOSITION = "PLAYLISTPOSITION";

    public PlaylistRecycle(Context context, ArrayList<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public PlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_playlist_item, parent, false);
        return new PlaylistHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull PlaylistHolder holder, int position) {

        holder.checkBox.setVisibility(View.GONE);
        holder.playlist_option.setVisibility(View.VISIBLE);

        holder.playlist_name.setText(playlists.get(position).playlist_name);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, InnerPlaylistActivity.class);
            intent.putExtra(PLAYLISTPOSITION, position);
            intent.putExtra(PLAYLISTNAME, playlists.get(position).playlist_name);
            context.startActivity(intent);
        });

        holder.playlist_option.setOnClickListener(view -> {
            View v = LayoutInflater.from(context).inflate(R.layout.single_playlist_card_item, null);
            RelativeLayout details, delete, play_all, add_remove_fev;
            TextView is_fev_playlist, playlist_name;
            details = v.findViewById(R.id.details);
            delete = v.findViewById(R.id.delete);
            play_all = v.findViewById(R.id.play_all);
            add_remove_fev = v.findViewById(R.id.add_to_fevourite);
            is_fev_playlist = v.findViewById(R.id.is_fev_playlist);
            playlist_name = v.findViewById(R.id.playlist_name);

            MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context)
                    .setView(v)
                    .setCancelable(true);
            AlertDialog dilog = alert.create();
            if (dilog.getWindow() != null)
                dilog.getWindow().getAttributes().windowAnimations = R.style.UpDownDialogAnimation;
            dilog.show();

            playlist_name.setText(playlists.get(position).playlist_name);

            if (favouritePlaylists.contains(playlists.get(position))) {
                is_fev_playlist.setText("Remove from Favourite");
            } else {
                is_fev_playlist.setText("Add to Favourite");
            }

            details.setOnClickListener(l -> {
                dilog.dismiss();
                TextView name, creator, created_on, totle;
                Button edit_details, done, ok;
                EditText edit_name, edit_creator;
                ConstraintLayout edit_layout, details_layout;
                @SuppressLint("InflateParams") View s = LayoutInflater.from(context).inflate(R.layout.playlist_details_dilog, null);
                MaterialAlertDialogBuilder detail_alert = new MaterialAlertDialogBuilder(context);
                        detail_alert.setView(s);
                AlertDialog dilog_details = detail_alert.create();
                dilog_details.show();

                name = s.findViewById(R.id.name);
                creator = s.findViewById(R.id.creator);
                created_on = s.findViewById(R.id.time);
                totle = s.findViewById(R.id.totle);
                edit_details = s.findViewById(R.id.edit_details);
                edit_layout = s.findViewById(R.id.layout_edit);
                details_layout = s.findViewById(R.id.layout_detail);
                done = s.findViewById(R.id.done);
                edit_name = s.findViewById(R.id.get_name);
                edit_creator = s.findViewById(R.id.get_creator);
                ok = s.findViewById(R.id.ok);

                ok.setTextColor(ACCENT_COLOR);
                done.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));
                edit_details.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));


                name.setText(playlists.get(position).getPlaylist_name());
                creator.setText(playlists.get(position).getCreatedBy());
                created_on.setText(playlists.get(position).getCreatedOn());
                totle.setText(playlists.get(position).getPlaylist().size() + " Song's");

                edit_details.setOnClickListener(m -> {
                    details_layout.setVisibility(View.GONE);
                    edit_layout.setVisibility(View.VISIBLE);
                    edit_name.setText(playlists.get(position).getPlaylist_name());
                    edit_creator.setText(playlists.get(position).getCreatedBy());
                });

                ok.setOnClickListener(m -> dilog_details.dismiss());

                done.setOnClickListener(m -> {
                    String e_name = edit_name.getText().toString();
                    String e_creator = edit_creator.getText().toString();
                    if (!(e_name.isEmpty()) && !(e_creator.isEmpty())) {
                        playlists.get(position).setPlaylist_name(e_name);
                        playlists.get(position).setCreatedBy(e_creator);
                        details_layout.setVisibility(View.VISIBLE);
                        edit_layout.setVisibility(View.GONE);
                        name.setText(playlists.get(position).getPlaylist_name());
                        creator.setText(playlists.get(position).getCreatedBy());
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Can Not Be Empty", Toast.LENGTH_SHORT).show();
                    }
                });
            });


            add_remove_fev.setOnClickListener(m -> {
                if (favouritePlaylists.contains(playlists.get(position))) {
                    favouritePlaylists.remove(playlists.get(position));
                    is_fev_playlist.setText("Add to Fevourite");
                } else {
                    favouritePlaylists.add(playlists.get(position));
                    is_fev_playlist.setText("Remove from Fevourite");
                }
            });

            delete.setOnClickListener(l -> {
                playlists.remove(position);
                notifyDataSetChanged();
                dilog.dismiss();
            });

            play_all.setOnClickListener(l -> {
                if (playlists.get(position).getPlaylist().size() > 0) {
                    service.setSongSource(playlists.get(position).getPlaylist());

                    Intent intent = new Intent(context, BackService.class);
                    intent.putExtra("position", position);
                    context.startService(intent);
                } else {
                    Snackbar snackbar = Snackbar.make(view, "No Song's Available", BaseTransientBottomBar.LENGTH_SHORT);
                    snackbar.setAction("ADD", view1 -> {
                        Intent intent = new Intent(context, AddToPlaylistActivity.class);
                        intent.putExtra(FORWHAT, "add");
                        intent.putExtra(PLAYLISTPOSITION, position);
                        context.startActivity(intent);
                        snackbar.dismiss();
                    });
                    snackbar.show();
                }
                dilog.dismiss();
            });
        });

    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class PlaylistHolder extends RecyclerView.ViewHolder {

        ImageView playlist_option;
        TextView playlist_name;
        MaterialCheckBox checkBox;

        public PlaylistHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.playlist_check_box);
            playlist_name = itemView.findViewById(R.id.playlist_name);
            playlist_option = itemView.findViewById(R.id.playlist_option);
        }
    }
}
