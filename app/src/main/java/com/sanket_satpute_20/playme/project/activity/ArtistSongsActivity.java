package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.MainActivity.favouriteArtists;
import static com.sanket_satpute_20.playme.MainActivity.favouriteList;
import static com.sanket_satpute_20.playme.MainActivity.musicFiles;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BSheetSongsMoreFragment.delete_song_removing_position;
import static com.sanket_satpute_20.playme.project.extra_stuffes.CommonMethods.REQUEST_PERMISSION_DELETE;
import static com.sanket_satpute_20.playme.project.recycler_views.ArtistRecycle.ARTISTNAME;
import static com.sanket_satpute_20.playme.project.recycler_views.ArtistRecycle.PASSEDARTIST;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.service.BackService.recentPlayed;
import static com.sanket_satpute_20.playme.project.service.BackService.shuffleBoolean;
import static com.sanket_satpute_20.playme.project.service.BackService.song;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.model.Artists;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.model.Playlist;
import com.sanket_satpute_20.playme.project.model.PlaylistArray;
import com.sanket_satpute_20.playme.project.receivers.MyAppReceiver;
import com.sanket_satpute_20.playme.project.recycler_views.PlaylistRecycleCreatePlaylist;
import com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle;
import com.sanket_satpute_20.playme.project.service.BackService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ArtistSongsActivity extends AppCompatActivity {

    public static final int ARTIST_FINISHING_REQUEST_CODE = 1901;
    public static final int REQUEST_PERMISSION_DELETE_ARTIST = 1201;
    public static ArrayList<MusicFiles> DELETING_MUSIC_FILES_OF_ARTIST = null;

    ImageView back_pressed, artist_back_src, options;
    RecyclerView recyclerView;
    TextView no_song , song_hours, artist_txt;
    FloatingActionButton play_with_shuffle;
    MaterialButton like;

    String artist_name;
    Artists artist;

    BackService service = new BackService();

    ArrayList<MusicFiles> artistSongs = new ArrayList<>();

    SongsRecycle songsRecycle;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case "action_song_deleted.REMOVE_FULL_SONG_OR_FILE_INSIDE_SONG_MAIN_LIST" :
                        int removal_index = intent.getIntExtra("REMOVAL_INDEX", -1);
                        if (removal_index != -1 && songsRecycle != null) {
                            songsRecycle.notifyItemRemoved(removal_index);
                            songsRecycle.notifyItemRangeChanged(removal_index, musicFiles.size());

                            Executor executor = Executors.newSingleThreadExecutor();
                            executor.execute(() -> {
                                String song_hours_str = artist.getArtistFiles().size() + " Audio - " + getDuration(artist.getTimeSize()) + " minutes";
                                runOnUiThread(() -> song_hours.setText(song_hours_str));
                            });
                        }
                        break;
                    case "songs_is_removed_OR_Full_Artist_is_removed.DELETED.Action" :
                        String artist_name_str = intent.getStringExtra("REMOVED_ARTIST_NAME");
                        int removable_position = intent.getIntExtra("ARTIST_REMOVAL_POSITION", -1);
                        if (artist_name_str != null) {
                            if (artist_name_str.trim().equals(artist.getArtistName().trim())) {
                                if (intent.getBooleanExtra("REMOVED_ALL", false)) {
                                    Intent finishing_intent = new Intent();
                                    finishing_intent.putExtra("RETURNING_VALUE", removable_position);
                                    finishing_intent.putExtra("REMOVED_ALL", intent.getBooleanExtra("REMOVED_ALL", false));
                                    ArtistSongsActivity.this.setResult(RESULT_OK, finishing_intent);
                                    finish();
                                } else {
                                    setRecycler();
                                }
                            }
                        }
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_songs);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(params);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        initViews();
        onClick();
        getMyIntent();
        setRecycler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("action_song_deleted.REMOVE_FULL_SONG_OR_FILE_INSIDE_SONG_MAIN_LIST");
        filter.addAction("songs_is_removed_OR_Full_Artist_is_removed.DELETED.Action");
        LocalBroadcastManager.getInstance(ArtistSongsActivity.this).registerReceiver(receiver, filter);

        if (favouriteArtists.contains(artist)) {
            like.setText(R.string.liked);
        } else {
            like.setText(R.string.like);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(ArtistSongsActivity.this).unregisterReceiver(receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_DELETE) {
            if (resultCode != 0) {
                sendBroadcastForSongDeleted();
            } else {
                // User denied permission, notify the adapter
                Toast.makeText(this, "Not Deleted", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_PERMISSION_DELETE_ARTIST) {
            if (resultCode != 0) {
                deleteClicked();
                Toast.makeText(this, "artist deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "failed to delete artist", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendBroadcastForSongDeleted() {
        Intent broad_item_removed = new Intent(this, MyAppReceiver.class);
        broad_item_removed.setAction("song_recycler.SONG_IS_DELETED.Action");
        broad_item_removed.putExtra("DELETED_ITEM_POSITION_EXTRAS", delete_song_removing_position);
        sendBroadcast(broad_item_removed);

        delete_song_removing_position = -1;

        Toast.makeText(this, "File deleted", Toast.LENGTH_SHORT).show();
    }

    private void initViews() {
        back_pressed = findViewById(R.id.back_pressed);
        recyclerView = findViewById(R.id.artist_recycler);
        artist_back_src = findViewById(R.id.artist_bg_src);
        no_song = findViewById(R.id.no_songs_txt);
        play_with_shuffle = findViewById(R.id.play_fab);
        like = findViewById(R.id.like);
        options = findViewById(R.id.options);
        song_hours = findViewById(R.id.songsandhours);
        artist_txt = findViewById(R.id.artist_name);
    }

    private void onClick() {
        // OnClick
        back_pressed.setOnClickListener(view -> onBackPressed());

        play_with_shuffle.setOnClickListener(view -> {
                shuffleBoolean = true;
                service.setSongSource(artistSongs);
                Intent intent = new Intent(ArtistSongsActivity.this, BackService.class);
                intent.putExtra("position", getRandom(artistSongs.size()));
                startService(intent);
        });

        like.setOnClickListener(view -> {
            if (favouriteArtists.contains(artist)) {
                favouriteArtists.remove(artist);
                like.setText(R.string.like);
            } else {
                favouriteArtists.add(artist);
                like.setText(R.string.liked);
            }
        });

        options.setOnClickListener(view -> popUpMenuClicked());
    }

    private void getMyIntent() {
        Intent intent = getIntent();
        artist_name = intent.getStringExtra(ARTISTNAME);
        artist = (Artists) intent.getSerializableExtra(PASSEDARTIST);
    }

    public void setRecycler() {
        artistSongs = artist.getArtistFiles();

        if (artistSongs.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            no_song.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            songsRecycle = new SongsRecycle(this, artistSongs, getSupportFragmentManager());
            recyclerView.setAdapter(songsRecycle);

            MyImageLoad load = new MyImageLoad();
            load.execute(artist.getArtistFiles().get(0));
        } else {
            recyclerView.setVisibility(View.GONE);
            no_song.setVisibility(View.VISIBLE);
        }

        artist_txt.setText(artist_name);

        String song_hours_str = "fetching artist details";
        song_hours.setText(song_hours_str);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String song_hours_str_2 = artist.getArtistFiles().size() + " Audio - " + getDuration(artist.getTimeSize()) + " minutes";
            song_hours.setText(song_hours_str_2);
        });

    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i);
    }

    private void popUpMenuClicked() {
        PopupMenu menu = new PopupMenu(this, options);
        menu.getMenuInflater().inflate(R.menu.artist_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(menuItem -> {
            if (R.id.play_all == menuItem.getItemId()) {
                playAllClicked();
            } else if (R.id.add_to_queue == menuItem.getItemId()) {
                addToQueueClicked();
            } else if (R.id.add_to_playlist == menuItem.getItemId()) {
                addToPlaylistDialog();
            } else if (R.id.delete == menuItem.getItemId()) {
                deleteSongAPI29OrGreater();
            }
            return true;
        });
        menu.show();
    }

    private void playAllClicked() {
        if (artistSongs.size() > 0) {
            service.setSongSource(artistSongs);
            Intent intent = new Intent(this, BackService.class);
            intent.putExtra("position", 0);
            startService(intent);
        } else {
            Toast.makeText(this, "Artist is Empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToQueueClicked() {
        if (artistSongs != null) {
            if (artistSongs.size() > 0) {
                song.addAll(artistSongs);
                Toast.makeText(this, "Added to Queue", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Not Added in Queue", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Not Added in Queue", Toast.LENGTH_SHORT).show();
        }
    }

    /*      Add To Playlist    */
    MaterialCheckBox recent_check, favourite_check;
    RelativeLayout recent_layout, favourite_layout, create_new_playlist;
    MaterialButton save_btn;
    RecyclerView recycler_view_playlist;

    PlaylistArray array;
    Playlist playlist;
    PlaylistRecycleCreatePlaylist adapter;
    AlertDialog dilog_0;

    public void addToPlaylistDialog() {

        View v = LayoutInflater.from( this).inflate(R.layout.layout_add_to_playlist_dilouge, null);
        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(this);
        alert.setView(v);
        AlertDialog dialog = alert.create();
        dialog.show();

        /*  Initializing Views  */
        recent_check = v.findViewById(R.id.recent_played_add_checkbox);
        favourite_check = v.findViewById(R.id.fevourite_add_checkbox);
        recent_layout = v.findViewById(R.id.recent_played_add);
        favourite_layout = v.findViewById(R.id.favourite_add);
        create_new_playlist = v.findViewById(R.id.create_new_playlist);
        save_btn = v.findViewById(R.id.save_btn_add_playlist);
        recycler_view_playlist = v.findViewById(R.id.recycler_view_add_playlist_dilog);
        array = new PlaylistArray();

        save_btn.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));

        retrievePlaylist();

        recent_layout.setOnClickListener(view -> recent_check.setChecked(!recent_check.isChecked()));
        favourite_layout.setOnClickListener(view -> favourite_check.setChecked(!favourite_check.isChecked()));

        create_new_playlist.setOnClickListener(view -> {
            dialog.dismiss();
            createNewPlaylist();
        });

        save_btn.setOnClickListener(view -> {
            if (recent_check.isChecked()) recentPlayed.addAll(artist.getArtistFiles());
            if (favourite_check.isChecked()) favouriteList.addAll(artist.getArtistFiles());
            boolean added_to_playlist = false;
            if (adapter != null) {
                int[] selected_playlist = adapter.returnSelectedList();
                for (int i = 0; i < selected_playlist.length; i++) {
                    if (selected_playlist[i] == 1) {
                        PlaylistArray.ref.get(i).getPlaylist().addAll(artist.getArtistFiles());
                        added_to_playlist = true;
                    }
                }
            }
            if (added_to_playlist || recent_check.isChecked() || favourite_check.isChecked())
                Toast.makeText(this, "Added to playlist", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

    }


    private void createNewPlaylist() {

        EditText playlist_name, user_name;
        Button add_playlist, cancel;
        View layout = LayoutInflater.from(this).inflate(R.layout.create_playlist, null);
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
                addToPlaylistDialog();
                dilog_0.dismiss();
            }
        });
        cancel.setOnClickListener(view -> {
            addToPlaylistDialog();
            dilog_0.dismiss();
        });
        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(this);
        alert.setView(layout);

        dilog_0 = alert.create();
        if (dilog_0.getWindow() != null)
            dilog_0.getWindow().getAttributes().windowAnimations = R.style.UpDownDialogAnimation;
        dilog_0.show();
    }

    private void retrievePlaylist() {
        if (PlaylistArray.ref.size() > 0) {
            recycler_view_playlist.setVisibility(View.VISIBLE);
            recycler_view_playlist.setHasFixedSize(true);
            LinearLayoutManager layout = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            adapter = new PlaylistRecycleCreatePlaylist(this, array.getRef(), artist.getArtistFiles());
            recycler_view_playlist.setAdapter(adapter);
            recycler_view_playlist.setLayoutManager(layout);
        } else
            recycler_view_playlist.setVisibility(View.GONE);
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
                Toast.makeText(this, "Playlist Already Exist", Toast.LENGTH_SHORT).show();
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
            Toast.makeText((this), "You Should to feel the Names", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteClicked() {
        Intent broad_deleted_songs_removing = new Intent(this, MyAppReceiver.class);
        broad_deleted_songs_removing.setAction("songs_deleted.SONG_IS_DELETED.Multiple_Album_Artist.Action");
        broad_deleted_songs_removing.putExtra("DELETED_SONG_LIST_REMOVABLE", artist.getArtistFiles());
        broad_deleted_songs_removing.putExtra("DELETED_SONG_LIST_TYPE", "ArtistsMultiple");
        DELETING_MUSIC_FILES_OF_ARTIST = null;
        sendBroadcast(broad_deleted_songs_removing);
    }

    private void deleteSongAPI29OrGreater() {
        List<Uri> uris = new ArrayList<>();

        for (MusicFiles music: artist.getArtistFiles()) {
            File file = new File(music.getPath());
            Uri path_uri = Uri.fromFile(file);
            Uri uri = getContentUri(path_uri);
            uris.add(uri);
        }
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                MaterialButton cancel, delete;
                TextView song_name;

                View v_w = LayoutInflater.from(this).inflate(R.layout.delete_song_dilog, null);
                MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
                cancel = v_w.findViewById(R.id.cancel);
                delete = v_w.findViewById(R.id.delete);
                song_name = v_w.findViewById(R.id.song_name);
                dialog.setView(v_w);
                String artist_txt = artist.getArtistName() + " artist";
                song_name.setText(artist_txt);

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
                        Toast.makeText(this, "failed to deleted artist", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        deleteClicked();

                        if (!isAllDeletedList.contains(0)) {
                            Toast.makeText(this, "Artist Fully deleted ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Artist Not Fully deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                    delete_dialog.dismiss();
                });
            } else {
                try {
                    deleteApi30(uris);
                } catch (Exception b) {
                    b.printStackTrace();
                    Toast.makeText(this, "File can't be deleted", Toast.LENGTH_SHORT).show();
                }
            }
        } catch(Exception e) {
            try {
                deleteApi30(uris);
            } catch (Exception b) {
                b.printStackTrace();
                Toast.makeText(this, "File can't be deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int deleteApi28(Uri songUri) {
        ContentResolver contentResolver = this.getContentResolver();
        return contentResolver.delete(songUri, null, null);
    }

    private void deleteApi30(List<Uri> songUri) {
        ContentResolver contentResolver = this.getContentResolver();
        //        Collections.addAll(uriList, songUri);
        try {
            PendingIntent pendingIntent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pendingIntent = MediaStore.createDeleteRequest(contentResolver, songUri);
            }
            if (pendingIntent != null) {
                DELETING_MUSIC_FILES_OF_ARTIST = artist.getArtistFiles();
                ArtistSongsActivity.this.startIntentSenderForResult(pendingIntent.getIntentSender(), REQUEST_PERMISSION_DELETE_ARTIST, null, 0,
                        0, 0, null);
            } else {
                Toast.makeText(this, "Artist can't be deleted", Toast.LENGTH_SHORT).show();
            }
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            Toast.makeText(this, "Artist can't be deleted", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getContentUri(Uri fileUri) {
        String[] projections = {MediaStore.MediaColumns._ID};
        Cursor cursor = this.getContentResolver().query(
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

    // Get Time
    public String getDuration(int time)
    {
        return String.valueOf((time / (1000*60)) % 60);
    }

    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]> {

        MediaMetadataRetriever retriever;
        byte[] album_pic;

        @Override
        protected byte[] doInBackground(MusicFiles... musicFiles) {
            try {
                retriever = new MediaMetadataRetriever();
                retriever.setDataSource(musicFiles[0].getPath());
                album_pic = retriever.getEmbeddedPicture();
                retriever.release();
                return album_pic;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            try {
                Glide.with(ArtistSongsActivity.this)
                        .load(album_pic)
                        .override(300, 300)
                        .placeholder(R.mipmap.ic_artist)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(artist_back_src);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}