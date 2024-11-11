package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.stat_position;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;
import static com.sanket_satpute_20.playme.project.service.BackService.most_played_songs;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.recycler_views.MostPlayedActivityRecycle;
import com.sanket_satpute_20.playme.project.service.BackService;

public class MostPlayedActivity extends AppCompatActivity implements ServiceConnection {

    ImageView back_pressed, option_button;
    RecyclerView recyclerView;
    TextView no_most_played_found;

    @SuppressLint("StaticFieldLeak")
    public static MostPlayedActivityRecycle adapter;

    public static BackService most_played_service;

    SharedPreferences preferences;
    public static int most_playing_position;

    private final BroadcastReceiver reciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            doShared();
            Handler handler = new Handler();
            handler.postDelayed(() -> doExtra(), 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_most_played);
        initViews();
        onClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("song.mp3.changed");
        LocalBroadcastManager.getInstance(this).registerReceiver(reciver, filter);
        Intent intent = new Intent(this, BackService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciver);
        unbindService(this);
        stat_position = -1;
    }

    private void initViews() {
        back_pressed = findViewById(R.id.back_pressed);
        option_button = findViewById(R.id.option);
        recyclerView = findViewById(R.id.most_played_recycler_view);
        no_most_played_found = findViewById(R.id.no_most_played_song_txt);
    }

    private void doExtra() {
        if (most_played_songs.size() > 0) {
            doShared();
            no_most_played_found.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new MostPlayedActivityRecycle(this, most_played_songs, getSupportFragmentManager());
            GridLayoutManager layout = new GridLayoutManager(this, 2);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layout);
        } else {
            no_most_played_found.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void onClick() {
        back_pressed.setOnClickListener(view -> onBackPressed());
        option_button.setOnClickListener(view -> popUpMenuClicked());
    }

    private void popUpMenuClicked() {
        PopupMenu menu = new PopupMenu(this, option_button);
        menu.getMenuInflater().inflate(R.menu.most_played_activity_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(menuItem -> {
            if (R.id.play_all == menuItem.getItemId()) {
                BackService service = new BackService();
                service.setSongSource(most_played_songs);

                Intent intent = new Intent(this, BackService.class);
                intent.putExtra("position", 0);
                startService(intent);
            } else if (R.id.clear_all == menuItem.getItemId()) {
                most_played_songs.clear();
                doExtra();
            } else if (R.id.modify_setting == menuItem.getItemId()) {
                Intent intent_modify_setting = new Intent(this, CoreSettingActivity.class);
                startActivity(intent_modify_setting);
            }
            return true;
        });
        menu.show();
    }

    private void doShared() {
        preferences = getSharedPreferences("PLAYING", Context.MODE_PRIVATE);
        most_playing_position = preferences.getInt(SONGPOSITION, 0);
    }

    public static MostPlayedActivityRecycle getAdapter() {
        return adapter;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        BackService.LocalBinder binder = (BackService.LocalBinder) iBinder;
        most_played_service = binder.getService();
        doExtra();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        most_played_service = null;
    }
}