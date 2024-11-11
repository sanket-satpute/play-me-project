package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.MainActivity.favouriteList;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.DAYS_OF_WEEK;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.month_names;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGARTIST;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGNAME;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;
import static com.sanket_satpute_20.playme.project.service.BackService.repeatBoolean;
import static com.sanket_satpute_20.playme.project.service.BackService.shuffleBoolean;
import static com.sanket_satpute_20.playme.project.service.BackService.song;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.extra_stuffes.CacheImageManager;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.service.BackService;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.Tag;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LockScreenActivity extends AppCompatActivity implements ServiceConnection {

    ConstraintLayout parent_layout;
    private float initialX;
    private ObjectAnimator animator;

    TextView time_txt, date_txt, song_name_txt, artist_txt, am_pm_txt;
    ImageView song_picture, favorite_btn, shuffle_btn, previous_btn, play_pause_btn, next_btn, repeat_btn;

    BackService backService;

    Handler time_handler;
    Runnable time_runnable;

    int song_position = -1;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals("song.mp3.changed")) {
                    doExtraExtraThings();
                } else if (intent.getAction().equals("action.Song_play_pause.Triggered")) {
                    playPauseActionTriggered();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showWhenLockAndTurnScreenOn();
        setContentView(R.layout.activity_lock_screen);
        initView();
        onClick();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(LockScreenActivity.this, BackService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter();
        filter.addAction("song.mp3.changed");
        filter.addAction("action.Song_play_pause.Triggered");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        doExtra();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        if (time_handler != null && time_runnable != null) {
            time_handler.removeCallbacks(time_runnable);
        }
    }

    private void showWhenLockAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
    }

    private void initView() {
        parent_layout = findViewById(R.id.parent_layout);
        time_txt = findViewById(R.id.time_txt);
        date_txt = findViewById(R.id.date_txt);
        song_name_txt = findViewById(R.id.song_name);
        artist_txt = findViewById(R.id.artist_name);
        song_picture = findViewById(R.id.song_picture);
        favorite_btn = findViewById(R.id.favorite_btn);
        shuffle_btn = findViewById(R.id.shuffle_btn);
        previous_btn = findViewById(R.id.previous_btn);
        play_pause_btn = findViewById(R.id.play_pause_btn);
        next_btn = findViewById(R.id.next_btn);
        repeat_btn = findViewById(R.id.replay_btn);
        am_pm_txt = findViewById(R.id.time_am_pm_txt);
    }

    private void doExtra() {
        if (shuffleBoolean) {
            shuffle_btn.setImageResource(R.drawable.shuffle_second_24);
            shuffle_btn.setColorFilter(previous_btn.getColorFilter());
        }
        else {
            shuffle_btn.setImageResource(R.drawable.nfill_shuffle_second_24);
            shuffle_btn.setColorFilter(previous_btn.getColorFilter());
        }
        if (shuffleBoolean) {
            shuffle_btn.setImageResource(R.drawable.shuffle_second_24);
            shuffle_btn.setColorFilter(previous_btn.getColorFilter());
        }
        else {
            shuffle_btn.setImageResource(R.drawable.nfill_shuffle_second_24);
            shuffle_btn.setColorFilter(previous_btn.getColorFilter());
        }
        doExtraTimeAndDate();
        doExtraExtraThings();
    }

    private void doExtraTimeAndDate() {
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int hour = (calendar.get(Calendar.HOUR) == 0) ? 12 : calendar.get(Calendar.HOUR);
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
        int am_pm_int = calendar.get(Calendar.AM_PM);

        String time_str = String.format(Locale.US, "%02d:%02d", hour, minutes);
        String date_str = String.format(Locale.US, "%s %d, %s", DAYS_OF_WEEK[day_of_week - 1], date, month_names[month]);
        date_txt.setText(date_str);
        time_txt.setText(time_str);
        am_pm_txt.setText((am_pm_int == Calendar.AM) ? "AM" : "PM");

//        AudioFile
//        Tag tag =

        time_handler = new Handler(Looper.myLooper());
        time_runnable = () -> {
            int minutes1 = calendar.get(Calendar.MINUTE);
            int hour1 = (calendar.get(Calendar.HOUR) == 0) ? 12 : calendar.get(Calendar.HOUR);
            int am_pm_int1 = calendar.get(Calendar.AM_PM);
            am_pm_txt.setText((am_pm_int1 == Calendar.AM) ? "AM" : "PM");
            String time_str1 = String.format(Locale.US, "%02d:%02d", hour1, minutes1);
            time_txt.setText(time_str1);
            time_handler.postDelayed(time_runnable, 1000);
        };
        time_handler.postDelayed(time_runnable, 60000);
    }

    SharedPreferences preferences;
    private void doExtraExtraThings() {
        if (preferences == null)
            preferences = getSharedPreferences("PLAYING", MODE_PRIVATE);
        String song_name_str = preferences.getString(SONGNAME, null);
        String artist_name_str = preferences.getString(SONGARTIST, null);
        song_position = preferences.getInt(SONGPOSITION, -1);
        song_name_txt.setText(song_name_str);
        artist_txt.setText(artist_name_str);
        if (song_position > -1 && song != null && song.size() > song_position) {
            if (favouriteList.contains(song.get(song_position))) {
                favorite_btn.setColorFilter(R.color.light_orangiesh);
                favorite_btn.setImageResource(R.drawable.heart_filled_icon_24);
            } else {
                favorite_btn.setImageTintList(previous_btn.getImageTintList());
                favorite_btn.setImageResource(R.drawable.heart_no_fill_24);
            }
        } else {
            favorite_btn.setImageTintList(previous_btn.getImageTintList());
            favorite_btn.setImageResource(R.drawable.heart_no_fill_24);
        }
        playPauseActionTriggered();
        loadSongImage();
    }

    private void playPauseActionTriggered() {
        if (backService != null) {
            if (backService.isPlaying()) {
                play_pause_btn.setImageResource(R.drawable.ic_round_pause_24);
            } else {
                play_pause_btn.setImageResource(R.drawable.ic_round_play_arrow_24);
            }
        } else {
            play_pause_btn.setImageResource(R.drawable.ic_round_play_arrow_24);
        }
    }

    private void loadSongImage() {
        Executor imageBackgroundExecutor = Executors.newSingleThreadExecutor();
        imageBackgroundExecutor.execute(() -> {
            try {
                Bitmap bitmap = CacheImageManager.getImage(LockScreenActivity.this, song.get(song_position));
                if (bitmap == null) {
                    loadSongImageFromBackground();
                } else {
                    runOnUiThread(() -> Glide.with(LockScreenActivity.this)
                            .load(bitmap)
                            .override(200, 200)
                            .placeholder(R.mipmap.ic_music)
                            .diskCacheStrategy( DiskCacheStrategy.ALL )
                            .into(song_picture));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadSongImageFromBackground() {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        MediaMetadataRetriever retriever;
        try {
            fileIO = song.get(song_position);
            path = fileIO.getPath();
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            album_pic = retriever.getEmbeddedPicture();
            retriever.release();

            if (album_pic != null)
                CacheImageManager.putImage(LockScreenActivity.this, fileIO, BitmapFactory.decodeByteArray(album_pic, 0, album_pic.length));
            runOnUiThread(() -> Glide.with(LockScreenActivity.this)
                    .load(album_pic)
                    .override(200, 200)
                    .placeholder(R.mipmap.ic_music)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(song_picture));
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onClick() {
        parent_layout.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = (int) event.getX();
                    if (animator != null && animator.isRunning()) {
                        animator.cancel();
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float currentX = event.getX();
                    float deltaX = currentX - initialX;
                    parent_layout.setTranslationX(deltaX);
                    return true;
                case MotionEvent.ACTION_UP:
                    float finalX = event.getX();
                    float deltaX2 = finalX - initialX;
                    if (deltaX2 < -100) { // Adjust this value to determine swipe distance
                        removeLockScreen();
                    } else {
                        // Animate back to the initial position
                        animateReset();
                    }
                    return true;
                default:
                    return false;
            }
        });

        favorite_btn.setOnClickListener(view -> {
            if (song != null) {
                if (song.size() > song_position) {
                    if (favouriteList.contains(song.get(song_position))) {
                        favouriteList.remove(song.get(song_position));
                        favorite_btn.setColorFilter(previous_btn.getColorFilter());
                        favorite_btn.setImageResource(R.drawable.heart_no_fill_24);
//                        unFavouriteClicked(bottom_favourite);
                    } else {
                        favouriteList.add(song.get(song_position));
                        favorite_btn.setImageResource(R.drawable.heart_filled_icon_24);
                        favorite_btn.setColorFilter(R.color.light_orangiesh);
//                        favouriteBtnClicked(bottom_favourite);
                    }
                }
            }
        });

        shuffle_btn.setOnClickListener(view -> {
            shuffleBoolean = !shuffleBoolean;
            if (shuffleBoolean) {
                shuffle_btn.setImageResource(R.drawable.shuffle_second_24);
                shuffle_btn.setColorFilter(previous_btn.getColorFilter());
            }
            else {
                shuffle_btn.setImageResource(R.drawable.nfill_shuffle_second_24);
                shuffle_btn.setColorFilter(previous_btn.getColorFilter());
            }
        });

        repeat_btn.setOnClickListener(view -> {
            repeatBoolean = !repeatBoolean;
            if (repeatBoolean) {
                repeat_btn.setColorFilter(previous_btn.getColorFilter());
                repeat_btn.setImageResource(R.drawable.fill_undo_and_repeat_alt_24);
            }
            else {
                repeat_btn.setColorFilter(previous_btn.getColorFilter());
                repeat_btn.setImageResource(R.drawable.undo_and_repeat_alt_24);
            }
        });

        previous_btn.setOnClickListener(view -> backService.setPreviousButtonClicked());

        play_pause_btn.setOnClickListener(view -> backService.play_pause());

        next_btn.setOnClickListener(view -> backService.setNextButtonClicked());
    }

    private void removeLockScreen() {
        // Animate the lock screen off the screen
        animator = ObjectAnimator.ofFloat(parent_layout, "translationX", parent_layout.getTranslationX(), -parent_layout.getWidth());
        animator.setDuration(500); // Set the animation duration
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                // Unlock the screen (e.g., navigate to home screen or your app)
                // Implement your unlock logic here
                finish(); // Finish the activity to remove it from the back stack
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        animator.start();
    }

    private void animateReset() {
        // Animate back to the initial position
        animator = ObjectAnimator.ofFloat(parent_layout, "translationX", parent_layout.getTranslationX(), 0f);
        animator.setDuration(200); // Set the animation duration for the reset
        animator.start();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        BackService.LocalBinder binder = (BackService.LocalBinder) service;
        backService = binder.getService();
        playPauseActionTriggered();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        backService = null;
    }
}