package com.sanket_satpute_20.playme.project.bottom_sheet_fragment;

import static com.sanket_satpute_20.playme.MainActivity.favouriteList;
import static com.sanket_satpute_20.playme.project.extra_stuffes.CommonMethods.REQUEST_PERMISSION_DELETE;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.IMAGEOFSONG;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.PASSEDSONGFILE;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGALBUM;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGARTIST;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGDURATION;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGNAME;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGPATH;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONG_EXTRA;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.service.BackService.SONGPOSITION;
import static com.sanket_satpute_20.playme.project.service.BackService.recentPlayed;
import static com.sanket_satpute_20.playme.project.service.BackService.song;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.activity.ShowSongInfoActivity;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.model.Playlist;
import com.sanket_satpute_20.playme.project.model.PlaylistArray;
import com.sanket_satpute_20.playme.project.recycler_views.PlaylistRecycleCreatePlaylist;
import com.sanket_satpute_20.playme.project.service.BackService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BSheetSongsMoreFragment extends BottomSheetDialogFragment implements ServiceConnection {

//    service
    BackService backService;

    public static int delete_song_removing_position = -1;
    public static final int UPDATE_SONG_TAG_REQUEST = 1021;

    MusicFiles music;

    ImageView song_src, isFavourite;
    RelativeLayout song_info, delete_song, play_next, add_to_playlist, share;
    TextView song_name, artist_album_name;

    int position;
    byte[] image;
    SharedPreferences preferences;

    /*      Add To Playlist    */
    MaterialCheckBox recent_check, favourite_check;
    RelativeLayout recent_layout, favourite_layout, create_new_playlist;
    MaterialButton save_btn;
    RecyclerView recyclerView;

    PlaylistArray array;
    Playlist playlist;
    PlaylistRecycleCreatePlaylist adapter;
    AlertDialog dilog_0;

    MaterialCardView divider;

    Activity context;

    int default_color;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.b_sheet_songs_more, container, false);
        initView(view);
        getBundleX();
        doExtra();
        onClick();
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        context = requireActivity();
        return new BottomSheetDialog(context, R.style.CustomBottomSheetDialog);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getContext(), BackService.class);
        requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unbindService(this);
    }

    private void getBundleX() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            music = (MusicFiles) bundle.getSerializable(PASSEDSONGFILE);
            position = bundle.getInt(SONGPOSITION);
            image = bundle.getByteArray(IMAGEOFSONG);
        }
    }

    private void initView(View view) {
        song_info = view.findViewById(R.id.details);
        delete_song = view.findViewById(R.id.delete);
        play_next = view.findViewById(R.id.play_next);
        add_to_playlist = view.findViewById(R.id.add_to);
        share = view.findViewById(R.id.share);
        divider = view.findViewById(R.id.divider);

        song_src = view.findViewById(R.id.song_src);
        isFavourite = view.findViewById(R.id.fevourite);
        song_name = view.findViewById(R.id.song_name);
        artist_album_name = view.findViewById(R.id.artist_album_name);
    }

    private void doExtra() {
        divider.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));

        preferences = context.getSharedPreferences("PLAYING", Context.MODE_PRIVATE);
        song_name.setSelected(true);
        artist_album_name.setSelected(true);

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                default_color = 0xddffffff;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                default_color = 0xdd000000;
                break;
        }

        if (favouriteList.contains(music)) {
            isFavourite.setImageResource(R.drawable.heart_filled_icon_24);
            isFavourite.setColorFilter(ACCENT_COLOR);
        } else {
            isFavourite.setImageResource(R.drawable.heart_no_fill_24);
            isFavourite.setColorFilter(default_color);
        }

        song_name.setText(music.getTitle());
        artist_album_name.setText(String.format("%s | %s", music.getAlbum(), music.getArtist()));
        Glide.with((Context) context).asBitmap()
                .override(150, 150)
                .load(image)
                .placeholder(R.mipmap.ic_music)
                .into(song_src);
    }

    private void onClick() {
        isFavourite.setOnClickListener(view1 -> {
            if (favouriteList.contains(music)) {
                isFavourite.setColorFilter(default_color);
                isFavourite.setImageResource(R.drawable.heart_no_fill_24);
                unFavouriteClicked(isFavourite);
                favouriteList.remove(music);
            } else {
                isFavourite.setColorFilter(ACCENT_COLOR);
                isFavourite.setImageResource(R.drawable.heart_filled_icon_24);
                favouriteBtnClicked(isFavourite);
                favouriteList.add(music);
            }
        });

        song_info.setOnClickListener(s -> {
            showSongInfo(position);
            this.dismiss();
        });

        delete_song.setOnClickListener(s -> {

////            cut this code and put when song is really get deleted
//            if (CURRENT_PLAYING_SONG_PATH != null) {
//                if (music.getPath().trim().equals(CURRENT_PLAYING_SONG_PATH)/* && backService != null*/) {
//                    backService.setNextButtonClicked();
//                }
//            }

            BSheetSongsMoreFragment.this.dismiss();

//            Intent broad_item_removed = new Intent(context, MyAppReceiver.class);
//            broad_item_removed.setAction("song_recycler.SONG_IS_DELETED.Action");
//            broad_item_removed.putExtra("DELETED_ITEM_POSITION_EXTRAS", position);
//            context.sendBroadcast(broad_item_removed);

            File file = new File(music.getPath());
            Uri path_uri = Uri.fromFile(file);
            Uri uri = getContentUri(path_uri);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                MaterialButton cancel, delete;
                TextView song_name;

                View v_w = LayoutInflater.from(context).inflate(R.layout.delete_song_dilog, null);
                MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);
                cancel = v_w.findViewById(R.id.cancel);
                delete = v_w.findViewById(R.id.delete);
                song_name = v_w.findViewById(R.id.song_name);
                dialog.setView(v_w);
                song_name.setText(music.getTitle());

                AlertDialog delete_dialog = dialog.create();
                delete_dialog.show();

                cancel.setOnClickListener(vi -> delete_dialog.dismiss());

                delete.setOnClickListener(vi -> {
                    try {
                        int del_value = deleteApi28(uri);
                        if (del_value > 0) {
                            Intent broad_item_removed = new Intent();
                            broad_item_removed.setAction("song_recycler.SONG_IS_DELETED.Action");
                            broad_item_removed.putExtra("DELETED_ITEM_POSITION_EXTRAS", position);
                            context.sendBroadcast(broad_item_removed);

                            Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "File can't be deleted", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                    }
                    delete_dialog.dismiss();
                });
            } else {
                deleteSongAPI29OrGreater(uri, position);
            }
        });

        play_next.setOnClickListener(s -> {
            playNext();
            this.dismiss();
        });

        add_to_playlist.setOnClickListener(s -> {
                addToPlaylistDilog();
                BSheetSongsMoreFragment.this.dismiss();
        });

        share.setOnClickListener(s -> {
            shareFile();
            this.dismiss();
        });
    }

//    using but not functionality
    private Uri getContentUri(Uri fileUri) {
        String[] projections = {MediaStore.MediaColumns._ID};
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projections,
                MediaStore.MediaColumns.DATA + "=?",
                new String[] {fileUri.getPath()},
                null
        );
        long id = 0;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
            }
            cursor.close();
        }
        return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf((int) id));
    }

    /*      Extra Methods       */
    public void showSongInfo(int position) {
        Intent intent = new Intent(context, ShowSongInfoActivity.class);
        intent.putExtra(SONG_EXTRA, music);
        intent.putExtra(SONGPOSITION, position);
        intent.putExtra(SONGNAME, music.getTitle());
        intent.putExtra(SONGARTIST, music.getArtist());
        intent.putExtra(SONGALBUM, music.getAlbum());
        intent.putExtra(SONGPATH, music.getPath());
        intent.putExtra(SONGDURATION, music.getDuration());
        context.startActivity(intent);
    }

    public void addToPlaylistDilog() {

        View v = LayoutInflater.from(context).inflate(R.layout.layout_add_to_playlist_dilouge, null);
        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
        alert.setView(v);
        AlertDialog dilog = alert.create();
        dilog.show();

        /*  Initializing Views  */
        recent_check = v.findViewById(R.id.recent_played_add_checkbox);
        favourite_check = v.findViewById(R.id.fevourite_add_checkbox);
        recent_layout = v.findViewById(R.id.recent_played_add);
        favourite_layout = v.findViewById(R.id.favourite_add);
        create_new_playlist = v.findViewById(R.id.create_new_playlist);
        save_btn = v.findViewById(R.id.save_btn_add_playlist);
        recyclerView = v.findViewById(R.id.recycler_view_add_playlist_dilog);
        array = new PlaylistArray();

        save_btn.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));

        retrievePlaylist();

        recent_layout.setOnClickListener(view -> recent_check.setChecked(!recent_check.isChecked()));
        favourite_layout.setOnClickListener(view -> favourite_check.setChecked(!favourite_check.isChecked()));

        create_new_playlist.setOnClickListener(view -> {
            createNewPlaylist();
            dilog.dismiss();
        });

        save_btn.setOnClickListener(view -> {
            if (recent_check.isChecked()) recentPlayed.add(music);
            if (favourite_check.isChecked()) favouriteList.add(music);
            boolean added_to_playlist = false;
            if (adapter != null) {
                int[] selected_playlist = adapter.returnSelectedList();
                for (int i = 0; i < selected_playlist.length; i++) {
                    if (selected_playlist[i] == 1) {
                        PlaylistArray.ref.get(i).getPlaylist().add(music);
                        added_to_playlist = true;
                    }
                }
            }
            if (added_to_playlist || recent_check.isChecked() || favourite_check.isChecked())
                Toast.makeText(context, "Added to playlist", Toast.LENGTH_SHORT).show();
            dilog.dismiss();
        });

    }

    private void playNext() {
        int nextPos;
        nextPos = preferences.getInt(SONGPOSITION, 0);
        if (song != null)
            if (song.size() > 0)
                song.set(nextPos+1, music);
    }

    private void shareFile() {
        String sender_path = music.getPath();
        if (sender_path != null)
        {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(android.content.Intent.ACTION_SEND_MULTIPLE);
            ArrayList<Uri> uris = new ArrayList<>();
            intent.setType("*/*");
            File file = new File(sender_path);

            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            uris.add(uri);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            context.startActivity(intent);
        }
    }

    private void deleteSongAPI29OrGreater(Uri uri, int removal_position) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    context.getContentResolver().delete(uri, null);
                int del_value = deleteApi28(uri);
                if (del_value > 0) {
                    Intent broad_item_removed = new Intent();
                    broad_item_removed.setAction("song_recycler.SONG_IS_DELETED.Action");
                    broad_item_removed.putExtra("DELETED_ITEM_POSITION_EXTRAS", removal_position);
                    context.sendBroadcast(broad_item_removed);

                    Toast.makeText(context, "File deleted ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "File can't be deleted", Toast.LENGTH_SHORT).show();
                }
            } else {
                try {
                    deleteApi30(uri, removal_position);
                } catch (Exception b) {
                    b.printStackTrace();
                    Toast.makeText(context, "File can't be deleted", Toast.LENGTH_SHORT).show();
                }
            }
        } catch(Exception e) {
            try {
                deleteApi30(uri, removal_position);
            } catch (Exception b) {
                b.printStackTrace();
                Toast.makeText(context, "File can't be deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int deleteApi28(Uri songUri) {
        ContentResolver contentResolver = context.getContentResolver();
        return contentResolver.delete(songUri, null, null);
    }

    private void deleteApi30(Uri songUri, int removal_position) {
        ContentResolver contentResolver = context.getContentResolver();
        List<Uri> uriList = new ArrayList<>();
        Collections.addAll(uriList, songUri);
        try {
            PendingIntent pendingIntent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pendingIntent = MediaStore.createDeleteRequest(contentResolver, uriList);
            }
            if (pendingIntent != null) {
                delete_song_removing_position = removal_position;
                context.startIntentSenderForResult(pendingIntent.getIntentSender(), REQUEST_PERMISSION_DELETE, null, 0,
                        0, 0, null);
            } else {
                Toast.makeText(context, "File can't be deleted", Toast.LENGTH_SHORT).show();
            }
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            Toast.makeText(context, "File can't be deleted", Toast.LENGTH_SHORT).show();
        }
    }

    public void createPlaylist(String playlist_name, String user_name) {
        if (!(playlist_name.isEmpty() || user_name.isEmpty())) {
            boolean playlistExist = false;
            ArrayList<Playlist> re_find = PlaylistArray.ref;
            for (Playlist list : re_find) {
                if (list.getPlaylist_name().equals(playlist_name)) {
                    playlistExist = true;
                    break;
                }
            }
            if (playlistExist) {
                Toast.makeText(context, "Playlist Already Exist", Toast.LENGTH_SHORT).show();
            } else {
                playlist = new Playlist();
                playlist.setPlaylist_name(playlist_name);
                playlist.setCreatedBy(user_name);
                playlist.setPlaylist(new ArrayList<>());
                Date calender = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                playlist.setCreatedOn(sdf.format(calender));
                PlaylistArray.ref.add(playlist);
                retrievePlaylist();
            }
        } else {
            Toast.makeText(context, "You Should to feel the Names", Toast.LENGTH_SHORT).show();
        }
    }

    private void retrievePlaylist() {
        if (PlaylistArray.ref.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layout = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
            adapter = new PlaylistRecycleCreatePlaylist(context, array.getRef(), music);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layout);
        } else
            recyclerView.setVisibility(View.GONE);
    }

    private void createNewPlaylist() {

        EditText playlist_name, user_name;
        Button add_playlist, cancel;
        View layout = LayoutInflater.from(context).inflate(R.layout.create_playlist, null);
        playlist_name = layout.findViewById(R.id.playlist_name);
        user_name = layout.findViewById(R.id.user_name);
        add_playlist = layout.findViewById(R.id.add_playlist);
        cancel = layout.findViewById(R.id.cancel_btn);

        cancel.setTextColor(ACCENT_COLOR);
        add_playlist.setBackgroundColor(Color.parseColor("#c1c1c1"));

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
            createPlaylist(playlist_name.getText().toString(), user_name.getText().toString());
            if (dilog_0!=null) {
                addToPlaylistDilog();
                dilog_0.dismiss();
            }
        });
        cancel.setOnClickListener(view -> {
            addToPlaylistDilog();
            dilog_0.dismiss();
        });
        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
        alert.setView(layout);

        dilog_0 = alert.create();
        if (dilog_0.getWindow() != null)
            dilog_0.getWindow().getAttributes().windowAnimations = R.style.UpDownDialogAnimation;
        dilog_0.show();
    }

    private void favouriteBtnClicked(ImageView v) {
        ObjectAnimator firstX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.4f);
        ObjectAnimator firstY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.4f);

        ObjectAnimator secondX = ObjectAnimator.ofFloat(v, "scaleX", 1.4f,  .7f);
        ObjectAnimator secondY = ObjectAnimator.ofFloat(v, "scaleY", 1.4f,  .7f);

        ObjectAnimator thirdX = ObjectAnimator.ofFloat(v, "scaleX", .7f, 1.2f);
        ObjectAnimator thirdY = ObjectAnimator.ofFloat(v, "scaleY", .7f, 1.2f);

        ObjectAnimator fourthX = ObjectAnimator.ofFloat(v, "scaleX", 1.2f, 1f);
        ObjectAnimator fourthY = ObjectAnimator.ofFloat(v, "scaleY", 1.2f, 1f);

        AnimatorSet first = new AnimatorSet();
        first.playTogether(firstX, firstY);
        first.setInterpolator(new AccelerateInterpolator());

        AnimatorSet second = new AnimatorSet();
        second.playTogether(secondX, secondY);
        second.setInterpolator(new DecelerateInterpolator());

        AnimatorSet third = new AnimatorSet();
        third.playTogether(thirdX, thirdY);
        third.setInterpolator(new DecelerateInterpolator());

        AnimatorSet fourth = new AnimatorSet();
        fourth.playTogether(fourthX, fourthY);
        fourth.setInterpolator(new LinearInterpolator());

        AnimatorSet set1 = new AnimatorSet();
        set1.play(first).before(second);

        AnimatorSet set2 = new AnimatorSet();
        set2.play(third).before(fourth);

        AnimatorSet set = new AnimatorSet();
        set.play(set1).before(set2);
        set.setDuration(100);
        set.start();
    }

    private void unFavouriteClicked(View v) {
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

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        BackService.LocalBinder binder = (BackService.LocalBinder) service;
        backService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        backService = null;
    }
}
