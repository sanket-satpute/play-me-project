package com.sanket_satpute_20.playme.project.service;


import static com.sanket_satpute_20.playme.MainActivity.albumFilesList;
import static com.sanket_satpute_20.playme.MainActivity.artistFiles;
import static com.sanket_satpute_20.playme.MainActivity.musicFiles;
import static com.sanket_satpute_20.playme.project.activity.ScanSongsActivity.isOnSkip100kb;
import static com.sanket_satpute_20.playme.project.activity.ScanSongsActivity.isOnSkip30s;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.SortLayoutBottomFragment.ORDER;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.SORTED;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.SORTINGORDER;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sanket_satpute_20.playme.project.model.Albums;
import com.sanket_satpute_20.playme.project.model.Artists;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class SearchSongIntentService extends IntentService {

    /*  variables    */
    public static final String START_SEARCHING_SONGS = "START_SEARCHING_SONGS";
    public static String current_scanning_song_name = null;

    protected ArrayList<Artists> artists;
    protected ArrayList<Albums> albums;
    protected ArrayList<MusicFiles> n_songs;

    public static int x = 1;

    public static ArrayList<MusicFiles> scanned_files;

    private final BroadcastReceiver reciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("stop.scanning.Clicked.btn")) {
                stopSearchingSongs();
            }
        }
    };

    /*  constructor */
    public SearchSongIntentService() {
        super(SearchSongIntentService.class.getSimpleName());
    }


    /*  default methods   */
    @Override
    protected void onHandleIntent(Intent intent) {
        IntentFilter filter = new IntentFilter("stop.scanning.Clicked.btn");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(reciver, filter);
        if (intent != null) {
            final String action = intent.getAction();
            if (START_SEARCHING_SONGS.equals(action)) {
                startSearchingSongs();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(reciver);
    }

    /*  user defined methods    */
    private void startSearchingSongs() {
        x = 0;
        scanned_files = readStorageSongs(this);
        Intent finish_scanning = new Intent();
        finish_scanning.setAction("Scanning_finished.broadcast");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(finish_scanning);
        if (scanned_files.size() > 0) {
            musicFiles.clear();
            Iterator<MusicFiles> it_file = scanned_files.iterator();
            while (it_file.hasNext()) {
                MusicFiles mFile = it_file.next();
                if (x < scanned_files.size()) {
                    current_scanning_song_name = "Added " + x + " / " + scanned_files.size();
                    musicFiles.add(mFile);
                    x++;
                    try {
                        Thread.sleep(75);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
            current_scanning_song_name = "Data Saved Successfully";
        } else {
            current_scanning_song_name = "Song Not Found, List is Up to Date";
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent finish_saving = new Intent();
        finish_saving.setAction("saving_finished.broadcast");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(finish_saving);

    }

    private void stopSearchingSongs() {
        if (musicFiles != null)
            musicFiles.clear();
        if (artistFiles != null)
            artistFiles.clear();
        if (albumFilesList != null)
            albumFilesList.clear();
        try {
            musicFiles = n_songs;
            artistFiles = artists;
            albumFilesList = albums;
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.stopSelf();
    }


    /*  Read song from storage  */
    private ArrayList<MusicFiles> readStorageSongs(Context context) {

        artists = artistFiles;
        albums = albumFilesList;
        n_songs = musicFiles;
        SharedPreferences preferences = getSharedPreferences(SORTINGORDER, MODE_PRIVATE);
        String sortOrder = preferences.getString(SORTED, "BYNAME");
        String order_asc_desc = preferences.getString(ORDER, "ASC");

        ArrayList<String> art_duplicate = new ArrayList<>();
        albumFilesList.clear();
        artistFiles.clear();
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
                MediaStore.Audio.Media._ID
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

                    Log.d("g1", title);
                    current_scanning_song_name = title;
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
                    try {
                        Thread.sleep(getRandom());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            cursor.close();
        }
        return tempAudioFiles;
    }

    private int getRandom() {
        return new Random().nextInt(65);
    }

    public long getSize(String path) {
        File file = new File(path);
        return file.length();
    }

}