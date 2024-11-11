package com.sanket_satpute_20.playme.project.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sanket_satpute_20.playme.MainActivity.favouriteList;
import static com.sanket_satpute_20.playme.project.activity.PlayActivity.isRoundBackgroundOn;
import static com.sanket_satpute_20.playme.project.activity.PlayActivity.which_act_background_round;
import static com.sanket_satpute_20.playme.project.activity.PlayerDesignActivity.is_visulizer_constraint_visible;
import static com.sanket_satpute_20.playme.project.activity.PlayerDesignActivity.vis_color;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.SpeedAndPitchLayoutFragment.ACTION_SPEED_AND_PITCH;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.SpeedAndPitchLayoutFragment.SPEED_PITCH_ON;
import static com.sanket_satpute_20.playme.project.enums.ROUND_ACT_BACKGROUNDS.GRADIENT;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.iSPitchON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.iSSpeedON;
import static com.sanket_satpute_20.playme.project.fragments.PlayLyricsFragment2.EXTRA_STUFFES;
import static com.sanket_satpute_20.playme.project.fragments.PlayLyricsFragment2.INTENT_ACTION;
import static com.sanket_satpute_20.playme.project.fragments.PlayLyricsFragment2.IS_ON;
import static com.sanket_satpute_20.playme.project.fragments.PlayLyricsFragment2.PLAY_PAUSE_EXTRA;
import static com.sanket_satpute_20.playme.project.fragments.PlayLyricsFragment2.REPEAT_EXTRA;
import static com.sanket_satpute_20.playme.project.fragments.PlayLyricsFragment2.SHUFFLE_EXTRA;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGARTIST;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGNAME;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPATH;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;
import static com.sanket_satpute_20.playme.project.service.BackService.currentTime;
import static com.sanket_satpute_20.playme.project.service.BackService.repeatBoolean;
import static com.sanket_satpute_20.playme.project.service.BackService.shuffleBoolean;
import static com.sanket_satpute_20.playme.project.service.BackService.timer_set;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.ColorFilter;
import android.media.MediaMetadataRetriever;
import android.media.audiofx.AudioEffect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.chibde.visualizer.BarVisualizer;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.activity.SoundEffectsActivity;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.SleepTimerLayoutFragment;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.SpeedAndPitchLayoutFragment;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.service.BackService;
import com.google.android.material.card.MaterialCardView;

import java.util.HashMap;

public class PlayOptionsFragment2 extends Fragment implements ServiceConnection, GestureDetector.OnGestureListener {

//    Broadcast Massages to PlayLyricsFragment2
    public static final String OPTION_REPEAT_EXTRA = "OPTION_REPEAT_EXTRA";
    public static final String OPTION_SHUFFLE_EXTRA = "OPTION_SHUFFLE_EXTRA";
    public static final String OPTION_PLAY_PAUSE_EXTRA = "OPTION_PLAY_PAUSE_EXTRA";
    public static final String OPTION_EXTRA_STUFFES = "OPTION_EXTRA_STUFFES";
    public static final String OPTION_INTENT_ACTION = "play_option.INTENT_ACTION.BUTTON.CLICKED";
    public static final String OPTION_IS_ON = "OPTION_IS_ON";

    HashMap<Integer, byte[]> pictures = new HashMap<>();

    ImageView first_art, song_art, second_art, previous, play_pause, next, repeat, shuffle,
    speed_pitch, equlizer_setting, sound_effect, fevourite, sleep_timer;
    CardView first_card, main_card, second_card;
    TextView song_name, artist_name, timer, duration;
    SeekBar seekBar;

    TextView sleep_timer_txt;

    BackService service;

    boolean prev, nex;

    BarVisualizer visulizer;

    String song, artist, path;
    int position, pos_img_x;
    int gross_position;
    ColorFilter color;
    int default_color, base_color;
    int white_color = 0xffffffff;

    MaterialCardView lyrics_card;
    GestureDetector gesture;
    boolean dilog_expanded = false, is_pitch_on = false;

    Intent lyrics_intent = new Intent();
    int []lyrics_card_locations = new int[2];
    int lyrics_location_y;

    BroadcastReceiver reciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("song.mp3.changed")) {
                pictures.clear();
                setSeekBar();
                getSharedPref();
                if (is_visulizer_constraint_visible)
                    setVisulizer(service);
            } else if (intent.getAction().equals(ACTION_SPEED_AND_PITCH)) {
                is_pitch_on = intent.getBooleanExtra(SPEED_PITCH_ON, false);
                if (is_pitch_on)
                    speed_pitch.setColorFilter(ACCENT_COLOR);
                else
                    speed_pitch.setColorFilter(default_color);
            }
            if (intent.getAction().equals("player.android.timer.action.performed")) {
                if (timer_set)
                    timerIsSeted();
                else
                    timerNotSeted();
            }
            if (INTENT_ACTION.equals(intent.getAction())) {
                switch (intent.getStringExtra(EXTRA_STUFFES)) {
                    case PLAY_PAUSE_EXTRA:
//                        play pause
                        if(intent.getBooleanExtra(IS_ON, false)) {
                            play_pause.setImageResource(R.drawable.ic_round_pause_24);
                        } else {
                            play_pause.setImageResource(R.drawable.ic_round_play_arrow_24);
                        }
                        break;
                    case SHUFFLE_EXTRA:
//                        shuffle
                        if(intent.getBooleanExtra(IS_ON, false)) {
                            shuffle.setColorFilter(ACCENT_COLOR);
                            shuffle.setImageResource(R.drawable.shuffle_second_24);
                        } else {
                            shuffle.setColorFilter(default_color);
                            shuffle.setImageResource(R.drawable.nfill_shuffle_second_24);
                        }
                        break;
                    case REPEAT_EXTRA:
//                        repeat
                        if(intent.getBooleanExtra(IS_ON, false)) {
                            repeat.setColorFilter(ACCENT_COLOR);
                            repeat.setImageResource(R.drawable.fill_undo_and_repeat_alt_24);
                        } else {
                            repeat.setColorFilter(default_color);
                            repeat.setImageResource(R.drawable.undo_and_repeat_alt_24);
                        }
                        break;
                }
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_options2, container, false);
        gesture = new GestureDetector(this);
        lyrics_intent.setAction(OPTION_INTENT_ACTION);
        initViews(view);
        onclick();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                base_color = 0xddffffff;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                base_color = 0xff222222;
                break;
        }
        if (is_visulizer_constraint_visible) {
            lyrics_card.setVisibility(View.GONE);
            visulizer.setVisibility(View.VISIBLE);
        } else {
            lyrics_card.setVisibility(View.VISIBLE);
            visulizer.setVisibility(View.GONE);
        }
        Intent intent = new Intent(getContext(), BackService.class);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SPEED_AND_PITCH);
        filter.addAction("song.mp3.changed");
        filter.addAction("player.android.timer.action.performed");
        filter.addAction(INTENT_ACTION);
        requireActivity().bindService(intent, PlayOptionsFragment2.this, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(reciver, filter);
        getSharedPref();
        getPreviousSetting();
        play_pause.setColorFilter(ACCENT_COLOR);
        seekBar.setProgressTintList(ColorStateList.valueOf(ACCENT_COLOR));
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unbindService(this);
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(reciver);
        deleteVisulizer();
    }

    public void initViews(View view) {
        first_art = view.findViewById(R.id.first_art);
        song_art = view.findViewById(R.id.song_art);
        second_art = view.findViewById(R.id.second_art);
        previous = view.findViewById(R.id.previous);
        next = view.findViewById(R.id.next);
        play_pause = view.findViewById(R.id.play_pause);
        repeat = view.findViewById(R.id.repeat);
        shuffle = view.findViewById(R.id.shuffle);
        first_card = view.findViewById(R.id.first_card);
        main_card = view.findViewById(R.id.main_card);
        second_card = view.findViewById(R.id.second_card);
        song_name = view.findViewById(R.id.song_name);
        artist_name = view.findViewById(R.id.artist_name);
        timer = view.findViewById(R.id.timer);
        sleep_timer = view.findViewById(R.id.sleep_timer);
        duration = view.findViewById(R.id.duration);
        seekBar = view.findViewById(R.id.seekbar);
        lyrics_card = view.findViewById(R.id.third_relative);
        visulizer = view.findViewById(R.id.waveVisulizer);
        speed_pitch = view.findViewById(R.id.speed_pitch);
        equlizer_setting = view.findViewById(R.id.equlizer_setting);
        sound_effect = view.findViewById(R.id.sound_effect);
        fevourite = view.findViewById(R.id.fevourite);
        sleep_timer_txt = view.findViewById(R.id.sleep_timer_txt);

        color = shuffle.getColorFilter();

        lyrics_card.getLocationOnScreen(lyrics_card_locations);
        lyrics_location_y = lyrics_card_locations[1];
    }

    public void setSeekBar() {
        seekBar.setMax(service.getDuration());
        duration.setText(service.getTime(service.getDuration()));
        Handler handler = new Handler();
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(service.getCurrentPosition());
                timer.setText(service.getTime(service.getCurrentPosition()));
                handler.postDelayed(this, 1000);
            }
        });

        if (service.isPlaying())
            play_pause.setImageResource(R.drawable.ic_round_pause_24);
        else
            play_pause.setImageResource(R.drawable.ic_round_play_arrow_24);
    }

    public void getSharedPref() {

        pictures.clear();
        pos_img_x = 0;

        if (shuffleBoolean) {
            shuffle.setColorFilter(ACCENT_COLOR);
            shuffle.setImageResource(R.drawable.shuffle_second_24);
        }
        if (repeatBoolean) {
            repeat.setColorFilter(ACCENT_COLOR);
            repeat.setImageResource(R.drawable.fill_undo_and_repeat_alt_24);
        }


        SharedPreferences preferences = requireContext().getSharedPreferences("PLAYING", MODE_PRIVATE);
        song = preferences.getString(SONGNAME, "Not Found");
        artist = preferences.getString(SONGARTIST, "Not Found");
        path = preferences.getString(SONGPATH, null);
        position = preferences.getInt(SONGPOSITION, 0);

        song_name.setText(song);
        artist_name.setText(artist);
        song_name.setSelected(true);

        if (BackService.song != null) {
        if (BackService.song.size() > 0) {
            try {
                if (favouriteList.contains(BackService.song.get(position))) {
                    fevourite.setColorFilter(ACCENT_COLOR);
                    fevourite.setImageResource(R.drawable.heart_filled_icon_24);
                } else {
                    fevourite.setColorFilter(default_color);
                    fevourite.setImageResource(R.drawable.heart_no_fill_24);
                }
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                e.printStackTrace();
            }

            prev = (position) > 0;

            nex = (position + 1) < BackService.song.size();

            if (position == 0)
                gross_position = 0;
            else
                gross_position = 1;

            if (BackService.song.size() > 0) {
                if (prev) {
                    MyImageLoad load0 = new MyImageLoad();
                    load0.execute(BackService.song.get(position - 1));
                    first_card.setVisibility(View.VISIBLE);
                } else {
                    first_card.setVisibility(View.GONE);
                }

                MyImageLoad load1 = new MyImageLoad();
                load1.execute(BackService.song.get(position));

                if (nex) {
                    MyImageLoad load2 = new MyImageLoad();
                    load2.execute(BackService.song.get(position + 1));
                    second_card.setVisibility(View.VISIBLE);
                } else {
                    second_card.setVisibility(View.GONE);
                }
            }
        }
        }
    }

    private void getPreviousSetting () {
        if (timer_set) {
            sleep_timer.setVisibility(View.GONE);
            sleep_timer_txt.setVisibility(View.VISIBLE);
            sleep_timer_txt.setTextColor(ACCENT_COLOR);
            timer_on_update_thread();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onclick() {

        lyrics_card.setOnTouchListener((view, motionEvent) -> gesture.onTouchEvent(motionEvent));

        fevourite.setOnClickListener(view -> {
            if (BackService.song != null) {
                if (BackService.song.size() > 0) {
                    if (favouriteList.contains(BackService.song.get(position))) {
                        favouriteList.remove(BackService.song.get(position));
                        fevourite.setImageResource(R.drawable.heart_no_fill_24);
                        fevourite.setColorFilter(default_color);
                        unFevouriteClicked(fevourite);
                    } else {
                        favouriteList.add(BackService.song.get(position));
                        fevourite.setImageResource(R.drawable.heart_filled_icon_24);
                        fevourite.setColorFilter(ACCENT_COLOR);
                        fevouriteBtnClicked(fevourite);
                    }
                }
            }
        });

        play_pause.setOnClickListener(view -> {
            lyrics_intent.putExtra(OPTION_EXTRA_STUFFES, OPTION_PLAY_PAUSE_EXTRA);
            if (BackService.isServiceRunning) {
                if (service.isPlaying()) {
                    service.pause();
                    play_pause.setImageResource(R.drawable.ic_round_play_arrow_24);
                    lyrics_intent.putExtra(OPTION_IS_ON, false);
                } else {
                    service.play();
                    play_pause.setImageResource(R.drawable.ic_round_pause_24);
                    lyrics_intent.putExtra(OPTION_IS_ON, true);
                }
            } else {
                Intent intent = new Intent(requireActivity(), BackService.class);
                intent.putExtra("position" , position);
                requireActivity().startService(intent);
                lyrics_intent.putExtra(OPTION_IS_ON, true);
            }
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(lyrics_intent);
        });

        next.setOnClickListener(view -> {
            if (BackService.isServiceRunning) {
                service.setNextButtonClicked();
            } else {
                if (BackService.song != null) {
                    if (BackService.song.size() > 0) {
                        Intent intent = new Intent(requireActivity(), BackService.class);
                        intent.putExtra("position", (BackService.song.size() > 1) ? position + 1 : position);
                        requireActivity().startService(intent);
                    }
                }
            }
        });

        previous.setOnClickListener(view -> {
            if (BackService.isServiceRunning) {
                service.setPreviousButtonClicked();
            } else {
                if (BackService.song != null) {
                    if (BackService.song.size() > 0) {
                        Intent intent = new Intent(requireActivity(), BackService.class);
                        intent.putExtra("position", (BackService.song.size() > 1) ? position - 1 : position);
                        requireActivity().startService(intent);
                    }
                }
            }
        });

        shuffle.setOnClickListener(view -> {
            lyrics_intent.putExtra(OPTION_EXTRA_STUFFES, OPTION_SHUFFLE_EXTRA);
            shuffleBoolean = !shuffleBoolean;
            if (shuffleBoolean) {
                shuffle.setColorFilter(ACCENT_COLOR);
                shuffle.setImageResource(R.drawable.shuffle_second_24);
            }
            else {
                shuffle.setColorFilter(default_color);
                shuffle.setImageResource(R.drawable.nfill_shuffle_second_24);
            }
            lyrics_intent.putExtra(OPTION_IS_ON, shuffleBoolean);
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(lyrics_intent);
        });

        repeat.setOnClickListener(view -> {
            lyrics_intent.putExtra(OPTION_EXTRA_STUFFES, OPTION_REPEAT_EXTRA);
            repeatBoolean = !repeatBoolean;
            if (repeatBoolean)
            {
                repeat.setColorFilter(ACCENT_COLOR);
                repeat.setImageResource(R.drawable.fill_undo_and_repeat_alt_24);
            }
            else {
                repeat.setColorFilter(default_color);
                repeat.setImageResource(R.drawable.undo_and_repeat_alt_24);
            }
            lyrics_intent.putExtra(OPTION_IS_ON, repeatBoolean);
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(lyrics_intent);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    service.seekTO(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        speed_pitch.setOnClickListener(view -> setPitching());

        equlizer_setting.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, service.getAudioSessionId());
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, requireContext().getPackageName());
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                startActivityForResult(intent, 13);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        sound_effect.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), SoundEffectsActivity.class);
            requireActivity().startActivity(intent);
        });

        sleep_timer.setOnClickListener(view -> {
            SleepTimerLayoutFragment sleepTimerLayoutFragment = new SleepTimerLayoutFragment();
            sleepTimerLayoutFragment.show(requireActivity().getSupportFragmentManager(), "ABC");
        });

        sleep_timer_txt.setOnClickListener(view -> {
            SleepTimerLayoutFragment sleepTimerLayoutFragment = new SleepTimerLayoutFragment();
            sleepTimerLayoutFragment.show(requireActivity().getSupportFragmentManager(), "ABC");
        });
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        BackService.LocalBinder binder = (BackService.LocalBinder) iBinder;
        service = binder.getService();
        setSeekBar();
        if (is_visulizer_constraint_visible)
            setVisulizer(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        bottomSheetUpDownMethod(v1);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
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
                return album_pic;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.S)
        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            pictures.put(pos_img_x, bytes);
            pos_img_x++;
            try {
                if (pictures.containsKey(0))
                    Glide.with(requireContext())
                            .asBitmap()
                            .load(pictures.get(0))
                            .override(200, 200)
                            .placeholder(R.mipmap.ic_music)
                            .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                            .into(first_art);

                if (pictures.containsKey(gross_position))
                    Glide.with(requireContext())
                            .asBitmap()
                            .load(pictures.get(gross_position))
                            .override(300, 300)
                            .placeholder(R.mipmap.ic_music)
                            .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                            .into(song_art);

                if (pictures.containsKey(gross_position + 1))
                    Glide.with(requireContext()).asBitmap()
                            .load(pictures.get(gross_position + 1))
                            .override(200, 200)
                            .placeholder(R.mipmap.ic_music)
                            .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                            .into(second_art);
                if (pictures.get(gross_position) != null && isRoundBackgroundOn) {
                    default_color = white_color;
                } else if (isRoundBackgroundOn && which_act_background_round == GRADIENT) {
                    default_color = white_color;
                } else {
                    default_color = base_color;
                }
                shuffle.setColorFilter((shuffleBoolean) ? ACCENT_COLOR : default_color);
                repeat.setColorFilter((repeatBoolean) ? ACCENT_COLOR : default_color);
                next.setColorFilter(default_color);
                previous.setColorFilter(default_color);
                sound_effect.setColorFilter(default_color);
                speed_pitch.setColorFilter((iSSpeedON || iSPitchON) ? ACCENT_COLOR : default_color);
                sleep_timer.setColorFilter(default_color);
                equlizer_setting.setColorFilter(default_color);
                timer.setTextColor(default_color);
                duration.setTextColor(default_color);
                song_name.setTextColor(default_color);
                artist_name.setTextColor(default_color);
                fevourite.setColorFilter((favouriteList.contains(BackService.song.get(position))) ? ACCENT_COLOR : default_color);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*  Other Method's  */

    public void setPitching() {
        SpeedAndPitchLayoutFragment speedAndPitchLayoutFragment = new SpeedAndPitchLayoutFragment();
        speedAndPitchLayoutFragment.show(requireActivity().getSupportFragmentManager(), "ABC");
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

    private void setVisulizer(BackService service) {
        if (visulizer != null) {
            visulizer.release();
        }
        if (service != null) {
            try {
                if (service.getAudioSessionId() != -1) {
                    visulizer.setEnabled(false);
                    visulizer.setPlayer(service.getAudioSessionId());
                    visulizer.setColor(vis_color);
                }
            } catch (Exception e) {
                e.printStackTrace();
                    try {
                        if (visulizer != null) {
                            visulizer.setEnabled(false);
                            visulizer.setPlayer(service.getAudioSessionId());
                            visulizer.setColor(vis_color);
                        }
                    } catch (Exception b) {
                        b.printStackTrace();
                    }
            }
        }
    }

    private void deleteVisulizer() {
        if (visulizer != null)
            visulizer.release();
    }

    private void timerIsSeted() {
        ObjectAnimator image_alpha = ObjectAnimator.ofFloat(sleep_timer, "alpha", 1f, 0f);
        image_alpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                sleep_timer.setVisibility(View.GONE);
                sleep_timer_txt.setVisibility(View.VISIBLE);
                sleep_timer_txt.setTextColor(ACCENT_COLOR);
                sleep_timer_txt.setAlpha(1f);
                ObjectAnimator text_trans_y = ObjectAnimator.ofFloat(sleep_timer_txt, "translationY", 150f, 0);
                text_trans_y.setDuration(200);
                text_trans_y.start();
                timer_on_update_thread();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        image_alpha.setDuration(150);
        image_alpha.start();
    }

    private void timerNotSeted() {
        ObjectAnimator txt_alpha = ObjectAnimator.ofFloat(sleep_timer_txt, "alpha", 1f, 0f);
        txt_alpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                sleep_timer_txt.setVisibility(View.GONE);
                sleep_timer.setVisibility(View.VISIBLE);
                sleep_timer.setAlpha(1f);
                ObjectAnimator img_trans_y = ObjectAnimator.ofFloat(sleep_timer, "translationY", 150f, 0);
                img_trans_y.setDuration(200);
                img_trans_y.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        txt_alpha.setDuration(150);
        txt_alpha.start();
    }

    private void timer_on_update_thread() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(timer_set) {
                    if (currentTime <= 1001) {
                        handler.removeCallbacks(this);
                        timerNotSeted();
                        play_pause.setImageResource(R.drawable.ic_round_play_arrow_24);
                    }
                    else if (currentTime <= 60000) {
                        setMillis();
                    } else if (currentTime <= 3600000) {
                        setMin();
                    } else {
                        setHrs();
                    }
                    handler.postDelayed(this, 1000);
                }
            }
        } , 0);
    }

    private void setMillis() {
        sleep_timer_txt.setText(getSecs((int)currentTime));
    }

    @SuppressLint("SetTextI18n")
    private void setMin() {
        sleep_timer_txt.setText(getMins((int)currentTime) + ":"
                + getSecs((int)currentTime));
    }

    @SuppressLint("SetTextI18n")
    private void setHrs() {
        sleep_timer_txt.setText(getHours((int)currentTime) + ":" + getMins((int)currentTime) + ":"
                + getSecs((int)currentTime));
    }

    private int getHours(int time) {
        return ((time / (60*60*1000)) % 24);
    }

    private String getMins(int time) {
        int mm = ((time / (1000*60)) % 60);
        if (mm >= 0 && mm <= 9)
            return "0"+mm;
        return String.valueOf((time / (1000*60) % 60));
    }

    private String getSecs(int time) {
        int ss = (time / 1000) % 60;
        if (ss >= 0 && ss <= 9)
            return "0"+ss;
        return String.valueOf((time / 1000) % 60);
    }

    private void bottomSheetUpDownMethod(float v1) {
        if (v1 > 0) {
            lyrics_card.animate().translationY(-(lyrics_card.getHeight() / 1.5f));
            dilog_expanded = true;
        } else if (v1 < 0){
            lyrics_card.animate().translationY(lyrics_location_y);
            dilog_expanded = false;
        }
    }

}