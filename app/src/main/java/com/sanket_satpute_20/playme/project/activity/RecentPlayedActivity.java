package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.project.service.BackService.recentPlayed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.recycler_views.RecentPlayedRecycle;
import com.sanket_satpute_20.playme.project.service.BackService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RecentPlayedActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton play_fab;
    TextView songs_hours, no_songs, recent_text;
    ConstraintLayout bottom_list_behaviour;
    ImageView back;

    int song_count , song_time;
    BackService service = new BackService();

    public static boolean isRecentActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_played);

        initViews();
        doExtra();
    }

    @Override
    protected void onResume() {
        super.onResume();
        animateViews();
        isRecentActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRecentActive = false;
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        songs_hours = findViewById(R.id.songsandhours);
        no_songs = findViewById(R.id.no_songs_txt);
        play_fab = findViewById(R.id.play_fab);
        bottom_list_behaviour = findViewById(R.id.bottom_list_behaviour);
        recent_text = findViewById(R.id.recent_txt);
        back = findViewById(R.id.back_pressed);
    }

    @SuppressLint("SetTextI18n")
    public void doExtra() {
        if (recentPlayed.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            no_songs.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new RecentPlayedRecycle(this, recentPlayed));
            song_count = recentPlayed.size();
            for(MusicFiles f : recentPlayed) {
                song_time += Integer.parseInt(f.getDuration());
            }
            song_time = getDuration(song_time);
            songs_hours.setText(song_count + " Audio - " + song_time + " minutes");
        } else {
            recyclerView.setVisibility(View.GONE);
            no_songs.setVisibility(View.VISIBLE);
            songs_hours.setText(0 + " Audio - " + 0 + " minutes");
        }

        play_fab.setOnClickListener(view -> {
            if (recentPlayed.size() > 0) {
                service.setSongSource(recentPlayed);

                Intent intent = new Intent(RecentPlayedActivity.this, BackService.class);
                intent.putExtra("position", 0);
                startService(intent);
            }
        });

        back.setOnClickListener(view -> onBackPressed());
    }

    public int getDuration(int time)
    {
        return ((time / (1000*60)) % 60);
    }

    public void animateViews() {
        //default values
        play_fab.setScaleY(0);
        play_fab.setScaleX(0);

        recent_text.setAlpha(0);
        songs_hours.setAlpha(0);

        bottom_list_behaviour.setTranslationY(1300);

        //fab
        ValueAnimator fabScaleX = ObjectAnimator.ofFloat(play_fab, "scaleX", 0, 1);
        ValueAnimator fabScaleY = ObjectAnimator.ofFloat(play_fab, "scaleY", 0, 1);
        AnimatorSet fab = new AnimatorSet();
        fab.playTogether(fabScaleX, fabScaleY);
        fab.setDuration(400);

        //text
        ValueAnimator recentA = ObjectAnimator.ofFloat(recent_text, "alpha", 0, 1);
        ValueAnimator hoursA = ObjectAnimator.ofFloat(songs_hours, "alpha", 0, 1);
        recentA.setDuration(200);
        hoursA.setDuration(200);
        AnimatorSet text = new AnimatorSet();
        text.play(recentA).before(hoursA);
        text.setInterpolator(new LinearInterpolator());

        ValueAnimator bottomListY = ObjectAnimator.ofFloat(bottom_list_behaviour, "translationY", 1300, 0);
        bottomListY.setDuration(400);

        AnimatorSet animator = new AnimatorSet();
        animator.play(fab).before(text).before(bottomListY);
        animator.start();
    }
}