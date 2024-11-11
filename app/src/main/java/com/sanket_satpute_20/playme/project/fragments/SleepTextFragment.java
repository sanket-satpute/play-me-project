package com.sanket_satpute_20.playme.project.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sanket_satpute_20.playme.R;

public class SleepTextFragment extends Fragment {

    TextView sleep_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep_text, container, false);
        initView(view);
        String text = getArguments().getString("SLEEP_TIMER_KEY");
        sleep_text.setText(text);
        return view;
    }

    private void initView (View view) {
        sleep_text = view.findViewById(R.id.sleep_text);
    }
}