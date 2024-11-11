package com.sanket_satpute_20.playme.project.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sanket_satpute_20.playme.MainActivity.RECENT_SEEK_POSITION_SONG;
import static com.sanket_satpute_20.playme.MainActivity.favouriteList;
import static com.sanket_satpute_20.playme.project.activity.PlayActivity.isRoundBackgroundOn;
import static com.sanket_satpute_20.playme.project.activity.PlayActivity.which_act_background_round;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.SpeedAndPitchLayoutFragment.ACTION_SPEED_AND_PITCH;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.SpeedAndPitchLayoutFragment.SPEED_PITCH_ON;
import static com.sanket_satpute_20.playme.project.enums.PlayActBottom.FRAMES;
import static com.sanket_satpute_20.playme.project.enums.PlayActBottom.LIST;
import static com.sanket_satpute_20.playme.project.enums.ROUND_ACT_BACKGROUNDS.BLUR;
import static com.sanket_satpute_20.playme.project.enums.ROUND_ACT_BACKGROUNDS.COLORED;
import static com.sanket_satpute_20.playme.project.enums.ROUND_ACT_BACKGROUNDS.GRADIENT;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.SONG_SEEK_CURRENT_POSITION;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.iSPitchON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.iSSpeedON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.isUsingSystemEqulizer;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.recent_duration;
import static com.sanket_satpute_20.playme.project.recycler_views.DilogRecycle.POSITION;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGARTIST;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGNAME;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPATH;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;
import static com.sanket_satpute_20.playme.project.service.BackService.SONG_DURATION_PREF;
import static com.sanket_satpute_20.playme.project.service.BackService.repeatBoolean;
import static com.sanket_satpute_20.playme.project.service.BackService.shuffleBoolean;
import static com.sanket_satpute_20.playme.project.service.BackService.song;
import static com.sanket_satpute_20.playme.project.service.BackService.timer_set;

import android.animation.AnimatorInflater;
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
import android.graphics.Color;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.activity.EqulizerActivity;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.SleepTimerLayoutFragment;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.SpeedAndPitchLayoutFragment;
import com.sanket_satpute_20.playme.project.enums.PlayActBottom;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.recycler_views.CurrentPlayingRecycle;
import com.sanket_satpute_20.playme.project.recycler_views.DilogRecycle;
import com.sanket_satpute_20.playme.project.service.BackService;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import me.tankery.lib.circularseekbar.CircularSeekBar;

public class PlayOptionFragment extends Fragment implements ServiceConnection, GestureDetector.OnGestureListener {

    public static final String UPDOWNREQUEST = "UPDOWNREQUEST";
    View view;
    AlertDialog alert;

    GestureDetector gesture;

    FrameLayout frameLayout;
    CoordinatorLayout co_bottom_layout;
    float translationY, default_Y;

    // Service
    BackService service;

    ImageView next, previous, isSongChanged, favourite, shuffle, repeat;
    TextView songName, songArtist;
    String song_name, artist_name, song_path;
    int position;
    CircularSeekBar seekBar;
    CircleImageView song_art;

    FloatingActionButton play_pause;

    String from;

    int white_color = 0xffffffff;
    int default_color = 0xffc1c1c1;
    int base_color = default_color;

    public static boolean expand_visible_disable;

    private boolean isExpanded = false;
    public static PlayActBottom is_which_bottom = FRAMES;
    MaterialCardView sleep_timer, equlizer, sound_effect, speed_pitch, expander;
    ImageView expander_img, speed_pitch_img, equlizer_img, sound_effect_img, sleep_timer_img;

    /*  bottom song_list_bottom_fragment_disclosed  */
    ConstraintLayout third_relative_list;
    MaterialCardView bottom_list_parent_card, bottom_list_fragment_divider;
    RecyclerView bottom_list_recycler_view;

    int b_s_d_height, b_s_d_y, expand_btn_h, expand_btn_position_x;
    boolean dialog_expanded;
    int []expand_btn_positions = new int[2];


    //BroadCastReceiver
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("ResourceType")
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "song_changed_dilog_recycler":
                    if (alert != null) {
                        alert.dismiss();
                        position = intent.getIntExtra(POSITION, 0);
                        playDilogSongs(position);
                    }
                    break;
                case "from.back.play_pause.activated":
                    if (service != null) {
                        if (service.isPlaying())
                            play_pause.setImageResource(R.drawable.ic_round_pause_24);
                        else
                            play_pause.setImageResource(R.drawable.ic_round_play_arrow_24);
                    }
                    break;
                case ACTION_SPEED_AND_PITCH:
                    if (intent.getBooleanExtra(SPEED_PITCH_ON, false))
                        speed_pitch_img.setColorFilter(ACCENT_COLOR);
                    else
                        speed_pitch_img.setColorFilter(default_color);
                    break;
                case "player.android.timer.action.performed":
                    if (timer_set) {
                        sleep_timer_img.setColorFilter(ACCENT_COLOR);
                    } else {
                        sleep_timer_img.setColorFilter(default_color);
                    }
                    break;
            }
            if (intent.getAction().equals("song.mp3.changed")) {
                setSeek(service);
                getSharedPref();
                if (is_which_bottom != null) {
                    if (is_which_bottom == LIST)
                        setBottomSongListRecycler();
                }
            }
            else if (intent.getAction().equals("play.activity.more.clicked")) {
                if (is_which_bottom == FRAMES) {
                    showSongDilogFrame();
                } else {
                    showBottomSheetList();
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_play_option, container, false);
        gesture = new GestureDetector(requireActivity(), this);
        initViews(view);
        onClick();
        setStarterExpander();
        setBottom(is_which_bottom);
        setRecentDefaultProgress();
        return view;
    }

    private void setRecentDefaultProgress() {
        if (SONG_SEEK_CURRENT_POSITION == 0 && recent_duration == 0) {
            ArrayList<Long> seeks = getRecentSeekPosition();
            SONG_SEEK_CURRENT_POSITION = seeks.get(0);
            recent_duration = seeks.get(1);
        }
        if (recent_duration > SONG_SEEK_CURRENT_POSITION) {
            seekBar.setMax((int) recent_duration);
            seekBar.setProgress((int) SONG_SEEK_CURRENT_POSITION);
        }
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
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                base_color = 0xddffffff;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                base_color = 0xdd222222;
                break;
        }
        if (expand_visible_disable) {
            expander.setVisibility(View.VISIBLE);
        } else {
            expander.setVisibility(View.GONE);
        }
        Intent intent = new Intent(getContext(), BackService.class);
        requireActivity().bindService(intent, PlayOptionFragment.this, Context.BIND_AUTO_CREATE);
        getSharedPref();
        doExtra();
        checkPreviousOn();
        from = BackService.from;
        IntentFilter filter = new IntentFilter();
        filter.addAction("song.mp3.changed");
        filter.addAction("play.activity.more.clicked");
        filter.addAction("from.back.play_pause.activated");
        filter.addAction(ACTION_SPEED_AND_PITCH);
        filter.addAction("song_changed_dilog_recycler");
        filter.addAction("player.android.timer.action.performed");
        filter.addAction("first_player.activity.up.down.called");
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(alert != null) {
            alert.dismiss();
        }
        requireActivity().unbindService(this);
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver);
    }

    private void initViews(View view) {
        song_art = view.findViewById(R.id.song_art);
        seekBar = view.findViewById(R.id.seekBar);
        songName = view.findViewById(R.id.song_name_play_option);
        songArtist = view.findViewById(R.id.artist_name_play_option);
        play_pause = view.findViewById(R.id.play_pause_fab);
        next = view.findViewById(R.id.next_song);
        previous = view.findViewById(R.id.previous_song);
        isSongChanged = view.findViewById(R.id.is_song_chanded);
        favourite = view.findViewById(R.id.fevourite);
        repeat = view.findViewById(R.id.repeat_song);
        shuffle = view.findViewById(R.id.shuffle_song);
        frameLayout = view.findViewById(R.id.last_frame_layout);
        co_bottom_layout = view.findViewById(R.id.third_relative);

        /*  Extra Card view's  */
        sound_effect = view.findViewById(R.id.sound_effect);
        equlizer = view.findViewById(R.id.equlizer);
        speed_pitch = view.findViewById(R.id.speed_pitch);
        sleep_timer = view.findViewById(R.id.sleep_timer);
        expander = view.findViewById(R.id.expand);

        /*  Extra Images    */
        expander_img = view.findViewById(R.id.expander_img);
        sound_effect_img = view.findViewById(R.id.sound_effect_img);
        sleep_timer_img = view.findViewById(R.id.sleep_timer_img);
        equlizer_img = view.findViewById(R.id.equlizer_img);
        speed_pitch_img = view.findViewById(R.id.speed_pitch_img);

        /*  song list bottom fragment   */
        bottom_list_parent_card = view.findViewById(R.id.parent_frame_layout);
        bottom_list_recycler_view = view.findViewById(R.id.bottom_list_recyclerview);
        third_relative_list = view.findViewById(R.id.third_relative_list);
        bottom_list_fragment_divider = view.findViewById(R.id.divider_first);

        expander.getLocationOnScreen(expand_btn_positions);
        expand_btn_position_x = expand_btn_positions[0];
    }


    private void getSharedPref() {
        SharedPreferences preferences = requireContext().getSharedPreferences("PLAYING", MODE_PRIVATE);
        song_name = preferences.getString(SONGNAME, "Not Found");
        artist_name = preferences.getString(SONGARTIST, "Not Found");
        song_path = preferences.getString(SONGPATH, null);
        position = preferences.getInt(SONGPOSITION, 0);

        try {
            if (song.size() > 0) {
            if (favouriteList.contains(song.get(position))) {
                favourite.setColorFilter(ACCENT_COLOR);
                favourite.setImageResource(R.drawable.heart_filled_icon_24);
            } else {
                favourite.setColorFilter(default_color);
                favourite.setImageResource(R.drawable.heart_no_fill_24);
            }
            MyImageLoad load = new MyImageLoad();
            load.execute(song.get(position));
        }
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            e.printStackTrace();
        }
        doAction();
    }

    private void doExtra() {
        if (shuffleBoolean) {
            shuffle.setColorFilter(ACCENT_COLOR);
            shuffle.setImageResource(R.drawable.shuffle_second_24);
        }
        if (repeatBoolean) {
            repeat.setColorFilter(ACCENT_COLOR);
            repeat.setImageResource(R.drawable.fill_undo_and_repeat_alt_24);
        }
    }

    private void onClick() {

        play_pause.setOnClickListener(view -> {
            if (BackService.isServiceRunning) {
                if (service.isPlaying()) {
                    play_pause.setImageResource(R.drawable.ic_round_play_arrow_24);
                    service.pause();
                } else {
                    play_pause.setImageResource(R.drawable.ic_round_pause_24);
                    service.play();
                }
            } else {
                Intent intent = new Intent(requireActivity(), BackService.class);
                intent.putExtra("position", position);
                if (recent_duration > SONG_SEEK_CURRENT_POSITION) {
                    intent.putExtra("seekTO", SONG_SEEK_CURRENT_POSITION);
                }
                requireActivity().startService(intent);
            }
        });

        shuffle.setOnClickListener(view -> {
            shuffleBoolean = !shuffleBoolean;
            if (shuffleBoolean) {
                shuffle.setImageResource(R.drawable.shuffle_second_24);
                shuffle.setColorFilter(ACCENT_COLOR);
            }
            else {
                shuffle.setImageResource(R.drawable.nfill_shuffle_second_24);
                shuffle.setColorFilter(default_color);
            }
        });

        repeat.setOnClickListener(view -> {
            repeatBoolean = !repeatBoolean;
            if (repeatBoolean) {
                repeat.setColorFilter(ACCENT_COLOR);
                repeat.setImageResource(R.drawable.fill_undo_and_repeat_alt_24);
            }
            else {
                repeat.setColorFilter(default_color);
                repeat.setImageResource(R.drawable.undo_and_repeat_alt_24);
            }
        });

        favourite.setOnClickListener(view -> {
            if (song != null) {
                if (song.size() > 0) {
                    if (favouriteList.contains(song.get(position))) {
                        favouriteList.remove(song.get(position));
                        favourite.setImageResource(R.drawable.heart_no_fill_24);
                        unFevouriteClicked(favourite);
                        favourite.setColorFilter(default_color);
                    } else {
                        favouriteList.add(song.get(position));
                        favourite.setImageResource(R.drawable.heart_filled_icon_24);
                        fevouriteBtnClicked(favourite);
                        favourite.setColorFilter(ACCENT_COLOR);
                    }
                }
            }
        });

        Handler handler = new Handler();
        expander.setOnClickListener(view -> {
            isExpanded = !isExpanded;
            setExtraItems();
            Runnable runner = () -> {
                if (isExpanded) {
                    isExpanded = false;
                    setExtraItems();
                }
            };
            if (isExpanded) {
                handler.postDelayed(runner, 5000);
            }
        });

        equlizer.setOnClickListener(view -> {
            btnAnimation(equlizer);
            if (isUsingSystemEqulizer) {
                try {
                    Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                    intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, service.getAudioSessionId());
                    intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, requireContext().getPackageName());
                    intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                    launchSystemEqualizerActivity.launch(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Intent intent = new Intent(requireActivity(), EqulizerActivity.class);
                startActivity(intent);
            }
        });

        sleep_timer.setOnClickListener(view -> {
            btnAnimation(sleep_timer);
            SleepTimerLayoutFragment sleepTimerLayoutFragment = new SleepTimerLayoutFragment();
            sleepTimerLayoutFragment.show(requireActivity().getSupportFragmentManager(), "ABC");
        });
        sound_effect.setOnClickListener(view -> btnAnimation(sound_effect));

        speed_pitch.setOnClickListener(view -> {
            btnAnimation(speed_pitch);
            SpeedAndPitchLayoutFragment speedAndPitchLayoutFragment = new SpeedAndPitchLayoutFragment();
            speedAndPitchLayoutFragment.show(requireActivity().getSupportFragmentManager(), "ABC");
        });
    }

    private void doAction() {

        if (song_name != null) {
            songName.setText(song_name);
            songName.setSelected(true);
        }

        if (artist_name != null)
            songArtist.setText(artist_name);
    }

    public void setSeek(BackService service)
    {
        if(BackService.isNext) {
            startAnimation(isSongChanged, 360f, 200L);
        }
        else {
            startAnimation(isSongChanged, -360f, 200L);
        }
        if (service != null) {
            if (service.isPlaying())
                play_pause.setImageResource(R.drawable.ic_round_pause_24);
            seekBar.setMax(service.getDuration());
            Handler handler = new Handler();
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (service.isPlaying()) {
                        int t = service.getCurrentPosition();
                        seekBar.setProgress(t);
                    }
                    handler.postDelayed(this, 1000);
                }
            });


            seekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
                @Override
                public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                    if (fromUser)
                    {
                        service.seekTO(progress);
                    }
                }

                @Override
                public void onStopTrackingTouch(CircularSeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(CircularSeekBar seekBar) {

                }
            });

            next.setOnClickListener(view -> {
                if (BackService.isServiceRunning) {
                    service.setNextButtonClicked();
                    getSharedPref();
                } else {
                    if (song != null) {
                        if (song.size() > 0) {
                            Intent intent = new Intent(requireActivity(), BackService.class);
                            intent.putExtra("position", (song.size() > 1) ? position + 1 : position);
                            requireActivity().startService(intent);
                        }
                    }
                }
            });

            previous.setOnClickListener(view -> {
                if (BackService.isServiceRunning) {
                    service.setPreviousButtonClicked();
                    getSharedPref();
                } else {
                    if (song != null) {
                        if (song.size() > 0) {
                            Intent intent = new Intent(requireActivity(), BackService.class);
                            intent.putExtra("position", (song.size() > 1) ? position - 1 : position);
                            requireActivity().startService(intent);
                        }
                    }
                }
            });
        }
    }

    //    on result activity
    ActivityResultLauncher<Intent> launchSystemEqualizerActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
//              don't do anything
            });

    public void startAnimation( View view,Float angle,Long duration)
    {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,"rotation",0f, angle);
        objectAnimator.setDuration(duration);
        AnimatorSet animatorset = new AnimatorSet();
        animatorset.playTogether(objectAnimator);
        animatorset.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setBottom(PlayActBottom type) {
        Fragment fragment;
        if (type != LIST) {
            co_bottom_layout.setVisibility(View.VISIBLE);
            third_relative_list.setVisibility(View.GONE);
            fragment = new BottomFramesFragment();
            co_bottom_layout.setTranslationY(default_Y);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.last_frame_layout, fragment);
            transaction.commit();
        }
        else {
            co_bottom_layout.setVisibility(View.GONE);
            third_relative_list.setVisibility(View.VISIBLE);
            setBottomSongListRecycler();
            third_relative_list.setOnTouchListener((view, motionEvent) -> gesture.onTouchEvent(motionEvent));
            b_s_d_height = third_relative_list.getHeight();
            int []bottom_song_list_positions = new int[2];
            third_relative_list.getLocationOnScreen(bottom_song_list_positions);
            b_s_d_y = bottom_song_list_positions[1];
//            fragment = new SongListBottomFragment();
        }

        translationY = co_bottom_layout.getTranslationY();
        default_Y = translationY;
    }

    public void showSongDilogFrame() {
        RecyclerView recyclerView;
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.alert_dilog_songs_layout, null);
        recyclerView = view.findViewById(R.id.alert_dilog_recycler);
        recyclerView.setHasFixedSize(true);
        if (song.size() > 0) {
            SharedPreferences preferences = requireActivity().getSharedPreferences("PLAYING", MODE_PRIVATE);
            position = preferences.getInt(SONGPOSITION, 0);
            GridLayoutManager layout = new GridLayoutManager(requireActivity(), 2);
            recyclerView.setLayoutManager(layout);
            DilogRecycle adapter = new DilogRecycle(requireActivity(), song, requireActivity().getSupportFragmentManager());
            recyclerView.setAdapter(adapter);
            layout.scrollToPosition(position);

            //Item Touch Helper
            ItemTouchHelper.SimpleCallback touch_helper = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,
                    ItemTouchHelper.LEFT |
                            ItemTouchHelper.RIGHT ) {
                int rem_item_position;
                MusicFiles rem_item_file;
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    int fromPosition = viewHolder.getAdapterPosition();
                    int toPosition = target.getAdapterPosition();
                    Collections.swap(song, fromPosition, toPosition);
                    adapter.notifyItemMoved(fromPosition, toPosition);
                    return true;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    rem_item_position = viewHolder.getAdapterPosition();
                    rem_item_file = song.get(viewHolder.getAdapterPosition());
                    song.remove(viewHolder.getAdapterPosition());
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    Snackbar snackbar = Snackbar.make(view, "BACK SONG!", BaseTransientBottomBar.LENGTH_SHORT);
                    snackbar.setAction("UNDO", view1 -> {
                        if (rem_item_file != null) {
                            song.add(rem_item_position, rem_item_file);
                            adapter.notifyItemInserted(rem_item_position);
                            snackbar.dismiss();
                        }
                    }).show();
                }
            };
            ItemTouchHelper helper = new ItemTouchHelper(touch_helper);
            helper.attachToRecyclerView(recyclerView);

            MaterialAlertDialogBuilder dilog = new MaterialAlertDialogBuilder(requireActivity());/*, R.style.RoundedCornerAlertDilog);*/
            dilog.setView(view);
            alert = dilog.create();
            alert.setOnDismissListener(dialogInterface -> sendDismissedDilogBroad());
            if (alert.getWindow() != null)
                alert.getWindow().getAttributes().windowAnimations = R.style.ScalingDilogAnimation;
            alert.show();
        }
    }

    private void sendDismissedDilogBroad() {
        try {
            Intent intent = new Intent();
            intent.setAction("play_act.dilog.dismissed");
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBottomSheetList() {
        if (dialog_expanded) {
            bottomSheetUpDownMethod(-10);
        } else {
            bottomSheetUpDownMethod(10);
        }
    }

    // service based
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        BackService.LocalBinder binder = (BackService.LocalBinder) iBinder;
        service = binder.getService();
        setSeek(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    @SuppressLint("StaticFieldLeak")
    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        MediaMetadataRetriever retriever;

        @Override
        protected byte[] doInBackground(MusicFiles... musicFiles) {
            try {
                fileIO = musicFiles[0];
                path = fileIO.getPath();
                retriever = new MediaMetadataRetriever();
                retriever.setDataSource(path);
                album_pic = retriever.getEmbeddedPicture();
                retriever.release();
                return album_pic;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @SuppressLint("ResourceType")
        @RequiresApi(api = Build.VERSION_CODES.S)
        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            try {
                if (bytes != null) {
                    if (isRoundBackgroundOn && (which_act_background_round == COLORED || which_act_background_round == BLUR)) {
                        default_color = white_color;
                    } else {
                        default_color = base_color;
                    }
                } else if (isRoundBackgroundOn && which_act_background_round == GRADIENT) {
                    default_color = white_color;
                } else {
                    default_color = base_color;
                }
                songName.setTextColor(default_color);
                songArtist.setTextColor(default_color);
                shuffle.setColorFilter((shuffleBoolean) ? ACCENT_COLOR : default_color);
                repeat.setColorFilter((repeatBoolean) ? ACCENT_COLOR : default_color);
                try {
                    favourite.setColorFilter(favouriteList.contains(song.get(position)) ? ACCENT_COLOR : default_color);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                next.setColorFilter(default_color);
                previous.setColorFilter(default_color);
                isSongChanged.setColorFilter(default_color);

                Glide.with(requireContext())
                        .asBitmap()
                        .load(bytes)
                        .override(200, 200)
                        .placeholder(R.mipmap.ic_music)
                        .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                        .into(song_art);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playDilogSongs(int position) {
        if (service != null) {
            service.createMedia(position);
        }
    }

    private void setStarterExpander() {
        equlizer.setTranslationX(-200);
        speed_pitch.setTranslationX(200);
        sleep_timer.setTranslationX(200);
        sound_effect.setTranslationX(200);
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

    private void setExtraItems() {
        expand_btn_h = expander.getWidth();
        if (isExpanded) {
            ObjectAnimator exp_transX = ObjectAnimator.ofFloat(expander, "translationX",
                    (expand_btn_h / 2.5f));
            ObjectAnimator exp_alpha = ObjectAnimator.ofFloat(expander, "alpha",
                    0.3f, 1f);
            AnimatorSet exp = new AnimatorSet();
            exp.play(exp_transX).with(exp_alpha);

            ObjectAnimator equ_transX = ObjectAnimator.ofFloat(equlizer, "translationX",
                    -200,
                    0);

            ObjectAnimator pit_transX = ObjectAnimator.ofFloat(speed_pitch, "translationX",
                    200,
                    0);
            pit_transX.setStartDelay(70);

            ObjectAnimator sleeper_transX = ObjectAnimator.ofFloat(sleep_timer, "translationX",
                    200,
                    0);
            sleeper_transX.setStartDelay(130);

            ObjectAnimator sound_transX = ObjectAnimator.ofFloat(sound_effect, "translationX",
                    200,
                    0);
            sound_transX.setStartDelay(170);

            AnimatorSet set = new AnimatorSet();
            set.play(exp).before(equ_transX).before(pit_transX).before(sleeper_transX).before(sound_transX);
            set.setDuration(300);
            set.setInterpolator(new AccelerateInterpolator());
            set.start();
        } else {
            ObjectAnimator exp_transX = ObjectAnimator.ofFloat(expander, "translationX",
                    expand_btn_position_x);
            ObjectAnimator exp_alpha = ObjectAnimator.ofFloat(expander, "alpha", 1f,
                    0.3f);
            AnimatorSet exp = new AnimatorSet();
            exp.play(exp_transX).with(exp_alpha);

            ObjectAnimator equ_transX = ObjectAnimator.ofFloat(equlizer, "translationX",
                    0,
                    -200);
            equ_transX.setStartDelay(170);

            ObjectAnimator pit_transX = ObjectAnimator.ofFloat(speed_pitch, "translationX",
                    0,
                    200);
            pit_transX.setStartDelay(70);

            ObjectAnimator sleeper_transX = ObjectAnimator.ofFloat(sleep_timer, "translationX",
                    0,
                    200);
            sleeper_transX.setStartDelay(130);

            ObjectAnimator sound_transX = ObjectAnimator.ofFloat(sound_effect, "translationX",
                    0,
                    200);
            AnimatorSet set = new AnimatorSet();
            set.play(exp).before(equ_transX).before(pit_transX).before(sleeper_transX).before(sound_transX);
            set.setDuration(300);
            set.setInterpolator(new AccelerateInterpolator());
            set.start();
        }
    }

    private void btnAnimation(View v) {
        ObjectAnimator pos_x = ObjectAnimator.ofFloat(v, "scaleX", 1, 1.2f);
        ObjectAnimator pos_y = ObjectAnimator.ofFloat(v, "scaleY", 1, 1.2f);
        AnimatorSet pos = new AnimatorSet();
        pos.playTogether(pos_x, pos_y);

        ObjectAnimator neg_x = ObjectAnimator.ofFloat(v, "scaleX", 1.2f, 1f);
        ObjectAnimator neg_y = ObjectAnimator.ofFloat(v, "scaleY", 1.2f, 1f);
        AnimatorSet neg = new AnimatorSet();
        neg.playTogether(neg_x, neg_y);

        AnimatorSet set = new AnimatorSet();
        set.play(pos).before(neg);
        set.setDuration(100);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    private void reciveDOWN() {
        ObjectAnimator animator = (ObjectAnimator) AnimatorInflater.loadAnimator(requireActivity(), R.animator.size_decrease);
        animator.setTarget(bottom_list_fragment_divider);
        animator.start();
        bottom_list_parent_card.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        bottom_list_parent_card.setBackgroundColor(Color.TRANSPARENT);
    }

    private void reciveUP() {
        ObjectAnimator animator = (ObjectAnimator) AnimatorInflater.loadAnimator(requireActivity(), R.animator.size_increase);
        animator.setTarget(bottom_list_fragment_divider);
        animator.start();
        bottom_list_parent_card.setBackgroundTintList(ColorStateList.valueOf(0xffc1c1c1));
        bottom_list_parent_card.setBackgroundColor(0xffc1c1c1);
    }

    private void bottomSheetUpDownMethod(float v1) {
        b_s_d_height = third_relative_list.getHeight();
        expand_btn_h = expander.getWidth();
        if (v1 > 0) {
            ObjectAnimator exp_transX = ObjectAnimator.ofFloat(expander, "translationX",
                    -expand_btn_h);
            exp_transX.setDuration(200);
            exp_transX.start();
            third_relative_list.animate().translationY(b_s_d_y - (b_s_d_height / 2f)).setDuration(300);
            reciveUP();
            dialog_expanded = true;
        } else if (v1 < 0) {
            third_relative_list.animate().translationY(b_s_d_y).setDuration(300);
            reciveDOWN();
            dialog_expanded = false;
            ObjectAnimator exp_transX = ObjectAnimator.ofFloat(expander, "translationX",
                    (isExpanded) ? (expand_btn_h / 2.5f) : expand_btn_position_x);
            exp_transX.setDuration(200);
            exp_transX.start();
        }
    }

//  Gesture Methods
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
    private void checkPreviousOn () {
        if (timer_set) {
            sleep_timer_img.setColorFilter(ACCENT_COLOR);
        } else {
            sleep_timer_img.setColorFilter(default_color);
        }
        if (iSSpeedON || iSPitchON) {
            speed_pitch_img.setColorFilter(ACCENT_COLOR);
        } else {
            speed_pitch_img.setColorFilter(default_color);
        }
    }

    private void setBottomSongListRecycler() {
        if (song != null) {
            if (song.size() > 0) {
                SharedPreferences preferences = requireActivity().getSharedPreferences("PLAYING", Context.MODE_PRIVATE);
                position = preferences.getInt(SONGPOSITION, 13);
                CurrentPlayingRecycle adapter = new CurrentPlayingRecycle(requireActivity(), song, bottom_list_recycler_view, requireActivity().getSupportFragmentManager());
                LinearLayoutManager layout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                layout.scrollToPosition(position);
                bottom_list_recycler_view.setAdapter(adapter);
                bottom_list_recycler_view.setLayoutManager(layout);
            }
        }
    }
}