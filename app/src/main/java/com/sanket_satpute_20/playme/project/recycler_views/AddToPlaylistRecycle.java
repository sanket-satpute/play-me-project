package com.sanket_satpute_20.playme.project.recycler_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.extra_stuffes.CacheImageManager;
import com.sanket_satpute_20.playme.project.model.MusicFiles;

import java.util.ArrayList;

public class AddToPlaylistRecycle extends RecyclerView.Adapter<AddToPlaylistRecycle.AddToPlaylistHolder> {

    Context context;
    ArrayList<MusicFiles> add_to_playlist_list;
    boolean selection;

    public static ArrayList<MusicFiles> addedFiles ;

    Intent intent = new Intent();

    public AddToPlaylistRecycle(Context context, ArrayList<MusicFiles> add_to_playlist_list, boolean selection) {
        this.context = context;
        this.add_to_playlist_list = add_to_playlist_list;
        this.selection = selection;
        addedFiles = new ArrayList<>();
    }

    @NonNull
    @Override
    public AddToPlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_to_playlist_item, parent, false);
        return new AddToPlaylistHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddToPlaylistHolder holder, int position) {
        holder.song_name.setText(add_to_playlist_list.get(position).getTitle());
        holder.song_artist.setText(add_to_playlist_list.get(position).getArtist());

        if (addedFiles.contains(add_to_playlist_list.get(position))) {
            holder.main_card.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2f45e2f1")));
        } else {
            holder.main_card.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00000000")));
        }

        try {
            Bitmap bitmap = CacheImageManager.getImage(context, add_to_playlist_list.get(position));
            if (bitmap == null) {
                MyImageLoad load = new MyImageLoad();
                load.setViewHolder(holder);
                load.execute(add_to_playlist_list.get(position));
            } else {
                Glide.with(context)
                        .load(bitmap)
                        .override(50, 50)
                        .placeholder(R.mipmap.ic_album)
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .into(holder.song_art);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.main_card.setOnClickListener(view -> {
            if (selection) {
                intent.setAction("com.selected.SONG");

            } else {
                intent.setAction("com.remove.SONG");
            }
            if (addedFiles.contains(add_to_playlist_list.get(position))) {
                addedFiles.remove(add_to_playlist_list.get(position));
                holder.main_card.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00000000")));
            } else {
                addedFiles.add(add_to_playlist_list.get(position));
                holder.main_card.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2f45e2f1")));
            }
            if (addedFiles.size() > 0) {
                intent.putExtra("msg", "selected");
                intent.putExtra("size", addedFiles.size());
            } else {
                intent.putExtra("msg", "unselected");
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });

    }

    @Override
    public int getItemCount() {
        return add_to_playlist_list.size();
    }

    public static class AddToPlaylistHolder extends RecyclerView.ViewHolder {

        TextView song_name, song_artist;
        CardView main_card;
        ImageView song_art;
        public AddToPlaylistHolder(@NonNull View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.songname);
            song_artist = itemView.findViewById(R.id.artist);
            main_card = itemView.findViewById(R.id.main_card);
            song_art = itemView.findViewById(R.id.song_art);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(ArrayList<MusicFiles> file) {
        add_to_playlist_list = new ArrayList<>();
        add_to_playlist_list.addAll(file);
        notifyDataSetChanged();
    }

    @SuppressLint("StaticFieldLeak")
    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        private AddToPlaylistHolder holder;
        MediaMetadataRetriever retriver;
        public void setViewHolder(AddToPlaylistHolder holder)
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return album_pic;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            if (bytes != null)
                CacheImageManager.putImage(context, fileIO, BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            try {
                Glide.with(context)
                        .load(bytes)
                        .override(50, 50)
                        .placeholder(R.mipmap.ic_music)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.song_art);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
