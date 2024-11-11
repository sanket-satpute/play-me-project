package com.sanket_satpute_20.playme.project.bottom_sheet_fragment;

import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.service.BackService.currentTime;
import static com.sanket_satpute_20.playme.project.service.BackService.timer_set;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.service.BackService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class SleepTimerLayoutFragment extends BottomSheetDialogFragment implements
                                                         GestureDetector.OnGestureListener , ServiceConnection {

    BackService service;

    /*      Variables       */
    GestureDetector gesture;
    Button cancel, set;
    CircularSeekBar progress;
    TextView hours, minutes, seconds;
    MaterialCardView divider;

    String[] hr = {"00", "01", "02", "03", "04", "05"};
    String[] min = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "59"};

    boolean is_h, is_m;

    final int max = 21600000;
    float p = 0.0f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sleep_timer_layout, container, false);
        initViews(view);
        gesture = new GestureDetector(this);
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
        if(timer_set) {
            startTimer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unbindService(this);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        sendBroadcast();
    }

    private void initViews(View view) {
        cancel = view.findViewById(R.id.cancel_btn);
        set = view.findViewById(R.id.set_btn);
        progress = view.findViewById(R.id.progressBar);
        hours = view.findViewById(R.id.hours);
        minutes = view.findViewById(R.id.minutes);
        seconds = view.findViewById(R.id.seconds);
        divider = view.findViewById(R.id.parent_divider);
    }

    private void doExtra() {
        divider.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));

        progress.setMax(max);
        String txt = (timer_set) ? "stop" : "cancel";
        cancel.setText(txt);
        cancel.setOnClickListener(m -> {
            if (cancel.getText().toString().equals("stop")) {
                timer_set = false;
                service.stopTimer();
            }
            this.dismiss();
        });

        set.setOnClickListener(m -> {
            SleepTimerLayoutFragment.this.dismiss();
            if (progress.getProgress() > 0) {
                service.stopTimer();
                timer_set = true;
                service.stopTimer();
                service.startTimer((int)p);
                startTimer();
            }
        });
    }

    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setAction("player.android.timer.action.performed");
        LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(intent);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onClick() {
        hours.setOnTouchListener((view, motionEvent) -> {
            is_h = true;
            is_m = false;
            return gesture.onTouchEvent(motionEvent);
        });

        minutes.setOnTouchListener((view, motionEvent) -> {
            is_h = false;
            is_m = true;
            return gesture.onTouchEvent(motionEvent);
        });

        progress.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                if (fromUser) {
                    p = progress;
                    setProgress(progress);
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });
    }

    private int getHours(int time) {
        return ((time / (60*60*1000)) % 24);
    }

    private String getMins(int time) {
        int mm = ((time / (1000*60)) % 60);
        if (mm >= 0 && mm <= 9)
            return "0"+mm;
        return String.valueOf((time / (1000*60) % 60));
    }

    private String getSecs(int time) {
        int ss = (time / 1000) % 60;
        if (ss >= 0 && ss <= 9)
            return "0"+ss;
        return String.valueOf((time / 1000) % 60);
    }

    @SuppressLint("SetTextI18n")
    public void setProgress(float progress) {
        if (hours != null)hours.setText("0" + getHours((int) progress));
        if (minutes != null)minutes.setText(String.valueOf(getMins((int)progress)));
        if (seconds != null)seconds.setText(String.valueOf(getSecs((int)progress)));

        if (this.progress != null)this.progress.setProgress(progress);
    }


    public void startTimer() {
        Handler handler = new Handler();
        Runnable thread ;
        thread = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if(timer_set && currentTime >= 0) {
                    setProgress(currentTime);
                    handler.postDelayed(this, 1000);
                }
            }
        };
        requireActivity().runOnUiThread(thread);
    }

    /*  Gesture   */

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        if (is_h) {
            if (v < 0) {
                switch (hours.getText().toString()) {
                    case "00":
                        hours.setText(hr[1]);
                        break;
                    case "01":
                        hours.setText(hr[2]);
                        break;
                    case "02":
                        hours.setText(hr[3]);
                        break;
                    case "03":
                        hours.setText(hr[4]);
                        break;
                    case "04":
                        hours.setText(hr[5]);
                        break;
                    default:
                        hours.setText(hr[0]);
                        break;
                }
                return true;
            }
            if (v > 0) {
                switch (hours.getText().toString()) {
                    case "00":
                        hours.setText(hr[5]);
                        break;
                    case "01":
                        hours.setText(hr[0]);
                        break;
                    case "02":
                        hours.setText(hr[1]);
                        break;
                    case "03":
                        hours.setText(hr[2]);
                        break;
                    case "04":
                        hours.setText(hr[3]);
                        break;
                    case "05":
                        hours.setText(hr[4]);
                        break;
                }
            }
            /*code for hours*/
        }
        else if (is_m) {
            if (v < 0) {
                switch (minutes.getText().toString()) {
                    case "00":
                        minutes.setText(min[1]);
                        break;
                    case "01":
                        minutes.setText(min[2]);
                        break;
                    case "02":
                        minutes.setText(min[3]);
                        break;
                    case "03":
                        minutes.setText(min[4]);
                        break;
                    case "04":
                        minutes.setText(min[5]);
                        break;
                    case "05":
                        minutes.setText(min[6]);
                        break;
                    case "06":
                        minutes.setText(min[7]);
                        break;
                    case "07":
                        minutes.setText(min[8]);
                        break;
                    case "08":
                        minutes.setText(min[9]);
                        break;
                    case "09":
                        minutes.setText(min[10]);
                        break;
                    case "59":
                        minutes.setText(min[11]);
                        break;
                    default:
                        minutes.setText(String.valueOf(Integer.parseInt(minutes.getText().toString()) + 1));
                        break;
                }
            } else {
                switch (minutes.getText().toString()) {
                    case "00":
                        minutes.setText(min[11]);
                        break;
                    case "01":
                        minutes.setText(min[0]);
                        break;
                    case "02":
                        minutes.setText(min[1]);
                        break;
                    case "03":
                        minutes.setText(min[2]);
                        break;
                    case "04":
                        minutes.setText(min[3]);
                        break;
                    case "05":
                        minutes.setText(min[4]);
                        break;
                    case "06":
                        minutes.setText(min[5]);
                        break;
                    case "07":
                        minutes.setText(min[6]);
                        break;
                    case "08":
                        minutes.setText(min[7]);
                        break;
                    case "09":
                        minutes.setText(min[8]);
                        break;
                    case "10":
                        minutes.setText(min[9]);
                        break;
                    default:
                        minutes.setText(String.valueOf(Integer.parseInt(minutes.getText().toString()) - 1));
                        break;
                }
            }
            /*Code for Minutes*/
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return true;
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
