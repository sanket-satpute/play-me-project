package com.sanket_satpute_20.playme.project.recycler_views;

import static com.sanket_satpute_20.playme.project.service.BackService.most_played_songs;
import static com.sanket_satpute_20.playme.project.service.BackService.recentPlayed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.extra_stuffes.CacheImageManager;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.service.BackService;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

public class MostPlayedRecycle extends RecyclerView.Adapter<MostPlayedRecycle.MostPlayedHolder> {

    Context context;
    ArrayList<MusicFiles> recent_f;
    BackService service = new BackService();
    boolean isMostPlayedNotRecent;

    Bitmap bitmap;

    public MostPlayedRecycle(Context context, ArrayList<MusicFiles> recent_f, boolean isMostPlayedNotRecent) {
        this.context = context;
        this.recent_f = recent_f;
        this.isMostPlayedNotRecent = isMostPlayedNotRecent;
    }

    @NonNull
    @Override
    public MostPlayedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_song_recent_played_card_item, parent, false);
        return new MostPlayedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MostPlayedHolder holder, int position) {
        if (new File(recent_f.get(position).getPath()).exists()) {
            holder.song_name.setText(recent_f.get(position).getTitle());

            holder.play_fab.setOnClickListener(view -> startSong(position));
            holder.itemView.setOnClickListener(view -> startSong(position));

            try {
                Bitmap bitmap = CacheImageManager.getImage(context, recent_f.get(position));
                if (bitmap != null) {
                    Glide.with(context)
                            .load(bitmap)
                            .placeholder(R.mipmap.ic_music)
                            .override(200, 200)
                            .into(holder.song_src);

                    Palette.from(bitmap).generate(palette -> {
                        if (palette != null) {
                            Palette.Swatch swatch = palette.getDominantSwatch();
                            if (swatch != null) {
                                GradientDrawable gradient = new GradientDrawable(
                                        GradientDrawable.Orientation.RIGHT_LEFT,
                                        new int[]{0x00616261, swatch.getRgb()}
                                );
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    holder.song_src.setForeground(gradient);
                                }
                                holder.cast_layout.setBackground(new ColorDrawable(swatch.getRgb()));

                                holder.song_name.setTextColor(swatch.getBodyTextColor());
                            }
                        }
                    });
                } else {
                    MyImageLoad load = new MyImageLoad();
                    load.setViewHolder(holder);
                    load.execute(recent_f.get(position));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            boolean isPresent = false;
            if (isMostPlayedNotRecent) {
                if (recent_f.size() == most_played_songs.size())
                    isPresent = most_played_songs.get(position).getPath().trim().equals(recent_f.get(position).getPath());
            } else {
                if (recent_f.size() == recentPlayed.size())
                    isPresent = recentPlayed.get(position).getPath().trim().equals(recent_f.get(position).getPath());
            }

            Intent broad_item_is_removing = new Intent();
            broad_item_is_removing.setAction("action_from_most_played_or_recent_recycle.ITEM_IS_NOT_PRESENT_IN_STORAGE.Removed");
            broad_item_is_removing.putExtra("REMOVED_ITEM_POSITION", position);
            broad_item_is_removing.putExtra("IN_PARENT_LIST_ITEM_PRESENT", isPresent);
            broad_item_is_removing.putExtra("IT_IS_MOST_PLAYED_NOT_RECENT", isMostPlayedNotRecent);
            LocalBroadcastManager.getInstance(context).sendBroadcast(broad_item_is_removing);

            recent_f.remove(position);
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(recent_f.size(), 7);
    }

    public static class MostPlayedHolder extends RecyclerView.ViewHolder {

        TextView song_name;
        ImageView song_src;
        FloatingActionButton play_fab;
        MaterialCardView main_card;
        ConstraintLayout cast_layout;

        public MostPlayedHolder(@NonNull View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.song_name);
            song_src = itemView.findViewById(R.id.song_src);
            play_fab = itemView.findViewById(R.id.play_fab_btn);
            main_card = itemView.findViewById(R.id.main_card);
            cast_layout = itemView.findViewById(R.id.cast_layout);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
        byte[] album_pic;
        private MostPlayedHolder holder;
        MediaMetadataRetriever retriever;
        String path;
        MusicFiles fileIO;
        public void setViewHolder(MostPlayedHolder holder)
        {
            this.holder = holder;
        }

        @Override
        protected byte[] doInBackground(MusicFiles... musicFiles) {
            try {
                fileIO = musicFiles[0];
                retriever = new MediaMetadataRetriever();
                path = fileIO.getPath();
                retriever.setDataSource(path);
                album_pic = retriever.getEmbeddedPicture();
                retriever.release();
                return album_pic;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            try {
                if (bytes != null)
                    CacheImageManager.putImage(context, fileIO, BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                Glide.with(context).asBitmap()
                        .load(bytes)
                        .override(200, 200)
                        .placeholder(R.mipmap.ic_music)
                        .into(holder.song_src);
                if (bytes != null) {
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Palette.from(bitmap).generate(palette -> {
                        if (palette != null) {
                            Palette.Swatch swatch = palette.getDominantSwatch();
                            if (swatch != null) {
                                GradientDrawable gradient = new GradientDrawable(
                                        GradientDrawable.Orientation.RIGHT_LEFT,
                                        new int[] {0x00616261, swatch.getRgb()}
                                );
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    holder.song_src.setForeground(gradient);
                                }
                                holder.cast_layout.setBackground(new ColorDrawable(swatch.getRgb()));

                                holder.song_name.setTextColor(swatch.getBodyTextColor());
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*    Methods   */
    private void startSong(int position) {
        service.setSongSource(recent_f);
        Intent intent = new Intent(context, BackService.class);
        intent.putExtra("position", position);
        context.startService(intent);
    }
}
