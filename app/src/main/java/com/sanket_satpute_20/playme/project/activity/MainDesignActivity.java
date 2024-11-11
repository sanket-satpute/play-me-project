package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.album_checked;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.artist_checked;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.home_checked;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.playlist_checked;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.selected_index;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.song_checked;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.tab_list;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.tab_list_changed;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.tab_name;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.sanket_satpute_20.playme.R;
import com.google.android.exoplayer2.util.Log;
import com.google.android.material.checkbox.MaterialCheckBox;

public class MainDesignActivity extends AppCompatActivity {

    ImageView back_pressed;
    Spinner spinner;
    MaterialCheckBox home, song, album, artist, playlist;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_design);
        initVies();
        doExtra();
        onClick();
    }

    private void initVies() {
        back_pressed = findViewById(R.id.back_pressed);
        spinner = findViewById(R.id.default_opening_tab_spinner);
        home = findViewById(R.id.home_check);
        song = findViewById(R.id.song_check);
        album = findViewById(R.id.album_check);
        artist = findViewById(R.id.artist_check);
        playlist = findViewById(R.id.playlist_check);
    }

    private void doExtra() {

        home.setChecked(home_checked);
        song.setChecked(song_checked);
        album.setChecked(album_checked);
        artist.setChecked(artist_checked);
        playlist.setChecked(playlist_checked);
        rootExtra();
    }

    private void rootExtra() {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tab_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Log.d("g1", ""+selected_index);
        spinner.setSelection(selected_index);
        spinner.setPrompt("Select Default Tab");
    }

    private void onClick() {
        back_pressed.setOnClickListener(view -> onBackPressed());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tab_name = adapterView.getSelectedItem().toString();
                selected_index = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        home.setOnCheckedChangeListener((compoundButton, b) -> {
            if (tab_list.contains("Home")) {
                tab_list.remove("Home");
            } else {
                tab_list.add("Home");
            }
            home_checked = b;
            tab_list_changed = true;
            if (adapter != null)
                adapter.notifyDataSetChanged();
        });

        song.setOnCheckedChangeListener((compoundButton, b) -> {
            if (tab_list.contains("Song")) {
                tab_list.remove("Song");
            } else {
                tab_list.add("Song");
            }
            song_checked = b;
            tab_list_changed = true;
            if (adapter != null)
                adapter.notifyDataSetChanged();
        });

        album.setOnCheckedChangeListener((compoundButton, b) -> {
            if (tab_list.contains("Album")) {
                tab_list.remove("Album");
            } else {
                tab_list.add("Album");
            }
            album_checked = b;
            tab_list_changed = true;
            if (adapter != null)
                adapter.notifyDataSetChanged();
        });

        artist.setOnCheckedChangeListener((compoundButton, b) -> {
            if (tab_list.contains("Artist")) {
                tab_list.remove("Artist");
            } else {
                tab_list.add("Artist");
            }
            artist_checked = b;
            tab_list_changed = true;
            if (adapter != null)
                adapter.notifyDataSetChanged();
        });

        playlist.setOnCheckedChangeListener((compoundButton, b) -> {
            if (tab_list.contains("Playlist")) {
                tab_list.remove("Playlist");
            } else {
                tab_list.add("Playlist");
            }
            playlist_checked = b;
            tab_list_changed = true;
            if (adapter != null)
                adapter.notifyDataSetChanged();
        });

    }
}