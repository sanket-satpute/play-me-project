package com.sanket_satpute_20.playme.project.recycler_views;

import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.IMAGEOFSONG;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.PASSEDSONGFILE;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BSheetSongsMoreFragment;
import com.sanket_satpute_20.playme.project.extra_stuffes.CacheImageManager;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DilogRecycle extends RecyclerView.Adapter<DilogRecycle.DilogHolder> {

    Context context;
    ArrayList<MusicFiles> dilog_flies;
    SharedPreferences preferences;
    public static final String POSITION = "POSITION";
    FragmentManager supportManager;
    int position_play;
    ColorStateList color;


    public DilogRecycle(Context context, ArrayList<MusicFiles> dilog_flies, FragmentManager supportManager) {
        this.context = context;
        this.dilog_flies = dilog_flies;
        this.supportManager = supportManager;
        preferences = context.getSharedPreferences("PLAYING", Context.MODE_PRIVATE);
        position_play = preferences.getInt(SONGPOSITION, 0);
    }

    @NonNull
    @Override
    public DilogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_dilog_song_item, parent, false);
        return new DilogHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull DilogHolder holder, int position) {
        holder.itemView.setScaleX(0.1f);
        holder.itemView.setScaleY(0.1f);
        holder.itemView.setTranslationY(1000f);
        holder.song_name.setText(dilog_flies.get(position).getTitle());
        holder.song_artist.setText(dilog_flies.get(position).getArtist());

        color = holder.song_name.getTextColors();

        if (position == position_play) {
            holder.imgIsPlaying.setVisibility(View.VISIBLE);
            holder.song_name.setTextColor(ACCENT_COLOR);
            holder.song_artist.setTextColor(ACCENT_COLOR);
        } else {
            holder.imgIsPlaying.setVisibility(View.GONE);
            holder.song_name.setTextColor(color);
            holder.song_artist.setTextColor(color);
        }

        try {
            Bitmap bitmap = CacheImageManager.getImage(context, dilog_flies.get(position));
            if (bitmap == null) {
                MyImageLoad load = new MyImageLoad();
                load.setViewHolder(holder);
                load.execute(dilog_flies.get(position));
            } else {
                Glide.with(context)
                        .load(bitmap)
                        .override(200, 200)
                        .placeholder(R.mipmap.ic_music)
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .into(holder.song_art);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.play_fab.setOnClickListener(view -> startSong(position));

        holder.itemView.animate().scaleX(1f).scaleY(1f).translationY(0).setDuration(350).start();
        holder.itemView.setOnClickListener(view -> startSong(position));

        holder.options.setOnClickListener(view -> {
            optionMenuClicked(position);
        });
    }

    @Override
    public int getItemCount() {
        return dilog_flies.size();
    }

    public static class DilogHolder extends RecyclerView.ViewHolder {

        ImageView song_art, options, imgIsPlaying;
        TextView song_name, song_artist;
        FloatingActionButton play_fab;

        public DilogHolder(@NonNull View itemView) {
            super(itemView);
            song_art = itemView.findViewById(R.id.alert_box_image);
            options = itemView.findViewById(R.id.alert_option);
            song_name = itemView.findViewById(R.id.alert_song_name);
            song_artist = itemView.findViewById(R.id.alert_song_artist);
            imgIsPlaying = itemView.findViewById(R.id.img_is_playing);
            play_fab = itemView.findViewById(R.id.play_fab);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        private DilogHolder holder;
        MediaMetadataRetriever retriver;
        public void setViewHolder(DilogHolder holder)
        {
            this.holder = holder;
        }

        @Override
        protected byte[] doInBackground(MusicFiles... musicFiles) {
            try {
                fileIO = musicFiles[0];
                path = fileIO.getPath();
                retriver = new MediaMetadataRetriever();
                retriver.setDataSource(path);
                album_pic = retriver.getEmbeddedPicture();
                retriver.release();
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
                Glide.with(context)
                        .load(bytes)
                        .override(200, 200)
                        .placeholder(R.mipmap.ic_music)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.song_art);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startSong(int position) {
        Intent intent = new Intent();
        intent.setAction("song_changed_dilog_recycler");
        intent.putExtra(POSITION, position);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void optionMenuClicked (int position) {
        byte[] byteArray = null;

        try {
            Bitmap bmp = CacheImageManager.getImage(context, dilog_flies.get(position));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Bundle bundle = new Bundle();
            BSheetSongsMoreFragment b_sheet = new BSheetSongsMoreFragment();
            bundle.putSerializable(PASSEDSONGFILE, dilog_flies.get(position));
            bundle.putInt(SONGPOSITION, position);
            bundle.putByteArray(IMAGEOFSONG, byteArray);
            b_sheet.setArguments(bundle);
            b_sheet.show(supportManager, "ABC");
        }
    }
}
