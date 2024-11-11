package com.sanket_satpute_20.playme.project.receivers;

import static androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance;
import static com.sanket_satpute_20.playme.MainActivity.albumFilesList;
import static com.sanket_satpute_20.playme.MainActivity.artistFiles;
import static com.sanket_satpute_20.playme.MainActivity.favouriteAlbums;
import static com.sanket_satpute_20.playme.MainActivity.favouriteArtists;
import static com.sanket_satpute_20.playme.MainActivity.favouriteList;
import static com.sanket_satpute_20.playme.MainActivity.musicFiles;
import static com.sanket_satpute_20.playme.project.service.BackService.CURRENT_PLAYING_SONG_PATH;
import static com.sanket_satpute_20.playme.project.service.BackService.most_played_songs;
import static com.sanket_satpute_20.playme.project.service.BackService.recentPlayed;
import static com.sanket_satpute_20.playme.project.service.BackService.recent_album;
import static com.sanket_satpute_20.playme.project.service.BackService.recent_artist;
import static com.sanket_satpute_20.playme.project.service.BackService.song;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.model.PlaylistArray;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyAppReceiver extends BroadcastReceiver {


    //        broadcast values
    boolean favourite_artist_removable = false, recent_artist_removable = false,
            favourite_album_removable, recent_album_removable;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals("song_recycler.SONG_IS_DELETED.Action")) {
                songDeletedAlert(context, intent);
            } else if (intent.getAction().equals("songs_deleted.SONG_IS_DELETED.Multiple_Album_Artist.Action")) {
                songsDeletedAlertMultipleAlbumArtist(context, intent);
            }
        }
    }


    private void songDeletedAlert(Context context, Intent intent) {
        int removal_index = intent.getIntExtra("DELETED_ITEM_POSITION_EXTRAS", -1);
        if (removal_index != -1) {
            if (musicFiles.size() > 0) {
                MusicFiles m_file = musicFiles.get(removal_index);
//                                    String m_file_name = m_file.getTitle();
                String m_file_path = m_file.getPath();
                String artist_name = m_file.getArtist();
                String album_name = m_file.getAlbum();

                musicFiles.remove(removal_index);

//                        send broadcast to song fragment where to notify item removed and notify item range changed
                String songFragmentAction = "action_song_deleted.REMOVE_FULL_SONG_OR_FILE_INSIDE_SONG_MAIN_LIST";
                Intent broad_intent_to_song_frag = new Intent();
                broad_intent_to_song_frag.setAction(songFragmentAction);
                broad_intent_to_song_frag.putExtra("REMOVAL_INDEX", removal_index);
                getInstance(context).sendBroadcast(broad_intent_to_song_frag);

                Executor album_executor = Executors.newSingleThreadExecutor();
                Executor artist_executor = Executors.newSingleThreadExecutor();
                Executor recent_played_executor = Executors.newSingleThreadExecutor();
                Executor favourite_files_executor = Executors.newSingleThreadExecutor();
                Executor playlist_files_executor = Executors.newSingleThreadExecutor();
                Executor most_played_executor = Executors.newSingleThreadExecutor();
                Executor current_playing_executor = Executors.newSingleThreadExecutor();

                current_playing_executor.execute(() -> {
                    try {
                        int parent_i = -1;
                        if (song != null) {
                            if (song.size() > 0) {
                                for (int i = 0; i < song.size(); i++) {
                                    if (m_file_path.trim().equals(song.get(i).getPath())) {
                                        parent_i = i;
                                        break;
                                    }
                                }
                            }
                        }

                        final int song_current_position = parent_i;
                        if (parent_i != -1) {
                            song.remove(parent_i);
                        }

                        Handler handler = new Handler(Looper.getMainLooper());

                        handler.post(() -> {
                            if (CURRENT_PLAYING_SONG_PATH != null) {
                                Log.d("i_am", "if 1");
                                if (m_file_path.trim().equals(CURRENT_PLAYING_SONG_PATH) && song_current_position != -1) {
                                    Log.d("i_am", "if 2 " + song_current_position);
                                    Intent change_song_broadcast = new Intent();
                                    String action_change_song = "action_song_deleted.IF_NEXT_SONG_AVAIL_THEN_CHANGE_SONG_ELSE_HIDE_BOTTOM_PLAYER";
                                    change_song_broadcast.setAction(action_change_song);
                                    change_song_broadcast.putExtra("CURRENT_SONG_POSITION", song_current_position);
                                    getInstance(context).sendBroadcast(change_song_broadcast);
                                }
                            }

                            String current_playing_action = "action_song_deleted.REMOVE_FULL_CURRENT_PLAYING_OR_FILE_INSIDE_CURRENT_PLAYING";
                            Intent broad_intent_current_playing_removal = new Intent();
                            broad_intent_current_playing_removal.setAction(current_playing_action);
                            getInstance(context).sendBroadcast(broad_intent_current_playing_removal);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                album_executor.execute(() -> {
                    try {
                        int parent_i = -1, parent_j = -1, parent_k = -1, parent_l = -1, parent_m = -1, parent_n = -1;
                        for (int i = 0; i < albumFilesList.size(); i++) {
                            if (album_name.trim().equals(albumFilesList.get(i).getAlbum_name())) {
                                if (albumFilesList.get(i).getAlbum_files().size() > 1) {
                                    for (int j = 0; j < albumFilesList.get(i).getAlbum_files().size(); j++) {
                                        if (albumFilesList.get(i).getAlbum_files().get(j).getPath().trim()
                                                .equals(m_file_path)) {
                                            parent_i = i;
                                            parent_j = j;
                                            int albumSize = albumFilesList.get(i).getAlbum_files().size();
                                            if (favouriteAlbums != null) {
                                                if (favouriteAlbums.size() > 0) {
                                                    for (int k = 0; k < favouriteAlbums.size(); k++) {
                                                        if (artist_name.trim().equals(favouriteAlbums.get(k).getAlbum_name()) &&
                                                                albumSize == favouriteAlbums.get(k).getAlbum_files().size()) {
                                                            for (int l = 0; l < favouriteAlbums.get(k).getAlbum_files().size(); l++) {
                                                                if (m_file_path.trim().equals(favouriteAlbums.get(k)
                                                                        .getAlbum_files().get(l).getPath())) {
                                                                    parent_k = k;
                                                                    parent_l = l;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            if (recent_album != null) {
                                                if (recent_album.size() > 0) {
                                                    for (int m = 0; m < recent_album.size(); m++) {
                                                        if (album_name.trim().equals(recent_album.get(m).getAlbum_name()) &&
                                                                albumSize == recent_album.get(m).getAlbum_files().size()) {
                                                            for (int n = 0; n < recent_album.get(m).getAlbum_files().size(); n++) {
                                                                if (m_file_path.trim().equals(recent_album.get(m)
                                                                        .getAlbum_files().get(n).getPath())) {
                                                                    parent_m = m;
                                                                    parent_n = n;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                    }
                                } else if (albumFilesList.get(i).getAlbum_files().size() == 1) {
                                    parent_i = i;
                                    if (favouriteAlbums != null) {
                                        if (favouriteAlbums.size() > 0) {
                                            for (int k = 0; k < favouriteAlbums.size(); k++) {
                                                if (album_name.trim().equals(favouriteAlbums.get(k).getAlbum_name())) {
                                                    parent_k = k;
                                                }
                                            }
                                        }
                                    }

                                    if (recent_album != null) {
                                        if (recent_album.size() > 0) {
                                            for (int m = 0; m < recent_album.size(); m++) {
                                                if (artist_name.trim().equals(recent_album.get(m).getAlbum_name())) {
                                                    parent_m = m;
                                                }
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }

                        if (parent_i != -1 && parent_j == -1) {
                            albumFilesList.remove(parent_i);
                        } else if (parent_i != -1) {
                            albumFilesList.get(parent_i).getAlbum_files().remove(parent_j);
                        }

//                                            favourite artist
                        if (parent_k != -1 && parent_l == -1) {
                            favouriteAlbums.remove(parent_k);
                            favourite_album_removable = true;
                        } else if (parent_k != -1) {
                            favouriteAlbums.get(parent_k).getAlbum_files().remove(parent_l);
                            favourite_album_removable = true;
                        }

//                                            recent artist
                        if (parent_m != -1 && parent_n == -1) {
                            favouriteAlbums.remove(parent_m);
                            favourite_album_removable = true;
                        } else if (parent_m != -1) {
                            favouriteAlbums.get(parent_m).getAlbum_files().remove(parent_n);
                            favourite_album_removable = true;
                        }


                        Handler handler = new Handler(Looper.getMainLooper());

                        handler.post(() -> {
                            String album_action = "action_song_deleted.REMOVE_FULL_ALBUM_OR_FILE_INSIDE_ALBUM";
                            Intent broad_intent_album_removal = new Intent();
                            broad_intent_album_removal.setAction(album_action);
                            getInstance(context).sendBroadcast(broad_intent_album_removal);

                            if (favourite_album_removable) {
//                                                        send broadcast for favourite artist removed or file removed from favourite
                                String favourite_album_action = "action_song_deleted.REMOVE_FULL_FAVOURITE_ALBUM_OR_FILE_INSIDE_FAVOURITE_ALBUM";
                                Intent broad_intent_favourite_album_removal = new Intent();
                                broad_intent_favourite_album_removal.setAction(favourite_album_action);
                                getInstance(context).sendBroadcast(broad_intent_favourite_album_removal);
                                favourite_album_removable = false;
                            }

                            if (recent_album_removable) {
//                                                        send broadcast for recent artist removed or file removed from recent
                                String recent_album_action = "action_song_deleted.REMOVE_FULL_RECENT_ALBUM_OR_FILE_INSIDE_RECENT_ALBUM";
                                Intent broad_intent_recent_album_removal = new Intent();
                                broad_intent_recent_album_removal.setAction(recent_album_action);
                                getInstance(context).sendBroadcast(broad_intent_recent_album_removal);
                                recent_album_removable = false;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                artist_executor.execute(() -> {
                    try {
                        int parent_i = -1, parent_j = -1, parent_k = -1, parent_l = -1, parent_m = -1, parent_n = -1;
                        for (int i = 0; i < artistFiles.size(); i++) {
                            if (artist_name.trim().equals(artistFiles.get(i).getArtistName())) {
                                if (artistFiles.get(i).getArtistFiles().size() > 1) {
                                    for (int j = 0; j < artistFiles.get(i).getArtistFiles().size(); j++) {
                                        if (artistFiles.get(i).getArtistFiles().get(j).getPath().trim()
                                                .equals(m_file_path)) {
                                            parent_i = i;
                                            parent_j = j;
                                            int artistSize = artistFiles.get(i).getArtistFiles().size();
                                            if (favouriteArtists != null) {
                                                if (favouriteArtists.size() > 0) {
                                                    for (int k = 0; k < favouriteArtists.size(); k++) {
                                                        if (artist_name.trim().equals(favouriteArtists.get(k).getArtistName()) &&
                                                                artistSize == favouriteArtists.get(k).getArtistFiles().size()) {
                                                            for (int l = 0; l < favouriteArtists.get(k).getArtistFiles().size(); l++) {
                                                                if (m_file_path.trim().equals(favouriteArtists.get(k)
                                                                        .getArtistFiles().get(l).getPath())) {
                                                                    parent_k = k;
                                                                    parent_l = l;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            if (recent_artist != null) {
                                                if (recent_artist.size() > 0) {
                                                    for (int m = 0; m < recent_artist.size(); m++) {
                                                        if (artist_name.trim().equals(recent_artist.get(m).getArtistName()) &&
                                                                artistSize == recent_artist.get(m).getArtistFiles().size()) {
                                                            for (int n = 0; n < recent_artist.get(m).getArtistFiles().size(); n++) {
                                                                if (m_file_path.trim().equals(recent_artist.get(m)
                                                                        .getArtistFiles().get(n).getPath())) {
                                                                    parent_m = m;
                                                                    parent_n = n;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                    }
                                } else if (artistFiles.get(i).getArtistFiles().size() == 1) {
                                    parent_i = i;
                                    if (favouriteArtists != null) {
                                        if (favouriteArtists.size() > 0) {
                                            for (int k = 0; k < favouriteArtists.size(); k++) {
                                                if (artist_name.trim().equals(favouriteArtists.get(k).getArtistName())) {
                                                    parent_k = k;
                                                }
                                            }
                                        }
                                    }

                                    if (recent_artist != null) {
                                        if (recent_artist.size() > 0) {
                                            for (int m = 0; m < recent_artist.size(); m++) {
                                                if (artist_name.trim().equals(recent_artist.get(m).getArtistName())) {
                                                    parent_m = m;
                                                }
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }

                        if (parent_i != -1 && parent_j == -1) {
                            artistFiles.remove(parent_i);
                        } else if (parent_i != -1) {
                            artistFiles.get(parent_i).getArtistFiles().remove(parent_j);
                        }

//                                            favourite artist
                        if (parent_k != -1 && parent_l == -1) {
                            favouriteArtists.remove(parent_k);
                            favourite_artist_removable = true;
                        } else if (parent_k != -1) {
                            favouriteArtists.get(parent_k).getArtistFiles().remove(parent_l);
                            favourite_artist_removable = true;
                        }

//                                            recent artist
                        if (parent_m != -1 && parent_n == -1) {
                            recent_artist.remove(parent_m);
                            recent_artist_removable = true;
                        } else if (parent_m != -1) {
                            recent_artist.get(parent_m).getArtistFiles().remove(parent_n);
                            recent_artist_removable = true;
                        }

                        Handler handler = new Handler(Looper.getMainLooper());

                        handler.post(() -> {
                            String artist_action = "action_song_deleted.REMOVE_FULL_ARTIST_OR_FILE_INSIDE_ARTIST";
                            Intent broad_intent_album_removal = new Intent();
                            broad_intent_album_removal.setAction(artist_action);
                            getInstance(context).sendBroadcast(broad_intent_album_removal);

                            if (favourite_artist_removable) {
//                                                        send broadcast for favourite artist removed or file removed from favourite
                                String favourite_artist_action = "action_song_deleted.REMOVE_FULL_FAVOURITE_ARTIST_OR_FILE_INSIDE_FAVOURITE_ARTIST";
                                Intent broad_intent_favourite_artist_removal = new Intent();
                                broad_intent_favourite_artist_removal.setAction(favourite_artist_action);
                                getInstance(context).sendBroadcast(broad_intent_favourite_artist_removal);
                                favourite_artist_removable = false;
                            }

                            if (recent_artist_removable) {
//                                                        send broadcast for recent artist removed or file removed from recent
                                String recent_artist_action = "action_song_deleted.REMOVE_FULL_RECENT_ARTIST_OR_FILE_INSIDE_RECENT_ARTIST";
                                Intent broad_intent_recent_artist_removal = new Intent();
                                broad_intent_recent_artist_removal.setAction(recent_artist_action);
                                getInstance(context).sendBroadcast(broad_intent_recent_artist_removal);
                                recent_artist_removable = false;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                most_played_executor.execute(() -> {
                    try {
                        int parent_i = -1;
                        if (most_played_songs != null) {
                            if (most_played_songs.size() > 0) {
                                for (int i = 0; i < most_played_songs.size(); i++) {
                                    if (m_file_path.trim().equals(most_played_songs.get(i).getPath().trim())) {
                                        parent_i = i;
                                        break;
                                    }
                                }
                            }
                        }

                        if (parent_i != -1) {
                            most_played_songs.remove(parent_i);
                        }

                        Handler handler = new Handler(Looper.getMainLooper());

                        handler.post(() -> {
                            String most_play_action = "action_song_deleted.REMOVE_FULL_SONG_OR_FILE_FROM_MOST_PLAYED_SONGS";
                            Intent broad_intent_most_played_removal = new Intent();
                            broad_intent_most_played_removal.setAction(most_play_action);
                            getInstance(context).sendBroadcast(broad_intent_most_played_removal);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                recent_played_executor.execute(() -> {
                    try {
                        int parent_i = -1;
                        for (int i = 0; i < recentPlayed.size(); i++) {
                            if (m_file_path.trim().equals(recentPlayed.get(i).getPath().trim())) {
                                parent_i = i;
                                break;
                            }
                        }

                        if (parent_i != -1) {
                            recentPlayed.remove(parent_i);
                        }

                        Handler handler = new Handler(Looper.getMainLooper());

                        handler.post(() -> {
                            String recent_action = "action_song_deleted.REMOVE_FULL_SONG_OR_FILE_FROM_RECENT_SONGS";
                            Intent broad_intent_recent_played_removal = new Intent();
                            broad_intent_recent_played_removal.setAction(recent_action);
                            getInstance(context).sendBroadcast(broad_intent_recent_played_removal);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                favourite_files_executor.execute(() -> {
                    try {
                        int parent_i = -1;
                        for (int i = 0; i < favouriteList.size(); i++) {
                            if (m_file_path.trim().equals(favouriteList.get(i).getPath())) {
                                parent_i = i;
                            }
                        }

                        if (parent_i != -1) {
                            favouriteList.remove(parent_i);
                        }

                        Handler handler = new Handler(Looper.getMainLooper());

                        handler.post(() -> {
                            String favourite_action = "action_song_deleted.REMOVE_FULL_FAVOURITE_OR_FILE_INSIDE_FAVOURITE";
                            Intent broad_intent_favourite_removal = new Intent();
                            broad_intent_favourite_removal.setAction(favourite_action);
                            getInstance(context).sendBroadcast(broad_intent_favourite_removal);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                playlist_files_executor.execute(() -> {
                    try {
                        if (PlaylistArray.ref != null) {
                            if (PlaylistArray.ref.size() > 0) {
                                for (int i = 0; i < PlaylistArray.ref.size(); i++) {
                                    if (PlaylistArray.ref.get(i) != null) {
                                        if (PlaylistArray.ref.get(i).getPlaylist().size() > 0) {
                                            for (int j = 0; j < PlaylistArray.ref.get(i).getPlaylist().size(); j++) {
                                                if (PlaylistArray.ref.get(i).getPlaylist().get(j).getPath()
                                                        .trim().equals(m_file_path.trim())) {
                                                    try {
                                                        if (j > 0 && j < PlaylistArray.ref.get(i).getPlaylist().size())
                                                            PlaylistArray.ref.get(i).getPlaylist().remove(j);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Handler handler = new Handler(Looper.getMainLooper());

                        handler.post(() -> {
                            String playlist_action = "action_song_deleted.REMOVE_FULL_PLAYLIST_OR_FILE_INSIDE_PLAYLIST";
                            Intent broad_intent_playlist_removal = new Intent();
                            broad_intent_playlist_removal.setAction(playlist_action);
                            getInstance(context).sendBroadcast(broad_intent_playlist_removal);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void songsDeletedAlertMultipleAlbumArtist(Context context, Intent intent) {
//        types -- > 1) Multiple  , 2) AlbumsMultiple     , 3) ArtistsMultiple

        ArrayList<MusicFiles> m_files = (ArrayList<MusicFiles>) intent.getSerializableExtra("DELETED_SONG_LIST_REMOVABLE");
        String type = intent.getStringExtra("DELETED_SONG_LIST_TYPE");
        if (m_files != null && m_files.size() > 0) {

            if (type != null && type.equalsIgnoreCase("AlbumsMultiple")) {
                try {
                    Executor albumExecutors = Executors.newSingleThreadExecutor();
                    albumExecutors.execute(() -> {
                        int removed_album_position;
                        for (int i = 0; i < albumFilesList.size(); i++) {
                            if (m_files.get(0).getAlbum().trim().equals(albumFilesList.get(i).getAlbum_name())) {
                                removed_album_position = i;
                                boolean removed_all;
                                if (m_files.size() == albumFilesList.get(i).getAlbum_files().size()) {
                                    for (int j = 0; j < favouriteAlbums.size(); j++) {
                                        if (albumFilesList.get(i).getAlbum_name().trim().equals(favouriteAlbums.get(j).getAlbum_name().trim())) {
                                            try {
                                                if (j < favouriteAlbums.size())
                                                    favouriteAlbums.remove(j);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    for (int k = 0; k < recent_album.size(); k++) {
                                        if (albumFilesList.get(i).getAlbum_name().trim().equals(recent_album.get(k).getAlbum_name().trim())) {
                                            try {
                                                if (k < recent_album.size())
                                                    recent_album.remove(k);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    removed_all = true;
                                    try {
                                        if (i < albumFilesList.size())
                                            albumFilesList.remove(i);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    removed_all = false;
                                    for (int j = 0; j < albumFilesList.get(i).getAlbum_files().size(); j++) {
                                        for (int k = 0; k < m_files.size(); k++) {
                                            if (m_files.get(k).getPath().trim().equals(albumFilesList.get(i)
                                                    .getAlbum_files().get(j).getPath().trim())) {
                                                albumFilesList.get(i)
                                                        .getAlbum_files().remove(j);
                                            }
                                        }
                                    }
                                }
                                Handler handler = new Handler(Looper.getMainLooper());
                                int finalRemoved_album_position = removed_album_position;
                                handler.post(() -> {
                                    Intent broad_intent_for_album_removal = new Intent();
                                    broad_intent_for_album_removal.setAction("songs_is_removed_OR_Full_Album_is_removed.DELETED.Action");
                                    broad_intent_for_album_removal.putExtra("REMOVED_ALL", removed_all);
                                    broad_intent_for_album_removal.putExtra("REMOVED_ALBUM_NAME", m_files.get(0).getAlbum().trim());
                                    broad_intent_for_album_removal.putExtra("REMOVED_ALBUM_POSITION", finalRemoved_album_position);
                                    getInstance(context).sendBroadcast(broad_intent_for_album_removal);
                                });
                                try {
                                    if (m_files.size() == albumFilesList.get(i).getAlbum_files().size()) {
                                        for (int m = 0; m < musicFiles.size(); m++) {
                                            if (musicFiles.get(m).getAlbum().trim().equals(m_files.get(0).getAlbum().trim())) {
                                                try {
                                                    if (m < musicFiles.size())
                                                        musicFiles.remove(m);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        for (int n = 0; n < recentPlayed.size(); n++) {
                                            if (recentPlayed.get(n).getAlbum().trim().equals(m_files.get(0).getAlbum().trim())) {
                                                try {
                                                    if (n < recentPlayed.size())
                                                        recentPlayed.remove(n);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        for (int o = 0; o < most_played_songs.size(); o++) {
                                            if (most_played_songs.get(o).getAlbum().trim().equals(m_files.get(0).getAlbum().trim())) {
                                                try {
                                                    if (o < most_played_songs.size())
                                                        most_played_songs.remove(o);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        for (int o = 0; o < favouriteList.size(); o++) {
                                            if (favouriteList.get(o).getAlbum().trim().equals(m_files.get(0).getAlbum().trim())) {
                                                try {
                                                    if (o < favouriteList.size())
                                                        favouriteList.remove(o);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    } else {
                                        for (int m = 0; m < musicFiles.size(); m++) {
                                            if (musicFiles.get(m).getAlbum().trim().equals(m_files.get(0).getAlbum().trim())) {
                                                for (int n = 0; n < m_files.size(); n++) {
                                                    if (musicFiles.get(m).getPath().trim().equals(m_files.get(n).getPath().trim())) {
                                                        try {
                                                            if (m < musicFiles.size())
                                                                musicFiles.remove(m);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        for (int m = 0; m < recentPlayed.size(); m++) {
                                            if (recentPlayed.get(m).getAlbum().trim().equals(m_files.get(0).getAlbum().trim())) {
                                                for (int n = 0; n < m_files.size(); n++) {
                                                    if (recentPlayed.get(m).getPath().trim().equals(m_files.get(n).getPath().trim())) {
                                                        try {
                                                            if (m < recentPlayed.size())
                                                                recentPlayed.remove(m);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        for (int m = 0; m < most_played_songs.size(); m++) {
                                            if (most_played_songs.get(m).getAlbum().trim().equals(m_files.get(0).getAlbum().trim())) {
                                                for (int n = 0; n < m_files.size(); n++) {
                                                    if (most_played_songs.get(m).getPath().trim().equals(m_files.get(n).getPath().trim())) {
                                                        try {
                                                            if (m < most_played_songs.size())
                                                                most_played_songs.remove(m);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        for (int m = 0; m < favouriteList.size(); m++) {
                                            if (favouriteList.get(m).getAlbum().trim().equals(m_files.get(0).getAlbum().trim())) {
                                                for (int n = 0; n < m_files.size(); n++) {
                                                    if (favouriteList.get(m).getPath().trim().equals(m_files.get(n).getPath().trim())) {
                                                        try {
                                                            if (m < favouriteList.size())
                                                                favouriteList.remove(m);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    break;  // we found our specific album so we work here after completion we exit from here
                                } catch (IndexOutOfBoundsException | NullPointerException e) {
                                    e.printStackTrace();
                                    break;
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (type.equalsIgnoreCase("ArtistsMultiple")) {
                try {
                    Executor artistExecutor = Executors.newSingleThreadExecutor();
                    artistExecutor.execute(() -> {
                        int artist_removed_position;
                        for (int i = 0; i < artistFiles.size(); i++) {
                            if (m_files.get(0).getArtist().trim().equals(artistFiles.get(i).getArtistName())) {
                                boolean removed_all;
                                artist_removed_position = i;
                                if (m_files.size() == artistFiles.get(i).getArtistFiles().size()) {
                                    for (int j = 0; j < favouriteArtists.size(); j++) {
                                        if (artistFiles.get(i).getArtistName().trim().equals(favouriteArtists.get(j).getArtistName().trim())) {
                                            try {
                                                if (j < favouriteArtists.size())
                                                    favouriteArtists.remove(j);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    for (int k = 0; k < recent_artist.size(); k++) {
                                        if (artistFiles.get(i).getArtistName().trim().equals(recent_artist.get(k).getArtistName().trim())) {
                                            try {
                                                if (k < recent_artist.size())
                                                    recent_artist.remove(k);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    removed_all = true;
                                    try {
                                        if (i < artistFiles.size())
                                            artistFiles.remove(i);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    removed_all = false;
                                    for (int j = 0; j < artistFiles.get(i).getArtistFiles().size(); j++) {
                                        for (int k = 0; k < m_files.size(); k++) {
                                            if (m_files.get(k).getPath().trim().equals(artistFiles.get(i)
                                                    .getArtistFiles().get(j).getPath().trim())) {
                                                artistFiles.get(i)
                                                        .getArtistFiles().remove(j);
                                            }
                                        }
                                    }
                                }
                                Handler handler = new Handler(Looper.getMainLooper());
                                int finalArtist_removed_position = artist_removed_position;
                                handler.post(() -> {
                                    Intent broad_intent_for_artist_removal = new Intent();
                                    broad_intent_for_artist_removal.setAction("songs_is_removed_OR_Full_Artist_is_removed.DELETED.Action");
                                    broad_intent_for_artist_removal.putExtra("REMOVED_ALL", removed_all);
                                    broad_intent_for_artist_removal.putExtra("ARTIST_REMOVAL_POSITION", finalArtist_removed_position);
                                    broad_intent_for_artist_removal.putExtra("REMOVED_ARTIST_NAME", m_files.get(0).getArtist());
                                    getInstance(context).sendBroadcast(broad_intent_for_artist_removal);
                                });

                                try {
                                    if (m_files.size() == artistFiles.get(i).getArtistFiles().size()) {
                                        for (int m = 0; m < musicFiles.size(); m++) {
                                            if (musicFiles.get(m).getArtist().trim().equals(m_files.get(0).getArtist().trim())) {
                                                try {
                                                    if (m < musicFiles.size())
                                                        musicFiles.remove(m);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        for (int n = 0; n < recentPlayed.size(); n++) {
                                            if (recentPlayed.get(n).getArtist().trim().equals(m_files.get(0).getArtist().trim())) {
                                                try {
                                                    if (n < recentPlayed.size())
                                                        recentPlayed.remove(n);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        for (int o = 0; o < most_played_songs.size(); o++) {
                                            if (most_played_songs.get(o).getArtist().trim().equals(m_files.get(0).getArtist().trim())) {
                                                try {
                                                    if (o < most_played_songs.size())
                                                        most_played_songs.remove(o);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        for (int o = 0; o < favouriteList.size(); o++) {
                                            if (favouriteList.get(o).getArtist().trim().equals(m_files.get(0).getArtist().trim())) {
                                                try {
                                                    if (o < favouriteList.size())
                                                        favouriteList.remove(o);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    } else {
                                        for (int m = 0; m < musicFiles.size(); m++) {
                                            if (musicFiles.get(m).getArtist().trim().equals(m_files.get(0).getArtist().trim())) {
                                                for (int n = 0; n < m_files.size(); n++) {
                                                    if (musicFiles.get(m).getPath().trim().equals(m_files.get(n).getPath().trim())) {
                                                        try {
                                                            if (m < musicFiles.size())
                                                                musicFiles.remove(m);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        for (int m = 0; m < recentPlayed.size(); m++) {
                                            if (recentPlayed.get(m).getArtist().trim().equals(m_files.get(0).getArtist().trim())) {
                                                for (int n = 0; n < m_files.size(); n++) {
                                                    if (recentPlayed.get(m).getPath().trim().equals(m_files.get(n).getPath().trim())) {
                                                        try {
                                                            if (m < recentPlayed.size())
                                                                recentPlayed.remove(m);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        for (int m = 0; m < most_played_songs.size(); m++) {
                                            if (most_played_songs.get(m).getArtist().trim().equals(m_files.get(0).getArtist().trim())) {
                                                for (int n = 0; n < m_files.size(); n++) {
                                                    if (most_played_songs.get(m).getPath().trim().equals(m_files.get(n).getPath().trim())) {
                                                        try {
                                                            if (m < most_played_songs.size())
                                                                most_played_songs.remove(m);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        for (int m = 0; m < favouriteList.size(); m++) {
                                            if (favouriteList.get(m).getArtist().trim().equals(m_files.get(0).getArtist().trim())) {
                                                for (int n = 0; n < m_files.size(); n++) {
                                                    if (favouriteList.get(m).getPath().trim().equals(m_files.get(n).getPath().trim())) {
                                                        try {
                                                            if (m < favouriteList.size())
                                                                favouriteList.remove(m);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    break;  // we found our specific artist so we work here after completion we exit from here
                                } catch (IndexOutOfBoundsException | NullPointerException e) {
                                    e.printStackTrace();
                                    break;
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Executor musicFilesExecutor = Executors.newSingleThreadExecutor();
                    Executor recentMusicFilesExecutor = Executors.newSingleThreadExecutor();
                    Executor favoriteMusicExecutor = Executors.newSingleThreadExecutor();
                    Executor mostPlayedMusicExecutor = Executors.newSingleThreadExecutor();
                    Executor albumExecutor = Executors.newSingleThreadExecutor();
                    Executor artistExecutor = Executors.newSingleThreadExecutor();

                    musicFilesExecutor.execute(() -> {
                        boolean items_are_removed = false;
                        int tour_stage = 0;
                        for (int j = 0; j < m_files.size(); j++) {
                            for (int i = 0; i < musicFiles.size(); i++) {
                                if (m_files.get(j).getPath().trim().equals(musicFiles.get(i).getPath().trim())) {
                                    try {
                                        if (i < musicFiles.size()) {
                                            musicFiles.remove(i);
                                            items_are_removed = true;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    tour_stage++;
                                }
                                if (tour_stage >= m_files.size())
                                    break;
                            }
                        }

                        Handler handler = new Handler(Looper.getMainLooper());
                        boolean finalItems_are_removed = items_are_removed;
                        handler.post(() -> {
                            Intent broad_music_removed = new Intent();
                            broad_music_removed.setAction("song_is_removed.DELETED.Action");
                            broad_music_removed.putExtra("ITEMS_ARE_REMOVED_ARE_NOT", finalItems_are_removed);
                            getInstance(context).sendBroadcast(broad_music_removed);
                        });
                    });

                    try {
                        recentMusicFilesExecutor.execute(() -> {
                            try {
                                for (int i = 0; i < recentPlayed.size(); i++) {
                                    for (int j = 0; j < m_files.size(); j++) {
                                        if (m_files.get(j).getPath().trim().equals(recentPlayed.get(i).getPath().trim())) {
                                            if (i < recentPlayed.size())
                                                recentPlayed.remove(i);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        favoriteMusicExecutor.execute(() -> {
                            for (int i = 0; i < favouriteList.size(); i++) {
                                for (int j = 0; j < m_files.size(); j++) {
                                    if (m_files.get(j).getPath().trim().equals(favouriteList.get(i).getPath().trim())) {
                                        try {
                                            if (i < favouriteList.size())
                                                favouriteList.remove(i);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        mostPlayedMusicExecutor.execute(() -> {
                            for (int i = 0; i < most_played_songs.size(); i++) {
                                for (int j = 0; j < m_files.size(); j++) {
                                    if (m_files.get(j).getPath().trim().equals(most_played_songs.get(i).getPath().trim())) {
                                        try {
                                            if (i < most_played_songs.size())
                                                most_played_songs.remove(i);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        albumExecutor.execute(() -> {
                            for (int i = 0; i < albumFilesList.size(); i++) {
                                for (int j = 0; j < m_files.size(); j++) {
                                    if (m_files.get(j).getAlbum().trim().equals(albumFilesList.get(i).getAlbum_name().trim())) {
                                        for (int k = 0; k < albumFilesList.get(i).getAlbum_files().size(); k++) {
                                            if (m_files.get(j).getPath().trim().equals(albumFilesList.get(i)
                                                    .getAlbum_files().get(k).getPath().trim())) {
                                                try {
                                                    if (k < albumFilesList.get(i).getAlbum_files().size()) {
                                                        for (int l = 0; l < recent_album.size(); l++) {
                                                            if (recent_album.get(l).getAlbum_name().trim().equals(albumFilesList.get(i).getAlbum_name().trim()) &&
                                                                    k < recent_album.get(l).getAlbum_files().size()) {
                                                                recent_album.get(l).getAlbum_files().remove(k);
                                                            }
                                                        }
                                                        for (int l = 0; l < favouriteAlbums.size(); l++) {
                                                            if (favouriteAlbums.get(l).getAlbum_name().trim().equals(albumFilesList.get(i).getAlbum_name().trim())
                                                                    && k < favouriteAlbums.get(l).getAlbum_files().size()) {
                                                                favouriteAlbums.get(l).getAlbum_files().remove(k);
                                                            }
                                                        }
                                                        albumFilesList.get(i).getAlbum_files().remove(k);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        artistExecutor.execute(() -> {
                            for (int i = 0; i < artistFiles.size(); i++) {
                                for (int j = 0; j < m_files.size(); j++) {
                                    if (m_files.get(j).getArtist().trim().equals(artistFiles.get(i).getArtistName().trim())) {
                                        for (int k = 0; k < artistFiles.get(i).getArtistFiles().size(); k++) {
                                            if (m_files.get(j).getPath().trim().equals(artistFiles.get(i)
                                                    .getArtistFiles().get(k).getPath().trim())) {
                                                try {
                                                    if (k < artistFiles.get(i).getArtistFiles().size()) {
                                                        for (int l = 0; l < recent_artist.size(); l++) {
                                                            if (recent_artist.get(l).getArtistName().trim().equals(artistFiles.get(i).getArtistName().trim())) {
                                                                recent_artist.get(l).getArtistFiles().remove(k);
                                                            }
                                                        }
                                                        for (int l = 0; l < favouriteArtists.size(); l++) {
                                                            if (favouriteArtists.get(l).getArtistName().trim().equals(artistFiles.get(i).getArtistName().trim())) {
                                                                favouriteArtists.get(l).getArtistFiles().remove(k);
                                                            }
                                                        }
                                                        artistFiles.get(i).getArtistFiles().remove(k);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

}