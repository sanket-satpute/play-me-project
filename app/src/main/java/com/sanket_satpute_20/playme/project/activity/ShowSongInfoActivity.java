package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.MainActivity.albumFilesList;
import static com.sanket_satpute_20.playme.MainActivity.artistFiles;
import static com.sanket_satpute_20.playme.project.recycler_views.AlbumsRecycle.ALBUMCLASS;
import static com.sanket_satpute_20.playme.project.recycler_views.AlbumsRecycle.ALBUMNAME;
import static com.sanket_satpute_20.playme.project.recycler_views.AlbumsRecycle.POS;
import static com.sanket_satpute_20.playme.project.recycler_views.ArtistRecycle.ARTISTNAME;
import static com.sanket_satpute_20.playme.project.recycler_views.ArtistRecycle.PASSEDARTIST;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGALBUM;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGARTIST;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGDURATION;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGNAME;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONGPATH;
import static com.sanket_satpute_20.playme.project.recycler_views.SongsRecycle.SONG_EXTRA;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.model.Albums;
import com.sanket_satpute_20.playme.project.model.Artists;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.service.BackService;

import java.io.File;
import java.util.ArrayList;

public class ShowSongInfoActivity extends AppCompatActivity {

    ContentResolver contentResolver;


    ImageView back;
    TextView song, artist, album, duration, size, location, song_name_2, played_times;
    MusicFiles music;
    String type = "B";

    String song_name, song_artist, song_album, song_duration, song_location;
    int played;

    MaterialButton play;

    ImageView song_art;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_song_info);

        contentResolver = getContentResolver();

        initView();
        setOnClick();
        doExtra();
    }

    public void initView() {
        song = findViewById(R.id.song_name);
        artist = findViewById(R.id.artist);
        album = findViewById(R.id.album);
        duration = findViewById(R.id.duration_time);
        size = findViewById(R.id.size_text);
        location = findViewById(R.id.location);
        back = findViewById(R.id.backpress);
        song_name_2 = findViewById(R.id.song_name_2);
        played_times = findViewById(R.id.played_time);

        play = findViewById(R.id.play_btn);

        song_art = findViewById(R.id.song_image);
    }

    public void setOnClick() {
        back.setOnClickListener(view -> onBackPressed());

        artist.setOnClickListener(view -> artistTxtClicked());

        album.setOnClickListener(view -> albumTxtClicked());

        play.setOnClickListener(view -> {
            ArrayList<MusicFiles> temp_list = new ArrayList<>();
            temp_list.add(music);
            BackService service = new BackService();
            service.setSongSource(temp_list);

            Intent intent = new Intent(this, BackService.class);
            intent.putExtra("position", 0);
            startService(intent);
        });

    }


    public void doExtra() {
        Intent intent = getIntent();
        music = (MusicFiles) intent.getSerializableExtra(SONG_EXTRA);
        song_name = intent.getStringExtra(SONGNAME);
        song_artist = intent.getStringExtra(SONGARTIST);
        song_album = intent.getStringExtra(SONGALBUM);
        song_duration = intent.getStringExtra(SONGDURATION);
        song_location = intent.getStringExtra(SONGPATH);
        played = music.getSong_most_played_count();

        song.setText(song_name);
        artist.setText(song_artist);
        album.setText(song_album);
        song_name_2.setText(song_name);
        duration.setText(getDuration(Integer.parseInt(song_duration)));
        long length = getSize(song_location);
        String size_txt = length + " " + type;
        String played_time_txt = "Played More than " + played + " time's ";
        size.setText(size_txt);
        played_times.setText(played_time_txt);
        location.setText(song_location);
    }

    private void artistTxtClicked() {
        Artists artists = null;
        ArrayList<Artists> artists_1 = new ArrayList<>(artistFiles);
        for (Artists artist: artists_1) {
            if (artist.getArtistName().equals(music.getArtist())) {
                artists = artist;
                break;
            }
        }
        if (artists != null) {
            Intent intent = new Intent(this, ArtistSongsActivity.class);
            intent.putExtra(ARTISTNAME, artists.getArtistName());
            intent.putExtra(PASSEDARTIST, artists);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Artist Not Available", Toast.LENGTH_SHORT).show();
        }
    }

    private void albumTxtClicked() {
        int alb_position = 0;
        Albums albums = null;
        for (Albums album: albumFilesList) {
            if (album.getAlbum_name().equals(music.getAlbum())) {
                albums = album;
                break;
            }
            alb_position++;
        }
        if (albums != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ALBUMCLASS, albums);
            Intent intent = new Intent(this, AlbumListActivity.class);
            intent.putExtra("B", bundle);
            intent.putExtra(ALBUMNAME, albums.getAlbum_name());
            intent.putExtra(POS, alb_position);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Album Not Available", Toast.LENGTH_SHORT).show();
        }
    }

    public long getSize(String path) {
        File file = new File(path);
        if (file.length() < 1024) {
            return file.length();
        } else {
            long ksize = file.length() / 1024;
            if (ksize < 1024) {
                type = "KB";
                return ksize;
            } else {
                long msize = ksize / 1024;
                if (msize < 1024) {
                    type = "MB";
                    return msize;
                } else {
                    type = "GB";
                    return msize / 1024;
                }
            }
        }
    }

    // Get Time
    public String getDuration(int time)
    {
        int seconds = (time / 1000) % 60 ;
        int minutes = ((time / (1000*60)) % 60);
        return minutes+" : "+seconds;
    }
}