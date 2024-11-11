package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.project.activity.PlayerDesignActivity.vis_color;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.GRADIENT_THEME_ORIENTATION;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.GRADIENT_THEME_SELECTED_GRADIENT_COLOR;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.IMAGE_THEME_SELECTED_IMAGE;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.IS_PREVIOUS_THEME_NOW;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.SOLID_THEME_SELECTED_COLOR;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_GRADIENT_SELECTED_GRADIENT;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_IMAGE_SELECTED_IMAGE;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_NORMAL_SELECTED_NORMAL;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_PREFERENCE;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_SOLID_SELECTED_COLOR;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_TYPE_PREFERENCE;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.THEME_TYPE;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.extra_stuffes.CacheImageManager;
import com.sanket_satpute_20.playme.project.recycler_views.ThemeSolidRecycle;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ThemesActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1110;
    String hexColor;

    int clicked_normal = 0;

    public static final String NORMAL_THEME = "NORMAL_THEME";
    public static final String SOLID_THEME = "SOLID_THEME";
    public static final String IMAGE_THEME = "IMAGE_THEME";
    public static final String GRADIENT_THEME = "GRADIENT_THEME";

    ImageView back_pressed;

    /*  accent color    */
    RelativeLayout red, green, blue, pink, orange, other_color;
    RadioButton red_btn, green_btn, blue_btn, pink_btn, orange_btn;

    /*  themes  */
    RadioGroup radioGroup_themes;
    RadioButton normal, solid, image, gradient;

    boolean visulizer_color_changing;

    MaterialButton select_btn;

    MaterialCardView solid_card_another_color, image_card_another_image, gradient_card_anther_card;

    /*  constraints */
    ConstraintLayout normal_constraint, solid_constraint, image_constraint, gradient_constraint, background_theme_activity;

    /*  recycler views  */
    RecyclerView solid_recycler_view, image_recycler_view, gradient_recycler_view;

    /*  image views */
    ImageView solid_add_other_color, image_add_other_image, gradient_add_other_gradient;

    /*  tab layout  */
    TabLayout normal_theme_tab_layout;

    int []colors_arr;
    int recreation_time = 0;

    SharedPreferences theme_preference;
    SharedPreferences.Editor theme_preference_editor;


    /*  gradient views  */
    MaterialCardView first_color_add_card, second_color_add_card, third_color_add_card;
    ImageView first_color_image_add, second_color_image_add, third_color_image_add;
    FloatingActionButton third_color_remove_add_fab;
    MaterialButton set_gradient, cancel_gradient_dialogue;
    ChipGroup gradient_orientation_selector_chip_group;

    String dex_color;
    int gradient_first_color = 0xffffffff, gradient_second_color = 0xffffffff, gradient_third_color = 0xffffffff;
    boolean isThirdColorEnable = true;
    boolean first_gradient_selected = false, second_gradient_selected = false, third_gradient_selected = false;
    public static boolean is_theme_changed = false;

    private final BroadcastReceiver reciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals("theme_broadcast_receiver.Selected.Color")) {
                    if (theme_preference == null)
                        theme_preference = getSharedPreferences(THEME_PREFERENCE, MODE_PRIVATE);
                    switch (theme_preference.getString(THEME_TYPE_PREFERENCE, SOLID_THEME)) {
                        case SOLID_THEME:
                            saveSolidTheme();
                            break;
                        case IMAGE_THEME:
                            saveImageTheme();
                            break;
                        case GRADIENT_THEME:
                            saveGradientTheme();
                            break;
                        case NORMAL_THEME:
                        default:
                            Log.d("lvm", "case 4");
                            break;
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recreation_time++;
        setFullScreen();
        if (THEME_TYPE.equals(NORMAL_THEME)) {
            setTheme(R.style.Theme_PlayMe);
        } else {
            setTheme(R.style.Theme_PlayMe_Another);
        }
        setContentView(R.layout.activity_themes);
        initViews();
        activityStarted();
        doExtra();
        onClick();
        doAgainExtra();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("theme_broadcast_receiver.Selected.Color");
        LocalBroadcastManager.getInstance(ThemesActivity.this).registerReceiver(reciver, filter);
        theme_preference = getSharedPreferences(THEME_PREFERENCE, MODE_PRIVATE);
        theme_preference_editor = theme_preference.edit();
        checkResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(ThemesActivity.this).unregisterReceiver(reciver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clicked_normal = 0;
    }

    private void activityStarted () {
        if (theme_preference == null)
                theme_preference = getSharedPreferences(THEME_PREFERENCE, MODE_PRIVATE);
        String theme_type = theme_preference.getString(THEME_TYPE_PREFERENCE, NORMAL_THEME);
        switch (theme_type) {
            case NORMAL_THEME:
                break;
            case SOLID_THEME:
                saveSolidTheme();
                break;
            case IMAGE_THEME:
                saveImageTheme();
                break;
            case GRADIENT_THEME:
                saveGradientTheme();
                break;
        }
    }

    private void initViews() {
        /*  activity    */
        back_pressed = findViewById(R.id.back_pressed);

        /*  themes  */
        radioGroup_themes = findViewById(R.id.radio_group_theme);
        normal = findViewById(R.id.normal_radio_btn);
        solid = findViewById(R.id.solid_radio_btn);
        image = findViewById(R.id.image_radio_btn);
        gradient = findViewById(R.id.gradient_radio_btn);

        /*  relative of accent  */
        red = findViewById(R.id.red);
        green = findViewById(R.id.green);
        blue = findViewById(R.id.blue);
        pink = findViewById(R.id.pink);
        orange = findViewById(R.id.orange);
        other_color = findViewById(R.id.other_color);

        /*  radio button    */
        red_btn = findViewById(R.id.red_btn);
        green_btn = findViewById(R.id.green_btn);
        blue_btn = findViewById(R.id.blue_btn);
        pink_btn = findViewById(R.id.pink_btn);
        orange_btn = findViewById(R.id.orange_btn);

        /*  constraints */
        normal_constraint = findViewById(R.id.constraint_normal_theme_radio);
        solid_constraint = findViewById(R.id.constraint_solid_theme_radio);
        image_constraint = findViewById(R.id.constraint_image_theme_radio);
        gradient_constraint = findViewById(R.id.constraint_gradient_theme_radio);

        /*  other layouts   */
        solid_add_other_color = findViewById(R.id.solid_another_color);
        solid_recycler_view = findViewById(R.id.theme_solid_colors_recycler_view);

        /*  image view  */
        image_add_other_image = findViewById(R.id.image_another_image);
        image_recycler_view = findViewById(R.id.theme_images_recycler_view);

        /*  gradient view   */
        gradient_add_other_gradient = findViewById(R.id.gradient_another_gradient);
        gradient_recycler_view = findViewById(R.id.theme_gradient_recycler_view);

        /*  Card views  */
        solid_card_another_color = findViewById(R.id.solid_card_another_color);
        image_card_another_image = findViewById(R.id.image_card_another_image);
        gradient_card_anther_card = findViewById(R.id.gradient_card_another_gradient);

        /*  Background  */
        background_theme_activity = findViewById(R.id.background_theme_activity);

        /*  tab layout  */
        normal_theme_tab_layout = findViewById(R.id.normal_theme_tab_layout);
    }

    private void doExtra() {
        if (ACCENT_COLOR == 0xffff0000) {
            red_btn.setChecked(true);
            green_btn.setChecked(false);
            blue_btn.setChecked(false);
            pink_btn.setChecked(false);
            orange_btn.setChecked(false);
        } else if (ACCENT_COLOR == 0xff00ff00) {
            green_btn.setChecked(true);
            red_btn.setChecked(false);
            blue_btn.setChecked(false);
            pink_btn.setChecked(false);
            orange_btn.setChecked(false);
        } else if (ACCENT_COLOR == 0xff0000ff) {
            blue_btn.setChecked(true);
            red_btn.setChecked(false);
            green_btn.setChecked(false);
            pink_btn.setChecked(false);
            orange_btn.setChecked(false);
        } else if (ACCENT_COLOR == 0xffffc0cb) {
            pink_btn.setChecked(true);
            red_btn.setChecked(false);
            green_btn.setChecked(false);
            blue_btn.setChecked(false);
            orange_btn.setChecked(false);
        } else if (ACCENT_COLOR == 0xfff3a243) {
            orange_btn.setChecked(true);
            pink_btn.setChecked(false);
            red_btn.setChecked(false);
            green_btn.setChecked(false);
            blue_btn.setChecked(false);
        } else {
            red_btn.setChecked(false);
            green_btn.setChecked(false);
            blue_btn.setChecked(false);
            pink_btn.setChecked(false);
            orange_btn.setChecked(false);
        }
    }

    private void checkResume() {
        visulizer_color_changing = ACCENT_COLOR == vis_color;
        switch (THEME_TYPE) {
            case GRADIENT_THEME :
                normal.setChecked(false);
                solid.setChecked(false);
                image.setChecked(false);
                gradient.setChecked(true);
                break;
            case SOLID_THEME :
                normal.setChecked(false);
                solid.setChecked(true);
                image.setChecked(false);
                gradient.setChecked(false);
                break;
            case IMAGE_THEME :
                normal.setChecked(false);
                solid.setChecked(false);
                image.setChecked(true);
                gradient.setChecked(false);
                break;
            case NORMAL_THEME:
            default :
                normal.setChecked(true);
                solid.setChecked(false);
                image.setChecked(false);
                gradient.setChecked(false);
                if (theme_preference == null)
                    theme_preference = getSharedPreferences(THEME_PREFERENCE, MODE_PRIVATE);
                String night_mode_system = theme_preference.getString(THEME_NORMAL_SELECTED_NORMAL, "NIGHT_MODE_SYSTEM");
                if ("NIGHT_MODE_OFF".equals(night_mode_system)) {
                    Objects.requireNonNull(normal_theme_tab_layout.getTabAt(0)).select();
                } else if ("NIGHT_MODE_ON".equals(night_mode_system)) {
                    Objects.requireNonNull(normal_theme_tab_layout.getTabAt(1)).select();
                } else if ("NIGHT_MODE_AUTO".equals(night_mode_system)) {
                    Objects.requireNonNull(normal_theme_tab_layout.getTabAt(3)).select();
                } else {
                    Objects.requireNonNull(normal_theme_tab_layout.getTabAt(2)).select();
                }
                break;
        }
    }

    private void doAgainExtra() {
        colors_arr = ThemesActivity.this.getResources().getIntArray(R.array.colors);
    }

    private void onClick() {
        back_pressed.setOnClickListener(view -> onBackPressed());

        red.setOnClickListener(view -> {
            ACCENT_COLOR = 0xffff0000;
            if (visulizer_color_changing)
                vis_color = ACCENT_COLOR;
            doExtra();
        });

        green.setOnClickListener(view -> {
            ACCENT_COLOR = 0xff00ff00;
            if (visulizer_color_changing)
                vis_color = ACCENT_COLOR;
            doExtra();
        });

        blue.setOnClickListener(view -> {
            ACCENT_COLOR = 0xff0000ff;
            if (visulizer_color_changing)
                vis_color = ACCENT_COLOR;
            doExtra();
        });

        pink.setOnClickListener(view -> {
            ACCENT_COLOR = 0xffffc0cb;
            if (visulizer_color_changing)
                vis_color = ACCENT_COLOR;
            doExtra();
        });

        orange.setOnClickListener(view -> {
            ACCENT_COLOR = 0xfff3a243;
            if (visulizer_color_changing)
                vis_color = ACCENT_COLOR;
            doExtra();
        });

        red_btn.setOnClickListener(view -> {
            ACCENT_COLOR = 0xffff0000;
            if (visulizer_color_changing)
                vis_color = ACCENT_COLOR;
            doExtra();
        });

        green_btn.setOnClickListener(view -> {
            ACCENT_COLOR = 0xff00ff00;
            if (visulizer_color_changing)
                vis_color = ACCENT_COLOR;
            doExtra();
        });

        blue_btn.setOnClickListener(view -> {
            ACCENT_COLOR = 0xff0000ff;
            if (visulizer_color_changing)
                vis_color = ACCENT_COLOR;
            doExtra();
        });

        pink_btn.setOnClickListener(view -> {
            ACCENT_COLOR = 0xffffc0cb;
            if (visulizer_color_changing)
                vis_color = ACCENT_COLOR;
            doExtra();
        });

        orange_btn.setOnClickListener(view -> {
            ACCENT_COLOR = 0xfff3a243;
            if (visulizer_color_changing)
                vis_color = ACCENT_COLOR;
            doExtra();
        });

        if (select_btn != null) {
            select_btn.setOnClickListener(view -> {
                finish();
                Toast.makeText(this, "Not Implemented", Toast.LENGTH_SHORT).show();
            });
        }

        other_color.setOnClickListener(view -> {
            openColorPickerDilog(ACCENT_COLOR, "ACCENT_COLOR");
            setCardColor();
        });

//        themes
        radioGroup_themes.setOnCheckedChangeListener((checks, i) -> {
            final int id_normal = R.id.normal_radio_btn;
            final int id_solid = R.id.solid_radio_btn;
            final int id_image = R.id.image_radio_btn;
            final int id_gradient = R.id.gradient_radio_btn;

            int checkedRadioButtonId = radioGroup_themes.getCheckedRadioButtonId();

            if (checkedRadioButtonId == id_solid) {
                pickedSolid();
            } else if (checkedRadioButtonId == id_image) {
                pickedImage();
            } else if (checkedRadioButtonId == id_gradient) {
                pickedGradient();
            } else {
                pickedNormal();
            }
        });


        normal_theme_tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (clicked_normal > 1)
                    Toast.makeText(ThemesActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
//                String normal_theme_type;
//                switch (tab.getPosition()) {
//                    case 0:
//                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
//                        normal_theme_type = "NIGHT_MODE_OFF";
//                        break;
//                    case 1:
//                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
//                        normal_theme_type = "NIGHT_MODE_ON";
//                        break;
//                    case 3:
//                        DateFormat dfTime = new SimpleDateFormat("HH", Locale.getDefault());
//                        int time = Integer.parseInt(dfTime.format(Calendar.getInstance().getTime()));
//                        if (time >= 19 || time <= 7) {
//                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                        } else {
//                            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
//                        }
//                        normal_theme_type = "NIGHT_MODE_AUTO";
//                        break;
//                    case 2:
//                    default:
////                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM);
//                        normal_theme_type = "NIGHT_MODE_SYSTEM";
//                        break;
//                }
//                THEME_TYPE = NORMAL_THEME;
//                selectNormalTheme(normal_theme_type);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        solid_card_another_color.setOnClickListener(view -> openColorPickerDilog(Color.parseColor("#ffffff"), "THEME"));
        image_card_another_image.setOnClickListener(view -> openImageChooser());
        gradient_card_anther_card.setOnClickListener(view -> gradientCardAnotherGradientClicked());
    }

    private void openColorPickerDilog(int color, String for_what) {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, color, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                hexColor = String.format("#%06X", (0xFFFFFF & color));
                Log.d("g1", ""+hexColor);
                if (for_what.equals("THEME"))
                    addSolidOtherColor();
                else if (for_what.equals("ACCENT_COLOR"))
                    setCardColor();
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // cancel was selected by the user
            }
        });
        dialog.show();
    }

    private void setCardColor() {
        if (hexColor != null) {
            Log.d("g1", "h " + Color.parseColor(hexColor));
            ACCENT_COLOR = Color.parseColor(hexColor);
        }
    }

    private void pickedNormal() {
        clicked_normal ++;
        normal_constraint.setVisibility(View.VISIBLE);
        solid_constraint.setVisibility(View.GONE);
        image_constraint.setVisibility(View.GONE);
        gradient_constraint.setVisibility(View.GONE);
        is_theme_changed = true;
        THEME_TYPE = NORMAL_THEME;
        if (theme_preference == null)
            theme_preference = getSharedPreferences(THEME_PREFERENCE, MODE_PRIVATE);
        if (theme_preference_editor == null)
            theme_preference_editor = theme_preference.edit();
        theme_preference_editor.putString(THEME_TYPE_PREFERENCE, THEME_TYPE);
        theme_preference_editor.apply();
        if (clicked_normal > 1) {
            recreate();
        }
    }

    private void pickedSolid() {
        normal_constraint.setVisibility(View.GONE);
        solid_constraint.setVisibility(View.VISIBLE);
        image_constraint.setVisibility(View.GONE);
        gradient_constraint.setVisibility(View.GONE);
        pickedSolidExtras();
    }

    private void pickedImage() {
        normal_constraint.setVisibility(View.GONE);
        solid_constraint.setVisibility(View.GONE);
        image_constraint.setVisibility(View.VISIBLE);
        gradient_constraint.setVisibility(View.GONE);
        pickedImageExtras();
    }

    private void pickedGradient() {
        normal_constraint.setVisibility(View.GONE);
        solid_constraint.setVisibility(View.GONE);
        image_constraint.setVisibility(View.GONE);
        gradient_constraint.setVisibility(View.VISIBLE);
        pickedGradientExtras();
    }

//    Extra Methods
    private void pickedSolidExtras() {
        if (colors_arr == null)
            colors_arr = ThemesActivity.this.getResources().getIntArray(R.array.colors);
        if (!THEME_TYPE.equals(SOLID_THEME)) {
            SOLID_THEME_SELECTED_COLOR = 0;
        }
        ThemeSolidRecycle adapter = new ThemeSolidRecycle(colors_arr, this, SOLID_THEME_SELECTED_COLOR);
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        solid_recycler_view.setHasFixedSize(true);
        solid_recycler_view.setAdapter(adapter);
        solid_recycler_view.setLayoutManager(layout);
    }

    private void pickedImageExtras () {
        ArrayList<String> images_url_arr = new ArrayList<>();

        images_url_arr.add("https://cdn.statusqueen.com/mobilewallpaper/thumbnail/moon_and_me-1012.jpg");
        images_url_arr.add("https://i.pinimg.com/736x/53/41/79/534179c1fc9077c6296e9cf82430be42.jpg");
        images_url_arr.add("https://cdn.bhdw.net/im/tom-and-jerry-wallpaper-71416_w635.webp");
        images_url_arr.add("https://www.finetoshine.com/wp-content/uploads/2020/08/Best-hd-wallpapers-hd-wallpapers-wallpapers-for-Android.jpg");
        images_url_arr.add("https://w0.peakpx.com/wallpaper/35/1004/HD-wallpaper-bgmi-cloud-sky-game-most-popular-game-android-dark-pubg.jpg");
        images_url_arr.add("https://wallpaperaccess.com/full/1353883.jpg");

        ThemeSolidRecycle adapter = new ThemeSolidRecycle(images_url_arr, this, SOLID_THEME_SELECTED_COLOR);
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        image_recycler_view.setHasFixedSize(true);
        image_recycler_view.setAdapter(adapter);
        image_recycler_view.setLayoutManager(layout);
    }

    private void pickedGradientExtras () {
        ArrayList<int[]> gradient_colors = new ArrayList<>();

        gradient_colors.add(new int[] {0xffff1063, 0xffd2e65b, 0xff3abadc});
        gradient_colors.add(new int[] {0xffffafbd, 0xffffc3a0});
        gradient_colors.add(new int[] {0xffee9ca7, 0xffffdde1});
        gradient_colors.add(new int[] {0xffde6262, 0xffffb88c});
        gradient_colors.add(new int[] {0xff06beb6, 0xff48b1bf});
        gradient_colors.add(new int[] {0xff2193b0, 0xff6dd5ed});

        ThemeSolidRecycle adapter = new ThemeSolidRecycle(this, SOLID_THEME_SELECTED_COLOR, gradient_colors);
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        gradient_recycler_view.setHasFixedSize(true);
        gradient_recycler_view.setAdapter(adapter);
        gradient_recycler_view.setLayoutManager(layout);
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    private void addSolidOtherColor() {
        String previous_theme_name;
        THEME_TYPE = SOLID_THEME;

        if (theme_preference == null)
            theme_preference = getSharedPreferences(THEME_PREFERENCE, MODE_PRIVATE);
        previous_theme_name = theme_preference.getString(THEME_TYPE_PREFERENCE, NORMAL_THEME);
        if (theme_preference_editor == null)
            theme_preference_editor = theme_preference.edit();
        theme_preference_editor.putString(THEME_TYPE_PREFERENCE, SOLID_THEME);
        theme_preference_editor.apply();
        theme_preference_editor.putInt(THEME_SOLID_SELECTED_COLOR, Color.parseColor(hexColor));
        theme_preference_editor.apply();
        theme_preference_editor.putBoolean(IS_PREVIOUS_THEME_NOW, previous_theme_name.equals(NORMAL_THEME));
        theme_preference_editor.apply();

        is_theme_changed = true;
        if (hexColor != null) {
            selectedSolidTheme(Color.parseColor(hexColor), theme_preference.getString(THEME_TYPE_PREFERENCE, NORMAL_THEME).equals(NORMAL_THEME));
        }
    }

/*    private void selectNormalTheme (String normal_active_theme_name) {
        is_theme_changed = true;
        THEME_TYPE = NORMAL_THEME;
        NORMAL_THEME_SELECTED_WHICH = normal_active_theme_name;
        if (theme_preference == null)
            theme_preference = getSharedPreferences(THEME_PREFERENCE, MODE_PRIVATE);
        if (theme_preference_editor == null)
            theme_preference_editor = theme_preference.edit();
        theme_preference_editor.putString(THEME_TYPE_PREFERENCE, THEME_TYPE);
        theme_preference_editor.apply();
        theme_preference_editor.putString(THEME_NORMAL_SELECTED_NORMAL, NORMAL_THEME_SELECTED_WHICH);
        theme_preference_editor.apply();
        recreate();
    }*/

    private void selectedSolidTheme (int theme_back_color, boolean recreation) {
        background_theme_activity.setBackgroundColor(theme_back_color);
        if (recreation && recreation_time % 2 == 0) {
            recreate();
            Log.d("lg", "recreating");
        } else {
            Log.d("lg", "not creating");
        }
    }

    private void selectedImageTheme (Bitmap bitmap, boolean recreation) {
        background_theme_activity.setBackgroundColor(Color.parseColor("#00000000"));
        background_theme_activity.setBackground(new BitmapDrawable(getResources(), bitmap));
        if (recreation && recreation_time % 2 == 0) {
            recreate();
            Log.d("lg", "recreating");
        } else {
            Log.d("lg", "not creating");
        }
    }

    private void selectGradientTheme (GradientDrawable drawable, boolean recreation) {
        background_theme_activity.setBackgroundColor(Color.parseColor("#00000000"));
        background_theme_activity.setBackground(drawable);
        if (recreation && recreation_time % 2 == 0) {
            recreate();
            Log.d("lg", "recreating");
        } else {
            Log.d("lg", "not creating");
        }
    }

    private void gradientCardAnotherGradientClicked () {
        ColorStateList tint_background, tint_foreground;
//        view
        View view = LayoutInflater.from(ThemesActivity.this).inflate(R.layout.select_theme_graident_add_another_theme_activity, null);
//        inflate views
        first_color_add_card = view.findViewById(R.id.first_color_card);
        second_color_add_card = view.findViewById(R.id.second_color_card);
        third_color_add_card = view.findViewById(R.id.third_color_card);

        first_color_image_add = view.findViewById(R.id.first_color_image);
        second_color_image_add = view.findViewById(R.id.second_color_image);
        third_color_image_add = view.findViewById(R.id.third_color_image);

        third_color_remove_add_fab = view.findViewById(R.id.remove_and_add_third_color_btn);

        set_gradient = view.findViewById(R.id.set_gradient_btn);
        cancel_gradient_dialogue = view.findViewById(R.id.cancel_btn);

        gradient_orientation_selector_chip_group = view.findViewById(R.id.gradient_orientation_chip_group);

        tint_background = third_color_add_card.getBackgroundTintList();
        tint_foreground = third_color_image_add.getImageTintList();

        MaterialAlertDialogBuilder dilog = new MaterialAlertDialogBuilder(ThemesActivity.this);
        dilog.setView(view);
        AlertDialog alert = dilog.create();
        alert.show();

        first_color_add_card.setOnClickListener(v -> openColorPickerDilogGradient(1));
        second_color_add_card.setOnClickListener(v -> openColorPickerDilogGradient(2));
        third_color_add_card.setOnClickListener(v -> openColorPickerDilogGradient(3));
        third_color_remove_add_fab.setOnClickListener(v -> {
            if (isThirdColorEnable) {
                third_color_add_card.setAlpha(0.2f);
                third_color_image_add.setAlpha(0.2f);
                third_color_add_card.setClickable(false);
                third_color_remove_add_fab.setImageResource(R.drawable.ic_round_addition_24);
                if (third_gradient_selected) {
                    if (tint_background != null && tint_foreground != null) {
                        third_color_image_add.setVisibility(View.VISIBLE);
                        third_color_add_card.setBackgroundTintList(tint_background);
                        third_color_image_add.setImageTintList(tint_foreground);
                    }
                }
            } else {
                third_color_add_card.setAlpha(1f);
                third_color_image_add.setAlpha(1f);
                third_color_add_card.setClickable(true);
                third_color_remove_add_fab.setImageResource(R.drawable.ic_round_remove_minus_24);
                third_color_image_add.setVisibility(View.VISIBLE);
            }
            isThirdColorEnable = !isThirdColorEnable;
        });

        cancel_gradient_dialogue.setOnClickListener(b -> alert.dismiss());
        set_gradient.setOnClickListener(b -> {
            String previous_theme_name;
            if (theme_preference == null)
                theme_preference = getSharedPreferences(THEME_PREFERENCE, MODE_PRIVATE);
            previous_theme_name = theme_preference.getString(THEME_TYPE_PREFERENCE, NORMAL_THEME);
            if (theme_preference_editor == null)
                theme_preference_editor = theme_preference.edit();

            if (isThirdColorEnable) {
                if (first_gradient_selected && second_gradient_selected && third_gradient_selected) {
                    int []gradient_colors = new int[]{Color.parseColor(String.format("#%06X", (0xFFFFFF & gradient_first_color))),
                            Color.parseColor(String.format("#%06X", (0xFFFFFF & gradient_second_color))),
                            Color.parseColor(String.format("#%06X", (0xFFFFFF & gradient_third_color)))};
                    int gradient_orientation = (gradient_orientation_selector_chip_group != null)
                            ? gradient_orientation_selector_chip_group.getCheckedChipId() : R.id.chip_t_to_b;
                    GradientDrawable draw = new GradientDrawable(
                            getGradientOrientation(gradient_orientation)
                            , gradient_colors);
                    background_theme_activity.setBackground(draw);


                    is_theme_changed = true;
                    List<int[]> list = new ArrayList<>();
                    list.add(gradient_colors);
                    Gson gson = new Gson();
                    String t_gradient = gson.toJson(list);
                    theme_preference_editor.putString(THEME_TYPE_PREFERENCE, THEME_TYPE);
                    theme_preference_editor.apply();
                    theme_preference_editor.putString(THEME_GRADIENT_SELECTED_GRADIENT, t_gradient);
                    theme_preference_editor.apply();
                    theme_preference_editor.putString(GRADIENT_THEME_ORIENTATION, saveGradientPreference(gradient_orientation));
                    theme_preference_editor.apply();
                    theme_preference_editor.putBoolean(IS_PREVIOUS_THEME_NOW, previous_theme_name.equals(NORMAL_THEME));
                    theme_preference_editor.apply();
                    alert.dismiss();
                } else {
                    Toast.makeText(this, "Select All Color's", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (first_gradient_selected && second_gradient_selected) {
                    int gradient_orientation = (gradient_orientation_selector_chip_group != null)
                            ? gradient_orientation_selector_chip_group.getCheckedChipId() : R.id.chip_t_to_b;
                    int []gradient_colors = new int[] {Color.parseColor(String.format("#%06X", (0xFFFFFF & gradient_first_color))),
                            Color.parseColor(String.format("#%06X", (0xFFFFFF & gradient_second_color))),
                    };
                    GradientDrawable draw = new GradientDrawable(getGradientOrientation(gradient_orientation), gradient_colors);
                    background_theme_activity.setBackground(draw);

                    is_theme_changed = true;
                    List<int[]> list = new ArrayList<>();
                    list.add(gradient_colors);
                    Gson gson = new Gson();
                    String t_gradient = gson.toJson(list);
                    theme_preference_editor.putString(THEME_TYPE_PREFERENCE, THEME_TYPE);
                    theme_preference_editor.apply();
                    theme_preference_editor.putString(THEME_GRADIENT_SELECTED_GRADIENT, t_gradient);
                    theme_preference_editor.apply();
                    theme_preference_editor.putString(GRADIENT_THEME_ORIENTATION, saveGradientPreference(gradient_orientation));
                    theme_preference_editor.apply();
                    theme_preference_editor.putBoolean(IS_PREVIOUS_THEME_NOW, previous_theme_name.equals(NORMAL_THEME));
                    theme_preference_editor.apply();
                    alert.dismiss();
                } else {
                    Toast.makeText(this, "Select All Color's", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openColorPickerDilogGradient(int card_position) {

        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, -1, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                dex_color = String.format("#%06X", (0xFFFFFF & color));
                setColorToGradientCards(dex_color, card_position);
            }
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // cancel was selected by the user
            }
        });
        dialog.show();
    }

    private void setColorToGradientCards(String color, int card_position) {
        int gradient_card_one_color = Color.parseColor(color);
        if (card_position == 1 && first_color_add_card != null && first_color_image_add != null) {
            first_gradient_selected = true;
            gradient_first_color = gradient_card_one_color;
            first_color_image_add.setVisibility(View.INVISIBLE);
            first_color_add_card.setBackgroundTintList(ColorStateList.valueOf(gradient_card_one_color));
        } else if (card_position == 2 && second_color_add_card != null && second_color_image_add != null) {
            second_gradient_selected = true;
            gradient_second_color = gradient_card_one_color;
            second_color_image_add.setVisibility(View.INVISIBLE);
            second_color_add_card.setBackgroundTintList(ColorStateList.valueOf(gradient_card_one_color));
        } else if (card_position == 3 && third_color_add_card != null && third_color_image_add != null) {
            third_gradient_selected = true;
            gradient_third_color = gradient_card_one_color;
            third_color_image_add.setVisibility(View.INVISIBLE);
            third_color_add_card.setBackgroundTintList(ColorStateList.valueOf(gradient_card_one_color));
        }
    }

    private GradientDrawable.Orientation getGradientOrientation (int selected_chip_id) {
        GradientDrawable.Orientation orientation = null;
        final int b_to_t = R.id.chip_b_to_t;
        final int r_to_l = R.id.chip_r_to_l;
        final int l_to_r = R.id.chip_l_to_r;
        final int tl_to_Br = R.id.chip_tl_to_Br;
        final int tr_to_bl = R.id.chip_tr_to_bl;
        final int bl_to_tr = R.id.chip_bl_to_tr;
        final int br_to_tl = R.id.chip_br_to_Tl;
        final int t_to_b = R.id.chip_t_to_b;

        if (selected_chip_id == b_to_t) {
            orientation = GradientDrawable.Orientation.BOTTOM_TOP;
        } else if (selected_chip_id == r_to_l) {
            orientation = GradientDrawable.Orientation.RIGHT_LEFT;
        } else if (selected_chip_id == l_to_r) {
            orientation = GradientDrawable.Orientation.LEFT_RIGHT;
        } else if (selected_chip_id == tl_to_Br) {
            orientation = GradientDrawable.Orientation.TL_BR;
        } else if (selected_chip_id == tr_to_bl) {
            orientation = GradientDrawable.Orientation.TR_BL;
        } else if (selected_chip_id == bl_to_tr) {
            orientation = GradientDrawable.Orientation.BL_TR;
        } else if (selected_chip_id == br_to_tl) {
            orientation = GradientDrawable.Orientation.BR_TL;
        } else {
            orientation = GradientDrawable.Orientation.TOP_BOTTOM;
        }
        return orientation;
    }

    /*  activity result */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE && data != null) {
                Uri selected_image_uri = data.getData();
                if (selected_image_uri != null) {
                    Bitmap selected_image_bitmap;
                    try {
                        selected_image_bitmap = MediaStore.Images.Media.getBitmap(
                                this.getContentResolver(), selected_image_uri
                        );
                        File file = new File(selected_image_uri.toString());
                        IMAGE_THEME_SELECTED_IMAGE = selected_image_bitmap;
                        if (CacheImageManager.getImage(ThemesActivity.this, file.getName()) == null) {
                            CacheImageManager.putImage(ThemesActivity.this, file.getName(), selected_image_bitmap);
                        }

                        THEME_TYPE = IMAGE_THEME;
                        if (theme_preference == null)
                            theme_preference = getSharedPreferences(THEME_PREFERENCE, MODE_PRIVATE);
                        if (theme_preference_editor == null)
                            theme_preference_editor = theme_preference.edit();
                        theme_preference_editor.putString(THEME_TYPE_PREFERENCE, IMAGE_THEME);
                        theme_preference_editor.apply();
                        theme_preference_editor.putString(THEME_IMAGE_SELECTED_IMAGE, file.getName());
                        theme_preference_editor.apply();
                        background_theme_activity.setBackground(new BitmapDrawable(getResources(), selected_image_bitmap));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "File Not Found" , Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to set Image", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    @SuppressLint("NonConstantResourceId")
    private String saveGradientPreference(int id) {
        String theme_orientation = "T_TO_B";

        if (id == R.id.chip_t_to_b) {
            theme_orientation = "B_TO_T";
        } else if (id == R.id.chip_l_to_r) {
            theme_orientation = "L_TO_R";
        } else if (id == R.id.chip_r_to_l) {
            theme_orientation = "R_TO_L";
        } else if (id == R.id.chip_tr_to_bl) {
            theme_orientation = "TR_TO_BL";
        } else if (id == R.id.chip_tl_to_Br) {
            theme_orientation = "TL_TO_BR";
        } else if (id == R.id.chip_br_to_Tl) {
            theme_orientation = "BR_TO_TL";
        } else if (id == R.id.chip_bl_to_tr) {
            theme_orientation = "BL_TO_TR";
        }
        return theme_orientation;
    }

    private void saveSolidTheme() {
        int theme_child_solid = theme_preference.getInt(THEME_SOLID_SELECTED_COLOR, 0xff999999);
        selectedSolidTheme(theme_child_solid, theme_preference.getBoolean(IS_PREVIOUS_THEME_NOW, true));
    }


    private void saveImageTheme() {
        if (theme_preference == null)
            theme_preference = getSharedPreferences(THEME_PREFERENCE, MODE_PRIVATE);

        String theme_child_image = theme_preference.getString(THEME_IMAGE_SELECTED_IMAGE, null);
        if (theme_child_image != null) {
            if (CacheImageManager.getImage(this, theme_child_image) != null)
                selectedImageTheme(CacheImageManager.getImage(this, theme_child_image), theme_preference.getBoolean(IS_PREVIOUS_THEME_NOW, true));
            else
                Toast.makeText(this, "T 1", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "T 2", Toast.LENGTH_SHORT).show();
    }

    private void saveGradientTheme() {
        Gson gson = new Gson();
        GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TOP_BOTTOM;
        String theme_child_gradient = theme_preference.getString(THEME_GRADIENT_SELECTED_GRADIENT, null);
        switch (theme_preference.getString(GRADIENT_THEME_ORIENTATION, "T_TO_B")) {
            case "B_TO_T":
                orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            case "L_TO_R":
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            case "R_TO_L":
                orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                break;
            case "TR_TO_BL":
                orientation = GradientDrawable.Orientation.TR_BL;
                break;
            case "TL_TO_BR":
                orientation = GradientDrawable.Orientation.TL_BR;
                break;
            case "BR_TO_TL":
                orientation = GradientDrawable.Orientation.BR_TL;
                break;
            case "BL_TO_TR":
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
        }
        Type type_gradient = new TypeToken<List<int[]>>() {}.getType();
        List<int[]> gradient = gson.fromJson(theme_child_gradient, type_gradient);
        GradientDrawable drawable = new GradientDrawable(orientation, gradient.get(0));
        GRADIENT_THEME_SELECTED_GRADIENT_COLOR = drawable;
        selectGradientTheme(drawable, theme_preference.getBoolean(IS_PREVIOUS_THEME_NOW, true));
    }

    private void setFullScreen() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(params);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }
}