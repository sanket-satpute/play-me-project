package com.sanket_satpute_20.playme.project.recycler_views;

import static com.sanket_satpute_20.playme.project.activity.AlbumListActivity.ALBUM_FINISHING_REQUEST_CODE;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BottomDialogAlbumFragment.ALBUM_COUNT_F;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BottomDialogAlbumFragment.ALBUM_NAME_F;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BottomDialogAlbumFragment.ALBUM_PATH_F;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.activity.AlbumListActivity;
import com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BottomDialogAlbumFragment;
import com.sanket_satpute_20.playme.project.extra_stuffes.CacheImageManager;
import com.sanket_satpute_20.playme.project.model.Albums;
import com.sanket_satpute_20.playme.project.model.MusicFiles;

import java.util.ArrayList;

public class AlbumsRecycle extends RecyclerView.Adapter<AlbumsRecycle.AlbumsHolder> {

    Context context;
    Activity activity;
    FragmentManager manager;
    ArrayList<Albums> albumFiles;

    public static final String ALBUMNAME = "ALBUMNAME";
    public static final String ALBUMCLASS = "ALBUMCLASS";
    public static final String POS = "POS";

    String fromWhere;

    public AlbumsRecycle(Context context, ArrayList<Albums> albumFiles, Activity activity, FragmentManager manager, String fromWhere)
    {
        this.context = context;
        this.albumFiles = albumFiles;
        this.activity = activity;
        this.manager = manager;
        this.fromWhere = fromWhere;
    }

    @NonNull
    @Override
    public AlbumsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_album_item, parent, false);
        return new AlbumsHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull AlbumsHolder holder, int position) {

        if (albumFiles.get(position).getAlbum_files().size() > 0) {
            holder.albumName.setText(albumFiles.get(position).getAlbum_name());

            try {
                Bitmap bitmap = CacheImageManager.getImage(context, albumFiles.get(position).getAlbum_files().get(0));
                if (bitmap == null) {
                    MyImageLoad load = new MyImageLoad();
                    load.setViewHolder(holder);
                    load.execute(albumFiles.get(position).getAlbum_files().get(0));
                } else {
                    Glide.with(context)
                            .load(bitmap)
                            .override(100, 100)
                            .placeholder(R.mipmap.ic_album)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.albumArt);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, AlbumListActivity.class);
                intent.putExtra(ALBUMNAME, albumFiles.get(position).getAlbum_name());
                intent.putExtra(POS, position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ALBUMCLASS, albumFiles.get(position));
                intent.putExtra("B", bundle);
                ((Activity) context).startActivityForResult(intent, ALBUM_FINISHING_REQUEST_CODE);
            });


            holder.expand_collapse.setOnClickListener(view -> showBottomDilog(position));

            holder.songCount.setText(String.format("%d Songs", albumFiles.get(position).getAlbum_files().size()));
        } else {
//            album with no songs send broadcast and remove it from recyclerview

            Intent broad_item_is_removing = new Intent();
            broad_item_is_removing.setAction("action.from_grid_or_non_grid_album_recycler_view.ITEM_REMOVED.FOR_0_Songs");
            broad_item_is_removing.putExtra("REMOVED_ITEM_POSITION", position);
            broad_item_is_removing.putExtra("IN_PARENT_LIST_ITEM_PRESENT_FULL_ALBUM", albumFiles.get(position));
            broad_item_is_removing.putExtra("THIS_ALBUM_RECYCLER_FROM", fromWhere);
            broad_item_is_removing.putExtra("IS_GRID_RECYCLER", false);
            LocalBroadcastManager.getInstance(context).sendBroadcast(broad_item_is_removing);

            albumFiles.remove(position);
        }
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public static class AlbumsHolder extends RecyclerView.ViewHolder {

        TextView albumName, songCount;
        ImageView albumArt, expand_collapse;
        public AlbumsHolder(@NonNull View itemView) {
            super(itemView);
            albumName = itemView.findViewById(R.id.album_name);
            songCount = itemView.findViewById(R.id.album_songs_count);
            albumArt = itemView.findViewById(R.id.album_art);
            expand_collapse = itemView.findViewById(R.id.option);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        private AlbumsHolder holder;
        MediaMetadataRetriever retriver;
        public void setViewHolder(AlbumsHolder holder)
        {
            this.holder = holder;
        }

        @Override
        protected byte[] doInBackground(MusicFiles... musicFiles) {
            try {
                fileIO = musicFiles[0];
                path = fileIO.getPath();
                retriver = new MediaMetadataRetriever();
                retriver.setDataSource(path);
                album_pic = retriver.getEmbeddedPicture();
                retriver.release();
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
                if (bytes != null)
                    CacheImageManager.putImage(context, fileIO, BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                Glide.with(context)
                        .load(bytes)
                        .override(100, 100)
                        .placeholder(R.mipmap.ic_album)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.albumArt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showBottomDilog(int position) {
        if (albumFiles != null) {
            try {
                Bundle bundle = new Bundle();
                bundle.putString(ALBUM_NAME_F, albumFiles.get(position).getAlbum_name());
                bundle.putInt(ALBUM_COUNT_F, albumFiles.get(position).getAlbum_files().size());
                bundle.putString(ALBUM_PATH_F, albumFiles.get(position).getAlbum_files().get(0).getPath());
                bundle.putSerializable(ALBUMCLASS, albumFiles.get(position));
                BottomDialogAlbumFragment dilog = new BottomDialogAlbumFragment();
                dilog.setArguments(bundle);
                dilog.show(manager, "ABG");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
