package com.sanket_satpute_20.playme.project.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.fragments.PlayOptionsFragment2.OPTION_EXTRA_STUFFES;
import static com.sanket_satpute_20.playme.project.fragments.PlayOptionsFragment2.OPTION_INTENT_ACTION;
import static com.sanket_satpute_20.playme.project.fragments.PlayOptionsFragment2.OPTION_IS_ON;
import static com.sanket_satpute_20.playme.project.fragments.PlayOptionsFragment2.OPTION_PLAY_PAUSE_EXTRA;
import static com.sanket_satpute_20.playme.project.fragments.PlayOptionsFragment2.OPTION_REPEAT_EXTRA;
import static com.sanket_satpute_20.playme.project.fragments.PlayOptionsFragment2.OPTION_SHUFFLE_EXTRA;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;
import static com.sanket_satpute_20.playme.project.service.BackService.repeatBoolean;
import static com.sanket_satpute_20.playme.project.service.BackService.shuffleBoolean;
import static com.sanket_satpute_20.playme.project.service.BackService.song;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.activity.PlayActivity;
import com.sanket_satpute_20.playme.project.recycler_views.CurrentPlayingRecycle;
import com.sanket_satpute_20.playme.project.service.BackService;

public class PlayLyricsFragment2 extends Fragment implements ServiceConnection {

    ImageView shuffle, previous, play_pause, next, repeat;
    RecyclerView recycler_view;
    TextView up_next_txt;

    BackService service;
    int position;

    public static final String REPEAT_EXTRA = "REPEAT_EXTRA";
    public static final String SHUFFLE_EXTRA = "SHUFFLE_EXTRA";
    public static final String PLAY_PAUSE_EXTRA = "PLAY_PAUSE_EXTRA";
    public static final String EXTRA_STUFFES = "EXTRA_STUFFES";
    public static final String INTENT_ACTION = "play_lyrics.INTENT_ACTION.BUTTON.CLICKED";
    public static final String IS_ON = "IS_ON";

    Intent intent = new Intent();

    public static boolean isLyrics2Alive = false;

    BroadcastReceiver reciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("song.mp3.changed")) {
                play_pause.setImageResource(R.drawable.ic_round_pause_24);
                doExtra();
                setColors();
            }
            if (intent.getAction().equals(OPTION_INTENT_ACTION)) {
                switch (intent.getStringExtra(OPTION_EXTRA_STUFFES)) {
                    case OPTION_PLAY_PAUSE_EXTRA:
//                        play pause
                        if (intent.getBooleanExtra(OPTION_IS_ON, false)) {
                            play_pause.setImageResource(R.drawable.ic_round_pause_24);
                        } else {
                            play_pause.setImageResource(R.drawable.ic_round_play_arrow_24);
                        }
                        break;
                    case OPTION_SHUFFLE_EXTRA:
//                        shuffle
                        if (intent.getBooleanExtra(OPTION_IS_ON, false)) {
                            shuffle.setColorFilter(ACCENT_COLOR);
                            shuffle.setImageResource(R.drawable.shuffle_second_24);
                        } else {
                            shuffle.setColorFilter(PlayActivity.default_color);
                            shuffle.setImageResource(R.drawable.nfill_shuffle_second_24);
                        }
                        break;
                    case OPTION_REPEAT_EXTRA:
//                        repeat
                        if (intent.getBooleanExtra(OPTION_IS_ON, false)) {
                            repeat.setColorFilter(ACCENT_COLOR);
                            repeat.setImageResource(R.drawable.fill_undo_and_repeat_alt_24);
                        } else {
                            repeat.setColorFilter(PlayActivity.default_color);
                            repeat.setImageResource(R.drawable.undo_and_repeat_alt_24);
                        }
                        break;
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.fragment_play_lyrics2, container, false);
        initViews(view);
        doExtra();
        intent.setAction(INTENT_ACTION);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getContext(), BackService.class);
        requireActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
        SharedPreferences preferences = requireContext().getSharedPreferences("PLAYING", MODE_PRIVATE);
        position = preferences.getInt(SONGPOSITION, 0);

        if (shuffleBoolean)
            shuffle.setColorFilter(ACCENT_COLOR);
        if (repeatBoolean)
            repeat.setColorFilter(ACCENT_COLOR);
        setColors();
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unbindService(this);
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(reciver);
    }

    public void initViews(View view) {
        shuffle = view.findViewById(R.id.shuffle);
        previous = view.findViewById(R.id.previous);
        play_pause = view.findViewById(R.id.play_pause);
        next = view.findViewById(R.id.next);
        repeat = view.findViewById(R.id.repeat);
        recycler_view = view.findViewById(R.id.recycler_view);
        up_next_txt = view.findViewById(R.id.up_next_txt);
    }

    public void doExtra(){
        if (song.size() > 0) {
            CurrentPlayingRecycle adapter = new CurrentPlayingRecycle(requireActivity(), song, recycler_view, requireActivity().getSupportFragmentManager());
            LinearLayoutManager layout = new LinearLayoutManager(requireActivity());
            layout.scrollToPosition(position);
            recycler_view.setLayoutManager(layout);
            recycler_view.setAdapter(adapter);
        }
        play_pause.setColorFilter(ACCENT_COLOR);
    }

    public void onClick() {
        if (service != null) {
            if (service.isPlaying()) {
                play_pause.setImageResource(R.drawable.ic_round_pause_24);
            } else {
                play_pause.setImageResource(R.drawable.ic_round_play_arrow_24);
            }
        }

        play_pause.setOnClickListener(view -> {
            intent.putExtra(EXTRA_STUFFES, PLAY_PAUSE_EXTRA);
            if (service.isPlaying()) {
                service.pause();
                play_pause.setImageResource(R.drawable.ic_round_play_arrow_24);
                intent.putExtra(IS_ON, false);
            } else {
                service.play();
                play_pause.setImageResource(R.drawable.ic_round_pause_24);
                intent.putExtra(IS_ON, true);
            }
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
        });

        next.setOnClickListener(view -> service.setNextButtonClicked());

        previous.setOnClickListener(view -> service.setPreviousButtonClicked());

        shuffle.setOnClickListener(view -> {
            intent.putExtra(EXTRA_STUFFES, SHUFFLE_EXTRA);
            shuffleBoolean = !shuffleBoolean;
            if (shuffleBoolean) {
                shuffle.setColorFilter(ACCENT_COLOR);
                shuffle.setImageResource(R.drawable.shuffle_second_24);
            }
            else {
                shuffle.setColorFilter(PlayActivity.default_color);
                shuffle.setImageResource(R.drawable.nfill_shuffle_second_24);
            }
            intent.putExtra(IS_ON, shuffleBoolean);
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
        });

        repeat.setOnClickListener(view -> {
            intent.putExtra(EXTRA_STUFFES, REPEAT_EXTRA);
            repeatBoolean = !repeatBoolean;
            if (repeatBoolean)
            {
                repeat.setColorFilter(ACCENT_COLOR);
                repeat.setImageResource(R.drawable.fill_undo_and_repeat_alt_24);
            }
            else {
                repeat.setColorFilter(PlayActivity.default_color);
                repeat.setImageResource(R.drawable.undo_and_repeat_alt_24);
            }
            intent.putExtra(IS_ON, repeatBoolean);
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
        });
    }

    private void setColors() {
        repeat.setImageResource((repeatBoolean) ? R.drawable.undo_and_repeat_alt_24 : R.drawable.fill_undo_and_repeat_alt_24);
        shuffle.setImageResource((shuffleBoolean) ? R.drawable.shuffle_second_24 : R.drawable.nfill_shuffle_second_24);
        repeat.setColorFilter((repeatBoolean) ? ACCENT_COLOR : PlayActivity.default_color);
        shuffle.setColorFilter((shuffleBoolean) ? ACCENT_COLOR : PlayActivity.default_color);
        next.setColorFilter(PlayActivity.default_color);
        previous.setColorFilter(PlayActivity.default_color);
        up_next_txt.setTextColor(PlayActivity.default_color);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        BackService.LocalBinder binder = (BackService.LocalBinder) iBinder;
        service = binder.getService();
        onClick();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("song.mp3.changed");
        intentFilter.addAction(OPTION_INTENT_ACTION);
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(reciver, intentFilter);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }
}