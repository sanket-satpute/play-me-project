package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.project.service.BackService.recent_artist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.recycler_views.GridArtistRecycle;

public class RecentArtistActivity extends AppCompatActivity {

    TextView no_artist;
    RecyclerView recycler_view;
    ImageView back_pressed, option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_artist);
        initViews();
        doExtra();
        onClick();
    }

    private void initViews() {
        back_pressed = findViewById(R.id.back_pressed);
        option = findViewById(R.id.option);
        recycler_view = findViewById(R.id.recent_played_artist_recycle);
        no_artist = findViewById(R.id.no_artist_txt);
    }

    private void doExtra() {
        if (recent_artist.size() > 0) {
            no_artist.setVisibility(View.GONE);
            recycler_view.setVisibility(View.VISIBLE);
            GridLayoutManager layoutManager = new GridLayoutManager(RecentArtistActivity.this, 2);
            GridArtistRecycle adapter = new GridArtistRecycle(this, recent_artist);
            recycler_view.setLayoutManager(layoutManager);
            recycler_view.setAdapter(adapter);
        } else {
            no_artist.setVisibility(View.VISIBLE);
            recycler_view.setVisibility(View.GONE);
        }
    }

    private void onClick() {
        back_pressed.setOnClickListener(view -> onBackPressed());
        option.setOnClickListener(view -> popUpClicked());
    }

    private void popUpClicked() {
        PopupMenu menu = new PopupMenu(this, option);
        menu.getMenuInflater().inflate(R.menu.recent_artist_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(menuItem -> {
            if (R.id.clear_artist == menuItem.getItemId()) {
                recent_artist.clear();
                doExtra();
            } else if (R.id.go_recent_song == menuItem.getItemId()) {
                Intent intent = new Intent(this, RecentPlayedActivity.class);
                startActivity(intent);
            } else if (R.id.go_recent_album == menuItem.getItemId()) {
                Intent intent2 = new Intent(this, RecentAlbumActivity.class);
                startActivity(intent2);
            }
            return true;
        });
        menu.show();
    }
}