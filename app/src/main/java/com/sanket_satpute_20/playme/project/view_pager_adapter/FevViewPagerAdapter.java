package com.sanket_satpute_20.playme.project.view_pager_adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sanket_satpute_20.playme.project.fevourite_fragments.FevAlbumFragment;
import com.sanket_satpute_20.playme.project.fevourite_fragments.FevArtistFragment;
import com.sanket_satpute_20.playme.project.fevourite_fragments.FevPlaylistFragment;
import com.sanket_satpute_20.playme.project.fevourite_fragments.FevSongFragment;

public class FevViewPagerAdapter extends FragmentStatePagerAdapter {

    Context context;
    FragmentManager fragmentManager;
    int tabs;

    public FevViewPagerAdapter(Context context, @NonNull FragmentManager fm, int tabs) {
        super(fm, tabs);
        this.context = context;
        fragmentManager = fm;
        this.tabs = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 3:
                fragment = new FevPlaylistFragment();
                break;
            case 1:
                fragment = new FevArtistFragment();
                break;
            case 2:
                fragment = new FevAlbumFragment();
                break;
            default:
                fragment = new FevSongFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return tabs;
    }
}