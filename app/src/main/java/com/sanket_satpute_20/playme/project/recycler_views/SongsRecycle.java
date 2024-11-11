package com.sanket_satpute_20.playme.project.recycler_views;

import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.FROMWHERE;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.activity.PlayActivity;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BSheetSongsMoreFragment;
import com.sanket_satpute_20.playme.project.extra_stuffes.CacheImageManager;
import com.sanket_satpute_20.playme.project.fragments.SongFragment;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.service.BackService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SongsRecycle extends RecyclerView.Adapter<SongsRecycle.SongsHolder> {

//    public static final String
    public static final String ISONGRECYCLE = "ISONGRECYCLE";
    public static final String SONGNAME = "SONGNAME";
    public static final String SONGALBUM = "SONGALBUM";
    public static final String SONGARTIST = "SONGARTIST";
    public static final String SONGPATH = "SONGPATH";
    public static final String SONGDURATION = "SONGDURATION";
    public static final String PASSEDSONGFILE = "PASSEDSONGFILE";
    public static final String IMAGEOFSONG = "IMAGEOFSONG";
    public static final String SONG_EXTRA = "SONG_EXTRA";

    private final BackService service = new BackService();

    public ArrayList<SongsHolder> holders = new ArrayList<>();

    String constPath;

    public static ColorStateList color;
    FragmentManager supportFragmentManager;

    Activity context;
    public static ArrayList<MusicFiles> files;
    int index = 0;
    SharedPreferences preferences;

    SongFragment song_frag = new SongFragment();


    public SongsRecycle(Activity context, ArrayList<MusicFiles> files, FragmentManager supportFragmentManager)
    {
        this.context = context;
        SongsRecycle.files = files;
        this.supportFragmentManager = supportFragmentManager;
        preferences = context.getSharedPreferences("PLAYING", Context.MODE_PRIVATE);
        if (holders != null)
            holders.clear();
        else
            holders = new ArrayList<>();
    }

    @NonNull
    @Override
    public SongsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_song_item, parent, false);
        return new SongsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holders != null) {
            if (!holders.contains(holder)) {
                holders.add(holder);
            }
        }
        holder.song_name.setText(files.get(position).getTitle());
        holder.song_artist.setText(files.get(position).getArtist());
        try {
            holder.song_duration.setText(getDuration(Integer.parseInt(files.get(position).getDuration())));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        index = preferences.getInt(SONGPOSITION, 0);
        constPath = preferences.getString(SONGPATH, null);

        if (constPath != null) {
            if (position == index && constPath.equals(files.get(position).getPath())) {
                holder.song_name.setTextColor(ACCENT_COLOR);
                holder.song_artist.setTextColor(ACCENT_COLOR);
                holder.song_duration.setTextColor(ACCENT_COLOR);
                holder.song_name.setHorizontallyScrolling(true);
                holder.song_name.setSelected(true);
            }
            else {
                if (color != null) {
                    holder.song_name.setTextColor(color);
                    holder.song_artist.setTextColor(color);
                    holder.song_duration.setTextColor(color);
                    holder.song_name.setHorizontallyScrolling(false);
                    holder.song_name.setSelected(false);
                }
            }
        }

        try {
            Bitmap bitmap = CacheImageManager.getImage(context, files.get(position));
            if (bitmap == null) {
                MyImageLoad load = new MyImageLoad();
                load.setViewHolder(holder);
                load.execute(files.get(position));
            } else {
                Glide.with(context)
                        .load(bitmap)
                        .override(50, 50)
                        .placeholder(R.mipmap.ic_music)
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .into(holder.song_art);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        click event
        holder.itemView.setOnClickListener(view -> {
            if (song_frag == null) {
                song_frag = new SongFragment();
            }
            song_frag.previous_holder = holder;

            service.setSongSource(files);

            Intent intent = new Intent(context, BackService.class);
            intent.putExtra("position", position);
            context.startService(intent);

            Intent act = new Intent(context, PlayActivity.class);
            act.putExtra(FROMWHERE, ISONGRECYCLE);
            context.startActivity(act);
            context.overridePendingTransition(R.anim.play_act_up, R.anim.no_animation);
        });

        holder.more.setOnClickListener(view -> {
                byte[] byteArray = null;
                try {
                    Bitmap bmp = CacheImageManager.getImage(context, files.get(position));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(PASSEDSONGFILE, files.get(position));
                    bundle.putInt(SONGPOSITION, position);
                    bundle.putByteArray(IMAGEOFSONG, byteArray);
                    BSheetSongsMoreFragment dilog = new BSheetSongsMoreFragment();
                    dilog.setArguments(bundle);
                    dilog.show(supportFragmentManager, "ABC");
                }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public static class SongsHolder extends RecyclerView.ViewHolder
    {
        public TextView song_name, song_duration, song_artist;
        ImageView song_art, more;
        public SongsHolder(@NonNull View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.songname);
            song_artist = itemView.findViewById(R.id.artist);
            song_duration = itemView.findViewById(R.id.duration);
            song_art = itemView.findViewById(R.id.song_art);
            more = itemView.findViewById(R.id.more);
            color = song_name.getTextColors();
        }
    }

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
        private SongsHolder holder;
        MediaMetadataRetriever retriever;
        public void setViewHolder(SongsHolder holder)
        {
            this.holder = holder;
        }

        @Override
        protected byte[] doInBackground(MusicFiles... musicFiles) {
            try {
                fileIO = musicFiles[0];
                path = fileIO.getPath();
                retriever = new MediaMetadataRetriever();
                retriever.setDataSource(path);
                album_pic = retriever.getEmbeddedPicture();
                retriever.release();
            } catch (RuntimeException | IOException e) {
                e.printStackTrace();
            }
            return album_pic;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
//            pictures.put(path, bytes);
            try {
                if (bytes != null)
                    CacheImageManager.putImage(context, fileIO, BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                Glide.with(context)
                        .load(bytes)
                        .override(50, 50)
                        .placeholder(R.mipmap.ic_music)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.song_art);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

}
