package com.sanket_satpute_20.playme.project.recycler_views;

import static com.sanket_satpute_20.playme.project.activity.PlayActivity.isRoundBackgroundOn;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.IMAGEOFSONG;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.PASSEDSONGFILE;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;
import static com.sanket_satpute_20.playme.project.service.BackService.song;

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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.activity.PlayActivity;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BSheetSongsMoreFragment;
import com.sanket_satpute_20.playme.project.extra_stuffes.CacheImageManager;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.google.android.material.card.MaterialCardView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class CurrentPlayingRecycle extends RecyclerView.Adapter<CurrentPlayingRecycle.CurrentPlayingHolder> {

    public static final String INTPOSITION = "INTPOSITION";
    Context context;
    ArrayList<MusicFiles> currentPlayingFiles;
    RecyclerView recyclerView;
    Intent intent = new Intent();

    SharedPreferences preferences;

    int position_play;
    ColorStateList color;

    FragmentManager manager;

    public CurrentPlayingRecycle(Context context, ArrayList<MusicFiles> currentPlayingFiles, RecyclerView recyclerView, FragmentManager manager) {
        this.context = context;
        this.currentPlayingFiles = currentPlayingFiles;
        this.recyclerView = recyclerView;
        this.manager = manager;
        preferences = context.getSharedPreferences("PLAYING", Context.MODE_PRIVATE);
        position_play = preferences.getInt(SONGPOSITION, 0);

        ItemTouchHelper.SimpleCallback helper = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|
                ItemTouchHelper.DOWN| ItemTouchHelper.START| ItemTouchHelper.END,0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(song, fromPosition, toPosition);
                notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        ItemTouchHelper item_helper = new ItemTouchHelper(helper);
        item_helper.attachToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public CurrentPlayingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_current_playing_song_item
        , parent, false);
        return new CurrentPlayingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentPlayingHolder holder, int position) {
        holder.song_name.setText(currentPlayingFiles.get(position)
        .getTitle());

        if (position == position_play) {
            holder.song_name.setTextColor(ACCENT_COLOR);
            holder.artist_name.setTextColor(ACCENT_COLOR);
        } else {
            holder.song_name.setTextColor(color);
            holder.artist_name.setTextColor(color);
        }

        try {
            Bitmap bitmap = CacheImageManager.getImage(context, currentPlayingFiles.get(position));
            if (bitmap == null) {
                MyImageLoad load = new MyImageLoad();
                load.setViewHolder(holder);
                load.execute(currentPlayingFiles.get(position));
            } else {
                Glide.with(context)
                        .load(bitmap)
                        .override(50, 50)
                        .placeholder(R.mipmap.ic_music)
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .into(holder.song_src);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.artist_name.setText(currentPlayingFiles.get(position)
                .getArtist());

        holder.remove.setOnClickListener(view -> {
            currentPlayingFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeRemoved(position, currentPlayingFiles.size());
        });

        holder.option.setOnClickListener(view -> {
            byte[] byteArray = null;
            try {
                Bitmap bmp = CacheImageManager.getImage(context, currentPlayingFiles.get(position));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Bundle bundle = new Bundle();
                bundle.putSerializable(PASSEDSONGFILE, currentPlayingFiles.get(position));
                bundle.putInt(SONGPOSITION, position);
                bundle.putByteArray(IMAGEOFSONG, byteArray);
                BSheetSongsMoreFragment bSheetSongsMoreFragment = new BSheetSongsMoreFragment();
                bSheetSongsMoreFragment.setArguments(bundle);
                bSheetSongsMoreFragment.show(manager, "ABC");
            }
        });

        holder.itemView.setOnClickListener(view -> startSong(position));
    }

    @Override
    public int getItemCount() {
        return currentPlayingFiles.size();
    }

    public class CurrentPlayingHolder extends RecyclerView.ViewHolder {

        ImageView align_tool, song_src, option, remove;
        TextView song_name, artist_name;
        MaterialCardView card;

        public CurrentPlayingHolder(@NonNull View itemView) {
            super(itemView);
            align_tool = itemView.findViewById(R.id.aligning_song);
            song_src = itemView.findViewById(R.id.song_src);
            song_name = itemView.findViewById(R.id.song_name);
            artist_name = itemView.findViewById(R.id.artist_name);
            remove = itemView.findViewById(R.id.remove);
            option = itemView.findViewById(R.id.option);
            card = itemView.findViewById(R.id.parent_card);
            if (isRoundBackgroundOn) {
                color = ColorStateList.valueOf(PlayActivity.default_color);
            } else {
                color = song_name.getTextColors();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        private CurrentPlayingHolder holder;
        MediaMetadataRetriever retriver;
        public void setViewHolder(CurrentPlayingHolder holder)
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
                Glide.with(context).asBitmap()
                        .load(bytes)
                        .override(50, 50)
                        .placeholder(R.mipmap.ic_music)
                        .into(holder.song_src);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startSong(int position){
        intent.setAction("current.song.played.Start");
        intent.putExtra(INTPOSITION, position);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
