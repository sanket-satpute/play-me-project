package com.sanket_satpute_20.playme.project.service;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.most_played_storing_days;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.recent_storing_days;
import static com.sanket_satpute_20.playme.project.service.BackService.most_played_songs;
import static com.sanket_satpute_20.playme.project.service.BackService.recentPlayed;

import android.app.IntentService;
import android.content.Intent;

import com.sanket_satpute_20.playme.project.model.MusicFiles;

import java.util.ArrayList;


public class RemoveItemsIntentService extends IntentService {

    public static final String ACTION_RECENT_SONG_REMOVER = "ACTION_RECENT_SONG_REMOVER";
    public static final String ACTION_MOST_PLAYED_SONG_REMOVER = "ACTION_MOST_PLAYED_SONG_REMOVER";

    public static final String EXTRA_REFRESHED_RECENT_PLAYED = "ACTION_REFRESHED_RECENT_PLAYED";
    public static final String EXTRA_REFRESHED_MOST_PLAYED = "ACTION_REFRESHED_MOST_PLAYED";

    public static final String BROAD_EXTRAS_REFRESHED_LISTS = "BROAD_EXTRAS_REFRESHED_LISTS";

    public static final String BROAD_ACTION_REFRESHED_LISTS = "BROAD_ACTION_REFRESHED_LISTS";
    public static final String DATE_DIFFERENCE_IS = "DATE_DIFFERENCE_IS";

    Intent broad_intent = new Intent();

    ArrayList<MusicFiles> r_played = new ArrayList<>();
    ArrayList<MusicFiles> m_played = new ArrayList<>();

    int difference = 0;

    public RemoveItemsIntentService() {
        super(RemoveItemsIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            difference = intent.getIntExtra(DATE_DIFFERENCE_IS, 0);
            if (action.equals(ACTION_RECENT_SONG_REMOVER)) {
                optimizeRecentPlayed();
            } else if (action.equals(ACTION_MOST_PLAYED_SONG_REMOVER)) {
                optimizeMostPlayed();
            }
        }
    }

    private void optimizeRecentPlayed() {
//        r_played.clear();
//        if (recentPlayed.size() > 0) {
//            for (MusicFiles f: recentPlayed) {
//                int days = (f.getRecent_play_day_count() + difference);
//                if(days <= recent_storing_days) {
//                    f.setRecent_play_day_count(days);
//                    r_played.add(f);
//                } else {
//                    f.setRecent_play_day_count(0);
//                }
//            }
//            recentPlayed.clear();
//            recentPlayed.addAll(r_played);
//            broad_intent.setAction(BROAD_ACTION_REFRESHED_LISTS);
//            broad_intent.putExtra(BROAD_EXTRAS_REFRESHED_LISTS, EXTRA_REFRESHED_RECENT_PLAYED);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(broad_intent);
//        }
        if (difference >= recent_storing_days) {
            recentPlayed.clear();
        }
    }

    private void optimizeMostPlayed() {
//        m_played.clear();
//        if (most_played_songs.size() > 0) {
//            for (MusicFiles f: most_played_songs) {
//                int days = (f.getMost_played_day_count());
//                if(days <= recent_storing_days) {
//                    f.setMost_played_day_count(days + difference);
//                    m_played.add(f);
//                } else {
//                    f.setMost_played_day_count(0);
//                }
//            }
//            most_played_songs.clear();
//            most_played_songs.addAll(m_played);
//            Log.d("g1", "most played size before : " + most_played_songs.size());
//            broad_intent.setAction(BROAD_ACTION_REFRESHED_LISTS);
//            broad_intent.putExtra(BROAD_EXTRAS_REFRESHED_LISTS, EXTRA_REFRESHED_MOST_PLAYED);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(broad_intent);
//        }
        if (most_played_storing_days <= difference) {
            most_played_songs.clear();
        }
    }
}