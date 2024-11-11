package com.sanket_satpute_20.playme.project.service;


import static android.media.AudioDeviceInfo.TYPE_BLUETOOTH_A2DP;
import static android.media.AudioDeviceInfo.TYPE_BLUETOOTH_SCO;
import static android.media.AudioManager.GET_DEVICES_OUTPUTS;
import static com.sanket_satpute_20.playme.MainActivity.RECENT_SEEK_POSITION_SONG;
import static com.sanket_satpute_20.playme.MainActivity.albumFilesList;
import static com.sanket_satpute_20.playme.MainActivity.artistFiles;
import static com.sanket_satpute_20.playme.MainActivity.musicFiles;
import static com.sanket_satpute_20.playme.project.activity.CoreSettingActivity.FORCE_THRESHOLD_SEEK;
import static com.sanket_satpute_20.playme.project.activity.CoreSettingActivity.SHAKERWHICHSETTING;
import static com.sanket_satpute_20.playme.project.activity.CoreSettingActivity.WHICHSHAKESETTED;
import static com.sanket_satpute_20.playme.project.enums.ShakeWhich.NEXTPLAYNORMAL;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.ACTION_NEXT;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.ACTION_PLAY;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.ACTION_PREVIOUS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.NOTIFICATION_TYPE;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.SONG_SEEK_CURRENT_POSITION;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__ALLOW_HEAD_SET_CONTROLS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__HEADPHONE_LISTENER_ALREADY_STARTED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__LOCK_SCREEN_PLAY_LISTENER_ON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__PAUSE_WHEN_BLUETOOTH_DISCONNECTED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__PAUSE_WHEN_HEADSET_PLUGGED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__PLAY_WHEN_BLUETOOTH_CONNECTED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__PLAY_WHEN_HEADSET_INSERTED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.iSPitchON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.iSSpeedON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.pitchF;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.speedF;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.FROMWHERE;
import static com.sanket_satpute_20.playme.project.notification.NotificationReceiver.ACTION;
import static com.sanket_satpute_20.playme.project.recycler_views.CurrentPlayingRecycle.INTPOSITION;
import static com.sanket_satpute_20.playme.project.recycler_views.InnerAlbumRecycle.innerAlbumFiles;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.ISONGRECYCLE;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.files;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.activity.LockScreenActivity;
import com.sanket_satpute_20.playme.project.activity.PlayActivity;
import com.sanket_satpute_20.playme.project.enums.ShakeWhich;
import com.sanket_satpute_20.playme.project.extra_stuffes.CacheImageManager;
import com.sanket_satpute_20.playme.project.listeners.LockScreenListener;
import com.sanket_satpute_20.playme.project.model.Albums;
import com.sanket_satpute_20.playme.project.model.Artists;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.notification.ActionPlaying;
import com.sanket_satpute_20.playme.project.notification.NotificationReceiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class BackService extends Service implements ActionPlaying {

    /*  Notification Data   */
    PlaybackState.Builder state_builder;
    MediaMetadata.Builder metadata;
    MediaSession mediaSession;

    //    default notification bitmap
    Bitmap defaultNotifyBitmap;

    /*  SHAKE DETECTION */
    public static ShakeWhich shakeWhich = NEXTPLAYNORMAL;
    public static int FORCE_THRESHOLD = 350;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 3;

    private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
    private long mLastTime;
    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;
    /*  SHAKE END   */

    SharedPreferences shared, preferencesShake;

    public static boolean timer_set = false, service_destroying = false,
            isShakeOn = false, isAutoNextPlayOn = true;
    boolean not_user_stop = false;

    public CountDownTimer timer;
    public static long sec = 0;
    public static long min = 0;
    public static long hours = 0;
    public static long currentTime;

    public static boolean isServiceRunning = false;

    public AudioManager audioManager;

    public static String CHANNEL_ID = "1";

    //    public static final String
    public static final String SONGNAME = "SONGNAME";
    public static final String SONGARTIST = "SONGARTIST";
    public static final String SONGPATH = "SONGPATH";
    public static final String SONG_DURATION_PREF = "SONG_DURATION_PREF";
    public static final String SONGPOSITION = "SONGPOSITION";
    public static final String ACTION_CLOSE = "ACTION_CLOSE";
    public static String CURRENT_PLAYING_SONG_PATH = null;

    /*  for settings    */
    public static final String CORESETTING = "CORESETTING";
    public static final String AUTOPLAYSONGS = "AUTOPLAYSONGS";
    public static final String GESTUREPLAY = "GESTUREPLAY";
    public static final String SHAKETOPLAY = "SHAKETOPLAY";

    public static ArrayList<MusicFiles> song = new ArrayList<>();
    public static ArrayList<MusicFiles> recentPlayed = new ArrayList<>();
    public static ArrayList<MusicFiles> most_played_songs = new ArrayList<>();
    public static ArrayList<Albums> recent_album = new ArrayList<>();
    public static ArrayList<Artists> recent_artist = new ArrayList<>();
    public static int recentPlayed_duration = 0;


    public static boolean shuffleBoolean = false, repeatBoolean = false, isNext;

    boolean isRecentPlaying = true;

    public static String from;
    Intent intent = new Intent();
    Intent adder_intent = new Intent();
    Intent recent_intent = new Intent();

    public final IBinder binder = new LocalBinder();

    BluetoothAdapter bluetoothAdapter;

    // MediaPlayer
    public MediaPlayer mediaPlayer;

    String action = null;

    //position of song
    public int position = 0;

    //    seeking value
    Long seeking_value = 0L;

    public static boolean isForegroundService = false;

    String previous_artist_name, new_artist_name, previous_album_name, new_album_name;

    public final AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            switch (i) {
                case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                    mediaPlayer.setVolume(0.22f, 0.22f);
                    break;
                case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                case (AudioManager.AUDIOFOCUS_LOSS):
                    try {
                        not_user_stop = isPlaying();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        not_user_stop = false;
                    } finally {
                        pause();
                    }
                    break;
                case (AudioManager.AUDIOFOCUS_GAIN):
                    if (not_user_stop) {
                        not_user_stop = false;
                        try {
                            mediaPlayer.setVolume(1f, 1f);
                            play();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case "current.song.played.Start":
                        createMedia(intent.getIntExtra(INTPOSITION, 0));
                        break;
                    case "action.shake.updated":
                        if (isShakeOn)
                            setSensor();
                        else
                            deleteSensor();
                        break;
                    case "notify.changed.Song":
                        action = intent.getStringExtra(ACTION);
                        if (action != null) {
                            if (action.equals("next"))
                                setNextButtonClicked();
                            else if (action.equals("previous"))
                                setPreviousButtonClicked();
                            else {
                                play_pause();
                            }
                        }
                        break;
                    case "action_song_deleted.IF_NEXT_SONG_AVAIL_THEN_CHANGE_SONG_ELSE_HIDE_BOTTOM_PLAYER":
                        int current_song_playing_position = intent.getIntExtra("CURRENT_SONG_POSITION", -1);
                        currentPlayingSongDeleted(current_song_playing_position);
                        break;
                    case "action.broadcast_from_core_settings_activity_TO.BackService.StartEarphoneListener" :
                        startHeadphoneListener();
                        break;
                    case "action.broadcast_from_core_settings_activity_TO.BackService.StopEarphoneListener" :
                        stopHeadphoneListener();
                        break;
                    case "action.from_core_setting_activity_To_BackService.ALLOW_HEAD_SET_CONTROLS.Changed" :
                        setMediaSessionCallback();
                        break;
                    case Intent.ACTION_SCREEN_OFF :
                        Log.d("h10", "Screen is of");
                        break;
                    case Intent.ACTION_SCREEN_ON :
                        Log.d("h10", "Screen is on");
                        break;
                    case Intent.ACTION_USER_PRESENT :
                        Log.d("h10", "got");
                        break;
                    case "action.NOTIFICATION_ACTION_CHANGED.from_lock_screen_and_notification_activity" :
                        sendNotify();
                        break;
                    case "action.LOCK_SCREEN_PLAY_LISTENER_CHANGED.from_LockScreenAndNotificationActivity" :
                        if (__LOCK_SCREEN_PLAY_LISTENER_ON)
                            startLockScreenListener();
                        else
                            stopLockScreenListener();
                        break;
                }
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public BackService getService() {
            return BackService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRecentPlaying = true;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (intent != null) {
            position = intent.getIntExtra("position", 0);
            seeking_value = intent.getLongExtra("seekTO", 0L);
        }
        createMedia(position);
        isServiceRunning = true;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("current.song.played.Start");
        filter.addAction("notify.changed.Song");
        filter.addAction("action.shake.updated");
        filter.addAction("action_song_deleted.IF_NEXT_SONG_AVAIL_THEN_CHANGE_SONG_ELSE_HIDE_BOTTOM_PLAYER");
        filter.addAction("action.broadcast_from_core_settings_activity_TO.BackService.StartEarphoneListener");
        filter.addAction("action.broadcast_from_core_settings_activity_TO.BackService.StopEarphoneListener");
        filter.addAction("action.from_core_setting_activity_To_BackService.ALLOW_HEAD_SET_CONTROLS.Changed");
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction("action.NOTIFICATION_ACTION_CHANGED.from_lock_screen_and_notification_activity");
        filter.addAction("action.LOCK_SCREEN_PLAY_LISTENER_CHANGED.from_LockScreenAndNotificationActivity");
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(receiver, filter);
        setSensor();

        if (__PLAY_WHEN_BLUETOOTH_CONNECTED || __PAUSE_WHEN_BLUETOOTH_DISCONNECTED
                || __PLAY_WHEN_HEADSET_INSERTED || __PAUSE_WHEN_HEADSET_PLUGGED)
            startHeadphoneListener();
        else
            __HEADPHONE_LISTENER_ALREADY_STARTED = false;

        if (__LOCK_SCREEN_PLAY_LISTENER_ON)
            startLockScreenListener();
    }

    Handler handler_earphone_bluetooth_or_wired_earphone;
    Runnable runnable_earphone_bluetooth_or_wired_earphone;
    private void startHeadphoneListener() {
        __HEADPHONE_LISTENER_ALREADY_STARTED = true;

        runnable_earphone_bluetooth_or_wired_earphone = new Runnable() {
            boolean songStoppedByBluetooth, songStoppedByWired, parentIsBluetoothConnected, parentIsWiredConnected;
            @Override
            public void run() {

                if (__PLAY_WHEN_BLUETOOTH_CONNECTED || __PAUSE_WHEN_BLUETOOTH_DISCONNECTED) {
                    boolean isConnected = isBluetoothHeadsetConnected();
                    if (isConnected != parentIsBluetoothConnected) {
                        if (isConnected && songStoppedByBluetooth && !isPlaying() && __PLAY_WHEN_BLUETOOTH_CONNECTED) {
                            play();
                            songStoppedByBluetooth = false;
                        } else if (!isConnected && isPlaying() && !parentIsWiredConnected && __PAUSE_WHEN_BLUETOOTH_DISCONNECTED) {
                            pause();
                            songStoppedByBluetooth = true;
                        }
                    }
                    parentIsBluetoothConnected = isConnected;
                } else {
                    parentIsBluetoothConnected = false;
                }

                if (__PLAY_WHEN_HEADSET_INSERTED || __PAUSE_WHEN_HEADSET_PLUGGED) {
                    boolean isConnected = isWiredHeadsetConnected();
                    if (isConnected != parentIsWiredConnected) {
                        if (isConnected && songStoppedByWired && !isPlaying() && __PLAY_WHEN_HEADSET_INSERTED) {
                            play();
                            songStoppedByWired = false;
                            Log.d("h10", "connected");
                        } else if (!isConnected && isPlaying() && __PAUSE_WHEN_HEADSET_PLUGGED && !parentIsBluetoothConnected) {
                            pause();
                            songStoppedByWired = true;
                            Log.d("h10", "not connected");
                        }
                    }
                    parentIsWiredConnected = isConnected;
                } else {
                    parentIsWiredConnected = false;
                }

                handler_earphone_bluetooth_or_wired_earphone.postDelayed(this, 100);
            }
        };

        handler_earphone_bluetooth_or_wired_earphone = new Handler();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        handler_earphone_bluetooth_or_wired_earphone.post(runnable_earphone_bluetooth_or_wired_earphone);
    }

    private boolean isBluetoothHeadsetConnected() {
        boolean isBluetoothConnected = false;
        if (audioManager == null)
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            isBluetoothConnected = Arrays.stream(audioManager.getDevices(GET_DEVICES_OUTPUTS)).
                    anyMatch(info -> info.getType() == TYPE_BLUETOOTH_SCO || info.getType() == TYPE_BLUETOOTH_A2DP);
        }
        return isBluetoothConnected;
    }

    private boolean isWiredHeadsetConnected(){
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] audioDevices;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioDevices = audioManager.getDevices(/*AudioManager.GET_DEVICES_ALL  | */ GET_DEVICES_OUTPUTS | AudioManager.GET_DEVICES_INPUTS);
            for(AudioDeviceInfo deviceInfo : audioDevices){
                if(deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                        || deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADSET){
                    return true;
                }
            }
        }
        return false;
    }

    private void stopHeadphoneListener() {
        if (handler_earphone_bluetooth_or_wired_earphone != null && runnable_earphone_bluetooth_or_wired_earphone!= null)
            handler_earphone_bluetooth_or_wired_earphone.removeCallbacks(runnable_earphone_bluetooth_or_wired_earphone);
        __HEADPHONE_LISTENER_ALREADY_STARTED = false;
    }

    LockScreenListener lockScreenListener;
    Handler lock_screen_handler;
    Runnable lock_screen_runnable;
    boolean isLockScreenListenerActive;
    private void startLockScreenListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            lockScreenListener = new LockScreenListener(getApplicationContext());
            boolean listenerRegistered = lockScreenListener.registerListener();
            isLockScreenListenerActive = true;
            if (!listenerRegistered) {
                isLockScreenListenerActive = false;
                lock_screen_handler = new Handler();
                lock_screen_runnable = new Runnable() {
                    boolean previousState = true;
                    @Override
                    public void run() {
                        boolean isOn = isScreenOn();
                        if (isOn && !previousState) {
                            Intent lockScreenIntent = new Intent(getApplicationContext(), LockScreenActivity.class);
                            lockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(lockScreenIntent);
                        }
                        previousState = isOn;
                        lock_screen_handler.postDelayed(this, 100);
                    }
                };
                lock_screen_handler.post(lock_screen_runnable);
            }
        } else {
            isLockScreenListenerActive = false;
            lock_screen_handler = new Handler();
            lock_screen_runnable = new Runnable() {
                boolean previousState = true;
                @Override
                public void run() {
                    boolean isOn = isScreenOn();
                    if (isOn && !previousState) {
                        Intent lockScreenIntent = new Intent(getApplicationContext(), LockScreenActivity.class);
                        lockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(lockScreenIntent);
                    }
                    previousState = isOn;
                    lock_screen_handler.postDelayed(this, 100);
                }
            };
            lock_screen_handler.post(lock_screen_runnable);
        }
    }

    private void stopLockScreenListener() {
        if (isLockScreenListenerActive && lockScreenListener != null) {
            lockScreenListener.unregisterListener();
        } else if (lock_screen_handler != null && lock_screen_runnable != null) {
            lock_screen_handler.removeCallbacks(lock_screen_runnable);
        }
    }

    PowerManager powerManager;
    private boolean isScreenOn() {
        if (powerManager == null)
            powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            return powerManager.isInteractive();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null)
            stop();
        isServiceRunning = false;
        LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(receiver);
        stopHeadphoneListener();
        stopLockScreenListener();
    }

    public void setSongSource(ArrayList<MusicFiles> file) {
        song = file;
    }

    // do media method's
    public void createMedia(int position) {
        this.position = position;
        if (mediaPlayer != null)
            stop();
        try {
            if (audioManager == null)
                audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            Uri uri = Uri.parse(song.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                mediaPlayer.setAudioAttributes(audioAttributes);
            } else {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            int result = audioManager.requestAudioFocus(focusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                CURRENT_PLAYING_SONG_PATH = song.get(position).getPath();
                if (isRecentPlaying)
                    start();
                if (seeking_value > 1L) {
                    seekTO(seeking_value);
                    seeking_value = 0L;
                }
                if (iSPitchON) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        setPitch(pitchF);
                    }
                } if (iSSpeedON) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        setSpeed(speedF);
                    }
                }

                String songname = song.get(position).getTitle();
                String songpath = song.get(position).getPath();
                String songartist = song.get(position).getArtist();

                SharedPreferences preferences = getSharedPreferences("PLAYING", MODE_PRIVATE);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString(SONGNAME, songname);
                edit.apply();
                edit.putString(SONGARTIST, songartist);
                edit.apply();
                edit.putString(SONGPATH, songpath);
                edit.apply();
                edit.putInt(SONGPOSITION, position);
                edit.apply();
                edit.putString(FROMWHERE, ISONGRECYCLE);
                edit.apply();
                edit.putLong(SONG_DURATION_PREF, Integer.parseInt(song.get(position).getDuration()));
                edit.apply();
                intent.setAction("song.mp3.changed");
                intent.putExtra("msg", "changed");
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                mediaPlayer.setOnCompletionListener(view -> {
                    if (song.size() > 0 && isAutoNextPlayOn) {
                        try {
                            isRecentPlaying = isPlaying();
                        } catch (Exception e) {
                            isRecentPlaying = true;
                        } finally {
                            songFinishedChangeSong();
                        }
                    }
                });
                sendNotify();
                if (song.get(position).getSong_most_played_count() > 2) {
                    if (!(most_played_songs.contains(song.get(position)))) {
                        most_played_songs.add(song.get(position));
                        adder_intent.setAction("most_played.added.song");
                        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(adder_intent);
                    }
                } else {
                    song.get(position).setSong_most_played_count(song.get(position).getSong_most_played_count()+1);
                }
                if (recentPlayed.size() > 19) {
                    recentPlayed.remove(0);
                }
                if (!(recentPlayed.contains(song.get(position)))) {
                    recentPlayed.add(song.get(position));
                    recentPlayed_duration += Integer.parseInt(song.get(position).getDuration());
                    recent_intent.setAction("recent_played.added.song");
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(recent_intent);
                }

                new_artist_name = song.get(position).getArtist();
                if (previous_artist_name != null && new_artist_name != null) {
                    if (previous_artist_name.equals(new_artist_name)) {
                        for (Artists art : artistFiles) {
                            if (art.getArtistName().equals(new_artist_name)) {
                                if (!recent_artist.contains(art)) {
                                    recent_artist.add(art);
                                }
                                break;
                            }
                        }
                    }
                }

                new_album_name = song.get(position).getAlbum();
                if (previous_album_name != null && new_artist_name != null) {
                    if (previous_album_name.equals(new_album_name)) {
                        for (Albums alb : albumFilesList) {
                            if (alb.getAlbum_name().equals(new_album_name)) {
                                if (!recent_album.contains(alb)) {
                                    recent_album.add(alb);
                                }
                                break;
                            }
                        }
                    }
                }

                previous_artist_name = new_artist_name;
                previous_album_name = new_album_name;
            }
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            e.printStackTrace();
            songFinishedChangeSong();
        }
    }

    public int getAudioSessionId()
    {
        if (mediaPlayer != null)
            return mediaPlayer.getAudioSessionId();
        return -1;
    }

    public void start()
    {
        if (mediaPlayer != null) {
            mediaPlayer.start();
//            sendNotify();
        }
    }

    public void pause()
    {
        if (mediaPlayer != null && isPlaying()) {
            mediaPlayer.pause();
            sendNotify();
            Intent play_pause_broadcast_intent = new Intent();
            play_pause_broadcast_intent.setAction("action.Song_play_pause.Triggered");
            play_pause_broadcast_intent.putExtra("IS_PLAYING_EXTRA", false);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(play_pause_broadcast_intent);
        }
    }

    public void stop()
    {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
//                mediaPlayer.reset();
//                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void play_pause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                pause();
            } else {
                play();
            }
            sendNotify();
            Intent broad_intent = new Intent();
            broad_intent.setAction("from.back.play_pause.activated");
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(broad_intent);
        }
    }

    public void play() {
        if (mediaPlayer != null) {
            start();
        }
        else {
            SharedPreferences preferences = getSharedPreferences("PLAYING", MODE_PRIVATE);
            position = preferences.getInt(SONGPOSITION, 0);
            from = preferences.getString(FROMWHERE, ISONGRECYCLE);
            if (from.equals(ISONGRECYCLE)) {
                song = files;
            } else {
                song = innerAlbumFiles;
            }
            createMedia(position);
            SharedPreferences preferences2 = getSharedPreferences("STORING_DATA", MODE_PRIVATE);
            SONG_SEEK_CURRENT_POSITION = preferences2.getLong(RECENT_SEEK_POSITION_SONG, 0L);
            long recent_duration = preferences.getLong(SONG_DURATION_PREF, 0L);
            if (recent_duration > SONG_SEEK_CURRENT_POSITION) {
                seekTO(SONG_SEEK_CURRENT_POSITION);
            }
        }
        sendNotify();
        Intent play_pause_broadcast_intent = new Intent();
        play_pause_broadcast_intent.setAction("action.Song_play_pause.Triggered");
        play_pause_broadcast_intent.putExtra("IS_PLAYING_EXTRA", true);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(play_pause_broadcast_intent);
    }

    public boolean isPlaying()
    {
        try {
            if (mediaPlayer != null)
                return mediaPlayer.isPlaying();
        } catch(IllegalStateException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public int getDuration() {
        if (mediaPlayer != null){
            try {
                return mediaPlayer.getDuration();
            } catch (IllegalStateException e) {
                return 0;
            }
        }
        return 0;
    }

    public int getCurrentPosition()
    {
        int current_duration = 0;
        if(mediaPlayer != null) {
            try {
                current_duration = mediaPlayer.getCurrentPosition();
            }
            catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return current_duration;
    }

    public void setNextButtonClicked() {
        if (!repeatBoolean) {
            SharedPreferences preferences = getSharedPreferences("PLAYING", MODE_PRIVATE);
            if (mediaPlayer != null) {
                position = preferences.getInt(SONGPOSITION, 0);
            }
            if (shuffleBoolean) {
                position = getRandom(song.size() - 1);
            } else {
                if (position < (song.size()) - 1)
                    position = position + 1;
                else
                    position = 0;
            }
            isNext = true;
        }

        try {
            isRecentPlaying = isPlaying();
        } catch (Exception e) {
            isRecentPlaying = true;
            e.printStackTrace();
        }

        if (song != null)
            if (song.size() > 0)
                createMedia(position);
            else
                Toast.makeText(this, "No Songs Available", Toast.LENGTH_SHORT).show();
    }

    public void songFinishedChangeSong() {
        if (!repeatBoolean) {
            SharedPreferences preferences = getSharedPreferences("PLAYING", MODE_PRIVATE);
            if (mediaPlayer != null) {
                position = preferences.getInt(SONGPOSITION, 0);
            }
            if (shuffleBoolean) {
                position = getRandom(song.size() - 1);
            } else {
                if (position < (song.size()) - 1)
                    position = position + 1;
                else
                    position = 0;
            }
            isNext = true;
        }

        isRecentPlaying = true;

        if (song != null)
            if (song.size() > 0)
                createMedia(position);
            else
                Toast.makeText(this, "No Songs Available", Toast.LENGTH_SHORT).show();
    }

    public void currentPlayingSongDeleted(int pos) {
        Log.d("i_am", "Got Position : " + pos + " , Size : " + song.size() );
        if (pos != -1) {
            try {
                isRecentPlaying = isPlaying();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (song != null)
                    if (song.size() > 0) {
                        if (pos >= song.size() || pos < 0) {
                            //index does not exists
                            createMedia(0);
                        } else {
                            // index exists
                            createMedia(position);
                        }
                    } else {
                        if (musicFiles != null) {
                            if (musicFiles.size() > 0) {
                                CURRENT_PLAYING_SONG_PATH = musicFiles.get(0).getPath();
                                song = musicFiles;
                                isRecentPlaying = false;
                                createMedia(0);
                            } else {
                                Toast.makeText(this, "No Songs Available", Toast.LENGTH_SHORT).show();
//                              send broadcast to hide the bottom player
                                String action = "action.broadcast.SONG_NOT_FOUND.Down_the_bottom_player";
                                Intent down_bottom_player_broad_intent = new Intent(action);
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(down_bottom_player_broad_intent);
                            }
                        } else {
                            Toast.makeText(this, "No Songs Available", Toast.LENGTH_SHORT).show();
//                          send broadcast
                            String action = "action.broadcast.SONG_NOT_FOUND.Down_the_bottom_player";
                            Intent down_bottom_player_broad_intent = new Intent(action);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(down_bottom_player_broad_intent);
                        }
                    }
            }
        } else {
            String action = "action.broadcast.SONG_NOT_FOUND.Down_the_bottom_player";
            Intent down_bottom_player_broad_intent = new Intent(action);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(down_bottom_player_broad_intent);
//            send broadcast to hide the bottom player
        }
    }

    public void setPreviousButtonClicked() {
        if (mediaPlayer != null) {
            SharedPreferences preferences = getSharedPreferences("PLAYING", MODE_PRIVATE);
            position = preferences.getInt(SONGPOSITION, 0);
        }
        if (shuffleBoolean) {
            position = getRandom(song.size() - 1);
        } else {
            if (position == 0)
                position = song.size() - 1;
            else
                position = position - 1;
        }
        isNext = false;

        try {
            isRecentPlaying = isPlaying();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (song != null)
            if (song.size() > 0)
                createMedia(position);
    }

    public void seekTO(float seeking) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo((int) seeking);
            sendNotify();
        }
        else
            Log.d("PlayMe_Error", "Player is Empty");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setPitch(float pitch) {
        if (mediaPlayer != null) {
            if (pitch == 0 || pitch == 0.0)
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setPitch(0.1f));
            else
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setPitch(pitch));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setSpeed(float speed) {
        if (mediaPlayer != null) {
            if (speed == 0 || speed == 0.0)
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(0.1f));
            else
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
            sendNotify();
        }
    }

    // Get Time
    public String getTime(int time) {
        int seconds = (time / 1000) % 60 ;
        int minutes = ((time / (1000*60)) % 60);
        if (time >= 3600000) {
            return ((time / (60*60*1000)) % 24) + " : " + minutes + " : " + seconds;
        }
        return minutes+" : "+seconds;
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    // getAlbumArt
    public byte[] getAlbumArt(String uri)
    {
        try {
            if (uri != null) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(uri);
                byte[] art = retriever.getEmbeddedPicture();
                retriever.release();
                return art;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public void stopMe() {
        stopSelf();
    }

    public void startTimer(int time) {
        timer_set = true;
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long l) {
                currentTime = l;
                sec = ((l / 1000) % 60);
                min = (l / (1000*60) % 60);
                hours = (l / (60*60*1000)) % 24;
            }

            @Override
            public void onFinish() {
                currentTime = 0;
                timer_set = false;
                if (service_destroying) {
                    pause();
                    stopMe();
                } else {
                    pause();
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        if(!isPlaying())
                            stopMe();
                    }, 2000000);
                }
            }
        }.start();
    }

    public void stopTimer() {
        if (timer != null)
            timer.cancel();
        currentTime = 0;
        timer_set = false;
    }
    SensorManager sensorManager;
    Sensor shake;
    SensorEventListener sensorEventListener;
    private void setSensor() {
        getShakeBefore();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        shake = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (isShakeOn) {
                    long now = System.currentTimeMillis();
                    if ((now - mLastForce) > SHAKE_TIMEOUT) {
                        mShakeCount = 0;
                    }

                    if ((now - mLastTime) > TIME_THRESHOLD) {
                        long diff = now - mLastTime;
                        float speed = Math.abs(sensorEvent.values[SensorManager.DATA_X] + sensorEvent.values[SensorManager.DATA_Y] + sensorEvent.values[SensorManager.DATA_Z] - mLastX - mLastY - mLastZ) / diff * 10000;
                        if (speed > FORCE_THRESHOLD) {
                            if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
                                mLastShake = now;
                                mShakeCount = 0;

                                if (shakeWhich == NEXTPLAYNORMAL) {
                                    setNextButtonClicked();
                                } else {
                                    if (sensorEvent.values[SensorManager.DATA_X] >
                                            sensorEvent.values[SensorManager.DATA_Y] &&
                                            sensorEvent.values[SensorManager.DATA_X] >
                                                    sensorEvent.values[SensorManager.DATA_Z])
                                        setNextButtonClicked();
                                    else if (sensorEvent.values[SensorManager.DATA_Y] >
                                            sensorEvent.values[SensorManager.DATA_X] &&
                                            sensorEvent.values[SensorManager.DATA_Y] >
                                                    sensorEvent.values[SensorManager.DATA_Z])
                                        setPreviousButtonClicked();
                                    else if (sensorEvent.values[SensorManager.DATA_Z] >
                                            sensorEvent.values[SensorManager.DATA_X] &&
                                            sensorEvent.values[SensorManager.DATA_Z] >
                                                    sensorEvent.values[SensorManager.DATA_Y])
                                        play_pause();
                                }
                            }
                            mLastForce = now;
                        }
                        mLastTime = now;
                        mLastX = sensorEvent.values[SensorManager.DATA_X];
                        mLastY = sensorEvent.values[SensorManager.DATA_Y];
                        mLastZ = sensorEvent.values[SensorManager.DATA_Z];
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        sensorManager.registerListener(sensorEventListener, shake, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void getShakeBefore() {
        shared = getSharedPreferences(CORESETTING, MODE_PRIVATE);
        preferencesShake = getSharedPreferences(SHAKERWHICHSETTING, MODE_PRIVATE);
        isShakeOn = shared.getBoolean(SHAKETOPLAY, false);
        shakeWhich = ShakeWhich.shakeWhichTo(preferencesShake.getString(WHICHSHAKESETTED, NEXTPLAYNORMAL.toString()));
        FORCE_THRESHOLD = shared.getInt(FORCE_THRESHOLD_SEEK, 350);
    }

    private void deleteSensor() {
        if (sensorManager != null && shake != null && sensorEventListener != null) {
            sensorManager.unregisterListener(sensorEventListener, shake);
        }
    }

    private void sendNotify() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (NOTIFICATION_TYPE.equals("OLD")) {
                sendNotifyOldVersion();
            } else {
                sendNotifyNewVersion();
            }
        } else {
            sendNotifyOldVersion();
        }
    }

    private int getPlaybackState() {
        int state = PlaybackState.STATE_STOPPED;
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                state = PlaybackState.STATE_PLAYING;
            else
                state = PlaybackState.STATE_PAUSED;
        }
        return state;
    }

    MediaSession.Callback media_session_callbacks = new MediaSession.Callback() {

        @Override
        public void onPlay() {
            super.onPlay();
            play();
        }

        @Override
        public void onPause() {
            super.onPause();
            pause();
        }

        @Override
        public void onStop() {
            super.onStop();
            stop();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            setNextButtonClicked();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            setPreviousButtonClicked();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            seekTO(pos);
            state_builder.setState(getPlaybackState(), pos, speedF, SystemClock.elapsedRealtime());
            mediaSession.setPlaybackState(state_builder.build());
        }
    };
    MediaSession.Callback media_session_callbacks_not_on = new MediaSession.Callback() {

        @Override
        public void onPlay() {
            super.onPlay();
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            seekTO(pos);
            state_builder.setState(getPlaybackState(), pos, speedF, SystemClock.elapsedRealtime());
            mediaSession.setPlaybackState(state_builder.build());
        }
    };

    private void sendNotifyNewVersion() {
        try {
            if (song != null) {
                if (song.size() > 0) {
                    mediaSession = new MediaSession(getApplicationContext(), "MusicPlayer");
                    final int flag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;

                    Bitmap bitmap;

                    bitmap = CacheImageManager.getImage(getApplicationContext(), song.get(position));
                    if (bitmap == null) {
                        byte[] image = getAlbumArt(song.get(position).getPath());
                        if (image != null)
                            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                        else {
                            if (defaultNotifyBitmap == null)
                                getDefaultBitmapForNotification();
                            bitmap = defaultNotifyBitmap;
                        }
                    }

                    metadata = new MediaMetadata.Builder();
                    metadata.putLong(MediaMetadata.METADATA_KEY_DURATION, getDuration());
                    metadata.putString(MediaMetadata.METADATA_KEY_TITLE, song.get(position).getTitle());
                    metadata.putString(MediaMetadata.METADATA_KEY_ARTIST, song.get(position).getArtist());
                    metadata.putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, song.get(position).getPath());
                    metadata.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, bitmap);

                    state_builder = new PlaybackState.Builder()
                            .setState(getPlaybackState(), getCurrentPosition(), speedF, SystemClock.elapsedRealtime())
                            .setBufferedPosition(getCurrentPosition())
                            .setActions(PlaybackState.ACTION_PLAY |
                                    PlaybackState.ACTION_PAUSE |
                                    PlaybackState.ACTION_SKIP_TO_NEXT |
                                    PlaybackState.ACTION_SKIP_TO_PREVIOUS |
                                    PlaybackState.ACTION_PLAY_PAUSE |
                                    PlaybackState.ACTION_SEEK_TO);

                    mediaSession.setMetadata(metadata.build());
                    mediaSession.setPlaybackState(state_builder.build());
                    setMediaSessionCallback();

                    state_builder.addCustomAction("h","b",R.drawable.ic_round_fast_forward_24);
                    state_builder.addCustomAction("r","g",R.drawable.ic_round_fast_rewind_24);
                    state_builder.addCustomAction("p","p",R.drawable.ic_round_pause_24);
                    state_builder.addCustomAction("pl","pl",R.drawable.ic_round_play_arrow_24);

                    Intent contentIntent = new Intent(this, PlayActivity.class)
                            .setAction(ACTION_CLOSE);
                    contentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    contentIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent contentPending = PendingIntent.getActivity(this, 0 ,contentIntent
                            , flag);

                    Intent prevIntent = new Intent(this, NotificationReceiver.class)
                            .setAction(ACTION_PREVIOUS);
                    PendingIntent prevPending = PendingIntent.getBroadcast(this, 0 ,prevIntent
                            , flag);

                    Intent pauseIntent = new Intent(this, NotificationReceiver.class)
                            .setAction(ACTION_PLAY);
                    PendingIntent pausePending = PendingIntent.getBroadcast(this, 0 ,pauseIntent
                            , flag);

                    Intent nextIntent = new Intent(this, NotificationReceiver.class)
                            .setAction(ACTION_NEXT);
                    PendingIntent nextPending = PendingIntent.getBroadcast(this, 0 ,nextIntent
                            , flag);

                    Intent lockScreenIntent = new Intent(this, LockScreenActivity.class);
                    PendingIntent lockScreenPending = PendingIntent.getActivity(this, 0, lockScreenIntent, flag);

                    Notification.Action.Builder play_pause_action = new Notification.Action.Builder((isPlaying()) ? R.drawable.ic_round_pause_24: R.drawable.ic_round_play_arrow_24, "Play Pause", pausePending);
                    Notification.Action.Builder next_action = new Notification.Action.Builder(R.drawable.ic_round_fast_forward_24, "Next", nextPending);
                    Notification.Action.Builder previous_action = new Notification.Action.Builder(R.drawable.ic_round_fast_rewind_24, "Previous", prevPending);

                    Notification.MediaStyle mediaStyle = new Notification.MediaStyle().setMediaSession(mediaSession.getSessionToken());
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        Notification.Builder notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                                .setStyle(mediaStyle)
                                .setSmallIcon(R.drawable.triangle_app_logo_notification)
                                .setContentTitle(song.get(position).getTitle())
                                .setContentText(song.get(position).getArtist())
                                .setContentIntent(contentPending)
                                .setVisibility(Notification.VISIBILITY_PUBLIC)
                                .setOnlyAlertOnce(true)
                                .setAutoCancel(true)
                                .setChannelId(getApplicationContext().getPackageName())
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                .setFullScreenIntent(lockScreenPending, true)
                                .addAction(previous_action.build())
                                .addAction(play_pause_action.build())
                                .addAction(next_action.build())
                                .setChannelId(getApplicationContext().getPackageName());

                        mediaStyle.setShowActionsInCompactView(0, 1, 2);

                        Notification notify = notification.build();
                        startForeground(1, notify);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMediaSessionCallback() {
        if (mediaSession != null) {
            if (media_session_callbacks != null && __ALLOW_HEAD_SET_CONTROLS) {
                mediaSession.setCallback(media_session_callbacks);
                mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
                mediaSession.setActive(true);
            } else {
                mediaSession.setCallback(media_session_callbacks_not_on);
            }
        }
    }

    public void sendNotifyOldVersion()
    {
        Bitmap bitmap;

        if (song != null) {
            if (song.size() > 0) {
                bitmap = CacheImageManager.getImage(getApplicationContext(), song.get(position));

                if (bitmap == null) {
                    byte[] image = getAlbumArt(song.get(position).getPath());
                    if (image != null) {
                        bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    } else {
                        if (defaultNotifyBitmap == null)
                            getDefaultBitmapForNotification();
                        bitmap = defaultNotifyBitmap;
                    }
                }

                final int flag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;

                Intent prevIntent = new Intent(this, NotificationReceiver.class)
                        .setAction(ACTION_PREVIOUS);
                PendingIntent prevPending = PendingIntent.getBroadcast(this, 0, prevIntent
                        , flag);

                Intent pauseIntent = new Intent(this, NotificationReceiver.class)
                        .setAction(ACTION_PLAY);
                PendingIntent pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent
                        , flag);

                Intent nextIntent = new Intent(this, NotificationReceiver.class)
                        .setAction(ACTION_NEXT);
                PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent
                        , flag);

                Intent contentIntent = new Intent(this, PlayActivity.class)
                        .setAction(ACTION_CLOSE);
                contentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                contentIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent contentPending = PendingIntent.getActivity(this, 0, contentIntent
                        , flag);

                Intent lockScreenIntent = new Intent(this, LockScreenActivity.class);
                PendingIntent lockScreenPending = PendingIntent.getActivity(this, 0, lockScreenIntent, flag);


                Notification builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.triangle_app_logo_notification)
                        .setContentTitle(song.get(position).getTitle())
                        .setContentText(song.get(position).getArtist())
                        .setLargeIcon(bitmap)
                        .setContentIntent(contentPending)
                        .addAction(R.drawable.ic_round_fast_rewind_24, "Previous", prevPending)
                        .addAction(isPlaying() ? R.drawable.ic_round_pause_24 : R.drawable.ic_round_play_arrow_24, "Play Pause",
                                pausePending)
                        .addAction(R.drawable.ic_round_fast_forward_24, "Next", nextPending)
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setOnlyAlertOnce(true)
                        .setFullScreenIntent(lockScreenPending, true)
                        .setAutoCancel(true)
                        .setChannelId(getApplicationContext().getPackageName())
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .build();

                // notificationId is a unique int for each notification that you must define
                // notificationManager.notify(1, builder);
                startForeground(1, builder);
            }
        }
    }

    private void getDefaultBitmapForNotification() {
        try {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_music, null);
            if (drawable != null) {
                defaultNotifyBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(defaultNotifyBitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
