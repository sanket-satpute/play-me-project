package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__IS_PLAY_ME_BASS_BOOST_ON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__IS_PLAY_ME_EQUALIZER_ON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__IS_PLAY_ME_LOUDNESS_ENHANCER_ON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.__IS_PLAY_ME_VIRTUALIZER_ON;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.BassBoost;
import android.media.audiofx.DynamicsProcessing;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.Virtualizer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.slider.Slider;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.service.BackService;

import java.util.Arrays;
import java.util.List;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class EqulizerActivity extends AppCompatActivity implements ServiceConnection {

//    in on focus set repeat equlizer

    BackService service;

//    views
    Slider eq_seek_4, eq_seek_2, eq_seek_3, eq_seek_0, eq_seek_1, preset_eq, dynamic_processing_eq, environmental_eq;
    CircularSeekBar bass_boost_eq, virtualize_eq, loudness_eq;
    Spinner presets_spinner;
    ConstraintLayout constraint_frame_layout_eq;
    SwitchCompat main_switch, bass_boost_switch, virtualizer_switch, sound_booster_switch;
    ChipGroup reverb_chips;
    ImageView back_pressed, device_equalizer;
    NestedScrollView nested_scroll_view;
    TextView []eq_main_txt = new TextView[5];
    TextView []eq_changer_txt = new TextView[5];

    //    variables
    int higher_level, minimum_level;
    boolean[] isSpeakerOnArr = {false, false, false, false, false, false, false, true};

//    audio effects
    Equalizer equalizer;
    BassBoost bassBoost;
    Virtualizer virtualizer;
    LoudnessEnhancer loudnessEnhancer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equlizer);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(EqulizerActivity.this, BackService.class);
        bindService(intent, EqulizerActivity.this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(EqulizerActivity.this);
    }

    private void initViews() {
        back_pressed = findViewById(R.id.back_pressed);
        device_equalizer = findViewById(R.id.parent_equlizer_system);
        main_switch = findViewById(R.id.equlizer_on_off_switch);

        eq_seek_0 = findViewById(R.id.eq_seek_0);
        eq_seek_1 = findViewById(R.id.eq_seek_1);
        eq_seek_2 = findViewById(R.id.eq_seek_2);
        eq_seek_3 = findViewById(R.id.eq_seek_3);
        eq_seek_4 = findViewById(R.id.eq_seek_4);

        bass_boost_switch = findViewById(R.id.bass_boost_switch);
        virtualizer_switch = findViewById(R.id.virtualizer_switch);
        sound_booster_switch = findViewById(R.id.loudness_enhancer_switch);

        bass_boost_eq = findViewById(R.id.bass_seekbar);
        virtualize_eq = findViewById(R.id.virtualizer_seekbar);
        loudness_eq = findViewById(R.id.loudness_seekbar);
        reverb_chips = findViewById(R.id.reverb_chips);

        nested_scroll_view = findViewById(R.id.nested_scroll_view);

//        preset_eq = findViewById(R.id.preset_reverb_seekbar);
//        dynamic_processing_eq = findViewById(R.id.dynamic_proccesing_seekbar);
//        environmental_eq = findViewById(R.id.enviroment_reverb_seekbar);

        presets_spinner = findViewById(R.id.presets_spinner);
        constraint_frame_layout_eq = findViewById(R.id.constraint_frame_layout_eq);
    }

    private void on_click() {
//        ArrayList<String> preset_array = new ArrayList<>(R.array.equlizer_presets);
//        ArrayAdapter<String> preset_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, preset_array);
//        presets_spinner.setAdapter(preset_adapter);
//        presets_spinner.setSelection(preset_array.size());

        nested_scroll_view.setOnTouchListener((view, motionEvent) -> {
            view.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });

        eq_seek_0.setOnTouchListener((view, motionEvent) -> {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        eq_seek_1.setOnTouchListener((view, motionEvent) -> {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        eq_seek_2.setOnTouchListener((view, motionEvent) -> {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        eq_seek_3.setOnTouchListener((view, motionEvent) -> {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        eq_seek_4.setOnTouchListener((view, motionEvent) -> {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        back_pressed.setOnClickListener(view -> finish());

        device_equalizer.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, service.getAudioSessionId());
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, EqulizerActivity.this.getPackageName());
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                launchSystemEqualizerActivity.launch(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(EqulizerActivity.this, "Failed to start Equalizer", Toast.LENGTH_SHORT).show();
            }
        });

        main_switch.setOnCheckedChangeListener((i, j) -> {
            __IS_PLAY_ME_EQUALIZER_ON = j;
            setEqualizer();
        });

        bass_boost_switch.setOnCheckedChangeListener((i, j) -> {
            __IS_PLAY_ME_BASS_BOOST_ON = j;
            setBassBoost();
        });

        virtualizer_switch.setOnCheckedChangeListener((i, j) -> {
            __IS_PLAY_ME_VIRTUALIZER_ON = j;
            setVirtualizer();
        });

        sound_booster_switch.setOnCheckedChangeListener((i, j) -> {
            __IS_PLAY_ME_LOUDNESS_ENHANCER_ON = j;
            setLoudnessEnhancement();
        });

        virtualize_eq.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
//                e.setStrength((short)progress);
                virtualizer.setStrength((short) progress);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {}
        });

        bass_boost_eq.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                // Adjust bass boost strength based on SeekBar progress
                bassBoost.setStrength((short) progress);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {}
        });

        loudness_eq.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                loudnessEnhancer.setTargetGain((int) progress);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {}
        });

        eq_seek_0.addOnChangeListener((slider, value, fromUser) -> {
            equalizer.setBandLevel((short) 0, (short) value);
            String frequency_db = Math.round((value / 100)) + " dB";
            eq_changer_txt[0].setText(frequency_db);
        });

        eq_seek_1.addOnChangeListener((slider, value, fromUser) -> {
            equalizer.setBandLevel((short) 1, (short) value);
            String frequency_db = Math.round((value / 100)) + " dB";
            eq_changer_txt[1].setText(frequency_db);
        });

        eq_seek_2.addOnChangeListener((slider, value, fromUser) -> {
            equalizer.setBandLevel((short) 2, (short) value);
            String frequency_db = Math.round((value / 100)) + " dB";
            eq_changer_txt[2].setText(frequency_db);
        });

        eq_seek_3.addOnChangeListener((slider, value, fromUser) -> {
            equalizer.setBandLevel((short) 3, (short) value);
            String frequency_db = Math.round((value / 100)) + " dB";
            eq_changer_txt[3].setText(frequency_db);
        });

        eq_seek_4.addOnChangeListener((slider, value, fromUser) -> {
            equalizer.setBandLevel((short) 4, (short) value);
            String frequency_db = Math.round((value / 100)) + " dB";
            eq_changer_txt[4].setText(frequency_db);
        });
    }

    //    on result activity
    ActivityResultLauncher<Intent> launchSystemEqualizerActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
//              don't do anything
            });

    private AudioTrack audioTrack;
    private final int sampleRate = 44100; // Use your desired sample rate
    private final int bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_7POINT1_SURROUND, AudioFormat.ENCODING_PCM_FLOAT);

    private void doExtra3DSurround() {
        // Enable 3D audio effect using AudioEffects API
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//            AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                    .setUsage(AudioAttributes.USAGE_MEDIA)
//                    .build();
//
//            service.mediaPlayer.setAudioAttributes(audioAttributes);
//            Log.d("h100", "greater than q");
//        } else {
//            service.mediaPlayer.setAudioStreamType(AudioTrack.MODE_STREAM);
//            Log.d("h100", "less than q");
//        }
//
//        BassBoost booster = new BassBoost(0, service.getAudioSessionId());//-->tried with sessionid also
//        booster.setStrength((short) 1000);
//        booster.setEnabled(true);
//        service.mediaPlayer.attachAuxEffect(booster.getId());
//        service.mediaPlayer.setAuxEffectSendLevel(1.0f);

        int channelConfig = AudioFormat.CHANNEL_OUT_7POINT1_SURROUND; // 8 speakers
        int audioFormat = AudioFormat.ENCODING_PCM_FLOAT; // Use FLOAT for better precision
        int sampleRate = 44100; // Use your desired sample rate
        int bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        // Initialize the AudioTrack with multi-channel support for 8 speakers
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                AudioFormat.CHANNEL_OUT_7POINT1_SURROUND, AudioFormat.ENCODING_PCM_FLOAT,
                bufferSize, AudioTrack.MODE_STREAM);
    }

    private float calculateSpeakerGain(float azimuth, float elevation) {
        // Define listener's position (azimuth and elevation)
        float listenerAzimuth = 0.0f; // Listener facing forward (front)
        float listenerElevation = 0.0f; // Listener at the same elevation

        // Distance between the listener and the speaker (in meters)
        float distance = calculateDistance(azimuth, elevation, listenerAzimuth, listenerElevation);

        // Define maximum distance at which the sound is audible (in meters)
        float maxAudibleDistance = 10.0f;

        // Calculate gain based on distance attenuation
        float gain = 1.0f - (distance / maxAudibleDistance); // Linear attenuation

        // Clamp gain to be between 0.0f and 1.0f
        gain = Math.max(gain, 0.0f);
        gain = Math.min(gain, 1.0f);

        return gain;
    }

    // Helper method to calculate distance between two points in 3D space
    private float calculateDistance(float azimuth1, float elevation1, float azimuth2, float elevation2) {
        // Convert azimuth and elevation to Cartesian coordinates
        float x1 = (float) (Math.cos(Math.toRadians(azimuth1)) * Math.cos(Math.toRadians(elevation1)));
        float y1 = (float) (Math.sin(Math.toRadians(azimuth1)) * Math.cos(Math.toRadians(elevation1)));
        float z1 = (float) Math.sin(Math.toRadians(elevation1));

        float x2 = (float) (Math.cos(Math.toRadians(azimuth2)) * Math.cos(Math.toRadians(elevation2)));
        float y2 = (float) (Math.sin(Math.toRadians(azimuth2)) * Math.cos(Math.toRadians(elevation2)));
        float z2 = (float) Math.sin(Math.toRadians(elevation2));

        // Calculate Euclidean distance between the two points
        float distance = (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) + (z2 - z1) * (z2 - z1));

        return distance;
    }


    // Method to update the audio data for each speaker in the main audio rendering loop
    private float[] updateAudioData() {
        float[] audioData = new float[bufferSize * 8]; // 8 channels for 8 speakers

        // Speaker positions in 3D space (azimuth, elevation)
        float[][] speakerPositions = {
                {0.0f, 0.0f},     // Front
                {45.0f, 0.0f},    // Front-right
                {90.0f, 0.0f},    // Right
                {135.0f, 0.0f},   // Rear-right
                {180.0f, 0.0f},   // Rear
                {225.0f, 0.0f},   // Rear-left
                {270.0f, 0.0f},   // Left
                {315.0f, 0.0f}    // Front-left
        };

        for (int i = 0; i < 8; i++) {
            if (isSpeakerOnArr[i]) {
                // Calculate the gain (volume) for the specific speaker based on their position
                float azimuth = speakerPositions[i][0];
                float elevation = speakerPositions[i][1];

                // Use azimuth and elevation to adjust the gain for this speaker
                float gain = calculateSpeakerGain(azimuth, elevation);

                // Apply the gain to the corresponding channel in the audio data
                for (int j = 0; j < bufferSize; j++) {
                    audioData[i * bufferSize + j] *= gain;
                }
            } else {
                // Speaker is off, set the gain to 0 (mute)
                for (int j = 0; j < bufferSize; j++) {
                    audioData[i * bufferSize + j] = 0.0f;
                }
            }
        }

        return audioData;
    }

    private void iAmResumed() {
        audioTrack.play();

        // Main audio rendering loop
        new Thread(() -> {
            while (!isFinishing()) {
                float[] audioData = updateAudioData();
                audioTrack.write(audioData, 0, audioData.length, AudioTrack.WRITE_BLOCKING);
            }
        }).start();
    }

    private void doExtra() {
        equalizer = new Equalizer(0, service.getAudioSessionId());
        equalizer.setEnabled(true);
        short bands_count = equalizer.getNumberOfBands();
        short presents = equalizer.getNumberOfPresets();
        minimum_level = equalizer.getBandLevelRange()[0];
        higher_level = equalizer.getBandLevelRange()[1];

        Log.d("h100", "band count : " + bands_count + " -- Presets : " + presents);

        eq_seek_0.setValueTo(higher_level);
        eq_seek_1.setValueTo(higher_level);
        eq_seek_2.setValueTo(higher_level);
        eq_seek_3.setValueTo(higher_level);
        eq_seek_4.setValueTo(higher_level);

        eq_seek_0.setValueFrom(minimum_level);
        eq_seek_1.setValueFrom(minimum_level);
        eq_seek_2.setValueFrom(minimum_level);
        eq_seek_3.setValueFrom(minimum_level);
        eq_seek_4.setValueFrom(minimum_level);

        if (!(equalizer.getBandLevel((short)0) < 0))
            eq_seek_0.setValue(equalizer.getBandLevel((short)0));
        if (!(equalizer.getBandLevel((short)1) < 0))
            eq_seek_1.setValue(equalizer.getBandLevel((short)1));
        if (!(equalizer.getBandLevel((short)2) < 0))
            eq_seek_2.setValue(equalizer.getBandLevel((short)2));
        if (!(equalizer.getBandLevel((short)3) < 0))
            eq_seek_3.setValue(equalizer.getBandLevel((short)3));
        if (!(equalizer.getBandLevel((short)4) < 0))
            eq_seek_4.setValue(equalizer.getBandLevel((short)4));

        for (int i = 0;i < 5; i++) {
            Log.d("xvs", "\t"+equalizer.getPresetName((short)i) + "\t Freq : " + Arrays.toString(equalizer.getBandFreqRange((short) i)) + "\t Frequency 2 " + equalizer.getCenterFreq((short)i));
        }

        BassBoost bass_boost = new BassBoost(0, service.getAudioSessionId());
        bass_boost.setEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            DynamicsProcessing au = new DynamicsProcessing(service.getAudioSessionId());
            au.setEnabled(true);
//            dynamic_processing_eq.setMax(au.getChannelCount());

//            dynamic_processing_eq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                    au.set((short)i);
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//
//                }
//            });
        }

//        EnvironmentalReverb au2 = new EnvironmentalReverb(0, service.getAudioSessionId());
//        LoudnessEnhancer loudnessEnhancer = new LoudnessEnhancer(service.getAudioSessionId());
//        PresetReverb p_reverb = new PresetReverb(0,service.getAudioSessionId());
////            BassBoost.Settings e_setting = new BassBoost.Settings();
////            Short a = e_setting.strength;
//        Virtualizer e = new Virtualizer(0, service.getAudioSessionId());
//        e.setEnabled(true);
//
//        loudnessEnhancer.setEnabled(true);
//
//        bass_boost_eq.setMax(7);
//        virtualize_eq.setMax(100/*e.getRoundedStrength()*/);
//        loudness_eq.setMax(/*(int) loudnessEnhancer.getTargetGain()*/100);
//
//        BassBoost.Settings bass_boost_setting_temp = bass_boost.getProperties();
//        BassBoost.Settings bass_boost_setting = new BassBoost.Settings(bass_boost_setting_temp.toString());
//        bass_boost_setting.strength = (1000 / 19);
//        bass_boost.setProperties(bass_boost_setting);
//
//        loudness_eq.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
////                loudnessEnhancer.setTargetGain((short)progress);
////                e.setStrength((short) progress);
//                bass_boost.setStrength((short) ((short) 1000 / 100 * progress));
//            }
//
//            @Override
//            public void onStopTrackingTouch(CircularSeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(CircularSeekBar seekBar) {
//
//            }
//        });
//
//        virtualize_eq.setMax(1000);
//        virtualize_eq.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
////                e.setStrength((short)progress);
//                virtualizer.setStrength((short) progress);
//            }
//
//            @Override
//            public void onStopTrackingTouch(CircularSeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(CircularSeekBar seekBar) {
//
//            }
//        });
//
//        p_reverb.setEnabled(true);
//        bass_boost_eq.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
//                //                bass_boost.setStrength((short)i);
//                switch ((int)progress) {
//                    case 0:
//                        p_reverb.setPreset(PresetReverb.PRESET_NONE);
//                        Log.d("presets_pm", "0 : None");
//                        break;
//                    case 1:
//                        p_reverb.setPreset(PresetReverb.PRESET_SMALLROOM);
//                        Log.d("presets_pm", "1 : PRESET_SMALLROOM");
//                        break;
//                    case 2:
//                        p_reverb.setPreset(PresetReverb.PRESET_MEDIUMROOM);
//                        Log.d("presets_pm", "2 : PRESET_MEDIUMROOM");
//                        break;
//                    case 3:
//                        p_reverb.setPreset(PresetReverb.PRESET_LARGEROOM);
//                        Log.d("presets_pm", "3 : PRESET_LARGEROOM");
//                        break;
//                    case 4:
//                        p_reverb.setPreset(PresetReverb.PRESET_MEDIUMHALL);
//                        Log.d("presets_pm", "4 : PRESET_MEDIUMHALL");
//                        break;
//                    case 5:
//                        p_reverb.setPreset(PresetReverb.PRESET_LARGEHALL);
//                        Log.d("presets_pm", "5 : PRESET_LARGEHALL");
//                        break;
//                    case 6:
//                        p_reverb.setPreset(PresetReverb.PRESET_PLATE);
//                        Log.d("presets_pm", "6 : PRESET_PLATE");
//                        break;
//                }
//                equalizer.usePreset(p_reverb.getPreset());
//                p_reverb.setEnabled(true);
//            }
//
//            @Override
//            public void onStopTrackingTouch(CircularSeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(CircularSeekBar seekBar) {
//
//            }
//        });
    }

    private void threeDSurround() {
        int sampleRate = 44100;
        int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        AudioTrack audioTrack;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioTrack = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(audioFormat)
                            .setSampleRate(sampleRate)
                            .setChannelMask(channelConfig)
                            .build())
                    .setBufferSizeInBytes(bufferSize)
                    .build();
        } else {
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channelConfig, audioFormat, bufferSize, AudioTrack.MODE_STREAM);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            audioTrack.setPreferredDevice(null);
//            audioTrack.setPrefferedSurroundFormat(nulll);
            audioTrack.setVolume(1f);
//            audioTrack.setPlaybackParams(audioTrack.getPlaybackParams().setSurroundFormatEnabled(true));
        }

        audioTrack.play();
    }

    private void settleReverbChips() {
        if (equalizer != null) {
            String[] presets = getResources().getStringArray(R.array.equlizer_presets);
            for (String preset_reverb: presets) {
                Chip chip = new Chip(this);
                ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(
                        this,
                        null,
                        0,
                        me.relex.circleindicator.R.style.Widget_MaterialComponents_Chip_Choice
                );
                chipDrawable.setShapeAppearanceModel(
                        chipDrawable.getShapeAppearanceModel().toBuilder()
                                .setAllCorners(CornerFamily.ROUNDED, getResources().getDimension(R.dimen.chipCornerRadius))
                                .build());
//                Drawable roundedDrawable = createRoundedDrawable(getResources().getColor(R.color.green),
//                        getResources().getColor(R.color.orange));
                chip.setChipBackgroundColorResource(android.R.color.transparent);
                chip.setChipStrokeColorResource(android.R.color.transparent);
                chip.setChipDrawable(chipDrawable);
                chip.setText(preset_reverb);
                chip.setChipCornerRadius(10f);
                reverb_chips.addView(chip);
                short i = equalizer.getCurrentPreset();
            }

//            equalizer.getNumberOfPresets()

            reverb_chips.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                @Override
                public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                    equalizer.usePreset(Short.parseShort(String.valueOf(checkedIds.get(0))));
                }
            });

        }
    }

    private Drawable createRoundedDrawable(int backgroundColor, int strokeColor) {
        GradientDrawable shapeDrawable = new GradientDrawable();
        shapeDrawable.setShape(GradientDrawable.RECTANGLE);
        shapeDrawable.setColor(backgroundColor);
        shapeDrawable.setStroke(2, strokeColor);
        shapeDrawable.setCornerRadius(getResources().getDimension(R.dimen.chipCornerRadius));
        return shapeDrawable;
    }

    private void setEqualizer() {
        int []eq_main_arr = {R.id.eq_0_main_txt, R.id.eq_1_main_txt, R.id.eq_2_main_txt, R.id.eq_3_main_txt, R.id.eq_4_main_txt};
        int []eq_changer_arr = {R.id.eq_0_changer_txt, R.id.eq_1_changer_txt, R.id.eq_2_changer_txt, R.id.eq_3_changer_txt, R.id.eq_4_changer_txt};
        for (int i = 0; i < equalizer.getNumberOfBands(); i++) {
            eq_main_txt[i] = findViewById(eq_main_arr[i]);
            eq_changer_txt[i] = findViewById(eq_changer_arr[i]);
        }

        for (int j = 0; j < eq_main_arr.length; j++) {
            int hart_z_short = equalizer.getCenterFreq((short) j);
            String frequency = (equalizer.getBandLevel((short) j) / 100) + " db";
            String hart_z = (hart_z_short >= 1000000) ? hart_z_short / 100000 + "KHz" : hart_z_short / 1000 + "Hz";
            eq_main_txt[j].setText(hart_z);
            eq_changer_txt[j].setText(frequency);
        }

        if (__IS_PLAY_ME_EQUALIZER_ON) {
            equalizer.setEnabled(true);
            eq_seek_0.setEnabled(true);
            eq_seek_1.setEnabled(true);
            eq_seek_2.setEnabled(true);
            eq_seek_3.setEnabled(true);
            eq_seek_4.setEnabled(true);
            eq_seek_0.setAlpha(1f);
            eq_seek_1.setAlpha(1f);
            eq_seek_2.setAlpha(1f);
            eq_seek_3.setAlpha(1f);
            eq_seek_4.setAlpha(1f);
        } else {
            equalizer.setEnabled(false);
            eq_seek_0.setEnabled(false);
            eq_seek_1.setEnabled(false);
            eq_seek_2.setEnabled(false);
            eq_seek_3.setEnabled(false);
            eq_seek_4.setEnabled(false);
            eq_seek_0.setAlpha(0.5f);
            eq_seek_1.setAlpha(0.5f);
            eq_seek_2.setAlpha(0.5f);
            eq_seek_3.setAlpha(0.5f);
            eq_seek_4.setAlpha(0.5f);
        }
    }

    private void setBassBoost() {
        bassBoost = new BassBoost(0, service.getAudioSessionId());
        if (__IS_PLAY_ME_BASS_BOOST_ON) {
            bassBoost.setEnabled(true);
            bass_boost_eq.setMax(1000); // Max strength value
            bass_boost_eq.setEnabled(true);
            bass_boost_eq.setAlpha(1f);
        } else {
            bassBoost.setEnabled(false);
            bass_boost_eq.setEnabled(false);
            bass_boost_eq.setAlpha(0.5f);
        }
    }

    private void setLoudnessEnhancement() {
        // Apply loudness enhancement effect
        loudnessEnhancer = new LoudnessEnhancer(service.getAudioSessionId());
        if (__IS_PLAY_ME_LOUDNESS_ENHANCER_ON) {
            loudnessEnhancer.setTargetGain(1000); // Adjust the gain level as per your requirement
            loudnessEnhancer.setEnabled(true);
            loudness_eq.setMax(1000);
            loudness_eq.setEnabled(true);
            loudness_eq.setAlpha(1f);
        } else {
            loudnessEnhancer.setEnabled(false);
            loudness_eq.setEnabled(false);
            loudness_eq.setAlpha(0.5f);
        }
    }

    private void setVirtualizer() {
        virtualizer = new Virtualizer(0, service.getAudioSessionId());
        if (__IS_PLAY_ME_VIRTUALIZER_ON) {
            virtualizer.setEnabled(true);
            virtualizer.setStrength((short) 100);
            virtualize_eq.setMax(1000);
            virtualize_eq.setEnabled(true);
            virtualize_eq.setAlpha(1f);
        } else {
            virtualizer.setEnabled(false);
            virtualize_eq.setEnabled(false);
            virtualize_eq.setAlpha(0.5f);
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        BackService.LocalBinder binder = (BackService.LocalBinder) iBinder;
        service = binder.getService();
//        doExtra3DSurround();
//        iAmResumed();
//        threeDSurround();
//        setVirtualizer();

//        float auxEffectSendLevel = 1.0f; // Full effect applied
//        service.mediaPlayer.setAuxEffectSendLevel(auxEffectSendLevel);
//        service.mediaPlayer.setLooping(true);

//        setLoudnessEnhancement();
//        Visualizer lizer = new Visualizer(service.getAudioSessionId());
//        lizer.setEnabled(true);

        doExtra();
        setEqualizer();
        settleReverbChips();
        on_click();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    private void createEqualizerProgramaticaly() {
        equalizer = new Equalizer(0, service.getAudioSessionId());
        short bands = equalizer.getNumberOfBands();
        short minEqLevelRange = equalizer.getBandLevelRange()[0];
        short maxEqLevelRange = equalizer.getBandLevelRange()[1];

        int[] sliderIDs = new int[bands];
        int[] frameLayoutIDs = new int[bands];
        Slider[] sliders = new Slider[bands];
        FrameLayout[] frameLayouts = new FrameLayout[bands];
        for (short i = 0; i < bands; i++) {
            short band = i;

            frameLayouts[i] = new FrameLayout(this);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,0
            );
            frameLayouts[i].setLayoutParams(params);

            sliders[i] = new Slider(this);
            sliderIDs[i] = View.generateViewId();
            sliders[i].setId(sliderIDs[i]);
            sliders[i].setValueTo(maxEqLevelRange);
            sliders[i].setValueFrom(minEqLevelRange);
            sliders[i].setRotation(270f);

            frameLayoutIDs[i] = View.generateViewId();
            frameLayouts[i].setId(frameLayoutIDs[i]);
            frameLayouts[i].addView(sliders[i]);
            constraint_frame_layout_eq.addView(frameLayouts[i]);
        }
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraint_frame_layout_eq);

        for (int i = 0; i < frameLayouts.length; i++) {
            // Connect thirdView to parent constraints
            if (i <= 0) {
                constraintSet.connect(frameLayouts[i].getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
//                constraintSet.connect(frameLayouts[i].getId(), ConstraintSet.END, frameLayouts[i + 1].getId(), ConstraintSet.END);
            } else if (i >= (frameLayouts.length - 1)) {
                constraintSet.connect(frameLayouts[i].getId(), ConstraintSet.START, frameLayouts[i - 1].getId(), ConstraintSet.END);
                constraintSet.connect(frameLayouts[i].getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            } else {
                constraintSet.connect(frameLayouts[i].getId(), ConstraintSet.START, frameLayouts[i - 1].getId(), ConstraintSet.END);
                constraintSet.connect(frameLayouts[i].getId(), ConstraintSet.END, frameLayouts[i + 1].getId(), ConstraintSet.END);
            }
            constraintSet.connect(frameLayouts[i].getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(frameLayouts[i].getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
//
//            // Apply the constraints to the ConstraintLayout
            constraintSet.applyTo(constraint_frame_layout_eq);
        }
    }
}