package com.sanket_satpute_20.playme.project.service;

import static com.sanket_satpute_20.playme.MainActivity.ACCENT_COLOR_PREFERENCE;
import static com.sanket_satpute_20.playme.MainActivity.ALBUM_CHECKED;
import static com.sanket_satpute_20.playme.MainActivity.ARTIST_CHECKED;
import static com.sanket_satpute_20.playme.MainActivity.FAVOURITE_SONGS;
import static com.sanket_satpute_20.playme.MainActivity.FAVOURITE_ALBUM;
import static com.sanket_satpute_20.playme.MainActivity.FAVOURITE_ARTIST;
import static com.sanket_satpute_20.playme.MainActivity.FAVOURITE_PLAYLIST;
import static com.sanket_satpute_20.playme.MainActivity.GESTURE_PLAY_SONG;
import static com.sanket_satpute_20.playme.MainActivity.HEADSET_ALLOW_HEADSET_CONTROLS;
import static com.sanket_satpute_20.playme.MainActivity.HEADSET_PAUSE_WHEN_BLUETOOTH_DISCONNECTED;
import static com.sanket_satpute_20.playme.MainActivity.HEADSET_PAUSE_WHEN_REMOVED;
import static com.sanket_satpute_20.playme.MainActivity.HEADSET_PLAY_WHEN_BLUETOOTH_CONNECTED;
import static com.sanket_satpute_20.playme.MainActivity.HEADSET_PLAY_WHEN_INSERTED;
import static com.sanket_satpute_20.playme.MainActivity.HOME_CHECKED;
import static com.sanket_satpute_20.playme.MainActivity.IS_SQUARE_PLAYER_VISIBLE;
import static com.sanket_satpute_20.playme.MainActivity.LAST_OPENED;
import static com.sanket_satpute_20.playme.MainActivity.LAST_PLAYED_LIST;
import static com.sanket_satpute_20.playme.MainActivity.LOCK_SCREEN_PLAY;
import static com.sanket_satpute_20.playme.MainActivity.MOST_PLAYED_STORING_DAYS_PREFERENCE;
import static com.sanket_satpute_20.playme.MainActivity.MOST_PLAYED_STORING_SONG_FOR_DAYS;
import static com.sanket_satpute_20.playme.MainActivity.NOTIFICATION_TYPE_PREFERENCE;
import static com.sanket_satpute_20.playme.MainActivity.PLAYLIST_DATA;
import static com.sanket_satpute_20.playme.MainActivity.PLAYLIST_CHECKED;
import static com.sanket_satpute_20.playme.MainActivity.RECENT_SEEK_POSITION_SONG;
import static com.sanket_satpute_20.playme.MainActivity.RECENT_STORING_DAYS_PREFERENCE;
import static com.sanket_satpute_20.playme.MainActivity.RECENT_STORING_SONG_FOR_DAYS;
import static com.sanket_satpute_20.playme.MainActivity.ROUND_ACT_BACKGROUND_TYPE;
import static com.sanket_satpute_20.playme.MainActivity.ROUND_ACT_EXPANDER_ENABLE_DISABLE;
import static com.sanket_satpute_20.playme.MainActivity.ROUND_BACKGROUND_IS_ENABLE;
import static com.sanket_satpute_20.playme.MainActivity.ROUND_PLAY_ACT_BOTTOM;
import static com.sanket_satpute_20.playme.MainActivity.SELECTED_INDEX_OF_HOME_FRAG;
import static com.sanket_satpute_20.playme.MainActivity.SELECTED_TAB_NAME_HOME_FRAG;
import static com.sanket_satpute_20.playme.MainActivity.SONG_CHECKED;
import static com.sanket_satpute_20.playme.MainActivity.SOUND_EFFECT_BASS_BOOST_PLAY_ME;
import static com.sanket_satpute_20.playme.MainActivity.SOUND_EFFECT_EQUALIZER_PLAY_ME;
import static com.sanket_satpute_20.playme.MainActivity.SOUND_EFFECT_LOUDNESS_ENHANCER_PLAY_ME;
import static com.sanket_satpute_20.playme.MainActivity.SOUND_EFFECT_VIRTUALIZER_PLAY_ME;
import static com.sanket_satpute_20.playme.MainActivity.STORING_DATA;
import static com.sanket_satpute_20.playme.MainActivity.THEME_TYPE_PREFERENCE;
import static com.sanket_satpute_20.playme.MainActivity.VISULIZER_COLOR;
import static com.sanket_satpute_20.playme.MainActivity.VISULIZER_POSITION;
import static com.sanket_satpute_20.playme.MainActivity.VISULIZER_SPEED;
import static com.sanket_satpute_20.playme.MainActivity.WHICH_ACT_STRING;
import static com.sanket_satpute_20.playme.MainActivity.albumFilesList;
import static com.sanket_satpute_20.playme.MainActivity.artistFiles;
import static com.sanket_satpute_20.playme.MainActivity.favouriteAlbums;
import static com.sanket_satpute_20.playme.MainActivity.favouriteArtists;
import static com.sanket_satpute_20.playme.MainActivity.favouriteList;
import static com.sanket_satpute_20.playme.MainActivity.favouritePlaylists;
import static com.sanket_satpute_20.playme.MainActivity.musicFiles;
import static com.sanket_satpute_20.playme.project.activity.PlayActivity.isRoundBackgroundOn;
import static com.sanket_satpute_20.playme.project.activity.PlayActivity.which_act_background_round;
import static com.sanket_satpute_20.playme.project.activity.PlayActivity.which_play_activity;
import static com.sanket_satpute_20.playme.project.activity.PlayerDesignActivity.is_visulizer_constraint_visible;
import static com.sanket_satpute_20.playme.project.activity.PlayerDesignActivity.vis_color;
import static com.sanket_satpute_20.playme.project.activity.PlayerDesignActivity.vis_speed;
import static com.sanket_satpute_20.playme.project.activity.PlayerDesignActivity.visulizer_position;
import static com.sanket_satpute_20.playme.project.activity.ScanSongsActivity.isOnSkip100kb;
import static com.sanket_satpute_20.playme.project.activity.ScanSongsActivity.isOnSkip30s;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.SortLayoutBottomFragment.ORDER;
import static com.sanket_satpute_20.playme.project.enums.PlayActBottom.FRAMES;
import static com.sanket_satpute_20.playme.project.enums.PlayActWhich.ROUND;
import static com.sanket_satpute_20.playme.project.enums.ROUND_ACT_BACKGROUNDS.DEFAULT;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__ALLOW_HEAD_SET_CONTROLS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__IS_GESTURE_PLAY_SONG_ON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__IS_PLAY_ME_BASS_BOOST_ON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__IS_PLAY_ME_EQUALIZER_ON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__IS_PLAY_ME_LOUDNESS_ENHANCER_ON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__IS_PLAY_ME_VIRTUALIZER_ON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__PLAY_WHEN_HEADSET_INSERTED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__PAUSE_WHEN_HEADSET_PLUGGED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__PLAY_WHEN_BLUETOOTH_CONNECTED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__PAUSE_WHEN_BLUETOOTH_DISCONNECTED;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__LOCK_SCREEN_PLAY_LISTENER_ON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.ADD_CHANGE_LOG_ARRAY;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.IS_ON_SKIP_100_KB_PREFERENCE;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.NOTIFICATION_TYPE;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.IS_ON_SKIP_30_S_PREFERENCE;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.SONG_SEEK_CURRENT_POSITION;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.most_played_storing_days;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.recent_storing_days;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.SORTED;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.SORTINGORDER;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.album_checked;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.artist_checked;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.home_checked;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.playlist_checked;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.selected_index;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.song_checked;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.tab_name;
import static com.sanket_satpute_20.playme.project.fragments.PlayOptionFragment.expand_visible_disable;
import static com.sanket_satpute_20.playme.project.fragments.PlayOptionFragment.is_which_bottom;
import static com.sanket_satpute_20.playme.project.service.BackService.most_played_songs;
import static com.sanket_satpute_20.playme.project.service.BackService.recentPlayed;
import static com.sanket_satpute_20.playme.project.service.BackService.song;
import static com.sanket_satpute_20.playme.project.service.RemoveItemsIntentService.ACTION_MOST_PLAYED_SONG_REMOVER;
import static com.sanket_satpute_20.playme.project.service.RemoveItemsIntentService.ACTION_RECENT_SONG_REMOVER;
import static com.sanket_satpute_20.playme.project.service.RemoveItemsIntentService.DATE_DIFFERENCE_IS;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sanket_satpute_20.playme.project.enums.PlayActBottom;
import com.sanket_satpute_20.playme.project.enums.PlayActWhich;
import com.sanket_satpute_20.playme.project.enums.ROUND_ACT_BACKGROUNDS;
import com.sanket_satpute_20.playme.project.model.Albums;
import com.sanket_satpute_20.playme.project.model.Artists;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.model.Playlist;
import com.sanket_satpute_20.playme.project.model.PlaylistArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class AppStarterIntentService extends IntentService {

    public static int ACCENT_COLOR;
    public static int STATUS_BAR_COLOR;
    public static Drawable THEME_COLOR;
    public static String THEME_TYPE;

    String last_time;
    public static int date_difference = 0;

    SharedPreferences theme_preference;

    public AppStarterIntentService() {
        super("AppStarterIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                getData();
            } catch (Exception e) {
                Log.d("mmkv", "in catch service " + e.getMessage());
                e.printStackTrace();
            }

            if (intent.getBooleanExtra("PERMISSION_GRANTED", false)) {
                Log.d("acp", "Permission Granted");
                musicFiles = readStorageSongs(getApplicationContext());
                serviceStartedPermissionGranted();
            } else {
                Log.d("acp", "Permission Not Granted");
            }
        }
    }

    private void serviceStartedPermissionGranted() {
        Intent intent = new Intent();
        intent.setAction("all_loaded.Start_Second.Activity");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        stopSelf();
    }

    private ArrayList<MusicFiles> readStorageSongs(Context context) {

        SharedPreferences preferences = getSharedPreferences(SORTINGORDER, MODE_PRIVATE);
        String sortOrder = preferences.getString(SORTED, "BYNAME");
        String order_asc_desc = preferences.getString(ORDER, "ASC");

        ArrayList<String> art_duplicate = new ArrayList<>();
        albumFilesList.clear();
        ArrayList<MusicFiles> tempAudioFiles = new ArrayList<>();
        String order = null;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        switch (sortOrder)
        {
            case "BYNAME":
                order = MediaStore.MediaColumns.DISPLAY_NAME + " " + order_asc_desc;
                break;
            case "BYDATE":
                order = MediaStore.MediaColumns.DATE_ADDED + " " + order_asc_desc;
                break;
            case "BYSIZE":
                order = MediaStore.MediaColumns.SIZE + " " + order_asc_desc;
                break;
        }

        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID,
        };

        Cursor cursor = context.getContentResolver().query(uri, projection,null,
                null,order);

        if (cursor != null)
        {
                while(cursor.moveToNext())
                {

                    String album = cursor.getString(0);
                    String title = cursor.getString(1);
                    String duration = cursor.getString(2);
                    String path = cursor.getString(3);
                    String artist = cursor.getString(4);
                    String id = cursor.getString(5);

                    if (album == null || title == null || duration == null
                            || path == null || artist == null || id == null)
                        continue;

                    if (isOnSkip100kb && getSize(path) < 1024)
                        continue;
                    if (isOnSkip30s && Integer.parseInt(duration) < 30000)
                        continue;

                    MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration, id);
                    tempAudioFiles.add(musicFiles);
                    boolean isAlbumContain = false;
                    if (albumFilesList.size() > 0) {
                        for (int i = 0; i < albumFilesList.size(); i++) {
                            if (albumFilesList.get(i).getAlbum_name() != null) {
                                if (albumFilesList.get(i).getAlbum_name().equals(album)) {
                                    albumFilesList.get(i).getAlbum_files().add(musicFiles);
                                    isAlbumContain = true;
                                    break;
                                }
                            }
                        }
                        if (!isAlbumContain) {
                            Albums albums = new Albums(album, new ArrayList<>(), false, false);
                            albums.getAlbum_files().add(musicFiles);
                            albumFilesList.add(albums);
                        }
                    } else {
                        Albums albums = new Albums(album, new ArrayList<>(), false, false);
                        albums.getAlbum_files().add(musicFiles);
                        albumFilesList.add(albums);
                    }
                    if (art_duplicate.contains(artist)) {
                        for (int i = 0; i < artistFiles.size(); i++) {
                            if (artist.equals(artistFiles.get(i).getArtistName())) {
                                artistFiles.get(i).getArtistFiles().add(musicFiles);
                            }
                        }
                    } else {
                        art_duplicate.add(artist);
                        Artists artists = new Artists(artist, new ArrayList<>());
                        artists.getArtistFiles().add(musicFiles);
                        artistFiles.add(artists);
                    }
                }
            cursor.close();
        }
        return tempAudioFiles;
    }

    public void getData() {
        SharedPreferences preferences = getSharedPreferences(STORING_DATA, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(LAST_PLAYED_LIST, null);
        String json_playlist = preferences.getString(PLAYLIST_DATA, null);
        String json_fev_song = preferences.getString(FAVOURITE_SONGS, null);
        String json_fev_artist = preferences.getString(FAVOURITE_ARTIST, null);
        String json_fev_album = preferences.getString(FAVOURITE_ALBUM, null);
        String json_fev_playlist = preferences.getString(FAVOURITE_PLAYLIST, null);
        String m_played_for_day = preferences.getString(MOST_PLAYED_STORING_SONG_FOR_DAYS, null);
        String r_played_for_day = preferences.getString(RECENT_STORING_SONG_FOR_DAYS, null);

        which_play_activity = PlayActWhich.playActWhichTo(preferences.getString(WHICH_ACT_STRING, String.valueOf(ROUND)));
        is_which_bottom = PlayActBottom.playActBottomTo(preferences.getString(ROUND_PLAY_ACT_BOTTOM, String.valueOf(FRAMES)));
        expand_visible_disable = preferences.getBoolean(ROUND_ACT_EXPANDER_ENABLE_DISABLE, true);
        home_checked = preferences.getBoolean(HOME_CHECKED, false);
        song_checked = preferences.getBoolean(SONG_CHECKED, true);
        album_checked = preferences.getBoolean(ALBUM_CHECKED, true);
        artist_checked = preferences.getBoolean(ARTIST_CHECKED, false);
        playlist_checked = preferences.getBoolean(PLAYLIST_CHECKED, true);
        isRoundBackgroundOn = preferences.getBoolean(ROUND_BACKGROUND_IS_ENABLE, true);
        which_act_background_round = ROUND_ACT_BACKGROUNDS.valueOf(preferences.getString(ROUND_ACT_BACKGROUND_TYPE, String.valueOf(DEFAULT)));
        THEME_TYPE = preferences.getString(THEME_TYPE_PREFERENCE, "Default");
        vis_color = preferences.getInt(VISULIZER_COLOR, 0xfff3a243);
        vis_speed = preferences.getInt(VISULIZER_SPEED, 2);
        visulizer_position = preferences.getInt(VISULIZER_POSITION, 0);
        isOnSkip100kb = preferences.getBoolean(IS_ON_SKIP_100_KB_PREFERENCE, true);
        isOnSkip30s = preferences.getBoolean(IS_ON_SKIP_30_S_PREFERENCE, true);
        is_visulizer_constraint_visible = preferences.getBoolean(IS_SQUARE_PLAYER_VISIBLE, false);
        recent_storing_days = preferences.getInt(RECENT_STORING_DAYS_PREFERENCE, 7);
        most_played_storing_days = preferences.getInt(MOST_PLAYED_STORING_DAYS_PREFERENCE, 7);
        last_time = preferences.getString(LAST_OPENED, null);
        selected_index = preferences.getInt(SELECTED_INDEX_OF_HOME_FRAG, 0);
        tab_name = preferences.getString(SELECTED_TAB_NAME_HOME_FRAG, null);
        NOTIFICATION_TYPE = preferences.getString(NOTIFICATION_TYPE_PREFERENCE, "NEW");
        SONG_SEEK_CURRENT_POSITION = preferences.getLong(RECENT_SEEK_POSITION_SONG, 0);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            __PLAY_WHEN_HEADSET_INSERTED = false;
            __PAUSE_WHEN_HEADSET_PLUGGED = false;
            __PLAY_WHEN_BLUETOOTH_CONNECTED = false;
            __PAUSE_WHEN_BLUETOOTH_DISCONNECTED = false;
        } else {
            __PLAY_WHEN_HEADSET_INSERTED = preferences.getBoolean(HEADSET_PLAY_WHEN_INSERTED, true);
            __PAUSE_WHEN_HEADSET_PLUGGED = preferences.getBoolean(HEADSET_PAUSE_WHEN_REMOVED, true);
            __PLAY_WHEN_BLUETOOTH_CONNECTED = preferences.getBoolean(HEADSET_PLAY_WHEN_BLUETOOTH_CONNECTED, true);
            __PAUSE_WHEN_BLUETOOTH_DISCONNECTED = preferences.getBoolean(HEADSET_PAUSE_WHEN_BLUETOOTH_DISCONNECTED, true);
        }
        __ALLOW_HEAD_SET_CONTROLS = preferences.getBoolean(HEADSET_ALLOW_HEADSET_CONTROLS, true);
        __LOCK_SCREEN_PLAY_LISTENER_ON = preferences.getBoolean(LOCK_SCREEN_PLAY, false);
        __IS_GESTURE_PLAY_SONG_ON = preferences.getBoolean(GESTURE_PLAY_SONG, false);
        __IS_PLAY_ME_EQUALIZER_ON = preferences.getBoolean(SOUND_EFFECT_EQUALIZER_PLAY_ME, false);
        __IS_PLAY_ME_BASS_BOOST_ON = preferences.getBoolean(SOUND_EFFECT_BASS_BOOST_PLAY_ME, false);
        __IS_PLAY_ME_VIRTUALIZER_ON = preferences.getBoolean(SOUND_EFFECT_VIRTUALIZER_PLAY_ME, false);
        __IS_PLAY_ME_LOUDNESS_ENHANCER_ON = preferences.getBoolean(SOUND_EFFECT_LOUDNESS_ENHANCER_PLAY_ME, false);

        /*  theme   */
        ACCENT_COLOR = preferences.getInt(ACCENT_COLOR_PREFERENCE, 0xfff3a243);
        if (ACCENT_COLOR == 0) {
            ACCENT_COLOR = 0xfff3a243;
        }

        Type type = new TypeToken<ArrayList<MusicFiles>>() {}.getType();
        Type type_playlist = new TypeToken<ArrayList<Playlist>>() {}.getType();
        Type type_fev_song = new TypeToken<ArrayList<MusicFiles>>() {}.getType();
        Type type_fev_artist = new TypeToken<ArrayList<Artists>>() {}.getType();
        Type type_fev_album = new TypeToken<ArrayList<Albums>>() {}.getType();
        Type type_fev_playlist = new TypeToken<ArrayList<Playlist>>() {}.getType();

        song = (gson.fromJson(json, type) == null) ? new ArrayList<>() : gson.fromJson(json, type);
        ArrayList<Playlist> p_list = gson.fromJson(json_playlist, type_playlist);
        PlaylistArray.ref = (p_list == null) ? new ArrayList<>() : p_list;
        favouriteList = (gson.fromJson(json_fev_song, type_fev_song) == null) ? new ArrayList<>() :  gson.fromJson(json_fev_song, type_fev_song);
        favouriteArtists = (gson.fromJson(json_fev_artist, type_fev_artist) == null) ? new ArrayList<>() :  gson.fromJson(json_fev_artist, type_fev_artist);
        favouriteAlbums = (gson.fromJson(json_fev_album, type_fev_album) == null) ? new ArrayList<>() :  gson.fromJson(json_fev_album, type_fev_album);
        favouritePlaylists = (gson.fromJson(json_fev_playlist, type_fev_playlist) == null) ? new ArrayList<>() :  gson.fromJson(json_fev_playlist, type_fev_playlist);
        most_played_songs = (gson.fromJson(m_played_for_day, type) == null) ? new ArrayList<>() : gson.fromJson(m_played_for_day, type);
        recentPlayed = (gson.fromJson(r_played_for_day, type) == null) ? new ArrayList<>() : gson.fromJson(r_played_for_day, type);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && last_time != null) {
            date_difference = getDateDifference(last_time);
        }

        startChangeLogService();
//        getThemeSaving();

        if (date_difference > 0) {
            if (most_played_songs.size() > 0) {
                Intent intent = new Intent(this, RemoveItemsIntentService.class);
                intent.setAction(ACTION_MOST_PLAYED_SONG_REMOVER);
                intent.putExtra(DATE_DIFFERENCE_IS, date_difference);
                startService(intent);
            }
            if (recentPlayed.size() > 0) {
                Intent intent = new Intent(this, RemoveItemsIntentService.class);
                intent.setAction(ACTION_RECENT_SONG_REMOVER);
                intent.putExtra(DATE_DIFFERENCE_IS, date_difference);
                startService(intent);
            }
        }
    }

    public long getSize(String path) {
        File file = new File(path);
        return file.length();
    }

    private int getDateDifference(String time) {
        LocalDate d1;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            d1 = LocalDate.now();
            LocalDate d2 = LocalDate.parse(time);
            return Math.toIntExact(ChronoUnit.DAYS.between(d2, d1));
        }
        return -1;
    }

    private void startChangeLogService() {
        Intent intent = new Intent(this, ChangeLogDataAdderIntentService.class);
        intent.setAction(ADD_CHANGE_LOG_ARRAY);
        startService(intent);
    }

    /*private void getThemeSaving() {
        if (theme_preference == null)
            theme_preference = getSharedPreferences(THEME_PREFERENCE, MODE_PRIVATE);
        String theme_type = theme_preference.getString(THEME_TYPE_PREFERENCE, NORMAL_THEME);
        Toast.makeText(this, theme_type, Toast.LENGTH_SHORT).show();
        switch (theme_type) {
            case NORMAL_THEME:
                Toast.makeText(this, "it is Normal", Toast.LENGTH_SHORT).show();
                break;
            case SOLID_THEME:
                saveSolidTheme();
                break;
            case IMAGE_THEME:
                saveImageTheme();
                break;
            case GRADIENT_THEME:
                saveGradientTheme();
                break;
        }
    }

    private void saveSolidTheme() {
        SOLID_THEME_SELECTED_COLOR = theme_preference.getInt(THEME_SOLID_SELECTED_COLOR, 0xff999999);
    }


    private void saveImageTheme() {
        Gson gson = new Gson();
        String theme_child_image = theme_preference.getString(THEME_IMAGE_SELECTED_IMAGE, null);
        Type type = new TypeToken<Bitmap>() {}.getType();
        IMAGE_THEME_SELECTED_IMAGE = gson.fromJson(theme_child_image, type);
    }

    private void saveGradientTheme() {
        Gson gson = new Gson();
        GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TOP_BOTTOM;
        String theme_child_gradient = theme_preference.getString(THEME_GRADIENT_SELECTED_GRADIENT, null);
        switch (theme_preference.getString(GRADIENT_THEME_ORIENTATION, "T_TO_B")) {
            case "B_TO_T":
                orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            case "L_TO_R":
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            case "R_TO_L":
                orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                break;
            case "TR_TO_BL":
                orientation = GradientDrawable.Orientation.TR_BL;
                break;
            case "TL_TO_BR":
                orientation = GradientDrawable.Orientation.TL_BR;
                break;
            case "BR_TO_TL":
                orientation = GradientDrawable.Orientation.BR_TL;
                break;
            case "BL_TO_TR":
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
        }
        Type type_gradient = new TypeToken<List<int[]>>() {}.getType();
        List<int[]> gradient = gson.fromJson(theme_child_gradient, type_gradient);
        GRADIENT_THEME_SELECTED_GRADIENT_COLOR = new GradientDrawable(orientation, gradient.get(0));
    }*/
}