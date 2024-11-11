package com.sanket_satpute_20.playme.project.fragments;

import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.fragments.PlayOptionFragment.UPDOWNREQUEST;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;
import static com.sanket_satpute_20.playme.project.service.BackService.song;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.recycler_views.CurrentPlayingRecycle;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

public class SongListBottomFragment extends BottomSheetDialogFragment implements GestureDetector.OnGestureListener{

    RecyclerView recyclerView;
    int position;


    MaterialCardView parent_frame;
    ConstraintLayout layout;
    MaterialCardView divider;

    GestureDetector gesture;
    Intent broad_intent = new Intent();

    boolean dilog_expanded;

    //    Intent broad_intent;
    int []bottom_sheet_dilog_positions = new int[2];
    int b_s_d_y, b_s_d_height;

    BroadcastReceiver reciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals("first_player.activity.up.down.called")) {
                    if (intent.getBooleanExtra(UPDOWNREQUEST, false)) {
                        reciveUP();
                    } else {
                        reciveDOWN();
                    }
                }
                if (intent.getAction().equals("song.mp3.changed")) {
                    doWork();
                }
                if (intent.getAction().equals("bottom_sheet.song_list_bottom_fragment.throw")) {
                    if (dilog_expanded)
                        bottomSheetUpDownMethod(-10);
                    else
                        bottomSheetUpDownMethod(10);
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog);
        gesture = new GestureDetector(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list_bottom, container, false);
        initViews(view);
        doWork();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("first_player.activity.up.down.called");
        filter.addAction("song.mp3.changed");
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(reciver, filter);
        parent_frame.animate().translationY(b_s_d_y + (parent_frame.getHeight() / 3.0f));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(reciver);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialog);
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.bottom_list_recyclerview);
        layout = view.findViewById(R.id.first_coordinate);
        divider = view.findViewById(R.id.divider_first);
        parent_frame = view.findViewById(R.id.parent_frame_layout);

        divider.setCardBackgroundColor(ACCENT_COLOR);

        parent_frame.getLocationOnScreen(bottom_sheet_dilog_positions);
        b_s_d_y = bottom_sheet_dilog_positions[1];
    }

    @SuppressLint("ClickableViewAccessibility")
    public void doWork()
    {
        parent_frame.setOnTouchListener((view, motionEvent) -> gesture.onTouchEvent(motionEvent));
//        parent_frame.performClick();
        SharedPreferences preferences = requireActivity().getSharedPreferences("PLAYING", Context.MODE_PRIVATE);
        position = preferences.getInt(SONGPOSITION, 13);
        CurrentPlayingRecycle adapter = new CurrentPlayingRecycle(requireActivity(), song, recyclerView, requireActivity().getSupportFragmentManager());
        LinearLayoutManager layout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layout.scrollToPosition(position);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layout);
        b_s_d_height = parent_frame.getHeight();
    }

    private void reciveDOWN() {
        ObjectAnimator animator = (ObjectAnimator) AnimatorInflater.loadAnimator(requireActivity(), R.animator.size_decrease);
        animator.setTarget(divider);
        animator.start();
        layout.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        layout.setBackgroundColor(Color.TRANSPARENT);
    }

    private void reciveUP() {
        ObjectAnimator animator = (ObjectAnimator) AnimatorInflater.loadAnimator(requireActivity(), R.animator.size_increase);
        animator.setTarget(divider);
        animator.start();
        layout.setBackgroundTintList(ColorStateList.valueOf(0xffc1c1c1));
        layout.setBackgroundColor(0xffc1c1c1);
    }

    private void bottomSheetUpDownMethod(float v1) {
//        b_s_d_height = parent_frame.getHeight();
        if (v1 > 0) {
            Log.d("abgv", "position : " + b_s_d_y + " height : " + b_s_d_height);
            parent_frame.animate().translationY(b_s_d_y - (b_s_d_height / 1.5f));
            broad_intent.putExtra(UPDOWNREQUEST, true);
            reciveUP();
            dilog_expanded = true;
        } else if (v1 < 0) {
            parent_frame.animate().translationY(b_s_d_y + (b_s_d_height / 1.5f));
            broad_intent.putExtra(UPDOWNREQUEST, false);
            reciveDOWN();
            dilog_expanded = false;
        }
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(broad_intent);
    }

    /*      Gesture Methods     */
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
        bottomSheetUpDownMethod(v1);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}