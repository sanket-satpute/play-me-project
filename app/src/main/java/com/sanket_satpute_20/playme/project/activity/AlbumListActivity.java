package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.MainActivity.albumFilesList;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BottomDialogAlbumFragment.ALBUM_COUNT_F;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BottomDialogAlbumFragment.ALBUM_NAME_F;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BottomDialogAlbumFragment.ALBUM_PATH_F;
import static com.sanket_satpute_20.playme.project.recycler_views.AlbumsRecycle.ALBUMCLASS;
import static com.sanket_satpute_20.playme.project.recycler_views.AlbumsRecycle.ALBUMNAME;
import static com.sanket_satpute_20.playme.project.recycler_views.AlbumsRecycle.POS;
import static com.sanket_satpute_20.playme.project.service.BackService.shuffleBoolean;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BottomDialogAlbumFragment;
import com.sanket_satpute_20.playme.project.model.Albums;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.recycler_views.InnerAlbumRecycle;
import com.sanket_satpute_20.playme.project.service.BackService;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AlbumListActivity extends AppCompatActivity {

    public static final int ALBUM_FINISHING_REQUEST_CODE = 1900;

    InnerAlbumRecycle adapter;
    RecyclerView recyclerView;
    MaterialButton shuffle_btn;
    Button play_btn;
    TextView albumName, song_count_hours,song_txt, no_song_found_txt;
    ImageView album_src, background_image, back_pressed, option;

    int song_hours;

    public String album;
    Albums albums;

    int album_position;

    BackService service = new BackService();

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals("songs_is_removed_OR_Full_Album_is_removed.DELETED.Action")) {
                    String album_name = intent.getStringExtra("REMOVED_ALBUM_NAME");
                    int removed_index = intent.getIntExtra("REMOVED_ALBUM_POSITION", -1);
                    if (album_name.trim().equals(albums.getAlbum_name().trim())) {
                        boolean is_all_removed = intent.getBooleanExtra("REMOVED_ALL", false);
                        if (is_all_removed) {
                            Intent finishing_intent = new Intent();
                            finishing_intent.putExtra("RETURNING_VALUE", removed_index);
                            finishing_intent.putExtra("REMOVED_ALL", true);
                            AlbumListActivity.this.setResult(RESULT_OK, finishing_intent);
                            finish();
                        } else {
                            setRecyclerAndDoExtra();
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);
        setStatusBarTransparent();
        initViews();
        doExtra();
        onClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("songs_is_removed_OR_Full_Album_is_removed.DELETED.Action");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.album_inner_recycle);
        shuffle_btn = findViewById(R.id.shuffle_btn);
        play_btn = findViewById(R.id.play_btn);
        album_src = findViewById(R.id.album_src);
        albumName = findViewById(R.id.album_name);
        song_count_hours = findViewById(R.id.song_count);
        background_image = findViewById(R.id.background_image);
        back_pressed = findViewById(R.id.back_pressed);
        option = findViewById(R.id.option);
        song_txt = findViewById(R.id.songs_txt);
        no_song_found_txt = findViewById(R.id.no_song_found_txt);
    }

    private void doExtra() {

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("B");
        album = intent.getStringExtra(ALBUMNAME);
        album_position = intent.getIntExtra(POS, 0);
        albums = (Albums) bundle.getSerializable(ALBUMCLASS);

        albumName.setText(album);
        setRecyclerAndDoExtra();

    }

    private void setRecyclerAndDoExtra() {
        if (albums.getAlbum_files().size() > 0)
        {
            no_song_found_txt.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            MyImageLoad load = new MyImageLoad(albums.getAlbum_files().get(0));
            load.execute();

            adapter = new InnerAlbumRecycle(this,
                    albums.getAlbum_files());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(adapter);
        } else {
            no_song_found_txt.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        String count_str = "fetching album data..";
        song_count_hours.setText(count_str);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String count_str_2;
            if (albums.getAlbum_files().size() > 0) {
                for (MusicFiles f: albums.getAlbum_files()) {
                    song_hours += Integer.parseInt(f.getDuration());
                }
                count_str_2 = albums.getAlbum_files().size() + " Audio - " + getDuration(song_hours) + " minutes ";
            } else {
                count_str_2 = "No songs found";
            }
            this.runOnUiThread(() -> song_count_hours.setText(count_str_2));
        });
    }

    public void onClick() {
        back_pressed.setOnClickListener(view -> onBackPressed());

        option.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString(ALBUM_NAME_F, albums.getAlbum_name());
            bundle.putInt(ALBUM_COUNT_F, albums.getAlbum_files().size());
            bundle.putString(ALBUM_PATH_F, albums.getAlbum_files().get(0).getPath());
            bundle.putSerializable(ALBUMCLASS, albums);
            BottomDialogAlbumFragment bottomDialogAlbumFragment = new BottomDialogAlbumFragment();
            bottomDialogAlbumFragment.setArguments(bundle);
            bottomDialogAlbumFragment.show(getSupportFragmentManager(), "ABC");
        });

        shuffle_btn.setOnClickListener(view -> {
            shuffleBoolean = true;
            service.setSongSource(albumFilesList.get(album_position).getAlbum_files());
            Intent intent = new Intent(AlbumListActivity.this, BackService.class);
            intent.putExtra("position", getRandom(albumFilesList.get(album_position).getAlbum_files().size()));
            startService(intent);
        });

        play_btn.setOnClickListener(view -> {
            service.setSongSource(albumFilesList.get(album_position).getAlbum_files());
            Intent intent = new Intent(AlbumListActivity.this, BackService.class);
            intent.putExtra("position", 0);
            startService(intent);
        });
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i);
    }

    private class MyImageLoad extends AsyncTask<Void, Void, byte[]>
    {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        MediaMetadataRetriever retriever;

        MyImageLoad(MusicFiles fileIO) {
            this.fileIO = fileIO;
        }

        @Override
        protected byte[] doInBackground(Void... voids) {
            try {
                path = fileIO.getPath();
                retriever = new MediaMetadataRetriever();
                retriever.setDataSource(path);
                album_pic = retriever.getEmbeddedPicture();
                retriever.release();
                return album_pic;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            try {
                Glide.with(AlbumListActivity.this)
                        .load(bytes)
                        .override(150, 150)
                        .placeholder(R.mipmap.ic_music)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(album_src);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    Glide.with(AlbumListActivity.this)
                            .load(bytes)
                            .override(300, 300)
                            .placeholder(R.mipmap.ic_music)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(background_image);
                } catch(IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getDuration(int time)
    {
        return ((time / (1000*60)) % 60);
    }

    private void a_animate() {

        shuffle_btn.setTranslationX(-500);
        play_btn.setTranslationX(500);
        albumName.setTranslationX(-500);
        song_count_hours.setTranslationX(-500);
        song_txt.setScaleX(0f);
        song_txt.setScaleY(0f);

        ObjectAnimator shuffle_trans_x = ObjectAnimator.ofFloat(shuffle_btn, "translationX", -500, .0f);
        shuffle_trans_x.setDuration(400);
        ObjectAnimator play_trans_x =  ObjectAnimator.ofFloat(play_btn, "translationX", 500, .0f);
        play_trans_x.setStartDelay(250);
        play_trans_x.setDuration(400);

        ObjectAnimator name_trans_x =  ObjectAnimator.ofFloat(albumName, "translationX", -500, .0f);
        ObjectAnimator count_trans_x =  ObjectAnimator.ofFloat(song_count_hours, "translationX", -500, .0f);

        ObjectAnimator song_txt_trans_x_i =  ObjectAnimator.ofFloat(song_txt, "scaleX", 0.1f, 1.4f);
        ObjectAnimator song_txt_trans_y_i =  ObjectAnimator.ofFloat(song_txt, "scaleY", 0.1f, 1.4f);
        ObjectAnimator song_txt_trans_x =  ObjectAnimator.ofFloat(song_txt, "scaleX", 1.4f, 1f);
        ObjectAnimator song_txt_trans_y =  ObjectAnimator.ofFloat(song_txt, "scaleY", 1.4f, 1f);

        AnimatorSet buttons = new AnimatorSet();
        buttons.playTogether(shuffle_trans_x, play_trans_x);

        AnimatorSet txt_set = new AnimatorSet();
        txt_set.play(name_trans_x).before(count_trans_x);
        txt_set.setDuration(150);

        AnimatorSet increment = new AnimatorSet();
        increment.setDuration(100);
        increment.playTogether(song_txt_trans_x_i, song_txt_trans_y_i);

        AnimatorSet decrement = new AnimatorSet();
        decrement.setDuration(70);
        decrement.playTogether(song_txt_trans_x, song_txt_trans_y);

        AnimatorSet song_txt = new AnimatorSet();
        song_txt.setDuration(200);
        song_txt.play(increment).before(decrement);

        AnimatorSet set = new AnimatorSet();
        set.play(buttons).before(txt_set);

        AnimatorSet set_2 = new AnimatorSet();
        set_2.play(set).before(song_txt);
        set_2.start();
    }

    private void setStatusBarTransparent () {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(params);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}