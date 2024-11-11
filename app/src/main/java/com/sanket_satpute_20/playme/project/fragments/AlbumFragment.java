package com.sanket_satpute_20.playme.project.fragments;

import static com.sanket_satpute_20.playme.MainActivity.albumFilesList;

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

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.SortLayoutBottomFragment;
import com.sanket_satpute_20.playme.project.model.Albums;
import com.sanket_satpute_20.playme.project.recycler_views.AlbumsRecycle;
import com.sanket_satpute_20.playme.project.recycler_views.GridAlbumRecycle;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {

    RecyclerView recyclerView;
    AlbumsRecycle albumAdapter;
    GridAlbumRecycle gridRecycler;
    GridLayoutManager gridLayout;
    TextView song_info;
    ImageView list_grid_mode, sort;

    ArrayList<Albums> albumFiles;
    View view;

    boolean isGridContainer = true;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case "action_song_deleted.REMOVE_FULL_ALBUM_OR_FILE_INSIDE_ALBUM":
                        doAction();
                        break;
                    case "songs_is_removed_OR_Full_Album_is_removed.DELETED.Action":
                        int removable_position = intent.getIntExtra("REMOVED_ALBUM_POSITION", -1);
                        if (removable_position >= 0) {
                            boolean all_songs_removed = intent.getBooleanExtra("REMOVED_ALL", false);
                            if (all_songs_removed) {
                                if (isGridContainer && gridRecycler != null) {
                                    gridRecycler.notifyItemRemoved(removable_position);
                                    gridRecycler.notifyItemRangeRemoved(removable_position, albumFiles.size());
                                } else if (albumAdapter != null) {
                                    albumAdapter.notifyItemRemoved(removable_position);
                                    albumAdapter.notifyItemRangeRemoved(removable_position, albumFiles.size());
                                } else {
                                    doAction();
                                }
                            } else {
                                doAction();
                            }
                        }
                        break;
                    case "song_is_removed.FROM_SELECTION_SOME_SONGS_OR_ALL_ARE.Removed_For_All_Fragment":
                        if (intent.getBooleanExtra("ITEMS_ARE_REMOVED", false))
                            doAction();
                        break;
                    case "action.from_grid_or_non_grid_album_recycler_view.ITEM_REMOVED.FOR_0_Songs" :
                        String fromHere = intent.getStringExtra("THIS_ALBUM_RECYCLER_FROM");
                        if (fromHere.equals("AlbumFragment")) {
                            int r_position = intent.getIntExtra("REMOVED_ITEM_POSITION", -1);
                            Albums album_came = (Albums) intent.getSerializableExtra("IN_PARENT_LIST_ITEM_PRESENT_FULL_ALBUM");
                            boolean isGridRecycler = intent.getBooleanExtra("IS_GRID_RECYCLER", true);
                            if (albumFiles.size() > r_position && r_position > -1) {
                                if (album_came.getAlbum_name().equals(albumFiles.get(r_position).getAlbum_name())
                                        && album_came.getAlbum_files().size() == albumFiles.get(r_position).getAlbum_files().size()) {
                                    albumFiles.remove(r_position);
                                    if (isGridRecycler && gridRecycler != null) {
                                        gridRecycler.notifyItemRemoved(r_position);
                                        gridRecycler.notifyItemRangeRemoved(r_position, albumFiles.size());
                                    }
                                    else if (albumAdapter != null) {
                                        albumAdapter.notifyItemRemoved(r_position);
                                        albumAdapter.notifyItemRangeRemoved(r_position, albumFiles.size());
                                    }
                                }
                            }
                        }
                        break;
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_album, container, false);
        initViews(view);
        onClick();
        doAction();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("action_song_deleted.REMOVE_FULL_ALBUM_OR_FILE_INSIDE_ALBUM");
        filter.addAction("songs_is_removed_OR_Full_Album_is_removed.DELETED.Action");
        filter.addAction("song_is_removed.FROM_SELECTION_SOME_SONGS_OR_ALL_ARE.Removed_For_All_Fragment");
        filter.addAction("action.from_grid_or_non_grid_album_recycler_view.ITEM_REMOVED.FOR_0_Songs");
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(receiver, filter);

        Intent intent = new Intent();
        intent.setAction("intent.album_fragment_resumed.To_MAIN");
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
        recyclerView = view.findViewById(R.id.album_recyclerView);
        song_info = view.findViewById(R.id.album_info_txt);
        list_grid_mode = view.findViewById(R.id.list_grid_mode);
        sort = view.findViewById(R.id.sort_by);
    }

    public void onClick() {
        list_grid_mode.setOnClickListener(view -> {
            isGridContainer = !isGridContainer;
            doAction();
        });

        sort.setOnClickListener(view -> {
            SortLayoutBottomFragment sortLayout = new SortLayoutBottomFragment();
            sortLayout.show(requireActivity().getSupportFragmentManager(), "ABC");
        });

    }

    private void doAction() {
        String album_size_str = albumFilesList.size() + " Albums";
        song_info.setText(album_size_str);
        if (albumFilesList.size() > 0)
        {
            albumFiles = albumFilesList;
            if (isGridContainer) {
                list_grid_mode.setImageResource(R.drawable.list_24);
                sort.setImageResource(R.drawable.sort_24);
                gridRecycler = new GridAlbumRecycle(requireActivity(), albumFiles, requireActivity().getSupportFragmentManager(), "AlbumFragment");
                gridLayout = new GridLayoutManager(requireActivity(), 2);
                recyclerView.setLayoutManager(gridLayout);
                recyclerView.setAdapter(gridRecycler);
            } else {
                sort.setImageResource(R.drawable.sort_for_list);
                list_grid_mode.setImageResource(R.drawable.grid_view);
                albumAdapter = new AlbumsRecycle(requireActivity(), albumFiles, requireActivity(), requireActivity().getSupportFragmentManager(), "AlbumFragment");
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                        LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(albumAdapter);
            }
        }
    }
}