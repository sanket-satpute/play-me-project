package com.sanket_satpute_20.playme.project.recycler_views;

import static com.sanket_satpute_20.playme.project.activity.AlbumListActivity.ALBUM_FINISHING_REQUEST_CODE;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BottomDialogAlbumFragment.ALBUM_COUNT_F;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BottomDialogAlbumFragment.ALBUM_NAME_F;
import static com.sanket_satpute_20.playme.project.bottom_sheet_fragment.BottomDialogAlbumFragment.ALBUM_PATH_F;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.FROMWHERE;
import static com.sanket_satpute_20.playme.project.recycler_views.AlbumsRecycle.ALBUMCLASS;
import static com.sanket_satpute_20.playme.project.recycler_views.AlbumsRecycle.ALBUMNAME;
import static com.sanket_satpute_20.playme.project.recycler_views.AlbumsRecycle.POS;
import static com.sanket_satpute_20.playme.project.recycler_views.InnerAlbumRecycle.IAMALBUM;
import static com.sanket_satpute_20.playme.project.service.BackService.recent_album;
import static com.sanket_satpute_20.playme.project.service.BackService.song;

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
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.sanket_satpute_20.playme.project.service.BackService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class GridAlbumRecycle extends RecyclerView.Adapter<GridAlbumRecycle.GridAlbumHolder> {

    Context context;
    ArrayList<Albums> albums;
    FragmentManager supportFragmentManager;
    String fromWhere;

    public GridAlbumRecycle(Context context, ArrayList<Albums> albums, FragmentManager supportFragmentManager, String fromeWhere) {
        this.context = context;
        this.albums = albums;
        this.supportFragmentManager = supportFragmentManager;
        this.fromWhere = fromeWhere;
    }

    @NonNull
    @Override
    public GridAlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GridAlbumHolder(LayoutInflater.from(context).inflate(R.layout.grid_single_album_item,
                parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GridAlbumHolder holder, int position) {

        if (albums.get(position).getAlbum_files().size() > 0) {
            holder.album_name.setText(albums.get(position).getAlbum_name());

            holder.song_count.setText(albums.get(position).getAlbum_files().size() + " Audio");

            try {
                Bitmap bitmap = CacheImageManager.getImage(context, albums.get(position).getAlbum_files().get(0));
                if (bitmap == null) {
                    MyImageLoad load = new MyImageLoad();
                    load.setViewHolder(holder);
                    load.execute(albums.get(position).getAlbum_files().get(0));
                } else {
                    Glide.with(context)
                            .load(bitmap)
                            .override(300, 300)
                            .placeholder(R.mipmap.ic_album)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.album_src);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*On Click Item*/
            holder.itemView.setOnClickListener(view -> {
//            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context,
//                    Pair.create(holder.album_src, "album_src"));
                Intent intent = new Intent(context, AlbumListActivity.class);
                intent.putExtra(ALBUMNAME, albums.get(position).getAlbum_name());
                intent.putExtra(POS, position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ALBUMCLASS, albums.get(position));
                intent.putExtra("B", bundle);
//            context.startActivity(intent);/*, options.toBundle());*/
                ((Activity) context).startActivityForResult(intent, ALBUM_FINISHING_REQUEST_CODE);
            });

            holder.options.setOnClickListener(view -> showBottomDilog(position));

            holder.play_all.setOnClickListener(view -> {
                if (song != null) {
                    if (song.size() > 0) {
                        song.clear();
                        song.addAll(albums.get(position).getAlbum_files());

                        recent_album.add(albums.get(position));

                        Intent intent = new Intent(context, BackService.class);
                        intent.putExtra(FROMWHERE, IAMALBUM);
                        intent.putExtra("position", 0);
                        context.startService(intent);
                    }
                }
            });
        } else {
//            album with no songs send broadcast and remove it from recyclerview
            Intent broad_item_is_removing = new Intent();
            broad_item_is_removing.setAction("action.from_grid_or_non_grid_album_recycler_view.ITEM_REMOVED.FOR_0_Songs");
            broad_item_is_removing.putExtra("REMOVED_ITEM_POSITION", position);
            broad_item_is_removing.putExtra("IN_PARENT_LIST_ITEM_PRESENT_FULL_ALBUM", albums.get(position));
            broad_item_is_removing.putExtra("THIS_ALBUM_RECYCLER_FROM", fromWhere);
            broad_item_is_removing.putExtra("IS_GRID_RECYCLER", true);
            LocalBroadcastManager.getInstance(context).sendBroadcast(broad_item_is_removing);

            albums.remove(position);
        }
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public static class GridAlbumHolder extends RecyclerView.ViewHolder {

        ImageView album_src, options;
        TextView album_name, song_count;
        FloatingActionButton play_all;
        ConstraintLayout album_card;
        public GridAlbumHolder(@NonNull View itemView) {
            super(itemView);
            album_src = itemView.findViewById(R.id.album_src);
            album_name = itemView.findViewById(R.id.album_name);
            play_all = itemView.findViewById(R.id.play_all);
            options = itemView.findViewById(R.id.option);
            song_count = itemView.findViewById(R.id.song_count);
            album_card = itemView.findViewById(R.id.album_card);
        }
    }

    private class MyImageLoad extends AsyncTask<MusicFiles, Void, byte[]>
    {
        MusicFiles fileIO;
        String path;
        byte[] album_pic;
        private GridAlbumHolder holder;
        MediaMetadataRetriever retriver;
        public void setViewHolder(GridAlbumHolder holder)
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
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return album_pic;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            try {
                if (bytes != null)
                    CacheImageManager.putImage(context, fileIO, BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                Glide.with(context)
                        .load(bytes)
                        .override(300, 300)
                        .placeholder(R.mipmap.ic_album)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.album_src);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void showBottomDilog(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(ALBUM_NAME_F, albums.get(position).getAlbum_name());
        bundle.putInt(ALBUM_COUNT_F, albums.get(position).getAlbum_files().size());
        bundle.putString(ALBUM_PATH_F, albums.get(position).getAlbum_files().get(0).getPath());
        bundle.putSerializable(ALBUMCLASS ,albums.get(position));
        BottomDialogAlbumFragment dilog = new BottomDialogAlbumFragment();
        dilog.setArguments(bundle);
        dilog.show(supportFragmentManager, "ABG");
    }
}
