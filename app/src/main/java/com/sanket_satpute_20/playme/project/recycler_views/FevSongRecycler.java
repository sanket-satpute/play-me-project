package com.sanket_satpute_20.playme.project.recycler_views;

import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.IMAGEOFSONG;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.PASSEDSONGFILE;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BSheetSongsMoreFragment;
import com.sanket_satpute_20.playme.project.extra_stuffes.CacheImageManager;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.service.BackService;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class FevSongRecycler extends RecyclerView.Adapter<FevSongRecycler.FevSongHolder> {

    Context context;
    ArrayList<MusicFiles> fevSongs;
    FragmentManager manager;
    private final BackService service = new BackService();

    public FevSongRecycler(Context context, ArrayList<MusicFiles> fevSongs, FragmentManager manager) {
        this.context = context;
        this.fevSongs = fevSongs;
        this.manager = manager;
    }

    @NonNull
    @Override
    public FevSongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_song_item, parent, false);
        return new FevSongHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FevSongHolder holder, int position) {
        holder.song_name.setText(fevSongs.get(position).getTitle());
        holder.artist_name.setText(fevSongs.get(position).getArtist());
        holder.time.setText(getDuration(Integer.parseInt(fevSongs.get(position).getDuration())));

        try {
            Bitmap bitmap = CacheImageManager.getImage(context, fevSongs.get(position));
            if (bitmap == null) {
                MyImageLoad load = new MyImageLoad();
                load.setViewHolder(holder);
                load.execute(fevSongs.get(position));
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
            service.setSongSource(fevSongs);

            Intent intent = new Intent(context, BackService.class);
            intent.putExtra("position", position);
            context.startService(intent);
        });


        holder.option.setOnClickListener(view -> {
            byte[] byteArray = null;
            try {
                Bitmap bmp = CacheImageManager.getImage(context, fevSongs.get(position));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(PASSEDSONGFILE, fevSongs.get(position));
                    bundle.putInt(SONGPOSITION, position);
                    bundle.putByteArray(IMAGEOFSONG, byteArray);

                    BSheetSongsMoreFragment fragment = new BSheetSongsMoreFragment();
                    fragment.setArguments(bundle);
                    fragment.show(manager, "ABC");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return fevSongs.size();
    }

    protected static class FevSongHolder extends RecyclerView.ViewHolder {

        TextView song_name, artist_name, time;
        ImageView song_src, option;
        public FevSongHolder(@NonNull View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.songname);
            artist_name = itemView.findViewById(R.id.artist);
            time = itemView.findViewById(R.id.duration);
            song_src = itemView.findViewById(R.id.song_art);
            option = itemView.findViewById(R.id.more);
        }
    }

    /*class Asynk*/

    @SuppressLint("StaticFieldLeak")
    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        private FevSongHolder holder;
        MediaMetadataRetriever retriver;
        public void setViewHolder(FevSongHolder holder)
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
                        .override(50, 50)
                        .placeholder(R.mipmap.ic_music)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.song_src);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /*Own Methods*/

    // Get Time
    public String getDuration(int time)
    {
        int seconds = (time / 1000) % 60 ;
        int minutes = ((time / (1000*60)) % 60);
        return minutes+" : "+seconds;
    }
}
