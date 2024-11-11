package com.sanket_satpute_20.playme.project.recycler_views;

import static com.sanket_satpute_20.playme.project.activity.MostPlayedActivity.most_played_service;
import static com.sanket_satpute_20.playme.project.activity.MostPlayedActivity.most_playing_position;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.stat_position;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGALBUM;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGARTIST;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGDURATION;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGNAME;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGPATH;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONG_EXTRA;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;
import static com.sanket_satpute_20.playme.project.service.BackService.song;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chibde.visualizer.LineBarVisualizer;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.activity.ShowSongInfoActivity;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BottomMostPlayedFragment;
import com.sanket_satpute_20.playme.project.extra_stuffes.CacheImageManager;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.service.BackService;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MostPlayedActivityRecycle extends RecyclerView.Adapter<MostPlayedActivityRecycle.MostPlayedActivityHolder> {

    public static ArrayList<MusicFiles> m_p_a_files;
    Context context;
    FragmentManager manager;

    MostPlayedActivityHolder playing_holder;


    public MostPlayedActivityRecycle(Context context, ArrayList<MusicFiles> m_p_a_files, FragmentManager manager) {
        this.context = context;
        MostPlayedActivityRecycle.m_p_a_files = m_p_a_files;
        this.manager = manager;
    }

    public MostPlayedActivityRecycle() {}

    @NonNull
    @Override
    public MostPlayedActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.most_played_activity_song_single_item, parent, false);
        return new MostPlayedActivityHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MostPlayedActivityHolder holder, @SuppressLint("RecyclerView") int position) {

        if (song.get(most_playing_position).equals(m_p_a_files.get(position))) {
            stat_position = position;
            playing_holder = holder;
            holder.song_name.setSelected(true);
            holder.play_song_fab.setVisibility(View.INVISIBLE);
            holder.visulizer_round_card.setVisibility(View.VISIBLE);
            setVisulizer(holder);
        } else {
            holder.song_name.setSelected(false);
            holder.play_song_fab.setVisibility(View.VISIBLE);
            holder.visulizer_round_card.setVisibility(View.INVISIBLE);
            if (holder.bar_visulizer != null)
                holder.bar_visulizer.release();
        }

        holder.song_name.setText(m_p_a_files.get(position).getTitle());

        try {
            Bitmap bitmap = CacheImageManager.getImage(context, m_p_a_files.get(position));
            if (bitmap == null) {
                MyImageLoad load = new MyImageLoad();
                load.setHolder(holder);
                load.execute(m_p_a_files.get(position));
            } else {
                Glide.with(context)
                        .load(bitmap)
                        .override(200, 200)
                        .placeholder(R.mipmap.ic_music)
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .into(holder.song_pic);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.option_button.setOnClickListener(view -> showBottomSheet(position));

        holder.play_song_fab.setOnClickListener(view -> {
            startMusic(position);
            if (stat_position != -1 ) {
                setRetunderFab();
            }
            playing_holder = holder;
            setRotatingFab(holder.play_song_fab, holder);
        });

        holder.visulizer_round_card.setOnClickListener(view -> startMusic(position));

        holder.song_pic.setOnClickListener(view -> {
//            start show info activity
            startShowInfoActivity(position);
        });
    }

    @Override
    public int getItemCount() {
        return m_p_a_files.size();
    }


    public static class MostPlayedActivityHolder extends RecyclerView.ViewHolder {

        MaterialCardView visulizer_round_card;
        LineBarVisualizer bar_visulizer;
        FloatingActionButton play_song_fab;
        TextView song_name;
        ImageView song_pic, option_button;

        public MostPlayedActivityHolder(@NonNull View itemView) {
            super(itemView);
            visulizer_round_card = itemView.findViewById(R.id.visulizer_card);
            bar_visulizer = itemView.findViewById(R.id.bar_visulizer);
            play_song_fab = itemView.findViewById(R.id.play_song_fab);
            song_name = itemView.findViewById(R.id.song_name);
            song_pic = itemView.findViewById(R.id.song_pic);
            option_button = itemView.findViewById(R.id.option_button);
            song_name.setSelected(true);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        MediaMetadataRetriever retriver;
        MostPlayedActivityHolder holder;

        void setHolder(MostPlayedActivityHolder holder) {
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
            if (bytes != null)
                CacheImageManager.putImage(context, fileIO, BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            try {
                Glide.with(context)
                        .load(bytes)
                        .override(200, 200)
                        .placeholder(R.mipmap.ic_music)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.song_pic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setVisulizer(MostPlayedActivityHolder holder) {
        if (most_played_service != null) {
            try {
                holder.bar_visulizer.setEnabled(false);
                holder.bar_visulizer.setPlayer(most_played_service.getAudioSessionId());
                holder.bar_visulizer.setColor(ACCENT_COLOR);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setRotatingFab(View view, MostPlayedActivityHolder holder) {
        ObjectAnimator rotate_fab = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        ObjectAnimator scale_x_fab = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
        ObjectAnimator scale_y_fab = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f);
        AnimatorSet fab = new AnimatorSet();
        rotate_fab.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.INVISIBLE);
                holder.visulizer_round_card.setVisibility(View.VISIBLE);
                holder.visulizer_round_card.setScaleX(0);
                holder.visulizer_round_card.setScaleY(0);
                setIncreaseSecond(holder);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        fab.playTogether(rotate_fab, scale_y_fab, scale_x_fab);
        fab.setDuration(150);
        fab.start();
    }

    private void setIncreaseSecond(MostPlayedActivityHolder holder) {
        ObjectAnimator scale_x = ObjectAnimator.ofFloat(holder.visulizer_round_card, "scaleX", 0f, 1f);
        ObjectAnimator scale_y = ObjectAnimator.ofFloat(holder.visulizer_round_card, "scaleY", 0f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scale_x, scale_y);
        set.setDuration(100);
        set.setInterpolator(new AccelerateInterpolator());
        set.start();
        setVisulizer(holder);
    }

    private void startMusic(int pos) {
        BackService backService = new BackService();
        backService.setSongSource(m_p_a_files);

        Intent intent = new Intent(context, BackService.class);
        intent.putExtra("position", pos);
        context.startService(intent);
    }

    private void setRetunderFab() {
        ObjectAnimator scale_x_increase = ObjectAnimator.ofFloat(playing_holder.visulizer_round_card, "scaleX", 1f, 0f);
        ObjectAnimator scale_y_increase = ObjectAnimator.ofFloat(playing_holder.visulizer_round_card, "scaleY", 1f, 0f);
        AnimatorSet set_increase = new AnimatorSet();
        set_increase.playTogether(scale_x_increase, scale_y_increase);
        set_increase.setDuration(70);
        set_increase.setInterpolator(new AccelerateInterpolator());

        playing_holder.visulizer_round_card.setVisibility(View.INVISIBLE);
        playing_holder.play_song_fab.setVisibility(View.VISIBLE);

        ObjectAnimator scale_x_decrease = ObjectAnimator.ofFloat(playing_holder.play_song_fab, "scaleX", 0f, 1f);
        ObjectAnimator scale_Y_decrease = ObjectAnimator.ofFloat(playing_holder.play_song_fab, "scaleY", 0f, 1f);
        AnimatorSet set_decrease = new AnimatorSet();
        set_decrease.playTogether(scale_x_decrease, scale_Y_decrease);
        set_decrease.setDuration(70);
        set_decrease.setStartDelay(70);
        set_decrease.setInterpolator(new AccelerateInterpolator());
        set_increase.start();
        set_decrease.start();
    }

    private void startShowInfoActivity(int position) {
        Intent intent = new Intent(context, ShowSongInfoActivity.class);
        intent.putExtra(SONG_EXTRA, m_p_a_files.get(position));
        intent.putExtra(SONGPOSITION, position);
        intent.putExtra(SONGNAME, m_p_a_files.get(position).getTitle());
        intent.putExtra(SONGARTIST, m_p_a_files.get(position).getArtist());
        intent.putExtra(SONGALBUM, m_p_a_files.get(position).getAlbum());
        intent.putExtra(SONGPATH, m_p_a_files.get(position).getPath());
        intent.putExtra(SONGDURATION, m_p_a_files.get(position).getDuration());
        context.startActivity(intent);
    }

    private void showBottomSheet(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(SONGPOSITION, position);
        bundle.putSerializable(SONG_EXTRA, m_p_a_files.get(position));
        BottomMostPlayedFragment fragment_bottom = new BottomMostPlayedFragment();
        fragment_bottom.setArguments(bundle);
        fragment_bottom.show(manager, "AB");
    }

}
