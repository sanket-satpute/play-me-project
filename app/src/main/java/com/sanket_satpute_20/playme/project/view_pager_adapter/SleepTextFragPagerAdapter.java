package com.sanket_satpute_20.playme.project.view_pager_adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sanket_satpute_20.playme.project.fragments.SleepTextFragment;

import java.util.ArrayList;

public class SleepTextFragPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<String> list;
    Context context;

    public SleepTextFragPagerAdapter(@NonNull FragmentManager fm, Context context, ArrayList<String> list) {
        super(fm);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        SleepTextFragment fragment = new SleepTextFragment();
        Bundle bundle = new Bundle();
        bundle.putString("SLEEP_TIMER_KEY", list.get(position));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
