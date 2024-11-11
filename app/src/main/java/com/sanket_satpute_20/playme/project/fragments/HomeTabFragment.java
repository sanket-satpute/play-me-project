package com.sanket_satpute_20.playme.project.fragments;

import static com.sanket_satpute_20.playme.project.service.BackService.most_played_songs;
import static com.sanket_satpute_20.playme.project.service.BackService.recentPlayed;
import static com.sanket_satpute_20.playme.project.service.BackService.recent_album;
import static com.sanket_satpute_20.playme.project.service.BackService.recent_artist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.activity.MostPlayedActivity;
import com.sanket_satpute_20.playme.project.activity.RecentAlbumActivity;
import com.sanket_satpute_20.playme.project.activity.RecentArtistActivity;
import com.sanket_satpute_20.playme.project.activity.RecentPlayedActivity;
import com.sanket_satpute_20.playme.project.model.Albums;
import com.sanket_satpute_20.playme.project.recycler_views.GridAlbumRecycle;
import com.sanket_satpute_20.playme.project.recycler_views.GridArtistRecycle;
import com.sanket_satpute_20.playme.project.recycler_views.MostPlayedRecycle;

public class HomeTabFragment extends Fragment {

    ConstraintLayout recent_song_layout, most_played_layout, recent_artist_layout, recent_album_layout;
    TextView recent_played_txt, most_played_txt, recent_album_txt, recent_artist_txt,
    recent_not_found_txt, recent_album_not_found_txt, recent_artist_not_found_txt;
    ImageView arrow_open_recent_song, arrow_open_most_played, arrow_open_recent_album, arrow_open_recent_artist;
    RecyclerView recent_played_recycler, most_played_recycler, recent_artist_recycler, recent_album_recycler;


//    adapters
    public static MostPlayedRecycle most_played_adapter;
    GridArtistRecycle adapterRecentArtist;
    GridAlbumRecycle adapterRecentAlbum;
    MostPlayedRecycle adapterRecentPlayed;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case "most_played.added.song" :
                        requireActivity().runOnUiThread(() -> {
                            most_played_layout.setVisibility(View.VISIBLE);
                            setMostPlayedRecycle();
                        });
                        break;
                    case "recent_played.added.song" :
                        recent_not_found_txt.setVisibility(View.GONE);
                        requireActivity().runOnUiThread(() -> setRecentPlayedRecycle());
                        break;
                    case "action_song_deleted.REMOVE_FULL_RECENT_ALBUM_OR_FILE_INSIDE_RECENT_ALBUM" :
                        setRecentPlayedAlbumRecycle();
                        break;
                    case "action_song_deleted.REMOVE_FULL_RECENT_ARTIST_OR_FILE_INSIDE_RECENT_ARTIST" :
                        setRecentPlayedArtistRecycle();
                        break;
                    case "action_song_deleted.REMOVE_FULL_SONG_OR_FILE_FROM_RECENT_SONGS" :
                        setRecentPlayedRecycle();
                        break;
                    case "action_song_deleted.REMOVE_FULL_SONG_OR_FILE_FROM_MOST_PLAYED_SONGS" :
                        setMostPlayedRecycle();
                        break;
                    case "action_from_most_played_or_recent_recycle.ITEM_IS_NOT_PRESENT_IN_STORAGE.Removed" :
                        boolean isMostPlayed = intent.getBooleanExtra("IT_IS_MOST_PLAYED_NOT_RECENT", false);
                        boolean item_present_in_parent = intent.getBooleanExtra("IN_PARENT_LIST_ITEM_PRESENT", false);
                        int removable_position = intent.getIntExtra("REMOVED_ITEM_POSITION", -1);
                        if (removable_position != -1) {
                            removeItemMostPlayedOrRecentPlayed(removable_position, item_present_in_parent, isMostPlayed);
                        } else {
                            if (isMostPlayed)
                                setMostPlayedRecycle();
                            else
                                setRecentPlayedRecycle();
                        }
                        break;
                    case "action.from_grid_or_non_grid_album_recycler_view.ITEM_REMOVED.FOR_0_Songs" :
                        String fromHere = intent.getStringExtra("THIS_ALBUM_RECYCLER_FROM");
                        if (fromHere.equals("AlbumFragment")) {
                            int r_position = intent.getIntExtra("REMOVED_ITEM_POSITION", -1);
                            Albums album_came = (Albums) intent.getSerializableExtra("IN_PARENT_LIST_ITEM_PRESENT_FULL_ALBUM");
                            boolean isGridRecycler = intent.getBooleanExtra("IS_GRID_RECYCLER", true);
                            if (recent_album.size() > r_position && r_position > -1) {
                                if (album_came.getAlbum_name().equals(recent_album.get(r_position).getAlbum_name())
                                        && album_came.getAlbum_files().size() == recent_album.get(r_position).getAlbum_files().size()) {
                                    recent_album.remove(r_position);
                                    if (isGridRecycler && adapterRecentAlbum != null) {
                                        adapterRecentAlbum.notifyItemRemoved(r_position);
                                        adapterRecentAlbum.notifyItemRangeRemoved(r_position, recent_album.size());
                                    }
                                }

                            }
                        }
                        break;
                }
            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_tab, container, false);
        initViews(view);
        doExtra();
        onClick();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("most_played.added.song");
        filter.addAction("recent_played.added.song");
        filter.addAction("action_song_deleted.REMOVE_FULL_RECENT_ALBUM_OR_FILE_INSIDE_RECENT_ALBUM");
        filter.addAction("action_from_most_played_or_recent_recycle.ITEM_IS_NOT_PRESENT_IN_STORAGE.Removed");
        filter.addAction("action.from_grid_or_non_grid_album_recycler_view.ITEM_REMOVED.FOR_0_Songs");
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(receiver, filter);
        if (recentPlayed.size() > 0) {
            recent_not_found_txt.setVisibility(View.GONE);
            recent_played_recycler.setVisibility(View.VISIBLE);
            setRecentPlayedRecycle();
        } else {
            recent_not_found_txt.setVisibility(View.VISIBLE);
            recent_played_recycler.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver);
    }

    private void doExtra() {
        if (most_played_songs.size() > 0) {
            most_played_layout.setVisibility(View.VISIBLE);
            setMostPlayedRecycle();
        } else {
            most_played_layout.setVisibility(View.GONE);
        }

        if (recentPlayed.size() > 0) {
            recent_not_found_txt.setVisibility(View.GONE);
            recent_played_recycler.setVisibility(View.VISIBLE);
            setRecentPlayedRecycle();
        } else {
            recent_not_found_txt.setVisibility(View.VISIBLE);
            recent_played_recycler.setVisibility(View.GONE);
        }

        if (recent_album.size() > 0) {
            recent_album_not_found_txt.setVisibility(View.GONE);
            recent_album_recycler.setVisibility(View.VISIBLE);
            setRecentPlayedAlbumRecycle();
        } else {
            recent_album_not_found_txt.setVisibility(View.VISIBLE);
            recent_album_recycler.setVisibility(View.GONE);
        }

        if (recent_artist.size() > 0) {
            recent_not_found_txt.setVisibility(View.GONE);
            recent_artist_recycler.setVisibility(View.VISIBLE);
            setRecentPlayedArtistRecycle();
        } else {
            recent_not_found_txt.setVisibility(View.VISIBLE);
            recent_artist_recycler.setVisibility(View.GONE);
        }
    }

    private void initViews(View view) {
//        layout
        recent_song_layout = view.findViewById(R.id.recent_song_layout);
        most_played_layout = view.findViewById(R.id.most_played_song_layout);
        recent_artist_layout = view.findViewById(R.id.recent_played_artist_layout);
        recent_album_layout = view.findViewById(R.id.recent_played_album_layout);

//        textview
        recent_played_txt = view.findViewById(R.id.recent_song_txt);
        most_played_txt = view.findViewById(R.id.most_played_song_txt);
        recent_album_txt = view.findViewById(R.id.recent_played_album_txt);
        recent_artist_txt = view.findViewById(R.id.recent_played_artist_txt);
//        not found txt
        recent_not_found_txt = view.findViewById(R.id.no_recent_song_txt);
        recent_album_not_found_txt = view.findViewById(R.id.no_recent_album_txt);
        recent_artist_not_found_txt = view.findViewById(R.id.no_recent_artist_txt);

//        open activity arrow
        arrow_open_recent_song = view.findViewById(R.id.open_recent_played_arrow);
        arrow_open_most_played = view.findViewById(R.id.open_most_played_arrow);
        arrow_open_recent_album = view.findViewById(R.id.open_recent_played_album_arrow);
        arrow_open_recent_artist = view.findViewById(R.id.open_recent_artist_arrow);

//         recycler
        recent_played_recycler = view.findViewById(R.id.recent_song_recycle);
        most_played_recycler = view.findViewById(R.id.most_played_song_recycle);
        recent_artist_recycler = view.findViewById(R.id.recent_played_artist_recycle);
        recent_album_recycler = view.findViewById(R.id.recent_played_album_recycle);
    }

    private void onClick() {
        arrow_open_recent_song.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), RecentPlayedActivity.class);
            startActivity(intent);
        });

        arrow_open_most_played.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), MostPlayedActivity.class);
            startActivity(intent);
        });

        arrow_open_recent_album.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), RecentAlbumActivity.class);
            startActivity(intent);
        });

        arrow_open_recent_artist.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), RecentArtistActivity.class);
            startActivity(intent);
        });
    }

    private void setMostPlayedRecycle() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        most_played_adapter = new MostPlayedRecycle(requireActivity(), most_played_songs, true);
        most_played_recycler.setLayoutManager(layoutManager);
        most_played_recycler.setAdapter(most_played_adapter);
    }

    private void setRecentPlayedRecycle() {
        if (recentPlayed.size() > 0) {
            recent_played_recycler.setVisibility(View.VISIBLE);
            recent_not_found_txt.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
            adapterRecentPlayed = new MostPlayedRecycle(requireActivity(), recentPlayed, false);
            recent_played_recycler.setLayoutManager(layoutManager);
            recent_played_recycler.setAdapter(adapterRecentPlayed);
        } else {
            recent_played_recycler.setVisibility(View.GONE);
            recent_not_found_txt.setVisibility(View.VISIBLE);
        }
    }

    private void setRecentPlayedAlbumRecycle() {
        if (recent_album.size() > 0) {
            recent_album_recycler.setVisibility(View.VISIBLE);
            recent_album_not_found_txt.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
            adapterRecentAlbum = new GridAlbumRecycle(requireActivity(), recent_album, requireActivity().getSupportFragmentManager(), "HomeTabFragment");
            recent_album_recycler.setLayoutManager(layoutManager);
            recent_album_recycler.setAdapter(adapterRecentAlbum);
        } else {
            recent_album_recycler.setVisibility(View.GONE);
            recent_album_not_found_txt.setVisibility(View.VISIBLE);
        }
    }

    private void setRecentPlayedArtistRecycle() {
        if (recent_artist.size() > 0) {
            recent_artist_not_found_txt.setVisibility(View.GONE);
            recent_artist_recycler.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
            adapterRecentArtist = new GridArtistRecycle(requireActivity(), recent_artist);
            recent_artist_recycler.setLayoutManager(layoutManager);
            recent_artist_recycler.setAdapter(adapterRecentArtist);
        } else {
            recent_artist_not_found_txt.setVisibility(View.VISIBLE);
            recent_artist_recycler.setVisibility(View.GONE);
        }
    }

    public void removeItemMostPlayedOrRecentPlayed(int position, boolean isPresent, boolean isMostPlayed) {
        most_played_recycler.post(() -> {
            if (isMostPlayed) {
                if (isPresent && most_played_songs.size() > position && position > -1)
                    most_played_songs.remove(position);
                most_played_adapter.notifyItemRemoved(position);
                most_played_adapter.notifyItemRangeChanged(position, most_played_songs.size());
            } else {
                if (isPresent && recentPlayed.size() > position && position > -1)
                    recentPlayed.remove(position);
                adapterRecentPlayed.notifyItemRemoved(position);
                adapterRecentPlayed.notifyItemRangeChanged(position, recentPlayed.size());
            }

//            notifyItemRemoved(position);
//            notifyItemRangeChanged(position, Math.min(recent_f.size(), 7));
        });
    }

    public static MostPlayedRecycle getMostPlayedAdapter() {
        return most_played_adapter;
    }
}