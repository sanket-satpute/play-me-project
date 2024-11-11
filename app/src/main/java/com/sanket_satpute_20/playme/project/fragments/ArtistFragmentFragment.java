package com.sanket_satpute_20.playme.project.fragments;

import static com.sanket_satpute_20.playme.MainActivity.artistFiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.SortLayoutBottomFragment;
import com.sanket_satpute_20.playme.project.recycler_views.ArtistRecycle;
import com.sanket_satpute_20.playme.project.recycler_views.GridArtistRecycle;

public class ArtistFragmentFragment extends Fragment {


    TextView artist_info;
    ImageView grid_list_btn, sort_btn;
    RecyclerView recyclerView;
    boolean isGrid = true;

    ArtistRecycle normalAdapter;
    GridArtistRecycle gridAdapter;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case "action_song_deleted.REMOVE_FULL_ARTIST_OR_FILE_INSIDE_ARTIST" :
                        doExtra();
                        break;
                    case "songs_is_removed_OR_Full_Artist_is_removed.DELETED.Action" :
                        int artist_removal_position = intent.getIntExtra("ARTIST_REMOVAL_POSITION", -1);
                        if (artist_removal_position != -1) {
                            if (intent.getBooleanExtra("REMOVED_ALL", false)) {
                                String artist_info_str = artistFiles.size() + " Artists";
                                artist_info.setText(artist_info_str);
                                if (isGrid && gridAdapter != null) {
                                    gridAdapter.notifyItemRemoved(artist_removal_position);
                                    gridAdapter.notifyItemRangeRemoved(artist_removal_position, artistFiles.size());
                                } else if (normalAdapter != null) {
                                    normalAdapter.notifyItemRemoved(artist_removal_position);
                                    normalAdapter.notifyItemRangeRemoved(artist_removal_position, artistFiles.size());
                                }
                            } else {
                                doExtra();
                            }
                        } else {
                            doExtra();
                        }
                        break;
                    case "song_is_removed.FROM_SELECTION_SOME_SONGS_OR_ALL_ARE.Removed_For_All_Fragment" :
                        if (intent.getBooleanExtra("ITEMS_ARE_REMOVED", false))
                            doExtra();
                            break;
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_fragment, container, false);
        initViews(view);
        doExtra();
        onClick();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("action_song_deleted.REMOVE_FULL_ARTIST_OR_FILE_INSIDE_ARTIST");
        filter.addAction("songs_is_removed_OR_Full_Artist_is_removed.DELETED.Action");
        filter.addAction("song_is_removed.FROM_SELECTION_SOME_SONGS_OR_ALL_ARE.Removed_For_All_Fragment");
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(receiver, filter);

        Intent intent = new Intent();
        intent.setAction("intent.artist_fragment_resumed.To_MAIN");
        LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);

        Intent to_main_for_i_am_resume = new Intent();
        to_main_for_i_am_resume.setAction("action_to_main.SONG_FRAGMENT_RESUMED");
        LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(to_main_for_i_am_resume);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver);
    }

    private void initViews(View view) {
        artist_info = view.findViewById(R.id.artist_info_txt);
        grid_list_btn = view.findViewById(R.id.list_grid_mode);
        sort_btn = view.findViewById(R.id.sort_by);
        recyclerView = view.findViewById(R.id.artist_recyclerView);
    }

    private void doExtra() {
        String artist_info_str = artistFiles.size() + " Artists";
        artist_info.setText(artist_info_str);
        setRecycle();
    }

    private void setRecycle() {
        if (artistFiles.size() > 0) {
            if (!isGrid) {
                sort_btn.setImageResource(R.drawable.sort_for_list);
                grid_list_btn.setImageResource(R.drawable.grid_view);
                normalAdapter = new ArtistRecycle(requireActivity(), artistFiles);
                LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(normalAdapter);
            } else {
                sort_btn.setImageResource(R.drawable.sort_24);
                grid_list_btn.setImageResource(R.drawable.list_24);
                gridAdapter = new GridArtistRecycle(requireActivity(), artistFiles);
                GridLayoutManager layoutManager = new GridLayoutManager(requireActivity(), 2);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(gridAdapter);
            }
        }
    }

    private void onClick() {
        sort_btn.setOnClickListener(view -> {
            SortLayoutBottomFragment sortLayoutBottomFragment = new SortLayoutBottomFragment();
            sortLayoutBottomFragment.show(requireActivity().getSupportFragmentManager(), "ABC");
        });

        grid_list_btn.setOnClickListener(view -> {
            isGrid = !isGrid;
            setRecycle();
        });
    }
}