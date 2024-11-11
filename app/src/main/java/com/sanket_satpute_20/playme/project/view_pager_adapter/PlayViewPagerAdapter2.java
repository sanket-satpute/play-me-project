package com.sanket_satpute_20.playme.project.view_pager_adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sanket_satpute_20.playme.project.fragments.PlayLyricsFragment2;
import com.sanket_satpute_20.playme.project.fragments.PlayOptionsFragment2;

public class PlayViewPagerAdapter2 extends FragmentStatePagerAdapter {

    FragmentManager manager;
    Context context;
    int tabs;

    public PlayViewPagerAdapter2(Context context, FragmentManager manager, int tabs) {
        super(manager);
        this.context = context;
        this.manager = manager;
        this.tabs = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new PlayOptionsFragment2();
        if (position == 0)
            fragment = new PlayLyricsFragment2();
        return fragment;
    }

    @Override
    public int getCount() {
        return tabs;
    }
}