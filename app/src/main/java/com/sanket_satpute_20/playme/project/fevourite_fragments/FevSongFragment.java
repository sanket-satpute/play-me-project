package com.sanket_satpute_20.playme.project.fevourite_fragments;

import static com.sanket_satpute_20.playme.MainActivity.favouriteList;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.recycler_views.FevSongRecycler;

public class FevSongFragment extends Fragment {

    TextView not_found_txt;
    RecyclerView recyclerView;

    FevSongRecycler recycler_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fev_song, container, false);
        initViews(view);
        doAction();
        return view;
    }

    private void initViews(View view) {
        not_found_txt = view.findViewById(R.id.not_found);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    private void doAction() {
        if (favouriteList.size() > 0) {
            not_found_txt.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recycler_adapter = new FevSongRecycler(requireContext(), favouriteList, requireActivity().getSupportFragmentManager());
            recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
            recyclerView.setAdapter(recycler_adapter);
            recyclerView.setHasFixedSize(true);
        } else {
            not_found_txt.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}