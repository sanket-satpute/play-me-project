package com.sanket_satpute_20.playme.project.bottom_sheet_fragment;

import static com.sanket_satpute_20.playme.MainActivity.favouriteAlbums;
import static com.sanket_satpute_20.playme.MainActivity.favouriteList;
import static com.sanket_satpute_20.playme.project.extra_stuffes.CommonMethods.REQUEST_PERMISSION_DELETE_ALBUM;
import static com.sanket_satpute_20.playme.project.recycler_views.AlbumsRecycle.ALBUMCLASS;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.service.BackService.recentPlayed;
import static com.sanket_satpute_20.playme.project.service.BackService.song;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.sanket_satpute_20.playme.project.model.Albums;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.model.Playlist;
import com.sanket_satpute_20.playme.project.model.PlaylistArray;
import com.sanket_satpute_20.playme.project.receivers.MyAppReceiver;
import com.sanket_satpute_20.playme.project.recycler_views.PlaylistRecycleCreatePlaylist;
import com.sanket_satpute_20.playme.project.service.BackService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BottomDialogAlbumFragment extends BottomSheetDialogFragment implements ServiceConnection {


    public static ArrayList<MusicFiles> DELETING_MUSIC_FILES_OF_ALBUM = null;
    Albums album;
    BackService service;

    public static final String ALBUM_NAME_F = "ALBUM_NAME_F";
    public static final String ALBUM_COUNT_F = "ALBUM_COUNT_F";
    public static final String ALBUM_PATH_F = "ALBUM_PATH_F";

    TextView album_name, song_count;
    ImageView album_art, favorite;
    RelativeLayout play_all, add_to_queue, add_to_playlist, delete_from_device;
    ColorFilter color;
    MaterialCardView divider;

    String name = "Album", path;
    int count;

    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog);
        View view = inflater.inflate(R.layout.bottom_dilog_album, container, false);
        initViews(view);
        context = requireActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            name = bundle.getString(ALBUM_NAME_F);
            count = bundle.getInt(ALBUM_COUNT_F);
            path = bundle.getString(ALBUM_PATH_F);
            album = (Albums) bundle.getSerializable(ALBUMCLASS);
        }
        doExtra();
        onclick();
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialog);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getContext(), BackService.class);
        ((Activity) context).bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((Activity) context).unbindService(this);
    }

    private void initViews(View view) {
        album_name = view.findViewById(R.id.album_name);
        song_count = view.findViewById(R.id.song_count);
        album_art = view.findViewById(R.id.album_src);
        favorite = view.findViewById(R.id.alb_fevourite);
        divider = view.findViewById(R.id.divider);
        color = favorite.getColorFilter();
        /*  Extra Views that can be click able*/
        play_all = view.findViewById(R.id.play_all);
        add_to_queue = view.findViewById(R.id.add_to_queue);
        add_to_playlist = view.findViewById(R.id.add_to_playlist);
        delete_from_device = view.findViewById(R.id.delete_from_device);
    }

    private void doExtra() {

        divider.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));

        album_name.setText(name);
        String song_count_str = count + " Audio";
        song_count.setText(song_count_str);
        if (path != null) {
            MyImageLoad load = new MyImageLoad();
            load.execute(path);
        }

        if (favouriteAlbums.contains(album)) {
            favorite.setImageResource(R.drawable.heart_filled_icon_24);
            favorite.setColorFilter(ACCENT_COLOR);
        } else {
            favorite.setImageResource(R.drawable.heart_no_fill_24);
            favorite.setColorFilter(color);
        }
    }

    private void onclick() {
        if (album != null) {
            play_all.setOnClickListener(view -> {
                song = album.getAlbum_files();
                service.createMedia(0);
                this.dismiss();
            });

            add_to_queue.setOnClickListener(view -> {
                Toast.makeText(getActivity(), "Album Added to Queue", Toast.LENGTH_SHORT).show();
                song.addAll(album.getAlbum_files());
                this.dismiss();
            });

            add_to_playlist.setOnClickListener(view -> {
                addToPlaylistDialog();
                this.dismiss();
            });

            delete_from_device.setOnClickListener(view -> deleteSongAPI29OrGreater());

            favorite.setOnClickListener(view -> {
                if (favouriteAlbums.contains(album)) {
                    favouriteAlbums.remove(album);
                    favorite.setImageResource(R.drawable.heart_no_fill_24);
                    favorite.setColorFilter(color);
                    unFevouriteClicked(favorite);
                } else {
                    favouriteAlbums.add(album);
                    favorite.setImageResource(R.drawable.heart_filled_icon_24);
                    favorite.setColorFilter(ACCENT_COLOR);
                    fevouriteBtnClicked(favorite);
                }
            });
        }
    }

    /*      Add To Playlist    */
    MaterialCheckBox recent_check, favourite_check;
    RelativeLayout recent_layout, favourite_layout, create_new_playlist;
    MaterialButton save_btn;
    RecyclerView recyclerView;

    PlaylistArray array;
    Playlist playlist;
    PlaylistRecycleCreatePlaylist adapter;
    AlertDialog dialog_0;

    public void addToPlaylistDialog() {

        View v = LayoutInflater.from(((Activity) context)).inflate(R.layout.layout_add_to_playlist_dilouge, null);
        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(((Activity) context));
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
            dilog.dismiss();
            createNewPlaylist();
        });

        save_btn.setOnClickListener(view -> {
            if (recent_check.isChecked()) recentPlayed.addAll(album.getAlbum_files());
            if (favourite_check.isChecked()) favouriteList.addAll(album.getAlbum_files());
            boolean added_to_playlist = false;
            if (adapter != null) {
                int[] selected_playlist = adapter.returnSelectedList();
                for (int i = 0; i < selected_playlist.length; i++) {
                    if (selected_playlist[i] == 1) {
                        PlaylistArray.ref.get(i).getPlaylist().addAll(album.getAlbum_files());
                        added_to_playlist = true;
                    }
                }
            }
            if (added_to_playlist || recent_check.isChecked() || favourite_check.isChecked())
                Toast.makeText(context, "Added to playlist", Toast.LENGTH_SHORT).show();
            dilog.dismiss();
        });

    }


    private void createNewPlaylist() {

        EditText playlist_name, user_name;
        Button add_playlist, cancel;
        View layout = LayoutInflater.from(((Activity) context)).inflate(R.layout.create_playlist, null);
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
            if (dialog_0 !=null) {
                addToPlaylistDialog();
                dialog_0.dismiss();
            }
        });
        cancel.setOnClickListener(view -> {
            addToPlaylistDialog();
            dialog_0.dismiss();
        });
        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(((Activity) context));
        alert.setView(layout);

        dialog_0 = alert.create();
        if (dialog_0.getWindow() != null)
            dialog_0.getWindow().getAttributes().windowAnimations = R.style.UpDownDialogAnimation;
        dialog_0.show();
    }

    private void retrievePlaylist() {
        if (PlaylistArray.ref.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layout = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
            adapter = new PlaylistRecycleCreatePlaylist(((Activity) context), array.getRef(), album.getAlbum_files());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layout);
        } else
            recyclerView.setVisibility(View.GONE);
    }

    public void createPlaylist(String playlist_name, String user_name) {
        if (!(playlist_name.isEmpty() || user_name.isEmpty())) {
            boolean playlistExist = false;
            ArrayList<Playlist> refind = PlaylistArray.ref;
            for (Playlist list : refind) {
                if (list.getPlaylist_name().equals(playlist_name)) {
                    playlistExist = true;
                    break;
                }
            }
            if (playlistExist) {
                Toast.makeText(((Activity) context), "Playlist Already Exist", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(((Activity) context), "You Should to feel the Names", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteFullAlbumFromTheDevice() {
//        perform first delete operation here if it successful then after send broadcast for removal it
        BottomDialogAlbumFragment.this.dismiss();
        Intent broad_deleted_songs_removing = new Intent(requireContext(), MyAppReceiver.class);
        broad_deleted_songs_removing.setAction("songs_deleted.SONG_IS_DELETED.Multiple_Album_Artist.Action");
        broad_deleted_songs_removing.putExtra("DELETED_SONG_LIST_REMOVABLE", album.getAlbum_files());
        broad_deleted_songs_removing.putExtra("DELETED_SONG_LIST_TYPE", "AlbumsMultiple");
        ((Activity) context).sendBroadcast(broad_deleted_songs_removing);
    }

    private void deleteSongAPI29OrGreater() {
        List<Uri> uris = new ArrayList<>();

        for (MusicFiles music: album.getAlbum_files()) {
            File file = new File(music.getPath());
            Uri path_uri = Uri.fromFile(file);
            Uri uri = getContentUri(path_uri);
            uris.add(uri);
        }
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                MaterialButton cancel, delete;
                TextView song_name;

                View v_w = LayoutInflater.from(context).inflate(R.layout.delete_song_dilog, null);
                MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);
                cancel = v_w.findViewById(R.id.cancel);
                delete = v_w.findViewById(R.id.delete);
                song_name = v_w.findViewById(R.id.song_name);
                dialog.setView(v_w);
                String album_txt = album.getAlbum_name() + " album";
                song_name.setText(album_txt);

                AlertDialog delete_dialog = dialog.create();
                delete_dialog.show();

                cancel.setOnClickListener(vi -> delete_dialog.dismiss());

                delete.setOnClickListener(vi -> {
                    //                    context.getContentResolver().delete(uri, null);
                    List<Integer> isAllDeletedList = new ArrayList<>();
                    boolean flag = false;
                    for (Uri uri: uris) {
                        int del_value = deleteApi28(uri);
                        if (del_value > 1)
                            flag = true;
                        isAllDeletedList.add(del_value);
                    }
                    if (!flag) {
                        Toast.makeText(context, "failed to deleted Album", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        deleteFullAlbumFromTheDevice();

                        if (!isAllDeletedList.contains(0)) {
                            Toast.makeText(context, "Album Fully deleted ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Album Not Fully deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                    delete_dialog.dismiss();
                });
            } else {
                try {
                    deleteApi30(uris);
                } catch (Exception b) {
                    b.printStackTrace();
                    Toast.makeText(context, "File can't be deleted", Toast.LENGTH_SHORT).show();
                }
            }
        } catch(Exception e) {
            try {
                deleteApi30(uris);
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

    private void deleteApi30(List<Uri> songUri) {
        ContentResolver contentResolver = context.getContentResolver();
        //        Collections.addAll(uriList, songUri);
        try {
            PendingIntent pendingIntent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pendingIntent = MediaStore.createDeleteRequest(contentResolver, songUri);
            }
            if (pendingIntent != null) {
                Activity activity = getActivity();
                DELETING_MUSIC_FILES_OF_ALBUM = album.getAlbum_files();
                if (activity != null)
                    activity.startIntentSenderForResult(pendingIntent.getIntentSender(), REQUEST_PERMISSION_DELETE_ALBUM, null, 0,
                        0, 0, null);
            } else {
                Toast.makeText(context, "Album can't be deleted", Toast.LENGTH_SHORT).show();
            }
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            Toast.makeText(context, "Album can't be deleted", Toast.LENGTH_SHORT).show();
        }
    }

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

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        BackService.LocalBinder binder = (BackService.LocalBinder) iBinder;
        service = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    @SuppressLint("StaticFieldLeak")
    private class MyImageLoad extends AsyncTask<String, Void, byte[]>
    {
        byte[] album_pic;
        MediaMetadataRetriever retriver;

        @Override
        protected byte[] doInBackground(String... path) {
            try {
                retriver = new MediaMetadataRetriever();
                retriver.setDataSource(path[0]);
                album_pic = retriver.getEmbeddedPicture();
                retriver.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return album_pic;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            try {
                Glide.with(((Activity) context))
                        .load(bytes)
                        .override(50, 50)
                        .placeholder(R.mipmap.ic_music)
                        .into(album_art);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*  Animations  */

    private void fevouriteBtnClicked(ImageView v) {
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

    private void unFevouriteClicked(View v) {
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
