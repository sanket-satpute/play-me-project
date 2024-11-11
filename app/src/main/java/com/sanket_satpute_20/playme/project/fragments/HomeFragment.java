package com.sanket_satpute_20.playme.project.fragments;

import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.view_pager_adapter.MyViewPagerAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {

    public static final int REQUEST_CODE = 1;
    public static final String FROMWHERE = "FROMWHERE";
    public static final String SORTINGORDER = "SORTBYORDER";
    public static final String SORTED = "SORTED";
    int default_color;

    TabLayout tabLayout;
    ViewPager viewPager;

    View view;
    public static int selected_index;
    public static String tab_name;

    public static ArrayList<String> tab_list = new ArrayList<>();
    public static boolean home_checked, song_checked, album_checked, artist_checked, playlist_checked, tab_list_changed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        doExtra();
        setTabbing();
        return view;
    }
    Fragment fragment;
    @Override
    public void onResume() {
        super.onResume();
        fragment = pager.getItem(viewPager.getCurrentItem());

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                default_color = 0xddffffff;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                default_color = 0xdd000000;
                break;
        }
        tabLayout.setTabTextColors(default_color, ACCENT_COLOR);
        tabLayout.setSelectedTabIndicatorColor(ACCENT_COLOR);
        if (tab_list_changed)
            doExtra();
    }

    private void initViews(View view) {
        // findingView's
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
    }

    private void doExtra() {
        if (tab_list == null)
            tab_list = new ArrayList<>();

        if (home_checked && !tab_list.contains("Home"))
            tab_list.add("Home");
        if (song_checked && !tab_list.contains("Song"))
            tab_list.add("Song");
        if (album_checked && !tab_list.contains("Album"))
            tab_list.add("Album");
        if (artist_checked & !tab_list.contains("Artist"))
            tab_list.add("Artist");
        if (playlist_checked && !tab_list.contains("Playlist"))
            tab_list.add("Playlist");

        if (!home_checked && !song_checked && !album_checked && !artist_checked && !playlist_checked ) {
            song_checked = true;
            album_checked = true;
            playlist_checked = true;
            doExtra();
        }

        doRepeatLoop();

        if (tab_list.contains(tab_name)) {
            selected_index = tab_list.indexOf(tab_name);
        }
    }

    private void doRepeatLoop() {
        for (String name: tab_list) {
            tabLayout.addTab(tabLayout.newTab().setText(name));
        }
        tab_list_changed = false;
    }

    MyViewPagerAdapter pager;

    private void setTabbing() {
        pager = new MyViewPagerAdapter(requireActivity()
                ,requireActivity().getSupportFragmentManager(), tabLayout.getTabCount() );
        viewPager.setAdapter(pager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        try {
            Objects.requireNonNull(tabLayout.getTabAt(selected_index)).select();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}