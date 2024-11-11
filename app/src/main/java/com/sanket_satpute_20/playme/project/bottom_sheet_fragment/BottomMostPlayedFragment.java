package com.sanket_satpute_20.playme.project.bottom_sheet_fragment;

import static com.sanket_satpute_20.playme.MainActivity.default_color;
import static com.sanket_satpute_20.playme.MainActivity.favouriteList;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.recycler_views.MostPlayedActivityRecycle.m_p_a_files;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGALBUM;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGARTIST;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGDURATION;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGNAME;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGPATH;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONG_EXTRA;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;
import static com.sanket_satpute_20.playme.project.service.BackService.most_played_songs;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.activity.MostPlayedActivity;
import com.sanket_satpute_20.playme.project.activity.ShowSongInfoActivity;
import com.sanket_satpute_20.playme.project.extra_stuffes.CacheImageManager;
import com.sanket_satpute_20.playme.project.fragments.HomeTabFragment;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.recycler_views.MostPlayedActivityRecycle;
import com.sanket_satpute_20.playme.project.recycler_views.MostPlayedRecycle;
import com.sanket_satpute_20.playme.project.service.BackService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

public class BottomMostPlayedFragment extends BottomSheetDialogFragment {

    //    from bundle
    MusicFiles music;
    int position;

    MaterialCardView divider;
    RelativeLayout play_layout, show_detail, remove_from_most_played;
    ImageView song_src, fevourite;
    TextView song_name, played_times;

    MostPlayedActivityRecycle adapter = new MostPlayedActivityRecycle();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_most_played_frgment, container, false);
        initViews(view);
        if (getArguments() != null) {
            getBundles(getArguments());
            doExtra();
            onClick();
        }
        return view;
    }

    private void getBundles(Bundle bundle) {
        music = (MusicFiles) bundle.getSerializable(SONG_EXTRA);
        position = bundle.getInt(SONGPOSITION);
    }

    private void initViews(View view) {
        divider = view.findViewById(R.id.divider);
        song_src = view.findViewById(R.id.song_img);
        song_name = view.findViewById(R.id.song_name);
        played_times = view.findViewById(R.id.played_txt);
        fevourite = view.findViewById(R.id.add_remove_fevourite);
        play_layout = view.findViewById(R.id.play_layout);
        show_detail = view.findViewById(R.id.song_info_layout);
        remove_from_most_played = view.findViewById(R.id.remove_layout);
    }

    @SuppressLint("SetTextI18n")
    private void doExtra() {
        divider.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));

        song_name.setText(music.getTitle());
        played_times.setText("Played more than " + music.getSong_most_played_count() + " times");

        try {
            Bitmap bitmap = CacheImageManager.getImage(requireActivity(), music);
            if (bitmap == null) {
                MyImageLoad load = new MyImageLoad();
                load.execute(music);
            } else {
                Glide.with(requireActivity())
                        .load(bitmap)
                        .override(50, 50)
                        .placeholder(R.mipmap.ic_music)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(song_src);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        isFevourite();
    }

    private void onClick() {
        play_layout.setOnClickListener(view -> playSong());
        show_detail.setOnClickListener(view -> showSongDetail());
        remove_from_most_played.setOnClickListener(view -> removeFromMostPlayed());

        fevourite.setOnClickListener(view -> {
            if (favouriteList.contains(music)) {
                unFevouriteClicked(fevourite);
                favouriteList.remove(music);
            } else {
                fevouriteBtnClicked(fevourite);
                favouriteList.add(music);
            }
            isFevourite();
        });
    }

    private void isFevourite() {
        try {
            if (favouriteList.contains(music)) {
                fevourite.setImageResource(R.drawable.heart_filled_icon_24);
                fevourite.setColorFilter(ACCENT_COLOR);
            } else {
                fevourite.setImageResource(R.drawable.heart_no_fill_24);
                fevourite.setColorFilter(default_color);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //    extra methods
    private void playSong() {
        if (m_p_a_files != null) {
            if (m_p_a_files.size() > 0) {
                BackService backService = new BackService();
                backService.setSongSource(m_p_a_files);

                Intent intent = new Intent(requireActivity(), BackService.class);
                intent.putExtra("position", position);
                requireActivity().startService(intent);
            }
        }
        this.dismiss();
    }

    private void showSongDetail() {
        Intent intent = new Intent(requireActivity(), ShowSongInfoActivity.class);
        intent.putExtra(SONG_EXTRA, music);
        intent.putExtra(SONGNAME, music.getTitle());
        intent.putExtra(SONGARTIST, music.getArtist());
        intent.putExtra(SONGALBUM, music.getAlbum());
        intent.putExtra(SONGPOSITION, position);
        intent.putExtra(SONGDURATION, music.getDuration());
        intent.putExtra(SONGPATH, music.getPath());
        startActivity(intent);
        this.dismiss();
    }

    private void removeFromMostPlayed() {
        m_p_a_files.remove(music);
        most_played_songs.remove(music);
        MostPlayedActivityRecycle activity_adapter = MostPlayedActivity.getAdapter();
        MostPlayedRecycle fragment_adapter = HomeTabFragment.getMostPlayedAdapter();
        if (activity_adapter != null) {
            activity_adapter.notifyItemRemoved(position);
        }
        if (fragment_adapter != null) {
            fragment_adapter.notifyItemRemoved(position);
        }
        this.dismiss();
    }


    @SuppressLint("StaticFieldLeak")
    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]> {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        MediaMetadataRetriever retriver;

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
                    CacheImageManager.putImage(requireActivity(), fileIO, BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                try {
                    Glide.with(requireActivity())
                            .load(bytes)
                            .override(100, 100)
                            .placeholder(R.mipmap.ic_music)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(song_src);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void fevouriteBtnClicked(ImageView v) {
        ObjectAnimator firstX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.4f);
        ObjectAnimator firstY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.4f);

        ObjectAnimator secondX = ObjectAnimator.ofFloat(v, "scaleX", 1.4f,  .7f);
        ObjectAnimator secondY = ObjectAnimator.ofFloat(v, "scaleY", 1.4f,  .7f);

        ObjectAnimator thirdX = ObjectAnimator.ofFloat(v, "scaleX", .7f, 1.2f);
        ObjectAnimator thirdY = ObjectAnimator.ofFloat(v, "scaleY", .7f, 1.2f);

        ObjectAnimator fourthX = ObjectAnimator.ofFloat(v, "scaleX", 1.2f, 1f);
        ObjectAnimator fourthY = ObjectAnimator.ofFloat(v, "scaleY", 1.2f, 1f);

        AnimatorSet first = new AnimatorSet();
        first.playTogether(firstX, firstY);
        first.setInterpolator(new AccelerateInterpolator());

        AnimatorSet second = new AnimatorSet();
        second.playTogether(secondX, secondY);
        second.setInterpolator(new DecelerateInterpolator());

        AnimatorSet third = new AnimatorSet();
        third.playTogether(thirdX, thirdY);
        third.setInterpolator(new DecelerateInterpolator());

        AnimatorSet fourth = new AnimatorSet();
        fourth.playTogether(fourthX, fourthY);
        fourth.setInterpolator(new LinearInterpolator());

        AnimatorSet set1 = new AnimatorSet();
        set1.play(first).before(second);

        AnimatorSet set2 = new AnimatorSet();
        set2.play(third).before(fourth);

        AnimatorSet set = new AnimatorSet();
        set.play(set1).before(set2);
        set.setDuration(100);
        set.start();
    }

    private void unFevouriteClicked(View v) {
        ObjectAnimator firstX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.8f);
        ObjectAnimator firstY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.8f);

        ObjectAnimator secondX = ObjectAnimator.ofFloat(v, "scaleX", .8f, 1f);
        ObjectAnimator secondY = ObjectAnimator.ofFloat(v, "scaleY", .8f, 1f);

        AnimatorSet first = new AnimatorSet();
        first.playTogether(firstX, firstY);
        first.setInterpolator(new LinearInterpolator());

        AnimatorSet second = new AnimatorSet();
        second.playTogether(secondX, secondY);
        second.setInterpolator(new BounceInterpolator());

        AnimatorSet set = new AnimatorSet();
        set.play(first).before(second);
        set.setDuration(100);
        set.start();
    }
}