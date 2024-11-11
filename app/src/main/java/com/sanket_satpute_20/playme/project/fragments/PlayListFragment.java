package com.sanket_satpute_20.playme.project.fragments;

import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.activity.FevouriteActivity;
import com.sanket_satpute_20.playme.project.activity.RecentPlayedActivity;
import com.sanket_satpute_20.playme.project.model.Playlist;
import com.sanket_satpute_20.playme.project.model.PlaylistArray;
import com.sanket_satpute_20.playme.project.recycler_views.PlaylistRecycle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PlayListFragment extends Fragment {

    RelativeLayout fav_card, recent_played;
    public static PlaylistArray array = new PlaylistArray();
    public static Playlist playlist;
    RecyclerView recyclerView;
    PlaylistRecycle adapter;
    RelativeLayout create_new;
    ImageView loved, create_playlist_src;

    // AlertBox For Creating playlist
    AlertDialog dilog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);
        initViews(view);
        onClick();
        if (PlaylistArray.ref != null)
            retrivePlaylist();
        return view;
    }

    private void initViews(View view) {
        fav_card = view.findViewById(R.id.fav_card);
        recyclerView = view.findViewById(R.id.recycler_view_playlist);
        recent_played = view.findViewById(R.id.recent_card);
        create_new = view.findViewById(R.id.create_new_playlist);
        loved = view.findViewById(R.id.loved);
        create_playlist_src = view.findViewById(R.id.create_playlist_src);
        //java
    }

    public void onClick() {

        fav_card.setOnClickListener(view -> {
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),
                    loved, "art_src");
            Intent intent = new Intent(requireActivity(), FevouriteActivity.class);
            startActivity(intent, compat.toBundle());
        });

        recent_played.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), RecentPlayedActivity.class);
            requireActivity().startActivity(intent);
        });

        create_new.setOnClickListener(view -> setDilog());
    }

    public void setDilog() {
        ValueAnimator fade1 = ObjectAnimator.ofFloat(create_playlist_src, "alpha", 0.8f, 0.6f);
        fade1.setDuration(100);
        fade1.setInterpolator(new FastOutSlowInInterpolator());

        ValueAnimator fade2 = ObjectAnimator.ofFloat(create_playlist_src, "alpha", 0.6f ,0.8f);
        fade2.setDuration(100);
        fade2.setInterpolator(new FastOutSlowInInterpolator());

        AnimatorSet animator = new AnimatorSet();
        animator.play(fade1).before(fade2);
        animator.start();

        TextInputLayout playlist_outer, creator_outer;
        EditText playlist_name, user_name;
        Button add_playlist, cancel;
        View layout = LayoutInflater.from(requireActivity()).inflate(R.layout.create_playlist, null);
        playlist_name = layout.findViewById(R.id.playlist_name);
        user_name = layout.findViewById(R.id.user_name);
        add_playlist = layout.findViewById(R.id.add_playlist);
        cancel = layout.findViewById(R.id.cancel_btn);
        playlist_outer = layout.findViewById(R.id.playlist_name_outer);
        creator_outer = layout.findViewById(R.id.creator_name_outer);

        add_playlist.setBackgroundColor(Color.parseColor("#c1c1c1"));
        cancel.setTextColor(ACCENT_COLOR);
        playlist_outer.setHintTextColor(ColorStateList.valueOf(ACCENT_COLOR));
        playlist_outer.setBoxStrokeColor(ACCENT_COLOR);
        creator_outer.setHintTextColor(ColorStateList.valueOf(ACCENT_COLOR));
        creator_outer.setBoxStrokeColor(ACCENT_COLOR);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.input_layout_edit_text_cursor, null);

// Set the desired color
            if (drawable != null) {
                drawable.setColorFilter(ACCENT_COLOR, PorterDuff.Mode.SRC_IN);

                playlist_name.setTextCursorDrawable(drawable);
                user_name.setTextCursorDrawable(drawable);
            }
        }


        playlist_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    if (user_name.getText().length() > 0) {
                        add_playlist.setBackgroundColor(ACCENT_COLOR);
                        add_playlist.setClickable(true);
                    } else {
                        add_playlist.setBackgroundColor(Color.parseColor("#c1c1c1"));
                        add_playlist.setClickable(false);
                    }
                } else {
                    add_playlist.setBackgroundColor(Color.parseColor("#c1c1c1"));
                    add_playlist.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    if (playlist_name.getText().length() > 0) {
                        add_playlist.setBackgroundColor(ACCENT_COLOR);
                        add_playlist.setClickable(true);
                    } else {
                        add_playlist.setBackgroundColor(Color.parseColor("#c1c1c1"));
                        add_playlist.setClickable(false);
                    }
                } else {
                    add_playlist.setBackgroundColor(Color.parseColor("#c1c1c1"));
                    add_playlist.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        add_playlist.setOnClickListener(view -> {
            btnPressed(add_playlist);
            createPlaylist(playlist_name.getText().toString(), user_name.getText().toString());
            if (dilog!=null)
                dilog.dismiss();
        });
        cancel.setOnClickListener(view -> {
            btnPressed(cancel);
            dilog.dismiss();
        });
        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(requireActivity());
        alert.setView(layout);

        dilog = alert.create();
        if (dilog.getWindow() != null)
            dilog.getWindow().getAttributes().windowAnimations = R.style.UpDownDialogAnimation;
        dilog.show();
    }

    public void createPlaylist(String playlist_name, String user_name) {
        if (!(playlist_name.isEmpty() || user_name.isEmpty())) {
            boolean playlistExist = false;
            ArrayList<Playlist> re_find = PlaylistArray.ref;
            if (PlaylistArray.ref != null) {
                for (Playlist list : re_find) {
                    if (list.getPlaylist_name().equals(playlist_name)) {
                        playlistExist = true;
                        break;
                    }
                }
            }
            if (playlistExist) {
                Toast.makeText(requireActivity(), "Playlist Already Exist", Toast.LENGTH_SHORT).show();
            } else {
                playlist = new Playlist();
                playlist.setPlaylist_name(playlist_name);
                playlist.setCreatedBy(user_name);
                playlist.setPlaylist(new ArrayList<>());
                Date calender = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                playlist.setCreatedOn(sdf.format(calender));
                array.setRef(playlist);
                retrivePlaylist();
            }
        } else {
            Toast.makeText(requireActivity(), "You Should to feel the Names", Toast.LENGTH_SHORT).show();
        }
    }

    public void retrivePlaylist() {
        if (PlaylistArray.ref.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layout = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
            adapter = new PlaylistRecycle(requireActivity(), array.getRef());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layout);
        } else
            recyclerView.setVisibility(View.GONE);
    }

    private void btnPressed(View v) {
        ObjectAnimator firstX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.8f);
        ObjectAnimator firstY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.8f);

        ObjectAnimator secondX = ObjectAnimator.ofFloat(v, "scaleX", .8f, 1f);
        ObjectAnimator secondY = ObjectAnimator.ofFloat(v, "scaleY", .8f, 1f);

        AnimatorSet first = new AnimatorSet();
        first.playTogether(firstX, firstY);
        first.setInterpolator(new LinearInterpolator());

        AnimatorSet second = new AnimatorSet();
        second.playTogether(secondX, secondY);
        second.setInterpolator(new BounceInterpolator());

        AnimatorSet set = new AnimatorSet();
        set.play(first).before(second);
        set.setDuration(100);
        set.start();
    }
}