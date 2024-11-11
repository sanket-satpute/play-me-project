package com.sanket_satpute_20.playme.project.recycler_views;

import static android.content.Context.MODE_PRIVATE;
import static com.sanket_satpute_20.playme.project.activity.ThemesActivity.GRADIENT_THEME;
import static com.sanket_satpute_20.playme.project.activity.ThemesActivity.IMAGE_THEME;
import static com.sanket_satpute_20.playme.project.activity.ThemesActivity.NORMAL_THEME;
import static com.sanket_satpute_20.playme.project.activity.ThemesActivity.SOLID_THEME;
import static com.sanket_satpute_20.playme.project.activity.ThemesActivity.is_theme_changed;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.GRADIENT_THEME_ORIENTATION;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.IS_PREVIOUS_THEME_NOW;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_GRADIENT_SELECTED_GRADIENT;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_IMAGE_SELECTED_IMAGE;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_PREFERENCE;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_SOLID_SELECTED_COLOR;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.THEME_TYPE_PREFERENCE;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.THEME_TYPE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.extra_stuffes.CacheImageManager;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThemeSolidRecycle extends RecyclerView.Adapter<ThemeSolidRecycle.ThemeSolidHolder> {

    SharedPreferences theme_preference;
    SharedPreferences.Editor theme_preference_editor;

    public static final String THEME_COLOR_NAME_BROAD = "THEME_COLOR_NAME_BROAD";
    private static final String THEME_IMAGE_OR_SOLID_BROAD = "THEME_IMAGE_OR_SOLID_BROAD";
    int []theme_items;
    Context context;
    int already_selected_color;
    String what_is;
    ArrayList<String> images_url;
    ArrayList<int[]> gradient_col;

    ThemeSolidHolder selected_holder;

    public ThemeSolidRecycle (int []theme_items, Context context, int already_selected_color) {
        this.theme_items = theme_items;
        this.context = context;
        this.already_selected_color = already_selected_color;
        this.what_is = "Solid";
    }

    public ThemeSolidRecycle (ArrayList<String> images_url, Context context, int already_selected_color) {
        this.images_url = images_url;
        this.context = context;
        this.already_selected_color = already_selected_color;
        this.what_is = "Images";
    }

    public ThemeSolidRecycle (Context context, int already_selected_color, ArrayList<int[]> gradient_col) {
        this.gradient_col = gradient_col;
        this.context = context;
        this.already_selected_color = already_selected_color;
        this.what_is = "Gradient";
    }

    @NonNull
    @Override
    public ThemeSolidHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_solid_theme_item, parent, false);
        return new ThemeSolidHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeSolidHolder holder, @SuppressLint("RecyclerView") int position) {

        if (what_is.equals("Solid")) {
            if (already_selected_color == theme_items[position]) {
                holder.color_theme_selector.setVisibility(View.VISIBLE);
                selected_holder = holder;
            }
            holder.main_image_image.setVisibility(View.GONE);
            holder.main_image.setVisibility(View.VISIBLE);
            holder.main_image.setBackgroundTintList(ColorStateList.valueOf(theme_items[position]));
        }
        else if (what_is.equals("Images")) {
            holder.color_theme_selector.setVisibility(View.GONE);
            if (CacheImageManager.getImage(context, "image_"+position) != null) {
                holder.progress_bar.setVisibility(View.GONE);
                holder.download_image.setVisibility(View.GONE);
                holder.main_image_image.setAlpha(1f);
            } else {
                holder.progress_bar.setVisibility(View.GONE);
                holder.download_image.setVisibility(View.VISIBLE);
                holder.main_image_image.setAlpha(0.5f);
            }
            holder.main_image_image.setVisibility(View.VISIBLE);
            try {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.ic_round_image_loading_24)
                        .error(R.drawable.ic_round_broken_image_retry_24);
                Glide.with(context)
                        .load((CacheImageManager.getImage(context, "image_" + position) != null) ?
                                CacheImageManager.getImage(context, "image_" + position) :
                                images_url.get(position))
                        .apply(options)
                        .into(holder.main_image_image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    gradient_col.get(position)
            );
            holder.main_image_image.setImageDrawable(gradient);
        }

        holder.itemView.setOnClickListener(view -> {
            if (what_is.equals("Solid")) {
                solidClicked(position, holder);
            } else if (what_is.equals("Images")) {
                imageClicked(position, holder);
            } else if (what_is.equals("Gradient")) {
                gradientClicked(position, holder);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (what_is.equals("Solid"))
            return theme_items.length;
        else if (what_is.equals("Images"))
            return images_url.size();
        else
            return gradient_col.size();
    }


    public static class ThemeSolidHolder extends RecyclerView.ViewHolder {

        MaterialCardView main_image;
        ImageView main_image_image, color_theme_selector, download_image;
        ProgressBar progress_bar;

        public ThemeSolidHolder(@NonNull View itemView) {
            super(itemView);
            main_image = itemView.findViewById(R.id.main_image);
            color_theme_selector = itemView.findViewById(R.id.color_theme_selector);
            main_image_image = itemView.findViewById(R.id.main_image_image);
            download_image = itemView.findViewById(R.id.is_downloaded);
            progress_bar = itemView.findViewById(R.id.it_is_downloading);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MyImageLoad extends AsyncTask<String, Void, Bitmap>
    {
        private ThemeSolidHolder holder;
        int pos;
        InputStream inputStream;
        Bitmap bitmap;
        String path;

        public void setViewHolder(ThemeSolidHolder holder)
        {
            this.holder = holder;
        }
        public void setPosition(int pos) {
            this.pos = pos;
        }

        @Override
        protected Bitmap doInBackground(String... path) {
            this.path = path[0];
            try {
                URL url = new URL(path[0]);
                inputStream = (InputStream) url.getContent();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
            catch (RuntimeException | IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d("lgv", "Downloading in Background : " + path[0]);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            try {
                CacheImageManager.putImage(context, "image_"+pos, bitmap);
                holder.progress_bar.setVisibility(View.GONE);
                holder.main_image_image.setAlpha(1f);

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.ic_round_image_loading_24)
                        .error(R.drawable.ic_round_broken_image_retry_24);
                Glide.with(context)
                        .load(CacheImageManager.getImage(context, "image_" + holder.getAbsoluteAdapterPosition()))
                        .apply(options)
                        .into(holder.main_image_image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void solidClicked (int position, ThemeSolidHolder holder) {
        String previous_theme_name;
        THEME_TYPE = SOLID_THEME;
        if (theme_preference == null)
            theme_preference = context.getSharedPreferences(THEME_PREFERENCE, MODE_PRIVATE);
        previous_theme_name = theme_preference.getString(THEME_TYPE_PREFERENCE, NORMAL_THEME);
        if (theme_preference_editor == null)
            theme_preference_editor = theme_preference.edit();
        theme_preference_editor.putString(THEME_TYPE_PREFERENCE, SOLID_THEME);
        theme_preference_editor.apply();
        theme_preference_editor.putInt(THEME_SOLID_SELECTED_COLOR, theme_items[position]);
        theme_preference_editor.apply();
        theme_preference_editor.putBoolean(IS_PREVIOUS_THEME_NOW, previous_theme_name.equals(NORMAL_THEME));
        theme_preference_editor.apply();

        is_theme_changed = true;
        if (selected_holder != null)
            selected_holder.color_theme_selector.setVisibility(View.GONE);
        holder.color_theme_selector.setVisibility(View.VISIBLE);

        Intent intent;
        intent = new Intent();
        intent.setAction("theme_broadcast_receiver.Selected.Color");
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(intent);
        already_selected_color = theme_items[position];
        selected_holder = holder;
    }

    private void imageClicked (int position, ThemeSolidHolder holder) {
        String path = "image_"+position;
        if (CacheImageManager.getImage(context, path) != null) {
            String previous_theme_name;
            holder.color_theme_selector.setVisibility(View.VISIBLE);
            if (selected_holder != null) {
                selected_holder.color_theme_selector.setVisibility(View.GONE);
            }
            selected_holder = holder;
            THEME_TYPE = IMAGE_THEME;
            if (theme_preference == null)
                theme_preference = context.getSharedPreferences(THEME_PREFERENCE, MODE_PRIVATE);
            previous_theme_name = theme_preference.getString(THEME_TYPE_PREFERENCE, NORMAL_THEME);
            if (theme_preference_editor == null)
                theme_preference_editor = theme_preference.edit();
            is_theme_changed = true;
            theme_preference_editor.putString(THEME_TYPE_PREFERENCE, IMAGE_THEME);
            theme_preference_editor.apply();
            theme_preference_editor.putString(THEME_IMAGE_SELECTED_IMAGE, path);
            theme_preference_editor.apply();
            theme_preference_editor.putBoolean(IS_PREVIOUS_THEME_NOW, previous_theme_name.equals(NORMAL_THEME));
            theme_preference_editor.apply();

            Intent intent;
            intent = new Intent();
            intent.setAction("theme_broadcast_receiver.Selected.Color");
            LocalBroadcastManager.getInstance(context)
                    .sendBroadcast(intent);
        } else {
            holder.progress_bar.setVisibility(View.VISIBLE);
            holder.download_image.setVisibility(View.GONE);
            MyImageLoad load = new MyImageLoad();
            load.setViewHolder(holder);
            load.setPosition(position);
            load.execute(images_url.get(position));
        }
    }

    private void gradientClicked (int position, ThemeSolidHolder holder) {
        String previous_theme_name;
        List<int[]> list = Collections.singletonList(gradient_col.get(position));
        THEME_TYPE = GRADIENT_THEME;
        if (theme_preference == null)
            theme_preference = context.getSharedPreferences(THEME_PREFERENCE, MODE_PRIVATE);
        previous_theme_name = theme_preference.getString(THEME_TYPE_PREFERENCE, NORMAL_THEME);
        if (theme_preference_editor == null)
            theme_preference_editor = theme_preference.edit();
        String orientation = theme_preference.getString(GRADIENT_THEME_ORIENTATION, "T_TO_B");
        holder.color_theme_selector.setVisibility(View.VISIBLE);
        if (selected_holder != null) {
            selected_holder.color_theme_selector.setVisibility(View.GONE);
        }
        is_theme_changed = true;
        Gson gson = new Gson();
        String t_gradient = gson.toJson(list);
        theme_preference_editor.putString(THEME_TYPE_PREFERENCE, THEME_TYPE);
        theme_preference_editor.apply();
        theme_preference_editor.putString(THEME_GRADIENT_SELECTED_GRADIENT, t_gradient);
        theme_preference_editor.apply();
        theme_preference_editor.putString(GRADIENT_THEME_ORIENTATION, orientation);
        theme_preference_editor.apply();
        theme_preference_editor.putBoolean(IS_PREVIOUS_THEME_NOW, previous_theme_name.equals(NORMAL_THEME));
        theme_preference_editor.apply();

        Intent intent;
        intent = new Intent();
        intent.setAction("theme_broadcast_receiver.Selected.Color");
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(intent);
        selected_holder = holder;
    }
}
