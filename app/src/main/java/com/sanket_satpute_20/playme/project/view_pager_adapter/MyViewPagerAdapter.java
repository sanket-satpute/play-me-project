package com.sanket_satpute_20.playme.project.view_pager_adapter;

import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.tab_list;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sanket_satpute_20.playme.project.fragments.AlbumFragment;
import com.sanket_satpute_20.playme.project.fragments.ArtistFragmentFragment;
import com.sanket_satpute_20.playme.project.fragments.HomeTabFragment;
import com.sanket_satpute_20.playme.project.fragments.PlayListFragment;
import com.sanket_satpute_20.playme.project.fragments.SongFragment;

public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

    Context context;
    FragmentManager fragmentManager;
    int tabs;

    public MyViewPagerAdapter(Context context, @NonNull FragmentManager fm, int tabs) {
        super(fm, tabs);
        this.context = context;
        fragmentManager = fm;
        this.tabs = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        try {
            if (tab_list.get(position).equals("Home"))
                return new HomeTabFragment();
            if (tab_list.get(position).equals("Song"))
                return new SongFragment();
            if (tab_list.get(position).equals("Album"))
                return new AlbumFragment();
            if (tab_list.get(position).equals("Artist"))
                return new ArtistFragmentFragment();
            if (tab_list.get(position).equals("Playlist"))
                return new PlayListFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SongFragment();
    }

    @Override
    public int getCount() {
        return tabs;
    }
}
