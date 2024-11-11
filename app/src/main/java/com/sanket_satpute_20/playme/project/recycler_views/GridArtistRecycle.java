package com.sanket_satpute_20.playme.project.recycler_views;

import static com.sanket_satpute_20.playme.project.activity.ArtistSongsActivity.ARTIST_FINISHING_REQUEST_CODE;
import static com.sanket_satpute_20.playme.project.recycler_views.ArtistRecycle.ARTISTNAME;
import static com.sanket_satpute_20.playme.project.recycler_views.ArtistRecycle.PASSEDARTIST;

import android.annotation.SuppressLint;
import android.app.Activity;
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

public class GridArtistRecycle extends RecyclerView.Adapter<GridArtistRecycle.GridArtistHolder>{


    ArrayList<Artists> artist_files;
    Activity activity;

    public GridArtistRecycle (Activity activity, ArrayList<Artists> artist_files) {
        this.activity = activity;
        this.artist_files = artist_files;
    }

    @NonNull
    @Override
    public GridArtistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.single_artist_item_grid, parent, false);
        return new GridArtistHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridArtistHolder holder, int position) {
        holder.artist_name.setText(artist_files.get(position).getArtistName());

        try {
            Bitmap bitmap = CacheImageManager.getImage(activity, artist_files.get(position).getArtistFiles().get(0));
            if (bitmap == null) {
                MyImageLoad load = new MyImageLoad();
                load.setViewHolder(holder);
                load.execute(artist_files.get(position).getArtistFiles().get(0));
            } else {
                Glide.with(activity)
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
            Intent intent = new Intent(activity, ArtistSongsActivity.class);
            intent.putExtra(ARTISTNAME, artist_files.get(position).getArtistName());
            intent.putExtra(PASSEDARTIST, artist_files.get(position));
            activity.startActivityForResult(intent, ARTIST_FINISHING_REQUEST_CODE);;
        });
    }

    @Override
    public int getItemCount() {
        return artist_files.size();
    }

    public static class GridArtistHolder extends RecyclerView.ViewHolder {

        ImageView artist_src;
        TextView artist_name;
        public GridArtistHolder(@NonNull View itemView) {
            super(itemView);
            artist_name = itemView.findViewById(R.id.artist_name);
            artist_src = itemView.findViewById(R.id.artist_src);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        private GridArtistRecycle.GridArtistHolder holder;
        MediaMetadataRetriever retriver;
        public void setViewHolder(GridArtistRecycle.GridArtistHolder holder)
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
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return album_pic;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            try {
                if (bytes != null)
                    CacheImageManager.putImage(activity, fileIO, BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                Glide.with(activity)
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
