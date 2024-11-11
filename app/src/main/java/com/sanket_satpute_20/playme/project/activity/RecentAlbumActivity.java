package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.project.service.BackService.recent_album;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.model.Albums;
import com.sanket_satpute_20.playme.project.recycler_views.GridAlbumRecycle;

public class RecentAlbumActivity extends AppCompatActivity {

    ImageView back_pressed, option;
    RecyclerView recycler_view;
    TextView no_album_txt;

    GridAlbumRecycle adapter;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals("action.from_grid_or_non_grid_album_recycler_view.ITEM_REMOVED.FOR_0_Songs")) {
                    String fromHere = intent.getStringExtra("THIS_ALBUM_RECYCLER_FROM");
                    if (fromHere.equals("RecentAlbumActivity")) {
                        int removable_position = intent.getIntExtra("REMOVED_ITEM_POSITION", -1);
                        Albums album_came = (Albums) intent.getSerializableExtra("IN_PARENT_LIST_ITEM_PRESENT_FULL_ALBUM");
                        if (recent_album.size() > removable_position && removable_position > -1) {
                            if (album_came.getAlbum_name().equals(recent_album.get(removable_position).getAlbum_name())
                                    && album_came.getAlbum_files().size() == recent_album.get(removable_position).getAlbum_files().size()) {
                                recent_album.remove(removable_position);
                                adapter.notifyItemRemoved(removable_position);
                                adapter.notifyItemRangeRemoved(removable_position, recent_album.size());
                            }
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_album);
        initViews();
        doExtra();
        onClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("action.from_grid_or_non_grid_album_recycler_view.ITEM_REMOVED.FOR_0_Songs");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void initViews() {
        back_pressed = findViewById(R.id.back_pressed);
        option = findViewById(R.id.option);
        recycler_view = findViewById(R.id.recent_played_album);
        no_album_txt = findViewById(R.id.no_albums_txt);
    }

    private void doExtra() {
        if (recent_album.size() > 0) {
            no_album_txt.setVisibility(View.GONE);
            recycler_view.setVisibility(View.VISIBLE);
            GridLayoutManager manager = new GridLayoutManager(RecentAlbumActivity.this, 2);
            adapter = new GridAlbumRecycle(this, recent_album, getSupportFragmentManager(), "RecentAlbumActivity");
            recycler_view.setLayoutManager(manager);
            recycler_view.setAdapter(adapter);
        } else {
            no_album_txt.setVisibility(View.VISIBLE);
            recycler_view.setVisibility(View.GONE);
        }
    }

    private void onClick() {
        back_pressed.setOnClickListener(view -> onBackPressed());
        option.setOnClickListener(view -> popUpClicked());
        registerForContextMenu(option);
    }

    private void popUpClicked() {
        PopupMenu menu = new PopupMenu(this, option);
        menu.getMenuInflater().inflate(R.menu.recent_album_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(menuItem -> {
            if (R.id.clear_album == menuItem.getItemId()) {
                recent_album.clear();
                doExtra();
            } else if (R.id.go_recent_song == menuItem.getItemId()) {
                Intent intent = new Intent(this, RecentPlayedActivity.class);
                startActivity(intent);
            } else if (R.id.go_recent_artist == menuItem.getItemId()) {
                Intent intent2 = new Intent(this, RecentArtistActivity.class);
                startActivity(intent2);
            }
            return true;
        });
        menu.show();
    }
}