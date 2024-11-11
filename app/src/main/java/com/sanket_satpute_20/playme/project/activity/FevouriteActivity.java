package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.MainActivity.favouriteAlbums;
import static com.sanket_satpute_20.playme.MainActivity.favouriteArtists;
import static com.sanket_satpute_20.playme.MainActivity.favouriteList;
import static com.sanket_satpute_20.playme.MainActivity.favouritePlaylists;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.view_pager_adapter.FevViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FevouriteActivity extends AppCompatActivity {

//    RecyclerView recyclerView;
    TextView which_is_on, which_and_how, txt_my_fev;
    ImageView back_pressed, option, card_img, background_img;
    ViewPager pager;
    TabLayout tab_layout;
    FevViewPagerAdapter pager_adapter;

    HashMap<String, byte[]> back_picture = new HashMap<>();
    ArrayList<String> page = new ArrayList<>();
    int point, position, default_color;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fevourite);
        setTransparentStatusBar();
        initViews();
        doExtra();
        setTabbing();
        onclick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        animationResume();
    }

    private void initViews() {
        which_is_on = findViewById(R.id.txt_which);
        which_and_how = findViewById(R.id.which_how);
        back_pressed = findViewById(R.id.back_pressed);
        option = findViewById(R.id.option);
        card_img = findViewById(R.id.fevourite_src);
        pager = findViewById(R.id.fev_view_pager);
        tab_layout = findViewById(R.id.tab_layoutFev);
        txt_my_fev = findViewById(R.id.txt_my_fev);
        background_img = findViewById(R.id.fevourite_background);
    }

    private void doExtra() {
        if (page != null) {
            page.clear();
            page.add("Song");
            page.add("Artist");
            page.add("Album");
            page.add("Playlist");
        }
        if (favouriteList.size() > 0) {
            MyImageLoad load = new MyImageLoad();
            load.execute(favouriteList.get(0));
        }
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                default_color = 0xddffffff;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                default_color = 0xff222222;
                break;
        }
        tab_layout.setSelectedTabIndicatorColor(ACCENT_COLOR);
        tab_layout.setTabTextColors(default_color, ACCENT_COLOR);
    }

    private void onclick() {
        back_pressed.setOnClickListener(view -> onBackPressed());

        option.setOnClickListener(view -> optionBtnClicked());

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                deAnimate(position);
                animation();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setTabbing() {
        pager_adapter = new FevViewPagerAdapter(this, getSupportFragmentManager(), tab_layout.getTabCount());
        pager.setAdapter(pager_adapter);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_layout));

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition(), true);
                position = tab.getPosition();
                point = position;
                if (back_picture.containsKey(Objects.requireNonNull(
                        Objects.requireNonNull(tab_layout.getTabAt(position))
                                .getText()).toString())) {
                    Glide.with(FevouriteActivity.this)
                            .load(back_picture.get(page.get(position)))
                            .placeholder(R.mipmap.ic_song)
                            .override(200, 300)
                            .into(card_img);
                } else {
                    MyImageLoad load = new MyImageLoad();
                    try {
                        switch (page.get(position)) {
                            case "Song":
                                load.execute(favouriteList.get(0));
                                break;
                            case "Artist":
                                load.execute(favouriteArtists.get(0).getArtistFiles().get(0));
                                break;
                            case "Album":
                                load.execute(favouriteAlbums.get(0).getAlbum_files().get(0));
                                break;
                            case "Playlist":
                                load.execute(favouritePlaylists.get(0).getPlaylist().get(0));
                                break;
                        }
                    } catch (NullPointerException | IndexOutOfBoundsException
                            | IllegalStateException | IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @SuppressLint("NonConstantResourceId")
    private void optionBtnClicked() {
        PopupMenu pop_up_menu = new PopupMenu(FevouriteActivity.this, option);
        pop_up_menu.getMenuInflater().inflate(R.menu.fevourite_pop_up_menu, pop_up_menu.getMenu());
        pop_up_menu.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (id == R.id.clear_all) {
                favouriteList.clear();
                favouriteAlbums.clear();
                favouriteArtists.clear();
                favouritePlaylists.clear();
            } else if (id == R.id.clear_song) {
                favouriteList.clear();
            } else if (id == R.id.clear_album) {
                favouriteAlbums.clear();
            } else if (id == R.id.clear_artist) {
                favouriteArtists.clear();
            } else if (id == R.id.clear_playlist) {
                favouritePlaylists.clear();
            }

            return true;
        });
        pop_up_menu.show();
    }

    private void animationResume() {
        ObjectAnimator my_fev_txt_transX = ObjectAnimator.ofFloat(txt_my_fev, "translationX", -300f, 0f);
        my_fev_txt_transX.setDuration(400);

        my_fev_txt_transX.start();

        which_and_how.setAlpha(0);
        which_is_on.setTranslationX(-400);
        animation();
    }

    private void animation() {
        ObjectAnimator item_count = ObjectAnimator.ofFloat(which_and_how, "alpha", 0.1f, 1f);
        item_count.setDuration(20);
        ObjectAnimator which_transX = ObjectAnimator.ofFloat(which_is_on, "translationX", -400f, 0f);
        item_count.setDuration(40);

        AnimatorSet set = new AnimatorSet();
        set.play(item_count).after(which_transX);
        set.start();
    }

    @SuppressLint("SetTextI18n")
    private void deAnimate(int position) {
        ObjectAnimator item_count = ObjectAnimator.ofFloat(which_and_how, "alpha", 1f, 0.0f);
        item_count.setDuration(20);
        ObjectAnimator which_transX = ObjectAnimator.ofFloat(which_is_on, "translationX", 0f, -400f);
        item_count.setDuration(40);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(item_count, which_transX);
        set.start();

        switch (position) {
            default:
                which_is_on.setText(R.string.song_s);
                which_and_how.setText(favouriteList.size() + " Song's");
                break;
            case 1:
                which_is_on.setText(R.string.artist_s);
                which_and_how.setText(favouriteArtists.size() + " Artist's");
                break;
            case 2:
                which_is_on.setText(R.string.album_s);
                which_and_how.setText(favouriteAlbums.size() + " Album's");
                break;
            case 3:
                which_is_on.setText(R.string.playlist);
                which_and_how.setText(favouritePlaylists.size() + " Playlist's");
                break;
        }
    }



    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        MediaMetadataRetriever retriver;

        @Override
        protected byte[] doInBackground(MusicFiles... musicFiles) {
            try {
                fileIO = musicFiles[0];
                path = fileIO.getPath();
                retriver = new MediaMetadataRetriever();
                retriver.setDataSource(path);
                album_pic = retriver.getEmbeddedPicture();
                retriver.release();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return album_pic;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            back_picture.put(page.get(point), bytes);
            try {
                Glide.with(FevouriteActivity.this)
                        .load(bytes)
                        .override(200, 300)
                        .placeholder(R.mipmap.ic_song)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(card_img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setTransparentStatusBar () {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(params);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }
}