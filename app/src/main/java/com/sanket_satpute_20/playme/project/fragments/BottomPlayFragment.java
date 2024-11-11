package com.sanket_satpute_20.playme.project.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sanket_satpute_20.playme.MainActivity.RECENT_SEEK_POSITION_SONG;
import static com.sanket_satpute_20.playme.MainActivity.favouriteList;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.SONG_SEEK_CURRENT_POSITION;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.recent_duration;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGARTIST;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGNAME;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPATH;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;
import static com.sanket_satpute_20.playme.project.service.BackService.SONG_DURATION_PREF;
import static com.sanket_satpute_20.playme.project.service.BackService.song;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.ColorFilter;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.service.BackService;

import java.util.ArrayList;


public class BottomPlayFragment extends Fragment implements ServiceConnection{

    // Service
    BackService backService;

    public boolean isPlaying = false;

    RelativeLayout parent_view;
    TextView bottom_song_name, bottom_song_artist;
    ProgressBar progressBar;
    ImageView bottom_play_pause, bottom_next, bottom_song_art, bottom_favourite;
    CardView song_art_card;

    static int position;

    // fragment data
    String songName;
    String artistName;
    String songPath;

    ColorFilter color;


    //BroadCastReceiver
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals("song.mp3.changed")) {
                    getShared();
                    setService(backService);
                } /*else if (intent.getAction().equals("action.broadcast.GESTURE_PLAY_UPDATED_AND_RECEIVED")) {
                    if (__IS_GESTURE_PLAY_SONG_ON) {
                        turnOnGesturePlay();
                    } else {
                        turnOffGesturePlay();
                    }
                }*/
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_play, container, false);
        Intent intent = new Intent(getContext(), BackService.class);
        requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
        initViews(view);
        getShared();

//        progress from last song
        ArrayList<Long> seeks = getRecentSeekPosition();
        SONG_SEEK_CURRENT_POSITION = seeks.get(0);
        recent_duration = seeks.get(1);
        if (recent_duration > SONG_SEEK_CURRENT_POSITION) {
            progressBar.setMax((int) recent_duration);
            progressBar.setProgress((int) SONG_SEEK_CURRENT_POSITION);
        }
        return view;
    }

    private ArrayList<Long> getRecentSeekPosition() {
        ArrayList<Long> list = new ArrayList<>();
        SharedPreferences preferences = requireContext().getSharedPreferences("PLAYING", MODE_PRIVATE);
        SharedPreferences preferences2 = requireActivity().getSharedPreferences("STORING_DATA", MODE_PRIVATE);
        list.add(0, preferences2.getLong(RECENT_SEEK_POSITION_SONG, 0L));
        list.add(1, preferences.getLong(SONG_DURATION_PREF, 0L));
        return list;
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getContext(), BackService.class);
        requireActivity().bindService(intent, BottomPlayFragment.this, Context.BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter();
        filter.addAction("action.broadcast.GESTURE_PLAY_UPDATED_AND_RECEIVED");
        filter.addAction("song.mp3.changed");
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(receiver, filter);
        doAction();
        getShared();
//        if (__IS_GESTURE_PLAY_SONG_ON) {
//            turnOnGesturePlay();
//        } else {
//            turnOffGesturePlay();
//        }
    }

    public void gestureSwipingIsOn(int dist, float alpha) {
        bottom_song_name.setTranslationX(dist);
        bottom_song_artist.setTranslationX(dist);
        song_art_card.setTranslationX(dist);
        bottom_song_name.setAlpha(alpha);
        bottom_song_artist.setAlpha(alpha);
        song_art_card.setAlpha(alpha);
    }

    public void gestureNextChanged() {
        animateGesturePlayNext();
    }

    public void gesturePreviousChanged() {
        animateGesturePlayPrevious();
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unbindService(this);
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver);
        backService = null;
    }

    private void initViews(View view) {
        bottom_song_name = view.findViewById(R.id.bottom_play_song_name);
        bottom_song_artist = view.findViewById(R.id.bottom_play_artist_name);
        bottom_play_pause = view.findViewById(R.id.bottom_play_play_pause);
        bottom_next = view.findViewById(R.id.bottom_play_next);
        bottom_song_art = view.findViewById(R.id.bottom_play_img);
        progressBar = view.findViewById(R.id.progress_bar);
        bottom_favourite = view.findViewById(R.id.bottom_fevourite);
        parent_view = view.findViewById(R.id.parent_view_relative);
        song_art_card = view.findViewById(R.id.cardView2);

        color = bottom_play_pause.getColorFilter();
        bottom_next.setColorFilter(color);
    }

    private void doAction() {
        bottom_play_pause.setOnClickListener(view -> {
                    if (backService != null) {
                        if (BackService.isServiceRunning) {
                            if (backService.isPlaying()) {
                                bottom_play_pause.setImageResource(R.drawable.ic_round_play_arrow_24);
                                backService.pause();
                                bottom_song_name.setSelected(false);
                            } else {
                                bottom_play_pause.setImageResource(R.drawable.ic_round_pause_24);
                                backService.play();
                                bottom_song_name.setSelected(true);
                            }
                        } else {
                            Intent intent = new Intent(requireActivity(), BackService.class);
                            intent.putExtra("position", position);
                            if (recent_duration > SONG_SEEK_CURRENT_POSITION) {
                                intent.putExtra("seekTO", SONG_SEEK_CURRENT_POSITION);
                            }
                            requireActivity().startService(intent);
                        }
                        isPlaying = !isPlaying;
                    }
                }
        );
        bottom_next.setOnClickListener(view -> nextButtonClicked());

        bottom_favourite.setOnClickListener(view -> {
                    try {
                        if (favouriteList.contains(song.get(position))) {
                            favouriteList.remove(song.get(position));
                            bottom_favourite.setColorFilter(color);
                            bottom_favourite.setImageResource(R.drawable.heart_no_fill_24);
                            unFavouriteClicked(bottom_favourite);
                        } else {
                            favouriteList.add(song.get(position));
                            bottom_favourite.setImageResource(R.drawable.heart_filled_icon_24);
                            bottom_favourite.setColorFilter(ACCENT_COLOR);
                            favouriteBtnClicked(bottom_favourite);
                        }
                    } catch (IndexOutOfBoundsException | IllegalStateException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            );
    }

    private void nextButtonClicked() {
        if (backService != null)
        {
            if (BackService.isServiceRunning)
                backService.setNextButtonClicked();
            else {
                Intent intent = new Intent(requireActivity(), BackService.class);
                intent.putExtra("position", (song.size() > 1) ? position + 1 : position);
                requireActivity().startService(intent);
            }
        }
    }

    private void previousButtonClicked() {
        if (backService != null)
        {
            if (BackService.isServiceRunning)
                backService.setPreviousButtonClicked();
            else {
                Intent intent = new Intent(requireActivity(), BackService.class);
                intent.putExtra("position", (position > 0) ? position - 1 : (song.size() - 1));
                requireActivity().startService(intent);
            }
        }
    }

    private void getShared() {
        SharedPreferences preferences = requireContext().getSharedPreferences("PLAYING", MODE_PRIVATE);
        if (preferences != null) {
            songName = preferences.getString(SONGNAME, "Not Found");
            artistName = preferences.getString(SONGARTIST, "Not Found");
            songPath = preferences.getString(SONGPATH, null);
            position = preferences.getInt(SONGPOSITION, 0);
            bottom_song_name.setText(songName);
            bottom_song_name.setSelected(true);
            bottom_song_artist.setText(artistName);
            if (song != null) {
                try {
                    if (song.size() > position) {
                        MyImageLoad load = new MyImageLoad();
                        load.execute(song.get(position));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (song.size() > position) {
                            if (!favouriteList.contains(song.get(position))) {
                                bottom_favourite.setColorFilter(color);
                                bottom_favourite.setImageResource(R.drawable.heart_no_fill_24);
                            } else {
                                bottom_favourite.setImageResource(R.drawable.heart_filled_icon_24);
                                bottom_favourite.setColorFilter(ACCENT_COLOR);
                            }
                        }
                    } catch (IndexOutOfBoundsException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setService(BackService service) {
        if (service.isPlaying()) {
            bottom_play_pause.setImageResource(R.drawable.ic_round_pause_24);
            bottom_song_name.setSelected(true);
        }
        else {
            bottom_play_pause.setImageResource(R.drawable.ic_round_play_arrow_24);
            bottom_song_name.setSelected(false);
        }
        progressBar.setMax(backService.getDuration());
        int durationI = backService.getDuration();
        progressBar.setMax(durationI);

        Handler handler = new Handler();
        try {
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (service.isPlaying())
                        progressBar.setProgress(service.getCurrentPosition());
                    handler.postDelayed(this, 1000);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // for Service
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        BackService.LocalBinder binder = (BackService.LocalBinder) iBinder;
        backService = binder.getService();
        if (backService != null) {
            if (BackService.isServiceRunning)
                setService(backService);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        backService = null;
    }

    /*  Animation Method    */
    private void favouriteBtnClicked(ImageView v) {
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

    private void unFavouriteClicked(View v) {
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

    ObjectAnimator o_animation_1, o_animation_2, o_animation_3, o_animation_1_alpha, o_animation_2_alpha, o_animation_3_alpha;
    public AnimatorSet animator;

    private void animateGesturePlayNext() {
        o_animation_1 = ObjectAnimator.ofFloat(song_art_card, "translationX", song_art_card.getTranslationX(), -song_art_card.getWidth());
        o_animation_2 = ObjectAnimator.ofFloat(bottom_song_name, "translationX", bottom_song_name.getTranslationX(), -bottom_song_name.getWidth());
        o_animation_3 = ObjectAnimator.ofFloat(bottom_song_artist, "translationX", bottom_song_artist.getTranslationX(), -bottom_song_artist.getWidth());
        o_animation_1_alpha = ObjectAnimator.ofFloat(song_art_card, "alpha", song_art_card.getAlpha(), 0);
        o_animation_2_alpha = ObjectAnimator.ofFloat(bottom_song_name, "alpha", bottom_song_name.getAlpha(), 0);
        o_animation_3_alpha = ObjectAnimator.ofFloat(bottom_song_artist, "alpha", bottom_song_artist.getAlpha(), 0);
        animator = new AnimatorSet();
        animator.setDuration(150);
        animator.playTogether(o_animation_1, o_animation_2, o_animation_3, o_animation_1_alpha, o_animation_2_alpha, o_animation_3_alpha);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                requireActivity().runOnUiThread(() -> nextButtonClicked());
                o_animation_1 = ObjectAnimator.ofFloat(song_art_card, "translationX", song_art_card.getWidth(), 0);
                o_animation_2 = ObjectAnimator.ofFloat(bottom_song_name, "translationX", bottom_song_name.getWidth(), 0);
                o_animation_3 = ObjectAnimator.ofFloat(bottom_song_artist, "translationX", bottom_song_artist.getWidth(), 0);
                o_animation_1_alpha = ObjectAnimator.ofFloat(song_art_card, "alpha", song_art_card.getAlpha(), 1f);
                o_animation_2_alpha = ObjectAnimator.ofFloat(bottom_song_name, "alpha", bottom_song_name.getAlpha(), 1f);
                o_animation_3_alpha = ObjectAnimator.ofFloat(bottom_song_artist, "alpha", bottom_song_artist.getAlpha(), 1f);
                animator = new AnimatorSet();
                animator.setDuration(200);
                animator.playTogether(o_animation_1, o_animation_2, o_animation_3, o_animation_1_alpha, o_animation_2_alpha, o_animation_3_alpha);
                animator.start();
            }
        });
        animator.start();
    }

    private void animateGesturePlayPrevious() {
        o_animation_1 = ObjectAnimator.ofFloat(song_art_card, "translationX", song_art_card.getTranslationX(), bottom_song_name.getWidth());
        o_animation_2 = ObjectAnimator.ofFloat(bottom_song_name, "translationX", bottom_song_name.getTranslationX(), bottom_song_name.getWidth());
        o_animation_3 = ObjectAnimator.ofFloat(bottom_song_artist, "translationX", bottom_song_artist.getTranslationX(), bottom_song_artist.getWidth());
        o_animation_1_alpha = ObjectAnimator.ofFloat(song_art_card, "alpha", song_art_card.getAlpha(), 0);
        o_animation_2_alpha = ObjectAnimator.ofFloat(bottom_song_name, "alpha", bottom_song_name.getAlpha(), 0);
        o_animation_3_alpha = ObjectAnimator.ofFloat(bottom_song_artist, "alpha", bottom_song_artist.getAlpha(), 0);
        animator = new AnimatorSet();
        animator.setDuration(150);
        animator.playTogether(o_animation_1, o_animation_2, o_animation_3, o_animation_1_alpha, o_animation_2_alpha, o_animation_3_alpha);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                requireActivity().runOnUiThread(() -> previousButtonClicked());
                o_animation_1 = ObjectAnimator.ofFloat(song_art_card, "translationX", -song_art_card.getWidth(), 0);
                o_animation_2 = ObjectAnimator.ofFloat(bottom_song_name, "translationX", -bottom_song_name.getWidth(), 0);
                o_animation_3 = ObjectAnimator.ofFloat(bottom_song_artist, "translationX", -bottom_song_artist.getWidth(), 0);
                o_animation_1_alpha = ObjectAnimator.ofFloat(song_art_card, "alpha", song_art_card.getAlpha(), 1f);
                o_animation_2_alpha = ObjectAnimator.ofFloat(bottom_song_name, "alpha", bottom_song_name.getAlpha(), 1f);
                o_animation_3_alpha = ObjectAnimator.ofFloat(bottom_song_artist, "alpha", bottom_song_artist.getAlpha(), 1f);
                animator = new AnimatorSet();
                animator.setDuration(200);
                animator.playTogether(o_animation_1, o_animation_2, o_animation_3, o_animation_1_alpha, o_animation_2_alpha, o_animation_3_alpha);
                animator.start();
            }
        });
        animator.start();
    }

    public void animateResetGesture() {
        // Animate back to the initial position
        o_animation_1 = ObjectAnimator.ofFloat(song_art_card, "translationX", song_art_card.getTranslationX(), 0f);
        o_animation_2 = ObjectAnimator.ofFloat(bottom_song_name, "translationX", bottom_song_name.getTranslationX(), 0f);
        o_animation_3 = ObjectAnimator.ofFloat(bottom_song_artist, "translationX", bottom_song_artist.getTranslationX(), 0f);
        o_animation_1_alpha = ObjectAnimator.ofFloat(song_art_card, "alpha", song_art_card.getAlpha(), 1f);
        o_animation_2_alpha = ObjectAnimator.ofFloat(bottom_song_name, "alpha", bottom_song_name.getAlpha(), 1f);
        o_animation_3_alpha = ObjectAnimator.ofFloat(bottom_song_artist, "alpha", bottom_song_artist.getAlpha(), 1f);
        animator = new AnimatorSet();
        animator.playTogether(o_animation_1, o_animation_2, o_animation_3, o_animation_1_alpha, o_animation_2_alpha, o_animation_3_alpha);
        animator.setDuration(200); // Set the animation duration for the reset
        animator.start();
    }

    /*  Background Task */
    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
        byte[] album_pic;
        MediaMetadataRetriever retriever;

        @Override
        protected byte[] doInBackground(MusicFiles... musicFiles) {
            try {
                retriever = new MediaMetadataRetriever();
                retriever.setDataSource(musicFiles[0].getPath());
                album_pic = retriever.getEmbeddedPicture();
                retriever.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return album_pic;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            try {
                if (!requireActivity().isDestroyed())
                    Glide.with(requireActivity())
                        .load(bytes)
                        .override(70, 70)
                        .placeholder(R.mipmap.ic_music)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(bottom_song_art);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}