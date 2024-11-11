package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.project.fragments.PlayListFragment.array;
import static com.sanket_satpute_20.playme.project.recycler_views.PlaylistRecycle.PLAYLISTNAME;
import static com.sanket_satpute_20.playme.project.recycler_views.PlaylistRecycle.PLAYLISTPOSITION;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.model.Playlist;
import com.sanket_satpute_20.playme.project.model.PlaylistArray;
import com.sanket_satpute_20.playme.project.recycler_views.InnerPlaylistRecycler;
import com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle;
import com.sanket_satpute_20.playme.project.service.BackService;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class InnerPlaylistActivity extends AppCompatActivity {

    public static final String FORWHAT = "FORWHAT";
    RecyclerView recyclerView;
    ImageView back_pressed, playlist_art;
    TextView playlist_nme;
    FloatingActionButton play_all, add_to_playlist, remove_from_playlist;
    int position = -1;
    PlaylistArray arr;
    Playlist playlist;
    InnerPlaylistRecycler adapter;
    String playlist_name;

    NestedScrollView nested_scroll_view;

    ExtendedFloatingActionButton edit_playlist;

    private boolean isEditExpanded = false;
    public static boolean isActivityAlive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_playlist);
        initViews();
        doExtra();
        onClickEvent();
        isActivityAlive = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityAlive = false;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();

        if (playlist.playlist.size() > 0) {
            try {
                ImageLoader loader = new ImageLoader();
                loader.execute(playlist.playlist.get(0));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        setAnimators();
        isEditExpanded = false;
        add_to_playlist.setScaleX(0);
        add_to_playlist.setScaleY(0);
        remove_from_playlist.setScaleX(0);
        remove_from_playlist.setScaleY(0);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.inner_playlist_recycler);
        back_pressed = findViewById(R.id.back_pressed);
        playlist_nme = findViewById(R.id.playlist_name);
        play_all = findViewById(R.id.play_floating);
        playlist_art = findViewById(R.id.art_of_playlist);
        add_to_playlist = findViewById(R.id.add_to_playlist);
        remove_from_playlist = findViewById(R.id.remove_from_playlist);
        edit_playlist = findViewById(R.id.edit_playlist);
        nested_scroll_view = findViewById(R.id.nested_scroll_view);

        Intent i = getIntent();
        position = i.getIntExtra(PLAYLISTPOSITION, -1);
        playlist_name = i.getStringExtra(PLAYLISTNAME);
    }

    public void onClickEvent() {

        if (playlist_name != null) {
            playlist_nme.setText(playlist_name);
        }

        back_pressed.setOnClickListener(view -> onBackPressed());

        add_to_playlist.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddToPlaylistActivity.class);
            intent.putExtra(FORWHAT, "add");
            intent.putExtra(PLAYLISTPOSITION, position);
            startActivity(intent);
        });

        remove_from_playlist.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddToPlaylistActivity.class);
            intent.putExtra(FORWHAT, "remove");
            intent.putExtra(PLAYLISTPOSITION, position);
            startActivity(intent);
        });

        edit_playlist.setOnClickListener(view -> {
            if (isEditExpanded) {
                collapseEdit();
            } else {
                expandEdit();
            }
            isEditExpanded = !isEditExpanded;
        });

        play_all.setOnClickListener(view -> {
            if (playlist.playlist.size() > 0) {
                BackService service = new BackService();

                service.setSongSource(playlist.playlist);

                Intent intent = new Intent(this, BackService.class);
                intent.putExtra("position", 0);
                startService(intent);
            } else {
                Snackbar snackbar = Snackbar.make(play_all, "Playlist is Empty", Snackbar.LENGTH_SHORT)
                        .setAction("Add", view1 -> {
                            Intent intent = new Intent(this, AddToPlaylistActivity.class);
                            intent.putExtra(FORWHAT, "add");
                            intent.putExtra(PLAYLISTPOSITION, position);
                            startActivity(intent);
                        });
                snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        expandBtnDown();
                    }

                    @Override
                    public void onShown(Snackbar transientBottomBar) {
                        super.onShown(transientBottomBar);
                        expandBtnUp();
                    }
                });
                snackbar.show();

            }
        });
    }

    public void doExtra() {
        arr = array;
        playlist = PlaylistArray.ref.get(position);
        recyclerView.setHasFixedSize(true);
        if (SongsRecycle.files != null) {
            if (SongsRecycle.files.size() > 0) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter = new InnerPlaylistRecycler(this, playlist.playlist);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private void setAnimators() {
        play_all.setScaleX(0);
        play_all.setScaleY(0);

        edit_playlist.setScaleX(0);
        edit_playlist.setScaleY(0);

        ValueAnimator fab1 = ObjectAnimator.ofFloat(play_all, "scaleX", 0f , 1f);
        fab1.setDuration(400);
        ValueAnimator fab2 = ObjectAnimator.ofFloat(play_all, "scaleY" , 0f, 1f);
        fab2.setDuration(400);

        ValueAnimator extend1 = ObjectAnimator.ofFloat(edit_playlist, "scaleX", 0f , 1f);
        extend1.setDuration(300);
        ValueAnimator extend2 = ObjectAnimator.ofFloat(edit_playlist, "scaleY" , 0f, 1f);
        extend2.setDuration(400);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(fab1, fab2);

        AnimatorSet set1 = new AnimatorSet();
        set1.playTogether(extend1, extend2);

        AnimatorSet set2 = new AnimatorSet();
        set2.play(set).before(set1);
        set2.start();
    }

    public void collapseEdit() {

        ValueAnimator asX = ObjectAnimator.ofFloat(add_to_playlist, "scaleX", 1f, 0);
        ValueAnimator asY = ObjectAnimator.ofFloat(add_to_playlist, "scaleY", 1f, 0);

        ValueAnimator atX = ObjectAnimator.ofFloat(add_to_playlist, "translationX", 0, 300f);

        ValueAnimator rsX = ObjectAnimator.ofFloat(remove_from_playlist, "scaleX", 1f, 0);
        ValueAnimator rsY = ObjectAnimator.ofFloat(remove_from_playlist, "scaleY", 1f, 0);

        ValueAnimator rtX = ObjectAnimator.ofFloat(remove_from_playlist, "translationX", 0, 200f);

        AnimatorSet obA = new AnimatorSet();
        obA.playTogether(asX, asY, atX);
        obA.setDuration(200);

        AnimatorSet obR = new AnimatorSet();
        obR.playTogether(rsX, rsY, rtX);
        obR.setDuration(200);

        AnimatorSet animator = new AnimatorSet();
        animator.play(obR).before(obA);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                add_to_playlist.setVisibility(View.GONE);
                remove_from_playlist.setVisibility(View.GONE);
            }
        });
    }

    private void expandEdit() {
        add_to_playlist.setVisibility(View.VISIBLE);
        remove_from_playlist.setVisibility(View.VISIBLE);
        ValueAnimator asX = ObjectAnimator.ofFloat(add_to_playlist, "scaleX", 0, 1f);
        ValueAnimator asY = ObjectAnimator.ofFloat(add_to_playlist, "scaleY", 0, 1f);

        ValueAnimator atX = ObjectAnimator.ofFloat(add_to_playlist, "translationX", 300f, 0);

        ValueAnimator rsX = ObjectAnimator.ofFloat(remove_from_playlist, "scaleX", 0, 1f);
        ValueAnimator rsY = ObjectAnimator.ofFloat(remove_from_playlist, "scaleY", 0, 1f);

        ValueAnimator rtX = ObjectAnimator.ofFloat(remove_from_playlist, "translationX", 200f, 0);

        AnimatorSet obA = new AnimatorSet();
        obA.playTogether(asX, asY, atX);
        obA.setDuration(200);

        AnimatorSet obR = new AnimatorSet();
        obR.playTogether(rsX, rsY, rtX);
        obR.setDuration(400);

        AnimatorSet animator = new AnimatorSet();
        animator.play(obR).after(obA);
        animator.start();
    }

    public void addToPlaylistCalled() {
        Intent intent = new Intent(this, AddToPlaylistActivity.class);
        intent.putExtra(FORWHAT, "add");
        intent.putExtra(PLAYLISTPOSITION, position);
        startActivity(intent);
    }

    private void expandBtnUp() {
        ObjectAnimator transY = ObjectAnimator.ofFloat(edit_playlist, "translationY", 0, -130f);
        transY.setInterpolator(new AccelerateDecelerateInterpolator());
        transY.setDuration(100);
        transY.start();
    }

    private void expandBtnDown() {
        ObjectAnimator transY = ObjectAnimator.ofFloat(edit_playlist, "translationY", -130f, 0f);
        transY.setInterpolator(new AccelerateDecelerateInterpolator());
        transY.setDuration(100);
        transY.start();
    }

    private final class ImageLoader extends AsyncTask<MusicFiles, Void, byte[]> {

        MusicFiles fileIO;
        String path;
        byte[] image;
        MediaMetadataRetriever retriever;

        @Override
        protected byte[] doInBackground(MusicFiles... musicFiles) {
            try {
                fileIO = musicFiles[0];
                path = fileIO.getPath();
                retriever = new MediaMetadataRetriever();
                retriever.setDataSource(path);
                image = retriever.getEmbeddedPicture();
                retriever.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return image;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            try {
                Glide.with(InnerPlaylistActivity.this).asBitmap()
                        .load(image)
                        .placeholder(R.mipmap.ic_music)
                        .into(playlist_art);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}