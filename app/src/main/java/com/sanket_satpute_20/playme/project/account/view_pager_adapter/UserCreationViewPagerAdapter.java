package com.sanket_satpute_20.playme.project.account.view_pager_adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class UserCreationViewPagerAdapter extends FragmentStateAdapter {

    Context context;
    ArrayList<Fragment> fragments;

    public UserCreationViewPagerAdapter(FragmentActivity context, ArrayList<Fragment> fragments) {
        super(context);
        this.context = context;
        this.fragments = fragments;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
