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

public class InnerAlbumRecycle extends RecyclerView.Adapter<InnerAlbumRecycle.InnerAlbumHolder>{

    Context context;
    public static ArrayList<MusicFiles> innerAlbumFiles;
    public static final String IAMALBUM = "IAMALBUM";
    private final BackService service = new BackService();

    public InnerAlbumRecycle(Context context, ArrayList<MusicFiles> innerAlbumFiles)
    {
        this.context = context;
        InnerAlbumRecycle.innerAlbumFiles = innerAlbumFiles;
    }


    @NonNull
    @Override
    public InnerAlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_album_song_item, parent, false);
        return new InnerAlbumHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerAlbumHolder holder, int position) {
        holder.itemView.setTranslationY(500f);
        holder.itemView.setScaleX(0.1f);
        holder.itemView.setScaleY(0.1f);
        holder.itemView.animate().translationY(0).scaleX(1f).scaleY(1f).setDuration(200).start();

        holder.songName.setText(innerAlbumFiles.get(position).getTitle());
        holder.artistName.setText(innerAlbumFiles.get(position).getArtist());
        holder.count.setText(String.valueOf(position+1));
        holder.time.setText(getDuration(Integer.parseInt(innerAlbumFiles.get(position).getDuration())));

        try {
            Bitmap bitmap = CacheImageManager.getImage(context, innerAlbumFiles.get(position));
            if (bitmap == null) {
                MyImageLoad load = new MyImageLoad();
                load.setViewHolder(holder);
                load.execute(innerAlbumFiles.get(position));
            } else {
                Glide.with(context)
                        .load(bitmap)
                        .override(70, 70)
                        .placeholder(R.mipmap.ic_music)
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .into(holder.song_art);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(view -> startNew(position));
    }

    @Override
    public int getItemCount() {
        return innerAlbumFiles.size();
    }

    public static class InnerAlbumHolder extends RecyclerView.ViewHolder {

        TextView songName, artistName, count, time;
        ImageView song_art;
        public InnerAlbumHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            artistName = itemView.findViewById(R.id.artist_name);
            count = itemView.findViewById(R.id.count);
            time = itemView.findViewById(R.id.songtime);
            song_art = itemView.findViewById(R.id.album_images);
        }
    }

    // Get Time
    public String getDuration(int time)
    {
        int seconds = (time / 1000) % 60 ;
        int minutes = ((time / (1000*60)) % 60);
        return minutes+" : "+seconds;
    }

    public void startNew(int position) {
        service.setSongSource(innerAlbumFiles);

        Intent intent = new Intent(context, BackService.class);
        intent.putExtra("position", position);
        context.startService(intent);
    }

    @SuppressLint("StaticFieldLeak")
    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        private InnerAlbumHolder holder;
        MediaMetadataRetriever retriver;
        public void setViewHolder(InnerAlbumHolder holder)
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
                        .override(70, 70)
                        .placeholder(R.mipmap.ic_music)
                        .into(holder.song_art);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
