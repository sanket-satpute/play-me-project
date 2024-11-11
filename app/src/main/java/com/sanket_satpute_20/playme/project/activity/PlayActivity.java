package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.MainActivity.RECENT_SEEK_POSITION_SONG;
import static com.sanket_satpute_20.playme.project.enums.PlayActWhich.ROUND;
import static com.sanket_satpute_20.playme.project.enums.ROUND_ACT_BACKGROUNDS.BLUR;
import static com.sanket_satpute_20.playme.project.enums.ROUND_ACT_BACKGROUNDS.COLORED;
import static com.sanket_satpute_20.playme.project.enums.ROUND_ACT_BACKGROUNDS.DEFAULT;
import static com.sanket_satpute_20.playme.project.enums.ROUND_ACT_BACKGROUNDS.GRADIENT;
import static com.sanket_satpute_20.playme.project.extra_stuffes.BlurBuilder.BLUR_RADIUS;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.SONG_SEEK_CURRENT_POSITION;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.IMAGEOFSONG;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.PASSEDSONGFILE;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;
import static com.sanket_satpute_20.playme.project.service.BackService.SONG_DURATION_PREF;
import static com.sanket_satpute_20.playme.project.service.BackService.min;
import static com.sanket_satpute_20.playme.project.service.BackService.song;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.palette.graphics.Palette;
import androidx.viewpager.widget.ViewPager;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BSheetSongsMoreFragment;
import com.sanket_satpute_20.playme.project.enums.PlayActWhich;
import com.sanket_satpute_20.playme.project.enums.ROUND_ACT_BACKGROUNDS;
import com.sanket_satpute_20.playme.project.extra_stuffes.BlurBuilder;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.service.BackService;
import com.sanket_satpute_20.playme.project.view_pager_adapter.PlayViewPagerAdapter;
import com.sanket_satpute_20.playme.project.view_pager_adapter.PlayViewPagerAdapter2;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

import jp.wasabeef.blurry.Blurry;


public class PlayActivity extends AppCompatActivity implements ServiceConnection {

    // BackService
    BackService service;

    TextView music_txt;

    ImageView backPressed, songList;
    TextView remainTime, duration;

    TabLayout tabLayout, tabLayout2;
    ViewPager viewPager ,viewPager2;

    //Second Frame
    ImageView back_btn, option_menu;

    ImageView dot_1, dot_2;
    ImageView r_a_background_img, s_a_background_img;

    byte[] passing_byte;

    int white_color = 0xffffffff;
    public static int default_color = 0xffc1c1c1;
    int base_color = default_color;

    public static boolean isRoundBackgroundOn;

    public static boolean isPlayRunning, isActivityChanged = false;
    public static PlayActWhich which_play_activity = ROUND;
    public static ROUND_ACT_BACKGROUNDS which_act_background_round = DEFAULT;
    int position, pos;

    long recent_duration = 0;

    //BroadCastReceiver
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("song.mp3.changed")) {
                if (which_play_activity == ROUND)
                    getSharedPref();
                if (isRoundBackgroundOn)
                    loadImageShared();
            }
        }
    };

    // server method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLayout();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_animation,R.anim.play_act_down);
    }

    public void createLayout() {
        if (which_play_activity == ROUND) {
            setContentView(R.layout.activity_play);
            initViews();
            setRecentTimeSeeker();
        } else {
            setContentView(R.layout.activity_play_2);
            initViews2();
            setClicker2();
        }
        isPlayRunning = true;
        isActivityChanged = false;
    }

    private void setRecentTimeSeeker() {
        ArrayList<Long> seeks = getRecentSeekPosition();
        SONG_SEEK_CURRENT_POSITION = seeks.get(0);
        recent_duration = seeks.get(1);
        if (recent_duration > SONG_SEEK_CURRENT_POSITION) {
            String remainStr = getHrs(SONG_SEEK_CURRENT_POSITION) + getMin(SONG_SEEK_CURRENT_POSITION) + getSec(SONG_SEEK_CURRENT_POSITION);
            String durationStr = getHrs(recent_duration) + getMin(recent_duration) + getSec(recent_duration);
            duration.setText(durationStr);
            remainTime.setText(remainStr);
        }
    }

    private ArrayList<Long> getRecentSeekPosition() {
        ArrayList<Long> list = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences("PLAYING", MODE_PRIVATE);
        SharedPreferences preferences2 = getSharedPreferences("STORING_DATA", MODE_PRIVATE);
        list.add(0, preferences2.getLong(RECENT_SEEK_POSITION_SONG, 0L));
        list.add(1, preferences.getLong(SONG_DURATION_PREF, 0L));
        return list;
    }

    private String getSec(long millis) {
        long seconds = (millis / 1000) % 60;
        if (seconds < 10)
            return "0" + seconds;
        return String.valueOf(seconds);
    }

    private String getMin(long millis) {
        long mins = (millis / 1000) / 60;
        if (min < 10 && min > 0)
            return "0" + min + " : ";
        return mins + " : ";
    }

    private String getHrs(long millis) {
        long hours   = ((millis / (1000*60*60)) % 24);
        if (hours > 0) {
            if (hours < 10)
                return "0" + hours + " : ";
            return hours + " : ";
        }
        return "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlayRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                base_color = 0xddffffff;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                base_color = 0xdd222222;
                break;
        }
        if (isActivityChanged)
            createLayout();
        if (which_play_activity == ROUND) {
            doAction();
            Intent intent = new Intent(PlayActivity.this, BackService.class);
            bindService(intent, PlayActivity.this, BIND_AUTO_CREATE);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("song.changed.Clicked.playActivity");
        filter.addAction("song.mp3.changed");
        LocalBroadcastManager.getInstance(PlayActivity.this).registerReceiver(receiver, filter);

        if (isRoundBackgroundOn) {
            loadImageShared();
        }
        else {
            setColors();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (which_play_activity == ROUND) {
            unbindService(this);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        }
    }

    private void setColors() {
        default_color = base_color;
        if (which_play_activity == ROUND) {
            try {
                songList.setColorFilter(default_color);
                backPressed.setColorFilter(default_color);
                duration.setTextColor(default_color);
                remainTime.setTextColor(default_color);
                music_txt.setTextColor(default_color);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                option_menu.setColorFilter(default_color);
                back_btn.setColorFilter(default_color);
                now_playing.setTextColor(default_color);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // user method
    private void initViews() {
        backPressed = findViewById(R.id.back_pressed);
        songList = findViewById(R.id.song_list);
        remainTime = findViewById(R.id.duration_remaining);
        duration = findViewById(R.id.duration);
        tabLayout = findViewById(R.id.first_tab_layout_play_act);
        viewPager = findViewById(R.id.view_pager_play_activity);
        music_txt = findViewById(R.id.music_txt);
        r_a_background_img = findViewById(R.id.round_activity_background_img);
    }

    private void doAction() {
        backPressed.setOnClickListener(view -> onBackPressed());
        songList.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction("play.activity.more.clicked");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        });
        setTabLayout();
    }

    private void getSharedPref() {
        Handler handler = new Handler();
        PlayActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (service != null) {
                    if (service.isPlaying())
                        remainTime.setText(service.getTime(service.getCurrentPosition()));
                    handler.postDelayed(this, 1000);
                }
            }
        });

        if (service != null)
            if (service.isPlaying())
                duration.setText(service.getTime(service.getDuration()));
    }

    private void setTabLayout() {

        PlayViewPagerAdapter adapter = new PlayViewPagerAdapter(this
                ,getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager, true);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    TextView now_playing;
    public void initViews2() {
        back_btn = findViewById(R.id.backbtn);
        option_menu = findViewById(R.id.options);
        tabLayout2 = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.view_pager_play_activity2);
        dot_1 = findViewById(R.id.dot_1);
        dot_2 = findViewById(R.id.dot_2);
        now_playing = findViewById(R.id.now_playing);
        s_a_background_img = findViewById(R.id.square_activity_2_background_img);

        setTabLayout2();
    }

    private void setTabLayout2() {
        PlayViewPagerAdapter2 adapter = new PlayViewPagerAdapter2(this
                ,getSupportFragmentManager(), 2);
        viewPager2.setAdapter(adapter);

        tabLayout2.setupWithViewPager(viewPager2, true);

        Objects.requireNonNull(tabLayout2.getTabAt(1)).select();

        viewPager2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    dot_1.setImageResource(R.drawable.default_dot_2);
                    dot_2.setImageResource(R.drawable.selected_dot2);
                } else {
                    dot_1.setImageResource(R.drawable.selected_dot2);
                    dot_2.setImageResource(R.drawable.default_dot_2);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager2.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout2));
    }

    public void setClicker2() {
        option_menu.setOnClickListener(view -> {
            try {
                SharedPreferences preferences = getSharedPreferences("PLAYING", MODE_PRIVATE);
                position = preferences.getInt(SONGPOSITION, 0);
                Bundle bundle = new Bundle();
                bundle.putSerializable(PASSEDSONGFILE, song.get(position));
                bundle.putInt(SONGPOSITION, position);
                bundle.putByteArray(IMAGEOFSONG, passing_byte);
                BSheetSongsMoreFragment bSheetSongsMoreFragment = new BSheetSongsMoreFragment();
                bSheetSongsMoreFragment.setArguments(bundle);
                bSheetSongsMoreFragment.show(getSupportFragmentManager(), "ABC");
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        back_btn.setOnClickListener(view -> onBackPressed());
    }

    private void loadImageShared() {
        SharedPreferences preferences;
        try {
            preferences = getSharedPreferences("PLAYING", MODE_PRIVATE);
            pos = preferences.getInt(SONGPOSITION, 0);
            MyImageLoad load = new MyImageLoad();
            load.execute(song.get(pos));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Service
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        BackService.LocalBinder binder = (BackService.LocalBinder) iBinder;
        service = binder.getService();
        getSharedPref();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }


    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        MediaMetadataRetriever retriever;
        Bitmap bitmap;


        @Override
        protected byte[] doInBackground(MusicFiles... musicFiles) {
            try {
                fileIO = musicFiles[0];
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

        @SuppressLint("ResourceAsColor")
        @RequiresApi(api = Build.VERSION_CODES.S)
        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            passing_byte = bytes;
            if (isRoundBackgroundOn && bytes == null) {
                if (which_play_activity == ROUND && r_a_background_img != null)
                    r_a_background_img.setBackgroundColor(android.R.color.transparent);
                else if (s_a_background_img != null)
                    s_a_background_img.setBackgroundColor(android.R.color.transparent);
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(android.R.color.transparent);
            }
            if (bytes != null) {
                if (isRoundBackgroundOn && which_act_background_round == COLORED || which_act_background_round == BLUR) {
                    default_color = white_color;
                }
            } else if (isRoundBackgroundOn && which_act_background_round == GRADIENT) {
                default_color = white_color;
            } else {
                default_color = base_color;
            }
            if (which_play_activity == ROUND) {
                try {
                    songList.setColorFilter(default_color);
                    backPressed.setColorFilter(default_color);
                    duration.setTextColor(default_color);
                    remainTime.setTextColor(default_color);
                    music_txt.setTextColor(default_color);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    option_menu.setColorFilter(default_color);
                    back_btn.setColorFilter(default_color);
                    now_playing.setTextColor(default_color);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (isRoundBackgroundOn) {
                if (which_act_background_round == COLORED) {
                    if (bytes != null) {
                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Palette.from(bitmap).generate(palette -> {
                            if (palette != null) {
                                Palette.Swatch swatch = palette.getDominantSwatch();
                                if (swatch != null) {
                                    if (which_play_activity == ROUND && r_a_background_img != null)
                                        r_a_background_img.setBackgroundColor(swatch.getRgb());
                                    else if (s_a_background_img != null)
                                        s_a_background_img.setBackgroundColor(swatch.getRgb());
                                    Window window = getWindow();
                                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                    window.setStatusBarColor(swatch.getRgb());
                                }
                            }
                        });
                    }
                } else if (which_act_background_round == GRADIENT) {
                    GradientDrawable drawable = new GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            new int[]{0xff0b0b45, 0xff000000}
                        );
                    if (which_play_activity == ROUND && r_a_background_img != null)
                        r_a_background_img.setImageDrawable(drawable);
                    else if (s_a_background_img != null)
                        s_a_background_img.setImageDrawable(drawable);
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(0xff0b0b45);
                } else if (which_act_background_round == BLUR) {
                    Bitmap bitmap;
                    if (bytes != null) {
                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        try {
                            Blurry.with(PlayActivity.this)
                                    .radius((int) BLUR_RADIUS)
                                    .sampling(8)
                                    .async()
                                    .from(bitmap)
                                    .into((which_play_activity == ROUND) ? r_a_background_img : s_a_background_img);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Window window = PlayActivity.this.getWindow();
                        Drawable drawable = new BitmapDrawable(getResources(), BlurBuilder.blur(PlayActivity.this, bitmap));
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
                        window.setNavigationBarColor(getResources().getColor(android.R.color.transparent));
                        window.setBackgroundDrawable(drawable);
                    }
                }
                else if (which_act_background_round == DEFAULT) {
                    Bitmap bitmap;
                    if (bytes != null) {
                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Palette.from(bitmap).generate(palette -> {
                            if (palette != null) {
                                Palette.Swatch swatch = palette.getDominantSwatch();
                                GradientDrawable gradientDrawableBg;
                                if (swatch != null) {
                                    gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                            new int[]{swatch.getRgb(), swatch.getRgb(), swatch.getRgb(), 0x00000000, 0x00000000});
                                } else {
                                    gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                            new int[]{0xff000000, 0x00000000});
                                }
                                if (which_play_activity == ROUND && r_a_background_img != null)
                                    r_a_background_img.setBackground(gradientDrawableBg);
                                else if (s_a_background_img != null)
                                    s_a_background_img.setBackground(gradientDrawableBg);
                            }
                        });
                    }
                }
            }
        }
    }
}
