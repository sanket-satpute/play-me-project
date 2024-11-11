package com.sanket_satpute_20.playme.project.fragments;


import static com.sanket_satpute_20.playme.project.activity.PlayerDesignActivity.vis_color;
import static com.sanket_satpute_20.playme.project.activity.PlayerDesignActivity.visulizer_position;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.chibde.visualizer.CircleBarVisualizer;
import com.chibde.visualizer.CircleBarVisualizerSmooth;
import com.chibde.visualizer.CircleVisualizer;
import com.chibde.visualizer.LineBarVisualizer;
import com.chibde.visualizer.LineVisualizer;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.service.BackService;


public class PlayLyricsFragment extends Fragment implements ServiceConnection {

    //Service
    BackService service;

//    // Variables
    CircleVisualizer circle_visulizer;
    CircleBarVisualizer circle_bar_visulizer;
    CircleBarVisualizerSmooth circle_bar_visulizer_smooth;
    LineBarVisualizer line_bar_visulizer;
    LineVisualizer line_visulizer;

    int stroke_width_visulizer = 1;

    //BroadCastReciver
    BroadcastReceiver reciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            doExtraWork();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_lyrics, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getContext(), BackService.class);
        requireActivity().bindService(intent, PlayLyricsFragment.this, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(reciver, new IntentFilter("song.mp3.changed"));
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unbindService(this);
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(reciver);
        stopAll();
    }

    private void initViews(View view) {
        circle_visulizer = view.findViewById(R.id.circle_visulizer);
        circle_bar_visulizer = view.findViewById(R.id.circle_bar_visulizer);
        circle_bar_visulizer_smooth = view.findViewById(R.id.circle_bar_visulizer_smooth);
        line_visulizer = view.findViewById(R.id.line_visulizer);
        line_bar_visulizer = view.findViewById(R.id.line_bar_visulizer);
    }

    private void doExtraWork() {
        circle_visulizer.setAlpha(0);
        circle_bar_visulizer.setAlpha(0);
        circle_bar_visulizer_smooth.setAlpha(0);
        line_visulizer.setAlpha(0);
        line_bar_visulizer.setAlpha(0);
        if (service != null) {
            if (visulizer_position == 0) {
                circle_visulizer.setAlpha(1);
                setCircleVisualizer(service.getAudioSessionId());
            } else if (visulizer_position == 1) {
                circle_bar_visulizer.setAlpha(1);
                setCircleBarVisulizer(service.getAudioSessionId());
            } else if (visulizer_position == 2) {
                circle_bar_visulizer_smooth.setAlpha(1);
                setCircleBarSmoothVisulizer(service.getAudioSessionId());
            } else if (visulizer_position == 3) {
                line_visulizer.setAlpha(1);
                setLineVisulizer(service.getAudioSessionId());
            } else {
                line_bar_visulizer.setAlpha(1);
                setLineBarVisulizer(service.getAudioSessionId());
            }
        }
    }

    private void setCircleVisualizer(int audioSessionId) {
        if (circle_visulizer != null) {
            circle_visulizer.release();
        }
        if (audioSessionId != -1) {
            try {
                circle_visulizer.setEnabled(false);
                circle_visulizer.setPlayer(service.getAudioSessionId());
                circle_visulizer.setColor(vis_color);
                circle_visulizer.setStrokeWidth(stroke_width_visulizer);
            } catch(Exception e) {
                try {
                    circle_visulizer.setEnabled(false);
                    circle_visulizer.setPlayer(service.getAudioSessionId());
                    circle_visulizer.setColor(vis_color);
                    circle_visulizer.setStrokeWidth(stroke_width_visulizer);
                } catch(Exception b) {
                    b.printStackTrace();
                }
            }
        }
    }

    private void setCircleBarVisulizer(int audioSessionId) {
        if (circle_bar_visulizer != null) {
            circle_bar_visulizer.release();
        }
        if (audioSessionId != -1) {
            try {
                circle_bar_visulizer.setEnabled(false);
                circle_bar_visulizer.setPlayer(service.getAudioSessionId());
                circle_bar_visulizer.setColor(vis_color);
            } catch(Exception e) {
                try {
                    circle_bar_visulizer.setEnabled(false);
                    circle_bar_visulizer.setPlayer(service.getAudioSessionId());
                    circle_bar_visulizer.setColor(vis_color);
                } catch(Exception b) {
                    b.printStackTrace();
                }
            }
        }
    }

    private void setCircleBarSmoothVisulizer(int audioSessionId) {
        if (circle_bar_visulizer_smooth != null) {
            circle_bar_visulizer_smooth.release();
        }
        if (audioSessionId != -1) {
            try {
                circle_bar_visulizer_smooth.setEnabled(false);
                circle_bar_visulizer_smooth.setPlayer(service.getAudioSessionId());
                circle_bar_visulizer_smooth.setColor(vis_color);
            } catch(Exception e) {
                try {
                    circle_bar_visulizer_smooth.setEnabled(false);
                    circle_bar_visulizer_smooth.setPlayer(service.getAudioSessionId());
                    circle_bar_visulizer_smooth.setColor(vis_color);
                } catch(Exception b) {
                    b.printStackTrace();
                }
            }
        }
    }

    private void setLineVisulizer(int audioSessionId) {
        if (line_visulizer != null) {
            line_visulizer.release();
        }
        if (audioSessionId != -1) {
            try {
                line_visulizer.setEnabled(false);
                line_visulizer.setPlayer(service.getAudioSessionId());
                line_visulizer.setColor(vis_color);
                line_visulizer.setStrokeWidth(stroke_width_visulizer);
            } catch(Exception e) {
                try {
                    line_visulizer.setEnabled(false);
                    line_visulizer.setPlayer(service.getAudioSessionId());
                    line_visulizer.setColor(vis_color);
                    line_visulizer.setStrokeWidth(stroke_width_visulizer);
                } catch(Exception b) {
                    b.printStackTrace();
                }
            }
        }
    }

    private void setLineBarVisulizer(int audioSessionId) {
        if (line_bar_visulizer != null) {
            line_bar_visulizer.release();
        }
        if (audioSessionId != -1) {
            try {
                line_bar_visulizer.setEnabled(false);
                line_bar_visulizer.setPlayer(service.getAudioSessionId());
                line_bar_visulizer.setColor(vis_color);
            } catch(Exception e) {
                try {
                    line_bar_visulizer.setEnabled(false);
                    line_bar_visulizer.setPlayer(service.getAudioSessionId());
                    line_bar_visulizer.setColor(vis_color);
                } catch(Exception b) {
                    b.printStackTrace();
                }
            }
        }
    }

    private void stopAll() {
        if (visulizer_position == 0) {
            if (circle_visulizer != null)
                circle_visulizer.release();
        } else if (visulizer_position == 1) {
            if (circle_bar_visulizer != null)
                circle_bar_visulizer.release();
        } else if (visulizer_position == 2) {
            if (circle_bar_visulizer_smooth != null)
                circle_bar_visulizer_smooth.release();
        } else if (visulizer_position == 3) {
            if (line_visulizer != null)
                line_visulizer.release();
        } else {
            if (line_bar_visulizer != null)
                line_bar_visulizer.release();
        }

    }

    // Service Stub
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        BackService.LocalBinder binder = (BackService.LocalBinder) iBinder;
        service = binder.getService();
        doExtraWork();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }
}