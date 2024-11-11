package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.MainActivity.musicFiles;
import static com.sanket_satpute_20.playme.project.activity.InnerPlaylistActivity.FORWHAT;
import static com.sanket_satpute_20.playme.project.fragments.PlayListFragment.array;
import static com.sanket_satpute_20.playme.project.recycler_views.AddToPlaylistRecycle.addedFiles;
import static com.sanket_satpute_20.playme.project.recycler_views.PlaylistRecycle.PLAYLISTPOSITION;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.model.Playlist;
import com.sanket_satpute_20.playme.project.model.PlaylistArray;
import com.sanket_satpute_20.playme.project.recycler_views.AddToPlaylistRecycle;

import java.util.ArrayList;

public class AddToPlaylistActivity extends AppCompatActivity {

    String what;

    RecyclerView recyclerView;
    int position;

    Playlist playlist;

    TextView text_info;

    Button button, remove_perticular, remove_all;
    int size;
    ImageView back_pressed;
    SearchView search_view;
    LinearLayout remove_layout;

    AddToPlaylistRecycle adapter;

    public static boolean isPlayActiAlive = false;


    ArrayList<MusicFiles> listSongs = new ArrayList<>();

    public BroadcastReceiver reciver = new BroadcastReceiver() {
        @SuppressLint("DefaultLocale")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.selected.SONG")) {
                if (intent.getStringExtra("msg").equals("selected")) {
                    size = intent.getIntExtra("size", 0);
                    button.setVisibility(View.VISIBLE);
                    button.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up));
                    button.setText(String.format("Add   %d", size));
                } else {
                    button.setVisibility(View.INVISIBLE);
                    button.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down));
                }
            } else if (intent.getAction().equals("com.remove.SONG")) {
                if (intent.getStringExtra("msg").equals("selected")) {
                    size = intent.getIntExtra("size", 0);
                    remove_layout.setVisibility(View.VISIBLE);
                    remove_layout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up));
                    remove_perticular.setText(String.format("Remove   %d", size));
                } else {
                    remove_layout.setVisibility(View.VISIBLE);
                    remove_layout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_playlist);
        initViews();
        doExtra();
        isPlayActiAlive = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlayActiAlive = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filters = new IntentFilter();
        filters.addAction("com.remove.SONG");
        filters.addAction("com.selected.SONG");
        LocalBroadcastManager.getInstance(this).registerReceiver(reciver, filters);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciver);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.ar_to_playlist_recyclerView);
        button = findViewById(R.id.btn_add);
        back_pressed = findViewById(R.id.back_pressed);
        search_view = findViewById(R.id.search_view);
        remove_perticular = findViewById(R.id.remove_perticular);
        remove_all = findViewById(R.id.remove_all);
        remove_layout = findViewById(R.id.remove_layout);
        text_info = findViewById(R.id.text_info);
    }

    private void doExtra() {

        button.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));
        remove_perticular.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));
        remove_all.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));

        Intent intent = getIntent();
        what = intent.getStringExtra(FORWHAT);
        position = intent.getIntExtra(PLAYLISTPOSITION, -1);

        if (array.getRef().size() > 0) {
            playlist = array.getRef().get(position);
        }

        if (what.equals("add")) {
            addToPlaylist();
        } else {
            removeFromPlaylist();
        }

        back_pressed.setOnClickListener(view -> onBackPressed());

        button.setOnClickListener(view -> {
            addingSongToList();
            @SuppressLint("Recycle") ValueAnimator bY = ObjectAnimator.ofFloat(button, "translationY", 0f, 300f);
            bY.setDuration(300);
            bY.start();
            bY.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    button.setVisibility(View.GONE);
                    finish();
                }
            });
        });

        remove_all.setOnClickListener(view -> {
            playlist.playlist.clear();
            finish();
        });

        remove_perticular.setOnClickListener(view -> {
            playlist.playlist.removeAll(addedFiles);
            finish();
        });

    }

    private void removeFromPlaylist() {
        recyclerView.setHasFixedSize(true);
        if (musicFiles.size() > 0) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new AddToPlaylistRecycle(this, playlist.playlist, false);
            recyclerView.setAdapter(adapter);
        }
        removeSearch();
    }

    private void addToPlaylist() {
        recyclerView.setHasFixedSize(true);
        if (musicFiles.size() > 0) {
            for (MusicFiles file: musicFiles) {
                if (!(playlist.playlist.contains(file))) {
                    listSongs.add(file);
                }
            }
            adapter = new AddToPlaylistRecycle(this, listSongs, true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
        addSearch();
    }

    public void addingSongToList() {
        PlaylistArray.ref.get(position).playlist.addAll(addedFiles);
        addToPlaylist();
    }

    private void removeSearch() {
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String userText = newText.toLowerCase();
                ArrayList<MusicFiles> removeSongList = new ArrayList<>();
                boolean sname;
                for (MusicFiles file:playlist.playlist) {
                    sname = file.getTitle().toLowerCase().contains(userText);
                    if (sname) {
                        removeSongList.add(file);
                    }
                }
                if (removeSongList.size() > 0) {
                    adapter.setList(removeSongList);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    text_info.setVisibility(View.VISIBLE);
                    text_info.setText(R.string.no_result_found);
                }
                return true;
            }
        });
    }

    private void addSearch() {
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String userText = newText.toLowerCase();
                ArrayList<MusicFiles> addSongList = new ArrayList<>();
                boolean sname;
                for (MusicFiles file:listSongs) {
                    sname = file.getTitle().toLowerCase().contains(userText);
                    if (sname) {
                        addSongList.add(file);
                    }
                }
                if (addSongList.size() > 0) {
                    adapter.setList(addSongList);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    text_info.setVisibility(View.VISIBLE);
                    text_info.setText(R.string.no_result_found);
                }
                return true;
            }
        });
    }
}