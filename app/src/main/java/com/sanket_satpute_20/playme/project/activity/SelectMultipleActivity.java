package com.sanket_satpute_20.playme.project.activity;

import static com.sanket_satpute_20.playme.MainActivity.favouriteList;
import static com.sanket_satpute_20.playme.MainActivity.musicFiles;
import static com.sanket_satpute_20.playme.project.extra_stuffes.CommonMethods.REQUEST_PERMISSION_DELETE;
import static com.sanket_satpute_20.playme.project.recycler_views.SelectSongRecycle.IS_LIST_FULL;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.service.BackService.recentPlayed;
import static com.sanket_satpute_20.playme.project.service.BackService.song;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.extra_stuffes.CommonMethods;
import com.sanket_satpute_20.playme.project.model.MusicFiles;
import com.sanket_satpute_20.playme.project.model.Playlist;
import com.sanket_satpute_20.playme.project.model.PlaylistArray;
import com.sanket_satpute_20.playme.project.receivers.MyAppReceiver;
import com.sanket_satpute_20.playme.project.recycler_views.PlaylistRecycleCreatePlaylist;
import com.sanket_satpute_20.playme.project.recycler_views.SelectSongRecycle;
import com.sanket_satpute_20.playme.project.service.BackService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SelectMultipleActivity extends AppCompatActivity {

    public static final int SELECT_MULTIPLE_DELETION_REQUEST_CODE = 1902;

    /*      Add To Playlist    */
    MaterialCheckBox recent_check, favourite_check;
    RelativeLayout recent_layout, favourite_layout, create_new_playlist;
    MaterialButton save_btn;
    RecyclerView recycler_view;

    PlaylistArray array;
    Playlist playlist;
    PlaylistRecycleCreatePlaylist adapter;
    AlertDialog dialog_0;


    ImageView back_pressed;
    MaterialCheckBox select_all;
    RecyclerView recyclerView;
    TextView no_song_txt;
    MaterialCardView menu_card;

    RelativeLayout delete, send, play, add;

    public static boolean isParentChecked = false;
    public static ArrayList<MusicFiles> selected_files = new ArrayList<>();

    boolean is_up = false, items_are_removed = false;

    SelectSongRecycle adapter_select;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals("select_multiple.song.broadcast")) {
                    if (intent.getBooleanExtra(IS_LIST_FULL, false)) {
                        setBarUp();
                    } else {
                        setBarDown();
                    }
                } else if (intent.getAction().equals("song_is_removed.DELETED.Action")) {
                    doExtra();
                    select_all.setChecked(false);
                    items_are_removed = intent.getBooleanExtra("ITEMS_ARE_REMOVED_ARE_NOT", false);
                    setBarDown();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_multiple);
        initViews();
        doExtra();
        if (musicFiles.size() > 0)
            onClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (selected_files != null) {
            if (selected_files.size() > 0) {
                menu_card.setTranslationY(0);
            } else {
                menu_card.setTranslationY(300);
            }
        } else {
            menu_card.setTranslationY(300);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("select_multiple.song.broadcast");
        filter.addAction("song_is_removed.DELETED.Action");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public void finish() {
        super.finish();
        if (selected_files != null) {
            selected_files.clear();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("ITEMS_ARE_REMOVED", items_are_removed);
        SelectMultipleActivity.this.setResult(RESULT_OK, intent);
        finish();
    }

    private void initViews() {
        back_pressed = findViewById(R.id.back_pressed);
        select_all = findViewById(R.id.check_all);
        recyclerView = findViewById(R.id.selection_recycle);
        no_song_txt = findViewById(R.id.no_song_txt);
        menu_card = findViewById(R.id.menu_card);
        delete = findViewById(R.id.delete);
        send = findViewById(R.id.send);
        play = findViewById(R.id.play);
        add = findViewById(R.id.add);
    }

    private void doExtra() {
        if (musicFiles.size() > 0) {
            Log.d("hay_0", "size of " + musicFiles.size());
            recyclerView.setVisibility(View.VISIBLE);
            no_song_txt.setVisibility(View.GONE);
            adapter_select = new SelectSongRecycle(SelectMultipleActivity.this, musicFiles, select_all);
            LinearLayoutManager layoutManager = new LinearLayoutManager(SelectMultipleActivity.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter_select);
        } else {
            recyclerView.setVisibility(View.GONE);
            no_song_txt.setVisibility(View.VISIBLE);
        }
    }

    private void onClick() {
        back_pressed.setOnClickListener(view -> onBackPressed());

        select_all.setOnCheckedChangeListener((compoundButton, b) -> {
            isParentChecked = b;
            if (b) {
                if (adapter_select != null) {
                    selected_files.clear();
                    selected_files.addAll(adapter_select.selection_files);
                }
                setBarUp();
            }
            else {
                if (adapter_select != null) {
                    selected_files.removeAll(adapter_select.selection_files);
                    selected_files.clear();
                }
                setBarDown();
            }
            doExtra();
        });

        play.setOnClickListener(view -> {
            if (selected_files.size() > 0) {
                song.clear();
                song.addAll(selected_files);
                Intent intent = new Intent(SelectMultipleActivity.this, BackService.class);
                intent.putExtra("position", 0);
                startService(intent);
            }
            finish();
        });

        delete.setOnClickListener(view -> {
            if (selected_files != null) {
                if (selected_files.size() > 0) {
                    deleteMultiples();
                } else {
                    Toast.makeText(this, "No file selected to delete", Toast.LENGTH_SHORT).show();
                }
            }
//            finish();
        });

        add.setOnClickListener(view -> addToPlaylistDialog());

        send.setOnClickListener(view -> {
            shareFiles();
            finish();
        });
    }

    private void deleteMultiples() {
        ArrayList<Uri> deleteAbleList = new ArrayList<>();

        AlertDialog alertDialog;
        View view = LayoutInflater.from(this).inflate(R.layout.loading_alert_dialog_layout, null);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this, R.style.RoundedCornerAlertDialogSmall);
        dialog.setView(view);
        alertDialog = dialog.create();
        alertDialog.show();
        alertDialog.setCancelable(false);

        ArrayList<MusicFiles> removedMusicFiles = selected_files;

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                for (MusicFiles f: selected_files) {
                    Uri uri = CommonMethods.getContentUri(Long.valueOf(f.getId()));
                    deleteAbleList.add(uri);
                }
                runOnUiThread(() -> {
                    alertDialog.dismiss();
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                        deleteMultipleApi28OrLessDialog(deleteAbleList, removedMusicFiles);
                    } else {
                        CommonMethods.deleteSongAPI29OrGreater(this, deleteAbleList);
                    }
                });
            } catch (Exception e) {
                alertDialog.dismiss();
                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteMultipleApi28OrLessDialog(ArrayList<Uri> uris, ArrayList<MusicFiles> removingFiles) {
        MaterialButton cancel, delete, deletion_completed_continue_btn;
        TextView song_name, deleting_files_progress_txt, deletion_completed_helper_txt, deletion_txt;
        ConstraintLayout first_alert_layout, second_deleting_layout, third_success_failed_layout;
        LinearProgressIndicator deleted_files_progress;
        LinearLayout operational_buttons_layout;

        View v_w = LayoutInflater.from(this).inflate(R.layout.delete_song_dilog, null);
        MaterialAlertDialogBuilder dilog = new MaterialAlertDialogBuilder(this);
        cancel = v_w.findViewById(R.id.cancel);
        delete = v_w.findViewById(R.id.delete);
        song_name = v_w.findViewById(R.id.song_name);
        first_alert_layout = v_w.findViewById(R.id.first_delete_alert);
        second_deleting_layout = v_w.findViewById(R.id.second_deleting_layout);
        third_success_failed_layout = v_w.findViewById(R.id.third_delete_success_or_failed);
        deleting_files_progress_txt = v_w.findViewById(R.id.deleting_files_progress_txt);
        deleted_files_progress = v_w.findViewById(R.id.deleting_files_progress_bar);
        operational_buttons_layout = v_w.findViewById(R.id.operational_buttons_layout);
        deletion_completed_continue_btn = v_w.findViewById(R.id.continue_btn);
        deletion_completed_helper_txt = v_w.findViewById(R.id.deletion_complete_helper_txt);
        deletion_txt = v_w.findViewById(R.id.deletion_txt);

        dilog.setView(v_w);

        AlertDialog delete_dialog = dilog.create();
        delete_dialog.setCancelable(false);
        delete_dialog.show();

        first_alert_layout.setVisibility(View.VISIBLE);
        second_deleting_layout.setVisibility(View.GONE);
        third_success_failed_layout.setVisibility(View.GONE);
        operational_buttons_layout.setVisibility(View.VISIBLE);

        String delete_files_str = "delete " + uris.size() + " audio file" + ((uris.size() > 1) ? "s." : ".");
        song_name.setText(delete_files_str);

        cancel.setOnClickListener(vi -> delete_dialog.dismiss());

        delete.setOnClickListener(vi -> {
            first_alert_layout.setVisibility(View.GONE);
            third_success_failed_layout.setVisibility(View.GONE);
            second_deleting_layout.setVisibility(View.VISIBLE);
            cancel.setAlpha(0.5f);
            delete.setAlpha(0.5f);
            cancel.setClickable(false);
            delete.setClickable(false);

            Executor executor = Executors.newSingleThreadExecutor();

            try {
                executor.execute(() -> {
                    String deleting_str = "successfully deleted audio files.";
                    AtomicBoolean isMaxSet = new AtomicBoolean(false);
                    int not_deleted_items_count = 0, deleted_items_count = 0;

                    ArrayList<MusicFiles> removingFilesSecond = new ArrayList<>();

                    for (int i = 0; i < uris.size(); i++) {
                        int deleted_state = CommonMethods.deleteApi28OrLess(this, uris.get(i));

                        int percentDeleted = (i * 100) % uris.size();   // to show in progressbar
                        if (deleted_state > 0) {
                            deleted_items_count++;
                            removingFilesSecond.add(removingFiles.get(i));
                        } else {
                            not_deleted_items_count++;
                        }

                        deleting_str = deleted_items_count + " items were deleted successfully. " + (((deleted_items_count > 0 && not_deleted_items_count == 0) ? "." :
                                " , but " + not_deleted_items_count
                                        + " deletion failed."));

                        String finalDeleting_str1 = deleting_str;
                        runOnUiThread(() -> {
                            if (!isMaxSet.get()) {
                                deleted_files_progress.setMax(100);
                                isMaxSet.set(true);
                            }
                            deleting_files_progress_txt.setText(finalDeleting_str1);
                            deleted_files_progress.setProgress(percentDeleted);
                        });
                    }
                    String finalDeleting_str = deleting_str;
                    runOnUiThread(() -> {
//                        send broadcast with files which is removable
                        sendBroadcastToDeleteMultiple();


                        first_alert_layout.setVisibility(View.GONE);
                        second_deleting_layout.setVisibility(View.GONE);
                        third_success_failed_layout.setVisibility(View.VISIBLE);
                        operational_buttons_layout.setVisibility(View.GONE);

                        deletion_completed_helper_txt.setText(finalDeleting_str);
                        deletion_completed_continue_btn.setOnClickListener(view -> delete_dialog.dismiss());
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
//                go direct to third stage of dialog and show there all deletion is failed due to some error you can continue
                first_alert_layout.setVisibility(View.GONE);
                second_deleting_layout.setVisibility(View.GONE);
                third_success_failed_layout.setVisibility(View.GONE);
                operational_buttons_layout.setVisibility(View.GONE);

                String failed_str = "Failed Deletion.";
                deletion_txt.setText(failed_str);
                deletion_txt.setTextColor(Color.RED);
                deletion_completed_helper_txt.setText(e.getMessage());

                deletion_completed_continue_btn.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                deletion_completed_continue_btn.setOnClickListener(view -> delete_dialog.dismiss());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_DELETE) {
            if (resultCode == RESULT_OK) {
//                generate a broadcast to notify all selected songs deleted successfully with a toast
                sendBroadcastToDeleteMultiple();
            } else {
                Toast.makeText(this, "Failed to Delete", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    void sendBroadcastToDeleteMultiple() {
        Intent broad_deleted_songs_removing = new Intent(SelectMultipleActivity.this, MyAppReceiver.class);
        broad_deleted_songs_removing.setAction("songs_deleted.SONG_IS_DELETED.Multiple_Album_Artist.Action");
        broad_deleted_songs_removing.putExtra("DELETED_SONG_LIST_REMOVABLE", selected_files);
        broad_deleted_songs_removing.putExtra("DELETED_SONG_LIST_TYPE", "MULTIPLE");
        sendBroadcast(broad_deleted_songs_removing);
        selected_files.clear();

        Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
    }

    private void setBarUp () {
        if (!is_up) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(menu_card, "translationY", 300, 0);
            animator.setDuration(100);
            animator.setInterpolator(new FastOutSlowInInterpolator());
            animator.start();
            is_up = true;
        }
    }

    private void setBarDown() {
        if (is_up) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(menu_card, "translationY", 0, 300);
            animator.setDuration(100);
            animator.setInterpolator(new FastOutSlowInInterpolator());
            animator.start();
            is_up = false;
        }
    }

    private void shareFiles() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(android.content.Intent.ACTION_SEND_MULTIPLE);
        ArrayList<Uri> uris = new ArrayList<>();
        intent.setType("*/*");

        AlertDialog alertDialog;
        View view = LayoutInflater.from(this).inflate(R.layout.loading_alert_dialog_layout, null);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this, R.style.RoundedCornerAlertDialogSmall);
        dialog.setView(view);
        alertDialog = dialog.create();
        alertDialog.show();
        alertDialog.setCancelable(false);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                for (MusicFiles f : selected_files) {
                    String sender_path = f.getPath();
                    if (sender_path != null) {
                        File file = new File(sender_path);
                        Uri uri = FileProvider.getUriForFile(SelectMultipleActivity.this, getApplicationContext().getPackageName() + ".provider", file);
                        uris.add(uri);
                    }
                }
                runOnUiThread(() -> {
                    alertDialog.dismiss();
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    startActivity(intent);
                });
            } catch (Exception e) {
                alertDialog.dismiss();
                try {
                    runOnUiThread(() -> Toast.makeText(SelectMultipleActivity.this, "Failed to send", Toast.LENGTH_SHORT).show());
                } catch (NullPointerException f) {
                    f.printStackTrace();
                }
            }
        });
    }


    public void addToPlaylistDialog() {

        View v = LayoutInflater.from(SelectMultipleActivity.this).inflate(R.layout.layout_add_to_playlist_dilouge, null);
        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(SelectMultipleActivity.this);
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
        recycler_view = v.findViewById(R.id.recycler_view_add_playlist_dilog);
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
            if (recent_check.isChecked()) recentPlayed.addAll(selected_files);
            if (favourite_check.isChecked()) favouriteList.addAll(selected_files);
            dilog.dismiss();
            SelectMultipleActivity.this.finish();
        });
    }

    private void retrievePlaylist() {
        if (PlaylistArray.ref.size() > 0) {
            recycler_view.setVisibility(View.VISIBLE);
            recycler_view.setHasFixedSize(true);
            LinearLayoutManager layout = new LinearLayoutManager(SelectMultipleActivity.this, RecyclerView.VERTICAL, false);
            adapter = new PlaylistRecycleCreatePlaylist(SelectMultipleActivity.this, array.getRef(), selected_files);
            recycler_view.setAdapter(adapter);
            recycler_view.setLayoutManager(layout);
        } else
            recycler_view.setVisibility(View.GONE);
    }

    private void createNewPlaylist() {

        EditText playlist_name, user_name;
        Button add_playlist, cancel;
        View layout = LayoutInflater.from(SelectMultipleActivity.this).inflate(R.layout.create_playlist, null);
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
        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(SelectMultipleActivity.this);
        alert.setView(layout);

        dialog_0 = alert.create();
        if (dialog_0.getWindow() != null)
            dialog_0.getWindow().getAttributes().windowAnimations = R.style.UpDownDialogAnimation;
        dialog_0.show();
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
                Toast.makeText(SelectMultipleActivity.this, "Playlist Already Exist", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(SelectMultipleActivity.this, "You Should to feel the Names", Toast.LENGTH_SHORT).show();
        }
    }
}