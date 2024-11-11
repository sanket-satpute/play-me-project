package com.sanket_satpute_20.playme.project.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sanket_satpute_20.playme.MainActivity.musicFiles;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.FROMWHERE;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.ISONGRECYCLE;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGNAME;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGPATH;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.color;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.SortLayoutBottomFragment;
import com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle;
import com.sanket_satpute_20.playme.project.service.BackService;

//android studio when we draw o or circle on screen when screen is of or lock in that time how to set action give code
public class SongFragment extends Fragment implements ServiceConnection {

        // Service Bound
        BackService backService;
        ImageView sort;

        RecyclerView recyclerView;
        SongsRecycle songsRecycle;
        public SongsRecycle.SongsHolder previous_holder = null;
        TextView song_count;

        FloatingActionButton fab;
        ConstraintLayout fetching_layout, fetched_layout;

        //Relative layout
        RelativeLayout relativeLayout;

        String song_name, song_path;

        int position = 0;

        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    switch (intent.getAction()) {
                        case "song.mp3.changed" :
                            if (songsRecycle != null) {
                                if (songsRecycle.holders != null) {
                                    getSharedPref();
                                    setHolders();
                                }
                            }
                            break;
                        case "fetching.songs" :
                            fetched_layout.setVisibility(View.GONE);
                            fetching_layout.setVisibility(View.VISIBLE);
                            break;
                        case "action_song_deleted.REMOVE_FULL_SONG_OR_FILE_INSIDE_SONG_MAIN_LIST" :
                            int removal_index = intent.getIntExtra("REMOVAL_INDEX", -1);
                            if (removal_index != -1 && songsRecycle != null) {
                                songsRecycle.notifyItemRemoved(removal_index);
                                songsRecycle.notifyItemRangeChanged(removal_index, musicFiles.size());

                                String song_count_str = musicFiles.size() + " Songs";
                                song_count.setText(song_count_str);
                            }
                            break;
                        case "song_is_removed.FROM_SELECTION_SOME_SONGS_OR_ALL_ARE.Removed_For_All_Fragment" :
                            boolean is_some_or_all_songs_are_removed = intent.getBooleanExtra("ITEMS_ARE_REMOVED", false);
                            if (is_some_or_all_songs_are_removed)
                                setRecycler();
                            break;
                    }
                }
            }
        };

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_song, container, false);
            initViews(view);
            doWork();
            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            fab.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));
            Intent intent = new Intent(getContext(), BackService.class);
            requireContext().bindService(intent, SongFragment.this, Context.BIND_AUTO_CREATE);
            IntentFilter filter = new IntentFilter();
            filter.addAction("song.mp3.changed");
            filter.addAction("all_loaded.Start_Second.Activity");
            filter.addAction("fetching.songs");
            filter.addAction("action_song_deleted.REMOVE_FULL_SONG_OR_FILE_INSIDE_SONG_MAIN_LIST");
            filter.addAction("song_is_removed.FROM_SELECTION_SOME_SONGS_OR_ALL_ARE.Removed_For_All_Fragment");
            LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(receiver, filter);

            Intent to_main_for_i_am_resume = new Intent();
            to_main_for_i_am_resume.setAction("action_to_main.SONG_FRAGMENT_RESUMED");
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(to_main_for_i_am_resume);
        }

        @Override
        public void onPause() {
            super.onPause();
            requireContext().unbindService(this);
            LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver);
        }

        private void initViews(View view) {
            // from xml
            recyclerView = view.findViewById(R.id.song_recycler_view);
            song_count = view.findViewById(R.id.songs_count);
            relativeLayout = view.findViewById(R.id.relative_songs);
            fab = view.findViewById(R.id.song_float_btn);
            sort = view.findViewById(R.id.sort);
            fetched_layout = view.findViewById(R.id.fetched_song_layout);
            fetching_layout = view.findViewById(R.id.fetching_song_layout);
            // java classes
        }

        private void doWork() {
            setRecycler();

            relativeLayout.setOnClickListener(view -> {
                if (musicFiles != null) {
                    if (musicFiles.size() > 0) {
                        BackService service_back = new BackService();
                        service_back.setSongSource(musicFiles);
                    } else {
                        Toast.makeText(backService, "No Songs Found", Toast.LENGTH_SHORT).show();
                    }
                }
                Intent intent = new Intent(requireActivity(), BackService.class);
                intent.putExtra(FROMWHERE, ISONGRECYCLE);
                intent.putExtra("position", 0);
                requireActivity().startService(intent);
            });

            sort.setOnClickListener(view1 -> showSortBottomSheet());
        }

        private void setRecycler() {
            // extra work
            fetched_layout.setVisibility(View.VISIBLE);
            fetching_layout.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);
            if (musicFiles != null) {
                if (!(musicFiles.size() < 1)) {
                    LinearLayoutManager layout = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                    songsRecycle = new SongsRecycle(requireActivity(), musicFiles, requireActivity().getSupportFragmentManager());
                    recyclerView.setAdapter(songsRecycle);
                    recyclerView.setLayoutManager(layout);
                }
                String song_count_str = musicFiles.size() + " Songs";
                song_count.setText(song_count_str);
            }
        }


        // for Service
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BackService.LocalBinder binder = (BackService.LocalBinder) iBinder;
            backService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            backService = null;
        }

        public void showSortBottomSheet() {
            SortLayoutBottomFragment sort_fragment = new SortLayoutBottomFragment();
            sort_fragment.show(requireActivity().getSupportFragmentManager(), "ABC");
        }

        private void getSharedPref() {
            SharedPreferences preferences = requireActivity().getSharedPreferences("PLAYING", MODE_PRIVATE);
            song_name = preferences.getString(SONGNAME, null);
            song_path = preferences.getString(SONGPATH, null);
            position = preferences.getInt(SONGPOSITION, 0);
        }

        private void setHolders() {
            if (previous_holder != null) {
                if (color != null) {
                    previous_holder.song_name.setTextColor(color);
                    previous_holder.song_artist.setTextColor(color);
                    previous_holder.song_duration.setTextColor(color);
                    previous_holder.song_name.setHorizontallyScrolling(false);
                    previous_holder.song_name.setSelected(false);
                }
            }

            if (songsRecycle.holders.size() > position ) {
                if (musicFiles.get(position).getTitle().equals(song_name)
                        && musicFiles.get(position).getPath().equals(song_path)) {
                    songsRecycle.holders.get(position).song_name.setTextColor(ACCENT_COLOR);
                    songsRecycle.holders.get(position).song_artist.setTextColor(ACCENT_COLOR);
                    songsRecycle.holders.get(position).song_duration.setTextColor(ACCENT_COLOR);
                    songsRecycle.holders.get(position).song_name.setHorizontallyScrolling(true);
                    songsRecycle.holders.get(position).song_name.setSelected(true);
                    previous_holder = songsRecycle.holders.get(position);
                }
            }
        }
}