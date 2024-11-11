package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.service.SearchSongIntentService.START_SEARCHING_SONGS;
import static com.sanket_satpute_20.playme.project.service.SearchSongIntentService.current_scanning_song_name;
import static com.sanket_satpute_20.playme.project.service.SearchSongIntentService.scanned_files;
import static com.sanket_satpute_20.playme.project.service.SearchSongIntentService.x;
import static com.google.android.material.progressindicator.LinearProgressIndicator.INDETERMINATE_ANIMATION_TYPE_CONTIGUOUS;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.service.SearchSongIntentService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class ScanSongsActivity extends AppCompatActivity {


    public static boolean scanning_on = false, is_expanded = false;
    final int time = 200, wave_time = 500, state_txt_time = 600;
    int i = 0;
    public static boolean isOnSkip30s, isOnSkip100kb;

    String []scanning_states = {"Scanning", "Scanning.", "Scanning..", "Scanning..."};
    String []saving_states = {"Saving", "Saving.", "Saving..", "Saving..."};

    int state = 0;


    @SuppressLint("StaticFieldLeak")
    public static TextView state_txt, percent_txt, scanned_songs;
    MaterialCardView blinking_card, wave_card_first, wave_card_second;
    MaterialButton start_stop_btn;
    LinearProgressIndicator progress_bar;
    ImageView back_pressed, option;
    RelativeLayout top_layout;

    private final BroadcastReceiver reciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("Scanning_finished.broadcast")) {
//                scanning finished
                state += 1;
                state_txt.setTextColor(0xff00ff00);
                startSavingData();
            }
            else if (intent.getAction().equals("saving_finished.broadcast")) {
//                saving finished
                scanning_on = false;
                stoppedScanning();
                if (scanned_files != null)
                    scanned_files.clear();
                start_stop_btn.setText(R.string.start);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_songs);
        initViews();
        onClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPriorColors();
        IntentFilter filter = new IntentFilter();
        filter.addAction("Scanning_finished.broadcast");
        filter.addAction("saving_finished.broadcast");
        LocalBroadcastManager.getInstance(ScanSongsActivity.this).registerReceiver(reciver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        state = 0;
        if (scanned_files != null)
            scanned_files.clear();
        LocalBroadcastManager.getInstance(ScanSongsActivity.this).unregisterReceiver(reciver);
        stopScanningCode();
    }

    private void initViews() {
        state_txt = findViewById(R.id.state_txt);
        percent_txt = findViewById(R.id.percent);
        scanned_songs = findViewById(R.id.scanned_songs);
        blinking_card = findViewById(R.id.main_card_blinker);
        wave_card_first = findViewById(R.id.wave_card_first);
        wave_card_second = findViewById(R.id.wave_card_second);
        start_stop_btn = findViewById(R.id.start_stop_scan_btn);
        progress_bar = findViewById(R.id.progress_bar);
        top_layout = findViewById(R.id.top_layout);
        back_pressed = findViewById(R.id.back_pressed);
        option = findViewById(R.id.option);
    }

    private void setPriorColors() {
        start_stop_btn.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            wave_card_first.setOutlineAmbientShadowColor(ACCENT_COLOR);
            wave_card_first.setOutlineSpotShadowColor(ACCENT_COLOR);
            wave_card_second.setOutlineAmbientShadowColor(ACCENT_COLOR);
            wave_card_second.setOutlineSpotShadowColor(ACCENT_COLOR);
            blinking_card.setOutlineAmbientShadowColor(ACCENT_COLOR);
            blinking_card.setOutlineSpotShadowColor(ACCENT_COLOR);
        }
        wave_card_first.setRippleColor(ColorStateList.valueOf(ACCENT_COLOR));
        wave_card_first.setStrokeColor(ColorStateList.valueOf(ACCENT_COLOR));
        wave_card_first.setCardBackgroundColor(ColorStateList.valueOf(ACCENT_COLOR));
        wave_card_first.setCardForegroundColor(ColorStateList.valueOf(ACCENT_COLOR));
        wave_card_first.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));

        wave_card_second.setRippleColor(ColorStateList.valueOf(ACCENT_COLOR));
        wave_card_second.setStrokeColor(ColorStateList.valueOf(ACCENT_COLOR));
        wave_card_second.setCardBackgroundColor(ColorStateList.valueOf(ACCENT_COLOR));
        wave_card_second.setCardForegroundColor(ColorStateList.valueOf(ACCENT_COLOR));
        wave_card_second.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));

        blinking_card.setRippleColor(ColorStateList.valueOf(ACCENT_COLOR));
        blinking_card.setStrokeColor(ColorStateList.valueOf(ACCENT_COLOR));
        blinking_card.setCardBackgroundColor(ColorStateList.valueOf(ACCENT_COLOR));
        blinking_card.setCardForegroundColor(ColorStateList.valueOf(ACCENT_COLOR));
        blinking_card.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));
    }

    private void onClick() {

        back_pressed.setOnClickListener(view -> onBackPressed());

        option.setOnClickListener(view -> openPopUpMenu());

        start_stop_btn.setOnClickListener(view -> {
            startStopBtnClick();
            if (scanning_on) {
                try {
                    start_stop_btn.setText(R.string.start);
                    progress_bar.setIndicatorColor(Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW);
                    progress_bar.setIndeterminate(false);
                    progress_bar.setIndeterminateAnimationType(INDETERMINATE_ANIMATION_TYPE_CONTIGUOUS);
                    scanning_on = false;
                    stopScanningCode();
                    stoppedScanning();
                    if (scanned_files != null)
                        scanned_files.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    startedScanning();
                    start_stop_btn.setText(R.string.stop);
                    scanning_on = true;
                    wave_card_first.setVisibility(View.VISIBLE);
                    wave_card_second.setVisibility(View.VISIBLE);
                    startBlinking();
                    progress_bar.setIndeterminate(true);
                    progress_bar.setIndicatorColor(Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW);
                    progress_bar.setIndeterminateAnimationType(LinearProgressIndicator.INDETERMINATE_ANIMATION_TYPE_DISJOINT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startBlinking() {

        Handler state_txt_handler = new Handler();
        state_txt_handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                i = (i < 3) ? i + 1 : 0;
                if (state == 0)
                    state_txt.setText(scanning_states[i]);
                else if (state == 1)
                    state_txt.setText(saving_states[i]);
                if (scanning_on)
                    state_txt_handler.postDelayed(this, state_txt_time);
                else
                    state_txt.setText(R.string.up_to_date);
            }
        }, 0);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (is_expanded)
                    defuseBlinker();
                else
                    refuseBlinker();
                is_expanded = !is_expanded;
                if (scanning_on)
                    handler.postDelayed(this, time);
                else {
                    blinking_card.setScaleY(1);
                    blinking_card.setScaleX(1);
                }
            }
        }, 0);

        Handler wave_first_handler = new Handler();
        wave_first_handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                extendFirstWave();
                if (scanning_on)
                    wave_first_handler.postDelayed(this, wave_time);
                else {
                    wave_card_first.setScaleX(1f);
                    wave_card_first.setScaleY(1f);
                    wave_card_first.setVisibility(View.GONE);
                }
            }
        }, 50);

        Handler wave_second_handler = new Handler();
        wave_second_handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                extendSecondWave();
                if (scanning_on) {
                    wave_second_handler.postDelayed(this, wave_time);
                }
                else {
                    wave_card_second.setScaleX(1f);
                    wave_card_second.setScaleY(1f);
                    wave_card_second.setVisibility(View.GONE);
                }
            }
        }, 50);

        startBackGroundService();
        Handler scan_songs_handler = new Handler();
        ScanSongsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (current_scanning_song_name != null)
                    scanned_songs.setText(current_scanning_song_name);
                if (scanning_on)
                    scan_songs_handler.postDelayed(this, 0);
            }
        });
    }

    private void startBackGroundService() {
        Intent intent = new Intent(ScanSongsActivity.this, SearchSongIntentService.class);
        intent.setAction(START_SEARCHING_SONGS);
        startService(intent);
    }

    private void stopScanningCode() {
        Intent intent = new Intent();
        intent.setAction("stop.scanning.Clicked.btn");
        LocalBroadcastManager.getInstance(ScanSongsActivity.this).sendBroadcast(intent);
    }

    private void refuseBlinker() {
        /*  Big Size    */
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(blinking_card, "scaleX", 1f, 1.1f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(blinking_card, "scaleY", 1f, 1.1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animatorX, animatorY);
        set.setDuration(time - 10);
        set.setInterpolator(new LinearInterpolator());
        set.start();
    }

    private void startSavingData() {
        if (scanned_files != null) {
            progress_bar.setIndicatorColor(Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW);
            progress_bar.setIndeterminate(false);
            progress_bar.setIndeterminateAnimationType(INDETERMINATE_ANIMATION_TYPE_CONTIGUOUS);
            progress_bar.setMax(scanned_files.size());
            Handler handler = new Handler();
            Runnable thread = new Runnable() {
                @Override
                public void run() {
                    if (x >= scanned_files.size()) {
                        scanned_songs.setTextColor(0xff00ff00);
                    }
                    progress_bar.setProgress(x);
                    handler.postDelayed(this, 100);
                }
            };
            handler.postDelayed(thread, 0);
        }
    }

    private void defuseBlinker() {
        /*  Small Size  */
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(blinking_card, "scaleX", 1.1f, 1f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(blinking_card, "scaleY", 1.1f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animatorX, animatorY);
        set.setDuration(time - 10);
        set.setInterpolator(new LinearInterpolator());
        set.start();
    }

    private void extendFirstWave() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(wave_card_first, "scaleX", 1f, 3f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(wave_card_first, "scaleY", 1f, 3f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animatorX, animatorY);
        set.setDuration(wave_time);
        set.setInterpolator(new LinearInterpolator());
        set.start();
    }

    private void extendSecondWave() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(wave_card_second, "scaleX", 1f, 3f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(wave_card_second, "scaleY", 1f, 3f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animatorX, animatorY);
        set.setDuration(wave_time);
        set.setInterpolator(new LinearInterpolator());
        set.start();
    }



    private void startedScanning() {
        ObjectAnimator top_alpha = ObjectAnimator.ofFloat(top_layout, "alpha", 1f, 0f);
        top_alpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                top_layout.setVisibility(View.GONE);
                scanned_songs.setVisibility(View.VISIBLE);
                progress_bar.setVisibility(View.VISIBLE);
                progress_bar.setTranslationY(700);
                progress_bar.setAlpha(0);
                scanned_songs.setAlpha(0);
                ObjectAnimator trans_x = ObjectAnimator.ofFloat(progress_bar, "translationY", 700, 0);
                ObjectAnimator alpha_pro = ObjectAnimator.ofFloat(progress_bar, "alpha", 0f, 1f);
                ObjectAnimator alpha_txt = ObjectAnimator.ofFloat(scanned_songs, "alpha", 0f, 1f);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(trans_x, alpha_pro, alpha_txt);
                set.setDuration(250);
                set.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        top_alpha.setDuration(200);
        top_alpha.start();
    }

    private void stoppedScanning() {
        ObjectAnimator trans_Y = ObjectAnimator.ofFloat(progress_bar, "translationY", 0, 700);
        ObjectAnimator alpha_pro = ObjectAnimator.ofFloat(progress_bar, "alpha", 1f, 0f);
        ObjectAnimator alpha_txt = ObjectAnimator.ofFloat(scanned_songs, "alpha", 1f, 0f);
        trans_Y.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                progress_bar.setVisibility(View.GONE);
                scanned_songs.setVisibility(View.GONE);
                top_layout.setVisibility(View.VISIBLE);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(top_layout, "alpha", 0f, 1f);
                alpha.setDuration(150);
                alpha.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        AnimatorSet set = new AnimatorSet();
        set.playTogether(trans_Y, alpha_pro, alpha_txt);
        set.setDuration(300);
        set.start();
    }

    @SuppressLint("NonConstantResourceId")
    private void openPopUpMenu() {
        PopupMenu menu = new PopupMenu(ScanSongsActivity.this, option);
        menu.getMenuInflater().inflate(R.menu.scan_media_menu, menu.getMenu());
        menu.getMenu().findItem(R.id.skip_less_than_100kb).setChecked(isOnSkip100kb);
        menu.getMenu().findItem(R.id.skip_less_than_30s).setChecked(isOnSkip30s);
        menu.setOnMenuItemClickListener(menuItem -> {

            int itemID = menuItem.getItemId();
            if (itemID == R.id.skip_less_than_30s) {
                menuItem.setChecked((!menuItem.isChecked()));
                isOnSkip30s = (!menuItem.isChecked());
            } else {
                menuItem.setChecked((!menuItem.isChecked()));
                isOnSkip100kb = (!menuItem.isChecked());
            }
            return true;
        });
        menu.show();
    }

    private void startStopBtnClick() {
        ObjectAnimator downX = ObjectAnimator.ofFloat(start_stop_btn, "scaleX", 1f, 0.9f);
        ObjectAnimator downY = ObjectAnimator.ofFloat(start_stop_btn, "scaleY", 1f, 0.9f);
        AnimatorSet down = new AnimatorSet();
        down.setDuration(100);
        down.setInterpolator(new LinearInterpolator());
        down.playTogether(downX, downY);
        down.start();

        ObjectAnimator upX = ObjectAnimator.ofFloat(start_stop_btn, "scaleX",  0.9f, 1f);
        ObjectAnimator upY = ObjectAnimator.ofFloat(start_stop_btn, "scaleY",  0.9f, 1f);
        AnimatorSet up = new AnimatorSet();
        up.setStartDelay(100);
        up.setDuration(80);
        up.setInterpolator(new LinearInterpolator());
        up.playTogether(upX, upY);
        up.start();
    }
}