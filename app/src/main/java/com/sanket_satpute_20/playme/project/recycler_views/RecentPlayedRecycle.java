package com.sanket_satpute_20.playme.project.recycler_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.extra_stuffes.CacheImageManager;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.service.BackService;

import java.util.ArrayList;

public class RecentPlayedRecycle extends RecyclerView.Adapter<RecentPlayedRecycle.RecentPlayedHolder> {

    Context context;
    public static ArrayList<MusicFiles> recentPlayedFiles;
    private final BackService service = new BackService();

    public RecentPlayedRecycle(Context context, ArrayList<MusicFiles> recentPlayedFiles) {
        this.context = context;
        RecentPlayedRecycle.recentPlayedFiles = recentPlayedFiles;
    }

    @NonNull
    @Override
    public RecentPlayedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_song_recent_item, parent, false);
        return new RecentPlayedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentPlayedHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.song_name.setText(recentPlayedFiles.get(position).getTitle());
        holder.artist_name.setText(recentPlayedFiles.get(position).getArtist());
        holder.duration.setText(getDuration(Integer.parseInt(recentPlayedFiles.get(position).getDuration())));

        try {
            Bitmap bitmap = CacheImageManager.getImage(context, recentPlayedFiles.get(position));
            if (bitmap == null) {
                MyImageLoad load = new MyImageLoad();
                load.setViewHolder(holder);
                load.execute(recentPlayedFiles.get(position));
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

        holder.itemView.setOnClickListener(view -> {

            service.setSongSource(recentPlayedFiles);
            Intent intent = new Intent(context, BackService.class);
            intent.putExtra("position", position);
            context.startService(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recentPlayedFiles.size();
    }

    public static class RecentPlayedHolder extends RecyclerView.ViewHolder
    {
        TextView song_name, artist_name, duration;
        ImageView song_src;
        public RecentPlayedHolder(@NonNull View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.song_name);
            artist_name = itemView.findViewById(R.id.song_artist);
            duration = itemView.findViewById(R.id.song_time);
            song_src = itemView.findViewById(R.id.song_src);
        }
    }

    // defined methods
    // Get Time
    public String getDuration(int time)
    {
        return (((time / (1000*60)) % 60)+" : "+(time / 1000) % 60);
    }

    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
        byte[] album_pic;
        private RecentPlayedHolder holder;
        MediaMetadataRetriever retriver;
        String path;
        MusicFiles fileIO;
        public void setViewHolder(RecentPlayedHolder holder)
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
            if (bytes != null)
                CacheImageManager.putImage(context, fileIO, BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            try {
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
}
