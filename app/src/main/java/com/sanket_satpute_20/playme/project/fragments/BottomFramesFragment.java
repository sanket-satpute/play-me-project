package com.sanket_satpute_20.playme.project.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;
import static com.sanket_satpute_20.playme.project.service.BackService.song;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.service.BackService;

import java.util.ArrayList;
/*getLocationOnScreen() && getLocationOnWindow() both used for get position of view*/
public class BottomFramesFragment extends Fragment implements ServiceConnection {

    ImageView imageView1, imageView2, imageView3;

    BackService service;

    int position = 0;
    float height;

    ArrayList<byte[]> img = new ArrayList<>();

    CardView first_card, second_card, third_card;

    int[] first_card_sizes = new int[2];
    int[] second_card_sizes = new int[2];
    int[] third_card_sizes = new int[2];

    int f_c_y, s_c_y, t_c_y;

    //BroadCastReciver
    BroadcastReceiver reciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("song.mp3.changed")) {
                img.clear();
                getSharedPref();
            }
            if (intent.getAction().equals("play.activity.more.clicked")) {
                setStartAnimation();
            } else if(intent.getAction().equals("song_changed_dilog_recycler") ||
            intent.getAction().equals("play_act.dilog.dismissed")) {
                setEndAnimation();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_frames, container, false);
        initViews(view);
        onClick();
        height = getResources().getDisplayMetrics().heightPixels / getResources().getDisplayMetrics().density;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getContext(), BackService.class);
        requireActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter();
        filter.addAction("play.activity.more.clicked");
        filter.addAction("song_changed_dilog_recycler");
        filter.addAction("play_act.dilog.dismissed");
        filter.addAction("song.mp3.changed");
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(reciver, filter);
        getSharedPref();
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unbindService(this);
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(reciver);
    }

    private void initViews(View view) {
        imageView1 = view.findViewById(R.id.image1);
        imageView2 = view.findViewById(R.id.image2);
        imageView3 = view.findViewById(R.id.image3);

        first_card = view.findViewById(R.id.card_first);
        second_card = view.findViewById(R.id.card_second);
        third_card = view.findViewById(R.id.card_third);

        first_card.getLocationOnScreen(first_card_sizes);
        f_c_y = first_card_sizes[1];

        second_card.getLocationOnScreen(second_card_sizes);
        s_c_y = second_card_sizes[1];

        third_card.getLocationOnScreen(third_card_sizes);
        t_c_y = third_card_sizes[1];

    }

    public void doAction()
    {
        try {
            if (song.size() >= 3) {
                if (position == song.size() - 1) {
                    MyImageLoad load = new MyImageLoad();
                    load.execute(song.get(0));
                    MyImageLoad load1 = new MyImageLoad();
                    load1.execute(song.get(1));
                    MyImageLoad load2 = new MyImageLoad();
                    load2.execute(song.get(2));
                } else if (position == song.size() - 2) {
                    MyImageLoad load = new MyImageLoad();
                    load.execute(song.get(position + 1));
                    MyImageLoad load1 = new MyImageLoad();
                    load1.execute(song.get(0));
                    MyImageLoad load2 = new MyImageLoad();
                    load2.execute(song.get(1));
                } else if (position == song.size() - 3) {
                    MyImageLoad load = new MyImageLoad();
                    load.execute(song.get(position + 1));
                    MyImageLoad load1 = new MyImageLoad();
                    load1.execute(song.get(position + 2));
                    MyImageLoad load2 = new MyImageLoad();
                    load2.execute(song.get(0));
                } else {
                    MyImageLoad load = new MyImageLoad();
                    load.execute(song.get(position + 1));
                    MyImageLoad load1 = new MyImageLoad();
                    load1.execute(song.get(position + 2));
                    MyImageLoad load2 = new MyImageLoad();
                    load2.execute(song.get(position + 3));
                }
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private void onClick() {

        try {
            if (song.size() > 0) {
                first_card.setVisibility(View.VISIBLE);
                if (song.size() > 1) {
                    second_card.setVisibility(View.VISIBLE);
                    if (song.size() > 2) {
                        third_card.setVisibility(View.VISIBLE);
                    } else {
                        third_card.setVisibility(View.GONE);
                    }
                } else {
                    second_card.setVisibility(View.GONE);
                    third_card.setVisibility(View.GONE);
                }
            } else {
                first_card.setVisibility(View.GONE);
                second_card.setVisibility(View.GONE);
                third_card.setVisibility(View.GONE);
            }

            imageView1.setOnClickListener(view -> {
                if (BackService.isServiceRunning) {
                    if (service != null) {
                        if (position == song.size() - 1)
                            service.createMedia(0);
                        else
                            service.createMedia(position + 1);
                    }
                }
                else {
                    if (position == song.size() - 1)
                        position = 0;
                    else
                        position = position + 1;
                    Intent intent = new Intent(requireActivity(), BackService.class);
                    intent.putExtra("position", position);
                    requireActivity().startService(intent);
                    Toast.makeText(requireContext(), "Clicked " + song.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                }
            });

            imageView2.setOnClickListener(view -> {
                if (BackService.isServiceRunning) {
                    if (service != null) {
                        if (position >= song.size() - 2)
                            service.createMedia(1);
                        else
                            service.createMedia(position + 2);
                    }
                }
                else {
                    if (position >= song.size() - 2)
                        position = 1;
                    else
                        position = position + 2;
                    Intent intent = new Intent(requireActivity(), BackService.class);
                    intent.putExtra("position", position);
                    requireActivity().startService(intent);
                    Toast.makeText(requireContext(), "Clicked " + song.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                }
            });

            imageView3.setOnClickListener(view -> {
                if (BackService.isServiceRunning) {
                    if (service != null) {
                        if (position >= song.size() - 3)
                            service.createMedia(2);
                        else
                            service.createMedia(position + 3);
                    }
                }
                else {
                    if (position >= song.size() - 3)
                        position = 2;
                    else
                        position = position + 3;
                    Intent intent = new Intent(requireActivity(), BackService.class);
                    intent.putExtra("position", position);
                    requireActivity().startService(intent);
                    Toast.makeText(requireContext(), "Clicked " + song.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private void getSharedPref() {
        SharedPreferences preferences = requireContext().getSharedPreferences("PLAYING", MODE_PRIVATE);
        position = preferences.getInt(SONGPOSITION, 0);
        doAction();
    }

    private void setStartAnimation() {
        @SuppressLint("Recycle") ValueAnimator trans_y_first =
                ObjectAnimator.ofFloat(first_card, "translationY", -700f);
        @SuppressLint("Recycle") ValueAnimator trans_x_first =
                ObjectAnimator.ofFloat(first_card, "translationX", -200f);
        @SuppressLint("Recycle") ValueAnimator scale_x_first =
                ObjectAnimator.ofFloat(first_card, "scaleX", 1, 0.3f);
        @SuppressLint("Recycle") ValueAnimator scale_y_first =
                ObjectAnimator.ofFloat(first_card, "scaleY", 1, 0.3f);

        @SuppressLint("Recycle") ValueAnimator trans_y_second =
                ObjectAnimator.ofFloat(second_card, "translationY", -700f);
        @SuppressLint("Recycle") ValueAnimator trans_x_second =
                ObjectAnimator.ofFloat(second_card, "translationX", 150f);
        @SuppressLint("Recycle") ValueAnimator scale_x_second =
                ObjectAnimator.ofFloat(second_card, "scaleX", 1, 0.3f);
        @SuppressLint("Recycle") ValueAnimator scale_y_second =
                ObjectAnimator.ofFloat(second_card, "scaleY", 1, 0.3f);

        @SuppressLint("Recycle") ValueAnimator trans_y_third =
                ObjectAnimator.ofFloat(third_card, "translationY", -600f);
        @SuppressLint("Recycle") ValueAnimator trans_x_third =
                ObjectAnimator.ofFloat(third_card, "translationX", -200f);
        @SuppressLint("Recycle") ValueAnimator scale_x_third =
                ObjectAnimator.ofFloat(third_card, "scaleX", 1, 0.3f);
        @SuppressLint("Recycle") ValueAnimator scale_y_third =
                ObjectAnimator.ofFloat(third_card, "scaleY", 1, 0.3f);

        AnimatorSet first = new AnimatorSet();
        first.playTogether(trans_y_first, trans_x_first, scale_x_first, scale_y_first);
        first.setDuration(500);

        AnimatorSet second = new AnimatorSet();
        second.playTogether(trans_y_second, trans_x_second, scale_x_second, scale_y_second);
        second.setDuration(500);
        second.setStartDelay(150);

        AnimatorSet third = new AnimatorSet();
        third.playTogether(trans_y_third, trans_x_third, scale_x_third, scale_y_third);
        third.setDuration(500);
        third.setStartDelay(250);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(first, second, third);
        set.start();

    }

    private void setEndAnimation() {
        first_card.setVisibility(View.VISIBLE);
        @SuppressLint("Recycle") ValueAnimator trans_y_first =
                ObjectAnimator.ofFloat(first_card, "translationY", f_c_y);
        @SuppressLint("Recycle") ValueAnimator trans_x_first =
                ObjectAnimator.ofFloat(first_card, "translationX", 0);
        @SuppressLint("Recycle") ValueAnimator scale_x_first =
                ObjectAnimator.ofFloat(first_card, "scaleX", 0, 1);
        @SuppressLint("Recycle") ValueAnimator scale_y_first =
                ObjectAnimator.ofFloat(first_card, "scaleY", 0, 1);

        @SuppressLint("Recycle") ValueAnimator trans_y_second =
                ObjectAnimator.ofFloat(second_card, "translationY", s_c_y);
        @SuppressLint("Recycle") ValueAnimator trans_x_second =
                ObjectAnimator.ofFloat(second_card, "translationX", .0f);
        @SuppressLint("Recycle") ValueAnimator scale_x_second =
                ObjectAnimator.ofFloat(second_card, "scaleX", 0, 1);
        @SuppressLint("Recycle") ValueAnimator scale_y_second =
                ObjectAnimator.ofFloat(second_card, "scaleY", 0, 1);

        @SuppressLint("Recycle") ValueAnimator trans_y_third =
                ObjectAnimator.ofFloat(third_card, "translationY", t_c_y);
        @SuppressLint("Recycle") ValueAnimator trans_x_third =
                ObjectAnimator.ofFloat(third_card, "translationX", 0);
        @SuppressLint("Recycle") ValueAnimator scale_x_third =
                ObjectAnimator.ofFloat(third_card, "scaleX", 0, 1);
        @SuppressLint("Recycle") ValueAnimator scale_y_third =
                ObjectAnimator.ofFloat(third_card, "scaleY", 0 , 1);

        AnimatorSet first = new AnimatorSet();
        first.playTogether(trans_y_first, trans_x_first, scale_x_first, scale_y_first);
        first.setDuration(500);

        AnimatorSet second = new AnimatorSet();
        second.playTogether(trans_y_second, trans_x_second, scale_x_second, scale_y_second);
        second.setDuration(500);
        second.setStartDelay(150);

        AnimatorSet third = new AnimatorSet();
        third.playTogether(trans_y_third, trans_x_third, scale_x_third, scale_y_third);
        third.setDuration(500);
        third.setStartDelay(250);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(first, second, third);
        set.start();

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


    @SuppressLint("StaticFieldLeak")
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
            } catch(Exception e) {
                e.printStackTrace();
            }
            return album_pic;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            img.add(bytes);
            if (img.size() >= 3) {
                try {
                    Glide.with(requireContext())
                            .asBitmap()
                            .load(img.get(0))
                            .override(150, 150)
                            .placeholder(R.mipmap.ic_music)
                            .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                            .into(imageView1);

                    Glide.with(requireContext())
                            .asBitmap()
                            .load(img.get(1))
                            .override(150, 150)
                            .placeholder(R.mipmap.ic_music)
                            .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                            .into(imageView2);

                    Glide.with(requireContext())
                            .asBitmap()
                            .load(img.get(2))
                            .override(150, 150)
                            .placeholder(R.mipmap.ic_music)
                            .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                            .into(imageView3);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
