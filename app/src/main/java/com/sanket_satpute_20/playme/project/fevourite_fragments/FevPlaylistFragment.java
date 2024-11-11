package com.sanket_satpute_20.playme.project.fevourite_fragments;

import static com.sanket_satpute_20.playme.MainActivity.favouritePlaylists;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.recycler_views.PlaylistRecycle;


public class FevPlaylistFragment extends Fragment {

    TextView no_playlist_found;
    RecyclerView recyclerView;
    LinearLayoutManager layout;
    PlaylistRecycle adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fev_playlist, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        doExtra();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        no_playlist_found = view.findViewById(R.id.no_playlist_found);
    }

    private void doExtra() {
        if (favouritePlaylists.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            no_playlist_found.setVisibility(View.GONE);
            layout = new LinearLayoutManager(requireActivity());
            adapter = new PlaylistRecycle(requireActivity(), favouritePlaylists);
            recyclerView.setLayoutManager(layout);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            no_playlist_found.setVisibility(View.VISIBLE);
        }
    }
}