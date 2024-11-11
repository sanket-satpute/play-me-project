package com.sanket_satpute_20.playme.project.fragments;

import static com.sanket_satpute_20.playme.MainActivity.albumFilesList;
import static com.sanket_satpute_20.playme.MainActivity.artistFiles;
import static com.sanket_satpute_20.playme.MainActivity.musicFiles;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.model.Albums;
import com.sanket_satpute_20.playme.project.model.Artists;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.recycler_views.AlbumsRecycle;
import com.sanket_satpute_20.playme.project.recycler_views.ArtistRecycle;
import com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 107;
    final int ANIMATION_DELAY_TIME = 5000;
    final int ANIMATION_DURATION = 1000;

    SearchView searchView;
    TextView text_info, songs, artists, albums, somore, artmore, albmore, search_hint_txt;
    RelativeLayout song_layout, album_layout, artist_layout;
    LinearLayout song_more, album_more, artist_more;
    RecyclerView song_recycler, album_recycler, artist_recycler;
    boolean songExpanded, artistExpanded, albumExpanded;
    boolean isSong, isAlbum, isArtist;

    ImageView search_voice_recognition;

    int viewHeight ,expandHeight , i = 0;

    public static String query;

    String []search_hints = {"Search Here", "Search For Song's", "Search For Album's", "Search For Artist's", "Search Song, Album, Artist…"};

    AlbumsRecycle albumAdapter;

    ArrayList<Albums> albumFiles;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals("action.from_grid_or_non_grid_album_recycler_view.ITEM_REMOVED.FOR_0_Songs")) {
                    String fromHere = intent.getStringExtra("THIS_ALBUM_RECYCLER_FROM");
                    if (fromHere.equals("AlbumFragment")) {
                        int r_position = intent.getIntExtra("REMOVED_ITEM_POSITION", -1);
                        Albums album_came = (Albums) intent.getSerializableExtra("IN_PARENT_LIST_ITEM_PRESENT_FULL_ALBUM");
                        if (albumFiles.size() > r_position && r_position > -1) {
                            if (album_came.getAlbum_name().equals(albumFiles.get(r_position).getAlbum_name())
                                    && album_came.getAlbum_files().size() == albumFiles.get(r_position).getAlbum_files().size()) {
                                if (albumFiles != null) {
                                    if (albumFiles.size() > r_position) {
                                        albumFiles.remove(r_position);
                                        if (albumAdapter != null) {
                                            albumAdapter.notifyItemRemoved(r_position);
                                            albumAdapter.notifyItemRangeRemoved(r_position, albumFiles.size());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initViews(view);
        querySearching();
        onClick();
        setAnimationOfSearchView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("action.from_grid_or_non_grid_album_recycler_view.ITEM_REMOVED.FOR_0_Songs");
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver);
    }

    public void voiceRecognitionActivated(ArrayList<String> searchingList) {
        try {
            search_hint_txt.setVisibility(View.GONE);
            searchView.setQuery(searchingList.get(0), false);
            searchForSongs(searchingList.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onClick() {
        viewHeight = 550;
        expandHeight = this.getResources().getDisplayMetrics().heightPixels;

        searchView.setOnQueryTextFocusChangeListener((view, b) -> {
            if (b) {
                search_hint_txt.setVisibility(View.GONE);
            } else if (searchView.getQuery().length() <= 0) {
                search_hint_txt.setVisibility(View.VISIBLE);
            }
        });

        search_voice_recognition.setOnClickListener(view -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech Recognition");
            ((Activity) requireActivity()).startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
        });

        song_more.setOnClickListener(view -> {
            songExpanded = !songExpanded;
            if (songExpanded) {
                ValueAnimator albX = ObjectAnimator.ofFloat(album_layout, "scaleX", 1, 0);
                albX.setDuration(200);
                ValueAnimator artX = ObjectAnimator.ofFloat(artist_layout, "scaleX", 1, 0);
                artX.setDuration(200);

                AnimatorSet animator = new AnimatorSet();
                animator.play(albX).before(artX);

                AnimatorSet set = new AnimatorSet();
                set.play(animator);//.before(extend);
                set.start();
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        album_layout.setVisibility(View.GONE);
                        artist_layout.setVisibility(View.GONE);
                        ValueAnimator am = ValueAnimator.ofInt(viewHeight, expandHeight);
                        am.addUpdateListener(valueAnimator -> {
                            int value = (Integer) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams params = song_layout.getLayoutParams();
                            params.height = value;
                            ViewGroup.LayoutParams params2 = song_recycler.getLayoutParams();
                            params2.height = value;
                            song_layout.setLayoutParams(params);
                            song_recycler.setLayoutParams(params2);
                        });
                        am.setDuration(400);
                        am.start();
                    }
                });
                somore.setText(R.string.see_less);
            } else {
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(expandHeight, viewHeight);
                valueAnimator.addUpdateListener(valueAnimator1 -> {
                    float value = Float.parseFloat(String.valueOf(valueAnimator1.getAnimatedValue()));
                    ViewGroup.LayoutParams params = song_layout.getLayoutParams();
                    params.height = (int)value;
                    ViewGroup.LayoutParams params2 = song_recycler.getLayoutParams();
                    params2.height = (int)value;
                    song_layout.setLayoutParams(params);
                    song_recycler.setLayoutParams(params2);
                });
                valueAnimator.setDuration(400);
                valueAnimator.start();
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ValueAnimator albX = ObjectAnimator.ofFloat(album_layout, "scaleX", 0, 1);
                        albX.setDuration(200);
                        ValueAnimator artX = ObjectAnimator.ofFloat(artist_layout, "scaleX", 0, 1);
                        artX.setDuration(200);
                        album_layout.setScaleX(0);
                        artist_layout.setScaleX(0);
                        if (isAlbum)
                            album_layout.setVisibility(View.VISIBLE);
                        if (isArtist)
                            artist_layout.setVisibility(View.VISIBLE);
                        AnimatorSet set = new AnimatorSet();
                        set.play(albX).before(artX);
                        set.start();
                    }
                });
                somore.setText(R.string.more);
            }
        });

        album_more.setOnClickListener(view -> {
            albumExpanded = !albumExpanded;
            if (albumExpanded) {
                ValueAnimator sonX = ObjectAnimator.ofFloat(song_layout, "scaleX", 1, 0);
                sonX.setDuration(200);
                ValueAnimator artX = ObjectAnimator.ofFloat(artist_layout, "scaleX", 1, 0);
                artX.setDuration(200);

                AnimatorSet animator = new AnimatorSet();
                animator.play(sonX).before(artX);

                AnimatorSet set = new AnimatorSet();
                set.play(animator);
                set.start();
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        song_layout.setVisibility(View.GONE);
                        artist_layout.setVisibility(View.GONE);
                        ValueAnimator am = ValueAnimator.ofInt(viewHeight, expandHeight);
                        am.addUpdateListener(valueAnimator -> {
                            int value = (Integer) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams params = album_layout.getLayoutParams();
                            params.height = value;
                            ViewGroup.LayoutParams params2 = album_recycler.getLayoutParams();
                            params2.height = value;
                            album_layout.setLayoutParams(params);
                            album_recycler.setLayoutParams(params2);
                        });
                        am.setDuration(400);
                        am.start();
                    }
                });
                albmore.setText(R.string.see_less);
            } else {
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(expandHeight, viewHeight);
                valueAnimator.addUpdateListener(valueAnimator12 -> {
                    float value = Float.parseFloat(String.valueOf(valueAnimator12.getAnimatedValue()));
                    ViewGroup.LayoutParams params = album_layout.getLayoutParams();
                    params.height = (int)value;
                    ViewGroup.LayoutParams params2 = album_recycler.getLayoutParams();
                    params2.height = (int)value;
                    album_layout.setLayoutParams(params);
                    album_recycler.setLayoutParams(params2);
                });
                valueAnimator.setDuration(400);
                valueAnimator.start();
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ValueAnimator sonX = ObjectAnimator.ofFloat(song_layout, "scaleX", 0, 1);
                        sonX.setDuration(200);
                        ValueAnimator artX = ObjectAnimator.ofFloat(artist_layout, "scaleX", 0, 1);
                        artX.setDuration(200);
                        song_layout.setScaleX(0);
                        artist_layout.setScaleX(0);
                        if (isSong)
                            song_layout.setVisibility(View.VISIBLE);
                        if (isArtist)
                            artist_layout.setVisibility(View.VISIBLE);
                        AnimatorSet set = new AnimatorSet();
                        set.play(sonX).before(artX);
                        set.start();
                    }
                });
                albmore.setText(R.string.more);
            }
        });

        artist_more.setOnClickListener(view -> {
            artistExpanded = !artistExpanded;
            if (artistExpanded) {
                ValueAnimator albX = ObjectAnimator.ofFloat(album_layout, "scaleX", 1, 0);
                albX.setDuration(200);
                ValueAnimator sonX = ObjectAnimator.ofFloat(song_layout, "scaleX", 1, 0);
                sonX.setDuration(200);

                AnimatorSet animator = new AnimatorSet();
                animator.play(albX).before(sonX);

                AnimatorSet set = new AnimatorSet();
                set.play(animator);
                set.start();
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        album_layout.setVisibility(View.GONE);
                        song_layout.setVisibility(View.GONE);
                        ValueAnimator am = ValueAnimator.ofInt(viewHeight, expandHeight);
                        am.addUpdateListener(valueAnimator -> {
                            int value = (Integer) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams params = artist_layout.getLayoutParams();
                            params.height = value;
                            ViewGroup.LayoutParams params2 = artist_recycler.getLayoutParams();
                            params2.height = value;
                            artist_layout.setLayoutParams(params);
                            artist_recycler.setLayoutParams(params2);
                        });
                        am.setDuration(400);
                        am.start();
                    }
                });
                artmore.setText(R.string.see_less);
            } else {
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(expandHeight, viewHeight);
                valueAnimator.addUpdateListener(valueAnimator13 -> {
                    float value = Float.parseFloat(String.valueOf(valueAnimator13.getAnimatedValue()));
                    ViewGroup.LayoutParams params = artist_layout.getLayoutParams();
                    params.height = (int)value;
                    ViewGroup.LayoutParams params2 = artist_recycler.getLayoutParams();
                    params2.height = (int)value;
                    artist_layout.setLayoutParams(params);
                    artist_recycler.setLayoutParams(params2);
                });
                valueAnimator.setDuration(400);
                valueAnimator.start();
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ValueAnimator albX = ObjectAnimator.ofFloat(album_layout, "scaleX", 0, 1);
                        albX.setDuration(200);
                        ValueAnimator sonX = ObjectAnimator.ofFloat(song_layout, "scaleX", 0, 1);
                        sonX.setDuration(200);
                        album_layout.setScaleX(0);
                        song_layout.setScaleX(0);
                        if (isAlbum)
                            album_layout.setVisibility(View.VISIBLE);
                        if (isSong)
                            song_layout.setVisibility(View.VISIBLE);
                        AnimatorSet set = new AnimatorSet();
                        set.play(albX).before(sonX);
                        set.start();
                    }
                });
                artmore.setText(R.string.more);
            }
        });
    }

    private void initViews(View view) {
        searchView = view.findViewById(R.id.search_view);
        songs = view.findViewById(R.id.songtxt);
        artists = view.findViewById(R.id.artisttxt);
        albums = view.findViewById(R.id.albumtxt);
        text_info = view.findViewById(R.id.text_info);
        song_layout = view.findViewById(R.id.relative_song);
        album_layout = view.findViewById(R.id.relative_album);
        artist_layout = view.findViewById(R.id.relative_artist);
        song_more = view.findViewById(R.id.more);
        album_more = view.findViewById(R.id.more_album);
        artist_more = view.findViewById(R.id.more_artist);
        song_recycler = view.findViewById(R.id.song_recycle);
        artist_recycler = view.findViewById(R.id.artist_recycle);
        album_recycler = view.findViewById(R.id.album_recycle);
        somore = view.findViewById(R.id.smore);
        artmore = view.findViewById(R.id.artmore);
        albmore = view.findViewById(R.id.albumore);

        search_voice_recognition = view.findViewById(R.id.search_voice_recognition);
        search_hint_txt = view.findViewById(R.id.search_hint_txt);
    }

    public void querySearching() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchForSongs(newText);
                return true;
            }
        });
    }

    private void searchForSongs(String newText) {
        if (newText != null) {
            query = newText.trim();
            String userText = newText.toLowerCase();
            ArrayList<MusicFiles> songFiles = new ArrayList<>();
            ArrayList<Albums> albumFiles = new ArrayList<>();
            ArrayList<Artists> artistF = new ArrayList<>();
            boolean sname, artistName, albumName;
            for (MusicFiles song : musicFiles) {
                sname = song.getTitle().toLowerCase().contains(userText);
                if (sname) {
                    songFiles.add(song);
                }
            }
            for (Albums albums : albumFilesList) {
                if (albums.getAlbum_name() == null)
                    continue;
                albumName = albums.getAlbum_name().toLowerCase().contains(userText);
                if (albumName) {
                    albumFiles.add(albums);
                }
            }
            for (Artists artists : artistFiles) {
                if (artists.getArtistName() == null)
                    continue;
                artistName = artists.getArtistName().toLowerCase().contains(userText);
                if (artistName) {
                    if (!artists.getArtistName().trim().isEmpty())
                        artistF.add(artists);
                }
            }
            setSongRecycle(songFiles);
            setAlbumRecycle(albumFiles);
            setArtistRecycle(artistF);
            if (songFiles.size() > 0 || albumFiles.size() > 0 || artistFiles.size() > 0) {
                text_info.setVisibility(View.INVISIBLE);
            } else {
                text_info.setVisibility(View.VISIBLE);
                text_info.setText(R.string.no_result_found);
            }
            if (userText.isEmpty() || userText.equals(" ")) {
                text_info.setVisibility(View.VISIBLE);
                text_info.setText(R.string.search);
                song_layout.setVisibility(View.GONE);
                album_layout.setVisibility(View.GONE);
                artist_layout.setVisibility(View.GONE);
            }
        } else {
            text_info.setVisibility(View.VISIBLE);
            text_info.setText(R.string.search);
            song_layout.setVisibility(View.GONE);
            album_layout.setVisibility(View.GONE);
            artist_layout.setVisibility(View.GONE);
        }
    }

    public void setSongRecycle(ArrayList<MusicFiles> songs) {
        if (songs.size() > 0) {
            song_layout.setVisibility(View.VISIBLE);
            song_recycler.setHasFixedSize(true);
            song_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            song_recycler.setAdapter(new SongsRecycle(requireActivity(), songs, requireActivity().getSupportFragmentManager()));
            isSong = true;
        } else {
            song_layout.setVisibility(View.GONE);
            isSong = false;
        }
    }

    private void setArtistRecycle(ArrayList<Artists> artistFiles) {
        if (artistFiles.size() > 0) {
            artist_layout.setVisibility(View.VISIBLE);
            artist_recycler.setHasFixedSize(true);
            artist_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            artist_recycler.setAdapter(new ArtistRecycle(requireActivity(), artistFiles));
            isArtist = true;
        } else {
            artist_layout.setVisibility(View.GONE);
            isArtist = false;
        }
    }

    private void setAlbumRecycle(ArrayList<Albums> albumFiles) {
        this.albumFiles = albumFiles;
        if (albumFiles.size() > 0) {
            album_layout.setVisibility(View.VISIBLE);
            album_recycler.setHasFixedSize(true);
            album_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            albumAdapter = new AlbumsRecycle(requireContext(), albumFiles, requireActivity(), requireActivity().getSupportFragmentManager(), "SearchFragment");
            album_recycler.setAdapter(albumAdapter);
            isAlbum = true;
        } else {
            album_layout.setVisibility(View.GONE);
            isAlbum = false;
        }
    }

    private void setAnimationOfSearchView() {
        if (i >= (search_hints.length - 1))
            i = 0;
        else
            i++;

        ObjectAnimator starter_trans = ObjectAnimator.ofFloat(search_hint_txt, "translationY", 0, -20f);
        ObjectAnimator starter_alpha = ObjectAnimator.ofFloat(search_hint_txt, "alpha", 0.5f, 0f);
        AnimatorSet starter = new AnimatorSet();
        starter.setStartDelay(ANIMATION_DELAY_TIME);
        starter.playTogether(starter_alpha, starter_trans);
        starter.setDuration(ANIMATION_DURATION);
        starter.start();

        starter_trans.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                search_hint_txt.setText(search_hints[i]);
                ObjectAnimator end_trans = ObjectAnimator.ofFloat(search_hint_txt, "translationY", 20f, 0f);
                ObjectAnimator end_alpha = ObjectAnimator.ofFloat(search_hint_txt, "alpha", 0f, 0.5f);
                AnimatorSet end = new AnimatorSet();
                end.playTogether(end_trans, end_alpha);
                end.setDuration(ANIMATION_DURATION);
                end.start();

                end.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        setAnimationOfSearchView();
                    }
                });
            }
        });
    }
}