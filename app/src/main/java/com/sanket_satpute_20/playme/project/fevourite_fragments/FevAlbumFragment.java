package com.sanket_satpute_20.playme.project.fevourite_fragments;

import static com.sanket_satpute_20.playme.MainActivity.favouriteAlbums;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.model.Albums;
import com.sanket_satpute_20.playme.project.recycler_views.AlbumsRecycle;

public class FevAlbumFragment extends Fragment {

    TextView not_found;
    RecyclerView recyclerView;
    AlbumsRecycle adapter;
    LinearLayoutManager layout;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if ("action.from_grid_or_non_grid_album_recycler_view.ITEM_REMOVED.FOR_0_Songs".equals(intent.getAction())) {
                    String fromHere = intent.getStringExtra("THIS_ALBUM_RECYCLER_FROM");
                    if (fromHere.equals("FevAlbumFragment")) {
                        int r_position = intent.getIntExtra("REMOVED_ITEM_POSITION", -1);
                        Albums album_came = (Albums) intent.getSerializableExtra("IN_PARENT_LIST_ITEM_PRESENT_FULL_ALBUM");
                        if (favouriteAlbums.size() > r_position && r_position > -1) {
                            if (album_came.getAlbum_name().equals(favouriteAlbums.get(r_position).getAlbum_name())
                                    && album_came.getAlbum_files().size() == favouriteAlbums.get(r_position).getAlbum_files().size()) {
                                favouriteAlbums.remove(r_position);
                                if (adapter != null)
                                    adapter.notifyItemRemoved(r_position);
                            }
                        }
                    }
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fev_album, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("action.from_grid_or_non_grid_album_recycler_view.ITEM_REMOVED.FOR_0_Songs");
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(receiver, filter);
        doExtra();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver);
    }

    private void initViews(View view) {
        not_found = view.findViewById(R.id.no_albums_txt);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    private void doExtra() {
        if (favouriteAlbums.size() > 0) {
            not_found.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new AlbumsRecycle(requireContext(), favouriteAlbums, requireActivity(), requireActivity().getSupportFragmentManager(), "FevAlbumFragment");
            layout = new LinearLayoutManager(requireActivity());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layout);
        } else {
            not_found.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}