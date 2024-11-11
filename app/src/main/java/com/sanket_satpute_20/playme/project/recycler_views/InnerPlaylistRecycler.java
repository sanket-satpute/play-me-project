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

public class InnerPlaylistRecycler extends RecyclerView.Adapter<InnerPlaylistRecycler.InnerPlaylistHolder> {


    Context context;
    public static ArrayList<MusicFiles> innerPlaylistFiles;

    private final BackService service = new BackService();

    public InnerPlaylistRecycler(Context context, ArrayList<MusicFiles> innerPlaylistFiles) {
        this.context = context;
        InnerPlaylistRecycler.innerPlaylistFiles = innerPlaylistFiles;
    }

    @NonNull
    @Override
    public InnerPlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_album_song_item, parent, false);
        return new InnerPlaylistHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerPlaylistHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.itemView.setTranslationY(500f);
        holder.itemView.setScaleX(0.1f);
        holder.itemView.setScaleY(0.1f);
        holder.itemView.animate().translationY(0).scaleX(1f).scaleY(1f).setDuration(200).start();

        holder.song_name.setText(innerPlaylistFiles.get(position).getTitle());
        holder.artist_name.setText(innerPlaylistFiles.get(position).getArtist());
        holder.duration.setText(getDuration(Integer.parseInt(innerPlaylistFiles.get(position).getDuration())));
        holder.index.setText(String.valueOf(position+1));

        try {
            Bitmap bitmap = CacheImageManager.getImage(context, innerPlaylistFiles.get(position));
            if (bitmap == null) {
                MyImageLoad load = new MyImageLoad();
                load.setViewHolder(holder);
                load.execute(innerPlaylistFiles.get(position));
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

            service.setSongSource(innerPlaylistFiles);

            Intent intent = new Intent(context, BackService.class);
            intent.putExtra("position", position);
            context.startService(intent);
        });
    }

    @Override
    public int getItemCount() {
        return innerPlaylistFiles.size();
    }

    public static class InnerPlaylistHolder extends RecyclerView.ViewHolder
    {
        TextView index, song_name, artist_name, duration;
        ImageView song_src;
        public InnerPlaylistHolder(@NonNull View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.song_name);
            artist_name = itemView.findViewById(R.id.artist_name);
            index = itemView.findViewById(R.id.count);
            duration = itemView.findViewById(R.id.songtime);
            song_src = itemView.findViewById(R.id.album_images);
        }
    }

    // defined methods
    // Get Time
    public String getDuration(int time)
    {
        int seconds = (time / 1000) % 60 ;
        int minutes = ((time / (1000*60)) % 60);
        return minutes+" : "+seconds;
    }

    @SuppressLint("StaticFieldLeak")
    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        private InnerPlaylistHolder holder;
        MediaMetadataRetriever retriver;
        public void setViewHolder(InnerPlaylistHolder holder)
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
}
