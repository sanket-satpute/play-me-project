package com.sanket_satpute_20.playme.project.fevourite_fragments;

import static com.sanket_satpute_20.playme.MainActivity.favouriteArtists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.recycler_views.ArtistRecycle;

public class FevArtistFragment extends Fragment {

    TextView not_found;
    RecyclerView recyclerView;
    ArtistRecycle adapter;
    LinearLayoutManager layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fev_artist, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        doExtra();
    }

    private void initViews(View view) {
        not_found = view.findViewById(R.id.no_artist_txt);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    private void doExtra() {
        if (favouriteArtists.size() > 0) {
            not_found.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new ArtistRecycle(requireContext(), favouriteArtists);
            layout = new LinearLayoutManager(requireActivity());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layout);
        } else {
            not_found.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}