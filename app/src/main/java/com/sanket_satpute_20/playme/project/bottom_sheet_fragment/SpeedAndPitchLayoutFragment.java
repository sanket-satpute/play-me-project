package com.sanket_satpute_20.playme.project.bottom_sheet_fragment;

import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.iSPitchON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.iSSpeedON;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.pitchF;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.pitchI;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.speedF;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.speedI;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.service.BackService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

public class SpeedAndPitchLayoutFragment extends BottomSheetDialogFragment implements ServiceConnection {

    public static final String ACTION_SPEED_AND_PITCH = "action.pitch_and_speed_send";
    public static final String SPEED_PITCH_ON = "SPEED_PITCH_ON";

    SeekBar speed;
    AppCompatSeekBar pitch;

    BackService service;

    ImageView speed_restore, pitch_restore;
    MaterialCardView divider;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.speed_and_pitch_layout, container, false);
        initView(view);
        doExtra();
        onClick();
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialog);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(requireActivity(), BackService.class);
        requireActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unbindService(this);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Intent intent = new Intent();
        intent.setAction(ACTION_SPEED_AND_PITCH);
        intent.putExtra(SPEED_PITCH_ON, iSSpeedON || iSPitchON);
        LocalBroadcastManager.getInstance(requireActivity())
                .sendBroadcast(intent);
    }

    private void initView(View view) {
        speed = view.findViewById(R.id.speed);
        pitch = view.findViewById(R.id.pitch);
        speed_restore = view.findViewById(R.id.speed_restore);
        pitch_restore = view.findViewById(R.id.pitch_restore);

        divider = view.findViewById(R.id.divider);
    }

    private void doExtra() {
        divider.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));
//        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {
//                0xffffffff, 0xffff7f00/*, 0xffffff00, 0xff00ff00, 0xff0000ff, 0xff4b0082, 0xff9400d3*/
//        });
//
//        speed.setProgressTintList(ColorStateList.valueOf(drawable));
//        pitch.setProgressTintList(ColorStateList.valueOf(drawable));
//        pitch.setBackgroundTintList(ColorStateList.);
        speed.setProgress(speedI - 1);
        pitch.setProgress(pitchI - 1);
    }

    private void onClick() {
        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                speedI = i + 1;
                speedF = ((float) 2 / speed.getMax()) * speedI;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    service.setSpeed(speedF);
                }
                iSSpeedON = speedI != 5;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pitchI = i + 1;
                pitchF = ((float) 2 / speed.getMax()) * pitchI;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    service.setPitch(pitchF);
                }
                iSPitchON = pitchI != 5;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        speed_restore.setOnClickListener(view -> {
            speedI = 5;
            speedF = ((float) 2 / speed.getMax()) * speedI;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                service.setSpeed(speedF);
            }
            iSSpeedON = speedI != 5;
            speed.setProgress(speedI - 1);
        });

        pitch_restore.setOnClickListener(view -> {
            pitchI = 5;
            pitchF = ((float) 2 / speed.getMax()) * pitchI;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                service.setPitch(pitchF);
            }
            iSPitchON = pitchI != 5;
            pitch.setProgress(pitchI - 1);
        });
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        BackService.LocalBinder binder = (BackService.LocalBinder) iBinder;
        service = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }
}
