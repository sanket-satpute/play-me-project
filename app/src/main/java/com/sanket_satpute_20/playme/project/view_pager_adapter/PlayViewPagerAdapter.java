package com.sanket_satpute_20.playme.project.view_pager_adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sanket_satpute_20.playme.project.fragments.PlayLyricsFragment;
import com.sanket_satpute_20.playme.project.fragments.PlayOptionFragment;

public class PlayViewPagerAdapter extends FragmentStatePagerAdapter {

  Context context;
  FragmentManager fragmentManager;
  int tabs;

  public PlayViewPagerAdapter(Context context, @NonNull FragmentManager fm, int tabs) {
    super(fm, tabs);
    this.context = context;
    fragmentManager = fm;
    this.tabs = tabs;
  }

  @NonNull
  @Override
  public Fragment getItem(int position) {
    Fragment fragment = new PlayOptionFragment();
    if (position == 1)
      fragment = new PlayLyricsFragment();
    return fragment;
  }

  @Override
  public int getCount() {
    return tabs;
  }
}
