package com.sanket_satpute_20.playme.project.recycler_views;

import static com.sanket_satpute_20.playme.project.activity.ArtistSongsActivity.ARTIST_FINISHING_REQUEST_CODE;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.sanket_satpute_20.playme.project.activity.ArtistSongsActivity;
import com.sanket_satpute_20.playme.project.extra_stuffes.CacheImageManager;
import com.sanket_satpute_20.playme.project.model.Artists;
import com.sanket_satpute_20.playme.project.model.MusicFiles;

import java.util.ArrayList;

public class ArtistRecycle extends RecyclerView.Adapter<ArtistRecycle.ArtistHolder> {

    public static final String PASSEDARTIST = "PASSEDARTIST";
    Context context;
    ArrayList<Artists> artistFiles;

    public static String ARTISTNAME = "ARTISTNAME";

    public ArtistRecycle(Context context, ArrayList<Artists> artistFiles) {
        this.context = context;
        this.artistFiles = artistFiles;
    }

    @NonNull
    @Override
    public ArtistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_artist_song_item, parent, false);
        return new ArtistHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.artist.setText(artistFiles.get(position).getArtistName());

        try {
            Bitmap bitmap = CacheImageManager.getImage(context, artistFiles.get(position).getArtistFiles().get(0));
            if (bitmap == null) {
                MyImageLoad load = new MyImageLoad();
                load.setViewHolder(holder);
                load.execute(artistFiles.get(position).getArtistFiles().get(0));
            } else {
                Glide.with(context)
                        .load(bitmap)
                        .override(200, 200)
                        .placeholder(R.mipmap.ic_artist)
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .into(holder.artist_src);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ArtistSongsActivity.class);
            intent.putExtra(ARTISTNAME, artistFiles.get(position).getArtistName());
            intent.putExtra(PASSEDARTIST, artistFiles.get(position));
            ((Activity) context).startActivityForResult(intent, ARTIST_FINISHING_REQUEST_CODE);
        });
    }

    @Override
    public int getItemCount() {
        return artistFiles.size();
    }

    public static class ArtistHolder extends RecyclerView.ViewHolder {

        TextView artist;
        ImageView artist_src;
        public ArtistHolder(@NonNull View itemView) {
            super(itemView);
            artist = itemView.findViewById(R.id.artist_name);
            artist_src = itemView.findViewById(R.id.artist_src);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]> {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        private ArtistHolder holder;
        MediaMetadataRetriever retriver;

        public void setViewHolder(ArtistHolder holder) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return album_pic;
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
                        .placeholder(R.mipmap.ic_artist)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.artist_src);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
