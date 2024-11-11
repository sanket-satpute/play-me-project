package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.project.activity.PlayActivity.isActivityChanged;
import static com.sanket_satpute_20.playme.project.activity.PlayActivity.isRoundBackgroundOn;
import static com.sanket_satpute_20.playme.project.activity.PlayActivity.which_act_background_round;
import static com.sanket_satpute_20.playme.project.activity.PlayActivity.which_play_activity;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.enums.PlayActBottom.FRAMES;
import static com.sanket_satpute_20.playme.project.enums.PlayActBottom.LIST;
import static com.sanket_satpute_20.playme.project.enums.PlayActWhich.ROUND;
import static com.sanket_satpute_20.playme.project.enums.PlayActWhich.SQUARE;
import static com.sanket_satpute_20.playme.project.enums.ROUND_ACT_BACKGROUNDS.BLUR;
import static com.sanket_satpute_20.playme.project.enums.ROUND_ACT_BACKGROUNDS.COLORED;
import static com.sanket_satpute_20.playme.project.enums.ROUND_ACT_BACKGROUNDS.DEFAULT;
import static com.sanket_satpute_20.playme.project.enums.ROUND_ACT_BACKGROUNDS.GRADIENT;
import static com.sanket_satpute_20.playme.project.fragments.PlayOptionFragment.expand_visible_disable;
import static com.sanket_satpute_20.playme.project.fragments.PlayOptionFragment.is_which_bottom;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.chibde.visualizer.BarVisualizer;
import com.chibde.visualizer.CircleBarVisualizer;
import com.chibde.visualizer.CircleBarVisualizerSmooth;
import com.chibde.visualizer.CircleVisualizer;
import com.chibde.visualizer.LineBarVisualizer;
import com.chibde.visualizer.LineVisualizer;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.enums.ROUND_ACT_BACKGROUNDS;
import com.sanket_satpute_20.playme.project.service.BackService;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.radiobutton.MaterialRadioButton;

public class PlayerDesignActivity extends AppCompatActivity implements ServiceConnection {

    ImageView back_pressed, selector_img_round, selector_img_square, select_frame_img, select_list_img;
    RelativeLayout round_design, square_design, frame_selected, list_selected;
    SwitchCompat player_background_switch;
    Spinner spinner_b_types, spinner_visualizer_colors;
    ConstraintLayout round_activity_options, square_activity_options;
    MaterialCheckBox check_expand_enable_disable;

    /*  Visulizer Types */
    CircleVisualizer circle_visulizer;
    CircleBarVisualizer circle_bar_visulizer;
    CircleBarVisualizerSmooth circle_bar_visulizer_smooth;
    LineVisualizer line_visulizer;
    LineBarVisualizer line_bar_visulizer;

    ImageView next_visulizer, previous_visulizer;
    TextView current_visulizer_txt, background_type_txt_round;

    public static int vis_speed = 2;


    String []background_types = {"Default", "Colored", "Gradient", "Blur"};
    String []visulizer_array = {"CIRCLE_VISULIZER", "CIRCLE_BAR_VISULIZER", "CIRCLE_BAR_VISULIZER_SMOOTH", "LINE_VISULIZER", "LINE_BAR_VISULIZER"};
    String []visulizer_color = {"Red", "Blue", "Pink", "Green", "Violet", "Orange", "Default"};
    public static int vis_color = Color.CYAN;
    public static int visulizer_position = 0;
    int sessionId;

    /*  square */
    BarVisualizer square_visulizer;
    SwitchCompat background_on_square;
    Spinner b_types_square, square_vis_color;
    TextView background_type_txt_square;

    int color_position;

    RadioGroup choose_bottom_r_group;
    ConstraintLayout visulizer_constraint;
    MaterialRadioButton lyrics_radio_btn, visulizer_radio_btn;

    public static boolean is_visulizer_constraint_visible;

    BackService service;

    private final BroadcastReceiver reciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (which_play_activity == ROUND) {
                defaultRoundVisulizer();
            } else {
                defaultSquareVisulizer();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_design);
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
        bindService(intent, this, BIND_AUTO_CREATE);
        doExtra();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciver);
        unbindService(this);
    }

    private void initViews() {
        back_pressed = findViewById(R.id.back_pressed);
        round_design = findViewById(R.id.round_design);
        square_design = findViewById(R.id.square_design);
        selector_img_round = findViewById(R.id.selector_img_round);
        selector_img_square = findViewById(R.id.selector_img_square);
        player_background_switch = findViewById(R.id.player_background_switch);
        spinner_b_types = findViewById(R.id.spinner_background_types);
        frame_selected = findViewById(R.id.frames_selected);
        list_selected = findViewById(R.id.list_selected);
        select_frame_img = findViewById(R.id.select_frame_img);
        select_list_img = findViewById(R.id.select_list_img);
        round_activity_options = findViewById(R.id.round_activity_option);
        square_activity_options = findViewById(R.id.square_activity_option);
        next_visulizer = findViewById(R.id.next_visulizer);
        previous_visulizer = findViewById(R.id.previous_visulizer);
        current_visulizer_txt = findViewById(R.id.current_visulizer_txt);
        check_expand_enable_disable = findViewById(R.id.expand_btn_enable_disable);
        spinner_visualizer_colors = findViewById(R.id.visulizer_colors_spinner);

//        square
        background_on_square = findViewById(R.id.square_background_switch);
        b_types_square = findViewById(R.id.square_background_types_spinner);
        square_vis_color = findViewById(R.id.square_vis_color_spinner);
        square_visulizer = findViewById(R.id.waveVisulizer);
        choose_bottom_r_group = findViewById(R.id.choose_bottom_radio_group);
        visulizer_constraint = findViewById(R.id.visulizer_constraint_layout);
        lyrics_radio_btn = findViewById(R.id.lyrics_radio_btn);
        visulizer_radio_btn = findViewById(R.id.visulizer_radio_btn);
        background_type_txt_square = findViewById(R.id.background_type_txt);
        background_type_txt_round = findViewById(R.id.choose_background_txt);

//        visulizer types
        circle_visulizer = findViewById(R.id.circle_visulizer);
        circle_bar_visulizer = findViewById(R.id.circle_bar_visulizer);
        circle_bar_visulizer_smooth = findViewById(R.id.circle_bar_visulizer_smooth);
        line_visulizer = findViewById(R.id.line_visulizer);
        line_bar_visulizer = findViewById(R.id.line_bar_visulizer);
    }

    private void doExtra() {
        if (vis_color == Color.RED || vis_color == 0xff0000) {
            color_position = 0;
        } else if (vis_color == Color.BLUE || vis_color == 0x0000ff) {
            color_position = 1;
        } else if (vis_color == 0xffc0cb || vis_color == 0xffffc0cb) {
            color_position = 2;
        } else if (vis_color == Color.GREEN || vis_color == 0x00ff00) {
            color_position = 3;
        } else if (vis_color == 0xff8f00ff || vis_color == 0x8f00ff) {
            color_position = 4;
        } else if (vis_color == 0xffffa500 || vis_color == 0xffa500) {
            color_position = 5;
        } else {
            color_position = 6;
        }
        if (which_play_activity == SQUARE) {
            selector_img_square.setVisibility(View.VISIBLE);
            selector_img_round.setVisibility(View.GONE);
            squareIsVisible();
        } else {
            selector_img_square.setVisibility(View.GONE);
            selector_img_round.setVisibility(View.VISIBLE);
            roundIsVisible();
        }
    }

    private void onClick() {

        back_pressed.setOnClickListener(view -> onBackPressed());
        round_design.setOnClickListener(view -> roundDesignActivated());
        square_design.setOnClickListener(view -> squareDesignActivated());
        frame_selected.setOnClickListener(view -> {
            is_which_bottom = FRAMES;
            select_frame_img.setVisibility(View.VISIBLE);
            select_list_img.setVisibility(View.GONE);
        });
        list_selected.setOnClickListener(view -> {
            is_which_bottom = LIST;
            select_frame_img.setVisibility(View.GONE);
            select_list_img.setVisibility(View.VISIBLE);
        });
    }

    private void roundDesignActivated() {
        isActivityChanged = true;
        which_play_activity = ROUND;
        selector_img_round.setVisibility(View.VISIBLE);
        selector_img_square.setVisibility(View.GONE);
        roundIsVisible();
    }

    private void squareDesignActivated() {
        isActivityChanged = true;
        which_play_activity = SQUARE;
        selector_img_round.setVisibility(View.GONE);
        selector_img_square.setVisibility(View.VISIBLE);
        squareIsVisible();
    }

    /*  Round   */
    private void roundIsVisible() {
        round_activity_options.setVisibility(View.VISIBLE);
        square_activity_options.setVisibility(View.GONE);
        if (is_which_bottom == FRAMES) {
            select_frame_img.setVisibility(View.VISIBLE);
            select_list_img.setVisibility(View.GONE);
        } else {
            select_frame_img.setVisibility(View.GONE);
            select_list_img.setVisibility(View.VISIBLE);
        }
        int background_position;
        if (which_act_background_round == GRADIENT) {
            background_position = 1;
        } else if (which_act_background_round == COLORED) {
            background_position = 2;
        } else if (which_act_background_round == BLUR) {
            background_position = 3;
        } else {
            background_position = 0;
        }
        setVisDisBackOnRound();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, background_types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_b_types.setAdapter(adapter);
        spinner_b_types.setSelection(background_position);
        spinner_b_types.setPrompt("Select Player Background");

        roundDoExtra();
        roundOnClick();
    }

    /*  Round Methods   */

    private void roundDoExtra() {
        check_expand_enable_disable.setChecked(expand_visible_disable);
        player_background_switch.setChecked(isRoundBackgroundOn);
        defaultRoundVisulizer();
    }

    private void defaultRoundVisulizer() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, visulizer_color);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_visualizer_colors.setAdapter(adapter);
        spinner_visualizer_colors.setSelection(color_position);


        if (service != null) {
            sessionId = service.getAudioSessionId();
            current_visulizer_txt.setText(visulizer_array[visulizer_position]);
            if (sessionId != -1) {
                try {
                    if (visulizer_array[visulizer_position].equals(visulizer_array[0])) {
                        if (circle_visulizer != null) {
                            circle_visulizer.release();
                        }
                        try {
                            circle_visulizer.setAlpha(1);
                            circle_visulizer.setEnabled(false);
                            circle_visulizer.setPlayer(sessionId);
                            circle_visulizer.setColor(vis_color);
                        } catch (Exception e) {
                            e.printStackTrace();
                            circle_visulizer.setAlpha(1);
                            circle_visulizer.setEnabled(false);
                            circle_visulizer.setPlayer(sessionId);
                            circle_visulizer.setColor(vis_color);
                        }
                    } else if (visulizer_array[visulizer_position].equals(visulizer_array[1])) {
                        if (circle_bar_visulizer != null) {
                            circle_bar_visulizer.release();
                        }
                        try {
                            circle_bar_visulizer.setAlpha(1);
                            circle_bar_visulizer.setEnabled(false);
                            circle_bar_visulizer.setPlayer(sessionId);
                            circle_bar_visulizer.setColor(vis_color);
                        } catch (Exception e) {
                            e.printStackTrace();
                            circle_bar_visulizer.setAlpha(1);
                            circle_bar_visulizer.setEnabled(false);
                            circle_bar_visulizer.setPlayer(sessionId);
                            circle_bar_visulizer.setColor(vis_color);
                        }
                    } else if (visulizer_array[visulizer_position].equals(visulizer_array[2])) {
                        if (circle_bar_visulizer_smooth != null) {
                            circle_bar_visulizer_smooth.release();
                        }
                        try {
                            circle_bar_visulizer_smooth.setAlpha(1);
                            circle_bar_visulizer_smooth.setEnabled(false);
                            circle_bar_visulizer_smooth.setPlayer(sessionId);
                            circle_bar_visulizer_smooth.setColor(vis_color);
                        } catch (Exception e) {
                            e.printStackTrace();
                            circle_bar_visulizer_smooth.setAlpha(1);
                            circle_bar_visulizer_smooth.setEnabled(false);
                            circle_bar_visulizer_smooth.setPlayer(sessionId);
                            circle_bar_visulizer_smooth.setColor(vis_color);
                        }
                    } else if (visulizer_array[visulizer_position].equals(visulizer_array[3])) {
                        if (line_visulizer != null) {
                            line_visulizer.release();
                        }
                        try {
                            line_visulizer.setAlpha(1);
                            line_visulizer.setEnabled(false);
                            line_visulizer.setPlayer(sessionId);
                            line_visulizer.setColor(vis_color);
                        } catch (Exception e) {
                            e.printStackTrace();
                            line_visulizer.setAlpha(1);
                            line_visulizer.setEnabled(false);
                            line_visulizer.setPlayer(sessionId);
                            line_visulizer.setColor(vis_color);
                        }
                    } else {
                        if (line_bar_visulizer != null) {
                            line_bar_visulizer.release();
                        }
                        try {
                            line_bar_visulizer.setAlpha(1);
                            line_bar_visulizer.setEnabled(false);
                            line_bar_visulizer.setPlayer(sessionId);
                            line_bar_visulizer.setColor(vis_color);
                        } catch (Exception e) {
                            e.printStackTrace();
                            line_bar_visulizer.setAlpha(1);
                            line_bar_visulizer.setEnabled(false);
                            line_bar_visulizer.setPlayer(sessionId);
                            line_bar_visulizer.setColor(vis_color);
                        }
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }

        circle_visulizer.setColor(vis_color);
        circle_bar_visulizer.setColor(vis_color);
        circle_bar_visulizer_smooth.setColor(vis_color);
        line_visulizer.setColor(vis_color);
        line_bar_visulizer.setColor(vis_color);
    }

    private void roundOnClick() {

        check_expand_enable_disable.setOnCheckedChangeListener((compoundButton, b) -> expand_visible_disable = b);

        player_background_switch.setOnCheckedChangeListener((compoundButton, b) -> {
            isRoundBackgroundOn = b;
            setVisDisBackOnRound();
        });

        spinner_visualizer_colors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItem().equals("Red")) {
                    vis_color = Color.RED;
                } else if (adapterView.getSelectedItem().equals("Blue")) {
                    vis_color = Color.BLUE;
                } else if (adapterView.getSelectedItem().equals("Pink")) {
                    vis_color = Color.parseColor("#ffc0cb");
                } else if (adapterView.getSelectedItem().equals("Green")) {
                    vis_color = Color.GREEN;
                } else if (adapterView.getSelectedItem().equals("Violet")) {
                    vis_color = Color.parseColor("#8f00ff");
                } else if (adapterView.getSelectedItem().equals("Orange")) {
                    vis_color = Color.parseColor("#ffa500");
                } else {
                    vis_color = ACCENT_COLOR;
                }
                circle_visulizer.setColor(vis_color);
                circle_bar_visulizer.setColor(vis_color);
                circle_bar_visulizer_smooth.setColor(vis_color);
                line_visulizer.setColor(vis_color);
                line_bar_visulizer.setColor(vis_color);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        previous_visulizer.setOnClickListener(view -> {
            if (visulizer_position > 0) {
                visulizer_position -= 1;
                current_visulizer_txt.setText(visulizer_array[visulizer_position]);
                previousVisulizer();
            }
        });

        next_visulizer.setOnClickListener(view -> {
            if (visulizer_position < 3) {
                visulizer_position += 1;
                current_visulizer_txt.setText(visulizer_array[visulizer_position]);
                nextVisulizer();
            }
        });

        spinner_b_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItem().equals(background_types[0])) {
                    which_act_background_round = DEFAULT;
                } else if (adapterView.getSelectedItem().equals(background_types[1])) {
                    which_act_background_round = COLORED;
                } else if (adapterView.getSelectedItem().equals(background_types[2])) {
                    which_act_background_round = ROUND_ACT_BACKGROUNDS.GRADIENT;
                } else {
                    which_act_background_round = ROUND_ACT_BACKGROUNDS.BLUR;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void previousVisulizer() {
        int sessionId = service.getAudioSessionId();
        if (visulizer_array[visulizer_position].equals(visulizer_array[0])) {
            if (circle_bar_visulizer != null) {
                circle_bar_visulizer.release();
                circle_bar_visulizer.setAlpha(0);
            }
            setPreviousVisulizerAnimation(circle_bar_visulizer, circle_visulizer);
            if (sessionId != -1) {
                try {
                    circle_visulizer.setAlpha(1);
                    circle_visulizer.setEnabled(false);
                    circle_visulizer.setPlayer(sessionId);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    circle_visulizer.setAlpha(1);
                    circle_visulizer.setEnabled(false);
                    circle_visulizer.setPlayer(sessionId);
                }
            }
        } else if (visulizer_array[visulizer_position].equals(visulizer_array[1])) {
            if (circle_bar_visulizer_smooth != null) {
                circle_bar_visulizer_smooth.release();
                circle_bar_visulizer_smooth.setAlpha(0);
            }
            setPreviousVisulizerAnimation(circle_bar_visulizer_smooth, circle_bar_visulizer);
            if (sessionId != -1) {
                try {
                    circle_bar_visulizer.setAlpha(1);
                    circle_bar_visulizer.setEnabled(false);
                    circle_bar_visulizer.setPlayer(sessionId);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    circle_bar_visulizer.setAlpha(1);
                    circle_bar_visulizer.setEnabled(false);
                    circle_bar_visulizer.setPlayer(sessionId);
                }
            }
        } else if (visulizer_array[visulizer_position].equals(visulizer_array[2])) {
            if (line_visulizer != null) {
                line_visulizer.release();
                line_visulizer.setAlpha(0);
            }
            setPreviousVisulizerAnimation(line_visulizer, circle_bar_visulizer_smooth);
            if (sessionId != -1) {
                try {
                    circle_bar_visulizer_smooth.setAlpha(1);
                    circle_bar_visulizer_smooth.setEnabled(false);
                    circle_bar_visulizer_smooth.setPlayer(sessionId);
                } catch(IllegalStateException e) {
                    e.printStackTrace();
                    circle_bar_visulizer_smooth.setAlpha(1);
                    circle_bar_visulizer_smooth.setEnabled(false);
                    circle_bar_visulizer_smooth.setPlayer(sessionId);
                }
            }
        } else if (visulizer_array[visulizer_position].equals(visulizer_array[3])) {
            if (line_bar_visulizer != null) {
                line_bar_visulizer.release();
                line_bar_visulizer.setAlpha(0);
            }
            setPreviousVisulizerAnimation(line_bar_visulizer, line_visulizer);
            if (sessionId != -1) {
                try {
                    line_visulizer.setAlpha(1);
                    line_visulizer.setEnabled(false);
                    line_visulizer.setPlayer(sessionId);
                } catch(IllegalStateException e) {
                    e.printStackTrace();
                    line_visulizer.setAlpha(1);
                    line_visulizer.setEnabled(false);
                    line_visulizer.setPlayer(sessionId);
                }
            }
        }
    }

    private void nextVisulizer() {
        int sessionId = service.getAudioSessionId();
        if (visulizer_array[visulizer_position].equals(visulizer_array[1])) {
            if (circle_visulizer != null) {
                circle_visulizer.release();
                circle_visulizer.setAlpha(0);
            }
            setNextVisulizerAnimation(circle_visulizer, circle_bar_visulizer);
            if (sessionId != -1) {
                try {
                    circle_bar_visulizer.setAlpha(1);
                    circle_bar_visulizer.setEnabled(false);
                    circle_bar_visulizer.setPlayer(sessionId);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    circle_bar_visulizer.setAlpha(1);
                    circle_bar_visulizer.setEnabled(false);
                    circle_bar_visulizer.setPlayer(sessionId);
                }
            }
        } else if (visulizer_array[visulizer_position].equals(visulizer_array[2])) {
            if (circle_bar_visulizer != null) {
                circle_bar_visulizer.release();
                circle_bar_visulizer.setAlpha(0);
            }
            setNextVisulizerAnimation(circle_bar_visulizer, circle_bar_visulizer_smooth);
            if (sessionId != -1) {
                try {
                    circle_bar_visulizer_smooth.setAlpha(1);
                    circle_bar_visulizer_smooth.setEnabled(false);
                    circle_bar_visulizer_smooth.setPlayer(sessionId);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    circle_bar_visulizer_smooth.setAlpha(1);
                    circle_bar_visulizer_smooth.setEnabled(false);
                    circle_bar_visulizer_smooth.setPlayer(sessionId);
                }
            }
        } else if (visulizer_array[visulizer_position].equals(visulizer_array[3])) {
            if (circle_bar_visulizer_smooth != null) {
                circle_bar_visulizer_smooth.release();
                circle_bar_visulizer_smooth.setAlpha(0);
            }
            setNextVisulizerAnimation(circle_bar_visulizer_smooth, line_visulizer);
            if (sessionId != -1) {
                try {
                    line_visulizer.setAlpha(1);
                    line_visulizer.setEnabled(false);
                    line_visulizer.setPlayer(sessionId);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    line_visulizer.setAlpha(1);
                    line_visulizer.setEnabled(false);
                    line_visulizer.setPlayer(sessionId);
                }
            }
        } else if (visulizer_array[visulizer_position].equals(visulizer_array[4])) {
            if (line_visulizer != null) {
                line_visulizer.release();
                line_visulizer.setAlpha(0);
            }
            setNextVisulizerAnimation(line_visulizer, line_bar_visulizer);
            if (sessionId != -1) {
                try {
                    line_bar_visulizer.setAlpha(1);
                    line_bar_visulizer.setEnabled(false);
                    line_bar_visulizer.setPlayer(sessionId);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    line_bar_visulizer.setAlpha(1);
                    line_bar_visulizer.setEnabled(false);
                    line_bar_visulizer.setPlayer(sessionId);
                }
            }
        }
    }

    private void setPreviousVisulizerAnimation(View v1, View v2) {
        ObjectAnimator first_transX = ObjectAnimator.ofFloat(v1, "translationX", 0, 200f);
        ObjectAnimator first_alpha = ObjectAnimator.ofFloat(v1, "alpha", 1f, 0f);
        ObjectAnimator second_transX = ObjectAnimator.ofFloat(v2, "translationX", -200f, 0f);
        ObjectAnimator second_alpha = ObjectAnimator.ofFloat(v2, "alpha", 0f, 1f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(first_transX, first_alpha, second_transX, second_alpha);
        set.setDuration(200);
        set.start();
    }

    private void setNextVisulizerAnimation(View v1, View v2) {
        ObjectAnimator first_transX = ObjectAnimator.ofFloat(v1, "translationX", 0, -200f);
        ObjectAnimator first_alpha = ObjectAnimator.ofFloat(v1, "alpha", 1f, 0f);
        ObjectAnimator second_transX = ObjectAnimator.ofFloat(v2, "translationX", 200f, 0f);
        ObjectAnimator second_alpha = ObjectAnimator.ofFloat(v2, "alpha", 0f, 1f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(first_transX, first_alpha, second_transX, second_alpha);
        set.setDuration(200);
        set.start();
    }

    /*  Square  */
    private void squareIsVisible() {
        round_activity_options.setVisibility(View.GONE);
        square_activity_options.setVisibility(View.VISIBLE);
        int background_position;
        if (which_act_background_round == COLORED) {
            background_position = 1;
        } else if (which_act_background_round == GRADIENT) {
            background_position = 2;
        } else if (which_act_background_round == BLUR) {
            background_position = 3;
        } else {
            background_position = 0;
        }
        setVisDisBackOnSquare();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, background_types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        b_types_square.setAdapter(adapter);
        b_types_square.setSelection(background_position);
        b_types_square.setPrompt("Select Player Background");

        squareDoExtra();
        squareOnClick();
    }

    /*  Square Methods  */

    private void squareDoExtra() {
        background_on_square.setChecked(isRoundBackgroundOn);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, visulizer_color);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        square_vis_color.setAdapter(adapter);
        square_vis_color.setSelection(color_position);

        if (is_visulizer_constraint_visible) {
            visulizer_radio_btn.setChecked(true);
            lyrics_radio_btn.setChecked(false);
            visulizer_constraint.setVisibility(View.VISIBLE);
            defaultSquareVisulizer();
        } else {
            visulizer_radio_btn.setChecked(false);
            lyrics_radio_btn.setChecked(true);
            visulizer_constraint.setVisibility(View.GONE);
        }
    }

    private void defaultSquareVisulizer() {
        if (square_visulizer != null) {
            square_visulizer.release();
        }
        if (service != null) {
            sessionId = service.getAudioSessionId();
            if (sessionId != -1) {
                try {
                    square_visulizer.setEnabled(false);
                    square_visulizer.setPlayer(sessionId);
                    square_visulizer.setColor(vis_color);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        square_visulizer.setEnabled(false);
                        square_visulizer.setPlayer(sessionId);
                        square_visulizer.setColor(vis_color);
                    } catch (Exception f) {
                        f.printStackTrace();
                    }
                }
            }
        }
    }

    private void squareOnClick() {
        background_on_square.setOnClickListener(view -> {
            isRoundBackgroundOn = !isRoundBackgroundOn;
            setVisDisBackOnSquare();
        });

        b_types_square.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItem().equals(background_types[0])) {
                    which_act_background_round = DEFAULT;
                } else if (adapterView.getSelectedItem().equals(background_types[1])) {
                    which_act_background_round = COLORED;
                } else if (adapterView.getSelectedItem().equals(background_types[2])) {
                    which_act_background_round = ROUND_ACT_BACKGROUNDS.GRADIENT;
                } else {
                    which_act_background_round = ROUND_ACT_BACKGROUNDS.BLUR;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        square_vis_color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItem().equals("Red")) {
                    vis_color = Color.RED;
                } else if (adapterView.getSelectedItem().equals("Blue")) {
                    vis_color = Color.BLUE;
                } else if (adapterView.getSelectedItem().equals("Pink")) {
                    vis_color = Color.parseColor("#ffc0cb");
                } else if (adapterView.getSelectedItem().equals("Green")) {
                    vis_color = Color.GREEN;
                } else if (adapterView.getSelectedItem().equals("Violet")) {
                    vis_color = Color.parseColor("#8f00ff");
                } else if (adapterView.getSelectedItem().equals("Orange")) {
                    vis_color = Color.parseColor("#ffa500");
                } else {
                    vis_color = ACCENT_COLOR;
                }
                setSquareVisulizerColor();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        choose_bottom_r_group.setOnCheckedChangeListener((radioGroup, i) -> {
            int id = radioGroup.getCheckedRadioButtonId();
            if (id == R.id.lyrics_radio_btn) {
                visulizer_constraint.setVisibility(View.GONE);
                is_visulizer_constraint_visible = false;
            } else if (id == R.id.visulizer_radio_btn) {
                visulizer_constraint.setVisibility(View.VISIBLE);
                is_visulizer_constraint_visible = true;
                defaultSquareVisulizer();
            }
        });
    }

    private void setSquareVisulizerColor() {
        square_visulizer.setColor(vis_color);
    }

//  Visible Disable Methods
    private void setVisDisBackOnRound() {
        if (isRoundBackgroundOn) {
            spinner_b_types.setVisibility(View.VISIBLE);
            background_type_txt_round.setVisibility(View.VISIBLE);
        } else {
            spinner_b_types.setVisibility(View.GONE);
            background_type_txt_round.setVisibility(View.GONE);
        }
    }

    private void setVisDisBackOnSquare() {
        if (isRoundBackgroundOn) {
            b_types_square.setVisibility(View.VISIBLE);
            background_type_txt_square.setVisibility(View.VISIBLE);
        } else {
            b_types_square.setVisibility(View.GONE);
            background_type_txt_square.setVisibility(View.GONE);
        }
    }

    /*  Service Connection  */
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        BackService.LocalBinder binder = (BackService.LocalBinder) iBinder;
        service = binder.getService();
        if (which_play_activity == ROUND) {
            defaultRoundVisulizer();
        } else {
            defaultSquareVisulizer();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }
}